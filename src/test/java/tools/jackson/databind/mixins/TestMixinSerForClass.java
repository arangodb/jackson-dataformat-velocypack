package tools.jackson.databind.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestMixinSerForClass
    extends DatabindTestUtil
{
    @JsonInclude(JsonInclude.Include.ALWAYS)
    static class BaseClass
    {
        protected String _a, _b;
        protected String _c = "c";

        protected BaseClass() { }

        public BaseClass(String a) {
            _a = a;
        }

        // will be auto-detectable unless disabled:
        public String getA() { return _a; }

        @JsonProperty
        public String getB() { return _b; }

        @JsonProperty
        public String getC() { return _c; }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class LeafClass
        extends BaseClass
    {
        public LeafClass() { super(null); }

        public LeafClass(String a) {
            super(a);
        }
    }

    /**
     * This interface only exists to add "mix-in annotations": that is, any
     * annotations it has can be virtually added to mask annotations
     * of other classes
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    interface MixIn { }

    // test disabling of autodetect...
    @JsonAutoDetect(getterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
    interface MixInAutoDetect { }

    // [databind#245]
    @JsonFilter(PortletRenderExecutionEventFilterMixIn.FILTER_NAME)
    private interface PortletRenderExecutionEventFilterMixIn {
        static final String FILTER_NAME = "PortletRenderExecutionEventFilter";
    }

    // [databind#3035]
    static class Bean3035 {
        public int getA() { return 42; }
    }

    static class Rename3035Mixin extends Bean3035 {
        @JsonProperty("b")
        @Override
        public int getA() { return super.getA(); }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testClassMixInsTopLevel() throws IOException
    {
        Map<String,Object> result;

        // first: with no mix-ins:
        result = writeAndMap(MAPPER, new LeafClass("abc"));
        assertEquals(1, result.size());
        assertEquals("abc", result.get("a"));

        // then with top-level override
        ObjectMapper mapper = vpackMapperBuilder()
                .addMixIn(LeafClass.class, MixIn.class)
                .build();
        result = writeAndMap(mapper, new LeafClass("abc"));
        assertEquals(2, result.size());
        assertEquals("abc", result.get("a"));
        assertEquals("c", result.get("c"));

        // mid-level override; should not have any effect
        mapper = vpackMapperBuilder()
                .addMixIn(BaseClass.class, MixIn.class)
                .build();
        result = writeAndMap(mapper, new LeafClass("abc"));
        assertEquals(1, result.size());
        assertEquals("abc", result.get("a"));
    }

    @Test
    public void testClassMixInsMidLevel() throws IOException
    {
        Map<String,Object> result;
        LeafClass bean = new LeafClass("xyz");
        bean._c = "c2";

        // with no mix-ins first...
        result = writeAndMap(MAPPER, bean);
        assertEquals(2, result.size());
        assertEquals("xyz", result.get("a"));
        assertEquals("c2", result.get("c"));

        // then with working mid-level override, which effectively suppresses 'a'
        ObjectMapper mapper = vpackMapperBuilder()
                .addMixIn(BaseClass.class, MixInAutoDetect.class)
                .build();
        result = writeAndMap(mapper, bean);
        assertEquals(1, result.size());
        assertEquals("c2", result.get("c"));
    }

    // [databind#3035]: ability to remove mix-ins:
    @Test
    public void testClassMixInRemoval() throws Exception
    {
        // First, no mix-in
        assertEquals(a2q("{'a':42}"), VPackUtils.toJson(MAPPER.writeValueAsBytes(new Bean3035())));

        // then with mix-in
        assertEquals(a2q("{'b':42}"),
                VPackUtils.toJson(VPackMapper.builder()
                    .addMixIn(Bean3035.class, Rename3035Mixin.class)
                    .build()
                    .writeValueAsBytes(new Bean3035())));

        // and then with a convoluted example to verify removal can work:
        assertEquals(a2q("{'a':42}"),
                VPackUtils.toJson(VPackMapper.builder()
                    .addMixIn(Bean3035.class, Rename3035Mixin.class)
                    .removeMixIn(Bean3035.class)
                    .build()
                    .writeValueAsBytes(new Bean3035())));
    }
}
