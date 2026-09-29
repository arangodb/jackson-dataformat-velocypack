package tools.jackson.databind.convert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0158F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] PROPERTY_POINT = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 13 05 33 34 02 01");
private static final byte[] PROPERTY_TEXT = VPackWireFixtureTest.hex(
            "14 0d 44 74 65 78 74 44 59 61 79 21 01");
private static final byte[] PROPERTY_TEXT_LIST_ABC = VPackWireFixtureTest.hex(
            "14 10 45 74 65 78 74 73 13 07 43 41 42 43 01 01");
private static final byte[] PROPERTY_TEXT_LIST_ABC_LOWER = VPackWireFixtureTest.hex(
            "14 10 45 74 65 78 74 73 13 07 43 61 62 63 01 01");
private static final byte[] ARRAY_1_2 = VPackWireFixtureTest.hex(
            "02 04 31 32");
private static final byte[] OBJECT_VALUES_1_2_3_4 = VPackWireFixtureTest.hex(
            "0b 15 01 46 76 61 6c 75 65 73 "
            + "02 0a 02 04 31 32 02 04 33 34 03");
private static final byte[] OBJECT_VALUE_3_4 = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 02 04 33 34 03");
private static final byte[] OBJECT_VALUES_4_5_5_4 = VPackWireFixtureTest.hex(
            "0b 15 01 46 76 61 6c 75 65 73 "
            + "02 0a 02 04 34 35 02 04 35 34 03");
private static final byte[] OBJECT_VALUES_7_8_8_7 = VPackWireFixtureTest.hex(
            "0b 15 01 46 76 61 6c 75 65 73 "
            + "02 0a 02 04 37 38 02 04 38 37 03");
private static final byte[] OBJECT_VALUES_A_1_2 = VPackWireFixtureTest.hex(
            "0b 15 01 46 76 61 6c 75 65 73 "
            + "0b 0a 01 41 61 02 04 31 32 03 03");
private static final byte[] OBJECT_LIST_HELLO_WORLD = VPackWireFixtureTest.hex(
            "0b 1a 01 44 6c 69 73 74 02 11 02 0f 4c "
            + "48 65 6c 6c 6f 20 77 6f 72 6c 64 21 03");
private static final byte[] OBJECT_STUFF_TARGET = VPackWireFixtureTest.hex(
            "0b 13 01 45 73 74 75 66 66 02 09 46 54 61 72 67 65 74 03");
private static final byte[] OBJECT_A_2_B_4 = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 32 41 62 34 03 06");

    void testPropertyAnnotationLowerCasing() throws Exception {
        LowerCaseText value = MAPPER.readValue(PROPERTY_TEXT, LowerCaseText.class);
        assertNotNull(value);
        assertNotNull(value.text);
        assertEquals("yay!", value.text);
    }

    void testPropertyAnnotationSimple() throws Exception {
        PointWrapper value = MAPPER.readValue(PROPERTY_POINT, PointWrapper.class);
        assertNotNull(value);
        assertNotNull(value.value);
        assertEquals(3, value.value.x);
        assertEquals(4, value.value.y);
    }

    void testPropertyAnnotationStringListLC() throws Exception {
        LowerCaseTextList value = MAPPER.readValue(PROPERTY_TEXT_LIST_ABC,
                LowerCaseTextList.class);
        assertNotNull(value);
        assertNotNull(value.texts);
        assertEquals(Collections.singletonList("abc"), value.texts);
    }

    void testPropertyAnnotationStringListWithDeserializerLC() throws Exception {
        LowerCaseTextListWithDeserializer value = MAPPER.readValue(
                PROPERTY_TEXT_LIST_ABC_LOWER, LowerCaseTextListWithDeserializer.class);
        assertNotNull(value);
        assertNotNull(value.texts);
        assertEquals(Collections.singletonList("abc!"), value.texts);
    }
@JsonSerialize(converter = ConvertingBeanSerializationConverter.class)
    static class ConvertingBean {
        public int x, y;
        ConvertingBean(int x, int y) { this.x = x; this.y = y; }
    }
static class ConvertingBeanContainer {
        public List<ConvertingBean> values;
        ConvertingBeanContainer(ConvertingBean... beans) { values = Arrays.asList(beans); }
    }
static class ConvertingBeanSerializationConverter extends StdConverter<ConvertingBean, int[]> {
        @Override public int[] convert(ConvertingBean value) {
            return new int[] { value.x, value.y };
        }
    }
