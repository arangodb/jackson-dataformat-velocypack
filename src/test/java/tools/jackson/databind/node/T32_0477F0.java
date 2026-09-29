package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0477F0 {
private static final byte[] FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final tools.jackson.databind.node.JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeBooleanValueTest#booleanValueOkFromBoolean().
    void booleanValueOkFromBooleanVpack() throws Exception {
        assertBooleanValue(false, NODES.booleanNode(false));
        assertBooleanValue(true, NODES.booleanNode(true));
        assertBooleanValue(false, MAPPER.readTree(FALSE));
        assertBooleanValue(true, MAPPER.readTree(TRUE));
    }

    // Provenance: JsonNodeBooleanValueTest#booleanValueFailFromNumbersInt().
    void booleanValueFailFromNumbersIntVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.numberNode((byte) 1),
                NODES.numberNode((short) 2),
                NODES.numberNode(3),
                NODES.numberNode(4L),
                NODES.numberNode(BigInteger.valueOf(5)),
                MAPPER.readTree(ONE) }) {
            assertFailBooleanValue(node);
        }
    }

    // Provenance: JsonNodeBooleanValueTest#booleanValueFailFromNumbersFloat().
    void booleanValueFailFromNumbersFloatVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.numberNode(0.25f),
                NODES.numberNode(-2.125d),
                NODES.numberNode(new BigDecimal("0.1")),
                MAPPER.readTree(DOUBLE_QUARTER) }) {
            assertFailBooleanValue(node);
        }
    }

    // Provenance: JsonNodeBooleanValueTest#booleanValueFailFromNonNumberScalars().
    void booleanValueFailFromNonNumberScalarsVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.binaryNode(new byte[3]),
                MAPPER.readTree(BINARY),
                NODES.stringNode("123"),
                NODES.rawValueNode(new RawValue("abc")),
                NODES.pojoNode(new AtomicInteger(1)) }) {
            assertFailBooleanValue(node);
        }
    }

    // Provenance: JsonNodeBooleanValueTest#booleanValueFailFromStructural().
    void booleanValueFailFromStructuralVpack() throws Exception {
        assertFailBooleanValue(MAPPER.readTree(EMPTY_ARRAY));
        assertFailBooleanValue(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeBooleanValueTest#booleanValueFailFromNonNumberMisc().
    void booleanValueFailFromNonNumberMiscVpack() throws Exception {
        assertFailBooleanValue(NODES.nullNode());
        assertFailBooleanValue(MAPPER.readTree(NULL));
        assertFailBooleanValue(NODES.missingNode());
    }

    // Provenance: JsonNodeBooleanValueTest#asBooleanOkFromBoolean().
    void asBooleanOkFromBooleanVpack() throws Exception {
        assertAsBoolean(false, NODES.booleanNode(false));
        assertAsBoolean(true, NODES.booleanNode(true));
        assertAsBoolean(false, MAPPER.readTree(FALSE));
        assertAsBoolean(true, MAPPER.readTree(TRUE));
    }

    // Provenance: JsonNodeBooleanValueTest#asBooleanOkFromNumbersInt().
    void asBooleanOkFromNumbersIntVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.numberNode((byte) 0),
                NODES.numberNode((byte) 1),
                NODES.numberNode((byte) -1),
                NODES.numberNode((short) 0),
                NODES.numberNode((short) 1),
                NODES.numberNode((short) 2),
                NODES.numberNode(0),
                NODES.numberNode(1),
                NODES.numberNode(-15),
                NODES.numberNode(0L),
                NODES.numberNode(1L),
                NODES.numberNode(2L),
                NODES.numberNode(BigInteger.ZERO),
                NODES.numberNode(BigInteger.ONE),
                NODES.numberNode(BigInteger.TEN),
                MAPPER.readTree(ZERO),
                MAPPER.readTree(ONE) }) {
            assertAsBoolean(node.asInt() != 0, node);
        }
    }

    // Provenance: JsonNodeBooleanValueTest#asBooleanFromNonNumberScalars().
    void asBooleanFromNonNumberScalarsVpack() throws Exception {
        assertFailAsBoolean(NODES.binaryNode(new byte[3]));
        assertFailAsBoolean(MAPPER.readTree(BINARY));
        assertAsBoolean(false, NODES.stringNode("false"));
        assertAsBoolean(true, NODES.stringNode("true"));
        assertFailAsBoolean(NODES.stringNode("123"));
        assertFailAsBoolean(NODES.rawValueNode(new RawValue("true")));
        assertFailAsBoolean(NODES.pojoNode(Long.valueOf(3)));
    }

    // Provenance: JsonNodeBooleanValueTest#asBooleanFromNonNumberMisc().
    void asBooleanFromNonNumberMiscVpack() throws Exception {
        JsonNode nullNode = MAPPER.readTree(NULL);
        assertEquals(false, nullNode.asBoolean());
        assertEquals(true, nullNode.asBoolean(true));
        assertFalse(nullNode.asBooleanOpt().isPresent());

        assertAsBoolean(false, NODES.pojoNode(Boolean.FALSE));
        assertAsBoolean(true, NODES.pojoNode(Boolean.TRUE));

        JsonNode missing = NODES.missingNode();
        assertEquals(false, missing.asBoolean());
        assertEquals(true, missing.asBoolean(true));
        assertFalse(missing.asBooleanOpt().isPresent());
    }
