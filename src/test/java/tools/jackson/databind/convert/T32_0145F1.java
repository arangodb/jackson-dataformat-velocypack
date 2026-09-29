package tools.jackson.databind.convert;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.util.StdConverter;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0145F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INTEGRAL_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 31 3f 30 28 62 28 7f 05");
private static final byte[] UNKNOWN_PROPERTY = VPackWireFixtureTest.hex(
            "14 10 4b 75 6e 6b 6e 6f 77 6e 50 72 6f 70 1a 01");
private static final byte[] INVALID_BOOLEAN = VPackWireFixtureTest.hex(
            "14 13 48 62 6f 6f 6c 50 72 6f 70 46 66 6f 6f 62 61 72 01");
private static final byte[] CONVERTED_BEAN = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 32 41 62 34 03 06");
private static final byte[] EMPTY_ARRAY_FIELD = VPackWireFixtureTest.hex(
            "14 0a 45 66 69 65 6c 64 01 01");
private static final byte[] NESTED_EMPTY_ARRAY_FIELD = VPackWireFixtureTest.hex(
            "14 0d 45 66 69 65 6c 64 13 04 01 01 01");
private static final byte[] MAP_EMPTY_ARRAY_FIELD = VPackWireFixtureTest.hex(
            "14 13 45 66 69 65 6c 64 14 0a 45 66 69 65 6c 64 01 01 01");

    void beanConvert() {
        PointZ point = MAPPER.convertValue(new PointStrings("37", "-9"), PointZ.class);
        assertEquals(37, point.x);
        assertEquals(-9, point.y);
        assertEquals(-13, point.z);
    }

    void errorReporting() {
        UnrecognizedPropertyException unknown = assertThrows(UnrecognizedPropertyException.class,
                () -> VPackMapper.builder()
                        .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .build().readValue(UNKNOWN_PROPERTY, BooleanBean.class));
        assertEquals(true, unknown.getMessage().contains("unknownProp"));

        InvalidFormatException invalid = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(INVALID_BOOLEAN, BooleanBean.class));
        assertEquals(true, invalid.getMessage().contains(
                "Cannot deserialize value of type `boolean` from String"));
    }

    void issue458() {
        ObjectWrapper a = new ObjectWrapper("foo");
        ObjectWrapper b = new ObjectWrapper(a);
        ObjectWrapper b2 = MAPPER.convertValue(b, ObjectWrapper.class);
        ObjectWrapper a2 = MAPPER.convertValue(b2.getData(), ObjectWrapper.class);
        assertEquals("foo", a2.getData());
    }

    void wrapping() {
        VPackMapper wrappingMapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();
        verifyPoint(wrappingMapper);

        wrappingMapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .disable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();
        verifyPoint(wrappingMapper);

        wrappingMapper = VPackMapper.builder()
                .disable(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();
        verifyPoint(wrappingMapper);
    }

    void convertUsingCast() {
        String str = new String("foo");
        CharSequence seq = str;
        assertSame(str, MAPPER.convertValue(seq, String.class));
    }

    void issue11() {
        StringBuilder builder = new StringBuilder("test");
        CharSequence seq = MAPPER.convertValue(builder, CharSequence.class);
        assertNotSame(builder, seq);

        Leaf leaf = new Leaf(13);
        Map<?, ?> map = MAPPER.convertValue(leaf, Map.class);
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals(Integer.valueOf(13), map.get("value"));

        Leaf reverse = MAPPER.convertValue(map, Leaf.class);
        assertEquals(13, reverse.value);

        Object untyped = MAPPER.convertValue(leaf, Object.class);
        assertNotNull(untyped);
        assertEquals(LinkedHashMap.class, untyped.getClass());

        Object plain = new Object();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(plain));
        map = MAPPER.convertValue(plain, Map.class);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    void conversionIssue288() throws Exception {
        assertArrayEquals(CONVERTED_BEAN,
                MAPPER.writeValueAsBytes(new ConvertingBean(1, 2)));
    }

    void conversionIssue1433() {
        assertNull(MAPPER.convertValue(null, Object.class));
        assertNull(MAPPER.convertValue(null, PointZ.class));
        assertSame(NullBean.NULL_INSTANCE, MAPPER.convertValue(null, NullBean.class));
    }
private static void verifyPoint(ObjectMapper mapper) {
        PointZ input = new PointZ(1, 2, 3);
        PointZ output = mapper.convertValue(input, PointZ.class);
        assertEquals(1, output.x);
        assertEquals(2, output.y);
        assertEquals(3, output.z);
    }
private static VPackMapper stringFailMapper() {
        return VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.String, CoercionAction.Fail))
                .build();
    }
