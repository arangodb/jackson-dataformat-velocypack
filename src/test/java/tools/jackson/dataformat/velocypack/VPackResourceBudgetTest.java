package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamConstraintsException;

import static org.junit.jupiter.api.Assertions.*;

class VPackResourceBudgetTest {
    @Test
    void compressedNamesChargeTheirResolvedUtf8ExpansionPerOccurrence() {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) { return "wide"; }
            @Override public BigInteger encode(String name) { return BigInteger.ZERO; }
        };
        // Two one-byte IDs, each followed by a one-byte value; offsets are body-relative.
        byte[] wire = VPackObjectParserTest.object(1, true,
                new byte[] { 0x30, 0x31, 0x31, 0x32 }, new long[] { 2, 0 });
        VPackFactory limited = VPackFactory.builder().attributeNameCodec(codec)
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootNameBytes(7).build()).build();
        try (VPackParser parser = (VPackParser) limited.createParser(wire)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("wide", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }

    @Test
    void rootByteBudgetIncludesFinalContainerFraming() {
        VPackFactory factory = VPackFactory.builder().vpackWriteConstraints(
                VPackWriteConstraints.builder().maxRootValueBytes(2).build()).build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) factory.createGenerator(output);
        generator.writeStartArray();
        generator.writeNumber(1);
        assertThrows(StreamConstraintsException.class, generator::writeEndArray);
        assertEquals(0, output.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(RuntimeException.class, generator::close);
    }

    @Test
    void declaredCountAndFramingLengthsFailBeforePayloadConsumption() {
        // A declared UINT32 count with a one-byte root cannot possibly fit.
        byte[] hugeCount = { 0x09, 0x0A, 0, 0, 0, 0x7F, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF };
        VPackFactory small = VPackFactory.builder().vpackReadConstraints(
                VPackReadConstraints.builder().maxRootEntries(8).build()).build();
        try (VPackParser parser = (VPackParser) small.createParser(hugeCount)) {
            assertThrows(RuntimeException.class, () -> {
                while (parser.nextToken() != null) { }
            });
        }

        CountingInput input = new CountingInput(new byte[] { (byte) 0xC2, 0, 0, 1,
                1, 2, 3, 4 }); // binary declares 65536 payload bytes
        VPackFactory bounded = VPackFactory.builder().vpackReadConstraints(
                VPackReadConstraints.builder().maxRootValueBytes(16).build()).build();
        try (VPackParser parser = (VPackParser) bounded.createParser(input)) {
            assertThrows(StreamConstraintsException.class, parser::nextToken);
            assertEquals(4, input.consumed);
        }
    }

    @Test
    void remainingRootBudgetsResetBetweenConcatenatedRoots() throws Exception {
        VPackFactory factory = VPackFactory.builder().vpackReadConstraints(
                VPackReadConstraints.builder().maxRootValueBytes(1).maxRootEntries(0)
                        .maxRootNameBytes(0).build()).build();
        try (VPackParser parser = (VPackParser) factory.createParser(
                new ByteArrayInputStream(new byte[] { (byte) VPackConstants.NULL,
                        (byte) VPackConstants.NULL }))) {
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    @Test
    void syntheticWideArithmeticRejectsOverflowWithoutLargeAllocations() {
        assertThrows(RuntimeException.class,
                () -> VPackBounds.checkedAdd(Long.MAX_VALUE - 1, 2, "synthetic location"));
        assertThrows(RuntimeException.class,
                () -> VPackBounds.checkedMultiply(Long.MAX_VALUE, 8, "synthetic count width"));
        VPackLayout.FixedCandidate wide = VPackLayout.indexedCandidate(false, 8,
                Long.MAX_VALUE / 4, 1, null);
        assertEquals(8, wide.width());
        assertThrows(RuntimeException.class, () -> VPackLayout.selectIndexedWidth(
                false, Long.MAX_VALUE, 1, null));
    }

    @Test
    void charArrayNumberLimitRejectsBeforeTextualConversion() {
        VPackFactory factory = VPackFactory.builder().vpackWriteConstraints(
                VPackWriteConstraints.builder().maxNumberDigits(4).build()).build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) factory.createGenerator(output);
        char[] oversized = new char[100_000];
        java.util.Arrays.fill(oversized, '7');
        assertThrows(StreamConstraintsException.class,
                () -> generator.writeNumber(oversized, 0, oversized.length));
        assertEquals(0, output.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(RuntimeException.class, generator::close);
    }

    private static final class CountingInput extends InputStream {
        private final byte[] bytes;
        private int consumed;
        CountingInput(byte[] bytes) { this.bytes = bytes; }
        @Override public int read() { return consumed == bytes.length ? -1 : bytes[consumed++] & 0xFF; }
        @Override public int read(byte[] target, int offset, int length) throws IOException {
            if (consumed == bytes.length) return -1;
            int count = Math.min(length, bytes.length - consumed);
            System.arraycopy(bytes, consumed, target, offset, count);
            consumed += count;
            return count;
        }
    }
}
