package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0083F1 {
private static final VPackFactory FACTORY = new VPackFactory();

    void closeClearsCurrentTokenOnLiteralArrayRoot() throws Exception {
        byte[] input = { 0x02, 0x04, 0x31, 0x32 };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertNull(parser.currentToken());
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.close();
            assertNull(parser.currentToken());
        }
    }

    void autoCloseInputStreamCoversReaderCloseAssertions() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .enable(StreamReadFeature.AUTO_CLOSE_SOURCE).build();
        assertTrue(factory.isEnabled(StreamReadFeature.AUTO_CLOSE_SOURCE));

        TrackingInput input = new TrackingInput(arrayOne());
        JsonParser parser = factory.createParser(ObjectReadContext.empty(), input);
        assertFalse(input.closed);
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        parser.close();
        assertTrue(input.closed);

        input = new TrackingInput(arrayOne());
        parser = factory.createParser(ObjectReadContext.empty(), input);
        assertFalse(input.closed);
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertNull(parser.nextToken());
        assertTrue(input.closed);
    }

    void noAutoCloseInputStreamLeavesLiteralSourceOpen() throws Exception {
        ObjectReadContext context = new ObjectReadContext.Base() {
            @Override
            public int getStreamReadFeatures(int defaults) {
                return defaults & ~StreamReadFeature.AUTO_CLOSE_SOURCE.getMask();
            }
        };
        TrackingInput input = new TrackingInput(arrayOne());
        try (JsonParser parser = FACTORY.createParser(context, input)) {
            assertFalse(input.closed);
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
            assertFalse(input.closed);
        }
        assertFalse(input.closed);
    }

    void releaseContentBytesReturnsCompletedRootTailOnce() throws Exception {
        byte[] input = {
                0x02, 0x03, 0x31, 'f', 'o', 'o', 'b', 'a', 'r'
        };
        try (JsonParser parser = FACTORY.createParser(input)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            assertEquals(6, parser.releaseBuffered(out));
            assertArrayEquals(new byte[] { 'f', 'o', 'o', 'b', 'a', 'r' }, out.toByteArray());
            assertEquals(0, parser.releaseBuffered(new java.io.ByteArrayOutputStream()));
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

    void __invoke_closeClearsCurrentTokenOnLiteralArrayRoot() throws Exception {
        try {
            closeClearsCurrentTokenOnLiteralArrayRoot();
        } finally {
        }
    }


    void __invoke_autoCloseInputStreamCoversReaderCloseAssertions() throws Exception {
        try {
            autoCloseInputStreamCoversReaderCloseAssertions();
        } finally {
        }
    }


    void __invoke_noAutoCloseInputStreamLeavesLiteralSourceOpen() throws Exception {
        try {
            noAutoCloseInputStreamLeavesLiteralSourceOpen();
        } finally {
        }
    }


    void __invoke_releaseContentBytesReturnsCompletedRootTailOnce() throws Exception {
        try {
            releaseContentBytesReturnsCompletedRootTailOnce();
        } finally {
        }
    }

}
