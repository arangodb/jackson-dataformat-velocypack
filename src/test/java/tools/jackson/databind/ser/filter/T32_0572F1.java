package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0572F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testGlobalNonDefaultRoundTripVpack() throws Exception {
        ObjectMapper mapper = realNonDefaultMapper();
        Entity entity = new Entity();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), mapper.writeValueAsBytes(entity));

        Entity deserialized = mapper.readValue(VPackWireFixtureTest.hex("0a"), Entity.class);
        assertEquals("a default", deserialized.getSomeFieldWithDefault());
    }

    void testGlobalNonDefaultIncludesNonDefaultVpack() throws Exception {
        ObjectMapper mapper = realNonDefaultMapper();
        Entity entity = new Entity();
        entity.setSomeFieldWithDefault("custom value");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 26 01 54 73 6f 6d 65 46 69 65 6c 64 57 69 74 68 44 65 66 61 75 6c 74 "
              + "4c 63 75 73 74 6f 6d 20 76 61 6c 75 65 03"),
                mapper.writeValueAsBytes(entity));
    }

    void testGlobalNonDefaultEmptyStringNonDefaultVpack() throws Exception {
        ObjectMapper mapper = realNonDefaultMapper();
        Entity entity = new Entity();
        entity.setSomeFieldWithDefault("");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1a 01 54 73 6f 6d 65 46 69 65 6c 64 57 69 74 68 44 65 66 61 75 6c 74 40 03"),
                mapper.writeValueAsBytes(entity));
    }

    void testGlobalNonDefaultPrimitiveFieldVpack() throws Exception {
        ObjectMapper mapper = realNonDefaultMapper();
        IntDefaultEntity entity = new IntDefaultEntity();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), mapper.writeValueAsBytes(entity));

        entity.value = 0;
        assertArrayEquals(VPackWireFixtureTest.hex("0b 0b 01 45 76 61 6c 75 65 30 03"),
                mapper.writeValueAsBytes(entity));

        entity.value = 99;
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 45 76 61 6c 75 65 28 63 03"),
                mapper.writeValueAsBytes(entity));
    }

    void testGlobalMatchesPerClassBehaviorVpack() throws Exception {
        ObjectMapper globalMapper = realNonDefaultMapper();
        ObjectMapper defaultMapper = new VPackMapper();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                defaultMapper.writeValueAsBytes(new AnnotatedEntity()));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                globalMapper.writeValueAsBytes(new Entity()));
    }
private static ObjectMapper realNonDefaultMapper() {
        return VPackMapper.builder()
                .enable(MapperFeature.USE_REAL_INCLUDE_NON_DEFAULT)
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
    }
@JsonFilter("RootFilter")
    @JsonPropertyOrder({ "a", "b" })
    static class FilterBean {
        public String a = "a";
        public String b = "b";
    }
@JsonPropertyOrder(alphabetic = true)
    static class FilteredProps {
        public FilterBean first = new FilterBean();

        @JsonFilter("b")
        public FilterBean second = new FilterBean();
    }
static class Pod {
        protected String username;
        protected String userPassword;

        public String getUsername() { return username; }
        public void setUsername(String value) { username = value; }

        @JsonIgnore
        @JsonProperty(value = "user_password")
        public String getUserPassword() { return userPassword; }

        @JsonProperty(value = "user_password")
        public void setUserPassword(String value) { userPassword = value; }
    }
static class Entity {
        private String someFieldWithDefault = "a default";

        public void setSomeFieldWithDefault(String value) { someFieldWithDefault = value; }
        public String getSomeFieldWithDefault() { return someFieldWithDefault; }
    }
static class IntDefaultEntity {
        public int value = 42;
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class AnnotatedEntity {
        private String someFieldWithDefault = "a default";

        public String getSomeFieldWithDefault() { return someFieldWithDefault; }
        public void setSomeFieldWithDefault(String value) { someFieldWithDefault = value; }
    }

    void __invoke_testGlobalNonDefaultRoundTripVpack() throws Exception {
        try {
            testGlobalNonDefaultRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testGlobalNonDefaultIncludesNonDefaultVpack() throws Exception {
        try {
            testGlobalNonDefaultIncludesNonDefaultVpack();
        } finally {
        }
    }


    void __invoke_testGlobalNonDefaultEmptyStringNonDefaultVpack() throws Exception {
        try {
            testGlobalNonDefaultEmptyStringNonDefaultVpack();
        } finally {
        }
    }


    void __invoke_testGlobalNonDefaultPrimitiveFieldVpack() throws Exception {
        try {
            testGlobalNonDefaultPrimitiveFieldVpack();
        } finally {
        }
    }


    void __invoke_testGlobalMatchesPerClassBehaviorVpack() throws Exception {
        try {
            testGlobalMatchesPerClassBehaviorVpack();
        } finally {
        }
    }

}
