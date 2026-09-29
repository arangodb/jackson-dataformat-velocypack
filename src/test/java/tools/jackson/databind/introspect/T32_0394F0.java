package tools.jackson.databind.introspect;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.introspect.PotentialCreator;
import tools.jackson.databind.EnumNamingStrategies;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0394F0 {
private static final byte[] CREATOR_INPUT = VPackWireFixtureTest.hex(
            "0b 15 02 43 73 74 72 45 76 61 6c 75 65 43 69 6e 74 28 2a 0d 03");
private static final byte[] IGNORED_CREATOR_INPUT = VPackWireFixtureTest.hex(
            "0b 2d 01 49 69 6e 6e 65 72 54 65 73 74 "
          + "0b 1f 02 43 73 74 72 43 73 74 72 "
          + "48 6f 74 68 65 72 53 74 72 48 6f 74 68 65 72 53 74 72 0b 03 03");
private static final byte[] IGNORED_FIELD_INPUT = VPackWireFixtureTest.hex(
            "14 0d 45 71 75 65 72 79 43 62 61 72 01");

    // Provenance: DefaultCreatorResolution4620Test#testCanonicalConstructor1ArgPropertiesCreator().
    void testCanonicalConstructor1ArgPropertiesCreatorVpack() throws Exception {
        POJO4620 result = readerWith(new PrimaryConstructorFindingIntrospector(
                String.class, Integer.TYPE)).readValue(CREATOR_INPUT);
        assertEquals("value/42", result.value);
    }
private static Stream<Arguments> enumNameConversionTestCases() {
        return Stream.of(
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, null, null),
                Arguments.of(EnumNamingStrategies.UPPER_CAMEL_CASE, null, null),
                Arguments.of(EnumNamingStrategies.SNAKE_CASE, null, null),
                Arguments.of(EnumNamingStrategies.UPPER_SNAKE_CASE, null, null),
                Arguments.of(EnumNamingStrategies.LOWER_CASE, null, null),
                Arguments.of(EnumNamingStrategies.KEBAB_CASE, null, null),
                Arguments.of(EnumNamingStrategies.LOWER_DOT_CASE, null, null),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "", ""),

                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a", "a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "abc", "abc"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A", "a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A1", "a1"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "1A", "1a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "ABC", "abc"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User", "user"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "Results", "results"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "WWW", "www"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER", "user"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "userName", "username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "someURI", "someuri"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "someURIs", "someuris"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "theWWW", "thewww"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "uId", "uid"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "usId", "usid"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "UserName", "username"),
                Arguments.of(EnumNamingStrategies.KEBAB_CASE, "UserName", "username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "user", "user"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "xCoordinate", "xcoordinate"),

                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a_", "a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_A", "A"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_a", "A"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a_A", "aA"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a_a", "aA"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A_A", "aA"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A_a", "aA"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "BARS_", "bars"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "BARS", "bars"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "THE_WWW", "theWww"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "U_ID", "uId"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "US_ID", "usId"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "X_COORDINATE", "xCoordinate"),

                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USERNAME_", "username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_User_Name", "UserName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_UserName", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_Username", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_user_name", "UserName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_USERNAME", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "__USERNAME", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "__Username", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "__username", "Username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER______NAME", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER_NAME", "userName"),
                Arguments.of(EnumNamingStrategies.UPPER_CAMEL_CASE, "USER_NAME", "UserName"),
                Arguments.of(EnumNamingStrategies.SNAKE_CASE, "USER_NAME", "user_name"),
                Arguments.of(EnumNamingStrategies.UPPER_SNAKE_CASE, "USER_NAME", "USER_NAME"),
                Arguments.of(EnumNamingStrategies.LOWER_CASE, "USER_NAME", "username"),
                Arguments.of(EnumNamingStrategies.KEBAB_CASE, "USER_NAME", "user-name"),
                Arguments.of(EnumNamingStrategies.LOWER_DOT_CASE, "USER_NAME", "user.name"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER__NAME", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER_NAME_", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User__Name", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER_NAME_S", "userNameS"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_user_name_s", "UserNameS"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USER_NAME_S", "userNameS"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "user__name", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "user_name", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "USERNAME", "username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "username", "username"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User_Name", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User_Name_", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User_Name_", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "User_Name__", "userName"),
                Arguments.of(EnumNamingStrategies.UPPER_SNAKE_CASE, "User_Name__", "USER_NAME"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "user_name_", "userName"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "user_name__", "userName"),

                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a$a", "a$a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A$A", "a$a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a_$", "a$"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a$", "a$"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "a1", "a1"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "$", "$"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A$", "a$"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "1", "1"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "$_A", "$A"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "$_a", "$A"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "1_A", "1A"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "1a", "1a"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "A_$", "a$"),
                Arguments.of(EnumNamingStrategies.LOWER_CAMEL_CASE, "_123_41", "12341")
        );
    }
private ObjectReader readerWith(AnnotationIntrospector introspector) {
        return VPackMapper.builder().annotationIntrospector(introspector)
                .build().readerFor(POJO4620.class);
    }
static class POJO4620 {
        String value;

        public POJO4620(@JsonProperty("int") int i) {
            throw new RuntimeException("Should not get called");
        }

        public POJO4620(@JsonProperty("str") String str,
                @JsonProperty("int") int v) {
            value = str + "/" + v;
        }

        public POJO4620(@JsonProperty("str") String str,
                @JsonProperty("int") int v, @JsonProperty("long") long l) {
            throw new RuntimeException("Should not get called");
        }
    }
static class PrimaryConstructorFindingIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;
        private final Class<?>[] argTypes;

        PrimaryConstructorFindingIntrospector(Class<?>... argTypes) {
            this.argTypes = argTypes;
        }

        @Override
        public PotentialCreator findPreferredCreator(MapperConfig<?> config,
                AnnotatedClass valueClass, List<PotentialCreator> declaredConstructors,
                List<PotentialCreator> declaredFactories,
                Optional<PotentialCreator> zeroParamsConstructor) {
            if (!valueClass.getRawType().toString().contains("4620")) {
                return null;
            }
            for (PotentialCreator creator : declaredConstructors) {
                if (creator.paramCount() != argTypes.length) {
                    continue;
                }
                int i = 0;
                for (; i < argTypes.length; ++i) {
                    if (argTypes[i] != creator.param(i).getRawType()) {
                        break;
                    }
                }
                if (i == argTypes.length) {
                    creator.overrideMode(JsonCreator.Mode.PROPERTIES);
                    return creator;
                }
            }
            return null;
        }
    }
static class InnerTest {
        public String str;
        public String otherStr;
    }
static class OuterTest {
        InnerTest innerTest;

        @JsonIgnore
        String otherOtherStr;

        @JsonCreator
        public OuterTest(InnerTest inner, String otherStr) {
            innerTest = inner;
        }
    }
static class ImplicitNames extends tools.jackson.databind.introspect.JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter param) {
                return switch (param.getIndex()) {
                case 0 -> "innerTest";
                case 1 -> "otherOtherStr";
                default -> null;
                };
            }
            return null;
        }
    }
static class Foo2001 {
        @JsonIgnore
        public String query;

        @JsonCreator
        @ConstructorProperties("rawQuery")
        public Foo2001(@JsonProperty("query") String rawQuery) {
            query = rawQuery;
        }
    }

    void __invoke_testCanonicalConstructor1ArgPropertiesCreatorVpack() throws Exception {
        try {
            testCanonicalConstructor1ArgPropertiesCreatorVpack();
        } finally {
        }
    }

}
