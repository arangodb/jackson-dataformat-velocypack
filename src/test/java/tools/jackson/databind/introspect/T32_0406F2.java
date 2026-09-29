package tools.jackson.databind.introspect;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0406F2 {
private static final byte[] KEBAB = VPackWireFixtureTest.hex(
            "0b 13 01 4a 66 69 72 73 74 2d 6e 61 6d 65 43 42 6f 62 03");
private static final byte[] LOWER = VPackWireFixtureTest.hex(
            "0b 23 02 49 66 69 72 73 74 6e 61 6d 65 43 42 6f 62 "
          + "48 6c 61 73 74 6e 61 6d 65 46 42 75 72 67 65 72 03 11");
private static final byte[] DOT = VPackWireFixtureTest.hex(
            "0b 13 01 4a 66 69 72 73 74 2e 6e 61 6d 65 43 42 6f 62 03");
private static final byte[] SNAKE_3368 = VPackWireFixtureTest.hex(
            "0b 29 01 49 74 69 6d 65 5f 7a 6f 6e 65 "
          + "0b 1b 02 44 6e 61 6d 65 43 58 58 58 "
          + "48 75 74 63 5f 7a 6f 6e 65 43 5a 5a 5a 03 0c 03");
private static final byte[] UPPER_PERSON = VPackWireFixtureTest.hex(
            "0b 2d 03 4a 46 49 52 53 54 5f 4e 41 4d 45 43 4a 6f 65 "
          + "49 4c 41 53 54 5f 4e 41 4d 45 47 53 69 78 70 61 63 6b "
          + "43 41 47 45 28 2a 24 03 12");
private static final byte[] VALUE_NAME = VPackWireFixtureTest.hex(
            "0b 0d 01 44 6e 61 6d 65 43 42 6f 62 03");
private static final byte[] VALUE_STUFF = VPackWireFixtureTest.hex(
            "0b 0e 01 45 73 74 75 66 66 43 42 6f 62 03");
private static final byte[] VALUE_STR = VPackWireFixtureTest.hex(
            "0b 13 01 43 73 74 72 4a 74 68 65 20 73 74 72 69 6e 67 03");
private static final byte[] VALUE_ONE = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 31 03");
private static final byte[] VALUE_TWO = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 32 03");
private static final byte[] VALUE_FOUR = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 34 03");
private static final byte[] CLEAVE_BOTH = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 37 41 62 37 03 06");
private static final byte[] CLEAVE_B = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 37 03");
private static final List<String[]> UPPER_SNAKE_CASE_TRANSLATIONS = List.of(
            new String[] { "", "" }, new String[] { "a", "A" },
            new String[] { "abc", "ABC" }, new String[] { "1", "1" },
            new String[] { "123", "123" }, new String[] { "1a", "1A" },
            new String[] { "a1", "A1" }, new String[] { "$", "$" },
            new String[] { "$a", "$A" }, new String[] { "a$", "A$" },
            new String[] { "$_a", "$_A" }, new String[] { "a_$", "A_$" },
            new String[] { "a$a", "A$A" }, new String[] { "$A", "$_A" },
            new String[] { "$_A", "$_A" }, new String[] { "_", "_" },
            new String[] { "__", "_" }, new String[] { "___", "__" },
            new String[] { "A", "A" }, new String[] { "A1", "A1" },
            new String[] { "1A", "1_A" }, new String[] { "_a", "A" },
            new String[] { "_A", "A" }, new String[] { "a_a", "A_A" },
            new String[] { "a_A", "A_A" }, new String[] { "A_A", "A_A" },
            new String[] { "A_a", "A_A" }, new String[] { "WWW", "WWW" },
            new String[] { "someURI", "SOME_URI" },
            new String[] { "someURIs", "SOME_URIS" },
            new String[] { "Results", "RESULTS" },
            new String[] { "_Results", "RESULTS" },
            new String[] { "_results", "RESULTS" },
            new String[] { "__results", "_RESULTS" },
            new String[] { "__Results", "_RESULTS" },
            new String[] { "___results", "__RESULTS" },
            new String[] { "___Results", "__RESULTS" },
            new String[] { "userName", "USER_NAME" },
            new String[] { "user_name", "USER_NAME" },
            new String[] { "user__name", "USER__NAME" },
            new String[] { "UserName", "USER_NAME" },
            new String[] { "User_Name", "USER_NAME" },
            new String[] { "User__Name", "USER__NAME" },
            new String[] { "_user_name", "USER_NAME" },
            new String[] { "_UserName", "USER_NAME" },
            new String[] { "_User_Name", "USER_NAME" },
            new String[] { "USER_NAME", "USER_NAME" },
            new String[] { "_Bars", "BARS" }, new String[] { "usId", "US_ID" },
            new String[] { "uId", "U_ID" }, new String[] { "xCoordinate", "X_COORDINATE" });

    // Provenance: TestPropertyRename#testCreatorPropRenameWithCleave().
    void testCreatorPropRenameWithCleaveVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(CLEAVE_BOTH,
                mapper.writeValueAsBytes(new Bean323WithExplicitCleave1(7)));
        assertArrayEquals(CLEAVE_B,
                mapper.writeValueAsBytes(new Bean323WithExplicitCleave2(7)));
    }
