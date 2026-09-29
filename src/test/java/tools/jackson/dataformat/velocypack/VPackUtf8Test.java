package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackUtf8Test {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void decodesEmbeddedNulAndSupplementaryCharactersWithExactMetadata() throws Exception {
        byte[] utf8 = { 'a', 0, (byte) 0xE2, (byte) 0x82, (byte) 0xAC,
                (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x80 };
        try (VPackParser parser = (VPackParser) factory.createParser(string(utf8))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a\0€😀", parser.getString());
            assertTrue(parser.hasStringCharacters());
            assertArrayEquals("a\0€😀".toCharArray(), parser.getStringCharacters());
            assertEquals(5, parser.getStringLength());
            assertEquals(0, parser.getStringOffset());
        }
    }

    @Test
    void rejectsAllStrictUtf8FailureFamilies() {
        byte[][] invalid = {
                { (byte) 0xC0, (byte) 0x80 }, // overlong
                { (byte) 0xE0, (byte) 0x80, (byte) 0x80 }, // overlong
                { (byte) 0xED, (byte) 0xA0, (byte) 0x80 }, // surrogate
                { (byte) 0xF4, (byte) 0x90, (byte) 0x80, (byte) 0x80 }, // out of range
                { (byte) 0xE2, (byte) 0x28, (byte) 0xA1 }, // bad continuation
                { (byte) 0xF0, (byte) 0x9F, (byte) 0x98 } // truncated
        };
        for (byte[] value : invalid) {
            try (VPackParser parser = (VPackParser) factory.createParser(string(value))) {
                assertThrows(StreamReadException.class, parser::nextToken,
                        Arrays.toString(value));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }

    @Test
    void decodedCharacterLimitIsSeparateFromWireByteLength() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxStringLength(2).build())
                .build();
        byte[] threeAscii = string(new byte[] { 'a', 'b', 'c' });
        try (VPackParser parser = (VPackParser) constrained.createParser(threeAscii)) {
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Test
    void invalidStringAfterAValidRootFailsWhenTraversed() throws Exception {
        byte[] invalid = { 0x41, 'a', 0x42, (byte) 0xC2 };
        try (VPackParser parser = (VPackParser) factory.createParser(invalid)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getString());
            assertThrows(StreamReadException.class, parser::nextToken);
        }
    }

    @Test
    void longEncodingUsesUtf8BytesAndAcceptsNonminimalLengths() throws Exception {
        byte[] payload = new byte[127];
        Arrays.fill(payload, (byte) 'x');
        byte[] encoded = stringLong(payload, 127L);
        try (VPackParser parser = (VPackParser) factory.createParser(
                new ByteArrayInputStream(encoded))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(127, parser.getStringLength());
            assertEquals(payload.length, parser.getString().getBytes(StandardCharsets.UTF_8).length);
            assertNull(parser.nextToken());
        }
    }

    private static byte[] string(byte[] payload) {
        if (payload.length > 126) throw new IllegalArgumentException();
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }

    private static byte[] stringLong(byte[] payload, long declaredLength) {
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        for (int i = 0; i < 8; i++) result[1 + i] = (byte) (declaredLength >>> (8 * i));
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
}
