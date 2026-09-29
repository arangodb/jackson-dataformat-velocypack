package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.sym.PropertyNameMatcher;
import tools.jackson.core.util.Named;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0069F1 {
private static final byte[] SIMPLE_OBJECT = {
            0x14, 0x06, 0x41, 'a', 0x31, 0x01
    };
private static final String LONG_NAME =
            "01234567890123456789012345678901234567890123456789012345678901234";
private static final byte[] LONG_NAME_OBJECT = longNameObject();

    void longName65CharactersAcrossBinarySources() throws Exception {
        assertEquals(65, LONG_NAME.length());
        assertLongName(new VPackFactory().createParser(LONG_NAME_OBJECT));
        assertLongName(new VPackFactory().createParser(
                new OneByteInputStream(LONG_NAME_OBJECT)));
        DataInput input = new DataInputStream(new ByteArrayInputStream(LONG_NAME_OBJECT));
        assertLongName(new VPackFactory().createParser(ObjectReadContext.empty(), input));
    }

    void longNameWithMatcher65CharsAcrossBinarySources() throws Exception {
        VPackFactory factory = new VPackFactory();
        PropertyNameMatcher matcher = factory.constructNameMatcher(
                List.of(Named.fromString("a"), Named.fromString(LONG_NAME)), false);

        assertLongNameWithMatcher(factory.createParser(LONG_NAME_OBJECT), matcher);
        assertLongNameWithMatcher(factory.createParser(
                new OneByteInputStream(LONG_NAME_OBJECT)), matcher);
        DataInput input = new DataInputStream(new ByteArrayInputStream(LONG_NAME_OBJECT));
        assertLongNameWithMatcher(factory.createParser(ObjectReadContext.empty(), input), matcher);
    }
private static VPackFactory factoryWith(boolean intern, boolean canonicalize) {
        return VPackFactory.builder()
                .configure(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES, intern)
                .configure(TokenStreamFactory.Feature.CANONICALIZE_PROPERTY_NAMES, canonicalize)
                .build();
    }
private static void assertLongName(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("123", parser.getString());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(LONG_NAME, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static void assertLongNameWithMatcher(JsonParser parser,
            PropertyNameMatcher matcher) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(0, parser.nextNameMatch(matcher));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("123", parser.getString());
            assertEquals(1, parser.nextNameMatch(matcher));
            assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
            assertEquals(LONG_NAME, parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("value", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }
private static byte[] longNameObject() {
        byte[] name = LONG_NAME.getBytes(StandardCharsets.US_ASCII);
        if (name.length != 65) {
            throw new AssertionError("long-name fixture must contain 65 ASCII bytes");
        }

        byte[] result = new byte[89];
        result[0] = 0x14;
        result[1] = 0x59;
        result[2] = 0x41;
        result[3] = 'a';
        result[4] = 0x43;
        result[5] = '1';
        result[6] = '2';
        result[7] = '3';
        result[8] = (byte) 0xBF;
        result[9] = 0x41;
        System.arraycopy(name, 0, result, 17, name.length);
        result[82] = 0x45;
        result[83] = 'v';
        result[84] = 'a';
        result[85] = 'l';
        result[86] = 'u';
        result[87] = 'e';
        result[88] = 0x02;
        return result;
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
        public int read(byte[] target, int offset, int length) throws IOException {
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

    void __invoke_longName65CharactersAcrossBinarySources() throws Exception {
        try {
            longName65CharactersAcrossBinarySources();
        } finally {
        }
    }


    void __invoke_longNameWithMatcher65CharsAcrossBinarySources() throws Exception {
        try {
            longNameWithMatcher65CharsAcrossBinarySources();
        } finally {
        }
    }

}
