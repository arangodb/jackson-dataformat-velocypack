package tools.jackson.databind.deser.jdk;

import java.beans.ConstructorProperties;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0260Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] RFC_1123 = VPackWireFixtureTest.hex(
            "5f 53 61 74 2c 20 31 37 20 4a 61 6e 20 32 30 30 39 20 30 36 3a 31 33 3a 35 38 20 2b 30 30 30 30");
private static final byte[] NO_TIMEZONE = VPackWireFixtureTest.hex(
            "57 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30");
private static final byte[] EXPLICIT_NEGATIVE_TIMEZONE = VPackWireFixtureTest.hex(
            "5d 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 2d 30 32 3a 30 30");
private static final byte[] PARTIAL_MILLIS_6 = VPackWireFixtureTest.hex(
            "5b 32 30 31 34 2d 31 30 2d 30 33 54 31 38 3a 30 30 3a 30 30 2e 36 2d 30 35 3a 30 30");
private static final byte[] PARTIAL_MILLIS_61 = VPackWireFixtureTest.hex(
            "5c 32 30 31 34 2d 31 30 2d 30 33 54 31 38 3a 30 30 3a 30 30 2e 36 31 2d 30 35 3a 30 30");
private static final byte[] PARTIAL_MILLIS_PLUS_COLON = VPackWireFixtureTest.hex(
            "5c 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 3a 33 30 2e 34 35 2b 30 31 3a 30 30");
private static final byte[] PARTIAL_MILLIS_PLUS_COMPACT = VPackWireFixtureTest.hex(
            "5b 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 3a 33 30 2e 34 35 2b 30 31 30 30");
private static final byte[] PARTIAL_MILLIS_PLUS_HOUR = VPackWireFixtureTest.hex(
            "59 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 3a 33 30 2e 34 35 2b 30 31");
private static final byte[] LONG_FRACTION = VPackWireFixtureTest.hex(
            "5e 32 30 31 34 2d 31 30 2d 30 33 54 31 38 3a 30 30 3a 30 30 2e 33 34 35 36 2d 30 35 3a 30 30");
private static final byte[] TOO_LONG_FRACTION = VPackWireFixtureTest.hex(
            "64 32 30 31 34 2d 31 30 2d 30 33 54 31 38 3a 30 30 3a 30 30 2e 31 32 33 34 35 36 37 38 39 30 2d 30 35 3a 30 30");
private static final byte[] FRACTIONAL_TIMEZONE = VPackWireFixtureTest.hex(
            "5c 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 3a 33 30 2e 34 35 2b 30 31 3a 33 30");
private static final byte[] MISSING_SECONDS_COLON = VPackWireFixtureTest.hex(
            "56 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 2b 30 31 3a 30 30");
private static final byte[] MISSING_SECONDS_COMPACT = VPackWireFixtureTest.hex(
            "55 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 2b 30 32 30 30");
private static final byte[] MISSING_SECONDS_HOUR = VPackWireFixtureTest.hex(
            "53 31 39 39 37 2d 30 37 2d 31 36 54 31 39 3a 32 30 2b 30 34");
private static final byte[] INVALID_FORMAT = VPackWireFixtureTest.hex(
            "46 66 6f 6f 62 61 72");
private static final byte[] FORMAT_AND_CTORS = VPackWireFixtureTest.hex(
            "14 20 44 64 61 74 65 57 31 39 37 30 2d 30 31 2d 30 31 20 30 30 3a 30 30 3a 30 30 2e 30 30 30 01");
