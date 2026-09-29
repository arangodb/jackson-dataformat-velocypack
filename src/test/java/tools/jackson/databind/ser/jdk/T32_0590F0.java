package tools.jackson.databind.ser.jdk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0590F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JDKTypeSerializationTest#testVoidSerialization().
    void testVoidSerializationVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 18 03"),
                MAPPER.writeValueAsBytes(new VoidBean()));
    }

    // Provenance: JDKTypeSerializationTest#testWithUnwrappableUnwrapped().
    void testWithUnwrappableUnwrappedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 14 01 49 6d 61 79 62 65 54 65 78 74"
              + "45 76 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(new MyBean2565()));
    }
private static SimpleDateFormat utcDateFormat(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
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

    void __invoke_testVoidSerializationVpack() throws Exception {
        try {
            testVoidSerializationVpack();
        } finally {
        }
    }


    void __invoke_testWithUnwrappableUnwrappedVpack() throws Exception {
        try {
            testWithUnwrappableUnwrappedVpack();
        } finally {
        }
    }

}
