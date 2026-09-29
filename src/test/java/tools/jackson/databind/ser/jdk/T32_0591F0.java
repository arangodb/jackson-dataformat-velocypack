package tools.jackson.databind.ser.jdk;

import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0591F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: JavaUtilDateSerializationTest#testDateWithJsonFormat().
    void testDateWithJsonFormatVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 44 64 61 74 65 30 03"),
                MAPPER.writer().without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .writeValueAsBytes(new DateAsNumberBean(0L)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 44 64 61 74 65 25 51 60 2c fc bd fe 03"),
                MAPPER.writer().without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .writeValueAsBytes(new DateAsNumberBean(-1383043669935L)));

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 14 01 44 64 61 74 65 4a 31 39 37 30 2d 30 31 2d 30 31 03"),
                MAPPER.writer().with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .with(TimeZone.getTimeZone("UTC"))
                        .writeValueAsBytes(new DateAsStringBean(0L)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1a 01 44 64 61 74 65 50 31 39 37 30 2d 30 31 2d 30 31 2c 30 31 3a 30 30 03"),
                MAPPER.writeValueAsBytes(new DateInCETBean(0L)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 45 76 61 6c 75 65 4a 31 39 37 30 2d 30 31 2d 30 31 03"),
                MAPPER.writer().with(TimeZone.getTimeZone("UTC"))
                        .writeValueAsBytes(new CalendarAsStringBean(0L)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 22 01 44 64 61 74 65 58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a 03"),
                MAPPER.writeValueAsBytes(new DateAsDefaultStringBean(0L)));
    }

    // Provenance: JavaUtilDateSerializationTest#testDatesAsMapKeys().
    void testDatesAsMapKeysVpack() throws Exception {
        Map<Date, Integer> map = new LinkedHashMap<>();
        map.put(new Date(0L), 1);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1e 01 58 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 2e 30 30 30 5a 31 03"),
                MAPPER.writeValueAsBytes(map));

        ObjectMapper timestampKeys = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 07 01 41 30 31 03"), timestampKeys.writeValueAsBytes(map));
    }

    // Provenance: JavaUtilDateSerializationTest#testFormatWithoutPattern().
    void testFormatWithoutPatternVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss"))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1d 01 44 64 61 74 65 53 31 39 37 30 2d 30 31 2d 30 31 58 30 31 3a 30 30 3a 30 30 03"),
                mapper.writeValueAsBytes(new DateAsDefaultBeanWithTimezone(0L)));
    }

    // Provenance: JavaUtilDateSerializationTest#testTimeZone().
    void testTimeZoneVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("43 50 53 54"),
                MAPPER.writeValueAsBytes(TimeZone.getTimeZone("PST")));
    }

    // Provenance: JavaUtilDateSerializationTest#testTimeZoneInBean().
    void testTimeZoneInBeanVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 42 74 7a 43 50 53 54 03"),
                MAPPER.writeValueAsBytes(new TimeZoneBean("PST")));
    }

    // Provenance: JavaUtilDateSerializationTest#testWithTimeZoneOverride().
    void testWithTimeZoneOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd/HH:mm z", Locale.US))
                .defaultTimeZone(TimeZone.getTimeZone("PST"))
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        byte[] pst = VPackWireFixtureTest.hex(
                "54 31 39 36 39 2d 31 32 2d 33 31 2f 31 36 3a 30 30 20 50 53 54");
        assertArrayEquals(pst, mapper.writeValueAsBytes(new Date(0L)));
        assertArrayEquals(pst, mapper.writer().with(TimeZone.getTimeZone("PST"))
                .writeValueAsBytes(new Date(0L)));

        byte[] est = VPackWireFixtureTest.hex(
                "54 31 39 36 39 2d 31 32 2d 33 31 2f 31 39 3a 30 30 20 45 53 54");
        assertArrayEquals(est, mapper.writer().with(TimeZone.getTimeZone("EST"))
                .writeValueAsBytes(new Date(0L)));

        // The short display name for Asia/Tehran is JDK time-zone data: older JDKs
        // emit "IRST", newer CLDR-based ones fall back to "GMT+03:30".
        TimeZone tehran = TimeZone.getTimeZone("Asia/Tehran");
        SimpleDateFormat zoneOnly = new SimpleDateFormat("z", Locale.US);
        zoneOnly.setTimeZone(tehran);
        byte[] irst = shortVpackString("1970-01-01/03:30 " + zoneOnly.format(new Date(0L)));
        assertArrayEquals(irst, mapper.writer().with(tehran)
                .writeValueAsBytes(new Date(0L)));
    }
