package tools.jackson.databind.deser.jdk;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0258F0 {
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

    // Provenance: DateDeserializationTZTest#testDateUtil_Annotation_TimeZone.
    void testDateUtil_Annotation_TimeZone() throws Exception {
        AnnotTimeZone withoutOffset = MAPPER.readValue(
                compactObject("date", shortString("2000-01-02T03:04:05.678")),
                AnnotTimeZone.class);
        assertNotNull(withoutOffset);
        assertEquals(date(2000, 1, 2, 3, 4, 5, 678, "GMT+4"), withoutOffset.date);

        AnnotTimeZone withOffset = MAPPER.readValue(
                compactObject("date", shortString("2000-01-02T03:04:05.678+01:00")),
                AnnotTimeZone.class);
        assertNotNull(withOffset);
        assertEquals(date(2000, 1, 2, 3, 4, 5, 678, "GMT+1"), withOffset.date);
    }

    // Provenance: DateDeserializationTZTest#testDateUtil_Numeric.
    void testDateUtil_Numeric() throws Exception {
        assertEquals(123456789L, MAPPER.readValue(LONG_123456789, Date.class).getTime());
        assertEquals(123456789L, MAPPER.readValue(shortString("123456789"), Date.class).getTime());
        assertEquals(1321992375446L,
                MAPPER.readValue(LONG_1321992375446, Date.class).getTime());
        assertEquals(1321992375446L,
                MAPPER.readValue(shortString("1321992375446"), Date.class).getTime());
        assertEquals(-86_400_000L,
                MAPPER.readValue(LONG_NEGATIVE_DAY, Date.class).getTime());
        assertEquals(-86_400_000L,
                MAPPER.readValue(shortString("-86400000"), Date.class).getTime());

        assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(TOO_LARGE_INTEGER, Date.class));
        assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(shortString(BigInteger.valueOf(Long.MAX_VALUE)
                        .add(BigInteger.ONE).toString()), Date.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(DECIMAL_ZERO, Date.class));
        assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(shortString("0.0"), Date.class));
    }

    // Provenance: DateDeserializationTZTest#testDateUtil_customDateFormat_withTZ.
    void testDateUtil_customDateFormat_withTZ() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ssZ", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        ObjectMapper mapper = VPackMapper.builder().defaultDateFormat(format).build();

        Date actual = mapper.readValue(shortString("2000-01-02X03:04:05+0300"), Date.class);
        assertEquals(date(2000, 1, 2, 3, 4, 5, 0, "GMT+3"), actual);
    }

    // Provenance: DateDeserializationTZTest#testDateUtil_customDateFormat_withoutTZ.
    void testDateUtil_customDateFormat_withoutTZ() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        ObjectMapper mapper = VPackMapper.builder()
                .defaultDateFormat(format)
                .defaultTimeZone(TimeZone.getTimeZone(LOCAL_TZ))
                .build();
        assertEquals(date(2000, 1, 2, 4, 0, 0, 0, LOCAL_TZ),
                mapper.readValue(shortString("2000-01-02X04:00:00"), Date.class));

        DateFormat secondFormat = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss", Locale.ROOT);
        secondFormat.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        ObjectMapper mapperUsingFormatZone = VPackMapper.builder()
                .defaultDateFormat(secondFormat)
                .build();
        assertEquals(date(2000, 1, 2, 4, 0, 0, 0, "GMT+4"),
                mapperUsingFormatZone.readValue(shortString("2000-01-02X04:00:00"), Date.class));
    }

    // Provenance: DateDeserializationTZTest#testWithTimezones1153.
    void testWithTimezones1153() throws Exception {
        for (String zone : new String[] {
                "UTC", "CET", "America/Los_Angeles", "Australia/Melbourne"
        }) {
            ObjectReader reader = MAPPER.readerFor(Date.class)
                    .with(TimeZone.getTimeZone(zone));
            Date value = reader.readValue(shortString("2016-01-01T17:00:00.000Z"));
            assertEquals(1451667600000L, value.getTime(), zone);
        }
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

    void __invoke_testDateUtil_Annotation_TimeZone() throws Exception {
        try {
            testDateUtil_Annotation_TimeZone();
        } finally {
        }
    }


    void __invoke_testDateUtil_Numeric() throws Exception {
        try {
            testDateUtil_Numeric();
        } finally {
        }
    }


    void __invoke_testDateUtil_customDateFormat_withTZ() throws Exception {
        try {
            testDateUtil_customDateFormat_withTZ();
        } finally {
        }
    }


    void __invoke_testDateUtil_customDateFormat_withoutTZ() throws Exception {
        try {
            testDateUtil_customDateFormat_withoutTZ();
        } finally {
        }
    }


    void __invoke_testWithTimezones1153() throws Exception {
        try {
            testWithTimezones1153();
        } finally {
        }
    }

}
