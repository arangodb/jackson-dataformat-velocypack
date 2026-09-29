package tools.jackson.databind.ser.filter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0578F1 {
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    void testBrokenFilterVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> MAPPER.writeValueAsBytes(new BrokenBean(null)));
        assertTrue(failure.getMessage().contains("Problem determining whether filter"));
        assertTrue(failure.getMessage().contains("should filter out `null` values"));
    }

    void testCustomFilterWithMapVpack() throws Exception {
        FooMapBean input = new FooMapBean()
                .add("a", "1")
                .add("b", "foo")
                .add("c", "2");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 01 45 73 74 75 66 66 0b 0d 02 41 61 41 31 41 63 41 32 03 07 03"),
                MAPPER.writeValueAsBytes(input));
    }

    void testRepeatedCallsVpack() throws Exception {
        CountingFooFilter.counter.set(0);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 78 03"),
                MAPPER.writeValueAsBytes(new CountingFooBean("x")));
        assertEquals(1, CountingFooFilter.counter.get());
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new CountingFooBean("foo")));
        assertEquals(2, CountingFooFilter.counter.get());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 18 03"),
                MAPPER.writeValueAsBytes(new CountingFooBean(null)));
        assertEquals(3, CountingFooFilter.counter.get());
    }

    void testSimpleCustomFilterVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 41 78 03"),
                MAPPER.writeValueAsBytes(new FooBean("x")));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new FooBean("foo")));
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class Bean {
        public String myString;
        public List<String> myList;
        public Map<String, String> myMap;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class StringSetBean {
        public java.util.Set<String> values;
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class StringArrayBean {
        public String[] values;
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
static class FooFilter {
        @Override
        public boolean equals(Object other) {
            if (other == null) return false;
            return "foo".equals(other);
        }
    }
static class BrokenFilter {
        @Override
        public boolean equals(Object other) {
            other.toString();
            return false;
        }
    }
static class FooBean {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = FooFilter.class)
        public String value;
        FooBean(String value) { this.value = value; }
    }
static class FooMapBean {
        @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = FooFilter.class)
        public Map<String, String> stuff = new LinkedHashMap<>();
        FooMapBean add(String key, String value) { stuff.put(key, value); return this; }
    }
static class BrokenBean {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = BrokenFilter.class)
        public String value;
        BrokenBean(String value) { this.value = value; }
    }
static class CountingFooFilter {
        static final AtomicInteger counter = new AtomicInteger();
        @Override
        public boolean equals(Object other) {
            counter.incrementAndGet();
            return "foo".equals(other);
        }
    }
static class CountingFooBean {
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = CountingFooFilter.class)
        public String value;
        CountingFooBean(String value) { this.value = value; }
    }
static class JacksonFieldLevelModel {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
        String name;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
        String description;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
        String familyName;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Optional<String> getDescription() { return Optional.ofNullable(description); }
        public void setDescription(String description) { this.description = description; }
        public Optional<String> getFamilyName() { return Optional.ofNullable(familyName); }
        public void setFamilyName(String familyName) { this.familyName = familyName; }
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
    static class JacksonClassLevelModel {
        String name;
        String description;
        String familyName;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Optional<String> getDescription() { return Optional.ofNullable(description); }
        public void setDescription(String description) { this.description = description; }
        public Optional<String> getFamilyName() { return Optional.ofNullable(familyName); }
        public void setFamilyName(String familyName) { this.familyName = familyName; }
    }

    void __invoke_testBrokenFilterVpack() throws Exception {
        try {
            testBrokenFilterVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithMapVpack() throws Exception {
        try {
            testCustomFilterWithMapVpack();
        } finally {
        }
    }


    void __invoke_testRepeatedCallsVpack() throws Exception {
        try {
            testRepeatedCallsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleCustomFilterVpack() throws Exception {
        try {
            testSimpleCustomFilterVpack();
        } finally {
        }
    }

}
