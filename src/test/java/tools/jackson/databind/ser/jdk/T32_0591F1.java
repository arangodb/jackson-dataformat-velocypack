package tools.jackson.databind.ser.jdk;

import java.util.*;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0591F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static byte[] shortVpackString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }

    // Provenance: MapEntrySerializationTest#testMapEntry().
    void testMapEntryVpack() throws Exception {
        StringIntMapEntry input = new StringIntMapEntry("answer", 42);
        byte[] entry = VPackWireFixtureTest.hex(
                "0b 0d 01 46 61 6e 73 77 65 72 28 2a 03");
        assertArrayEquals(entry, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 0f 0b 0d 01 46 61 6e 73 77 65 72 28 2a 03"),
                MAPPER.writeValueAsBytes(new StringIntMapEntry[] { input }));

        ObjectMapper typed = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("").build(),
                        DefaultTyping.NON_FINAL)
                .build();
        List<?> actual = MAPPER.readValue(typed.writeValueAsBytes(input), List.class);
        assertEquals(List.of(StringIntMapEntry.class.getName(), Map.of("answer", 42)), actual);
    }

    // Provenance: MapEntrySerializationTest#testMapEntryWrapper().
    void testMapEntryWrapperVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 01 45 76 61 6c 75 65 0b 0d 01 46 61 6e 73 77 65 72 28 2a 03 03"),
                MAPPER.writeValueAsBytes(new StringIntMapEntryWrapper("answer", 42)));
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

    void __invoke_testMapEntryVpack() throws Exception {
        try {
            testMapEntryVpack();
        } finally {
        }
    }


    void __invoke_testMapEntryWrapperVpack() throws Exception {
        try {
            testMapEntryWrapperVpack();
        } finally {
        }
    }

}
