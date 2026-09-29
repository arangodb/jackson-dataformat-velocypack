package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0575Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testCustomFilterWithListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 69 74 65 6d 73 02 06 41 31 41 32 03"),
                MAPPER.writeValueAsBytes(new FooListBean().add("1").add("foo").add("2")));
    }

    void testCustomFilterWithLongListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 28 0a 28 14 03"),
                MAPPER.writeValueAsBytes(new LongListPojo().add(10L).add(100L).add(20L)));
    }

    void testCustomFilterWithNumbersVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 47 6e 75 6d 62 65 72 73 02 04 31 33 03"),
                MAPPER.writeValueAsBytes(new NumberListBean().add(1).add(42).add(3)));
    }

    void testCustomFilterWithSetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 69 74 65 6d 73 02 06 41 31 41 32 03"),
                MAPPER.writeValueAsBytes(new FooSetBean().add("1").add("foo").add("2")));
    }

    void testCustomFilterWithShortListVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 46 76 61 6c 75 65 73 02 04 31 32 03"),
                MAPPER.writeValueAsBytes(
                        new ShortListPojo().add((short) 1).add((short) 7).add((short) 2)));
    }

    void testEmptyListWithCustomFilterVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 69 74 65 6d 73 01 03"),
                MAPPER.writeValueAsBytes(new FooListBean()));
    }

    void testEnumSetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyEnumSet()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 41 76 02 04 41 42 03"),
                MAPPER.writeValueAsBytes(new NonEmptyEnumSet(ABC.B)));
    }

    void testEnumSetWithContentFilterVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 41 41 41 42 03"),
                MAPPER.writeValueAsBytes(
                        new EnumSetBean(EnumSet.of(TestEnum.A, TestEnum.FOO, TestEnum.B))));
    }

    void testIterableNonEmptyWithWildcardElementTypeVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1c 01 46 76 61 6c 75 65 73 06 11 02 "
              + "0b 08 01 41 6b 41 76 03 02 04 41 78 03 0b 03"),
                MAPPER.writeValueAsBytes(new IterableNonEmptyBean(
                        java.util.Collections.emptyMap(),
                        java.util.Collections.singletonMap("k", "v"),
                        java.util.Collections.emptyList(),
                        java.util.Collections.singletonList("x"))));
    }

    void testIterableWithContentFilteringForNullsVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 46 76 61 6c 75 65 73 02 05 31 32 33 03"),
                MAPPER.writeValueAsBytes(new IterableNonNullBean(1, null, 2, null, 3)));
    }

    void testIterableWithContentFilteringMagicNumberVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 46 76 61 6c 75 65 73 02 05 29 f9 14 03"),
                MAPPER.writeValueAsBytes(new IterableMagicBean(1, null, 2, 3, 5369)));
    }

    void testMixedNullsAndFilteredVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 01 45 69 74 65 6d 73 06 0d 04 41 31 18 41 32 18 "
              + "03 05 06 08 03"),
                MAPPER.writeValueAsBytes(
                        new FooListBean().add("1").add(null).add("foo").add("2").add(null)));
    }
enum ABC { A, B, C }
static class NonEmptyEnumSet {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public EnumSet<ABC> v;

        NonEmptyEnumSet(ABC... values) {
            v = values.length == 0 ? EnumSet.noneOf(ABC.class)
                    : EnumSet.copyOf(Arrays.asList(values));
        }
    }
static class FooFilter {
        @Override
        public boolean equals(Object other) {
            return other != null && "foo".equals(other);
        }
    }
static class FooListBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = FooFilter.class)
        public List<String> items = new ArrayList<>();

        FooListBean add(String value) { items.add(value); return this; }
    }
static class FooSetBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = FooFilter.class)
        public Set<String> items = new LinkedHashSet<>();

        FooSetBean add(String value) { items.add(value); return this; }
    }
static class ShortFilter {
        @Override
        public boolean equals(Object other) { return Short.valueOf((short) 7).equals(other); }
    }
