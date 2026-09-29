package tools.jackson.databind.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0144Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ANNOTATED_BEAN = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 61 01");
private static final byte[] INTEGRAL_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 31 3f 30 28 62 28 7f 05");
private static final byte[] DECIMAL_ARRAY = VPackWireFixtureTest.hex(
            "13 30"
          + "1b 00 00 00 00 00 00 00 00"
          + "1b 00 00 00 00 00 00 d0 3f"
          + "1b 00 00 00 00 00 00 c0 bf"
          + "1b 00 00 00 00 00 00 25 40"
          + "1b 00 00 00 00 80 49 c3 40"
          + "05");
private static final byte[] BINARY_SURE = VPackWireFixtureTest.hex(
            "c0 05 73 75 72 65 2e");
private static final byte[] EXPECTED_METHOD = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 73 65 65 3a 66 6f 6f 62 61 72 01");
private static final byte[] EXPECTED_WRAPPED = VPackWireFixtureTest.hex(
            "14 1c 47 77 72 61 70 70 65 64 14 11 45 76 61 6c 75 65"
          + "47 73 65 65 3a 78 79 7a 01 01");
private static final byte[] EXPECTED_MAP = VPackWireFixtureTest.hex(
            "14 1e 45 62 65 61 6e 73 14 15 45 66 69 72 73 74"
          + "4b 6d 61 70 2d 3e 49 6e 20 4d 61 70 01 01");
private static final byte[] EXPECTED_RESOLVED = VPackWireFixtureTest.hex(
            "57 63 6f 6e 74 65 78 74 75 61 6c 3d 31 2c 72 65 73 6f 6c"
          + "76 65 64 3d 31");
private static final byte[] EXPECTED_ROOT_FOO = VPackWireFixtureTest.hex(
            "49 2f 52 4f 4f 54 2f 66 6f 6f");
private static final byte[] EXPECTED_ROOT_BAR = VPackWireFixtureTest.hex(
            "49 2f 52 4f 4f 54 2f 62 61 72");
private static final byte[] EXPECTED_ROOT_3 = VPackWireFixtureTest.hex(
            "47 2f 52 4f 4f 54 2f 33");

    void arrayIdentityTransforms() throws Exception {
        byte[] bytes = MAPPER.readValue(INTEGRAL_ARRAY, byte[].class);
        short[] shorts = MAPPER.readValue(INTEGRAL_ARRAY, short[].class);
        int[] ints = MAPPER.readValue(INTEGRAL_ARRAY, int[].class);
        long[] longs = MAPPER.readValue(INTEGRAL_ARRAY, long[].class);
        float[] floats = MAPPER.readValue(DECIMAL_ARRAY, float[].class);
        double[] doubles = MAPPER.readValue(DECIMAL_ARRAY, double[].class);

        assertArrayEquals(bytes, convert(bytes, byte[].class));
        assertArrayEquals(shorts, convert(shorts, short[].class));
        assertArrayEquals(ints, convert(ints, int[].class));
        assertArrayEquals(longs, convert(longs, long[].class));
        assertArrayEquals(floats, convert(floats, float[].class));
        float[] doubleToFloat = convert(doubles, float[].class);
        for (int i = 0; i < doubles.length; ++i) {
            assertEquals(doubles[i], doubleToFloat[i], 0.0);
        }
    }

    void byteArrayFrom() throws Exception {
        byte[] actual = MAPPER.readValue(BINARY_SURE, byte[].class);
        assertArrayEquals("sure.".getBytes(java.nio.charset.StandardCharsets.US_ASCII), actual);
    }

    void intArrayToX() throws Exception {
        int[] data = MAPPER.readValue(INTEGRAL_ARRAY, int[].class);
        assertArrayEquals(new byte[] { 1, -1, 0, 98, 127 }, convert(data, byte[].class));
        assertArrayEquals(new short[] { 1, -1, 0, 98, 127 }, convert(data, short[].class));
        assertArrayEquals(new long[] { 1, -1, 0, 98, 127 }, convert(data, long[].class));
        assertEquals(List.of(1, -1, 0, 98, 127),
                MAPPER.convertValue(data, new TypeReference<List<Integer>>() { }));
    }

    void longArrayToX() throws Exception {
        long[] data = MAPPER.readValue(INTEGRAL_ARRAY, long[].class);
        assertArrayEquals(new byte[] { 1, -1, 0, 98, 127 }, convert(data, byte[].class));
        assertArrayEquals(new short[] { 1, -1, 0, 98, 127 }, convert(data, short[].class));
        assertArrayEquals(new int[] { 1, -1, 0, 98, 127 }, convert(data, int[].class));
        assertEquals(List.of(1L, -1L, 0L, 98L, 127L),
                MAPPER.convertValue(data, new TypeReference<List<Long>>() { }));
    }

    void nullXform() {
        assertNull(MAPPER.convertValue(null, Integer.class));
        assertNull(MAPPER.convertValue(null, String.class));
        assertNull(MAPPER.convertValue(null, byte[].class));
    }

    void overflows() {
        InputCoercionException byteOverflow = assertThrows(InputCoercionException.class,
                () -> MAPPER.convertValue(new int[] { 1000 }, byte[].class));
        assertEquals(true, byteOverflow.getMessage().contains("out of range of `byte`"));

        InputCoercionException shortOverflow = assertThrows(InputCoercionException.class,
                () -> MAPPER.convertValue(new int[] { -99999 }, short[].class));
        assertEquals(true, shortOverflow.getMessage().contains("out of range of `short`"));

        InputCoercionException intOverflow = assertThrows(InputCoercionException.class,
                () -> MAPPER.convertValue(new long[] { Long.MAX_VALUE }, int[].class));
        assertEquals(true, intOverflow.getMessage().contains("out of range of `int`"));

        java.math.BigInteger biggie = java.math.BigInteger.valueOf(Long.MAX_VALUE).add(
                java.math.BigInteger.ONE);
        List<java.math.BigInteger> values = new ArrayList<>();
        values.add(biggie);
        InputCoercionException longOverflow = assertThrows(InputCoercionException.class,
                () -> MAPPER.convertValue(values, long[].class));
        assertEquals(true, longOverflow.getMessage().contains("out of range of `long`"));
    }
