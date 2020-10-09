package com.fasterxml.jackson.databind.ser.jdk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import java.io.IOException;
import java.util.*;

public class CollectionSerializationTest
    extends BaseMapTest
{
    enum Key { A, B, C };

    // Field-based simple bean with a single property, "values"
    final static class CollectionBean
    {
        @JsonProperty // not required
        public Collection<Object> values;

        public CollectionBean(Collection<Object> c) { values = c; }
    }

    static class EnumMapBean
    {
        EnumMap<Key,String> _map;

        public EnumMapBean(EnumMap<Key,String> m)
        {
            _map = m;
        }

        public EnumMap<Key,String> getMap() { return _map; }
    }

    /**
     * Class needed for testing [JACKSON-220]
     */
    @SuppressWarnings("serial")
    @JsonSerialize(using=ListSerializer.class)    
    static class PseudoList extends ArrayList<String>
    {
        public PseudoList(String... values) {
            super(Arrays.asList(values));
        }
    }

    static class ListSerializer extends JsonSerializer<List<String>>
    {
        @Override
        public void serialize(List<String> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException
        {
            // just use standard List.toString(), output as JSON String
            gen.writeString(value.toString());
        }
    }

    // for [JACKSON-254], suppression of empty collections
    static class EmptyListBean {
        public List<String> empty = new ArrayList<String>();
    }

    static class EmptyArrayBean {
        public String[] empty = new String[0];
    }

    static class StaticListWrapper {
        protected List<String> list;

        public StaticListWrapper(String ... v) {
            list = new ArrayList<String>(Arrays.asList(v));
        }
        protected StaticListWrapper() { }
        
        public List<String> getList( ) { return list; }
        public void setList(List<String> l) { list = l; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final static ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testCollections() throws IOException
    {
        // Let's try different collections, arrays etc
        final int entryLen = 98;

        for (int type = 0; type < 4; ++type) {
            Object value;

            if (type == 0) { // first, array
                int[] ints = new int[entryLen];
                for (int i = 0; i < entryLen; ++i) {
                    ints[i] = Integer.valueOf(i);
                }
                value = ints;
            } else {
                Collection<Integer> c;

                switch (type) {
                case 1:
                    c = new LinkedList<Integer>();
                    break;
                case 2:
                    c = new TreeSet<Integer>(); // has to be ordered
                    break;
                default:
                    c = new ArrayList<Integer>();
                    break;
                }
                for (int i = 0; i < entryLen; ++i) {
                    c.add(Integer.valueOf(i));
                }
                value = c;
            }
            String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(value));
            
            // and then need to verify:
            JsonParser jp = new JsonFactory().createParser(json);
            assertToken(JsonToken.START_ARRAY, jp.nextToken());
            for (int i = 0; i < entryLen; ++i) {
                assertToken(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
                assertEquals(i, jp.getIntValue());
            }
            assertToken(JsonToken.END_ARRAY, jp.nextToken());
            jp.close();
        }
    }

    @SuppressWarnings("resource")
    public void testBigCollection() throws IOException {
        final int COUNT = 9999;
        ArrayList<Integer> value = new ArrayList<Integer>();
        for (int i = 0; i <= COUNT; ++i) {
            value.add(i);
        }

        byte[] data = MAPPER.writeValueAsBytes(value);
        JsonParser jp = MAPPER.getFactory().createParser(data);

        // and verify
        assertToken(JsonToken.START_ARRAY, jp.nextToken());
        for (int i = 0; i <= COUNT; ++i) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, jp.nextToken());
            assertEquals(i, jp.getIntValue());
        }
        assertToken(JsonToken.END_ARRAY, jp.nextToken());
        jp.close();
    }

    public void testEnumMap() throws IOException
    {
        EnumMap<Key,String> map = new EnumMap<Key,String>(Key.class);
        map.put(Key.B, "xyz");
        map.put(Key.C, "abc");
        // assuming EnumMap uses enum entry order, which I think is true...
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(map));
        assertEquals("{\"B\":\"xyz\",\"C\":\"abc\"}",json.trim());
    }

    // Test that checks that empty collections are properly serialized
    // when they are Bean properties
    @SuppressWarnings("unchecked")
    public void testEmptyBeanCollection() throws IOException
    {
        Collection<Object> x = new ArrayList<Object>();
        x.add("foobar");
        CollectionBean cb = new CollectionBean(x);
        Map<String,Object> result = writeAndMap(MAPPER, cb);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("values"));
        Collection<Object> x2 = (Collection<Object>) result.get("values");
        assertNotNull(x2);
        assertEquals(x, x2);
    }

    public void testNullBeanCollection()
        throws IOException
    {
        CollectionBean cb = new CollectionBean(null);
        Map<String,Object> result = writeAndMap(MAPPER, cb);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("values"));
        assertNull(result.get("values"));
    }

    @SuppressWarnings("unchecked")
    public void testEmptyBeanEnumMap() throws IOException
    {
        EnumMap<Key,String> map = new EnumMap<Key,String>(Key.class);
        EnumMapBean b = new EnumMapBean(map);
        Map<String,Object> result = writeAndMap(MAPPER, b);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("map"));
        // we deserialized to untyped, not back to bean, so:
        Map<Object,Object> map2 = (Map<Object,Object>) result.get("map");
        assertNotNull(map2);
        assertEquals(0, map2.size());
    }

    // Should also be able to serialize null EnumMaps as expected
    public void testNullBeanEnumMap() throws IOException
    {
        EnumMapBean b = new EnumMapBean(null);
        Map<String,Object> result = writeAndMap(MAPPER, b);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("map"));
        assertNull(result.get("map"));
    }

    public void testListSerializer() throws IOException
    {
        assertEquals(quote("[ab, cd, ef]"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new PseudoList("ab", "cd", "ef"))));
        assertEquals(quote("[]"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new PseudoList())));
    }

    @SuppressWarnings("deprecation")
    public void testEmptyListOrArray() throws IOException
    {
        // by default, empty lists serialized normally
        EmptyListBean list = new EmptyListBean();
        EmptyArrayBean array = new EmptyArrayBean();
        assertTrue(MAPPER.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
        assertEquals("{\"empty\":[]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(list)));
        assertEquals("{\"empty\":[]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(array)));

        // note: value of setting may be cached when constructing serializer, need a new instance
        ObjectMapper m = new TestVelocypackMapper();
        m.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(list)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(array)));
    }

    public void testStaticList() throws IOException
    {
        // First: au naturel
        StaticListWrapper w = new StaticListWrapper("a", "b", "c");
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(w));
        assertEquals(aposToQuotes("{'list':['a','b','c']}"), json);

        // but then with default typing
        ObjectMapper mapper = jsonMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance, DefaultTyping.NON_FINAL)
                .build();
        json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(w));
        assertEquals(aposToQuotes(String.format("['%s',{'list':['%s',['a','b','c']]}]",
                w.getClass().getName(), w.list.getClass().getName())), json);
    }
}
