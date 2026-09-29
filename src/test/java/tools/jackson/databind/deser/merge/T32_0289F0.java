package tools.jackson.databind.deser.merge;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0289F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final byte[] OBJECT_NODE_UPDATE = VPackWireFixtureTest.hex(
            "14 1d 46 73 65 63 6f 6e 64 43 62 61 72 45 74 68 69 72 64 35 "
          + "46 66 6f 75 72 74 68 1a 03");
private static final byte[] OBJECT_NODE_MERGE = VPackWireFixtureTest.hex(
            "14 16 45 70 72 6f 70 73 14 0d 45 73 74 75 66 66 43 78 79 7a 01 01");
private static final byte[] OBJECT_DEEP_UPDATE = VPackWireFixtureTest.hex(
            "14 2c 45 70 72 6f 70 73 14 23 45 76 61 6c 75 65 1a "
          + "45 65 78 74 72 61 1b 00 00 00 00 00 80 39 40 "
          + "45 61 72 72 61 79 13 04 33 01 03 01");
private static final byte[] OBJECT_WITH_NULL = VPackWireFixtureTest.hex(
            "14 09 44 74 65 73 74 18 01");
private static final byte[] OBJECT_WITH_NUMBER = VPackWireFixtureTest.hex(
            "14 0a 44 74 65 73 74 28 7b 01");
private static final byte[] OBJECT_WITH_STRING = VPackWireFixtureTest.hex(
            "14 0b 44 74 65 73 74 42 4e 41 01");
private static final byte[] LOC_WITH_THREE = VPackWireFixtureTest.hex(
            "14 0d 43 6c 6f 63 14 06 41 62 33 01 01");
private static final byte[] LOC_WITH_TWO = VPackWireFixtureTest.hex(
            "14 0d 43 6c 6f 63 14 06 41 62 32 01 01");
private static final byte[] LOC_WITH_A_THREE = VPackWireFixtureTest.hex(
            "14 0d 43 6c 6f 63 14 06 41 61 33 01 01");
private static final byte[] CONSTRUCTOR_UPDATE = VPackWireFixtureTest.hex(
            "14 1f 4d 6d 65 72 67 65 61 62 6c 65 42 65 61 6e "
          + "14 0e 43 66 6f 6f 46 6e 65 77 46 6f 6f 01 01");
private static final byte[] ARRAY_TWO_VALUES = VPackWireFixtureTest.hex(
            "13 05 31 33 02");
private static final byte[] ARRAY_ONE_VALUE = VPackWireFixtureTest.hex(
            "13 04 39 01");
private static final byte[] ARRAY_THREE_VALUES = VPackWireFixtureTest.hex(
            "13 07 39 38 28 0e 03");
