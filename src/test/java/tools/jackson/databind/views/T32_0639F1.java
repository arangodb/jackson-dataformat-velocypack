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

class T32_0639F1 {
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
 void testSimple() throws Exception {
        byte[] input = VPackWireFixtureTest.hex("14 10 41 61 33 42 61 61 43 66 6f 6f 41 62 39 03");
        DeserBean bean = INPUT_MAPPER.readValue(input, DeserBean.class);
        assertEquals(3, bean.a); assertEquals("foo", bean.aa); assertEquals(9, bean.b);
        bean = INPUT_MAPPER.readerWithView(ViewAA.class).forType(DeserBean.class).readValue(input);
        assertEquals(3, bean.a); assertEquals("foo", bean.aa); assertEquals(0, bean.b);
        bean = INPUT_MAPPER.readerWithView(ViewA.class).forType(DeserBean.class)
                .readValue(VPackWireFixtureTest.hex("14 0e 41 61 31 42 61 61 41 78 41 62 33 03"));
        assertEquals(1, bean.a); assertNull(bean.aa); assertEquals(0, bean.b);
        bean = INPUT_MAPPER.readerFor(DeserBean.class).withView(ViewB.class)
                .readValue(VPackWireFixtureTest.hex("14 0e 41 61 3c 42 61 61 41 79 41 62 32 03"));
        assertEquals(0, bean.a); assertEquals("y", bean.aa); assertEquals(2, bean.b);
    }
 void testWithoutDefaultInclusion() throws Exception {
        DefaultsBean bean = INPUT_MAPPER.readValue(VPackWireFixtureTest.hex("14 09 41 61 33 41 62 39 02"), DefaultsBean.class);
        assertEquals(3, bean.a); assertEquals(9, bean.b);
        ObjectMapper mapper = VPackMapper.builder().disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
        bean = mapper.readerWithView(ViewAA.class).forType(DefaultsBean.class)
                .readValue(VPackWireFixtureTest.hex("14 09 41 61 31 41 62 32 02"));
        assertEquals(0, bean.a); assertEquals(2, bean.b);
    }
 void testWithCreatorAndViews() throws Exception {
        byte[] input = VPackWireFixtureTest.hex("14 09 41 61 31 41 62 32 02");
        CreatorBean result = INPUT_MAPPER.readerFor(CreatorBean.class).withView(ViewA.class).readValue(input);
        assertEquals(1, result.a); assertNull(result.b);
        result = INPUT_MAPPER.readerFor(CreatorBean.class).withView(ViewB.class).readValue(input);
        assertNull(result.a); assertEquals(2, result.b);
        result = INPUT_MAPPER.readerFor(CreatorBean.class).withView(ViewB.class)
                .readValue(VPackWireFixtureTest.hex("14 11 41 61 13 09 31 28 17 14 03 00 03 41 62 32 02"));
        assertNull(result.a); assertEquals(2, result.b);
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

    void __invoke_testSimple() throws Exception {
        try {
            testSimple();
        } finally {
        }
    }


    void __invoke_testWithoutDefaultInclusion() throws Exception {
        try {
            testWithoutDefaultInclusion();
        } finally {
        }
    }


    void __invoke_testWithCreatorAndViews() throws Exception {
        try {
            testWithCreatorAndViews();
        } finally {
        }
    }

}
