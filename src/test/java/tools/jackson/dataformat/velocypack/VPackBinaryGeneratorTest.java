package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackBinaryGeneratorTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void usesMinimalBinaryLengthWidthsIncludingEmpty() throws Exception {
        byte[] one = new byte[1];
        byte[] twoFiftyFive = new byte[255];
        byte[] twoFiftySix = new byte[256];
        Arrays.fill(twoFiftyFive, (byte) 0x55);
        Arrays.fill(twoFiftySix, (byte) 0x66);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeBinary(new byte[0]);
            generator.writeBinary(one);
            generator.writeBinary(twoFiftyFive);
            generator.writeBinary(twoFiftySix);
        }
        byte[] actual = output.toByteArray();
        assertEquals((byte) 0xC0, actual[0]);
        assertEquals(0, actual[1]);
        assertEquals((byte) 0xC0, actual[2]);
        assertEquals(1, actual[3]);
        int second = 5;
        assertEquals((byte) 0xC0, actual[second]);
        assertEquals((byte) 0xFF, actual[second + 1]);
        int third = second + 257;
        assertEquals((byte) 0xC1, actual[third]);
        assertEquals(0, actual[third + 1]);
        assertEquals(1, actual[third + 2]);
    }

    @Test
    void byteSlicesAreCopiedAndStreamLengthsAreExact() throws Exception {
        byte[] source = { 9, 1, 2, 3, 8 };
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            generator.writeBinary(source, 1, 3);
            source[1] = 99;
            assertEquals(3, generator.writeBinary(new ByteArrayInputStream(
                    new byte[] { 4, 5, 6, 7 }), 3));
        }
        assertArrayEquals(new byte[] { (byte) 0xC0, 3, 1, 2, 3,
                (byte) 0xC0, 3, 4, 5, 6 }, output.toByteArray());
    }

    @Test
    void streamReadsToEofWithUnknownLengthAndRejectsEarlyEof() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(output)) {
            assertEquals(3, generator.writeBinary(new ZeroReadInputStream(
                    new byte[] { 1, 2, 3 }), -1));
        }
        assertArrayEquals(new byte[] { (byte) 0xC0, 3, 1, 2, 3 }, output.toByteArray());

        JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream());
        try {
            assertThrows(StreamWriteException.class,
                    () -> generator.writeBinary(new ByteArrayInputStream(new byte[] { 1 }), 2));
        } finally {
            try {
                generator.close();
            } catch (RuntimeException ignored) {
            }
        }
    }

    private static final class ZeroReadInputStream extends ByteArrayInputStream {
        ZeroReadInputStream(byte[] value) {
            super(value);
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            return 0;
        }
    }
}
