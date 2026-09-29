package tools.jackson.databind.ser.jdk;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0584Fixture {
private static final ObjectMapper CONTENT_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static ObjectMapper orderedMapper() {
        return VPackMapper.builder()
                .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                .build();
    }

    void testDayOfWeekSetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 1e 03 46 4d 4f 4e 44 41 59 "
              + "49 57 45 44 4e 45 53 44 41 59 "
              + "46 46 52 49 44 41 59 03 0a 14"),
                orderedMapper().writeValueAsBytes(new LinkedHashSet<>(Arrays.asList(
                        java.time.DayOfWeek.FRIDAY,
                        java.time.DayOfWeek.MONDAY,
                        java.time.DayOfWeek.WEDNESDAY))));
    }

    void testEmptySetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("01"),
                orderedMapper().writeValueAsBytes(new LinkedHashSet<>()));
    }

    void testEnumSetOrderUnchangedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 1e 03 43 4e 45 57 48 52 55 4e 4e 41 42 4c 45 "
              + "4a 54 45 52 4d 49 4e 41 54 45 44 03 07 10"),
                orderedMapper().writeValueAsBytes(EnumSet.of(
                        Thread.State.TERMINATED,
                        Thread.State.NEW,
                        Thread.State.RUNNABLE)));
    }

    void testFeatureDisabledNoSortingVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 08 41 63 41 61 41 62"),
                new VPackMapper().writeValueAsBytes(
                        new LinkedHashSet<>(Arrays.asList("c", "a", "b"))));
    }

    void testLinkedHashSetSortedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 08 41 61 41 62 41 63"),
                orderedMapper().writeValueAsBytes(
                        new LinkedHashSet<>(Arrays.asList("c", "a", "b"))));
    }

    void testListNotAffectedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 08 41 63 41 61 41 62"),
                orderedMapper().writeValueAsBytes(Arrays.asList("c", "a", "b")));
    }

    void testMixedIncomparableTypesVpack() throws Exception {
        Set<Object> set = new LinkedHashSet<>();
        set.add("hello");
        set.add(42);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 0d 02 45 68 65 6c 6c 6f 28 2a 03 09"),
                VPackMapper.builder()
                        .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                        .disable(SerializationFeature.FAIL_ON_ORDER_SET_BY_INCOMPARABLE_ELEMENT)
                        .build()
                        .writeValueAsBytes(set));
    }

    void testNonComparableElementsFailVpack() {
        Set<Object> set = new LinkedHashSet<>();
        set.add(new NonComparable("x"));
        set.add(new NonComparable("y"));
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                .enable(SerializationFeature.FAIL_ON_ORDER_SET_BY_INCOMPARABLE_ELEMENT)
                .build();
        assertThrows(DatabindException.class, () -> mapper.writeValueAsBytes(set));
    }

    void testNonComparableElementsSkipVpack() throws Exception {
        Set<Object> set = new LinkedHashSet<>();
        set.add(new NonComparable("x"));
        set.add(new NonComparable("y"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 18 0b 0b 01 44 6e 61 6d 65 41 78 03 "
              + "0b 0b 01 44 6e 61 6d 65 41 79 03"),
                VPackMapper.builder()
                        .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                        .disable(SerializationFeature.FAIL_ON_ORDER_SET_BY_INCOMPARABLE_ELEMENT)
                        .build()
                        .writeValueAsBytes(set));
    }

    void testNullAndNonComparableMixedVpack() throws Exception {
        Set<Object> set = new LinkedHashSet<>();
        set.add(null);
        set.add(new NonComparable("x"));
        set.add(new NonComparable("y"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 1d 03 18 0b 0b 01 44 6e 61 6d 65 41 78 03 "
              + "0b 0b 01 44 6e 61 6d 65 41 79 03 03 04 0f"),
                VPackMapper.builder()
                        .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                        .disable(SerializationFeature.FAIL_ON_ORDER_SET_BY_INCOMPARABLE_ELEMENT)
                        .build()
                        .writeValueAsBytes(set));
    }
static class NonComparable {
        public final String name;

        NonComparable(String name) {
            this.name = name;
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_NULL)
    static class NoNullValuesMapContainer {
        public java.util.Map<String, String> stuff = new java.util.LinkedHashMap<>();

        NoNullValuesMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }
static class Wrapper497 {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY,
                value = JsonInclude.Include.NON_EMPTY)
        public StringMap497 values;

        Wrapper497(StringMap497 values) {
            this.values = values;
        }
    }
static class StringMap497 extends java.util.LinkedHashMap<String, String> {
        StringMap497 add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    void __invoke_testDayOfWeekSetVpack() throws Exception {
        try {
            testDayOfWeekSetVpack();
        } finally {
        }
    }


    void __invoke_testEmptySetVpack() throws Exception {
        try {
            testEmptySetVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetOrderUnchangedVpack() throws Exception {
        try {
            testEnumSetOrderUnchangedVpack();
        } finally {
        }
    }


    void __invoke_testFeatureDisabledNoSortingVpack() throws Exception {
        try {
            testFeatureDisabledNoSortingVpack();
        } finally {
        }
    }


    void __invoke_testLinkedHashSetSortedVpack() throws Exception {
        try {
            testLinkedHashSetSortedVpack();
        } finally {
        }
    }


    void __invoke_testListNotAffectedVpack() throws Exception {
        try {
            testListNotAffectedVpack();
        } finally {
        }
    }


    void __invoke_testMixedIncomparableTypesVpack() throws Exception {
        try {
            testMixedIncomparableTypesVpack();
        } finally {
        }
    }


    void __invoke_testNonComparableElementsFailVpack() throws Exception {
        try {
            testNonComparableElementsFailVpack();
        } finally {
        }
    }


    void __invoke_testNonComparableElementsSkipVpack() throws Exception {
        try {
            testNonComparableElementsSkipVpack();
        } finally {
        }
    }


    void __invoke_testNullAndNonComparableMixedVpack() throws Exception {
        try {
            testNullAndNonComparableMixedVpack();
        } finally {
        }
    }

}
