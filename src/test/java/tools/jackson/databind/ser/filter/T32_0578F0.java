package tools.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0578F0 {
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    void primitiveArraysKeepNonDefaultVpack() throws Exception {
        PrimArraysBean bean = new PrimArraysBean();
        bean.ints = new int[] { 0, 42 };
        bean.longs = new long[] { 0L, 7L };
        bean.doubles = new double[] { 0.0, 1.5 };
        bean.bools = new boolean[] { false, true };
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 35 04 45 62 6f 6f 6c 73 02 03 1a "
              + "47 64 6f 75 62 6c 65 73 02 0b 1b 00 00 00 00 00 00 f8 3f "
              + "44 69 6e 74 73 02 04 28 2a "
              + "45 6c 6f 6e 67 73 02 03 37 03 0c 1f 28"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
    }

    void stringArrayEmptyAfterContentFilterVpack() throws Exception {
        StringArrayBean bean = new StringArrayBean();
        bean.values = new String[] { null, "" };
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
    }

    void stringArrayKeepsNonEmptyVpack() throws Exception {
        StringArrayBean bean = new StringArrayBean();
        bean.values = new String[] { null, "keep", "" };
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 02 07 44 6b 65 65 70 03"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
    }

    void stringListKeepsNonEmptyVpack() throws Exception {
        Bean bean = new Bean();
        bean.myList = Arrays.asList(null, "keep", "");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 6d 79 4c 69 73 74 02 07 44 6b 65 65 70 03"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
    }

    void stringListWithEmptyStringOnlyVpack() throws Exception {
        Bean bean = new Bean();
        bean.myList = Arrays.asList("", "");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
    }

    void stringSetWithNullsOnlyVpack() throws Exception {
        StringSetBean bean = new StringSetBean();
        bean.values = java.util.Collections.singleton(null);
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                CONTAINER_MAPPER.writeValueAsBytes(bean));
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

    void __invoke_primitiveArraysKeepNonDefaultVpack() throws Exception {
        try {
            primitiveArraysKeepNonDefaultVpack();
        } finally {
        }
    }


    void __invoke_stringArrayEmptyAfterContentFilterVpack() throws Exception {
        try {
            stringArrayEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_stringArrayKeepsNonEmptyVpack() throws Exception {
        try {
            stringArrayKeepsNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_stringListKeepsNonEmptyVpack() throws Exception {
        try {
            stringListKeepsNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_stringListWithEmptyStringOnlyVpack() throws Exception {
        try {
            stringListWithEmptyStringOnlyVpack();
        } finally {
        }
    }


    void __invoke_stringSetWithNullsOnlyVpack() throws Exception {
        try {
            stringSetWithNullsOnlyVpack();
        } finally {
        }
    }

}
