package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0481F1 {
private static final double DOUBLE_DEFAULT = -9999.5d;
private static final byte[] HUGE_BCD_POSITIVE = VPackWireFixtureTest.hex(
            "c8 01 35 01 00 00 01");
private static final byte[] HUGE_BCD_NEGATIVE = VPackWireFixtureTest.hex(
            "d0 01 35 01 00 00 01");
private static final byte[] UNSIGNED_128 = VPackWireFixtureTest.hex("28 80");
private static final byte[] SIGNED_NEGATIVE_128 = VPackWireFixtureTest.hex("20 80");
private static final byte[] DOUBLE_1_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] BINARY_ONE_BYTE = VPackWireFixtureTest.hex("c0 01 07");
private static final byte[] TEXT_X = VPackWireFixtureTest.hex("41 78");
private static final byte[] TEXT_123 = VPackWireFixtureTest.hex("43 31 32 33");
private static final byte[] ARRAY_ONE = VPackWireFixtureTest.hex("13 04 31 01");
private static final byte[] ARRAY_TWO = VPackWireFixtureTest.hex("13 04 32 01");
private static final byte[] OBJECT_A_ONE = VPackWireFixtureTest.hex(
            "14 06 41 61 31 01");
private static final byte[] OBJECT_B_TWO = VPackWireFixtureTest.hex(
            "14 06 41 62 32 01");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] DECIMAL_12_5000 = VPackWireFixtureTest.hex(
            "c8 03 fc ff ff ff 12 50 00");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeFactoryTest#testBigDecimalNormalization().
    void testBigDecimalNormalizationVpack() throws Exception {
        BigDecimal nonNormalized = new BigDecimal("12.5000");
        BigDecimal normalized = nonNormalized.stripTrailingZeros();

        JsonNode n1 = MAPPER.readTree(DECIMAL_12_5000);
        assertEquals(nonNormalized, n1.decimalValue());
        assertEquals(4, n1.decimalValue().scale());

        VPackMapper normalizedMapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
                .build();
        JsonNode n2 = normalizedMapper.readTree(DECIMAL_12_5000);
        assertEquals(normalized, n2.decimalValue());
        assertEquals(1, n2.decimalValue().scale());
    }

    // Provenance: JsonNodeFactoryTest#testBigDecimalNormalizationViaValueToTree().
    void testBigDecimalNormalizationViaValueToTreeVpack() {
        BigDecimal withTrailing = new BigDecimal("1.000");
        BigDecimal normalized = withTrailing.stripTrailingZeros();

        JsonNode n1 = MAPPER.valueToTree(withTrailing);
        assertEquals(withTrailing, n1.decimalValue());

        VPackMapper normalizedMapper = VPackMapper.builder()
                .enable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
                .build();
        JsonNode n2 = normalizedMapper.valueToTree(withTrailing);
        assertEquals(normalized, n2.decimalValue());

        Map<String, BigDecimal> values = new LinkedHashMap<>();
        values.put("bd1", new BigDecimal("1.000"));
        values.put("bd2", new BigDecimal("2.00"));
        values.put("bd3", new BigDecimal("3000"));
        ObjectNode tree = normalizedMapper.valueToTree(values);
        assertEquals(normalized, tree.get("bd1").decimalValue());
        assertEquals(new BigDecimal("2").stripTrailingZeros(),
                tree.get("bd2").decimalValue());
        assertEquals(new BigDecimal("3000").stripTrailingZeros(),
                tree.get("bd3").decimalValue());
    }
private static void assertDoubleValue(double expected, JsonNode node) {
        assertEquals(expected, node.doubleValue());
        assertEquals(expected, node.doubleValue(DOUBLE_DEFAULT));
        assertEquals(expected, node.doubleValueOpt().orElseThrow());
    }
private static void assertFailDoubleValueForValueRange(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::doubleValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `double` range"));
        assertEquals(-2.25d, node.doubleValue(-2.25d));
        assertFalse(node.doubleValueOpt().isPresent());
    }
private static void assertFailDoubleValueForNonNumber(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::doubleValue);
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not numeric"));
        assertEquals(1.5d, node.doubleValue(1.5d));
        assertFalse(node.doubleValueOpt().isPresent());
    }
private static void assertAsDouble(double expected, JsonNode node) {
        assertEquals(expected, node.asDouble());
        assertEquals(expected, node.asDouble(DOUBLE_DEFAULT));
        assertEquals(expected, node.asDoubleOpt().orElseThrow());
    }
private static void assertFailAsDoubleForValueRange(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value not in 64-bit `double` range"));
        assertEquals(-2.25d, node.asDouble(-2.25d));
        assertFalse(node.asDoubleOpt().isPresent());
    }
private static void assertFailAsDoubleForNonNumber(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class,
                node::asDouble);
        assertTrue(exception.getMessage().contains("asDouble()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not coercible"));
        assertEquals(1.5d, node.asDouble(1.5d));
        assertFalse(node.asDoubleOpt().isPresent());
    }

    void __invoke_testBigDecimalNormalizationVpack() throws Exception {
        try {
            testBigDecimalNormalizationVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalNormalizationViaValueToTreeVpack() throws Exception {
        try {
            testBigDecimalNormalizationViaValueToTreeVpack();
        } finally {
        }
    }

}
