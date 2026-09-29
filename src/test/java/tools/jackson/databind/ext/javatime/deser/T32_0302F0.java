package tools.jackson.databind.ext.javatime.deser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0302F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] LOCAL_DATE_TIME_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 0e 29 e7 07 28 0d 28 0f 28 0c 28 1e 05");
private static final byte[] LOCAL_DATE_TIME_INVALID_TIME = VPackWireFixtureTest.hex(
            "13 0e 29 e7 07 28 02 28 0f 28 19 28 1e 05");
private static final byte[] LOCAL_DATE_TIME_INVALID_DATE_STRING = VPackWireFixtureTest.hex(
            "53 32 30 32 35 2d 30 32 2d 33 30 54 31 32 3a 30 30 3a 30 30");
private static final byte[] LOCAL_TIME_INVALID_HOUR = VPackWireFixtureTest.hex(
            "13 07 28 19 28 1e 02");
private static final byte[] LOCAL_TIME_INVALID_MINUTE = VPackWireFixtureTest.hex(
            "13 07 28 0c 28 3c 02");
private static final byte[] LOCAL_TIME_INVALID_MINUTE_STRING = VPackWireFixtureTest.hex(
            "48 31 32 3a 36 39 3a 30 30");
private static final byte[] MONTH_DAY_INVALID_DATE = VPackWireFixtureTest.hex(
            "13 07 28 02 28 1e 02");
private static final byte[] MONTH_DAY_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 07 28 0d 28 0f 02");
private static final byte[] YEAR_MONTH_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 07 29 e7 07 30 02");
private static final byte[] YEAR_MONTH_INVALID_MONTH_13 = VPackWireFixtureTest.hex(
            "13 08 29 e7 07 28 0d 02");
private static final byte[] YEAR_OUT_OF_RANGE = VPackWireFixtureTest.hex(
            "2b 00 ca 9a 3b");
private static final ObjectMapper TYPING_MAPPER = VPackMapper.builder()
            .activateDefaultTyping(new NoCheckSubTypeValidator())
            .build();

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateTimeInvalidDateString.
    void testLocalDateTimeInvalidDateStringVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> MAPPER.readerFor(LocalDateTime.class)
                        .readValue(LOCAL_DATE_TIME_INVALID_DATE_STRING));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateTimeInvalidMonth.
    void testLocalDateTimeInvalidMonthVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalDateTime.class)
                        .readValue(LOCAL_DATE_TIME_INVALID_MONTH));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalDateTimeInvalidTime.
    void testLocalDateTimeInvalidTimeVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalDateTime.class)
                        .readValue(LOCAL_DATE_TIME_INVALID_TIME));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalTimeInvalidHour.
    void testLocalTimeInvalidHourVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalTime.class).readValue(LOCAL_TIME_INVALID_HOUR));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalTimeInvalidMinute.
    void testLocalTimeInvalidMinuteVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(LocalTime.class)
                        .readValue(LOCAL_TIME_INVALID_MINUTE));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testLocalTimeInvalidMinuteString.
    void testLocalTimeInvalidMinuteStringVpack() {
        DatabindException failure = assertThrows(DatabindException.class,
                () -> MAPPER.readerFor(LocalTime.class)
                        .readValue(LOCAL_TIME_INVALID_MINUTE_STRING));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testMonthDayInvalidDate.
    void testMonthDayInvalidDateVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(MonthDay.class).readValue(MONTH_DAY_INVALID_DATE));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testMonthDayInvalidMonth.
    void testMonthDayInvalidMonthVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(MonthDay.class).readValue(MONTH_DAY_INVALID_MONTH));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testYearMonthInvalidMonth.
    void testYearMonthInvalidMonthVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(YearMonth.class)
                        .readValue(YEAR_MONTH_INVALID_MONTH));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }

    // Provenance: DateTimeExceptionHandlingTest#testYearMonthInvalidMonth13.
    void testYearMonthInvalidMonth13Vpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(YearMonth.class)
                        .readValue(YEAR_MONTH_INVALID_MONTH_13));
        assertInstanceOf(DateTimeException.class, failure.getCause());
    }

    // Provenance: DateTimeExceptionHandlingTest#testYearOutOfRange.
    void testYearOutOfRangeVpack() {
        DateTimeParseException failure = assertThrows(DateTimeParseException.class,
                () -> MAPPER.readerFor(Year.class).readValue(YEAR_OUT_OF_RANGE));
        assertInstanceOf(DateTimeException.class, failure.getCause());
        assertTrue(failure.getMessage().contains("Failed to deserialize"),
                failure.getMessage());
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testLocalDateTimeInvalidDateStringVpack() throws Exception {
        try {
            testLocalDateTimeInvalidDateStringVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeInvalidMonthVpack() throws Exception {
        try {
            testLocalDateTimeInvalidMonthVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateTimeInvalidTimeVpack() throws Exception {
        try {
            testLocalDateTimeInvalidTimeVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeInvalidHourVpack() throws Exception {
        try {
            testLocalTimeInvalidHourVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeInvalidMinuteVpack() throws Exception {
        try {
            testLocalTimeInvalidMinuteVpack();
        } finally {
        }
    }


    void __invoke_testLocalTimeInvalidMinuteStringVpack() throws Exception {
        try {
            testLocalTimeInvalidMinuteStringVpack();
        } finally {
        }
    }


    void __invoke_testMonthDayInvalidDateVpack() throws Exception {
        try {
            testMonthDayInvalidDateVpack();
        } finally {
        }
    }


    void __invoke_testMonthDayInvalidMonthVpack() throws Exception {
        try {
            testMonthDayInvalidMonthVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthInvalidMonthVpack() throws Exception {
        try {
            testYearMonthInvalidMonthVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthInvalidMonth13Vpack() throws Exception {
        try {
            testYearMonthInvalidMonth13Vpack();
        } finally {
        }
    }


    void __invoke_testYearOutOfRangeVpack() throws Exception {
        try {
            testYearOutOfRangeVpack();
        } finally {
        }
    }

}
