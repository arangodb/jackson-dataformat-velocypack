package tools.jackson.databind.jsontype.deftyping;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0438F0 {
private static final byte[] NODE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] NODE_WITH_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "14 06 41 61 01 01");
private static final byte[] TIME_UNIT_OBJECT = VPackWireFixtureTest.hex(
            "14 14 48 74 69 6d 65 55 6e 69 74 47 53 45 43 4f 4e 44 53 01");

    // Provenance: PolymorphicArrays3194Test#twoDimensionalArrayViaUntyped().
    void twoDimensionalArrayViaUntypedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .polymorphicTypeValidator(OBJECT_ALLOWING_VALIDATOR)
                .build();

        String[][] input = { { "abc", "def" } };
        UntypedWrapper3195 wrapper = new UntypedWrapper3195();
        wrapper.value = input;

        UntypedWrapper3195 result = mapper.readValue(
                mapper.writeValueAsBytes(wrapper), UntypedWrapper3195.class);
        assertInstanceOf(String[][].class, result.value);
        String[][] strings = (String[][]) result.value;
        assertEquals(1, strings.length);
        assertEquals(2, strings[0].length);
        assertEquals("abc", strings[0][0]);
        assertEquals("def", strings[0][1]);
    }

    // Provenance: PolymorphicArrays3194Test#twoDimensionalArrayViaDefaultTyping().
    void twoDimensionalArrayViaDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper().build();

        ArrayBean3194 input = new ArrayBean3194();
        input.value = new String[][] { { "1.1", "1.2" }, { "2.1", "2.2" } };
        ArrayBean3194 result = mapper.readValue(
                mapper.writeValueAsBytes(input), ArrayBean3194.class);
        assertSame(String[][].class, result.value.getClass());
        assertSame(String[].class, result.value[0].getClass());
    }

    // Provenance: PolymorphicArrays3194Test#twoDimensionalPrimitiveArrayViaDefaultTyping().
    void twoDimensionalPrimitiveArrayViaDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper().build();

        UntypedBean3194 input = new UntypedBean3194();
        input.value = new int[][] { { 1, 2 }, { 3, 4 } };
        UntypedBean3194 result = mapper.readValue(
                mapper.writeValueAsBytes(input), UntypedBean3194.class);
        assertSame(int[][].class, result.value.getClass());
        int[][] values = (int[][]) result.value;
        assertEquals(2, values[0][1]);
        assertEquals(4, values[1][1]);
    }

    // Provenance: PolymorphicArrays3194Test#twoDimensionalPojoArrayViaDefaultTyping().
    void twoDimensionalPojoArrayViaDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper().build();

        ArrayBean3194 input = new ArrayBean3194();
        input.value = new Bean3194[][] {
                { new Bean3194(1, "a") }, { new Bean3194(2, "b") }
        };
        ArrayBean3194 result = mapper.readValue(
                mapper.writeValueAsBytes(input), ArrayBean3194.class);
        assertSame(Bean3194[][].class, result.value.getClass());
        assertSame(Bean3194[].class, result.value[0].getClass());
        assertEquals(1, ((Bean3194) result.value[0][0]).x);
        assertEquals("b", ((Bean3194) result.value[1][0]).y);
    }
private static VPackMapper.Builder defaultTypingMapper() {
        return VPackMapper.builder().activateDefaultTyping(OBJECT_ALLOWING_VALIDATOR,
                DefaultTyping.NON_FINAL);
    }
private static void assertPrimitiveArrayRoundTrip(ObjectMapper mapper, Object value)
            throws Exception {
        PrimitiveArrayBean input = new PrimitiveArrayBean(value);
        PrimitiveArrayBean result = mapper.readValue(
                mapper.writeValueAsBytes(input), PrimitiveArrayBean.class);
        assertNotNull(result.stuff);
        assertSame(value.getClass(), result.stuff.getClass());
    }
private static void assertArrayShape(Object value) {
        assertInstanceOf(Object[].class, value);
        Object[] outer = (Object[]) value;
        assertEquals(1, outer.length);
        assertInstanceOf(Object[].class, outer[0]);
        assertEquals(0, ((Object[]) outer[0]).length);
    }
private static final BasicPolymorphicTypeValidator OBJECT_ALLOWING_VALIDATOR =
            BasicPolymorphicTypeValidator.builder()
                    .allowIfSubTypeIsArray().allowIfSubType(Object.class).build();
static final class ArrayBean3194 {
        public Object[][] value;
    }
static final class UntypedBean3194 {
        public Object value;
    }
static final class Bean3194 {
        public int x;
        public String y;

        protected Bean3194() { }

        Bean3194(int x, String y) {
            this.x = x;
            this.y = y;
        }
    }
static class UntypedWrapper3195 {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;
    }
static class ArrayBean {
        public Object[] values;

        public ArrayBean() { this(null); }
        public ArrayBean(Object[] values) { this.values = values; }
    }
static class PrimitiveArrayBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object stuff;

        protected PrimitiveArrayBean() { }
        PrimitiveArrayBean(Object value) { stuff = value; }
    }
static final class TimeUnitBean {
        public TimeUnit timeUnit;
    }
static class Foo3569<T> {
        public T item;
    }
enum Bar3569 {
        ENABLED, DISABLED, HIDDEN;

        @JsonCreator
        public static Bar3569 fromValue(String value) {
            return valueOf(value.toUpperCase());
        }
    }
enum TestEnum { A, B }
static class NoCheckSubTypeValidator
            extends tools.jackson.databind.jsontype.PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext ctxt,
                JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_twoDimensionalArrayViaUntypedVpack() throws Exception {
        try {
            twoDimensionalArrayViaUntypedVpack();
        } finally {
        }
    }


    void __invoke_twoDimensionalArrayViaDefaultTypingVpack() throws Exception {
        try {
            twoDimensionalArrayViaDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_twoDimensionalPrimitiveArrayViaDefaultTypingVpack() throws Exception {
        try {
            twoDimensionalPrimitiveArrayViaDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_twoDimensionalPojoArrayViaDefaultTypingVpack() throws Exception {
        try {
            twoDimensionalPojoArrayViaDefaultTypingVpack();
        } finally {
        }
    }

}
