package tools.jackson.databind.jsontype.vld;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0453F0 {
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

    // Provenance: BasicPTVArrayComponentBypassTest#directGadgetAndGadgetArrayBothDenied().
    void directGadgetAndGadgetArrayBothDeniedVpack() throws Exception {
        ObjectMapper mapper = mapperWithSafePayloadAndArrays();
        String classId = FakeGadget.class.getName();

        FakeGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException directDenied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(typedObject(classId, CMD_X), ObjectWrapper.class));
        assertTrue(directDenied.getMessage().contains(classId));
        assertEquals(0, FakeGadget.INSTANTIATIONS);

        String arrayId = "[L" + classId + ";";
        FakeGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException arrayDenied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(typedObject(arrayId, oneElementArray(CMD_X)),
                        ObjectWrapper.class));
        assertTrue(arrayDenied.getMessage().contains(arrayId));
        assertEquals(0, FakeGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVArrayComponentBypassTest#nestedGadgetArrayAlsoDenied().
    void nestedGadgetArrayAlsoDeniedVpack() throws Exception {
        ObjectMapper mapper = mapperWithSafePayloadAndArrays();
        String classId = FakeGadget.class.getName();
        String nestedArrayId = "[[L" + classId + ";";

        FakeGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(typedObject(nestedArrayId,
                        oneElementArray(oneElementArray(CMD_X))), ObjectWrapper.class));
        assertTrue(denied.getMessage().contains(nestedArrayId));
        assertEquals(0, FakeGadget.INSTANTIATIONS);
    }

    // Provenance: BasicPTVArrayComponentBypassTest#allowListedConcreteComponentArrayAccepted().
    void allowListedConcreteComponentArrayAcceptedVpack() throws Exception {
        ObjectMapper mapper = mapperWithSafePayloadAndArrays();
        String arrayId = "[L" + SafePayload.class.getName() + ";";

        ObjectWrapper out = mapper.readValue(typedObject(arrayId, oneElementArray(DATA_42)),
                ObjectWrapper.class);
        assertNotNull(out);
        assertInstanceOf(SafePayload[].class, out.value);
        assertEquals(1, ((SafePayload[]) out.value).length);
        assertEquals(42, ((SafePayload[]) out.value)[0].data);
    }

    // Provenance: BasicPTVArrayComponentBypassTest#primitiveComponentArrayAccepted().
    void primitiveComponentArrayAcceptedVpack() throws Exception {
        ObjectWrapper out = mapperWithSafePayloadAndArrays().readValue(
                typedObject("[I", INT_ARRAY), ObjectWrapper.class);
        assertNotNull(out);
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) out.value);
    }

    // Provenance: BasicPTVArrayComponentBypassTest#namePrefixAllowsBothElementAndArray().
    void namePrefixAllowsBothElementAndArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(T32_0453F0.class.getName())
                        .allowIfSubTypeIsArray()
                        .build(), DefaultTyping.NON_FINAL)
                .build();
        String arrayId = "[L" + SafePayload.class.getName() + ";";

        ObjectWrapper out = mapper.readValue(typedObject(arrayId, oneElementArray(DATA_42)),
                ObjectWrapper.class);
        assertEquals(SafePayload[].class, out.value.getClass());
        assertEquals(42, ((SafePayload[]) out.value)[0].data);
    }

    // Provenance: BasicPTVArrayComponentBypassTest#namePrefixDeniesUnmatchedArrayElement().
    void namePrefixDeniesUnmatchedArrayElementVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("nonexistent.package.")
                        .allowIfSubTypeIsArray()
                        .build(), DefaultTyping.NON_FINAL)
                .build();
        String arrayId = "[L" + FakeGadget.class.getName() + ";";

        FakeGadget.INSTANTIATIONS = 0;
        InvalidTypeIdException denied = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(typedObject(arrayId, oneElementArray(CMD_X)),
                        ObjectWrapper.class));
        assertTrue(denied.getMessage().contains(arrayId));
        assertEquals(0, FakeGadget.INSTANTIATIONS);
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

    void __invoke_directGadgetAndGadgetArrayBothDeniedVpack() throws Exception {
        try {
            directGadgetAndGadgetArrayBothDeniedVpack();
        } finally {
        }
    }


    void __invoke_nestedGadgetArrayAlsoDeniedVpack() throws Exception {
        try {
            nestedGadgetArrayAlsoDeniedVpack();
        } finally {
        }
    }


    void __invoke_allowListedConcreteComponentArrayAcceptedVpack() throws Exception {
        try {
            allowListedConcreteComponentArrayAcceptedVpack();
        } finally {
        }
    }


    void __invoke_primitiveComponentArrayAcceptedVpack() throws Exception {
        try {
            primitiveComponentArrayAcceptedVpack();
        } finally {
        }
    }


    void __invoke_namePrefixAllowsBothElementAndArrayVpack() throws Exception {
        try {
            namePrefixAllowsBothElementAndArrayVpack();
        } finally {
        }
    }


    void __invoke_namePrefixDeniesUnmatchedArrayElementVpack() throws Exception {
        try {
            namePrefixDeniesUnmatchedArrayElementVpack();
        } finally {
        }
    }

}
