package tools.jackson.databind.access;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Separate tests located in different package than code being
import jackson.databind.VPackUtils;
 * exercised; needed to trigger some access-related failures.
 */
public class AnyGetterAccessTest
{
    /*
    /**********************************************************
    /* Helper bean classes
    /**********************************************************
     */

    static class DynaBean {
        public int id;

        protected Map<String,String> other = new HashMap<String,String>();

        @JsonAnyGetter
        public Map<String,String> any() {
            return other;
        }

        @JsonAnySetter
        public void set(String name, String value) {
            other.put(name, value);
        }
    }

    static class PrivateThing
    {
        @JsonAnyGetter
        public Map<?,?> getProperties()
        {
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("a", "A");
            return map;
        }
    }

    /*
    /**********************************************************
    /* Test cases
    /**********************************************************
     */

    private final ObjectMapper MAPPER = VPackMapper.builder().build();

    @Test
    public void testDynaBean() throws Exception
    {
        DynaBean b = new DynaBean();
        b.id = 123;
        b.set("name", "Billy");
        assertEquals("{\"id\":123,\"name\":\"Billy\"}", VPackUtils.toJson(MAPPER.writeValueAsBytes(b)));

        DynaBean result = MAPPER.readValue(VPackUtils.toVPack("{\"id\":2,\"name\":\"Joe\"}"), DynaBean.class);
        assertEquals(2, result.id);
        assertEquals("Joe", result.other.get("name"));
    }

    @Test
    public void testPrivate() throws Exception
    {
        assertEquals("{\"a\":\"A\"}",
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new PrivateThing())));
    }
}
