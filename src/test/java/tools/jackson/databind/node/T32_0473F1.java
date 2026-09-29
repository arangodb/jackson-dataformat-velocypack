package tools.jackson.databind.node;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.node.ArrayNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0473F1 {
private static final byte[] ELEMENT_ARRAY = VPackWireFixtureTest.hex(
            "02 0a 47 65 6c 65 6d 65 6e 74");
private static final byte[] NULL_FALSE_ARRAY = VPackWireFixtureTest.hex(
            "02 04 18 19");
private static final byte[] NUMBER_ARRAY = VPackWireFixtureTest.hex(
            "02 04 28 7b");
private static final byte[] REMOVE_ALL_ARRAY = VPackWireFixtureTest.hex(
            "06 0c 04 41 61 32 18 19 03 05 06 07");
private static final byte[] REMOVE_IF_ARRAY_213 = VPackWireFixtureTest.hex(
            "02 05 32 31 33");
private static final byte[] REMOVE_IF_ARRAY_123 = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] REMOVE_NULLS_ARRAY = VPackWireFixtureTest.hex(
            "06 0b 04 18 18 32 18 03 04 05 06");
private static final byte[] DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] DOUBLE_9999 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 80 87 c3 40");
private static final byte[] DOUBLE_NEGATIVE_28 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 3c c0");
private static final byte[] DOUBLE_275 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 06 40");
private static final byte[] DOUBLE_NEGATIVE_475 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 13 c0");
private final ObjectMapper MAPPER = new VPackMapper();
private final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .build();

    void fpConversionsToIntOkVpack() throws Exception {
        assertEquals(0, MAPPER.treeToValue(readDouble(DOUBLE_ZERO), Integer.class));
        assertEquals(9999, MAPPER.treeToValue(readDouble(DOUBLE_9999), Integer.class));
        assertEquals(-28, MAPPER.treeToValue(readDouble(DOUBLE_NEGATIVE_28), Integer.class));
    }

    void fpConversionsToIntFailVpack() throws Exception {
        try {
            STRICT_MAPPER.treeToValue(readDouble(DOUBLE_275), Integer.class);
            fail("Should have thrown an exception");
        } catch (InvalidFormatException e) {
            assertTrue(e.getMessage().contains("Cannot coerce Floating-point value (2.75)"),
                    e.getMessage());
        }

        try {
            STRICT_MAPPER.treeToValue(readDouble(DOUBLE_NEGATIVE_475), Integer.class);
            fail("Should have thrown an exception");
        } catch (InvalidFormatException e) {
            assertTrue(e.getMessage().contains("Cannot coerce Floating-point value (-4.75)"),
                    e.getMessage());
        }
    }
private ArrayNode readArray(byte[] bytes) throws Exception {
        return (ArrayNode) MAPPER.readTree(bytes);
    }
private JsonNode readDouble(byte[] bytes) throws Exception {
        return MAPPER.readTree(bytes);
    }

    void __invoke_fpConversionsToIntOkVpack() throws Exception {
        try {
            fpConversionsToIntOkVpack();
        } finally {
        }
    }


    void __invoke_fpConversionsToIntFailVpack() throws Exception {
        try {
            fpConversionsToIntFailVpack();
        } finally {
        }
    }

}
