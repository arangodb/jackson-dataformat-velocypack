package tools.jackson.databind.struct;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0599Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_WITH_VIEWS = VPackMapper.builder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

    // Provenance: POJOAsArrayTest#testSerializeArrayWithAnyGetterAsRoot().
    void testSerializeArrayWithAnyGetterAsRootVpack() throws Exception {
        assertArrayEquals(ANY_GETTER_ROOT_OUTPUT,
                MAPPER.writeValueAsBytes(new BeanWithAnyGetter()));
    }

    // Provenance: POJOAsArrayTest#testSerializeArrayWithAnyGetterWithWrapper().
    void testSerializeArrayWithAnyGetterWithWrapperVpack() throws Exception {
        WrapperForAnyGetter wrapper = new WrapperForAnyGetter();
        wrapper.value = new BeanWithAnyGetter();
        assertArrayEquals(ANY_GETTER_WRAPPER_OUTPUT, MAPPER.writeValueAsBytes(wrapper));
    }

    // Provenance: POJOAsArrayTest#testSerializeAsArrayWithSingleProperty().
    void testSerializeAsArrayWithSinglePropertyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertArrayEquals(FOO_OUTPUT, mapper.writeValueAsBytes(new SingleBean()));
    }

    // Provenance: POJOAsArrayTest#testSimpleBuilder().
    void testSimpleBuilderVpack() throws Exception {
        ValueClassXY value = MAPPER.readValue(TWO_VALUES, ValueClassXY.class);
        assertEquals(2, value.x);
        assertEquals(3, value.y);
    }

    // Provenance: POJOAsArrayTest#testSimpleWithIndex().
    void testSimpleWithIndexVpack() throws Exception {
        CreatorWithIndex value = MAPPER.readValue(TWO_REVERSED_VALUES,
                CreatorWithIndex.class);
        assertEquals(2, value.a);
        assertEquals(1, value.b);
    }

    // Provenance: POJOAsArrayTest#testUnknownExtraProp().
    void testUnknownExtraPropVpack() throws Exception {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readerFor(PojoAsArrayWrapper.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(UNKNOWN_EXTRA_INPUT));
        assertTrue(exception.getMessage().contains("Unexpected"));

        PojoAsArrayWrapper value = MAPPER.readerFor(PojoAsArrayWrapper.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(UNKNOWN_EXTRA_INPUT);
        assertNotNull(value);
        assertNotNull(value.value);
        assertTrue(value.value.complete);
        assertEquals("Foobar", value.value.name);
        assertEquals(42, value.value.x);
        assertEquals(13, value.value.y);
    }

    // Provenance: POJOAsArrayTest#testWithConfigOverrides().
    void testWithConfigOverridesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(NonAnnotatedXY.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.ARRAY)))
                .build();
        assertArrayEquals(TWO_THREE_OUTPUT, mapper.writeValueAsBytes(new NonAnnotatedXY(2, 3)));

        NonAnnotatedXY result = mapper.readValue(TWO_THREE_OUTPUT, NonAnnotatedXY.class);
        assertNotNull(result);
        assertEquals(3, result.y);
    }

    // Provenance: POJOAsArrayTest#testWithCreator().
    void testWithCreatorVpack() throws Exception {
        var reader = MAPPER.readerFor(CreatorValue.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

        CreatorValue value = reader.readValue(THREE_VALUES);
        assertEquals(1, value.a);
        assertEquals(2, value.b);
        assertEquals(3, value.c);

        value = reader.readValue(TWO_VALUES);
        assertEquals(1, value.a);
        assertEquals(2, value.b);
        assertEquals(0, value.c);

        value = reader.readValue(ONE_VALUE);
        assertEquals(1, value.a);
        assertEquals(0, value.b);
        assertEquals(0, value.c);

        value = reader.readValue(EMPTY_ARRAY);
        assertEquals(0, value.a);
        assertEquals(0, value.b);
        assertEquals(0, value.c);
    }

    // Provenance: POJOAsArrayTest#testWithCreatorAndView().
    void testWithCreatorAndViewVpack() throws Exception {
        var reader = MAPPER_WITH_VIEWS.readerFor(CreatorValue.class);

        CreatorValue value = reader.withView(String.class).readValue(THREE_VALUES);
        assertEquals(1, value.a);
        assertEquals(2, value.b);
        assertEquals(3, value.c);

        value = reader.withView(Character.class).readValue(THREE_VALUES);
        assertEquals(1, value.a);
        assertEquals(2, value.b);
        assertEquals(0, value.c);
    }

    // Provenance: POJOAsArrayTest#testWithCreatorsOrdered().
    void testWithCreatorsOrderedVpack() throws Exception {
        CreatorAsArray input = new CreatorAsArray(3, 4);
        input.a = 1;
        input.b = 2;

        assertArrayEquals(ORDERED_OUTPUT, MAPPER_WITH_VIEWS.writeValueAsBytes(input));
        CreatorAsArray output = MAPPER_WITH_VIEWS.readValue(ORDERED_OUTPUT,
                CreatorAsArray.class);
        assertEquals(1, output.a);
        assertEquals(2, output.b);
        assertEquals(3, output.x);
        assertEquals(4, output.y);
    }

    // Provenance: POJOAsArrayTest#testWithCreatorsShuffled().
    void testWithCreatorsShuffledVpack() throws Exception {
        CreatorAsArrayShuffled input = new CreatorAsArrayShuffled(3, 4);
        input.a = 1;
        input.b = 2;

        assertArrayEquals(SHUFFLED_OUTPUT, MAPPER_WITH_VIEWS.writeValueAsBytes(input));
        CreatorAsArrayShuffled output = MAPPER_WITH_VIEWS.readValue(SHUFFLED_OUTPUT,
                CreatorAsArrayShuffled.class);
        assertEquals(1, output.a);
        assertEquals(2, output.b);
        assertEquals(3, output.x);
        assertEquals(4, output.y);
    }

    // Provenance: POJOAsArrayTest#testWithCustomTypeId().
    void testWithCustomTypeIdVpack() throws Exception {
        // The source test uses a writer/read round trip; this literal is the
        // corresponding independently calculated wrapper-array representation.
        Outer646 result = MAPPER.readValue(CUSTOM_TYPE_INPUT, Outer646.class);
        assertNotNull(result);
        assertNotNull(result.attributes);
        assertEquals(1, result.attributes.size());
        assertEquals("first", result.attributes.get("entry1").strValue);
        assertFalse(result.attributes.get("entry1").boolValue);
        assertEquals(2, result.attributes.get("entry1").nestedItems.size());
    }
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] ONE_VALUE = VPackWireFixtureTest.hex("02 03 31");
private static final byte[] TWO_VALUES = VPackWireFixtureTest.hex("02 04 31 32");
private static final byte[] THREE_VALUES = VPackWireFixtureTest.hex("02 05 31 32 33");
private static final byte[] TWO_REVERSED_VALUES = VPackWireFixtureTest.hex("02 04 32 31");
private static final byte[] TWO_THREE_OUTPUT = VPackWireFixtureTest.hex("02 04 32 33");
private static final byte[] FOO_OUTPUT = VPackWireFixtureTest.hex("43 66 6f 6f");
private static final byte[] ORDERED_OUTPUT = VPackWireFixtureTest.hex("02 06 33 34 31 32");
private static final byte[] SHUFFLED_OUTPUT = VPackWireFixtureTest.hex("02 06 31 32 33 34");
private static final byte[] ANY_GETTER_OBJECT = VPackWireFixtureTest.hex(
            "0b 25 02 47 74 68 69 72 64 5f 41 47 74 68 69 72 64 5f 41"
          + "47 74 68 69 72 64 5f 42 47 74 68 69 72 64 5f 42 03 13");
