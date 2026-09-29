package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VPackCompactParserTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void parsesTheIndependentCompactArrayAndObjectExamples() throws Exception {
        assertTokens(new byte[] { 0x13, 0x06, 0x31, 0x28, 0x10, 0x02 },
                JsonToken.START_ARRAY, JsonToken.VALUE_NUMBER_INT,
                JsonToken.VALUE_NUMBER_INT, JsonToken.END_ARRAY);
        assertTokens(new byte[] { 0x14, 0x0A, 0x41, 0x61, 0x31, 0x41, 0x62,
                        0x28, 0x10, 0x02 },
                JsonToken.START_OBJECT, JsonToken.PROPERTY_NAME,
                JsonToken.VALUE_NUMBER_INT, JsonToken.PROPERTY_NAME,
                JsonToken.VALUE_NUMBER_INT, JsonToken.END_OBJECT);
    }

    @Test
    void decodesReverseCountGroupsAt127128And129() throws Exception {
        for (int count : new int[] { 127, 128, 129 }) {
            byte[] suffix = count < 128
                    ? new byte[] { (byte) count }
                    : new byte[] { 0x01, (byte) count };
            byte[] input = compactArray(count, suffix);
            try (JsonParser parser = factory.createParser(input)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                for (int i = 0; i < count; ++i) {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(1, parser.getIntValue());
                }
                assertEquals(JsonToken.END_ARRAY, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }
    }

    @Test
    void acceptsNonminimalForwardAndReverseGroupsAndEmptyValues() throws Exception {
        assertTokens(new byte[] { 0x13, (byte) 0x86, 0x00, 0x00, (byte) 0x80, (byte) 0x80 },
                JsonToken.START_ARRAY, JsonToken.END_ARRAY);
        assertTokens(new byte[] { 0x14, (byte) 0x86, 0x00, 0x00, (byte) 0x80, (byte) 0x80 },
                JsonToken.START_OBJECT, JsonToken.END_OBJECT);
    }

    @Test
    void traversesNestedRegularCompactAndCompressedKeyValues() throws Exception {
        // Regular equal array containing compact array [1, 16].
        byte[] compactArray = new byte[] { 0x13, 0x06, 0x31, 0x28, 0x10, 0x02 };
        byte[] regular = new byte[2 + compactArray.length];
        regular[0] = 0x02;
        regular[1] = (byte) regular.length;
        System.arraycopy(compactArray, 0, regular, 2, compactArray.length);
        // Compact object with an ID-0 key and a regular empty object value.
        byte[] compactObject = new byte[] { 0x14, 0x05, 0x30, 0x0A, 0x01 };
        byte[] input = concat(regular, compactObject);
        VPackFactory idFactory = VPackFactory.builder()
                .attributeNameCodec(new VPackAttributeNameCodec() {
                    @Override public String decode(BigInteger id) {
                        return id.signum() == 0 ? "zero" : null;
                    }
                    @Override public BigInteger encode(String name) { return null; }
                })
                .build();
        try (JsonParser parser = idFactory.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("zero", parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    private void assertTokens(byte[] input, JsonToken... expected) throws Exception {
        try (JsonParser parser = factory.createParser(input)) {
            for (JsonToken token : expected) assertEquals(token, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    private static byte[] compactArray(int count, byte[] suffix) {
        int length = 1 + varintLength(lengthFor(count, suffix.length)) + count + suffix.length;
        byte[] result = new byte[length];
        result[0] = 0x13;
        int header = writeForward(result, 1, length);
        Arrays.fill(result, header, header + count, (byte) 0x31);
        System.arraycopy(suffix, 0, result, header + count, suffix.length);
        return result;
    }

    private static long lengthFor(int count, int suffixLength) {
        int length = 1 + 1 + count + suffixLength;
        while (varintLength(length) + 1 + count + suffixLength != length) {
            length = 1 + varintLength(length) + count + suffixLength;
        }
        return length;
    }

    private static int writeForward(byte[] output, int offset, long value) {
        int at = offset;
        do {
            int group = (int) (value & 0x7F);
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return at;
    }

    private static int varintLength(long value) {
        int result = 1;
        while ((value >>>= 7) != 0) ++result;
        return result;
    }

    private static byte[] concat(byte[]... values) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] value : values) output.writeBytes(value);
        return output.toByteArray();
    }
}
