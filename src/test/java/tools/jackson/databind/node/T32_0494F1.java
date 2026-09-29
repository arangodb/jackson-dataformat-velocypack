package tools.jackson.databind.node;

import java.math.BigDecimal;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0494F1 {
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] ARRAY_ROOT = VPackWireFixtureTest.hex(
            "13 12 19 14 0c 46 61 6e 73 77 65 72 28 2a 01 28 89 03");
private static final byte[] OBJECT_ROOT = VPackWireFixtureTest.hex(
            "14 39 46 61 6e 73 77 65 72 28 2a"
          + "46 6d 61 74 72 69 78 13 0f 31 2c 35 1c dc df 02 1a 43 2e 2e 2e 04"
          + "44 6d 69 73 63 14 12 45 76 61 6c 75 65 1b 00 00 00 00 00 00 d0 3f 01 03");
private static final byte[] NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 06 41 78 18 01");
private static final byte[] NULL_COVARIANCE = VPackWireFixtureTest.hex(
            "14 12 46 6f 62 6a 65 63 74 18 45 61 72 72 61 79 18 02");
private static final byte[] SCALED_DECIMAL = VPackWireFixtureTest.hex(
            "c8 04 fc ff ff ff 01 23 45 00");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NotANumberConversionTest#testBigDecimalWithNaN().
    void testBigDecimalWithNaNVpack() throws Exception {
        JsonNode nan = mapper.readTree(NAN);
        JsonNode negativeInfinity = mapper.readTree(NEGATIVE_INFINITY);
        JsonNode positiveInfinity = mapper.readTree(POSITIVE_INFINITY);

        assertNotNull(nan);
        assertNotNull(negativeInfinity);
        assertNotNull(positiveInfinity);
        assertTrue(nan.isNumber());
        assertTrue(negativeInfinity.isNumber());
        assertTrue(positiveInfinity.isNumber());
        assertTrue(Double.isNaN(nan.doubleValue()));
        assertEquals(Double.NEGATIVE_INFINITY, negativeInfinity.doubleValue());
        assertEquals(Double.POSITIVE_INFINITY, positiveInfinity.doubleValue());
    }

    // Provenance: NotANumberConversionTest#testBigDecimalWithoutNaN().
    void testBigDecimalWithoutNaNVpack() throws Exception {
        BigDecimal input = new BigDecimal(Double.MIN_VALUE).divide(new BigDecimal(10L));
        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(input));
        assertTrue(tree.isBigDecimal());
        assertEquals(input, tree.decimalValue());

        JsonNode literal = mapper.readTree(SCALED_DECIMAL);
        assertTrue(literal.isBigDecimal());
        assertEquals(new BigDecimal("123.4500"), literal.decimalValue());
        assertEquals(4, literal.decimalValue().scale());
    }
private JsonNode roundTrip(JsonNode input) throws Exception {
        return mapper.readTree(mapper.writeValueAsBytes(input));
    }
public static class CovarianceBean {
        public ObjectNode object;
        public ArrayNode array;
    }
@SuppressWarnings("serial")
    public static class MyNull extends NullNode { }

    void __invoke_testBigDecimalWithNaNVpack() throws Exception {
        try {
            testBigDecimalWithNaNVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalWithoutNaNVpack() throws Exception {
        try {
            testBigDecimalWithoutNaNVpack();
        } finally {
        }
    }

}
