package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.ContainerNode;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.NumericNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.exc.JsonNodeException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0475F0 {
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] DOUBLE_100_75 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 30 59 40");
private static final byte[] DOUBLE_NEGATIVE_1_25 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f4 bf");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeBasicTest#testBinary().
    void testBinaryVpack() throws Exception {
        assertNull(BinaryNode.valueOf(null));
        assertNull(BinaryNode.valueOf(null, 0, 0));

        BinaryNode empty = BinaryNode.valueOf(new byte[1], 0, 0);
        assertSame(empty, BinaryNode.valueOf(new byte[0]));
        assertEquals(empty, BinaryNode.valueOf(new byte[0]));

        byte[] data = new byte[3];
        data[1] = (byte) 3;
        BinaryNode n = BinaryNode.valueOf(data, 1, 1);
        assertFalse(n.isNumber());
        assertFalse(n.canConvertToInt());
        assertFalse(n.canConvertToLong());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals(-42, n.asInt(-42));
        assertEquals(12345678901L, n.asLong(12345678901L));
        assertEquals(-19.25, n.asDouble(-19.25));

        data[2] = (byte) 3;
        BinaryNode n2 = BinaryNode.valueOf(data, 2, 1);
        assertEquals(n, n2);
        assertEquals("\"Aw==\"", n.toString());
        assertEquals("AAMD", new BinaryNode(data).asString());
        assertEmptyStreams(n2);

        BinaryNode parsed = (BinaryNode) MAPPER.readTree(BINARY);
        assertArrayEquals(new byte[] { 0, 3, 3 }, parsed.binaryValue());
        assertEquals("AAMD", parsed.asString());
    }

    // Provenance: JsonNodeBasicTest#testBoolean().
    void testBooleanVpack() throws Exception {
        BooleanNode f = BooleanNode.getFalse();
        assertNotNull(f);
        assertTrue(f.isBoolean());
        assertSame(f, BooleanNode.valueOf(false));
        assertFalse(f.booleanValue());
        assertFalse(f.asBoolean());
        assertEquals("false", f.asString());
        assertEquals(JsonToken.VALUE_FALSE, f.asToken());
        assertFalse(f.isNumber());
        assertFalse(f.canConvertToInt());
        assertFalse(f.canConvertToLong());
        assertFalse(f.canConvertToExactIntegral());

        BooleanNode t = BooleanNode.getTrue();
        assertNotNull(t);
        assertTrue(t.isBoolean());
        assertSame(t, BooleanNode.valueOf(true));
        assertTrue(t.booleanValue());
        assertTrue(t.asBoolean());
        assertEquals("true", t.asString());
        assertEquals(JsonToken.VALUE_TRUE, t.asToken());

        JsonNode result = MAPPER.readTree(TRUE);
        assertFalse(result.isNull());
        assertFalse(result.isNumber());
        assertFalse(result.isString());
        assertTrue(result.isBoolean());
        assertEquals(BooleanNode.class, result.getClass());
        assertTrue(result.booleanValue());
        assertEquals("true", result.asString());
        assertFalse(result.isMissingNode());
        assertEquals(result, BooleanNode.valueOf(true));
        assertEquals(result, BooleanNode.getTrue());
        assertEmptyStreams(f);
    }

    // Provenance: JsonNodeBasicTest#testCustomComparators().
    void testCustomComparatorsVpack() {
        ObjectNode nestedObject1 = NODES.objectNode();
        nestedObject1.put("value", 6);
        ArrayNode nestedArray1 = NODES.arrayNode();
        nestedArray1.add(7);
        ObjectNode root1 = NODES.objectNode();
        root1.put("value", 5);
        root1.set("nested_object", nestedObject1);
        root1.set("nested_array", nestedArray1);

        ObjectNode nestedObject2 = NODES.objectNode();
        nestedObject2.put("value", 6.9);
        ArrayNode nestedArray2 = NODES.arrayNode();
        nestedArray2.add(7.0);
        ObjectNode root2 = NODES.objectNode();
        root2.put("value", 5.0);
        root2.set("nested_object", nestedObject2);
        root2.set("nested_array", nestedArray2);

        assertFalse(root1.equals(root2));
        assertFalse(root2.equals(root1));
        assertTrue(root1.equals(root1));
        assertTrue(root2.equals(root2));
        assertTrue(nestedArray1.equals(nestedArray1));
        assertFalse(nestedArray1.equals(nestedArray2));
        assertFalse(nestedArray2.equals(nestedArray1));

        Comparator<JsonNode> cmp = (left, right) -> {
            if (left instanceof ContainerNode || right instanceof ContainerNode) {
                throw new AssertionError("container nodes should be traversed");
            }
            if (left.equals(right)) {
                return 0;
            }
            if (left instanceof NumericNode leftNumber
                    && right instanceof NumericNode rightNumber) {
                int d1 = leftNumber.numberValue().intValue();
                int d2 = rightNumber.numberValue().intValue();
                return Integer.compare(d1, d2);
            }
            return 0;
        };
        assertTrue(root1.equals(cmp, root2));
        assertTrue(root2.equals(cmp, root1));
        assertTrue(root1.equals(cmp, root1));
        assertTrue(root2.equals(cmp, root2));

        ArrayNode array3 = NODES.arrayNode();
        array3.add(123);
        assertFalse(root2.equals(cmp, nestedArray1));
        assertTrue(nestedArray1.equals(cmp, nestedArray1));
        assertFalse(nestedArray1.equals(cmp, root2));
        assertFalse(nestedArray1.equals(cmp, array3));
    }

    // Provenance: JsonNodeBasicTest#testOptionalAccessorOnArray().
    void testOptionalAccessorOnArrayVpack() {
        ArrayNode arrayNode = NODES.arrayNode();
        arrayNode.add("firstElement");
        assertTrue(arrayNode.optional(0).isPresent());
        assertEquals("firstElement", arrayNode.optional(0).get().asString());
        assertFalse(arrayNode.optional(1).isPresent());
        assertFalse(arrayNode.optional(-1).isPresent());
        assertFalse(arrayNode.optional(999).isPresent());
        assertFalse(arrayNode.optional("anyField").isPresent());
    }

    // Provenance: JsonNodeBasicTest#testOptionalAccessorOnNumbers().
    void testOptionalAccessorOnNumbersVpack() {
        IntNode intNode = IntNode.valueOf(42);
        assertFalse(intNode.optional("anyField").isPresent());
        assertFalse(intNode.optional(0).isPresent());

        LongNode longNode = LongNode.valueOf(123456789L);
        assertFalse(longNode.optional("anyField").isPresent());
        assertFalse(longNode.optional(0).isPresent());

        DoubleNode doubleNode = DoubleNode.valueOf(3.14);
        assertFalse(doubleNode.optional("anyField").isPresent());
        assertFalse(doubleNode.optional(0).isPresent());

        DecimalNode decimalNode = DecimalNode.valueOf(new BigDecimal("12345.6789"));
        assertFalse(decimalNode.optional("anyField").isPresent());
        assertFalse(decimalNode.optional(0).isPresent());
    }

    // Provenance: JsonNodeBasicTest#testOptionalAccessorOnObject().
    void testOptionalAccessorOnObjectVpack() {
        ObjectNode objectNode = NODES.objectNode();
        objectNode.put("existingField", "value");
        assertTrue(objectNode.optional("existingField").isPresent());
        assertEquals("value", objectNode.optional("existingField").get().asString());
        assertFalse(objectNode.optional("missingField").isPresent());
        assertFalse(objectNode.optional(0).isPresent());
        assertFalse(objectNode.optional(-1).isPresent());
    }

    // Provenance: JsonNodeBasicTest#testOptionalAccessorOnOtherTypes().
    void testOptionalAccessorOnOtherTypesVpack() {
        StringNode stringNode = StringNode.valueOf("sampleText");
        assertFalse(stringNode.optional("anyField").isPresent());
        assertFalse(stringNode.optional(0).isPresent());

        JsonNode nullNode = NODES.nullNode();
        assertFalse(nullNode.optional("anyField").isPresent());
        assertFalse(nullNode.optional(0).isPresent());

        BooleanNode booleanNode = BooleanNode.TRUE;
        assertFalse(booleanNode.optional("anyField").isPresent());
        assertFalse(booleanNode.optional(0).isPresent());
    }

    // Provenance: JsonNodeBasicTest#testPOJO().
    void testPOJOVpack() {
        POJONode n = new POJONode("x");
        assertEquals(n, new POJONode("x"));
        assertEquals("x", n.asString());
        assertEquals("\"x\"", n.toString());
        assertEquals(new POJONode(null), new POJONode(null));
        assertFalse(n.isNumber());
        assertFalse(n.canConvertToInt());
        assertFalse(n.canConvertToLong());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals(-42, n.asInt(-42));
        assertEquals(12345678901L, n.asLong(12345678901L));
        assertEquals(-19.25, n.asDouble(-19.25));

        JsonNode numeric = new POJONode(Integer.valueOf(123));
        assertEquals(123, numeric.asInt());
        assertEquals(123, numeric.asInt(-42));
        assertEquals(123L, numeric.asLong());
        assertEquals(123L, numeric.asLong(19L));
        assertEquals(123.0, numeric.asDouble());
        assertEquals(123.0, numeric.asDouble(-19.25));
        assertTrue(numeric.isEmpty());
        assertEmptyStreams(n);
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

    void __invoke_testBinaryVpack() throws Exception {
        try {
            testBinaryVpack();
        } finally {
        }
    }


    void __invoke_testBooleanVpack() throws Exception {
        try {
            testBooleanVpack();
        } finally {
        }
    }


    void __invoke_testCustomComparatorsVpack() throws Exception {
        try {
            testCustomComparatorsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalAccessorOnArrayVpack() throws Exception {
        try {
            testOptionalAccessorOnArrayVpack();
        } finally {
        }
    }


    void __invoke_testOptionalAccessorOnNumbersVpack() throws Exception {
        try {
            testOptionalAccessorOnNumbersVpack();
        } finally {
        }
    }


    void __invoke_testOptionalAccessorOnObjectVpack() throws Exception {
        try {
            testOptionalAccessorOnObjectVpack();
        } finally {
        }
    }


    void __invoke_testOptionalAccessorOnOtherTypesVpack() throws Exception {
        try {
            testOptionalAccessorOnOtherTypesVpack();
        } finally {
        }
    }


    void __invoke_testPOJOVpack() throws Exception {
        try {
            testPOJOVpack();
        } finally {
        }
    }

}
