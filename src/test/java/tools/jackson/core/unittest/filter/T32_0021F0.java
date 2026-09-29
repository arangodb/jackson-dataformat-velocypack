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

class T32_0021F0 {

    void issue890SingleProperty() throws Exception {
        Object value = writeIssue890(Set.of("/0/id"));
        assertEquals(List.of(Map.of("id", 1)), value);
    }

    void issue890TwoProperties() throws Exception {
        Object value = writeIssue890(Set.of("/0/id", "/0/stuff/0/name"));
        assertEquals(List.of(Map.of("id", 1,
                "stuff", List.of(Map.of("name", "first")))), value);
    }

    void issue890FullArray() throws Exception {
        Object value = writeIssue890(Set.of("//id", "//stuff//name"));
        assertEquals(List.of(
                Map.of("id", 1, "stuff", List.of(
                        Map.of("name", "first"), Map.of("name", "second"))),
                Map.of("id", 2, "stuff", List.of(
                        Map.of("name", "third"), Map.of("name", "fourth")))), value);
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

    void __invoke_issue890SingleProperty() throws Exception {
        try {
            issue890SingleProperty();
        } finally {
        }
    }


    void __invoke_issue890TwoProperties() throws Exception {
        try {
            issue890TwoProperties();
        } finally {
        }
    }


    void __invoke_issue890FullArray() throws Exception {
        try {
            issue890FullArray();
        } finally {
        }
    }

}
