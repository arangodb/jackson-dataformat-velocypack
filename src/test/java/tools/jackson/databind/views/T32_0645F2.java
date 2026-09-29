package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0645F2 {
private static final byte[] RECORD_WITH_ROLE = VPackWireFixtureTest.hex(
            "14 19 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "44 72 6f 6c 65 45 61 64 6d 69 6e 02");
private static final byte[] ROLE_ADMIN = VPackWireFixtureTest.hex(
            "14 0e 44 72 6f 6c 65 45 61 64 6d 69 6e 01");
private static final byte[] ROLE_HACKED = VPackWireFixtureTest.hex(
            "14 0f 44 72 6f 6c 65 46 48 41 43 4b 45 44 01");
private static final byte[] SETTER_UNWRAPPED = VPackWireFixtureTest.hex(
            "14 20 45 65 6d 61 69 6c 41 65 44 72 6f 6c 65 45 61 64 6d 69 6e "
            + "44 74 69 65 72 44 67 6f 6c 64 03");
private static ObjectMapper mapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void setterUnwrappedAdminViewBindsRole() throws Exception {
        SetterUnwrapped.Account value = mapper(true)
                .readerFor(SetterUnwrapped.Account.class).withView(Admin.class)
                .readValue(SETTER_UNWRAPPED);
        assertEquals("admin", value.role);
        assertNotNull(value.flags);
        assertEquals("gold", value.flags.tier);
    }
 void setterUnwrappedHiddenRoleRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(SetterUnwrapped.Account.class).withView(Public.class)
                .readValue(SETTER_UNWRAPPED));
    }
 void setterUnwrappedHiddenRoleSkippedWhenDisabled() throws Exception {
        SetterUnwrapped.Account value = mapper(false)
                .readerFor(SetterUnwrapped.Account.class).withView(Public.class)
                .readValue(SETTER_UNWRAPPED);
        assertEquals("e", value.email);
        assertNull(value.role);
        assertNotNull(value.flags);
        assertEquals("gold", value.flags.tier);
    }
static class Public { }
static class Admin extends Public { }
static class Records {
        record User(@JsonView(Public.class) String name, @JsonView(Admin.class) String role) { }
    }
public static class RecordUpdate {
        public record User(@JsonView(Public.class) String name,
                @JsonView(Admin.class) String role) { }
    }
static class Flags { public String tier; }
static class SetterUnwrapped {
        static class Account {
            String email;
            @JsonView(Admin.class) String role;
            Flags flags;

            @JsonView(Public.class) public void setEmail(String value) { email = value; }
            @JsonView(Admin.class) public void setRole(String value) { role = value; }
            @JsonUnwrapped public void setFlags(Flags value) { flags = value; }
        }
    }

    void __invoke_setterUnwrappedAdminViewBindsRole() throws Exception {
        try {
            setterUnwrappedAdminViewBindsRole();
        } finally {
        }
    }


    void __invoke_setterUnwrappedHiddenRoleRejectedWhenEnabled() throws Exception {
        try {
            setterUnwrappedHiddenRoleRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_setterUnwrappedHiddenRoleSkippedWhenDisabled() throws Exception {
        try {
            setterUnwrappedHiddenRoleSkippedWhenDisabled();
        } finally {
        }
    }

}
