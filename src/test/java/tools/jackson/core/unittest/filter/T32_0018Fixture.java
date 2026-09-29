package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0018Fixture {

    void multipleMatchFilteringWithPath1() throws Exception {
        FilteredResult result = filtered(SIMPLE_WITH_DECIMAL,
                new NameMatchFilter("value0", "value2"),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("ob", Map.of("value0", 2, "value2", 0.25)), result.value());
        assertEquals(2, result.matchCount());
    }

    void multipleMatchFilteringWithPath2() throws Exception {
        FilteredResult result = filtered(SIMPLE,
                new NameMatchFilter("b", "value"),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("ob", Map.of("value", 3), "b", true), result.value());
        assertEquals(2, result.matchCount());
    }

    void includeNonNullWithNestedObjectContext() throws Exception {
        try (FilteringParserDelegate parser = newFilteredParser(NESTED_OBJECT,
                new TokenFilter() { }, Inclusion.INCLUDE_NON_NULL, true)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.streamReadContext().inObject());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());

            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.streamReadContext().inObject());
            assertEquals("a", parser.currentName());

            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());

            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());

            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void includeNonNullWithBufferedIncludeAllObject() throws Exception {
        FilteredResult result = filtered(NESTED_OBJECT, new StrictNameMatchFilter("a"),
                Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(Map.of("a", Map.of("b", 1)), result.value());
    }

    void indexMatchWithPath1() throws Exception {
        FilteredResult result = filtered(SIMPLE, new IndexMatchFilter(1),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("array", List.of(2)), result.value());
        assertEquals(1, result.matchCount());

        result = filtered(SIMPLE, new IndexMatchFilter(0),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("array", List.of(1)), result.value());
        assertEquals(1, result.matchCount());
    }

    void indexMatchWithPath2() throws Exception {
        FilteredResult result = filtered(SIMPLE, new IndexMatchFilter(0, 1),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("array", List.of(1, 2)), result.value());
        assertEquals(2, result.matchCount());

        result = filtered(INDEX_MATCH_INPUT, new IndexMatchFilter(1, 3),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("array", List.of(2, 4), "b", List.of(2)), result.value());
        assertEquals(3, result.matchCount());
    }

    void includeEmptyObjectIfNotFiltered() throws Exception {
        FilteredResult result = filtered(EMPTY_OBJECTS, INCLUDE_EMPTY_IF_NOT_FILTERED,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("empty_object", Map.of()), result.value());
    }

    void includeEmptyObject() throws Exception {
        FilteredResult result = filtered(EMPTY_OBJECTS, INCLUDE_EMPTY,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("empty_object", Map.of(), "filtered_object", Map.of()), result.value());
    }

    void includeEmptyArrayInObjectIfNotFiltered() throws Exception {
        FilteredResult result = filtered(OBJECTS_WITH_ARRAYS, INCLUDE_EMPTY_IF_NOT_FILTERED,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("object_with_empty_array", Map.of("foo", List.of())), result.value());
    }

    void includeEmptyArrayInObject() throws Exception {
        FilteredResult result = filtered(OBJECTS_WITH_ARRAYS, INCLUDE_EMPTY,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of(
                "object_with_empty_array", Map.of("foo", List.of()),
                "object_with_filtered_array", Map.of("foo", List.of())), result.value());
    }

    void includeEmptyObjectInArrayIfNotFiltered() throws Exception {
        FilteredResult result = filtered(ARRAYS_WITH_OBJECTS, INCLUDE_EMPTY_IF_NOT_FILTERED,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("array_with_empty_object", List.of(Map.of())), result.value());
    }

    void includeEmptyObjectInArray() throws Exception {
        FilteredResult result = filtered(ARRAYS_WITH_OBJECTS, INCLUDE_EMPTY,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of(
                "array_with_empty_object", List.of(Map.of()),
                "array_with_filtered_object", List.of(Map.of())), result.value());
    }
private static final TokenFilter INCLUDE_EMPTY_IF_NOT_FILTERED = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return !contentsFiltered;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };
private static final TokenFilter INCLUDE_EMPTY = new TokenFilter() {
        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return true;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return true;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    };
private static final byte[] SIMPLE = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] { 0x34 }))),
            pair("b", new byte[] { 0x1A }));
private static final byte[] SIMPLE_WITH_DECIMAL = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] {
                            0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xD0, 0x3F }))),
            pair("b", new byte[] { 0x1A }));
private static final byte[] INDEX_MATCH_INPUT = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(
                    new byte[] { 0x31 }, new byte[] { 0x32 }, new byte[] { 0x33 },
                    new byte[] { 0x34 }, new byte[] { 0x35 })),
            pair("b", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 }, new byte[] { 0x33 })));
private static final byte[] NESTED_OBJECT = compactObject(
            pair("a", compactObject(pair("b", new byte[] { 0x31 }))));
