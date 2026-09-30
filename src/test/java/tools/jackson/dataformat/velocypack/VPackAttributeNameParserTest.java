package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            assertTrue(parser.hasCurrentAttributeId());
            assertEquals(0L, parser.currentAttributeIdBits());
            assertEquals(VPackType.SMALL_INTEGER, parser.currentVPackType());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertNull(parser.currentAttributeId());
            assertEquals(false, parser.hasCurrentAttributeId());
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
    void rawLongCodecPathHandlesAllIdMarkerFormsAndClearsState() throws Exception {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) {
                throw new AssertionError("parser should use decode(long)");
            }
            @Override public String decode(long idBits) {
                return "id-" + Long.toUnsignedString(idBits);
            }
            @Override public BigInteger encode(String name) { return null; }
        };
        byte[][] keys = {
                { 0x30 }, { 0x39 }, { 0x28, 0x07 },
                { 0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF }
        };
        long[] ids = { 0L, 9L, 7L, -1L };
        VPackType[] physical = { VPackType.SMALL_INTEGER, VPackType.SMALL_INTEGER,
                VPackType.UNSIGNED_INTEGER, VPackType.UNSIGNED_INTEGER };
        for (int i = 0; i < keys.length; ++i) {
            byte[] input = VPackObjectParserTest.object(1, true,
                    concat(keys[i], new byte[] { 0x30 }), new long[] { 3 });
            try (VPackParser parser = (VPackParser) VPackFactory.builder()
                    .attributeNameCodec(codec).build().createParser(input)) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("id-" + Long.toUnsignedString(ids[i]), parser.currentName());
                assertTrue(parser.hasCurrentAttributeId());
                assertEquals(ids[i], parser.currentAttributeIdBits());
                assertEquals(new BigInteger(Long.toUnsignedString(ids[i])),
                        parser.currentAttributeId());
                assertEquals(physical[i], parser.currentVPackType());
                if (i == 3) assertEquals("18446744073709551615",
                        parser.currentAttributeId().toString());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertNull(parser.currentAttributeId());
                assertEquals(false, parser.hasCurrentAttributeId());
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                assertEquals(false, parser.hasCurrentAttributeId());
            }
        }
    }

    @Test
    void defaultLongOverloadDelegatesToBigIntegerCodec() throws Exception {
        VPackAttributeNameCodec codec = decoder(id -> id.equals(UINT64_MAX) ? "maximum" : null);
        byte[] input = VPackObjectParserTest.object(1, true,
                concat(key(UINT64_MAX), new byte[] { 0x30 }), new long[] { 3 });
        try (VPackParser parser = (VPackParser) VPackFactory.builder()
                .attributeNameCodec(codec).build().createParser(input)) {
            parser.nextToken();
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("maximum", parser.currentName());
            assertEquals(UINT64_MAX, parser.currentAttributeId());
            assertEquals(-1L, parser.currentAttributeIdBits());
            assertTrue(parser.hasCurrentAttributeId());
            parser.clearCurrentToken();
            assertNull(parser.currentAttributeId());
            assertEquals(false, parser.hasCurrentAttributeId());
        }
    }

    @Test
    void sortedIndexChecksResolvedAttributeNamesInUnsignedUtf8Order() {
        VPackAttributeNameCodec codec = decoder(id -> id.signum() == 0 ? "b"
                : id.equals(BigInteger.ONE) ? "a" : null);
        // Physical order is b then a; the deliberately physical-order index is invalid.
        byte[] body = concat(new byte[] { 0x30, 0x18, 0x31, 0x18 });
        byte[] input = VPackObjectParserTest.object(1, true, body, new long[] { 3, 5 });
        assertMessage("sorted object index is not in unsigned UTF-8 name order", () -> {
            try (VPackParser parser = (VPackParser) VPackFactory.builder()
                    .attributeNameCodec(codec).build().createParser(input)) {
                while (parser.nextToken() != null) { }
            }
        });
    }

    @Test
    void sortedIndexComparesStringAndResolvedAttributeNamesTogether() throws Exception {
        VPackAttributeNameCodec codec = decoder(id -> id.signum() == 0 ? "b" : null);
        byte[] body = concat(new byte[] { 0x30, 0x31 },
                VPackObjectParserTest.pair("a", new byte[] { 0x32 }));
        byte[] sorted = VPackObjectParserTest.object(1, true, body, new long[] { 5, 3 });
        VPackFactory factory = VPackFactory.builder().attributeNameCodec(codec).build();
        try (VPackParser parser = (VPackParser) factory.createParser(sorted)) {
            while (parser.nextToken() != null) { }
        }

        byte[] misSorted = VPackObjectParserTest.object(1, true, body, new long[] { 3, 5 });
        assertMessage("sorted object index is not in unsigned UTF-8 name order", () -> {
            try (VPackParser parser = (VPackParser) factory.createParser(misSorted)) {
                while (parser.nextToken() != null) { }
            }
        });
    }

    @Test
    void decodedNameMetadataAndScalarCoordinatesSurviveNestedAndSequenceTransitions()
            throws Exception {
        VPackAttributeNameCodec codec = decoder(id -> id.signum() == 0 ? "a" : null);
        byte[] nested = { 0x02, 0x04, 0x32, 0x33 };
        byte[] body = concat(key(0x30), new byte[] { 0x31 },
                new byte[] { 0x41, 'b' }, nested);
        byte[] object = VPackObjectParserTest.object(1, true, body, new long[] { 3, 5 });
        byte[] input = concat(object, new byte[] { 0x42, 'o', 'k', 0x37 });

        try (VPackParser parser = (VPackParser) VPackFactory.builder()
                .attributeNameCodec(codec).build().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(0L, parser.currentAttributeIdBits());
            assertEquals(VPackType.SMALL_INTEGER, parser.currentVPackType());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(VPackType.STRING, parser.currentVPackType());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());

            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("ok", parser.getText());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(7, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }

    @Test
    void repeatedAndCodecNamesDecodeWithoutCanonicalization() throws Exception {
        byte[] body = concat(new byte[] { 0x44, 's', 'a', 'm', 'e', 0x1A },
                key(0x30), new byte[] { 0x19 });
        byte[] input = VPackObjectParserTest.object(1, false, body,
                new long[] { 3, 9 });
        VPackFactory factory = VPackFactory.builder()
                .enable(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES)
                .attributeNameCodec(decoder(id -> id.signum() == 0 ? "same" : null))
                .build();

        try (VPackParser parser = (VPackParser) factory.createParser(
                ObjectReadContext.empty(), input)) {
            assertEquals(false, parser.willInternPropertyNames());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("same", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("same", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
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
    void wrapsCodecFailures() {
        byte[] input = VPackObjectParserTest.object(1, true,
                concat(key(0x30), new byte[] { 0x30 }), new long[] { 3 });
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
