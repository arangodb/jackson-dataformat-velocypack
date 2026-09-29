package tools.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.annotation.JsonAppend;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.ser.VirtualBeanPropertyWriter;
import tools.jackson.databind.util.Annotations;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0561Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] BOOLEAN_ARRAY = VPackWireFixtureTest.hex(
            "02 04 1a 19");
private static final byte[] NATIVE_BINARY = VPackWireFixtureTest.hex(
            "c0 05 01 11 fd 7f 80");
private static final byte[] BOXED_BYTE_ARRAY = VPackWireFixtureTest.hex(
            "06 10 05 31 28 11 3d 28 7f 20 80 03 04 06 07 09");
private static final byte[] CLASS_NAME = VPackWireFixtureTest.hex(
            "4e 6a 61 76 61 2e 75 74 69 6c 2e 4c 69 73 74");
private static final byte[] INT_ARRAY = VPackWireFixtureTest.hex(
            "06 0c 04 31 32 33 20 f9 03 04 05 06");
private static final byte[] DOUBLE_ARRAY = VPackWireFixtureTest.hex(
            "02 38"
          + "1b 29 5c 8f c2 f5 28 f0 3f"
          + "1b 00 00 00 00 00 00 00 40"
          + "1b 00 00 00 00 00 00 1c c0"
          + "1b 00 00 00 00 00 00 f8 7f"
          + "1b 00 00 00 00 00 00 f0 ff"
          + "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] FLOAT_ARRAY = VPackWireFixtureTest.hex(
            "02 38"
          + "1b 00 00 00 c0 f5 28 f0 3f"
          + "1b 00 00 00 00 00 00 00 40"
          + "1b 00 00 00 00 00 00 1c c0"
          + "1b 00 00 00 00 00 00 f8 7f"
          + "1b 00 00 00 00 00 00 f0 ff"
          + "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] DEFAULT_FIELD = VPackWireFixtureTest.hex(
            "0b 0e 01 42 70 31 46 70 75 62 6c 69 63 03");
private static final byte[] DEFAULT_METHOD = VPackWireFixtureTest.hex(
            "0b 08 01 41 61 41 61 03");
private static final byte[] ISSUE_240 = VPackWireFixtureTest.hex(
            "0b 0b 01 42 69 64 43 61 31 32 03");
private static final byte[] CUSTOM_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 22 03 42 69 64 46 61 62 63 31 32 33"
          + "45 65 78 74 72 61 02 04 28 2a"
          + "45 76 61 6c 75 65 28 48 0d 03 17");
