package tools.jackson.databind.ser;

import java.io.File;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0562Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] LONG_ARRAY = VPackWireFixtureTest.hex(
            "06 19 03 27 00 00 00 00 00 00 00 80 30 "
          + "2f ff ff ff ff ff ff ff 7f 03 0c 0d");
private static final byte[] LOCATION = VPackWireFixtureTest.hex(
            "0b 33 04 4a 62 79 74 65 4f 66 66 73 65 74 3f "
          + "4a 63 68 61 72 4f 66 66 73 65 74 3f "
          + "48 63 6f 6c 75 6d 6e 4e 72 28 0d "
          + "46 6c 69 6e 65 4e 72 28 64 03 0f 1b 26");
private static final byte[] SIMPLE_ANNOTATION = VPackWireFixtureTest.hex(
            "0b 11 01 46 76 61 6c 75 65 73 02 06 41 61 41 62 03");
private static final byte[] NO_AUTO_DETECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 7a 3c 03");
private static final byte[] METHOD_PRECEDENCE = VPackWireFixtureTest.hex(
            "0b 08 01 41 7a 28 0a 03");
private static final byte[] OK_DUP_FIELDS = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06");
private static final byte[] RESOLVED_DUPLICATE = VPackWireFixtureTest.hex(
            "0b 07 01 41 7a 34 03");
private static final byte[] PROTECTED_FIELDS = VPackWireFixtureTest.hex(
            "0b 1c 02 42 70 31 46 70 75 62 6c 69 63 "
          + "42 70 32 49 70 72 6f 74 65 63 74 65 64 03 0d");
private static final byte[] PROTECTED_METHODS = VPackWireFixtureTest.hex(
            "0b 0d 02 41 61 41 61 41 62 41 62 03 07");
private static final byte[] ALL_FIELDS = VPackWireFixtureTest.hex(
            "0b 28 03 42 70 31 46 70 75 62 6c 69 63 42 70 32 49 70 72 6f 74 65 63 74 65 64 "
          + "42 70 33 47 70 72 69 76 61 74 65 03 0d 1a");
private static final byte[] ALL_GETTERS = VPackWireFixtureTest.hex(
            "0b 12 03 41 61 41 61 41 62 41 62 41 63 41 63 03 07 0b");
private static final byte[] REPLACED_OBJECT = VPackWireFixtureTest.hex(
            "0b 11 02 42 69 64 31 46 70 61 72 65 6e 74 18 03 07");
