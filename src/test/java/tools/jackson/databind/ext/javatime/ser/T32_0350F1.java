package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalTime;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0350F1 {
private static final byte[] LOCAL_TIME_TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "06 10 04 28 16 28 1f 35 2a 8d a9 0c 03 05 07 08");
private static final byte[] LOCAL_TIME_TIMESTAMP_04_MILLIS = VPackWireFixtureTest.hex(
            "06 0f 04 28 16 28 1f 35 29 a6 01 03 05 07 08");
private static final byte[] LOCAL_TIME_TYPE_INFO_NANOS = VPackWireFixtureTest.hex(
            "06 29 02 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "06 10 04 28 16 28 1f 35 2a 8d a9 0c 03 05 07 08 03 17");
private static final byte[] LOCAL_TIME_TYPE_INFO_MILLIS = VPackWireFixtureTest.hex(
            "06 28 02 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "06 0f 04 28 16 28 1f 35 29 a6 01 03 05 07 08 03 17");
private static final byte[] LOCAL_TIME_TYPE_INFO_STRING = VPackWireFixtureTest.hex(
            "06 2c 02 53 6a 61 76 61 2e 74 69 6d 65 2e 4c 6f 63 61 6c 54 69 6d 65 "
          + "52 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 03 17");
private static final byte[] LOCAL_TIME_CUSTOM = VPackWireFixtureTest.hex(
            "0b 10 01 45 76 61 6c 75 65 45 31 35 2f 34 33 03");
private static final byte[] MONTH_DAY_JANUARY_17 = VPackWireFixtureTest.hex(
            "47 2d 2d 30 31 2d 31 37");
private static final byte[] MONTH_DAY_AUGUST_21 = VPackWireFixtureTest.hex(
            "47 2d 2d 30 38 2d 32 31");
private static final byte[] MONTH_DAY_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 20 02 52 6a 61 76 61 2e 74 69 6d 65 2e 4d 6f 6e 74 68 44 61 79 "
          + "47 2d 2d 31 31 2d 30 35 03 16");
private static final byte[] MONTH_DAY_SHAPE_INT = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 47 2d 2d 30 33 2d 31 37 03");
private static final byte[] MONTH_DAY_SHAPE_INT_ARRAY = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 06 08 02 33 28 11 03 04 03");
private static final byte[] MONTH_DAY_FRENCH = VPackWireFixtureTest.hex(
            "0b 12 01 45 76 61 6c 75 65 47 6d 61 72 73 2d 31 37 03");
private static final byte[] MONTH_DAY_SHAPE_ARRAY = VPackWireFixtureTest.hex(
            "0b 10 01 45 76 61 6c 75 65 02 06 28 0c 28 1f 03");

    // Provenance: MonthDaySerTest#testSerialization01.
    void testMonthDaySerialization01Vpack() throws Exception {
        assertArrayEquals(MONTH_DAY_JANUARY_17,
                VPackMapper.builder().build().writeValueAsBytes(MonthDay.of(1, 17)));
    }

    // Provenance: MonthDaySerTest#testSerialization02.
    void testMonthDaySerialization02Vpack() throws Exception {
        assertArrayEquals(MONTH_DAY_AUGUST_21,
                VPackMapper.builder().build().writeValueAsBytes(MonthDay.of(8, 21)));
    }

    // Provenance: MonthDaySerTest#testSerializationWithFrLocale.
    void testMonthDaySerializationWithFrLocaleVpack() throws Exception {
        assertArrayEquals(MONTH_DAY_FRENCH,
                VPackMapper.builder().build().writeValueAsBytes(
                        new FrBean(MonthDay.of(3, 17))));
    }

    // Provenance: MonthDaySerTest#testSerializationWithShapeArray.
    void testMonthDaySerializationWithShapeArrayVpack() throws Exception {
        assertArrayEquals(MONTH_DAY_SHAPE_ARRAY,
                VPackMapper.builder().build().writeValueAsBytes(
                        new ShapeArrayBean(MonthDay.of(12, 31))));
    }

    // Provenance: MonthDaySerTest#testSerializationWithShapeInt.
    void testMonthDaySerializationWithShapeIntVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        assertArrayEquals(MONTH_DAY_SHAPE_INT_ARRAY,
                mapper.writeValueAsBytes(new ShapeIntWrapper(MonthDay.of(3, 17))));
        assertArrayEquals(MONTH_DAY_SHAPE_INT,
                mapper.writeValueAsBytes(new NoShapeIntWrapper(MonthDay.of(3, 17))));
    }

    // Provenance: MonthDaySerTest#testSerializationWithTypeInfo01.
    void testMonthDaySerializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(java.time.temporal.TemporalAccessor.class, TemporalTypeInfo.class)
                .build();
        assertArrayEquals(MONTH_DAY_TYPE_INFO,
                mapper.writeValueAsBytes(MonthDay.of(11, 5)));
    }
private static ObjectMapper timestampMapper(boolean nanos) {
        var builder = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (nanos) {
            builder.enable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        } else {
            builder.disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        }
        return builder.build();
    }
private static ObjectMapper typeInfoMapper(boolean timestamps, boolean nanos) {
        return VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, timestamps)
                .configure(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, nanos)
                .build();
    }
static class CustomLocalTimeSerializer extends LocalTimeSerializer {
        public CustomLocalTimeSerializer() {
            super(DateTimeFormatter.ofPattern("HH/mm"));
        }
    }
static class CustomWrapper {
        @JsonSerialize(using = CustomLocalTimeSerializer.class)
        public LocalTime value;

        CustomWrapper(LocalTime value) {
            this.value = value;
        }
    }
static class ShapeIntWrapper {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public MonthDay value;

        ShapeIntWrapper(MonthDay value) {
            this.value = value;
        }
    }
static class NoShapeIntWrapper {
        public MonthDay value;

        NoShapeIntWrapper(MonthDay value) {
            this.value = value;
        }
    }
static class FrBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM-dd", locale = "fr")
        public MonthDay value;

        FrBean(MonthDay value) {
            this.value = value;
        }
    }
static class ShapeArrayBean {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public MonthDay value;

        ShapeArrayBean(MonthDay value) {
            this.value = value;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testMonthDaySerialization01Vpack() throws Exception {
        try {
            testMonthDaySerialization01Vpack();
        } finally {
        }
    }


    void __invoke_testMonthDaySerialization02Vpack() throws Exception {
        try {
            testMonthDaySerialization02Vpack();
        } finally {
        }
    }


    void __invoke_testMonthDaySerializationWithFrLocaleVpack() throws Exception {
        try {
            testMonthDaySerializationWithFrLocaleVpack();
        } finally {
        }
    }


    void __invoke_testMonthDaySerializationWithShapeArrayVpack() throws Exception {
        try {
            testMonthDaySerializationWithShapeArrayVpack();
        } finally {
        }
    }


    void __invoke_testMonthDaySerializationWithShapeIntVpack() throws Exception {
        try {
            testMonthDaySerializationWithShapeIntVpack();
        } finally {
        }
    }


    void __invoke_testMonthDaySerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testMonthDaySerializationWithTypeInfo01Vpack();
        } finally {
        }
    }

}
