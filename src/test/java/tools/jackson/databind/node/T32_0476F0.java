package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0476F0 {
private static final byte[] INT_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] DOUBLE_ONE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 3f");
private static final byte[] DOUBLE_100_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 59 40");
private static final byte[] DOUBLE_NEGATIVE_0_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 bf");
private static final byte[] DOUBLE_NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] DOUBLE_NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] DOUBLE_POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final VPackMapper NODES_MAPPER = MAPPER;

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromNumberIntOk().
    void bigIntegerValueFromNumberIntOkVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().numberNode((byte) 1),
                NODES_MAPPER.getNodeFactory().numberNode((short) 1),
                NODES_MAPPER.getNodeFactory().numberNode(1),
                NODES_MAPPER.getNodeFactory().numberNode(1L),
                NODES_MAPPER.getNodeFactory().numberNode(BigInteger.ONE),
                MAPPER.readTree(INT_ONE) }) {
            assertBigIntegerValue(BigInteger.ONE, node);
        }
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromNumberFPOk().
    void bigIntegerValueFromNumberFPOkVpack() throws Exception {
        assertBigIntegerValue(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(1.0f));
        assertBigIntegerValue(BigInteger.valueOf(100_000),
                NODES_MAPPER.getNodeFactory().numberNode(100_000.0f));
        assertBigIntegerValue(BigInteger.valueOf(-100_000),
                NODES_MAPPER.getNodeFactory().numberNode(-100_000.0f));
        assertBigIntegerValue(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(1.0d));
        assertBigIntegerValue(BigInteger.valueOf(100_000_000),
                NODES_MAPPER.getNodeFactory().numberNode(100_000_000.0d));
        assertBigIntegerValue(BigInteger.valueOf(-100_000_000),
                NODES_MAPPER.getNodeFactory().numberNode(-100_000_000.0d));
        assertBigIntegerValue(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(1.0d)));
        assertBigIntegerValue(BigInteger.valueOf(Long.MIN_VALUE),
                NODES_MAPPER.getNodeFactory().numberNode(new BigDecimal(Long.MIN_VALUE + ".0")));
        assertBigIntegerValue(BigInteger.valueOf(Long.MAX_VALUE),
                NODES_MAPPER.getNodeFactory().numberNode(new BigDecimal(Long.MAX_VALUE + ".0")));
        assertBigIntegerValue(BigInteger.ONE, MAPPER.readTree(DOUBLE_ONE));
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromNumberFPFailForNaN().
    void bigIntegerValueFromNumberFPFailForNaNVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().numberNode(Float.NaN),
                NODES_MAPPER.getNodeFactory().numberNode(Float.NEGATIVE_INFINITY),
                NODES_MAPPER.getNodeFactory().numberNode(Float.POSITIVE_INFINITY),
                NODES_MAPPER.getNodeFactory().numberNode(Double.NaN),
                NODES_MAPPER.getNodeFactory().numberNode(Double.NEGATIVE_INFINITY),
                NODES_MAPPER.getNodeFactory().numberNode(Double.POSITIVE_INFINITY),
                MAPPER.readTree(DOUBLE_NAN),
                MAPPER.readTree(DOUBLE_NEGATIVE_INFINITY),
                MAPPER.readTree(DOUBLE_POSITIVE_INFINITY) }) {
            assertFailBigIntegerValue(node, "cannot convert value", "value non-Finite ('NaN')");
        }
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromNumberFPFailFraction().
    void bigIntegerValueFromNumberFPFailFractionVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().numberNode(100.5f),
                NODES_MAPPER.getNodeFactory().numberNode(-0.25f),
                NODES_MAPPER.getNodeFactory().numberNode(100.5d),
                NODES_MAPPER.getNodeFactory().numberNode(-0.25d),
                NODES_MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(100.5d)),
                NODES_MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(-0.25d)),
                MAPPER.readTree(DOUBLE_100_5),
                MAPPER.readTree(DOUBLE_NEGATIVE_0_25) }) {
            assertFailBigIntegerValue(node, "cannot convert value",
                    "to `java.math.BigInteger`: value has fractional part");
        }
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromNonNumberScalarFail().
    void bigIntegerValueFromNonNumberScalarFailVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().booleanNode(true),
                NODES_MAPPER.getNodeFactory().binaryNode(new byte[3]),
                NODES_MAPPER.getNodeFactory().stringNode("123"),
                NODES_MAPPER.getNodeFactory().rawValueNode(new RawValue("abc")),
                NODES_MAPPER.getNodeFactory().pojoNode(Boolean.TRUE),
                MAPPER.readTree(BINARY) }) {
            assertFailBigIntegerValue(node, "cannot coerce value", "value type not numeric");
        }
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromStructuralFail().
    void bigIntegerValueFromStructuralFailVpack() throws Exception {
        assertFailBigIntegerValue(MAPPER.readTree(ARRAY),
                "cannot coerce value", "value type not numeric");
        assertFailBigIntegerValue(MAPPER.readTree(OBJECT),
                "cannot coerce value", "value type not numeric");
    }

    // Provenance: JsonNodeBigIntegerValueTest#bigIntegerValueFromMiscOtherFail().
    void bigIntegerValueFromMiscOtherFailVpack() {
        assertFailBigIntegerValue(NODES_MAPPER.getNodeFactory().nullNode(),
                "cannot coerce value", "value type not numeric");
        assertFailBigIntegerValue(NODES_MAPPER.getNodeFactory().missingNode(),
                "cannot coerce value", "value type not numeric");
    }

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromNumberIntOk().
    void asBigIntegerFromNumberIntOkVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().numberNode((byte) 1),
                NODES_MAPPER.getNodeFactory().numberNode((short) 1),
                NODES_MAPPER.getNodeFactory().numberNode(1),
                NODES_MAPPER.getNodeFactory().numberNode(1L),
                NODES_MAPPER.getNodeFactory().numberNode(BigInteger.ONE),
                MAPPER.readTree(INT_ONE) }) {
            assertAsBigInteger(BigInteger.ONE, node);
        }
    }

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromNumberFPOk().
    void asBigIntegerFromNumberFPOkVpack() throws Exception {
        assertAsBigInteger(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(1.0f));
        assertAsBigInteger(BigInteger.valueOf(100_000),
                NODES_MAPPER.getNodeFactory().numberNode(100_000.0f));
        assertAsBigInteger(BigInteger.valueOf(-100_000),
                NODES_MAPPER.getNodeFactory().numberNode(-100_000.0f));
        assertAsBigInteger(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(1.0d));
        assertAsBigInteger(BigInteger.valueOf(100_000_000),
                NODES_MAPPER.getNodeFactory().numberNode(100_000_000.0d));
        assertAsBigInteger(BigInteger.valueOf(-100_000_000),
                NODES_MAPPER.getNodeFactory().numberNode(-100_000_000.0d));
        assertAsBigInteger(BigInteger.ONE,
                NODES_MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(1.0d)));
        assertAsBigInteger(BigInteger.valueOf(Long.MIN_VALUE),
                NODES_MAPPER.getNodeFactory().numberNode(new BigDecimal(Long.MIN_VALUE + ".0")));
        assertAsBigInteger(BigInteger.valueOf(Long.MAX_VALUE),
                NODES_MAPPER.getNodeFactory().numberNode(new BigDecimal(Long.MAX_VALUE + ".0")));
        assertAsBigInteger(BigInteger.ONE, MAPPER.readTree(DOUBLE_ONE));
    }

    // Provenance: JsonNodeBigIntegerValueTest#asBigIntegerFromStructuralFail().
    void asBigIntegerFromStructuralFailVpack() throws Exception {
        assertFailAsBigInteger(MAPPER.readTree(ARRAY));
        assertFailAsBigInteger(MAPPER.readTree(OBJECT));
    }
