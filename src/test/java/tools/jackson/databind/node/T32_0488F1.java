package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0488F1 {
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] STRING_HELLO = VPackWireFixtureTest.hex(
            "45 68 65 6c 6c 6f");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");
private static final byte[] BYTE_127 = VPackWireFixtureTest.hex("28 7f");
private static final byte[] LONG_3456 = VPackWireFixtureTest.hex("29 80 0d");
private static final byte[] DOUBLE_NEGATIVE_2_125 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 01 c0");
private static final byte[] BCD_POINT_ONE = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 01");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 01 02 03");
private static final byte[] BOOLEAN_TRUE = VPackWireFixtureTest.hex("19");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeNumberValueTest#numberValueFromNumbersInt().
    void numberValueFromNumbersIntVpack() throws Exception {
        assertEquals(Integer.valueOf(127), NODES.numberNode((byte) 127).numberValue());
        assertEquals(Short.valueOf((short) 123), NODES.numberNode((short) 123).numberValue());
        assertEquals(Integer.valueOf(234), NODES.numberNode(234).numberValue());
        assertEquals(Long.valueOf(3456L), NODES.numberNode(3456L).numberValue());
        assertEquals(BigInteger.TWO, NODES.numberNode(BigInteger.TWO).numberValue());

        assertEquals(Integer.valueOf(127), MAPPER.readTree(BYTE_127).numberValue());
        // VPack preserves the exact value, not the source Java integral wrapper type.
        assertEquals(Integer.valueOf(3456), MAPPER.readTree(LONG_3456).numberValue());
        assertEquals(Integer.valueOf(42), MAPPER.readTree(INTEGER_42).numberValue());
    }

    // Provenance: JsonNodeNumberValueTest#numberValueFromNumbersFP().
    void numberValueFromNumbersFPVpack() throws Exception {
        assertEquals(Float.valueOf(0.25f), NODES.numberNode(0.25f).numberValue());
        assertEquals(Double.valueOf(-2.125d), NODES.numberNode(-2.125d).numberValue());
        assertEquals(new BigDecimal("0.1"),
                NODES.numberNode(new BigDecimal("0.1")).numberValue());

        assertEquals(Double.valueOf(-2.125d), MAPPER.readTree(DOUBLE_NEGATIVE_2_125).numberValue());
        assertEquals(new BigDecimal("0.1"), MAPPER.readTree(BCD_POINT_ONE).numberValue());
    }

    // Provenance: JsonNodeNumberValueTest#numberValueFromNonNumberScalars().
    void numberValueFromNonNumberScalarsVpack() throws Exception {
        assertNonNumericIntValue(NODES.booleanNode(true));
        assertNonNumericIntValue(NODES.binaryNode(new byte[3]));
        assertNonNumericIntValue(NODES.stringNode("123"));
        assertNonNumericIntValue(NODES.rawValueNode(new RawValue("abc")));
        assertNonNumericIntValue(NODES.pojoNode(Boolean.TRUE));

        assertNonNumericIntValue(MAPPER.readTree(BOOLEAN_TRUE));
        assertNonNumericIntValue(MAPPER.readTree(BINARY));
        assertNonNumericIntValue(MAPPER.readTree(VPackWireFixtureTest.hex("43 31 32 33")));
    }

    // Provenance: JsonNodeNumberValueTest#numberValueFromStructural().
    void numberValueFromStructuralVpack() throws Exception {
        assertNonNumericIntValue(NODES.arrayNode(3));
        assertNonNumericIntValue(NODES.objectNode());
        assertNonNumericIntValue(MAPPER.readTree(EMPTY_ARRAY));
        assertNonNumericIntValue(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeNumberValueTest#numberValueFromNonNumberMisc().
    void numberValueFromNonNumberMiscVpack() throws Exception {
        assertNonNumericIntValue(NODES.nullNode());
        assertNonNumericIntValue(NODES.missingNode());
        assertNonNumericIntValue(MAPPER.readTree(NULL));
    }
private static void assertNonNumericIntValue(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::intValue);
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not numeric"));
    }
private static void assertNaNFailure(JsonNode node) {
        assertThrows(JsonNodeException.class, node::asShort);
        assertEquals((short) 99, node.asShort((short) 99));
        assertFalse(node.asShortOpt().isPresent());
    }

    void __invoke_numberValueFromNumbersIntVpack() throws Exception {
        try {
            numberValueFromNumbersIntVpack();
        } finally {
        }
    }


    void __invoke_numberValueFromNumbersFPVpack() throws Exception {
        try {
            numberValueFromNumbersFPVpack();
        } finally {
        }
    }


    void __invoke_numberValueFromNonNumberScalarsVpack() throws Exception {
        try {
            numberValueFromNonNumberScalarsVpack();
        } finally {
        }
    }


    void __invoke_numberValueFromStructuralVpack() throws Exception {
        try {
            numberValueFromStructuralVpack();
        } finally {
        }
    }


    void __invoke_numberValueFromNonNumberMiscVpack() throws Exception {
        try {
            numberValueFromNonNumberMiscVpack();
        } finally {
        }
    }

}
