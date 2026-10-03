package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.databind.json.JsonMapper;
import java.nio.file.*;
import java.util.*;
import java.math.BigDecimal;
import java.util.function.Consumer;

/** Offline fixtures from the production writer; JSON expectations use an independent format. */
public class WriterInteropFixtures {
    static Path destination;
    static final JsonMapper json = new JsonMapper();
    static final List<String> manifest = new ArrayList<>();
    static void fixture(String name, int features, Object expected, int tags, Consumer<VPackGenerator> body) throws Exception {
        var out = new java.io.ByteArrayOutputStream();
        try (var g = new VPackGenerator(tools.jackson.core.ObjectWriteContext.empty(), BaseTestForVPack.testIOContext(),
                tools.jackson.core.StreamWriteFeature.collectDefaults(), features, out)) { body.accept(g); }
        Files.write(destination.resolve(name+".vpack"), out.toByteArray());
        Files.write(destination.resolve(name+".json"), json.writeValueAsBytes(expected));
        manifest.add(name+" "+tags);
    }
    static int layout(boolean compact, boolean sorted) {
        int f = VPackWriteFeature.collectDefaults();
        if (!compact) f &= ~(VPackWriteFeature.WRITE_COMPACT_ARRAYS.getMask()|VPackWriteFeature.WRITE_COMPACT_OBJECTS.getMask());
        if (sorted) f |= VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED.getMask();
        return f;
    }
    public static void main(String[] args) throws Exception {
        destination = Path.of(args[0]); Files.createDirectories(destination);
        for (boolean compact : new boolean[]{true,false}) for (boolean sorted : new boolean[]{false,true}) {
            String label = (compact?"compact":"indexed")+(sorted?"-sorted":"-unsorted"); int f=layout(compact,sorted);
            fixture(label,f,Map.of("z",List.of(1,"é漢😀",Map.of("inner",List.of(true,false,3))),"a",List.of(7,8,9),"empty",Map.of()),0,g->{
                g.writeStartObject(); g.writeName("z"); g.writeStartArray();g.writeNumber(1);g.writeString("é漢😀");g.writeStartObject();
                g.writeName("inner");g.writeStartArray();g.writeBoolean(true);g.writeBoolean(false);g.writeNumber(3);g.writeEndArray();g.writeEndObject();g.writeEndArray();
                g.writeName("a");g.writeArray(new int[]{7,8,9},0,3);g.writeName("empty");g.writeStartObject();g.writeEndObject();g.writeEndObject();
            });
            fixture(label+"-tags",f,Map.of("tag",List.of(Map.of("n",5),List.of(),"later")),4,g->{
                g.writeStartObject();g.writeName("tag");g.writeTaggedValuePrefix(42);g.writeTaggedValuePrefix(123456789L);g.writeStartArray();
                g.writeTaggedValuePrefix(7);g.writeStartObject();g.writeName("n");g.writeNumber(5);g.writeEndObject();
                g.writeTaggedValuePrefix(255);g.writeStartArray();g.writeEndArray();g.writeString("later");g.writeEndArray();g.writeEndObject();
            });
            var ids = new LinkedHashMap<String,Object>(); for(int i=1;i<=6;i++)ids.put("id"+i,i);
            fixture(label+"-ids",f,ids,0,g->{g.writeStartObject();for(int i=6;i>=1;i--){g.writePropertyId(i);g.writeNumber(i);}g.writeEndObject();});
        }
        for(int n:new int[]{122,123,16376,16377}) {
            byte[] b=new byte[n];
            fixture("length-"+n,layout(true,false),List.of("binary:"+"00".repeat(n)),0,g->{g.writeStartArray();g.writeBinary(b);g.writeEndArray();});
        }
        for(int n:new int[]{127,128,16383,16384}) {
            fixture("count-"+n,layout(true,false),Collections.nCopies(n,1),0,g->{g.writeStartArray();for(int i=0;i<n;i++)g.writeNumber(1);g.writeEndArray();});
        }
        for(int n:new int[]{247,248,65522,65523}) {
            byte[] b=new byte[n];
            fixture("mixed-"+n,layout(false,false),Arrays.asList(null,"binary:"+"00".repeat(n)),0,g->{g.writeStartArray();g.writeNull();g.writeBinary(b);g.writeEndArray();});
        }
        for(int n:new int[]{120,121,16374,16375}) {
            byte[] b=new byte[n];
            fixture("object-length-"+n,layout(true,false),Map.of("a","binary:"+"00".repeat(n)),0,g->{g.writeStartObject();g.writeName("a");g.writeBinary(b);g.writeEndObject();});
        }
        for(int n:new int[]{247,248,65523,65524}) {
            byte[] b=new byte[n];
            fixture("object-index-length-"+n,layout(false,true),Map.of("a","binary:"+"00".repeat(n)),0,g->{g.writeStartObject();g.writeName("a");g.writeBinary(b);g.writeEndObject();});
        }
        for(int n:new int[]{253,254,65532,65533}) {
            fixture("equal-width-"+n,layout(false,false),Collections.nCopies(n,true),0,g->{g.writeStartArray();for(int i=0;i<n;i++)g.writeBoolean(true);g.writeEndArray();});
        }
        for(boolean compact:new boolean[]{true,false}) fixture("unicode-keys-"+compact,layout(compact,true),Map.of("é",1,"漢",2,"😀",3,"a",4,"",5),0,g->{
            g.writeStartObject();g.writeNumberProperty("😀",3);g.writeNumberProperty("漢",2);g.writeNumberProperty("é",1);g.writeNumberProperty("a",4);g.writeNumberProperty("",5);g.writeEndObject();
        });
        fixture("scalars",layout(true,false),Arrays.asList(null,true,false,-129,32768,Long.MIN_VALUE,1.25,"","?","binary:0001ff"),0,g->{
            g.writeStartArray();g.writeNull();g.writeBoolean(true);g.writeBoolean(false);g.writeNumber(-129);g.writeNumber(32768);g.writeNumber(Long.MIN_VALUE);
            g.writeNumber(1.25);g.writeString("");g.writeString("\ud800");g.writeBinary(new byte[]{0,1,(byte)255});g.writeEndArray();
        });
        fixture("cursor",layout(true,false),ArangoDocuments.cursor(ArangoDocuments.customers(1000,ArangoDocuments.SEED)),0,g->{
            var mapper=new VPackMapper();mapper.writeValue(g,ArangoDocuments.cursor(ArangoDocuments.customers(1000,ArangoDocuments.SEED)));
        });
        // C++ Validator explicitly returns NotImplemented for BCD, even when allowed.
        // Literal independent specification: positive 123.45 => exponent -2, packed mantissa 01 23 45.
        byte[] bcd=WriterReplay.encode(false,layout(true,false),w->w.g.writeNumber(new BigDecimal("123.45")));
        byte[] expected={(byte)0xc8,3,(byte)0xfe,(byte)0xff,(byte)0xff,(byte)0xff,0x01,0x23,0x45};
        if(!Arrays.equals(bcd,expected))throw new AssertionError("Independent BCD vector: "+HexFormat.of().formatHex(bcd));
        Files.write(destination.resolve("bcd.vpack"),bcd);
        Files.writeString(destination.resolve("manifest.txt"),String.join("\n",manifest)+"\n");
        System.out.println("Exported "+manifest.size()+" C++ fixtures and one independent BCD vector to "+destination);
    }
}
