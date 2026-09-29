package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0641F2 {
private static final byte[] ARRAY_INPUT = VPackWireFixtureTest.hex(
            "02 0e 45 61 6c 69 63 65 45 61 64 6d 69 6e");
private static ObjectMapper arrayMapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void setterAdminViewBindsRole() throws Exception {
        SetterUser user = arrayMapper(true).readerFor(SetterUser.class).withView(Admin.class)
                .readValue(ARRAY_INPUT);
        assertEquals("alice", user.name);
        assertEquals("admin", user.role);
    }
 void setterHiddenRoleRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> arrayMapper(true)
                .readerFor(SetterUser.class).withView(Public.class).readValue(ARRAY_INPUT));
    }
 void setterHiddenRoleSkippedWhenDisabled() throws Exception {
        SetterUser user = arrayMapper(false).readerFor(SetterUser.class).withView(Public.class)
                .readValue(ARRAY_INPUT);
        assertEquals("alice", user.name);
        assertNull(user.role);
    }
static class Public { }
static class Admin extends Public { }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "name", "role" })
    static class SetterUser {
        @JsonView(Public.class) public String name;
        @JsonView(Admin.class) public String role;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "name", "role" })
    static class CreatorUser {
        final String name;
        final String role;

        @JsonCreator
        CreatorUser(@JsonProperty("name") @JsonView(Public.class) String name,
                @JsonProperty("role") @JsonView(Admin.class) String role) {
            this.name = name;
            this.role = role;
        }
    }
@JsonDeserialize(builder = BuilderUser.Builder.class)
    static class BuilderUser {
        final String name;
        final String role;

        BuilderUser(String name, String role) { this.name = name; this.role = role; }

        @JsonPOJOBuilder(withPrefix = "")
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        @JsonPropertyOrder({ "name", "role" })
        static class Builder {
            String name;
            String role;

            @JsonView(Public.class) Builder name(String value) { name = value; return this; }
            @JsonView(Admin.class) Builder role(String value) { role = value; return this; }
            BuilderUser build() { return new BuilderUser(name, role); }
        }
    }

    void __invoke_setterAdminViewBindsRole() throws Exception {
        try {
            setterAdminViewBindsRole();
        } finally {
        }
    }


    void __invoke_setterHiddenRoleRejectedWhenEnabled() throws Exception {
        try {
            setterHiddenRoleRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_setterHiddenRoleSkippedWhenDisabled() throws Exception {
        try {
            setterHiddenRoleSkippedWhenDisabled();
        } finally {
        }
    }

}
