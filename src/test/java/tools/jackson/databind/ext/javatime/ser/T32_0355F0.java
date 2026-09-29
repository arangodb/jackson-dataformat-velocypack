package tools.jackson.databind.ext.javatime.ser;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.YearMonthDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.YearMonthSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0355F0 {
private static final byte[] LOCAL_DATE_TIME_ISO = VPackWireFixtureTest.hex(
            "57 32 30 32 34 2d 30 36 2d 30 37 54 31 32 3a 33 34 3a 35 36 2e 37 38 39");
private static final byte[] LOCAL_TIME_ISO = VPackWireFixtureTest.hex(
            "4c 31 32 3a 33 34 3a 35 36 2e 37 38 39");
private static final byte[] YEAR_MONTH_PADDED = VPackWireFixtureTest.hex(
            "47 32 30 32 34 2d 30 36");
private static final byte[] YEAR_MONTH_REDUCED = VPackWireFixtureTest.hex(
            "44 32 34 2d 36");
private static final LocalDateTime LOCAL_DATE_TIME =
            LocalDateTime.of(2024, 6, 7, 12, 34, 56, 789_000_000);
private static final LocalTime LOCAL_TIME =
            LocalTime.of(12, 34, 56, 789_000_000);
private static final YearMonth YEAR_MONTH = YearMonth.of(2024, 6);

    
    // Provenance: TestLocalDateTimeSerializationWithCustomFormatter#testSerialization(DateTimeFormatter).
    void testLocalDateTimeSerializationWithCustomFormatterVpack(FormatterCase formatter)
            throws Exception {
        assertArrayEquals(formatter.fixture(), formatter.serializationMapper()
                .writeValueAsBytes(LOCAL_DATE_TIME));
    }

    
    // Provenance: TestLocalDateTimeSerializationWithCustomFormatter#testDeserialization(DateTimeFormatter).
    void testLocalDateTimeDeserializationWithCustomFormatterVpack(FormatterCase formatter)
            throws Exception {
        assertEquals(LOCAL_DATE_TIME, formatter.deserializationMapper()
                .readValue(formatter.fixture(), LocalDateTime.class));
    }
private static Stream<FormatterCase> localDateTimeFormatters() {
        return Stream.of(
                new FormatterCase("ISO_DATE_TIME", DateTimeFormatter.ISO_DATE_TIME,
                        LOCAL_DATE_TIME_ISO, LocalDateTime.class),
                new FormatterCase("ISO_LOCAL_DATE_TIME", DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                        LOCAL_DATE_TIME_ISO, LocalDateTime.class));
    }
private static Stream<FormatterCase> localTimeFormatters() {
        return Stream.of(
                new FormatterCase("ISO_LOCAL_TIME", DateTimeFormatter.ISO_LOCAL_TIME,
                        LOCAL_TIME_ISO, LocalTime.class),
                new FormatterCase("ISO_TIME", DateTimeFormatter.ISO_TIME,
                        LOCAL_TIME_ISO, LocalTime.class));
    }
private static Stream<FormatterCase> yearMonthFormatters() {
        return Stream.of(
                new FormatterCase("uuuu-MM", DateTimeFormatter.ofPattern("uuuu-MM"),
                        YEAR_MONTH_PADDED, YearMonth.class),
                new FormatterCase("uu-M", DateTimeFormatter.ofPattern("uu-M"),
                        YEAR_MONTH_REDUCED, YearMonth.class));
    }
private record FormatterCase(String name, DateTimeFormatter formatter, byte[] fixture,
            Class<?> valueType) {
        ObjectMapper serializationMapper() {
            SimpleModule module = new SimpleModule();
            if (valueType == LocalDateTime.class) {
                module.addSerializer(new LocalDateTimeSerializer(formatter));
            } else if (valueType == LocalTime.class) {
                module.addSerializer(new LocalTimeSerializer(formatter));
            } else {
                module.addSerializer(new YearMonthSerializer(formatter));
            }
            return VPackMapper.builder().addModule(module).build();
        }

        ObjectMapper deserializationMapper() {
            SimpleModule module = new SimpleModule();
            if (valueType == LocalDateTime.class) {
                module.addDeserializer(LocalDateTime.class,
                        new LocalDateTimeDeserializer(formatter));
            } else if (valueType == LocalTime.class) {
                module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(formatter));
            } else {
                module.addDeserializer(YearMonth.class, new YearMonthDeserializer(formatter));
            }
            return VPackMapper.builder().addModule(module).build();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    void __invoke_testLocalDateTimeSerializationWithCustomFormatterVpack(Object formatter) throws Exception {
        try {
            testLocalDateTimeSerializationWithCustomFormatterVpack((FormatterCase) formatter);
        } finally {
        }
    }


    void __invoke_testLocalDateTimeDeserializationWithCustomFormatterVpack(Object formatter) throws Exception {
        try {
            testLocalDateTimeDeserializationWithCustomFormatterVpack((FormatterCase) formatter);
        } finally {
        }
    }

}
