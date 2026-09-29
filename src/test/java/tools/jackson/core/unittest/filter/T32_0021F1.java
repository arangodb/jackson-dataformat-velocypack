package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.JsonPointerBasedFilter;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0021F1 {

    void simplePropertyWithPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/c", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))), false);
        assertFiltered(SIMPLE_INPUT, "/c/d", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))), false);
        assertFiltered(SIMPLE_INPUT, "/c/d/a", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))), false);
        assertFiltered(SIMPLE_INPUT, "/c/d/a", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))), false);
        assertFiltered(SIMPLE_INPUT, "/a", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", 1), false);
        assertFiltered(SIMPLE_INPUT, "/d", Inclusion.INCLUDE_ALL_AND_PATH,
                java.util.Collections.singletonMap("d", null), false);
        assertNoOutput(SIMPLE_INPUT, "/x", Inclusion.INCLUDE_ALL_AND_PATH, false);
    }

    void simplePropertyWithoutPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/c", Inclusion.ONLY_INCLUDE_ALL,
                Map.of("d", Map.of("a", true)), false);
        assertFiltered(SIMPLE_INPUT, "/c/d", Inclusion.ONLY_INCLUDE_ALL,
                Map.of("a", true), false);
        assertFiltered(SIMPLE_INPUT, "/c/d/a", Inclusion.ONLY_INCLUDE_ALL,
                true, false);
        assertFiltered(SIMPLE_INPUT, "/a", Inclusion.ONLY_INCLUDE_ALL, 1, false);
        assertFiltered(SIMPLE_INPUT, "/d", Inclusion.ONLY_INCLUDE_ALL, null, false);
        assertNoOutput(SIMPLE_INPUT, "/x", Inclusion.ONLY_INCLUDE_ALL, false);
    }

    void arrayElementWithPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/b", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(1, 2, 3)), false);
        assertFiltered(SIMPLE_INPUT, "/b/1", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(2)), false);
        assertFiltered(SIMPLE_INPUT, "/b/2", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(3)), false);
        assertNoOutput(SIMPLE_INPUT, "/b/8", Inclusion.INCLUDE_ALL_AND_PATH, false);
    }

    void arrayNestedWithPath() throws Exception {
        assertFiltered(NESTED_OBJECT_ARRAY, "/a/1/b", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", List.of(Map.of("b", 3))), false);
        assertFiltered(NESTED_BOOLEAN_ARRAY, "/0", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(true), false);
        assertFiltered(NESTED_BOOLEAN_ARRAY, "/1", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(List.of(1)), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/0", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(true), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(List.of(1, 2, List.of(true), 3)), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1/2", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(List.of(List.of(true))), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1/2/0", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(List.of(List.of(true))), false);
        assertNoOutput(NESTED_MIXED_ARRAY, "/1/3/0", Inclusion.INCLUDE_ALL_AND_PATH, false);
    }

    void arrayNestedWithoutPath() throws Exception {
        assertFiltered(NESTED_OBJECT_ARRAY, "/a/1/b", Inclusion.ONLY_INCLUDE_ALL, 3, false);
        assertFiltered(NESTED_BOOLEAN_ARRAY, "/0", Inclusion.ONLY_INCLUDE_ALL, true, false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1", Inclusion.ONLY_INCLUDE_ALL,
                List.of(1, 2, List.of(true), 3), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1/2", Inclusion.ONLY_INCLUDE_ALL,
                List.of(true), false);
        assertFiltered(NESTED_MIXED_ARRAY, "/1/2/0", Inclusion.ONLY_INCLUDE_ALL,
                true, false);
        assertNoOutput(NESTED_MIXED_ARRAY, "/1/3/0", Inclusion.ONLY_INCLUDE_ALL, false);
    }

    void arrayElementWithoutPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/b", Inclusion.ONLY_INCLUDE_ALL,
                List.of(1, 2, 3), false);
        assertFiltered(SIMPLE_INPUT, "/b/1", Inclusion.ONLY_INCLUDE_ALL, 2, false);
        assertFiltered(SIMPLE_INPUT, "/b/2", Inclusion.ONLY_INCLUDE_ALL, 3, false);
        assertNoOutput(SIMPLE_INPUT, "/b/8", Inclusion.ONLY_INCLUDE_ALL, false);
        assertNoOutput(SIMPLE_INPUT, "/x", Inclusion.ONLY_INCLUDE_ALL, false);
    }

    void allowMultipleMatchesWithPath() throws Exception {
        assertFiltered(ARRAY_123, "/0", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(1), true);
        assertFiltered(ARRAY_123, "/1", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(2), true);
        assertFiltered(ARRAY_123, "/2", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(3), true);
        assertFiltered(OBJECT_ARRAY_123, "/a/0", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", List.of(1)), true);
        assertFiltered(OBJECT_ARRAY_123, "/a/1", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", List.of(2)), true);
        assertFiltered(OBJECT_ARRAY_123, "/a/2", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", List.of(3)), true);
        assertFiltered(OBJECTS_123, "/0/id", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("id", 1)), true);
        assertFiltered(OBJECTS_123, "/1/id", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("id", 2)), true);
        assertFiltered(OBJECTS_123, "/2/id", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("id", 3)), true);
        assertFiltered(STUFF_OBJECTS, "/0/stuff/0", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("stuff", List.of(1))), true);
        assertFiltered(STUFF_OBJECTS, "/1/stuff/1", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("stuff", List.of(5))), true);
        assertFiltered(STUFF_OBJECTS, "/2/stuff/2", Inclusion.INCLUDE_ALL_AND_PATH,
                List.of(Map.of("stuff", List.of(9))), true);
    }

    void arrayFiltering582WithoutObject() throws Exception {
        assertArrayFiltering582(0);
    }

    void arrayFiltering582WithoutSize() throws Exception {
        assertArrayFiltering582(1);
    }

    void arrayFiltering582WithSize() throws Exception {
        assertArrayFiltering582(2);
    }
