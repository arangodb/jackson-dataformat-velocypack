package tools.jackson.databind.convert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0157F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] TWO_INTS = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] VALUES_INT_PAIRS_PROPERTY = VPackWireFixtureTest.hex(
            "14 17 46 76 61 6c 75 65 73 "
            + "13 0d 13 05 31 32 02 13 05 33 34 02 02 01");
private static final byte[] TEXT_ARRAY_PROPERTY = VPackWireFixtureTest.hex(
            "14 10 45 74 65 78 74 73 13 07 43 41 42 43 01 01");
private static final byte[] POINT_ARRAY_PROPERTY = VPackWireFixtureTest.hex(
            "14 17 46 76 61 6c 75 65 73 "
            + "13 0d 13 05 34 35 02 13 05 35 34 02 02 01");
private static final byte[] VALUES_LIST_PROPERTY = VPackWireFixtureTest.hex(
            "14 17 46 76 61 6c 75 65 73 "
            + "13 0d 13 05 37 38 02 13 05 38 37 02 02 01");
private static final byte[] VALUES_MAP_PROPERTY = VPackWireFixtureTest.hex(
            "14 14 46 76 61 6c 75 65 73 "
            + "14 0a 41 61 13 05 31 32 02 01 01");
private static final byte[] VALUES_ENUM_MAP_PROPERTY = VPackWireFixtureTest.hex(
            "14 14 46 76 61 6c 75 65 73 "
            + "14 0a 41 41 13 05 31 32 02 01 01");
private static final byte[] VALUES_ENUM_SET_PROPERTY = VPackWireFixtureTest.hex(
            "14 11 46 76 61 6c 75 65 73 13 07 41 61 41 62 02 01");
private static final byte[] REF_PROPERTY = VPackWireFixtureTest.hex(
            "14 0c 43 72 65 66 13 05 31 32 02 01");
private static final byte[] OPTIONAL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0c 43 6f 70 74 13 05 32 33 02 01");
private static final byte[] DECIMAL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 44 31 2e 32 35 01");
private static final byte[] CUSTOM_FIELD_PROPERTY = VPackWireFixtureTest.hex(
            "14 11 4b 63 75 73 74 6f 6d 46 69 65 6c 64 41 78 01");

    void testClassAnnotationForLists() throws Exception {
        ConvertingBeanContainer container = MAPPER.readValue(VALUES_INT_PAIRS_PROPERTY,
                ConvertingBeanContainer.class);
        assertNotNull(container);
        assertNotNull(container.values);
        assertEquals(2, container.values.size());
        assertEquals(4, container.values.get(1).y);
    }

    void testClassAnnotationSimple() throws Exception {
        ConvertingBean bean = MAPPER.readValue(TWO_INTS, ConvertingBean.class);
        assertNotNull(bean);
        assertEquals(1, bean.x);
        assertEquals(2, bean.y);
    }

    void testConvertToAbstract() throws Exception {
        Issue795Bean bean = MAPPER.readValue(DECIMAL_PROPERTY, Issue795Bean.class);
        assertNotNull(bean.value);
        assertInstanceOf(BigDecimal.class, bean.value);
        assertEquals(new BigDecimal("1.25"), bean.value);
    }

    void testPropertyAnnotationArrayLC() throws Exception {
        LowerCaseTextArray value = MAPPER.readValue(TEXT_ARRAY_PROPERTY,
                LowerCaseTextArray.class);
        assertNotNull(value);
        assertNotNull(value.texts);
        assertEquals(1, value.texts.length);
        assertEquals("abc", value.texts[0]);
    }

    void testPropertyAnnotationForArrays() throws Exception {
        PointWrapperArray value = MAPPER.readValue(POINT_ARRAY_PROPERTY,
                PointWrapperArray.class);
        assertNotNull(value);
        assertNotNull(value.values);
        assertEquals(2, value.values.length);
        assertEquals(5, value.values[1].x);
    }

    void testPropertyAnnotationForEnumMaps() throws Exception {
        PointWrapperEnumMap value = MAPPER.readValue(VALUES_ENUM_MAP_PROPERTY,
                PointWrapperEnumMap.class);
        assertNotNull(value);
        assertNotNull(value.values);
        assertEquals(1, value.values.size());
        Point point = value.values.get(EnumKey.A);
        assertNotNull(point);
        assertEquals(1, point.x);
        assertEquals(2, point.y);
    }

    void testPropertyAnnotationForEnumSets() throws Exception {
        PointWrapperEnumSet value = MAPPER.readValue(VALUES_ENUM_SET_PROPERTY,
                PointWrapperEnumSet.class);
        assertNotNull(value);
        assertEquals(EnumSet.of(EnumKey.A, EnumKey.B), value.values);
    }

    void testPropertyAnnotationForLists() throws Exception {
        PointWrapperList value = MAPPER.readValue(VALUES_LIST_PROPERTY,
                PointWrapperList.class);
        assertNotNull(value);
        assertNotNull(value.values);
        assertEquals(2, value.values.size());
        assertEquals(7, value.values.get(0).x);
    }

    void testPropertyAnnotationForMaps() throws Exception {
        PointWrapperMap value = MAPPER.readValue(VALUES_MAP_PROPERTY,
                PointWrapperMap.class);
        assertNotNull(value);
        assertNotNull(value.values);
        assertEquals(1, value.values.size());
        Point point = value.values.get("a");
        assertNotNull(point);
        assertEquals(1, point.x);
        assertEquals(2, point.y);
    }

    void testPropertyAnnotationForOptionals() throws Exception {
        PointWrapperOptional value = MAPPER.readValue(OPTIONAL_PROPERTY,
                PointWrapperOptional.class);
        assertNotNull(value);
        assertNotNull(value.opt);
        assertEquals(2, value.opt.get().x);
        assertEquals(3, value.opt.get().y);
    }

    void testPropertyAnnotationForReferences() throws Exception {
        PointWrapperReference value = MAPPER.readValue(REF_PROPERTY,
                PointWrapperReference.class);
        assertNotNull(value);
        assertNotNull(value.ref);
        assertEquals(1, value.ref.get().x);
        assertEquals(2, value.ref.get().y);
    }
