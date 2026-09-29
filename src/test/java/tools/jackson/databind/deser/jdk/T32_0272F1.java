package tools.jackson.databind.deser.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0272F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ARRAYS_AS_LIST = VPackWireFixtureTest.hex(
            "06 2f 02 5a 6a 61 76 61 2e 75 74 69 6c 2e 41 72 72 61 79 73 24 41 72 72 61 79 4c 69 73 74 "
          + "06 0f 03 41 61 42 62 63 43 64 65 66 03 05 08 03 1e");
private static final byte[] EMPTY_LIST_TYPED = VPackWireFixtureTest.hex(
            "06 26 02 5f 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 45 6d 70 74 79 4c 69 73 74 01 03 23");
private static final byte[] EMPTY_MAP_TYPED = VPackWireFixtureTest.hex(
            "0b 2a 01 46 40 63 6c 61 73 73 5e 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 45 6d 70 74 79 4d 61 70 03");

    // Provenance: JavaUtilCollectionsTypesTest#testArraysAsList().
    void testArraysAsList() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        List<String> input = Arrays.asList("a", "bc", "def");
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) mapper.readValue(ARRAYS_AS_LIST, List.class);
        assertEquals(input, result);
        result.set(1, "b");
        assertEquals(List.of("a", "b", "def"), result);
    }

    // Provenance: JavaUtilCollectionsTypesTest#testEmptyList().
    void testEmptyList() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        List<?> result = mapper.readValue(EMPTY_LIST_TYPED, List.class);
        assertEquals(Collections.emptyList(), result);
        assertEquals(Collections.emptyList().getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testEmptyMap().
    void testEmptyMap() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        Map<?, ?> result = mapper.readValue(EMPTY_MAP_TYPED, Map.class);
        assertEquals(Collections.emptyMap(), result);
        assertEquals(Collections.emptyMap().getClass(), result.getClass());
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

    void __invoke_testArraysAsList() throws Exception {
        try {
            testArraysAsList();
        } finally {
        }
    }


    void __invoke_testEmptyList() throws Exception {
        try {
            testEmptyList();
        } finally {
        }
    }


    void __invoke_testEmptyMap() throws Exception {
        try {
            testEmptyMap();
        } finally {
        }
    }

}
