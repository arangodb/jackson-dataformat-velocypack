package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.ShortNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0496F0 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INT_NEGATIVE_90184 = VPackWireFixtureTest.hex(
            "22 b8 9f fe");
private static final byte[] LONG_12345678_SHIFTED = VPackWireFixtureTest.hex(
            "2f 00 00 00 00 4e 61 bc 00");
private static final byte[] BCD_TEN = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 10");
private static final byte[] BCD_QUARTER = VPackWireFixtureTest.hex(
            "c8 01 fe ff ff ff 25");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NumberNodesTest#testShort().
    void testShortVpack() throws Exception {
        ShortNode n = ShortNode.valueOf((short) 1);
        assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
        assertEquals(JsonParser.NumberType.INT, n.numberType());
        assertEquals(1, n.intValue());
        assertEquals(1L, n.longValue());
        assertEquals(BigDecimal.ONE, n.decimalValue());
        assertEquals(BigInteger.ONE, n.bigIntegerValue());
        assertEquals("1", n.asString());
        assertTrue(ShortNode.valueOf((short) 0).canConvertToInt());
        assertTrue(ShortNode.valueOf(Short.MAX_VALUE).canConvertToInt());
        assertTrue(ShortNode.valueOf(Short.MIN_VALUE).canConvertToInt());
        assertTrue(ShortNode.valueOf((short) 0).canConvertToLong());
        assertTrue(ShortNode.valueOf(Short.MAX_VALUE).canConvertToLong());
        assertTrue(ShortNode.valueOf(Short.MIN_VALUE).canConvertToLong());

        assertEquals(ShortNode.valueOf((short) 1), mapper.valueToTree(n));
        assertEquals(1, mapper.readTree(VPackWireFixtureTest.hex("31")).intValue());
    }

    // Provenance: NumberNodesTest#testIntViaMapper().
    void testIntViaMapperVpack() throws Exception {
        int value = -90184;
        JsonNode result = mapper.readTree(INT_NEGATIVE_90184);
        assertTrue(result.isNumber());
        assertTrue(result.isIntegralNumber());
        assertTrue(result.isInt());
        assertEquals(IntNode.class, result.getClass());
        assertFalse(result.isLong());
        assertFalse(result.isFloatingPointNumber());
        assertFalse(result.isDouble());
        assertFalse(result.isNull());
        assertFalse(result.isString());
        assertFalse(result.isMissingNode());
        assertTrue(result.canConvertToInt());
        assertTrue(result.canConvertToLong());
        assertTrue(result.canConvertToExactIntegral());
        assertEquals(value, result.numberValue().intValue());
        assertEquals(value, result.intValue());
        assertEquals(String.valueOf(value), result.asString());
        assertEquals((double) value, result.doubleValue());
        assertEquals((long) value, result.longValue());
        assertEquals(result, IntNode.valueOf(value));
    }

    // Provenance: NumberNodesTest#testLong().
    void testLongVpack() throws Exception {
        LongNode n = LongNode.valueOf(1L);
        assertEquals(JsonToken.VALUE_NUMBER_INT, n.asToken());
        assertEquals(JsonParser.NumberType.LONG, n.numberType());
        assertEquals(1, n.intValue());
        assertEquals(1L, n.longValue());
        assertEquals(BigDecimal.ONE, n.decimalValue());
        assertEquals(BigInteger.ONE, n.bigIntegerValue());
        assertEquals("1", n.asString());
        assertTrue(LongNode.valueOf(0L).canConvertToInt());
        assertTrue(LongNode.valueOf(Integer.MAX_VALUE).canConvertToInt());
        assertTrue(LongNode.valueOf(Integer.MIN_VALUE).canConvertToInt());
        assertFalse(LongNode.valueOf(1L + Integer.MAX_VALUE).canConvertToInt());
        assertFalse(LongNode.valueOf(-1L + Integer.MIN_VALUE).canConvertToInt());
        assertTrue(LongNode.valueOf(0L).canConvertToLong());
        assertTrue(LongNode.valueOf(Long.MAX_VALUE).canConvertToLong());
        assertTrue(LongNode.valueOf(Long.MIN_VALUE).canConvertToLong());
        assertEquals(1L, mapper.readTree(VPackWireFixtureTest.hex("31")).longValue());
    }

    // Provenance: NumberNodesTest#testLongViaMapper().
    void testLongViaMapperVpack() throws Exception {
        long value = 12345678L << 32;
        JsonNode result = mapper.readTree(LONG_12345678_SHIFTED);
        assertTrue(result.isNumber());
        assertTrue(result.isIntegralNumber());
        assertTrue(result.isLong());
        assertEquals(LongNode.class, result.getClass());
        assertFalse(result.isInt());
        assertFalse(result.isFloatingPointNumber());
        assertFalse(result.isDouble());
        assertFalse(result.isNull());
        assertFalse(result.isString());
        assertFalse(result.isMissingNode());
        assertEquals(value, result.numberValue().longValue());
        assertEquals(value, result.longValue());
        assertEquals(String.valueOf(value), result.asString());
        assertEquals((double) value, result.doubleValue());
        assertFalse(result.canConvertToInt());
        assertTrue(result.canConvertToLong());
        assertTrue(result.canConvertToExactIntegral());
        assertEquals(result, LongNode.valueOf(value));
    }
@JsonDeserialize(as = DataImpl.class)
    public interface Data {
    }
public static class DataImpl implements Data {
        private final JsonNode root;

        @JsonCreator
        public DataImpl(JsonNode root) {
            this.root = root;
        }

        @JsonValue
        public JsonNode value() {
            return root;
        }
    }

    void __invoke_testShortVpack() throws Exception {
        try {
            testShortVpack();
        } finally {
        }
    }


    void __invoke_testIntViaMapperVpack() throws Exception {
        try {
            testIntViaMapperVpack();
        } finally {
        }
    }


    void __invoke_testLongVpack() throws Exception {
        try {
            testLongVpack();
        } finally {
        }
    }


    void __invoke_testLongViaMapperVpack() throws Exception {
        try {
            testLongViaMapperVpack();
        } finally {
        }
    }

}
