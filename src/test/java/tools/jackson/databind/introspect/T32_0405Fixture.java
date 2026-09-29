package tools.jackson.databind.introspect;

import java.util.List;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0405Fixture {
private static final byte[] ACRONYMS = VPackWireFixtureTest.hex(
            "0b 58 03 43 77 77 77 4e 77 6f 72 6c 64 20 77 69 64 65 20 77 65 62 "
          + "48 73 6f 6d 65 5f 75 72 6c 5b 68 74 74 70 3a 2f 2f 6a 61 63 6b 73 6f 6e "
          + "2e 63 6f 64 65 68 61 75 73 2e 6f 72 67 49 73 6f 6d 65 5f 75 72 69 73 "
          + "4f 2f 70 61 74 68 31 2f 2c 2f 70 61 74 68 32 2f 3b 16 03");
private static final byte[] OTHER_NAMES = VPackWireFixtureTest.hex(
            "0b 36 04 46 24 5f 75 73 65 72 45 24 55 73 65 72 "
          + "42 5f 5f 43 5f 5f 5f 47 72 65 73 75 6c 74 73 47 52 65 73 75 6c 74 73 "
          + "44 75 73 65 72 45 5f 55 73 65 72 03 10 17 27");
private static final byte[] UNCHANGED_NAMES = VPackWireFixtureTest.hex(
            "0b 54 05 49 66 72 6f 6d 5f 75 73 65 72 49 66 72 6f 6d 5f 75 73 65 72 "
          + "44 75 73 65 72 45 5f 75 73 65 72 49 66 72 6f 6d 24 75 73 65 72 "
          + "49 66 72 6f 6d 24 75 73 65 72 49 66 72 6f 6d 37 75 73 65 72 "
          + "49 66 72 6f 6d 37 75 73 65 72 41 78 42 5f 78 22 36 03 17 4a");
private static final byte[] PERSON = VPackWireFixtureTest.hex(
            "0b 2d 03 4a 66 69 72 73 74 5f 6e 61 6d 65 43 4a 6f 65 "
          + "49 6c 61 73 74 5f 6e 61 6d 65 47 53 69 78 70 61 63 6b "
          + "43 61 67 65 28 2a 24 03 12");
private static final byte[] EXPLICIT_DEFAULT = VPackWireFixtureTest.hex(
            "0b 33 03 49 66 69 72 73 74 4e 61 6d 65 45 50 65 74 65 72 "
          + "48 6c 61 73 74 4e 61 6d 65 47 56 65 6e 6b 6d 61 6e "
          + "48 75 73 65 72 5f 61 67 65 42 33 35 03 13 24");
private static final byte[] EXPLICIT_RENAMED = VPackWireFixtureTest.hex(
            "0b 35 03 4a 66 69 72 73 74 5f 6e 61 6d 65 45 50 65 74 65 72 "
          + "49 6c 61 73 74 5f 6e 61 6d 65 47 56 65 6e 6b 6d 61 6e "
          + "48 75 73 65 72 5f 61 67 65 42 33 35 03 14 26");
private static final byte[] EXPLICIT_INPUT = VPackWireFixtureTest.hex(
            "0b 35 03 4a 66 69 72 73 74 5f 6e 61 6d 65 44 45 67 6f 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 48 53 70 65 6e 67 6c 65 72 "
          + "48 75 73 65 72 5f 61 67 65 42 33 32 03 13 26");
private static final byte[] BEAN428 = VPackWireFixtureTest.hex(
            "0b 0c 01 46 66 6f 6f 42 61 72 40 03");
private static final byte[] OBJECT_NODE = VPackWireFixtureTest.hex(
            "14 21 42 69 64 41 31 44 6a 73 6f 6e 14 14 43 66 6f 6f 43 62 61 72 "
          + "43 62 61 7a 44 62 69 6e 67 02 02");
private static final byte[] CONSTRUCTOR_PARAMS = VPackWireFixtureTest.hex(
            "14 1f 42 69 64 46 66 6f 6f 62 61 72 49 66 75 6c 6c 5f 6e 61 6d 65 "
          + "47 46 6f 6f 20 42 61 72 02");
private static final List<String[]> SNAKE_CASE_TRANSLATIONS = List.of(
            new String[] { "", "" }, new String[] { "a", "a" },
            new String[] { "abc", "abc" }, new String[] { "1", "1" },
            new String[] { "123", "123" }, new String[] { "1a", "1a" },
            new String[] { "a1", "a1" }, new String[] { "$", "$" },
            new String[] { "$a", "$a" }, new String[] { "a$", "a$" },
            new String[] { "$_a", "$_a" }, new String[] { "a_$", "a_$" },
            new String[] { "a$a", "a$a" }, new String[] { "$A", "$_a" },
            new String[] { "$_A", "$_a" }, new String[] { "_", "_" },
            new String[] { "__", "_" }, new String[] { "___", "__" },
            new String[] { "A", "a" }, new String[] { "A1", "a1" },
            new String[] { "1A", "1_a" }, new String[] { "_a", "a" },
            new String[] { "_A", "a" }, new String[] { "a_a", "a_a" },
            new String[] { "a_A", "a_a" }, new String[] { "A_A", "a_a" },
            new String[] { "A_a", "a_a" }, new String[] { "WWW", "www" },
            new String[] { "someURI", "some_uri" },
            new String[] { "someURIs", "some_uris" },
            new String[] { "Results", "results" },
            new String[] { "_Results", "results" },
            new String[] { "_results", "results" },
            new String[] { "__results", "_results" },
            new String[] { "__Results", "_results" },
            new String[] { "___results", "__results" },
            new String[] { "___Results", "__results" },
            new String[] { "userName", "user_name" },
            new String[] { "user_name", "user_name" },
            new String[] { "user__name", "user__name" },
            new String[] { "UserName", "user_name" },
            new String[] { "User_Name", "user_name" },
            new String[] { "User__Name", "user__name" },
            new String[] { "_user_name", "user_name" },
            new String[] { "_UserName", "user_name" },
            new String[] { "_User_Name", "user_name" },
            new String[] { "UGLY_NAME", "ugly_name" },
            new String[] { "_Bars", "bars" }, new String[] { "usId", "us_id" },
            new String[] { "uId", "u_id" },
            new String[] { "xCoordinate", "x_coordinate" },
            new String[] { "RGBA", "rgba" }, new String[] { "RGBa", "rgba" });

    // Provenance: TestNamingStrategyStd#testLowerCaseStrategyStandAlone().
    void testLowerCaseStrategyStandAloneVpack() {
        for (String[] pair : SNAKE_CASE_TRANSLATIONS) {
            assertEquals(pair[1], PropertyNamingStrategies.SNAKE_CASE
                    .nameForField(null, null, pair[0]));
        }
    }

    // Provenance: TestNamingStrategyStd#testLowerCaseTranslations().
    void testLowerCaseTranslationsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        assertArrayEquals(PERSON, mapper.writeValueAsBytes(new PersonBean(
                "Joe", "Sixpack", 42)));
        PersonBean result = mapper.readValue(PERSON, PersonBean.class);
        assertEquals("Joe", result.firstName);
        assertEquals("Sixpack", result.lastName);
        assertEquals(42, result.age);
    }

    // Provenance: TestNamingStrategyStd#testLowerCaseAcronymsTranslations().
    void testLowerCaseAcronymsTranslationsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        assertArrayEquals(ACRONYMS, mapper.writeValueAsBytes(new Acronyms(
                "world wide web", "http://jackson.codehaus.org", "/path1/,/path2/")));
        Acronyms result = mapper.readValue(ACRONYMS, Acronyms.class);
        assertEquals("world wide web", result.WWW);
        assertEquals("http://jackson.codehaus.org", result.someURL);
        assertEquals("/path1/,/path2/", result.someURIs);
    }

    // Provenance: TestNamingStrategyStd#testLowerCaseOtherNonStandardNamesTranslations().
    void testLowerCaseOtherNonStandardNamesTranslationsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        byte[] encoded = mapper.writeValueAsBytes(new OtherNonStandardNames(
                "Results", "_User", "___", "$User"));
        assertArrayEquals(OTHER_NAMES, encoded, () -> Arrays.toString(encoded));
        OtherNonStandardNames result = mapper.readValue(OTHER_NAMES,
                OtherNonStandardNames.class);
        assertEquals("Results", result.Results);
        assertEquals("_User", result._User);
        assertEquals("___", result.___);
        assertEquals("$User", result.$User);
    }

    // Provenance: TestNamingStrategyStd#testLowerCaseUnchangedNames().
    void testLowerCaseUnchangedNamesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        byte[] encoded = mapper.writeValueAsBytes(new UnchangedNames(
                "from_user", "_user", "from$user", "from7user", "_x"));
        assertArrayEquals(UNCHANGED_NAMES, encoded, () -> Arrays.toString(encoded));
        UnchangedNames result = mapper.readValue(UNCHANGED_NAMES, UnchangedNames.class);
        assertEquals("from_user", result.from_user);
        assertEquals("_user", result._user);
        assertEquals("from$user", result.from$user);
        assertEquals("from7user", result.from7user);
        assertEquals("_x", result._x);
    }

    // Provenance: TestNamingStrategyStd#testPascalCaseStandAlone().
    void testPascalCaseStandAloneVpack() {
        assertEquals("UserName", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForField(null, null, "userName"));
        assertEquals("User", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForField(null, null, "User"));
        assertEquals("User", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForField(null, null, "user"));
        assertEquals("X", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForField(null, null, "x"));
        assertEquals("BADPublicName", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForField(null, null, "bADPublicName"));
        assertEquals("BADPublicName", PropertyNamingStrategies.UPPER_CAMEL_CASE
                .nameForGetterMethod(null, null, "bADPublicName"));
    }

    // Provenance: TestNamingStrategyStd#testIssue428PascalWithOverrides().
    void testIssue428PascalWithOverridesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE).build();
        assertArrayEquals(BEAN428, mapper.writeValueAsBytes(new Bean428()));
    }

    // Provenance: TestNamingStrategyStd#testKebabCaseStrategyStandAlone().
    void testKebabCaseStrategyStandAloneVpack() {
        assertEquals("some-value", PropertyNamingStrategies.KEBAB_CASE
                .nameForField(null, null, "someValue"));
        assertEquals("some-value", PropertyNamingStrategies.KEBAB_CASE
                .nameForField(null, null, "SomeValue"));
        assertEquals("url", PropertyNamingStrategies.KEBAB_CASE
                .nameForField(null, null, "URL"));
        assertEquals("url-stuff", PropertyNamingStrategies.KEBAB_CASE
                .nameForField(null, null, "URLStuff"));
        assertEquals("some-url-stuff", PropertyNamingStrategies.KEBAB_CASE
                .nameForField(null, null, "SomeURLStuff"));
    }

    // Provenance: TestNamingStrategyStd#testLowerCaseWithDotsStrategyStandAlone().
    void testLowerCaseWithDotsStrategyStandAloneVpack() {
        assertEquals("some.value", PropertyNamingStrategies.LOWER_DOT_CASE
                .nameForField(null, null, "someValue"));
        assertEquals("some.value", PropertyNamingStrategies.LOWER_DOT_CASE
                .nameForField(null, null, "SomeValue"));
        assertEquals("url", PropertyNamingStrategies.LOWER_DOT_CASE
                .nameForField(null, null, "URL"));
        assertEquals("url.stuff", PropertyNamingStrategies.LOWER_DOT_CASE
                .nameForField(null, null, "URLStuff"));
        assertEquals("some.url.stuff", PropertyNamingStrategies.LOWER_DOT_CASE
                .nameForField(null, null, "SomeURLStuff"));
    }

    // Provenance: TestNamingStrategyStd#testNamingWithObjectNode().
    void testNamingWithObjectNodeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE).build();
        ClassWithObjectNodeField result = mapper.readValue(OBJECT_NODE,
                ClassWithObjectNodeField.class);
        assertNotNull(result);
        assertEquals("1", result.id);
        assertNotNull(result.json);
        assertEquals(2, result.json.size());
        assertEquals("bing", result.json.path("baz").asString());
    }

    // Provenance: TestNamingStrategyStd#testExplicitRename().
    void testExplicitRenameVpack() throws Exception {
        ObjectMapper unchanged = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        assertArrayEquals(EXPLICIT_DEFAULT, unchanged.writeValueAsBytes(new ExplicitBean()));

        ObjectMapper renamed = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .enable(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING).build();
        byte[] encoded = renamed.writeValueAsBytes(new ExplicitBean());
        assertArrayEquals(EXPLICIT_RENAMED, encoded, () -> Arrays.toString(encoded));
        ExplicitBean bean = renamed.readValue(EXPLICIT_INPUT, ExplicitBean.class);
        assertNotNull(bean);
        assertEquals("Egon", bean.userFirstName);
        assertEquals("Spengler", bean.userLastName);
        assertEquals("32", bean.userAge);
    }

    // Provenance: TestNamingStrategyStd#testNamingViaConstructorParams().
    void testNamingViaConstructorParamsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new ParamNameIntrospector()).build();
        SnakeNameBean value = mapper.readValue(CONSTRUCTOR_PARAMS, SnakeNameBean.class);
        assertEquals("foobar", value._id);
        assertEquals("Foo Bar", value._fullName);
    }
