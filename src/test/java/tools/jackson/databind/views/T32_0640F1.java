package tools.jackson.databind.views;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0640F1 {
private static final byte[] CONFIG_INPUT = VPackWireFixtureTest.hex(
            "14 27 42 69 64 28 63 44 6e 61 6d 65 45 41 6c 69 63 65 "
            + "4c 69 6e 74 65 72 6e 61 6c 44 61 74 61 46 68 61 63 6b 65 64 03");
private static final byte[] DEFAULTING_INPUT = VPackWireFixtureTest.hex(
            "14 09 41 61 31 41 62 32 02");
private static final byte[] BUILDER_ARRAY_INPUT = VPackWireFixtureTest.hex(
            "02 0e 45 61 6c 69 63 65 45 61 64 6d 69 6e");
@SuppressWarnings("unchecked")
    private static Map<String,Object> readMap(ObjectMapper mapper, byte[] bytes) throws Exception {
        return (Map<String,Object>) mapper.readValue(bytes, Map.class);
    }
private static ObjectMapper configMapper() {
        return VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
    }
 void testDefaultViewDeserialization() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES).build();
        Defaulting result = mapper.readValue(DEFAULTING_INPUT, Defaulting.class);
        assertEquals(1, result.a); assertEquals(2, result.b);
        result = mapper.readerFor(Defaulting.class).withView(ViewA.class).readValue(DEFAULTING_INPUT);
        assertEquals(1, result.a); assertEquals(5, result.b);
        result = mapper.readerFor(Defaulting.class).withView(ViewBB.class).readValue(DEFAULTING_INPUT);
        assertEquals(3, result.a); assertEquals(2, result.b);
    }
 void testDefaultViewSerialization() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        Defaulting bean = new Defaulting();
        assertEquals(Map.of("a", 3, "b", 5), readMap(mapper, mapper.writeValueAsBytes(bean)));
        assertEquals(Map.of("a", 3), readMap(mapper,
                mapper.writerWithView(ViewA.class).writeValueAsBytes(bean)));
        assertEquals(Map.of("b", 5), readMap(mapper,
                mapper.writerWithView(ViewB.class).writeValueAsBytes(bean)));
    }
private static ObjectMapper arrayMapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        else builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        return builder.build();
    }
static class ViewA { }
static class ViewB { }
static class ViewBB extends ViewB { }
static class ViewPublic { }
static class ViewInternal { }
static class Public { }
static class Admin extends Public { }
static class ConfigBean {
        @JsonView(ViewPublic.class) public int id = 1;
        @JsonView(ViewPublic.class) public String name = "Bob";
        @JsonView(ViewInternal.class) public String internalData = "secret";
    }
@JsonView(ViewA.class)
    @JsonPropertyOrder({ "a", "b" })
    static class Defaulting {
        public int a = 3;
        @JsonView(ViewB.class) public int b = 5;
    }
@JsonDeserialize(builder = User.Builder.class)
    static class User {
        final String name;
        final String role;
        User(String name, String role) { this.name = name; this.role = role; }

        @JsonPOJOBuilder(withPrefix = "")
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        @JsonPropertyOrder({ "name", "role" })
        static class Builder {
            String name;
            String role;
            @JsonCreator
            Builder(@JsonProperty("name") @JsonView(Public.class) String name) { this.name = name; }
            @JsonView(Admin.class) Builder role(String value) { role = value; return this; }
            User build() { return new User(name, role); }
        }
    }

    void __invoke_testDefaultViewDeserialization() throws Exception {
        try {
            testDefaultViewDeserialization();
        } finally {
        }
    }


    void __invoke_testDefaultViewSerialization() throws Exception {
        try {
            testDefaultViewSerialization();
        } finally {
        }
    }

}
