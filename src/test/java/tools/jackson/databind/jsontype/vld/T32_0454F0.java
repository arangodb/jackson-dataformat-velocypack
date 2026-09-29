package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0454F0 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] UUID_BINARY = VPackWireFixtureTest.hex(
            "c0 10 90 01 50 98 3c d2 3f b0 96 96 3f 7d 28 e1 7f 72");

    // Provenance: BasicPTVGenericParameterBypassTest#mapWithAllowedKeyAndValueAccepted().
    void mapWithAllowedKeyAndValueAcceptedVpack() throws Exception {
        String safe = SafePayload.class.getName();
        Object result = genericMapper().readValue(typedObject(
                "java.util.HashMap<" + safe + "," + safe + ">", EMPTY_OBJECT),
                Container.class).value;
        assertNotNull(result);
        assertEquals(java.util.HashMap.class, result.getClass());
    }

    // Provenance: BasicPTVGenericParameterBypassTest#namePrefixAllowsBothContainerAndParameter().
    void namePrefixAllowsBothContainerAndParameterVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.util.ArrayList")
                        .allowIfSubType(T32_0454F0.class.getName())
                        .build())
                .build();
        Object result = mapper.readValue(typedObject(
                "java.util.ArrayList<" + SafePayload.class.getName() + ">",
                oneElementArray(object("data", string("hello")))), Container.class).value;
        assertNotNull(result);
        assertEquals(java.util.ArrayList.class, result.getClass());
        assertEquals("hello", ((SafePayload) ((java.util.ArrayList<?>) result).get(0)).data);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#nestedGenericGadgetDenied().
    void nestedGenericGadgetDeniedVpack() throws Exception {
        String evil = EvilGadget.class.getName();
        EvilGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> genericMapper().readValue(typedObject(
                        "java.util.ArrayList<java.util.ArrayList<" + evil + ">>",
                        oneElementArray(oneElementArray(object("secret", string("hacked"))))),
                        Container.class));
        assertInstanceOf(InvalidTypeIdException.class, denied);
        assertTrue(denied.getMessage().contains(evil));
        assertEquals(0, EvilGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#objectTypeParameterAccepted().
    void objectTypeParameterAcceptedVpack() throws Exception {
        Object result = genericMapper().readValue(typedObject(
                "java.util.ArrayList<java.lang.Object>", EMPTY_ARRAY), Container.class).value;
        assertNotNull(result);
        assertEquals(java.util.ArrayList.class, result.getClass());
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

    void __invoke_mapWithAllowedKeyAndValueAcceptedVpack() throws Exception {
        try {
            mapWithAllowedKeyAndValueAcceptedVpack();
        } finally {
        }
    }


    void __invoke_namePrefixAllowsBothContainerAndParameterVpack() throws Exception {
        try {
            namePrefixAllowsBothContainerAndParameterVpack();
        } finally {
        }
    }


    void __invoke_nestedGenericGadgetDeniedVpack() throws Exception {
        try {
            nestedGenericGadgetDeniedVpack();
        } finally {
        }
    }


    void __invoke_objectTypeParameterAcceptedVpack() throws Exception {
        try {
            objectTypeParameterAcceptedVpack();
        } finally {
        }
    }

}
