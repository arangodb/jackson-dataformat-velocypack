package tools.jackson.databind.ext.jdk17;

import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0366Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] STREAM_LITERAL = VPackWireFixtureTest.hex(
            "13 09 41 61 41 62 41 63 03");
private static final byte[] STREAM_WRITE = VPackWireFixtureTest.hex(
            "02 08 41 61 41 62 41 63");
private static final byte[] OPTIONAL_INT_MAX = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 2b ff ff ff 7f 03");
private static final byte[] OPTIONAL_INT_MIN = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 23 00 00 00 80 03");
private static final byte[] OPTIONAL_DOUBLE_MAX = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b ff ff ff ff ff ff ef 7f 03");
private static final byte[] OPTIONAL_DOUBLE_MIN = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 01 00 00 00 00 00 00 00 03");
private static final byte[] OPTIONAL_DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 00 00 03");
private static final byte[] OPTIONAL_DOUBLE_NEGATIVE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 80");
private static final byte[] OPTIONAL_DOUBLE_1_5 = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 f8 3f 03");
private static final byte[] OPTIONAL_DOUBLE_2_5 = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 04 40 03");
private static final byte[] OPTIONAL_DOUBLE_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_3_14 = VPackWireFixtureTest.hex(
            "13 0c 1b 1f 85 eb 51 b8 1e 09 40 01");
private static final byte[] OPTIONAL_DOUBLE_NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] OPTIONAL_DOUBLE_POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] OPTIONAL_DOUBLE_NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");

    // Provenance: Java17CollectionsTest#testJava9StreamOf().
    void testJava9StreamOfVpack() throws Exception {
        ObjectWriter writer = MAPPER.writerFor(List.class);
        List<String> expected = List.of("a", "b", "c");

        assertEquals(expected, MAPPER.readValue(STREAM_LITERAL, List.class));

        List<String> collected = Stream.of("a", "b", "c").collect(Collectors.toList());
        assertArrayEquals(STREAM_WRITE, writer.writeValueAsBytes(collected));
        assertEquals(collected, MAPPER.readValue(writer.writeValueAsBytes(collected), List.class));

        List<String> immutable = Stream.of("a", "b", "c").toList();
        assertArrayEquals(STREAM_WRITE, writer.writeValueAsBytes(immutable));
        assertEquals(immutable, MAPPER.readValue(writer.writeValueAsBytes(immutable), List.class));
    }
static class OptionalIntBean {
        public OptionalInt value;

        public OptionalIntBean() {
            value = OptionalInt.empty();
        }

        OptionalIntBean(int value) {
            this.value = OptionalInt.of(value);
        }
    }
static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() {
            value = OptionalDouble.empty();
        }

        OptionalDoubleBean(double value) {
            this.value = OptionalDouble.of(value);
        }
    }

    void __invoke_testJava9StreamOfVpack() throws Exception {
        try {
            testJava9StreamOfVpack();
        } finally {
        }
    }

}
