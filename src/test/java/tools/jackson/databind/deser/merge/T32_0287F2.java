package tools.jackson.databind.deser.merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0287F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final byte[] MAP_1844_INITIAL = VPackWireFixtureTest.hex(
            "14 25 44 6b 65 79 31 14 0c 41 31 31 41 32 32 41 33 33 03 "
          + "44 6b 65 79 32 14 0c 41 31 31 41 32 32 41 33 33 03 02");
private static final byte[] MAP_1844_UPDATE = VPackWireFixtureTest.hex(
            "14 25 44 6b 65 79 31 14 0c 41 31 32 41 32 33 41 34 35 03 "
          + "44 6b 65 79 32 14 0c 41 31 32 41 32 33 41 34 35 03 02");
private static final byte[] SHALLOW_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 14 46 76 61 6c 75 65 73 14 0a 41 63 41 79 41 64 18 02 01");
private static final byte[] SHALLOW_INT_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 17 46 76 61 6c 75 65 73 14 0d 42 37 32 41 62 "
          + "43 36 36 36 18 02 01");
private static final byte[] DEEP_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 2f 46 76 61 6c 75 65 73 14 25 45 70 72 6f 70 73 "
          + "14 1c 41 78 43 78 79 7a 41 79 43 2e 2e 2e 45 65 78 74 72 61 "
          + "14 07 42 61 62 1a 01 03 01 01");
private static final byte[] ARRAY_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 23 46 76 61 6c 75 65 73 14 19 45 70 72 6f 70 73 "
          + "14 10 45 6e 61 6d 65 73 13 07 43 62 61 72 01 01 01 01");
private static final byte[] LIST_B_UPDATE = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 62 01 01");
private static final byte[] LIST_Y_UPDATE = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 79 01 01");
private static final byte[] CONTENT_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 16 44 64 61 74 61 14 0e 44 6e 75 6d 73 13 06 34 35 36 03 01 01");
private static final byte[] POLYMORPHIC_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 51 48 73 6f 6d 65 70 72 6f 70 45 68 6f 75 73 65 "
          + "44 64 61 74 61 14 3a 47 53 4f 4d 45 4b 45 59 14 2f "
          + "4d 64 69 73 63 72 69 6d 69 6e 61 74 6f 72 51 46 69 72 73 74 "
          + "43 6f 6e 63 72 65 74 65 49 6d 70 6c 44 6e 61 6d 65 43 6a 69 6d "
          + "41 62 32 03 01 02");

    // Provenance: MapPolymorphicMerge2336Test#testPolymorphicMapMerge().
    void testPolymorphicMapMergeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        SomeOtherClass base = new SomeOtherClass("house");
        base.addValue("SOMEKEY", new SomeClassA("fred", 1, null));
        SomeOtherClass result = mapper.readerForUpdating(base)
                .readValue(POLYMORPHIC_MAP_UPDATE);

        assertSame(base, result);
        assertSame(base.data, result.data);
        assertEquals(1, result.data.size());
        assertEquals(SomeClassA.class, result.data.get("SOMEKEY").getClass());
        assertEquals("jim", result.data.get("SOMEKEY").getName());
    }
static class MergedMap {
        @JsonMerge
        public Map<String, Object> values;

        protected MergedMap() {
            values = new LinkedHashMap<>();
            values.put("a", "x");
        }

        MergedMap(String a, String b) {
            values = new LinkedHashMap<>();
            values.put(a, b);
        }
    }
static class MergedIntMap {
        @JsonMerge
        public Map<Integer, Object> values;

        protected MergedIntMap() {
            values = new LinkedHashMap<>();
            values.put(13, "a");
        }
    }
static class MapWithListValues {
        public Map<String, List<Integer>> data;

        public MapWithListValues() {
            data = new LinkedHashMap<>();
            data.put("nums", new ArrayList<>(Arrays.asList(1, 2, 3)));
        }
    }
static class Map1844 {
        private Map<String, Integer> mapStringInteger = new LinkedHashMap<>();
        private Map<Integer, Integer> mapIntegerInteger = new LinkedHashMap<>();

        public Map<String, Integer> getMapStringInteger() {
            return mapStringInteger;
        }

        @JsonProperty("key1")
        public void setMapStringInteger(Map<String, Integer> value) {
            mapStringInteger = value;
        }

        public Map<Integer, Integer> getMapIntegerInteger() {
            return mapIntegerInteger;
        }

        @JsonProperty("key2")
        public void setMapIntegerInteger(Map<Integer, Integer> value) {
            mapIntegerInteger = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "discriminator")
    @JsonSubTypes({@JsonSubTypes.Type(value = SomeClassA.class, name = "FirstConcreteImpl")})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static abstract class SomeBaseClass {
        private String name;

        @JsonCreator
        public SomeBaseClass(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
@JsonTypeName("FirstConcreteImpl")
    public static class SomeClassA extends SomeBaseClass {
        private Integer a;
        private Integer b;

        @JsonCreator
        public SomeClassA(@JsonProperty("name") String name, @JsonProperty("a") Integer a,
                @JsonProperty("b") Integer b) {
            super(name);
            this.a = a;
            this.b = b;
        }

        public Integer getA() {
            return a;
        }

        public void setA(Integer a) {
            this.a = a;
        }

        public Integer getB() {
            return b;
        }

        public void setB(Integer b) {
            this.b = b;
        }
    }
public static class SomeOtherClass {
        String someprop;

        @JsonMerge
        Map<String, SomeBaseClass> data = new LinkedHashMap<>();

        @JsonCreator
        public SomeOtherClass(@JsonProperty("someprop") String someprop) {
            this.someprop = someprop;
        }

        public void setSomeprop(String someprop) {
            this.someprop = someprop;
        }

        public void addValue(String key, SomeBaseClass value) {
            data.put(key, value);
        }

        public Map<String, SomeBaseClass> getData() {
            return data;
        }

        public void setData(Map<String, SomeBaseClass> data) {
            this.data = data;
        }
    }

    void __invoke_testPolymorphicMapMergeVpack() throws Exception {
        try {
            testPolymorphicMapMergeVpack();
        } finally {
        }
    }

}
