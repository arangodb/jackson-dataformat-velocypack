package tools.jackson.core.unittest.read.loc;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.List;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0095F2 {
private static final byte[] ARRAY = {
            // indexed array, length 11, count 3, body 10, 251, 3, offsets 3, 5, 7
            0x06, 0x0B, 0x03, 0x28, 0x0A, 0x28, (byte) 0xFB, 0x33, 0x03, 0x05, 0x07
    };
private static final byte[] OBJECT = {
            // compact object {f1:v1, f2:{f3:v3}, f4:[true,false], f5:5}
            0x14, 0x21,
            0x42, 0x66, 0x31, 0x42, 0x76, 0x31,
            0x42, 0x66, 0x32,
            0x14, 0x09, 0x42, 0x66, 0x33, 0x42, 0x76, 0x33, 0x01,
            0x42, 0x66, 0x34,
            0x13, 0x05, 0x1A, 0x19, 0x02,
            0x42, 0x66, 0x35, 0x35,
            0x04
    };

    void malformedVpackLocationsRemainBytePreciseAcrossBinarySources() throws Exception {
        List<InvalidVPack> cases = List.of(
                new InvalidVPack(new byte[] { (byte) 0xF0 }, 0L),
                new InvalidVPack(new byte[] { 0x42, 0x61 }, 2L),
                new InvalidVPack(new byte[] { 0x06, 0x05, 0x01 }, 3L),
                new InvalidVPack(new byte[] { 0x13, 0x01 }, 1L)
        );
        for (InvalidVPack invalid : cases) {
            assertFailure(new VPackFactory().createParser(invalid.bytes), invalid.offset);
            assertFailure(new VPackFactory().createParser(
                    new ByteArrayInputStream(invalid.bytes)), invalid.offset);
            DataInput input = new DataInputStream(new ByteArrayInputStream(invalid.bytes));
            assertFailureWithoutPortableLocation(new VPackFactory().createParser(
                    ObjectReadContext.empty(), input));
        }
    }
private static void assertFailure(JsonParser parser, long expectedOffset) {
        try (parser) {
            StreamReadException failure = assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) {
                    // Consume until the malformed VPack is reported.
                }
            });
            assertEquals(expectedOffset, failure.getLocation().getByteOffset());
        }
    }
private static void assertFailureWithoutPortableLocation(JsonParser parser) {
        try (parser) {
            assertThrows(StreamReadException.class, () -> {
                while (parser.nextToken() != null) {
                    // Consume until the malformed VPack is reported.
                }
            });
        }
    }
private static void assertLocation(TokenStreamLocation location, long expectedOffset) {
        assertEquals(expectedOffset, location.getByteOffset());
        assertEquals(-1L, location.getCharOffset());
        assertEquals(-1, location.getLineNr());
        assertEquals(-1, location.getColumnNr());
    }
private record InvalidVPack(byte[] bytes, long offset) { }

    void __invoke_malformedVpackLocationsRemainBytePreciseAcrossBinarySources() throws Exception {
        try {
            malformedVpackLocationsRemainBytePreciseAcrossBinarySources();
        } finally {
        }
    }

}