private static ObjectMapper annotatedStringMapper() {
        SimpleModule module = new SimpleModule("test", Version.unknownVersion())
                .addSerializer(String.class, new AnnotatedContextualSerializer());
        return VPackMapper.builder().addModule(module).build();
    }
private static <T> T convert(Object input, Class<T> outputType) {
        return MAPPER.convertValue(input, outputType);
    }
private static void assertWrittenTreeEquals(byte[] expected, ObjectMapper mapper,
            Object value) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(mapper.writeValueAsBytes(value)));
    }
@Target({ ElementType.FIELD, ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    public @interface Prefix {
        String value();
    }
static class ContextualBean {
        protected final String value;

        ContextualBean(String value) {
            this.value = value;
        }

        @Prefix("see:")
        public String getValue() {
            return value;
        }
    }
@Prefix("wrappedBean:")
    static class ContextualBeanWrapper {
        @Prefix("wrapped:")
        public ContextualBean wrapped;

        ContextualBeanWrapper(String value) {
            wrapped = new ContextualBean(value);
        }
    }
static class ContextualMapBean {
        @Prefix("map->")
        public Map<String, String> beans = new java.util.LinkedHashMap<>();
    }
static class AnnotatedContextualClassBean {
        @Name("xyz")
        @JsonDeserialize(using = AnnotatedContextualDeserializer.class)
        public StringValue value;
    }
static class StringValue {
        protected String value;

        StringValue(String value) {
            this.value = value;
        }
    }
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    public @interface Name {
        String value();
    }
static class AnnotatedContextualDeserializer extends ValueDeserializer<StringValue> {
        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue("=" + parser.getString());
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                BeanProperty property) {
            Name annotation = property.getAnnotation(Name.class);
            if (annotation == null) {
                annotation = property.getContextAnnotation(Name.class);
            }
            String name = annotation == null ? "UNKNOWN" : annotation.value();
            return new NamedValueDeserializer(name);
        }
    }
static class NamedValueDeserializer extends ValueDeserializer<StringValue> {
        private final String name;

        NamedValueDeserializer(String name) {
            this.name = name;
        }

        @Override
        public StringValue deserialize(JsonParser parser, DeserializationContext context) {
            return new StringValue(name + "=" + parser.getString());
        }
    }
static class AnnotatedContextualSerializer extends ValueSerializer<String> {
        private final String prefix;

        AnnotatedContextualSerializer() {
            this("");
        }

        AnnotatedContextualSerializer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                SerializationContext context) {
            generator.writeString(prefix + value);
        }

        @Override
        public ValueSerializer<?> createContextual(SerializationContext context,
                BeanProperty property) {
            String resolved = "UNKNOWN";
            Prefix annotation = null;
            if (property != null) {
                annotation = property.getAnnotation(Prefix.class);
                if (annotation == null) {
                    annotation = property.getContextAnnotation(Prefix.class);
                }
            }
            if (annotation != null) {
                resolved = annotation.value();
            }
            return new AnnotatedContextualSerializer(resolved);
        }
    }
static class ContextualAndResolvable extends ValueSerializer<String> {
        private int contextual;
        private int resolved;

        ContextualAndResolvable() {
            this(0, 0);
        }

        ContextualAndResolvable(int resolved, int contextual) {
            this.contextual = contextual;
            this.resolved = resolved;
        }

        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                SerializationContext context) {
            generator.writeString("contextual=" + contextual + ",resolved=" + resolved);
        }

        @Override
        public ValueSerializer<?> createContextual(SerializationContext context,
                BeanProperty property) {
            return new ContextualAndResolvable(resolved, contextual + 1);
        }

        @Override
        public void resolve(SerializationContext context) {
            ++resolved;
        }
    }
static class AccumulatingContextual extends ValueSerializer<String> {
        private final String description;

        AccumulatingContextual() {
            this("");
        }

        AccumulatingContextual(String description) {
            this.description = description;
        }

        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                SerializationContext context) {
            generator.writeString(description + "/" + value);
        }

        @Override
        public ValueSerializer<?> createContextual(SerializationContext context,
                BeanProperty property) {
            if (property == null) {
                return new AccumulatingContextual(description + "/ROOT");
            }
            return new AccumulatingContextual(description + "/" + property.getName());
        }
    }

    void __invoke_arrayIdentityTransforms() throws Exception {
        try {
            arrayIdentityTransforms();
        } finally {
        }
    }


    void __invoke_byteArrayFrom() throws Exception {
        try {
            byteArrayFrom();
        } finally {
        }
    }


    void __invoke_intArrayToX() throws Exception {
        try {
            intArrayToX();
        } finally {
        }
    }


    void __invoke_longArrayToX() throws Exception {
        try {
            longArrayToX();
        } finally {
        }
    }


    void __invoke_nullXform() throws Exception {
        try {
            nullXform();
        } finally {
        }
    }


    void __invoke_overflows() throws Exception {
        try {
            overflows();
        } finally {
        }
    }

}
