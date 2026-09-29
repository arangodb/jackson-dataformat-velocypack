package tools.jackson.databind.deser.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0273Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Object.class).build(),
                    DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
            .build();
private static final byte[] EMPTY_SET = VPackWireFixtureTest.hex(
            "06 25 02 5e 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 45 6d 70 74 79 53 65 74 01 03 22");
private static final byte[] SINGLETON_LIST = VPackWireFixtureTest.hex(
            "06 32 02 63 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 69 6e 67 6c 65 74 6f 6e 4c 69 73 74 02 09 46 54 68 65 4f 6e 65 03 27");
private static final byte[] SINGLETON_MAP = VPackWireFixtureTest.hex(
            "0b 37 02 46 40 63 6c 61 73 73 62 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 69 6e 67 6c 65 74 6f 6e 4d 61 70 43 66 6f 6f 43 62 61 72 03 2d");
private static final byte[] SINGLETON_SET = VPackWireFixtureTest.hex(
            "06 31 02 62 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 69 6e 67 6c 65 74 6f 6e 53 65 74 02 09 46 54 68 65 4f 6e 65 03 26");
private static final byte[] UNMODIFIABLE_LIST = VPackWireFixtureTest.hex(
            "06 4a 02 72 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 55 6e 6d 6f 64 69 66 69 61 62 6c 65 52 61 6e 64 6f 6d 41 63 63 65 73 73 4c 69 73 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 36");
private static final byte[] UNMODIFIABLE_LINKED_LIST = VPackWireFixtureTest.hex(
            "06 3e 02 66 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 55 6e 6d 6f 64 69 66 69 61 62 6c 65 4c 69 73 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 2a");
private static final byte[] UNMODIFIABLE_SET = VPackWireFixtureTest.hex(
            "06 3d 02 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 55 6e 6d 6f 64 69 66 69 61 62 6c 65 53 65 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 29");
private static final byte[] UNMODIFIABLE_MAP = VPackWireFixtureTest.hex(
            "0b 3b 03 46 40 63 6c 61 73 73 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 55 6e 6d 6f 64 69 66 69 61 62 6c 65 4d 61 70 41 61 41 62 41 63 41 64 03 30 34");
private static final byte[] SYNCHRONIZED_COLLECTION = VPackWireFixtureTest.hex(
            "06 44 02 6c 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 79 6e 63 68 72 6f 6e 69 7a 65 64 43 6f 6c 6c 65 63 74 69 6f 6e 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 30");
private static final byte[] SYNCHRONIZED_SET = VPackWireFixtureTest.hex(
            "06 3d 02 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 79 6e 63 68 72 6f 6e 69 7a 65 64 53 65 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 29");
private static final byte[] SYNCHRONIZED_RANDOM_ACCESS_LIST = VPackWireFixtureTest.hex(
            "06 4a 02 72 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 79 6e 63 68 72 6f 6e 69 7a 65 64 52 61 6e 64 6f 6d 41 63 63 65 73 73 4c 69 73 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 36");
private static final byte[] SYNCHRONIZED_LINKED_LIST = VPackWireFixtureTest.hex(
            "06 3e 02 66 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 79 6e 63 68 72 6f 6e 69 7a 65 64 4c 69 73 74 06 12 02 45 66 69 72 73 74 46 73 65 63 6f 6e 64 03 09 03 2a");
