package tools.jackson.databind.misc;

import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0457F1 {

    // Provenance: CaseInsensitiveDeser953Test#testTurkishILetterDeserializationWithEn().
    void testTurkishILetterDeserializationWithEnVpack() throws Exception {
        testTurkishILetterDeserialization(englishMapper(), Locale.US);
    }

    // Provenance: CaseInsensitiveDeser953Test#testTurkishILetterDeserializationWithTr().
    void testTurkishILetterDeserializationWithTrVpack() throws Exception {
        testTurkishILetterDeserialization(turkishMapper(), new Locale("tr", "TR"));
    }
private static ObjectMapper nameMapper() {
        return defaultTypingMapper(new SimpleNameBasedValidator());
    }
private static ObjectMapper classNamedMapper() {
        // Preserve ValidatePolymSubTypeTest's pinned CLASS_CHECK initialization.
        return defaultTypingMapper(new SimpleNameBasedValidator());
    }
private static ObjectMapper defaultTypingMapper(PolymorphicTypeValidator validator) {
        return VPackMapper.builder()
                .activateDefaultTyping(validator, DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
    }
private static ObjectMapper englishMapper() {
        return VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .defaultLocale(Locale.US)
                .build();
    }
private static ObjectMapper turkishMapper() {
        return VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .defaultLocale(new Locale("tr", "TR"))
                .build();
    }
private static void testTurkishILetterDeserialization(ObjectMapper mapper, Locale locale)
            throws Exception {
        assertEquals(locale, mapper.deserializationConfig().getLocale());

        String originalKey = "someId";
        for (String key : new String[] { originalKey, originalKey.toUpperCase(locale),
                originalKey.toLowerCase(locale) }) {
            Id953 result = mapper.readValue(indexedObject(key, integer(1)), Id953.class);
            assertEquals(1, result.someId);
        }

        Id953 input = new Id953();
        input.someId = 1;
        Id953 result = mapper.readValue(mapper.writeValueAsBytes(input), Id953.class);
        assertEquals(1, result.someId);
    }
private static void assertGood(BaseValue value) {
        assertNotNull(value);
        assertEquals(GoodValue.class, value.getClass());
        assertEquals(3, value.x);
    }
private static void assertDenied(ObjectMapper mapper, byte[] input) {
        InvalidTypeIdException exception = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(input, DefTypeWrapper.class));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("Could not resolve type id"));
        assertTrue(exception.getMessage().contains("PolymorphicTypeValidator"));
        assertTrue(exception.getMessage().contains("denied resolution"));
    }
private static byte[] valueObject(byte[] value) {
        return indexedObject("value", value);
    }
private static byte[] typed(String typeId, byte[] value) {
        return indexedArray(string(typeId), value);
    }
private static byte[] indexedArray(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) {
            bodyLength += value.length;
        }
        int length = 3 + bodyLength + values.length;
        if (length > 255 || values.length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout");
        }
        byte[] result = new byte[length];
        result[0] = 0x06;
        result[1] = (byte) length;
        result[2] = (byte) values.length;
        int cursor = 3;
        int index = length - values.length;
        for (byte[] value : values) {
            result[index++] = (byte) cursor;
            System.arraycopy(value, 0, result, cursor, value.length);
            cursor += value.length;
        }
        return result;
    }
private static byte[] indexedObject(String name, byte[] value) {
        byte[] encodedName = string(name);
        int length = 4 + encodedName.length + value.length;
        if (length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout");
        }
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = 1;
        System.arraycopy(encodedName, 0, result, 3, encodedName.length);
        System.arraycopy(value, 0, result, 3 + encodedName.length, value.length);
        result[length - 1] = (byte) 3;
        return result;
    }
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) {
            throw new IllegalArgumentException("fixture string exceeds compact layout");
        }
        byte[] result = new byte[bytes.length + 1];
        result[0] = (byte) (0x40 + bytes.length);
        System.arraycopy(bytes, 0, result, 1, bytes.length);
        return result;
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) {
            return new byte[] { (byte) (0x30 + value) };
        }
        throw new IllegalArgumentException("fixture integer outside helper range");
    }
static abstract class BaseValue {
        public int x = 3;
    }
static class BadValue extends BaseValue { }
static class GoodValue extends BaseValue { }
static class MehValue extends BaseValue { }
static class DefTypeWrapper {
        public BaseValue value;
    }
static class SimpleNameBasedValidator extends PolymorphicTypeValidator {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubClassName(DatabindContext ctxt, JavaType baseType,
                String subClassName) {
            if (subClassName.equals(BadValue.class.getName())) {
                return Validity.DENIED;
            }
            if (subClassName.equals(GoodValue.class.getName())) {
                return Validity.ALLOWED;
            }
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubType(DatabindContext ctxt, JavaType baseType,
                JavaType subType) {
            return Validity.DENIED;
        }
    }
static class Id953 {
        @JsonProperty("someId")
        public int someId;
    }
static class CauseBlockingSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission permission) throws SecurityException {
            if ("suppressAccessChecks".equals(permission.getName())) {
                throw new SecurityException("Cannot force permission: " + permission);
            }
        }
    }

    void __invoke_testTurkishILetterDeserializationWithEnVpack() throws Exception {
        try {
            testTurkishILetterDeserializationWithEnVpack();
        } finally {
        }
    }


    void __invoke_testTurkishILetterDeserializationWithTrVpack() throws Exception {
        try {
            testTurkishILetterDeserializationWithTrVpack();
        } finally {
        }
    }

}
