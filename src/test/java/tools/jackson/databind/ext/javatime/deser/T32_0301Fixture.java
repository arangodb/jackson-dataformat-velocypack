package tools.jackson.databind.ext.javatime.deser;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0301Fixture {
private static final VPackMapper MAPPER = new VPackMapper();
private static final VPackMapper MAPPER_TRUNCATE_WRITE = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_READ = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();
private static final VPackMapper MAPPER_TRUNCATE_BOTH = VPackMapper.builder()
            .enable(DateTimeFeature.TRUNCATE_TO_MSECS_ON_WRITE,
                    DateTimeFeature.TRUNCATE_TO_MSECS_ON_READ)
            .build();
private static final byte[] INSTANT_FULL = VPackWireFixtureTest.hex(
            "5e 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 34 35 36 37 38 39 5a");
private static final byte[] INSTANT_MILLIS = VPackWireFixtureTest.hex(
            "58 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 5a");
private static final byte[] INSTANT_ZERO_NANOS = VPackWireFixtureTest.hex(
            "54 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 5a");
private static final byte[] LOCAL_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "57 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33");
private static final byte[] OFFSET_DATE_TIME_FULL = VPackWireFixtureTest.hex(
            "63 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 34 35 36 37 38 39 2b 30 31 3a 30 30");
private static final byte[] OFFSET_DATE_TIME_MILLIS = VPackWireFixtureTest.hex(
            "5d 32 30 32 33 2d 30 31 2d 31 35 54 31 30 3a 33 30 3a 34 35 "
          + "2e 31 32 33 2b 30 31 3a 30 30");
private static final byte[] LOCAL_DATE_INVALID_DATE = VPackWireFixtureTest.hex(
            "06 0c 03 29 e7 07 32 28 1e 03 06 07");
private static final byte[] LOCAL_DATE_INVALID_MONTH = VPackWireFixtureTest.hex(
            "06 0c 03 29 e7 07 30 28 0f 03 06 07");
private static final byte[] LOCAL_DATE_TIME_INVALID_DATE = VPackWireFixtureTest.hex(
            "06 12 05 29 e7 07 32 28 1e 28 0c 28 1e 03 06 07 09 0b");
private static final byte[] LOCAL_DATE_INVALID_DATE_STRING = VPackWireFixtureTest.hex(
            "4a 32 30 32 35 2d 30 32 2d 33 30");
private static final byte[] EXTREME_DECIMAL = VPackWireFixtureTest.hex(
            "c8 01 ff ff ff 7f 07");

    // Provenance: DateTimeExceptionHandlingTest#testDurationFromLargeDecimal.
    void testDurationFromLargeDecimalVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(java.time.Duration.class)
                        .readValue(EXTREME_DECIMAL));
        assertInstanceOf(ArithmeticException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testInstantFromLargeDecimal.
    void testInstantFromLargeDecimalVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(Instant.class).readValue(EXTREME_DECIMAL));
        assertInstanceOf(ArithmeticException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateInvalidDate.
    void testLocalDateInvalidDateVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalDate.class).readValue(LOCAL_DATE_INVALID_DATE));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateInvalidDateString.
    void testLocalDateInvalidDateStringVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> MAPPER.readerFor(LocalDate.class)
                        .readValue(LOCAL_DATE_INVALID_DATE_STRING));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateInvalidMonth.
    void testLocalDateInvalidMonthVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalDate.class).readValue(LOCAL_DATE_INVALID_MONTH));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateTimeInvalidDate.
    void testLocalDateTimeInvalidDateVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalDateTime.class)
                        .readValue(LOCAL_DATE_TIME_INVALID_DATE));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    void __invoke_testDurationFromLargeDecimalVpack() throws Exception {
        try {
            testDurationFromLargeDecimalVpack();
        } finally {
        }
    }


    void __invoke_testInstantFromLargeDecimalVpack() throws Exception {
        try {
            testInstantFromLargeDecimalVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateInvalidDateVpack() throws Exception {
        try {
            testLocalDateInvalidDateVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateInvalidDateStringVpack() throws Exception {
        try {
            testLocalDateInvalidDateStringVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateInvalidMonthVpack() throws Exception {
        try {
            testLocalDateInvalidMonthVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeInvalidDateVpack() throws Exception {
        try {
            testLocalDateTimeInvalidDateVpack();
        } finally {
        }
    }

}