private static final byte[] LINKED = VPackWireFixtureTest.hex(
            "0b 2a 02 44 6e 61 6d 65 45 66 69 72 73 74"
          + "44 6e 65 78 74 0b 15 02 44 6e 61 6d 65 44 6c 61 73 74"
          + "44 6e 65 78 74 18 03 0d 03 0e");

    // Provenance: SimpleTypeSerializationTest#testBoolean().
    void testBooleanVpack() throws Exception {
        assertArrayEquals(TRUE, MAPPER.writeValueAsBytes(Boolean.TRUE));
        assertArrayEquals(FALSE, MAPPER.writeValueAsBytes(Boolean.FALSE));
        assertEquals(Boolean.TRUE, MAPPER.readValue(TRUE, Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER.readValue(FALSE, Boolean.class));
    }

    // Provenance: SimpleTypeSerializationTest#testBooleanArray().
    void testBooleanArrayVpack() throws Exception {
        assertArrayEquals(BOOLEAN_ARRAY,
                MAPPER.writeValueAsBytes(new boolean[] { true, false }));
        assertArrayEquals(BOOLEAN_ARRAY,
                MAPPER.writeValueAsBytes(new Boolean[] { Boolean.TRUE, Boolean.FALSE }));
        assertArrayEquals(new boolean[] { true, false },
                MAPPER.readValue(BOOLEAN_ARRAY, boolean[].class));
    }

    // Provenance: SimpleTypeSerializationTest#testByteArray().
    void testByteArrayVpack() throws Exception {
        byte[] data = { 1, 17, -3, 127, -128 };
        Byte[] boxed = { 1, 17, -3, 127, -128 };
        assertArrayEquals(NATIVE_BINARY, MAPPER.writeValueAsBytes(data));
        assertArrayEquals(BOXED_BYTE_ARRAY, MAPPER.writeValueAsBytes(boxed));
        assertArrayEquals(data, MAPPER.readValue(NATIVE_BINARY, byte[].class));
        assertArrayEquals(boxed, MAPPER.readValue(BOXED_BYTE_ARRAY, Byte[].class));
    }

    // Provenance: SimpleTypeSerializationTest#testClass().
    void testClassVpack() throws Exception {
        assertArrayEquals(CLASS_NAME, MAPPER.writeValueAsBytes(java.util.List.class));
        assertEquals("java.util.List", MAPPER.readValue(CLASS_NAME, String.class));
    }

    // Provenance: SimpleTypeSerializationTest#testCustomProperties().
    void testCustomPropertiesVpack() throws Exception {
        assertArrayEquals(CUSTOM_PROPERTIES,
                MAPPER.writer().withAttribute("desc", "nice")
                        .writeValueAsBytes(new CustomVBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testDefaults().
    void testDefaultsVpack() throws Exception {
        assertArrayEquals(DEFAULT_FIELD,
                MAPPER.writeValueAsBytes(new FieldBean()));
        assertArrayEquals(DEFAULT_METHOD,
                MAPPER.writeValueAsBytes(new MethodBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testDoubleArray().
    void testDoubleArrayVpack() throws Exception {
        double[] values = { 1.01, 2.0, -7, Double.NaN,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
        assertArrayEquals(DOUBLE_ARRAY, MAPPER.writeValueAsBytes(values));
    }

    // Provenance: SimpleTypeSerializationTest#testFailureDueToDupField1().
    void testFailureDueToDupField1Vpack() {
        assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new DupFieldBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testFloatArray().
    void testFloatArrayVpack() throws Exception {
        float[] values = { 1.01f, 2.0f, -7f, Float.NaN,
                Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };
        assertArrayEquals(FLOAT_ARRAY, MAPPER.writeValueAsBytes(values));
    }

    // Provenance: SimpleTypeSerializationTest#testIntArray().
    void testIntArrayVpack() throws Exception {
        assertArrayEquals(INT_ARRAY,
                MAPPER.writeValueAsBytes(new int[] { 1, 2, 3, -7 }));
        assertArrayEquals(new int[] { 1, 2, 3, -7 },
                MAPPER.readValue(INT_ARRAY, int[].class));
    }

    // Provenance: SimpleTypeSerializationTest#testIssue240().
    void testIssue240Vpack() throws Exception {
        assertArrayEquals(ISSUE_240,
                MAPPER.writeValueAsBytes(new Item240("a12", null)));
    }

    // Provenance: SimpleTypeSerializationTest#testLinkedButNotCyclic().
    
    void testLinkedButNotCyclicVpack() throws Exception {
        CyclicBean last = new CyclicBean(null, "last");
        CyclicBean first = new CyclicBean(last, "first");
        assertArrayEquals(LINKED, MAPPER.writeValueAsBytes(first));

        java.util.Map<String, Object> map = MAPPER.readValue(LINKED, java.util.Map.class);
        assertEquals(2, map.size());
        assertEquals("first", map.get("name"));
        java.util.Map<String, Object> map2 = (java.util.Map<String, Object>) map.get("next");
        assertEquals(2, map2.size());
        assertEquals("last", map2.get("name"));
        assertNull(map2.get("next"));
    }
static class DupFieldBean {
        @JsonProperty("foo") public int _z = 1;
        @JsonSerialize private int foo = 2;
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class Item240 {
        @JsonProperty private String id;
        @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
        private String state;

        Item240(String id, String state) {
            this.id = id;
            this.state = state;
        }
    }
static class FieldBean {
        public String p1 = "public";
        protected String p2 = "protected";
        @SuppressWarnings("unused")
        private String p3 = "private";
    }
static class MethodBean {
        public String getA() { return "a"; }
        protected String getB() { return "b"; }
        @SuppressWarnings("unused")
        private String getC() { return "c"; }
    }
static class CyclicBean {
        CyclicBean _next;
        final String _name;

        CyclicBean(CyclicBean next, String name) {
            _next = next;
            _name = name;
        }

        public CyclicBean getNext() { return _next; }
        public String getName() { return _name; }
    }
static class CustomVProperty extends VirtualBeanPropertyWriter {
        private CustomVProperty() { super(); }

        private CustomVProperty(BeanPropertyDefinition propDef,
                Annotations ctxtAnn, JavaType type) {
            super(propDef, ctxtAnn, type);
        }

        @Override
        protected Object value(Object bean, JsonGenerator jgen, SerializationContext prov) {
            if (_name.toString().equals("id")) {
                return "abc123";
            }
            if (_name.toString().equals("extra")) {
                return new int[] { 42 };
            }
            return "???";
        }

        @Override
        public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config,
                AnnotatedClass declaringClass, BeanPropertyDefinition propDef,
                JavaType type) {
            return new CustomVProperty(propDef, declaringClass.getAnnotations(), type);
        }
    }
@JsonAppend(prepend = true, props = {
            @JsonAppend.Prop(value = CustomVProperty.class, name = "id"),
            @JsonAppend.Prop(value = CustomVProperty.class, name = "extra")
    })
    static class CustomVBean {
        public int value = 72;
    }

    void __invoke_testBooleanVpack() throws Exception {
        try {
            testBooleanVpack();
        } finally {
        }
    }


    void __invoke_testBooleanArrayVpack() throws Exception {
        try {
            testBooleanArrayVpack();
        } finally {
        }
    }


    void __invoke_testByteArrayVpack() throws Exception {
        try {
            testByteArrayVpack();
        } finally {
        }
    }


    void __invoke_testClassVpack() throws Exception {
        try {
            testClassVpack();
        } finally {
        }
    }


    void __invoke_testCustomPropertiesVpack() throws Exception {
        try {
            testCustomPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testDefaultsVpack() throws Exception {
        try {
            testDefaultsVpack();
        } finally {
        }
    }


    void __invoke_testDoubleArrayVpack() throws Exception {
        try {
            testDoubleArrayVpack();
        } finally {
        }
    }


    void __invoke_testFailureDueToDupField1Vpack() throws Exception {
        try {
            testFailureDueToDupField1Vpack();
        } finally {
        }
    }


    void __invoke_testFloatArrayVpack() throws Exception {
        try {
            testFloatArrayVpack();
        } finally {
        }
    }


    void __invoke_testIntArrayVpack() throws Exception {
        try {
            testIntArrayVpack();
        } finally {
        }
    }


    void __invoke_testIssue240Vpack() throws Exception {
        try {
            testIssue240Vpack();
        } finally {
        }
    }


    void __invoke_testLinkedButNotCyclicVpack() throws Exception {
        try {
            testLinkedButNotCyclicVpack();
        } finally {
        }
    }

}
