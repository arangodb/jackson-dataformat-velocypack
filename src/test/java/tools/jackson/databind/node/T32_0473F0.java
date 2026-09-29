package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.BooleanNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.TreeTraversingParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0473F0 {
private static final byte[] ELEMENT_ARRAY = VPackWireFixtureTest.hex(
            "02 0a 47 65 6c 65 6d 65 6e 74");
private static final byte[] NULL_FALSE_ARRAY = VPackWireFixtureTest.hex(
            "02 04 18 19");
private static final byte[] NUMBER_ARRAY = VPackWireFixtureTest.hex(
            "02 04 28 7b");
private static final byte[] REMOVE_ALL_ARRAY = VPackWireFixtureTest.hex(
            "06 0c 04 41 61 32 18 19 03 05 06 07");
private static final byte[] REMOVE_IF_ARRAY_213 = VPackWireFixtureTest.hex(
            "02 05 32 31 33");
private static final byte[] REMOVE_IF_ARRAY_123 = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] REMOVE_NULLS_ARRAY = VPackWireFixtureTest.hex(
            "06 0b 04 18 18 32 18 03 04 05 06");
private static final byte[] DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] DOUBLE_9999 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 80 87 c3 40");
private static final byte[] DOUBLE_NEGATIVE_28 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 3c c0");
private static final byte[] DOUBLE_275 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 06 40");
private static final byte[] DOUBLE_NEGATIVE_475 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 13 c0");
private final ObjectMapper MAPPER = new VPackMapper();
private final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .build();

    void testNullChecking2Vpack() throws Exception {
        ArrayNode src = readArray(ELEMENT_ARRAY);
        ArrayNode dest = readArray(VPackWireFixtureTest.hex("01"));
        dest.addAll(src);

        assertEquals(1, dest.size());
        assertEquals("element", dest.get(0).stringValue());
    }

    void testNullInsertsVpack() {
        ArrayNode array = MAPPER.getNodeFactory().arrayNode(3);

        array.insert(0, (BigDecimal) null);
        array.insert(0, (BigInteger) null);
        array.insert(0, (Boolean) null);
        array.insert(-56, (byte[]) null);
        array.insert(0, (Double) null);
        array.insert(200, (Float) null);
        array.insert(0, (Integer) null);
        array.insert(1, (JsonNode) null);
        array.insert(array.size(), (Long) null);
        array.insert(1, (String) null);

        assertEquals(10, array.size());
        for (JsonNode node : array) {
            assertTrue(node.isNull());
        }
    }

    void testNullSetVpack() {
        ArrayNode array = MAPPER.getNodeFactory().arrayNode(3);
        for (int i = 0; i < 14; ++i) {
            array.add("Not Null");
        }
        for (JsonNode node : array) {
            assertFalse(node.isNull());
        }

        array.set(0, (BigDecimal) null);
        array.set(1, (BigInteger) null);
        array.set(2, (Boolean) null);
        array.set(3, (byte[]) null);
        array.set(4, (Double) null);
        array.set(5, (Float) null);
        array.set(6, (Integer) null);
        array.set(7, (Short) null);
        array.set(8, (JsonNode) null);
        array.set(9, (Long) null);
        array.set(10, (String) null);
        array.setNull(11);
        array.setRawValue(12, null);
        array.setPOJO(13, null);

        assertEquals(14, array.size());
        for (JsonNode node : array) {
            assertTrue(node.isNull());
        }
    }

    void testParserVpack() throws Exception {
        ArrayNode node = readArray(NUMBER_ARRAY);
        TreeTraversingParser parser = new TreeTraversingParser(node,
                ObjectReadContext.empty());
        assertNotNull(parser.objectReadContext());
        assertNotNull(parser.streamReadContext());
        assertTrue(parser.streamReadContext().inRoot());
        assertNotNull(parser.currentTokenLocation());
        assertNotNull(parser.currentLocation());
        assertNull(parser.getEmbeddedObject());
        // TreeTraversingParser.currentNode() is protected outside its source package;
        // the public token state is the equivalent initial-state assertion.
        assertNull(parser.currentToken());

        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertNotNull(parser.streamReadContext());
        assertTrue(parser.streamReadContext().inArray());
        parser.skipChildren();
        assertEquals(JsonToken.END_ARRAY, parser.currentToken());
        parser.close();

        parser = new TreeTraversingParser(node, ObjectReadContext.empty());
        parser.nextToken();
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(JsonParser.NumberType.INT, parser.getNumberType());
        parser.close();
    }

    void testRemoveAllVpack() throws Exception {
        ArrayNode array = readArray(REMOVE_ALL_ARRAY);
        assertEquals(readArray(VPackWireFixtureTest.hex("01")), array.removeAll());
    }

    void testRemoveIfVpack() throws Exception {
        assertEquals(readArray(VPackWireFixtureTest.hex("02 03 33")),
                readArray(REMOVE_IF_ARRAY_213)
                        .removeIf(value -> value.asInt() <= 2));
        assertEquals(readArray(VPackWireFixtureTest.hex("02 03 31")),
                readArray(REMOVE_IF_ARRAY_123)
                        .removeIf(value -> value.asInt() > 1));
    }

    void testRemoveNullsVpack() throws Exception {
        assertEquals(readArray(VPackWireFixtureTest.hex("02 03 32")),
                readArray(REMOVE_NULLS_ARRAY).removeNulls());
    }

    void testSimpleArrayVpack() throws Exception {
        ArrayNode result = readArray(NULL_FALSE_ARRAY);

        assertTrue(result.isArray());
        assertEquals(ArrayNode.class, result.getClass());
        assertFalse(result.isObject());
        assertFalse(result.isNumber());
        assertFalse(result.isNull());
        assertFalse(result.isString());

        assertEquals(result, result);
        assertFalse(result.equals(null));
        assertEquals(NullNode.instance, result.path(0));
        assertEquals(NullNode.instance, result.get(0));
        assertEquals(NullNode.instance, result.optional(0).get());
        assertEquals(BooleanNode.FALSE, result.path(1));
        assertEquals(BooleanNode.FALSE, result.get(1));
        assertEquals(BooleanNode.FALSE, result.optional(1).get());
        assertEquals(2, result.size());

        assertNull(result.get(-1));
        assertNull(result.get(2));
        assertFalse(result.optional(-1).isPresent());
        assertFalse(result.optional(2).isPresent());
        assertTrue(result.path(2).isMissingNode());
        assertTrue(result.path(-100).isMissingNode());

        ArrayNode array2 = readArray(NULL_FALSE_ARRAY);
        assertEquals(result, array2);
        JsonNode removed = array2.remove(0);
        assertEquals(NullNode.instance, removed);
        assertEquals(1, array2.size());
        assertEquals(BooleanNode.FALSE, array2.get(0));
        assertFalse(result.equals(array2));
        removed = array2.remove(0);
        assertEquals(BooleanNode.FALSE, removed);
        assertEquals(0, array2.size());
    }

    void testSimpleMismatchVpack() throws Exception {
        try {
            MAPPER.readValue(VPackWireFixtureTest.hex("28 7b"), ArrayNode.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            assertTrue(e.getMessage() != null && !e.getMessage().isEmpty());
        }
    }

    void testStreamMethodsVpack() {
        ArrayNode array = MAPPER.createArrayNode();
        array.add(1).add("foo");
        JsonNode first = array.get(0);
        JsonNode second = array.get(1);

        assertEquals(2, array.valueStream().count());
        assertEquals(Arrays.asList(first, second),
                array.valueStream().collect(Collectors.toList()));
        assertEquals(0, array.propertyStream().count());
        assertEquals(List.of(), array.propertyStream().collect(Collectors.toList()));
        array.forEachEntry((name, value) -> {
            throw new UnsupportedOperationException();
        });
    }
private ArrayNode readArray(byte[] bytes) throws Exception {
        return (ArrayNode) MAPPER.readTree(bytes);
    }
private JsonNode readDouble(byte[] bytes) throws Exception {
        return MAPPER.readTree(bytes);
    }

    void __invoke_testNullChecking2Vpack() throws Exception {
        try {
            testNullChecking2Vpack();
        } finally {
        }
    }


    void __invoke_testNullInsertsVpack() throws Exception {
        try {
            testNullInsertsVpack();
        } finally {
        }
    }


    void __invoke_testNullSetVpack() throws Exception {
        try {
            testNullSetVpack();
        } finally {
        }
    }


    void __invoke_testParserVpack() throws Exception {
        try {
            testParserVpack();
        } finally {
        }
    }


    void __invoke_testRemoveAllVpack() throws Exception {
        try {
            testRemoveAllVpack();
        } finally {
        }
    }


    void __invoke_testRemoveIfVpack() throws Exception {
        try {
            testRemoveIfVpack();
        } finally {
        }
    }


    void __invoke_testRemoveNullsVpack() throws Exception {
        try {
            testRemoveNullsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleArrayVpack() throws Exception {
        try {
            testSimpleArrayVpack();
        } finally {
        }
    }


    void __invoke_testSimpleMismatchVpack() throws Exception {
        try {
            testSimpleMismatchVpack();
        } finally {
        }
    }


    void __invoke_testStreamMethodsVpack() throws Exception {
        try {
            testStreamMethodsVpack();
        } finally {
        }
    }

}
