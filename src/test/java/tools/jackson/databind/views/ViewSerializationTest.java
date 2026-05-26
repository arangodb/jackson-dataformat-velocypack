package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying JSON view functionality: ability to declaratively
 * suppress subset of properties from being serialized.
 */
public class ViewSerializationTest extends DatabindTestUtil
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }

    static class Bean
    {
        @JsonView(ViewA.class)
        public String a = "1";

        @JsonView({ViewAA.class, ViewB.class})
        public String aa = "2";

        @JsonView(ViewB.class)
        public String getB() { return "3"; }
    }

    /**
     * Bean with mix of explicitly annotated
     * properties, and implicit ones that may or may
     * not be included in views.
     */
    static class MixedBean
    {
        @JsonView(ViewA.class)
        public String a = "1";

        public String getB() { return "2"; }
    }

    /**
     * As indicated by [JACKSON-261], @JsonView should imply
     * that associated element (method, field) is to be considered
     * a property
     */
    static class ImplicitBean {
        @JsonView(ViewA.class)
        private int a = 1;
    }

    static class VisibilityBean {
        @JsonProperty protected String id = "id";

        @JsonView(ViewA.class)
        public String value = "x";
    }

    public static class WebView { }
    public static class OtherView { }
    public static class Foo {
        @JsonView(WebView.class)
        public int getFoo() { return 3; }
    }

    // [databind#5937]
    static class Bean5937 { }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    // Ensure `MapperFeature.DEFAULT_VIEW_INCLUSION` is enabled
    // (its default differs b/w Jackson 2.x and 3.x)
    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

    @SuppressWarnings("unchecked")
    @Test
    public void testSimple() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Ok, first, using no view whatsoever; all 3
        Bean bean = new Bean();
        Map<String,Object> map = writeAndMap(MAPPER, bean);
        assertEquals(3, map.size());

        // Then with "ViewA", just one property
        out = new ByteArrayOutputStream();
        MAPPER.writerWithView(ViewA.class).writeValue(out, bean);
        map = MAPPER.readValue(VPackUtils.toVPack(VPackUtils.toJson(out.toByteArray())), Map.class);
        assertEquals(1, map.size());
        assertEquals("1", map.get("a"));

        // "ViewAA", 2 properties
        out = new ByteArrayOutputStream();
        MAPPER.writerWithView(ViewAA.class).writeValue(out, bean);
        map = MAPPER.readValue(VPackUtils.toVPack(VPackUtils.toJson(out.toByteArray())), Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("aa"));

        // "ViewB", 2 prop2
        String json = VPackUtils.toJson(MAPPER.writerWithView(ViewB.class).writeValueAsBytes(bean));
        map = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));

        // and "ViewBB", 2 as well
        json = VPackUtils.toJson(MAPPER.writerWithView(ViewBB.class).writeValueAsBytes(bean));
        map = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(2, map.size());
        assertEquals("2", map.get("aa"));
        assertEquals("3", map.get("b"));

        // and finally, without view.
        json = VPackUtils.toJson(MAPPER.writerWithView(null).writeValueAsBytes(bean));
        map = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(3, map.size());
    }

    /**
     * Unit test to verify implementation of [JACKSON-232], to
     * allow "opt-in" handling for JSON Views: that is, that
     * default for properties is to exclude unless included in
     * a view.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDefaultExclusion() throws IOException
    {
        MixedBean bean = new MixedBean();

        // default setting: both fields will get included
        String json = VPackUtils.toJson(MAPPER.writerWithView(ViewA.class).writeValueAsBytes(bean));
        Map<String,Object> map = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("b"));

        // but can also change (but not necessarily on the fly...)
        ObjectMapper mapper = vpackMapperBuilder()
                .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .build();

                // with this setting, only explicit inclusions count:
        json = VPackUtils.toJson(mapper.writerWithView(ViewA.class).writeValueAsBytes(bean));
        map = mapper.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(1, map.size());
        assertEquals("1", map.get("a"));
        assertNull(map.get("b"));

        // but without view, view processing disabled:
        json = VPackUtils.toJson(mapper.writer().withView(null).writeValueAsBytes(bean));
        map = mapper.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(2, map.size());
        assertEquals("1", map.get("a"));
        assertEquals("2", map.get("b"));
    }

    /**
     * As per [JACKSON-261], @JsonView annotation should imply that associated
     * method/field does indicate a property.
     */
    @Test
    public void testImplicitAutoDetection() throws Exception
    {
        assertEquals("{\"a\":1}",
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new ImplicitBean())));
    }

    @Test
    public void testVisibility() throws Exception
    {
        VisibilityBean bean = new VisibilityBean();
        // Without view setting, should only see "id"
        String json = VPackUtils.toJson(MAPPER.writerWithView(Object.class).writeValueAsBytes(bean));
        //json = VPackUtils.toJson(mapper.writeValueAsBytes(bean));
        assertEquals("{\"id\":\"id\"}", json);
    }

    // [JACKSON-868]
    @Test
    public void test868() throws IOException
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        assertEquals("{}",
                VPackUtils.toJson(mapper.writerWithView(OtherView.class).writeValueAsBytes(new Foo())));
    }

    // [databind#5937]
    @Test
    public void testWithActiveView() throws Exception
    {
        final Class<?>[] insideView = new Class<?>[1];
        final Class<?>[] afterView = new Class<?>[1];

        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(new SimpleModule()
                        .addSerializer(Bean5937.class, new StdSerializer<Bean5937>(Bean5937.class) {
                            @Override
                            public void serialize(Bean5937 value, JsonGenerator g,
                                    SerializationContext ctxt) {
                                ctxt.withActiveView(ViewA.class, () -> {
                                    insideView[0] = ctxt.getActiveView();
                                });
                                afterView[0] = ctxt.getActiveView();
                                g.writeStartObject();
                                g.writeEndObject();
                            }
                        }))
                .build();

        // No initial view: inside == ViewA, after reverts to null
        VPackUtils.toJson(mapper.writeValueAsBytes(new Bean5937()));
        assertSame(ViewA.class, insideView[0]);
        assertNull(afterView[0]);

        // With initial view ViewB: inside == ViewA, after reverts to ViewB
        VPackUtils.toJson(mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937()));
        assertSame(ViewA.class, insideView[0]);
        assertSame(ViewB.class, afterView[0]);
    }

    // [databind#5937]: active view must be reverted even if callback throws
    @Test
    public void testWithActiveViewRevertsOnThrow() throws Exception
    {
        final Class<?>[] afterView = new Class<?>[1];
        final RuntimeException boom = new RuntimeException("boom");

        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(new SimpleModule()
                        .addSerializer(Bean5937.class, new StdSerializer<Bean5937>(Bean5937.class) {
                            @Override
                            public void serialize(Bean5937 value, JsonGenerator g,
                                    SerializationContext ctxt) {
                                try {
                                    ctxt.withActiveView(ViewA.class, () -> { throw boom; });
                                } catch (RuntimeException e) {
                                    if (e != boom) throw e;
                                }
                                afterView[0] = ctxt.getActiveView();
                                g.writeStartObject();
                                g.writeEndObject();
                            }
                        }))
                .build();

        VPackUtils.toJson(mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937()));
        assertSame(ViewB.class, afterView[0]);
    }

    // [databind#5937]: nested withActiveView calls must each revert to the
    //   view in effect at their entry
    @Test
    public void testWithActiveViewNested() throws Exception
    {
        final Class<?>[] innerView = new Class<?>[1];
        final Class<?>[] betweenView = new Class<?>[1];
        final Class<?>[] afterView = new Class<?>[1];

        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(new SimpleModule()
                        .addSerializer(Bean5937.class, new StdSerializer<Bean5937>(Bean5937.class) {
                            @Override
                            public void serialize(Bean5937 value, JsonGenerator g,
                                    SerializationContext ctxt) {
                                ctxt.withActiveView(ViewA.class, () -> {
                                    ctxt.withActiveView(ViewAA.class, () -> {
                                        innerView[0] = ctxt.getActiveView();
                                    });
                                    betweenView[0] = ctxt.getActiveView();
                                });
                                afterView[0] = ctxt.getActiveView();
                                g.writeStartObject();
                                g.writeEndObject();
                            }
                        }))
                .build();

        VPackUtils.toJson(mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937()));
        assertSame(ViewAA.class, innerView[0]);
        assertSame(ViewA.class, betweenView[0]);
        assertSame(ViewB.class, afterView[0]);
    }
}