private static final byte[] EMPTY_OBJECTS = compactObject(
            pair("empty_object", new byte[] { 0x0A }),
            pair("filtered_object", compactObject(pair("foo", new byte[] { 0x35 }))));
private static final byte[] OBJECTS_WITH_ARRAYS = compactObject(
            pair("object_with_empty_array", compactObject(
                    pair("foo", new byte[] { 0x01 }))),
            pair("object_with_filtered_array", compactObject(
                    pair("foo", compactArray(new byte[] { 0x35 })))));
private static final byte[] ARRAYS_WITH_OBJECTS = compactObject(
            pair("array_with_empty_object", compactArray(new byte[] { 0x0A })),
            pair("array_with_filtered_object", compactArray(
                    compactObject(pair("foo", new byte[] { 0x35 })))));
private static FilteringParserDelegate newFilteredParser(byte[] input, TokenFilter filter,
            Inclusion inclusion, boolean multipleMatches) {
        return new FilteringParserDelegate(new VPackFactory().createParser(
                ObjectReadContext.empty(), input), filter, inclusion, multipleMatches);
    }
private static FilteredResult filtered(byte[] input, TokenFilter filter,
            Inclusion inclusion, boolean multipleMatches) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringParserDelegate parser = newFilteredParser(input, filter, inclusion, multipleMatches);
        try (parser; JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        return new FilteredResult(new VPackMapper().readValue(output.toByteArray(), Object.class),
                parser.getMatchCount());
    }
private static byte[] compactObject(byte[]... pairs) {
        return compact(0x14, pairs);
    }
private static byte[] compactArray(byte[]... values) {
        return compact(0x13, values);
    }
private static byte[] compact(int marker, byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) {
            bodyLength += value.length;
        }
        int length = 1 + 1 + bodyLength + 1;
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        result[1] = (byte) length;
        int offset = 2;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        result[offset] = (byte) values.length;
        return result;
    }
private static byte[] pair(String name, byte[] value) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[1 + nameBytes.length + value.length];
        result[0] = (byte) (0x40 + nameBytes.length);
        System.arraycopy(nameBytes, 0, result, 1, nameBytes.length);
        System.arraycopy(value, 0, result, 1 + nameBytes.length, value.length);
        return result;
    }
private record FilteredResult(Object value, int matchCount) { }
private static final class NameMatchFilter extends TokenFilter {
        private final Set<String> names;

        NameMatchFilter(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
        }

        @Override
        public TokenFilter includeElement(int index) {
            return this;
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return names.contains(name) ? TokenFilter.INCLUDE_ALL : this;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    }
private static final class StrictNameMatchFilter extends TokenFilter {
        private final Set<String> names;

        StrictNameMatchFilter(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return names.contains(name) ? TokenFilter.INCLUDE_ALL : null;
        }
    }
private static final class IndexMatchFilter extends TokenFilter {
        private final Set<Integer> indices;

        IndexMatchFilter(int... indices) {
            this.indices = new HashSet<>();
            for (int index : indices) {
                this.indices.add(index);
            }
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return this;
        }

        @Override
        public TokenFilter includeElement(int index) {
            return indices.contains(index) ? TokenFilter.INCLUDE_ALL : null;
        }

        @Override
        protected boolean _includeScalar() {
            return false;
        }
    }

    void __invoke_multipleMatchFilteringWithPath1() throws Exception {
        try {
            multipleMatchFilteringWithPath1();
        } finally {
        }
    }


    void __invoke_multipleMatchFilteringWithPath2() throws Exception {
        try {
            multipleMatchFilteringWithPath2();
        } finally {
        }
    }


    void __invoke_includeNonNullWithNestedObjectContext() throws Exception {
        try {
            includeNonNullWithNestedObjectContext();
        } finally {
        }
    }


    void __invoke_includeNonNullWithBufferedIncludeAllObject() throws Exception {
        try {
            includeNonNullWithBufferedIncludeAllObject();
        } finally {
        }
    }


    void __invoke_indexMatchWithPath1() throws Exception {
        try {
            indexMatchWithPath1();
        } finally {
        }
    }


    void __invoke_indexMatchWithPath2() throws Exception {
        try {
            indexMatchWithPath2();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectIfNotFiltered() throws Exception {
        try {
            includeEmptyObjectIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyObject() throws Exception {
        try {
            includeEmptyObject();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayInObjectIfNotFiltered() throws Exception {
        try {
            includeEmptyArrayInObjectIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayInObject() throws Exception {
        try {
            includeEmptyArrayInObject();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectInArrayIfNotFiltered() throws Exception {
        try {
            includeEmptyObjectInArrayIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyObjectInArray() throws Exception {
        try {
            includeEmptyObjectInArray();
        } finally {
        }
    }

}
