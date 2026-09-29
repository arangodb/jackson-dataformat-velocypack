package tools.jackson.databind.views;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonApplyView;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0639F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
private static final ObjectMapper NO_DEFAULT = VPackMapper.builder()
            .disable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
private static final ObjectMapper INPUT_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
@SuppressWarnings("unchecked")
    private static Map<String,Object> readMap(ObjectMapper mapper, byte[] bytes) throws Exception {
        return (Map<String,Object>) mapper.readValue(bytes, Map.class);
    }
 void testJsonApplyView() throws Exception {
        Map<String,Object> root = readMap(MAPPER, MAPPER.writerWithView(ViewA.class).writeValueAsBytes(new ApplyBean()));
        assertEquals(2, root.size());
        Map<String,Object> b = (Map<String,Object>) root.get("beanWithApplyViewB");
        assertEquals(2, b.size()); assertFalse(b.containsKey("a"));
        assertEquals("2", b.get("aa")); assertEquals("3", b.get("b"));
        Map<String,Object> none = (Map<String,Object>) root.get("beanWithApplyNoneView");
        assertEquals(3, none.size()); assertEquals("1", none.get("a"));
        assertEquals("2", none.get("aa")); assertEquals("3", none.get("b"));
    }
 void testJsonApplyViewNested() throws Exception {
        Map<String,Object> root = readMap(MAPPER, MAPPER.writerWithView(ViewAA.class).writeValueAsBytes(new NestedApplyBean()));
        assertEquals(3, root.size());
        Map<String,Object> bean = (Map<String,Object>) root.get("bean");
        assertEquals(2, bean.size()); assertEquals("1", bean.get("a"));
        assertEquals("2", bean.get("aa")); assertFalse(bean.containsKey("b"));
        Map<String,Object> nested = (Map<String,Object>) root.get("bean2");
        assertEquals(2, nested.size());
        Map<String,Object> withB = (Map<String,Object>) nested.get("beanWithApplyViewB");
        assertEquals(2, withB.size()); assertFalse(withB.containsKey("a"));
        assertEquals("2", withB.get("aa")); assertEquals("3", withB.get("b"));
        Map<String,Object> none = (Map<String,Object>) nested.get("beanWithApplyNoneView");
        assertEquals(3, none.size()); assertEquals("1", none.get("a"));
        assertEquals("2", none.get("aa")); assertEquals("3", none.get("b"));
        assertEquals(0, ((Map<?,?>) root.get("bean2WithApplyViewB")).size());
    }
 void testJsonApplyViewOverridesOuter() throws Exception {
        Map<String,Object> root = readMap(MAPPER, MAPPER.writerWithView(ViewB.class).writeValueAsBytes(new OuterApplyBean()));
        assertEquals(1, root.size());
        Map<String,Object> wrapped = (Map<String,Object>) root.get("wrapped");
        assertEquals(2, wrapped.size());
        Map<String,Object> withB = (Map<String,Object>) wrapped.get("beanWithApplyViewB");
        assertEquals(2, withB.size()); assertFalse(withB.containsKey("a"));
        assertEquals("2", withB.get("aa")); assertEquals("3", withB.get("b"));
        Map<String,Object> none = (Map<String,Object>) wrapped.get("beanWithApplyNoneView");
        assertEquals(3, none.size()); assertEquals("1", none.get("a"));
        assertEquals("2", none.get("aa")); assertEquals("3", none.get("b"));
    }
 void testJsonApplyViewWithoutDefaultInclusion() throws Exception {
        Map<String,Object> root = readMap(NO_DEFAULT, NO_DEFAULT.writerWithView(ViewA.class).writeValueAsBytes(new ApplyBean()));
        assertEquals(2, root.size());
        Map<String,Object> withB = (Map<String,Object>) root.get("beanWithApplyViewB");
        assertEquals(2, withB.size()); assertFalse(withB.containsKey("a"));
        assertEquals("2", withB.get("aa")); assertEquals("3", withB.get("b"));
        Map<String,Object> none = (Map<String,Object>) root.get("beanWithApplyNoneView");
        assertEquals(3, none.size()); assertEquals("1", none.get("a"));
        assertEquals("2", none.get("aa")); assertEquals("3", none.get("b"));
    }
private static ObjectMapper configured(Class<?> view, boolean serialization) {
        var builder = VPackMapper.builder();
        if (serialization) builder.defaultSerializationView(view);
        else builder.defaultDeserializationView(view);
        return builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
    }
private static byte[] configInput() throws Exception {
        return VPackWireFixtureTest.hex("14 27 42 69 64 28 63 44 6e 61 6d 65 45 41 6c 69 63 65 "
                + "4c 69 6e 74 65 72 6e 61 6c 44 61 74 61 46 68 61 63 6b 65 64 03");
    }
static class ViewA { }
static class ViewAA extends ViewA { }
static class ViewB { }
static class ViewBB extends ViewB { }
static class ViewPublic { }
static class ViewInternal { }
static class ViewBean {
        @JsonView(ViewA.class) public String a = "1";
        @JsonView({ViewAA.class, ViewB.class}) public String aa = "2";
        @JsonView(ViewB.class) public String getB() { return "3"; }
    }
static class ApplyBean {
        @JsonView(ViewA.class) @JsonApplyView(ViewB.class)
        public ViewBean beanWithApplyViewB = new ViewBean();
        @JsonView(ViewA.class) @JsonApplyView(JsonApplyView.NONE.class)
        public ViewBean beanWithApplyNoneView = new ViewBean();
    }
static class NestedApplyBean {
        @JsonView(ViewA.class) public ViewBean bean = new ViewBean();
        @JsonView(ViewA.class) public ApplyBean bean2 = new ApplyBean();
        @JsonView(ViewA.class) @JsonApplyView(ViewB.class)
        public ApplyBean bean2WithApplyViewB = new ApplyBean();
    }
static class OuterApplyBean {
        @JsonView(ViewB.class) @JsonApplyView(ViewA.class)
        public ApplyBean wrapped = new ApplyBean();
    }
static class DeserBean {
        @JsonView(ViewA.class) public int a;
        @JsonView({ViewAA.class, ViewB.class}) public String aa;
        protected int b;
        @JsonView(ViewB.class) public void setB(int value) { b = value; }
    }
static class DefaultsBean {
        public int a;
        @JsonView(ViewA.class) public int b;
    }
static class CreatorBean {
        @JsonView(ViewA.class) public Integer a;
        @JsonView(ViewB.class) public Integer b;
        @JsonCreator CreatorBean(@JsonProperty("a") Integer a, @JsonProperty("b") Integer b) {
            this.a = a; this.b = b;
        }
    }
static class ConfigBean {
        @JsonView(ViewPublic.class) public int id = 1;
        @JsonView(ViewPublic.class) public String name = "Bob";
        @JsonView(ViewInternal.class) public String internalData = "secret";
    }

    void __invoke_testJsonApplyView() throws Exception {
        try {
            testJsonApplyView();
        } finally {
        }
    }


    void __invoke_testJsonApplyViewNested() throws Exception {
        try {
            testJsonApplyViewNested();
        } finally {
        }
    }


    void __invoke_testJsonApplyViewOverridesOuter() throws Exception {
        try {
            testJsonApplyViewOverridesOuter();
        } finally {
        }
    }


    void __invoke_testJsonApplyViewWithoutDefaultInclusion() throws Exception {
        try {
            testJsonApplyViewWithoutDefaultInclusion();
        } finally {
        }
    }

}