static class ShortListPojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = ShortFilter.class)
        public List<Short> values = new ArrayList<>();

        ShortListPojo add(short value) { values.add(value); return this; }
    }
static class LongFilter {
        @Override
        public boolean equals(Object other) { return Long.valueOf(100L).equals(other); }
    }
static class LongListPojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = LongFilter.class)
        public List<Long> values = new ArrayList<>();

        LongListPojo add(long value) { values.add(value); return this; }
    }
static class NumberFilter {
        @Override
        public boolean equals(Object other) {
            return other != null && Integer.valueOf(42).equals(other);
        }
    }
static class NumberListBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = NumberFilter.class)
        public List<Integer> numbers = new ArrayList<>();

        NumberListBean add(Integer value) { numbers.add(value); return this; }
    }
enum TestEnum { A, FOO, B }
static class EnumFilter {
        @Override
        public boolean equals(Object other) { return TestEnum.FOO.equals(other); }
    }
static class EnumSetBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = EnumFilter.class)
        public EnumSet<TestEnum> values;

        EnumSetBean(EnumSet<TestEnum> values) { this.values = values; }
    }
static class IterableValues implements Iterable<Integer> {
        private final List<Integer> values;

        IterableValues(Integer... values) { this.values = Arrays.asList(values); }
        @Override public Iterator<Integer> iterator() { return values.iterator(); }
    }
static class IterableNonNullBean {
        @JsonInclude(content = JsonInclude.Include.NON_NULL)
        public Iterable<Integer> values;

        IterableNonNullBean(Integer... values) { this.values = new IterableValues(values); }
    }
static class MagicFilter {
        @Override
        public boolean equals(Object other) { return !Integer.valueOf(5369).equals(other); }
    }
static class IterableMagicBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = MagicFilter.class)
        public Iterable<Integer> values;

        IterableMagicBean(Integer... values) { this.values = new IterableValues(values); }
    }
static class IterableNonEmptyBean {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        public Iterable<Object> values;

        IterableNonEmptyBean(Object... values) { this.values = new IterableObjects(values); }
    }
static class IterableObjects implements Iterable<Object> {
        private final List<Object> values;

        IterableObjects(Object... values) { this.values = Arrays.asList(values); }
        @Override public Iterator<Object> iterator() { return values.iterator(); }
    }

    void __invoke_testCustomFilterWithListVpack() throws Exception {
        try {
            testCustomFilterWithListVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithLongListVpack() throws Exception {
        try {
            testCustomFilterWithLongListVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithNumbersVpack() throws Exception {
        try {
            testCustomFilterWithNumbersVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithSetVpack() throws Exception {
        try {
            testCustomFilterWithSetVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithShortListVpack() throws Exception {
        try {
            testCustomFilterWithShortListVpack();
        } finally {
        }
    }


    void __invoke_testEmptyListWithCustomFilterVpack() throws Exception {
        try {
            testEmptyListWithCustomFilterVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetVpack() throws Exception {
        try {
            testEnumSetVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetWithContentFilterVpack() throws Exception {
        try {
            testEnumSetWithContentFilterVpack();
        } finally {
        }
    }


    void __invoke_testIterableNonEmptyWithWildcardElementTypeVpack() throws Exception {
        try {
            testIterableNonEmptyWithWildcardElementTypeVpack();
        } finally {
        }
    }


    void __invoke_testIterableWithContentFilteringForNullsVpack() throws Exception {
        try {
            testIterableWithContentFilteringForNullsVpack();
        } finally {
        }
    }


    void __invoke_testIterableWithContentFilteringMagicNumberVpack() throws Exception {
        try {
            testIterableWithContentFilteringMagicNumberVpack();
        } finally {
        }
    }


    void __invoke_testMixedNullsAndFilteredVpack() throws Exception {
        try {
            testMixedNullsAndFilteredVpack();
        } finally {
        }
    }

}
