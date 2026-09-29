package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0479F1 {
private static final BigDecimal BD_DEFAULT = new BigDecimal("12.125");
private static final byte[] ROOT = VPackWireFixtureTest.hex(
            "14 13 44 6c 65 61 66 14 0b 45 76 61 6c 75 65 28 0d 01 01");
private static final byte[] ROOT_ARRAY = VPackWireFixtureTest.hex(
            "13 29"
            + "14 13 44 6c 65 61 66 14 0b 45 76 61 6c 75 65 28 0d 01 01"
            + "14 13 44 6c 65 61 66 14 0b 45 76 61 6c 75 65 28 0c 01 01"
            + "02");
private static final byte[] WRAPPED_EVENT = VPackWireFixtureTest.hex(
            "14 19 45 65 76 65 6e 74 14 10 42 69 64 31"
            + "44 6e 61 6d 65 43 66 6f 6f 02 01");
private static final byte[] SIGNED_128 = VPackWireFixtureTest.hex("20 80");
private static final byte[] MAX_UNSIGNED = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff ff");
private static final byte[] DOUBLE_100000 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 6a f8 40");
private static final byte[] DOUBLE_NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] DOUBLE_POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] DOUBLE_NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] BINARY = VPackWireFixtureTest.hex("c0 03 00 03 03");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] TEXT_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] TEXT_TWO = VPackWireFixtureTest.hex("41 32");
private final VPackMapper MAPPER = new VPackMapper();
private final tools.jackson.databind.node.JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeDecimalValueTest#asDecimalFailFromStructural().
    void asDecimalFailFromStructuralVpack() throws Exception {
        assertFailAsDecimalForNonNumber(NODES.arrayNode(3));
        assertFailAsDecimalForNonNumber(NODES.objectNode());
        assertFailAsDecimalForNonNumber(MAPPER.readTree(EMPTY_ARRAY));
        assertFailAsDecimalForNonNumber(MAPPER.readTree(EMPTY_OBJECT));
    }

    // Provenance: JsonNodeDecimalValueTest#asDecimalFromMiscOther().
    void asDecimalFromMiscOtherVpack() throws Exception {
        assertEquals(BigDecimal.ZERO, NODES.nullNode().asDecimal());
        assertEquals(BD_DEFAULT, NODES.nullNode().asDecimal(BD_DEFAULT));
        assertFalse(NODES.nullNode().asDecimalOpt().isPresent());

        assertEquals(BigDecimal.ZERO, NODES.missingNode().asDecimal());
        assertEquals(BD_DEFAULT, NODES.missingNode().asDecimal(BD_DEFAULT));
        assertFalse(NODES.missingNode().asDecimalOpt().isPresent());

        JsonNode wireNull = MAPPER.readTree(NULL);
        assertEquals(BigDecimal.ZERO, wireNull.asDecimal());
        assertEquals(BD_DEFAULT, wireNull.asDecimal(BD_DEFAULT));
        assertFalse(wireNull.asDecimalOpt().isPresent());
    }

    // Provenance: JsonNodeDecimalValueTest#asDecimalFromNonNumberScalar().
    void asDecimalFromNonNumberScalarVpack() throws Exception {
        assertFailAsDecimalForNonNumber(NODES.booleanNode(true));
        assertFailAsDecimalForNonNumber(NODES.binaryNode(new byte[3]));
        assertFailAsDecimalForNonNumber(MAPPER.readTree(BINARY));
        assertFailAsDecimalForNonNumber(NODES.rawValueNode(new RawValue("abc")));
        assertFailAsDecimalForNonNumber(NODES.pojoNode(Boolean.TRUE));

        assertFailAsDecimal(NODES.stringNode("abc"), "value not a valid String representation");
        assertFailAsDecimal(MAPPER.readTree(TEXT_ABC), "value not a valid String representation");

        assertAsDecimal(BigDecimal.valueOf(2), NODES.stringNode("2"));
        assertAsDecimal(BigDecimal.valueOf(2), MAPPER.readTree(TEXT_TWO));
        assertAsDecimal(BigDecimal.TEN, NODES.pojoNode(10));
    }

    // Provenance: JsonNodeDecimalValueTest#asDecimalFromNumberFPFail().
    void asDecimalFromNumberFPFailVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.numberNode(Float.NaN),
                NODES.numberNode(Float.POSITIVE_INFINITY),
                NODES.numberNode(Float.NEGATIVE_INFINITY),
                NODES.numberNode(Double.NaN),
                NODES.numberNode(Double.POSITIVE_INFINITY),
                NODES.numberNode(Double.NEGATIVE_INFINITY),
                MAPPER.readTree(DOUBLE_NAN),
                MAPPER.readTree(DOUBLE_POSITIVE_INFINITY),
                MAPPER.readTree(DOUBLE_NEGATIVE_INFINITY) }) {
            assertFailAsDecimalForNaN(node);
        }
    }

    // Provenance: JsonNodeDecimalValueTest#asDecimalFromNumberFPOk().
    void asDecimalFromNumberFPOkVpack() throws Exception {
        assertAsDecimal(new BigDecimal("1.0"), NODES.numberNode(1.0f));
        assertAsDecimal(new BigDecimal("100000.0"), NODES.numberNode(100_000.0f));
        assertAsDecimal(new BigDecimal("-100000.0"), NODES.numberNode(-100_000.0f));

        assertAsDecimal(new BigDecimal("1.0"), NODES.numberNode(1.0d));
        assertAsDecimal(new BigDecimal("100000.0"), NODES.numberNode(100_000.0d));
        assertAsDecimal(new BigDecimal("-100000.0"), NODES.numberNode(-100_000.0d));
        assertAsDecimal(new BigDecimal("100000.0"), MAPPER.readTree(DOUBLE_100000));

        assertAsDecimal(new BigDecimal("100.001"),
                NODES.numberNode(new BigDecimal("100.001")));
    }

    // Provenance: JsonNodeDecimalValueTest#asDecimalFromNumberIntOk().
    void asDecimalFromNumberIntOkVpack() throws Exception {
        assertAsDecimal(BigDecimal.ONE, NODES.numberNode((byte) 1));
        assertAsDecimal(BigDecimal.valueOf(Byte.MIN_VALUE), NODES.numberNode(Byte.MIN_VALUE));
        assertAsDecimal(BigDecimal.valueOf(Byte.MAX_VALUE), NODES.numberNode(Byte.MAX_VALUE));

        assertAsDecimal(BigDecimal.ONE, NODES.numberNode((short) 1));
        assertAsDecimal(BigDecimal.valueOf(Short.MIN_VALUE), NODES.numberNode(Short.MIN_VALUE));
        assertAsDecimal(BigDecimal.valueOf(Short.MAX_VALUE), NODES.numberNode(Short.MAX_VALUE));

        assertAsDecimal(BigDecimal.ONE, NODES.numberNode(1));
        assertAsDecimal(BigDecimal.valueOf(Integer.MIN_VALUE), NODES.numberNode(Integer.MIN_VALUE));
        assertAsDecimal(BigDecimal.valueOf(Integer.MAX_VALUE), NODES.numberNode(Integer.MAX_VALUE));

        assertAsDecimal(BigDecimal.ONE, NODES.numberNode(1L));
        assertAsDecimal(BigDecimal.valueOf(Long.MIN_VALUE), NODES.numberNode(Long.MIN_VALUE));
        assertAsDecimal(BigDecimal.valueOf(Long.MAX_VALUE), NODES.numberNode(Long.MAX_VALUE));

        assertAsDecimal(BigDecimal.ONE, NODES.numberNode(BigInteger.ONE));
        assertAsDecimal(BigDecimal.valueOf(Long.MIN_VALUE),
                NODES.numberNode(BigInteger.valueOf(Long.MIN_VALUE)));
        assertAsDecimal(BigDecimal.valueOf(Long.MAX_VALUE),
                NODES.numberNode(BigInteger.valueOf(Long.MAX_VALUE)));

        assertAsDecimal(BigDecimal.valueOf(-128), MAPPER.readTree(SIGNED_128));
        assertAsDecimal(new BigDecimal(MAX_UNSIGNED_VALUE), MAPPER.readTree(MAX_UNSIGNED));
    }

    // Provenance: JsonNodeDecimalValueTest#decimalValueFromNumberFPFail().
    void decimalValueFromNumberFPFailVpack() throws Exception {
        for (JsonNode node : new JsonNode[] {
                NODES.numberNode(Float.NaN),
                NODES.numberNode(Float.POSITIVE_INFINITY),
                NODES.numberNode(Float.NEGATIVE_INFINITY),
                NODES.numberNode(Double.NaN),
                NODES.numberNode(Double.POSITIVE_INFINITY),
                NODES.numberNode(Double.NEGATIVE_INFINITY),
                MAPPER.readTree(DOUBLE_NAN),
                MAPPER.readTree(DOUBLE_POSITIVE_INFINITY),
                MAPPER.readTree(DOUBLE_NEGATIVE_INFINITY) }) {
            assertFailDecimalValueForNaN(node);
        }
    }
