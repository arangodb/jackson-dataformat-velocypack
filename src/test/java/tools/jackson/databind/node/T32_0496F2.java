package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.databind.node.JsonNodeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0496F2 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INT_NEGATIVE_90184 = VPackWireFixtureTest.hex(
            "22 b8 9f fe");
private static final byte[] LONG_12345678_SHIFTED = VPackWireFixtureTest.hex(
            "2f 00 00 00 00 4e 61 bc 00");
private static final byte[] BCD_TEN = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 10");
private static final byte[] BCD_QUARTER = VPackWireFixtureTest.hex(
            "c8 01 fe ff ff ff 25");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: ObjectNodeTest#testBasics().
    void testBasicsVpack() throws Exception {
        ObjectNode n = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        assertTrue(n.isEmpty());
        assertTrue(n.values().isEmpty());
        assertTrue(n.properties().isEmpty());
        assertTrue(n.propertyNames().isEmpty());
        assertNull(n.get("a"));
        assertFalse(n.optional("a").isPresent());
        assertTrue(n.path("a").isMissingNode());

        JsonNode text = mapper.getNodeFactory().textNode("x");
        assertSame(n, n.set("a", text));
        assertEquals(1, n.size());
        assertFalse(n.properties().isEmpty());
        assertFalse(n.propertyNames().isEmpty());
        assertFalse(n.values().isEmpty());
        assertSame(text, n.get("a"));
        assertSame(text, n.path("a"));
        assertNull(n.get("b"));
        assertNull(n.get(0));
        assertFalse(n.has(0));
        assertFalse(n.hasNonNull(0));
        assertTrue(n.has("a"));
        assertTrue(n.hasNonNull("a"));
        assertFalse(n.has("b"));
        assertFalse(n.hasNonNull("b"));

        ObjectNode n2 = mapper.createObjectNode();
        n2.put("b", 13);
        assertFalse(n.equals(n2));
        n.setAll(n2);
        assertEquals(2, n.size());
        n.set("null", (JsonNode) null);
        assertEquals(3, n.size());
        assertTrue(n.has("null"));
        assertFalse(n.hasNonNull("null"));
        n.put("null", "notReallNull");
        assertEquals(3, n.size());
        assertNotNull(n.remove("null"));
        assertEquals(2, n.size());

        Map<String, JsonNode> nodes = new HashMap<>();
        nodes.put("d", text);
        n.setAll(nodes);
        assertEquals(3, n.size());
        n.removeAll();
        assertEquals(0, n.size());
    }

    // Provenance: ObjectNodeTest#testBasicsPutSet().
    void testBasicsPutSetVpack() throws Exception {
        JsonNodeFactory f = mapper.getNodeFactory();
        ObjectNode root = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        JsonNode old = root.putIfAbsent("key", f.stringNode("foobar"));
        assertNull(old);
        assertEquals(1, root.size());
        old = root.putIfAbsent("key", f.numberNode(3));
        assertEquals(1, root.size());
        assertSame(old, root.get("key"));
        old = root.replace("key", f.numberNode(72));
        assertNotNull(old);
        assertEquals("foobar", old.stringValue());
    }

    // Provenance: ObjectNodeTest#testBigNumbers().
    void testBigNumbersVpack() throws Exception {
        ObjectNode n = (ObjectNode) mapper.readTree(EMPTY_OBJECT);
        BigInteger integer = BigInteger.valueOf(3);
        BigDecimal decimal = new BigDecimal("0.1");
        n.put("a", decimal);
        n.put("b", integer);
        assertEquals(2, n.size());
        assertTrue(n.path("a").isBigDecimal());
        assertEquals(decimal, n.get("a").decimalValue());
        assertTrue(n.path("b").isBigInteger());
        assertEquals(integer, n.get("b").bigIntegerValue());
        JsonNode roundTripped = mapper.readTree(mapper.writeValueAsBytes(n));
        assertEquals(decimal, roundTripped.get("a").decimalValue());
        assertEquals(integer, roundTripped.get("b").bigIntegerValue());
    }

    // Provenance: ObjectNodeTest#testEmptyNodeAsValue().
    void testEmptyNodeAsValueVpack() throws Exception {
        Data value = mapper.readValue(EMPTY_OBJECT, Data.class);
        assertNotNull(value);
        JsonNode root = ((DataImpl) value).value();
        assertNotNull(root);
        assertTrue(root.isObject());
        assertTrue(root.isEmpty());
    }
@JsonDeserialize(as = DataImpl.class)
    public interface Data {
    }
public static class DataImpl implements Data {
        private final JsonNode root;

        @JsonCreator
        public DataImpl(JsonNode root) {
            this.root = root;
        }

        @JsonValue
        public JsonNode value() {
            return root;
        }
    }

    void __invoke_testBasicsVpack() throws Exception {
        try {
            testBasicsVpack();
        } finally {
        }
    }


    void __invoke_testBasicsPutSetVpack() throws Exception {
        try {
            testBasicsPutSetVpack();
        } finally {
        }
    }


    void __invoke_testBigNumbersVpack() throws Exception {
        try {
            testBigNumbersVpack();
        } finally {
        }
    }


    void __invoke_testEmptyNodeAsValueVpack() throws Exception {
        try {
            testEmptyNodeAsValueVpack();
        } finally {
        }
    }

}
