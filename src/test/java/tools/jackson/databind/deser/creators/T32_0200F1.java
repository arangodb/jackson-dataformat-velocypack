package tools.jackson.databind.deser.creators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0200F1 {
private static final byte[] URI_FOO = VPackWireFixtureTest.hex(
            "14 0b 43 75 72 69 43 66 6f 6f 01");
private static final byte[] REDACTED_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0f 01 43 75 72 69 46 6d 79 2d 2a 2a 2a 03");
private static final byte[] CONSTRAINTS_YX = VPackWireFixtureTest.hex(
            "14 09 41 79 32 41 78 31 02");
private static final byte[] CONSTRAINTS_XY = VPackWireFixtureTest.hex(
            "14 09 41 78 33 41 79 34 02");
private static final byte[] CONSTRAINTS_X_ONLY = VPackWireFixtureTest.hex(
            "14 06 41 78 33 01");
private static final byte[] CONSTRAINTS_Y_ONLY = VPackWireFixtureTest.hex(
            "14 06 41 79 33 01");
private static final byte[] REQUIRED_PARAMETER_INPUT = VPackWireFixtureTest.hex(
            "14 0d 46 73 74 61 74 75 73 42 4f 4b 01");
private static final byte[] DUPLICATE_VALUE = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 31 45 76 61 6c 75 65 32 02");
private static final byte[] READ_ONLY_CREATOR = VPackWireFixtureTest.hex(
            "14 0f 43 66 6f 6f 41 61 43 62 61 72 41 62 02");
private static final byte[] UPDATED_VALUES = VPackWireFixtureTest.hex(
            "14 10 46 76 61 6c 75 65 73 02 06 41 41 41 42 01");
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .build();
private static final ObjectMapper UNKNOWN_PROPERTIES_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: CreatorPropertyConstraintsTest#testRequiredAnnotatedParam.
    void testRequiredAnnotatedParam() throws Exception {
        FascistPoint point = MAPPER.readValue(CONSTRAINTS_YX, FascistPoint.class);
        assertEquals(Integer.valueOf(1), point.x);
        assertEquals(Integer.valueOf(2), point.y);

        point = MAPPER.readValue(CONSTRAINTS_XY, FascistPoint.class);
        assertEquals(Integer.valueOf(3), point.x);
        assertEquals(Integer.valueOf(4), point.y);

        point = MAPPER.readValue(CONSTRAINTS_X_ONLY, FascistPoint.class);
        assertEquals(Integer.valueOf(3), point.x);
        assertNull(point.y);

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(CONSTRAINTS_Y_ONLY, FascistPoint.class));
        assertEquals("x", exception.getPath().get(0).getPropertyName());
        assertTrueContains(exception, "Missing required creator property 'x'");
    }

    // Provenance: CreatorPropertyConstraintsTest#testRequiredGloballyParam.
    void testRequiredGloballyParam() throws Exception {
        FascistPoint point = MAPPER.readValue(CONSTRAINTS_X_ONLY, FascistPoint.class);
        assertEquals(Integer.valueOf(3), point.x);
        assertNull(point.y);

        ObjectMapper requiredMapper = MAPPER.rebuild()
                .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .build();
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> requiredMapper.readValue(CONSTRAINTS_X_ONLY, FascistPoint.class));
        assertTrueContains(exception, "Missing creator property 'y'");
    }

    // Provenance: CreatorPropertyConstraintsTest#testRequiredViaParameter2591.
    void testRequiredViaParameter2591() {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(REQUIRED_PARAMETER_INPUT, LoginUserResponse.class));
        assertTrueContains(exception, "Missing required creator property 'otp'");
    }

    // Provenance: CreatorPropertyConstraintsTest#testCreatorFallback2438.
    void testCreatorFallback2438() throws Exception {
        Creator2438 bean = MAPPER.readValue(DUPLICATE_VALUE, Creator2438.class);
        assertEquals("Creator:2", bean.value);
    }

    // Provenance: CreatorPropertyConstraintsTest#testCreatorWithReadOnly4119.
    void testCreatorWithReadOnly4119() throws Exception {
        Bean4119 bean = MAPPER.readerFor(Bean4119.class).readValue(READ_ONLY_CREATOR);
        assertEquals("a", bean.foo);
        assertNull(bean.bar);
    }
