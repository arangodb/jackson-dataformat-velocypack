package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0642F1 {
private static final byte[] CREATOR_INPUT = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 45 61 6c 69 63 65 44 72 6f 6c 65 45 61 64 6d 69 6e 02");
private static final byte[] UNWRAPPED_INPUT = VPackWireFixtureTest.hex(
            "14 20 45 65 6d 61 69 6c 41 65 44 72 6f 6c 65 45 61 64 6d 69 6e "
            + "44 74 69 65 72 44 67 6f 6c 64 03");
private static final byte[] EXTERNAL_TYPE_ID_INPUT = VPackWireFixtureTest.hex(
            "14 31 45 6c 61 62 65 6c 42 68 69 46 73 65 63 72 65 74 41 73 "
            + "44 6b 69 6e 64 44 74 65 78 74 47 70 61 79 6c 6f 61 64 "
            + "14 0a 44 62 6f 64 79 41 62 01 04");
private static ObjectMapper mapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void externalTypeIdAdminViewBindsSecret() throws Exception {
        ExternalEnvelope envelope = mapper(true).readerFor(ExternalEnvelope.class)
                .withView(Admin.class).readValue(EXTERNAL_TYPE_ID_INPUT);
        assertEquals("s", envelope.secret);
        assertNotNull(envelope.payload);
    }
 void externalTypeIdHiddenSecretRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(ExternalEnvelope.class).withView(Public.class).readValue(EXTERNAL_TYPE_ID_INPUT));
    }
 void externalTypeIdHiddenSecretSkippedWhenDisabled() throws Exception {
        ExternalEnvelope envelope = mapper(false).readerFor(ExternalEnvelope.class)
                .withView(Public.class).readValue(EXTERNAL_TYPE_ID_INPUT);
        assertEquals("hi", envelope.label);
        assertNull(envelope.secret);
        assertNotNull(envelope.payload);
    }
static class Public { }
static class Admin extends Public { }
@JsonDeserialize(builder = CreatorUser.Builder.class)
    static class CreatorUser {
        final String name;
        final String role;

        CreatorUser(String name, String role) { this.name = name; this.role = role; }

        @JsonPOJOBuilder(withPrefix = "")
        static class Builder {
            String name;
            String role;

            @com.fasterxml.jackson.annotation.JsonCreator
            Builder(@com.fasterxml.jackson.annotation.JsonProperty("name")
                    @JsonView(Public.class) String name) { this.name = name; }

            @JsonView(Public.class) Builder name(String value) { name = value; return this; }
            @JsonView(Admin.class) Builder role(String value) { role = value; return this; }
            CreatorUser build() { return new CreatorUser(name, role); }
        }
    }
static class Flags { public String tier; }
@JsonDeserialize(builder = UnwrappedAccount.Builder.class)
    static class UnwrappedAccount {
        final String email;
        final String role;
        final Flags flags;

        UnwrappedAccount(String email, String role, Flags flags) {
            this.email = email;
            this.role = role;
            this.flags = flags;
        }

        @JsonPOJOBuilder(withPrefix = "")
        static class Builder {
            String email;
            String role;
            Flags flags;

            @JsonView(Public.class) Builder email(String value) { email = value; return this; }
            @JsonView(Admin.class) Builder role(String value) { role = value; return this; }
            @com.fasterxml.jackson.annotation.JsonUnwrapped Builder flags(Flags value) {
                flags = value;
                return this;
            }
            UnwrappedAccount build() { return new UnwrappedAccount(email, role, flags); }
        }
    }
interface Payload { }
@JsonTypeName("text")
    static class TextPayload implements Payload { public String body; }
@JsonDeserialize(builder = ExternalEnvelope.Builder.class)
    static class ExternalEnvelope {
        final String label;
        final String secret;
        final Payload payload;

        ExternalEnvelope(String label, String secret, Payload payload) {
            this.label = label;
            this.secret = secret;
            this.payload = payload;
        }

        @JsonPOJOBuilder(withPrefix = "")
        static class Builder {
            String label;
            String secret;
            Payload payload;

            @JsonView(Public.class) Builder label(String value) { label = value; return this; }
            @JsonView(Admin.class) Builder secret(String value) { secret = value; return this; }

            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                    property = "kind")
            @JsonSubTypes({ @JsonSubTypes.Type(TextPayload.class) })
            Builder payload(Payload value) { payload = value; return this; }
            ExternalEnvelope build() { return new ExternalEnvelope(label, secret, payload); }
        }
    }

    void __invoke_externalTypeIdAdminViewBindsSecret() throws Exception {
        try {
            externalTypeIdAdminViewBindsSecret();
        } finally {
        }
    }


    void __invoke_externalTypeIdHiddenSecretRejectedWhenEnabled() throws Exception {
        try {
            externalTypeIdHiddenSecretRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_externalTypeIdHiddenSecretSkippedWhenDisabled() throws Exception {
        try {
            externalTypeIdHiddenSecretSkippedWhenDisabled();
        } finally {
        }
    }

}
