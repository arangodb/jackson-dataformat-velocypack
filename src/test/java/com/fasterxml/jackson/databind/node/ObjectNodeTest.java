package com.fasterxml.jackson.databind.node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.ValueType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static com.fasterxml.jackson.TestUtils.isAtLeastVersion;

/**
 * Additional tests for {@link ObjectNode} container class.
 */
public class ObjectNodeTest
    extends BaseMapTest
{
    @JsonDeserialize(as = DataImpl.class)
    public interface Data {
    }

    public static class DataImpl implements Data
    {
        protected JsonNode root;

        @JsonCreator
        public DataImpl(JsonNode n) {
            root = n;
        }
        
        @JsonValue
        public JsonNode value() { return root; }
        
        /*
        public Wrapper(ObjectNode n) { root = n; }
        
        @JsonValue
        public ObjectNode value() { return root; }
        */
    }

    static class ObNodeWrapper {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public ObjectNode node;

        protected ObNodeWrapper() { }
        public ObNodeWrapper(ObjectNode n) {
            node = n;
        }
    }

    // [databind#941]
    static class MyValue
    {
        private final ObjectNode object;

        @JsonCreator
        public MyValue(ObjectNode object) { this.object = object; }

        @JsonValue
        public ObjectNode getObject() { return object; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = sharedMapper();

    public void testSimpleObject() throws Exception
    {
        String JSON = "{ \"key\" : 1, \"b\" : \"x\" }";
        JsonNode root = MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toVPack(JSON));

        // basic properties first:
        assertFalse(root.isValueNode());
        assertTrue(root.isContainerNode());
        assertFalse(root.isArray());
        assertTrue(root.isObject());
        assertEquals(2, root.size());
        assertFalse(root.isEmpty());

        Iterator<JsonNode> it = root.iterator();
        assertNotNull(it);
        assertTrue(it.hasNext());
        JsonNode n = it.next();
        assertNotNull(n);
        assertEquals(IntNode.valueOf(1), n);

        assertTrue(it.hasNext());
        n = it.next();
        assertNotNull(n);
        assertEquals(TextNode.valueOf("x"), n);

        assertFalse(it.hasNext());

        // Ok, then, let's traverse via extended interface
        ObjectNode obNode = (ObjectNode) root;
        Iterator<Map.Entry<String,JsonNode>> fit = obNode.fields();
        // we also know that LinkedHashMap is used, i.e. order preserved
        assertTrue(fit.hasNext());
        Map.Entry<String,JsonNode> en = fit.next();
        assertEquals("key", en.getKey());
        assertEquals(IntNode.valueOf(1), en.getValue());

        assertTrue(fit.hasNext());
        en = fit.next();
        assertEquals("b", en.getKey());
        assertEquals(TextNode.valueOf("x"), en.getValue());

        // Plus: we should be able to modify the node via iterator too:
        fit.remove();
        assertEquals(1, obNode.size());
        assertEquals(IntNode.valueOf(1), root.get("key"));
        assertNull(root.get("b"));
    }    
    // for [databind#346]
    public void testEmptyNodeAsValue() throws Exception
    {
        Data w = MAPPER.readValue("{}", Data.class);
        assertNotNull(w);
    }
    
    public void testBasics()
    {
        ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
        assertStandardEquals(n);
        assertTrue(n.isEmpty());
        
        assertFalse(n.elements().hasNext());
        assertFalse(n.fields().hasNext());
        assertFalse(n.fieldNames().hasNext());
        assertNull(n.get("a"));
        assertTrue(n.path("a").isMissingNode());

        TextNode text = TextNode.valueOf("x");
        assertSame(n, n.set("a", text));
        
        assertEquals(1, n.size());
        assertTrue(n.elements().hasNext());
        assertTrue(n.fields().hasNext());
        assertTrue(n.fieldNames().hasNext());
        assertSame(text, n.get("a"));
        assertSame(text, n.path("a"));
        assertNull(n.get("b"));
        assertNull(n.get(0)); // not used with objects

        assertFalse(n.has(0));
        assertFalse(n.hasNonNull(0));
        assertTrue(n.has("a"));
        assertTrue(n.hasNonNull("a"));
        assertFalse(n.has("b"));
        assertFalse(n.hasNonNull("b"));

        ObjectNode n2 = new ObjectNode(JsonNodeFactory.instance);
        n2.put("b", 13);
        assertFalse(n.equals(n2));
        n.setAll(n2);
        
        assertEquals(2, n.size());
        n.set("null", (JsonNode)null);
        assertEquals(3, n.size());
        // may be non-intuitive, but explicit nulls do exist in tree:
        assertTrue(n.has("null"));
        assertFalse(n.hasNonNull("null"));
        // should replace, not add
        n.put("null", "notReallNull");
        assertEquals(3, n.size());
        assertNotNull(n.remove("null"));
        assertEquals(2, n.size());

        Map<String,JsonNode> nodes = new HashMap<String,JsonNode>();
        nodes.put("d", text);
        n.setAll(nodes);
        assertEquals(3, n.size());

        n.removeAll();
        assertEquals(0, n.size());
    }

    public void testBigNumbers()
    {
        ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
        assertStandardEquals(n);
        BigInteger I = BigInteger.valueOf(3);
        BigDecimal DEC = new BigDecimal("0.1");

        n.put("a", DEC);
        n.put("b", I);

        assertEquals(2, n.size());

        assertTrue(n.path("a").isBigDecimal());
        assertEquals(DEC, n.get("a").decimalValue());
        assertTrue(n.path("b").isBigInteger());
        assertEquals(I, n.get("b").bigIntegerValue());
    }

    /**
     * Verify null handling
     */
    public void testNullChecking()
    {
        ObjectNode o1 = JsonNodeFactory.instance.objectNode();
        ObjectNode o2 = JsonNodeFactory.instance.objectNode();
        // used to throw NPE before fix:
        o1.setAll(o2);
        assertEquals(0, o1.size());
        assertEquals(0, o2.size());

        // also: nulls should be converted to NullNodes...
        o1.set("x", null);
        JsonNode n = o1.get("x");
        assertNotNull(n);
        assertSame(n, NullNode.instance);

        o1.put("str", (String) null);
        n = o1.get("str");
        assertNotNull(n);
        assertSame(n, NullNode.instance);

        o1.put("d", (BigDecimal) null);
        n = o1.get("d");
        assertNotNull(n);
        assertSame(n, NullNode.instance);

        o1.put("3", (BigInteger) null);
        n = o1.get("3");
        assertNotNull(3);
        assertSame(n, NullNode.instance);

        assertEquals(4, o1.size());
    }

    /**
     * Another test to verify [JACKSON-227]...
     */
    public void testNullChecking2()
    {
        ObjectNode src = MAPPER.createObjectNode();
        ObjectNode dest = MAPPER.createObjectNode();
        src.put("a", "b");
        dest.setAll(src);
    }

    public void testRemove()
    {
        ObjectNode ob = MAPPER.createObjectNode();
        ob.put("a", "a");
        ob.put("b", "b");
        ob.put("c", "c");
        assertEquals(3, ob.size());
        assertSame(ob, ob.without(Arrays.asList("a", "c")));
        assertEquals(1, ob.size());
        assertEquals("b", ob.get("b").textValue());
    }

    public void testRetain()
    {
        ObjectNode ob = MAPPER.createObjectNode();
        ob.put("a", "a");
        ob.put("b", "b");
        ob.put("c", "c");
        assertEquals(3, ob.size());
        assertSame(ob, ob.retain("a", "c"));
        assertEquals(2, ob.size());
        assertEquals("a", ob.get("a").textValue());
        assertNull(ob.get("b"));
        assertEquals("c", ob.get("c").textValue());
    }

    public void testValidWith() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(root)));
        JsonNode child = root.with("prop");
        assertTrue(child instanceof ObjectNode);
        assertEquals("{\"prop\":{}}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(root)));
    }

    public void testValidWithArray() throws Exception
    {
        JsonNode root = MAPPER.createObjectNode();
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(root)));
        ArrayNode child = root.withArray("arr");
        assertTrue(child instanceof ArrayNode);
        assertEquals("{\"arr\":[]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(root)));
    }

    public void testInvalidWith() throws Exception
    {
        JsonNode root = MAPPER.createArrayNode();
        try { // should not work for non-ObjectNode nodes:
            root.with("prop");
            fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "not of type", "ObjectNode");
        }
        // also: should fail of we already have non-object property
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.put("prop", 13);
        try { // should not work for non-ObjectNode nodes:
            root2.with("prop");
            fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "has value that is not");
        }
    }

    public void testInvalidWithArray() throws Exception
    {
        JsonNode root = MAPPER.createArrayNode();
        try { // should not work for non-ObjectNode nodes:
            root.withArray("prop");
            fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "not of type", "ObjectNode");
        }
        // also: should fail of we already have non-Array property
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.put("prop", 13);
        try { // should not work for non-ObjectNode nodes:
            root2.withArray("prop");
            fail("Expected exception");
        } catch (UnsupportedOperationException e) {
            verifyException(e, "has value that is not");
        }
    }

    public void testSetAll() throws Exception
    {
        ObjectNode root = MAPPER.createObjectNode();
        assertEquals(0, root.size());
        HashMap<String,JsonNode> map = new HashMap<String,JsonNode>();
        map.put("a", root.numberNode(1));
        root.setAll(map);
        assertEquals(1, root.size());
        assertTrue(root.has("a"));
        assertFalse(root.has("b"));

        map.put("b", root.numberNode(2));
        root.setAll(map);
        assertEquals(2, root.size());
        assertTrue(root.has("a"));
        assertTrue(root.has("b"));
        assertEquals(2, root.path("b").intValue());

        // Then with ObjectNodes...
        ObjectNode root2 = MAPPER.createObjectNode();
        root2.setAll(root);
        assertEquals(2, root.size());
        assertEquals(2, root2.size());

        root2.setAll(root);
        assertEquals(2, root.size());
        assertEquals(2, root2.size());

        ObjectNode root3 = MAPPER.createObjectNode();
        root3.put("a", 2);
        root3.put("c", 3);
        assertEquals(2, root3.path("a").intValue());
        root3.setAll(root2);
        assertEquals(3, root3.size());
        assertEquals(1, root3.path("a").intValue());
    }

    // [databind#237] (databind): support DeserializationFeature#FAIL_ON_READING_DUP_TREE_KEY
    public void testFailOnDupKeys() throws Exception
    {
        VPackBuilder builder = new VPackBuilder();
        builder.add(ValueType.OBJECT);
        builder.add("a", 1);
        builder.add("a", 2);
        builder.close();

        byte[] dupJsonBytes = builder.slice().toByteArray();

        // first: verify defaults:
        assertFalse(MAPPER.isEnabled(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY));
        ObjectNode root = (ObjectNode) MAPPER.readTree(dupJsonBytes);
        assertEquals(2, root.path("a").asInt());
        
        // and then enable checks:
        try {
            MAPPER.reader(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY).readTree(dupJsonBytes);
            fail("Should have thrown exception!");
        } catch (JsonMappingException e) {
            verifyException(e, "duplicate field 'a'");
        }
    }

    public void testFailOnDupNestedKeys() throws Exception
    {
        VPackBuilder builder = new VPackBuilder();
        builder.add(ValueType.OBJECT);
        builder.add("node", ValueType.OBJECT);
        builder.add("data", ValueType.ARRAY);
        builder.add(1);
        builder.add(2);
        builder.add(ValueType.OBJECT);
        builder.add("a", 3);
        builder.close();
        builder.add(ValueType.OBJECT);
        builder.add("foo", 1);
        builder.add("bar", 2);
        builder.add("foo", 3);
        builder.close();
        builder.close();
        builder.close();
        builder.close();
        byte[] bytes = builder.slice().toByteArray();

        try {
            MAPPER.readerFor(ObNodeWrapper.class)
                .with(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
                .readValue(bytes);
            fail("Should have thrown exception!");
        } catch (JsonMappingException e) {
            verifyException(e, "duplicate field 'foo'");
        }
    }

    public void testEqualityWrtOrder() throws Exception
    {
        ObjectNode ob1 = MAPPER.createObjectNode();
        ObjectNode ob2 = MAPPER.createObjectNode();

        // same contents, different insertion order; should not matter
        
        ob1.put("a", 1);
        ob1.put("b", 2);
        ob1.put("c", 3);

        ob2.put("b", 2);
        ob2.put("c", 3);
        ob2.put("a", 1);

        assertTrue(ob1.equals(ob2));
        assertTrue(ob2.equals(ob1));
    }

    public void testSimplePath() throws Exception
    {
        JsonNode root = MAPPER.readTree(com.fasterxml.jackson.VPackUtils.toVPack("{ \"results\" : { \"a\" : 3 } }"));
        assertTrue(root.isObject());
        JsonNode rnode = root.path("results");
        assertNotNull(rnode);
        assertTrue(rnode.isObject());
        assertEquals(3, rnode.path("a").intValue());
    }

    public void testNonEmptySerialization() throws Exception
    {
        ObNodeWrapper w = new ObNodeWrapper(MAPPER.createObjectNode()
                .put("a", 3));
        assertEquals("{\"node\":{\"a\":3}}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(w)));
        w = new ObNodeWrapper(MAPPER.createObjectNode());
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(w)));
    }

    public void testIssue941() throws Exception
    {
        ObjectNode object = MAPPER.createObjectNode();

        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(object));
//        System.out.println("json: "+json);

        ObjectNode de1 = MAPPER.readValue(json, ObjectNode.class);  // this works
//        System.out.println("Deserialized to ObjectNode: "+de1);
        assertNotNull(de1);

        MyValue de2 = MAPPER.readValue(json, MyValue.class);  // but this throws exception
//        System.out.println("Deserialized to MyValue: "+de2);
        assertNotNull(de2);
    }

    public void testSimpleMismatch() throws Exception
    {
        if(!isAtLeastVersion(2, 12)) return;

        ObjectMapper mapper = objectMapper();
        try {
            mapper.readValue("[ 1, 2, 3 ]", ObjectNode.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "from Array value (token `JsonToken.START_ARRAY`)");
        }
    }
}