private static final BigInteger MAX_UNSIGNED_VALUE = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);
private static void assertAsDecimal(BigDecimal expected, JsonNode node) {
        assertEquals(expected, node.asDecimal());
        assertEquals(expected, node.asDecimal(BD_DEFAULT));
        assertEquals(expected, node.asDecimalOpt().get());
    }
private static void assertFailAsDecimalForNonNumber(JsonNode node) {
        assertFailAsDecimal(node, "value type not coercible");
    }
private static void assertFailAsDecimal(JsonNode node, String expectedMessage) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asDecimal);
        assertTrue(exception.getMessage().contains("asDecimal()"));
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains(expectedMessage));
        assertEquals(BD_DEFAULT, node.asDecimal(BD_DEFAULT));
        assertFalse(node.asDecimalOpt().isPresent());
    }
private static void assertFailAsDecimalForNaN(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asDecimal);
        assertTrue(exception.getMessage().contains("asDecimal()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value non-Finite ('NaN')"));
        assertEquals(BD_DEFAULT, node.asDecimal(BD_DEFAULT));
        assertFalse(node.asDecimalOpt().isPresent());
    }
private static void assertFailDecimalValueForNaN(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::decimalValue);
        assertTrue(exception.getMessage().contains("decimalValue()"));
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value non-Finite ('NaN')"));
        assertEquals(BD_DEFAULT, node.decimalValue(BD_DEFAULT));
        assertFalse(node.decimalValueOpt().isPresent());
    }
