package tools.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0571F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JsonFilterTest#testAddFilterLastOneRemains().
    void testAddFilterLastOneRemainsVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("filterB", SimpleBeanPropertyFilter.serializeAll())
                .addFilter("filterB", SimpleBeanPropertyFilter.filterOutAllExcept());
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writer(provider).writeValueAsBytes(new AnyBeanB("1a", "2b")));
    }

    // Provenance: JsonFilterTest#testAddFilterLastOneRemainsFlip().
    void testAddFilterLastOneRemainsFlipVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("filterB", SimpleBeanPropertyFilter.filterOutAllExcept("a"))
                .addFilter("filterB", SimpleBeanPropertyFilter.serializeAll());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 02 41 61 42 31 61 41 62 42 32 62 03 08"),
                MAPPER.writer(provider).writeValueAsBytes(new AnyBeanB("1a", "2b")));
    }

    // Provenance: JsonFilterTest#testAddFilterWithEmptyStringId().
    void testAddFilterWithEmptyStringIdVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("", SimpleBeanPropertyFilter.filterOutAllExcept("d"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 18 02 41 63 18 41 64 4d 44 20 69 73 20 66 69 6c 74 65 72 65 64 03 06"),
                MAPPER.writer(provider).writeValueAsBytes(new AnyBeanC(null, "D is filtered")));
    }

    // Provenance: JsonFilterTest#testAddingNullFilter2ThrowsException().
    void testAddingNullFilter2ThrowsExceptionVpack() {
        FilterProvider provider = new SimpleFilterProvider().addFilter("filterB", null);
        DatabindException exception = assertThrows(DatabindException.class,
                () -> MAPPER.writer(provider).writeValueAsBytes(new AnyBeanB("1a", "2b")));
        assertTrue(exception.getMessage().contains("No filter configured with id 'filterB'"));
    }

    // Provenance: JsonFilterTest#testAddingNullFilterIdThrowsException().
    void testAddingNullFilterIdThrowsExceptionVpack() {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter(null, SimpleBeanPropertyFilter.serializeAll());
        DatabindException exception = assertThrows(DatabindException.class,
                () -> MAPPER.writer(provider).writeValueAsBytes(new AnyBeanB("1a", "2b")));
        assertTrue(exception.getMessage().contains("No filter configured with id 'filterB'"));
    }

    // Provenance: JsonFilterTest#testAllFiltersWithSameOutput().
    void testAllFiltersWithSameOutputVpack() throws Exception {
        SimpleBeanPropertyFilter[] filters = {
                SimpleBeanPropertyFilter.filterOutAllExcept("a", "b"),
                SimpleBeanPropertyFilter.filterOutAllExcept(setOf("a", "b")),
                SimpleBeanPropertyFilter.serializeAllExcept("c"),
                SimpleBeanPropertyFilter.serializeAllExcept(setOf("c")),
                new SimpleBeanPropertyFilter.SerializeExceptFilter(setOf("c")),
                SimpleBeanPropertyFilter.SerializeExceptFilter.serializeAllExcept("c"),
                SimpleBeanPropertyFilter.SerializeExceptFilter.serializeAllExcept(setOf("c")),
                SimpleBeanPropertyFilter.SerializeExceptFilter.filterOutAllExcept("a", "b"),
                SimpleBeanPropertyFilter.SerializeExceptFilter.filterOutAllExcept(setOf("a", "b")),
                new SimpleBeanPropertyFilter.FilterExceptFilter(setOf("a", "b")),
                SimpleBeanPropertyFilter.FilterExceptFilter.serializeAllExcept("c"),
                SimpleBeanPropertyFilter.FilterExceptFilter.serializeAllExcept(setOf("c")),
                SimpleBeanPropertyFilter.FilterExceptFilter.filterOutAllExcept(setOf("a", "b")),
                SimpleBeanPropertyFilter.FilterExceptFilter.filterOutAllExcept("a", "b")
        };
        for (SimpleBeanPropertyFilter filter : filters) {
            FilterProvider provider = new SimpleFilterProvider().addFilter("filterB", filter);
            assertArrayEquals(VPackWireFixtureTest.hex(
                    "0b 0f 02 41 61 42 61 61 41 62 42 62 62 03 08"),
                    MAPPER.writer(provider).writeValueAsBytes(new BeanB("aa", "bb", "cc")));
        }
    }

    // Provenance: JsonFilterTest#testCheckSiblingContextFilter().
    void testCheckSiblingContextFilterVpack() {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("checkSiblingContextFilter", new CheckSiblingContextFilter());
        ObjectMapper mapper = VPackMapper.builder()
                .filterProvider(provider)
                .build();
        mapper.valueToTree(new CheckSiblingContextBean());
    }

    // Provenance: JsonFilterTest#testDefaultFilter().
    void testDefaultFilterVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider()
                .setDefaultFilter(SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 41 62 41 62 03"),
                MAPPER.writer(provider).writeValueAsBytes(new Bean()));
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

    void __invoke_testAddFilterLastOneRemainsVpack() throws Exception {
        try {
            testAddFilterLastOneRemainsVpack();
        } finally {
        }
    }


    void __invoke_testAddFilterLastOneRemainsFlipVpack() throws Exception {
        try {
            testAddFilterLastOneRemainsFlipVpack();
        } finally {
        }
    }


    void __invoke_testAddFilterWithEmptyStringIdVpack() throws Exception {
        try {
            testAddFilterWithEmptyStringIdVpack();
        } finally {
        }
    }


    void __invoke_testAddingNullFilter2ThrowsExceptionVpack() throws Exception {
        try {
            testAddingNullFilter2ThrowsExceptionVpack();
        } finally {
        }
    }


    void __invoke_testAddingNullFilterIdThrowsExceptionVpack() throws Exception {
        try {
            testAddingNullFilterIdThrowsExceptionVpack();
        } finally {
        }
    }


    void __invoke_testAllFiltersWithSameOutputVpack() throws Exception {
        try {
            testAllFiltersWithSameOutputVpack();
        } finally {
        }
    }


    void __invoke_testCheckSiblingContextFilterVpack() throws Exception {
        try {
            testCheckSiblingContextFilterVpack();
        } finally {
        }
    }


    void __invoke_testDefaultFilterVpack() throws Exception {
        try {
            testDefaultFilterVpack();
        } finally {
        }
    }

}
