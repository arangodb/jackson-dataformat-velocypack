package tools.jackson.databind.ser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0542Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] DYNA_FIELD = VPackWireFixtureTest.hex(
            "0b 15 02 42 69 64 28 7b 44 6e 61 6d 65 45 42 69 6c 6c 79 03 08");
private static final byte[] DYNA_FIELD_INPUT = VPackWireFixtureTest.hex(
            "0b 12 02 42 69 64 32 44 6e 61 6d 65 43 4a 6f 65 03 07");
private static final byte[] DYNA_FIELD_ORDERED = VPackWireFixtureTest.hex(
            "0b 30 04 42 69 64 28 7b 45 6e 61 6d 65 41 45 41 69 6c 6c 79 "
          + "45 6e 61 6d 65 42 45 42 69 6c 6c 79 45 6e 61 6d 65 43 45 43 69 6c 6c 79 "
          + "03 08 14 20");
private static final byte[] ANY_ONLY = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] ANY_NULL = VPackWireFixtureTest.hex(
            "0b 09 01 43 62 61 72 18 03");
private static final byte[] ISSUE_705 = VPackWireFixtureTest.hex(
            "0b 16 01 45 73 74 75 66 66 4b 5b 6b 65 79 2f 76 61 6c 75 65 5d 03");
private static final byte[] VALUE_SERIALIZER = VPackWireFixtureTest.hex(
            "0b 0e 01 43 6b 65 79 45 56 41 4c 55 45 03");
private static final byte[] INCLUDE_NON_EMPTY = VPackWireFixtureTest.hex(
            "0b 17 01 49 6e 6f 6e 2d 65 6d 70 74 79 48 70 72 6f 70 65 72 74 79 03");
private static final byte[] IGNORE_A_B3 = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 62 33 03 06");
private static final byte[] IGNORE_A_B2 = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 62 32 03 06");
private static final byte[] LINK_UNLINK = VPackWireFixtureTest.hex(
            "0b 0e 01 43 6b 65 79 45 76 61 6c 75 65 03");
private static final byte[] OBJECT_NODE_FIELD = VPackWireFixtureTest.hex(
            "0b 14 03 42 69 64 31 41 61 32 41 62 44 74 65 78 74 07 0a 03");
private static final byte[] OBJECT_NODE_METHOD = VPackWireFixtureTest.hex(
            "0b 11 03 42 69 64 31 41 78 1a 41 79 28 2a 03 07 0a");
