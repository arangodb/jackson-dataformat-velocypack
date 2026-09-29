package tools.jackson.databind.deser.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0285F2 {
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

    // Provenance: CustomCollectionMerge4783Test#testCustomMapperReading().
    void testCustomMapperReadingVpack() throws Exception {
        MergeCustomStringList result = MAPPER.readValue(VALUES_CUSTOM_UPDATE,
                MergeCustomStringList.class);
        assertEquals(2, result.values.size());
        assertTrue(result.values.contains("a"));
        assertTrue(result.values.contains("x"));
    }

    // Provenance: CustomCollectionMerge4783Test#testCustomMapperReadingLongArrayList().
    void testCustomMapperReadingLongArrayListVpack() throws Exception {
        MergeMyCustomLongList result = MAPPER.readValue(VALUES_LONG_UPDATE,
                MergeMyCustomLongList.class);
        assertEquals(2, result.values.size());
        assertTrue(result.values.contains(1L));
        assertTrue(result.values.contains(7L));
    }

    // Provenance: CustomCollectionMerge4783Test#failNonMergeInterfaceList().
    void failNonMergeInterfaceListVpack() throws Exception {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(NON_MERGE_INTERFACE, NonMergeCustomStringList.class));
        assertTrue(exception.getMessage().contains(MyListCustom.class.getName()));
    }

    // Provenance: CustomCollectionMerge4783Test#failNonMergeAbstractList().
    void failNonMergeAbstractListVpack() throws Exception {
        InvalidDefinitionException exception = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(EMPTY_ARRAY, MyAbstractStringList.class));
        assertTrue(exception.getMessage().contains(MyAbstractStringList.class.getName()));
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

    void __invoke_testCustomMapperReadingVpack() throws Exception {
        try {
            testCustomMapperReadingVpack();
        } finally {
        }
    }


    void __invoke_testCustomMapperReadingLongArrayListVpack() throws Exception {
        try {
            testCustomMapperReadingLongArrayListVpack();
        } finally {
        }
    }


    void __invoke_failNonMergeInterfaceListVpack() throws Exception {
        try {
            failNonMergeInterfaceListVpack();
        } finally {
        }
    }


    void __invoke_failNonMergeAbstractListVpack() throws Exception {
        try {
            failNonMergeAbstractListVpack();
        } finally {
        }
    }

}
