package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0647F1 {
private static final byte[] MULTIPLE_VIEWS = VPackWireFixtureTest.hex(
            "14 58 4c 6e 6f 6e 56 69 65 77 46 69 65 6c 64 "
            + "51 6e 6f 6e 56 69 65 77 46 69 65 6c 64 56 61 6c 75 65 "
            + "4a 76 69 65 77 31 46 69 65 6c 64 4f 76 69 65 77 31 46 69 65 6c 64 56 61 6c 75 65 "
            + "4a 76 69 65 77 32 46 69 65 6c 64 4f 76 69 65 77 32 46 69 65 6c 64 56 61 6c 75 65 03");
private static final byte[] ARRAY_ACCOUNT = VPackWireFixtureTest.hex(
            "06 1d 02 51 6d 61 6c 6c 6f 72 79 40 65 76 69 6c 2e 74 65 73 74 "
            + "45 41 44 4d 49 4e 03 15");
private static final byte[] NESTED_CONTROL = VPackWireFixtureTest.hex(
            "14 3c 45 65 6d 61 69 6c 41 65 45 66 6c 61 67 73 "
            + "14 2b 44 72 6f 6c 65 45 41 44 4d 49 4e "
            + "48 61 70 70 72 6f 76 65 64 1a "
            + "4d 63 72 65 64 69 74 42 61 6c 61 6e 63 65 2b 40 42 0f 00 03 02");
private static final byte[] UNWRAPPED_ACCOUNT = VPackWireFixtureTest.hex(
            "14 4f 45 65 6d 61 69 6c 51 6d 61 6c 6c 6f 72 79 40 65 76 69 6c 2e 74 65 73 74 "
            + "48 70 61 73 73 77 6f 72 64 42 70 77 "
            + "44 72 6f 6c 65 45 41 44 4d 49 4e "
            + "48 61 70 70 72 6f 76 65 64 1a "
            + "4d 63 72 65 64 69 74 42 61 6c 61 6e 63 65 2b 40 42 0f 00 05");
private static final byte[] ARRAY_CREATE_UPDATE = VPackWireFixtureTest.hex(
            "06 1d 02 51 6d 61 6c 6c 6f 72 79 40 65 76 69 6c 2e 74 65 73 74 "
            + "45 41 44 4d 49 4e 03 15");
private static ObjectMapper mapper(boolean includeDefaultView, boolean failOnUnexpected) {
        var builder = VPackMapper.builder();
        if (includeDefaultView) builder.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        else builder.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        else builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        return builder.build();
    }
 void arrayCreateHonorsView() throws Exception {
        ArrayAccount created = mapper(false, false).readerWithView(PublicView.class)
                .forType(ArrayAccount.class).readValue(ARRAY_CREATE_UPDATE);
        assertEquals("mallory@evil.test", created.email);
        assertNull(created.role);
    }
 void arrayUpdateHonorsView() throws Exception {
        ArrayAccount original = new ArrayAccount("orig@corp.test", "user");
        ArrayAccount updated = mapper(false, false).readerWithView(PublicView.class)
                .forType(ArrayAccount.class).withValueToUpdate(original).readValue(ARRAY_ACCOUNT);
        assertEquals("mallory@evil.test", updated.email);
        assertEquals("user", updated.role);
    }
 void arrayUpdateWithoutViewUpdatesAll() throws Exception {
        ArrayAccount updated = mapper(false, false).readerForUpdating(
                new ArrayAccount("orig@corp.test", "user")).forType(ArrayAccount.class)
                .readValue(ARRAY_ACCOUNT);
        assertEquals("mallory@evil.test", updated.email);
        assertEquals("ADMIN", updated.role);
    }
private static ObjectMapper unwrappedMapper() {
        return VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();
    }
static class View1 { }
static class View2 { }
static class MultiViewBean {
        public String nonViewField;
        @JsonView(View1.class) public String view1Field;
        @JsonView(View2.class) public String view2Field;
    }
static class PublicView { }
static class InternalView extends PublicView { }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "email", "role" })
    static class ArrayAccount {
        @JsonView(PublicView.class) public String email;
        @JsonView(InternalView.class) public String role;
        public ArrayAccount() { }
        ArrayAccount(String email, String role) { this.email = email; this.role = role; }
    }
static class AccountFlags {
        public String role;
        public boolean approved;
        public long creditBalance;
    }
static class Registration {
        @JsonView(PublicView.class) public String email;
        @JsonView(PublicView.class) public String password;
        @JsonView(InternalView.class) @JsonUnwrapped public AccountFlags flags;
    }
static class Control {
        @JsonView(PublicView.class) public String email;
        @JsonView(InternalView.class) public AccountFlags flags;
    }
static class RegistrationWithSetters {
        String email;
        String password;
        AccountFlags flags;
        @JsonView(PublicView.class) public void setEmail(String value) { email = value; }
        @JsonView(PublicView.class) public void setPassword(String value) { password = value; }
        @JsonView(InternalView.class) @JsonUnwrapped
        public void setFlags(AccountFlags value) { flags = value; }
    }

    void __invoke_arrayCreateHonorsView() throws Exception {
        try {
            arrayCreateHonorsView();
        } finally {
        }
    }


    void __invoke_arrayUpdateHonorsView() throws Exception {
        try {
            arrayUpdateHonorsView();
        } finally {
        }
    }


    void __invoke_arrayUpdateWithoutViewUpdatesAll() throws Exception {
        try {
            arrayUpdateWithoutViewUpdatesAll();
        } finally {
        }
    }

}