private static final byte[] REPLACED_ARRAY = VPackWireFixtureTest.hex(
            "02 04 32 18");

    // Provenance: SimpleTypeSerializationTest#testLongArray().
    void testLongArrayVpack() throws Exception {
        long[] values = { Long.MIN_VALUE, 0, Long.MAX_VALUE };
        assertArrayEquals(LONG_ARRAY, MAPPER.writeValueAsBytes(values));
        assertArrayEquals(values, MAPPER.readValue(LONG_ARRAY, long[].class));
    }

    // Provenance: SimpleTypeSerializationTest#testLongStringArray().
    void testLongStringArrayVpack() throws Exception {
        final int size = 40000;
        StringBuilder builder = new StringBuilder(size * 2);
        for (int i = 0; i < size; ++i) {
            builder.append((char) i);
        }
        String value = builder.toString();
        byte[] encoded = MAPPER.writeValueAsBytes(new String[] { "abc", value, null, value });
        assertEquals(0x08, encoded[0] & 0xff,
                "the large root uses a four-byte indexed-array layout");
        try (JsonParser parser = MAPPER.createParser(encoded)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("abc", parser.getString());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(value, parser.getString());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: SimpleTypeSerializationTest#testLocation().
    void testLocationVpack() throws Exception {
        TokenStreamLocation location = new TokenStreamLocation(
                ContentReference.rawReference(new File("/tmp/test.json")),
                -1, 100, 13);
        assertArrayEquals(LOCATION, MAPPER.writeValueAsBytes(location));
        Map<?, ?> result = MAPPER.readValue(LOCATION, Map.class);
        assertEquals(Integer.valueOf(-1), result.get("charOffset"));
        assertEquals(Integer.valueOf(-1), result.get("byteOffset"));
        assertEquals(Integer.valueOf(100), result.get("lineNr"));
        assertEquals(Integer.valueOf(13), result.get("columnNr"));
        assertEquals(4, result.size());
    }

    // Provenance: SimpleTypeSerializationTest#testSimpleAnnotation().
    void testSimpleAnnotationVpack() throws Exception {
        SimpleFieldBean2 bean = new SimpleFieldBean2();
        bean.values = new String[] { "a", "b" };
        assertArrayEquals(SIMPLE_ANNOTATION, MAPPER.writeValueAsBytes(bean));
    }

    // Provenance: SimpleTypeSerializationTest#testNoAutoDetect().
    void testNoAutoDetectVpack() throws Exception {
        NoAutoDetectBean bean = new NoAutoDetectBean();
        bean._z = -4;
        assertArrayEquals(NO_AUTO_DETECT, MAPPER.writeValueAsBytes(bean));
    }

    // Provenance: SimpleTypeSerializationTest#testMethodPrecedence().
    void testMethodPrecedenceVpack() throws Exception {
        FieldAndMethodBean bean = new FieldAndMethodBean();
        bean.z = 9;
        assertEquals(10, bean.getZ());
        assertArrayEquals(METHOD_PRECEDENCE, MAPPER.writeValueAsBytes(bean));
    }

    // Provenance: SimpleTypeSerializationTest#testOkDupFields().
    void testOkDupFieldsVpack() throws Exception {
        assertArrayEquals(OK_DUP_FIELDS,
                MAPPER.writeValueAsBytes(new OkDupFieldBean(1, 2)));
    }

    // Provenance: SimpleTypeSerializationTest#testResolvedDuplicate().
    void testResolvedDuplicateVpack() throws Exception {
        assertArrayEquals(RESOLVED_DUPLICATE,
                MAPPER.writeValueAsBytes(new DupFieldBean2()));
    }

    // Provenance: SimpleTypeSerializationTest#testProtectedViaAnnotations().
    void testProtectedViaAnnotationsVpack() throws Exception {
        assertArrayEquals(PROTECTED_FIELDS,
                MAPPER.writeValueAsBytes(new ProtFieldBean()));
        assertArrayEquals(PROTECTED_METHODS,
                MAPPER.writeValueAsBytes(new ProtMethodBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testPrivateUsingGlobals().
    void testPrivateUsingGlobalsVpack() throws Exception {
        ObjectMapper fields = VPackMapper.builder()
                .changeDefaultVisibility(vc ->
                    vc.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .build();
        assertArrayEquals(ALL_FIELDS,
                fields.writeValueAsBytes(new FieldBean()));

        ObjectMapper getters = VPackMapper.builder()
                .changeDefaultVisibility(vc ->
                    vc.withGetterVisibility(JsonAutoDetect.Visibility.ANY))
                .build();
        assertArrayEquals(ALL_GETTERS,
                getters.writeValueAsBytes(new MethodBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testMapperShortcutMethods().
    void testMapperShortcutMethodsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                                JsonAutoDetect.Visibility.ANY))
                .build();
        assertArrayEquals(ALL_FIELDS,
                mapper.writeValueAsBytes(new FieldBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testReplacedCycle().
    void testReplacedCycleVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer()
                .without(SerializationFeature.FAIL_ON_SELF_REFERENCES)
                .with(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL);

        Selfie2501 object = new Selfie2501(1);
        object.parent = object;
        assertArrayEquals(REPLACED_OBJECT, writer.writeValueAsBytes(object));

        Selfie2501AsArray array = new Selfie2501AsArray(2);
        array.parent = array;
        assertArrayEquals(REPLACED_ARRAY, writer.writeValueAsBytes(array));
    }
static class SimpleFieldBean2 {
        @JsonSerialize String[] values;
    }
@JsonAutoDetect(setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
            fieldVisibility = JsonAutoDetect.Visibility.NONE)
    static class NoAutoDetectBean {
        public int x;

        @JsonProperty("z")
        public int _z;
    }
static class FieldAndMethodBean {
        @JsonProperty public int z;

        @JsonProperty("z") public int getZ() { return z + 1; }
    }
static class SimpleFieldBean {
        public int x, y;
        int z;
    }
static class OkDupFieldBean extends SimpleFieldBean {
        @JsonProperty("x")
        protected int myX;

        public int y;

        OkDupFieldBean(int x, int y) {
            myX = x;
            this.y = y;
        }
    }
static class DupFieldBean2 {
        public int z = 3;

        @JsonProperty("z")
        public int _z = 4;
    }
static class FieldBean {
        public String p1 = "public";
        protected String p2 = "protected";
        @SuppressWarnings("unused")
        private String p3 = "private";
    }
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    static class ProtFieldBean extends FieldBean { }
static class MethodBean {
        public String getA() { return "a"; }
        protected String getB() { return "b"; }
        @SuppressWarnings("unused")
        private String getC() { return "c"; }
    }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    static class ProtMethodBean extends MethodBean { }
@JsonPropertyOrder({ "id", "parent" })
    static class Selfie2501 {
        public int id;
        public Selfie2501 parent;

        Selfie2501(int id) { this.id = id; }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class Selfie2501AsArray extends Selfie2501 {
        Selfie2501AsArray(int id) { super(id); }
    }

    void __invoke_testLongArrayVpack() throws Exception {
        try {
            testLongArrayVpack();
        } finally {
        }
    }


    void __invoke_testLongStringArrayVpack() throws Exception {
        try {
            testLongStringArrayVpack();
        } finally {
        }
    }


    void __invoke_testLocationVpack() throws Exception {
        try {
            testLocationVpack();
        } finally {
        }
    }


    void __invoke_testSimpleAnnotationVpack() throws Exception {
        try {
            testSimpleAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testNoAutoDetectVpack() throws Exception {
        try {
            testNoAutoDetectVpack();
        } finally {
        }
    }


    void __invoke_testMethodPrecedenceVpack() throws Exception {
        try {
            testMethodPrecedenceVpack();
        } finally {
        }
    }


    void __invoke_testOkDupFieldsVpack() throws Exception {
        try {
            testOkDupFieldsVpack();
        } finally {
        }
    }


    void __invoke_testResolvedDuplicateVpack() throws Exception {
        try {
            testResolvedDuplicateVpack();
        } finally {
        }
    }


    void __invoke_testProtectedViaAnnotationsVpack() throws Exception {
        try {
            testProtectedViaAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testPrivateUsingGlobalsVpack() throws Exception {
        try {
            testPrivateUsingGlobalsVpack();
        } finally {
        }
    }


    void __invoke_testMapperShortcutMethodsVpack() throws Exception {
        try {
            testMapperShortcutMethodsVpack();
        } finally {
        }
    }


    void __invoke_testReplacedCycleVpack() throws Exception {
        try {
            testReplacedCycleVpack();
        } finally {
        }
    }

}
