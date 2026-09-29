package tools.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.POJONode;
import tools.jackson.databind.node.StringNode;
import tools.jackson.databind.exc.JsonNodeException;
import tools.jackson.databind.util.RawValue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0472F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] NESTED_ARRAY = VPackWireFixtureTest.hex(
            "02 14 02 12 02 04 30 30 02 04 30 30 02 04 30 30 02 04 30 30");
private final ObjectMapper MAPPER = new VPackMapper();
private final JsonNodeFactory NODE_F = MAPPER.getNodeFactory();

    void testAddAllWithNullInCollectionVpack() {
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        array.addAll(Arrays.asList(null, JsonNodeFactory.instance.objectNode()));

        assertEquals(2, array.size());
        for (JsonNode node : array) {
            assertNotNull(node);
        }
        assertEquals(NullNode.getInstance(), array.get(0));
    }

    void testAddsVpack() {
        ArrayNode array = new ArrayNode(NODE_F);
        assertNotNull(array.addArray());
        assertNotNull(array.addObject());
        array.addPOJO("foobar");
        array.add(Integer.valueOf(1));
        array.add(Long.valueOf(1L));
        array.add((short) 13);
        array.add(Double.valueOf(0.5));
        array.add(Float.valueOf(0.5f));
        array.add(0.25f);
        array.add(new BigDecimal("0.2"));
        array.add(BigInteger.TEN);
        assertEquals(11, array.size());
        assertFalse(array.isEmpty());

        assertNotNull(array.insertArray(0));
        assertNotNull(array.insertObject(0));
        array.insertPOJO(2, "xxx");
        assertEquals(14, array.size());

        array.insert(0, BigInteger.ONE);
        array.insert(0, new BigDecimal("0.1"));
        assertEquals(16, array.size());
    }

    void testArrayNodeEqualityVpack() {
        ArrayNode first = new ArrayNode(null);
        ArrayNode second = new ArrayNode(null);

        assertTrue(first.equals(second));
        assertTrue(second.equals(first));

        first.add(StringNode.valueOf("Test"));
        assertFalse(first.equals(second));
        assertFalse(second.equals(first));

        second.add(StringNode.valueOf("Test"));
        assertTrue(first.equals(second));
        assertTrue(second.equals(first));
    }

    void testArrayReplaceVpack() {
        ArrayNode array = NODE_F.arrayNode();
        array.add("foo");

        JsonNode old = array.replace(0, NODE_F.booleanNode(true));
        assertEquals("foo", old.stringValue());

        old = array.replace(0, null);
        assertTrue(old.booleanValue());

        try {
            array.replace(100, null);
            fail("Should not pass");
        } catch (JsonNodeException e) {
            assertTrue(e.getMessage().contains("Illegal index 100, array size 1"),
                    e.getMessage());
        }
    }

    void testArraySetVpack() {
        ArrayNode array = NODE_F.arrayNode();
        for (int i = 0; i < 20; i++) {
            array.add("Original Data");
        }

        array.setPOJO(0, "MyPojo");
        array.setRawValue(1, new RawValue("MyRawValue"));
        array.setNull(2);
        array.set(3, (short) 155);
        array.set(4, Short.valueOf((short) 130));
        array.set(5, 132);
        array.set(6, Integer.valueOf(452));
        array.set(7, 4342L);
        array.set(8, Long.valueOf(154242L));
        array.set(9, 1.25f);
        array.set(10, Float.valueOf(242.25f));
        array.set(11, 132.25D);
        array.set(12, Double.valueOf(231.5D));
        array.set(13, BigDecimal.TEN);
        array.set(14, BigInteger.ONE);
        array.set(15, "Modified Data");
        array.set(16, true);
        array.set(17, Boolean.FALSE);
        array.set(18, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});

        assertEquals("MyPojo", ((POJONode) array.get(0)).getPojo());
        assertEquals(new RawValue("MyRawValue"), ((POJONode) array.get(1)).getPojo());
        assertEquals(NullNode.instance, array.get(2));
        assertEquals((short) 155, array.get(3).shortValue());
        assertEquals((short) 130, array.get(4).shortValue());
        assertEquals(132, array.get(5).intValue());
        assertEquals(452, array.get(6).intValue());
        assertEquals(4342L, array.get(7).longValue());
        assertEquals(154242L, array.get(8).longValue());
        assertEquals(1.25f, array.get(9).floatValue(), 0.00001f);
        assertEquals(242.25f, array.get(10).floatValue(), 0.00001f);
        assertEquals(132.25D, array.get(11).doubleValue(), 0.000000001d);
        assertEquals(231.5D, array.get(12).doubleValue(), 0.000000001d);
        assertEquals(0, BigDecimal.TEN.compareTo(array.get(13).decimalValue()));
        assertEquals(BigInteger.ONE, array.get(14).bigIntegerValue());
        assertEquals("Modified Data", array.get(15).stringValue());
        assertTrue(array.get(16).booleanValue());
        assertFalse(array.get(17).booleanValue());
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                array.get(18).binaryValue());

        assertEquals(20, array.size());
        for (int i = 0; i < 20; i++) {
            if (i <= 18) {
                if (i != 1) {
                    assertNotEquals("Original Data", array.get(i).asString());
                }
            } else {
                assertEquals("Original Data", array.get(i).stringValue());
            }
        }
    }

    void testArrayViaMapperVpack() throws Exception {
        JsonNode node = MAPPER.readTree(NESTED_ARRAY);
        assertNotNull(node);
        assertTrue(node.isArray());
        ArrayNode outer = (ArrayNode) node;
        assertEquals(1, outer.size());
        ArrayNode inner = (ArrayNode) outer.get(0);
        assertTrue(inner.isArray());
        assertEquals(4, inner.size());
    }

    void testDirectCreationVpack() {
        ArrayNode array = new ArrayNode(NODE_F);

        assertFalse(array.isBoolean());
        assertFalse(array.isString());
        assertFalse(array.isNumber());
        assertFalse(array.canConvertToInt());
        assertFalse(array.canConvertToLong());
        assertFalse(array.canConvertToExactIntegral());
        assertTrue(array.isArray());
        assertFalse(array.isObject());
        assertTrue(array.isContainer());
        assertEquals(array, array.deepCopy());
        assertFalse(array.values().iterator().hasNext());
        assertTrue(array.propertyNames().isEmpty());
        assertTrue(array.isEmpty());

        StringNode text = StringNode.valueOf("x");
        array.add(text);
        assertEquals(1, array.size());
        assertFalse(array.isEmpty());
        assertNotEquals(0, array.hashCode());
        assertTrue(array.values().iterator().hasNext());
        assertNull(array.get("x"));
        assertTrue(array.path("x").isMissingNode());
        assertFalse(array.optional("x").isPresent());
        assertSame(text, array.get(0));
        assertFalse(array.has("field"));
        assertFalse(array.hasNonNull("field"));
        assertTrue(array.has(0));
        assertTrue(array.hasNonNull(0));
        assertFalse(array.has(1));
        assertFalse(array.hasNonNull(1));

        array.add((JsonNode) null);
        assertEquals(2, array.size());
        assertTrue(array.get(1).isNull());
        assertTrue(array.has(1));
        assertFalse(array.hasNonNull(1));
        array.set(1, text);
        assertSame(text, array.get(1));
        array.set(0, (JsonNode) null);
        assertTrue(array.get(0).isNull());

        ArrayNode second = new ArrayNode(NODE_F);
        second.add("foobar");
        assertFalse(array.equals(second));
        array.addAll(second);
        assertEquals(3, array.size());
        assertFalse(array.get(0).isString());
        assertNotNull(array.remove(0));
        assertEquals(2, array.size());
        assertTrue(array.get(0).isString());
        assertNull(array.remove(-1));
        assertNull(array.remove(100));
        assertEquals(2, array.size());

        ArrayList<JsonNode> nodes = new ArrayList<>();
        nodes.add(text);
        array.addAll(nodes);
        assertEquals(3, array.size());
        assertNull(array.get(10000));
        assertNull(array.remove(-4));

        StringNode text2 = StringNode.valueOf("b");
        array.insert(0, text2);
        assertEquals(4, array.size());
        assertSame(text2, array.get(0));
        assertNotNull(array.addArray());
        assertEquals(5, array.size());
        array.addPOJO("foo");
        assertEquals(6, array.size());
        array.removeAll();
        assertEquals(0, array.size());
    }

    void testDirectCreation2Vpack() {
        ArrayList<JsonNode> list = new ArrayList<>();
        list.add(NODE_F.booleanNode(true));
        list.add(NODE_F.stringNode("foo"));
        ArrayNode array = new ArrayNode(NODE_F, list);
        assertEquals(2, array.size());
        assertTrue(array.get(0).isBoolean());
        assertTrue(array.get(1).isString());

        try {
            array.set(2, NODE_F.nullNode());
            fail("Should not pass");
        } catch (JsonNodeException e) {
            assertTrue(e.getMessage().contains("Illegal index"), e.getMessage());
        }
        array.insert(1, (String) null);
        assertEquals(3, array.size());
        assertTrue(array.get(0).isBoolean());
        assertTrue(array.get(1).isNull());
        assertTrue(array.get(2).isString());

        array.removeAll();
        array.insert(0, (JsonNode) null);
        assertEquals(1, array.size());
        assertTrue(array.get(0).isNull());
    }

    void testInsertsVpack() {
        ArrayNode array = NODE_F.arrayNode(16);
        array.insert(0, (short) 3);
        array.insert(0, Short.valueOf((short) 5));
        array.insert(0, (Short) null);
        array.insert(0, 1);
        array.insert(0, Integer.valueOf(2));
        array.insert(0, (Integer) null);
        array.insert(0, 1L);
        array.insert(0, Long.valueOf(2L));
        array.insert(0, (Long) null);
        array.insert(0, 0.0f);
        array.insert(0, Float.valueOf(1.0f));
        array.insert(0, (Float) null);
        array.insert(0, 0.5);
        array.insert(0, Double.valueOf(2.0));
        array.insert(0, (Double) null);
        array.insert(0, NODE_F.pojoNode("foobar"));
        assertEquals(16, array.size());
    }

    void testNullAddsVpack() {
        ArrayNode array = NODE_F.arrayNode(14);
        array.add((BigDecimal) null);
        array.add((BigInteger) null);
        array.add((Boolean) null);
        array.add((byte[]) null);
        array.add((Double) null);
        array.add((Float) null);
        array.add((Integer) null);
        array.add((JsonNode) null);
        array.add((Long) null);
        array.add((String) null);

        assertEquals(10, array.size());
        for (JsonNode node : array) {
            assertTrue(node.isNull());
        }
    }

    void testNullCheckingVpack() {
        ArrayNode first = JsonNodeFactory.instance.arrayNode();
        ArrayNode second = JsonNodeFactory.instance.arrayNode();
        first.addAll(second);
        assertEquals(0, first.size());
        assertEquals(0, second.size());
        second.addAll(first);
        assertEquals(0, first.size());
        assertEquals(0, second.size());
    }
