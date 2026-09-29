package tools.jackson.databind.deser.jdk;

import java.util.UUID;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0283F1 {
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

    // Provenance: UUIDDeserializationTest#testUUID().
    void testUUIDVpack() throws Exception {
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_STRING, UUID.class));
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_STRING_UPPER, UUID.class));

        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BINARY, UUID.class));
    }

    // Provenance: UUIDDeserializationTest#testUUIDInvalid().
    void testUUIDInvalidVpack() {
        InvalidFormatException shortFailure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(UUID_INVALID_SHORT, UUID.class));
        assertTrue(shortFailure.getMessage().contains("UUID has to be represented by standard"));

        InvalidFormatException hexFailure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(UUID_INVALID_HEX, UUID.class));
        assertTrue(hexFailure.getMessage().contains("Non-hex character 'x'"));

        InvalidFormatException controlFailure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(UUID_INVALID_CONTROL, UUID.class));
        assertTrue(controlFailure.getMessage().contains("Non-hex character"));
        assertTrue(controlFailure.getMessage().contains("0x7f"));
    }

    // Provenance: UUIDDeserializationTest#testUUIDAux().
    void testUUIDAuxVpack() throws Exception {
        assertNull(MAPPER.readValue(new byte[] { 0x18 }, UUID.class));
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BINARY, UUID.class));
        assertEquals(TEST_UUID,
                MAPPER.readValue(MAPPER.writeValueAsBytes(TEST_UUID), UUID.class));
    }

    // Provenance: UUIDDeserializationTest#testCanDeserializeUUIDFromBase64(),
    // testCanDeserializeUUIDFromBase64WithoutPadding(), testCanDeserializeUUIDFromBase64Url(),
    // and testCanDeserializeUUIDFromBase64UrlWithoutPadding().
    void testUUIDBase64SpellingsVpack() throws Exception {
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BASE64, UUID.class));
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BASE64_NO_PADDING, UUID.class));
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BASE64, UUID.class));
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BASE64_NO_PADDING, UUID.class));
    }

    // Provenance: UUIDDeserializationTest#testUUIDAux() native byte[] branch.
    void testUUIDNativeBinaryVpack() throws Exception {
        assertEquals(TEST_UUID, MAPPER.readValue(UUID_BINARY, UUID.class));
    }
static class VoidBean {
        protected Void value;

        public Void getValue() {
            return null;
        }
    }

    void __invoke_testUUIDVpack() throws Exception {
        try {
            testUUIDVpack();
        } finally {
        }
    }


    void __invoke_testUUIDInvalidVpack() throws Exception {
        try {
            testUUIDInvalidVpack();
        } finally {
        }
    }


    void __invoke_testUUIDAuxVpack() throws Exception {
        try {
            testUUIDAuxVpack();
        } finally {
        }
    }


    void __invoke_testUUIDBase64SpellingsVpack() throws Exception {
        try {
            testUUIDBase64SpellingsVpack();
        } finally {
        }
    }


    void __invoke_testUUIDNativeBinaryVpack() throws Exception {
        try {
            testUUIDNativeBinaryVpack();
        } finally {
        }
    }

}
