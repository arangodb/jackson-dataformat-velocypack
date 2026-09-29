package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.node.BinaryNode;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0477F1 {
private static final byte[] FALSE = VPackWireFixtureTest.hex("19");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] DOUBLE_QUARTER = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 d0 3f");
private static final byte[] BINARY = VPackWireFixtureTest.hex(
            "c0 03 00 03 03");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final tools.jackson.databind.node.JsonNodeFactory NODES = MAPPER.getNodeFactory();

    // Provenance: JsonNodeConversionsTest#byteArrayToTree6059().
    void byteArrayToTree6059Vpack() {
        final byte[] bytes = { 1, 2, 3 };
        JsonNode node = MAPPER.valueToTree(bytes);

        BinaryNode binary = assertInstanceOf(BinaryNode.class, node);
        assertTrue(binary.isBinary());
        assertArrayEquals(bytes, binary.binaryValue());

        JsonNode node2 = MAPPER.valueToTree(new byte[] { 1, 2, 3 });
        assertEquals(node, node2);
        assertEquals(MAPPER.valueToTree(new byte[0]), MAPPER.valueToTree(new byte[0]));
    }

    // Provenance: JsonNodeConversionsTest#byteArrayViaTokenBufferToTree6059().
    void byteArrayViaTokenBufferToTree6059Vpack() {
        final byte[] bytes = { 7, 8, 9 };
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeBinary(bytes);

        JsonNode node = MAPPER.valueToTree(buffer);
        BinaryNode binary = assertInstanceOf(BinaryNode.class, node);
        assertArrayEquals(bytes, binary.binaryValue());
    }
private void assertBooleanValue(boolean expected, JsonNode node) {
        assertEquals(expected, node.booleanValue());
        assertEquals(expected, node.booleanValue(!expected));
        assertEquals(expected, node.booleanValueOpt().get());
    }
private void assertAsBoolean(boolean expected, JsonNode node) {
        assertEquals(expected, node.asBoolean());
        assertEquals(expected, node.asBoolean(!expected));
        assertEquals(expected, node.asBooleanOpt().get());
    }
private void assertFailBooleanValue(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::booleanValue);
        assertTrue(exception.getMessage().contains("cannot convert value"));
        assertTrue(exception.getMessage().contains("value type not boolean"));
        assertFalse(node.booleanValue(false));
        assertTrue(node.booleanValue(true));
        assertFalse(node.booleanValueOpt().isPresent());
    }
private void assertFailAsBoolean(JsonNode node) {
        JsonNodeException exception = assertThrows(JsonNodeException.class, node::asBoolean);
        assertTrue(exception.getMessage().contains("cannot coerce value"));
        assertTrue(exception.getMessage().contains("value type not coercible"));
        assertFalse(node.asBoolean(false));
        assertTrue(node.asBoolean(true));
        assertFalse(node.asBooleanOpt().isPresent());
    }

    void __invoke_byteArrayToTree6059Vpack() throws Exception {
        try {
            byteArrayToTree6059Vpack();
        } finally {
        }
    }


    void __invoke_byteArrayViaTokenBufferToTree6059Vpack() throws Exception {
        try {
            byteArrayViaTokenBufferToTree6059Vpack();
        } finally {
        }
    }

}