private void assertBigIntegerValue(BigInteger expected, JsonNode node) {
        assertEquals(expected, node.bigIntegerValue());
        assertEquals(expected, node.bigIntegerValue(BigInteger.valueOf(9999999L)));
        assertEquals(expected, node.bigIntegerValueOpt().get());
    }
private void assertFailBigIntegerValue(JsonNode node, String... messageParts) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::bigIntegerValue);
        for (String messagePart : messageParts) {
            assertTrue(exception.getMessage().contains(messagePart),
                    () -> "Missing diagnostic '" + messagePart + "' in: " + exception.getMessage());
        }
        assertEquals(BigInteger.ONE, node.bigIntegerValue(BigInteger.ONE));
        assertFalse(node.bigIntegerValueOpt().isPresent());
    }
private void assertAsBigInteger(BigInteger expected, JsonNode node) {
        assertEquals(expected, node.asBigInteger());
        assertEquals(expected, node.asBigInteger(BigInteger.valueOf(9999999L)));
        assertEquals(expected, node.asBigIntegerOpt().get());
    }
private void assertFailAsBigInteger(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asBigInteger);
        assertTrue(exception.getMessage().contains("cannot coerce value"),
                () -> "Missing coercion diagnostic in: " + exception.getMessage());
        assertEquals(BigInteger.ONE, node.asBigInteger(BigInteger.ONE));
        assertFalse(node.asBigIntegerOpt().isPresent());
    }
private void assertFailAsBoolean(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asBoolean);
        assertTrue(exception.getMessage().contains("cannot coerce value"),
                () -> "Missing coercion diagnostic in: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("value type not coercible"),
                () -> "Missing type diagnostic in: " + exception.getMessage());
        assertFalse(node.asBoolean(false));
        assertTrue(node.asBoolean(true));
        assertFalse(node.asBooleanOpt().isPresent());
    }

    void __invoke_bigIntegerValueFromNumberIntOkVpack() throws Exception {
        try {
            bigIntegerValueFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromNumberFPOkVpack() throws Exception {
        try {
            bigIntegerValueFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromNumberFPFailForNaNVpack() throws Exception {
        try {
            bigIntegerValueFromNumberFPFailForNaNVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromNumberFPFailFractionVpack() throws Exception {
        try {
            bigIntegerValueFromNumberFPFailFractionVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromNonNumberScalarFailVpack() throws Exception {
        try {
            bigIntegerValueFromNonNumberScalarFailVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromStructuralFailVpack() throws Exception {
        try {
            bigIntegerValueFromStructuralFailVpack();
        } finally {
        }
    }


    void __invoke_bigIntegerValueFromMiscOtherFailVpack() throws Exception {
        try {
            bigIntegerValueFromMiscOtherFailVpack();
        } finally {
        }
    }


    void __invoke_asBigIntegerFromNumberIntOkVpack() throws Exception {
        try {
            asBigIntegerFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_asBigIntegerFromNumberFPOkVpack() throws Exception {
        try {
            asBigIntegerFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_asBigIntegerFromStructuralFailVpack() throws Exception {
        try {
            asBigIntegerFromStructuralFailVpack();
        } finally {
        }
    }

}
