package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    void asInt() throws Exception {
        forEachBinarySource(AS_INT_VALUES, parser -> {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9));

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1L, parser.getValueAsLong());
            assertEquals(1L, parser.getValueAsLong(-99));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-3L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(4L, parser.getValueAsLong());
            assertEquals(4L, parser.getValueAsLong(99));
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(1L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(0L, parser.getValueAsLong(27));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(-17L, parser.getValueAsLong());
            assertEquals(-17L, parser.getValueAsLong(3));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9));

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9));
        });
    }

    void asBoolean() throws Exception {
        forEachBinarySource(AS_BOOLEAN_VALUES, parser -> {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());
            assertTrue(parser.getValueAsBoolean(true));

            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertTrue(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertTrue(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(0, parser.getIntValue());
            assertFalse(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertTrue(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertFalse(parser.getValueAsBoolean());
            assertTrue(parser.getValueAsBoolean(true));
        });
    }

    void asLong() throws Exception {
        forEachBinarySource(AS_INT_VALUES, parser -> {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9L));

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1L, parser.getValueAsLong());
            assertEquals(1L, parser.getValueAsLong(-99L));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-3L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(4L, parser.getValueAsLong());
            assertEquals(4L, parser.getValueAsLong(99L));
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(1L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(0L, parser.getValueAsLong(27L));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(-17L, parser.getValueAsLong());
            assertEquals(-17L, parser.getValueAsLong(3L));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9L));

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(0L, parser.getValueAsLong());
            assertEquals(9L, parser.getValueAsLong(9L));
        });
    }

    void asDouble() throws Exception {
        forEachBinarySource(AS_DOUBLE_VALUES, parser -> {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(0.0, parser.getValueAsDouble());
            assertEquals(9.0, parser.getValueAsDouble(9.0));

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1.0, parser.getValueAsDouble());
            assertEquals(1.0, parser.getValueAsDouble(-99.0));
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(-3.0, parser.getValueAsDouble());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(4.98, parser.getValueAsDouble());
            assertEquals(4.98, parser.getValueAsDouble(12.5));
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(1.0, parser.getValueAsDouble());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertEquals(0.0, parser.getValueAsDouble());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            assertEquals(0.0, parser.getValueAsDouble());
            assertEquals(0.0, parser.getValueAsDouble(27.8));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(-17.25, parser.getValueAsDouble());
            assertEquals(-17.25, parser.getValueAsDouble(1.9));
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals(0.0, parser.getValueAsDouble());
            assertEquals(1.25, parser.getValueAsDouble(1.25));

            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(0.0, parser.getValueAsDouble());
            assertEquals(7.5, parser.getValueAsDouble(7.5));
        });
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

    void __invoke_asInt() throws Exception {
        try {
            asInt();
        } finally {
        }
    }


    void __invoke_asBoolean() throws Exception {
        try {
            asBoolean();
        } finally {
        }
    }


    void __invoke_asLong() throws Exception {
        try {
            asLong();
        } finally {
        }
    }


    void __invoke_asDouble() throws Exception {
        try {
            asDouble();
        } finally {
        }
    }

}
