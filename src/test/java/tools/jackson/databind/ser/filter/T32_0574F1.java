package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0574F1 {
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testAllFilteredOutVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 69 74 65 6d 73 01 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new FooListBean().add("foo").add("foo").add("foo")));
    }

    void testContentIncludeOverrideForCollectionVpack() throws Exception {
        ObjectMapper mapper = mapperWithFooContentFilter();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 41 31 41 32 03"),
                mapper.writeValueAsBytes(new SimpleList5369Bean().add("1").add("foo").add("2")));
    }

    void testContentIncludeOverrideForListVpack() throws Exception {
        ObjectMapper mapper = mapperWithFooContentFilter();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 41 31 41 32 03"),
                mapper.writeValueAsBytes(new SimpleList5369Bean().add("1").add("foo").add("2")));
    }

    void testCustomFilterWithByteListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 31 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new ByteListPojo().add((byte) 1).add((byte) 9).add((byte) 2)));
    }

    void testCustomFilterWithDoubleListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1f 01 46 76 61 6c 75 65 73 02 14 "
              + "1b 00 00 00 00 00 00 e0 3f "
              + "1b 00 00 00 00 00 00 04 40 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new DoubleListPojo().add(0.5).add(1.25).add(2.5)));
    }

    void testCustomFilterWithIntegerListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 31 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new IntegerListPojo().add(1).add(42).add(2)));
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

    void __invoke_testAllFilteredOutVpack() throws Exception {
        try {
            testAllFilteredOutVpack();
        } finally {
        }
    }


    void __invoke_testContentIncludeOverrideForCollectionVpack() throws Exception {
        try {
            testContentIncludeOverrideForCollectionVpack();
        } finally {
        }
    }


    void __invoke_testContentIncludeOverrideForListVpack() throws Exception {
        try {
            testContentIncludeOverrideForListVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithByteListVpack() throws Exception {
        try {
            testCustomFilterWithByteListVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithDoubleListVpack() throws Exception {
        try {
            testCustomFilterWithDoubleListVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithIntegerListVpack() throws Exception {
        try {
            testCustomFilterWithIntegerListVpack();
        } finally {
        }
    }

}
