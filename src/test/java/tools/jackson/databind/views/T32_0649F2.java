package tools.jackson.databind.views;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonView;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0649F2 {
private static final byte[] SETTERLESS_INPUT = VPackWireFixtureTest.hex(
            "14 1d 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "45 72 6f 6c 65 73 13 09 45 61 64 6d 69 6e 01 02");
private static final byte[] SETTERLESS_ROLES_BEFORE = VPackWireFixtureTest.hex(
            "14 1d 45 72 6f 6c 65 73 13 09 45 61 64 6d 69 6e 01 "
            + "44 6e 61 6d 65 45 61 6c 69 63 65 02");
private static final byte[] UNWRAPPED_CREATOR_INPUT = VPackWireFixtureTest.hex(
            "14 30 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 74 72 65 65 74 49 31 20 4d 61 69 6e 20 53 74 "
            + "44 63 69 74 79 4b 53 70 72 69 6e 67 66 69 65 6c 64 03");
private static final byte[] UNWRAPPED_POJO_ADMIN = VPackWireFixtureTest.hex(
            "14 32 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 65 63 72 65 74 44 73 73 73 68 "
            + "48 70 61 73 73 77 6f 72 64 46 73 65 63 72 65 74 "
            + "44 63 69 74 79 42 4e 59 04");
private static final byte[] UNWRAPPED_POJO_REGULAR_ATTACK = VPackWireFixtureTest.hex(
            "14 26 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "48 70 61 73 73 77 6f 72 64 46 42 59 50 41 53 53 "
            + "44 63 69 74 79 42 4e 59 03");
private static final byte[] UNWRAPPED_POJO_CREATOR_ATTACK = VPackWireFixtureTest.hex(
            "14 24 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 65 63 72 65 74 46 42 59 50 41 53 53 "
            + "44 63 69 74 79 42 4e 59 03");

    void unwrappedPojoAdminViewPopulatesAll() throws Exception {
        UnwrappedPojoUser value = VPackMapper.builder().build().readerWithView(AdminView.class)
                .forType(UnwrappedPojoUser.class).readValue(UNWRAPPED_POJO_ADMIN);
        assertEquals("alice", value.name);
        assertEquals("sssh", value.secret);
        assertEquals("secret", value.password);
        assertNotNull(value.address);
        assertEquals("NY", value.address.city);
    }

    void unwrappedPojoCreatorPropertyHonorsView() throws Exception {
        UnwrappedPojoUser value = VPackMapper.builder().build().readerWithView(PublicView.class)
                .forType(UnwrappedPojoUser.class).readValue(UNWRAPPED_POJO_CREATOR_ATTACK);
        assertEquals("alice", value.name);
        assertNotNull(value.address);
        assertEquals("NY", value.address.city);
        assertNull(value.secret, "AdminView creator property must be filtered on the unwrapped path");
    }

    void unwrappedPojoRegularPropertyHonorsView() throws Exception {
        UnwrappedPojoUser value = VPackMapper.builder().build().readerWithView(PublicView.class)
                .forType(UnwrappedPojoUser.class).readValue(UNWRAPPED_POJO_REGULAR_ATTACK);
        assertEquals("alice", value.name);
        assertNotNull(value.address);
        assertEquals("NY", value.address.city);
        assertNull(value.password, "AdminView regular property must be filtered on the unwrapped path");
    }
static class PublicView { }
static class AdminView extends PublicView { }
static class CreatorBean {
        @JsonView(PublicView.class)
        private String name;
        @JsonView(AdminView.class)
        private final List<String> roles = new ArrayList<>();

        @JsonCreator
        CreatorBean(@JsonProperty("name") @JsonView(PublicView.class) String name) {
            this.name = name;
        }

        @JsonView(PublicView.class)
        public String getName() { return name; }

        @JsonView(AdminView.class)
        public List<String> getRoles() { return roles; }
    }
static class Address {
        @JsonView(AdminView.class)
        public String street;
        @JsonView(PublicView.class)
        public String city;
    }
static class UnwrappedCreatorUser {
        @JsonView(PublicView.class)
        public final String name;
        @JsonView(AdminView.class)
        public final Address address;

        @JsonCreator
        UnwrappedCreatorUser(@JsonProperty("name") @JsonView(PublicView.class) String name,
                @JsonView(AdminView.class) @JsonUnwrapped Address address) {
            this.name = name;
            this.address = address;
        }
    }
static class UnwrappedPojoUser {
        @JsonView(PublicView.class)
        public final String name;
        @JsonView(AdminView.class)
        public final String secret;
        @JsonView(AdminView.class)
        public String password;
        @JsonView(PublicView.class)
        @JsonUnwrapped
        public Address address;

        @JsonCreator
        UnwrappedPojoUser(@JsonProperty("name") @JsonView(PublicView.class) String name,
                @JsonProperty("secret") @JsonView(AdminView.class) String secret) {
            this.name = name;
            this.secret = secret;
        }
    }

    void __invoke_unwrappedPojoAdminViewPopulatesAll() throws Exception {
        try {
            unwrappedPojoAdminViewPopulatesAll();
        } finally {
        }
    }


    void __invoke_unwrappedPojoCreatorPropertyHonorsView() throws Exception {
        try {
            unwrappedPojoCreatorPropertyHonorsView();
        } finally {
        }
    }


    void __invoke_unwrappedPojoRegularPropertyHonorsView() throws Exception {
        try {
            unwrappedPojoRegularPropertyHonorsView();
        } finally {
        }
    }

}
