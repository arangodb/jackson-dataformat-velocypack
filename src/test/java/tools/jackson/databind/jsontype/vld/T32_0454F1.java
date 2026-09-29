package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0454F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] UUID_BINARY = VPackWireFixtureTest.hex(
            "c0 10 90 01 50 98 3c d2 3f b0 96 96 3f 7d 28 e1 7f 72");

    // Provenance: BasicPTVTest#testAllowByBaseClass().
    void testAllowByBaseClassVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(BaseValue.class).build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueA.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);

        byte[] byteValue = valueObject(typed(Byte.class.getName(), integer(4)));
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(byteValue, NumberWrapper.class));
        assertTrue(denied.getMessage().contains("java.lang.Byte"));
        assertTrue(denied.getMessage().contains("as a subtype of"));

        NumberWrapper allowed = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Number.class).build())
                .readValue(byteValue, NumberWrapper.class);
        assertEquals(Byte.valueOf((byte) 4), allowed.value);
    }

    // Provenance: BasicPTVTest#testAllowByBaseClassPattern().
    void testAllowByBaseClassPatternVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Pattern.compile("\\w+\\.jackson\\..+"))
                .build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueA.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(Byte.class.getName(), integer(4))),
                        NumberWrapper.class));
        assertTrue(denied.getMessage().contains("java.lang.Byte"));
        assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: BasicPTVTest#testAllowByBaseClassPrefix().
    void testAllowByBaseClassPrefixVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType("tools.jackson.").build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueA.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(Byte.class.getName(), integer(4))),
                        NumberWrapper.class));
        assertTrue(denied.getMessage().contains("java.lang.Byte"));
        assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: BasicPTVTest#testAllowBySubClass().
    void testAllowBySubClassVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ValueB.class).build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueB.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(ValueA.class.getName(), object("x", integer(43)))),
                        BaseValueWrapper.class));
        assertTrue(denied.getMessage().contains("tools.jackson."));
        assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: BasicPTVTest#testAllowBySubClassPrefix().
    void testAllowBySubClassPrefixVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ValueB.class.getName()).build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueB.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(ValueA.class.getName(), object("x", integer(43)))),
                        BaseValueWrapper.class));
        assertTrue(denied.getMessage().contains("tools.jackson."));
        assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: BasicPTVTest#testAllowBySubClassPattern().
    void testAllowBySubClassPatternVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Pattern.compile(Pattern.quote(ValueB.class.getName()))).build());
        BaseValueWrapper accepted = mapper.readValue(
                valueObject(typed(ValueB.class.getName(), object("x", integer(42)))),
                BaseValueWrapper.class);
        assertEquals(42, accepted.value.x);
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(valueObject(typed(ValueA.class.getName(), object("x", integer(43)))),
                        BaseValueWrapper.class));
        assertTrue(denied.getMessage().contains("tools.jackson."));
        assertTrue(denied.getMessage().contains("as a subtype of"));
    }

    // Provenance: BasicPTVTest#testDenyByBaseClass().
    void testDenyByBaseClassVpack() throws Exception {
        ObjectMapper mapper = defaultMapper(BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(BaseValue.class)
                .denyForExactBaseType(Object.class).build());
        InvalidDefinitionException denied = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(valueObject(
                        typed(ValueA.class.getName(), object("x", integer(15)))), ObjectWrapper.class));
        assertTrue(denied.getMessage().contains("denied resolution"));
    }

    // Provenance: BasicPTVTest#testWithJDKBasicsOk().
    void testWithJDKBasicsOkVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowSubTypesWithExplicitDeserializer().build(),
                        DefaultTyping.NON_FINAL_AND_ENUMS).build();
        byte[] accepted = typed("[Ljava.lang.Object;", oneElementArray(
                string("test"), integer(42), typed("java.net.URL", string("http://localhost")),
                typed("java.util.UUID", UUID_BINARY), typed("[Ljava.lang.Object;", EMPTY_ARRAY)));
        Object[] result = (Object[]) mapper.readValue(accepted, Object.class);
        assertEquals(Object[].class, result.getClass());
        assertEquals("test", result[0]);
        assertEquals(URL.class, result[2].getClass());
        assertEquals(UUID.fromString("90015098-3cd2-3fb0-9696-3f7d28e17f72"), result[3]);

        byte[] dangerous = typed("[Ljava.lang.Object;", oneElementArray(
                typed(Dangerous2539.class.getName(), object("x", integer(1)))));
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(dangerous, Object.class));
        assertTrue(denied.getMessage().contains(Dangerous2539.class.getName()));

        byte[] dangerousArray = typed("[Ljava.lang.Object;", oneElementArray(typed(
                "[L" + Dangerous2539.class.getName() + ";",
                oneElementArray(object("x", integer(1))))));
        InvalidTypeIdException deniedArray = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(dangerousArray, Object.class));
        assertTrue(deniedArray.getMessage().contains("[L" + Dangerous2539.class.getName()));
    }
