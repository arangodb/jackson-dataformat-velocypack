package tools.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0571F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: IncludePropsForSerTest#testIncludeViaPropForUntyped().
    void testIncludeViaPropForUntypedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 31 03 03"),
                MAPPER.writeValueAsBytes(new WrapperWithPropIncludeUntyped()));
    }

    // Provenance: IncludePropsForSerTest#testIncludeWithMapProperty().
    void testIncludeWithMapPropertyVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 61 31 03 03"),
                MAPPER.writeValueAsBytes(new MapWrapper()));
    }

    // Provenance: IncludePropsForSerTest#testIncludeViaPropsAndClass().
    void testIncludeViaPropsAndClassVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 31 03 03"),
                MAPPER.writeValueAsBytes(new WrapperWithPropInclude2()));
    }

    // Provenance: IncludePropsForSerTest#testJsonPropertyOrderTakesPrecedence().
    void testJsonPropertyOrderTakesPrecedenceVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 03 41 62 32 41 61 31 41 63 33 06 03 09"),
                MAPPER.writeValueAsBytes(new IncludeWithOrderAndPropertyOrder()));
    }
private static Set<String> setOf(String... properties) {
        Set<String> set = new HashSet<>(properties.length);
        set.addAll(Arrays.asList(properties));
        return set;
    }
@JsonIncludeProperties({"x", "y"})
    static class XYZ {
        public int x = 1;
        public int y = 2;
        public int z = 3;
    }
static class WrapperWithPropIncludeUntyped {
        @JsonIncludeProperties("x")
        public Object value = new XYZ();
    }
static class MapWrapper {
        @JsonIncludeProperties("a")
        public final java.util.HashMap<String, Integer> value = new java.util.HashMap<>();

        {
            value.put("a", 1);
            value.put("b", 2);
        }
    }
static class WrapperWithPropInclude2 {
        @JsonIncludeProperties("x")
        public XYZ value = new XYZ();
    }
@JsonPropertyOrder({"b", "a", "c"})
    @JsonIncludeProperties(value = {"c", "a", "b"}, order = com.fasterxml.jackson.annotation.OptBoolean.TRUE)
    static class IncludeWithOrderAndPropertyOrder {
        public int a = 1;
        public int b = 2;
        public int c = 3;
    }
@JsonFilter("filterB")
    @JsonPropertyOrder({"a", "b", "c"})
    static class BeanB {
        public String a;
        public String b;
        public String c;

        BeanB(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
@JsonFilter("filterB")
    static class AnyBeanB {
        public String a;
        public String b;

        AnyBeanB(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }
@JsonFilter("")
    static class AnyBeanC {
        public String c;
        public String d;

        AnyBeanC(String c, String d) {
            this.c = c;
            this.d = d;
        }
    }
@JsonFilter("RootFilter")
    @JsonPropertyOrder({"a", "b"})
    static class Bean {
        public String a = "a";
        public String b = "b";
    }
@JsonFilter("checkSiblingContextFilter")
    static class CheckSiblingContextBean {
        public A a = new A();
        public B b = new B();

        @JsonFilter("checkSiblingContextFilter")
        static class A { }

        @JsonFilter("checkSiblingContextFilter")
        static class B {
            public C c = new C();

            @JsonFilter("checkSiblingContextFilter")
            static class C { }
        }
    }
static class CheckSiblingContextFilter extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsProperty(Object bean, JsonGenerator generator,
                SerializationContext context, PropertyWriter writer) throws Exception {
            TokenStreamContext streamContext = generator.streamWriteContext();
            if (writer.getName() != null && writer.getName().equals("c")) {
                org.junit.jupiter.api.Assertions.assertEquals("b",
                        streamContext.getParent().currentName());
            }
            writer.serializeAsProperty(bean, generator, context);
        }
    }

    void __invoke_testIncludeViaPropForUntypedVpack() throws Exception {
        try {
            testIncludeViaPropForUntypedVpack();
        } finally {
        }
    }


    void __invoke_testIncludeWithMapPropertyVpack() throws Exception {
        try {
            testIncludeWithMapPropertyVpack();
        } finally {
        }
    }


    void __invoke_testIncludeViaPropsAndClassVpack() throws Exception {
        try {
            testIncludeViaPropsAndClassVpack();
        } finally {
        }
    }


    void __invoke_testJsonPropertyOrderTakesPrecedenceVpack() throws Exception {
        try {
            testJsonPropertyOrderTakesPrecedenceVpack();
        } finally {
        }
    }

}
