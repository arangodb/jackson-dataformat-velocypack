package tools.jackson.core.unittest.sym;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.sym.SimpleNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0099F1 {
private static final String KEY_1 = "aaaabbbbcccc";
private static final String KEY_2 = "aaaabbbbcccc2";
private static final byte[] COLLISION_OBJECT = {
            0x14, 0x24,
            0x4C, 'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b', 'c', 'c', 'c', 'c',
            0x42, 'v', '3',
            0x4D, 'a', 'a', 'a', 'a', 'b', 'b', 'b', 'b', 'c', 'c', 'c', 'c', '2',
            0x42, 'v', '4',
            0x02
    };
private static final byte[] SYMBOL_OBJECT = {
            0x14, 0x0F,
            0x41, 'a', 0x33,
            0x43, 'a', 'a', 'a', 0x34,
            0x42, '_', 'a', 0x30,
            0x03
    };
private final VPackFactory factory = new VPackFactory();
private static void assertMatcherCollision(PropertyNameMatcher matcher, JsonParser parser)
            throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0, parser.nextNameMatch(matcher));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals(KEY_1, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("v3", parser.getString());
            assertEquals(1, parser.nextNameMatch(matcher));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals(KEY_2, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("v4", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    void caseInsensitiveMatcherRetainsSourceSizeAndNullFamilies() {
        assertCaseInsensitive(List.of("single"));
        assertCaseInsensitive(Arrays.asList(null, "b", null));
        assertCaseInsensitive(List.of("a", "bcd", "Fittipaldi", "goober"));
        assertCaseInsensitive(Arrays.asList("a", "bcd", null, "goober"));
        assertCaseInsensitive(Arrays.asList("a", null, null, "goober", "xyz"));
        assertCaseInsensitive(List.of("foo", "bar", "foobar", "fubar", "bizzbah",
                "grimagnoefwemp"));
        assertCaseInsensitive(List.of("a", "b", "c", "d", "E", "f", "G", "h"));
        assertCaseInsensitive(Arrays.asList("a", "b", null, "d", "E", "f", "G", null));
        assertCaseInsensitive(generate("base", 39));
        assertCaseInsensitive(generate("Of ", 139));
        assertCaseInsensitive(generate("ACE-", 499));
    }
private static void assertRegularCollision(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(KEY_1, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("v3", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(KEY_2, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("v4", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static void assertCaseInsensitive(List<String> names) {
        PropertyNameMatcher matcher = SimpleNameMatcher.constructCaseInsensitive(
                Locale.US, names.stream().map(Named::fromString).toList(), true);
        for (int i = 0; i < names.size(); ++i) {
            String name = names.get(i);
            if (name == null) {
                continue;
            }
            assertEquals(i, matcher.matchName(name));
            assertEquals(i, matcher.matchName(new String(name)));
            assertEquals(i, matcher.matchName(name.toLowerCase(Locale.US)));
            assertEquals(i, matcher.matchName(name.toUpperCase(Locale.US)));
            assertEquals(PropertyNameMatcher.MATCH_UNKNOWN_NAME,
                    matcher.matchName(name + "FOOBAR"));
        }
    }
private static List<String> generate(String base, int count) {
        List<String> result = new ArrayList<>(count);
        while (--count >= 0) {
            result.add((base + count).intern());
        }
        return result;
    }
private static int quad(byte[] bytes, int offset, int length) {
        int value = bytes[offset++] & 0xFF;
        if (length >= 2) value = (value << 8) | (bytes[offset++] & 0xFF);
        if (length >= 3) value = (value << 8) | (bytes[offset++] & 0xFF);
        if (length == 4) value = (value << 8) | (bytes[offset] & 0xFF);
        return value;
    }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        OneByteInputStream(byte[] input) {
            delegate = new ByteArrayInputStream(input);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            if (length == 0) return 0;
            int value = delegate.read();
            if (value < 0) return -1;
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_caseInsensitiveMatcherRetainsSourceSizeAndNullFamilies() throws Exception {
        try {
            caseInsensitiveMatcherRetainsSourceSizeAndNullFamilies();
        } finally {
        }
    }

}
