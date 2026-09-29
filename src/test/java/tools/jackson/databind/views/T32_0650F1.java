package tools.jackson.databind.views;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0650F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
@SuppressWarnings("unchecked")
    private static Map<String,Object> readMap(byte[] bytes) throws Exception {
        return (Map<String,Object>) MAPPER.readValue(bytes, Map.class);
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
 void creatorViewDeserialization() throws Exception {
        byte[] input = { 0x14, 0x0f, 0x41, 0x61, 0x41, 0x61, 0x41, 0x62, 0x41, 0x62,
                0x41, 0x63, 0x41, 0x63, 0x03 };
        ObjectMapper readerMapper = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
        CreatorBean value = readerMapper.readerFor(CreatorBean.class).withView(ViewA.class).readValue(input);
        assertEquals("a-b-null", value.a + "-" + value.b + "-" + value.c);
    }
 void noCreatorViewDeserialization() throws Exception {
        byte[] input = { 0x14, 0x0f, 0x41, 0x61, 0x41, 0x61, 0x41, 0x62, 0x41, 0x62,
                0x41, 0x63, 0x41, 0x63, 0x03 };
        ObjectMapper readerMapper = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
        NoCreatorBean value = readerMapper.readerFor(NoCreatorBean.class).withView(ViewA.class).readValue(input);
        assertEquals("a-b-null", value.a + "-" + value.b + "-" + value.c);
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

    void __invoke_creatorViewDeserialization() throws Exception {
        try {
            creatorViewDeserialization();
        } finally {
        }
    }


    void __invoke_noCreatorViewDeserialization() throws Exception {
        try {
            noCreatorViewDeserialization();
        } finally {
        }
    }

}
