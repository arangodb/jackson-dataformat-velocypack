package com.fasterxml.jackson.databind.deser;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * This unit test suite tests use of "value" Annotations;
 * annotations that define actual type (Class) to use for
 * deserialization.
 */
public class TestValueAnnotations
    extends BaseMapTest
{
    /*
    /**********************************************************
    /* Annotated root classes for @JsonDeserialize#as
    /**********************************************************
     */

    @JsonDeserialize(using=RootStringDeserializer.class)
    interface RootString {
        public String contents();
    }

    static class RootStringImpl implements RootString
    {
        final String _contents;

        public RootStringImpl(String x) { _contents = x; }

        @Override
        public String contents() { return _contents; }
        public String contents2() { return _contents; }
    }

    @JsonDeserialize(as=RootInterfaceImpl.class)
    interface RootInterface {
        public String getA();
    }

    static class RootInterfaceImpl implements RootInterface {
        public String a;

        public RootInterfaceImpl() { }

        @Override
        public String getA() { return a; }
    }

    @SuppressWarnings("serial")
    @JsonDeserialize(contentAs=RootStringImpl.class)
    static class RootMap extends HashMap<String,RootStringImpl> { }

    @SuppressWarnings("serial")
    @JsonDeserialize(contentAs=RootStringImpl.class)
    static class RootList extends LinkedList<RootStringImpl> { }

    @SuppressWarnings("serial")
    static class RootStringDeserializer
        extends StdDeserializer<RootString>
    {
        public RootStringDeserializer() { super(RootString.class); }

        @Override
        public RootString deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
        {
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                return new RootStringImpl(p.getText());
            }
            return (RootString) ctxt.handleUnexpectedToken(_valueClass, p);
        }
    }

    /*
    /**********************************************************
    /* Annotated helper classes for @JsonDeserialize#as
    /**********************************************************
     */

    /* Class for testing valid {@link JsonDeserialize} annotation
     * with 'as' parameter to define concrete class to deserialize to
     */
    final static class CollectionHolder
    {
        Collection<String> _strings;

        /* Default for 'Collection' would probably be ArrayList or so;
         * let's try to make it a TreeSet instead.
         */
        @JsonDeserialize(as=TreeSet.class)
        public void setStrings(Collection<String> s)
        {
            _strings = s;
        }
    }

    /* Another class for testing valid {@link JsonDeserialize} annotation
     * with 'as' parameter to define concrete class to deserialize to
     */
    final static class MapHolder
    {
        // Let's also coerce numbers into Strings here
        Map<String,String> _data;

        /* Default for 'Collection' would be HashMap,
         * let's try to make it a TreeMap instead.
         */
        @JsonDeserialize(as=TreeMap.class)
        public void setStrings(Map<String,String> s)
        {
            _data = s;
        }
    }

    /* Another class for testing valid {@link JsonDeserialize} annotation
     * with 'as' parameter, but with array
     */
    final static class ArrayHolder
    {
        String[] _strings;

        @JsonDeserialize(as=String[].class)
        public void setStrings(Object[] o)
        {
            // should be passed instances of proper type, as per annotation
            _strings = (String[]) o;
        }
    }

    /* Another class for testing broken {@link JsonDeserialize} annotation
     * with 'as' parameter; one with incompatible type
     */
    final static class BrokenCollectionHolder
    {
        @JsonDeserialize(as=String.class) // not assignable to Collection
        public void setStrings(Collection<String> s) { }
    }

    /*
    /**********************************************************
    /* Annotated helper classes for @JsonDeserialize.keyAs
    /**********************************************************
     */

    final static class StringWrapper
    {
        final String _string;

        public StringWrapper(String s) { _string = s; }
    }

    final static class MapKeyHolder
    {
        Map<Object, String> _map;

        @JsonDeserialize(keyAs=StringWrapper.class)
        public void setMap(Map<Object,String> m)
        {
            // type should be ok, but no need to cast here (won't matter)
            _map = m;
        }
    }

    final static class BrokenMapKeyHolder
    {
        // Invalid: Integer not a sub-class of String
        @JsonDeserialize(keyAs=Integer.class)
            public void setStrings(Map<String,String> m) { }
    }

    /*
    /**********************************************************
    /* Annotated helper classes for @JsonDeserialize#contentAs
    /**********************************************************
     */

    final static class ListContentHolder
    {
        List<?> _list;

        @JsonDeserialize(contentAs=StringWrapper.class)
        public void setList(List<?> l) {
            _list = l;
        }
    }

    // for [databind#2553]
    @SuppressWarnings("rawtypes")
    static class List2553 {
        @JsonDeserialize(contentAs = Item2553.class)
        public List items;
    }

    static class Item2553 {
        public String name;
    }

    final static class InvalidContentClass
    {
        /* Such annotation not allowed, since it makes no sense;
         * non-container classes have no contents to annotate (but
         * note that it is possible to first use @JsonDesiarialize.as
         * to mark Object as, say, a List, and THEN use
         * @JsonDeserialize.contentAs!)
         */
        @JsonDeserialize(contentAs=String.class)
            public void setValue(Object x) { }
    }

    final static class ArrayContentHolder
    {
        Object[] _data;

        @JsonDeserialize(contentAs=Long.class)
        public void setData(Object[] o)
        { // should have proper type, but no need to coerce here
            _data = o;
        }
    }

    final static class MapContentHolder
    {
        Map<Object,Object> _map;

        @JsonDeserialize(contentAs=Integer.class)
        public void setMap(Map<Object,Object> m)
        {
            _map = m;
        }
    }

    /*
    /**********************************************************
    /* Test methods for @JsonDeserialize#as
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newJsonMapper();

    public void testOverrideClassValid() throws Exception
    {
        CollectionHolder result = MAPPER.readValue
            ("{ \"strings\" : [ \"test\" ] }", CollectionHolder.class);

        Collection<String> strs = result._strings;
        assertEquals(1, strs.size());
        assertEquals(TreeSet.class, strs.getClass());
        assertEquals("test", strs.iterator().next());
    }

    public void testOverrideMapValid() throws Exception
    {
        // note: expecting conversion from number to String, as well
        MapHolder result = MAPPER.readValue
            ("{ \"strings\" :  { \"a\" : 3 } }", MapHolder.class);

        Map<String,String> strs = result._data;
        assertEquals(1, strs.size());
        assertEquals(TreeMap.class, strs.getClass());
        String value = strs.get("a");
        assertEquals("3", value);
    }

    public void testOverrideArrayClass() throws Exception
    {
        ArrayHolder result = MAPPER.readValue
            ("{ \"strings\" : [ \"test\" ] }", ArrayHolder.class);

        String[] strs = result._strings;
        assertEquals(1, strs.length);
        assertEquals(String[].class, strs.getClass());
        assertEquals("test", strs[0]);
    }

    public void testOverrideClassInvalid() throws Exception
    {
        // should fail due to incompatible Annotation
        try {
            BrokenCollectionHolder result = MAPPER.readValue
                (com.fasterxml.jackson.VPackUtils.toVPack("{ \"strings\" : [ ] }"), BrokenCollectionHolder.class);
            fail("Expected a failure, but got results: "+result);
        } catch (JsonMappingException jme) {
            verifyException(jme, "not subtype of");
        }
    }

    /*
    /**********************************************************
    /* Test methods for @JsonDeserialize#as used for root values
    /**********************************************************
     */

    public void testRootInterfaceAs() throws Exception
    {
        RootInterface value = MAPPER.readValue("{\"a\":\"abc\" }", RootInterface.class);
        assertTrue(value instanceof RootInterfaceImpl);
        assertEquals("abc", value.getA());
    }

    public void testRootInterfaceUsing() throws Exception
    {
        RootString value = MAPPER.readValue("\"xxx\"", RootString.class);
        assertTrue(value instanceof RootString);
        assertEquals("xxx", value.contents());
    }

    public void testRootListAs() throws Exception
    {
        RootMap value = MAPPER.readValue("{\"a\":\"b\"}", RootMap.class);
        assertEquals(1, value.size());
        Object v2 = value.get("a");
        assertEquals(RootStringImpl.class, v2.getClass());
        assertEquals("b", ((RootString) v2).contents());
    }

    public void testRootMapAs() throws Exception
    {
        RootList value = MAPPER.readValue("[ \"c\" ]", RootList.class);
        assertEquals(1, value.size());
        Object v2 = value.get(0);
        assertEquals(RootStringImpl.class, v2.getClass());
        assertEquals("c", ((RootString) v2).contents());
    }

    /*
    /**********************************************************
    /* Test methods for @JsonDeserialize#keyAs
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
	public void testOverrideKeyClassValid() throws Exception
    {
        MapKeyHolder result = MAPPER.readValue("{ \"map\" : { \"xxx\" : \"yyy\" } }", MapKeyHolder.class);
        Map<StringWrapper, String> map = (Map<StringWrapper,String>)(Map<?,?>)result._map;
        assertEquals(1, map.size());
        Map.Entry<StringWrapper, String> en = map.entrySet().iterator().next();

        StringWrapper key = en.getKey();
        assertEquals(StringWrapper.class, key.getClass());
        assertEquals("xxx", key._string);
        assertEquals("yyy", en.getValue());
    }

    public void testOverrideKeyClassInvalid() throws Exception
    {
        // should fail due to incompatible Annotation
        try {
            BrokenMapKeyHolder result = MAPPER.readValue
                (com.fasterxml.jackson.VPackUtils.toVPack("{ \"123\" : \"xxx\" }"), BrokenMapKeyHolder.class);
            fail("Expected a failure, but got results: "+result);
        } catch (JsonMappingException jme) {
            verifyException(jme, "not subtype of");
        }
    }

    /*
    /**********************************************************
    /* Test methods for @JsonDeserialize#contentAs
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
    public void testOverrideContentClassValid() throws Exception
    {
        ListContentHolder result = MAPPER.readValue("{ \"list\" : [ \"abc\" ] }", ListContentHolder.class);
        List<StringWrapper> list = (List<StringWrapper>)result._list;
        assertEquals(1, list.size());
        Object value = list.get(0);
        assertEquals(StringWrapper.class, value.getClass());
        assertEquals("abc", ((StringWrapper) value)._string);
    }

    public void testOverrideArrayContents() throws Exception
    {
        ArrayContentHolder result = MAPPER.readValue("{ \"data\" : [ 1, 2, 3 ] }",
                                                ArrayContentHolder.class);
        Object[] data = result._data;
        assertEquals(3, data.length);
        assertEquals(Long[].class, data.getClass());
        assertEquals(1L, data[0]);
        assertEquals(2L, data[1]);
        assertEquals(3L, data[2]);
    }

    public void testOverrideMapContents() throws Exception
    {
        MapContentHolder result = MAPPER.readValue("{ \"map\" : { \"a\" : 9 } }",
                                                MapContentHolder.class);
        Map<Object,Object> map = result._map;
        assertEquals(1, map.size());
        Object ob = map.values().iterator().next();
        assertEquals(Integer.class, ob.getClass());
        assertEquals(Integer.valueOf(9), ob);
    }

    // [databind#2553]
    public void testRawListTypeContentAs() throws Exception
    {
        List2553 list =  MAPPER.readValue("{\"items\": [{\"name\":\"item1\"}]}", List2553.class);
        assertEquals(1, list.items.size());
        Object value = list.items.get(0);
        assertEquals(Item2553.class, value.getClass());
        assertEquals("item1", ((Item2553) value).name);
    }
}
