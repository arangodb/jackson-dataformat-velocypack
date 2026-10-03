package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.io.SerializedString;
import java.io.*;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static com.arangodb.jackson.dataformat.velocypack.WriterReferenceWire.*;

/** Valid output is compared byte for byte; broken calls live in LegacyWriterCharacterizationTest. */
class VPackWriterDifferentialTest {
    static final int DEFAULTS = VPackWriteFeature.collectDefaults();
    static byte[] same(int mask, Consumer<WriterReplay> calls) {
        byte[] expected = WriterReplay.encode(true, mask, calls);
        assertArrayEquals(expected, WriterReplay.encode(false, mask, calls), "feature mask " + mask);
        return expected;
    }
    static void vector(int mask, String hex, Consumer<WriterReplay> calls) {
        assertArrayEquals(HexFormat.of().parseHex(hex), same(mask, calls));
    }

    @Test void allLayoutsScalarsArraysStringsAndContainers() {
        for (int mask = 0; mask < 16; mask++) {
            same(mask, w -> {
                var g = w.g;
                g.writeStartArray(new Object(), 50);
                g.writeNull(); g.writeString((String) null);
                g.writeBoolean(false); g.writeBoolean(true);
                for (long n : new long[]{-7,-6,-1,0,9,10,127,128,-128,-129,32767,32768,-32769,
                        Integer.MAX_VALUE,1L << 32,Long.MIN_VALUE,Long.MAX_VALUE}) g.writeNumber(n);
                g.writeNumber((short) 42); g.writeNumber(42); g.writeNumber(1.25f);
                g.writeNumber(-0.0); g.writeNumber(Double.POSITIVE_INFINITY);
                g.writeNumber(Double.longBitsToDouble(0x7ff8000000000001L));
                g.writeNumber(new BigInteger("18446744073709551616"));
                g.writeNumber(BigInteger.valueOf(12)); g.writeNumber((BigInteger) null);
                g.writeNumber(new BigDecimal("-123.4500")); g.writeNumber((BigDecimal) null);
                g.writeNumber("42"); g.writeNumber("1.25e2"); g.writeNumber("18446744073709551616");
                g.writeNumber((String) null); g.writeNumber("!123.5!".toCharArray(),1,5);
                g.writeEmbeddedObject(new byte[]{0,1,2}); g.writeEmbeddedObject(null);
                g.writeString(""); g.writeString("a".repeat(126)); g.writeString("a".repeat(127));
                g.writeString("Größe 東京 \u0000 \uD83D\uDE80");
                char[] chars = "!é東京!".toCharArray(); g.writeString(chars,1,chars.length-2);
                g.writeString(new SerializedString("é\uD83D\uDE80"));
                byte[] utf = "!é東京!".getBytes(StandardCharsets.UTF_8);
                g.writeUTF8String(utf,1,utf.length-2); g.writeRawUTF8String(utf,1,utf.length-2);
                for(int len:new int[]{126,127}) {byte[] ascii=new byte[len];Arrays.fill(ascii,(byte)'x');
                    g.writeUTF8String(ascii,0,len);g.writeRawUTF8String(ascii,0,len);}
                byte[] bin = {9,0,1,2,9}; g.writeBinary(Base64Variants.getDefaultVariant(), bin,1,3);
                assertEquals(3,g.writeBinary(new ByteArrayInputStream(new byte[]{0,1,2}),3));
                assertEquals(3,g.writeBinary(Base64Variants.getDefaultVariant(),new ByteArrayInputStream(new byte[]{0,1,2}),3));
                // Serializable raw value contains one complete encoded null, not JSON text.
                g.writeRawValue(new SerializedString("\u0018"));
                g.writeArray(new int[]{99,0,-6,128,99},1,3);
                g.writeArray(new long[]{99,Long.MIN_VALUE,1L << 40,99},1,2);
                g.writeArray(new double[]{99,-0.0,1.25,99},1,2);
                g.writeArray(new String[]{"ignore","é","","ignore"},1,2);
                g.writeStartArray(); g.writeEndArray();
                g.writeStartObject(); g.writeEndObject();
                g.writeStartArray(new Object()); g.writeNull(); g.writeBoolean(true); g.writeEndArray();
                g.writeStartObject(new Object(),3);
                g.writeName("z"); g.writeStartArray(); g.writeNumber(1); g.writeString("mixed"); g.writeEndArray();
                g.writeName(new SerializedString("a")); g.writeStartObject(new Object());
                g.writeName("nested"); g.writeBoolean(false); g.writeEndObject();
                g.writeName("long".repeat(40)); g.writeNull(); g.writeEndObject();
                g.writeEndArray();
                // Multiple roots exercise scalar/composite ordering and flush.
                g.writeNull(); g.flush(); g.writeStartArray(); g.writeBoolean(false); g.writeEndArray();
            });
        }
    }

