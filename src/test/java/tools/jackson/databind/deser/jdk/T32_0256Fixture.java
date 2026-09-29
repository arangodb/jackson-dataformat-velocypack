package tools.jackson.databind.deser.jdk;

import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0256Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final BasicPolymorphicTypeValidator ALLOW_ALL_TYPES =
            BasicPolymorphicTypeValidator.builder().allowIfSubType("").build();
private static final byte[] UNTYPED_LIST = VPackWireFixtureTest.hex(
            "13 0d 45 74 65 78 74 21 1a 18 28 17 04");
private static final byte[] ENUM_SET = VPackWireFixtureTest.hex(
            "13 0d 44 4b 45 59 31 44 4b 45 59 32 02");
private static final byte[] FOUR = VPackWireFixtureTest.hex("34");
private static final byte[] ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] MINUS_SEVEN = VPackWireFixtureTest.hex("20 f9");
private static final byte[] XYZ = VPackWireFixtureTest.hex("43 78 79 7a");
private static final byte[] X_FOUR = VPackWireFixtureTest.hex(
            "14 06 41 78 34 01");
private static final byte[] X_29 = VPackWireFixtureTest.hex(
            "14 07 41 78 28 1d 01");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] STRING_ITERABLE = VPackWireFixtureTest.hex(
            "14 11 46 76 61 6c 75 65 73 13 07 41 61 41 62 02 01");
private static final byte[] BEAN_ITERABLE = VPackWireFixtureTest.hex(
            "14 17 44 6e 75 6d 73 13 0f 14 06 41 78 31 01 "
          + "14 06 41 78 32 01 02 01");
private static final byte[] ONE = VPackWireFixtureTest.hex("13 04 31 01");
private static final byte[] TRUE = VPackWireFixtureTest.hex("13 04 1a 01");
private static final byte[] TREE_SET_WITH_NULL = VPackWireFixtureTest.hex(
            "13 0a 43 61 63 62 18 28 7b 03");
private static final byte[] SINGLETON_THREE = VPackWireFixtureTest.hex(
            "13 09 14 06 41 78 33 01 01");
private static final byte[] SINGLETON_28 = VPackWireFixtureTest.hex(
            "13 0a 14 07 41 78 28 1c 01 01");
private static final byte[] STRING_COLLECTION_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 45 76 61 6c 75 65 44 74 65 73 74 01");
private static final byte[] TYPED_UNMODIFIABLE_SET = VPackWireFixtureTest.hex(
            "13 2e 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 "
          + "55 6e 6d 6f 64 69 66 69 61 62 6c 65 53 65 74 13 05 41 61 01 02");
