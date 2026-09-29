package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0574F0 {
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testNonDefaultWithBooleanArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 1a 1a 03"),
                CONTAINER_MAPPER.writeValueAsBytes(new BooleanArray5515Pojo(true, false, true)));
    }

    void testNonDefaultWithDoubleArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 46 76 61 6c 75 65 73 02 0b 1b 00 00 00 00 00 00 f8 3f 03"),
                CONTAINER_MAPPER.writeValueAsBytes(new DoubleArray5515Pojo(0.0, 1.5, 0.0)));
    }

    void testNonDefaultWithIntArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 31 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(new IntArray5515Pojo(0, 1, 0, 2)));
    }

    void testNonDefaultWithLongArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 31 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(new LongArray5515Pojo(0L, 1L, 0L, 2L)));
    }

    void testNonEmptyFilterWithStringArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 46 76 61 6c 75 65 73 06 0b 02 41 31 43 66 6f 6f 03 05 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new StringArray5515PojoNonEmpty("1", "foo", "")));
    }

    void testShortArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                new VPackMapper().writeValueAsBytes(new NonEmptyShortArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 01 45 76 61 6c 75 65 02 03 31 03"),
                new VPackMapper().writeValueAsBytes(new NonEmptyShortArray((short) 1)));
    }
private static ObjectMapper mapperWithFooContentFilter() {
        return VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .withConfigOverride(List.class,
                        o -> o.setInclude(JsonInclude.Value.empty()
                                .withContentFilter(FooFilter.class)))
                .build();
    }
static class BooleanArray5515Pojo {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public boolean[] values;
        BooleanArray5515Pojo(boolean... values) { this.values = values; }
    }
static class IntArray5515Pojo {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public int[] values;
        IntArray5515Pojo(int... values) { this.values = values; }
    }
static class LongArray5515Pojo {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public long[] values;
        LongArray5515Pojo(long... values) { this.values = values; }
    }
static class DoubleArray5515Pojo {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public double[] values;
        DoubleArray5515Pojo(double... values) { this.values = values; }
    }
static class StringArray5515PojoNonEmpty {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        public String[] values;
        StringArray5515PojoNonEmpty(String... values) { this.values = values; }
    }
static class NonEmptyShortArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public short[] value;
        NonEmptyShortArray(short... value) { this.value = value; }
    }
static class FooFilter {
        @Override
        public boolean equals(Object other) {
            return other != null && "foo".equals(other);
        }
    }
static class FooListBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = FooFilter.class)
        public List<String> items = new ArrayList<>();

        FooListBean add(String value) {
            items.add(value);
            return this;
        }
    }
static class SimpleList5369Bean {
        public List<String> values = new ArrayList<>();

        SimpleList5369Bean add(String value) {
            values.add(value);
            return this;
        }
    }
static class IntegerFilter {
        @Override
        public boolean equals(Object other) {
            return Integer.valueOf(42).equals(other);
        }
    }
static class IntegerListPojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = IntegerFilter.class)
        public List<Integer> values = new ArrayList<>();

        IntegerListPojo add(int value) {
            values.add(value);
            return this;
        }
    }
static class ByteFilter {
        @Override
        public boolean equals(Object other) {
            return Byte.valueOf((byte) 9).equals(other);
        }
    }
static class ByteListPojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = ByteFilter.class)
        public List<Byte> values = new ArrayList<>();

        ByteListPojo add(byte value) {
            values.add(value);
            return this;
        }
    }
static class DoubleFilter {
        @Override
        public boolean equals(Object other) {
            return Double.valueOf(1.25).equals(other);
        }
    }
static class DoubleListPojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = DoubleFilter.class)
        public List<Double> values = new ArrayList<>();

        DoubleListPojo add(double value) {
            values.add(value);
            return this;
        }
    }

    void __invoke_testNonDefaultWithBooleanArrayVpack() throws Exception {
        try {
            testNonDefaultWithBooleanArrayVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultWithDoubleArrayVpack() throws Exception {
        try {
            testNonDefaultWithDoubleArrayVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultWithIntArrayVpack() throws Exception {
        try {
            testNonDefaultWithIntArrayVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultWithLongArrayVpack() throws Exception {
        try {
            testNonDefaultWithLongArrayVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyFilterWithStringArrayVpack() throws Exception {
        try {
            testNonEmptyFilterWithStringArrayVpack();
        } finally {
        }
    }


    void __invoke_testShortArrayVpack() throws Exception {
        try {
            testShortArrayVpack();
        } finally {
        }
    }

}
