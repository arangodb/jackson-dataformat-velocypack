package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackAttributeNameParserTest {
    private static final BigInteger UINT64_MAX = BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    @Test
    void resolvesZeroAndFullUint64WithoutChangingPhysicalOrder() throws Exception {
        BigInteger high = UINT64_MAX;
        VPackAttributeNameCodec codec = decoder(id -> {
            if (id.signum() == 0) return "b";
            if (id.equals(high)) return "a";
            return null;
        });
        byte[] body = concat(key(0x30), new byte[] { 0x31 }, key(high), new byte[] { 0x32 });
        byte[] input = VPackObjectParserTest.object(1, true, body,
                new long[] { 3 + 2, 3 });

        try (VPackParser parser = (VPackParser) VPackFactory.builder()
                .attributeNameCodec(codec).build().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(BigInteger.ZERO, parser.currentAttributeId());
            assertEquals(VPackType.SMALL_INTEGER, parser.currentVPackType());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertNull(parser.currentAttributeId());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(high, parser.currentAttributeId());
            assertEquals(VPackType.UNSIGNED_INTEGER, parser.currentVPackType());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertNull(parser.currentAttributeId());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.currentAttributeId());
        }
    }

    @Test
    void rejectsMissingAndUnknownResolution() {
        byte[] input = VPackObjectParserTest.object(1, true,
                concat(key(0x31), new byte[] { 0x30 }), new long[] { 3 });
        assertMessage("compressed attribute name", () -> {
            try (JsonParser parser = new VPackFactory().createParser(input)) {
                parser.nextToken();
                parser.nextToken();
            }
        });
        VPackFactory unknown = VPackFactory.builder()
                .attributeNameCodec(decoder(id -> null)).build();
        assertMessage("could not resolve ID", () -> {
            try (JsonParser parser = unknown.createParser(input)) {
                parser.nextToken();
                parser.nextToken();
            }
        });
    }

    @Test
    void rejectsSignedKeysEvenWhenPayloadIsPositive() {
        byte[] body = concat(new byte[] { 0x21, 0x01 }, new byte[] { 0x30 });
        byte[] input = VPackObjectParserTest.object(1, true, body, new long[] { 3 });
        assertMessage("signed integer keys are forbidden", () -> {
            try (JsonParser parser = new VPackFactory().createParser(input)) {
                parser.nextToken();
                parser.nextToken();
            }
        });
    }

    @Test
    void validatesResolvedNamesAndWrapsCodecFailures() {
        byte[] input = VPackObjectParserTest.object(1, true,
                concat(key(0x30), new byte[] { 0x30 }), new long[] { 3 });
        assertMessage("invalid UTF-16", () -> {
            try (VPackParser parser = (VPackParser) VPackFactory.builder()
                    .attributeNameCodec(decoder(id -> "\uD800")).build().createParser(input)) {
                parser.nextToken();
                parser.nextToken();
            }
        });
        assertMessage("compressed attribute name", () -> {
            try (VPackParser parser = (VPackParser) VPackFactory.builder()
                    .attributeNameCodec(decoder(id -> { throw new IllegalStateException("boom"); }))
                    .build().createParser(input)) {
                parser.nextToken();
                parser.nextToken();
            }
        });
    }

    @Test
    void resolvedNamesDriveDuplicateDetection() {
        byte[] body = concat(key(0x30), new byte[] { 0x31 }, key(0x31), new byte[] { 0x32 });
        byte[] input = VPackObjectParserTest.object(1, true, body,
                new long[] { 3, 5 });
        VPackFactory strict = VPackFactory.builder().attributeNameCodec(decoder(id -> "same"))
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = strict.createParser(input)) {
                while (parser.nextToken() != null) { }
            }
        });
    }

    private static byte[] key(int marker) {
        return new byte[] { (byte) marker };
    }

    private static byte[] key(BigInteger value) {
        byte[] result = new byte[9];
        result[0] = 0x2F;
        BigInteger current = value;
        for (int i = 0; i < 8; ++i) {
            result[i + 1] = current.byteValue();
            current = current.shiftRight(8);
        }
        return result;
    }

    private static byte[] concat(byte[]... values) {
        int length = 0;
        for (byte[] value : values) length += value.length;
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }

    private static VPackAttributeNameCodec decoder(Function<BigInteger, String> decoder) {
        return new VPackAttributeNameCodec() {
            @Override
            public String decode(BigInteger unsignedId) {
                return decoder.apply(unsignedId);
            }

            @Override
            public BigInteger encode(String name) {
                return null;
            }
        };
    }

    private static void assertMessage(String expected, Throwing action) {
        StreamReadException failure = assertThrows(StreamReadException.class, action::run);
        org.junit.jupiter.api.Assertions.assertTrue(failure.getMessage().contains(expected),
                failure.getMessage());
    }

    @FunctionalInterface
    private interface Throwing {
        void run() throws Exception;
    }
}