private void assertBooleanValue(boolean expected, JsonNode node) {
        assertEquals(expected, node.booleanValue());
        assertEquals(expected, node.booleanValue(!expected));
        assertEquals(expected, node.booleanValueOpt().get());
    }
private void assertAsBoolean(boolean expected, JsonNode node) {
        assertEquals(expected, node.asBoolean());
        assertEquals(expected, node.asBoolean(!expected));
        assertEquals(expected, node.asBooleanOpt().get());
    }
private void assertFailBooleanValue(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::booleanValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value type not boolean"));
        assertFalse(node.booleanValue(false));
        assertTrue(node.booleanValue(true));
        assertFalse(node.booleanValueOpt().isPresent());
    }
private void assertFailAsBoolean(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asBoolean);
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not coercible"));
        assertFalse(node.asBoolean(false));
        assertTrue(node.asBoolean(true));
        assertFalse(node.asBooleanOpt().isPresent());
    }

    void __invoke_booleanValueOkFromBooleanVpack() throws Exception {
        try {
            booleanValueOkFromBooleanVpack();
        } finally {
        }
    }


    void __invoke_booleanValueFailFromNumbersIntVpack() throws Exception {
        try {
            booleanValueFailFromNumbersIntVpack();
        } finally {
        }
    }


    void __invoke_booleanValueFailFromNumbersFloatVpack() throws Exception {
        try {
            booleanValueFailFromNumbersFloatVpack();
        } finally {
        }
    }


    void __invoke_booleanValueFailFromNonNumberScalarsVpack() throws Exception {
        try {
            booleanValueFailFromNonNumberScalarsVpack();
        } finally {
        }
    }


    void __invoke_booleanValueFailFromStructuralVpack() throws Exception {
        try {
            booleanValueFailFromStructuralVpack();
        } finally {
        }
    }


    void __invoke_booleanValueFailFromNonNumberMiscVpack() throws Exception {
        try {
            booleanValueFailFromNonNumberMiscVpack();
        } finally {
        }
    }


    void __invoke_asBooleanOkFromBooleanVpack() throws Exception {
        try {
            asBooleanOkFromBooleanVpack();
        } finally {
        }
    }


    void __invoke_asBooleanOkFromNumbersIntVpack() throws Exception {
        try {
            asBooleanOkFromNumbersIntVpack();
        } finally {
        }
    }


    void __invoke_asBooleanFromNonNumberScalarsVpack() throws Exception {
        try {
            asBooleanFromNonNumberScalarsVpack();
        } finally {
        }
    }


    void __invoke_asBooleanFromNonNumberMiscVpack() throws Exception {
        try {
            asBooleanFromNonNumberMiscVpack();
        } finally {
        }
    }

}
