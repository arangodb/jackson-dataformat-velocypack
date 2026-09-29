package tools.jackson.databind.node;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0505F0 {
private static final byte[] SIMPLE_ROOT = VPackWireFixtureTest.hex(
            "14 1c 41 61 28 7b 44 6c 69 73 74 13 10 "
          + "1b 00 00 00 00 00 80 28 40 18 1a 0a 01 05 02");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] NESTED_EMPTY_ARRAY = VPackWireFixtureTest.hex(
            "13 04 01 01");
private static final byte[] NESTED_VALUE_ARRAY = VPackWireFixtureTest.hex(
            "13 0f 13 0c 1b 33 33 33 33 33 33 28 40 01 01");
private static final byte[] COORDINATES_ROOT = VPackWireFixtureTest.hex(
            "14 2f 4b 63 6f 6f 72 64 69 6e 61 74 65 73 "
          + "13 20 13 1d 13 05 3d 31 02 "
          + "13 15 1b c3 7f ba 81 82 7b 66 40 "
          + "1b 47 e5 26 6a 69 96 49 40 02 02 01 01");
private static final byte[] PERSON_ROOT = VPackWireFixtureTest.hex(
            "14 31 44 6e 61 6d 65 44 54 61 74 75 "
          + "4b 6d 61 67 69 63 4e 75 6d 62 65 72 28 2a "
          + "44 6b 69 64 73 13 11 43 4c 65 6f 44 4c 69 6c 61 "
          + "44 4c 65 69 61 03 03");
private static final byte[] POJO_STRING_ROOT = VPackWireFixtureTest.hex(
            "14 0c 44 70 6f 6a 6f 43 61 62 63 01");
private static final byte[] POJO_INT_ARRAY_ROOT = VPackWireFixtureTest.hex(
            "14 0e 44 70 6f 6a 6f 13 06 31 32 33 03 01");
private static final byte[] POJO_BEAN_ROOT = VPackWireFixtureTest.hex(
            "14 13 44 70 6f 6a 6f 14 0b 41 78 41 79 41 79 28 0d 02 01");
private static final byte[] BINARY_ROOT = VPackWireFixtureTest.hex(
            "c0 02 00 fb");
private static final byte[] INT_OVERFLOW_ARRAY = VPackWireFixtureTest.hex(
            "02 07 2b 00 00 00 80");
private static final byte[] INT_OVERFLOW_OBJECT = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 2b 00 00 00 80 01");
private static final byte[] LONG_OVERFLOW_ARRAY = VPackWireFixtureTest.hex(
            "02 0b 2f 00 00 00 00 00 00 00 80");
private static final byte[] LONG_OVERFLOW_OBJECT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 2f 00 00 00 00 00 00 00 80 01");
private static final byte[] DOUBLE_INT_OVERFLOW_ARRAY = VPackWireFixtureTest.hex(
            "02 0b 1b 00 00 00 20 5f a0 02 42");
private static final byte[] DOUBLE_LONG_OVERFLOW_ARRAY = VPackWireFixtureTest.hex(
            "02 0b 1b ea 8c a0 39 59 3e 29 46");
private static final byte[] LONG_ABOVE_INT_OBJECT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 2f 00 00 00 80 00 00 00 00 01");
private static final byte[] LONG_BELOW_INT_OBJECT = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 24 ff ff ff 7f ff 01");
private static final byte[] LONG_MAX_OBJECT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 27 ff ff ff ff ff ff ff 7f 01");
private static final byte[] LONG_MIN_OBJECT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 27 00 00 00 00 00 00 00 80 01");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: TreeSerializationViaMapperTest#testSimpleViaObjectMapper().
    void testSimpleViaObjectMapperVpack() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put("number", 15);
        node.put("string", "abc");
        node.putObject("ob").putArray("arr");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = mapper.readValue(mapper.writeValueAsBytes(node), Map.class);
        assertEquals(3, result.size());
        assertEquals("abc", result.get("string"));
        assertEquals(Integer.valueOf(15), result.get("number"));
        @SuppressWarnings("unchecked")
        Map<String, Object> object = (Map<String, Object>) result.get("ob");
        assertEquals(1, object.size());
        @SuppressWarnings("unchecked")
        List<Object> array = (List<Object>) object.get("arr");
        assertNotNull(array);
        assertTrue(array.isEmpty());

        JsonNode independent = mapper.readTree(SIMPLE_ROOT);
        assertEquals(123, independent.path("a").intValue());
        assertEquals(12.25, independent.path("list").get(0).doubleValue(), 0.0);
    }

    // Provenance: TreeSerializationViaMapperTest#testPOJOString().
    void testPOJOStringVpack() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.set("pojo", mapper.getNodeFactory().pojoNode("abc"));
        @SuppressWarnings("unchecked")
        Map<String, Object> serialized = mapper.readValue(mapper.writeValueAsBytes(node), Map.class);
        assertEquals(Map.of("pojo", "abc"), serialized);

        @SuppressWarnings("unchecked")
        Map<String, Object> literal = mapper.readValue(POJO_STRING_ROOT, Map.class);
        assertEquals(serialized, literal);
    }

    // Provenance: TreeSerializationViaMapperTest#testPOJOIntArray().
    void testPOJOIntArrayVpack() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.set("pojo", mapper.getNodeFactory().pojoNode(new int[] { 1, 2, 3 }));
        @SuppressWarnings("unchecked")
        Map<String, Object> serialized = mapper.readValue(mapper.writeValueAsBytes(node), Map.class);
        assertEquals(List.of(1, 2, 3), serialized.get("pojo"));

        @SuppressWarnings("unchecked")
        Map<String, Object> literal = mapper.readValue(POJO_INT_ARRAY_ROOT, Map.class);
        assertEquals(List.of(1, 2, 3), literal.get("pojo"));
    }

    // Provenance: TreeSerializationViaMapperTest#testPOJOBean().
    void testPOJOBeanVpack() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.set("pojo", mapper.getNodeFactory().pojoNode(new Bean()));
        @SuppressWarnings("unchecked")
        Map<String, Object> serialized = mapper.readValue(mapper.writeValueAsBytes(node), Map.class);
        assertEquals(Map.of("x", "y", "y", 13), serialized.get("pojo"));

        @SuppressWarnings("unchecked")
        Map<String, Object> literal = mapper.readValue(POJO_BEAN_ROOT, Map.class);
        assertEquals(serialized, literal);
    }
