package tools.jackson.databind.deser.jdk;

import java.util.UUID;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0283F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] UUID_STRING = VPackWireFixtureTest.hex(
            "64 61 37 31 36 31 63 36 63 2d 62 65 31 34 2d 34 61 65 33 2d 61 33 63 34 2d "
          + "66 32 37 63 32 62 32 63 36 65 66 34");
private static final byte[] UUID_STRING_UPPER = VPackWireFixtureTest.hex(
            "64 41 37 31 36 31 43 36 43 2d 42 45 31 34 2d 34 41 45 33 2d 41 33 43 34 2d "
          + "46 32 37 43 32 42 32 43 36 45 46 34");
private static final byte[] UUID_BASE64 = VPackWireFixtureTest.hex(
            "58 70 78 59 63 62 4c 34 55 53 75 4f 6a 78 50 4a 38 4b 79 78 75 39 41 3d 3d");
private static final byte[] UUID_BASE64_NO_PADDING = VPackWireFixtureTest.hex(
            "56 70 78 59 63 62 4c 34 55 53 75 4f 6a 78 50 4a 38 4b 79 78 75 39 41");
private static final byte[] UUID_BINARY = VPackWireFixtureTest.hex(
            "c0 10 a7 16 1c 6c be 14 4a e3 a3 c4 f2 7c 2b 2c 6e f4");
private static final byte[] UUID_INVALID_SHORT = VPackWireFixtureTest.hex(
            "45 61 62 63 64 65");
private static final byte[] UUID_INVALID_HEX = VPackWireFixtureTest.hex(
            "64 37 36 65 36 64 31 38 33 2d 35 66 36 38 2d 34 61 66 61 2d 62 39 34 61 2d "
          + "39 32 32 63 31 66 64 62 38 33 66 78");
private static final byte[] UUID_INVALID_CONTROL = VPackWireFixtureTest.hex(
            "64 37 36 65 36 64 31 38 33 2d 35 66 36 38 2d 34 61 66 61 2d 62 39 34 61 2d "
          + "39 32 32 63 31 66 64 62 38 33 66 7f");
private static final byte[] VOID_VALUE_NULL = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 18 01");
private static final UUID TEST_UUID = UUID.fromString(
            "a7161c6c-be14-4ae3-a3c4-f27c2b2c6ef4");

    // Provenance: ThreadGroupDeserTest#deserThreadGroupFromEmpty().
    void deserThreadGroupFromEmptyVpack() throws Exception {
        ThreadGroup result = MAPPER.readValue(EMPTY_OBJECT, ThreadGroup.class);
        assertNotNull(result);
    }

    // Provenance: ThreadGroupDeserTest#roundtripThreadGroup().
    void roundtripThreadGroupVpack() throws Exception {
        ThreadGroup original = new ThreadGroup("testTG");
        ThreadGroup result = MAPPER.readValue(MAPPER.writeValueAsBytes(original),
                ThreadGroup.class);
        assertNotNull(result);
        assertEquals(original.getName(), result.getName());
    }
static class VoidBean {
        protected Void value;

        public Void getValue() {
            return null;
        }
    }

    void __invoke_deserThreadGroupFromEmptyVpack() throws Exception {
        try {
            deserThreadGroupFromEmptyVpack();
        } finally {
        }
    }


    void __invoke_roundtripThreadGroupVpack() throws Exception {
        try {
            roundtripThreadGroupVpack();
        } finally {
        }
    }

}
