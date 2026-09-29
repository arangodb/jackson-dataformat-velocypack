package tools.jackson.databind.struct;

import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0600F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper NUMERIC_KEYS = VPackMapper.builder(
            VPackFactory.builder().attributeNameCodec(new VPackAttributeNameCodec() {
                @Override public String decode(BigInteger id) { return id.toString(); }
                @Override public BigInteger encode(String name) {
                    try { return new BigInteger(name); }
                    catch (NumberFormatException e) { return null; }
                }
            }).build()).build();
private static final ObjectMapper VIEWS = VPackMapper.builder()
            .enable(tools.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION)
            .build();

    // Provenance: POJOAsArrayTest#testWithMaps().
    void testWithMapsVpack() throws Exception {
        AsArrayWithMap value = MAPPER.readValue(MAP_INPUT, AsArrayWithMap.class);
        assertNotNull(value.attrs);
        assertEquals(1, value.attrs.size());
        assertEquals(Integer.valueOf(2), value.attrs.get(1));
        byte[] output = NUMERIC_KEYS.writeValueAsBytes(new AsArrayWithMap(1, 2));
        assertArrayEquals(MAP_OUTPUT, output);
    }

    // Provenance: POJOAsArrayTest#testWithView().
    void testWithViewVpack() throws Exception {
        AsArrayWithView input = new AsArrayWithView();
        input.a = 1;
        input.b = 2;
        input.c = 3;
        assertArrayEquals(VIEW_OUTPUT, VIEWS.writerWithView(ViewA.class)
                .writeValueAsBytes(input));

        AsArrayWithView result = VIEWS.readerFor(AsArrayWithView.class)
                .withView(ViewB.class).readValue(VIEW_INPUT);
        assertEquals(3, result.c);
        assertEquals(2, result.b);
        assertEquals(0, result.a);
    }

    // Provenance: POJOAsArrayTest#testWithViewAndCreator().
    void testWithViewAndCreatorVpack() throws Exception {
        AsArrayWithViewAndCreator result = VIEWS.readerFor(AsArrayWithViewAndCreator.class)
                .withView(ViewB.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(VIEW_INPUT);
        assertEquals(3, result.c);
        assertEquals(2, result.b);
        assertEquals(0, result.a);
    }

    // Provenance: POJOAsArrayTest#testWriteSimplePropertyValue().
    void testWriteSimplePropertyValueVpack() throws Exception {
        assertArrayEquals(PROPERTY_OUTPUT,
                MAPPER.writeValueAsBytes(new PojoAsArrayWrapper("Foobar", 42, 13, true)));
    }

    // Provenance: POJOAsArrayTest#testWriteSimpleRootValue().
    void testWriteSimpleRootValueVpack() throws Exception {
        assertArrayEquals(ROOT_OUTPUT,
                MAPPER.writeValueAsBytes(new FlatPojo("Bubba", 1, 2, false)));
    }
private static final byte[] MAP_INPUT = VPackWireFixtureTest.hex(
            "13 09 14 06 41 31 32 01 01");
private static final byte[] MAP_OUTPUT = VPackWireFixtureTest.hex(
            "02 08 0b 06 01 31 32 03");
private static final byte[] VIEW_INPUT = VPackWireFixtureTest.hex("02 05 31 32 33");
private static final byte[] VIEW_OUTPUT = VPackWireFixtureTest.hex("02 05 31 18 33");
private static final byte[] PROPERTY_OUTPUT = VPackWireFixtureTest.hex(
            "0b 1d 01 45 76 61 6c 75 65 06 13 04 1a 46 46 6f 6f 62 61 72 28 2a 28 0d 03 04 0b 0d 03");
private static final byte[] ROOT_OUTPUT = VPackWireFixtureTest.hex(
            "06 10 04 19 45 42 75 62 62 61 31 32 03 04 0a 0b");
private static final byte[] ABSTRACT_INPUT = VPackWireFixtureTest.hex(
            "14 31 44 6b 69 6e 64 48 63 6f 6e 63 72 65 74 65"
          + "42 69 64 41 70 44 6e 65 78 74"
          + "14 16 44 6b 69 6e 64 48 63 6f 6e 63 72 65 74 65"
          + "42 69 64 41 63 02 03");
private static final byte[] ARRAY_REFS_INPUT = VPackWireFixtureTest.hex(
            "0b 25 01 45 6e 6f 64 65 73 06 1b 02"
          + "0b 0b 01 44 6e 61 6d 65 41 61 03"
          + "0b 0b 01 44 6e 61 6d 65 41 62 03 03 0e 03");
private static final byte[] SUBTYPE_INPUT = VPackWireFixtureTest.hex(
            "14 26 48 63 68 69 6c 64 72 65 6e 13 1a"
          + "14 17 44 74 79 70 65 46 53 49 4d 50 4c 45"
          + "45 76 61 6c 75 65 41 78 01 01 01");
private static final byte[] BUILDER_INPUT = VPackWireFixtureTest.hex(
            "0b 49 02 4e 63 6f 6e 74 61 69 6e 65 72 56 61 6c 75 65"
          + "4e 63 6f 6e 74 61 69 6e 65 72 56 61 6c 75 65 47 66 6f 72 77 61 72 64"
          + "0b 1e 01 4c 63 6f 6e 74 65 6e 74 56 61 6c 75 65 4c 63 6f 6e 74 65 6e 74 56 61 6c 75 65 03 03 21");
private static final byte[] CHILD_INPUT = VPackWireFixtureTest.hex(
            "14 06 41 62 0a 01");
private static final byte[] SHEET_INPUT = VPackWireFixtureTest.hex(
            "0b7a014a70726f706572746965730b6b024670316e616d650b2c034474797065"
          + "46737472696e67446e616d654670316e616d654576616c756547703176616c7565"
          + "0f031b4670326e616d650b2c03447479706546737472696e67446e616d65467032"
          + "6e616d654576616c756547703276616c75650f031b033603");
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class AsArrayWithMap {
        public Map<Integer, Integer> attrs;
        public AsArrayWithMap() { }
        AsArrayWithMap(int x, int y) {
            attrs = new java.util.HashMap<>();
            attrs.put(x, y);
        }
    }
static class ViewA { }
static class ViewB { }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class AsArrayWithView {
        @JsonView(ViewA.class) public int a;
        @JsonView(ViewB.class) public int b;
        public int c;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class AsArrayWithViewAndCreator {
        @JsonView(ViewA.class) public int a;
        @JsonView(ViewB.class) public int b;
        public int c;
        @com.fasterxml.jackson.annotation.JsonCreator
        AsArrayWithViewAndCreator(@JsonProperty("a") int a,
                @JsonProperty("b") int b, @JsonProperty("c") int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
static class PojoAsArrayWrapper {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public PojoAsArray value;
        public PojoAsArrayWrapper() { }
        PojoAsArrayWrapper(String name, int x, int y, boolean complete) {
            value = new PojoAsArray(name, x, y, complete);
        }
    }
@JsonPropertyOrder(alphabetic = true)
    static class PojoAsArray {
        public int x, y;
        public String name;
        public boolean complete;
        PojoAsArray() { }
        PojoAsArray(String name, int x, int y, boolean complete) {
            this.name = name; this.x = x; this.y = y; this.complete = complete;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder(alphabetic = true)
    static class FlatPojo {
        public int x, y;
        public String name;
        public boolean complete;
        FlatPojo() { }
        FlatPojo(String name, int x, int y, boolean complete) {
            this.name = name; this.x = x; this.y = y; this.complete = complete;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
    @JsonSubTypes(@JsonSubTypes.Type(value = ConcreteNode.class, name = "concrete"))
    static abstract class AbstractNode {
        public String id;
        @JsonManagedReference public AbstractNode next;
        @JsonBackReference public AbstractNode prev;
    }
static class ConcreteNode extends AbstractNode {
        public ConcreteNode() { }
        ConcreteNode(String id) { this.id = id; }
    }
static class NodeArray {
        @JsonManagedReference("arr") public ArrayNode[] nodes;
    }
static class ArrayNode {
        public String name;
        @JsonBackReference("arr") public NodeArray parent;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = SimpleChild3304.class, name = "SIMPLE"))
    interface IChild3304 { }
static class SimpleChild3304 implements IChild3304 { public String value; }
static class Container3304 {
        @JsonManagedReference public IChild3304[] children;
    }
static class ContainerWithList3304 {
        @JsonManagedReference public List<IChild3304> children;
    }
public static class Container2686 {
        Content2686 forward;
        String containerValue;
        @JsonManagedReference public Content2686 getForward() { return forward; }
        @JsonManagedReference public void setForward(Content2686 value) { forward = value; }
        public String getContainerValue() { return containerValue; }
        public void setContainerValue(String value) { containerValue = value; }
    }
@tools.jackson.databind.annotation.JsonDeserialize(builder = Content2686.Builder.class)
    public static class Content2686 {
        private Container2686 back;
        private String contentValue;
        Content2686(Container2686 back, String value) { this.back = back; contentValue = value; }
        public String getContentValue() { return contentValue; }
        @JsonBackReference public Container2686 getBack() { return back; }
        @JsonPOJOBuilder(withPrefix = "")
        public static class Builder {
            private Container2686 back;
            private String contentValue;
            @JsonBackReference Builder back(Container2686 value) { back = value; return this; }
            Builder contentValue(String value) { contentValue = value; return this; }
            Content2686 build() { return new Content2686(back, contentValue); }
        }
    }
static class Child1878 { @JsonBackReference public Parent1878 b; }
static class Parent1878 { @JsonManagedReference public Child1878 a; }
static class Sheet {
        @JsonManagedReference public Map<String, SheetProperty> properties;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = SheetProperty.class, name = "string"))
    static class SheetProperty {
        public String name;
        public String value;
        @JsonBackReference public Sheet parent;
    }

    void __invoke_testWithMapsVpack() throws Exception {
        try {
            testWithMapsVpack();
        } finally {
        }
    }


    void __invoke_testWithViewVpack() throws Exception {
        try {
            testWithViewVpack();
        } finally {
        }
    }


    void __invoke_testWithViewAndCreatorVpack() throws Exception {
        try {
            testWithViewAndCreatorVpack();
        } finally {
        }
    }


    void __invoke_testWriteSimplePropertyValueVpack() throws Exception {
        try {
            testWriteSimplePropertyValueVpack();
        } finally {
        }
    }


    void __invoke_testWriteSimpleRootValueVpack() throws Exception {
        try {
            testWriteSimpleRootValueVpack();
        } finally {
        }
    }

}