static class Pojo3214 {
        JsonNode fromCtor = StringNode.valueOf("x");

        @com.fasterxml.jackson.annotation.JsonCreator
        public Pojo3214(@com.fasterxml.jackson.annotation.JsonProperty("node") JsonNode node) {
            fromCtor = node;
        }
    }

    void __invoke_testAddAllWithNullInCollectionVpack() throws Exception {
        try {
            testAddAllWithNullInCollectionVpack();
        } finally {
        }
    }


    void __invoke_testAddsVpack() throws Exception {
        try {
            testAddsVpack();
        } finally {
        }
    }


    void __invoke_testArrayNodeEqualityVpack() throws Exception {
        try {
            testArrayNodeEqualityVpack();
        } finally {
        }
    }


    void __invoke_testArrayReplaceVpack() throws Exception {
        try {
            testArrayReplaceVpack();
        } finally {
        }
    }


    void __invoke_testArraySetVpack() throws Exception {
        try {
            testArraySetVpack();
        } finally {
        }
    }


    void __invoke_testArrayViaMapperVpack() throws Exception {
        try {
            testArrayViaMapperVpack();
        } finally {
        }
    }


    void __invoke_testDirectCreationVpack() throws Exception {
        try {
            testDirectCreationVpack();
        } finally {
        }
    }


    void __invoke_testDirectCreation2Vpack() throws Exception {
        try {
            testDirectCreation2Vpack();
        } finally {
        }
    }


    void __invoke_testInsertsVpack() throws Exception {
        try {
            testInsertsVpack();
        } finally {
        }
    }


    void __invoke_testNullAddsVpack() throws Exception {
        try {
            testNullAddsVpack();
        } finally {
        }
    }


    void __invoke_testNullCheckingVpack() throws Exception {
        try {
            testNullCheckingVpack();
        } finally {
        }
    }

}
