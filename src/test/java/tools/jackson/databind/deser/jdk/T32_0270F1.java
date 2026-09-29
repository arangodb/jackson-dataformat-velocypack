package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0270F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] URI_VALUE = VPackWireFixtureTest.hex(
            "4e 68 74 74 70 3a 2f 2f 66 6f 6f 2e 63 6f 6d");
private static final byte[] URI_INVALID = VPackWireFixtureTest.hex(
            "43 61 20 62");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 12 34 56 78 90 12 34 56 78 90");
private static final byte[] DECIMAL_PI = VPackWireFixtureTest.hex(
            "c8 03 fb ff ff ff 31 41 59");
private static final byte[] DOUBLE_ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] DOUBLE_E = VPackWireFixtureTest.hex(
            "1b 9b 91 04 8b 0a bf 05 40");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 42 00 00 00 00 00 f8 7f");
private static final byte[] MIXED = mixedArrayFixture();
private static final byte[] NESTED = nestedObjectFixture();

    // Provenance: JavaLangObjectDeserializationTest#testBigIntegerCoercion().
    void testBigIntegerCoercion() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
                .build();
        Object result = mapper.readValue(BIG_INTEGER, Object.class);
        assertNotNullBigInteger(result);
        assertEquals(new BigInteger("12345678901234567890"), result);
    }

    // Provenance: JavaLangObjectDeserializationTest#testDeeplyNestedStructures().
    void testDeeplyNestedStructures() throws Exception {
        Map<?, ?> result = MAPPER.readValue(NESTED, Map.class);
        Map<?, ?> level1 = mapValue(result, "level1");
        Map<?, ?> level2 = mapValue(level1, "level2");
        Map<?, ?> level3 = mapValue(level2, "level3");
        Map<?, ?> level4 = mapValue(level3, "level4");
        assertEquals("deep", level4.get("value"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testEmptyArrayAndObject().
    void testEmptyArrayAndObject() throws Exception {
        assertEquals(List.of(), MAPPER.readValue(VPackWireFixtureTest.hex("01"), Object.class));
        assertEquals(Map.of(), MAPPER.readValue(VPackWireFixtureTest.hex("0a"), Object.class));
    }

    // Provenance: JavaLangObjectDeserializationTest#testFloatDeserializationWithBigDecimal().
    void testFloatDeserializationWithBigDecimal() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();
        Object result = mapper.readValue(DECIMAL_PI, Object.class);
        assertEquals(BigDecimal.class, result.getClass());
        assertEquals(new BigDecimal("3.14159"), result);
    }

    // Provenance: JavaLangObjectDeserializationTest#testFloatTypesFloat32AndFloat64().
    void testFloatTypesFloat32AndFloat64() throws Exception {
        Object result = MAPPER.readValue(DOUBLE_ONE_POINT_FIVE, Object.class);
        assertEquals(Double.class, result.getClass());
        result = MAPPER.readValue(DOUBLE_E, Object.class);
        assertEquals(Double.class, result.getClass());
    }

    // Provenance: JavaLangObjectDeserializationTest#testLargeArray().
    void testLargeArray() throws Exception {
        List<?> result = MAPPER.readValue(largeArrayFixture(), List.class);
        assertEquals(100, result.size());
        assertEquals(Integer.valueOf(0), result.get(0));
        assertEquals(Integer.valueOf(99), result.get(99));
    }

    // Provenance: JavaLangObjectDeserializationTest#testLargeObject().
    void testLargeObject() throws Exception {
        Map<?, ?> result = MAPPER.readValue(largeObjectFixture(), Map.class);
        assertEquals(50, result.size());
        assertEquals(Integer.valueOf(0), result.get("key0"));
        assertEquals(Integer.valueOf(49), result.get("key49"));
    }

    // Provenance: JavaLangObjectDeserializationTest#testMixedTypesInArray().
    void testMixedTypesInArray() throws Exception {
        List<?> result = MAPPER.readValue(MIXED, List.class);
        assertEquals(7, result.size());
        assertEquals(Integer.valueOf(1), result.get(0));
        assertEquals("text", result.get(1));
        assertEquals(Boolean.TRUE, result.get(2));
        assertNull(result.get(3));
        assertEquals(Double.valueOf(3.14), result.get(4));
        assertInstanceOf(Map.class, result.get(5));
        assertInstanceOf(List.class, result.get(6));
    }

    // Provenance: JavaLangObjectDeserializationTest#testNaNHandling().
    void testNaNHandling() throws Exception {
        Object result = MAPPER.readValue(NAN, Object.class);
        assertEquals(Double.class, result.getClass());
        assertTrue(((Double) result).isNaN());
    }

    // Provenance: JavaLangObjectDeserializationTest#testNestedUntyped().
    void testNestedUntyped() throws Exception {
        Object root = MAPPER.readValue(nestedUntypedFixture(), Object.class);
        assertInstanceOf(Map.class, root);
        assertEquals(Map.of("a", 3, "b", List.of(1, 2), "c", List.of(3)), root);
    }
private static void assertNotNullBigInteger(Object value) {
        assertTrue(value instanceof BigInteger, "expected an exact BigInteger result");
    }
@SuppressWarnings("unchecked")
    private static Map<?, ?> mapValue(Map<?, ?> map, String key) {
        return (Map<?, ?>) map.get(key);
    }
private static byte[] nestedUntypedFixture() {
        return compactObject(
                field("a", new byte[] { 0x33 }),
                field("b", compactArray(new byte[][] { { 0x31 }, { 0x32 } })),
                field("c", compactArray(new byte[][] { { 0x33 } })));
    }
private static byte[] mixedArrayFixture() {
        return compactArray(new byte[][] {
                { 0x31 },
                text("text"),
                { 0x1a },
                { 0x18 },
                { 0x1b, 0x1f, (byte) 0x85, (byte) 0xeb, 0x51, (byte) 0xb8, 0x1e, 0x09, 0x40 },
                compactObject(field("nested", text("object"))),
                compactArray(new byte[][] { { 0x31 }, { 0x32 }, { 0x33 } })
        });
    }
private static byte[] nestedObjectFixture() {
        byte[] value = compactObject(field("value", text("deep")));
        value = compactObject(field("level4", value));
        value = compactObject(field("level3", value));
        value = compactObject(field("level2", value));
        return compactObject(field("level1", value));
    }
private static byte[] largeArrayFixture() {
        byte[][] values = new byte[100][];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i < 10 ? new byte[] { (byte) (0x30 + i) }
                    : new byte[] { 0x28, (byte) i };
        }
        return compactArray(values);
    }
private static byte[] largeObjectFixture() {
        byte[][] fields = new byte[50][];
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = field("key" + i, i < 10 ? new byte[] { (byte) (0x30 + i) }
                    : new byte[] { 0x28, (byte) i });
        }
        return compactObject(fields);
    }
