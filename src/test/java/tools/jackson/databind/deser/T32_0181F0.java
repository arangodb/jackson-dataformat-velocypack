package tools.jackson.databind.deser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0181F0 {
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

    void testTokenStreamLocation() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        TokenStreamLocation location = new TokenStreamLocation(
                ContentReference.rawReference("whatever"), -1, -1, 100, 13);

        byte[] encoded = mapper.writeValueAsBytes(location);
        TokenStreamLocation result = mapper.readValue(encoded, TokenStreamLocation.class);
        assertNotNull(result);
        assertEquals(location.getByteOffset(), result.getByteOffset());
        assertEquals(location.getCharOffset(), result.getCharOffset());
        assertEquals(location.getColumnNr(), result.getColumnNr());
        assertEquals(location.getLineNr(), result.getLineNr());
    }

    void testJavaType() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JavaType type = mapper.getTypeFactory().constructType(String.class);

        assertArrayEquals(JAVA_TYPE, mapper.writeValueAsBytes(type));
        JavaType result = mapper.readValue(JAVA_TYPE, JavaType.class);
        assertNotNull(result);
        assertEquals(String.class, result.getRawClass());
    }

    void testTokenBufferWithSample() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        try (TokenBuffer result = mapper.readValue(TOKEN_BUFFER_SAMPLE, TokenBuffer.class);
                JsonParser parser = result.asParser(ObjectReadContext.empty())) {
            verifySample(parser);
        }
    }

    void testTokenBufferWithSequence() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        try (JsonParser parser = mapper.createParser(TOKEN_BUFFER_SEQUENCE)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            TokenBuffer buffer = mapper.readValue(parser, TokenBuffer.class);
            try (JsonParser bufferParser = buffer.asParser(ObjectReadContext.empty())) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, bufferParser.nextToken());
                assertEquals(32, bufferParser.getIntValue());
                assertNull(bufferParser.nextToken());
            }

            buffer = mapper.readValue(parser, TokenBuffer.class);
            try (JsonParser bufferParser = buffer.asParser(ObjectReadContext.empty())) {
                assertEquals(JsonToken.START_ARRAY, bufferParser.nextToken());
                assertEquals(JsonToken.VALUE_NUMBER_INT, bufferParser.nextToken());
                assertEquals(1, bufferParser.getIntValue());
                assertEquals(JsonToken.END_ARRAY, bufferParser.nextToken());
                assertNull(bufferParser.nextToken());
            }

            buffer = mapper.readValue(parser, TokenBuffer.class);
            assertEquals("abc", mapper.readValue(
                    buffer.asParser(ObjectReadContext.empty()), String.class));

            buffer = mapper.readValue(parser, TokenBuffer.class);
            @SuppressWarnings("unchecked")
            Map<String, Boolean> map = mapper.readValue(
                    buffer.asParser(ObjectReadContext.empty()), Map.class);
            assertEquals(Map.of("a", Boolean.TRUE), map);

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void testDeeplyNestedObjects() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxNestingDepth(Integer.MAX_VALUE).build())
                .build();
        ObjectMapper mapper = VPackMapper.builder(factory).build();
        try (JsonParser parser = mapper.createParser(deeplyNestedObjects(25_000))) {
            assertNotNull(parser.nextToken());
            TokenBuffer buffer = TokenBuffer.forGeneration();
            buffer.copyCurrentStructure(parser);
            buffer.close();
        }
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

    void __invoke_testTokenStreamLocation() throws Exception {
        try {
            testTokenStreamLocation();
        } finally {
        }
    }


    void __invoke_testJavaType() throws Exception {
        try {
            testJavaType();
        } finally {
        }
    }


    void __invoke_testTokenBufferWithSample() throws Exception {
        try {
            testTokenBufferWithSample();
        } finally {
        }
    }


    void __invoke_testTokenBufferWithSequence() throws Exception {
        try {
            testTokenBufferWithSequence();
        } finally {
        }
    }


    void __invoke_testDeeplyNestedObjects() throws Exception {
        try {
            testDeeplyNestedObjects();
        } finally {
        }
    }

}