private static ObjectMapper genericMapper() {
        return VPackMapper.builder().polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("java.util.ArrayList")
                .allowIfSubType("java.util.HashMap")
                .allowIfSubType(SafePayload.class).build()).build();
    }
private static ObjectMapper defaultMapper(BasicPolymorphicTypeValidator validator) {
        return VPackMapper.builder().activateDefaultTyping(validator, DefaultTyping.NON_FINAL).build();
    }
private static byte[] valueObject(byte[] value) {
        return object("value", value);
    }
private static byte[] typedObject(String typeId, byte[] value) {
        return valueObject(typed(typeId, value));
    }
private static byte[] typed(String typeId, byte[] value) {
        byte[] type = string(typeId);
        int length = 5 + type.length + value.length;
        if (length > 255) throw new IllegalArgumentException("fixture exceeds one-byte layout");
        byte[] result = new byte[length];
        result[0] = 0x06;
        result[1] = (byte) length;
        result[2] = 2;
        System.arraycopy(type, 0, result, 3, type.length);
        System.arraycopy(value, 0, result, 3 + type.length, value.length);
        result[length - 2] = 3;
        result[length - 1] = (byte) (3 + type.length);
        return result;
    }
private static byte[] object(String key, byte[] value) {
        byte[] name = string(key);
        int length = 4 + name.length + value.length;
        if (length > 255) throw new IllegalArgumentException("fixture exceeds one-byte layout");
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = 1;
        System.arraycopy(name, 0, result, 3, name.length);
        System.arraycopy(value, 0, result, 3 + name.length, value.length);
        result[length - 1] = 3;
        return result;
    }
private static byte[] oneElementArray(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) bodyLength += value.length;
        int length = 3 + bodyLength + values.length;
        if (length > 255 || values.length > 255) throw new IllegalArgumentException("fixture exceeds one-byte layout");
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
private static byte[] string(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= 126) {
            byte[] result = new byte[bytes.length + 1];
            result[0] = (byte) (0x40 + bytes.length);
            System.arraycopy(bytes, 0, result, 1, bytes.length);
            return result;
        }
        // W2: marker bf is followed by an eight-byte little-endian byte length.
        byte[] result = new byte[bytes.length + 9];
        result[0] = (byte) 0xbf;
        long length = bytes.length;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) (length >>> (8 * i));
        }
        System.arraycopy(bytes, 0, result, 9, bytes.length);
        return result;
    }
private static byte[] integer(int value) {
        if (value >= 0 && value <= 9) return new byte[] { (byte) (0x30 + value) };
        if (value >= 0 && value <= 255) return new byte[] { 0x28, (byte) value };
        throw new IllegalArgumentException("fixture integer outside helper range");
    }
static abstract class BaseValue { public int x; }
static final class ValueA extends BaseValue { public ValueA() { } }
static final class ValueB extends BaseValue { public ValueB() { } }
static final class BaseValueWrapper { public BaseValue value; }
static final class ObjectWrapper { public Object value; }
static final class NumberWrapper { public Number value; }
static final class Dangerous2539 { public int x; }
static final class SafePayload { public String data; public SafePayload() { } public SafePayload(String d) { data = d; } }
static final class EvilGadget {
        static int INSTANTIATIONS;
        public String secret;
        public EvilGadget() { ++INSTANTIATIONS; }
    }
static final class Container {
        @com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS,
                include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;
    }

    void __invoke_testAllowByBaseClassVpack() throws Exception {
        try {
            testAllowByBaseClassVpack();
        } finally {
        }
    }


    void __invoke_testAllowByBaseClassPatternVpack() throws Exception {
        try {
            testAllowByBaseClassPatternVpack();
        } finally {
        }
    }


    void __invoke_testAllowByBaseClassPrefixVpack() throws Exception {
        try {
            testAllowByBaseClassPrefixVpack();
        } finally {
        }
    }


    void __invoke_testAllowBySubClassVpack() throws Exception {
        try {
            testAllowBySubClassVpack();
        } finally {
        }
    }


    void __invoke_testAllowBySubClassPrefixVpack() throws Exception {
        try {
            testAllowBySubClassPrefixVpack();
        } finally {
        }
    }


    void __invoke_testAllowBySubClassPatternVpack() throws Exception {
        try {
            testAllowBySubClassPatternVpack();
        } finally {
        }
    }


    void __invoke_testDenyByBaseClassVpack() throws Exception {
        try {
            testDenyByBaseClassVpack();
        } finally {
        }
    }


    void __invoke_testWithJDKBasicsOkVpack() throws Exception {
        try {
            testWithJDKBasicsOkVpack();
        } finally {
        }
    }

}
