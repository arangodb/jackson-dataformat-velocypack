package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0577Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void intListWithNullsOnlyVpack() throws Exception {
        IntListBean bean = new IntListBean();
        bean.values = new ArrayList<>(Arrays.asList(null, null));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void iterableEmptyAfterContentFilterVpack() throws Exception {
        IterableBean bean = new IterableBean();
        bean.values = new StringIterable(null, "");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void iterableKeepsNonEmptyVpack() throws Exception {
        IterableBean bean = new IterableBean();
        bean.values = new StringIterable(null, "keep", "");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 02 07 44 6b 65 65 70 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void mapOfListsAllEmptyAfterContentFilterVpack() throws Exception {
        MapOfListsBean bean = new MapOfListsBean();
        bean.values = new LinkedHashMap<>();
        bean.values.put("a", new ArrayList<>(Arrays.asList("", "")));
        bean.values.put("b", new ArrayList<>());
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void mapOfListsKeepsNonEmptyVpack() throws Exception {
        MapOfListsBean bean = new MapOfListsBean();
        bean.values = new LinkedHashMap<>();
        bean.values.put("a", new ArrayList<>(List.of("")));
        bean.values.put("b", new ArrayList<>(Arrays.asList(null, "keep")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 18 01 46 76 61 6c 75 65 73 0b 0d 01 41 62 02 07 44 6b 65 65 70 03 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void nestedListsAllEmptyAfterContentFilterVpack() throws Exception {
        NestedListBean bean = new NestedListBean();
        bean.values = new ArrayList<>(Arrays.asList(
                new ArrayList<>(List.of("", "")),
                new ArrayList<>(Arrays.asList((String) null)),
                new ArrayList<>()));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void nestedListsKeepNonEmptyVpack() throws Exception {
        NestedListBean bean = new NestedListBean();
        bean.values = new ArrayList<>(Arrays.asList(
                new ArrayList<>(List.of("", "")),
                new ArrayList<>(Arrays.asList(null, "keep"))));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 14 01 46 76 61 6c 75 65 73 02 09 02 07 44 6b 65 65 70 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void nonNullContentAllNullsVpack() throws Exception {
        NonNullContentBean bean = new NonNullContentBean();
        bean.strings = new ArrayList<>(Arrays.asList(null, null));
        bean.numbers = new ArrayList<>(Arrays.asList((Integer) null));
        bean.stringArray = new String[] { null };
        bean.numberArray = new Integer[] { null, null };
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void nonNullContentRetainsEmptyValuesVpack() throws Exception {
        NonNullContentBean bean = new NonNullContentBean();
        bean.strings = new ArrayList<>(Arrays.asList(null, ""));
        bean.numbers = new ArrayList<>(Arrays.asList(null, 0));
        bean.stringArray = new String[] { null, "" };
        bean.numberArray = new Integer[] { null, 0 };
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 3b 04 4b 6e 75 6d 62 65 72 41 72 72 61 79 02 03 30 "
              + "47 6e 75 6d 62 65 72 73 02 03 30 "
              + "4b 73 74 72 69 6e 67 41 72 72 61 79 02 03 40 "
              + "47 73 74 72 69 6e 67 73 02 03 40 03 12 1d 2c"),
                MAPPER.writeValueAsBytes(bean));
    }

    void objectArrayKeepsValuesVpack() throws Exception {
        IntArrayBean bean = new IntArrayBean();
        bean.values = new Integer[] { null, 42 };
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 28 2a 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void objectArrayNullsOnlyVpack() throws Exception {
        IntArrayBean bean = new IntArrayBean();
        bean.values = new Integer[] { null, null };
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }

    void primitiveArraysAllDefaultVpack() throws Exception {
        PrimArraysBean bean = new PrimArraysBean();
        bean.ints = new int[] { 0, 0 };
        bean.longs = new long[] { 0L };
        bean.shorts = new short[] { 0, 0 };
        bean.doubles = new double[] { 0.0, 0.0 };
        bean.floats = new float[] { 0.0f };
        bean.bools = new boolean[] { false, false };
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), MAPPER.writeValueAsBytes(bean));
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class IntListBean {
        public List<Integer> values;
    }
static class StringIterable implements Iterable<String> {
        private final List<String> values;
        StringIterable(String... values) { this.values = Arrays.asList(values); }
        @Override public java.util.Iterator<String> iterator() { return values.iterator(); }
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class IterableBean {
        public Iterable<String> values;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class NestedListBean {
        public List<List<String>> values;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class MapOfListsBean {
        public java.util.Map<String, List<String>> values;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
    static class NonNullContentBean {
        public List<String> strings;
        public List<Integer> numbers;
        public String[] stringArray;
        public Integer[] numberArray;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_DEFAULT)
    static class PrimArraysBean {
        public int[] ints;
        public long[] longs;
        public short[] shorts;
        public double[] doubles;
        public float[] floats;
        public boolean[] bools;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class IntArrayBean {
        public Integer[] values;
    }

    void __invoke_intListWithNullsOnlyVpack() throws Exception {
        try {
            intListWithNullsOnlyVpack();
        } finally {
        }
    }


    void __invoke_iterableEmptyAfterContentFilterVpack() throws Exception {
        try {
            iterableEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_iterableKeepsNonEmptyVpack() throws Exception {
        try {
            iterableKeepsNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_mapOfListsAllEmptyAfterContentFilterVpack() throws Exception {
        try {
            mapOfListsAllEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_mapOfListsKeepsNonEmptyVpack() throws Exception {
        try {
            mapOfListsKeepsNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_nestedListsAllEmptyAfterContentFilterVpack() throws Exception {
        try {
            nestedListsAllEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_nestedListsKeepNonEmptyVpack() throws Exception {
        try {
            nestedListsKeepNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_nonNullContentAllNullsVpack() throws Exception {
        try {
            nonNullContentAllNullsVpack();
        } finally {
        }
    }


    void __invoke_nonNullContentRetainsEmptyValuesVpack() throws Exception {
        try {
            nonNullContentRetainsEmptyValuesVpack();
        } finally {
        }
    }


    void __invoke_objectArrayKeepsValuesVpack() throws Exception {
        try {
            objectArrayKeepsValuesVpack();
        } finally {
        }
    }


    void __invoke_objectArrayNullsOnlyVpack() throws Exception {
        try {
            objectArrayNullsOnlyVpack();
        } finally {
        }
    }


    void __invoke_primitiveArraysAllDefaultVpack() throws Exception {
        try {
            primitiveArraysAllDefaultVpack();
        } finally {
        }
    }

}