private static final byte[] NULL_OBJECT_NODE = VPackWireFixtureTest.hex(
            "0b 08 01 42 69 64 31 03");

    void testDynaFieldBeanVpack() throws Exception {
        DynaFieldBean bean = new DynaFieldBean();
        bean.id = 123;
        bean.set("name", "Billy");
        assertVpack(DYNA_FIELD, MAPPER.writeValueAsBytes(bean));

        DynaFieldBean result = MAPPER.readValue(DYNA_FIELD_INPUT, DynaFieldBean.class);
        assertEquals(2, result.id);
        assertEquals("Joe", result.other.get("name"));
    }

    void testDynaFieldOrderedBeanVpack() throws Exception {
        DynaFieldOrderedBean bean = new DynaFieldOrderedBean();
        bean.set("nameC", "Cilly");
        bean.set("nameB", "Billy");
        bean.set("nameA", "Ailly");
        assertVpack(DYNA_FIELD_ORDERED, MAPPER.writeValueAsBytes(bean));
    }

    void testAnyOnlyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .build();
        assertVpack(ANY_ONLY, mapper.writeValueAsBytes(new AnyOnlyBean()));
        assertVpack(ANY_ONLY, mapper.writer()
                .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .writeValueAsBytes(new AnyOnlyBean()));
    }

    void testAnyWithNullVpack() throws Exception {
        MapAsAny bean = new MapAsAny();
        bean.add("bar", null);
        assertVpack(ANY_NULL, MAPPER.writeValueAsBytes(bean));
    }

    void testIssue705Vpack() throws Exception {
        assertVpack(ISSUE_705, MAPPER.writeValueAsBytes(new Issue705Bean("key", "value")));
    }

    void testAnyGetterWithValueSerializerVpack() throws Exception {
        Bean1124 bean = new Bean1124();
        bean.addAdditionalProperty("key", "value");
        assertVpack(VALUE_SERIALIZER, MAPPER.writeValueAsBytes(bean));
    }

    void testAnyGetterWithPropertyIncludeNonEmptyVpack() throws Exception {
        Bean2592PropertyIncludeNonEmpty bean = new Bean2592PropertyIncludeNonEmpty();
        bean.add("non-empty", "property");
        bean.add("empty", "");
        bean.add("null", null);
        assertVpack(INCLUDE_NON_EMPTY, MAPPER.writeValueAsBytes(bean));
    }

    void testIgnorePropertiesVpack() throws Exception {
        IgnorePropertiesOnFieldPojo fieldBean = new IgnorePropertiesOnFieldPojo();
        fieldBean.map.put("b", 3);
        assertVpack(IGNORE_A_B3, MAPPER.writeValueAsBytes(fieldBean));

        IgnorePropertiesOnAnyGetterPojo anyGetterBean = new IgnorePropertiesOnAnyGetterPojo();
        anyGetterBean.map.put("b", 3);
        assertVpack(IGNORE_A_B2, MAPPER.writeValueAsBytes(anyGetterBean));

        IgnoreOnFieldPojo ignoredFieldBean = new IgnoreOnFieldPojo();
        ignoredFieldBean.map.put("b", 3);
        assertVpack(IGNORE_A_B3, MAPPER.writeValueAsBytes(ignoredFieldBean));
    }

    void testLinkUnlinkWithJsonIgnoreVpack() throws Exception {
        assertVpack(LINK_UNLINK, MAPPER.writeValueAsBytes(new LinkUnlinkConflictPojo()));
    }

    void testAnyGetterWithObjectNodeFieldVpack() throws Exception {
        ObjectNodeAnyGetterFieldBean bean = new ObjectNodeAnyGetterFieldBean();
        bean.id = 1;
        bean.extra = MAPPER.createObjectNode();
        bean.extra.put("a", 2);
        bean.extra.put("b", "text");
        assertVpack(OBJECT_NODE_FIELD, MAPPER.writeValueAsBytes(bean));
    }

    void testAnyGetterWithObjectNodeMethodVpack() throws Exception {
        ObjectNodeAnyGetterMethodBean bean = new ObjectNodeAnyGetterMethodBean();
        bean.id = 1;
        ObjectNode node = MAPPER.createObjectNode();
        node.put("x", true);
        node.put("y", 42);
        bean.setExtra(node);
        assertVpack(OBJECT_NODE_METHOD, MAPPER.writeValueAsBytes(bean));
    }

    void testAnyGetterWithNullObjectNodeVpack() throws Exception {
        ObjectNodeAnyGetterFieldBean bean = new ObjectNodeAnyGetterFieldBean();
        bean.id = 1;
        bean.extra = null;
        assertVpack(NULL_OBJECT_NODE, MAPPER.writeValueAsBytes(bean));
    }
private static void assertVpack(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + toHex(actual));
    }
private static String toHex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte b : value) {
            if (result.length() != 0) result.append(' ');
            result.append(String.format("%02x", b & 0xff));
        }
        return result.toString();
    }
static class AnyOnlyBean {
        @JsonAnyGetter
        public Map<String, Integer> any() { return Map.of("a", 3); }
    }
static class MapAsAny {
        private final Map<String, Object> stuff = new LinkedHashMap<>();
        @JsonAnyGetter
        public Map<String, Object> any() { return stuff; }
        void add(String key, Object value) { stuff.put(key, value); }
    }
static class DynaFieldBean {
        public int id;
        @JsonAnyGetter
        @JsonAnySetter
        protected HashMap<String, String> other = new HashMap<>();
        void set(String name, String value) { other.put(name, value); }
    }
static class DynaFieldOrderedBean {
        public int id = 123;
        @JsonPropertyOrder(alphabetic = true)
        @JsonAnyGetter
        @JsonAnySetter
        private HashMap<String, String> other = new LinkedHashMap<>();
        void set(String name, String value) { other.put(name, value); }
    }
static class Issue705Bean {
        private final Map<String, String> stuff = new LinkedHashMap<>();
        Issue705Bean(String key, String value) { stuff.put(key, value); }
        @JsonSerialize(using = Issue705Serializer.class)
        @JsonAnyGetter
        public Map<String, String> getParameters() { return stuff; }
    }
