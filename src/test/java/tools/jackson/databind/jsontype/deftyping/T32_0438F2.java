package tools.jackson.databind.jsontype.deftyping;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0438F2 {
private static final byte[] NODE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] NODE_WITH_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "14 06 41 61 01 01");
private static final byte[] TIME_UNIT_OBJECT = VPackWireFixtureTest.hex(
            "14 14 48 74 69 6d 65 55 6e 69 74 47 53 45 43 4f 4e 44 53 01");

    // Provenance: TestDefaultForEnums#testEnumAsWrapperArrayWithCreator().
    void testEnumAsWrapperArrayWithCreatorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(),
                        DefaultTyping.NON_FINAL_AND_ENUMS,
                        JsonTypeInfo.As.WRAPPER_ARRAY).build();
        Foo3569<Bar3569> expected = new Foo3569<>();
        expected.item = Bar3569.ENABLED;

        byte[] encoded = mapper.writeValueAsBytes(expected);
        assertNotNull(mapper.readValue(encoded,
                new TypeReference<Foo3569<Bar3569>>() { }));
        JavaType javaType = mapper.getTypeFactory().constructParametricType(
                Foo3569.class, new Class<?>[] { Bar3569.class });
        assertNotNull(mapper.readValue(encoded, javaType));
    }

    // Provenance: TestDefaultForEnums#testSimpleEnumBean().
    void testSimpleEnumBeanVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        TimeUnitBean literal = plain.readValue(TIME_UNIT_OBJECT, TimeUnitBean.class);
        assertEquals(TimeUnit.SECONDS, literal.timeUnit);

        TimeUnitBean input = new TimeUnitBean();
        input.timeUnit = TimeUnit.SECONDS;
        ObjectMapper typed = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE).build();
        TimeUnitBean result = typed.readValue(
                typed.writeValueAsBytes(input), TimeUnitBean.class);
        assertEquals(TimeUnit.SECONDS, result.timeUnit);
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

    void __invoke_testEnumAsWrapperArrayWithCreatorVpack() throws Exception {
        try {
            testEnumAsWrapperArrayWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testSimpleEnumBeanVpack() throws Exception {
        try {
            testSimpleEnumBeanVpack();
        } finally {
        }
    }

}
