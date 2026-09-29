package tools.jackson.databind.deser.merge;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0289F1 {
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

    // Provenance: PropertyMergeTest#testBeanMergingViaProp().
    void testBeanMergingViaPropVpack() throws Exception {
        Config config = MAPPER.readValue(LOC_WITH_THREE, Config.class);
        assertEquals(1, config.loc.a);
        assertEquals(3, config.loc.b);

        config = MAPPER.readerForUpdating(new Config(5, 7)).readValue(LOC_WITH_TWO);
        assertEquals(5, config.loc.a);
        assertEquals(2, config.loc.b);
    }

    // Provenance: PropertyMergeTest#testBeanMergingViaType().
    void testBeanMergingViaTypeVpack() throws Exception {
        NonMergeConfig config = MAPPER.readValue(LOC_WITH_A_THREE, NonMergeConfig.class);
        assertEquals(3, config.loc.a);
        assertEquals(0, config.loc.b);

        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(AB.class, o -> o.setMergeable(true))
                .build();
        config = mapper.readValue(LOC_WITH_A_THREE, NonMergeConfig.class);
        assertEquals(3, config.loc.a);
        assertEquals(2, config.loc.b);
    }

    // Provenance: PropertyMergeTest#testBeanMergingViaGlobal().
    void testBeanMergingViaGlobalVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().defaultMergeable(true).build();
        NonMergeConfig config = mapper.readValue(LOC_WITH_A_THREE, NonMergeConfig.class);
        assertEquals(3, config.loc.a);
        assertEquals(2, config.loc.b);

        User existing = new User("Bob", "Bush");
        User result = mapper.readerForUpdating(existing).readValue(
                VPackWireFixtureTest.hex(
                        "14 16 44 6e 61 6d 65 14 0e 44 6c 61 73 74 45 42 72 6f 77 6e 01 01"));
        assertEquals("Bob", result.name.first);
        assertEquals("Brown", result.name.last);
    }

    // Provenance: PropertyMergeTest#testBeanMergeUsingConstructors().
    void testBeanMergeUsingConstructorsVpack() throws Exception {
        ConstructorArgsPojo input = new ConstructorArgsPojo(
                new ConstructorArgsPojo.MergeablePojo("foo", "bar"));

        ConstructorArgsPojo result = VPackMapper.builder().defaultMergeable(true).build()
                .readerForUpdating(input).readValue(CONSTRUCTOR_UPDATE);
        assertEquals("newFoo", result.mergeableBean.foo);
        assertEquals("bar", result.mergeableBean.bar);
    }

    // Provenance: PropertyMergeTest#testBeanAsArrayMerging().
    void testBeanAsArrayMergingVpack() throws Exception {
        ABAsArray input = new ABAsArray();
        input.a = 4;
        input.b = 6;

        assertSame(input, MAPPER.readerForUpdating(input).readValue(ARRAY_TWO_VALUES));
        assertEquals(1, input.a);
        assertEquals(3, input.b);

        assertSame(input, MAPPER.readerForUpdating(input).readValue(ARRAY_ONE_VALUE));
        assertEquals(9, input.a);
        assertEquals(3, input.b);

        assertThrows(MismatchedInputException.class, () -> MAPPER.readerForUpdating(input)
                .with(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(ARRAY_THREE_VALUES));
        assertThrows(MismatchedInputException.class, () -> MAPPER.readerForUpdating(input)
                .readValue(BLOB_STRING));
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

    void __invoke_testBeanMergingViaPropVpack() throws Exception {
        try {
            testBeanMergingViaPropVpack();
        } finally {
        }
    }


    void __invoke_testBeanMergingViaTypeVpack() throws Exception {
        try {
            testBeanMergingViaTypeVpack();
        } finally {
        }
    }


    void __invoke_testBeanMergingViaGlobalVpack() throws Exception {
        try {
            testBeanMergingViaGlobalVpack();
        } finally {
        }
    }


    void __invoke_testBeanMergeUsingConstructorsVpack() throws Exception {
        try {
            testBeanMergeUsingConstructorsVpack();
        } finally {
        }
    }


    void __invoke_testBeanAsArrayMergingVpack() throws Exception {
        try {
            testBeanAsArrayMergingVpack();
        } finally {
        }
    }

}
