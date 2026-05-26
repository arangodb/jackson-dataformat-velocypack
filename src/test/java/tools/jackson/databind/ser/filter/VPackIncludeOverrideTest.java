package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for checking that overridden settings for
 * <code>JsonInclude</code> annotation property work
 * as expected.
 */
public class VPackIncludeOverrideTest
    extends DatabindTestUtil
{
    @JsonPropertyOrder({"list", "map"})
    static class EmptyListMapBean
    {
        public List<String> list = Collections.emptyList();

        public Map<String,String> map = Collections.emptyMap();
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeAlwaysBean
    {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num = null;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String annotated = null;

        public String plain = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeNonNullBean
    {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num = null;

        @JsonInclude(JsonInclude.Include.ALWAYS)
        public String annotated = null;

        public String plain = null;
    }

    @Test
    public void testPropConfigOverridesForInclude() throws IOException
    {
        ObjectMapper mapper = new VPackMapper();
        // First, with defaults, both included:
        EmptyListMapBean empty = new EmptyListMapBean();
        assertEquals(a2q("{'list':[],'map':{}}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(empty)));

        // and then change inclusion criteria for either
        mapper = vpackMapperBuilder()
                .withConfigOverride(Map.class,
                        o -> o.setInclude(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null)))
                .build();
        assertEquals(a2q("{'list':[]}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(empty)));

        mapper = vpackMapperBuilder()
                .withConfigOverride(List.class,
                        o -> o.setInclude(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null)))
                .build();
        assertEquals(a2q("{'map':{}}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(empty)));
    }

    @Test
    public void testOverrideForIncludeAsPropertyNonNull() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        // First, with defaults, all but NON_NULL annotated included
        MixedTypeAlwaysBean nullValues = new MixedTypeAlwaysBean();
        assertEquals(a2q("{'num':null,'plain':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = vpackMapperBuilder()
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
                .build();
        assertEquals("{\"num\":null}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        mapper = vpackMapperBuilder()
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
                .build();
        assertEquals("{\"plain\":null}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));
    }

    @Test
    public void testOverrideForIncludeAsPropertyAlways() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        // First, with defaults, only ALWAYS annotated included
        MixedTypeNonNullBean nullValues = new MixedTypeNonNullBean();
        assertEquals("{\"annotated\":null}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = vpackMapperBuilder()
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null)))
                .build();
        assertEquals(a2q("{'annotated':null,'plain':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        mapper = vpackMapperBuilder()
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null)))
                .build();
        assertEquals(a2q("{'num':null,'annotated':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));
    }

    @Test
    public void testOverridesForIncludeAndIncludeAsPropertyNonNull() throws Exception
    {
        // First, with ALWAYS override on containing bean, all included
        MixedTypeNonNullBean nullValues = new MixedTypeNonNullBean();
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null)))
                .build();
        assertEquals(a2q("{'num':null,'annotated':null,'plain':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null)))
                .withConfigOverride(String.class,
                    o -> o.setIncludeAsProperty(JsonInclude.Value
                            .construct(JsonInclude.Include.NON_NULL, null)))
                .build();
        assertEquals(a2q("{'num':null,'annotated':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value
                                .construct(JsonInclude.Include.ALWAYS, null)))
                .withConfigOverride(Integer.class,
                    o -> o.setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
            .build();
        assertEquals(a2q("{'annotated':null,'plain':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));
    }

    @Test
    public void testOverridesForIncludeAndIncludeAsPropertyAlways() throws Exception
    {
        // First, with NON_NULL override on containing bean, empty
        MixedTypeAlwaysBean nullValues = new MixedTypeAlwaysBean();
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
                .build();
        assertEquals("{}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                                .construct(JsonInclude.Include.ALWAYS, null)))
                .build();
        assertEquals("{\"plain\":null}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));

        mapper = vpackMapperBuilder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null)))
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value
                                .construct(JsonInclude.Include.ALWAYS, null)))
                .build();
        assertEquals("{\"num\":null}",
                VPackUtils.toJson(mapper.writeValueAsBytes(nullValues)));
    }
}