    @Test void independentlyDerivedVectorsAndIntegerWidths() {
        vector(DEFAULTS,"18191a30393a3f200a2180002300800000270000000001000000", w -> {
            var g=w.g; g.writeNull();g.writeBoolean(false);g.writeBoolean(true);
            g.writeNumber(0);g.writeNumber(9);g.writeNumber(-6);g.writeNumber(-1);
            g.writeNumber(10);g.writeNumber(128);g.writeNumber(32768);g.writeNumber(1L<<32);
        });
        vector(DEFAULTS & ~8,"2000200920fa20ff",w->{w.g.writeNumber(0);w.g.writeNumber(9);w.g.writeNumber(-6);w.g.writeNumber(-1);});
        vector(DEFAULTS,"4042c3a944f09f9a80",w->{w.g.writeString("");w.g.writeString("é");w.g.writeString("🚀");});
        vector(DEFAULTS,"c0030001021b000000000000f03fc802feffffff0123",w->{
            w.g.writeBinary(new byte[]{0,1,2});w.g.writeNumber(1.0);w.g.writeNumber(new BigDecimal("1.23"));
        });
        vector(DEFAULTS,"010a13041801140641611801",w->{w.g.writeStartArray();w.g.writeEndArray();
            w.g.writeStartObject();w.g.writeEndObject();w.g.writeStartArray();w.g.writeNull();w.g.writeEndArray();
            w.g.writeStartObject();w.g.writeName("a");w.g.writeNull();w.g.writeEndObject();});
        vector(8,"0204181a06080218417803040f070141611803",w->{w.g.writeStartArray();w.g.writeNull();w.g.writeBoolean(true);w.g.writeEndArray();
            w.g.writeStartArray();w.g.writeNull();w.g.writeString("x");w.g.writeEndArray();
            w.g.writeStartObject();w.g.writeName("a");w.g.writeNull();w.g.writeEndObject();});
        for(int len:new int[]{126,127}) {
            byte[] text = new byte[len]; Arrays.fill(text,(byte)'x');
            byte[] expected=cat(len==126?new byte[]{(byte)0xbe}:cat(new byte[]{(byte)0xbf},le(127,8)),text);
            assertArrayEquals(expected,same(DEFAULTS,w->w.g.writeString("x".repeat(len))));
        }
    }

    @Test void duplicateKeysSortStablyAndPropertyIds() {
        for(int mask=0;mask<16;mask++) {
            final int m=mask;
            byte[] a=m<8?new byte[]{0x41,0x61,0x20,1}:new byte[]{0x41,0x61,0x31};
            byte[] a2=m<8?new byte[]{0x41,0x61,0x20,2}:new byte[]{0x41,0x61,0x32};
            byte[] z=m<8?new byte[]{0x41,0x7a,0x20,3}:new byte[]{0x41,0x7a,0x33};
            byte[][] pairs=(m&1)!=0?new byte[][]{a,a2,z}:new byte[][]{z,a,a2};
            byte[] expected=(m&4)!=0?compact(true,3,cat(pairs)):indexed(true,(m&1)!=0,pairs);
            assertArrayEquals(expected,same(m,w->{w.g.writeStartObject();w.g.writeName("z");w.g.writeNumber(3);
                w.g.writeName("a");w.g.writeNumber(1);w.g.writeName("a");w.g.writeNumber(2);w.g.writeEndObject();}));
            same(m,w->{w.g.writeStartObject();for(long id:new long[]{0,42,255,256,65535,65536,1L<<32}){
                w.g.writePropertyId(id);w.g.writeNull();}w.g.writeEndObject();});
        }
        vector(DEFAULTS,"1406282a1801",w->{w.g.writeStartObject();w.g.writePropertyId(42);w.g.writeNull();w.g.writeEndObject();});
    }