private static void assertWrappedEvent(JsonNode expected, JsonNode actual) {
        assertTrue(actual.isObject());
        assertEquals(expected.get("event").get("id").intValue(),
                actual.get("event").get("id").intValue());
        assertEquals(expected.get("event").get("name"), actual.get("event").get("name"));
    }
static class Root {
        public Leaf leaf;
    }
static class Leaf {
        public int value;

        public Leaf() { }
    }
@JsonDeserialize(using = LeafDeserializer.class)
    public static class LeafMixIn { }
static class LeafDeserializer extends ValueDeserializer<Leaf> {
        @Override
        public Leaf deserialize(JsonParser parser, DeserializationContext context) {
            JsonNode tree = context.readTree(parser);
            Leaf leaf = new Leaf();
            leaf.value = tree.get("value").intValue();
            return leaf;
        }
    }
@JsonRootName("event")
    static class Event {
        public Long id;
        public String name;
    }

    void __invoke_asDecimalFailFromStructuralVpack() throws Exception {
        try {
            asDecimalFailFromStructuralVpack();
        } finally {
        }
    }


    void __invoke_asDecimalFromMiscOtherVpack() throws Exception {
        try {
            asDecimalFromMiscOtherVpack();
        } finally {
        }
    }


    void __invoke_asDecimalFromNonNumberScalarVpack() throws Exception {
        try {
            asDecimalFromNonNumberScalarVpack();
        } finally {
        }
    }


    void __invoke_asDecimalFromNumberFPFailVpack() throws Exception {
        try {
            asDecimalFromNumberFPFailVpack();
        } finally {
        }
    }


    void __invoke_asDecimalFromNumberFPOkVpack() throws Exception {
        try {
            asDecimalFromNumberFPOkVpack();
        } finally {
        }
    }


    void __invoke_asDecimalFromNumberIntOkVpack() throws Exception {
        try {
            asDecimalFromNumberIntOkVpack();
        } finally {
        }
    }


    void __invoke_decimalValueFromNumberFPFailVpack() throws Exception {
        try {
            decimalValueFromNumberFPFailVpack();
        } finally {
        }
    }

}
