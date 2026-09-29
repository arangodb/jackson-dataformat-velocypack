package tools.jackson.databind.node;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0494F2 {
private static final byte[] NULL_ROOT = VPackWireFixtureTest.hex("18");
private static final byte[] ARRAY_ROOT = VPackWireFixtureTest.hex(
            "13 12 19 14 0c 46 61 6e 73 77 65 72 28 2a 01 28 89 03");
private static final byte[] OBJECT_ROOT = VPackWireFixtureTest.hex(
            "14 39 46 61 6e 73 77 65 72 28 2a"
          + "46 6d 61 74 72 69 78 13 0f 31 2c 35 1c dc df 02 1a 43 2e 2e 2e 04"
          + "44 6d 69 73 63 14 12 45 76 61 6c 75 65 1b 00 00 00 00 00 00 d0 3f 01 03");
private static final byte[] NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 06 41 78 18 01");
private static final byte[] NULL_COVARIANCE = VPackWireFixtureTest.hex(
            "14 12 46 6f 62 6a 65 63 74 18 45 61 72 72 61 79 18 02");
private static final byte[] SCALED_DECIMAL = VPackWireFixtureTest.hex(
            "c8 04 fc ff ff ff 01 23 45 00");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NullNodeTest#testBasicsWithNullNode().
    void testBasicsWithNullNodeVpack() throws Exception {
        NullNode n = NullNode.instance;
        assertTrue(mapper.readTree(NULL_ROOT).isNull());
        assertFalse(n.isContainer());
        assertFalse(n.isBigDecimal());
        assertFalse(n.isBigInteger());
        assertFalse(n.isBinary());
        assertFalse(n.isBoolean());
        assertFalse(n.isPojo());
        assertFalse(n.isMissingNode());
        assertFalse(n.isNumber());
        assertFalse(n.canConvertToInt());
        assertFalse(n.canConvertToLong());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals("", n.asString());
        assertEquals("fallback", n.asString("fallback"));
        assertEquals(0, n.size());
        assertTrue(n.isEmpty());
        assertTrue(n.values().isEmpty());
        assertTrue(n.propertyNames().isEmpty());
        assertNotNull(n.path("xyz"));
        assertTrue(n.path("xyz").isMissingNode());
        assertFalse(n.has("field"));
        assertFalse(n.has(3));
        assertFalse(n.isNumber());
        assertFalse(n.canConvertToInt());
        assertFalse(n.canConvertToLong());
        assertFalse(n.canConvertToExactIntegral());
        assertEquals(-42, n.asInt(-42));
        assertEquals(12345678901L, n.asLong(12345678901L));
        assertEquals(-19.25, n.asDouble(-19.25));
        assertEquals(0, n.valueStream().count());
        assertEquals(0, n.propertyStream().count());
        n.forEachEntry((key, value) -> { throw new AssertionError("unexpected entry"); });
    }

    // Provenance: NullNodeTest#testNullEquality().
    void testNullEqualityVpack() throws Exception {
        JsonNode n = mapper.readTree(NULL_ROOT);
        assertTrue(n.isNull());
        assertEquals(n, new MyNull());
        assertEquals(new MyNull(), n);
        assertFalse(n.equals(null));
        assertFalse(n.equals("foo"));
    }

    // Provenance: NullNodeTest#testNullHandling().
    void testNullHandlingVpack() throws Exception {
        JsonNode n = mapper.readTree(NULL_ROOT);
        assertNotNull(n);
        assertTrue(n.isNull());
        assertFalse(n.isNumber());
        assertFalse(n.isString());
        assertEquals("", n.asString());
        assertEquals(n, NullNode.instance);

        ObjectNode root = (ObjectNode) mapper.readTree(NULL_PROPERTY);
        assertEquals(1, root.size());
        n = root.get("x");
        assertNotNull(n);
        assertTrue(n.isNull());
    }

    // Provenance: NullNodeTest#testNullHandlingCovariance().
    void testNullHandlingCovarianceVpack() throws Exception {
        CovarianceBean bean = mapper.readValue(NULL_COVARIANCE, CovarianceBean.class);
        assertNull(bean.object);
        assertNull(bean.array);
    }

    // Provenance: NullNodeTest#testNullSerialization().
    void testNullSerializationVpack() throws Exception {
        assertArrayEquals(NULL_ROOT, mapper.writeValueAsBytes(NullNode.instance));
    }
private JsonNode roundTrip(JsonNode input) throws Exception {
        return mapper.readTree(mapper.writeValueAsBytes(input));
    }
public static class CovarianceBean {
        public ObjectNode object;
        public ArrayNode array;
    }
@SuppressWarnings("serial")
    public static class MyNull extends NullNode { }

    void __invoke_testBasicsWithNullNodeVpack() throws Exception {
        try {
            testBasicsWithNullNodeVpack();
        } finally {
        }
    }


    void __invoke_testNullEqualityVpack() throws Exception {
        try {
            testNullEqualityVpack();
        } finally {
        }
    }


    void __invoke_testNullHandlingVpack() throws Exception {
        try {
            testNullHandlingVpack();
        } finally {
        }
    }


    void __invoke_testNullHandlingCovarianceVpack() throws Exception {
        try {
            testNullHandlingCovarianceVpack();
        } finally {
        }
    }


    void __invoke_testNullSerializationVpack() throws Exception {
        try {
            testNullSerializationVpack();
        } finally {
        }
    }

}
