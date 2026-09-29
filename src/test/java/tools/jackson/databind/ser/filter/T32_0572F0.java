package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0572F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testSimpleInclusionFilterVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("RootFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("a"));
        byte[] expected = VPackWireFixtureTest.hex("0b 08 01 41 61 41 61 03");
        assertArrayEquals(expected,
                MAPPER.writer(provider).writeValueAsBytes(new FilterBean()));

        ObjectMapper mapper = VPackMapper.builder().filterProvider(provider).build();
        assertArrayEquals(expected, mapper.writeValueAsBytes(new FilterBean()));
    }

    void testIncludeAllFilterVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("RootFilter",
                SimpleBeanPropertyFilter.serializeAll());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 41 61 41 61 41 62 41 62 03 07"),
                MAPPER.writer(provider).writeValueAsBytes(new FilterBean()));
    }

    void testExcludeAllFilterVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("RootFilter",
                SimpleBeanPropertyFilter.filterOutAll());
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writer(provider).writeValueAsBytes(new FilterBean()));
    }

    void testSimpleExclusionFilterVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("RootFilter",
                SimpleBeanPropertyFilter.serializeAllExcept("a"));
        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 41 62 41 62 03"),
                MAPPER.writer(provider).writeValueAsBytes(new FilterBean()));
    }

    void testMissingFilterVpack() throws Exception {
        InvalidDefinitionException failure = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new FilterBean()));
        assertEquals(true, failure.getMessage().contains(
                "Cannot resolve PropertyFilter with id 'RootFilter'"));

        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        ObjectMapper mapper = VPackMapper.builder().filterProvider(provider).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 41 61 41 61 41 62 41 62 03 07"),
                mapper.writeValueAsBytes(new FilterBean()));
    }

    void testIssue89Vpack() throws Exception {
        Pod pod = new Pod();
        pod.username = "Bob";
        pod.userPassword = "s3cr3t!";
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 48 75 73 65 72 6e 61 6d 65 43 42 6f 62 03"),
                MAPPER.writeValueAsBytes(pod));

        Pod deserialized = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 26 02 48 75 73 65 72 6e 61 6d 65 44 42 69 6c 6c "
              + "4d 75 73 65 72 5f 70 61 73 73 77 6f 72 64 44 66 6f 6f 21 "
              + "11 03"), Pod.class);
        assertEquals("Bill", deserialized.username);
        assertEquals("foo!", deserialized.userPassword);
    }

    void testFilterOnPropertyVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider()
                .addFilter("RootFilter", SimpleBeanPropertyFilter.filterOutAllExcept("a"))
                .addFilter("b", SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
              "0b 22 02 45 66 69 72 73 74 0b 08 01 41 61 41 61 03 "
              + "46 73 65 63 6f 6e 64 0b 08 01 41 62 41 62 03 03 11"),
                MAPPER.writer(provider).writeValueAsBytes(new FilteredProps()));
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

    void __invoke_testSimpleInclusionFilterVpack() throws Exception {
        try {
            testSimpleInclusionFilterVpack();
        } finally {
        }
    }


    void __invoke_testIncludeAllFilterVpack() throws Exception {
        try {
            testIncludeAllFilterVpack();
        } finally {
        }
    }


    void __invoke_testExcludeAllFilterVpack() throws Exception {
        try {
            testExcludeAllFilterVpack();
        } finally {
        }
    }


    void __invoke_testSimpleExclusionFilterVpack() throws Exception {
        try {
            testSimpleExclusionFilterVpack();
        } finally {
        }
    }


    void __invoke_testMissingFilterVpack() throws Exception {
        try {
            testMissingFilterVpack();
        } finally {
        }
    }


    void __invoke_testIssue89Vpack() throws Exception {
        try {
            testIssue89Vpack();
        } finally {
        }
    }


    void __invoke_testFilterOnPropertyVpack() throws Exception {
        try {
            testFilterOnPropertyVpack();
        } finally {
        }
    }

}
