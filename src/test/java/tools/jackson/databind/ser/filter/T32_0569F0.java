package tools.jackson.databind.ser.filter;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0569F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CURRENT_VALUE_FULL = VPackWireFixtureTest.hex(
            "0b 2b 02 44 72 6f 6c 65 0b 11 01 44 6e 61 6d 65 "
            + "47 4d 61 6e 61 67 65 72 03 44 74 79 70 65 0b 0b 01 "
            + "45 76 61 6c 75 65 31 03 03 19");
private static final byte[] CURRENT_VALUE_EMPTY = VPackWireFixtureTest.hex(
            "0b 1b 02 44 72 6f 6c 65 0a 44 74 79 70 65 0b 0b 01 "
            + "45 76 61 6c 75 65 31 03 03 09");
private static final byte[] CURRENT_VALUE_CREATOR = VPackWireFixtureTest.hex(
            "0b 2d 02 46 73 6f 75 72 63 65 0b 0a 01 42 69 64 42 73 31 03 "
            + "46 61 73 73 65 74 73 06 10 01 0b 0c 01 44 6e 61 6d 65 42 61 31 "
            + "03 03 14 03");
private static final byte[] CURRENT_VALUE_UNWRAPPED_CREATOR = VPackWireFixtureTest.hex(
            "0b 44 02 46 73 6f 75 72 63 65 0b 21 03 42 69 64 42 73 31 "
            + "45 66 69 72 73 74 43 42 6f 62 44 6c 61 73 74 45 53 6d 69 74 68 "
            + "09 03 13 46 61 73 73 65 74 73 06 10 01 0b 0c 01 44 6e 61 6d 65 "
            + "42 61 31 03 03 2b 03");

    // Provenance: CurrentValueDeser4184Test#testCurrentValue4184FullPojo().
    void testCurrentValue4184FullPojoVpack() throws Exception {
        User user = MAPPER.readValue(CURRENT_VALUE_FULL, User.class);
        assertNotNull(user);
        assertEquals(UserType.ADMIN, user.type);
    }

    // Provenance: CurrentValueDeser4184Test#testCurrentValue4184EmptyPojo().
    void testCurrentValue4184EmptyPojoVpack() throws Exception {
        User user = MAPPER.readValue(CURRENT_VALUE_EMPTY, User.class);
        assertNotNull(user);
        assertEquals(UserType.ADMIN, user.type);
    }
static Object CURRENT_VALUE;
@JsonDeserialize(using = UserTypeDeserializer.class)
    enum UserType {
        ADMIN(1), USER(2);

        final int value;

        UserType(int value) { this.value = value; }
        public Integer getValue() { return value; }
        public String getName() { return name(); }
    }
static class User {
        public Role role;
        public UserType type;
    }
static class Role {
        public String name;
    }
static class UserTypeDeserializer extends ValueDeserializer<UserType> {
        @Override
        public UserType deserialize(JsonParser p, DeserializationContext ctxt) {
            Object currentValue = p.streamReadContext().getParent().currentValue();
            if (currentValue == null) {
                ctxt.reportInputMismatch(UserType.class, "No currentValue() available");
            }
            if (!(currentValue instanceof User)) {
                ctxt.reportInputMismatch(UserType.class, "currentValue() of wrong type, not User but: "
                        + currentValue.getClass().getName());
            }
            JsonNode node = ctxt.readTree(p);
            return node.path("value").asInt(-1) == 1 ? UserType.ADMIN : UserType.USER;
        }
    }
static class Project {
        public Source source;

        @JsonDeserialize(using = AssetDeserializer.class)
        public List<Asset> assets;
    }
static class Source {
        public String id;

        @JsonCreator
        public Source(@JsonProperty("id") String id) { this.id = id; }
    }
static class UnwrappedSource {
        String id;
        Name name;

        @JsonCreator
        public UnwrappedSource(@JsonProperty("id") String id) { this.id = id; }

        @JsonUnwrapped
        public void setName(Name name) { this.name = name; }
    }
static class Name {
        public String first;
        public String last;
    }
static class ProjectWithUnwrapped {
        public UnwrappedSource source;

        @JsonDeserialize(using = AssetDeserializer.class)
        public List<Asset> assets;
    }
static class Asset {
        public String name;
    }
static class AssetDeserializer extends ValueDeserializer<List<Asset>> {
        @Override
        public List<Asset> deserialize(JsonParser p, DeserializationContext ctxt) {
            p.readValueAsTree();
            CURRENT_VALUE = p.currentValue();
            return List.of();
        }
    }
@JsonIgnoreProperties({ "b", "c" })
    static class IgnoreSome {
        public int a = 3;
        public String b = "x";
        public int getC() { return -6; }
        public String getD() { return "abc"; }
    }
@JsonIgnoreProperties({ "@class" })
    static class MyMap extends HashMap<String, String> { }
static class WrapperWithPropIgnore {
        @JsonIgnoreProperties("y")
        public XY value = new XY();
    }
static class XY {
        public int x = 1;
        public int y = 2;
    }
static class WrapperWithPropIgnore2 {
        @JsonIgnoreProperties("z")
        public XYZ value = new XYZ();
    }
@JsonIgnoreProperties({ "x" })
    static class XYZ {
        public int x = 1;
        public int y = 2;
        public int z = 3;
    }
static class WrapperWithPropIgnoreUntyped {
        @JsonIgnoreProperties("y")
        public Object value = new XYZ();
    }
static class MapWrapper {
        @JsonIgnoreProperties({ "a" })
        public final HashMap<String, Integer> value = new HashMap<>();
        { value.put("a", 1); value.put("b", 2); }
    }
static class IgnoreForListValuesXY {
        @JsonIgnoreProperties({ "x" })
        public List<XY> coordinates = List.of(new XY());
    }
static class IgnoreForListValuesXYZ {
        @JsonIgnoreProperties({ "y" })
        public List<XYZ> coordinates = List.of(new XYZ());
    }
static class Point {
        public int x;
        public int y;

        Point(int x, int y) { this.x = x; this.y = y; }
    }

    void __invoke_testCurrentValue4184FullPojoVpack() throws Exception {
        try {
            testCurrentValue4184FullPojoVpack();
        } finally {
        }
    }


    void __invoke_testCurrentValue4184EmptyPojoVpack() throws Exception {
        try {
            testCurrentValue4184EmptyPojoVpack();
        } finally {
        }
    }

}
