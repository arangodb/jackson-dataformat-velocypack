package tools.jackson.databind.deser.jdk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0258F1 {
private static final String LOCAL_TZ = "GMT+2";
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .defaultTimeZone(TimeZone.getTimeZone(LOCAL_TZ))
            .build();
private static final byte[] LONG_123456789 = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] LONG_1321992375446 = VPackWireFixtureTest.hex(
            "2d 96 cc e2 cc 33 01");
private static final byte[] LONG_NEGATIVE_DAY = VPackWireFixtureTest.hex(
            "23 00 a4 d9 fa");
private static final byte[] CALENDAR_MILLIS = VPackWireFixtureTest.hex(
            "2b 4e 61 bc 00");
private static final byte[] TOO_LARGE_INTEGER = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 09 22 33 72 03 68 54 77 58 08");
private static final byte[] DECIMAL_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 00");
private static final byte[] CALENDAR_ARRAY = VPackWireFixtureTest.hex(
            "14 24 41 76 02 1f 5c 31 39 37 32 2d 31 32 2d 32 38 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 2b 30 30 30 30 01");

    // Provenance: DateDeserializationTest#test8601DateTimeNoMilliSecs.
    void test8601DateTimeNoMilliSecs() throws Exception {
        for (String value : new String[] {
                "2010-06-28T23:34:22Z", "2010-06-28T23:34:22+0000",
                "2010-06-28T23:34:22+00:00", "2010-06-28T23:34:22+00"
        }) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            calendar.setTime(MAPPER.readValue(shortString(value), Date.class));
            assertEquals(2010, calendar.get(Calendar.YEAR), value);
            assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH), value);
            assertEquals(28, calendar.get(Calendar.DAY_OF_MONTH), value);
            assertEquals(23, calendar.get(Calendar.HOUR_OF_DAY), value);
            assertEquals(34, calendar.get(Calendar.MINUTE), value);
            assertEquals(22, calendar.get(Calendar.SECOND), value);
            assertEquals(0, calendar.get(Calendar.MILLISECOND), value);
        }
    }

    // Provenance: DateDeserializationTest#testCalendar.
    void testCalendar() throws Exception {
        Calendar numeric = MAPPER.readValue(CALENDAR_MILLIS, Calendar.class);
        assertEquals(12_345_678L, numeric.getTimeInMillis());

        Calendar text = MAPPER.readValue(
                shortString("1970-01-01T03:25:45.678Z"), Calendar.class);
        assertEquals(12_345_678L, text.getTimeInMillis());
    }

    // Provenance: DateDeserializationTest#testCalendarArrayUnwrap.
    void testCalendarArrayUnwrap() throws Exception {
        ObjectMapper noUnwrap = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone(LOCAL_TZ))
                .disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        ObjectReader reader = noUnwrap.readerFor(CalendarBean.class);
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(CALENDAR_ARRAY));

        CalendarBean bean = reader.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(CALENDAR_ARRAY);
        assertNotNull(bean.value);
        assertEquals(1972, bean.value.get(Calendar.YEAR));
    }

    // Provenance: DateDeserializationTest#testContextTimezone.
    void testContextTimezone() throws Exception {
        String input = "1997-07-16T19:20:30.45+0100";
        ObjectReader reader = MAPPER.readerFor(Calendar.class)
                .with(TimeZone.getTimeZone("PST"));
        assertTrue(MAPPER.isEnabled(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));

        Calendar calendar = reader.readValue(shortString(input));
        assertEquals("PST", calendar.getTimeZone().getID());
        assertEquals(1997, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, calendar.get(Calendar.MONTH));
        assertEquals(16, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(11, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, calendar.get(Calendar.MINUTE));
        assertEquals(30, calendar.get(Calendar.SECOND));

        // The original declaration also exercises the no-adjustment read, whose
        // only assertion is that parsing completes.
        reader.without(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(shortString(input));
    }

    // Provenance: DateDeserializationTest#testCustom.
    void testCustom() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        ObjectMapper mapper = VPackMapper.builder().defaultDateFormat(format).build();

        Date result = mapper.readValue(shortString("1972-12-28X15:45:00"), Date.class);
        assertEquals(format.parse("1972-12-28X15:45:00"), result);
    }

    // Provenance: DateDeserializationTest#testCustomCalendarWithAnnotation.
    void testCustomCalendarWithAnnotation() throws Exception {
        CalendarAsStringBean result = MAPPER.readValue(
                compactObject("cal", shortString(";2007/07/13;")),
                CalendarAsStringBean.class);
        assertNotNull(result);
        assertNotNull(result.cal);
        assertEquals(2007, result.cal.get(Calendar.YEAR));
        assertEquals(Calendar.JULY, result.cal.get(Calendar.MONTH));
        assertEquals(13, result.cal.get(Calendar.DAY_OF_MONTH));
    }

    // Provenance: DateDeserializationTest#testCustomCalendarWithTimeZone.
    void testCustomCalendarWithTimeZone() throws Exception {
        DateInCETBean result = MAPPER.readValue(
                compactObject("date", shortString("2001-01-01,10")),
                DateInCETBean.class);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(result.date.getTime());
        assertEquals(2001, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(9, calendar.get(Calendar.HOUR_OF_DAY));
    }
private static Date date(int year, int month, int day, int hour, int minute,
            int second, int millis, String zone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(zone), Locale.ROOT);
        calendar.setLenient(false);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millis);
        return calendar.getTime();
    }
