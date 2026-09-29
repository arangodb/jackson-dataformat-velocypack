package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0084Fixture {

    void adjacentBinaryRootsAreSelfDelimitingWithoutJsonWhitespace() throws Exception {
        byte[] integerRoots = { 0x31, 0x1A };
        byte[] floatingRoots = {
                0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF8, 0x3F,
                0x19
        };

        assertIntegerRoots(new VPackFactory().createParser(integerRoots));
        assertIntegerRoots(new VPackFactory().createParser(
                new ByteArrayInputStream(integerRoots)));
        assertIntegerRoots(new VPackFactory().createParser(
                new ChunkedInput(integerRoots)));
        assertIntegerRoots(new VPackFactory().createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(integerRoots))));

        assertFloatingRoots(new VPackFactory().createParser(floatingRoots));
        assertFloatingRoots(new VPackFactory().createParser(
                new ByteArrayInputStream(floatingRoots)));
        assertFloatingRoots(new VPackFactory().createParser(
                new ChunkedInput(floatingRoots)));
        assertFloatingRoots(new VPackFactory().createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(floatingRoots))));
    }
private static void assertIntegerRoots(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertFloatingRoots(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(1.5d, parser.getDoubleValue());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static final class ChunkedInput extends ByteArrayInputStream {
        private ChunkedInput(byte[] input) {
            super(input);
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            return super.read(target, offset, Math.min(length, 1));
        }

    }

    void __invoke_adjacentBinaryRootsAreSelfDelimitingWithoutJsonWhitespace() throws Exception {
        try {
            adjacentBinaryRootsAreSelfDelimitingWithoutJsonWhitespace();
        } finally {
        }
    }

}
