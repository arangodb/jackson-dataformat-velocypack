package tools.jackson.databind.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.databind.util.ArrayBuilders.BooleanBuilder;
import tools.jackson.databind.util.ArrayBuilders.ByteBuilder;
import tools.jackson.databind.util.ArrayBuilders.DoubleBuilder;
import tools.jackson.databind.util.ArrayBuilders.FloatBuilder;
import tools.jackson.databind.util.ArrayBuilders.IntBuilder;
import tools.jackson.databind.util.ArrayBuilders.LongBuilder;
import tools.jackson.databind.util.ArrayBuilders.ShortBuilder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0621F0 {
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: ArrayBuildersTest#testInsertInListNoDup().
    void insertInListNoDupVpack() throws Exception {
        String[] values = { "me", "you", "him" };
        assertArrayEquals(new String[] { "you", "me", "him" },
                ArrayBuilders.insertInListNoDup(values, "you"));
        assertArrayEquals(new String[] { "me", "you", "him" },
                ArrayBuilders.insertInListNoDup(values, "me"));
        assertArrayEquals(new String[] { "him", "me", "you" },
                ArrayBuilders.insertInListNoDup(values, "him"));
        String[] inserted = ArrayBuilders.insertInListNoDup(values, "foobar");
        assertArrayEquals(new String[] { "foobar", "me", "you", "him" }, inserted);
        assertEquals(List.of("foobar", "me", "you", "him"),
                MAPPER.readValue(MAPPER.writeValueAsBytes(inserted), new TypeReference<List<String>>() { }));
    }

    // Provenance: ArrayBuildersTest#testBuilderAccess().
    void builderAccessVpack() {
        ArrayBuilders builders = new ArrayBuilders();
        BooleanBuilder booleans = builders.getBooleanBuilder();
        assertNotNull(booleans);
        assertSame(booleans, builders.getBooleanBuilder());
        ByteBuilder bytes = builders.getByteBuilder();
        assertNotNull(bytes);
        assertSame(bytes, builders.getByteBuilder());
        ShortBuilder shorts = builders.getShortBuilder();
        assertNotNull(shorts);
        assertSame(shorts, builders.getShortBuilder());
        IntBuilder ints = builders.getIntBuilder();
        assertNotNull(ints);
        assertSame(ints, builders.getIntBuilder());
        LongBuilder longs = builders.getLongBuilder();
        assertNotNull(longs);
        assertSame(longs, builders.getLongBuilder());
        FloatBuilder floats = builders.getFloatBuilder();
        assertNotNull(floats);
        assertSame(floats, builders.getFloatBuilder());
        DoubleBuilder doubles = builders.getDoubleBuilder();
        assertNotNull(doubles);
        assertSame(doubles, builders.getDoubleBuilder());
    }

    // Provenance: ArrayBuildersTest#testArrayComparator().
    void arrayComparatorVpack() {
        int[] expected = { 3, 4, 5 };
        Object comparator = ArrayBuilders.getArrayComparator(expected);
        assertFalse(comparator.equals(null));
        assertTrue(comparator.equals(expected));
        assertTrue(comparator.equals(new int[] { 3, 4, 5 }));
        assertFalse(comparator.equals(new int[] { 5 }));
        assertFalse(comparator.equals(new int[] { 3, 4 }));
        assertFalse(comparator.equals(new int[] { 3, 5, 4 }));
        assertFalse(comparator.equals(new int[] { 3, 4, 5, 6 }));
        assertEquals(List.of(3, 4, 5), MAPPER.readValue(MAPPER.writeValueAsBytes(expected),
                new TypeReference<List<Integer>>() { }));
    }

    // Provenance: ArrayBuildersTest#testArraySet().
    void arraySetVpack() throws Exception {
        HashSet<String> values = ArrayBuilders.arrayToSet(new String[] { "foo", "bar" });
        assertEquals(2, values.size());
        assertEquals(new HashSet<>(Arrays.asList("bar", "foo")), values);
        assertEquals(values, MAPPER.readValue(MAPPER.writeValueAsBytes(values),
                new TypeReference<HashSet<String>>() { }));
    }
interface Settings { }
record MessageWrapper<T extends Settings>(T settings) { }
static class WrapperHolder { MessageWrapper<?> wrapper; }
static class NumberBox<T extends Number> { public T value; NumberBox() { } NumberBox(T value) { this.value = value; } }
static class NumberBoxHolder { NumberBox<?> box; }

    void __invoke_insertInListNoDupVpack() throws Exception {
        try {
            insertInListNoDupVpack();
        } finally {
        }
    }


    void __invoke_builderAccessVpack() throws Exception {
        try {
            builderAccessVpack();
        } finally {
        }
    }


    void __invoke_arrayComparatorVpack() throws Exception {
        try {
            arrayComparatorVpack();
        } finally {
        }
    }


    void __invoke_arraySetVpack() throws Exception {
        try {
            arraySetVpack();
        } finally {
        }
    }

}
