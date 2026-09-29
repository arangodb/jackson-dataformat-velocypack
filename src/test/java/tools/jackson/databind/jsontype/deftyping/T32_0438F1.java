package tools.jackson.databind.jsontype.deftyping;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0438F1 {
private static final byte[] NODE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] NODE_WITH_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "14 06 41 61 01 01");
private static final byte[] TIME_UNIT_OBJECT = VPackWireFixtureTest.hex(
            "14 14 48 74 69 6d 65 55 6e 69 74 47 53 45 43 4f 4e 44 53 01");

    // Provenance: TestDefaultForArrays#testArrayTypingForPrimitiveArrays().
    void testArrayTypingForPrimitiveArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .polymorphicTypeValidator(OBJECT_ALLOWING_VALIDATOR).build();
        assertPrimitiveArrayRoundTrip(mapper, new int[] { 1, 2, 3 });
        assertPrimitiveArrayRoundTrip(mapper, new long[] { 1, 2, 3 });
        assertPrimitiveArrayRoundTrip(mapper, new short[] { 1, 2, 3 });
        assertPrimitiveArrayRoundTrip(mapper, new double[] { 0.5, 5.5, -1.0 });
        assertPrimitiveArrayRoundTrip(mapper, new float[] { 0.5f, 5.5f, -1.0f });
        assertPrimitiveArrayRoundTrip(mapper, new boolean[] { true, false });
        assertPrimitiveArrayRoundTrip(mapper, new byte[] { 1, 2, 3 });
        assertPrimitiveArrayRoundTrip(mapper, new char[] { 'a', 'b' });
    }

    // Provenance: TestDefaultForArrays#testArrayTypingNested().
    void testArrayTypingNestedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_CONCRETE_AND_ARRAYS).build();
        ArrayBean input = new ArrayBean(new String[0][0]);
        ArrayBean result = mapper.readValue(
                mapper.writeValueAsBytes(input), ArrayBean.class);
        assertNotNull(result.values);
        assertSame(String[][].class, result.values.getClass());
    }

    // Provenance: TestDefaultForArrays#testArrayTypingSimple().
    void testArrayTypingSimpleVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_CONCRETE_AND_ARRAYS).build();
        ArrayBean input = new ArrayBean(new String[0]);
        ArrayBean result = mapper.readValue(
                mapper.writeValueAsBytes(input), ArrayBean.class);
        assertNotNull(result.values);
        assertSame(String[].class, result.values.getClass());
    }

    // Provenance: TestDefaultForArrays#testArraysOfArrays().
    void testArraysOfArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL).build();
        Object value = new Object[][] { new Object[] { } };
        byte[] encoded = mapper.writeValueAsBytes(value);

        assertArrayShape(mapper.readValue(encoded, Object[][].class));
        assertArrayShape(mapper.readValue(encoded, Object[].class));
        assertArrayShape(mapper.readValue(encoded, Object.class));
    }

    // Provenance: TestDefaultForArrays#testNodeInArray().
    void testNodeInArrayVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        JsonNode node = plain.readTree(NODE_OBJECT);
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.JAVA_LANG_OBJECT).build();

        Object[] result = mapper.readValue(
                mapper.writeValueAsBytes(new Object[] { node }), Object[].class);
        assertEquals(1, result.length);
        assertInstanceOf(JsonNode.class, result[0]);
    }

    // Provenance: TestDefaultForArrays#testNodeInEmptyArray().
    void testNodeInEmptyArrayVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder()
                .disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS).build();
        JsonNode node = plain.readTree(NODE_WITH_EMPTY_ARRAY);
        ObjectMapper mapper = VPackMapper.builder()
                .disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS)
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.JAVA_LANG_OBJECT).build();

        Object[] result = mapper.readValue(
                mapper.writeValueAsBytes(new Object[] { node }), Object[].class);
        assertEquals(1, result.length);
        JsonNode element = assertInstanceOf(JsonNode.class, result[0]);
        assertEquals(0, element.size());
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

    void __invoke_testArrayTypingForPrimitiveArraysVpack() throws Exception {
        try {
            testArrayTypingForPrimitiveArraysVpack();
        } finally {
        }
    }


    void __invoke_testArrayTypingNestedVpack() throws Exception {
        try {
            testArrayTypingNestedVpack();
        } finally {
        }
    }


    void __invoke_testArrayTypingSimpleVpack() throws Exception {
        try {
            testArrayTypingSimpleVpack();
        } finally {
        }
    }


    void __invoke_testArraysOfArraysVpack() throws Exception {
        try {
            testArraysOfArraysVpack();
        } finally {
        }
    }


    void __invoke_testNodeInArrayVpack() throws Exception {
        try {
            testNodeInArrayVpack();
        } finally {
        }
    }


    void __invoke_testNodeInEmptyArrayVpack() throws Exception {
        try {
            testNodeInEmptyArrayVpack();
        } finally {
        }
    }

}