@JsonDeserialize(converter = ConvertingBeanConverter.class)
    static class ConvertingBean {
        protected int x, y;
        protected ConvertingBean(int x, int y) { this.x = x; this.y = y; }
    }
static class ConvertingBeanContainer {
        public List<ConvertingBean> values;
        public ConvertingBeanContainer() { }
        public ConvertingBeanContainer(ConvertingBean... beans) {
            values = Arrays.asList(beans);
        }
    }
static class ConvertingBeanConverter extends StdConverter<int[], ConvertingBean> {
        @Override public ConvertingBean convert(int[] values) {
            return new ConvertingBean(values[0], values[1]);
        }
    }
static class Point {
        protected int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
    }
static class PointConverter extends StdConverter<int[], Point> {
        @Override public Point convert(int[] value) {
            return new Point(value[0], value[1]);
        }
    }
static class PointWrapperArray {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public Point[] values;
    }
static class LowerCaseTextArray {
        @JsonDeserialize(contentConverter = LowerCaser.class)
        public String[] texts;
    }
static class LowerCaser extends StdConverter<String, String> {
        @Override public String convert(String value) { return value.toLowerCase(); }
    }
static class PointWrapperList {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public List<Point> values;
    }
static class PointWrapperMap {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public Map<String, Point> values;
    }
enum EnumKey { A, B }
static class PointWrapperEnumMap {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public EnumMap<EnumKey, Point> values;
    }
static class PointWrapperEnumSet {
        @JsonDeserialize(contentConverter = EnumKeyConverter.class)
        public EnumSet<EnumKey> values;
    }
static class EnumKeyConverter extends StdConverter<String, EnumKey> {
        @Override public EnumKey convert(String value) {
            return EnumKey.valueOf(value.toUpperCase());
        }
    }
static class PointWrapperReference {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public AtomicReference<Point> ref;
    }
static class PointWrapperOptional {
        @JsonDeserialize(contentConverter = PointConverter.class)
        public Optional<Point> opt;
    }
static class ToNumberConverter extends StdConverter<String, Number> {
        @Override public Number convert(String value) { return new BigDecimal(value); }
    }
static class Issue795Bean {
        @JsonDeserialize(converter = ToNumberConverter.class)
        public Number value;
    }
static class NonAbstractCustomType {
        final String value;
        public NonAbstractCustomType(String value) { this.value = value; }
    }
static class NonAbstractCustomTypeDeserializationConverter
            extends StdConverter<String, NonAbstractCustomType> {
        @Override public NonAbstractCustomType convert(String value) {
            return new NonAbstractCustomType(value);
        }
    }
static class NonAbstractCustomTypeUser {
        @JsonProperty
        @JsonDeserialize(converter = NonAbstractCustomTypeDeserializationConverter.class)
        private final NonAbstractCustomType customField;

        @JsonCreator
        NonAbstractCustomTypeUser(@JsonProperty("customField") NonAbstractCustomType customField) {
            this.customField = customField;
        }
    }

    void __invoke_testClassAnnotationForLists() throws Exception {
        try {
            testClassAnnotationForLists();
        } finally {
        }
    }


    void __invoke_testClassAnnotationSimple() throws Exception {
        try {
            testClassAnnotationSimple();
        } finally {
        }
    }


    void __invoke_testConvertToAbstract() throws Exception {
        try {
            testConvertToAbstract();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationArrayLC() throws Exception {
        try {
            testPropertyAnnotationArrayLC();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForArrays() throws Exception {
        try {
            testPropertyAnnotationForArrays();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForEnumMaps() throws Exception {
        try {
            testPropertyAnnotationForEnumMaps();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForEnumSets() throws Exception {
        try {
            testPropertyAnnotationForEnumSets();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForLists() throws Exception {
        try {
            testPropertyAnnotationForLists();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForMaps() throws Exception {
        try {
            testPropertyAnnotationForMaps();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForOptionals() throws Exception {
        try {
            testPropertyAnnotationForOptionals();
        } finally {
        }
    }


    void __invoke_testPropertyAnnotationForReferences() throws Exception {
        try {
            testPropertyAnnotationForReferences();
        } finally {
        }
    }

}