@JsonPropertyOrder({ "www", "some_url", "some_uris" })
    static class Acronyms {
        public String WWW;
        public String someURL;
        public String someURIs;
        Acronyms() { }
        Acronyms(String www, String url, String uris) {
            WWW = www; someURL = url; someURIs = uris;
        }
    }
@JsonPropertyOrder({ "from_user", "user", "from$user", "from7user", "_x" })
    static class UnchangedNames {
        public String from_user;
        public String _user;
        public String from$user;
        public String from7user;
        public String _x;
        UnchangedNames() { }
        UnchangedNames(String a, String b, String c, String d, String e) {
            from_user = a; _user = b; from$user = c; from7user = d; _x = e;
        }
    }
static class OtherNonStandardNames {
        public String Results;
        public String _User;
        public String ___;
        public String $User;
        OtherNonStandardNames() { }
        OtherNonStandardNames(String a, String b, String c, String d) {
            Results = a; _User = b; ___ = c; $User = d;
        }
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
static class Bean428 {
        @JsonProperty("fooBar")
        public String whatever() { return ""; }
    }
static class ClassWithObjectNodeField {
        public String id;
        public ObjectNode json;
    }
static class ExplicitBean {
        @JsonProperty("firstName")
        String userFirstName = "Peter";
        @JsonProperty("lastName")
        String userLastName = "Venkman";
        @JsonProperty
        String userAge = "35";
    }
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class SnakeNameBean {
        String _id;
        String _fullName;
        @JsonCreator
        SnakeNameBean(@Name("id") String id, @Name("fullName") String fullName) {
            _id = id; _fullName = fullName;
        }
    }
@java.lang.annotation.Target({ java.lang.annotation.ElementType.FIELD,
            java.lang.annotation.ElementType.PARAMETER, java.lang.annotation.ElementType.TYPE })
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @com.fasterxml.jackson.annotation.JacksonAnnotation
    public @interface Name { String value(); }
@SuppressWarnings("serial")
    static class ParamNameIntrospector
            extends tools.jackson.databind.introspect.JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            Name name = member.getAnnotation(Name.class);
            return (name == null) ? super.findImplicitPropertyName(config, member) : name.value();
        }
    }

    void __invoke_testLowerCaseStrategyStandAloneVpack() throws Exception {
        try {
            testLowerCaseStrategyStandAloneVpack();
        } finally {
        }
    }


    void __invoke_testLowerCaseTranslationsVpack() throws Exception {
        try {
            testLowerCaseTranslationsVpack();
        } finally {
        }
    }


    void __invoke_testLowerCaseAcronymsTranslationsVpack() throws Exception {
        try {
            testLowerCaseAcronymsTranslationsVpack();
        } finally {
        }
    }


    void __invoke_testLowerCaseOtherNonStandardNamesTranslationsVpack() throws Exception {
        try {
            testLowerCaseOtherNonStandardNamesTranslationsVpack();
        } finally {
        }
    }


    void __invoke_testLowerCaseUnchangedNamesVpack() throws Exception {
        try {
            testLowerCaseUnchangedNamesVpack();
        } finally {
        }
    }


    void __invoke_testPascalCaseStandAloneVpack() throws Exception {
        try {
            testPascalCaseStandAloneVpack();
        } finally {
        }
    }


    void __invoke_testIssue428PascalWithOverridesVpack() throws Exception {
        try {
            testIssue428PascalWithOverridesVpack();
        } finally {
        }
    }


    void __invoke_testKebabCaseStrategyStandAloneVpack() throws Exception {
        try {
            testKebabCaseStrategyStandAloneVpack();
        } finally {
        }
    }


    void __invoke_testLowerCaseWithDotsStrategyStandAloneVpack() throws Exception {
        try {
            testLowerCaseWithDotsStrategyStandAloneVpack();
        } finally {
        }
    }


    void __invoke_testNamingWithObjectNodeVpack() throws Exception {
        try {
            testNamingWithObjectNodeVpack();
        } finally {
        }
    }


    void __invoke_testExplicitRenameVpack() throws Exception {
        try {
            testExplicitRenameVpack();
        } finally {
        }
    }


    void __invoke_testNamingViaConstructorParamsVpack() throws Exception {
        try {
            testNamingViaConstructorParamsVpack();
        } finally {
        }
    }

}
