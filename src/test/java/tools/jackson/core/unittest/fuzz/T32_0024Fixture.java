package tools.jackson.core.unittest.fuzz;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0024Fixture {
private static final BigInteger BIG_NUMBER =
            new BigInteger("32222222222222222222222");
private static final byte[] BIG_NUMBER_THEN_INVALID_ROOT = {
            (byte) 0xC8, 0x0C, 0x00, 0x00, 0x00, 0x00,
            0x03, 0x22, 0x22, 0x22, 0x22, 0x22, 0x22,
            0x22, 0x22, 0x22, 0x22, 0x22, 0x00
    };

    void bigNumberByteArrayRemainsExactBeforeMalformedNextRoot() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(BIG_NUMBER_THEN_INVALID_ROOT)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BIG_NUMBER, parser.getBigIntegerValue());
            assertThrows(StreamReadException.class, parser::nextToken);
        }
    }

    void bigNumberOneByteStreamRemainsExactBeforeMalformedNextRoot() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(
                oneByteAtATime(BIG_NUMBER_THEN_INVALID_ROOT))) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(BIG_NUMBER, parser.getBigIntegerValue());
            assertThrows(StreamReadException.class, parser::nextToken);
        }
    }
private static InputStream oneByteAtATime(byte[] input) {
        return new ByteArrayInputStream(input) {
            @Override
            public int read(byte[] buffer, int offset, int length) {
                return super.read(buffer, offset, Math.min(length, 1));
            }
        };
    }

    void __invoke_bigNumberByteArrayRemainsExactBeforeMalformedNextRoot() throws Exception {
        try {
            bigNumberByteArrayRemainsExactBeforeMalformedNextRoot();
        } finally {
        }
    }


    void __invoke_bigNumberOneByteStreamRemainsExactBeforeMalformedNextRoot() throws Exception {
        try {
            bigNumberOneByteStreamRemainsExactBeforeMalformedNextRoot();
        } finally {
        }
    }

}
