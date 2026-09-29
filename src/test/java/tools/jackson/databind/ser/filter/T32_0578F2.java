package tools.jackson.databind.ser.filter;

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

class T32_0578F2 {
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();

    void testWithClassLevelConfigurationVpack() throws Exception {
        JacksonClassLevelModel model = new JacksonClassLevelModel();
        model.setName("");
        model.setDescription("");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(model));
    }

    void testWithFieldConfigurationVpack() throws Exception {
        JacksonFieldLevelModel model = new JacksonFieldLevelModel();
        model.setName("");
        model.setDescription("");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(model));
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

    void __invoke_testWithClassLevelConfigurationVpack() throws Exception {
        try {
            testWithClassLevelConfigurationVpack();
        } finally {
        }
    }


    void __invoke_testWithFieldConfigurationVpack() throws Exception {
        try {
            testWithFieldConfigurationVpack();
        } finally {
        }
    }

}