@JsonPropertyOrder({ "firstName", "lastName" })
    @tools.jackson.databind.annotation.JsonNaming(PropertyNamingStrategies.LowerCaseStrategy.class)
    static class BoringBean {
        public String firstName = "Bob";
        public String lastName = "Burger";
    }
static class FirstNameBean {
        public String firstName;
        FirstNameBean() { }
        FirstNameBean(String name) { firstName = name; }
    }
static class Value3368 {
        private String timeZone;
        private String utcZone;

        @JsonProperty("time_zone")
        void unpackTimeZone(java.util.Map<String, String> timeZone) {
            this.setTimeZone(timeZone.get("name"));
            this.setUtcZone(timeZone.get("utc_zone"));
        }

        public String getTimeZone() { return timeZone; }
        public String getUtcZone() { return utcZone; }
        public void setTimeZone(String value) { timeZone = value; }
        public void setUtcZone(String value) { utcZone = value; }
    }
@JsonPropertyOrder({ "firstName", "lastName", "age" })
    static class PersonBean {
        public String firstName;
        public String lastName;
        public int age;
        PersonBean() { }
        PersonBean(String first, String last, int value) {
            firstName = first; lastName = last; age = value;
        }
    }
static class BeanWithConflict {
        public int getX() { return 3; }
        public boolean getx() { return false; }
    }
protected static class Getters1A {
        @JsonProperty
        protected int value = 3;
        public int getValue() { return value + 1; }
        public boolean isValue() { return false; }
    }
protected static class Getters1B {
        public boolean isValue() { return false; }
        @JsonProperty
        protected int value = 3;
        public int getValue() { return value + 1; }
    }
protected static class InferingIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            String name = member.getName();
            return name.startsWith("_") ? name.substring(1) : null;
        }
    }
static class Infernal {
        public String _name() { return "foo"; }
        public String getName() { return "Bob"; }
        public void setStuff(String value) { }
        public void _stuff(String value) { throw new UnsupportedOperationException(); }
    }
static class Bean541 {
        protected String str;
        @JsonCreator
        public Bean541(@JsonProperty("str") String value) { str = value; }
        @JsonProperty("s")
        public String getStr() { return str; }
    }
@JsonPropertyOrder({ "a", "b" })
    static class Bean323WithExplicitCleave1 {
        @JsonProperty("a")
        private int a;
        public Bean323WithExplicitCleave1(@JsonProperty("a") int value) { a = value; }
        @JsonProperty("b")
        private int getA() { return a; }
    }
@JsonPropertyOrder({ "a", "b" })
    static class Bean323WithExplicitCleave2 {
        @JsonProperty("b")
        private int a;
        public Bean323WithExplicitCleave2(@JsonProperty("a") int value) { a = value; }
        @JsonProperty("b")
        private int getA() { return a; }
    }

    void __invoke_testCreatorPropRenameWithCleaveVpack() throws Exception {
        try {
            testCreatorPropRenameWithCleaveVpack();
        } finally {
        }
    }

}
