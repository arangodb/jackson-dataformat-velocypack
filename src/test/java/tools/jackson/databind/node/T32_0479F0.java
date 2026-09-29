package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0479F0 {
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

    // Provenance: JsonNodeConversionsTest#testTreeToValue().
    void testTreeToValueVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .addMixIn(Leaf.class, LeafMixIn.class)
                .build();
        JsonNode root = mapper.readTree(ROOT);

        Root result = mapper.treeToValue(root, Root.class);
        assertNotNull(result);
        assertEquals(13, result.leaf.value);

        result = mapper.treeToValue(root, mapper.constructType(Root.class));
        assertEquals(13, result.leaf.value);

        result = mapper.treeToValue(root, new TypeReference<Root>() { });
        assertEquals(13, result.leaf.value);

        root = mapper.readTree(ROOT_ARRAY);
        List<Root> array = mapper.treeToValue(root, new TypeReference<List<Root>>() { });
        assertEquals(2, array.size());
        assertEquals(13, array.get(0).leaf.value);
        assertEquals(12, array.get(1).leaf.value);
    }

    // Provenance: JsonNodeConversionsTest#testTreeToValueNullInput().
    void testTreeToValueNullInputVpack() throws Exception {
        assertNull(MAPPER.treeToValue(null, String.class));
        assertNull(MAPPER.treeToValue(null, MAPPER.constructType(String.class)));

        assertNull(MAPPER.reader().treeToValue(null, String.class));
        assertNull(MAPPER.reader().treeToValue(null, MAPPER.constructType(String.class)));
    }

    // Provenance: JsonNodeConversionsTest#testTreeToValueWithPOJO().
    void testTreeToValueWithPOJOVpack() throws Exception {
        Calendar input = Calendar.getInstance();
        input.setTime(new Date(0));
        JsonNode pojoNode = NODES.pojoNode(input);

        Calendar result = MAPPER.treeToValue(pojoNode, Calendar.class);
        assertEquals(input.getTimeInMillis(), result.getTimeInMillis());

        result = MAPPER.treeToValue(pojoNode, MAPPER.constructType(Calendar.class));
        assertEquals(input.getTimeInMillis(), result.getTimeInMillis());
    }

    // Provenance: JsonNodeConversionsTest#testValueToTree().
    void testValueToTreeVpack() throws Exception {
        Event value = new Event();
        value.id = 1L;
        value.name = "foo";

        VPackMapper wrapRootMapper = VPackMapper.builder()
                .enable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();
        JsonNode expected = MAPPER.readTree(WRAPPED_EVENT);

        assertWrappedEvent(expected,
                wrapRootMapper.readTree(wrapRootMapper.writeValueAsBytes(value)));
        assertWrappedEvent(expected, wrapRootMapper.valueToTree(value));
    }

    // Provenance: JsonNodeConversionsTest#treeToValueWithMissingNode4932().
    void treeToValueWithMissingNode4932Vpack() throws Exception {
        assertNull(MAPPER.treeToValue(MAPPER.readTree(NULL), Object.class));
        assertNull(MAPPER.treeToValue(NODES.missingNode(), Object.class));

        ObjectReader reader = MAPPER.readerFor(Object.class);
        assertNull(reader.treeToValue(MAPPER.readTree(NULL), Object.class));
        assertNull(reader.treeToValue(NODES.missingNode(), Object.class));
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

    void __invoke_testTreeToValueVpack() throws Exception {
        try {
            testTreeToValueVpack();
        } finally {
        }
    }


    void __invoke_testTreeToValueNullInputVpack() throws Exception {
        try {
            testTreeToValueNullInputVpack();
        } finally {
        }
    }


    void __invoke_testTreeToValueWithPOJOVpack() throws Exception {
        try {
            testTreeToValueWithPOJOVpack();
        } finally {
        }
    }


    void __invoke_testValueToTreeVpack() throws Exception {
        try {
            testValueToTreeVpack();
        } finally {
        }
    }


    void __invoke_treeToValueWithMissingNode4932Vpack() throws Exception {
        try {
            treeToValueWithMissingNode4932Vpack();
        } finally {
        }
    }

}
