package tools.jackson.databind.node;

import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0491F0 {
private static final byte[] POINTER_ROOT = VPackWireFixtureTest.hex(
            "0b 10 01 45 6e 75 6d 73 7e 02 06 28 2a 20 9d 03");
private static final byte[] ARRAY_NUMBERS = VPackWireFixtureTest.hex(
            "02 0a 28 0a 28 14 28 1e 28 28");
private static final byte[] CHAINED_OBJECT = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 31 41 62 32 41 63 33 41 64 34 03 06 09 0c");
private static final byte[] ONE_PROPERTY = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] NESTED_VALUES_ARRAY = VPackWireFixtureTest.hex(
            "0b 1b 01 44 64 61 74 61 0b 12 01 46 76 61 6c 75 65 73 "
            + "02 07 31 32 33 34 35 03 03");
private static final byte[] NESTED_OBJECT = VPackWireFixtureTest.hex(
            "0b 1f 01 45 6f 75 74 65 72 0b 15 01 45 69 6e 6e 65 72 "
            + "0b 0b 01 44 64 65 65 70 28 2a 03 03 03");
private static final byte[] FIRST_LAST = VPackWireFixtureTest.hex(
            "06 18 03 45 66 69 72 73 74 46 6d 69 64 64 6c 65 "
            + "44 6c 61 73 74 03 09 10");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NESTED_ARRAY = VPackWireFixtureTest.hex(
            "0b 11 01 45 61 72 72 61 79 02 07 31 32 33 34 35 03");
private static final byte[] VALUE_NODE_ROOT = VPackWireFixtureTest.hex(
            "0b 0e 01 41 61 0b 08 01 41 62 28 7b 03 03");
private static final byte[] MIXED_ROOT = VPackWireFixtureTest.hex(
            "0b 39 01 45 49 6d 61 67 65 0b 2f 03 45 57 69 64 74 68 "
            + "29 20 03 46 48 65 69 67 68 74 29 58 02 43 49 44 73 "
            + "06 12 04 28 74 29 af 03 29 ea 00 29 89 97 03 05 08 0b "
            + "0c 16 03 03");
private final VPackMapper MAPPER = new VPackMapper();

    // Provenance: JsonPointerCore1361Test#test1361().
    void test1361Vpack() throws Exception {
        ObjectNode root = (ObjectNode) MAPPER.readTree(POINTER_ROOT);
        JsonPointer pointer = JsonPointer.compile("/nums~/0");

        JsonNode element = root.at(pointer);
        assertFalse(element.isMissingNode());
        assertEquals(42, element.asInt());
    }

    void __invoke_test1361Vpack() throws Exception {
        try {
            test1361Vpack();
        } finally {
        }
    }

}
