package tools.jackson.databind.ext.javatime.ser;

import java.time.Year;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.stream.Stream;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.YearDeserializer;
import tools.jackson.databind.ext.javatime.ser.YearSerializer;
import tools.jackson.databind.ext.javatime.ser.ZonedDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0356F0 {
private static final Year YEAR = Year.of(2024);
private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(
            2024, 6, 7, 12, 34, 56, 789_000_000, ZoneOffset.UTC);
private static final byte[] YEAR_YYYY = VPackWireFixtureTest.hex(
            "44 32 30 32 34");
private static final byte[] YEAR_YY = VPackWireFixtureTest.hex(
            "42 32 34");
private static final byte[] ZONED_ISO = VPackWireFixtureTest.hex(
            "5d 32 30 32 34 2d 30 36 2d 30 37 54 31 32 3a 33 34 3a 35 36 2e 37 38 39 5a "
          + "5b 55 54 43 5d");
private static final byte[] ZONED_OFFSET = VPackWireFixtureTest.hex(
            "58 32 30 32 34 2d 30 36 2d 30 37 54 31 32 3a 33 34 3a 35 36 2e 37 38 39 5a");
private static final byte[] ZONED_LOCAL = VPackWireFixtureTest.hex(
            "57 32 30 32 34 2d 30 36 2d 30 37 54 31 32 3a 33 34 3a 35 36 2e 37 38 39");
private static final byte[] ZONED_PATTERN = VPackWireFixtureTest.hex(
            "5c 32 30 32 34 2d 30 36 2d 30 37 54 31 32 3a 33 34 3a 35 36 2e 37 38 39 2b 30 30 30 30");
private static final byte[] ZONE_ID_UTC = VPackWireFixtureTest.hex(
            "43 55 54 43");
private static final byte[] ZONE_OFFSET_PLUS_TWO = VPackWireFixtureTest.hex(
            "46 2b 30 32 3a 30 30");
private static final byte[] ZONED_TIMESTAMP = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 01 74 63 81 66 20 00 00 00 00");
private static final byte[] ZONED_STRING = VPackWireFixtureTest.hex(
            "54 32 30 32 35 2d 30 35 2d 30 34 54 31 38 3a 30 31 3a 30 32 5a");

    
    // Provenance: TestYearSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testYearSerializationWithCustomFormatterVpack(FormatterCase formatter)
            throws Exception {
        assertArrayEquals(formatter.fixture(), formatter.serializationMapper()
                .writeValueAsBytes(YEAR));
    }

    
    // Provenance: TestYearSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testYearDeserializationWithCustomFormatterVpack(FormatterCase formatter)
            throws Exception {
        assertEquals(YEAR, formatter.deserializationMapper()
                .readValue(formatter.fixture(), Year.class));
    }
private static final ObjectMapper WITH_TIMESTAMP_MAPPER = VPackMapper.builder()
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
private static final ObjectMapper WITHOUT_TIMESTAMP_MAPPER = VPackMapper.builder()
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
private static Stream<FormatterCase> yearFormatters() {
        return Stream.of(
                new FormatterCase("yyyy", DateTimeFormatter.ofPattern("yyyy"), YEAR_YYYY,
                        Year.class),
                new FormatterCase("yy", DateTimeFormatter.ofPattern("yy"), YEAR_YY,
                        Year.class));
    }
private static Stream<FormatterCase> zonedDateTimeFormatters() {
        return Stream.of(
                new FormatterCase("ISO_ZONED_DATE_TIME", DateTimeFormatter.ISO_ZONED_DATE_TIME,
                        ZONED_ISO, ZonedDateTime.class),
                new FormatterCase("ISO_OFFSET_DATE_TIME", DateTimeFormatter.ISO_OFFSET_DATE_TIME,
                        ZONED_OFFSET, ZonedDateTime.class),
                new FormatterCase("ISO_LOCAL_DATE_TIME", DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                        ZONED_LOCAL, ZonedDateTime.class),
                new FormatterCase("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
                        ZONED_PATTERN, ZonedDateTime.class));
    }
private record FormatterCase(String name, DateTimeFormatter formatter, byte[] fixture,
            Class<?> valueType) {
        ObjectMapper serializationMapper() {
            SimpleModule module = new SimpleModule();
            if (valueType == Year.class) {
                module.addSerializer(new YearSerializer(formatter));
            } else {
                module.addSerializer(new ZonedDateTimeSerializer(formatter));
            }
            return VPackMapper.builder()
                    .defaultTimeZone(TimeZone.getTimeZone("UTC"))
                    .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .addModule(module)
                    .build();
        }

        ObjectMapper deserializationMapper() {
            SimpleModule module = new SimpleModule()
                    .addDeserializer(Year.class, new YearDeserializer(formatter));
            return VPackMapper.builder().addModule(module).build();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    void __invoke_testYearSerializationWithCustomFormatterVpack(Object formatter) throws Exception {
        try {
            testYearSerializationWithCustomFormatterVpack((FormatterCase) formatter);
        } finally {
        }
    }


    void __invoke_testYearDeserializationWithCustomFormatterVpack(Object formatter) throws Exception {
        try {
            testYearDeserializationWithCustomFormatterVpack((FormatterCase) formatter);
        } finally {
        }
    }

}
