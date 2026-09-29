package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0354F0 {
private static final byte[] OFFSET_TIME_TIMESTAMP_04_NANOS = VPackWireFixtureTest.hex(
            "06 18 05 28 16 28 1f 35 2a 8d a9 0c 46 2b 31 31 3a 30 30 "
          + "03 05 07 08 0c");
private static final byte[] OFFSET_TIME_TYPE_INFO_01 = VPackWireFixtureTest.hex(
            "06 32 02 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "06 18 05 28 16 28 1f 35 2a 8d a9 0c 46 2b 31 31 3a 30 30 03 05 07 08 0c "
          + "03 18");
private static final byte[] OFFSET_TIME_TYPE_INFO_02 = VPackWireFixtureTest.hex(
            "06 31 02 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "06 17 05 28 16 28 1f 35 29 a6 01 46 2b 31 31 3a 30 30 03 05 07 08 0b "
          + "03 18");
private static final byte[] OFFSET_TIME_TYPE_INFO_03 = VPackWireFixtureTest.hex(
            "06 33 02 54 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 54 69 6d 65 "
          + "58 32 32 3a 33 31 3a 30 35 2e 30 30 30 38 32 39 38 33 37 2b 31 31 3a 30 30 "
          + "03 18");
private static final byte[] PERIOD_01 = VPackWireFixtureTest.hex(
            "48 50 31 59 36 4d 31 35 44");
private static final byte[] PERIOD_02 = VPackWireFixtureTest.hex(
            "44 50 32 31 44");
private static final byte[] PERIOD_TYPE_INFO = VPackWireFixtureTest.hex(
            "06 1f 02 50 6a 61 76 61 2e 74 69 6d 65 2e 50 65 72 69 6f 64 "
          + "48 50 35 59 31 4d 31 32 44 03 14");
private static final LocalDate FORMAT_DATE = LocalDate.of(2024, 6, 7);

    // Provenance: OffsetTimeSerTest#testSerializationAsTimestamp04Nanoseconds.
    void testOffsetTimeSerializationAsTimestamp04NanosecondsVpack() throws Exception {
        assertArrayEquals(OFFSET_TIME_TIMESTAMP_04_NANOS,
                timestampMapper(true).writeValueAsBytes(
                        OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"))));
    }

    // Provenance: OffsetTimeSerTest#testSerializationWithTypeInfo01.
    void testOffsetTimeSerializationWithTypeInfo01Vpack() throws Exception {
        assertArrayEquals(OFFSET_TIME_TYPE_INFO_01,
                typeInfoMapper(true, true).writeValueAsBytes(
                        OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"))));
    }

    // Provenance: OffsetTimeSerTest#testSerializationWithTypeInfo02.
    void testOffsetTimeSerializationWithTypeInfo02Vpack() throws Exception {
        assertArrayEquals(OFFSET_TIME_TYPE_INFO_02,
                typeInfoMapper(true, false).writeValueAsBytes(
                        OffsetTime.of(22, 31, 5, 422829837, ZoneOffset.of("+1100"))));
    }

    // Provenance: OffsetTimeSerTest#testSerializationWithTypeInfo03.
    void testOffsetTimeSerializationWithTypeInfo03Vpack() throws Exception {
        assertArrayEquals(OFFSET_TIME_TYPE_INFO_03,
                typeInfoMapper(false, true).writeValueAsBytes(
                        OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"))));
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
        var builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, timestamps)
                .configure(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, nanos);
        return builder.build();
    }
private static Stream<FormatterCase> customFormatters() {
        return Stream.of(
                new FormatterCase("BASIC_ISO_DATE", DateTimeFormatter.BASIC_ISO_DATE,
                        VPackWireFixtureTest.hex("48 32 30 32 34 30 36 30 37")),
                new FormatterCase("ISO_DATE", DateTimeFormatter.ISO_DATE,
                        VPackWireFixtureTest.hex(
                                "4a 32 30 32 34 2d 30 36 2d 30 37")),
                new FormatterCase("ISO_LOCAL_DATE", DateTimeFormatter.ISO_LOCAL_DATE,
                        VPackWireFixtureTest.hex(
                                "4a 32 30 32 34 2d 30 36 2d 30 37")),
                new FormatterCase("ISO_ORDINAL_DATE", DateTimeFormatter.ISO_ORDINAL_DATE,
                        VPackWireFixtureTest.hex("48 32 30 32 34 2d 31 35 39")),
                new FormatterCase("ISO_WEEK_DATE", DateTimeFormatter.ISO_WEEK_DATE,
                        VPackWireFixtureTest.hex(
                                "4a 32 30 32 34 2d 57 32 33 2d 35")),
                new FormatterCase("MM/dd/yyyy", DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                        VPackWireFixtureTest.hex(
                                "4a 30 36 2f 30 37 2f 32 30 32 34")));
    }
private record FormatterCase(String name, DateTimeFormatter formatter, byte[] fixture) {
        ObjectMapper serializationMapper() {
            return VPackMapper.builder()
                    .addModule(new SimpleModule()
                            .addSerializer(new LocalDateSerializer(formatter)))
                    .build();
        }

        ObjectMapper deserializationMapper() {
            return VPackMapper.builder()
                    .addModule(new SimpleModule()
                            .addDeserializer(LocalDate.class,
                                    new LocalDateDeserializer(formatter)))
                    .build();
        }

        @Override
        public String toString() {
            return name;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalAmountTypeInfo { }

    void __invoke_testOffsetTimeSerializationAsTimestamp04NanosecondsVpack() throws Exception {
        try {
            testOffsetTimeSerializationAsTimestamp04NanosecondsVpack();
        } finally {
        }
    }


    void __invoke_testOffsetTimeSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testOffsetTimeSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testOffsetTimeSerializationWithTypeInfo02Vpack() throws Exception {
        try {
            testOffsetTimeSerializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testOffsetTimeSerializationWithTypeInfo03Vpack() throws Exception {
        try {
            testOffsetTimeSerializationWithTypeInfo03Vpack();
        } finally {
        }
    }

}