private static void assertTrueContains(MismatchedInputException exception, String text) {
        if (exception.getMessage() == null || !exception.getMessage().contains(text)) {
            throw new AssertionError("Expected exception message to contain: " + text
                    + "; was: " + exception.getMessage());
        }
    }
static class FieldBean {
        @JsonProperty(value = "uri", access = JsonProperty.Access.READ_ONLY)
        public final String redactedUri;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public FieldBean(@ImplicitName("uri") String uri) {
            this.redactedUri = uri;
        }
    }
static class GetterBean {
        private final String uri;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public GetterBean(@ImplicitName("uri") String uri) {
            this.uri = uri;
        }

        @JsonProperty(value = "uri", access = JsonProperty.Access.READ_ONLY)
        public String getRedactedUri() {
            return uri;
        }
    }
static class WriteOnlyCtorBean {
        @JsonProperty(value = "uri", access = JsonProperty.Access.READ_ONLY)
        public final String redactedUri;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public WriteOnlyCtorBean(@ImplicitName("uri")
                @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String uri) {
            this.redactedUri = uri;
        }
    }
static class RedactingRoundTripBean {
        @JsonProperty(value = "uri", access = JsonProperty.Access.READ_ONLY)
        public final String redactedUri;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RedactingRoundTripBean(@ImplicitName("uri") String uri) {
            this.redactedUri = (uri == null) ? null : uri.replace("password", "***");
        }
    }
static class FascistPoint {
        Integer x, y;

        @JsonCreator
        public FascistPoint(@JsonProperty(value = "x", required = true) Integer x,
                @JsonProperty(value = "y", isRequired = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
                Integer y) {
            this.x = x;
            this.y = y;
        }
    }
static class LoginUserResponse {
        private String otp;
        private String userType;

        @JsonCreator
        public LoginUserResponse(
                @JsonProperty(value = "otp", isRequired = com.fasterxml.jackson.annotation.OptBoolean.TRUE)
                String otp,
                @JsonProperty(value = "userType", required = true) String userType) {
            this.otp = otp;
            this.userType = userType;
        }
    }
static class Creator2438 {
        String value = "";

        @JsonCreator
        public Creator2438(@JsonProperty("value") int value) {
            this.value = "Creator:" + value;
        }

        public void setValue(int value) {
            this.value = "Setter:" + value;
        }
    }
static class Bean4119 {
        String foo, bar;

        @JsonCreator
        public Bean4119(@JsonProperty("foo") String foo,
                @JsonProperty(value = "bar", access = JsonProperty.Access.READ_ONLY) String bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
static class ArrayListHolder {
        Collection<String> values;

        public ArrayListHolder(String... values) {
            this.values = new ArrayList<>();
            this.values.addAll(Arrays.asList(values));
        }

        public void setValues(Collection<String> values) {
            this.values = values;
        }
    }
static class MatchingTypeHolder {
        Collection<String> values;

        public MatchingTypeHolder(Collection<String> values) {
            this.values = (values == null) ? new ArrayList<>() : new ArrayList<>(values);
        }

        public void setValues(Collection<String> values) {
            this.values = values;
        }
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                ImplicitName annotation = parameter.getAnnotation(ImplicitName.class);
                if (annotation != null) {
                    return annotation.value();
                }
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_testRequiredAnnotatedParam() throws Exception {
        try {
            testRequiredAnnotatedParam();
        } finally {
        }
    }


    void __invoke_testRequiredGloballyParam() throws Exception {
        try {
            testRequiredGloballyParam();
        } finally {
        }
    }


    void __invoke_testRequiredViaParameter2591() throws Exception {
        try {
            testRequiredViaParameter2591();
        } finally {
        }
    }


    void __invoke_testCreatorFallback2438() throws Exception {
        try {
            testCreatorFallback2438();
        } finally {
        }
    }


    void __invoke_testCreatorWithReadOnly4119() throws Exception {
        try {
            testCreatorWithReadOnly4119();
        } finally {
        }
    }

}
