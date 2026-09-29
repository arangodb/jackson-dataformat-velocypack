package tools.jackson.databind.ser.jdk;

import java.util.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0591F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static byte[] shortVpackString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }

    // Provenance: MapSerializationTest#testBoth().
    void testBothVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 43 6d 61 70 0b 0e 01 48 4e 6f 74 20 4b 61 72 6c 31 03 03"),
                MAPPER.writeValueAsBytes(new NotKarlBean()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 43 6d 61 70 0b 0a 01 44 4b 61 72 6c 31 03 03"),
                MAPPER.writeValueAsBytes(new KarlBean()));
    }

    // Provenance: MapSerializationTest#testClassAsKey().
    void testClassAsKeyVpack() throws Exception {
        Outer2871 outer = new Outer2871(new Inner2871("innerKey", "innerValue"));
        Map<Outer2871, String> map = Map.of(outer, "value");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 48 69 6e 6e 65 72 4b 65 79 45 76 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(map));
    }

    // Provenance: MapSerializationTest#testClassAsValue().
    void testClassAsValueVpack() throws Exception {
        Map<String, Outer2871> map = Map.of("key",
                new Outer2871(new Inner2871("innerKey", "innerValue")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 43 6b 65 79 4a 69 6e 6e 65 72 56 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(map));
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

    void __invoke_testBothVpack() throws Exception {
        try {
            testBothVpack();
        } finally {
        }
    }


    void __invoke_testClassAsKeyVpack() throws Exception {
        try {
            testClassAsKeyVpack();
        } finally {
        }
    }


    void __invoke_testClassAsValueVpack() throws Exception {
        try {
            testClassAsValueVpack();
        } finally {
        }
    }

}
