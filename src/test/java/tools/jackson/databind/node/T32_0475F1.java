package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0475F1 {
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] DOUBLE_100_75 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 30 59 40");
private static final byte[] DOUBLE_NEGATIVE_1_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f4 bf");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromMiscOther().
    void asBigIntegerFromMiscOtherVpack() {
        assertEquals(BigInteger.ZERO, NODES.nullNode().asBigInteger());
        assertEquals(BigInteger.valueOf(9999999L),
                NODES.nullNode().asBigInteger(BigInteger.valueOf(9999999L)));
        assertFalse(NODES.nullNode().asBigIntegerOpt().isPresent());

        assertEquals(BigInteger.ZERO, NODES.missingNode().asBigInteger());
        assertEquals(BigInteger.valueOf(9999999L),
                NODES.missingNode().asBigInteger(BigInteger.valueOf(9999999L)));
        assertFalse(NODES.missingNode().asBigIntegerOpt().isPresent());
    }

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromNonNumberScalar().
    void asBigIntegerFromNonNumberScalarVpack() throws Exception {
        assertAsBigIntegerFail(NODES.binaryNode(new byte[3]));
        assertAsBigIntegerFail(NODES.booleanNode(true));
        assertAsBigIntegerFail(NODES.rawValueNode(new RawValue("abc")));
        assertAsBigIntegerFail(NODES.pojoNode(Boolean.TRUE));
        assertAsBigIntegerFail(NODES.stringNode("E000"));

        assertAsBigInteger(BigInteger.TEN, NODES.pojoNode(BigInteger.TEN));
        assertAsBigInteger(BigInteger.TEN, NODES.pojoNode(Integer.valueOf(10)));
        assertAsBigInteger(BigInteger.TEN, NODES.pojoNode(Long.valueOf(10)));
        assertAsBigInteger(BigInteger.TEN, NODES.stringNode("10"));
        assertAsBigInteger(BigInteger.valueOf(-99), NODES.stringNode("-99"));

        // The native binary value is independently encoded, not writer output.
        assertAsBigIntegerFail(MAPPER.readTree(BINARY));
    }

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromNumberFPFraction().
    void asBigIntegerFromNumberFPFractionVpack() throws Exception {
        assertAsBigInteger(BigInteger.valueOf(100), readDouble(DOUBLE_100_75));
        assertAsBigInteger(BigInteger.valueOf(-1), readDouble(DOUBLE_NEGATIVE_1_25));
        assertAsBigInteger(BigInteger.valueOf(100), NODES.numberNode(new BigDecimal("100.75")));
        assertAsBigInteger(BigInteger.valueOf(-1), NODES.numberNode(new BigDecimal("-1.25")));
    }
private void assertAsBigInteger(BigInteger expected, JsonNode node) {
        assertEquals(expected, node.asBigInteger());
        assertEquals(expected, node.asBigInteger(BigInteger.valueOf(9999999L)));
        assertEquals(expected, node.asBigIntegerOpt().get());
    }
private void assertAsBigIntegerFail(JsonNode node) {
        assertThrows(JsonNodeException.class, node::asBigInteger);
        assertEquals(BigInteger.ONE, node.asBigInteger(BigInteger.ONE));
        assertFalse(node.asBigIntegerOpt().isPresent());
    }
private void assertEmptyStreams(JsonNode node) {
        assertEquals(0, node.valueStream().count());
        assertEquals(0, node.propertyStream().count());
        node.forEachEntry((name, value) -> {
            throw new AssertionError("value node has no object entries");
        });
    }
private JsonNode readDouble(byte[] bytes) throws Exception {
        return MAPPER.readTree(bytes);
    }

    void __invoke_asBigIntegerFromMiscOtherVpack() throws Exception {
        try {
            asBigIntegerFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_asBigIntegerFromNonNumberScalarVpack() throws Exception {
        try {
            asBigIntegerFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_asBigIntegerFromNumberFPFractionVpack() throws Exception {
        try {
            asBigIntegerFromNumberFPFractionVpack();
        } finally {
        }
    }

}