static class Point {
        public int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }
static class PointConverter extends StdConverter<Point, int[]> {
        @Override public int[] convert(Point value) {
            return new int[] { value.x, value.y };
        }
    }
static class PointWrapper {
        @JsonDeserialize(converter = PointDeserializationConverter.class)
        public Point value;
    }
static class PointDeserializationConverter extends StdConverter<int[], Point> {
        @Override public Point convert(int[] value) {
            return new Point(value[0], value[1]);
        }
    }
static class LowerCaser extends StdConverter<String, String> {
        @Override public String convert(String value) { return value.toLowerCase(); }
    }
static class LowerCaseText {
        @JsonDeserialize(converter = LowerCaser.class)
        public String text;
    }
static class LowerCaseTextList {
        @JsonDeserialize(contentConverter = LowerCaser.class)
        public List<String> texts;
    }
static class UpperCasingStringDeserializer extends StdScalarDeserializer<String> {
        UpperCasingStringDeserializer() { super(String.class); }
        @Override public String deserialize(JsonParser parser, DeserializationContext ctxt) {
            return parser.getValueAsString().toUpperCase() + "!";
        }
    }
static class LowerCaseTextListWithDeserializer {
        @JsonDeserialize(contentUsing = UpperCasingStringDeserializer.class,
                contentConverter = LowerCaser.class)
        public List<String> texts;
    }
static class PointListWrapperArray {
        @JsonSerialize(contentConverter = PointConverter.class)
        public Point[] values;
        PointListWrapperArray(int x, int y) {
            values = new Point[] { new Point(x, y), new Point(y, x) };
        }
    }
static class PointListWrapperList {
        @JsonSerialize(contentConverter = PointConverter.class)
        public List<Point> values;
        PointListWrapperList(int x, int y) {
            values = Arrays.asList(new Point(x, y), new Point(y, x));
        }
    }
static class PointListWrapperMap {
        @JsonSerialize(contentConverter = PointConverter.class)
        public java.util.Map<String, Point> values;
        PointListWrapperMap(String key, int x, int y) {
            values = Collections.singletonMap(key, new Point(x, y));
        }
    }
static class ListWrapper {
        @JsonSerialize(contentConverter = ValueToStringListConverter.class)
        public List<Value> list = Collections.singletonList(new Value());
    }
static class Value { }
static class ValueToStringListConverter extends StdConverter<Value, List<String>> {
        @Override public List<String> convert(Value value) {
            return Collections.singletonList("Hello world!");
        }
    }
static class Bean359 {
        @JsonSerialize(as = List.class, contentAs = Source.class)
        public List<Source> stuff = Collections.singletonList(new Source());
    }
@JsonSerialize(using = TargetSerializer.class)
    static class Target {
        public String unexpected = "Bye.";
    }
@JsonSerialize(converter = SourceToTargetConverter.class)
    static class Source { }
static class SourceToTargetConverter extends StdConverter<Source, Target> {
        @Override public Target convert(Source value) { return new Target(); }
    }
static class TargetSerializer extends ValueSerializer<Target> {
        @Override public void serialize(Target value, JsonGenerator generator,
                SerializationContext ctxt) {
            generator.writeString("Target");
        }
    }
@JsonPropertyOrder({ "a", "b" })
    static class DummyBean {
        public final int a, b;
        DummyBean(int x, int y) { a = x * 2; b = y * 2; }
    }
@JsonSerialize(converter = UntypedConvertingBeanConverter.class)
    static class ConvertingBeanWithUntypedConverter {
        public int x, y;
        ConvertingBeanWithUntypedConverter(int x, int y) { this.x = x; this.y = y; }
    }
static class UntypedConvertingBeanConverter
            extends StdConverter<ConvertingBeanWithUntypedConverter, Object> {
        @Override public Object convert(ConvertingBeanWithUntypedConverter value) {
            return new DummyBean(value.x, value.y);
        }
    }

    void __invoke_testPropertyAnnotationLowerCasing() throws Exception {
        try {
            testPropertyAnnotationLowerCasing();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationSimple() throws Exception {
        try {
            testPropertyAnnotationSimple();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationStringListLC() throws Exception {
        try {
            testPropertyAnnotationStringListLC();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationStringListWithDeserializerLC() throws Exception {
        try {
            testPropertyAnnotationStringListWithDeserializerLC();
        } finally {
        }
    }

}
