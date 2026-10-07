package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.SerializedString;
import java.util.HexFormat;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static com.arangodb.jackson.dataformat.velocypack.VPackWriterDifferentialTest.*;

/** Only the frozen writer is constrained by these bugs/ambiguous calls. */
class LegacyWriterCharacterizationTest {
    static void legacy(String hex, Consumer<WriterReplay> calls) {
        assertArrayEquals(HexFormat.of().parseHex(hex),WriterReplay.encode(true,DEFAULTS,calls));
    }
    static void lostTag(String actual, String corrected, Consumer<WriterReplay> calls) {
        legacy(actual,calls);
        // The corrected vector is independently specified, NOT a byte identity requirement.
        assertFalse(java.util.Arrays.equals(HexFormat.of().parseHex(corrected),WriterReplay.encode(true,DEFAULTS,calls)));
    }
    static void value(WriterReplay w, boolean composite) {
        if(composite){w.g.writeStartArray();w.g.writeEndArray();}else w.g.writeNull();
    }

    @Test void validTagsAndRootRawPassThrough() {
        vector(DEFAULTS,"ee0718ef000100000000000001ee01ee021a",w->{w.tag(7);w.g.writeNull();
            w.tag(256);value(w,true);w.tag(1);w.tag(2);w.g.writeBoolean(true);});
        vector(DEFAULTS,"1306ee071801",w->{w.g.writeStartArray();w.tag(7);w.g.writeNull();w.g.writeEndArray();});
        vector(DEFAULTS,"130701ee071802",w->{w.g.writeStartArray();value(w,true);w.tag(7);w.g.writeNull();w.g.writeEndArray();});
        for(boolean composite:new boolean[]{false,true}) for(boolean before:new boolean[]{false,true}) {
            String v=composite?"01":"18";
            vector(DEFAULTS,before?"18191a"+v:v+"18191a",w->{
                if(!before)value(w,composite);
                w.raw((byte)0x18);byte[] b={0,0x19,0x1a,0};w.bytes(b,1,2);java.util.Arrays.fill(b,(byte)0);
                if(before)value(w,composite);
            });
        }
        vector(DEFAULTS,"181a0118",w->{w.bytes(new byte[]{0x18,0x1a},0,2);value(w,true);w.raw((byte)0x18);});
    }

    @Test void validPrefixBehaviorAcrossAllLayouts() {
        for(int mask=0;mask<16;mask++) same(mask,w->{
            w.tag(7);value(w,false);w.tag(256);value(w,true);
            w.g.writeStartArray();w.tag(7);value(w,false);w.tag(8);value(w,false);
            value(w,true);w.tag(9);value(w,false);w.g.writeEndArray();
        });
    }

    @Test void tagsLostBeforeObjectValuesAndCompositeArrayElements() {
        for(boolean composite:new boolean[]{false,true}) {
            String v=composite?"01":"18";
            lostTag("14064161"+v+"01","14084161ee07"+v+"01",w->{
                w.g.writeStartObject();w.g.writeName("a");w.tag(7);value(w,composite);w.g.writeEndObject();});
        }
        lostTag("13040101","1306ee070101",w->{w.g.writeStartArray();w.tag(7);value(w,true);w.g.writeEndArray();});
        lostTag("13081801ee071a03","130818ee07011a03",w->{
            w.g.writeStartArray();value(w,false);w.tag(7);value(w,true);w.g.writeBoolean(true);w.g.writeEndArray();});
        for(boolean composite:new boolean[]{false,true}) {
            String v=composite?"01":"18";
            legacy("14094161"+v+"41621802",w->{
                w.g.writeStartObject();w.g.writeName("a");value(w,composite);w.tag(7);
                w.g.writeName("b");w.g.writeNull();w.g.writeEndObject();});
        }
        // A trailing prefix has no following value: characterize, do not invent semantics.
        legacy("18ee07",w->{value(w,false);w.tag(7);});
        legacy("01ee07",w->{value(w,true);w.tag(7);});
        legacy("13041801",w->{w.g.writeStartArray();value(w,false);w.tag(7);w.g.writeEndArray();});
        legacy("140641610101",w->{w.g.writeStartObject();w.g.writeName("a");value(w,true);w.tag(7);w.g.writeEndObject();});
    }

    @Test void rawCallsAreValuesNotPrefixOperationsAndContainerFragmentsAreAmbiguous() {
        for(boolean block:new boolean[]{false,true}) {
            Consumer<WriterReplay> raw=w->{if(block)w.bytes(new byte[]{0x18,0x1a},0,2);else w.raw((byte)0x18);};
            String bytes=block?"181a":"18";
            for(boolean composite:new boolean[]{false,true}) for(boolean before:new boolean[]{false,true}) {
                String v=composite?"01":"18";
                // Raw never finishes a captured item. It is dropped, or concatenated with a later scalar.
                String expected=before&&!composite ? (block?"1306":"1305")+bytes+v+"01" : "1304"+v+"01";
                legacy(expected,w->{w.g.writeStartArray();if(before)raw.accept(w);value(w,composite);
                    if(!before)raw.accept(w);w.g.writeEndArray();});
                assertThrows(StreamWriteException.class,()->WriterReplay.encode(true,DEFAULTS,w->{
                    w.g.writeStartObject();w.g.writeName("a");
                    if(before)raw.accept(w);value(w,composite);if(!before)raw.accept(w);
                }));
            }
            legacy("01",w->{w.g.writeStartArray();raw.accept(w);w.g.writeEndArray();});
            assertThrows(IndexOutOfBoundsException.class,()->WriterReplay.encode(true,DEFAULTS,w->{
                w.g.writeStartObject();w.g.writeName("a");raw.accept(w);w.g.writeEndObject();}));
        }
        // Raw bytes happen to work as a tag prefix in an array, despite consuming value slots.
        // This is characterized only; it does not define general raw fragment behavior.
        legacy("1306ee071801",w->{w.g.writeStartArray();w.raw((byte)0xee);w.raw((byte)7);
            assertEquals(1,w.g.streamWriteContext().getCurrentIndex());w.g.writeNull();w.g.writeEndArray();});
        // Multi-value raw SerializableString blocks inside a container are also invalid count-wise.
        legacy("1305181a01",w->{w.g.writeStartArray();w.g.writeRawValue(new SerializedString("\u0018\u001a"));w.g.writeEndArray();});
    }
}