    @Test void compactTotalLengthAndCountTransitions() {
        // L = 1 + vbyteWidth(L) + content + reverseVbyteWidth(count).
        // 128 and 16384 cannot be totals at these crossings: widening adds one byte.
        for(boolean object:new boolean[]{false,true}) {
            for(int contentLength:new int[]{124,125,126,16379,16380,16381}) {
                int binLength=contentLength-(object?2:0)- (contentLength<256?2:3);
                byte[] content=cat(object?new byte[]{0x41,0x61}:new byte[0],binary(binLength));
                byte[] expected=compact(object,1,content);
                assertEquals(contentLength,content.length);
                assertArrayEquals(expected,same(DEFAULTS,w->{
                    if(object){w.g.writeStartObject();w.g.writeName("a");}else w.g.writeStartArray();
                    w.g.writeBinary(new byte[binLength]);
                    if(object)w.g.writeEndObject();else w.g.writeEndArray();
                }));
                assertEquals(contentLength+(contentLength<=124?3:contentLength<=16379?4:5),expected.length);
            }
            for(int count:new int[]{127,128,16383,16384}) {
                var c=new ByteArrayOutputStream();
                for(int i=0;i<count;i++) c.writeBytes(object?new byte[]{0x41,0x61,0x18}:new byte[]{0x18});
                assertArrayEquals(compact(object,count,c.toByteArray()),same(DEFAULTS,w->{
                    if(object)w.g.writeStartObject();else w.g.writeStartArray();
                    for(int i=0;i<count;i++){if(object)w.g.writeName("a");w.g.writeNull();}
                    if(object)w.g.writeEndObject();else w.g.writeEndArray();
                }));
            }
        }
    }

    @Test void indexedAndEqualWidthTotalLengthTransitions() {
        for(int len:new int[]{246,247,248,249,65521,65522,65523,65524}) {
            byte[] expected=indexed(false,false,new byte[]{0x18},binary(len));
            assertArrayEquals(expected,same(8,w->{w.g.writeStartArray();w.g.writeNull();w.g.writeBinary(new byte[len]);w.g.writeEndArray();}));
        }
        for(boolean sorted:new boolean[]{false,true}) for(int len:new int[]{246,247,248,249,65522,65523,65524,65525}) {
            assertArrayEquals(indexed(true,sorted,cat(new byte[]{0x41,0x61},binary(len))),same(sorted?9:8,w->{
                w.g.writeStartObject();w.g.writeName("a");w.g.writeBinary(new byte[len]);w.g.writeEndObject();}));
        }
        for(int count:new int[]{253,254,65532,65533}) {
            byte[][] items=new byte[count][];Arrays.fill(items,new byte[]{0x18});
            assertArrayEquals(noIndex(items),same(8,w->{w.g.writeStartArray();for(int i=0;i<count;i++)w.g.writeNull();w.g.writeEndArray();}));
        }
    }

    @Test void featuresAreSampledAtScalarWriteAndContainerClose() {
        vector(DEFAULTS,"0608023120020304",w->{w.g.writeStartArray();w.g.writeNumber(1);
            w.feature(VPackWriteFeature.WRITE_MIN_INT_WIDTH,false);w.g.writeNumber(2);
            w.feature(VPackWriteFeature.WRITE_COMPACT_ARRAYS,false);w.g.writeEndArray();});
        vector(8,"1409416118417a1802",w->{w.g.writeStartObject();w.g.writeName("z");w.g.writeNull();
            w.g.writeName("a");w.g.writeNull();w.feature(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED,true);
            w.feature(VPackWriteFeature.WRITE_COMPACT_OBJECTS,true);w.g.writeEndObject();});
        same(DEFAULTS,w->{w.g.writeStartArray();w.g.writeStartObject();w.g.writeName("z");w.g.writeNull();
            w.g.writeName("a");w.g.writeNull();w.feature(VPackWriteFeature.WRITE_COMPACT_OBJECTS,false);
            w.feature(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED,true);w.g.writeEndObject();
            w.feature(VPackWriteFeature.WRITE_COMPACT_ARRAYS,false);w.g.writeEndArray();});
    }

    @Test void reverseFeatureChangesAndUnsignedKeyWidths() {
        vector(0,"130620013202",w->{w.g.writeStartArray();w.g.writeNumber(1);
            w.feature(VPackWriteFeature.WRITE_MIN_INT_WIDTH,true);w.g.writeNumber(2);
            w.feature(VPackWriteFeature.WRITE_COMPACT_ARRAYS,true);w.g.writeEndArray();});
        vector(DEFAULTS|1,"0f0b02417a184161180306",w->{w.g.writeStartObject();w.g.writeName("z");w.g.writeNull();
            w.g.writeName("a");w.g.writeNull();w.feature(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED,false);
            w.feature(VPackWriteFeature.WRITE_COMPACT_OBJECTS,false);w.g.writeEndObject();});
        byte[][] entries={new byte[]{0x28,(byte)255,0x18},new byte[]{0x29,0,1,0x18},
            new byte[]{0x2b,0,0,1,0,0x18},new byte[]{0x2f,0,0,0,0,1,0,0,0,0x18}};
        assertArrayEquals(compact(true,4,cat(entries)),same(DEFAULTS,w->{w.g.writeStartObject();
            for(long id:new long[]{255,256,65536,1L<<32}){w.g.writePropertyId(id);w.g.writeNull();}w.g.writeEndObject();}));
    }