private static final byte[] LENIENT_DATE = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 32 30 31 35 2d 31 31 2d 33 32 01");

    // Provenance: DateDeserializationTest#testDateUtilISO8601NoTimezoneNonDefault.
    void testDateUtilISO8601NoTimezoneNonDefault() throws Exception {
        ObjectReader reader = MAPPER.readerFor(Date.class)
                .with(TimeZone.getTimeZone("GMT-2"));
        Date withoutTimezone = reader.readValue(NO_TIMEZONE);

        Date withExplicitTimezone = MAPPER.readerFor(Date.class)
                .with(TimeZone.getTimeZone("GMT+5"))
                .readValue(EXPLICIT_NEGATIVE_TIMEZONE);

        assertEquals(withoutTimezone, withExplicitTimezone);
        assertEquals(2L * 60L * 60L * 1000L, withoutTimezone.getTime());
    }

    // Provenance: DateDeserializationTest#testDateUtilRFC1123.
    void testDateUtilRFC1123() throws Exception {
        DateFormat format = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        Date expected = format.parse("Sat, 17 Jan 2009 06:13:58 +0000");
        assertEquals(expected, MAPPER.readValue(RFC_1123, Date.class));
    }

    // Provenance: DateDeserializationTest#testDateUtilRFC1123OnNonUSLocales.
    void testDateUtilRFC1123OnNonUSLocales() throws Exception {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.GERMAN);
            DateFormat format = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            Date expected = format.parse("Sat, 17 Jan 2009 06:13:58 +0000");
            assertEquals(expected, MAPPER.readValue(RFC_1123, Date.class));
        } finally {
            Locale.setDefault(previous);
        }
    }

    // Provenance: DateDeserializationTest#testFormatAndCtors1722.
    void testFormatAndCtors1722() throws Exception {
        Date1722 result = MAPPER.readValue(FORMAT_AND_CTORS, Date1722.class);
        assertNotNull(result);
        assertEquals(0L, result.getDate().getTime());
    }

    // Provenance: DateDeserializationTest#testISO8601PartialMilliseconds.
    void testISO8601PartialMilliseconds() throws Exception {
        assertEquals(utcDate(2014, Calendar.OCTOBER, 3, 23, 0, 0, 600),
                MAPPER.readValue(PARTIAL_MILLIS_6, Date.class));
        assertEquals(utcDate(2014, Calendar.OCTOBER, 3, 23, 0, 0, 610),
                MAPPER.readValue(PARTIAL_MILLIS_61, Date.class));

        Date expected = utcDate(1997, Calendar.JULY, 16, 18, 20, 30, 450);
        assertEquals(expected, MAPPER.readValue(PARTIAL_MILLIS_PLUS_COLON, Date.class));
        assertEquals(expected, MAPPER.readValue(PARTIAL_MILLIS_PLUS_COMPACT, Date.class));
        assertEquals(expected, MAPPER.readValue(PARTIAL_MILLIS_PLUS_HOUR, Date.class));
    }

    // Provenance: DateDeserializationTest#testISO8601FractionalTimezoneOffset.
    void testISO8601FractionalTimezoneOffset() throws Exception {
        assertEquals(utcDate(1997, Calendar.JULY, 16, 17, 50, 30, 450),
                MAPPER.readValue(FRACTIONAL_TIMEZONE, Date.class));
    }

    // Provenance: DateDeserializationTest#testISO8601FractSecondsLong.
    void testISO8601FractSecondsLong() throws Exception {
        assertEquals(utcDate(2014, Calendar.OCTOBER, 3, 23, 0, 0, 345),
                MAPPER.readValue(LONG_FRACTION, Date.class));

        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(TOO_LONG_FRACTION, Date.class));
        assertEquals(Date.class, failure.getTargetType());
        assertEquals("2014-10-03T18:00:00.1234567890-05:00", failure.getValue());
        assertEquals(true, failure.getMessage().contains("invalid fractional seconds"));
        assertEquals(true, failure.getMessage().contains("can use at most 9 digits"));
    }

    // Provenance: DateDeserializationTest#testISO8601MissingSeconds.
    void testISO8601MissingSeconds() throws Exception {
        assertEquals(utcDate(1997, Calendar.JULY, 16, 18, 20, 0, 0),
                MAPPER.readValue(MISSING_SECONDS_COLON, Date.class));
        assertEquals(utcDate(1997, Calendar.JULY, 16, 17, 20, 0, 0),
                MAPPER.readValue(MISSING_SECONDS_COMPACT, Date.class));
        assertEquals(utcDate(1997, Calendar.JULY, 16, 15, 20, 0, 0),
                MAPPER.readValue(MISSING_SECONDS_HOUR, Date.class));
    }

    // Provenance: DateDeserializationTest#testInvalidFormat.
    void testInvalidFormat() {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(INVALID_FORMAT, Date.class));
        assertEquals("foobar", failure.getValue());
        assertEquals(Date.class, failure.getTargetType());
        assertEquals(true, failure.getMessage().contains("java.util.Date"));
    }

    // Provenance: DateDeserializationTest#testLenientJDKDateTypes.
    void testLenientJDKDateTypes() throws Exception {
        LenientCalendarBean lenient = MAPPER.readValue(LENIENT_DATE,
                LenientCalendarBean.class);
        assertEquals(Calendar.DECEMBER, lenient.value.get(Calendar.MONTH));
        assertEquals(2, lenient.value.get(Calendar.DAY_OF_MONTH));

        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(LENIENT_DATE, StrictCalendarBean.class));

        ObjectMapper strict = VPackMapper.builder()
                .withConfigOverride(Date.class,
                        cfg -> cfg.setFormat(JsonFormat.Value.forLeniency(Boolean.FALSE)))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> strict.readValue(VPackWireFixtureTest.hex(
                        "4a 32 30 31 35 2d 31 31 2d 33 32"), Date.class));
    }
