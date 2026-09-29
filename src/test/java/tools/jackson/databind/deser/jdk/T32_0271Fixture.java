package tools.jackson.databind.deser.jdk;

import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0271Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: JavaLangObjectDeserializationTest#testNestedUntyped989().
    void testNestedUntyped989() throws Exception {
        ObjectReader reader = MAPPER.readerFor(DelegatingUntyped.class);

        DelegatingUntyped value = reader.readValue(VPackWireFixtureTest.hex("13 03 00"));
        assertInstanceOf(List.class, value.value);
        value = reader.readValue(compactArray(compactObject()));
        assertInstanceOf(List.class, value.value);

        value = reader.readValue(VPackWireFixtureTest.hex("0a"));
        assertInstanceOf(Map.class, value.value);
        value = reader.readValue(compactObject(field("a", VPackWireFixtureTest.hex("01"))));
        assertInstanceOf(Map.class, value.value);
    }

    // Provenance: JavaLangObjectDeserializationTest#testNonVanilla().
    void testNonVanilla() throws Exception {
        SimpleModule module = new SimpleModule("T32-0271-upper-case")
                .addDeserializer(String.class, new UpperCaseStringDeserializer());
        ObjectReader reader = VPackMapper.builder()
                .addModule(module)
                .build()
                .readerFor(Object.class);

        List<?> values = reader.readValue(compactArray(
                VPackWireFixtureTest.hex("1a"), VPackWireFixtureTest.hex("19"), integer(7),
                doubleValue(0.5), text("foo"), VPackWireFixtureTest.hex("18")));
        assertEquals(6, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Integer.valueOf(7), values.get(2));
        assertEquals(Double.valueOf(0.5), values.get(3));
        assertEquals("FOO", values.get(4));
        assertNull(values.get(5));

        Map<?, ?> map = reader.readValue(compactObject(
                field("a", doubleValue(0.25)), field("b", integer(3)),
                field("c", VPackWireFixtureTest.hex("1a")),
                field("d", VPackWireFixtureTest.hex("19"))));
        assertEquals(Map.of("a", 0.25, "b", 3, "c", true, "d", false), map);

        List<Object> update = new java.util.ArrayList<>();
        assertEquals(Integer.valueOf(42), reader.readValue(integer(42)));
        assertEquals(Integer.valueOf(42), reader.withValueToUpdate(update).readValue(integer(42)));
        assertEquals(Double.valueOf(2.5), reader.readValue(doubleValue(2.5)));
        assertEquals(Double.valueOf(2.5), reader.withValueToUpdate(update).readValue(doubleValue(2.5)));
        assertEquals("ABC", reader.readValue(text("abc")));
        assertEquals("ABC", reader.withValueToUpdate(update).readValue(text("abc")));
        assertEquals(Boolean.TRUE, reader.readValue(VPackWireFixtureTest.hex("1a")));
        assertEquals(Boolean.TRUE, reader.withValueToUpdate(update).readValue(VPackWireFixtureTest.hex("1a")));
        assertEquals(Boolean.FALSE, reader.readValue(VPackWireFixtureTest.hex("19")));
        assertEquals(Boolean.FALSE, reader.withValueToUpdate(update).readValue(VPackWireFixtureTest.hex("19")));
        assertNull(reader.readValue(VPackWireFixtureTest.hex("18")));
        assertSame(update, reader.withValueToUpdate(update).readValue(VPackWireFixtureTest.hex("18")));

        List<?> nested = reader.readValue(compactArray(compactObject(), compactArray()));
        assertEquals(List.of(Map.of(), List.of()), nested);
    }

    // Provenance: JavaLangObjectDeserializationTest#testNullInDifferentContexts().
    void testNullInDifferentContexts() throws Exception {
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("18"), Object.class));

        List<?> list = (List<?>) MAPPER.readValue(compactArray(
                VPackWireFixtureTest.hex("18"), integer(1), VPackWireFixtureTest.hex("18")), Object.class);
        assertEquals(3, list.size());
        assertNull(list.get(0));
        assertEquals(Integer.valueOf(1), list.get(1));
        assertNull(list.get(2));

        Map<?, ?> map = (Map<?, ?>) MAPPER.readValue(compactObject(
                field("a", VPackWireFixtureTest.hex("18")), field("b", integer(2))), Object.class);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("a"));
        assertNull(map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testObjectSerializeWithLong().
    void testObjectSerializeWithLong() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class).build(),
                        DefaultTyping.JAVA_LANG_OBJECT,
                        com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY)
                .build();
        long expected = 1337800584532L;
        byte[] fixture = compactObject(field("timestamp", unsignedLong(expected)));
        JsonNode deserialized = mapper.readTree(fixture);
        assertEquals(expected, deserialized.get("timestamp").asLong());
        Map<?, ?> result = mapper.readValue(fixture, Map.class);
        Number value = (Number) result.get("timestamp");
        assertNotNullNumber(value);
        assertSame(Long.class, value.getClass());
        assertEquals(Long.valueOf(expected), value);
    }

    // Provenance: JavaLangObjectDeserializationTest#testObjectWithArraysAndObjects().
    void testObjectWithArraysAndObjects() throws Exception {
        Map<?, ?> result = (Map<?, ?>) MAPPER.readValue(compactObject(
                field("numbers", compactArray(integer(1), integer(2), integer(3))),
                field("nested", compactObject(field("flag", VPackWireFixtureTest.hex("1a")))),
                field("text", text("hello"))), Object.class);
        assertEquals(3, result.size());
        List<?> numbers = (List<?>) result.get("numbers");
        assertEquals(3, numbers.size());
        Map<?, ?> nested = (Map<?, ?>) result.get("nested");
        assertEquals(Boolean.TRUE, nested.get("flag"));
        assertEquals("hello", result.get("text"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testPolymorphicUntypedCustom().
    void testPolymorphicUntypedCustom() throws Exception {
        SimpleModule module = new SimpleModule("T32-0271-polymorphic-upper-case")
                .addDeserializer(String.class, new UpperCaseStringDeserializer());
        ObjectReader reader = VPackMapper.builder()
                .addModule(module)
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build())
                .build()
                .readerFor(WrappedPolymorphicUntyped.class);

        WrappedPolymorphicUntyped value = reader.readValue(compactObject(field("value", integer(10))));
        assertEquals(Integer.valueOf(10), value.value);
        value = reader.readValue(compactObject(field("value", unsignedLong(9988776655L))));
        assertEquals(Long.valueOf(9988776655L), value.value);
        value = reader.readValue(compactObject(field("value", doubleValue(0.75))));
        assertEquals(Double.valueOf(0.75), value.value);
        value = reader.readValue(compactObject(field("value", text("abc"))));
        assertEquals("ABC", value.value);
        value = reader.readValue(compactObject(field("value", VPackWireFixtureTest.hex("19"))));
        assertEquals(Boolean.FALSE, value.value);
        value = reader.readValue(compactObject(field("value", VPackWireFixtureTest.hex("18"))));
        assertNull(value.value);

        // The source round-trip's generated type-id spelling is replaced by
        // an independently literal VPack type-id array.
        value = reader.readValue(compactObject(field("value",
                compactArray(text("java.util.Date"), integer(123)))));
        assertInstanceOf(java.util.Date.class, value.value);
    }

    // Provenance: JavaLangObjectDeserializationTest#testPolymorphicUntypedVanilla().
    void testPolymorphicUntypedVanilla() throws Exception {
        ObjectReader defaultReader = VPackMapper.builder()
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build())
                .build()
                .readerFor(WrappedPolymorphicUntyped.class);
        ObjectReader alternateReader = defaultReader.with(
                DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS,
                DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);

        WrappedPolymorphicUntyped value = defaultReader.readValue(compactObject(field("value", integer(10))));
        assertEquals(Integer.valueOf(10), value.value);
        value = alternateReader.readValue(compactObject(field("value", integer(10))));
        assertEquals(java.math.BigInteger.TEN, value.value);
        value = defaultReader.readValue(compactObject(field("value", doubleValue(5.0))));
        assertEquals(Double.valueOf(5.0), value.value);
        value = alternateReader.readValue(compactObject(field("value", doubleValue(5.0))));
        assertEquals(new BigDecimal("5.0"), value.value);

        byte[][] values = new byte[100][];
        for (int i = 0; i < values.length; ++i) values[i] = integer(i);
        Object result = MAPPER.readerFor(Object.class)
                .with(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
                .readValue(compactArray(values));
        assertInstanceOf(Object[].class, result);
        Object[] list = (Object[]) result;
        for (int i = 0; i < 100; ++i) assertEquals(Integer.valueOf(i), list[i]);

        value = defaultReader.readValue(compactObject(field("value",
                compactArray(text("java.util.Date"), integer(123)))));
        assertInstanceOf(java.util.Date.class, value.value);
    }

    // Provenance: JavaLangObjectDeserializationTest#testSampleDoc().
    void testSampleDoc() throws Exception {
        Map<?, ?> root = (Map<?, ?>) MAPPER.readValue(sampleDocFixture(), Object.class);
        Map<?, ?> image = mapValue(root, "Image");
        assertEquals(5, image.size());
        assertEquals(Integer.valueOf(800), image.get("Width"));
        assertEquals(Integer.valueOf(600), image.get("Height"));
        assertEquals("View from 15th Floor", image.get("Title"));
        Map<?, ?> thumbnail = mapValue(image, "Thumbnail");
        assertEquals(3, thumbnail.size());
        assertEquals(Integer.valueOf(125), thumbnail.get("Height"));
        assertEquals("100", thumbnail.get("Width"));
        assertEquals("http://www.example.com/image/481989943", thumbnail.get("Url"));
        assertEquals(List.of(116, 943, 234, 38793), image.get("IDs"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testSerializable().
    void testSerializable() throws Exception {
        byte[] object = compactObject(field("value", integer(123)));
        SerializableContainer container = MAPPER.readValue(object, SerializableContainer.class);
        assertEquals(Integer.valueOf(123), container.value);
        container = MAPPER.readValue(compactObject(field("value", VPackWireFixtureTest.hex("1a"))),
                SerializableContainer.class);
        assertEquals(Boolean.TRUE, container.value);

        Map<?, ?> map = MAPPER.readValue(object, new TypeReference<Map<String, Serializable>>() { });
        assertEquals(1, map.size());
        assertEquals(Integer.valueOf(123), map.get("value"));
        map = MAPPER.readValue(object, new TypeReference<Map<Serializable, Object>>() { });
        assertEquals("value", map.keySet().iterator().next());
    }

    // Provenance: JavaLangObjectDeserializationTest#testSimpleVanillaScalars().
    void testSimpleVanillaScalars() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Object.class);
        assertEquals("foo", reader.readValue(text("foo")));
        assertEquals("foo", reader.withValueToUpdate("xxx").readValue(text("foo")));
        assertEquals(Boolean.FALSE, reader.readValue(VPackWireFixtureTest.hex("19")));
        assertEquals(Boolean.TRUE, reader.readValue(VPackWireFixtureTest.hex("1a")));
        assertEquals(Integer.valueOf(13), reader.readValue(integer(13)));
        assertEquals(Double.valueOf(0.5), reader.readValue(doubleValue(0.5)));
    }

    // Provenance: JavaLangObjectDeserializationTest#testSimpleVanillaStructured().
    void testSimpleVanillaStructured() throws Exception {
        List<?> list = (List<?>) MAPPER.readValue(compactArray(integer(1), integer(2), integer(3)), Object.class);
        assertEquals(Integer.valueOf(1), list.get(0));
    }

    // Provenance: JavaLangObjectDeserializationTest#testSingleElementArrayAndObject().
    void testSingleElementArrayAndObject() throws Exception {
        assertEquals(List.of(42), MAPPER.readValue(compactArray(integer(42)), Object.class));
        assertEquals(Map.of("key", true), MAPPER.readValue(
                compactObject(field("key", VPackWireFixtureTest.hex("1a"))), Object.class));
    }
private static void assertNotNullNumber(Number value) {
        assertTrue(value != null, "expected a numeric timestamp");
    }
@SuppressWarnings("unchecked")
    private static Map<?, ?> mapValue(Map<?, ?> map, String key) {
        return (Map<?, ?>) map.get(key);
    }
private static byte[] sampleDocFixture() {
        return compactObject(field("Image", compactObject(
                field("Width", integer(800)),
                field("Height", integer(600)),
                field("Title", text("View from 15th Floor")),
                field("Thumbnail", compactObject(
                        field("Url", text("http://www.example.com/image/481989943")),
                        field("Height", integer(125)), field("Width", text("100")))),
                field("IDs", compactArray(integer(116), integer(943), integer(234), integer(38793))))));
    }
private static byte[] field(String name, byte[] value) {
        return concat(text(name), value);
    }
private static byte[] text(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) throw new IllegalArgumentException("fixture string is too long");
        byte[] result = new byte[bytes.length + 1];
        result[0] = (byte) (0x40 + bytes.length);
        System.arraycopy(bytes, 0, result, 1, bytes.length);
        return result;
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) return new byte[] { (byte) (0x30 + value) };
        if (value >= 0 && value <= 255) return new byte[] { 0x28, (byte) value };
        return new byte[] { 0x29, (byte) value, (byte) (value >>> 8) };
    }
private static byte[] unsignedLong(long value) {
        if (value >= 0 && value <= 255) return integer((int) value);
        int width = 1;
        while (width < 8 && (value >>> (width * 8)) != 0) ++width;
        byte[] result = new byte[width + 1];
        result[0] = (byte) (0x27 + width);
        for (int i = 0; i < width; ++i) result[i + 1] = (byte) (value >>> (i * 8));
        return result;
    }
private static byte[] doubleValue(double value) {
        long bits = Double.doubleToRawLongBits(value);
        byte[] result = new byte[9];
        result[0] = 0x1b;
        for (int i = 0; i < 8; ++i) result[i + 1] = (byte) (bits >>> (i * 8));
        return result;
    }
private static byte[] compactArray(byte[]... values) {
        return compact(0x13, values, values.length);
    }
private static byte[] compactObject(byte[]... fields) {
        return compact(0x14, fields, fields.length);
    }
private static byte[] compact(int marker, byte[][] body, int count) {
        int bodyLength = 0;
        for (byte[] value : body) bodyLength += value.length;
        int length = 1 + varintLength(bodyLength + 3) + bodyLength + varintLength(count);
        while (length != 1 + varintLength(length) + bodyLength + varintLength(count)) {
            length = 1 + varintLength(length) + bodyLength + varintLength(count);
        }
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream(length);
        output.write(marker);
        writeForward(output, length);
        for (byte[] value : body) output.writeBytes(value);
        writeReverse(output, count);
        return output.toByteArray();
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) ++length;
        return length;
    }