private static Object writeIssue890(Set<String> pointers) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                OrTokenFilter.create(pointers), Inclusion.INCLUDE_ALL_AND_PATH, true)) {
            generator.writeStartArray();
            writeOuterObject(generator, 1, "first", "a", "second", "b");
            writeOuterObject(generator, 2, "third", "c", "fourth", "d");
            generator.writeEndArray();
        }
        return readValue(output);
    }
private static void writeOuterObject(JsonGenerator generator, int id,
            String name1, String type1, String name2, String type2) throws Exception {
        generator.writeStartObject();
        generator.writeName("id");
        generator.writeNumber(id);
        generator.writeName("stuff");
        generator.writeStartArray();
        writeInnerObject(generator, name1, type1);
        writeInnerObject(generator, name2, type2);
        generator.writeEndArray();
        generator.writeEndObject();
    }
private static void writeInnerObject(JsonGenerator generator, String name, String type)
            throws Exception {
        generator.writeStartObject();
        generator.writeName("name");
        generator.writeString(name);
        generator.writeName("type");
        generator.writeString(type);
        generator.writeEndObject();
    }
private static void assertFiltered(byte[] input, String path, Inclusion inclusion,
            Object expected, boolean allowMultipleMatches) throws Exception {
        FilteredResult result = filter(input, path, inclusion, allowMultipleMatches);
        assertEquals(expected, result.value());
    }
private static void assertNoOutput(byte[] input, String path, Inclusion inclusion,
            boolean allowMultipleMatches) throws Exception {
        FilteredResult result = filter(input, path, inclusion, allowMultipleMatches);
        assertEquals(0, result.outputLength());
    }
private static FilteredResult filter(byte[] input, String path, Inclusion inclusion,
            boolean allowMultipleMatches) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringGeneratorDelegate generator = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                new JsonPointerBasedFilter(JsonPointer.compile(path)), inclusion,
                allowMultipleMatches);
        try (JsonParser parser = new VPackFactory().createParser(ObjectReadContext.empty(), input);
                generator) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        return new FilteredResult(output.size() == 0 ? null : readValue(output), output.size());
    }
private static void assertArrayFiltering582(int mode) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator underlying = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), output);
        try (JsonGenerator generator = new FilteringGeneratorDelegate(underlying,
                new JsonPointerBasedFilter("/noMatch"), Inclusion.ONLY_INCLUDE_ALL, true)) {
            Object[] stuff = { "foo", "bar" };
            switch (mode) {
            case 0 -> generator.writeStartArray();
            case 1 -> generator.writeStartArray(stuff);
            default -> generator.writeStartArray(stuff, stuff.length);
            }
            generator.writeString(stuff[0].toString());
            generator.writeString(stuff[1].toString());
            generator.writeEndArray();
        }
        assertEquals(0, output.size());
    }
private static Object readValue(ByteArrayOutputStream output) throws Exception {
        return new VPackMapper().readValue(output.toByteArray(), Object.class);
    }
private static final byte[] SIMPLE_INPUT = compactObject(
            pair("a", number(1)),
            pair("b", compactArray(number(1), number(2), number(3))),
            pair("c", compactObject(pair("d", compactObject(pair("a", bool(true)))))),
            pair("d", new byte[] { 0x18 }));
private static final byte[] NESTED_OBJECT_ARRAY = compactObject(
            pair("a", compactArray(bool(true), compactObject(
                    pair("b", number(3)), pair("d", number(2))), bool(false))));
private static final byte[] NESTED_BOOLEAN_ARRAY = compactArray(
            bool(true), compactArray(number(1)));
private static final byte[] NESTED_MIXED_ARRAY = compactArray(
            bool(true), compactArray(number(1), number(2), compactArray(bool(true)), number(3)),
            number(0));
