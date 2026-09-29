package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0474F0 {
private static final byte[] DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] DOUBLE_9999 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 80 87 c3 40");
private static final byte[] DOUBLE_NEGATIVE_28 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 3c c0");
private static final byte[] DOUBLE_15 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] DOUBLE_NEGATIVE_625 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 19 c0");
private static final byte[] DOUBLE_425 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 40 45 40");
private static final byte[] ARRAY = VPackWireFixtureTest.hex(
            "13 08 31 43 74 77 6f 02");
private static final byte[] OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 61 31 01");
private static final byte[] TWO_ARRAY = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] ARRAY_OF_OBJECTS = VPackWireFixtureTest.hex(
            "13 20 14 0e 44 6e 61 6d 65 45 66 69 72 73 74 01"
          + "14 0f 44 6e 61 6d 65 46 73 65 63 6f 6e 64 01 02");
private static final byte[] ARRAY_OF_ARRAYS = VPackWireFixtureTest.hex(
            "13 0b 02 04 31 32 02 04 33 34 02");
private final VPackMapper MAPPER = VPackMapper.builder().build();
private final VPackMapper STRICT_MAPPER = VPackMapper.builder()
            .disable(tools.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .build();

    // Provenance: DoubleNodeToInt5309Test#fpConversionsToLongOk().
    void fpConversionsToLongOkVpack() throws Exception {
        assertEquals(0L, MAPPER.treeToValue(readDouble(DOUBLE_ZERO), Long.class));
        assertEquals(9999L, MAPPER.treeToValue(readDouble(DOUBLE_9999), Long.class));
        assertEquals(-28L, MAPPER.treeToValue(readDouble(DOUBLE_NEGATIVE_28), Long.class));
    }

    // Provenance: DoubleNodeToInt5309Test#fpConversionsToLongFail().
    void fpConversionsToLongFailVpack() throws Exception {
        try {
            STRICT_MAPPER.treeToValue(readDouble(DOUBLE_15), Long.class);
            fail("Should have thrown an exception");
        } catch (InvalidFormatException e) {
            assertTrue(e.getMessage().contains("Cannot coerce Floating-point value (1.5)"),
                    e.getMessage());
        }

        try {
            STRICT_MAPPER.treeToValue(readDouble(DOUBLE_NEGATIVE_625), Long.class);
            fail("Should have thrown an exception");
        } catch (InvalidFormatException e) {
            assertTrue(e.getMessage().contains("Cannot coerce Floating-point value (-6.25)"),
                    e.getMessage());
        }
    }
private JsonNode readDouble(byte[] bytes) throws Exception {
        return MAPPER.readTree(bytes);
    }
private JsonNode[] scalarNodes() throws Exception {
        return new JsonNode[] {
            MAPPER.readTree(VPackWireFixtureTest.hex("1a")),
            MAPPER.readTree(VPackWireFixtureTest.hex("19")),
            MAPPER.readTree(VPackWireFixtureTest.hex("32")),
            MAPPER.readTree(VPackWireFixtureTest.hex("28 2a")),
            MAPPER.readTree(DOUBLE_425),
            MAPPER.getNodeFactory().numberNode(BigInteger.TEN),
            MAPPER.getNodeFactory().numberNode(BigDecimal.valueOf(12.5)),
            MAPPER.readTree(VPackWireFixtureTest.hex("44 74 65 73 74")),
            MAPPER.readTree(VPackWireFixtureTest.hex("c0 03 00 00 00")),
            MAPPER.readTree(VPackWireFixtureTest.hex("18")),
            MAPPER.readTree(VPackWireFixtureTest.hex("40")),
            MAPPER.getNodeFactory().missingNode(),
            MAPPER.getNodeFactory().rawValueNode(new RawValue("abc")),
            MAPPER.getNodeFactory().pojoNode(new AtomicInteger(1))
        };
    }
private void assertFailAsArrayFor(JsonNode node) {
        JsonNodeException e = assertThrows(JsonNodeException.class, node::asArray);
        assertTrue(e.getMessage().contains("asArray()"));
        assertTrue(e.getMessage().contains("ArrayNode"));
        assertFalse(node.asArrayOpt().isPresent());
    }
private void assertFailAsObjectFor(JsonNode node) {
        JsonNodeException e = assertThrows(JsonNodeException.class, node::asObject);
        assertTrue(e.getMessage().contains("asObject()"));
        assertTrue(e.getMessage().contains("ObjectNode"));
        assertFalse(node.asObjectOpt().isPresent());
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        static final NoCheckSubTypeValidator instance = new NoCheckSubTypeValidator();
    }

    void __invoke_fpConversionsToLongOkVpack() throws Exception {
        try {
            fpConversionsToLongOkVpack();
        } finally {
        }
    }


    void __invoke_fpConversionsToLongFailVpack() throws Exception {
        try {
            fpConversionsToLongFailVpack();
        } finally {
        }
    }

}
