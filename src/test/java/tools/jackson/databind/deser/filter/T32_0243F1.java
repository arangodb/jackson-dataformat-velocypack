package tools.jackson.databind.deser.filter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0243F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] UNKNOWN_ENUM = VPackWireFixtureTest.hex(
            "14 16 45 74 79 70 65 73 13 0d 43 54 57 4f 45 54 48 52 45 45 02 01");
private static final byte[] UNKNOWN_SUBTYPE = VPackWireFixtureTest.hex(
            "13 1f 14 0e 44 74 79 70 65 45 54 59 50 45 31 01 "
          + "14 0e 44 74 79 70 65 45 54 59 50 45 32 01 02");
private static final byte[] FILTERED_OBJECT = VPackWireFixtureTest.hex(
            "14 20 45 40 74 79 70 65 43 78 78 78 41 61 "
          + "14 11 45 40 74 79 70 65 43 79 79 79 41 62 28 0b 02 02");
private static final byte[] READ_ONLY_95 = VPackWireFixtureTest.hex(
            "14 15 45 76 61 6c 75 65 33 48 63 6f 6d 70 75 74 65 64 28 20 02");
private static final byte[] READ_ONLY_ONE_FIELD = VPackWireFixtureTest.hex(
            "14 0d 48 74 65 73 74 45 6e 75 6d 40 01");
private static final byte[] READ_ONLY_TWO_FIELDS = VPackWireFixtureTest.hex(
            "14 1c 48 74 65 73 74 45 6e 75 6d 40 44 6e 61 6d 65 "
          + "49 63 68 61 6e 67 79 6f 6e 67 02");
private static final byte[] LOGIN = VPackWireFixtureTest.hex(
            "14 0d 45 6c 6f 67 69 6e 43 66 6f 6f 01");
private static final byte[] LOGIN_PASSWORD = VPackWireFixtureTest.hex(
            "14 1a 45 6c 6f 67 69 6e 43 66 6f 6f 48 70 61 73 73 77 6f 72 64 "
          + "43 62 61 72 02");
private static final byte[] LOGIN_USERNAME = VPackWireFixtureTest.hex(
            "14 1a 45 6c 6f 67 69 6e 43 66 6f 6f 48 75 73 65 72 6e 61 6d 65 "
          + "43 62 61 72 02");

    // Provenance: ParserFilterViaMapTest#testSimplePropertyExcludeFilter.
    void testSimplePropertyExcludeFilterVpack() throws Exception {
        try (JsonParser parser = new FilteringParserDelegate(
                MAPPER.createParser(FILTERED_OBJECT),
                new NoTypeFilter(),
                TokenFilter.Inclusion.INCLUDE_ALL_AND_PATH,
                true)) {
            Map<?, ?> actual = MAPPER.readValue(parser, Map.class);
            assertEquals(Map.of("a", Map.of("b", 11)), actual);
        }
    }
static class Data1 {
        public List<Type> types;
    }
enum Type {
        ONE, TWO
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type",
            include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
    @JsonSubTypes(value = { @JsonSubTypes.Type(value = DataType1.class, names = { "TYPE1" }) })
    static abstract class Data2 {
        public String type;
    }
static class DataType1 extends Data2 { }
static class NoTypeFilter extends TokenFilter {
        @Override
        public TokenFilter includeProperty(String name) {
            return "@type".equals(name) ? null : this;
        }
    }
@JsonIgnoreProperties(value = { "computed" }, allowGetters = true)
    static class ReadOnly95Bean {
        public int value = 3;

        public int getComputed() { return 32; }
    }
static class Person {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        public TestEnum getTestEnum() { return testEnum; }

        public void setTestEnum(TestEnum testEnum) { this.testEnum = testEnum; }
    }
enum TestEnum {
        DEFAULT, TEST
    }
static class UserWithReadOnly {
        @JsonProperty(value = "username", access = JsonProperty.Access.READ_ONLY)
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public String password;
        public String login;
    }

    void __invoke_testSimplePropertyExcludeFilterVpack() throws Exception {
        try {
            testSimplePropertyExcludeFilterVpack();
        } finally {
        }
    }

}