private static byte[] shortString(String value) {
        if (value.length() > 126) throw new IllegalArgumentException("fixture too long");
        byte[] result = new byte[value.length() + 1];
        result[0] = (byte) (0x40 + value.length());
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            if (c > 0x7f) throw new IllegalArgumentException("ASCII fixture expected");
            result[i + 1] = (byte) c;
        }
        return result;
    }
private static byte[] compactObject(Object... fields) {
        int bodyLength = 0;
        for (int i = 0; i < fields.length; i += 2) {
            bodyLength += shortString((String) fields[i]).length;
            bodyLength += ((byte[]) fields[i + 1]).length;
        }
        int length = 1 + bodyLength + 2;
        byte[] result = new byte[1 + 1 + bodyLength + 1];
        result[0] = 0x14;
        result[1] = (byte) length;
        int offset = 2;
        for (int i = 0; i < fields.length; i += 2) {
            byte[] name = shortString((String) fields[i]);
            byte[] value = (byte[]) fields[i + 1];
            System.arraycopy(name, 0, result, offset, name.length);
            offset += name.length;
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        result[offset] = 1;
        return result;
    }
static class AnnotTimeZone {
        @JsonFormat(timezone = "GMT+4")
        public Date date;
    }
static class CalendarBean {
        @JsonProperty("v")
        public Calendar value;
    }
static class CalendarAsStringBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ";yyyy/MM/dd;")
        public Calendar cal;
    }
static class DateInCETBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH", timezone = "CET")
        public Date date;
    }

    void __invoke_test8601DateTimeNoMilliSecs() throws Exception {
        try {
            test8601DateTimeNoMilliSecs();
        } finally {
        }
    }


    void __invoke_testCalendar() throws Exception {
        try {
            testCalendar();
        } finally {
        }
    }


    void __invoke_testCalendarArrayUnwrap() throws Exception {
        try {
            testCalendarArrayUnwrap();
        } finally {
        }
    }


    void __invoke_testContextTimezone() throws Exception {
        try {
            testContextTimezone();
        } finally {
        }
    }


    void __invoke_testCustom() throws Exception {
        try {
            testCustom();
        } finally {
        }
    }


    void __invoke_testCustomCalendarWithAnnotation() throws Exception {
        try {
            testCustomCalendarWithAnnotation();
        } finally {
        }
    }


    void __invoke_testCustomCalendarWithTimeZone() throws Exception {
        try {
            testCustomCalendarWithTimeZone();
        } finally {
        }
    }

}
