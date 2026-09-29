package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;

import org.junit.jupiter.api.Test;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.util.JsonParserDelegate;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

class VPackReviewGeneratorRegressionTest {
    @Test
    void malformedStructureCopyDiscardsTheEntireUncommittedRoot() {
        // Reserved child; invalid index after a valid child; truncated generic JSON.
        for (boolean exact : new boolean[] {false, true}) {
            for (int source = 0; source < 3; ++source) {
                try (JsonParser parser = source == 2 ? JsonMapper.builder().build().createParser("[1")
                        : new VPackFactory().createParser(hex(source == 0
                                ? "13 04 15 01" : "06 05 01 31 04"))) {
                    assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
                    generator.writeNumber(9); // Earlier committed root must remain intact.
                    generator.writeStartArray();
                    generator.writeNumber(1);
                    assertThrows(RuntimeException.class, () -> {
                        if (exact) generator.copyCurrentStructureExact(parser);
                        else generator.copyCurrentStructure(parser);
                    });
                    assertDiscarded(generator, out, hex("39"));
                }
            }
        }
    }

    @Test
    void failuresInGenericEventAccessorsAlsoPoisonTheTargetRoot() {
        for (boolean exact : new boolean[] {false, true}) {
            try (JsonParser source = new VPackFactory().createParser(hex("41 61"));
                    JsonParser parser = new JsonParserDelegate(source) {
                        @Override public boolean hasStringCharacters() { return false; }
                        @Override public String getString() {
                            throw new IllegalStateException("source accessor failed");
                        }
                    }) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
                generator.writeStartArray();
                generator.writeNumber(1);
                assertThrows(IllegalStateException.class, () -> {
                    if (exact) generator.copyCurrentEventExact(parser);
                    else generator.copyCurrentEvent(parser);
                });
                assertDiscarded(generator, out, new byte[0]);
            }
        }
    }

    @Test
    void binaryLengthsUseAllScalarWidthsNotOnlyContainerWidths() {
        for (int length : new int[] {65535, 65536}) {
            byte[] payload = new byte[length];
            Arrays.fill(payload, (byte) 0xA5);
            byte[] header = hex(length == 65535 ? "c1 ff ff" : "c2 00 00 01");
            byte[] expected = Arrays.copyOf(header, header.length + length);
            System.arraycopy(payload, 0, expected, header.length, length);
            for (int mode = 0; mode < 3; ++mode) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try (VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out)) {
                    if (mode == 0) generator.writeBinary(payload);
                    else assertEquals(length, generator.writeBinary(Base64Variants.getDefaultVariant(),
                            new ByteArrayInputStream(payload), mode == 1 ? length : -1));
                }
                assertArrayEquals(expected, out.toByteArray());
            }
        }
    }

    @Test
    void syntheticScalarWidthBoundariesCoverThreeFiveSixAndSevenBytes() {
        assertEquals(1, VPackNumbers.scalarLengthWidth(0));
        for (int width = 1; width < 8; ++width) {
            long boundary = 1L << (8 * width);
            assertEquals(width, VPackNumbers.scalarLengthWidth(boundary - 1));
            assertEquals(width + 1, VPackNumbers.scalarLengthWidth(boundary));
        }
        assertEquals(8, VPackNumbers.scalarLengthWidth(Long.MAX_VALUE));
    }

    @Test
    void knownBinaryInputIsNotConsumedWhenRemainingRootBudgetCannotFitIt() {
        VPackFactory factory = VPackFactory.builder().vpackWriteConstraints(
                VPackWriteConstraints.builder().maxRootValueBytes(10).build()).build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) factory.createGenerator(out);
        generator.writeStartArray();
        generator.writeBinary(new byte[4]); // Six retained bytes, not a fresh ten-byte budget.
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[3]);
        assertThrows(RuntimeException.class, () -> generator.writeBinary(
                Base64Variants.getDefaultVariant(), input, 3));
        assertEquals(3, input.available());
        assertDiscarded(generator, out, new byte[0]);
    }

    @Test
    void failedGeneratorDoesNotConsumeAnotherBinaryStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        assertThrows(RuntimeException.class, generator::writeEndObject);
        ByteArrayInputStream input = new ByteArrayInputStream(new byte[3]);
        assertThrows(RuntimeException.class, () -> generator.writeBinary(
                Base64Variants.getDefaultVariant(), input, 3));
        assertEquals(3, input.available());
        assertDiscarded(generator, out, new byte[0]);
    }

    @Test
    void bcdBudgetIncludesTheMarkerBeforeAllocatingTheEncodedValue() {
        assertThrows(RuntimeException.class, () -> VPackNumbers.encodeBcd(BigInteger.ONE, 0, 100, 6));
        assertArrayEquals(hex("c8 01 00 00 00 00 01"),
                VPackNumbers.encodeBcd(BigInteger.ONE, 0, 100, 7));
    }

    @Test
    void oversizeBcdIsRejectedBeforeDecimalStringConversion() {
        BigInteger enormous = new BigInteger("1" + "0".repeat(10000)) {
            @Override public String toString() {
                throw new AssertionError("decimal conversion must not run for an already oversized value");
            }
        };
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> VPackNumbers.encodeBcd(enormous, 0, 1000, 64L * 1024 * 1024));
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> VPackNumbers.encodeBcd(enormous, 0, 20000, 16));
        assertDoesNotThrow(() -> VPackNumbers.parseTextualNumber("-1.2e-3", 3));
        assertThrows(tools.jackson.core.exc.StreamConstraintsException.class,
                () -> VPackNumbers.validateTextualLength(10000, 3));
    }

    @Test
    void failedGeneratorDoesNotConsumeUnknownLengthReader() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        assertThrows(RuntimeException.class, generator::writeEndObject);
        StringReader input = new StringReader("abc");
        assertThrows(RuntimeException.class, () -> generator.writeString(input, -1));
        assertEquals('a', input.read());
        assertDiscarded(generator, out, new byte[0]);
    }

    private static void assertDiscarded(VPackGenerator generator, ByteArrayOutputStream out,
            byte[] committedRoots) {
        assertArrayEquals(committedRoots, out.toByteArray());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(RuntimeException.class, generator::writeNull);
        assertThrows(RuntimeException.class, generator::close);
        assertDoesNotThrow(generator::close);
        assertArrayEquals(committedRoots, out.toByteArray());
    }

    private static byte[] hex(String value) {
        return HexFormat.of().parseHex(value.replace(" ", ""));
    }
}
