package tools.jackson.databind.node;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.List;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.node.POJONode;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0505F1 {
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

    // Provenance: TreeTraversingParserTest#testSimple().
    void testSimpleVpack() throws Exception {
        try (JsonParser parser = mapper.readTree(SIMPLE_ROOT).traverse(ObjectReadContext.empty())) {
            assertNull(parser.currentToken());
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertNull(parser.currentName());
            assertEquals(JsonToken.START_OBJECT.asString(), parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals("a", parser.getString());
            assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(123, parser.getIntValue());
            assertEquals((short) 123, parser.getShortValue());
            assertEquals(123, parser.getValueAsInt(-1));
            assertEquals(123L, parser.getValueAsLong(42L));
            assertEquals(BigInteger.valueOf(123), parser.getBigIntegerValue());
            assertEquals("123", parser.getString());
            assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("list", parser.currentName());
            assertEquals("list", parser.getString());
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals("list", parser.currentName());
            assertEquals(JsonToken.START_ARRAY.asString(), parser.getString());
            assertToken(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertNull(parser.currentName());
            assertEquals(12.25, parser.getDoubleValue(), 0.0);
            assertEquals(12.25f, parser.getFloatValue(), 0.0f);
            assertEquals(JsonParser.NumberType.DOUBLE, parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.DOUBLE64, parser.getNumberTypeFP());
            assertFalse(parser.isNaN());
            InputCoercionException shortFailure = assertThrows(InputCoercionException.class,
                    parser::getShortValue);
            assertTrue(shortFailure.getMessage().contains("short"));
            InputCoercionException intFailure = assertThrows(InputCoercionException.class,
                    parser::getIntValue);
            assertTrue(intFailure.getMessage().contains("int"));
            assertEquals(12, parser.getValueAsInt(1));
            InputCoercionException longFailure = assertThrows(InputCoercionException.class,
                    parser::getLongValue);
            assertTrue(longFailure.getMessage().contains("long"));
            assertEquals(12L, parser.getValueAsLong(2L));
            assertEquals("12.25", parser.getString());
            assertToken(JsonToken.VALUE_NULL, parser.nextToken());
            assertNull(parser.currentName());
            assertNull(parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.UNKNOWN, parser.getNumberTypeFP());
            assertEquals(JsonToken.VALUE_NULL.asString(), parser.getString());
            assertToken(JsonToken.VALUE_TRUE, parser.nextToken());
            assertNull(parser.currentName());
            assertTrue(parser.getBooleanValue());
            InputCoercionException booleanIntFailure = assertThrows(InputCoercionException.class,
                    () -> parser.getValueAsInt(1));
            assertTrue(booleanIntFailure.getMessage().contains("VALUE_TRUE"));
            InputCoercionException booleanLongFailure = assertThrows(InputCoercionException.class,
                    () -> parser.getValueAsLong(2L));
            assertTrue(booleanLongFailure.getMessage().contains("VALUE_TRUE"));
            assertNull(parser.getNumberType());
            assertEquals(JsonParser.NumberTypeFP.UNKNOWN, parser.getNumberTypeFP());
            assertEquals(JsonToken.VALUE_TRUE.asString(), parser.getString());
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertNull(parser.currentName());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.currentName());
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertNull(parser.currentName());
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.currentName());
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TreeTraversingParserTest#testArray().
    void testArrayVpack() throws Exception {
        assertTokens(EMPTY_ARRAY, JsonToken.START_ARRAY, JsonToken.END_ARRAY);
        assertTokens(NESTED_EMPTY_ARRAY, JsonToken.START_ARRAY, JsonToken.START_ARRAY,
                JsonToken.END_ARRAY, JsonToken.END_ARRAY);
        assertTokens(NESTED_VALUE_ARRAY, JsonToken.START_ARRAY, JsonToken.START_ARRAY,
                JsonToken.VALUE_NUMBER_FLOAT, JsonToken.END_ARRAY, JsonToken.END_ARRAY);
    }

    // Provenance: TreeTraversingParserTest#testNested().
    void testNestedVpack() throws Exception {
        try (JsonParser parser = mapper.readTree(COORDINATES_ROOT).traverse(ObjectReadContext.empty())) {
            JsonToken[] expected = {
                    JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME,
                    JsonToken.START_ARRAY, JsonToken.START_ARRAY, JsonToken.START_ARRAY,
                    JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_INT, JsonToken.END_ARRAY,
                    JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_FLOAT, JsonToken.VALUE_NUMBER_FLOAT,
                    JsonToken.END_ARRAY, JsonToken.END_ARRAY, JsonToken.END_ARRAY,
                    JsonToken.END_OBJECT
            };
            for (JsonToken token : expected) {
                assertToken(token, parser.nextToken());
            }
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TreeTraversingParserTest#testBinaryNode().
    void testBinaryNodeVpack() throws Exception {
        JsonNode node = mapper.readTree(BINARY_ROOT);
        assertTrue(node instanceof BinaryNode);
        try (JsonParser parser = node.traverse(ObjectReadContext.empty())) {
            assertNull(parser.currentToken());
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(new byte[] { 0, -5 }, parser.getBinaryValue());
            assertEquals("APs=", parser.getString());
            assertNull(parser.nextToken());
        }
    }

    // Provenance: TreeTraversingParserTest#testBinaryPojo().
    void testBinaryPojoVpack() throws Exception {
        byte[] input = { 1, 2, 100 };
        POJONode node = new POJONode(input);
        try (JsonParser parser = node.traverse(ObjectReadContext.empty())) {
            assertNull(parser.currentToken());
            assertToken(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            byte[] data = parser.getBinaryValue();
            assertArrayEquals(input, data);
            assertSame(data, parser.getEmbeddedObject());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            assertEquals(input.length,
                    parser.readBinaryValue(Base64Variants.getDefaultVariant(), output));
            assertArrayEquals(input, output.toByteArray());
        }
    }

    // Provenance: TreeTraversingParserTest#testDataBind().
    void testDataBindVpack() throws Exception {
        Person person = mapper.treeToValue(mapper.readTree(PERSON_ROOT), Person.class);
        assertNotNull(person);
        assertEquals(42, person.magicNumber);
        assertEquals("Tatu", person.name);
        assertEquals(List.of("Leo", "Lila", "Leia"), person.kids);
    }

    // Provenance: TreeTraversingParserTest#testNumberOverflowInt().
    void testNumberOverflowIntVpack() throws Exception {
        assertIntOverflow(INT_OVERFLOW_ARRAY);
        assertIntOverflow(INT_OVERFLOW_OBJECT);
        assertIntOverflow(DOUBLE_INT_OVERFLOW_ARRAY);
    }

    // Provenance: TreeTraversingParserTest#testNumberOverflowLong().
    void testNumberOverflowLongVpack() throws Exception {
        BigInteger tooBig = BigInteger.ONE.shiftLeft(63);
        assertLongOverflow(LONG_OVERFLOW_ARRAY, tooBig);
        assertLongOverflow(LONG_OVERFLOW_OBJECT, tooBig);
        assertLongOverflow(DOUBLE_LONG_OVERFLOW_ARRAY, null);
        assertLongInRange(LONG_ABOVE_INT_OBJECT, 1L + Integer.MAX_VALUE);
        assertLongInRange(LONG_BELOW_INT_OBJECT, -1L + Integer.MIN_VALUE);
        assertLongInRange(LONG_MAX_OBJECT, Long.MAX_VALUE);
        assertLongInRange(LONG_MIN_OBJECT, Long.MIN_VALUE);
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

    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }


    void __invoke_testArrayVpack() throws Exception {
        try {
            testArrayVpack();
        } finally {
        }
    }


    void __invoke_testNestedVpack() throws Exception {
        try {
            testNestedVpack();
        } finally {
        }
    }


    void __invoke_testBinaryNodeVpack() throws Exception {
        try {
            testBinaryNodeVpack();
        } finally {
        }
    }


    void __invoke_testBinaryPojoVpack() throws Exception {
        try {
            testBinaryPojoVpack();
        } finally {
        }
    }


    void __invoke_testDataBindVpack() throws Exception {
        try {
            testDataBindVpack();
        } finally {
        }
    }


    void __invoke_testNumberOverflowIntVpack() throws Exception {
        try {
            testNumberOverflowIntVpack();
        } finally {
        }
    }


    void __invoke_testNumberOverflowLongVpack() throws Exception {
        try {
            testNumberOverflowLongVpack();
        } finally {
        }
    }

}