private static void verifyStartArrayFailure(VPackMapper mapper, byte[] input,
            JavaType targetType) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(input, targetType));
        assertEquals(true, failure.getMessage().contains("from Array value"), failure.getMessage());
        assertEquals(true, failure.getMessage().contains("JsonToken.START_ARRAY"),
                failure.getMessage());
    }
static class PointZ {
        public int x, y;
        public int z = -13;
        PointZ() { }
        PointZ(int x, int y, int z) { this.x = x; this.y = y; this.z = z; }
    }
static class Wrapper6040<T> {
        public T field;
    }
static class PointStrings {
        public final String x, y;
        PointStrings(String x, String y) { this.x = x; this.y = y; }
    }
public static class BooleanBean {
        public boolean boolProp;
    }
static class ObjectWrapper {
        private Object data;
        ObjectWrapper() { }
        ObjectWrapper(Object data) { this.data = data; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
static class Leaf {
        public int value;
        Leaf() { }
        Leaf(int value) { this.value = value; }
    }
@JsonSerialize(converter = ConvertingBeanConverter.class)
    static class ConvertingBean {
        public int x, y;
        ConvertingBean(int x, int y) { this.x = x; this.y = y; }
    }
@JsonPropertyOrder({ "a", "b" })
    public static class DummyBean {
        public final int a, b;
        DummyBean(int x, int y) { a = x * 2; b = y * 2; }
    }
static class ConvertingBeanConverter extends StdConverter<ConvertingBean, DummyBean> {
        @Override
        public DummyBean convert(ConvertingBean value) {
            return new DummyBean(value.x, value.y);
        }
    }
@JsonDeserialize(using = NullBeanDeserializer.class)
    static class NullBean {
        static final NullBean NULL_INSTANCE = new NullBean();
    }
static class NullBeanDeserializer extends ValueDeserializer<NullBean> {
        @Override
        public NullBean getNullValue(DeserializationContext context) {
            return NullBean.NULL_INSTANCE;
        }

        @Override
        public NullBean deserialize(JsonParser parser, DeserializationContext context) {
            throw new UnsupportedOperationException();
        }
    }
private static final class JavaTypeWrapper {
        private final JavaType type;

        JavaTypeWrapper(Class<?> raw, Class<?>... parameters) {
            type = TypeFactory.createDefaultInstance().constructParametricType(raw, parameters);
        }

        JavaTypeWrapper(Class<?> raw, JavaType parameter) {
            type = TypeFactory.createDefaultInstance().constructParametricType(raw, parameter);
        }

        JavaTypeWrapper(JavaType type) {
            this.type = type;
        }

        JavaType type() { return type; }
    }

    void __invoke_beanConvert() throws Exception {
        try {
            beanConvert();
        } finally {
        }
    }


    void __invoke_errorReporting() throws Exception {
        try {
            errorReporting();
        } finally {
        }
    }


    void __invoke_issue458() throws Exception {
        try {
            issue458();
        } finally {
        }
    }


    void __invoke_wrapping() throws Exception {
        try {
            wrapping();
        } finally {
        }
    }


    void __invoke_convertUsingCast() throws Exception {
        try {
            convertUsingCast();
        } finally {
        }
    }


    void __invoke_issue11() throws Exception {
        try {
            issue11();
        } finally {
        }
    }


    void __invoke_conversionIssue288() throws Exception {
        try {
            conversionIssue288();
        } finally {
        }
    }


    void __invoke_conversionIssue1433() throws Exception {
        try {
            conversionIssue1433();
        } finally {
        }
    }

}
