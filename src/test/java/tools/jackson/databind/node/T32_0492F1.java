package tools.jackson.databind.node;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0492F1 {
private static final byte[] NESTED_PROPERTY = VPackWireFixtureTest.hex(
            "14 1d 41 61 14 14 41 62 14 0b 41 63 28 0d 41 64 28 0e 02 "
            + "41 65 28 0f 02 41 66 28 10 02");
private static final byte[] SIMPLE_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 62 32 41 63 33 03");
private static final byte[] TWO_ELEMENT_ARRAY = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] NESTED_PATH = VPackWireFixtureTest.hex(
            "14 0b 41 61 14 06 41 62 31 01 01");
private static final byte[] NULL_PROPERTY_OBJECT = VPackWireFixtureTest.hex(
            "14 1a 48 6e 75 6c 6c 50 72 6f 70 18 4a 6e 6f 72 6d 61 6c 50 72 6f 70 "
            + "28 2a 02");
private static final byte[] SPECIAL_PROPERTY_OBJECT = VPackWireFixtureTest.hex(
            "14 15 43 61 2f 62 31 43 63 7e 64 32 46 6e 6f 72 6d 61 6c 33 03");
private static final byte[] EMPTY_NAME_OBJECT = VPackWireFixtureTest.hex(
            "14 20 40 49 65 6d 70 74 79 20 6b 65 79 46 6e 6f 72 6d 61 6c "
            + "4a 6e 6f 72 6d 61 6c 20 6b 65 79 02");
private static final byte[] POINTER_TREE = VPackWireFixtureTest.hex(
            "0b 38 01 45 49 6d 61 67 65 0b 2e 03 45 57 69 64 74 68 29 20 03 "
            + "46 48 65 69 67 68 74 29 58 02 43 49 44 73 06 11 04 28 74 29 af 03 "
            + "28 ea 29 89 97 03 05 08 0a 0c 16 03 03");
private static final byte[] LONG_KEY_SMALL = VPackWireFixtureTest.hex(
            "14 0a 43 31 32 33 29 c8 01 01");
private static final byte[] LONG_KEY_LARGE = VPackWireFixtureTest.hex(
            "14 12 4b 33 35 33 36 31 37 30 36 30 34 35 29 d2 04 01");
private static final byte[] EMPTY_NAME_VALUE = VPackWireFixtureTest.hex(
            "14 06 40 28 7b 01");
private final VPackMapper MAPPER = new VPackMapper();

    // Provenance: JsonPointerWithNodeTest#testIt().
    void testItVpack() throws Exception {
        JsonNode root = MAPPER.readTree(POINTER_TREE);

        assertSame(root, root.at(JsonPointer.compile("")));
        assertTrue(root.at(JsonPointer.compile("/")).isMissingNode());
        assertTrue(root.at(JsonPointer.compile("/Image")).isObject());
        assertEquals(800, root.at(JsonPointer.compile("/Image/Width")).asInt());
        assertEquals(600, root.at("/Image/Height").asInt());
        assertEquals(234, root.at(JsonPointer.compile("/Image/IDs/2")).asInt());
        assertTrue(root.at("/Image/Depth").isMissingNode());
        assertTrue(root.at("/Image/1").isMissingNode());
    }

    // Provenance: JsonPointerWithNodeTest#testLongNumbers().
    void testLongNumbersVpack() throws Exception {
        JsonNode smallKeyRoot = MAPPER.readTree(LONG_KEY_SMALL);
        assertEquals(456, smallKeyRoot.at("/123").asInt());

        JsonNode largeKeyRoot = MAPPER.readTree(LONG_KEY_LARGE);
        assertEquals(1234, largeKeyRoot.at("/35361706045").asInt());
    }

    // Provenance: JsonPointerWithNodeTest#testIssue2934().
    void testIssue2934Vpack() throws Exception {
        JsonNode root = MAPPER.readTree(EMPTY_NAME_VALUE);

        assertEquals(123, root.at("/").intValue());
        assertSame(root, root.at(""));
    }

    void __invoke_testItVpack() throws Exception {
        try {
            testItVpack();
        } finally {
        }
    }


    void __invoke_testLongNumbersVpack() throws Exception {
        try {
            testLongNumbersVpack();
        } finally {
        }
    }


    void __invoke_testIssue2934Vpack() throws Exception {
        try {
            testIssue2934Vpack();
        } finally {
        }
    }

}
