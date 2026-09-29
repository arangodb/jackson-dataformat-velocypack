package tools.jackson.databind.views;

import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0650F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
@SuppressWarnings("unchecked")
    private static Map<String,Object> readMap(byte[] bytes) throws Exception {
        return (Map<String,Object>) MAPPER.readValue(bytes, Map.class);
    }
 void simple() throws Exception {
        Bean bean = new Bean();
        assertEquals(3, readMap(MAPPER.writeValueAsBytes(bean)).size());
        Map<String,Object> map = readMap(MAPPER.writerWithView(ViewA.class).writeValueAsBytes(bean));
        assertEquals(Map.of("a", "1"), map);
        map = readMap(MAPPER.writerWithView(ViewAA.class).writeValueAsBytes(bean));
        assertEquals(Map.of("a", "1", "aa", "2"), map);
        map = readMap(MAPPER.writerWithView(ViewB.class).writeValueAsBytes(bean));
        assertEquals(Map.of("aa", "2", "b", "3"), map);
        assertEquals(2, readMap(MAPPER.writerWithView(ViewBB.class).writeValueAsBytes(bean)).size());
        assertEquals(3, readMap(MAPPER.writerWithView(null).writeValueAsBytes(bean)).size());
    }
 void defaultExclusion() throws Exception {
        MixedBean bean = new MixedBean();
        assertEquals(Map.of("a", "1", "b", "2"), readMap(MAPPER.writerWithView(ViewA.class).writeValueAsBytes(bean)));
        ObjectMapper noDefault = VPackMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
        Map<?,?> map = (Map<?,?>) noDefault.readValue(noDefault.writerWithView(ViewA.class).writeValueAsBytes(bean), Map.class);
        assertEquals(Map.of("a", "1"), map);
        map = (Map<?,?>) noDefault.readValue(noDefault.writerWithView(null).writeValueAsBytes(bean), Map.class);
        assertEquals(Map.of("a", "1", "b", "2"), map);
    }
 void implicitAutoDetection() throws Exception {
        Map<String,Object> map = readMap(MAPPER.writeValueAsBytes(new ImplicitBean()));
        assertEquals(Map.of("a", 1), map);
    }
 void visibility() throws Exception {
        assertEquals(Map.of("id", "id"), readMap(MAPPER.writerWithView(Object.class)
                .writeValueAsBytes(new VisibilityBean())));
    }
 void issue868() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        assertEquals(0, ((Map<?,?>) mapper.readValue(mapper.writerWithView(OtherView.class)
                .writeValueAsBytes(new Foo()), Map.class)).size());
    }
 void withActiveView() throws Exception {
        final Class<?>[] inside = new Class<?>[1], after = new Class<?>[1];
        ObjectMapper mapper = mapperForActiveView((ctxt, g) -> {
            ctxt.withActiveView(ViewA.class, () -> inside[0] = ctxt.getActiveView());
            after[0] = ctxt.getActiveView();
        });
        mapper.writeValueAsBytes(new Bean5937());
        assertSame(ViewA.class, inside[0]); assertNull(after[0]);
        mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937());
        assertSame(ViewA.class, inside[0]); assertSame(ViewB.class, after[0]);
    }
 void withActiveViewRevertsOnThrow() throws Exception {
        final Class<?>[] after = new Class<?>[1];
        final RuntimeException boom = new RuntimeException("boom");
        ObjectMapper mapper = mapperForActiveView((ctxt, g) -> {
            try { ctxt.withActiveView(ViewA.class, () -> { throw boom; }); }
            catch (RuntimeException e) { if (e != boom) throw e; }
            after[0] = ctxt.getActiveView();
        });
        mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937());
        assertSame(ViewB.class, after[0]);
    }
 void withActiveViewNested() throws Exception {
        final Class<?>[] inner = new Class<?>[1], between = new Class<?>[1], after = new Class<?>[1];
        ObjectMapper mapper = mapperForActiveView((ctxt, g) -> {
            ctxt.withActiveView(ViewA.class, () -> {
                ctxt.withActiveView(ViewAA.class, () -> inner[0] = ctxt.getActiveView());
                between[0] = ctxt.getActiveView();
            });
            after[0] = ctxt.getActiveView();
        });
        mapper.writerWithView(ViewB.class).writeValueAsBytes(new Bean5937());
        assertSame(ViewAA.class, inner[0]); assertSame(ViewA.class, between[0]); assertSame(ViewB.class, after[0]);
    }
