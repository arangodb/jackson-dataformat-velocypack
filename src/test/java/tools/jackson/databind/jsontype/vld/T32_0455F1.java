package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0455F1 {

    // Provenance: CustomPTVMatchersTest#testCustomBaseMatchers().
    void testCustomBaseMatchersVpack() throws Exception {
        PolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType((ctxt, base) -> base.getName().startsWith("tools.jackson."))
                .build();
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(validator, DefaultTyping.NON_FINAL).build();

        CustomBase accepted = mapper.readValue(
                valueObject(typed(CustomBad.class.getName(), indexedObject("x", integer(42)))),
                CustomBaseWrapper.class).value;
        assertEquals(CustomBad.class, accepted.getClass());

        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(TimeZone.class.getName(), EMPTY_OBJECT)),
                        TimeZoneWrapper.class));
        assertNotNull(denied.getMessage());
        org.junit.jupiter.api.Assertions.assertTrue(denied.getMessage().contains("TimeZone"));
        org.junit.jupiter.api.Assertions.assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: CustomPTVMatchersTest#testCustomSubtypeMatchers().
    void testCustomSubtypeMatchersVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType((ctxt, sub) -> sub.getSimpleName().endsWith("Good"))
                        .build(), DefaultTyping.NON_FINAL).build();

        ObjectWrapper accepted = mapper.readValue(
                valueObject(typed(CustomGood.class.getName(), indexedObject("x", integer(42)))),
                ObjectWrapper.class);
        assertNotNull(accepted);
        assertEquals(CustomGood.class, accepted.value.getClass());

        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(CustomBad.class.getName(), indexedObject("x", integer(42)))),
                        ObjectWrapper.class));
        assertNotNull(denied.getMessage());
        org.junit.jupiter.api.Assertions.assertTrue(denied.getMessage().contains("CustomBad"));
        org.junit.jupiter.api.Assertions.assertTrue(denied.getMessage().contains("as a subtype of"));
    }
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static ObjectMapper annotatedMapper() {
        return VPackMapper.builder().polymorphicTypeValidator(new BaseTypeValidator()).build();
    }
private static ObjectMapper defaultValidationMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(new BaseTypeValidator(), DefaultTyping.NON_FINAL).build();
    }
private static byte[] annotatedValue(Class<?> type) {
        return indexedObject(
                "@class", string(type.getName()),
                "x", integer(3));
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
private static byte[] indexedObject(String firstName, byte[] firstValue,
            String secondName, byte[] secondValue) {
        return indexedObject(new String[] { firstName, secondName },
                new byte[][] { firstValue, secondValue });
    }
private static byte[] indexedObject(String name, byte[] value) {
        return indexedObject(new String[] { name }, new byte[][] { value });
    }
private static byte[] indexedObject(String[] names, byte[][] values) {
        int bodyLength = 0;
        byte[][] encodedNames = new byte[names.length][];
        for (int i = 0; i < names.length; ++i) {
            encodedNames[i] = string(names[i]);
            bodyLength += encodedNames[i].length + values[i].length;
        }
        int length = 3 + bodyLength + names.length;
        if (length > 255 || names.length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout");
        }
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = (byte) names.length;
        int cursor = 3;
        int index = length - names.length;
        for (int i = 0; i < names.length; ++i) {
            result[index++] = (byte) cursor;
            System.arraycopy(encodedNames[i], 0, result, cursor, encodedNames[i].length);
            cursor += encodedNames[i].length;
            System.arraycopy(values[i], 0, result, cursor, values[i].length);
            cursor += values[i].length;
        }
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
        if (value >= 0 && value <= 255) {
            return new byte[] { 0x28, (byte) value };
        }
        throw new IllegalArgumentException("fixture integer outside helper range");
    }
private static void assertTypeValidationFailure(InvalidTypeIdException exception) {
        assertNotNull(exception.getMessage());
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("as a subtype of"));
    }
private static void assertDefinitionValidationFailure(InvalidDefinitionException exception) {
        assertNotNull(exception.getMessage());
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("denied resolution"));
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("all subtypes of base type"));
    }
static abstract class Base2534 { public int x = 3; }
static class Good2534 extends Base2534 { public Good2534() { } }
static class Bad2534 extends Base2534 { public Bad2534() { } }
static final class ObjectWrapper { public Object value; }
static abstract class CustomBase { public int x = 3; }
static class CustomGood extends CustomBase { public CustomGood() { } }
static class CustomBad extends CustomBase { public CustomBad() { } }
static final class CustomBaseWrapper { public CustomBase value; }
static final class TimeZoneWrapper { public TimeZone value; }
static abstract class BaseValue { public int x = 3; }
static class BadValue extends BaseValue { }
static class GoodValue extends BaseValue { }
static final class AnnotatedGoodWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public GoodValue value;
    }
static final class AnnotatedBadWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public BadValue value;
    }
static final class DefTypeGoodWrapper { public GoodValue value; }
static final class DefTypeBadWrapper { public BadValue value; }
static class BaseTypeValidator extends PolymorphicTypeValidator {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType) {
            Class<?> raw = baseType.getRawClass();
            if (raw == BadValue.class) return Validity.DENIED;
            if (raw == GoodValue.class) return Validity.ALLOWED;
            return Validity.INDETERMINATE;
        }

        @Override
        public Validity validateSubClassName(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType, String subClassName) {
            return Validity.DENIED;
        }

        @Override
        public Validity validateSubType(tools.jackson.databind.DatabindContext ctxt,
                tools.jackson.databind.JavaType baseType, tools.jackson.databind.JavaType subType) {
            return Validity.DENIED;
        }
    }

    void __invoke_testCustomBaseMatchersVpack() throws Exception {
        try {
            testCustomBaseMatchersVpack();
        } finally {
        }
    }


    void __invoke_testCustomSubtypeMatchersVpack() throws Exception {
        try {
            testCustomSubtypeMatchersVpack();
        } finally {
        }
    }

}