private static final byte[] BLOB_STRING = VPackWireFixtureTest.hex(
            "44 62 6c 6f 62");

    // Provenance: NodeMergeTest#testObjectNodeUpdateValue().
    void testObjectNodeUpdateValueVpack() throws Exception {
        ObjectNode base = MAPPER.createObjectNode();
        base.put("first", "foo");

        assertSame(base, MAPPER.readerForUpdating(base).readValue(OBJECT_NODE_UPDATE));
        assertEquals(4, base.size());
        assertEquals("bar", base.path("second").asString());
        assertEquals("foo", base.path("first").asString());
        assertEquals(5, base.path("third").asInt());
        assertTrue(base.path("fourth").asBoolean());
    }

    // Provenance: NodeMergeTest#testObjectNodeMerge().
    void testObjectNodeMergeVpack() throws Exception {
        ObjectNodeWrapper wrapper = MAPPER.readValue(OBJECT_NODE_MERGE,
                ObjectNodeWrapper.class);

        assertEquals(2, wrapper.props.size());
        assertEquals("enabled", wrapper.props.path("default").asString());
        assertEquals("xyz", wrapper.props.path("stuff").asString());
    }

    // Provenance: NodeMergeTest#testObjectDeepUpdate().
    void testObjectDeepUpdateVpack() throws Exception {
        ObjectNode base = MAPPER.createObjectNode();
        ObjectNode props = base.putObject("props");
        props.put("base", 123);
        props.put("value", 456);
        props.putArray("array").add(true);
        base.putNull("misc");

        assertSame(base, MAPPER.readerForUpdating(base).readValue(OBJECT_DEEP_UPDATE));
        assertEquals(2, base.size());
        ObjectNode resultProps = (ObjectNode) base.get("props");
        assertEquals(4, resultProps.size());
        assertEquals(123, resultProps.path("base").asInt());
        assertTrue(resultProps.path("value").asBoolean());
        assertEquals(25.5, resultProps.path("extra").asDouble());
        assertEquals(ArrayNode.class, resultProps.get("array").getClass());
        assertEquals(2, resultProps.get("array").size());
        assertEquals(3, resultProps.get("array").get(1).asInt());
    }

    // Provenance: NodeMergeTest#testUpdateObjectNodeWithNull().
    void testUpdateObjectNodeWithNullVpack() throws Exception {
        ObjectNode source = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("14 09 44 74 65 73 74 0a 01"));
        ObjectNode update = (ObjectNode) MAPPER.readTree(OBJECT_WITH_NULL);

        ObjectNode result = MAPPER.readerForUpdating(source).readValue(update);
        ObjectNode expected = MAPPER.createObjectNode();
        expected.set("test", expected.nullNode());
        assertEquals(expected, result);
    }

    // Provenance: NodeMergeTest#testUpdateObjectNodeWithNumber().
    void testUpdateObjectNodeWithNumberVpack() throws Exception {
        ObjectNode source = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("14 09 44 74 65 73 74 0a 01"));
        ObjectNode update = (ObjectNode) MAPPER.readTree(OBJECT_WITH_NUMBER);

        ObjectNode result = MAPPER.readerForUpdating(source).readValue(update);
        assertEquals(123, result.path("test").asInt());
        assertTrue(result.path("test").isIntegralNumber());
    }

    // Provenance: NodeMergeTest#testUpdateArrayWithNull().
    void testUpdateArrayWithNullVpack() throws Exception {
        ObjectNode source = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("14 09 44 74 65 73 74 0a 01"));
        ObjectNode result = MAPPER.readerForUpdating(source).readValue(OBJECT_WITH_NULL);

        assertTrue(result.path("test").isNull());
    }

    // Provenance: NodeMergeTest#testUpdateArrayWithString().
    void testUpdateArrayWithStringVpack() throws Exception {
        ObjectNode source = (ObjectNode) MAPPER.readTree(
                VPackWireFixtureTest.hex("14 09 44 74 65 73 74 0a 01"));
        ObjectNode result = MAPPER.readerForUpdating(source).readValue(OBJECT_WITH_STRING);

        assertEquals("NA", result.path("test").asString());
    }
static class ObjectNodeWrapper {
        @JsonMerge
        public ObjectNode props = MAPPER.createObjectNode();
        { props.put("default", "enabled"); }
    }
static class Config {
        @JsonMerge
        public AB loc = new AB(1, 2);
        protected Config() { }
        Config(int a, int b) { loc = new AB(a, b); }
    }
static class NonMergeConfig {
        public AB loc = new AB(1, 2);
    }
static class AB {
        public int a;
        public int b;
        protected AB() { }
        AB(int a, int b) { this.a = a; this.b = b; }
    }
@JsonPropertyOrder(alphabetic = true)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class ABAsArray {
        public int a;
        public int b;
    }
static class ConstructorArgsPojo {
        static class MergeablePojo {
            public String foo;
            public String bar;
            public MergeablePojo(String foo, String bar) {
                this.foo = foo;
                this.bar = bar;
            }
        }

        public MergeablePojo mergeableBean;

        @JsonCreator
        public ConstructorArgsPojo(
                @JsonMerge @JsonProperty("mergeableBean") MergeablePojo mergeableBean) {
            this.mergeableBean = mergeableBean;
        }
    }
static class User {
        public Name name;
        User(String first, String last) { name = new Name(first, last); }
    }
static class Name {
        public String first;
        public String last;
        Name(String first, String last) {
            this.first = Objects.requireNonNull(first);
            this.last = Objects.requireNonNull(last);
        }
    }

    void __invoke_testObjectNodeUpdateValueVpack() throws Exception {
        try {
            testObjectNodeUpdateValueVpack();
        } finally {
        }
    }


    void __invoke_testObjectNodeMergeVpack() throws Exception {
        try {
            testObjectNodeMergeVpack();
        } finally {
        }
    }


    void __invoke_testObjectDeepUpdateVpack() throws Exception {
        try {
            testObjectDeepUpdateVpack();
        } finally {
        }
    }


    void __invoke_testUpdateObjectNodeWithNullVpack() throws Exception {
        try {
            testUpdateObjectNodeWithNullVpack();
        } finally {
        }
    }


    void __invoke_testUpdateObjectNodeWithNumberVpack() throws Exception {
        try {
            testUpdateObjectNodeWithNumberVpack();
        } finally {
        }
    }


    void __invoke_testUpdateArrayWithNullVpack() throws Exception {
        try {
            testUpdateArrayWithNullVpack();
        } finally {
        }
    }


    void __invoke_testUpdateArrayWithStringVpack() throws Exception {
        try {
            testUpdateArrayWithStringVpack();
        } finally {
        }
    }

}