private static final byte[] ARRAY_123 = compactArray(number(1), number(2), number(3));
private static final byte[] OBJECT_ARRAY_123 = compactObject(
            pair("a", compactArray(number(1), number(2), number(3))));
private static final byte[] OBJECTS_123 = compactArray(
            compactObject(pair("id", number(1))),
            compactObject(pair("id", number(2))),
            compactObject(pair("id", number(3))));
private static final byte[] STUFF_OBJECTS = compactArray(
            compactObject(pair("id", number(1)), pair("stuff",
                    compactArray(number(1), number(2), number(3)))),
            compactObject(pair("id", number(2)), pair("stuff",
                    compactArray(number(4), number(5), number(6)))),
            compactObject(pair("id", number(3)), pair("stuff",
                    compactArray(number(7), number(8), number(9)))));
private static byte[] number(int value) {
        return new byte[] { (byte) (0x30 + value) };
    }
private static byte[] bool(boolean value) {
        return new byte[] { (byte) (value ? 0x1A : 0x18) };
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
        int countLength = varintLength(values.length);
        int length = 1 + 1 + bodyLength + countLength;
        while (true) {
            int adjusted = 1 + varintLength(length) + bodyLength + countLength;
            if (adjusted == length) {
                break;
            }
            length = adjusted;
        }
        byte[] result = new byte[length];
        result[0] = (byte) marker;
        int headerLength = writeForward(result, 1, length);
        int offset = 1 + headerLength;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        writeReverse(result, offset, values.length);
        return result;
    }
private static int writeForward(byte[] output, int offset, int value) {
        int at = offset;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        return at - offset;
    }
private static void writeReverse(byte[] output, int offset, int value) {
        int groups = varintLength(value);
        int at = offset + groups;
        do {
            int group = value & 0x7F;
            value >>>= 7;
            output[--at] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
    }
private static int varintLength(int value) {
        int result = 1;
        while ((value >>>= 7) != 0) {
            ++result;
        }
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
private static final class OrTokenFilter extends TokenFilter {
        private final List<? extends TokenFilter> delegates;

        private OrTokenFilter(List<? extends TokenFilter> delegates) {
            this.delegates = delegates;
        }

        static OrTokenFilter create(Set<String> pointers) {
            List<TokenFilter> filters = new ArrayList<>();
            for (String pointer : pointers) {
                filters.add(new JsonPointerBasedFilter(JsonPointer.compile(pointer), true));
            }
            return new OrTokenFilter(filters);
        }

        @Override
        public TokenFilter includeElement(int index) {
            return execute(delegate -> delegate.includeElement(index));
        }

        @Override
        public TokenFilter includeProperty(String name) {
            return execute(delegate -> delegate.includeProperty(name));
        }

        @Override
        public TokenFilter filterStartArray() {
            return this;
        }

        @Override
        public TokenFilter filterStartObject() {
            return this;
        }

        private TokenFilter execute(java.util.function.UnaryOperator<TokenFilter> operation) {
            List<TokenFilter> next = null;
            for (TokenFilter delegate : delegates) {
                TokenFilter result = operation.apply(delegate);
                if (result == null) {
                    continue;
                }
                if (result == TokenFilter.INCLUDE_ALL) {
                    return TokenFilter.INCLUDE_ALL;
                }
                if (next == null) {
                    next = new ArrayList<>(delegates.size());
                }
                next.add(result);
            }
            return next == null ? null : new OrTokenFilter(next);
        }
    }
private record FilteredResult(Object value, int outputLength) { }

    void __invoke_simplePropertyWithPath() throws Exception {
        try {
            simplePropertyWithPath();
        } finally {
        }
    }


    void __invoke_simplePropertyWithoutPath() throws Exception {
        try {
            simplePropertyWithoutPath();
        } finally {
        }
    }


    void __invoke_arrayElementWithPath() throws Exception {
        try {
            arrayElementWithPath();
        } finally {
        }
    }


    void __invoke_arrayNestedWithPath() throws Exception {
        try {
            arrayNestedWithPath();
        } finally {
        }
    }


    void __invoke_arrayNestedWithoutPath() throws Exception {
        try {
            arrayNestedWithoutPath();
        } finally {
        }
    }


    void __invoke_arrayElementWithoutPath() throws Exception {
        try {
            arrayElementWithoutPath();
        } finally {
        }
    }


    void __invoke_allowMultipleMatchesWithPath() throws Exception {
        try {
            allowMultipleMatchesWithPath();
        } finally {
        }
    }


    void __invoke_arrayFiltering582WithoutObject() throws Exception {
        try {
            arrayFiltering582WithoutObject();
        } finally {
        }
    }


    void __invoke_arrayFiltering582WithoutSize() throws Exception {
        try {
            arrayFiltering582WithoutSize();
        } finally {
        }
    }


    void __invoke_arrayFiltering582WithSize() throws Exception {
        try {
            arrayFiltering582WithSize();
        } finally {
        }
    }

}
