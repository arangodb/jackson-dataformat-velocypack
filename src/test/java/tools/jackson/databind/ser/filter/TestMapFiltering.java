package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.ser.*;
import tools.jackson.databind.ser.jdk.MapProperty;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("serial")
public class TestMapFiltering extends DatabindTestUtil
{
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomOffset
    {
        public int value();
    }

    @JsonFilter("filterForMaps")
    static class FilteredBean extends LinkedHashMap<String,Integer> { }

    static class MapBean {
        @JsonFilter("filterX")
        @CustomOffset(1)
        public Map<String,Integer> values;

        public MapBean() {
            values = new LinkedHashMap<String,Integer>();
            values.put("a", 1);
            values.put("b", 5);
            values.put("c", 9);
        }
    }

    static class MapBeanNoOffset {
        @JsonFilter("filterX")
        public Map<String,Integer> values;

        public MapBeanNoOffset() {
            values = new LinkedHashMap<String,Integer>();
            values.put("a", 1);
            values.put("b", 2);
            values.put("c", 3);
        }
    }

    static class TestMapFilter implements PropertyFilter
    {
        @Override
        public PropertyFilter snapshot() {
            return this;
        }

        @Override
        public void serializeAsProperty(Object bean, JsonGenerator g,
                SerializationContext provider, PropertyWriter writer)
            throws Exception
        {
            String name = writer.getName();

            // sanity checks
            assertNotNull(writer.getType());
            assertEquals(name, writer.getFullName().getSimpleName());

            if (!"a".equals(name)) {
                return;
            }
            CustomOffset n = writer.findAnnotation(CustomOffset.class);
            int offset = (n == null) ? 0 : n.value();

            // 12-Jun-2017, tatu: With 2.9, `value` is the surrounding POJO, so
            //    need to do casting
            MapProperty prop = (MapProperty) writer;
            Integer old = (Integer) prop.getValue();
            prop.setValue(Integer.valueOf(offset + old.intValue()));

            writer.serializeAsProperty(bean, g, provider);
        }

        @Override
        public void serializeAsElement(Object elementValue, JsonGenerator jgen,
                SerializationContext prov, PropertyWriter writer)
                throws Exception {
            // not needed for testing
        }

        @Override

        public void depositSchemaProperty(PropertyWriter writer,
                JsonObjectFormatVisitor objectVisitor,
                SerializationContext provider) { }
    }

    // [databind#527]
    static class NoNullValuesMapContainer {
        @JsonInclude(content=JsonInclude.Include.NON_NULL)
        public Map<String,String> stuff = new LinkedHashMap<String,String>();

        public NoNullValuesMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }

    // [databind#527]
    @JsonInclude(content=JsonInclude.Include.NON_NULL)
    static class NoNullsStringMap extends LinkedHashMap<String,String> {
        public NoNullsStringMap add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    // [databind#527]
    @JsonInclude(content=JsonInclude.Include.NON_ABSENT)
    static class NoAbsentStringMap extends LinkedHashMap<String, AtomicReference<?>> {
        public NoAbsentStringMap add(String key, Object value) {
            put(key, new AtomicReference<Object>(value));
            return this;
        }
    }

    // [databind#527]
    @JsonInclude(content=JsonInclude.Include.NON_EMPTY)
    static class NoEmptyStringsMap extends LinkedHashMap<String,String> {
        public NoEmptyStringsMap add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    // [databind#497]: both Map AND contents excluded if empty
    static class Wrapper497 {
        @JsonInclude(content=JsonInclude.Include.NON_EMPTY,
                value=JsonInclude.Include.NON_EMPTY)
        public StringMap497 values;

        public Wrapper497(StringMap497 v) { values = v; }
    }

    static class StringMap497 extends LinkedHashMap<String,String> {
        public StringMap497 add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testMapFilteringViaProps() throws Exception
    {
        FilterProvider prov = new SimpleFilterProvider().addFilter("filterX",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        String json = VPackUtils.toJson(MAPPER.writer(prov).writeValueAsBytes(new MapBean()));
        assertEquals(a2q("{'values':{'b':5}}"), json);
    }

    @Test
    public void testMapFilteringViaClass() throws Exception
    {
        FilteredBean bean = new FilteredBean();
        bean.put("a", 4);
        bean.put("b", 3);
        FilterProvider prov = new SimpleFilterProvider().addFilter("filterForMaps",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        String json = VPackUtils.toJson(MAPPER.writer(prov).writeValueAsBytes(bean));
        assertEquals(a2q("{'b':3}"), json);
    }

    // [databind#527]
    @Test
    public void testNonNullValueMapViaProp() throws IOException
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new NoNullValuesMapContainer()
            .add("a", "foo")
            .add("b", null)
            .add("c", "bar")));
        assertEquals(a2q("{'stuff':{'a':'foo','c':'bar'}}"), json);
    }

    // [databind#522]
    @Test
    public void testMapFilteringWithAnnotations() throws Exception
    {
        FilterProvider prov = new SimpleFilterProvider().addFilter("filterX",
                new TestMapFilter());
        String json = VPackUtils.toJson(MAPPER.writer(prov).writeValueAsBytes(new MapBean()));
        // a=1 should become a=2
        assertEquals(a2q("{'values':{'a':2}}"), json);

        // and then one without annotation as contrast
        json = VPackUtils.toJson(MAPPER.writer(prov).writeValueAsBytes(new MapBeanNoOffset()));
        assertEquals(a2q("{'values':{'a':1}}"), json);
    }

    // [databind#527]
    @Test
    public void testMapNonNullValue() throws IOException
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new NoNullsStringMap()
            .add("a", "foo")
            .add("b", null)
            .add("c", "bar")));
        assertEquals(a2q("{'a':'foo','c':'bar'}"), json);
    }

    // [databind#527]
    @Test
    public void testMapNonEmptyValue() throws IOException
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new NoEmptyStringsMap()
            .add("a", "foo")
            .add("b", "bar")
            .add("c", "")));
        assertEquals(a2q("{'a':'foo','b':'bar'}"), json);
    }

    // Test to ensure absent content of AtomicReference handled properly
    // [databind#527]
    @Test
    public void testMapAbsentValue() throws IOException
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new NoAbsentStringMap()
            .add("a", "foo")
            .add("b", null)));
        assertEquals(a2q("{'a':'foo'}"), json);
    }

    // [databind#527]
    @Test
    public void testMapWithOnlyEmptyValues() throws IOException
    {
        String json;

        // First, non empty:
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Wrapper497(new StringMap497()
            .add("a", "123"))));
        assertEquals(a2q("{'values':{'a':'123'}}"), json);

        // then empty
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Wrapper497(new StringMap497()
            .add("a", "")
            .add("b", null))));
        assertEquals(a2q("{}"), json);
    }

    @Test
    public void testMapViaGlobalNonEmpty() throws Exception
    {
        // basic Map<String,String> subclass:
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl
                        .withContentInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertEquals(a2q("{'a':'b'}"), VPackUtils.toJson(mapper.writeValueAsBytes(
                new StringMap497()
                    .add("x", "")
                    .add("a", "b")
                    )));
    }

    @Test
    public void testMapViaTypeOverride() throws Exception
    {
        // basic Map<String,String> subclass:
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(Map.class,
                        o -> o.setInclude(JsonInclude.Value.empty()
                                .withContentInclusion(JsonInclude.Include.NON_EMPTY)))
                .build();
        assertEquals(a2q("{'a':'b'}"), VPackUtils.toJson(mapper.writeValueAsBytes(
                new StringMap497()
                    .add("foo", "")
                    .add("a", "b")
                    )));
    }
}