    @Test void frozenSourceHashAndUnsupportedScalarOverloads() throws Exception {
        var source=java.nio.file.Files.readString(java.nio.file.Path.of(
            "src/test/java/com/arangodb/jackson/dataformat/velocypack/LegacyVPackGenerator.java"));
        byte[] original=source.replace("LegacyVPackGenerator","VPackGenerator").getBytes(StandardCharsets.UTF_8);
        assertEquals("cc6d9cc29fd2193f283fdc0fd99b6247c37cec91af44d026d68b3c7a3ff16b7b",
            HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(original)));
        for(boolean legacy:new boolean[]{false,true}) {
            for(Consumer<WriterReplay> calls:List.<Consumer<WriterReplay>>of(
                w->w.g.writeString(new StringReader("hello"),5),
                w->w.g.writeRaw("x"),w->w.g.writeRaw("x",0,1),w->w.g.writeRaw(new char[]{'x'},0,1),w->w.g.writeRaw('x'),
                w->w.g.writeRaw(new SerializedString("x")),w->w.g.writeRawValue("x"),w->w.g.writeRawValue("x",0,1),
                w->w.g.writeRawValue(new char[]{'x'},0,1))) {
                assertThrows(UnsupportedOperationException.class,()->WriterReplay.encode(legacy,DEFAULTS,calls));
            }
        }
    }

    @Test void malformedSurrogatesIgnoreLenientFeature() {
        for(boolean lenient:new boolean[]{false,true}) {
            int mask=DEFAULTS | (lenient?16:0);
            vector(mask,"413f413f433f783f44f09f9a80",w->{w.g.writeString("\uD800");w.g.writeString("\uDC00");
                w.g.writeString("\uD800x\uDC00");w.g.writeString("\uD83D\uDE80");});
            vector(mask,"1407413f413f01",w->{w.g.writeStartObject();w.g.writeName("\uD800");w.g.writeString("\uDC00");w.g.writeEndObject();});
            vector(mask,"413f413f",w->{w.g.writeString(new char[]{'\uD800'},0,1);w.g.writeString(new SerializedString("\uDC00"));});
        }
    }

    @Test void callerBuffersAreCopiedBeforeReturn() {
        for(int mask=0;mask<16;mask++) same(mask,w->{
            byte[] b={0,65,66,0};char[] c={'!', 'a','b','!'};int[] ints={1,2};long[] longs={1,2};double[] doubles={1,2};
            w.g.writeStartArray();w.g.writeBinary(Base64Variants.getDefaultVariant(),b,1,2);
            w.g.writeUTF8String(b,1,2);w.g.writeRawUTF8String(b,1,2);w.g.writeString(c,1,2);
            w.g.writeArray(ints,0,2);w.g.writeArray(longs,0,2);w.g.writeArray(doubles,0,2);
            Arrays.fill(b,(byte)9);Arrays.fill(c,'x');Arrays.fill(ints,9);Arrays.fill(longs,9);Arrays.fill(doubles,9);
            w.g.writeEndArray();
        });
        vector(DEFAULTS,"130ac002414242414202",w->{byte[] b={65,66};w.g.writeStartArray();w.g.writeBinary(b);w.g.writeUTF8String(b,0,2);
            Arrays.fill(b,(byte)'x');w.g.writeEndArray();});
    }

    @Test void customOutputOffsetsFlushAndClose() {
        for(boolean legacy:new boolean[]{false,true}) for(int offset:new int[]{0,7,255,256}) {
            var out=new TrackingOutput();byte[] buffer=new byte[256];Arrays.fill(buffer,0,offset,(byte)0x19);
            var w=new WriterReplay(legacy,DEFAULTS,StreamWriteFeature.collectDefaults(),out,buffer,offset);
            assertSame(out,w.g.streamWriteOutputTarget());assertEquals(offset,w.g.streamWriteOutputBuffered());
            w.g.writeNull();w.g.flush();assertEquals(offset+1,out.size());assertEquals(1,out.flushes);
            w.g.writeStartArray();w.g.writeNull();w.g.flush();assertEquals(offset+1,out.size());
            w.g.writeEndArray();w.g.writeString("x".repeat(1000));w.close();w.close();w.g.flush();
            assertEquals(1,out.closes);
            byte[] prefix=new byte[offset];Arrays.fill(prefix,(byte)0x19);
            assertArrayEquals(cat(prefix,new byte[]{0x18},compact(false,1,new byte[]{0x18}),cat(new byte[]{(byte)0xbf},le(1000,8),"x".repeat(1000).getBytes(StandardCharsets.UTF_8))),out.toByteArray());
        }
        for(boolean legacy:new boolean[]{false,true}) {
            var out=new TrackingOutput();try(var w=new WriterReplay(legacy,DEFAULTS,0,out,null,0)){w.g.writeNull();}
            assertEquals(0,out.closes);assertArrayEquals(new byte[]{0x18},out.toByteArray());
        }
    }
    static class TrackingOutput extends ByteArrayOutputStream {
        int flushes,closes;
        @Override public void flush(){flushes++;}
        @Override public void close(){closes++;}
    }

    @Test void binaryLengthHeadersAndStreamFeatures() {
        for(int len:new int[]{0,255,256,65535,65536}) for(int mask=0;mask<16;mask++)
            assertArrayEquals(binary(len),same(mask,w->w.g.writeBinary(new byte[len])));
        for(boolean legacy:new boolean[]{false,true}) {
            for(boolean recyclable:new boolean[]{false,true}) {
                var io=BaseTestForVPack.testIOContext();var out=new TrackingOutput();
                byte[] b=recyclable?io.allocWriteEncodingBuffer():new byte[256];
                try(var w=new WriterReplay(legacy,DEFAULTS,StreamWriteFeature.FLUSH_PASSED_TO_STREAM.getMask(),
                        out,b,0,recyclable,io)){w.g.writeNull();}
                assertArrayEquals(new byte[]{0x18},out.toByteArray());assertEquals(0,out.closes);assertEquals(1,out.flushes);
            }
            var out=new TrackingOutput();
            try(var w=new WriterReplay(legacy,DEFAULTS,StreamWriteFeature.STRICT_DUPLICATE_DETECTION.getMask(),out,null,0)) {
                w.g.writeStartObject();w.g.writeName("a");w.g.writeNull();
                assertThrows(tools.jackson.core.exc.StreamWriteException.class,()->w.g.writeName("a"));
            }
        }
    }

    @Test void benchmarkFixtureAndBoundedSeededTrees() {
        // Match Bench: decode each format's own POJO bytes before writing tree events.
        var mapper=new VPackMapper();
        byte[] fixture=mapper.writeValueAsBytes(ArangoDocuments.cursor(ArangoDocuments.customers(32,ArangoDocuments.SEED)));
        for(int mask=0;mask<16;mask++) {
            same(mask,w->{try(var p=mapper.createParser(fixture)){while(p.nextToken()!=null)w.g.copyCurrentEvent(p);}});
            for(int seed=0;seed<24;seed++) {
                final int s=seed;
                same(mask,w->randomValue(w.g,new Random(0x56acL+s),0));
            }
        }
        byte[] full=mapper.writeValueAsBytes(ArangoDocuments.cursor(ArangoDocuments.customers(1000,ArangoDocuments.SEED)));
        assertEquals(1144774,full.length);
        assertArrayEquals(full,same(DEFAULTS,w->{try(var p=mapper.createParser(full)){while(p.nextToken()!=null)w.g.copyCurrentEvent(p);}}));
    }
    static void randomValue(JsonGenerator g,Random r,int depth) {
        switch(r.nextInt(depth<4?8:6)) {
            case 0 -> g.writeNull();case 1 -> g.writeBoolean(r.nextBoolean());case 2 -> g.writeNumber(r.nextLong());
            case 3 -> g.writeString("é🚀"+"x".repeat(r.nextInt(140)));case 4 -> g.writeNumber(r.nextDouble());
            case 5 -> g.writeBinary(new byte[r.nextInt(20)]);
            case 6 -> {g.writeStartArray();for(int i=0,n=r.nextInt(6);i<n;i++)randomValue(g,r,depth+1);g.writeEndArray();}
            case 7 -> {g.writeStartObject();for(int i=0,n=r.nextInt(6);i<n;i++){g.writeName("k"+r.nextInt(4));randomValue(g,r,depth+1);}g.writeEndObject();}
        }
    }
}
