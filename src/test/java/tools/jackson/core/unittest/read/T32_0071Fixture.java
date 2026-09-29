package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.SerializableString;
import tools.jackson.core.io.SerializedString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0071Fixture {
private static final byte[] NAME_ACCESS = {
            0x14, 0x17,
            0x44, 'n', 'a', 'm', 'e', 0x31,
            0x45, 'n', 'a', 'm', 'e', '2', 0x3E,
            0x41, 'x', 0x44, 'n', 'a', 'm', 'e',
            0x03
    };
private static final byte[] NAME_NUMBERS = {
            0x14, 0x12,
            0x44, 'n', 'a', 'm', 'e', 0x20, (byte) 0x85,
            0x45, 'n', 'a', 'm', 'e', '2', 0x28, 0x63,
            0x02
    };
private static final byte[] NAME_NESTED = {
            0x14, 0x10,
            0x44, 'n', 'a', 'm', 'e', 0x0A,
            0x45, 'n', 'a', 'm', 'e', '2', 0x18,
            0x02
    };
private static final byte[] EMPTY_ARRAY_MEMBER = {
            0x14, 0x09, 0x44, 'n', 'a', 'm', 'e', 0x01, 0x01
    };
private static final byte[] ISSUE_38 = {
            0x14, 0x0F,
            0x45, 'f', 'i', 'e', 'l', 'd',
            0x45, 'v', 'a', 'l', 'u', 'e',
            0x01
    };
private static final byte[] NEXT_INT = {
            0x14, 0x15,
            0x41, 'a', 0x43, '1', '2', '3',
            0x41, 'b', 0x35,
            0x41, 'c', 0x13, 0x07, 0x19, 0x29, (byte) 0xC8, 0x01, 0x02,
            0x03
    };
private static final byte[] NEXT_LONG = {
            0x14, 0x14,
            0x41, 'a', 0x43, 'x', 'y', 'z',
            0x41, 'b', 0x20, (byte) 0xC5,
            0x41, 'c', 0x13, 0x05, 0x19, 0x3F, 0x02,
            0x03
    };
private static final byte[] NEXT_BOOLEAN = {
            0x14, 0x13,
            0x41, 'a', 0x43, 'x', 'y', 'z',
            0x41, 'b', 0x1A,
            0x41, 'c', 0x13, 0x05, 0x19, 0x30, 0x02,
            0x03
    };

    void isNextTokenNameMatchesAndSkipsLiteralObject() throws Exception {
        forEachBinarySource(NAME_ACCESS, T32_0071Fixture::assertIsNextTokenName);
    }

    void isNextTokenNameAcceptsSerializableStringInterface() throws Exception {
        forEachBinarySource(NAME_ACCESS, T32_0071Fixture::assertSerializableStringAccess);
    }

    void nextNameReturnsNamesAndAdvances() throws Exception {
        forEachBinarySource(NAME_ACCESS, T32_0071Fixture::assertNextNameAccess);
    }

    void nextNameMatchesNegativeAndPositiveIntegers() throws Exception {
        forEachBinarySource(NAME_NUMBERS, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(new SerializedString("name")));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-123, parser.getIntValue());
            assertTrue(parser.nextName(new SerializedString("name2")));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(99, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }

    void nextNameMatchesNestedObjectAndNull() throws Exception {
        forEachBinarySource(NAME_NESTED, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(new SerializedString("name")));
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(new SerializedString("name2")));
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }

    void nextNameSkipsEmptyArrayInBinaryObject() throws Exception {
        forEachBinarySource(EMPTY_ARRAY_MEMBER, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(new SerializedString("name")));
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertFalse(parser.nextName(new SerializedString("x")));
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }

    void nextNameHandlesManyDeterministicFields() throws Exception {
        byte[] document = largeCompactObject(4096);
        forEachBinarySource(document, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (int i = 0; i < 4096; ++i) {
                String name = "f" + i;
                assertTrue(parser.nextName(new SerializedString(name)));
                assertEquals(1, parser.nextIntValue(-1));
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }

    void nextIntValueUsesVpackIntegerTokens() throws Exception {
        forEachBinarySource(NEXT_INT, parser -> {
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.START_OBJECT, parser.currentToken());
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals("123", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(5, parser.nextIntValue(0));
            assertEquals("c", parser.nextName());
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.START_ARRAY, parser.currentToken());
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.VALUE_FALSE, parser.currentToken());
            assertEquals(456, parser.nextIntValue(0));
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(0, parser.nextIntValue(0));
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
        });
    }

    void nextLongValueUsesVpackIntegerTokens() throws Exception {
        forEachBinarySource(NEXT_LONG, parser -> {
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.START_OBJECT, parser.currentToken());
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals("xyz", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(-59L, parser.nextLongValue(0L));
            assertEquals("c", parser.nextName());
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.START_ARRAY, parser.currentToken());
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.VALUE_FALSE, parser.currentToken());
            assertEquals(-1L, parser.nextLongValue(0L));
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(0L, parser.nextLongValue(0L));
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
        });
    }

    void nextBooleanValueUsesBooleanTokens() throws Exception {
        forEachBinarySource(NEXT_BOOLEAN, parser -> {
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.START_OBJECT, parser.currentToken());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
            assertEquals("xyz", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(Boolean.TRUE, parser.nextBooleanValue());
            assertEquals("c", parser.nextName());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.START_ARRAY, parser.currentToken());
            assertEquals(Boolean.FALSE, parser.nextBooleanValue());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
            assertEquals(0, parser.getIntValue());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
            assertNull(parser.nextBooleanValue());
            assertEquals(JsonToken.END_OBJECT, parser.currentToken());
        });
    }

    void repeatedRootsKeepNextNameStateAfterIssue34() throws Exception {
        byte[] root = {
                0x14, 0x0E, 0x49, 'f', 'i', 'e', 'l', 'd', 'N', 'a', 'm', 'e', 0x31, 0x01
        };
        ByteArrayOutputStream output = new ByteArrayOutputStream(root.length * 223);
        for (int i = 0; i < 223; ++i) {
            output.write(root);
        }
        try (JsonParser parser = new VPackFactory().createParser(new OneByteInputStream(output.toByteArray()))) {
            SerializedString fieldName = new SerializedString("fieldName");
            for (int i = 0; i < 222; ++i) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertTrue(parser.nextName(fieldName));
                assertEquals(1L, parser.nextLongValue(-1L));
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            }
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(fieldName));
        }
    }

    void nextNameHandlesIssue38Object() throws Exception {
        forEachBinarySource(ISSUE_38, parser -> {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.nextName(new SerializedString("field")));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        });
    }
