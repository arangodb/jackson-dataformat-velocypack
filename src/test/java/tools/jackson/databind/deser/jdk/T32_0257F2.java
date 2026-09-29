package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0257F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .defaultTimeZone(TimeZone.getTimeZone("GMT+2"))
            .build();
private static final byte[] WRAP_INPUT = compactObject(
            "containers", compactObject("key", compactObject("processor-id", shortString("123"))),
            "maxChangeLogStreamPartitions", VPackWireFixtureTest.hex("29 0d"));
private static final byte[] FOO_NULL = VPackWireFixtureTest.hex(
            "14 08 43 66 6f 6f 18 01");

    // Provenance: DateDeserializationTZTest#testDateUtilISO8601_Timezone.
    void testDateUtilISO8601_Timezone() throws Exception {
        date("2000-01-02T03:04:05.678+01:00", date(2000, 1, 2, 3, 4, 5, 678, "GMT+1"));
        date("2000-01-02T03:04:05.678+0100", date(2000, 1, 2, 3, 4, 5, 678, "GMT+1"));
        date("2000-01-02T03:04:05.678+01", date(2000, 1, 2, 3, 4, 5, 678, "GMT+1"));
        date("2000-01-02T03:04:05.678Z", date(2000, 1, 2, 3, 4, 5, 678, "UTC"));
        for (String value : new String[] {
                "2000-01-02T03:04:05.678+", "2000-01-02T03:04:05.678+1",
                "2000-01-02T03:04:05.678+001", "2000-01-02T03:04:05.678+00:",
                "2000-01-02T03:04:05.678+00:001", "2000-01-02T03:04:05.678+001:001",
                "2000-01-02T03:04:05.678+1:", "2000-01-02T03:04:05.678+00:1"
        }) invalidDate(value);
    }

    // Provenance: DateDeserializationTZTest#testDateUtilISO8601_DateTimeMillis.
    void testDateUtilISO8601_DateTimeMillis() throws Exception {
        for (String value : new String[] {
                "2000-01-02T03:04:05.6789+01:00", "2000-01-02T03:04:05.678+01:00",
                "2000-01-02T03:04:05.67+01:00", "2000-01-02T03:04:05.6+01:00",
                "2000-01-02T03:04:05+01:00"
        }) date(value, date(2000, 1, 2, 3, 4, 5, millis(value), "GMT+1"));
        for (String value : new String[] {
                "2000-01-02T03:04:05.6789Z", "2000-01-02T03:04:05.678Z",
                "2000-01-02T03:04:05.67Z", "2000-01-02T03:04:05.6Z",
                "2000-01-02T03:04:05Z"
        }) date(value, date(2000, 1, 2, 3, 4, 5, millis(value), "UTC"));
        for (String value : new String[] {
                "2000-01-02T03:04:05.6789", "2000-01-02T03:04:05.678",
                "2000-01-02T03:04:05.67", "2000-01-02T03:04:05.6",
                "2000-01-02T03:04:05"
        }) date(value, date(2000, 1, 2, 3, 4, 5, millis(value), "GMT+2"));
        for (String value : new String[] {
                "2000-01-02T03:04:05.0123456789+01:00",
                "2000-01-02T03:04:05.0123456789Z",
                "2000-01-02T03:04:05.0123456789",
                "2000-01-02T03:04:05.+01:00", "2000-01-02T03:04:05.",
                "2000-01-02T03:04:05.Z"
        }) invalidDate(value);
    }

    // Provenance: DateDeserializationTZTest#testDateUtilISO8601_DateTime.
    void testDateUtilISO8601_DateTime() throws Exception {
        date("2000-01-02T03:04:05+01:00", date(2000, 1, 2, 3, 4, 5, 0, "GMT+1"));
        date("2000-01-02T03:04:05", date(2000, 1, 2, 3, 4, 5, 0, "GMT+2"));
        date("2000-01-02T03:04", date(2000, 1, 2, 3, 4, 0, 0, "GMT+2"));
        date("2000-01-02T03:04+01:00", date(2000, 1, 2, 3, 4, 0, 0, "GMT+1"));
        date("2000-01-02T03:04Z", date(2000, 1, 2, 3, 4, 0, 0, "UTC"));
        for (String value : new String[] {
                "2000-01-02T", "2000-01-02T03", "2000-01-02T03:",
                "2000-01-02T03:04:", "2000-01-02T+01:00", "2000-01-02T03+01:00",
                "2000-01-02T03:+01:00", "2000-01-02T03:04:+01:00",
                "2000-01-02TZ", "2000-01-02T03Z", "2000-01-02T03:Z",
                "2000-01-02T03:04:Z", "2000-01-02T3:04:05.000+01:00",
                "2000-01-02T003:04:05+01:00", "2000-01-02T3:04:05Z",
                "2000-01-02T3:04:05.000Z", "2000-01-02T003:04:05Z",
                "2000-01-02T03:04:5", "2000-01-02T03:04:5.000",
                "2000-01-02T03:04:005", "2000-01-02T03:04:5+01:00",
                "2000-01-02T03:04:5.000+01:00", "2000-01-02T03:04:005+01:00",
                "2000-01-02T03:04:5Z", "2000-01-02T03:04:5.000Z",
                "2000-01-02T03:04:005Z", "2000-01-02T03:4:05",
                "2000-01-02T03:4:05.000", "2000-01-02T03:004:05",
                "2000-01-02T03:4:05+01:00", "2000-01-02T03:4:05.000+01:00",
                "2000-01-02T03:004:05+01:00", "2000-01-02T03:4:05Z",
                "2000-01-02T03:4:05.000Z", "2000-01-02T03:004:05Z",
                "2000-01-02T3:04:05", "2000-01-02T3:04:05.000",
                "2000-01-02T003:04:05", "2000-01-02T3:04:05+01:00",
                "2000-01-02T3:04:05.000+01:00", "2000-01-02T003:04:05+01:00"
        }) invalidDate(value);
    }

    // Provenance: DateDeserializationTZTest#testDateUtilISO8601_Date.
    void testDateUtilISO8601_Date() throws Exception {
        date("2000-01-02", date(2000, 1, 2, 0, 0, 0, 0, "GMT+2"));
        for (String value : new String[] {
                "2000-01-2", "2000-01-002", "2000-1-02", "2000-001-02",
                "20000-01-02", "200-01-02", "20-01-02", "2-01-02"
        }) invalidDate(value);
    }

    // Provenance: DateDeserializationTZTest#testDateUtil_Annotation.
    void testDateUtil_Annotation() throws Exception {
        Date expected = date(2005, 5, 25, 0, 0, 0, 0, "GMT+2");
        DateDeserializationBean result = MAPPER.readValue(ANNOTATED_DATE, DateDeserializationBean.class);
        assertNotNull(result);
        assertEquals(expected, result.date);
        result = MAPPER.readerFor(DateDeserializationBean.class).with(Locale.GERMANY)
                .readValue(ANNOTATED_DATE);
        assertEquals(expected, result.date);
        AnnotatedDateGermany german = MAPPER.readValue(ANNOTATED_DATE, AnnotatedDateGermany.class);
        assertEquals(expected, german.date);
    }

    // Provenance: DateDeserializationTZTest#testDateUtil_Annotation_PatternAndLocale.
    void testDateUtil_Annotation_PatternAndLocale() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .defaultTimeZone(TimeZone.getTimeZone("GMT+2"))
                .defaultLocale(Locale.ITALY).build();
        PatternDates result = mapper.readValue(PATTERN_DATES, PatternDates.class);
        Date italian = date(2000, 6, 1, 1, 2, 3, 0, "GMT+2");
        Date gmt4 = date(2000, 6, 1, 1, 2, 3, 0, "GMT+4");
        assertNotNull(result);
        assertEquals(italian, result.pattern);
        assertEquals(italian, result.pattern_FR);
        assertEquals(gmt4, result.pattern_GMT4);
        assertEquals(gmt4, result.pattern_FR_GMT4);
    }