private static final byte[] SYNCHRONIZED_MAP = VPackWireFixtureTest.hex(
            "0b 3b 03 46 40 63 6c 61 73 73 65 6a 61 76 61 2e 75 74 69 6c 2e 43 6f 6c 6c 65 63 74 69 6f 6e 73 24 53 79 6e 63 68 72 6f 6e 69 7a 65 64 4d 61 70 41 61 41 62 41 63 41 64 03 30 34");

    // Provenance: JavaUtilCollectionsTypesTest#testEmptySet().
    void testEmptySet() throws Exception {
        Set<?> result = MAPPER.readValue(EMPTY_SET, Set.class);
        assertEquals(Collections.emptySet(), result);
        assertEquals(Collections.emptySet().getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSingletonList().
    void testSingletonList() throws Exception {
        List<?> result = MAPPER.readValue(SINGLETON_LIST, List.class);
        assertEquals(Collections.singletonList("TheOne"), result);
        assertEquals(Collections.singletonList("TheOne").getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSingletonMap().
    void testSingletonMap() throws Exception {
        Map<?, ?> result = MAPPER.readValue(SINGLETON_MAP, Map.class);
        assertEquals(Collections.singletonMap("foo", "bar"), result);
        assertEquals(Collections.singletonMap("foo", "bar").getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSingletonSet().
    void testSingletonSet() throws Exception {
        Set<?> result = MAPPER.readValue(SINGLETON_SET, Set.class);
        assertEquals(Collections.singleton("TheOne"), result);
        assertEquals(Collections.singleton("TheOne").getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testUnmodifiableList().
    void testUnmodifiableList() throws Exception {
        List<?> result = MAPPER.readValue(UNMODIFIABLE_LIST, List.class);
        assertEquals(List.of("first", "second"), result);
        assertEquals(Collections.unmodifiableList(Arrays.asList("first", "second")).getClass(),
                result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testUnmodifiableListFromLinkedList().
    void testUnmodifiableListFromLinkedList() throws Exception {
        List<String> input = List.of("first", "second");
        List<?> result = MAPPER.readValue(UNMODIFIABLE_LINKED_LIST, List.class);
        assertEquals(input, result);
        assertEquals(Collections.unmodifiableList(new ArrayList<>(input)).getClass(),
                result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testUnmodifiableSet().
    void testUnmodifiableSet() throws Exception {
        Set<?> result = MAPPER.readValue(UNMODIFIABLE_SET, Set.class);
        assertEquals(new LinkedHashSet<>(List.of("first", "second")), result);
        assertEquals(Collections.unmodifiableSet(new LinkedHashSet<>(List.of("first", "second"))).getClass(),
                result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testUnmodifiableMap().
    void testUnmodifiableMap() throws Exception {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("a", "b");
        expected.put("c", "d");
        Map<?, ?> result = MAPPER.readValue(UNMODIFIABLE_MAP, Map.class);
        assertEquals(expected, result);
        assertEquals(Collections.unmodifiableMap(expected).getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSynchronizedCollection().
    void testSynchronizedCollection() throws Exception {
        Collection<?> result = MAPPER.readValue(SYNCHRONIZED_COLLECTION, Collection.class);
        assertTrue(Collection.class.isAssignableFrom(result.getClass()));
        assertEquals(List.of("first", "second"), new ArrayList<>(result));
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSynchronizedSet().
    void testSynchronizedSet() throws Exception {
        Set<String> expected = new LinkedHashSet<>(List.of("first", "second"));
        Set<?> result = MAPPER.readValue(SYNCHRONIZED_SET, Set.class);
        assertEquals(Collections.synchronizedSet(expected), result);
        assertEquals(Collections.synchronizedSet(expected).getClass(), result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSynchronizedListLinked().
    void testSynchronizedListLinked() throws Exception {
        List<?> result = MAPPER.readValue(SYNCHRONIZED_LINKED_LIST, List.class);
        assertEquals(List.of("first", "second"), result);
        assertTrue(List.class.isAssignableFrom(result.getClass()));
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSynchronizedListRandomAccess().
    void testSynchronizedListRandomAccess() throws Exception {
        List<?> result = MAPPER.readValue(SYNCHRONIZED_RANDOM_ACCESS_LIST, List.class);
        assertEquals(List.of("first", "second"), result);
        assertEquals(Collections.synchronizedList(Arrays.asList("first", "second")).getClass(),
                result.getClass());
    }

    // Provenance: JavaUtilCollectionsTypesTest#testSynchronizedMap().
    void testSynchronizedMap() throws Exception {
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("a", "b");
        expected.put("c", "d");
        Map<?, ?> result = MAPPER.readValue(SYNCHRONIZED_MAP, Map.class);
        assertEquals(expected, result);
        assertEquals(Collections.synchronizedMap(expected).getClass(), result.getClass());
    }

    void __invoke_testEmptySet() throws Exception {
        try {
            testEmptySet();
        } finally {
        }
    }


    void __invoke_testSingletonList() throws Exception {
        try {
            testSingletonList();
        } finally {
        }
    }


    void __invoke_testSingletonMap() throws Exception {
        try {
            testSingletonMap();
        } finally {
        }
    }


    void __invoke_testSingletonSet() throws Exception {
        try {
            testSingletonSet();
        } finally {
        }
    }


    void __invoke_testUnmodifiableList() throws Exception {
        try {
            testUnmodifiableList();
        } finally {
        }
    }


    void __invoke_testUnmodifiableListFromLinkedList() throws Exception {
        try {
            testUnmodifiableListFromLinkedList();
        } finally {
        }
    }


    void __invoke_testUnmodifiableSet() throws Exception {
        try {
            testUnmodifiableSet();
        } finally {
        }
    }


    void __invoke_testUnmodifiableMap() throws Exception {
        try {
            testUnmodifiableMap();
        } finally {
        }
    }


    void __invoke_testSynchronizedCollection() throws Exception {
        try {
            testSynchronizedCollection();
        } finally {
        }
    }


    void __invoke_testSynchronizedSet() throws Exception {
        try {
            testSynchronizedSet();
        } finally {
        }
    }


    void __invoke_testSynchronizedListLinked() throws Exception {
        try {
            testSynchronizedListLinked();
        } finally {
        }
    }


    void __invoke_testSynchronizedListRandomAccess() throws Exception {
        try {
            testSynchronizedListRandomAccess();
        } finally {
        }
    }


    void __invoke_testSynchronizedMap() throws Exception {
        try {
            testSynchronizedMap();
        } finally {
        }
    }

}
