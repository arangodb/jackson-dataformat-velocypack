package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0311F2 {
private static final Instant ISSUED_AT =
            Instant.ofEpochSecond(1_234_567_890L, 123_456_789L);
private static final byte[] BIG_DECIMAL_WRAPPER = VPackWireFixtureTest.hex(
            "14 19 45 76 61 6c 75 65 c8 0a f7 ff ff ff "
          + "01 23 45 67 89 01 23 45 67 89 01");
private static final byte[] NULL_INSTANT = VPackWireFixtureTest.hex(
            "14 0c 47 69 6e 73 74 61 6e 74 18 01");
private static final byte[] EMPTY_INSTANT = VPackWireFixtureTest.hex(
            "14 0c 47 69 6e 73 74 61 6e 74 40 01");
private static final byte[] NEGATIVE_ONE_SECONDS = VPackWireFixtureTest.hex(
            "d0 05 f7 ff ff ff 10 00 00 00 01");
private static final byte[] NEGATIVE_ONE_NANOSECOND = VPackWireFixtureTest.hex(
            "d0 01 f7 ff ff ff 01");

    // Provenance: InstantDeserializerNegative359Test#testDeserializationAsFloat04.
    void testDeserializationAsFloat04Vpack() throws Exception {
        Instant actual = new VPackMapper().readValue(NEGATIVE_ONE_SECONDS, Instant.class);
        assertEquals(Instant.ofEpochSecond(-1L, -1L), actual);
    }

    // Provenance: InstantDeserializerNegative359Test#testDeserializationAsFloat05.
    void testDeserializationAsFloat05Vpack() throws Exception {
        Instant actual = new VPackMapper().readValue(NEGATIVE_ONE_NANOSECOND, Instant.class);
        assertEquals(Instant.ofEpochSecond(0L, -1L), actual);
    }
public static class Wrapper307 {
        public Instant value;

        public Wrapper307() { }
    }

    void __invoke_testDeserializationAsFloat04Vpack() throws Exception {
        try {
            testDeserializationAsFloat04Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat05Vpack() throws Exception {
        try {
            testDeserializationAsFloat05Vpack();
        } finally {
        }
    }

}
