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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0157F0 {
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

    void testNonAbstractDeserialization() throws Exception {
        NonAbstractCustomTypeUser value = MAPPER.readValue(CUSTOM_FIELD_PROPERTY,
                NonAbstractCustomTypeUser.class);
        assertNotNull(value);
        assertNotNull(value.customField);
        assertEquals("x", value.customField.value);
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

    void __invoke_testNonAbstractDeserialization() throws Exception {
        try {
            testNonAbstractDeserialization();
        } finally {
        }
    }

}