private static final byte[] ANY_GETTER_ROOT_OUTPUT = VPackWireFixtureTest.hex(
            "06 3f 04 45 66 69 72 73 74 46 73 65 63 6f 6e 64 45 66 6f 72 74 68"
          + "0b 25 02 47 74 68 69 72 64 5f 41 47 74 68 69 72 64 5f 41"
          + "47 74 68 69 72 64 5f 42 47 74 68 69 72 64 5f 42 03 13"
          + "03 09 10 16");
private static final byte[] ANY_GETTER_WRAPPER_OUTPUT = VPackWireFixtureTest.hex(
            "0b 49 01 45 76 61 6c 75 65"
          + "06 3f 04 45 66 69 72 73 74 46 73 65 63 6f 6e 64 45 66 6f 72 74 68"
          + "0b 25 02 47 74 68 69 72 64 5f 41 47 74 68 69 72 64 5f 41"
          + "47 74 68 69 72 64 5f 42 47 74 68 69 72 64 5f 42 03 13"
          + "03 09 10 16 03");
private static final byte[] UNKNOWN_EXTRA_INPUT = VPackWireFixtureTest.hex(
            "0b 1f 01 45 76 61 6c 75 65"
          + "06 15 05 1a 46 46 6f 6f 62 61 72 28 2a 28 0d 19 03 04 0b 0d 0f 03");
