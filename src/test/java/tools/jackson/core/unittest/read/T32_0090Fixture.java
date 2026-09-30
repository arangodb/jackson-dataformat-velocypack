package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0090Fixture {
private static final byte[] NULL = { 0x18 };
private static final byte[] TRUE = { 0x1A };
private static final byte[] FALSE = { 0x19 };

    void longTextRetainsLiteralUtf8AcrossBinarySources() throws Exception {
        for (int length : new int[] { 310, 7700, 49000, 96000 }) {
            String value = randomText(length);
            byte[] input = indexedObject(pair("doc", longString(value)));
            assertLongText(new VPackFactory().createParser(input), value);
            assertLongText(new VPackFactory().createParser(
                    new OneByteInputStream(input)), value);
        }
    }

    void longerReadTextWritesTheEntireLiteralString() throws Exception {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; ++i) {
            builder.append("Sample Text").append(i);
        }
        String expected = builder.toString();
        byte[] input = indexedObject(
                pair("a", longString(expected)),
                pair("b", TRUE),
                pair("c", NULL),
                pair("d", string("foo")));

        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.getString(writer));
            assertEquals(expected, writer.toString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("c", parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("d", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foo", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    void literalFieldNamesRetainTheDecodedControlCharacters() throws Exception {
        String[] expected = {
                "", "\"funny\"", "\\", "\r", "\n", "\t", "\r\n",
                "\"\"", "Line\nfeed", "Yet even longer \"name\"!"
        };
        Pair[] pairs = new Pair[expected.length];
        for (int i = 0; i < expected.length; ++i) {
            pairs[i] = pair(expected[i], NULL);
        }

        try (JsonParser parser = new VPackFactory().createParser(indexedObject(pairs))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (String name : expected) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals(name, parser.currentName());
                assertEquals(name, parser.getString());
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void readStringConsumesStringAndScalarTokensInObjectOrder() throws Exception {
        String inputText = "this is a sample text for json parsing using readString() method";
        byte[] input = indexedObject(
                pair("a", longString(inputText)),
                pair("b", TRUE),
                pair("c", NULL),
                pair("d", string("foobar!")));

        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(inputText.length(), parser.readString(writer));
            assertEquals(inputText, writer.toString());
            assertEquals("", parser.getString());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            writer = new StringWriter();
            assertEquals(4L, parser.readString(writer));
            assertEquals("true", writer.toString());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("c", parser.currentName());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            writer = new StringWriter();
            assertEquals(4L, parser.readString(writer));
            assertEquals("null", writer.toString());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("d", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            writer = new StringWriter();
            assertEquals(7L, parser.readString(writer));
            assertEquals("foobar!", writer.toString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    void specExampleRetainsFullValuesAndTokenOnlyTraversal() throws Exception {
        byte[] input = specExample();
        assertSpecValues(new VPackFactory().createParser(input));
        assertSpecTokens(new VPackFactory().createParser(input));
    }
private static void assertLongText(JsonParser parser, String expected) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("doc", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            StringWriter writer = new StringWriter();
            assertEquals(expected.length(), parser.getString(writer));
            assertEquals(expected, writer.toString());
            assertEquals("doc", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertSpecValues(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertName(parser, "Image");
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertName(parser, "Width");
            assertNumber(parser, 800);
            assertName(parser, "Height");
            assertNumber(parser, 600);
            assertName(parser, "Title");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("View from 15th Floor", parser.getString());
            assertName(parser, "Thumbnail");
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertName(parser, "Url");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("http://www.example.com/image/481989943", parser.getString());
            assertName(parser, "Height");
            assertNumber(parser, 125);
            assertName(parser, "Width");
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("100", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertName(parser, "IDs");
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (int value : new int[] { 116, 943, 234, 38793 }) {
                assertNumber(parser, value);
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertSpecTokens(JsonParser parser) throws Exception {
        try (parser) {
            int tokens = 0;
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                ++tokens;
            }
            assertEquals(27, tokens);
        }
    }
private static void assertName(JsonParser parser, String expected) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(expected, parser.currentName());
        assertEquals(expected, parser.getString());
    }
private static void assertNumber(JsonParser parser, int expected) throws Exception {
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(expected, parser.getIntValue());
        assertEquals(Integer.toString(expected), parser.getString());
    }
private static byte[] specExample() {
        byte[] ids = compactArray(
                unsignedInt(116), unsignedInt(943), unsignedInt(234), unsignedInt(38793));
        byte[] thumbnail = indexedObject(
                pair("Url", string("http://www.example.com/image/481989943")),
                pair("Height", unsignedInt(125)),
                pair("Width", string("100")));
        byte[] image = indexedObject(
                pair("Width", unsignedInt(800)),
                pair("Height", unsignedInt(600)),
                pair("Title", string("View from 15th Floor")),
                pair("Thumbnail", thumbnail),
                pair("IDs", ids));
        return indexedObject(pair("Image", image));
    }
private static String randomText(int length) {
        StringBuilder builder = new StringBuilder(length + 100);
        Random random = new Random(length);
        while (builder.length() < length) {
            builder.append(random.nextInt()).append(" xyz foo");
            if (random.nextBoolean()) {
                builder.append(" and \"bar\"");
            } else if (random.nextBoolean()) {
                builder.append(" [whatever].... ");
            } else {
                builder.append(" UTF-8-fu: try this {\u00E2/\u0BF8/\uA123!} (look funny?)");
            }
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    builder.append('\n');
                } else if (random.nextBoolean()) {
                    builder.append('\r');
                } else {
                    builder.append("\r\n");
                }
            }
        }
        return builder.toString();
    }
private static Pair pair(String name, byte[] value) {
        return new Pair(name, value);
    }
private static byte[] indexedObject(Pair... pairs) {
        byte[][] names = new byte[pairs.length][];
        int bodyLength = 0;
        for (int i = 0; i < pairs.length; ++i) {
            names[i] = string(pairs[i].name());
            bodyLength += names[i].length + pairs[i].value().length;
        }
        int length = 1 + 4 + 4 + bodyLength + 4 * pairs.length;
        byte[] result = new byte[length];
        result[0] = 0x0D;
        putLittleEndian(result, 1, length, 4);
        putLittleEndian(result, 5, pairs.length, 4);
        int cursor = 9;
        int[] starts = new int[pairs.length];
        for (int i = 0; i < pairs.length; ++i) {
            starts[i] = cursor;
            System.arraycopy(names[i], 0, result, cursor, names[i].length);
            cursor += names[i].length;
            byte[] value = pairs[i].value();
            System.arraycopy(value, 0, result, cursor, value.length);
            cursor += value.length;
        }
        Integer[] order = new Integer[pairs.length];
        for (int i = 0; i < order.length; ++i) order[i] = i;
        Arrays.sort(order, (left, right) -> compareUnsigned(names[left], names[right]));
        for (int index : order) {
            putLittleEndian(result, cursor, starts[index], 4);
            cursor += 4;
        }
        return result;
    }
private static byte[] compactArray(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) bodyLength += value.length;
        int length = 1 + 1 + bodyLength + 1;
        if (length > 127 || values.length > 127) throw new AssertionError("fixture too large");
        byte[] result = new byte[length];
        result[0] = 0x13;
        result[1] = (byte) length;
        int cursor = 2;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, cursor, value.length);
            cursor += value.length;
        }
        result[cursor] = (byte) values.length;
        return result;
    }
private static byte[] string(String value) {
        return string(value.getBytes(StandardCharsets.UTF_8));
    }
private static byte[] string(byte[] payload) {
        if (payload.length > 126) throw new AssertionError("short fixture too large");
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] longString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        putLittleEndian(result, 1, payload.length, 8);
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static byte[] unsignedInt(int value) {
        if (value < 0) throw new AssertionError("positive fixture expected");
        if (value <= 9) return new byte[] { (byte) (0x30 + value) };
        int width = value <= 0xFF ? 1 : value <= 0xFFFF ? 2 : value <= 0xFFFFFF ? 3 : 4;
        byte[] result = new byte[1 + width];
        result[0] = (byte) (0x27 + width);
        putLittleEndian(result, 1, value, width);
        return result;
    }
private static int compareUnsigned(byte[] left, byte[] right) {
        int count = Math.min(left.length, right.length);
        for (int i = 1; i < count; ++i) {
            int comparison = Byte.toUnsignedInt(left[i]) - Byte.toUnsignedInt(right[i]);
            if (comparison != 0) return comparison;
        }
        return (left.length - 1) - (right.length - 1);
    }
private static void putLittleEndian(byte[] target, int offset, long value, int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }
private record Pair(String name, byte[] value) { }
private static final class OneByteInputStream extends ByteArrayInputStream {
        private OneByteInputStream(byte[] input) {
            super(input);
        }

        @Override
        public synchronized int read(byte[] target, int offset, int length) {
            return super.read(target, offset, Math.min(length, 1));
        }
    }

    void __invoke_longTextRetainsLiteralUtf8AcrossBinarySources() throws Exception {
        try {
            longTextRetainsLiteralUtf8AcrossBinarySources();
        } finally {
        }
    }


    void __invoke_longerReadTextWritesTheEntireLiteralString() throws Exception {
        try {
            longerReadTextWritesTheEntireLiteralString();
        } finally {
        }
    }


    void __invoke_literalFieldNamesRetainTheDecodedControlCharacters() throws Exception {
        try {
            literalFieldNamesRetainTheDecodedControlCharacters();
        } finally {
        }
    }


    void __invoke_readStringConsumesStringAndScalarTokensInObjectOrder() throws Exception {
        try {
            readStringConsumesStringAndScalarTokensInObjectOrder();
        } finally {
        }
    }


    void __invoke_specExampleRetainsFullValuesAndTokenOnlyTraversal() throws Exception {
        try {
            specExampleRetainsFullValuesAndTokenOnlyTraversal();
        } finally {
        }
    }

}