private static byte[] field(String name, byte[] value) {
        return concat(text(name), value);
    }
private static byte[] text(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] compactArray(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) bodyLength += value.length;
        return compact(0x13, values, values.length);
    }
private static byte[] compactObject(byte[]... fields) {
        int bodyLength = 0;
        for (byte[] field : fields) bodyLength += field.length;
        return compact(0x14, fields, fields.length);
    }
private static byte[] compact(int marker, byte[][] body, int count) {
        int bodyLength = 0;
        for (byte[] value : body) bodyLength += value.length;
        int length = 1 + varintLength(1 + 1 + bodyLength + varintLength(count))
                + bodyLength + varintLength(count);
        while (length != 1 + varintLength(length) + bodyLength + varintLength(count)) {
            length = 1 + varintLength(length) + bodyLength + varintLength(count);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(length);
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
private static void writeForward(ByteArrayOutputStream output, int value) {
        do {
            int group = value & 0x7f;
            value >>>= 7;
            output.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
    }
private static void writeReverse(ByteArrayOutputStream output, int value) {
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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] value : values) output.writeBytes(value);
        return output.toByteArray();
    }

    void __invoke_testBigIntegerCoercion() throws Exception {
        try {
            testBigIntegerCoercion();
        } finally {
        }
    }


    void __invoke_testDeeplyNestedStructures() throws Exception {
        try {
            testDeeplyNestedStructures();
        } finally {
        }
    }


    void __invoke_testEmptyArrayAndObject() throws Exception {
        try {
            testEmptyArrayAndObject();
        } finally {
        }
    }


    void __invoke_testFloatDeserializationWithBigDecimal() throws Exception {
        try {
            testFloatDeserializationWithBigDecimal();
        } finally {
        }
    }


    void __invoke_testFloatTypesFloat32AndFloat64() throws Exception {
        try {
            testFloatTypesFloat32AndFloat64();
        } finally {
        }
    }


    void __invoke_testLargeArray() throws Exception {
        try {
            testLargeArray();
        } finally {
        }
    }


    void __invoke_testLargeObject() throws Exception {
        try {
            testLargeObject();
        } finally {
        }
    }


    void __invoke_testMixedTypesInArray() throws Exception {
        try {
            testMixedTypesInArray();
        } finally {
        }
    }


    void __invoke_testNaNHandling() throws Exception {
        try {
            testNaNHandling();
        } finally {
        }
    }


    void __invoke_testNestedUntyped() throws Exception {
        try {
            testNestedUntyped();
        } finally {
        }
    }

}