private static final byte[] CUSTOM_TYPE_INPUT = VPackWireFixtureTest.hex(
            "02 68 0b 66 01 46 65 6e 74 72 79 31 06 5b 02 78" +
                "74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61" +
                "74 61 62 69 6e 64 2e 73 74 72 75 63 74 2e 54 33" +
                "32 5f 30 35 39 39 46 69 78 74 75 72 65 24 54 68" +
                "65 49 74 65 6d 36 34 36 06 1d 03 19 02 10 02 07" +
                "44 66 6f 6f 31 02 07 44 66 6f 6f 32 45 66 69 72" +
                "73 74 03 04 14 03 3c 03");
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class NonAnnotatedXY {
        public int x, y;

        NonAnnotatedXY() { }
        NonAnnotatedXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class SingleBean {
        public String name = "foo";
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class CreatorWithIndex {
        int a, b;

        @JsonCreator
        CreatorWithIndex(@JsonProperty(index = 0, value = "a") int a,
                @JsonProperty(index = 1, value = "b") int b) {
            this.a = a;
            this.b = b;
        }
    }
@JsonDeserialize(builder = SimpleBuilderXY.class)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class ValueClassXY {
        final int x, y;

        ValueClassXY(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class SimpleBuilderXY {
        public int x, y;

        public SimpleBuilderXY withX(int value) {
            x = value;
            return this;
        }

        public SimpleBuilderXY withY(int value) {
            y = value;
            return this;
        }

        public ValueClassXY build() {
            return new ValueClassXY(x, y);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class PojoAsArray {
        public boolean complete;
        public String name;
        public int x, y;
    }
static class PojoAsArrayWrapper {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public PojoAsArray value;
    }
@JsonDeserialize(builder = CreatorBuilder.class)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class CreatorValue {
        final int a, b, c;

        CreatorValue(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class CreatorBuilder {
        private final int a, b;
        private int c;

        @JsonCreator
        CreatorBuilder(@JsonProperty("a") int a, @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }

        @JsonView(String.class)
        public CreatorBuilder withC(int value) {
            c = value;
            return this;
        }

        public CreatorValue build() {
            return new CreatorValue(a, b, c);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class CreatorAsArray {
        protected int x, y;
        public int a, b;

        @JsonCreator
        CreatorAsArray(@JsonProperty("x") int x, @JsonProperty("y") int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "a", "b", "x", "y" })
    static class CreatorAsArrayShuffled {
        protected int x, y;
        public int a, b;

        @JsonCreator
        CreatorAsArrayShuffled(@JsonProperty("x") int x, @JsonProperty("y") int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "firstProperty", "secondProperties", "forthProperty" })
    static class BeanWithAnyGetter {
        public String firstProperty = "first";
        public String secondProperties = "second";
        public String forthProperty = "forth";

        @JsonAnyGetter
        public Map<String, String> getAnyProperty() {
            Map<String, String> values = new TreeMap<>();
            values.put("third_A", "third_A");
            values.put("third_B", "third_B");
            return values;
        }
    }
static class WrapperForAnyGetter {
        public BeanWithAnyGetter value;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class Outer646 {
        protected Map<String, TheItem646> attributes = new HashMap<>();

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Map<String, TheItem646> getAttributes() {
            return attributes;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class TheItem646 {
        String strValue;
        boolean boolValue;
        java.util.List<NestedItem> nestedItems;

        public String getStrValue() { return strValue; }
        public boolean isBoolValue() { return boolValue; }
        public java.util.List<NestedItem> getNestedItems() { return nestedItems; }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class NestedItem {
        public String nestedStrValue;
    }

    void __invoke_testSerializeArrayWithAnyGetterAsRootVpack() throws Exception {
        try {
            testSerializeArrayWithAnyGetterAsRootVpack();
        } finally {
        }
    }


    void __invoke_testSerializeArrayWithAnyGetterWithWrapperVpack() throws Exception {
        try {
            testSerializeArrayWithAnyGetterWithWrapperVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsArrayWithSinglePropertyVpack() throws Exception {
        try {
            testSerializeAsArrayWithSinglePropertyVpack();
        } finally {
        }
    }


    void __invoke_testSimpleBuilderVpack() throws Exception {
        try {
            testSimpleBuilderVpack();
        } finally {
        }
    }


    void __invoke_testSimpleWithIndexVpack() throws Exception {
        try {
            testSimpleWithIndexVpack();
        } finally {
        }
    }


    void __invoke_testUnknownExtraPropVpack() throws Exception {
        try {
            testUnknownExtraPropVpack();
        } finally {
        }
    }


    void __invoke_testWithConfigOverridesVpack() throws Exception {
        try {
            testWithConfigOverridesVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorVpack() throws Exception {
        try {
            testWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorAndViewVpack() throws Exception {
        try {
            testWithCreatorAndViewVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorsOrderedVpack() throws Exception {
        try {
            testWithCreatorsOrderedVpack();
        } finally {
        }
    }


    void __invoke_testWithCreatorsShuffledVpack() throws Exception {
        try {
            testWithCreatorsShuffledVpack();
        } finally {
        }
    }


    void __invoke_testWithCustomTypeIdVpack() throws Exception {
        try {
            testWithCustomTypeIdVpack();
        } finally {
        }
    }

}
