package tools.jackson.databind.ser.jdk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.util.StdDateFormat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0590F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JavaUtilDateSerializationTest#testDateNumeric().
    void testDateNumericVpack() throws Exception {
        assertFalse(MAPPER.isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertArrayEquals(VPackWireFixtureTest.hex("28 c7"),
                MAPPER.writer().with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .writeValueAsBytes(new Date(199L)));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601().
    void testDateISO8601Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        byte[] expected = VPackWireFixtureTest.hex(
                "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a");
        assertArrayEquals(expected, mapper.writeValueAsBytes(
                date(1970, 1, 1, 2, 0, 0, 0, "GMT+2")));
        assertArrayEquals(expected, mapper.writeValueAsBytes(
                date(1970, 1, 1, 0, 0, 0, 0, "UTC")));

        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 30 39 31 31 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), mapper.writeValueAsBytes(
                date(911, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 30 30 38 37 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), mapper.writeValueAsBytes(
                date(87, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 30 30 30 31 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), mapper.writeValueAsBytes(
                date(1, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601_10k().
    void testDateISO8601_10kVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "5a 2b 31 30 32 30 34 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(10204, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "5b 2b 31 32 33 34 35 36 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(123456, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601_BCE().
    void testDateISO8601_BCEVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "59 2b 30 30 30 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(0, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "59 2d 30 30 30 31 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(-1, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "59 2d 30 30 34 39 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(-49, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "59 2d 30 32 36 34 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), writer.writeValueAsBytes(
                date(-264, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601_customTZ().
    void testDateISO8601_customTZVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone("GMT+2"))
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        byte[] local = VPackWireFixtureTest.hex(
                "5d 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 32 3a 30 30");
        byte[] shifted = VPackWireFixtureTest.hex(
                "5d 31 39 37 30 2d 30 31 2d 30 31 54 30 32 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 32 3a 30 30");
        assertArrayEquals(local, mapper.writeValueAsBytes(
                date(1970, 1, 1, 0, 0, 0, 0, "GMT+2")));
        assertArrayEquals(shifted, mapper.writeValueAsBytes(
                date(1970, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601_colonInTZ().
    void testDateISO8601_colonInTZVpack() throws Exception {
        StdDateFormat dateFormat = new StdDateFormat();
        assertTrue(dateFormat.isColonIncludedInTimeZone());
        dateFormat = dateFormat.withColonInTimeZone(false);
        assertFalse(dateFormat.isColonIncludedInTimeZone());
        ObjectMapper mapper = VPackMapper.builder().defaultDateFormat(dateFormat)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), mapper.writeValueAsBytes(
                date(1970, 1, 1, 2, 0, 0, 0, "GMT+2")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), mapper.writeValueAsBytes(
                date(1970, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateOther().
    void testDateOtherVpack() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss");
        ObjectMapper mapper = VPackMapper.builder().defaultDateFormat(format)
                .defaultTimeZone(TimeZone.getTimeZone("PST")).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "53 31 39 36 39 2d 31 32 2d 33 31 58 31 36 3a 30 30 3a 30 30"),
                mapper.writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "UTC")));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateUsingObjectWriter().
    void testDateUsingObjectWriterVpack() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss");
        TimeZone timezone = TimeZone.getTimeZone("PST");
        byte[] formatted = VPackWireFixtureTest.hex(
                "53 31 39 36 39 2d 31 32 2d 33 31 58 31 36 3a 30 30 3a 30 30");
        assertArrayEquals(formatted, MAPPER.writer(format).with(timezone)
                .writeValueAsBytes(new Date(0L)));
        ObjectWriter writer = MAPPER.writer((DateFormat) null);
        assertArrayEquals(VPackWireFixtureTest.hex("30"),
                writer.writeValueAsBytes(new Date(0L)));
        writer = writer.with(format).with(timezone);
        assertArrayEquals(formatted, writer.writeValueAsBytes(new Date(0L)));
        writer = writer.with((DateFormat) null);
        assertArrayEquals(VPackWireFixtureTest.hex("30"),
                writer.writeValueAsBytes(new Date(0L)));
    }

    // Provenance: JavaUtilDateSerializationTest#testDateISO8601_zeroOffsetAsNumeric().
    void testDateISO8601_zeroOffsetAsNumericVpack() throws Exception {
        byte[] utc = VPackWireFixtureTest.hex(
                "5d 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 30 3a 30 30");
        byte[] plusTwo = VPackWireFixtureTest.hex(
                "5d 31 39 37 30 2d 30 31 2d 30 31 54 30 32 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 32 3a 30 30");
        byte[] compactUtc = VPackWireFixtureTest.hex(
                "5c 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 30 30 30");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"),
                MAPPER.writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(utc, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "UTC")));

        StdDateFormat format = new StdDateFormat()
                .withColonInTimeZone(false).withZeroOffsetAsZ(false);
        assertArrayEquals(compactUtc, VPackMapper.builder().defaultDateFormat(format)
                .build().writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "UTC")));
        assertArrayEquals(plusTwo, VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone("GMT+2"))
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(new Date(0L)));

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(0L);
        assertArrayEquals(utc, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(calendar));
        assertArrayEquals(utc, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "GMT")));

        byte[] map = VPackWireFixtureTest.hex(
                "0b 28 01 5d 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 30 3a 30 30 45 65 70 6f 63 68 03");
        Map<Date, String> values = new LinkedHashMap<>();
        values.put(date(1970, 1, 1, 0, 0, 0, 0, "UTC"), "epoch");
        assertArrayEquals(map, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(values));

        assertArrayEquals(VPackWireFixtureTest.hex(
                "58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 5a"), VPackMapper.builder()
                .defaultDateFormat(utcDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(date(1970, 1, 1, 0, 0, 0, 0, "UTC")));

        byte[] timestampKey = VPackWireFixtureTest.hex(
                "0b 0c 01 41 30 45 65 70 6f 63 68 03");
        assertArrayEquals(timestampKey, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS,
                        DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(values));
        assertArrayEquals(VPackWireFixtureTest.hex("30"), VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DateTimeFeature.WRITE_UTC_AS_OFFSET).build()
                .writeValueAsBytes(new Date(0L)));

        ObjectMapper london = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone("Europe/London"))
                .enable(DateTimeFeature.WRITE_UTC_AS_OFFSET).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "5d 32 30 32 34 2d 30 31 2d 31 35 54 30 30 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 30 3a 30 30"),
                london.writeValueAsBytes(new Date(1705276800000L)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "5d 32 30 32 34 2d 30 37 2d 31 35 54 30 31 3a 30 30"
              + "3a 30 30 2e 30 30 30 2b 30 31 3a 30 30"),
                london.writeValueAsBytes(new Date(1721001600000L)));
    }
