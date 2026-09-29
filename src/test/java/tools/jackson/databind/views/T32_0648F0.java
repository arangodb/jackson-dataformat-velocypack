package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;

import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0648F0 {
private static final byte[] BUILDER_ADMIN = VPackWireFixtureTest.hex(
            "14 26 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "48 70 61 73 73 77 6f 72 64 46 73 65 63 72 65 74 "
            + "44 63 69 74 79 42 4e 59 03");
private static final byte[] BUILDER_WINDOW = VPackWireFixtureTest.hex(
            "14 26 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "48 70 61 73 73 77 6f 72 64 46 42 59 50 41 53 53 "
            + "44 63 69 74 79 42 4e 59 03");
private static final byte[] BUILDER_AFTER = VPackWireFixtureTest.hex(
            "14 26 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "44 63 69 74 79 42 4e 59 "
            + "48 70 61 73 73 77 6f 72 64 46 42 59 50 41 53 53 03");
 void builderAdminViewPopulatesAll() throws Exception {
        BuilderUser value = VPackMapper.builder().build().readerWithView(AdminView.class)
                .forType(BuilderUser.class).readValue(BUILDER_ADMIN);
        assertEquals("alice", value.name);
        assertEquals("NY", value.city);
        assertEquals("secret", value.password);
    }
 void builderRegularPropertyInCreatorWindowHonorsView() throws Exception {
        BuilderUser value = VPackMapper.builder().build().readerWithView(PublicView.class)
                .forType(BuilderUser.class).readValue(BUILDER_WINDOW);
        assertEquals("alice", value.name);
        assertEquals("NY", value.city);
        assertNull(value.password, "AdminView builder property must be filtered inside the creator window");
    }
 void builderRegularPropertyAfterCreatorHonorsView() throws Exception {
        BuilderUser value = VPackMapper.builder().build().readerWithView(PublicView.class)
                .forType(BuilderUser.class).readValue(BUILDER_AFTER);
        assertEquals("alice", value.name);
        assertEquals("NY", value.city);
        assertNull(value.password, "AdminView builder property must be filtered after creator properties");
    }
private static final byte[] EXTERNAL_ADMIN = VPackWireFixtureTest.hex(
            "14 44 "
            + "44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 65 63 72 65 74 44 73 73 73 68 "
            + "48 70 61 73 73 77 6f 72 64 42 70 77 "
            + "49 76 61 6c 75 65 54 79 70 65 46 73 74 72 69 6e 67 "
            + "45 76 61 6c 75 65 14 07 41 76 41 78 01 05");
private static final byte[] RECORD_UPDATE = VPackWireFixtureTest.hex(
            "14 1a 44 6e 61 6d 65 46 61 6c 69 63 65 32 44 72 6f 6c 65 45 61 64 6d 69 6e 02");
static class PublicView { }
static class AdminView extends PublicView { }
@JsonDeserialize(builder = BuilderUser.Builder.class)
    static class BuilderUser {
        final String name, city, password;
        BuilderUser(String name, String city, String password) {
            this.name = name; this.city = city; this.password = password;
        }
        @JsonPOJOBuilder(withPrefix = "")
        static class Builder {
            String name, city, password;
            @JsonCreator
            Builder(@JsonProperty("name") @JsonView(PublicView.class) String name,
                    @JsonProperty("city") @JsonView(PublicView.class) String city) {
                this.name = name; this.city = city;
            }
            @JsonView(PublicView.class) public Builder name(String v) { name = v; return this; }
            @JsonView(PublicView.class) public Builder city(String v) { city = v; return this; }
            @JsonView(AdminView.class) public Builder password(String v) { password = v; return this; }
            public BuilderUser build() { return new BuilderUser(name, city, password); }
        }
    }
interface Value { }
@JsonTypeName("string") static class StringValue implements Value { public String v; }
static class Envelope {
        @JsonView(PublicView.class) public final String name;
        @JsonView(AdminView.class) public final String secret;
        @JsonView(AdminView.class) public String password;
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "valueType")
        @JsonSubTypes({ @JsonSubTypes.Type(StringValue.class) })
        @JsonView(PublicView.class) public final Value value;
        @JsonCreator Envelope(@JsonProperty("name") @JsonView(PublicView.class) String name,
                @JsonProperty("secret") @JsonView(AdminView.class) String secret,
                @JsonProperty("value") @JsonView(PublicView.class) Value value) {
            this.name = name; this.secret = secret; this.value = value;
        }
    }
public record UserRecord(@JsonView(PublicView.class) String name,
                             @JsonView(AdminView.class) String role) { }

    void __invoke_builderAdminViewPopulatesAll() throws Exception {
        try {
            builderAdminViewPopulatesAll();
        } finally {
        }
    }


    void __invoke_builderRegularPropertyInCreatorWindowHonorsView() throws Exception {
        try {
            builderRegularPropertyInCreatorWindowHonorsView();
        } finally {
        }
    }


    void __invoke_builderRegularPropertyAfterCreatorHonorsView() throws Exception {
        try {
            builderRegularPropertyAfterCreatorHonorsView();
        } finally {
        }
    }

}
