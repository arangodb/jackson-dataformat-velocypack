package tools.jackson.databind.ser.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0581F2 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void test2572MapDefaultVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .build();
        Map<String, Integer> properties = new LinkedHashMap<>();
        properties.put("Speed", 100);
        properties.put("Weight", null);
        Car car = new Car();
        car.model = "F60";
        car.properties = properties;

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 53 70 65 65 64 28 64 03"),
                mapper.writeValueAsBytes(properties));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 26 02 45 6d 6f 64 65 6c 43 46 36 30 4a 70 72 6f 70 65 72 74 69 65 73 "
              + "0b 0c 01 45 53 70 65 65 64 28 64 03 03 0d"),
                mapper.writeValueAsBytes(car));
    }

    void test2572MapOverrideInclAlwaysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .withConfigOverride(Map.class, o -> o.setInclude(JsonInclude.Value.construct(
                        JsonInclude.Include.ALWAYS, JsonInclude.Include.ALWAYS)))
                .build();
        Map<String, Integer> properties = new LinkedHashMap<>();
        properties.put("Speed", 100);
        properties.put("Weight", null);
        Car car = new Car();
        car.model = "F60";
        car.properties = properties;

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 02 45 53 70 65 65 64 28 64 46 57 65 69 67 68 74 18 03 0b"),
                mapper.writeValueAsBytes(properties));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 2f 02 45 6d 6f 64 65 6c 43 46 36 30 4a 70 72 6f 70 65 72 74 69 65 73 "
              + "0b 15 02 45 53 70 65 65 64 28 64 46 57 65 69 67 68 74 18 03 0b 03 0d"),
                mapper.writeValueAsBytes(car));
    }
@JsonInclude(JsonInclude.Include.ALWAYS)
    static class NoNullsBean {
        private String a;
        private String b;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getA() { return a; }
        public String getB() { return b; }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class NonDefaultBean {
        public String a = "a";
        public String b = "b";
    }
@JsonPropertyOrder({ "x", "y", "z" })
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class NonDefaultBeanXYZ {
        public int x;
        public int y = 3;
        public int z = 7;
        NonDefaultBeanXYZ(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class MixedBean {
        String a = "a";
        String b = "b";

        public String getA() { return a; }
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getB() { return b; }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class ArrayBean {
        public int[] ints = new int[] { 1, 2 };
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class NonDefaultBean4741 {
        public String value;
    }
static class Foo3647 {
        public String p1 = "hello";
        public String p2 = "world";
    }
static class Bar3647 {
        @JsonValue
        @JsonIgnoreProperties("p1")
        public Foo3647 getFoo() { return new Foo3647(); }
    }
@JsonIgnoreProperties({ "a" })
    static class Bean3647 {
        public String a = "hello";
        public String b = "world";
    }
static class Container3647 {
        @JsonValue
        @JsonIgnoreProperties("b")
        public Bean3647 getBean() { return new Bean3647(); }
    }
static class Base3647 {
        public String a = "hello";
        public String b = "world";
    }
static class BaseContainer3647 {
        @JsonValue
        public Base3647 getBean() { return new Base3647(); }
    }
static class MixinContainer3647 {
        @JsonIgnoreProperties("b")
        public Base3647 getBean() { return new Base3647(); }
    }
@JsonIgnoreProperties({ "a", "b" })
    static class Mixin3647 {
        public String a = "hello";
        public String b = "world";
    }
@JsonPropertyOrder({ "model", "properties" })
    static class Car {
        public String model;
        public Map<String, Integer> properties;
    }

    void __invoke_test2572MapDefaultVpack() throws Exception {
        try {
            test2572MapDefaultVpack();
        } finally {
        }
    }


    void __invoke_test2572MapOverrideInclAlwaysVpack() throws Exception {
        try {
            test2572MapOverrideInclAlwaysVpack();
        } finally {
        }
    }

}
