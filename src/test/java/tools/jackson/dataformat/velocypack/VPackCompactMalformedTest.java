package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackCompactMalformedTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void rejectsContinuationInTheWrongDirection() {
        assertMalformed(new byte[] { 0x13, 0x05, (byte) 0x80, 0x01, 0x01 });
    }

    @Test
    void rejectsOverlongReverseGroups() {
        byte[] input = new byte[] { 0x13, 0x0B, 0x00,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
                (byte) 0x80 };
        assertMalformed(input);
    }

    @Test
    void rejectsSuffixOverlappingLengthHeader() {
        assertMalformed(new byte[] { 0x13, 0x03, (byte) 0x80 });
    }

    @Test
    void rejectsMissingPairsAndBodyLeftoversBeforeEnd() {
        assertMalformed(new byte[] { 0x14, 0x05, 0x41, 0x61, 0x01 });
        assertMalformed(new byte[] { 0x14, 0x07, 0x41, 0x61, 0x31, 0x30, 0x01 });
        assertMalformed(new byte[] { 0x13, 0x05, 0x31, 0x32, 0x01 });
    }

    @Test
    void rejectsCountBodyMismatchAndTruncation() {
        assertMalformed(new byte[] { 0x13, 0x04, 0x31, 0x02 });
        assertMalformed(new byte[] { 0x14, 0x04, 0x41, 0x61 });
    }

    private void assertMalformed(byte[] input) {
        assertThrows(StreamReadException.class, () -> {
            try (JsonParser parser = factory.createParser(input)) {
                while (parser.nextToken() != null) { }
            }
        });
    }
}