private static ObjectMapper mapperForActiveView(ActiveViewAction action) {
        return VPackMapper.builder().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .addModule(new SimpleModule().addSerializer(Bean5937.class,
                        new StdSerializer<Bean5937>(Bean5937.class) {
                            @Override public void serialize(Bean5937 value, JsonGenerator g,
                                    tools.jackson.databind.SerializationContext ctxt) {
                                action.run(ctxt, g);
                                g.writeStartObject(); g.writeEndObject();
                            }
                        })).build();
    }
 void dataBindingUsage() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).build();
        byte[] bytes = mapper.writerWithView(Views.View.class).writeValueAsBytes(new ComplexTestData());
        Map<?,?> root = (Map<?,?>) mapper.readValue(bytes, Object.class);
        assertTrue(root.containsKey("nameComplex"));
        assertFalse(root.containsKey("nameComplexHidden"));
        Map<?,?> nested = (Map<?,?>) root.get("testData");
        assertTrue(nested.containsKey("name"));
        assertFalse(nested.containsKey("nameHidden"));
        Map<?,?> arrayItem = (Map<?,?>) ((List<?>) root.get("testDataArray")).get(0);
        assertTrue(arrayItem.containsKey("name"));
        assertFalse(arrayItem.containsKey("nameHidden"));
        assertFalse(root.containsKey("nameNull"));
    }
 void dataBindingUsageWithoutView() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS).build();
        byte[] bytes = mapper.writerWithView(null).writeValueAsBytes(new ComplexTestData());
        Map<?,?> root = (Map<?,?>) mapper.readValue(bytes, Object.class);
        assertTrue(root.containsKey("nameComplexHidden"));
        Map<?,?> nested = (Map<?,?>) root.get("testData");
        assertTrue(nested.containsKey("nameHidden"));
    }
static class ViewA { }
static class ViewAA extends ViewA { }
static class ViewB { }
static class ViewBB extends ViewB { }
static class Views { interface View { } }
static class Bean {
        @JsonView(ViewA.class) public String a = "1";
        @JsonView({ViewAA.class, ViewB.class}) public String aa = "2";
        @JsonView(ViewB.class) public String getB() { return "3"; }
    }
static class MixedBean {
        @JsonView(ViewA.class) public String a = "1";
        public String getB() { return "2"; }
    }
static class ImplicitBean {
        @JsonView(ViewA.class) private int a = 1;
    }
static class VisibilityBean {
        @JsonProperty protected String id = "id";
        @JsonView(ViewA.class) public String value = "x";
    }
static class OtherView { }
static class Foo {
        @JsonView(ViewA.class) public int getFoo() { return 3; }
    }
static class Bean5937 { }
static class ComplexTestData {
        String nameNull = null;
        String nameComplex = "complexValue";
        String nameComplexHidden = "nameComplexHiddenValue";
        SimpleTestData testData = new SimpleTestData();
        SimpleTestData[] testDataArray = { new SimpleTestData(), null };

        @JsonView(Views.View.class) public String getNameNull() { return nameNull; }
        public void setNameNull(String value) { nameNull = value; }
        @JsonView(Views.View.class) public String getNameComplex() { return nameComplex; }
        public void setNameComplex(String value) { nameComplex = value; }
        public String getNameComplexHidden() { return nameComplexHidden; }
        public void setNameComplexHidden(String value) { nameComplexHidden = value; }
        @JsonView(Views.View.class) public SimpleTestData getTestData() { return testData; }
        public void setTestData(SimpleTestData value) { testData = value; }
        @JsonView(Views.View.class) public SimpleTestData[] getTestDataArray() { return testDataArray; }
        public void setTestDataArray(SimpleTestData[] value) { testDataArray = value; }
    }
static class SimpleTestData {
        String name = "shown";
        String nameHidden = "hidden";
        @JsonView(Views.View.class) public String getName() { return name; }
        public void setName(String value) { name = value; }
        public String getNameHidden() { return nameHidden; }
        public void setNameHidden(String value) { nameHidden = value; }
    }
static class CreatorBean {
        @JsonView(ViewA.class) public String a;
        @JsonView(ViewA.class) public String b;
        @JsonView(ViewB.class) public String c;
        @JsonCreator CreatorBean(@JsonProperty("a") String a, @JsonProperty("b") String b,
                @JsonProperty("c") String c) {
            this.a = a; this.b = b; this.c = c;
        }
    }
static class NoCreatorBean {
        public String a;
        @JsonView(ViewA.class) public String b;
        @JsonView(ViewB.class) public String c;
        public NoCreatorBean() { }
        public NoCreatorBean(String a, String b, String c) { this.a = a; this.b = b; this.c = c; }
    }
@FunctionalInterface private interface ActiveViewAction {
        void run(tools.jackson.databind.SerializationContext ctxt, JsonGenerator g);
    }

    void __invoke_simple() throws Exception {
        try {
            simple();
        } finally {
        }
    }


    void __invoke_defaultExclusion() throws Exception {
        try {
            defaultExclusion();
        } finally {
        }
    }


    void __invoke_implicitAutoDetection() throws Exception {
        try {
            implicitAutoDetection();
        } finally {
        }
    }


    void __invoke_visibility() throws Exception {
        try {
            visibility();
        } finally {
        }
    }


    void __invoke_issue868() throws Exception {
        try {
            issue868();
        } finally {
        }
    }


    void __invoke_withActiveView() throws Exception {
        try {
            withActiveView();
        } finally {
        }
    }


    void __invoke_withActiveViewRevertsOnThrow() throws Exception {
        try {
            withActiveViewRevertsOnThrow();
        } finally {
        }
    }


    void __invoke_withActiveViewNested() throws Exception {
        try {
            withActiveViewNested();
        } finally {
        }
    }


    void __invoke_dataBindingUsage() throws Exception {
        try {
            dataBindingUsage();
        } finally {
        }
    }


    void __invoke_dataBindingUsageWithoutView() throws Exception {
        try {
            dataBindingUsageWithoutView();
        } finally {
        }
    }

}
