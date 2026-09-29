package tools.jackson.databind.deser.merge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0288F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .enable(tools.jackson.databind.DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
            .build();
private static final byte[] INVALID_ACCOUNT = VPackWireFixtureTest.hex(
            "14 37 48 76 61 6c 69 64 69 74 79 14 2b 49 76 61 6c 69 64 46 72 6f 6d "
          + "4a 32 30 31 38 2d 30 32 2d 30 31 47 76 61 6c 69 64 54 6f 4a 32 30 31 38 "
          + "2d 30 31 2d 33 31 02 01");
private static final byte[] PARTIAL_IMMUTABLE_UPDATE = VPackWireFixtureTest.hex(
            "14 16 47 70 61 79 6c 6f 61 64 14 0b 41 61 45 6e 65 77 2d 61 01 01");
private static final byte[] EMPTY_IMMUTABLE_UPDATE = VPackWireFixtureTest.hex(
            "14 0c 47 70 61 79 6c 6f 61 64 0a 01");
private static final byte[] CREATOR_SETTER_UPDATE = VPackWireFixtureTest.hex(
            "14 09 41 61 43 6e 65 77 01");
private static final byte[] ANY_SETTER_UPDATE = VPackWireFixtureTest.hex(
            "14 07 41 78 41 79 01");
private static final byte[] NULL_LOC_UPDATE = VPackWireFixtureTest.hex(
            "14 08 43 6c 6f 63 18 01");
private static final byte[] NULL_VALUE_UPDATE = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 18 01");
private static final byte[] ARRAY_NODE_UPDATE = VPackWireFixtureTest.hex(
            "13 0c 46 73 65 63 6f 6e 64 18 18 03");
private static final byte[] ARRAY_NODE_MERGE_UPDATE = VPackWireFixtureTest.hex(
            "14 15 44 6c 69 73 74 13 0d 29 c8 01 1a 0a 01 43 66 6f 6f 05 01");
private static final byte[] NODE_DEEP_INITIAL = VPackWireFixtureTest.hex(
            "14 1b 44 72 6f 6f 74 14 13 41 61 43 61 61 61 43 66 6f 6f 45 68 65 6c 6c 6f 02 01");
private static final byte[] NODE_DEEP_UPDATE = VPackWireFixtureTest.hex(
            "14 1d 44 72 6f 6f 74 14 15 41 62 43 62 62 62 43 66 6f 6f 47 67 6f 6f 64 62 79 65 02 01");

    // Provenance: NodeMergeTest#testArrayNodeUpdateValue().
    void testArrayNodeUpdateValueVpack() throws Exception {
        ArrayNode base = MAPPER.createArrayNode();
        base.add("first");
        assertSame(base, MAPPER.readerForUpdating(base).readValue(ARRAY_NODE_UPDATE));
        assertEquals(4, base.size());
        assertEquals("first", base.path(0).asString());
        assertEquals("second", base.path(1).asString());
        assertFalse(base.path(2).asBoolean());
        assertTrue(base.path(3).isNull());
    }

    // Provenance: NodeMergeTest#testArrayNodeMerge().
    void testArrayNodeMergeVpack() throws Exception {
        ArrayNodeWrapper result = MAPPER.readValue(ARRAY_NODE_MERGE_UPDATE,
                ArrayNodeWrapper.class);
        assertEquals(6, result.list.size());
        assertEquals(123, result.list.get(0).asInt());
        assertEquals(456, result.list.get(1).asInt());
        assertTrue(result.list.get(2).asBoolean());
        JsonNode node = result.list.get(3);
        assertTrue(node.isObject());
        assertEquals(0, node.size());
        node = result.list.get(4);
        assertTrue(node.isArray());
        assertEquals(0, node.size());
        assertEquals("foo", result.list.get(5).asString());
    }

    // Provenance: NodeMergeTest#testObjectDeepMerge3122().
    void testObjectDeepMerge3122Vpack() throws Exception {
        JsonNode node = MAPPER.readTree(NODE_DEEP_INITIAL);
        JsonNode result = MAPPER.readerForUpdating(node).readValue(NODE_DEEP_UPDATE);
        assertMergedDeepNode(node, result);

        node = MAPPER.readTree(NODE_DEEP_INITIAL);
        result = MAPPER.readerForUpdating(node).readTree(NODE_DEEP_UPDATE);
        assertMergedDeepNode(node, result);

        node = MAPPER.readTree(NODE_DEEP_INITIAL);
        try (JsonParser parser = MAPPER.tokenStreamFactory().createParser(NODE_DEEP_UPDATE)) {
            result = MAPPER.readerForUpdating(node).readTree(parser);
        }
        assertMergedDeepNode(node, result);
    }
private static void assertMergedDeepNode(JsonNode original, JsonNode result) {
        assertSame(original, result);
        assertEquals(1, result.size());
        ObjectNode root = (ObjectNode) result.get("root");
        assertEquals(3, root.size());
        assertEquals("aaa", root.path("a").asString());
        assertEquals("goodbye", root.path("foo").asString());
        assertEquals("bbb", root.path("b").asString());
    }
static class Account {
        @JsonMerge
        private final Validity validity;

        @JsonCreator
        public Account(@JsonProperty(value = "validity", required = true) Validity validity) {
            this.validity = validity;
        }

        public Validity getValidity() { return validity; }
    }
static class Validity {
        static final String VALID_TO_CANT_BE_BEFORE_VALID_FROM =
                "Valid to can't be before valid from";
        private final String validFrom;
        private final String validTo;

        @JsonCreator
        public Validity(@JsonProperty(value = "validFrom", required = true) String validFrom,
                @JsonProperty("validTo") String validTo) {
            Objects.requireNonNull(validFrom, "Valid from can't be null");
            if (validTo != null && validFrom.compareTo(validTo) > 0) {
                throw new IllegalStateException(VALID_TO_CANT_BE_BEFORE_VALID_FROM);
            }
            this.validFrom = validFrom;
            this.validTo = validTo;
        }

        public String getValidFrom() { return validFrom; }
        public String getValidTo() { return validTo; }
    }
static class MergeWrapper {
        @JsonMerge
        private OptionalFields payload;
        public OptionalFields getPayload() { return payload; }
        public void setPayload(OptionalFields payload) { this.payload = payload; }
    }
static class OptionalFields {
        public final String a;
        public final String b;
        @JsonCreator
        public OptionalFields(@JsonProperty("a") String a, @JsonProperty("b") String b) {
            this.a = a;
            this.b = b;
        }
    }
static class CreatorPlusSetter {
        private String a;
        @JsonCreator
        public CreatorPlusSetter(@JsonProperty("a") String a) { this.a = a; }
        public String getA() { return a; }
        public void setA(String a) { this.a = a; }
    }
static class CreatorPlusAnySetter {
        public final String a;
        public final Map<String, Object> extras = new LinkedHashMap<>();
        @JsonCreator
        public CreatorPlusAnySetter(@JsonProperty("a") String a) { this.a = a; }
        @JsonAnySetter
        public void put(String key, Object value) { extras.put(key, value); }
    }
static class ConfigDefault {
        @JsonMerge
        public AB loc = new AB(1, 2);
        protected ConfigDefault() { }
        public ConfigDefault(int a, int b) { loc = new AB(a, b); }
    }
static class ConfigSkipNull {
        @JsonMerge
        @JsonSetter(nulls = Nulls.SKIP)
        public AB loc = new AB(1, 2);
        protected ConfigSkipNull() { }
        public ConfigSkipNull(int a, int b) { loc = new AB(a, b); }
    }
static class ConfigAllowNullOverwrite {
        @JsonMerge
        @JsonSetter(nulls = Nulls.SET)
        public AB loc = new AB(1, 2);
        protected ConfigAllowNullOverwrite() { }
        public ConfigAllowNullOverwrite(int a, int b) { loc = new AB(a, b); }
    }
static class NoSetterConfig {
        AB _value = new AB(2, 3);
        @JsonMerge
        public AB getValue() { return _value; }
    }
static class AB {
        public int a;
        public int b;
        protected AB() { }
        public AB(int a, int b) { this.a = a; this.b = b; }
    }
static class ArrayNodeWrapper {
        @JsonMerge
        public ArrayNode list = MAPPER.createArrayNode();
        { list.add(123); }
    }

    void __invoke_testArrayNodeUpdateValueVpack() throws Exception {
        try {
            testArrayNodeUpdateValueVpack();
        } finally {
        }
    }


    void __invoke_testArrayNodeMergeVpack() throws Exception {
        try {
            testArrayNodeMergeVpack();
        } finally {
        }
    }


    void __invoke_testObjectDeepMerge3122Vpack() throws Exception {
        try {
            testObjectDeepMerge3122Vpack();
        } finally {
        }
    }

}
