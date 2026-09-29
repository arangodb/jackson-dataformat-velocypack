package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0019Fixture {

    void multipleMatchFilteringWithPath3() throws Exception {
        FilteredResult result = filtered(MULTIPLE_MATCH_PATH3,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, true);
        assertEquals(Map.of("root", Map.of(
                "a", Map.of("value", 3),
                "b", Map.of("value", "foo"))), result.value());
        assertEquals(2, result.matchCount());
    }

    void noMatchFiltering1() throws Exception {
        FilteredResult result = filtered(SIMPLE, new NameMatchFilter("invalid"),
                Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(Map.of("array", List.of(), "ob", Map.of()), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering2() throws Exception {
        FilteredResult result = filtered(compactArray(SIMPLE, SIMPLE, SIMPLE),
                new NameMatchFilter("invalid"), Inclusion.INCLUDE_NON_NULL, true);
        Map<String, Object> expected = Map.of("array", List.of(), "ob", Map.of());
        assertEquals(List.of(expected, expected, expected), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering3() throws Exception {
        FilteredResult result = filtered(compactArray(
                        compactArray(SIMPLE), compactArray(SIMPLE), compactArray(SIMPLE)),
                new NameMatchFilter("invalid"), Inclusion.INCLUDE_NON_NULL, true);
        Map<String, Object> expected = Map.of("array", List.of(), "ob", Map.of());
        assertEquals(List.of(List.of(expected), List.of(expected), List.of(expected)),
                result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering4() throws Exception {
        FilteredResult result = filtered(SIMPLE, new StrictNameMatchFilter("invalid"),
                Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(Map.of(), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering5() throws Exception {
        FilteredResult result = filtered(compactArray(SIMPLE, SIMPLE, SIMPLE),
                new StrictNameMatchFilter("invalid"), Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(List.of(Map.of(), Map.of(), Map.of()), result.value());
        assertEquals(0, result.matchCount());
    }

    void noMatchFiltering6() throws Exception {
        FilteredResult result = filtered(compactArray(
                        compactArray(SIMPLE), compactArray(SIMPLE), compactArray(SIMPLE)),
                new StrictNameMatchFilter("invalid"), Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(List.of(List.of(Map.of()), List.of(Map.of()), List.of(Map.of())),
                result.value());
        assertEquals(0, result.matchCount());
    }

    void nonFiltering() throws Exception {
        assertEquals(Map.of(
                "a", 123,
                "array", List.of(1, 2),
                "ob", Map.of("value0", 2, "value", 3, "value2", 0.25),
                "b", true), readModel(SIMPLE));
    }

    void notAllowMultipleMatchesWithPath1() throws Exception {
        FilteredResult result = filtered(DUPLICATE_ARRAY_PATH, new IndexMatchFilter(1),
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("array", List.of(2)), result.value());
        assertEquals(1, result.matchCount());
    }

    void notAllowMultipleMatchesWithPath2() throws Exception {
        FilteredResult result = filtered(NESTED_ARRAY_PATH, new IndexMatchFilter(1),
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("array", List.of(2))), result.value());
        assertEquals(1, result.matchCount());
    }

    void notAllowMultipleMatchesWithPath3() throws Exception {
        FilteredResult result = filtered(NESTED_VALUE_PATH,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("value", 3)), result.value());
        assertEquals(1, result.matchCount());
    }

    void notAllowMultipleMatchesWithPath4() throws Exception {
        FilteredResult result = filtered(OBJECT_MATCH_PATH,
                new NameMatchFilter("ob"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("value1", 1)), result.value());
        assertEquals(1, result.matchCount());
    }
private static final byte[] SIMPLE = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] {
                            0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xD0, 0x3F }))),
            pair("b", new byte[] { 0x1A }));
private static final byte[] MULTIPLE_MATCH_PATH3 = compactObject(
            pair("root", compactObject(
                    pair("a0", new byte[] { 0x1A }),
                    pair("a", compactObject(pair("value", new byte[] { 0x33 }))),
                    pair("b", compactObject(pair("value",
                            new byte[] { 0x43, 0x66, 0x6F, 0x6F }))))),
            pair("b0", new byte[] { 0x19 }));
private static final byte[] DUPLICATE_ARRAY_PATH = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("array", compactArray(new byte[] { 0x33 }, new byte[] { 0x34 })),
            pair("ob", compactObject(
                    pair("value", new byte[] { 0x33 }),
                    pair("array", compactArray(new byte[] { 0x35 }, new byte[] { 0x36 })),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 0x76, 0x61, 0x6C }),
            pair("b", new byte[] { 0x1A }));
private static final byte[] NESTED_ARRAY_PATH = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("ob", compactObject(
                    pair("value", new byte[] { 0x33 }),
                    pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("array", compactArray(new byte[] { 0x33 }, new byte[] { 0x34 })));
private static final byte[] NESTED_VALUE_PATH = compactObject(
            pair("ob", compactObject(
                    pair("value", new byte[] { 0x33 }),
                    pair("ob", compactObject(pair("value", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 0x76, 0x61, 0x6C }));
private static final byte[] OBJECT_MATCH_PATH = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("ob", compactObject(pair("value1", new byte[] { 0x31 }))),
            pair("ob2", compactObject(pair("ob",
                    compactObject(pair("value2", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 0x76, 0x61, 0x6C }),
            pair("b", new byte[] { 0x1A }));
private static Object readModel(byte[] input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = new VPackFactory().createParser(
                        ObjectReadContext.empty(), input);
                JsonGenerator generator = new VPackFactory().createGenerator(
                        ObjectWriteContext.empty(), output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        return new VPackMapper().readValue(output.toByteArray(), Object.class);
    }
private static FilteredResult filtered(byte[] input, TokenFilter filter,
            Inclusion inclusion, boolean multipleMatches) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringParserDelegate parser = new FilteringParserDelegate(new VPackFactory().createParser(
                ObjectReadContext.empty(), input), filter, inclusion, multipleMatches);
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

    void __invoke_multipleMatchFilteringWithPath3() throws Exception {
        try {
            multipleMatchFilteringWithPath3();
        } finally {
        }
    }


    void __invoke_noMatchFiltering1() throws Exception {
        try {
            noMatchFiltering1();
        } finally {
        }
    }


    void __invoke_noMatchFiltering2() throws Exception {
        try {
            noMatchFiltering2();
        } finally {
        }
    }


    void __invoke_noMatchFiltering3() throws Exception {
        try {
            noMatchFiltering3();
        } finally {
        }
    }


    void __invoke_noMatchFiltering4() throws Exception {
        try {
            noMatchFiltering4();
        } finally {
        }
    }


    void __invoke_noMatchFiltering5() throws Exception {
        try {
            noMatchFiltering5();
        } finally {
        }
    }


    void __invoke_noMatchFiltering6() throws Exception {
        try {
            noMatchFiltering6();
        } finally {
        }
    }


    void __invoke_nonFiltering() throws Exception {
        try {
            nonFiltering();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithPath1() throws Exception {
        try {
            notAllowMultipleMatchesWithPath1();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithPath2() throws Exception {
        try {
            notAllowMultipleMatchesWithPath2();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithPath3() throws Exception {
        try {
            notAllowMultipleMatchesWithPath3();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithPath4() throws Exception {
        try {
            notAllowMultipleMatchesWithPath4();
        } finally {
        }
    }

}
