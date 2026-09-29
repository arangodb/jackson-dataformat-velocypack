package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0476F1 {
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

    // Provenance: JsonNodeBooleanValueTest#asBooleanFailFromNumbersFloat().
    void asBooleanFailFromNumbersFloatVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES_MAPPER.getNodeFactory().numberNode(0.25f),
                NODES_MAPPER.getNodeFactory().numberNode(-2.125d),
                NODES_MAPPER.getNodeFactory().numberNode(new BigDecimal("0.1")),
                MAPPER.readTree(DOUBLE_100_5) }) {
            assertFailAsBoolean(node);
        }
    }

    // Provenance: JsonNodeBooleanValueTest#asBooleanFailFromStructural().
    void asBooleanFailFromStructuralVpack() throws Exception {
        assertFailAsBoolean(MAPPER.readTree(ARRAY));
        assertFailAsBoolean(MAPPER.readTree(OBJECT));
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

    void __invoke_asBooleanFailFromNumbersFloatVpack() throws Exception {
        try {
            asBooleanFailFromNumbersFloatVpack();
        } finally {
        }
    }


    void __invoke_asBooleanFailFromStructuralVpack() throws Exception {
        try {
            asBooleanFailFromStructuralVpack();
        } finally {
        }
    }

}
