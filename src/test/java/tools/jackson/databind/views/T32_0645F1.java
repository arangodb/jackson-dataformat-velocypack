package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0645F1 {
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
 void recordsAdminViewBindsRole() throws Exception {
        Records.User value = mapper(true).readerFor(Records.User.class).withView(Admin.class)
                .readValue(RECORD_WITH_ROLE);
        assertEquals("alice", value.name());
        assertEquals("admin", value.role());
    }
 void recordsHiddenRoleRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(Records.User.class).withView(Public.class).readValue(RECORD_WITH_ROLE));
    }
 void recordsHiddenRoleSkippedWhenDisabled() throws Exception {
        Records.User value = mapper(false).readerFor(Records.User.class).withView(Public.class)
                .readValue(RECORD_WITH_ROLE);
        assertEquals("alice", value.name());
        assertNull(value.role());
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

    void __invoke_recordsAdminViewBindsRole() throws Exception {
        try {
            recordsAdminViewBindsRole();
        } finally {
        }
    }


    void __invoke_recordsHiddenRoleRejectedWhenEnabled() throws Exception {
        try {
            recordsHiddenRoleRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_recordsHiddenRoleSkippedWhenDisabled() throws Exception {
        try {
            recordsHiddenRoleSkippedWhenDisabled();
        } finally {
        }
    }

}
