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
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0646F0 {
private static final byte[] UNWRAPPED_CREATOR = VPackWireFixtureTest.hex(
            "14 21 44 6e 61 6d 65 45 61 6c 69 63 65 "
            + "46 73 65 63 72 65 74 41 73 44 74 69 65 72 44 67 6f 6c 64 03");
private static final byte[] EXTERNAL_TYPE_ID_FIRST = VPackWireFixtureTest.hex(
            "14 42 44 64 61 74 61 "
            + "14 3a 45 6c 61 62 65 6c 45 68 65 6c 6c 6f "
            + "44 6b 69 6e 64 45 61 64 6d 69 6e "
            + "45 61 73 73 65 74 14 1a 44 6e 61 6d 65 43 66 6f 6f "
            + "46 73 65 63 72 65 74 46 4c 45 41 4b 45 44 02 03 01");
private static final byte[] EXTERNAL_VALUE_FIRST = VPackWireFixtureTest.hex(
            "14 42 44 64 61 74 61 "
            + "14 3a 45 6c 61 62 65 6c 45 68 65 6c 6c 6f "
            + "45 61 73 73 65 74 14 1a 44 6e 61 6d 65 43 66 6f 6f "
            + "46 73 65 63 72 65 74 46 4c 45 41 4b 45 44 02 "
            + "44 6b 69 6e 64 45 61 64 6d 69 6e 03 01");
private static final byte[] BUILDER_VIEWS = VPackWireFixtureTest.hex(
            "14 0d 41 78 35 41 79 28 0a 41 7a 30 03");
private static ObjectMapper mapper(boolean failOnUnexpected) {
        var builder = VPackMapper.builder().enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (failOnUnexpected) {
            builder.enable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        } else {
            builder.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES);
        }
        return builder.build();
    }
 void unwrappedCreatorAdminViewBindsSecret() throws Exception {
        UnwrappedCreatorAccount value = mapper(true).readerFor(UnwrappedCreatorAccount.class)
                .withView(Admin.class).readValue(UNWRAPPED_CREATOR);
        assertEquals("alice", value.name);
        assertEquals("s", value.secret);
        assertNotNull(value.flags);
        assertEquals("gold", value.flags.tier);
    }
 void unwrappedCreatorHiddenSecretRejectedWhenEnabled() {
        assertThrows(MismatchedInputException.class, () -> mapper(true)
                .readerFor(UnwrappedCreatorAccount.class).withView(Public.class)
                .readValue(UNWRAPPED_CREATOR));
    }
 void unwrappedCreatorHiddenSecretSkippedWhenDisabled() throws Exception {
        UnwrappedCreatorAccount value = mapper(false).readerFor(UnwrappedCreatorAccount.class)
                .withView(Public.class).readValue(UNWRAPPED_CREATOR);
        assertEquals("alice", value.name);
        assertNull(value.secret);
        assertNotNull(value.flags);
        assertEquals("gold", value.flags.tier);
    }
private static Wrapper creatorWrapper(Class<?> view, byte[] bytes) throws Exception {
        return mapper(false).readerWithView(view).forType(Wrapper.class).readValue(bytes);
    }
private static BeanWrapper beanWrapper(Class<?> view, byte[] bytes) throws Exception {
        return mapper(false).readerWithView(view).forType(BeanWrapper.class).readValue(bytes);
    }
private static void assertHidden(Wrapper wrapper) {
        assertEquals("hello", wrapper.data.label);
        assertNull(wrapper.data.asset);
    }
private static void assertHidden(BeanWrapper wrapper) {
        assertEquals("hello", wrapper.data.label);
        assertNull(wrapper.data.asset);
    }
private static void assertAdmin(Wrapper wrapper) {
        assertEquals("hello", wrapper.data.label);
        AdminAsset asset = (AdminAsset) wrapper.data.asset;
        assertEquals("foo", asset.name);
        assertEquals("LEAKED", asset.secret);
    }
private static void assertAdmin(BeanWrapper wrapper) {
        assertEquals("hello", wrapper.data.label);
        AdminAsset asset = (AdminAsset) wrapper.data.asset;
        assertEquals("foo", asset.name);
        assertEquals("LEAKED", asset.secret);
    }
static class Public { }
static class Admin extends Public { }
static class Flags { public String tier; }
static class UnwrappedCreatorAccount {
        final String name;
        final String secret;
        @JsonUnwrapped Flags flags;

        @JsonCreator
        UnwrappedCreatorAccount(@JsonProperty("name") @JsonView(Public.class) String name,
                @JsonProperty("secret") @JsonView(Admin.class) String secret) {
            this.name = name;
            this.secret = secret;
        }
    }
public static abstract class Asset { }
@JsonTypeName("admin") public static class AdminAsset extends Asset {
        @JsonView(AdminView.class) public String name;
        @JsonView(AdminView.class) public String secret;
    }
public static class ExternalContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes(@JsonSubTypes.Type(value = AdminAsset.class, name = "admin"))
        @JsonView(AdminView.class)
        public Asset asset;
        @JsonView(PublicView.class) public String label;

        @JsonCreator
        ExternalContainer(@JsonProperty("label") @JsonView(PublicView.class) String label,
                @JsonProperty("asset") @JsonView(AdminView.class) Asset asset) {
            this.label = label;
            this.asset = asset;
        }
    }
public static class Wrapper { @JsonView(PublicView.class) public ExternalContainer data; }
public static class BeanExternalContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes(@JsonSubTypes.Type(value = AdminAsset.class, name = "admin"))
        @JsonView(AdminView.class) public Asset asset;
        @JsonView(PublicView.class) public String label;
    }
public static class BeanWrapper { @JsonView(PublicView.class) public BeanExternalContainer data; }
public static class PublicView { }
public static class AdminView extends PublicView { }
@JsonDeserialize(builder = ViewBuilder.class)
    static class BuiltValue {
        final int x, y, z;
        BuiltValue(int x, int y, int z) { this.x = x + 1; this.y = y + 1; this.z = z + 1; }
    }
static class ViewX { }
static class ViewY { }
static class ViewZ { }
static class ViewBuilder {
        public int x, y, z;
        @JsonView(ViewX.class) public ViewBuilder withX(int value) { x = value; return this; }
        @JsonView(ViewY.class) public ViewBuilder withY(int value) { y = value; return this; }
        @JsonView(ViewZ.class) public ViewBuilder withZ(int value) { z = value; return this; }
        public BuiltValue build() { return new BuiltValue(x, y, z); }
    }

    void __invoke_unwrappedCreatorAdminViewBindsSecret() throws Exception {
        try {
            unwrappedCreatorAdminViewBindsSecret();
        } finally {
        }
    }


    void __invoke_unwrappedCreatorHiddenSecretRejectedWhenEnabled() throws Exception {
        try {
            unwrappedCreatorHiddenSecretRejectedWhenEnabled();
        } finally {
        }
    }


    void __invoke_unwrappedCreatorHiddenSecretSkippedWhenDisabled() throws Exception {
        try {
            unwrappedCreatorHiddenSecretSkippedWhenDisabled();
        } finally {
        }
    }

}