private static byte[] shortVpackString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }

    // Provenance: JavaUtilDateSerializationTest#testWriteDatesAsTimeStamps().
    void testWriteDatesAsTimeStampsVpack() throws Exception {
        byte[] timestamp = VPackWireFixtureTest.hex(
                "2d cb 04 fb 71 1f 01");
        byte[] text = VPackWireFixtureTest.hex(
                "58 32 30 30 39 2d 30 32 2d 31 33 54 32 33 3a 33 31 3a 33 30 2e 31 32 33 5a");
        ObjectMapper withTimestamps = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        ObjectMapper withoutTimestamps = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        Date date = new Date(1234567890123L);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(1234567890123L);
        assertArrayEquals(timestamp, withTimestamps.writerFor(Date.class).writeValueAsBytes(date));
        assertArrayEquals(text, withoutTimestamps.writerFor(Date.class).writeValueAsBytes(date));
        assertArrayEquals(timestamp, withTimestamps.writerFor(Calendar.class).writeValueAsBytes(calendar));
        assertArrayEquals(text, withoutTimestamps.writerFor(Calendar.class).writeValueAsBytes(calendar));
    }
static class TimeZoneBean {
        private final TimeZone tz;
        TimeZoneBean(String name) { tz = TimeZone.getTimeZone(name); }
        public TimeZone getTz() { return tz; }
    }
static class DateAsNumberBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Date date;
        DateAsNumberBean(long value) { date = new Date(value); }
    }
static class DateAsStringBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        public Date date;
        DateAsStringBean(long value) { date = new Date(value); }
    }
static class DateInCETBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd,HH:00", timezone = "CET")
        public Date date;
        DateInCETBean(long value) { date = new Date(value); }
    }
static class CalendarAsStringBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        public Calendar value;
        CalendarAsStringBean(long value) {
            this.value = new GregorianCalendar();
            this.value.setTimeInMillis(value);
        }
    }
static class DateAsDefaultStringBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Date date;
        DateAsDefaultStringBean(long value) { date = new Date(value); }
    }
static class DateAsDefaultBeanWithTimezone {
        @JsonFormat(timezone = "CET")
        public Date date;
        DateAsDefaultBeanWithTimezone(long value) { date = new Date(value); }
    }
static class StringIntMapEntry implements Map.Entry<String, Integer> {
        public final String k;
        public final Integer v;
        StringIntMapEntry(String key, Integer value) { k = key; v = value; }
        @Override public String getKey() { return k; }
        @Override public Integer getValue() { return v; }
        @Override public Integer setValue(Integer value) { throw new UnsupportedOperationException(); }
    }
static class StringIntMapEntryWrapper {
        public StringIntMapEntry value;
        StringIntMapEntryWrapper(String key, Integer value) {
            this.value = new StringIntMapEntry(key, value);
        }
    }
static class NotKarlBean {
        public Map<String, Integer> map = new HashMap<>();
        { map.put("Not Karl", 1); }
    }
static class KarlBean {
        @JsonSerialize(keyUsing = KarlSerializer.class)
        public Map<String, Integer> map = new HashMap<>();
        { map.put("Not Karl", 1); }
    }
static class KarlSerializer extends tools.jackson.databind.ValueSerializer<String> {
        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                tools.jackson.databind.SerializationContext context) {
            generator.writeName("Karl");
        }
    }
static class Inner2871 {
        @com.fasterxml.jackson.annotation.JsonKey String key;
        @com.fasterxml.jackson.annotation.JsonValue String value;
        Inner2871(String key, String value) { this.key = key; this.value = value; }
    }
static class Outer2871 {
        @com.fasterxml.jackson.annotation.JsonKey
        @com.fasterxml.jackson.annotation.JsonValue
        Inner2871 inner;
        Outer2871(Inner2871 value) { inner = value; }
    }

    void __invoke_testDateWithJsonFormatVpack() throws Exception {
        try {
            testDateWithJsonFormatVpack();
        } finally {
        }
    }


    void __invoke_testDatesAsMapKeysVpack() throws Exception {
        try {
            testDatesAsMapKeysVpack();
        } finally {
        }
    }


    void __invoke_testFormatWithoutPatternVpack() throws Exception {
        try {
            testFormatWithoutPatternVpack();
        } finally {
        }
    }


    void __invoke_testTimeZoneVpack() throws Exception {
        try {
            testTimeZoneVpack();
        } finally {
        }
    }


    void __invoke_testTimeZoneInBeanVpack() throws Exception {
        try {
            testTimeZoneInBeanVpack();
        } finally {
        }
    }


    void __invoke_testWithTimeZoneOverrideVpack() throws Exception {
        try {
            testWithTimeZoneOverrideVpack();
        } finally {
        }
    }


    void __invoke_testWriteDatesAsTimeStampsVpack() throws Exception {
        try {
            testWriteDatesAsTimeStampsVpack();
        } finally {
        }
    }

}