private static final byte[] EMPTY_OBJECT_ARRAY = VPackWireFixtureTest.hex(
            "13 04 0a 01");

    // Provenance: CollectionDeserializationTest#testUntypedList.
    void testUntypedList() throws Exception {
        Object value = MAPPER.readValue(UNTYPED_LIST, Object.class);
        assertNotNull(value);
        assertInstanceOf(ArrayList.class, value);
        List<?> result = (List<?>) value;

        assertEquals(4, result.size());
        assertEquals("text!", result.get(0));
        assertEquals(Boolean.TRUE, result.get(1));
        assertNull(result.get(2));
        assertEquals(Integer.valueOf(23), result.get(3));
    }

    // Provenance: CollectionDeserializationTest#testHashSet.
    void testHashSet() throws Exception {
        EnumSet<Key> result = MAPPER.readValue(ENUM_SET,
                new TypeReference<EnumSet<Key>>() { });
        assertNotNull(result);
        assertTrue(EnumSet.class.isAssignableFrom(result.getClass()));
        assertEquals(2, result.size());
        assertTrue(result.contains(Key.KEY1));
        assertTrue(result.contains(Key.KEY2));
        assertFalse(result.contains(Key.WHATEVER));
    }

    // Provenance: CollectionDeserializationTest#testImplicitArrays.
    void testImplicitArrays() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();

        List<Integer> ints = mapper.readValue(FOUR, List.class);
        assertEquals(1, ints.size());
        assertEquals(Integer.valueOf(4), ints.get(0));

        List<String> strings = mapper.readValue(ABC,
                new TypeReference<ArrayList<String>>() { });
        assertEquals(1, strings.size());
        assertEquals("abc", strings.get(0));

        int[] intArray = mapper.readValue(MINUS_SEVEN, int[].class);
        assertEquals(1, intArray.length);
        assertEquals(-7, intArray[0]);

        String[] stringArray = mapper.readValue(XYZ, String[].class);
        assertEquals(1, stringArray.length);
        assertEquals("xyz", stringArray[0]);

        List<XBean> xbeanList = mapper.readValue(X_FOUR,
                new TypeReference<List<XBean>>() { });
        assertEquals(1, xbeanList.size());
        assertEquals(XBean.class, xbeanList.get(0).getClass());
        assertEquals(4, xbeanList.get(0).x);

        XBean[] xbeanArray = mapper.readValue(X_29, XBean[].class);
        assertEquals(1, xbeanArray.length);
        assertEquals(XBean.class, xbeanArray[0].getClass());
        assertEquals(29, xbeanArray[0].x);
    }

    // Provenance: CollectionDeserializationTest#testFromEmptyString.
    void testFromEmptyString() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        List<?> result = reader.forType(List.class).readValue(EMPTY_STRING);
        assertNull(result);
    }

    // Provenance: CollectionDeserializationTest#testIterableWithStrings.
    void testIterableWithStrings() throws Exception {
        ListAsIterable value = MAPPER.readValue(STRING_ITERABLE, ListAsIterable.class);
        assertNotNull(value);
        assertNotNull(value.values);
        Iterator<String> it = value.values.iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        assertEquals("b", it.next());
        assertFalse(it.hasNext());
    }

    // Provenance: CollectionDeserializationTest#testIterableWithBeans.
    void testIterableWithBeans() throws Exception {
        ListAsIterableX value = MAPPER.readValue(BEAN_ITERABLE, ListAsIterableX.class);
        assertNotNull(value);
        assertNotNull(value.nums);
        Iterator<XBean> it = value.nums.iterator();
        assertTrue(it.hasNext());
        XBean bean = it.next();
        assertNotNull(bean);
        assertEquals(1, bean.x);
        bean = it.next();
        assertEquals(2, bean.x);
        assertFalse(it.hasNext());
    }

    // Provenance: CollectionDeserializationTest#testJava6Types.
    void testJava6Types() throws Exception {
        Deque<?> deque = MAPPER.readValue(ONE, Deque.class);
        assertNotNull(deque);
        assertEquals(1, deque.size());
        assertInstanceOf(Deque.class, deque);

        NavigableSet<?> set = MAPPER.readValue(TRUE, NavigableSet.class);
        assertEquals(1, set.size());
        assertInstanceOf(NavigableSet.class, set);
    }

    // Provenance: CollectionDeserializationTest#testNullsWithTreeSet.
    void testNullsWithTreeSet() throws Exception {
        try {
            MAPPER.readValue(TREE_SET_WITH_NULL, TreeSet.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            assertTrue(e.getMessage().contains("`java.util.Collection` of type "),
                    e.getMessage());
            assertTrue(e.getMessage().contains(" does not accept `null` values"),
                    e.getMessage());
        }
    }

    // Provenance: CollectionDeserializationTest#testSingletonCollections.
    void testSingletonCollections() throws Exception {
        XBean first = MAPPER.readValue(SINGLETON_THREE,
                new TypeReference<List<XBean>>() { }).get(0);
        assertEquals(3, first.x);

        XBean second = MAPPER.readValue(SINGLETON_28,
                new TypeReference<List<XBean>>() { }).get(0);
        assertEquals(28, second.x);
    }

    // Provenance: CollectionDeserializationTest#testStringCollectionDeserializeInField5522.
    void testStringCollectionDeserializeInField5522() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();

        CustomClassForListField5522 result = mapper.readValue(
                STRING_COLLECTION_PROPERTY, CustomClassForListField5522.class);
        assertEquals(List.of("test"), result.value);
    }

    // Provenance: CollectionDeserializationTest#testUnmodifiableSet.
    void testUnmodifiableSet() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(ALLOW_ALL_TYPES,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
        Set<?> result = mapper.readValue(TYPED_UNMODIFIABLE_SET, Set.class);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // Provenance: CollectionDeserializationTest#testWrapExceptions.
    void testWrapExceptions() throws Exception {
        ObjectReader wrappingReader = MAPPER.readerFor(new TypeReference<List<SomeObject>>() { })
                .with(DeserializationFeature.WRAP_EXCEPTIONS);

        try {
            wrappingReader.readValue(EMPTY_OBJECT_ARRAY);
            fail("Should not pass");
        } catch (JacksonException e) {
            assertEquals("I want to catch this exception", e.getOriginalMessage());
        } catch (RuntimeException e) {
            fail("The RuntimeException should have been wrapped with a DatabindException, got: "
                    + e.getClass());
        }

        ObjectReader noWrapReader = MAPPER.readerFor(new TypeReference<List<SomeObject>>() { })
                .without(DeserializationFeature.WRAP_EXCEPTIONS);
        try {
            noWrapReader.readValue(EMPTY_OBJECT_ARRAY);
            fail("Should not pass");
        } catch (DatabindException e) {
            fail("It should not have wrapped the RuntimeException.");
        } catch (RuntimeException e) {
            assertEquals("I want to catch this exception", e.getMessage());
        }
    }
enum Key {
        KEY1, KEY2, WHATEVER
    }
static class XBean {
        public int x;
    }
static class ListAsIterable {
        public Iterable<String> values;
    }
static class ListAsIterableX {
        public Iterable<XBean> nums;
    }
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    static class CustomStringList5522 extends ArrayList<String> { }
static class CustomClassForListField5522 {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> value;
    }
@JsonDeserialize(using = SomeObjectDeserializer.class)
    static class SomeObject { }
static class SomeObjectDeserializer extends StdDeserializer<SomeObject> {
        SomeObjectDeserializer() {
            super(SomeObject.class);
        }

        @Override
        public SomeObject deserialize(JsonParser parser, DeserializationContext context) {
            throw new RuntimeException("I want to catch this exception");
        }
    }

    void __invoke_testUntypedList() throws Exception {
        try {
            testUntypedList();
        } finally {
        }
    }


    void __invoke_testHashSet() throws Exception {
        try {
            testHashSet();
        } finally {
        }
    }


    void __invoke_testImplicitArrays() throws Exception {
        try {
            testImplicitArrays();
        } finally {
        }
    }


    void __invoke_testFromEmptyString() throws Exception {
        try {
            testFromEmptyString();
        } finally {
        }
    }


    void __invoke_testIterableWithStrings() throws Exception {
        try {
            testIterableWithStrings();
        } finally {
        }
    }


    void __invoke_testIterableWithBeans() throws Exception {
        try {
            testIterableWithBeans();
        } finally {
        }
    }


    void __invoke_testJava6Types() throws Exception {
        try {
            testJava6Types();
        } finally {
        }
    }


    void __invoke_testNullsWithTreeSet() throws Exception {
        try {
            testNullsWithTreeSet();
        } finally {
        }
    }


    void __invoke_testSingletonCollections() throws Exception {
        try {
            testSingletonCollections();
        } finally {
        }
    }


    void __invoke_testStringCollectionDeserializeInField5522() throws Exception {
        try {
            testStringCollectionDeserializeInField5522();
        } finally {
        }
    }


    void __invoke_testUnmodifiableSet() throws Exception {
        try {
            testUnmodifiableSet();
        } finally {
        }
    }


    void __invoke_testWrapExceptions() throws Exception {
        try {
            testWrapExceptions();
        } finally {
        }
    }

}