static class Issue705Serializer extends StdSerializer<Object> {
        Issue705Serializer() { super(Map.class); }
        @Override
        public void serialize(Object value, JsonGenerator generator, SerializationContext context) {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                result.append('[').append(entry.getKey()).append('/').append(entry.getValue()).append(']');
            }
            generator.writeStringProperty("stuff", result.toString());
        }
    }
static class Bean1124 {
        private Map<String, String> additionalProperties;
        void addAdditionalProperty(String key, String value) {
            if (additionalProperties == null) additionalProperties = new HashMap<>();
            additionalProperties.put(key, value);
        }
        @JsonAnyGetter
        @JsonSerialize(contentUsing = UpperCaseSerializer.class)
        public Map<String, String> getAdditionalProperties() { return additionalProperties; }
    }
static class UpperCaseSerializer extends StdScalarSerializer<String> {
        UpperCaseSerializer() { super(String.class); }
        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext context) {
            generator.writeString(value.toUpperCase());
        }
    }
static class Bean2592NoAnnotations {
        protected final Map<String, String> properties = new LinkedHashMap<>();
        @JsonAnyGetter
        public Map<String, String> getProperties() { return properties; }
        void add(String key, String value) { properties.put(key, value); }
    }
static class Bean2592PropertyIncludeNonEmpty extends Bean2592NoAnnotations {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        @JsonAnyGetter
        @Override
        public Map<String, String> getProperties() { return properties; }
    }
@JsonIgnoreProperties("b")
    static class IgnorePropertiesOnFieldPojo {
        public int a = 1, b = 2;
        @JsonAnyGetter
        public Map<String, Object> map = new HashMap<>();
    }
@JsonPropertyOrder({ "a", "b" })
    static class IgnorePropertiesOnAnyGetterPojo {
        public int a = 1, b = 2;
        @JsonIgnoreProperties("b")
        @JsonAnyGetter
        public Map<String, Object> map = new HashMap<>();
    }
static class IgnoreOnFieldPojo {
        public int a = 1;
        @JsonIgnore
        public int b = 2;
        @JsonAnyGetter
        public Map<String, Object> map = new HashMap<>();
    }
static class LinkUnlinkConflictPojo {
        private final Map<String, Object> properties = new HashMap<>();
        @JsonAnyGetter
        public Map<String, Object> getProperties() {
            properties.put("key", "value");
            return properties;
        }
        @JsonIgnore
        public String getProperties(String key) { return "unrelated"; }
        @JsonIgnore
        public String getKey() { return "unrelated"; }
    }
static class ObjectNodeAnyGetterFieldBean {
        public int id;
        @JsonAnyGetter
        public ObjectNode extra;
    }
static class ObjectNodeAnyGetterMethodBean {
        public int id;
        private ObjectNode extra;
        @JsonAnyGetter
        public ObjectNode getExtra() { return extra; }
        public void setExtra(ObjectNode extra) { this.extra = extra; }
    }

    void __invoke_testDynaFieldBeanVpack() throws Exception {
        try {
            testDynaFieldBeanVpack();
        } finally {
        }
    }


    void __invoke_testDynaFieldOrderedBeanVpack() throws Exception {
        try {
            testDynaFieldOrderedBeanVpack();
        } finally {
        }
    }


    void __invoke_testAnyOnlyVpack() throws Exception {
        try {
            testAnyOnlyVpack();
        } finally {
        }
    }


    void __invoke_testAnyWithNullVpack() throws Exception {
        try {
            testAnyWithNullVpack();
        } finally {
        }
    }


    void __invoke_testIssue705Vpack() throws Exception {
        try {
            testIssue705Vpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterWithValueSerializerVpack() throws Exception {
        try {
            testAnyGetterWithValueSerializerVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterWithPropertyIncludeNonEmptyVpack() throws Exception {
        try {
            testAnyGetterWithPropertyIncludeNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testIgnorePropertiesVpack() throws Exception {
        try {
            testIgnorePropertiesVpack();
        } finally {
        }
    }


    void __invoke_testLinkUnlinkWithJsonIgnoreVpack() throws Exception {
        try {
            testLinkUnlinkWithJsonIgnoreVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterWithObjectNodeFieldVpack() throws Exception {
        try {
            testAnyGetterWithObjectNodeFieldVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterWithObjectNodeMethodVpack() throws Exception {
        try {
            testAnyGetterWithObjectNodeMethodVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterWithNullObjectNodeVpack() throws Exception {
        try {
            testAnyGetterWithNullObjectNodeVpack();
        } finally {
        }
    }

}
