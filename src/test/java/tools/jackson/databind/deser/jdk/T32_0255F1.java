package tools.jackson.databind.deser.jdk;

import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0255F1 {
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

    // Provenance: CollectionDeserializationTest#testAbstractListAndSet.
    void testAbstractListAndSet() throws Exception {
        ListAsAbstract list = MAPPER.readValue(ABSTRACT_COLLECTIONS, ListAsAbstract.class);
        assertEquals(2, list.values.size());
        assertEquals(ArrayList.class, list.values.getClass());

        SetAsAbstract set = MAPPER.readValue(ABSTRACT_COLLECTIONS, SetAsAbstract.class);
        assertEquals(2, set.values.size());
        assertEquals(java.util.HashSet.class, set.values.getClass());
    }

    // Provenance: CollectionDeserializationTest#testArrayBlockingQueue.
    void testArrayBlockingQueue() throws Exception {
        ArrayBlockingQueue<?> queue = MAPPER.readValue(INTEGER_QUEUE,
                ArrayBlockingQueue.class);
        assertNotNull(queue);
        assertEquals(3, queue.size());
        assertEquals(Integer.valueOf(1), queue.take());
        assertEquals(Integer.valueOf(2), queue.take());
        assertEquals(Integer.valueOf(3), queue.take());
    }

    // Provenance: CollectionDeserializationTest#testArrayIndexForExceptions1.
    void testArrayIndexForExceptions1() throws Exception {
        MismatchedInputException failure = assertThrowsMismatched(BAD_ENUM_ARRAY, Key[].class);
        assertTrue(failure.getMessage().contains("Cannot deserialize value"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("from Boolean value"), failure.getMessage());
        assertEquals(1, failure.getPath().size());
        assertEquals(1, failure.getPath().get(0).getIndex());
    }

    // Provenance: CollectionDeserializationTest#testArrayIndexForExceptions2.
    void testArrayIndexForExceptions2() throws Exception {
        MismatchedInputException failure = assertThrowsMismatched(BAD_STRING_ARRAY,
                String[].class);
        assertTrue(failure.getMessage().contains("Cannot deserialize value"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("from Object value"), failure.getMessage());
        assertEquals(1, failure.getPath().size());
        assertEquals(1, failure.getPath().get(0).getIndex());
    }

    // Provenance: CollectionDeserializationTest#testArrayIndexForExceptions3.
    void testArrayIndexForExceptions3() throws Exception {
        MismatchedInputException failure = assertThrowsMismatched(BAD_KEY_LIST,
                KeyListBean.class);
        assertTrue(failure.getMessage().contains("Cannot deserialize value"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("from Boolean value"), failure.getMessage());
        assertEquals(2, failure.getPath().size());
        assertEquals(-1, failure.getPath().get(0).getIndex());
        assertEquals("keys", failure.getPath().get(0).getPropertyName());
        assertEquals(1, failure.getPath().get(1).getIndex());
        assertNull(failure.getPath().get(1).getPropertyName());
    }

    // Provenance: CollectionDeserializationTest#testCustomDeserializer.
    void testCustomDeserializer() throws Exception {
        CustomList result = MAPPER.readValue(CUSTOM_VALUE, CustomList.class);
        assertEquals(1, result.size());
        assertEquals("abc", result.get(0));
    }

    // Provenance: CollectionDeserializationTest#testCustomNumberCollectionDeserialize5522.
    void testCustomNumberCollectionDeserialize5522() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();

        CustomClassForNumber5522 result = mapper.readValue(SINGLE_NUMBER_PROPERTY,
                CustomClassForNumber5522.class);
        assertEquals(1, result.value.size());
        assertEquals(Integer.valueOf(1), result.value.get(0));
    }

    // Provenance: CollectionDeserializationTest#testCustomStringCollectionDeserialize5522.
    void testCustomStringCollectionDeserialize5522() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();

        CustomClassForString5522 result = mapper.readValue(SINGLE_STRING_PROPERTY,
                CustomClassForString5522.class);
        assertEquals(1, result.value.size());
        assertEquals("test", result.value.get(0));
    }

    // Provenance: CollectionDeserializationTest#testExactStringCollection.
    void testExactStringCollection() throws Exception {
        List<String> result = MAPPER.readValue(EXACT_STRING_ARRAY,
                new TypeReference<ArrayList<String>>() { });
        assertNotNull(result);
        assertEquals(ArrayList.class, result.getClass());
        assertEquals(2, result.size());
        assertEquals("a", result.get(0));
        assertEquals("b", result.get(1));
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

    void __invoke_testAbstractListAndSet() throws Exception {
        try {
            testAbstractListAndSet();
        } finally {
        }
    }


    void __invoke_testArrayBlockingQueue() throws Exception {
        try {
            testArrayBlockingQueue();
        } finally {
        }
    }


    void __invoke_testArrayIndexForExceptions1() throws Exception {
        try {
            testArrayIndexForExceptions1();
        } finally {
        }
    }


    void __invoke_testArrayIndexForExceptions2() throws Exception {
        try {
            testArrayIndexForExceptions2();
        } finally {
        }
    }


    void __invoke_testArrayIndexForExceptions3() throws Exception {
        try {
            testArrayIndexForExceptions3();
        } finally {
        }
    }


    void __invoke_testCustomDeserializer() throws Exception {
        try {
            testCustomDeserializer();
        } finally {
        }
    }


    void __invoke_testCustomNumberCollectionDeserialize5522() throws Exception {
        try {
            testCustomNumberCollectionDeserialize5522();
        } finally {
        }
    }


    void __invoke_testCustomStringCollectionDeserialize5522() throws Exception {
        try {
            testCustomStringCollectionDeserialize5522();
        } finally {
        }
    }


    void __invoke_testExactStringCollection() throws Exception {
        try {
            testExactStringCollection();
        } finally {
        }
    }

}
