package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.sym.PropertyNameMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0069F0 {
private static final byte[] SIMPLE_OBJECT = {
            0x14, 0x06, 0x41, 'a', 0x31, 0x01
    };
private static final String LONG_NAME =
            "01234567890123456789012345678901234567890123456789012345678901234";
private static final byte[] LONG_NAME_OBJECT = longNameObject();

    void interningEnabledWithStreamParser() throws Exception {
        VPackFactory factory = factoryWith(true, true);
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(SIMPLE_OBJECT))) {
            assertTrue(parser.willInternPropertyNames());
        }
    }

    void interningEnabledWithDataInputParser() throws Exception {
        VPackFactory factory = factoryWith(true, true);
        DataInput input = new DataInputStream(new ByteArrayInputStream(SIMPLE_OBJECT));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertTrue(parser.willInternPropertyNames());
        }
    }

    void interningDisabledWithStreamParser() throws Exception {
        VPackFactory factory = factoryWith(false, true);
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(SIMPLE_OBJECT))) {
            assertFalse(parser.willInternPropertyNames());
        }
    }

    void interningDisabledWithDataInputParser() throws Exception {
        VPackFactory factory = factoryWith(false, true);
        DataInput input = new DataInputStream(new ByteArrayInputStream(SIMPLE_OBJECT));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertFalse(parser.willInternPropertyNames());
        }
    }

    void interningWhenCanonicalizationDisabledUsesVpackBinaryCapabilityBoundary()
            throws Exception {
        VPackFactory internConfigured = factoryWith(true, false);
        assertTrue(internConfigured.isEnabled(
                TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));
        try (JsonParser parser = internConfigured.createParser(SIMPLE_OBJECT)) {
            // VPack's placeholder canonicalizer intentionally disables interning
            // when CANONICALIZE_PROPERTY_NAMES is disabled.
            assertFalse(parser.willInternPropertyNames());
        }

        VPackFactory noInternConfigured = factoryWith(false, false);
        assertFalse(noInternConfigured.isEnabled(
                TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));
        try (JsonParser parser = noInternConfigured.createParser(SIMPLE_OBJECT)) {
            assertFalse(parser.willInternPropertyNames());
        }
    }

    void interningWithDefaultFactory() throws Exception {
        VPackFactory factory = new VPackFactory();
        assertFalse(factory.isEnabled(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES));
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(SIMPLE_OBJECT))) {
            assertFalse(parser.willInternPropertyNames());
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(SIMPLE_OBJECT));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertFalse(parser.willInternPropertyNames());
        }
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

    void __invoke_interningEnabledWithStreamParser() throws Exception {
        try {
            interningEnabledWithStreamParser();
        } finally {
        }
    }


    void __invoke_interningEnabledWithDataInputParser() throws Exception {
        try {
            interningEnabledWithDataInputParser();
        } finally {
        }
    }


    void __invoke_interningDisabledWithStreamParser() throws Exception {
        try {
            interningDisabledWithStreamParser();
        } finally {
        }
    }


    void __invoke_interningDisabledWithDataInputParser() throws Exception {
        try {
            interningDisabledWithDataInputParser();
        } finally {
        }
    }


    void __invoke_interningWhenCanonicalizationDisabledUsesVpackBinaryCapabilityBoundary() throws Exception {
        try {
            interningWhenCanonicalizationDisabledUsesVpackBinaryCapabilityBoundary();
        } finally {
        }
    }


    void __invoke_interningWithDefaultFactory() throws Exception {
        try {
            interningWithDefaultFactory();
        } finally {
        }
    }

}
