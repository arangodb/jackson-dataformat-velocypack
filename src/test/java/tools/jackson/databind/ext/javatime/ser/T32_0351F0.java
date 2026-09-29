package tools.jackson.databind.ext.javatime.ser;

import java.time.Month;
import java.time.ZoneId;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0351F0 {
private static final ZoneId Z1 = ZoneId.of("America/Chicago");
private static final ZoneId Z2 = ZoneId.of("America/Anchorage");
private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");
private static final byte[] MONTH_JANUARY_ZERO_BASED = VPackWireFixtureTest.hex("30");
private static final byte[] MONTH_JANUARY_ONE_BASED = VPackWireFixtureTest.hex("31");
private static final byte[] MONTH_OBJECT_ZERO_BASED = VPackWireFixtureTest.hex(
            "0b 0b 01 45 6d 6f 6e 74 68 30 03");
private static final byte[] MONTH_OBJECT_ONE_BASED = VPackWireFixtureTest.hex(
            "0b 0b 01 45 6d 6f 6e 74 68 31 03");
private static final byte[] MONTH_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 16 02 4f 6a 61 76 61 2e 74 69 6d 65 2e 4d 6f 6e 74 68 33 03 13");
private static final byte[] MONTH_SHAPE_INT = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 02 03 33 03");
private static final byte[] MONTH_FRENCH = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 44 6d 61 72 73 03");
private static final byte[] MONTH_SHAPE_ARRAY = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 02 04 28 0c 03");
private static final byte[] OFFSET_STRING_01 = VPackWireFixtureTest.hex(
            "59 31 39 36 39 2d 31 32 2d 33 31 54 31 38 3a 30 30 3a 30 30 2d 30 36 3a 30 30");
private static final byte[] OFFSET_STRING_02 = VPackWireFixtureTest.hex(
            "63 31 39 37 33 2d 31 31 2d 32 39 54 31 31 3a 33 33 3a 30 39 2e 31 38 33 39 31 37 33 32 32 2d 31 30 3a 30 30");
private static final byte[] OFFSET_STRING_03 = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 30 36 2d 30 37 54 30 38 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");
private static final byte[] ZONED_CONTEXT_OFF = VPackWireFixtureTest.hex(
            "63 32 30 32 30 2d 30 36 2d 30 37 54 30 38 3a 30 39 3a 31 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30");

    // Provenance: MonthSerializerTest#testSerializationFromEnum.
    void testMonthSerializationFromEnumVpack() throws Exception {
        assertArrayEquals(MONTH_JANUARY_ONE_BASED,
                monthMapper(true).writeValueAsBytes(Month.JANUARY));
        assertArrayEquals(MONTH_JANUARY_ZERO_BASED,
                monthMapper(false).writeValueAsBytes(Month.JANUARY));
    }

    // Provenance: MonthSerializerTest#testSerializationWithTypeInfo.
    void testMonthSerializationWithTypeInfoVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(java.time.temporal.TemporalAccessor.class, TemporalTypeInfo.class)
                .build();
        assertArrayEquals(MONTH_TYPE_INFO, mapper.writeValueAsBytes(Month.MARCH));
    }

    // Provenance: MonthSerializerTest#testDefaultSerialization.
    void testMonthDefaultSerializationVpack() throws Exception {
        assertArrayEquals(MONTH_JANUARY_ONE_BASED,
                VPackMapper.builder().build().writeValueAsBytes(Month.JANUARY));
    }

    
    // Provenance: MonthSerializerTest#testParameterizedOneBasedVsIndex.
    void testMonthParameterizedOneBasedVsIndexVpack(boolean oneBased, Object input,
            byte[] expected) throws Exception {
        ObjectWriter writer = monthMapper(oneBased).writer();
        assertArrayEquals(expected, writer.writeValueAsBytes(input));
    }

    // Provenance: MonthSerializerTest#testOneBasedSerialization.
    void testMonthOneBasedSerializationVpack() throws Exception {
        assertArrayEquals(MONTH_OBJECT_ZERO_BASED,
                monthMapper(false).writeValueAsBytes(new Wrapper(Month.JANUARY)));
        assertArrayEquals(MONTH_OBJECT_ONE_BASED,
                monthMapper(true).writeValueAsBytes(new Wrapper(Month.JANUARY)));
    }

    // Provenance: MonthSerializerTest#testSerializationWithShapeInt.
    void testMonthSerializationWithShapeIntVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        assertArrayEquals(MONTH_SHAPE_INT,
                mapper.writeValueAsBytes(new ShapeIntWrapper(Month.MARCH)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0b 01 45 76 61 6c 75 65 33 03"),
                mapper.writeValueAsBytes(new NoShapeIntWrapper(Month.MARCH)));
    }

    // Provenance: MonthSerializerTest#testSerializationWithFrLocale.
    void testMonthSerializationWithFrLocaleVpack() throws Exception {
        assertArrayEquals(MONTH_FRENCH,
                VPackMapper.builder().build().writeValueAsBytes(new FrBean(Month.MARCH)));
    }

    // Provenance: MonthSerializerTest#testSerializationWithShapeArray.
    void testMonthSerializationWithShapeArrayVpack() throws Exception {
        assertArrayEquals(MONTH_SHAPE_ARRAY,
                VPackMapper.builder().build().writeValueAsBytes(new ShapeArrayBean(Month.DECEMBER)));
    }