private static void assertIsNextTokenName(JsonParser parser) throws Exception {
        SerializedString name = new SerializedString("name");
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.START_OBJECT, parser.currentToken());
        assertEquals(JsonTokenId.ID_START_OBJECT, parser.currentTokenId());
        assertTrue(parser.nextName(name));
        assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
        assertEquals("name", parser.currentName());
        assertEquals("name", parser.getString());
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.currentToken());
        assertEquals(1, parser.getIntValue());
        assertFalse(parser.nextName(name));
        assertEquals("name2", parser.currentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertFalse(parser.nextName(name));
        assertEquals("x", parser.currentName());
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.END_OBJECT, parser.currentToken());
    }
private static void assertSerializableStringAccess(JsonParser parser) throws Exception {
        SerializableString name = new SerializedString("name");
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.START_OBJECT, parser.currentToken());
        assertTrue(parser.nextName(name));
        assertEquals("name", parser.currentName());
        assertFalse(parser.nextName(name));
        assertEquals(1, parser.getIntValue());
        assertFalse(parser.nextName(name));
        assertEquals("name2", parser.currentName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertFalse(parser.nextName(name));
        assertEquals("x", parser.currentName());
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
        assertFalse(parser.nextName(name));
        assertEquals(JsonToken.END_OBJECT, parser.currentToken());
    }
private static void assertNextNameAccess(JsonParser parser) throws Exception {
        assertNull(parser.nextName());
        assertEquals(JsonToken.START_OBJECT, parser.currentToken());
        assertEquals("name", parser.nextName());
        assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
        assertEquals("name", parser.currentName());
        assertEquals("name", parser.getString());
        assertNull(parser.nextName());
        assertEquals(1, parser.getIntValue());
        assertEquals("name2", parser.nextName());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals("x", parser.nextName());
        assertNull(parser.nextName());
        assertEquals(JsonToken.VALUE_STRING, parser.currentToken());
        assertNull(parser.nextName());
        assertEquals(JsonToken.END_OBJECT, parser.currentToken());
    }
