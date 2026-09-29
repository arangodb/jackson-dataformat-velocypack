package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0311F1 {
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

    // Provenance: InstantDeserViaBigDecimal307Test#instantViaReadValue.
    void instantViaReadValueVpack() throws Exception {
        Wrapper307 deserialized = new VPackMapper().readValue(BIG_DECIMAL_WRAPPER,
                Wrapper307.class);
        assertEquals(ISSUED_AT, deserialized.value);
    }

    // Provenance: InstantDeserViaBigDecimal307Test#instantViaReadTree.
    void instantViaReadTreeVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JsonNode tree = mapper.readTree(BIG_DECIMAL_WRAPPER);
        Wrapper307 deserialized = mapper.treeToValue(tree, Wrapper307.class);
        assertEquals(ISSUED_AT, deserialized.value);
    }
public static class Wrapper307 {
        public Instant value;

        public Wrapper307() { }
    }

    void __invoke_instantViaReadValueVpack() throws Exception {
        try {
            instantViaReadValueVpack();
        } finally {
        }
    }


    void __invoke_instantViaReadTreeVpack() throws Exception {
        try {
            instantViaReadTreeVpack();
        } finally {
        }
    }

}
