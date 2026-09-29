package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0643F0 {
private static final byte[] ALL_EXTERNAL_FIELDS = VPackWireFixtureTest.hex(
            "14 38 45 6c 61 62 65 6c 42 68 69 "
            + "46 73 65 63 72 65 74 41 73 "
            + "44 6e 6f 74 65 41 6e "
            + "44 6b 69 6e 64 44 74 65 78 74 "
            + "47 70 61 79 6c 6f 61 64 "
            + "14 0a 44 62 6f 64 79 41 62 01 05");
private static final byte[] EXTERNAL_SECRET_ONLY = VPackWireFixtureTest.hex(
            "14 31 45 6c 61 62 65 6c 42 68 69 "
            + "46 73 65 63 72 65 74 41 73 "
            + "44 6b 69 6e 64 44 74 65 78 74 "
            + "47 70 61 79 6c 6f 61 64 "
            + "14 0a 44 62 6f 64 79 41 62 01 04");
private static final byte[] EXTERNAL_NOTE_ONLY = VPackWireFixtureTest.hex(
            "14 2f 45 6c 61 62 65 6c 42 68 69 "
            + "44 6e 6f 74 65 41 6e "
            + "44 6b 69 6e 64 44 74 65 78 74 "
            + "47 70 61 79 6c 6f 61 64 "
            + "14 0a 44 62 6f 64 79 41 62 01 04");
private static final byte[] INJECTION_ONLY_FIELD = VPackWireFixtureTest.hex(
            "14 0f 44 72 6f 6c 65 46 48 41 43 4b 45 44 01");
private static final InjectableValues INJECTED = new InjectableValues.Std()
            .addValue("role", "injected");
private static ObjectMapper mapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void externalCreatorAdminViewBindsHiddenProps() throws Exception {
        ExternalCreatorEnvelope value = mapper(true).readerFor(ExternalCreatorEnvelope.class)
                .withView(Admin.class).readValue(ALL_EXTERNAL_FIELDS);
        assertEquals("s", value.secret);
        assertEquals("n", value.note);
        assertNotNull(value.payload);
    }
 void externalCreatorHiddenCreatorParamRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(ExternalCreatorEnvelope.class).withView(Public.class)
                .readValue(EXTERNAL_SECRET_ONLY));
    }
 void externalCreatorHiddenRegularPropRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(ExternalCreatorEnvelope.class).withView(Public.class)
                .readValue(EXTERNAL_NOTE_ONLY));
    }
 void externalCreatorHiddenPropsSkippedWhenDisabled() throws Exception {
        ExternalCreatorEnvelope value = mapper(false).readerFor(ExternalCreatorEnvelope.class)
                .withView(Public.class).readValue(ALL_EXTERNAL_FIELDS);
        assertEquals("hi", value.label);
        assertNull(value.secret);
        assertNull(value.note);
        assertNotNull(value.payload);
    }
static class Public { }
static class Admin extends Public { }
interface Payload { }
@JsonTypeName("text")
    static class TextPayload implements Payload { public String body; }
static class ExternalSetterEnvelope {
        @JsonView(Public.class) public String label;
        @JsonView(Admin.class) public String secret;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes({ @JsonSubTypes.Type(TextPayload.class) })
        public Payload payload;
    }
static class ExternalCreatorEnvelope {
        final String label;
        final String secret;
        @JsonView(Admin.class) public String note;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes({ @JsonSubTypes.Type(TextPayload.class) })
        public Payload payload;

        @JsonCreator
        ExternalCreatorEnvelope(@JsonProperty("label") @JsonView(Public.class) String label,
                @JsonProperty("secret") @JsonView(Admin.class) String secret) {
            this.label = label;
            this.secret = secret;
        }
    }
public record VisibleInject(@JsonView(Public.class) String name,
            @JacksonInject(value = "role", useInput = OptBoolean.FALSE)
            @JsonView(Public.class) String role) { }
public record HiddenInject(@JsonView(Public.class) String name,
            @JacksonInject(value = "role", useInput = OptBoolean.FALSE)
            @JsonView(Admin.class) String role) { }

    void __invoke_externalCreatorAdminViewBindsHiddenProps() throws Exception {
        try {
            externalCreatorAdminViewBindsHiddenProps();
        } finally {
        }
    }


    void __invoke_externalCreatorHiddenCreatorParamRejectedWhenEnabled() throws Exception {
        try {
            externalCreatorHiddenCreatorParamRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_externalCreatorHiddenRegularPropRejectedWhenEnabled() throws Exception {
        try {
            externalCreatorHiddenRegularPropRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_externalCreatorHiddenPropsSkippedWhenDisabled() throws Exception {
        try {
            externalCreatorHiddenPropsSkippedWhenDisabled();
        } finally {
        }
    }

}