private static ObjectMapper monthMapper(boolean oneBased) {
        VPackMapper.Builder builder = VPackMapper.builder();
        if (oneBased) {
            builder.enable(DateTimeFeature.ONE_BASED_MONTHS);
        } else {
            builder.disable(DateTimeFeature.ONE_BASED_MONTHS);
        }
        return builder.build();
    }
private static ObjectMapper stringMapper() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
private static java.util.stream.Stream<Arguments> oneBasedVsIndex() {
        return java.util.stream.Stream.of(
                Arguments.of(false, Month.JANUARY, MONTH_JANUARY_ZERO_BASED),
                Arguments.of(true, Month.JANUARY, MONTH_JANUARY_ONE_BASED),
                Arguments.of(false, new Wrapper(Month.JANUARY), MONTH_OBJECT_ZERO_BASED),
                Arguments.of(true, new Wrapper(Month.JANUARY), MONTH_OBJECT_ONE_BASED));
    }
static class Wrapper {
        public Month month;
        Wrapper(Month month) { this.month = month; }
    }
static class ShapeIntWrapper {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Month value;
        ShapeIntWrapper(Month value) { this.value = value; }
    }
static class NoShapeIntWrapper {
        public Month value;
        NoShapeIntWrapper(Month value) { this.value = value; }
    }
static class FrBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM", locale = "fr")
        public Month value;
        FrBean(Month value) { this.value = value; }
    }
static class ShapeArrayBean {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public Month value;
        ShapeArrayBean(Month value) { this.value = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testMonthSerializationFromEnumVpack() throws Exception {
        try {
            testMonthSerializationFromEnumVpack();
        } finally {
        }
    }


    void __invoke_testMonthSerializationWithTypeInfoVpack() throws Exception {
        try {
            testMonthSerializationWithTypeInfoVpack();
        } finally {
        }
    }


    void __invoke_testMonthDefaultSerializationVpack() throws Exception {
        try {
            testMonthDefaultSerializationVpack();
        } finally {
        }
    }


    void __invoke_testMonthParameterizedOneBasedVsIndexVpack(boolean oneBased, Object input, byte[] expected) throws Exception {
        try {
            testMonthParameterizedOneBasedVsIndexVpack(oneBased, input, expected);
        } finally {
        }
    }


    void __invoke_testMonthOneBasedSerializationVpack() throws Exception {
        try {
            testMonthOneBasedSerializationVpack();
        } finally {
        }
    }


    void __invoke_testMonthSerializationWithShapeIntVpack() throws Exception {
        try {
            testMonthSerializationWithShapeIntVpack();
        } finally {
        }
    }


    void __invoke_testMonthSerializationWithFrLocaleVpack() throws Exception {
        try {
            testMonthSerializationWithFrLocaleVpack();
        } finally {
        }
    }


    void __invoke_testMonthSerializationWithShapeArrayVpack() throws Exception {
        try {
            testMonthSerializationWithShapeArrayVpack();
        } finally {
        }
    }

}
