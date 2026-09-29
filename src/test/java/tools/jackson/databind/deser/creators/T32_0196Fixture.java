package tools.jackson.databind.deser.creators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.annotation.JsonDeserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#mapAnySetterViaCreator562.
    void mapAnySetterViaCreator562() throws Exception {
        Map<String, Object> expected = new HashMap<>();
        expected.put("b", Integer.valueOf(42));
        expected.put("c", Integer.valueOf(111));

        POJO562 pojo = mapper.readValue(ANY_VALUE, POJO562.class);
        assertEquals("value", pojo.a);
        assertEquals(expected, pojo.stuff);

        pojo = mapper.readValue(ANY_VALUE_REORDERED, POJO562.class);
        assertEquals("value", pojo.a);
        assertEquals(expected, pojo.stuff);

        pojo = mapper.readValue(ANY_VALUE_ONLY_CREATOR, POJO562.class);
        assertEquals("value2", pojo.a);
        assertEquals(Collections.emptyMap(), pojo.stuff);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#mapAnySetterViaCreatorWhenBothCreatorAndFieldAreAnnotated.
    void mapAnySetterViaCreatorWhenBothCreatorAndFieldAreAnnotated() throws Exception {
        POJO562WithAnnotationOnBothCtorParamAndField pojo = mapper.readValue(
                ANY_VALUE, POJO562WithAnnotationOnBothCtorParamAndField.class);

        assertEquals("value", pojo.a);
        assertEquals(Map.of("b", 42, "c", 111), pojo.stuffFromConstructor);
        assertNull(pojo.stuffFromField);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#mapAnySetterViaCreatorAndField.
    void mapAnySetterViaCreatorAndField() throws Exception {
        POJO562WithField pojo = mapper.readValue(ANY_VALUE_WITH_FIELD,
                POJO562WithField.class);

        assertEquals("value", pojo.a);
        assertEquals("xyz", pojo.b);
        assertEquals(Collections.singletonMap("c", "abc"), pojo.stuff);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testNodeAnySetterViaCreator562.
    void testNodeAnySetterViaCreator562() throws Exception {
        PojoWithNodeAnySetter pojo = mapper.readValue(ANY_VALUE, PojoWithNodeAnySetter.class);

        assertEquals("value", pojo.a);
        assertEquals("{\"b\":42,\"c\":111}", pojo.anySetterNode.toString());

        pojo = mapper.readValue(ANY_VALUE_ONLY_CREATOR, PojoWithNodeAnySetter.class);
        assertEquals("value2", pojo.a);
        assertEquals(mapper.createObjectNode(), pojo.anySetterNode);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testAnyMapWithNullCreatorProp.
    void testAnyMapWithNullCreatorProp() throws Exception {
        ObjectMapper failOnNullMapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES).build();

        POJO562 value = failOnNullMapper.readValue(ANY_VALUE_ONLY_CREATOR, POJO562.class);
        assertEquals(Collections.emptyMap(), value.stuff);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testAnyMapWithMissingCreatorProp.
    void testAnyMapWithMissingCreatorProp() throws Exception {
        ObjectMapper failOnMissingMapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES).build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> failOnMissingMapper.readValue(ANY_VALUE_ONLY_CREATOR, POJO562.class));
        assertEquals(true, exception.getMessage().contains("Missing creator property"));

        POJO562 value = failOnMissingMapper.readValue(ANY_VALUE_ONE, POJO562.class);
        assertEquals(Collections.singletonMap("b", "x"), value.stuff);
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testAnyMapWithNullOrMissingCreatorProp.
    void testAnyMapWithNullOrMissingCreatorProp() throws Exception {
        ObjectMapper failOnBothMapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
                .build();

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> failOnBothMapper.readValue(ANY_VALUE_ONLY_CREATOR, POJO562.class));
        assertEquals(true, exception.getMessage().contains("Missing creator property"));
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#anySetterCreatorPreservesOrder5353.
    void anySetterCreatorPreservesOrder5353() throws Exception {
        POJO562 pojo = mapper.readValue(ANY_VALUE_ORDERED, POJO562.class);

        assertEquals("value", pojo.a);
        ArrayList<String> keys = new ArrayList<>(pojo.stuff.keySet());
        assertEquals(3, keys.size());
        assertEquals("d", keys.get(0));
        assertEquals("c", keys.get(1));
        assertEquals("b", keys.get(2));
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testAnySetterViaCreator562FailForDup.
    void testAnySetterViaCreator562FailForDup() throws Exception {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(VPackWireFixtureTest.hex("0a"), MultipleAny562.class));
        assertEquals(true, exception.getMessage().contains("any-setter"));
    }

    // Provenance: databind/deser/creators/AnySetterForCreator562Test#testAnySetterViaCreator562Disabled.
    void testAnySetterViaCreator562Disabled() throws Exception {
        ObjectMapper strictMapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

        UnrecognizedPropertyException exception = assertThrows(UnrecognizedPropertyException.class,
                () -> strictMapper.readValue(ANY_VALUE_UNKNOWN, PojoWithDisabled.class));
        assertEquals("b", exception.getPropertyName());
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

    void __invoke_mapAnySetterViaCreator562() throws Exception {
        try {
            mapAnySetterViaCreator562();
        } finally {
        }
    }


    void __invoke_mapAnySetterViaCreatorWhenBothCreatorAndFieldAreAnnotated() throws Exception {
        try {
            mapAnySetterViaCreatorWhenBothCreatorAndFieldAreAnnotated();
        } finally {
        }
    }


    void __invoke_mapAnySetterViaCreatorAndField() throws Exception {
        try {
            mapAnySetterViaCreatorAndField();
        } finally {
        }
    }


    void __invoke_testNodeAnySetterViaCreator562() throws Exception {
        try {
            testNodeAnySetterViaCreator562();
        } finally {
        }
    }


    void __invoke_testAnyMapWithNullCreatorProp() throws Exception {
        try {
            testAnyMapWithNullCreatorProp();
        } finally {
        }
    }


    void __invoke_testAnyMapWithMissingCreatorProp() throws Exception {
        try {
            testAnyMapWithMissingCreatorProp();
        } finally {
        }
    }


    void __invoke_testAnyMapWithNullOrMissingCreatorProp() throws Exception {
        try {
            testAnyMapWithNullOrMissingCreatorProp();
        } finally {
        }
    }


    void __invoke_anySetterCreatorPreservesOrder5353() throws Exception {
        try {
            anySetterCreatorPreservesOrder5353();
        } finally {
        }
    }


    void __invoke_testAnySetterViaCreator562FailForDup() throws Exception {
        try {
            testAnySetterViaCreator562FailForDup();
        } finally {
        }
    }


    void __invoke_testAnySetterViaCreator562Disabled() throws Exception {
        try {
            testAnySetterViaCreator562Disabled();
        } finally {
        }
    }

}
