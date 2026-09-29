package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0453F1 {
private static final byte[] CMD_X = VPackWireFixtureTest.hex(
            "0b 0a 01 43 63 6d 64 41 78 03");
private static final byte[] DATA_42 = VPackWireFixtureTest.hex(
            "0b 0b 01 44 64 61 74 61 28 2a 03");
private static final byte[] DATA_HELLO = VPackWireFixtureTest.hex(
            "0b 0f 01 44 64 61 74 61 45 68 65 6c 6c 6f 03");
private static final byte[] SECRET_HACKED = VPackWireFixtureTest.hex(
            "0b 12 01 46 73 65 63 72 65 74 46 68 61 63 6b 65 64 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INT_ARRAY = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] ENUM_ARRAY = VPackWireFixtureTest.hex(
            "02 0a 47 56 41 4c 55 45 5f 41");

    // Provenance: BasicPTVGenericParameterBypassTest#allowedGenericTypeAccepted().
    void allowedGenericTypeAcceptedVpack() throws Exception {
        Object result = genericMapper().readValue(
                typedObject("java.util.ArrayList<" + SafePayload.class.getName() + ">",
                        oneElementArray(DATA_HELLO)), Container.class).value;
        assertNotNull(result);
        assertEquals(ArrayList.class, result.getClass());
        assertEquals("hello", ((SafePayload) ((ArrayList<?>) result).get(0)).data);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#genericTypeIdBypassesAllowlistDenied().
    void genericTypeIdBypassesAllowlistDeniedVpack() throws Exception {
        String evilClass = EvilGadget.class.getName();
        EvilGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> genericMapper().readValue(typedObject(
                        "java.util.ArrayList<" + evilClass + ">",
                        oneElementArray(SECRET_HACKED)), Container.class));
        assertTrue(denied.getMessage().contains(evilClass));
        assertEquals(0, EvilGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#mapValueGadgetDenied().
    void mapValueGadgetDeniedVpack() throws Exception {
        String evilClass = EvilGadget.class.getName();
        EvilGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> genericMapper().readValue(typedObject(
                        "java.util.HashMap<java.lang.String," + evilClass + ">",
                        objectWithK(SECRET_HACKED)), Container.class));
        assertTrue(denied.getMessage().contains("java.lang.String")
                || denied.getMessage().contains(evilClass));
        assertEquals(0, EvilGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#mapKeyGadgetDenied().
    void mapKeyGadgetDeniedVpack() throws Exception {
        String evilClass = EvilGadget.class.getName();
        EvilGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> genericMapper().readValue(typedObject(
                        "java.util.HashMap<" + evilClass + ",java.lang.String>",
                        EMPTY_OBJECT), Container.class));
        assertTrue(denied.getMessage().contains(evilClass));
        assertEquals(0, EvilGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#gadgetArrayAsGenericParameterDenied().
    void gadgetArrayAsGenericParameterDeniedVpack() throws Exception {
        String evilClass = EvilGadget.class.getName();
        String arrayId = "[L" + evilClass + ";";
        EvilGadget.INSTANTIATIONS = 0;

        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> genericMapper().readValue(typedObject(
                        "java.util.ArrayList<" + arrayId + ">",
                        oneElementArray(oneElementArray(SECRET_HACKED))), Container.class));
        assertTrue(denied.getMessage().contains(evilClass));
        assertEquals(0, EvilGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVGenericParameterBypassTest#enumTypeParameterAccepted().
    void enumTypeParameterAcceptedVpack() throws Exception {
        Object result = VPackMapper.builder()
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.").build())
                .build()
                .readValue(typedObject(
                        "java.util.EnumSet<" + NonAllowListedEnum.class.getName() + ">",
                        ENUM_ARRAY), Container.class).value;
        assertEquals(EnumSet.of(NonAllowListedEnum.VALUE_A), result);
    }
private static ObjectMapper mapperWithSafePayloadAndArrays() {
        return VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(SafePayload.class)
                        .allowIfSubTypeIsArray()
                        .build(), DefaultTyping.NON_FINAL)
                .build();
    }
private static ObjectMapper genericMapper() {
        return VPackMapper.builder()
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.util.ArrayList")
                        .allowIfSubType("java.util.HashMap")
                        .allowIfSubType(SafePayload.class)
                        .build())
                .build();
    }
private static byte[] typedObject(String typeId, byte[] value) {
        byte[] typed = wrapperArray(typeId, value);
        int length = 4 + 6 + typed.length;
        if (length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout: " + length);
        }
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = 1;
        byte[] key = VPackWireFixtureTest.hex("45 76 61 6c 75 65");
        System.arraycopy(key, 0, result, 3, key.length);
        System.arraycopy(typed, 0, result, 9, typed.length);
        result[length - 1] = 3;
        return result;
    }
private static byte[] wrapperArray(String typeId, byte[] value) {
        byte[] type = string(typeId);
        int length = 5 + type.length + value.length;
        if (length > 255) {
            throw new IllegalArgumentException("fixture exceeds one-byte layout: " + length);
        }
        byte[] result = new byte[length];
        result[0] = 6;
        result[1] = (byte) length;
        result[2] = 2;
        System.arraycopy(type, 0, result, 3, type.length);
        System.arraycopy(value, 0, result, 3 + type.length, value.length);
        result[length - 2] = 3;
        result[length - 1] = (byte) (3 + type.length);
        return result;
    }
private static byte[] string(String value) {
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        if (utf8.length > 126) {
            throw new IllegalArgumentException("fixture string exceeds short VPack string: " + value);
        }
        byte[] result = new byte[utf8.length + 1];
        result[0] = (byte) (0x40 + utf8.length);
        System.arraycopy(utf8, 0, result, 1, utf8.length);
        return result;
    }
private static byte[] oneElementArray(byte[] element) {
        int length = 4 + element.length;
        byte[] result = new byte[length];
        result[0] = 6;
        result[1] = (byte) length;
        result[2] = 1;
        System.arraycopy(element, 0, result, 3, element.length);
        result[length - 1] = 3;
        return result;
    }
private static byte[] objectWithK(byte[] value) {
        int length = 4 + 2 + value.length;
        byte[] result = new byte[length];
        result[0] = 0x0b;
        result[1] = (byte) length;
        result[2] = 1;
        result[3] = 0x41;
        result[4] = 0x6b;
        System.arraycopy(value, 0, result, 5, value.length);
        result[length - 1] = 3;
        return result;
    }
static final class FakeGadget {
        static int INSTANTIATIONS;
        public String cmd;

        public FakeGadget() {
            ++INSTANTIATIONS;
        }
    }
static final class SafePayload {
        public Object data;
    }
static final class ObjectWrapper {
        public Object value;
    }
static final class EvilGadget {
        static int INSTANTIATIONS;
        public String secret;

        public EvilGadget() {
            ++INSTANTIATIONS;
        }
    }
enum NonAllowListedEnum {
        VALUE_A, VALUE_B
    }
static final class Container {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;
    }

    void __invoke_allowedGenericTypeAcceptedVpack() throws Exception {
        try {
            allowedGenericTypeAcceptedVpack();
        } finally {
        }
    }


    void __invoke_genericTypeIdBypassesAllowlistDeniedVpack() throws Exception {
        try {
            genericTypeIdBypassesAllowlistDeniedVpack();
        } finally {
        }
    }


    void __invoke_mapValueGadgetDeniedVpack() throws Exception {
        try {
            mapValueGadgetDeniedVpack();
        } finally {
        }
    }


    void __invoke_mapKeyGadgetDeniedVpack() throws Exception {
        try {
            mapKeyGadgetDeniedVpack();
        } finally {
        }
    }


    void __invoke_gadgetArrayAsGenericParameterDeniedVpack() throws Exception {
        try {
            gadgetArrayAsGenericParameterDeniedVpack();
        } finally {
        }
    }


    void __invoke_enumTypeParameterAcceptedVpack() throws Exception {
        try {
            enumTypeParameterAcceptedVpack();
        } finally {
        }
    }

}
