package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0070F2 {

    void matcherNavigationRetainsCaseRules() throws Exception {
        String[] names = { "enabled", "a", "longerName", "otherStuff3" };
        PropertyNameMatcher sensitive = new VPackFactory().constructNameMatcher(
                names(names), true);
        PropertyNameMatcher insensitive = new VPackFactory().constructCINameMatcher(
                names(names), true, java.util.Locale.US);
        byte[] document = object(names, new String[] { "true", "4", "Billy-Bob Burger", "0.25" });
        try (JsonParser parser = new VPackFactory().createParser(document)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0, parser.nextNameMatch(sensitive));
            assertEquals("enabled", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("true", parser.getString());
            assertEquals(1, parser.nextNameMatch(sensitive));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("4", parser.getString());
        }
        try (JsonParser parser = new VPackFactory().createParser(object(
                new String[] { "ENABLED" }, new String[] { "true" }))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0, parser.nextNameMatch(insensitive));
            assertEquals("ENABLED", parser.currentName());
        }
    }
private static void assertSingleName(String name, byte[] document) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(document)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(name, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static void assertNameValue(JsonParser parser, String name, String value) throws Exception {
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(name, parser.currentName());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertEquals(value, parser.getString());
    }
private static List<Named> names(String[] values) {
        return java.util.Arrays.stream(values).map(Named::fromString).toList();
    }
private static String repeatedDigits(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; ++i) result.append((char) ('0' + i % 10));
        return result.toString();
    }
private static byte[] object(String[] names, String[] values) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (int i = 0; i < names.length; ++i) {
            byte[] key = names[i].getBytes(StandardCharsets.UTF_8);
            writeString(body, key);
            byte[] value = values[i].getBytes(StandardCharsets.UTF_8);
            if (value.length <= 63) {
                body.write(0x40 + value.length);
                body.write(value);
            } else {
                throw new IllegalArgumentException("test value unexpectedly long");
            }
        }
        int bodyLength = body.size();
        int countLength = varintLength(names.length);
        int length = 1 + 1 + bodyLength + countLength;
        while (true) {
            int adjusted = 1 + varintLength(length) + bodyLength + countLength;
            if (adjusted == length) break;
            length = adjusted;
        }
        byte[] result = new byte[length];
        result[0] = 0x14;
        int headerLength = writeForward(result, 1, length);
        byte[] bodyBytes = body.toByteArray();
        System.arraycopy(bodyBytes, 0, result, 1 + headerLength, bodyBytes.length);
        writeReverse(result, 1 + headerLength + bodyBytes.length, names.length);
        return result;
    }
private static void writeString(ByteArrayOutputStream out, byte[] value) throws IOException {
        if (value.length <= 126) {
            out.write(0x40 + value.length);
            out.write(value);
            return;
        }
        out.write(0xBF);
        long length = value.length;
        for (int i = 0; i < 8; ++i) {
            out.write((int) (length & 0xFF));
            length >>>= 8;
        }
        out.write(value);
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) ++length;
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
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;
        OneByteInputStream(byte[] input) { delegate = new ByteArrayInputStream(input); }
        @Override public int read() { return delegate.read(); }
        @Override public int read(byte[] target, int offset, int length) {
            if (length == 0) return 0;
            int value = delegate.read();
            if (value < 0) return -1;
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_matcherNavigationRetainsCaseRules() throws Exception {
        try {
            matcherNavigationRetainsCaseRules();
        } finally {
        }
    }

}