private void assertIntOverflow(byte[] input) throws Exception {
        JsonNode tree = mapper.readTree(input);
        try (JsonParser parser = tree.traverse(ObjectReadContext.empty())) {
            if (tree.isArray()) {
                assertToken(JsonToken.START_ARRAY, parser.nextToken());
            } else {
                assertToken(JsonToken.START_OBJECT, parser.nextToken());
                assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("value", parser.currentName());
            }
            if (input == DOUBLE_INT_OVERFLOW_ARRAY) {
                assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
            } else {
                assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(JsonParser.NumberType.LONG, parser.getNumberType());
            }
            InputCoercionException failure = assertThrows(InputCoercionException.class,
                    parser::getIntValue);
            assertTrue(failure.getMessage().contains("int"));
        }
    }
private void assertLongOverflow(byte[] input, BigInteger expected) throws Exception {
        JsonNode tree = mapper.readTree(input);
        try (JsonParser parser = tree.traverse(ObjectReadContext.empty())) {
            if (tree.isArray()) {
                assertToken(JsonToken.START_ARRAY, parser.nextToken());
            } else {
                assertToken(JsonToken.START_OBJECT, parser.nextToken());
                assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("value", parser.currentName());
            }
            assertToken(expected == null ? JsonToken.VALUE_NUMBER_FLOAT : JsonToken.VALUE_NUMBER_INT,
                    parser.nextToken());
            if (expected != null) {
                assertEquals(JsonParser.NumberType.BIG_INTEGER, parser.getNumberType());
                assertEquals(expected, parser.getBigIntegerValue());
            } else {
                assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
            }
            InputCoercionException failure = assertThrows(InputCoercionException.class,
                    parser::getLongValue);
            assertTrue(failure.getMessage().contains("long"));
        }
    }
private void assertLongInRange(byte[] input, long expected) throws Exception {
        JsonNode tree = mapper.readTree(input);
        try (JsonParser parser = tree.traverse(ObjectReadContext.empty())) {
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertTrue(parser.getNumberType() == JsonParser.NumberType.LONG
                    || parser.getNumberType() == JsonParser.NumberType.BIG_INTEGER);
            assertEquals(expected, parser.getLongValue());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private void assertTokens(byte[] input, JsonToken... expected) throws Exception {
        try (JsonParser parser = mapper.readTree(input).traverse(ObjectReadContext.empty())) {
            for (JsonToken token : expected) {
                assertToken(token, parser.nextToken());
            }
            assertNull(parser.nextToken());
        }
    }
private static void assertToken(JsonToken expected, JsonToken actual) {
        assertEquals(expected, actual);
    }
static final class Bean {
        public String getX() { return "y"; }
        public int getY() { return 13; }
    }
static final class Person {
        public String name;
        public int magicNumber;
        public List<String> kids;
    }

    void __invoke_testSimpleViaObjectMapperVpack() throws Exception {
        try {
            testSimpleViaObjectMapperVpack();
        } finally {
        }
    }


    void __invoke_testPOJOStringVpack() throws Exception {
        try {
            testPOJOStringVpack();
        } finally {
        }
    }


    void __invoke_testPOJOIntArrayVpack() throws Exception {
        try {
            testPOJOIntArrayVpack();
        } finally {
        }
    }


    void __invoke_testPOJOBeanVpack() throws Exception {
        try {
            testPOJOBeanVpack();
        } finally {
        }
    }

}