private static void assertTrueMessage(Throwable value, String message) {
        assertEquals(message, value.getCause() instanceof CustomException
                ? value.getCause().getMessage() : value.getMessage());
    }
private static void date(String value, Date expected) throws Exception {
        assertEquals(expected.getTime(), MAPPER.readValue(shortString(value), Date.class).getTime(), value);
    }
private static void invalidDate(String value) {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(shortString(value), Date.class), value);
    }
private static int millis(String value) {
        int dot = value.indexOf('.');
        if (dot < 0 || dot + 1 >= value.length()) return 0;
        int end = dot + 1;
        while (end < value.length() && Character.isDigit(value.charAt(end))) ++end;
        String fraction = value.substring(dot + 1, end);
        if (fraction.length() > 3) fraction = fraction.substring(0, 3);
        while (fraction.length() < 3) fraction += "0";
        return Integer.parseInt(fraction);
    }
private static Date date(int year, int month, int day, int hour, int minute,
            int second, int millis, String zone) {
        java.util.Calendar calendar = java.util.Calendar.getInstance(TimeZone.getTimeZone(zone), Locale.ROOT);
        calendar.setLenient(false);
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(java.util.Calendar.MILLISECOND, millis);
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
        try {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            for (int i = 0; i < fields.length; i += 2) {
                body.write(shortString((String) fields[i]));
                body.write((byte[]) fields[i + 1]);
            }
            byte[] contents = body.toByteArray();
            int total = 1 + contents.length + 2;
            byte[] length;
            while (true) {
                length = forwardVarint(total);
                int actualTotal = 1 + length.length + contents.length + 1;
                if (actualTotal == total) break;
                total = actualTotal;
            }
            ByteArrayOutputStream result = new ByteArrayOutputStream(1 + length.length + contents.length + 1);
            result.write(0x14);
            result.write(length);
            result.write(contents);
            result.write(reverseVarint(fields.length / 2));
            return result.toByteArray();
        } catch (java.io.IOException e) {
            throw new AssertionError(e);
        }
    }
