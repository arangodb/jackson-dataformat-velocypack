package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ensuring that entries accessible via "any filter"
 * can also be filtered with JSON Filter functionality.
 */
public class TestAnyGetterFiltering extends DatabindTestUtil
{
    @JsonFilter("anyFilter")
    public static class AnyBean
    {
        private Map<String, String> properties = new HashMap<String, String>();
        {
            properties.put("a", "1");
            properties.put("b", "2");
        }

        @JsonAnyGetter
        public Map<String, String> anyProperties()
        {
            return properties;
        }
    }

    public static class AnyBeanWithIgnores
    {
        private Map<String, String> properties = new LinkedHashMap<String, String>();
        {
            properties.put("a", "1");
            properties.put("bogus", "2");
            properties.put("b", "3");
        }

        @JsonAnyGetter
        @JsonIgnoreProperties({ "bogus" })
        public Map<String, String> anyProperties()
        {
            return properties;
        }
    }

    // [databind#1281]
    public static class AnyBeanWithMultipleIgnores
    {
        public String name = "bob";

        private Map<String, String> properties = new LinkedHashMap<String, String>();
        {
            properties.put("a", "1");
            properties.put("secret", "s");
            properties.put("b", "2");
            properties.put("internal", "i");
        }

        @JsonAnyGetter
        @JsonIgnoreProperties({ "secret", "internal" })
        public Map<String, String> anyProperties()
        {
            return properties;
        }
    }

    // [databind#1655]
    @JsonFilter("CustomFilter")
    static class OuterObject {
         public int getExplicitProperty() {
              return 42;
         }

         @JsonAnyGetter
         public Map<String, Object> getAny() {
              Map<String, Object> extra = new HashMap<>();
              extra.put("dynamicProperty", "I will not serialize");
              return extra;
         }
    }

    static class CustomFilter extends SimpleBeanPropertyFilter {
         @Override
         public void serializeAsProperty(Object pojo, JsonGenerator gen, SerializationContext provider,
                 PropertyWriter writer) throws Exception
         {
             if (pojo instanceof OuterObject) {
                 writer.serializeAsProperty(pojo, gen, provider);
              }
         }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new VPackMapper();

    @Test
    public void testAnyGetterFiltering() throws Exception
    {
        FilterProvider prov = new SimpleFilterProvider().addFilter("anyFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertEquals("{\"b\":\"2\"}", VPackUtils.toJson(MAPPER.writer(prov).writeValueAsBytes(new AnyBean())));
    }

    // for [databind#1142]
    @Test
    public void testAnyGetterIgnore() throws Exception
    {
        assertEquals(a2q("{'a':'1','b':'3'}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new AnyBeanWithIgnores())));
    }

    // [databind#1281]: @JsonIgnoreProperties on @JsonAnyGetter method should
    //   filter multiple map entries, coexist with regular properties
    @Test
    public void testAnyGetterIgnoreProperties1281() throws Exception
    {
        assertEquals(a2q("{'name':'bob','a':'1','b':'2'}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new AnyBeanWithMultipleIgnores())));
    }

    // [databind#1655]
    @Test
    public void testAnyGetterPojo1655() throws Exception
    {
        FilterProvider filters = new SimpleFilterProvider().addFilter("CustomFilter", new CustomFilter());
        String json = VPackUtils.toJson(MAPPER.writer(filters).writeValueAsBytes(new OuterObject()));
        Map<?,?> stuff = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        if (stuff.size() != 2) {
            fail("Should have 2 properties, got: "+stuff);
        }
   }
}