private static byte[] largeCompactObject(int count) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream(count * 8);
        for (int i = 0; i < count; ++i) {
            writeString(body, ("f" + i).getBytes(StandardCharsets.UTF_8));
            body.write(0x31);
        }
        byte[] bodyBytes = body.toByteArray();
        int length = 1 + 1 + bodyBytes.length + 1;
        while (true) {
            int adjusted = 1 + varintLength(length) + bodyBytes.length + varintLength(count);
            if (adjusted == length) {
                break;
            }
            length = adjusted;
        }
        byte[] result = new byte[length];
        result[0] = 0x14;
        int headerLength = writeForward(result, 1, length);
        System.arraycopy(bodyBytes, 0, result, 1 + headerLength, bodyBytes.length);
        writeReverse(result, 1 + headerLength + bodyBytes.length, count);
        return result;
    }
private static void writeString(ByteArrayOutputStream output, byte[] value) throws IOException {
        if (value.length <= 126) {
            output.write(0x40 + value.length);
            output.write(value);
            return;
        }
        throw new IllegalArgumentException("test property name unexpectedly exceeds compact form");
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) {
            ++length;
        }
        return length;
    }
private static int writeForward(byte[] output, int offset, int value) {
        int at = offset;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return at - offset;
    }
private static void writeReverse(byte[] output, int offset, int value) {
        int at = offset;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        for (int left = offset, right = at - 1; left < right; ++left, --right) {
            byte swap = output[left];
            output[left] = output[right];
            output[right] = swap;
        }
    }
private static void forEachBinarySource(byte[] document, ParserAssertions assertions)
            throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(document)) {
            assertions.check(parser);
        }
        try (JsonParser parser = factory.createParser(new OneByteInputStream(document))) {
            assertions.check(parser);
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertions.check(parser);
        }
    }
@FunctionalInterface
    private interface ParserAssertions {
        void check(JsonParser parser) throws Exception;
    }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        private OneByteInputStream(byte[] input) {
            delegate = new ByteArrayInputStream(input);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            if (length == 0) {
                return 0;
            }
            int value = delegate.read();
            if (value < 0) {
                return -1;
            }
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_isNextTokenNameMatchesAndSkipsLiteralObject() throws Exception {
        try {
            isNextTokenNameMatchesAndSkipsLiteralObject();
        } finally {
        }
    }


    void __invoke_isNextTokenNameAcceptsSerializableStringInterface() throws Exception {
        try {
            isNextTokenNameAcceptsSerializableStringInterface();
        } finally {
        }
    }


    void __invoke_nextNameReturnsNamesAndAdvances() throws Exception {
        try {
            nextNameReturnsNamesAndAdvances();
        } finally {
        }
    }


    void __invoke_nextNameMatchesNegativeAndPositiveIntegers() throws Exception {
        try {
            nextNameMatchesNegativeAndPositiveIntegers();
        } finally {
        }
    }


    void __invoke_nextNameMatchesNestedObjectAndNull() throws Exception {
        try {
            nextNameMatchesNestedObjectAndNull();
        } finally {
        }
    }


    void __invoke_nextNameSkipsEmptyArrayInBinaryObject() throws Exception {
        try {
            nextNameSkipsEmptyArrayInBinaryObject();
        } finally {
        }
    }


    void __invoke_nextNameHandlesManyDeterministicFields() throws Exception {
        try {
            nextNameHandlesManyDeterministicFields();
        } finally {
        }
    }


    void __invoke_nextIntValueUsesVpackIntegerTokens() throws Exception {
        try {
            nextIntValueUsesVpackIntegerTokens();
        } finally {
        }
    }


    void __invoke_nextLongValueUsesVpackIntegerTokens() throws Exception {
        try {
            nextLongValueUsesVpackIntegerTokens();
        } finally {
        }
    }


    void __invoke_nextBooleanValueUsesBooleanTokens() throws Exception {
        try {
            nextBooleanValueUsesBooleanTokens();
        } finally {
        }
    }


    void __invoke_repeatedRootsKeepNextNameStateAfterIssue34() throws Exception {
        try {
            repeatedRootsKeepNextNameStateAfterIssue34();
        } finally {
        }
    }


    void __invoke_nextNameHandlesIssue38Object() throws Exception {
        try {
            nextNameHandlesIssue38Object();
        } finally {
        }
    }

}
