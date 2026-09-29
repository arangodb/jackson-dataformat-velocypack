package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0083F0 {
private static final VPackFactory FACTORY = new VPackFactory();

    void veryLongIntRootValueUsesExactLiteralBcd() throws Exception {
        // -2 * 10^220, represented directly as a negative BCD root value.
        byte[] input = {
                (byte) 0xD0, 0x01, (byte) 0xDC, 0x00, 0x00, 0x00, 0x02
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertNotNull(parser.getBigIntegerValue());
            assertEquals(BigInteger.valueOf(-2L).multiply(BigInteger.TEN.pow(220)),
                    parser.getBigIntegerValue());
            assertNull(parser.nextToken());
        }
    }
private static void assertDuplicate(JsonParser parser) throws Exception {
        try (parser) {
            StreamReadException failure = assertThrows(StreamReadException.class,
                    () -> consume(parser));
            assertTrue(failure.getMessage().contains("Object property \"a\""),
                    failure.getMessage());
        }
    }
private static void consume(JsonParser parser) throws Exception {
        try (parser) {
            while (parser.nextToken() != null) { }
        }
    }
private static byte[] arrayOne() {
        return new byte[] { 0x02, 0x03, 0x31 };
    }
private static byte[][] duplicateDocuments() {
        byte[] first = object(new String[] { "a", "a" },
                new byte[][] { { 0x31 }, { 0x32 } });
        byte[] third = object(new String[] { "a", "b", "c", "a", "e" },
                new byte[][] { { 0x31 }, { 0x32 }, { 0x33 }, { 0x1A }, { 0x18 } });
        byte[] fourth = object(new String[] { "foo" }, new byte[][] {
                object(new String[] { "bar", "x", "a", "b", "a" }, new byte[][] {
                        array(array(object(new String[] { "x", "a" },
                                new byte[][] { { 0x33 }, { 0x31 } }))),
                        { 0x30 }, { 0x33 }, { 0x33 }, { 0x33 }
                })
        });
        byte[] fifth = array(first, object(new String[] { "b" }, new byte[][] {{ 0x33 }}),
                array(object(new String[] { "a" }, new byte[][] {{ 0x33 }})), first);
        byte[] sixth = object(new String[] { "b", "array", "ob" }, new byte[][] {
                { 0x31 },
                array(object(new String[] { "b" }, new byte[][] {{ 0x33 }})),
                object(new String[] { "b", "x", "y", "a", "a" }, new byte[][] {
                        { 0x34 }, { 0x30 }, { 0x33 }, { 0x1A }, { 0x31 }
                })
        });
        return new byte[][] { first, array(first), third, fourth, fifth, sixth };
    }
private static byte[] object(String[] names, byte[][] values) {
        byte[][] pairs = new byte[names.length][];
        long[] indexes = new long[names.length];
        int bodyOffset = 3;
        int offset = bodyOffset;
        for (int i = 0; i < names.length; ++i) {
            pairs[i] = VPackObjectParserTest.pair(names[i], values[i]);
            indexes[i] = offset;
            offset += pairs[i].length;
        }
        return VPackObjectParserTest.object(1, false,
                VPackObjectParserTest.body(pairs), indexes);
    }
private static byte[] array(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) bodyLength += value.length;
        int bodyStart = 3;
        int indexStart = bodyStart + bodyLength;
        byte[] result = new byte[indexStart + values.length];
        result[0] = 0x06;
        result[1] = (byte) result.length;
        result[2] = (byte) values.length;
        int offset = bodyStart;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        offset = bodyStart;
        for (int i = 0; i < values.length; ++i) {
            result[indexStart + i] = (byte) offset;
            offset += values[i].length;
        }
        return result;
    }
private static final class TrackingInput extends ByteArrayInputStream {
        boolean closed;

        TrackingInput(byte[] input) {
            super(input);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
private static final class ChunkedInput extends ByteArrayInputStream {
        ChunkedInput(byte[] input) {
            super(input);
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            return super.read(target, offset, Math.min(length, 1));
        }
    }

    void __invoke_veryLongIntRootValueUsesExactLiteralBcd() throws Exception {
        try {
            veryLongIntRootValueUsesExactLiteralBcd();
        } finally {
        }
    }

}
