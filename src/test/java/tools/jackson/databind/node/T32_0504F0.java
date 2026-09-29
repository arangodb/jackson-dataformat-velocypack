package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0504F0 {
private static final byte[] STRING_FIELD = VPackWireFixtureTest.hex(
            "14 1f 45 66 69 65 6c 64 55 7b 22 6e 61 6d 65 22 3a 22 4a 6f 68 6e 20 53 6d 69 74 68 22 7d 01");
private static final byte[] INCOMPLETE_OBJECT = VPackWireFixtureTest.hex("14");
private static final byte[] EOF_ROOT = VPackWireFixtureTest.hex(
            "14 1c 43 6b 65 79 31 44 6e 61 6d 65 43 78 79 7a 44 74 79 70 65 31 43 75 72 6c 18 04");
private static final byte[] MIXED_ROOT = VPackWireFixtureTest.hex(
            "14 11 44 6e 6f 64 65 14 06 41 61 33 01 41 78 39 02");
private static final byte[] MULTIPLE_ROOTS = VPackWireFixtureTest.hex(
            "28 0c 46 73 74 72 69 6e 67 02 05 31 32 33");
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] SAMPLE_ROOT = VPackWireFixtureTest.hex(
            "14 98 01 45 49 6d 61 67 65 14 8e 01 "
          + "45 57 69 64 74 68 29 20 03 "
          + "46 48 65 69 67 68 74 29 58 02 "
          + "45 54 69 74 6c 65 54 56 69 65 77 20 66 72 6f 6d 20 31 35 74 68 20 46 6c 6f 6f 72 "
          + "49 54 68 75 6d 62 6e 61 69 6c 14 41 "
          + "43 55 72 6c 66 68 74 74 70 3a 2f 2f 77 77 77 2e 65 78 61 6d 70 6c 65 2e 63 6f 6d 2f 69 6d 61 67 65 2f 34 38 31 39 38 39 39 34 33 "
          + "46 48 65 69 67 68 74 28 7d 45 57 69 64 74 68 43 31 30 30 03 "
          + "43 49 44 73 13 0d 28 74 29 af 03 28 ea 29 89 97 04 05 01");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: TreeDeserializationTest#testObjectNodeEquality().
    void testObjectNodeEqualityVpack() {
        ObjectNode first = mapper.createObjectNode();
        ObjectNode second = mapper.createObjectNode();

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));

        first.put("x", "Test");
        assertFalse(first.equals(second));
        assertFalse(second.equals(first));

        second.put("x", "Test");
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    // Provenance: TreeDeserializationTest#testReadFromString().
    void testReadFromStringVpack() throws Exception {
        JsonNode input = mapper.readTree(STRING_FIELD);
        JsonNode output = mapper.readTree(mapper.writeValueAsBytes(input));

        assertTrue(output.isObject());
        assertEquals(1, output.size());
        assertEquals("{\"name\":\"John Smith\"}", output.path("field").asString());
        assertNotNull(output.path("field"));
    }
public static class MixedBean {
        public int x;
        public JsonNode node;
    }

    void __invoke_testObjectNodeEqualityVpack() throws Exception {
        try {
            testObjectNodeEqualityVpack();
        } finally {
        }
    }


    void __invoke_testReadFromStringVpack() throws Exception {
        try {
            testReadFromStringVpack();
        } finally {
        }
    }

}
