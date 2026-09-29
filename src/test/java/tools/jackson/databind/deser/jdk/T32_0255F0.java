package tools.jackson.databind.deser.jdk;

import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0255F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] INTEGER_QUEUE = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] ABSTRACT_COLLECTIONS = VPackWireFixtureTest.hex(
            "14 15 46 76 61 6c 75 65 73 13 0b 43 66 6f 6f 43 62 61 72 02 01");
private static final byte[] BAD_ENUM_ARRAY = VPackWireFixtureTest.hex(
            "13 09 44 4b 45 59 32 19 02");
private static final byte[] BAD_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 08 43 78 79 7a 0a 02");
private static final byte[] BAD_KEY_LIST = VPackWireFixtureTest.hex(
            "14 11 44 6b 65 79 73 13 09 44 4b 45 59 32 19 02 01");
private static final byte[] CUSTOM_VALUE = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] SINGLE_NUMBER_PROPERTY = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 31 01");
private static final byte[] SINGLE_STRING_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 44 74 65 73 74 01");
private static final byte[] EXACT_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 07 41 61 41 62 02");

    // Provenance: ClassDeserNoStaticInitTest#classValueDoesNotTriggerStaticInitializer.
    void classValueDoesNotTriggerStaticInitializer() throws Exception {
        assertFalse(InitFlag.gadgetInitialized);

        Holder holder = MAPPER.readValue(classValueObject("type", Gadget.class.getName()),
                Holder.class);

        assertEquals(Gadget.class, holder.type);
        assertFalse(InitFlag.gadgetInitialized,
                "Deserializing a Class value must not run the target class static initializer");
    }

    // Provenance: ClassDeserNoStaticInitTest#polymorphicInstantiationTriggersStaticInitializer.
    void polymorphicInstantiationTriggersStaticInitializer() throws Exception {
        assertFalse(SubInitFlag.subInitialized);

        Base result = MAPPER.readValue(polymorphicObject(Sub.class.getName()), Base.class);

        assertEquals(Sub.class, result.getClass());
        assertEquals(42, ((Sub) result).value);
        assertTrue(SubInitFlag.subInitialized,
                "Instantiating a polymorphic subtype must run its static initializer");
    }
private static MismatchedInputException assertThrowsMismatched(byte[] input,
            Class<?> target) {
        try {
            MAPPER.readValue(input, target);
        } catch (MismatchedInputException e) {
            return e;
        } catch (Exception e) {
            throw new AssertionError("Expected MismatchedInputException", e);
        }
        throw new AssertionError("Expected MismatchedInputException");
    }
private static byte[] classValueObject(String property, String className) {
        return compactObject(compactString(property), compactString(className));
    }
private static byte[] polymorphicObject(String className) {
        byte[] body = concat(compactString("@class"), compactString(className),
                compactString("value"), new byte[] { 0x28, 0x2a });
        return compactObjectBody(body, 2);
    }
private static byte[] compactObject(byte[] key, byte[] value) {
        return compactObjectBody(concat(key, value), 1);
    }
private static byte[] compactObjectBody(byte[] body, int count) {
        int length = 1 + 1 + body.length + 1;
        byte[] result = new byte[length];
        result[0] = 0x14;
        result[1] = (byte) length;
        System.arraycopy(body, 0, result, 2, body.length);
        result[length - 1] = (byte) count;
        return result;
    }
private static byte[] compactString(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 126) {
            throw new IllegalArgumentException("test fixture string exceeds compact length");
        }
        byte[] result = new byte[bytes.length + 1];
        result[0] = (byte) (0x40 + bytes.length);
        System.arraycopy(bytes, 0, result, 1, bytes.length);
        return result;
    }
private static byte[] concat(byte[]... values) {
        int length = 0;
        for (byte[] value : values) {
            length += value.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }
enum Key { KEY1, KEY2, WHATEVER }
static class KeyListBean {
        public List<Key> keys;
    }
@JsonDeserialize(using = ListDeserializer.class)
    static class CustomList extends java.util.LinkedList<String> { }
static class ListDeserializer extends StdDeserializer<CustomList> {
        ListDeserializer() { super(CustomList.class); }

        @Override
        public CustomList deserialize(JsonParser parser, DeserializationContext context) {
            CustomList result = new CustomList();
            result.add(parser.getString());
            return result;
        }
    }
static class ListAsAbstract {
        public AbstractList<String> values;
    }
static class SetAsAbstract {
        public AbstractSet<String> values;
    }
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    static class CustomNumberList5522 extends ArrayList<Number> { }
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    static class CustomStringList5522 extends ArrayList<String> { }
static class CustomClassForNumber5522 {
        private CustomNumberList5522 value;

        public CustomNumberList5522 getValue() { return value; }
        public void setValue(CustomNumberList5522 value) { this.value = value; }
    }
static class CustomClassForString5522 {
        private CustomStringList5522 value;

        public CustomStringList5522 getValue() { return value; }
        public void setValue(CustomStringList5522 value) { this.value = value; }
    }
static class InitFlag {
        static volatile boolean gadgetInitialized;
    }
static class Gadget {
        static {
            InitFlag.gadgetInitialized = true;
        }
    }
static class Holder {
        public Class<?> type;
    }
static class SubInitFlag {
        static volatile boolean subInitialized;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static class Base { }
static class Sub extends Base {
        static {
            SubInitFlag.subInitialized = true;
        }

        public int value;
    }

    void __invoke_classValueDoesNotTriggerStaticInitializer() throws Exception {
        try {
            classValueDoesNotTriggerStaticInitializer();
        } finally {
        }
    }


    void __invoke_polymorphicInstantiationTriggersStaticInitializer() throws Exception {
        try {
            polymorphicInstantiationTriggersStaticInitializer();
        } finally {
        }
    }

}
