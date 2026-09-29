package tools.jackson.databind.deser.merge;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0286F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] JDK_LIST_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 46 76 61 6c 75 65 73 13 05 41 78 01 01");
private static final byte[] POJO_LIST_UPDATE = VPackWireFixtureTest.hex(
            "14 1c 46 76 61 6c 75 65 73 13 12 14 0f "
          + "44 6e 61 6d 65 41 63 43 61 67 65 33 02 01 01");
private static final byte[] CUSTOM_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 10 43 6d 61 70 14 09 41 33 43 41 44 53 01 01");
private static final byte[] CREATOR_MAP_UPDATE = VPackWireFixtureTest.hex(
            "14 1d 45 69 6e 74 65 72 35 43 6d 61 70 14 09 "
          + "41 33 43 41 44 53 01 41 73 43 61 62 63 03");

    // Provenance: CustomMapMerge5237Test#customMapMerging5237().
    void customMapMerging5237Vpack() throws Exception {
        MergeMap result = MAPPER.readValue(CREATOR_MAP_UPDATE, MergeMap.class);

        assertNotNull(result);
        assertEquals(Collections.singletonMap(3, "ADS"), result.map);
        assertEquals(5, result.getInter());
        assertEquals("abc", result.s);
    }
static CustomPojo pojo(String name, int age) {
        CustomPojo value = new CustomPojo();
        value.name = name;
        value.age = age;
        return value;
    }
static class MyArrayListJDK<T> extends java.util.ArrayList<T> { }
static class MergeListJDK {
        @JsonMerge
        @JsonProperty
        public java.util.List<String> values = new MyArrayListJDK<>();
        { values.add("a"); }
    }
interface MyListCustom<T> extends java.util.List<T> { }
static class MyArrayListCustom<T> extends java.util.ArrayList<T>
            implements MyListCustom<T> { }
static class CustomPojo {
        public String name;
        public int age;
    }
static class MergeMyCustomPojoList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<CustomPojo> values = new MyArrayListCustom<>();
        {
            values.add(pojo("a", 1));
            values.add(pojo("b", 2));
        }
    }
interface MyMap4922<K, V> extends Map<K, V> { }
static class MapImpl4922<K, V> extends HashMap<K, V> implements MyMap4922<K, V> { }
static class MergeMap4922 {
        @JsonMerge
        public MyMap4922<Integer, String> map = new MapImpl4922<>();
    }
interface MyMap<K, V> extends Map<K, V> { }
static class MapImpl<K, V> extends HashMap<K, V> implements MyMap<K, V> { }
static class MergeMap {
        int inter;
        String s;

        @JsonMerge
        public MyMap<Integer, String> map = new MapImpl<>();

        @JsonCreator
        MergeMap(@JsonProperty("inter") int inter, @JsonProperty("s") String s) {
            this.inter = inter;
            this.s = s;
        }

        public int getInter() {
            return inter;
        }
    }

    void __invoke_customMapMerging5237Vpack() throws Exception {
        try {
            customMapMerging5237Vpack();
        } finally {
        }
    }

}