private static Date utcDate(int year, int month, int day, int hour, int minute,
            int second, int millis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ROOT);
        calendar.clear();
        calendar.setLenient(false);
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millis);
        return calendar.getTime();
    }
static class LenientCalendarBean {
        @JsonFormat(lenient = OptBoolean.TRUE)
        public Calendar value;
    }
static class StrictCalendarBean {
        @JsonFormat(lenient = OptBoolean.FALSE)
        public Calendar value;
    }
public static class Date1722 {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private Date date;

        @JsonIgnore
        private String foo;

        @ConstructorProperties({ "date", "foo" })
        public Date1722(Date date, String foo) {
            this.date = date;
            this.foo = foo;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    void __invoke_testDateUtilISO8601NoTimezoneNonDefault() throws Exception {
        try {
            testDateUtilISO8601NoTimezoneNonDefault();
        } finally {
        }
    }


    void __invoke_testDateUtilRFC1123() throws Exception {
        try {
            testDateUtilRFC1123();
        } finally {
        }
    }


    void __invoke_testDateUtilRFC1123OnNonUSLocales() throws Exception {
        try {
            testDateUtilRFC1123OnNonUSLocales();
        } finally {
        }
    }


    void __invoke_testFormatAndCtors1722() throws Exception {
        try {
            testFormatAndCtors1722();
        } finally {
        }
    }


    void __invoke_testISO8601PartialMilliseconds() throws Exception {
        try {
            testISO8601PartialMilliseconds();
        } finally {
        }
    }


    void __invoke_testISO8601FractionalTimezoneOffset() throws Exception {
        try {
            testISO8601FractionalTimezoneOffset();
        } finally {
        }
    }


    void __invoke_testISO8601FractSecondsLong() throws Exception {
        try {
            testISO8601FractSecondsLong();
        } finally {
        }
    }


    void __invoke_testISO8601MissingSeconds() throws Exception {
        try {
            testISO8601MissingSeconds();
        } finally {
        }
    }


    void __invoke_testInvalidFormat() throws Exception {
        try {
            testInvalidFormat();
        } finally {
        }
    }


    void __invoke_testLenientJDKDateTypes() throws Exception {
        try {
            testLenientJDKDateTypes();
        } finally {
        }
    }

}
