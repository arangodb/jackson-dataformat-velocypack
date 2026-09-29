package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0490F1 {
private static final byte[] SHORT_MIN = VPackWireFixtureTest.hex("21 00 80");
private static final byte[] SHORT_MAX = VPackWireFixtureTest.hex("21 ff 7f");
private static final byte[] INT_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] INT_TWO = VPackWireFixtureTest.hex("32");
private static final byte[] INT_THREE = VPackWireFixtureTest.hex("33");
private static final byte[] INT_FOUR = VPackWireFixtureTest.hex("34");
private static final byte[] INT_TEN = VPackWireFixtureTest.hex("28 0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] BINARY_TWO_ZEROES = VPackWireFixtureTest.hex(
            "c0 02 00 00");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] ARRAY_ONE = VPackWireFixtureTest.hex("02 03 31");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] DOUBLE_NEGATIVE_2_125 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 01 c0");
private static final byte[] BCD_POINT_ONE = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff ff 01");
private final VPackMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeStringValueTest#stringValueSuccess().
    void stringValueSuccessVpack() throws Exception {
        assertStringValueSuccess(NODES.stringNode("abc"));
        assertStringValueSuccess(MAPPER.readTree(TEXT_ABC));
    }

    // Provenance: JsonNodeStringValueTest#stringValueFailFromNumbers().
    void stringValueFailFromNumbersVpack() throws Exception {
        assertStringValueFailure(NODES.numberNode((byte) 1));
        assertStringValueFailure(NODES.numberNode((short) 2));
        assertStringValueFailure(NODES.numberNode(3));
        assertStringValueFailure(NODES.numberNode(4L));
        assertStringValueFailure(NODES.numberNode(BigInteger.valueOf(5)));
        assertStringValueFailure(NODES.numberNode(0.25f));
        assertStringValueFailure(NODES.numberNode(-2.125d));
        assertStringValueFailure(NODES.numberNode(new BigDecimal("0.1")));

        assertStringValueFailure(MAPPER.readTree(INT_ONE));
        assertStringValueFailure(MAPPER.readTree(DOUBLE_QUARTER));
        assertStringValueFailure(MAPPER.readTree(BCD_POINT_ONE));
    }

    // Provenance: JsonNodeStringValueTest#stringValueFailFromNonNumberScalars().
    void stringValueFailFromNonNumberScalarsVpack() throws Exception {
        assertStringValueFailure(NODES.binaryNode(new byte[3]));
        assertStringValueFailure(NODES.rawValueNode(new RawValue("abc")));
        assertStringValueFailure(NODES.pojoNode(new AtomicInteger(1)));
        assertStringValueFailure(MAPPER.readTree(BINARY_TWO_ZEROES));
    }

    // Provenance: JsonNodeStringValueTest#stringValueFromStructural().
    void stringValueFromStructuralVpack() throws Exception {
        assertStringValueFailure(NODES.arrayNode(3));
        assertStringValueFailure(NODES.objectNode());
        assertStringValueFailure(MAPPER.readTree(ARRAY_ONE));
        assertStringValueFailure(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeStringValueTest#stringValueFromNonNumberMisc().
    void stringValueFromNonNumberMiscVpack() {
        assertStringValueFailure(NODES.missingNode());
    }

    // Provenance: JsonNodeStringValueTest#stringValueFromNullNode().
    void stringValueFromNullNodeVpack() throws Exception {
        assertEquals(null, NODES.nullNode().stringValue());
        assertEquals("foo", NODES.nullNode().stringValue("foo"));
        assertFalse(NODES.nullNode().stringValueOpt().isPresent());

        JsonNode wireNull = MAPPER.readTree(NULL);
        assertEquals(null, wireNull.stringValue());
        assertEquals("foo", wireNull.stringValue("foo"));
        assertFalse(wireNull.stringValueOpt().isPresent());
    }

    // Provenance: JsonNodeStringValueTest#asStringSuccess().
    void asStringSuccessVpack() throws Exception {
        assertAsStringSuccess("abc", NODES.stringNode("abc"));
        assertAsStringSuccess("abc", MAPPER.readTree(TEXT_ABC));
    }

    // Provenance: JsonNodeStringValueTest#asStringFromNumbers().
    void asStringFromNumbersVpack() throws Exception {
        assertAsStringSuccess("1", NODES.numberNode((byte) 1));
        assertAsStringSuccess("2", NODES.numberNode((short) 2));
        assertAsStringSuccess("3", NODES.numberNode(3));
        assertAsStringSuccess("4", NODES.numberNode(4L));
        assertAsStringSuccess("10", NODES.numberNode(BigInteger.TEN));
        assertAsStringSuccess("0.25", NODES.numberNode(0.25f));
        assertAsStringSuccess("-2.125", NODES.numberNode(-2.125d));
        assertAsStringSuccess("0.1", NODES.numberNode(new BigDecimal("0.1")));

        assertAsStringSuccess("1", MAPPER.readTree(INT_ONE));
        assertAsStringSuccess("2", MAPPER.readTree(INT_TWO));
        assertAsStringSuccess("3", MAPPER.readTree(INT_THREE));
        assertAsStringSuccess("4", MAPPER.readTree(INT_FOUR));
        assertAsStringSuccess("10", MAPPER.readTree(INT_TEN));
        assertAsStringSuccess("0.25", MAPPER.readTree(DOUBLE_QUARTER));
        assertAsStringSuccess("-2.125", MAPPER.readTree(DOUBLE_NEGATIVE_2_125));
        assertAsStringSuccess("0.1", MAPPER.readTree(BCD_POINT_ONE));
    }

    // Provenance: JsonNodeStringValueTest#asStringForNonNumberScalars().
    void asStringForNonNumberScalarsVpack() throws Exception {
        assertAsStringSuccess("AAA=", NODES.binaryNode(new byte[2]));
        assertAsStringSuccess("xyz", NODES.pojoNode("xyz"));
        assertAsStringFailure(NODES.pojoNode(new AtomicInteger(1)));
        assertAsStringFailure(NODES.rawValueNode(new RawValue("abcd")));
        assertAsStringSuccess("AAA=", MAPPER.readTree(BINARY_TWO_ZEROES));
    }

    // Provenance: JsonNodeStringValueTest#asStringFailForStructural().
    void asStringFailForStructuralVpack() throws Exception {
        assertAsStringFailure(NODES.arrayNode(3));
        assertAsStringFailure(NODES.objectNode());
        assertAsStringFailure(MAPPER.readTree(ARRAY_ONE));
        assertAsStringFailure(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeStringValueTest#asStringFromNonNumberMisc().
    void asStringFromNonNumberMiscVpack() throws Exception {
        assertEquals("", NODES.nullNode().asString());
        assertEquals("fallback", NODES.nullNode().asString("fallback"));
        assertFalse(NODES.nullNode().asStringOpt().isPresent());

        assertEquals("", NODES.missingNode().asString());
        assertEquals("fallback", NODES.missingNode().asString("fallback"));
        assertFalse(NODES.missingNode().asStringOpt().isPresent());

        JsonNode wireNull = MAPPER.readTree(NULL);
        assertEquals("", wireNull.asString());
        assertEquals("fallback", wireNull.asString("fallback"));
        assertFalse(wireNull.asStringOpt().isPresent());
    }
private static void assertShortValue(short expected, JsonNode node) {
        assertEquals(expected, node.shortValue());
    }
private static void assertStringValueSuccess(JsonNode node) {
        assertEquals("abc", node.stringValue());
        assertEquals("abc", node.stringValue("xyz"));
        assertEquals("abc", node.stringValueOpt().get());
    }
private static void assertStringValueFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::stringValue);
        assertEquals(true, exception.getMessage().contains("cannot convert value"));
        assertEquals(true, exception.getMessage().contains("value type not String"));
        assertEquals("foo", node.stringValue("foo"));
        assertFalse(node.stringValueOpt().isPresent());
    }
private static void assertAsStringSuccess(String expected, JsonNode node) {
        assertEquals(expected, node.asString());
        assertEquals(expected, node.asString("fallback"));
        assertEquals(expected, node.asStringOpt().get());
    }
private static void assertAsStringFailure(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asString);
        assertEquals(true, exception.getMessage().contains("cannot coerce value"));
        assertEquals(true, exception.getMessage().contains("value type not coercible"));
        assertEquals("foo", node.asString("foo"));
        assertFalse(node.asStringOpt().isPresent());
    }

    void __invoke_stringValueSuccessVpack() throws Exception {
        try {
            stringValueSuccessVpack();
        } finally {
        }
    }


    void __invoke_stringValueFailFromNumbersVpack() throws Exception {
        try {
            stringValueFailFromNumbersVpack();
        } finally {
        }
    }


    void __invoke_stringValueFailFromNonNumberScalarsVpack() throws Exception {
        try {
            stringValueFailFromNonNumberScalarsVpack();
        } finally {
        }
    }


    void __invoke_stringValueFromStructuralVpack() throws Exception {
        try {
            stringValueFromStructuralVpack();
        } finally {
        }
    }


    void __invoke_stringValueFromNonNumberMiscVpack() throws Exception {
        try {
            stringValueFromNonNumberMiscVpack();
        } finally {
        }
    }


    void __invoke_stringValueFromNullNodeVpack() throws Exception {
        try {
            stringValueFromNullNodeVpack();
        } finally {
        }
    }


    void __invoke_asStringSuccessVpack() throws Exception {
        try {
            asStringSuccessVpack();
        } finally {
        }
    }


    void __invoke_asStringFromNumbersVpack() throws Exception {
        try {
            asStringFromNumbersVpack();
        } finally {
        }
    }


    void __invoke_asStringForNonNumberScalarsVpack() throws Exception {
        try {
            asStringForNonNumberScalarsVpack();
        } finally {
        }
    }


    void __invoke_asStringFailForStructuralVpack() throws Exception {
        try {
            asStringFailForStructuralVpack();
        } finally {
        }
    }


    void __invoke_asStringFromNonNumberMiscVpack() throws Exception {
        try {
            asStringFromNonNumberMiscVpack();
        } finally {
        }
    }

}
