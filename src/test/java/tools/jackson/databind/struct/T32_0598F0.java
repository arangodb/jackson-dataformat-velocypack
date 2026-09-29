package tools.jackson.databind.struct;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0598F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ManagedReferenceNullHandling4758Test#testNullsAsEmptyWithManagedReference().
    void testNullsAsEmptyWithManagedReferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setNullHandling(com.fasterxml.jackson.annotation.JsonSetter.Value
                                .forValueNulls(com.fasterxml.jackson.annotation.Nulls.AS_EMPTY)))
                .build();

        Parent result = mapper.readValue(NULL_CHILDREN_INPUT, Parent.class);
        assertNotNull(result.children,
                "children should be empty list, not null, when Nulls.AS_EMPTY is configured");
        assertTrue(result.children.isEmpty());
    }
private static <T> void assertUnknownArrayElement(Class<T> type) {
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readerFor(type)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                        .readValue(FOUR_VALUES));
        assertTrue(exception.getMessage().contains("Unexpected"));
    }
private static final byte[] NULL_CHILDREN_INPUT = VPackWireFixtureTest.hex(
            "0b 1b 02 44 6e 61 6d 65 46 70 61 72 65 6e 74"
          + "48 63 68 69 6c 64 72 65 6e 18 0f 03");
private static final byte[] SINGLE_BEAN_INPUT = VPackWireFixtureTest.hex(
            "02 09 46 66 6f 6f 62 61 72");
private static final byte[] TWO_VALUES = VPackWireFixtureTest.hex("02 04 31 32");
private static final byte[] FOUR_VALUES = VPackWireFixtureTest.hex("02 06 31 32 33 34");
private static final byte[] TWO_BY_TWO_BY_TWO_BY_TWO = FOUR_VALUES;
private static final byte[] SIMPLE_PROPERTY_INPUT = VPackWireFixtureTest.hex(
            "0b 1d 01 45 76 61 6c 75 65"
          + "06 13 04 1a 46 46 6f 6f 62 61 72 28 2a 28 0d 03 04 0b 0d 03");
private static final byte[] SIMPLE_ROOT_INPUT = VPackWireFixtureTest.hex(
            "06 10 04 19 45 42 75 62 62 61 31 32 03 04 0a 0b");
private static final byte[] POLYMORPHIC_INPUT = VPackWireFixtureTest.hex(
            "06 0d 02 46 44 69 72 65 63 74 01 03 0a");
private static final byte[] MEDIA_ITEM_INPUT = VPackWireFixtureTest.hex(
            "06 27 02 06 1d 02 4f 68 74 74 70 3a 2f 2f 65 78 61 6d 70 6c 65 2f"
          + "47 45 78 61 6d 70 6c 65 03 13 02 05 01 01 01 03 20");
private static final byte[] NULL_COLUMN_OUTPUT = VPackWireFixtureTest.hex(
            "06 0a 02 18 43 62 61 72 03 04");
private static final byte[] ANNOTATION_OVERRIDE_OUTPUT = VPackWireFixtureTest.hex(
            "02 06 02 04 31 32");
private static final byte[] DEFAULT_A_OUTPUT = VPackWireFixtureTest.hex(
            "0b 15 01 45 76 61 6c 75 65 0b 0b 02 41 78 31 41 79 32 03 06 03");
static class Parent {
        public String name;

        @JsonManagedReference
        public List<Item> children = new ArrayList<>();
    }
static class Item {
        public String name;

        @JsonBackReference
        public Parent parent;
    }
static class PojoAsArrayWrapper {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public PojoAsArray value;
    }
@JsonPropertyOrder(alphabetic = true)
    static class PojoAsArray {
        public int x, y;
        public String name;
        public boolean complete;
    }
@JsonPropertyOrder(alphabetic = true)
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class FlatPojo {
        public int x, y;
        public String name;
        public boolean complete;
    }
static class A {
        public B value = new B();
    }
@JsonPropertyOrder(alphabetic = true)
    static class B {
        public int x = 1;
        public int y = 2;
    }
static class ForceArraysIntrospector extends JacksonAnnotationIntrospector {
        private static final long serialVersionUID = 1L;

        @Override
        public JsonFormat.Value findFormat(MapperConfig<?> config, Annotated annotated) {
            return new JsonFormat.Value().withShape(JsonFormat.Shape.ARRAY);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class SingleBean {
        public String name;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class TwoStringsBean {
        public String bar;
        public String foo = "bar";
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    @JsonSubTypes(@JsonSubTypes.Type(value = DirectLayout.class, name = "Direct"))
    interface Layout { }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class DirectLayout implements Layout { }
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
    @JsonPropertyOrder(alphabetic = true)
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

        public CreatorBuilder withC(int value) {
            c = value;
            return this;
        }

        public CreatorValue build() {
            return new CreatorValue(a, b, c);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    record XYZParams(int x, int y, int z) { }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "a", "b", "x", "y" })
    static class CreatorAsArrayShuffled {
        public int a, b;
        public int x, y;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "content", "images" })
    static class MediaItem {
        public MediaContent content;
        public List<MediaPhoto> images;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "uri", "title" })
    static class MediaContent {
        public String uri;
        public String title;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    static class MediaPhoto { }

    void __invoke_testNullsAsEmptyWithManagedReferenceVpack() throws Exception {
        try {
            testNullsAsEmptyWithManagedReferenceVpack();
        } finally {
        }
    }

}