private static byte[] forwardVarint(int value) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        do {
            int group = value & 0x7f;
            value >>>= 7;
            result.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return result.toByteArray();
    }
private static byte[] reverseVarint(int value) {
        byte[] forward = forwardVarint(value);
        for (int i = 0, j = forward.length - 1; i < j; ++i, --j) {
            byte swap = forward[i];
            forward[i] = forward[j];
            forward[j] = swap;
        }
        return forward;
    }
private static final byte[] ANNOTATED_DATE = compactObject(
            "date", shortString("/2005/05/25/"));
private static final byte[] PATTERN_DATES = compactObject(
            "pattern", shortString("*1 giu 2000 01:02:03*"),
            "pattern_FR", shortString("*01 juin 2000 01:02:03*"),
            "pattern_GMT4", shortString("*1 giu 2000 01:02:03*"),
            "pattern_FR_GMT4", shortString("*1 juin 2000 01:02:03*"));
private static final byte[] NESTED_KEYS = compactObject(
            "name*", shortString("Erik"),
            "address*", compactObject(
                    "city*", compactObject("id*", VPackWireFixtureTest.hex("31"),
                            "name*", shortString("Berlin")),
                    "street*", shortString("Elvirastr")));
@JsonDeserialize(keyUsing = Key2454Deserializer.class)
    @JsonSerialize(keyUsing = Key2454Serializer.class)
    static class Key2454 {
        String id;
        Key2454(String id, boolean ignored) { this.id = id; }
    }
static class Key2454Deserializer extends KeyDeserializer {
        @Override public Object deserializeKey(String key, DeserializationContext ctxt) {
            return new Key2454(key, false);
        }
    }
static class Key2454Serializer extends ValueSerializer<Key2454> {
        @Override public void serialize(Key2454 value, JsonGenerator generator, SerializationContext context) {
            generator.writeName("id=" + value.id);
        }
    }
@JsonDeserialize(keyUsing = Key4444ForClass.class)
    static class Key4444 {
        final String value;
        Key4444(String value) { this.value = value; }
    }
static class Key4444ForClass extends KeyDeserializer {
        @Override public Object deserializeKey(String key, DeserializationContext ctxt) {
            return new Key4444(key + "-class");
        }
    }
static class Key4444ForMapper extends KeyDeserializer {
        @Override public Object deserializeKey(String key, DeserializationContext ctxt) {
            return new Key4444(key + "-mapper");
        }
    }
static class SanitizingKeyDeserializer extends KeyDeserializer {
        @Override public Object deserializeKey(String key, DeserializationContext ctxt) {
            return key.replace('*', '_');
        }
    }
static class MyContainerModel {
        @JsonProperty("processor-id") public String id;
    }
static class MyJobModel {
        public Map<String, MyContainerModel> containers;
        public int maxChangeLogStreamPartitions;
    }
static class CustomException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        CustomException(String message) { super(message); }
    }
static class DateDeserializationBean {
        @JsonFormat(pattern="'/'yyyy'/'MM'/'dd'/'")
        public Date date;
    }
static class AnnotatedDateGermany {
        @JsonFormat(pattern="'/'yyyy'/'MM'/'dd'/'")
        public Date date;
    }
static class PatternDates {
        @JsonFormat(pattern="'*'d MMM yyyy HH:mm:ss'*'") public Date pattern;
        @JsonFormat(pattern="'*'d MMM yyyy HH:mm:ss'*'", locale="FR") public Date pattern_FR;
        @JsonFormat(pattern="'*'d MMM yyyy HH:mm:ss'*'", timezone="GMT+4") public Date pattern_GMT4;
        @JsonFormat(pattern="'*'d MMM yyyy HH:mm:ss'*'", locale="FR", timezone="GMT+4")
        public Date pattern_FR_GMT4;
    }

    void __invoke_testDateUtilISO8601_Timezone() throws Exception {
        try {
            testDateUtilISO8601_Timezone();
        } finally {
        }
    }


    void __invoke_testDateUtilISO8601_DateTimeMillis() throws Exception {
        try {
            testDateUtilISO8601_DateTimeMillis();
        } finally {
        }
    }


    void __invoke_testDateUtilISO8601_DateTime() throws Exception {
        try {
            testDateUtilISO8601_DateTime();
        } finally {
        }
    }


    void __invoke_testDateUtilISO8601_Date() throws Exception {
        try {
            testDateUtilISO8601_Date();
        } finally {
        }
    }


    void __invoke_testDateUtil_Annotation() throws Exception {
        try {
            testDateUtil_Annotation();
        } finally {
        }
    }


    void __invoke_testDateUtil_Annotation_PatternAndLocale() throws Exception {
        try {
            testDateUtil_Annotation_PatternAndLocale();
        } finally {
        }
    }

}
