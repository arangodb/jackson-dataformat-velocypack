package tools.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0238F0 {
private static final byte[] ISSUE_426 = VPackWireFixtureTest.hex(
            "14 1a 46 75 73 65 72 49 64 39 "
          + "49 66 69 72 73 74 4e 61 6d 65 44 4d 69 6b 65 02");
private static final byte[] POINT = VPackWireFixtureTest.hex(
            "14 0b 41 78 28 01 41 79 28 02 02");
private static final byte[] IGNORE_UNKNOWN_ON_FIELD = VPackWireFixtureTest.hex(
            "14 2d 45 76 61 6c 75 65 "
          + "14 1a 44 6e 61 6d 65 47 6d 79 5f 6e 61 6d 65 "
          + "45 65 78 74 72 61 43 76 61 6c 02 "
          + "44 74 79 70 65 44 4a 73 6f 6e 02");
private static final byte[] MUSEUM = VPackWireFixtureTest.hex(
            "14 12 45 6c 6f 62 62 79 14 09 42 69 64 42 4c 31 01 01");
private static final byte[] CREATOR = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] FIELD_CREATOR = VPackWireFixtureTest.hex(
            "14 1a 45 63 68 69 6c 64 "
          + "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02 01");
private static final byte[] NULL_INNER = VPackWireFixtureTest.hex(
            "14 0a 45 69 6e 6e 65 72 18 01");
private static final byte[] NON_NULL_INNER = VPackWireFixtureTest.hex(
            "14 18 45 69 6e 6e 65 72 "
          + "14 0f 45 66 69 65 6c 64 45 69 6e 6e 65 72 01 01");

    // Provenance: JsonIgnorePropertiesDeserTest#testIssue426.
    void testIssue426Vpack() throws Exception {
        User426 result = new VPackMapper().readValue(ISSUE_426, User426.class);
        assertNotNull(result);
        assertEquals("Mike", result.firstName);
        assertNull(result.userId);
    }

    // Provenance: JsonIgnorePropertiesDeserTest#testIgnoreOnProperty1217.
    void testIgnoreViaConfigOverride1217Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Point.class,
                        o -> o.setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("y")))
                .build();
        Point result = mapper.readValue(POINT, Point.class);
        assertEquals(1, result.x);
        assertEquals(0, result.y);
    }

    // Provenance: JsonIgnorePropertiesDeserTest#testIgnoreUnknownViaConfigOverride.
    void testIgnoreUnknownViaConfigOverrideVpack() throws Exception {
        ObjectMapper strict = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
        assertThrows(UnrecognizedPropertyException.class,
                () -> strict.readValue(POINT_WITH_UNKNOWN, Point.class));

        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .withConfigOverride(Point.class,
                        o -> o.setIgnorals(JsonIgnoreProperties.Value.forIgnoreUnknown(true)))
                .build();
        Point result = mapper.readValue(POINT_WITH_UNKNOWN, Point.class);
        assertEquals(2, result.x);
    }

    // Provenance: JsonIgnorePropertiesDeserTest#testIgnoreProps2803.
    void testIgnoreProps2803Vpack() throws Exception {
        Museum2803 museum = new VPackMapper().readValue(MUSEUM, Museum2803.class);
        assertNotNull(museum);
        Building2803 building = new VPackMapper().readValue(MUSEUM, Building2803.class);
        assertNotNull(building);
    }

    // Provenance: JsonIgnorePropertiesDeserTest#testIgnoreUnknownOnField.
    void testIgnoreUnknownOnFieldVpack() throws Exception {
        MyPojoValue result = new VPackMapper().readValue(
                IGNORE_UNKNOWN_ON_FIELD, MyPojoValue.class);
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals("my_name", result.getValue().name);
    }
private static ObjectMapper emptyValueMapper() {
        return VPackMapper.builder()
                .changeDefaultNullHandling(h -> h.withOverrides(
                        JsonSetter.Value.construct(Nulls.AS_EMPTY, Nulls.AS_EMPTY)))
                .build();
    }
private static final byte[] POINT_WITH_UNKNOWN = VPackWireFixtureTest.hex(
            "14 10 41 78 28 02 46 66 6f 6f 62 61 72 28 03 02");
@JsonIgnoreProperties({ "userId" })
    static class User426 {
        public String firstName;
        Integer userId;
        public Integer getUserId() { return userId; }
        public void setUserId(CharSequence id) { userId = Integer.valueOf(id.toString()); }
        public void setUserId(Integer value) { userId = value; }
        public void setUserId(User426 value) { }
        public void setUserId(boolean value) { }
    }
static class Point {
        public int x;
        public int y;
    }
static class Building2803 {
        @JsonIgnoreProperties({ "something" })
        @JsonProperty
        private Room2803 lobby;
    }
static class Museum2803 extends Building2803 { }
static class Room2803 {
        public Building2803 something;
        public String id;
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    static class MyPojoValue {
        @JsonIgnoreProperties(ignoreUnknown = true)
        MyPojo2627 value;
        public MyPojo2627 getValue() { return value; }
    }
static class MyPojo2627 {
        public String name;
    }
@JsonIgnoreProperties("name")
    static class PojoWithCreator {
        final int id;
        final String name;

        @JsonCreator
        PojoWithCreator(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }
static class IdAndName {
        final int id;
        final String name;

        @JsonCreator
        IdAndName(@JsonProperty("id") int id, @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }
static class WrapperWithIgnore {
        @JsonIgnoreProperties("name")
        public IdAndName child;
    }
static class Outer2572 {
        @JsonProperty("inner")
        private final Inner2572 inner;

        @JsonCreator
        Outer2572(@JsonProperty("inner") Inner2572 inner) { this.inner = inner; }
    }
static class Inner2572 {
        @JsonProperty("field")
        private final String field;

        @JsonCreator
        Inner2572(@JsonProperty("field") String field) { this.field = field; }
    }
static class InnerNonPublicCtor {
        @JsonProperty("field")
        private final String field;

        private InnerNonPublicCtor(String field) { this.field = field; }
    }
static class OuterWithNonPublicInner {
        @JsonProperty("inner")
        private final InnerNonPublicCtor inner;

        @JsonCreator
        OuterWithNonPublicInner(@JsonProperty("inner") InnerNonPublicCtor inner) {
            this.inner = inner;
        }
    }

    void __invoke_testIssue426Vpack() throws Exception {
        try {
            testIssue426Vpack();
        } finally {
        }
    }


    void __invoke_testIgnoreViaConfigOverride1217Vpack() throws Exception {
        try {
            testIgnoreViaConfigOverride1217Vpack();
        } finally {
        }
    }


    void __invoke_testIgnoreUnknownViaConfigOverrideVpack() throws Exception {
        try {
            testIgnoreUnknownViaConfigOverrideVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreProps2803Vpack() throws Exception {
        try {
            testIgnoreProps2803Vpack();
        } finally {
        }
    }


    void __invoke_testIgnoreUnknownOnFieldVpack() throws Exception {
        try {
            testIgnoreUnknownOnFieldVpack();
        } finally {
        }
    }

}
