package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0644F0 {
private static final byte[] CREATOR_THEN_ROLE = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "44 72 6f 6c 65 45 61 64 6d 69 6e 02");
private static final byte[] ROLE_THEN_CREATOR = VPackWireFixtureTest.hex(
            "14 19 44 72 6f 6c 65 45 61 64 6d 69 6e "
            + "44 6e 61 6d 65 45 61 6c 69 63 65 02");
private static final byte[] UNWRAPPED_ACCOUNT = VPackWireFixtureTest.hex(
            "14 23 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "44 72 6f 6c 65 45 61 64 6d 69 6e "
            + "44 74 69 65 72 44 67 6f 6c 64 03");
private static final byte[] EXTERNAL_PAYLOAD = VPackWireFixtureTest.hex(
            "14 28 45 6c 61 62 65 6c 42 68 69 "
            + "44 6b 69 6e 64 44 74 65 78 74 "
            + "47 70 61 79 6c 6f 61 64 14 0a 44 62 6f 64 79 41 78 01 03");
private static final byte[] UNWRAPPED_CREATOR = VPackWireFixtureTest.hex(
            "14 1c 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 74 72 65 65 74 46 31 20 4d 61 69 6e 02");
private static ObjectMapper mapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void externalTypeIdValueStillSilentlySkippedWhenEnabled() throws Exception {
        ExternalEnvelope value = mapper(true).readerFor(ExternalEnvelope.class).withView(Public.class)
                .readValue(EXTERNAL_PAYLOAD);
        assertEquals("hi", value.label);
        assertNull(value.payload);
    }
 void unwrappedCreatorParamStillSilentlySkippedWhenEnabled() throws Exception {
        UnwrappedPerson value = mapper(true).readerFor(UnwrappedPerson.class).withView(Public.class)
                .readValue(UNWRAPPED_CREATOR);
        assertEquals("alice", value.name);
        assertNull(value.address);
    }
static class Public { }
static class Admin extends Public { }
interface Payload { }
@JsonTypeName("text")
    static class TextPayload implements Payload { public String body; }
static class CreatorAccount {
        final String name;
        @JsonView(Admin.class) String role;

        @JsonCreator
        CreatorAccount(@JsonProperty("name") @JsonView(Public.class) String name) {
            this.name = name;
        }
    }
static class Flags { public String tier; }
static class UnwrappedAccount {
        final String name;
        @JsonView(Admin.class) String role;
        @JsonUnwrapped Flags flags;

        @JsonCreator
        UnwrappedAccount(@JsonProperty("name") @JsonView(Public.class) String name) {
            this.name = name;
        }
    }
static class ExternalEnvelope {
        @JsonView(Public.class) public String label;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes({ @JsonSubTypes.Type(TextPayload.class) })
        @JsonView(Admin.class)
        public Payload payload;
    }
static class Address {
        @JsonView(Admin.class) public String street;
    }
static class UnwrappedPerson {
        final String name;
        final Address address;

        @JsonCreator
        UnwrappedPerson(@JsonProperty("name") @JsonView(Public.class) String name,
                @JsonView(Admin.class) @JsonUnwrapped Address address) {
            this.name = name;
            this.address = address;
        }
    }

    void __invoke_externalTypeIdValueStillSilentlySkippedWhenEnabled() throws Exception {
        try {
            externalTypeIdValueStillSilentlySkippedWhenEnabled();
        } finally {
        }
    }


    void __invoke_unwrappedCreatorParamStillSilentlySkippedWhenEnabled() throws Exception {
        try {
            unwrappedCreatorParamStillSilentlySkippedWhenEnabled();
        } finally {
        }
    }

}
