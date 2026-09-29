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

class T32_0639F2 {
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
 void testDefaultSerializationView() throws Exception {
        ObjectMapper mapper = configured(ViewPublic.class, true);
        assertEquals(Map.of("id", 1, "name", "Bob"), readMap(MAPPER, mapper.writeValueAsBytes(new ConfigBean())));
    }
 void testDefaultSerializationViewOverride() throws Exception {
        ObjectMapper mapper = configured(ViewPublic.class, true);
        assertEquals(Map.of("internalData", "secret"), readMap(MAPPER,
                mapper.writerWithView(ViewInternal.class).writeValueAsBytes(new ConfigBean())));
        assertEquals(Map.of("id", 1, "name", "Bob"), readMap(MAPPER, mapper.writer().writeValueAsBytes(new ConfigBean())));
    }
 void testDefaultDeserializationView() throws Exception {
        ObjectMapper mapper = configured(ViewPublic.class, false);
        ConfigBean bean = mapper.readerFor(ConfigBean.class).readValue(configInput());
        assertEquals(99, bean.id); assertEquals("Alice", bean.name); assertEquals("secret", bean.internalData);
    }
 void testDefaultDeserializationViewOverride() throws Exception {
        ObjectMapper mapper = configured(ViewPublic.class, false);
        ConfigBean bean = mapper.readerFor(ConfigBean.class).withView(ViewInternal.class).readValue(configInput());
        assertEquals(1, bean.id); assertEquals("Bob", bean.name); assertEquals("hacked", bean.internalData);
        bean = mapper.readerFor(ConfigBean.class).readValue(configInput());
        assertEquals(99, bean.id); assertEquals("Alice", bean.name); assertEquals("secret", bean.internalData);
    }
 void testDefaultViewBothSerAndDeser() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().defaultView(ViewPublic.class)
                .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
        assertEquals(Map.of("id", 1, "name", "Bob"), readMap(MAPPER, mapper.writeValueAsBytes(new ConfigBean())));
        ConfigBean bean = mapper.readerFor(ConfigBean.class).readValue(configInput());
        assertEquals(99, bean.id); assertEquals("Alice", bean.name); assertEquals("secret", bean.internalData);
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

    void __invoke_testDefaultSerializationView() throws Exception {
        try {
            testDefaultSerializationView();
        } finally {
        }
    }


    void __invoke_testDefaultSerializationViewOverride() throws Exception {
        try {
            testDefaultSerializationViewOverride();
        } finally {
        }
    }


    void __invoke_testDefaultDeserializationView() throws Exception {
        try {
            testDefaultDeserializationView();
        } finally {
        }
    }


    void __invoke_testDefaultDeserializationViewOverride() throws Exception {
        try {
            testDefaultDeserializationViewOverride();
        } finally {
        }
    }


    void __invoke_testDefaultViewBothSerAndDeser() throws Exception {
        try {
            testDefaultViewBothSerAndDeser();
        } finally {
        }
    }

}
