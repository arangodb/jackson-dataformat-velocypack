package tools.jackson.databind.ser.filter;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0581F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testNonNullByClassVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0b 07 01 41 62 18 03"),
                MAPPER.writeValueAsBytes(new NoNullsBean()));
    }

    void testNonDefaultByClassVpack() throws Exception {
        NonDefaultBean bean = new NonDefaultBean();
        bean.a = "notA";
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 41 61 44 6e 6f 74 41 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void testNonDefaultByClassNoCtorVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 02 41 78 31 41 79 32 03 06"),
                MAPPER.writeValueAsBytes(new NonDefaultBeanXYZ(1, 2, 0)));
    }

    void testMixedMethodVpack() throws Exception {
        MixedBean bean = new MixedBean();
        bean.a = "xyz";
        bean.b = null;
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 41 61 43 78 79 7a 03"),
                MAPPER.writeValueAsBytes(bean));

        bean.a = "a";
        bean.b = "b";
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 08 01 41 62 41 62 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void testNonEmptyDefaultArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new ArrayBean()));
    }

    void testSerialization4741Vpack() throws Exception {
        NonDefaultBean4741 bean = new NonDefaultBean4741();
        bean.value = "";
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 40 03"),
                MAPPER.writeValueAsBytes(bean));
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

    void __invoke_testNonNullByClassVpack() throws Exception {
        try {
            testNonNullByClassVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultByClassVpack() throws Exception {
        try {
            testNonDefaultByClassVpack();
        } finally {
        }
    }


    void __invoke_testNonDefaultByClassNoCtorVpack() throws Exception {
        try {
            testNonDefaultByClassNoCtorVpack();
        } finally {
        }
    }


    void __invoke_testMixedMethodVpack() throws Exception {
        try {
            testMixedMethodVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyDefaultArrayVpack() throws Exception {
        try {
            testNonEmptyDefaultArrayVpack();
        } finally {
        }
    }


    void __invoke_testSerialization4741Vpack() throws Exception {
        try {
            testSerialization4741Vpack();
        } finally {
        }
    }

}
