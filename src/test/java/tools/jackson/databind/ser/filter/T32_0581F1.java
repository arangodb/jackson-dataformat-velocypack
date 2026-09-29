package tools.jackson.databind.ser.filter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0581F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testIgnorePropsAndJsonValueAtSameLevelVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 01 42 70 32 45 77 6f 72 6c 64 03"),
                MAPPER.writeValueAsBytes(new Bar3647()));
    }

    void testUnionOfIgnoralsVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new Container3647()));
    }

    void testMixinContainerAndJsonValueVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(BaseContainer3647.class, MixinContainer3647.class)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 41 61 45 68 65 6c 6c 6f 03"),
                mapper.writeValueAsBytes(new BaseContainer3647()));
    }

    void testMixinAndJsonValueVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Base3647.class, Mixin3647.class)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new Base3647()));
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

    void __invoke_testIgnorePropsAndJsonValueAtSameLevelVpack() throws Exception {
        try {
            testIgnorePropsAndJsonValueAtSameLevelVpack();
        } finally {
        }
    }


    void __invoke_testUnionOfIgnoralsVpack() throws Exception {
        try {
            testUnionOfIgnoralsVpack();
        } finally {
        }
    }


    void __invoke_testMixinContainerAndJsonValueVpack() throws Exception {
        try {
            testMixinContainerAndJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testMixinAndJsonValueVpack() throws Exception {
        try {
            testMixinAndJsonValueVpack();
        } finally {
        }
    }

}
