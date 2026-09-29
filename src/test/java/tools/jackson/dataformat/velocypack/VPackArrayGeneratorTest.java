package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackArrayGeneratorTest {
    @Test
    void emitsEmptyAndEqualArraysOnlyAtEndAndIgnoresSizeHint() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartArray(null, 1000);
            generator.flush();
            assertEquals(0, out.size());
            generator.writeNumber(1);
            generator.writeNumber(2);
            assertEquals(0, out.size());
            generator.writeEndArray();
        }
        assertArrayEquals(new byte[] { 0x02, 0x04, 0x31, 0x32 }, out.toByteArray());

        out.reset();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartArray(null, 1000);
            generator.writeEndArray();
        }
        assertArrayEquals(new byte[] { 0x01 }, out.toByteArray());
    }

    @Test
    void emitsMinimalIndexedArrayWhenChildSizesDiffer() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS).build()
                .createGenerator(out)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeBoolean(true);
            generator.writeEndArray();
        }
        assertArrayEquals(new byte[] { 0x06, 0x07, 0x02, 0x31, 0x1A, 0x03, 0x04 },
                out.toByteArray());
    }

    @Test
    void widthTransitionsIncludeHeaderAndIndexOverhead() throws Exception {
        ByteArrayOutputStream equalOut = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(equalOut)) {
            generator.writeStartArray();
            for (int i = 0; i < 256; ++i) generator.writeNumber(1);
            generator.writeEndArray();
        }
        byte[] equalExpected = new byte[259];
        equalExpected[0] = 0x03;
        equalExpected[1] = 0x03;
        equalExpected[2] = 0x01;
        Arrays.fill(equalExpected, 3, equalExpected.length, (byte) 0x31);
        assertArrayEquals(equalExpected, equalOut.toByteArray());

        ByteArrayOutputStream indexedOut = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS).build()
                .createGenerator(indexedOut)) {
            generator.writeStartArray();
            for (int i = 0; i < 256; ++i) generator.writeNumber(1);
            generator.writeEndArray();
        }
        byte[] indexedExpected = new byte[773];
        indexedExpected[0] = 0x07;
        indexedExpected[1] = 0x05;
        indexedExpected[2] = 0x03;
        indexedExpected[3] = 0;
        indexedExpected[4] = 1;
        Arrays.fill(indexedExpected, 5, 261, (byte) 0x31);
        for (int i = 0; i < 256; ++i) {
            put(indexedExpected, 261 + 2 * i, 5L + i);
        }
        assertArrayEquals(indexedExpected, indexedOut.toByteArray());
    }

    @Test
    void nestedArraysTransferWithoutCopyingChildPayloadPerAncestor() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        for (int i = 0; i < 32; ++i) generator.writeStartArray();
        generator.writeNumber(1);
        for (int i = 0; i < 32; ++i) generator.writeEndArray();
        assertEquals(31L, generator.segmentTransfers());
        assertEquals(out.size(), generator.bytesCopied());
        generator.close();
    }

    @Test
    void inputBytesAreCopiedBeforeCallerMutation() throws Exception {
        byte[] input = { 1, 2, 3 };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartArray();
            generator.writeBinary(null, input, 0, input.length);
            input[0] = 99;
            generator.writeEndArray();
        }
        assertArrayEquals(new byte[] { 0x02, 0x07, (byte) 0xC0, 0x03, 1, 2, 3 },
                out.toByteArray());
    }

    @Test
    void wrongCloseAndEntryConstraintFailAsWriteErrors() throws Exception {
        VPackGenerator wrong = (VPackGenerator) new VPackFactory().createGenerator(
                new ByteArrayOutputStream());
        wrong.writeStartArray();
        assertThrows(StreamWriteException.class, wrong::writeEndObject);
        assertThrows(StreamWriteException.class, wrong::close);

        VPackGenerator limited = (VPackGenerator) VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxRootEntries(1).build())
                .build().createGenerator(new ByteArrayOutputStream());
        limited.writeStartArray();
        limited.writeNull();
        assertThrows(StreamConstraintsException.class, limited::writeNull);
        assertThrows(StreamConstraintsException.class, limited::close);
    }

    private static void put(byte[] target, int offset, long value) {
        target[offset] = (byte) value;
        target[offset + 1] = (byte) (value >>> 8);
    }
}
