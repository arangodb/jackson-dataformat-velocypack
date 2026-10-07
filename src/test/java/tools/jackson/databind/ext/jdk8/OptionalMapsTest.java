package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalMapsTest
    extends DatabindTestUtil
{
    static final class OptMapBean {
        public Map<String, Optional<?>> values;

        public OptMapBean(String key, Optional<?> v) {
            values = new LinkedHashMap<>();
            values.put(key, v);
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    @Test
    public void testMapElementInclusion() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder().changeDefaultPropertyInclusion(
                incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL)
                    .withContentInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        // first: Absent entry/-ies should NOT be included
        assertEquals("{\"values\":{}}",
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptMapBean("key", Optional.empty()))));
        // but non-empty should
        assertEquals("{\"values\":{\"key\":\"value\"}}",
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptMapBean("key", Optional.of("value")))));
        // and actually even empty
        assertEquals("{\"values\":{\"key\":\"\"}}",
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptMapBean("key", Optional.of("")))));
    }

}