private static SimpleDateFormat utcDateFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    // Provenance: JavaUtilDateSerializationTest#testDateDefaultShape().
    void testDateDefaultShapeVpack() throws Exception {
        byte[] numeric = VPackWireFixtureTest.hex(
                "0b 0a 01 44 64 61 74 65 30 03");
        byte[] iso = VPackWireFixtureTest.hex(
                "0b 22 01 44 64 61 74 65 58 31 39 37 30 2d 30 31 2d 30 31"
              + "54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a 03");
        byte[] day = VPackWireFixtureTest.hex(
                "0b 14 01 44 64 61 74 65 4a 31 39 37 30 2d 30 31 2d 30 31 03");
        byte[] plusOne = VPackWireFixtureTest.hex(
                "0b 27 01 44 64 61 74 65 5d 31 39 37 30 2d 30 31 2d 30 31"
              + "54 30 31 3a 30 30 3a 30 30 2e 30 30 30 2b 30 31 3a 30 30 03");

        assertArrayEquals(numeric, MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBean(0L)));
        assertArrayEquals(iso, MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBean(0L)));
        assertArrayEquals(numeric, MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithEmptyJsonFormat(0L)));
        assertArrayEquals(iso, MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithEmptyJsonFormat(0L)));
        assertArrayEquals(day, MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithPattern(0L)));
        assertArrayEquals(day, MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithPattern(0L)));
        assertArrayEquals(iso, MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithLocale(0L)));
        assertArrayEquals(iso, MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithLocale(0L)));
        assertArrayEquals(plusOne, MAPPER.writer()
                .with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithTimezone(0L)));
        assertArrayEquals(plusOne, MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(new DateAsDefaultBeanWithTimezone(0L)));
    }
private static Date date(int year, int month, int day, int hour, int minutes,
            int seconds, int millis, String timezone) {
        Calendar calendar = Calendar.getInstance();
        if (year < 0) {
            year = -year + 1;
            calendar.set(Calendar.ERA, GregorianCalendar.BC);
        }
        calendar.set(year, month - 1, day, hour, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, millis);
        calendar.setTimeZone(TimeZone.getTimeZone(timezone));
        return calendar.getTime();
    }
static class VoidBean {
        public Void value;
    }
static class MyBean2565 {
        @JsonUnwrapped
        public AtomicReference<String> maybeText = new AtomicReference<>("value");
    }
static class DateAsDefaultBean {
        public Date date;
        DateAsDefaultBean(long value) { date = new Date(value); }
    }
static class DateAsDefaultBeanWithEmptyJsonFormat {
        @JsonFormat
        public Date date;
        DateAsDefaultBeanWithEmptyJsonFormat(long value) { date = new Date(value); }
    }
static class DateAsDefaultBeanWithPattern {
        @JsonFormat(pattern = "yyyy-MM-dd")
        public Date date;
        DateAsDefaultBeanWithPattern(long value) { date = new Date(value); }
    }
static class DateAsDefaultBeanWithLocale {
        @JsonFormat(locale = "fr")
        public Date date;
        DateAsDefaultBeanWithLocale(long value) { date = new Date(value); }
    }
static class DateAsDefaultBeanWithTimezone {
        @JsonFormat(timezone = "CET")
        public Date date;
        DateAsDefaultBeanWithTimezone(long value) { date = new Date(value); }
    }

    void __invoke_testDateNumericVpack() throws Exception {
        try {
            testDateNumericVpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601Vpack() throws Exception {
        try {
            testDateISO8601Vpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601_10kVpack() throws Exception {
        try {
            testDateISO8601_10kVpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601_BCEVpack() throws Exception {
        try {
            testDateISO8601_BCEVpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601_customTZVpack() throws Exception {
        try {
            testDateISO8601_customTZVpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601_colonInTZVpack() throws Exception {
        try {
            testDateISO8601_colonInTZVpack();
        } finally {
        }
    }


    void __invoke_testDateOtherVpack() throws Exception {
        try {
            testDateOtherVpack();
        } finally {
        }
    }


    void __invoke_testDateUsingObjectWriterVpack() throws Exception {
        try {
            testDateUsingObjectWriterVpack();
        } finally {
        }
    }


    void __invoke_testDateISO8601_zeroOffsetAsNumericVpack() throws Exception {
        try {
            testDateISO8601_zeroOffsetAsNumericVpack();
        } finally {
        }
    }


    void __invoke_testDateDefaultShapeVpack() throws Exception {
        try {
            testDateDefaultShapeVpack();
        } finally {
        }
    }

}
