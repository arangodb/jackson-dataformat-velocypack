package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import tools.jackson.core.filter.JsonPointerBasedFilter;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0017Fixture {

    void allowMultipleMatchesWithoutPath() throws Exception {
        try (FilteringParserDelegate parser = newFilteredParser(STANDARD_WITH_NESTED_VALUE,
                new NameMatchFilter("value"), Inclusion.ONLY_INCLUDE_ALL, true)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value0", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("val", parser.getString());
            assertEquals(3, parser.getMatchCount());
            assertNull(parser.nextToken());
        }
    }

    void allowMultipleMatchesWithPath1() throws Exception {
        try (FilteringParserDelegate parser = newFilteredParser(STANDARD_WITH_NESTED_VALUE,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, true)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("ob", parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(3, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value0", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(2, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("value", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("val", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(3, parser.getMatchCount());
            assertNull(parser.nextToken());
        }
    }

    void allowMultipleMatchesWithPath2() throws Exception {
        FilteredResult result = filtered(STANDARD_WITH_INDEX_MATCH,
                new IndexMatchFilter(1), Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("array", List.of(2), "ob", Map.of("array", List.of(4))),
                result.value());
        assertEquals(2, result.matchCount());
    }

    void basicSingleMatchFilteringWithPath() throws Exception {
        FilteredResult result = filtered(STANDARD_WITH_NESTED_VALUE,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("value", 3)), result.value());
        assertEquals(1, result.matchCount());
    }

    void callbacksFromFilteringParserDelegate1() throws Exception {
        LoggingFilter filter = new LoggingFilter(new JsonPointerBasedFilter("/parent"));
        FilteredResult result = filtered(PARENT_CHILD, filter,
                Inclusion.ONLY_INCLUDE_ALL, true);
        assertEquals(Map.of("child", 1), result.value());
        assertEquals(Arrays.asList("filterStartObject", "includeProperty: parent",
                "filterFinishObject"), filter.log);
    }

    void excludeLastArrayInsideArray() throws Exception {
        FilteredResult result = filtered(NESTED_ARRAYS, INCLUDE_EMPTY_IF_NOT_FILTERED,
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(List.of(List.of()), result.value());
    }

    void excludeObjectAtTheBeginningOfArray() throws Exception {
        FilteredResult result = filtered(OBJECT_AT_BEGINNING,
                new NameMatchFilter("include"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("parent", List.of(Map.of("include", true))), result.value());
    }

    void excludeObjectAtTheEndOfArray() throws Exception {
        FilteredResult result = filtered(OBJECT_AT_END,
                new NameMatchFilter("include"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("parent", List.of(Map.of("include", true))), result.value());
    }

    void excludeObjectInMiddleOfArray() throws Exception {
        FilteredResult result = filtered(OBJECT_IN_MIDDLE,
                new NameMatchFilter("include-1", "include-2"),
                Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("parent", List.of(Map.of("include-1", 1),
                Map.of("include-2", 2))), result.value());
    }

    void includeEmptyArray() throws Exception {
        FilteredResult result = filtered(EMPTY_AND_FILTERED_ARRAY, INCLUDE_EMPTY,
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("empty_array", List.of(), "filtered_array", List.of()), result.value());
    }

    void includeEmptyArrayIfNotFiltered() throws Exception {
        FilteredResult result = filtered(EMPTY_AND_FILTERED_ARRAY,
                INCLUDE_EMPTY_IF_NOT_FILTERED, Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("empty_array", List.of()), result.value());
    }

    void includeEmptyArrayIfNotFilteredAfterFiltered() throws Exception {
        FilteredResult result = filtered(ARRAY_WITH_EMPTY_AND_FILTERED,
                INCLUDE_EMPTY_IF_NOT_FILTERED, Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(List.of(Map.of("empty_array", List.of())), result.value());
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
private static final byte[] STANDARD_WITH_NESTED_VALUE = compactObject(
            pair("a", new byte[] { (byte) 0x28, 0x7B }),
            pair("array", new byte[] { 0x13, 0x05, 0x31, 0x32, 0x02 }),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] { 0x34 }),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 'v', 'a', 'l' }),
            pair("b", new byte[] { 0x1A }));
private static final byte[] STANDARD_WITH_INDEX_MATCH = compactObject(
            pair("a", new byte[] { (byte) 0x28, 0x7B }),
            pair("array", new byte[] { 0x13, 0x05, 0x31, 0x32, 0x02 }),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("array", new byte[] { 0x13, 0x05, 0x33, 0x34, 0x02 }),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 'v', 'a', 'l' }),
            pair("b", new byte[] { 0x1A }));
private static final byte[] EMPTY_AND_FILTERED_ARRAY = compactObject(
            pair("empty_array", new byte[] { 0x01 }),
            pair("filtered_array", new byte[] { 0x13, 0x04, 0x35, 0x01 }));
private static final byte[] ARRAY_WITH_EMPTY_AND_FILTERED = compactArray(
            new byte[] { 0x35 }, EMPTY_AND_FILTERED_ARRAY);
private static final byte[] OBJECT_AT_BEGINNING = parentArray(
            compactObject(pair("exclude", new byte[] { 0x18 }),
                    pair("include", new byte[] { 0x1A })),
            compactObject(pair("include", new byte[] { 0x1A })));
private static final byte[] OBJECT_AT_END = parentArray(
            compactObject(pair("include", new byte[] { 0x1A })),
            compactObject(pair("exclude", new byte[] { 0x18 })));
private static final byte[] OBJECT_IN_MIDDLE = parentArray(
            compactObject(pair("include-1", new byte[] { 0x31 })),
            compactObject(pair("skip", new byte[] { 0x30 })),
            compactObject(pair("include-2", new byte[] { 0x32 })));
private static final byte[] NESTED_ARRAYS = compactArray(
            new byte[] { 0x47, 's', 'k', 'i', 'p', 'p', 'e', 'd' },
            new byte[] { 0x01 },
            compactArray(new byte[] { 0x47, 's', 'k', 'i', 'p', 'p', 'e', 'd' }));
private static final byte[] PARENT_CHILD = compactObject(
            pair("parent", compactObject(pair("child", new byte[] { 0x31 }))));
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
private static byte[] parentArray(byte[]... values) {
        return compactObject(pair("parent", compactArray(values)));
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
private static final class IndexMatchFilter extends TokenFilter {
        private final Set<Integer> indices;

        IndexMatchFilter(int... indices) {
            this.indices = new HashSet<>();
            for (int index : indices) this.indices.add(index);
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
private static final class LoggingFilter extends TokenFilter {
        private final TokenFilter parent;
        private final List<String> log;

        LoggingFilter(TokenFilter parent) {
            this(parent, new ArrayList<>());
        }

        LoggingFilter(TokenFilter parent, List<String> log) {
            this.parent = parent;
            this.log = log;
        }

        private TokenFilter rewrap(TokenFilter filter) {
            if (filter == null || filter == TokenFilter.INCLUDE_ALL) return filter;
            return new LoggingFilter(filter, log);
        }

        @Override
        public TokenFilter includeElement(int index) {
            log.add("includeElement: " + index);
            return rewrap(parent.includeElement(index));
        }

        @Override
        public TokenFilter includeProperty(String name) {
            log.add("includeProperty: " + name);
            return rewrap(parent.includeProperty(name));
        }

        @Override
        public TokenFilter filterStartObject() {
            log.add("filterStartObject");
            return rewrap(parent.filterStartObject());
        }

        @Override
        public TokenFilter filterStartArray() {
            log.add("filterStartArray");
            return rewrap(parent.filterStartArray());
        }

        @Override
        public void filterFinishObject() {
            log.add("filterFinishObject");
            parent.filterFinishObject();
        }

        @Override
        public void filterFinishArray() {
            log.add("filterFinishArray");
            parent.filterFinishArray();
        }
    }

    void __invoke_allowMultipleMatchesWithoutPath() throws Exception {
        try {
            allowMultipleMatchesWithoutPath();
        } finally {
        }
    }


    void __invoke_allowMultipleMatchesWithPath1() throws Exception {
        try {
            allowMultipleMatchesWithPath1();
        } finally {
        }
    }


    void __invoke_allowMultipleMatchesWithPath2() throws Exception {
        try {
            allowMultipleMatchesWithPath2();
        } finally {
        }
    }


    void __invoke_basicSingleMatchFilteringWithPath() throws Exception {
        try {
            basicSingleMatchFilteringWithPath();
        } finally {
        }
    }


    void __invoke_callbacksFromFilteringParserDelegate1() throws Exception {
        try {
            callbacksFromFilteringParserDelegate1();
        } finally {
        }
    }


    void __invoke_excludeLastArrayInsideArray() throws Exception {
        try {
            excludeLastArrayInsideArray();
        } finally {
        }
    }


    void __invoke_excludeObjectAtTheBeginningOfArray() throws Exception {
        try {
            excludeObjectAtTheBeginningOfArray();
        } finally {
        }
    }


    void __invoke_excludeObjectAtTheEndOfArray() throws Exception {
        try {
            excludeObjectAtTheEndOfArray();
        } finally {
        }
    }


    void __invoke_excludeObjectInMiddleOfArray() throws Exception {
        try {
            excludeObjectInMiddleOfArray();
        } finally {
        }
    }


    void __invoke_includeEmptyArray() throws Exception {
        try {
            includeEmptyArray();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayIfNotFiltered() throws Exception {
        try {
            includeEmptyArrayIfNotFiltered();
        } finally {
        }
    }


    void __invoke_includeEmptyArrayIfNotFilteredAfterFiltered() throws Exception {
        try {
            includeEmptyArrayIfNotFilteredAfterFiltered();
        } finally {
        }
    }

}
