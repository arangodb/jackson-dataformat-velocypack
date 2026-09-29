package tools.jackson.databind.deser.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0272F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ARRAYS_AS_LIST = VPackWireFixtureTest.hex(
            "06 2f 02 5a 6a 61 76 61 2e 75 74 69 6c 2e 41 72 72 61 79 73 24 41 72 72 61 79 4c 69 73 74 "
          + "06 0f 03 41 61 42 62 63 43 64 65 66 03 05 08 03 1e");
private static final byte[] EMPTY_LIST_TYPED = VPackWireFixtureTest.hex(
            "06 26 02 5f 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 45 6d 70 74 79 4c 69 73 74 01 03 23");
private static final byte[] EMPTY_MAP_TYPED = VPackWireFixtureTest.hex(
            "0b 2a 01 46 40 63 6c 61 73 73 5e 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 45 6d 70 74 79 4d 61 70 03");

    // Provenance: JavaLangObjectDeserializationTest#testUntypedMap().
    void testUntypedMap() throws Exception {
        Map<?, ?> result = (Map<?, ?>) MAPPER.readValue(compactObject(
                field("foo", text("bar")),
                field("crazy", VPackWireFixtureTest.hex("1a")),
                field("null", VPackWireFixtureTest.hex("18"))), Object.class);
        assertEquals(3, result.size());
        assertEquals("bar", result.get("foo"));
        assertEquals(Boolean.TRUE, result.get("crazy"));
        assertNull(result.get("null"));
        assertNull(result.get("bar"));
        assertNull(result.get(3));
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedWithCustomScalarDesers().
    void testUntypedWithCustomScalarDesers() throws Exception {
        SimpleModule module = new SimpleModule("T32-0272-custom-scalars")
                .addDeserializer(String.class, new UpperCaseStringDeserializer())
                .addDeserializer(Number.class, new FixedNumberDeserializer(13));
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Map<?, ?> result = (Map<?, ?>) mapper.readValue(compactObject(
                field("a", text("b")), field("nr", integer(1))), Object.class);
        assertInstanceOf(Map.class, result);
        assertEquals("B", result.get("a"));
        assertInstanceOf(String.class, result.get("a"));
        assertEquals(Integer.valueOf(13), result.get("nr"));
        assertInstanceOf(Number.class, result.get("nr"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedWithListDeser().
    void testUntypedWithListDeser() throws Exception {
        SimpleModule module = new SimpleModule("T32-0272-list-deserializer")
                .addDeserializer(List.class, new ListValueDeserializer());
        ObjectReader reader = VPackMapper.builder().addModule(module).build()
                .readerFor(Object.class);
        assertEquals(List.of("X1", "X2", "Xtrue"),
                reader.readValue(compactArray(integer(1), integer(2),
                        VPackWireFixtureTest.hex("1a"))));

        Object result = reader.with(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
                .readValue(compactArray(integer(1), integer(2),
                        VPackWireFixtureTest.hex("1a")));
        assertInstanceOf(Object[].class, result);
        assertEquals(List.of(1, 2, true), Arrays.asList((Object[]) result));
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedWithMapDeser().
    void testUntypedWithMapDeser() throws Exception {
        SimpleModule module = new SimpleModule("T32-0272-map-deserializer")
                .addDeserializer(Map.class, new YMapDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Map<?, ?> result = (Map<?, ?>) mapper.readValue(compactObject(
                field("a", VPackWireFixtureTest.hex("1a"))), Object.class);
        assertEquals(1, result.size());
        assertEquals("Ytrue", result.get("a"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedWithJsonArrays().
    void testUntypedWithJsonArrays() throws Exception {
        Object value = MAPPER.readValue(compactArray(integer(1)), Object.class);
        assertInstanceOf(List.class, value);

        ObjectMapper arrayMapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)
                .build();
        Object result = arrayMapper.readValue(compactArray(integer(1),
                VPackWireFixtureTest.hex("19"), VPackWireFixtureTest.hex("1a"),
                doubleValue(0.5), compactObject()), Object.class);
        assertInstanceOf(Object[].class, result);
        assertEquals(List.of(1, false, true, 0.5, Map.of()),
                Arrays.asList((Object[]) result));
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedIntAsLong().
    void testUntypedIntAsLong() throws Exception {
        byte[] input = compactObject(field("value", integer(3)));
        WrappedUntyped value = MAPPER.readerFor(WrappedUntyped.class).readValue(input);
        assertEquals(Integer.valueOf(3), value.value);

        value = MAPPER.readerFor(WrappedUntyped.class)
                .with(DeserializationFeature.USE_LONG_FOR_INTS).readValue(input);
        assertEquals(Long.valueOf(3), value.value);
    }

    // Provenance: JavaLangObjectDeserializationTest#testValueUpdateVanillaUntyped().
    void testValueUpdateVanillaUntyped() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 42);
        ObjectReader reader = MAPPER.readerFor(Object.class).withValueToUpdate(map);
        Object result = reader.readValue(compactObject(field("b", doubleValue(0.25)),
                field("c", compactArray())));
        assertSame(map, result);
        assertEquals(3, map.size());
        assertEquals(0.25, map.get("b"));
        assertEquals(List.of(), map.get("c"));

        List<Object> list = new ArrayList<>();
        list.add(1);
        reader = MAPPER.readerFor(Object.class).withValueToUpdate(list);
        result = reader.readValue(compactArray(VPackWireFixtureTest.hex("1a"),
                doubleValue(-0.5), compactObject()));
        assertSame(list, result);
        assertEquals(List.of(1, true, -0.5, Map.of()), result);

        reader = MAPPER.readerFor(Object.class).withValueToUpdate(map);
        result = reader.readValue(compactArray(integer(42), doubleValue(-0.25),
                VPackWireFixtureTest.hex("19"), VPackWireFixtureTest.hex("18")));
        assertEquals(Arrays.asList(42, -0.25, false, null), result);

        map.clear();
        map.put("a", 0.5);
        map.put("b", null);
        result = MAPPER.readerFor(Object.class).withValueToUpdate(new ArrayList<>())
                .readValue(compactObject(field("a", doubleValue(0.5)),
                        field("b", VPackWireFixtureTest.hex("18"))));
        assertEquals(map, result);
    }

    // Provenance: JavaLangObjectDeserializationTest#testValueUpdateCustomUntyped().
    void testValueUpdateCustomUntyped() throws Exception {
        SimpleModule module = new SimpleModule("T32-0272-update-custom")
                .addDeserializer(String.class, new UpperCaseStringDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 42);
        ObjectReader reader = mapper.readerFor(Object.class).withValueToUpdate(map);
        Object result = reader.readValue(compactObject(field("b", text("value")),
                field("c", unsignedLong(111222333444L)),
                field("enabled", VPackWireFixtureTest.hex("1a"))));
        assertSame(map, result);
        assertEquals(4, map.size());
        assertEquals("VALUE", map.get("b"));
        assertEquals(Long.valueOf(111222333444L), map.get("c"));
        assertEquals(Boolean.TRUE, map.get("enabled"));

        List<Object> list = new ArrayList<>();
        list.add(1);
        result = mapper.readerFor(Object.class).withValueToUpdate(list)
                .readValue(compactArray(integer(2), text("foobar")));
        assertSame(list, result);
        assertEquals(List.of(1, 2, "FOOBAR"), result);
    }

    // Provenance: JavaLangObjectDeserializationTest#testUntypedCustomMapWithDups().
    void testUntypedCustomMapWithDups() throws Exception {
        SimpleModule module = new SimpleModule("T32-0272-duplicate-map")
                .addDeserializer(String.class, new UpperCaseStringDeserializer());
        ObjectReader reader = VPackMapper.builder().addModule(module).build()
                .readerFor(Object.class);
        assertEquals(Map.of("a", 0), reader.readValue(compactObject(
                field("a", VPackWireFixtureTest.hex("19")), field("a", integer(0)))));
        assertEquals(Map.of("a", 1, "b", false), reader.readValue(compactObject(
                field("a", integer(0)), field("b", VPackWireFixtureTest.hex("19")),
                field("a", integer(1)))));
        assertEquals(Map.of("a", 2, "b", 3, "c", 0.25), reader.readValue(compactObject(
                field("a", integer(1)), field("b", VPackWireFixtureTest.hex("1a")),
                field("c", doubleValue(0.25)), field("a", text("abc")),
                field("a", integer(2)), field("b", integer(3)))));
    }
private static ObjectMapper defaultTypingMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build(),
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
    }
private static byte[] field(String name, byte[] value) {
        return concat(text(name), value);
    }
private static byte[] text(String value) {
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
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
private static final class WrappedUntyped {
        public Object value;
    }
private static final class UpperCaseStringDeserializer
            extends StdScalarDeserializer<String> {
        UpperCaseStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) {
            return parser.getString().toUpperCase(java.util.Locale.ROOT);
        }
    }
private static final class FixedNumberDeserializer extends StdScalarDeserializer<Number> {
        private final Integer value;

        FixedNumberDeserializer(int value) {
            super(Number.class);
            this.value = value;
        }

        @Override
        public Number deserialize(JsonParser parser, DeserializationContext context) {
            return value;
        }
    }
private static final class ListValueDeserializer extends StdDeserializer<List<Object>> {
        ListValueDeserializer() { super(List.class); }

        @Override
        public List<Object> deserialize(JsonParser parser, DeserializationContext context) {
            ArrayList<Object> result = new ArrayList<>();
            while (parser.nextValue() != JsonToken.END_ARRAY) {
                result.add("X" + parser.getString());
            }
            return result;
        }

        @Override
        public ValueDeserializer<?> createContextual(DeserializationContext context,
                tools.jackson.databind.BeanProperty property) {
            context.findContextualValueDeserializer(context.constructType(Object.class), property);
            return this;
        }
    }
private static final class YMapDeserializer extends StdDeserializer<Map<String, Object>> {
        YMapDeserializer() { super(Map.class); }

        @Override
        public Map<String, Object> deserialize(JsonParser parser, DeserializationContext context) {
            Map<String, Object> result = new LinkedHashMap<>();
            while (parser.nextValue() != JsonToken.END_OBJECT) {
                result.put(parser.currentName(), "Y" + parser.getString());
            }
            return result;
        }
    }

    void __invoke_testUntypedMap() throws Exception {
        try {
            testUntypedMap();
        } finally {
        }
    }


    void __invoke_testUntypedWithCustomScalarDesers() throws Exception {
        try {
            testUntypedWithCustomScalarDesers();
        } finally {
        }
    }


    void __invoke_testUntypedWithListDeser() throws Exception {
        try {
            testUntypedWithListDeser();
        } finally {
        }
    }


    void __invoke_testUntypedWithMapDeser() throws Exception {
        try {
            testUntypedWithMapDeser();
        } finally {
        }
    }


    void __invoke_testUntypedWithJsonArrays() throws Exception {
        try {
            testUntypedWithJsonArrays();
        } finally {
        }
    }


    void __invoke_testUntypedIntAsLong() throws Exception {
        try {
            testUntypedIntAsLong();
        } finally {
        }
    }


    void __invoke_testValueUpdateVanillaUntyped() throws Exception {
        try {
            testValueUpdateVanillaUntyped();
        } finally {
        }
    }


    void __invoke_testValueUpdateCustomUntyped() throws Exception {
        try {
            testValueUpdateCustomUntyped();
        } finally {
        }
    }


    void __invoke_testUntypedCustomMapWithDups() throws Exception {
        try {
            testUntypedCustomMapWithDups();
        } finally {
        }
    }

}
