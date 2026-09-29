package tools.jackson.core.unittest.read.loc;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0094Fixture {
private static final byte[] AS_INT_VALUES = {
            0x06, 0x21, 0x08,
            0x31, 0x3D,
            0x1B, (byte) 0xEC, 0x51, (byte) 0xB8, 0x1E, (byte) 0x85, (byte) 0xEB, 0x13, 0x40,
            0x1A, 0x19, 0x18,
            0x43, 0x2D, 0x31, 0x37,
            0x43, 0x66, 0x6F, 0x6F,
            0x03, 0x04, 0x05, 0x0E, 0x0F, 0x10, 0x11, 0x15
    };
private static final byte[] AS_BOOLEAN_VALUES = {
            0x06, 0x1F, 0x08,
            0x1A, 0x19, 0x18, 0x31, 0x30,
            0x44, 0x74, 0x72, 0x75, 0x65,
            0x45, 0x66, 0x61, 0x6C, 0x73, 0x65,
            0x43, 0x66, 0x6F, 0x6F,
            0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x0D, 0x13
    };
private static final byte[] AS_DOUBLE_VALUES = {
            0x06, 0x24, 0x08,
            0x31, 0x3D,
            0x1B, (byte) 0xEC, 0x51, (byte) 0xB8, 0x1E, (byte) 0x85, (byte) 0xEB, 0x13, 0x40,
            0x1A, 0x19, 0x18,
            0x46, 0x2D, 0x31, 0x37, 0x2E, 0x32, 0x35,
            0x43, 0x66, 0x6F, 0x6F,
            0x03, 0x04, 0x05, 0x0E, 0x0F, 0x10, 0x11, 0x18
    };
private static final byte[] STREAM_LOCATION_VALUES = {
            // compact array: 1, "hi", true
            0x13, 0x08, 0x31, 0x42, 0x68, 0x69, 0x1A, 0x03,
            // compact object: {"a": 1, "b": 16}
            0x14, 0x0A, 0x41, 0x61, 0x31, 0x41, 0x62, 0x28, 0x10, 0x02
    };

    void streamInitialLocation() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                new ByteArrayInputStream(new byte[] { 0x18 }))) {
            assertEquals(0L, parser.currentLocation().getByteOffset());
            assertEquals(-1L, parser.currentTokenLocation().getByteOffset());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        }
    }

    void streamLocationAtEndOfParse() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                new ByteArrayInputStream(STREAM_LOCATION_VALUES))) {
            while (parser.nextToken() != null) {
                // Consume every binary token, including both concatenated roots.
            }
            assertEquals((long) STREAM_LOCATION_VALUES.length,
                    parser.currentLocation().getByteOffset());
        }
    }

    void streamTokenLocations() throws Exception {
        long[] expectedOffsets = { 0, 2, 3, 6, 8, 8, 10, 12, 13, 15, 18 };
        try (JsonParser parser = new VPackFactory().createParser(
                new ByteArrayInputStream(STREAM_LOCATION_VALUES))) {
            for (long expectedOffset : expectedOffsets) {
                assertTrue(parser.nextToken() != null);
                assertEquals(expectedOffset, parser.currentTokenLocation().getByteOffset());
            }
            assertNull(parser.nextToken());
        }
    }
private static void forEachBinarySource(byte[] document, ParserAssertions assertions)
            throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(document)) {
            assertions.check(parser);
        }
        try (JsonParser parser = factory.createParser(new OneByteInputStream(document))) {
            assertions.check(parser);
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertions.check(parser);
        }
    }
@FunctionalInterface
    private interface ParserAssertions {
        void check(JsonParser parser) throws Exception;
    }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        private OneByteInputStream(byte[] input) {
            delegate = new ByteArrayInputStream(input);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            if (length == 0) {
                return 0;
            }
            int value = delegate.read();
            if (value < 0) {
                return -1;
            }
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_streamInitialLocation() throws Exception {
        try {
            streamInitialLocation();
        } finally {
        }
    }


    void __invoke_streamLocationAtEndOfParse() throws Exception {
        try {
            streamLocationAtEndOfParse();
        } finally {
        }
    }


    void __invoke_streamTokenLocations() throws Exception {
        try {
            streamTokenLocations();
        } finally {
        }
    }

}
