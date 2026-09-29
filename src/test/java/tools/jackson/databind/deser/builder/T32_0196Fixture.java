package tools.jackson.databind.deser.builder;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0196Fixture {
private static final byte[] VIEWS = VPackWireFixtureTest.hex(
            "14 0a 41 78 35 41 79 28 0a 02");
private static final byte[] CREATOR_VIEWS = VPackWireFixtureTest.hex(
            "14 11 41 78 35 41 79 28 0a 45 62 6f 67 75 73 18 03");
private static final byte[] ANY_VALUE = VPackWireFixtureTest.hex(
            "14 13 41 61 45 76 61 6c 75 65 41 62 28 2a 41 63 28 6f 03");
private static final byte[] ANY_VALUE_REORDERED = VPackWireFixtureTest.hex(
            "14 13 41 62 28 2a 41 61 45 76 61 6c 75 65 41 63 28 6f 03");
private static final byte[] ANY_VALUE_ONLY_CREATOR = VPackWireFixtureTest.hex(
            "14 0c 41 61 46 76 61 6c 75 65 32 01");
private static final byte[] ANY_VALUE_WITH_FIELD = VPackWireFixtureTest.hex(
            "14 17 41 61 45 76 61 6c 75 65 41 62 43 78 79 7a 41 63 43 61 62 63 03");
private static final byte[] ANY_VALUE_ORDERED = VPackWireFixtureTest.hex(
            "14 14 41 61 45 76 61 6c 75 65 41 64 34 41 63 33 41 62 32 04");
private static final byte[] ANY_VALUE_UNKNOWN = ANY_VALUE;
private static final byte[] ANY_VALUE_ONE = VPackWireFixtureTest.hex(
            "14 0f 41 61 45 76 61 6c 75 65 41 62 41 78 02");
private final ObjectMapper mapper = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
            .build();

    // Provenance: databind/deser/builder/BuilderWithViewTest#testSimpleViews.
    void testSimpleViews() throws Exception {
        ValueClassXY resultX = mapper.readerFor(ValueClassXY.class)
                .withView(ViewX.class).readValue(VIEWS);
        assertEquals(6, resultX.x);
        assertEquals(1, resultX.y);

        ValueClassXY resultY = mapper.readerFor(ValueClassXY.class)
                .withView(ViewY.class).readValue(VIEWS);
        assertEquals(1, resultY.x);
        assertEquals(11, resultY.y);
    }

    // Provenance: databind/deser/builder/BuilderWithViewTest#testCreatorViews.
    void testCreatorViews() throws Exception {
        CreatorValueXY resultX = mapper.readerFor(CreatorValueXY.class)
                .withView(ViewX.class).readValue(CREATOR_VIEWS);
        assertEquals(Integer.valueOf(5), resultX.x);
        assertNull(resultX.y);

        CreatorValueXY resultY = mapper.readerFor(CreatorValueXY.class)
                .withView(ViewY.class).readValue(CREATOR_VIEWS);
        assertNull(resultY.x);
        assertEquals(Integer.valueOf(10), resultY.y);
    }
@JsonDeserialize(builder = SimpleBuilderXY.class)
    static class ValueClassXY {
        final int x;
        final int y;

        ValueClassXY(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
        }
    }
static class SimpleBuilderXY {
        public int x;
        public int y;

        @JsonView(ViewX.class)
        public SimpleBuilderXY withX(int value) {
            x = value;
            return this;
        }

        @JsonView(ViewY.class)
        public SimpleBuilderXY withY(int value) {
            y = value;
            return this;
        }

        public ValueClassXY build() {
            return new ValueClassXY(x, y);
        }
    }
static class ViewX { }
static class ViewY { }
@JsonDeserialize(builder = CreatorBuilderXY.class)
    static class CreatorValueXY {
        final Integer x;
        final Integer y;

        CreatorValueXY(Integer x, Integer y) {
            this.x = x;
            this.y = y;
        }
    }
@JsonIgnoreProperties({ "bogus" })
    static class CreatorBuilderXY {
        public Integer x;
        public Integer y;

        @JsonCreator
        public CreatorBuilderXY(@JsonProperty("x") @JsonView(ViewX.class) Integer x,
                @JsonProperty("y") @JsonView(ViewY.class) Integer y) {
            this.x = x;
            this.y = y;
        }

        public CreatorValueXY build() {
            return new CreatorValueXY(x, y);
        }
    }
static class POJO562 {
        String a;
        Map<String, Object> stuff;

        @JsonCreator
        public POJO562(@JsonProperty("a") String a,
                @JsonAnySetter Map<String, Object> leftovers) {
            this.a = a;
            stuff = leftovers;
        }
    }
static class POJO562WithAnnotationOnBothCtorParamAndField {
        String a;
        @JsonAnySetter
        Map<String, Object> stuffFromField;
        Map<String, Object> stuffFromConstructor;

        @JsonCreator
        public POJO562WithAnnotationOnBothCtorParamAndField(@JsonProperty("a") String a,
                @JsonAnySetter Map<String, Object> leftovers) {
            this.a = a;
            stuffFromConstructor = leftovers;
        }
    }
static class POJO562WithField {
        String a;
        Map<String, Object> stuff;
        public String b;

        @JsonCreator
        public POJO562WithField(@JsonProperty("a") String a,
                @JsonAnySetter Map<String, Object> leftovers) {
            this.a = a;
            stuff = leftovers;
        }
    }
static class PojoWithNodeAnySetter {
        String a;
        JsonNode anySetterNode;

        @JsonCreator
        public PojoWithNodeAnySetter(@JsonProperty("a") String a,
                @JsonAnySetter JsonNode leftovers) {
            this.a = a;
            anySetterNode = leftovers;
        }
    }
static class MultipleAny562 {
        @JsonCreator
        public MultipleAny562(@JsonProperty("a") String a,
                @JsonAnySetter Map<String, Object> leftovers,
                @JsonAnySetter Map<String, Object> leftovers2) { }
    }
static class PojoWithDisabled {
        String a;
        Map<String, Object> stuff;

        @JsonCreator
        public PojoWithDisabled(@JsonProperty("a") String a,
                @JsonAnySetter(enabled = false)
                @JsonProperty("disabledLeftovers") Map<String, Object> leftovers) {
            this.a = a;
            stuff = leftovers;
        }
    }

    void __invoke_testSimpleViews() throws Exception {
        try {
            testSimpleViews();
        } finally {
        }
    }


    void __invoke_testCreatorViews() throws Exception {
        try {
            testCreatorViews();
        } finally {
        }
    }

}
