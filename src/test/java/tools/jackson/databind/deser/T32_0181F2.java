package tools.jackson.databind.deser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0181F2 {
private static final byte[] JAVA_TYPE = VPackWireFixtureTest.hex(
            "50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67");
private static final byte[] TOKEN_BUFFER_SAMPLE = VPackWireFixtureTest.hex(
            "14 99 01 45 49 6d 61 67 65 14 8f 01 "
          + "45 57 69 64 74 68 29 20 03 "
          + "46 48 65 69 67 68 74 29 58 02 "
          + "45 54 69 74 6c 65 54 56 69 65 77 20 66 72 6f 6d 20 31 35 74 68 20 46 6c 6f 6f 72 "
          + "49 54 68 75 6d 62 6e 61 69 6c 14 41 "
          + "43 55 72 6c 66 68 74 74 70 3a 2f 2f 77 77 77 2e 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 69 6d 61 67 65 2f 34 38 31 39 38 39 39 34 33 "
          + "46 48 65 69 67 68 74 28 7d "
          + "45 57 69 64 74 68 43 31 30 30 03 "
          + "43 49 44 73 13 0e 28 74 29 af 03 29 ea 00 29 89 97 04 05 01");
private static final byte[] TOKEN_BUFFER_SEQUENCE = VPackWireFixtureTest.hex(
            "13 12 28 20 02 03 31 43 61 62 63 14 06 41 61 1a 01 04");
private static final byte[] POLYMORPHIC_NEW = VPackWireFixtureTest.hex(
            "14 2a 45 63 68 69 6c 64 14 21 45 40 74 79 70 65 46 43 68 69 6c 64 41 "
          + "44 6e 61 6d 65 4b 49 27 6d 20 63 68 69 6c 64 20 41 02 01");
private static final byte[] POLYMORPHIC_UPDATED = VPackWireFixtureTest.hex(
            "14 2f 45 63 68 69 6c 64 14 26 45 40 74 79 70 65 46 43 68 69 6c 64 41 "
          + "44 6e 61 6d 65 50 49 27 6d 20 74 68 65 20 6e 65 77 20 6e 61 6d 65 02 01");
private static final byte[] POLYMORPHIC_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 63 68 69 6c 64 18 01");
private static final byte[] POLYMORPHIC_CHANGED = VPackWireFixtureTest.hex(
            "14 2b 45 63 68 69 6c 64 14 22 45 40 74 79 70 65 46 43 68 69 6c 64 42 "
          + "44 63 6f 64 65 4c 49 27 6d 20 74 68 65 20 63 6f 64 65 02 01");
private static final byte[] ANY_SETTER_NULL = VPackWireFixtureTest.hex(
            "14 0e 49 66 69 65 6c 64 4e 61 6d 65 18 01");

    void testAnySetterNulls() throws Exception {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addDeserializer(String.class, new FunnyNullDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        AnySetter result = mapper.readValue(ANY_SETTER_NULL, AnySetter.class);
        assertEquals(Map.of("fieldName", "funny"), result.getAny());

        result = mapper.readerFor(AnySetter.class).readValue(ANY_SETTER_NULL);
        assertEquals(Map.of("fieldName", "funny"), result.getAny());
    }
private static final ObjectMapper MAPPER = new VPackMapper();
private static void verifySample(JsonParser parser) throws Exception {
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        field(parser, "Image");
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        field(parser, "Width");
        number(parser, 800);
        field(parser, "Height");
        number(parser, 600);
        field(parser, "Title");
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("View from 15th Floor", parser.getString());
        field(parser, "Thumbnail");
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        field(parser, "Url");
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("http://www.example.com/image/481989943", parser.getString());
        field(parser, "Height");
        number(parser, 125);
        field(parser, "Width");
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals("100", parser.getString());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        field(parser, "IDs");
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        number(parser, 116);
        number(parser, 943);
        number(parser, 234);
        number(parser, 38793);
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertNull(parser.nextToken());
    }
private static void field(JsonParser parser, String expected) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(expected, parser.getString());
        assertEquals(expected, parser.currentName());
    }
private static void number(JsonParser parser, int expected) throws Exception {
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(expected, parser.getIntValue());
    }
private static byte[] deeplyNestedObjects(int depth) {
        int[] lengths = new int[depth];
        int childLength = 2; // unsigned one-byte integer 42: 28 2a
        for (int i = 0; i < depth; ++i) {
            int length = 1 + 1 + 2 + childLength + 1;
            int width;
            do {
                width = forwardVarintWidth(length);
                int recalculated = 1 + width + 2 + childLength + 1;
                if (recalculated == length) {
                    break;
                }
                length = recalculated;
            } while (true);
            lengths[i] = length;
            childLength = length;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(lengths[depth - 1]);
        for (int i = depth - 1; i >= 0; --i) {
            out.write(0x14);
            writeForwardVarint(out, lengths[i]);
            out.write(0x41);
            out.write(0x61);
        }
        out.write(0x28);
        out.write(0x2a);
        for (int i = 0; i < depth; ++i) {
            out.write(0x01);
        }
        return out.toByteArray();
    }
private static int forwardVarintWidth(int value) {
        int width = 1;
        while ((value >>>= 7) != 0) {
            ++width;
        }
        return width;
    }
private static void writeForwardVarint(ByteArrayOutputStream out, int value) {
        while (value >= 128) {
            out.write((value & 0x7f) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }
static class FunnyNullDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(tools.jackson.core.JsonParser parser,
                DeserializationContext ctxt) {
            return "text";
        }

        @Override
        public String getNullValue(DeserializationContext ctxt) {
            return "funny";
        }
    }
static class AnySetter {
        private final Map<String, String> any = new HashMap<>();

        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void setAny(String name, String value) {
            any.put(name, value);
        }

        public Map<String, String> getAny() {
            return any;
        }
    }
static class Root {
        @JsonMerge
        public Child child;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = ChildA.class, name = "ChildA"),
        @JsonSubTypes.Type(value = ChildB.class, name = "ChildB")
    })
    static abstract class Child { }
static class ChildA extends Child {
        public String name;
    }
static class ChildB extends Child {
        public String code;
    }

    void __invoke_testAnySetterNulls() throws Exception {
        try {
            testAnySetterNulls();
        } finally {
        }
    }

}
