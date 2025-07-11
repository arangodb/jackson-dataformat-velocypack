package com.fasterxml.jackson.databind.ser;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * Tests for verifying various issues with custom serializers.
 */
@SuppressWarnings("serial")
public class TestCustomSerializers extends BaseMapTest
{
    static class ElementSerializer extends JsonSerializer<Element>
    {
        @Override
        public void serialize(Element value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
            gen.writeString("element");
        }
    }
    
    @JsonSerialize(using = ElementSerializer.class)
    public static class ElementMixin {}

    public static class Immutable {
        protected int x() { return 3; }
        protected int y() { return 7; }
    }

    /**
     * Trivial simple custom escape definition set.
     */
    static class CustomEscapes extends CharacterEscapes
    {
        private final int[] _asciiEscapes;

        public CustomEscapes() {
            _asciiEscapes = standardAsciiEscapesForJSON();
            _asciiEscapes['a'] = 'A'; // to basically give us "\A" instead of 'a'
            _asciiEscapes['b'] = CharacterEscapes.ESCAPE_STANDARD; // too force "\u0062"
        }
        
        @Override
        public int[] getEscapeCodesForAscii() {
            return _asciiEscapes;
        }

        @Override
        public SerializableString getEscapeSequence(int ch) {
            return null;
        }
    }

    @JsonFormat(shape=JsonFormat.Shape.OBJECT)
    static class LikeNumber extends Number {
        private static final long serialVersionUID = 1L;

        public int x;

        public LikeNumber(int value) { x = value; }
        
        @Override
        public double doubleValue() {
            return x;
        }

        @Override
        public float floatValue() {
            return x;
        }

        @Override
        public int intValue() {
            return x;
        }

        @Override
        public long longValue() {
            return x;
        }
    }

    // for [databind#631]
    static class Issue631Bean
    {
        @JsonSerialize(using=ParentClassSerializer.class)
        public Object prop;

        public Issue631Bean(Object o) {
            prop = o;
        }
    }

    static class ParentClassSerializer
        extends StdScalarSerializer<Object>
    {
        protected ParentClassSerializer() {
            super(Object.class);
        }

        @Override
        public void serialize(Object value, JsonGenerator gen,
                SerializerProvider provider) throws IOException {
            Object parent = gen.getCurrentValue();
            String desc = (parent == null) ? "NULL" : parent.getClass().getSimpleName();
            gen.writeString(desc+"/"+value);
        }
    }

    static class UCStringSerializer extends StdScalarSerializer<String>
    {
        public UCStringSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                SerializerProvider provider) throws IOException {
            gen.writeString(value.toUpperCase());
        }
    }

    // IMPORTANT: must associate serializer via property annotations
    protected static class StringListWrapper
    {
        @JsonSerialize(contentUsing=UCStringSerializer.class)
        public List<String> list;

        public StringListWrapper(String... values) {
            list = new ArrayList<>();
            for (String value : values) {
                list.add(value);
            }
        }
    }

    // [databind#2475]
    static class MyFilter2475 extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
            // Ensure that "current value" remains pojo
            final JsonStreamContext ctx = jgen.getOutputContext();
            final Object curr = ctx.getCurrentValue();

            if (!(curr instanceof Item2475)) {
                throw new Error("Field '"+writer.getName()+"', context not that of `Item2475` instance");
            }
            super.serializeAsField(pojo, jgen, provider, writer);
        }
    }

    @JsonFilter("myFilter")
    @JsonPropertyOrder({ "id", "set" })
    public static class Item2475 {
        private Collection<String> set;
        private String id;

        public Item2475(Collection<String> set, String id) {
            this.set = set;
            this.id = id;
        }

        public Collection<String> getSet() {
            return set;
        }

        public String getId() {
            return id;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testCustomization() throws Exception
    {
        ObjectMapper objectMapper = new TestVelocypackMapper();
        objectMapper.addMixIn(Element.class, ElementMixin.class);
        Element element = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("el");
        byte[] bytes = objectMapper.writeValueAsBytes(element);
        assertEquals(com.fasterxml.jackson.VPackUtils.toJson(bytes), "\"element\"");
    }

    // [databind#87]: delegating serializer
    public void testDelegating() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addSerializer(new StdDelegatingSerializer(Immutable.class,
                new StdConverter<Immutable, Map<String,Integer>>() {
                    @Override
                    public Map<String, Integer> convert(Immutable value)
                    {
                        HashMap<String,Integer> map = new LinkedHashMap<String,Integer>();
                        map.put("x", value.x());
                        map.put("y", value.y());
                        return map;
                    }
        }));
        mapper.registerModule(module);
        assertEquals("{\"x\":3,\"y\":7}", com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new Immutable())));
    }

    public void testNumberSubclass() throws Exception
    {
        assertEquals(aposToQuotes("{'x':42}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new LikeNumber(42))));
    }

    public void testWithCurrentValue() throws Exception
    {
        assertEquals(aposToQuotes("{'prop':'Issue631Bean/42'}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new Issue631Bean(42))));
    }

    public void testWithCustomElements() throws Exception
    {
        // First variant that uses per-property override
        StringListWrapper wr = new StringListWrapper("a", null, "b");
        assertEquals(aposToQuotes("{'list':['A',null,'B']}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(wr)));

        // and then per-type registration
        
        SimpleModule module = new SimpleModule("test", Version.unknownVersion());
        module.addSerializer(String.class, new UCStringSerializer());
        ObjectMapper mapper = new TestVelocypackMapper()
                .registerModule(module);

        assertEquals(quote("FOOBAR"), com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes("foobar")));
        assertEquals(aposToQuotes("['FOO',null]"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new String[] { "foo", null })));

        List<String> list = Arrays.asList("foo", null);
        assertEquals(aposToQuotes("['FOO',null]"), com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(list)));

        Set<String> set = new LinkedHashSet<String>(Arrays.asList("foo", null));
        assertEquals(aposToQuotes("['FOO',null]"), com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(set)));
    }

    // [databind#2475]
    public void testIssue2475() throws Exception {
        SimpleFilterProvider provider = new SimpleFilterProvider().addFilter("myFilter", new MyFilter2475());
        ObjectWriter writer = MAPPER.writer(provider);

        // contents don't really matter that much as verification within filter but... let's
        // check anyway
        assertEquals(aposToQuotes("{'id':'ID-1','set':[]}"), com.fasterxml.jackson.VPackUtils.toJson(
                writer.writeValueAsBytes(new Item2475(new ArrayList<String>(), "ID-1"))));

        assertEquals(aposToQuotes("{'id':'ID-2','set':[]}"), com.fasterxml.jackson.VPackUtils.toJson(
                writer.writeValueAsBytes(new Item2475(new HashSet<String>(), "ID-2"))));
    }    

}
