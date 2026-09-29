package tools.jackson.databind.deser.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0285F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final byte[] ARRAY_TARGET = VPackWireFixtureTest.hex(
            "14 1e 45 61 72 72 61 79 13 0b 43 4d 72 2e 43 4d 73 2e 02 "
          + "46 6e 75 6d 62 65 72 29 78 03 02");
private static final byte[] ARRAY_UPDATE = VPackWireFixtureTest.hex(
            "14 18 45 61 72 72 61 79 13 0f 46 4d 69 73 74 65 72 44 4d 69 73 73 02 01");
private static final byte[] OBJECT_TARGET = VPackWireFixtureTest.hex(
            "14 2a 46 6f 62 6a 65 63 74 14 0b 41 61 41 31 41 62 41 32 02 "
          + "45 61 72 72 61 79 13 06 31 32 33 03 46 6e 75 6d 62 65 72 28 2a 03");
private static final byte[] OBJECT_UPDATE = VPackWireFixtureTest.hex(
            "14 13 46 6f 62 6a 65 63 74 14 09 41 62 43 78 79 7a 01 01");
private static final byte[] BAG_UPDATE = VPackWireFixtureTest.hex(
            "14 0c 43 62 61 67 13 05 41 62 01 01");
private static final byte[] VALUES_STRING_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 46 76 61 6c 75 65 73 13 05 41 78 01 01");
private static final byte[] VALUES_BAR_UPDATE = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 13 07 43 62 61 72 01 01");
private static final byte[] ABC_UPDATE = VPackWireFixtureTest.hex(
            "14 0c 43 61 62 63 13 05 41 41 01 01");
private static final byte[] VALUES_LONG_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 46 76 61 6c 75 65 73 13 05 28 07 01 01");
private static final byte[] VALUES_CUSTOM_UPDATE = VALUES_STRING_UPDATE;
private static final byte[] NON_MERGE_INTERFACE = VALUES_STRING_UPDATE;
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");

    // Provenance: CollectionMergeTest#testCollectionMerging().
    void testCollectionMergingVpack() throws Exception {
        CollectionWrapper result = MAPPER.readValue(BAG_UPDATE, CollectionWrapper.class);
        assertEquals(2, result.bag.size());
        assertTrue(result.bag.contains("a"));
        assertTrue(result.bag.contains("b"));
    }

    // Provenance: CollectionMergeTest#testListMerging().
    void testListMergingVpack() throws Exception {
        MergedList result = MAPPER.readValue(VALUES_STRING_UPDATE, MergedList.class);
        assertEquals(2, result.values.size());
        assertTrue(result.values.contains("a"));
        assertTrue(result.values.contains("x"));
    }

    // Provenance: CollectionMergeTest#testGenericListMerging().
    void testGenericListMergingVpack() throws Exception {
        Collection<String> values = new ArrayList<>();
        values.add("foo");
        MergedX<Collection<String>> input = new MergedX<>(values);

        MergedX<Collection<String>> result = MAPPER
                .readerFor(new TypeReference<MergedX<Collection<String>>>() { })
                .withValueToUpdate(input)
                .readValue(VALUES_BAR_UPDATE);
        assertSame(input, result);
        assertEquals(2, result.value.size());
        Iterator<String> iterator = result.value.iterator();
        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
    }

    // Provenance: CollectionMergeTest#testEnumSetMerging().
    void testEnumSetMergingVpack() throws Exception {
        MergedEnumSet result = MAPPER.readValue(ABC_UPDATE, MergedEnumSet.class);
        assertEquals(2, result.abc.size());
        assertTrue(result.abc.contains(ABC.B));
        assertTrue(result.abc.contains(ABC.A));
    }
static class CollectionWrapper {
        @JsonMerge
        public Collection<String> bag = new TreeSet<>();
        { bag.add("a"); }
    }
static class MergedList {
        @JsonMerge
        public List<String> values = new ArrayList<>();
        { values.add("a"); }
    }
static class MergedEnumSet {
        @JsonMerge
        public EnumSet<ABC> abc = EnumSet.of(ABC.B);
    }
static class MergedX<T> {
        @JsonMerge
        T value;

        MergedX(T value) { this.value = value; }
        protected MergedX() { }
    }
interface MyListCustom<T> extends List<T> { }
static class MyArrayListCustom<T> extends ArrayList<T> implements MyListCustom<T> { }
static abstract class MyAbstractStringList extends ArrayList<String> {
        MyAbstractStringList() { super(); }
        MyAbstractStringList(int size) { super(size); }
    }
static class MergeCustomStringList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<String> values = new MyArrayListCustom<>();
        { values.add("a"); }
    }
static class MergeMyCustomLongList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<Long> values = new MyArrayListCustom<>();
        { values.add(1L); }
    }
static class NonMergeCustomStringList {
        public MyListCustom<String> values;
    }
private enum ABC { A, B }

    void __invoke_testCollectionMergingVpack() throws Exception {
        try {
            testCollectionMergingVpack();
        } finally {
        }
    }


    void __invoke_testListMergingVpack() throws Exception {
        try {
            testListMergingVpack();
        } finally {
        }
    }


    void __invoke_testGenericListMergingVpack() throws Exception {
        try {
            testGenericListMergingVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetMergingVpack() throws Exception {
        try {
            testEnumSetMergingVpack();
        } finally {
        }
    }

}