private static void writeForward(java.io.ByteArrayOutputStream output, int value) {
        do {
            int group = value & 0x7f;
            value >>>= 7;
            output.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
    }
private static void writeReverse(java.io.ByteArrayOutputStream output, int value) {
        byte[] groups = new byte[varintLength(value)];
        int offset = 0;
        do {
            int group = value & 0x7f;
            value >>>= 7;
            groups[offset++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        for (int i = groups.length - 1; i >= 0; --i) output.write(groups[i]);
    }
private static byte[] concat(byte[]... values) {
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        for (byte[] value : values) output.writeBytes(value);
        return output.toByteArray();
    }
private static final class DelegatingUntyped {
        protected Object value;

        @JsonCreator
        DelegatingUntyped(Object value) { this.value = value; }
    }
private static final class SerializableContainer {
        public Serializable value;
    }
private static final class WrappedPolymorphicUntyped {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object value;

        protected WrappedPolymorphicUntyped() { }
    }
private static final class UpperCaseStringDeserializer extends StdScalarDeserializer<String> {
        UpperCaseStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) {
            return parser.getString().toUpperCase(java.util.Locale.ROOT);
        }
    }

    void __invoke_testNestedUntyped989() throws Exception {
        try {
            testNestedUntyped989();
        } finally {
        }
    }


    void __invoke_testNonVanilla() throws Exception {
        try {
            testNonVanilla();
        } finally {
        }
    }


    void __invoke_testNullInDifferentContexts() throws Exception {
        try {
            testNullInDifferentContexts();
        } finally {
        }
    }


    void __invoke_testObjectSerializeWithLong() throws Exception {
        try {
            testObjectSerializeWithLong();
        } finally {
        }
    }


    void __invoke_testObjectWithArraysAndObjects() throws Exception {
        try {
            testObjectWithArraysAndObjects();
        } finally {
        }
    }


    void __invoke_testPolymorphicUntypedCustom() throws Exception {
        try {
            testPolymorphicUntypedCustom();
        } finally {
        }
    }


    void __invoke_testPolymorphicUntypedVanilla() throws Exception {
        try {
            testPolymorphicUntypedVanilla();
        } finally {
        }
    }


    void __invoke_testSampleDoc() throws Exception {
        try {
            testSampleDoc();
        } finally {
        }
    }


    void __invoke_testSerializable() throws Exception {
        try {
            testSerializable();
        } finally {
        }
    }


    void __invoke_testSimpleVanillaScalars() throws Exception {
        try {
            testSimpleVanillaScalars();
        } finally {
        }
    }


    void __invoke_testSimpleVanillaStructured() throws Exception {
        try {
            testSimpleVanillaStructured();
        } finally {
        }
    }


    void __invoke_testSingleElementArrayAndObject() throws Exception {
        try {
            testSingleElementArrayAndObject();
        } finally {
        }
    }

}
