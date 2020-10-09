package com.fasterxml.jackson.databind.jsontype.deftyping;

import java.util.*;

import static org.junit.Assert.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests to verify that Java/JSON scalar values (non-structured values)
 * are handled properly with respect to additional type information.
 */
public class TestDefaultForScalars
    extends BaseMapTest
{
    static class Jackson417Bean {
        public String foo = "bar";
        public java.io.Serializable bar = Integer.valueOf(13);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    static class Data {
        public long key;
    }

    // Basic `ObjectWrapper` from base uses delegating ctor, won't work well; should
    // figure out why, but until then we'll use separate impl
    protected static class ObjectWrapperForPoly {
        Object object;

        protected ObjectWrapperForPoly() { }
        public ObjectWrapperForPoly(final Object o) {
            object = o;
        }
        public Object getObject() { return object; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper DEFAULT_TYPING_MAPPER = jsonMapperBuilder()
        .activateDefaultTyping(NoCheckSubTypeValidator.instance)
        .build();

    /**
     * Unit test to verify that limited number of core types do NOT include
     * type information, even if declared as Object. This is only done for types
     * that JSON scalar values natively map to: String, Integer and Boolean (and
     * nulls never have type information)
     */
    public void testNumericScalars() throws Exception
    {
        // no typing for Integer, Double, yes for others
        assertEquals("[123]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Integer.valueOf(123) })));
        assertEquals("[[\"java.lang.Long\",37]]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Long.valueOf(37) })));
        assertEquals("[0.25]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Double.valueOf(0.25) })));
        assertEquals("[[\"java.lang.Float\",0.5]]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Float.valueOf(0.5f) })));
    }

    public void testDateScalars() throws Exception
    {
        long ts = 12345678L;
        assertEquals("[[\"java.util.Date\","+ts+"]]", com.fasterxml.jackson.VPackUtils.toJson(
                DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { new Date(ts) })));

        // Calendar is trickier... hmmh. Need to ensure round-tripping
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        String json = com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { c }));
        assertEquals("[[\""+c.getClass().getName()+"\","+ts+"]]", json);
        // and let's make sure it also comes back same way:
        Object[] result = DEFAULT_TYPING_MAPPER.readValue(json, Object[].class);
        assertEquals(1, result.length);
        assertTrue(result[0] instanceof Calendar);
        assertEquals(ts, ((Calendar) result[0]).getTimeInMillis());
    }

    public void testMiscScalars() throws Exception
    {
        // no typing for Strings, booleans
        assertEquals("[\"abc\"]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { "abc" })));
        assertEquals("[true,null,false]", com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Boolean[] { true, null, false })));
    }

    /**
     * Test for verifying that contents of "untyped" homogenous arrays are properly
     * handled,
     */
    public void testScalarArrays() throws Exception
    {
        ObjectMapper m = jsonMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT)
                .build();
        Object[] input = new Object[] {
                "abc", new Date(1234567), null, Integer.valueOf(456)
        };
        String json = com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(input));
        assertEquals("[\"abc\",[\"java.util.Date\",1234567],null,456]", json);

        // and should deserialize back as well:
        Object[] output = m.readValue(json, Object[].class);
        assertArrayEquals(input, output);
    }

    public void test417() throws Exception
    {
        Jackson417Bean input = new Jackson417Bean();
        String json = com.fasterxml.jackson.VPackUtils.toJson( DEFAULT_TYPING_MAPPER.writeValueAsBytes(input));
        Jackson417Bean result = DEFAULT_TYPING_MAPPER.readValue(json, Jackson417Bean.class);
        assertEquals(input.foo, result.foo);
        assertEquals(input.bar, result.bar);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    public void testDefaultTypingWithLong() throws Exception
    {
        Data data = new Data();
        data.key = 1L;
        Map<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("longInMap", 2L);
        mapData.put("longAsField", data);

        // Configure Jackson to preserve types
        ObjectMapper mapper = new TestVelocypackMapper();
        StdTypeResolverBuilder resolver = new StdTypeResolverBuilder();
        resolver.init(JsonTypeInfo.Id.CLASS, null);
        resolver.inclusion(JsonTypeInfo.As.PROPERTY);
        resolver.typeProperty("__t");
        mapper.setDefaultTyping(resolver);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Serialize
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(mapData));

        // Deserialize
        Map<?,?> result = mapper.readValue(json, Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

}
