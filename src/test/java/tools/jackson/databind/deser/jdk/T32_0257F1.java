package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
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
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0257F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .defaultTimeZone(TimeZone.getTimeZone("GMT+2"))
            .build();
private static final byte[] WRAP_INPUT = compactObject(
            "containers", compactObject("key", compactObject("processor-id", shortString("123"))),
            "maxChangeLogStreamPartitions", VPackWireFixtureTest.hex("29 0d"));
private static final byte[] FOO_NULL = VPackWireFixtureTest.hex(
            "14 08 43 66 6f 6f 18 01");

    // Provenance: CustomMapKeyDeserializationTest#testCustomSerializer.
    void testCustomSerializer() throws Exception {
        byte[] actual = MAPPER.writeValueAsBytes(
                Collections.singletonMap(new Key2454("a", true), "b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 44 69 64 3d 61 41 62 03"), actual);
    }

    // Provenance: CustomMapKeyDeserializationTest#testCustomDeserializer.
    void testCustomDeserializer() throws Exception {
        Map<Key2454, String> result = MAPPER.readValue(
                VPackWireFixtureTest.hex("14 07 41 61 41 62 01"),
                new tools.jackson.core.type.TypeReference<Map<Key2454, String>>() { });
        assertEquals(1, result.size());
        assertEquals("a", result.keySet().iterator().next().id);
    }

    // Provenance: CustomMapKeyDeserializationTest#testAnnotationKeyDeserWithoutMapperOverride4444.
    void testAnnotationKeyDeserWithoutMapperOverride4444() throws Exception {
        Map<Key4444, String> result = MAPPER.readValue(FOO_NULL,
                new tools.jackson.core.type.TypeReference<Map<Key4444, String>>() { });
        assertEquals("foo-class", result.keySet().iterator().next().value);
    }

    // Provenance: CustomMapKeyDeserializationTest#testAnnotationKeyDeserWithMapperOverride4444.
    void testAnnotationKeyDeserWithMapperOverride4444() throws Exception {
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Key4444.class, new Key4444ForMapper());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Map<Key4444, String> result = mapper.readValue(FOO_NULL,
                new tools.jackson.core.type.TypeReference<Map<Key4444, String>>() { });
        assertEquals("foo-class", result.keySet().iterator().next().value);
    }

    // Provenance: CustomMapKeyDeserializationTest#testCustomKeyDeserializerNested4680.
    
    void testCustomKeyDeserializerNested4680() throws Exception {
        SimpleModule module = new SimpleModule("key-sanitization");
        module.addKeyDeserializer(String.class, new SanitizingKeyDeserializer());
        module.addKeyDeserializer(Object.class, new SanitizingKeyDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();

        Map<String, Object> result = mapper.readValue(NESTED_KEYS,
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() { });
        assertEquals("Erik", result.get("name_"));
        Map<String, Object> address = (Map<String, Object>) result.get("address_");
        assertEquals("Elvirastr", address.get("street_"));
        Map<String, Object> city = (Map<String, Object>) address.get("city_");
        assertEquals(1, city.get("id_"));
        assertEquals("Berlin", city.get("name_"));
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

    void __invoke_testCustomSerializer() throws Exception {
        try {
            testCustomSerializer();
        } finally {
        }
    }


    void __invoke_testCustomDeserializer() throws Exception {
        try {
            testCustomDeserializer();
        } finally {
        }
    }


    void __invoke_testAnnotationKeyDeserWithoutMapperOverride4444() throws Exception {
        try {
            testAnnotationKeyDeserWithoutMapperOverride4444();
        } finally {
        }
    }


    void __invoke_testAnnotationKeyDeserWithMapperOverride4444() throws Exception {
        try {
            testAnnotationKeyDeserWithMapperOverride4444();
        } finally {
        }
    }


    void __invoke_testCustomKeyDeserializerNested4680() throws Exception {
        try {
            testCustomKeyDeserializerNested4680();
        } finally {
        }
    }

}
