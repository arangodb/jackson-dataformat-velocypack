package tools.jackson.databind.node;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.POJONode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.ser.std.StdSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0502F2 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] REQUIRED_OBJECT = VPackWireFixtureTest.hex(
            "14 66 "
            + "44 64 61 74 61 "
            + "14 26 "
            + "47 70 72 69 6d 61 72 79 28 0f "
            + "46 76 65 63 74 6f 72 13 08 43 79 65 73 19 02 "
            + "48 6e 75 6c 6c 61 62 6c 65 18 03 "
            + "45 61 72 72 61 79 13 32 "
            + "1a "
            + "14 24 "
            + "48 6d 65 73 73 73 61 67 65 45 68 65 6c 6c 6f "
            + "45 76 61 6c 75 65 28 2a "
            + "44 6d 69 73 63 13 05 31 32 02 03 "
            + "18 "
            + "1b 00 00 00 00 00 00 d0 3f "
            + "04 02");
private static final byte[] REQUIRED_ARRAY = VPackWireFixtureTest.hex(
            "13 36 "
            + "1a "
            + "14 24 "
            + "44 64 61 74 61 "
            + "14 1c "
            + "47 70 72 69 6d 61 72 79 28 0f "
            + "46 76 65 63 74 6f 72 13 08 43 79 65 73 19 02 02 01 "
            + "1b 00 00 00 00 00 00 d0 3f "
            + "44 6c 61 73 74 "
            + "04");
private static final byte[] CUSTOM_SERIALIZER_RESULT = VPackWireFixtureTest.hex(
            "0b 27 01 44 64 61 74 61 "
            + "0b 1e 01 44 61 53 74 72 "
            + "54 54 68 65 20 76 61 6c 75 65 20 69 73 3a 20 48 65 6c 6c 6f 21 03 "
            + "03");
private final ObjectMapper mapper = new VPackMapper();
private POJONode pojo(Object value) throws Exception {
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        root.putPOJO("value", value);
        return (POJONode) root.get("value");
    }

    // Provenance: StringNodeTest#testBasics().
    void testBasicsVpack() throws Exception {
        assertNull(StringNode.valueOf(null));
        StringNode empty = (StringNode) mapper.readTree(VPackWireFixtureTest.hex("40"));
        assertEquals(StringNode.valueOf(""), empty);
        assertSame(StringNode.valueOf(""), empty);
        assertTrue(empty.equals(empty));
        assertFalse(empty.equals(null));
        assertFalse(empty.equals(new Object()));
        empty.hashCode();
        assertEquals(0, empty.size());
        assertTrue(empty.isEmpty());

        StringNode minusThree = (StringNode) mapper.readTree(
                VPackWireFixtureTest.hex("42 2d 33"));
        assertEquals(-3, minusThree.asInt());
        assertEquals(-3.0, minusThree.asDouble());

        long value = 127353264013893L;
        StringNode number = (StringNode) mapper.readTree(VPackWireFixtureTest.hex(
                "4f 31 32 37 33 35 33 32 36 34 30 31 33 38 39 33"));
        assertEquals(value, number.asLong());
        assertFalse(number.isNumber());
        assertFalse(number.canConvertToInt());
        assertFalse(number.canConvertToLong());
        assertFalse(number.canConvertToExactIntegral());

        StringNode text = (StringNode) mapper.readTree(
                VPackWireFixtureTest.hex("46 66 6f 6f 62 61 72"));
        assertFalse(text.isNumber());
        assertFalse(text.canConvertToInt());
        assertFalse(text.canConvertToLong());
        assertFalse(text.canConvertToExactIntegral());
        assertEquals(-42, text.asInt(-42));
        assertEquals(12345678901L, text.asLong(12345678901L));
        assertEquals(-19.25, text.asDouble(-19.25));
        assertEquals("foobar", text.asString());
        assertEquals("", empty.asString());

        StringNode trueNode = (StringNode) mapper.readTree(
                VPackWireFixtureTest.hex("44 74 72 75 65"));
        StringNode falseNode = (StringNode) mapper.readTree(
                VPackWireFixtureTest.hex("45 66 61 6c 73 65"));
        assertTrue(trueNode.asBoolean(true));
        assertTrue(trueNode.asBoolean(false));
        assertFalse(falseNode.asBoolean(true));
        assertFalse(falseNode.asBoolean(false));
        assertEquals(0, text.valueStream().count());
        assertEquals(0, text.propertyStream().count());
        text.forEachEntry((key, valueNode) -> fail("String node has no entries"));
    }
private static void assertRequiredAtFailure(JsonNode document, String fullPath,
            String mismatchPart) {
        try {
            JsonNode node = document.requiredAt(fullPath);
            fail("Should NOT pass: got node (" + node.getClass().getSimpleName() + ") -> {"
                    + node + "}");
        } catch (DatabindException e) {
            assertTrue(e.getMessage().contains("No node at '" + fullPath
                    + "' (unmatched part: '" + mismatchPart + "')"));
        }
    }
@JsonSerialize(using = CustomSer.class)
    public static class Data {
        public String aStr;
    }
public static class CustomSer extends StdSerializer<Data> {
        public CustomSer() {
            super(Data.class);
        }

        @Override
        public void serialize(Data value, JsonGenerator generator, SerializationContext provider) {
            String attrStr = (String) provider.getAttribute("myAttr");
            generator.writeStartObject();
            generator.writeStringProperty("aStr", "The value is: "
                    + (attrStr == null ? "NULL" : attrStr));
            generator.writeEndObject();
        }
    }

    void __invoke_testBasicsVpack() throws Exception {
        try {
            testBasicsVpack();
        } finally {
        }
    }

}
