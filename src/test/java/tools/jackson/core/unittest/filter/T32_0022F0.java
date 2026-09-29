package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0022F0 {

    void simplestWithPath() throws Exception {
        assertFiltered(SIMPLEST_INPUT, "/a", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", 1));
        assertFiltered(SIMPLEST_INPUT, "/b", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", 2));
        assertFiltered(SIMPLEST_INPUT, "/c", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", 3));
        assertNoOutput(SIMPLEST_INPUT, "/c/0", Inclusion.INCLUDE_ALL_AND_PATH);
        assertNoOutput(SIMPLEST_INPUT, "/d", Inclusion.INCLUDE_ALL_AND_PATH);
    }

    void simplestNoPath() throws Exception {
        assertFiltered(SIMPLEST_INPUT, "/a", Inclusion.ONLY_INCLUDE_ALL, 1);
        assertFiltered(SIMPLEST_INPUT, "/b", Inclusion.ONLY_INCLUDE_ALL, 2);
        assertNoOutput(SIMPLEST_INPUT, "/b/2", Inclusion.ONLY_INCLUDE_ALL);
        assertFiltered(SIMPLEST_INPUT, "/c", Inclusion.ONLY_INCLUDE_ALL, 3);
        assertNoOutput(SIMPLEST_INPUT, "/d", Inclusion.ONLY_INCLUDE_ALL);
    }

    void simpleWithPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/c", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))));
        assertFiltered(SIMPLE_INPUT, "/c/d", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("c", Map.of("d", Map.of("a", true))));
        assertFiltered(SIMPLE_INPUT, "/a", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("a", 1));
        assertFiltered(SIMPLE_INPUT, "/b", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(1, 2, 3)));
        assertFiltered(SIMPLE_INPUT, "/b/0", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(1)));
        assertFiltered(SIMPLE_INPUT, "/b/1", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(2)));
        assertFiltered(SIMPLE_INPUT, "/b/2", Inclusion.INCLUDE_ALL_AND_PATH,
                Map.of("b", List.of(3)));
        assertNoOutput(SIMPLE_INPUT, "/b/3", Inclusion.INCLUDE_ALL_AND_PATH);
    }

    void simpleNoPath() throws Exception {
        assertFiltered(SIMPLE_INPUT, "/c", Inclusion.ONLY_INCLUDE_ALL,
                Map.of("d", Map.of("a", true)));
        assertFiltered(SIMPLE_INPUT, "/c/d", Inclusion.ONLY_INCLUDE_ALL,
                Map.of("a", true));
        assertFiltered(SIMPLE_INPUT, "/a", Inclusion.ONLY_INCLUDE_ALL, 1);
        assertFiltered(SIMPLE_INPUT, "/b", Inclusion.ONLY_INCLUDE_ALL,
                List.of(1, 2, 3));
        assertFiltered(SIMPLE_INPUT, "/b/0", Inclusion.ONLY_INCLUDE_ALL, 1);
        assertFiltered(SIMPLE_INPUT, "/b/1", Inclusion.ONLY_INCLUDE_ALL, 2);
        assertFiltered(SIMPLE_INPUT, "/b/2", Inclusion.ONLY_INCLUDE_ALL, 3);
        assertNoOutput(SIMPLE_INPUT, "/b/3", Inclusion.ONLY_INCLUDE_ALL);
    }
private static void assertFiltered(byte[] input, String path, Inclusion inclusion,
            Object expected) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (FilteringParserDelegate parser = new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new tools.jackson.core.filter.JsonPointerBasedFilter(
                        JsonPointer.compile(path)), inclusion, false);
                JsonGenerator generator = new VPackFactory().createGenerator(
                        ObjectWriteContext.empty(), output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        assertEquals(expected, new VPackMapper().readValue(output.toByteArray(), Object.class));
    }
private static void assertNoOutput(byte[] input, String path, Inclusion inclusion)
            throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (FilteringParserDelegate parser = new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new tools.jackson.core.filter.JsonPointerBasedFilter(
                        JsonPointer.compile(path)), inclusion, false);
                JsonGenerator generator = new VPackFactory().createGenerator(
                        ObjectWriteContext.empty(), output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        assertEquals(0, output.size());
    }
private static FilteringParserDelegate filteredParser(byte[] input) {
        return new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new OnePropertyFilter1418Fixed(), Inclusion.INCLUDE_ALL_AND_PATH, true);
    }
private static FilteringParserDelegate originalFilteredParser(byte[] input) {
        return new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new OnePropertyFilter1418Original(), Inclusion.INCLUDE_ALL_AND_PATH, true);
    }
private static void assertOneObject(JsonParser parser) throws Exception {
        assertToken(JsonToken.START_OBJECT, parser.nextToken());
        assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals("one", parser.currentName());
        assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(1, parser.getIntValue());
        assertToken(JsonToken.END_OBJECT, parser.nextToken());
    }
private static void assertEmptyObject(JsonParser parser) throws Exception {
        assertToken(JsonToken.START_OBJECT, parser.nextToken());
        assertToken(JsonToken.END_OBJECT, parser.nextToken());
    }
private static void assertToken(JsonToken expected, JsonToken actual) {
        assertEquals(expected, actual);
    }
private static final byte[] SIMPLEST_INPUT = compactObject(
            pair("a", number(1)), pair("b", number(2)), pair("c", number(3)));
private static final byte[] SIMPLE_INPUT = compactObject(
            pair("a", number(1)),
            pair("b", compactArray(number(1), number(2), number(3))),
            pair("c", compactObject(pair("d", compactObject(pair("a", bool(true)))))),
            pair("d", new byte[] { 0x18 }));
private static final byte[] ARRAY_ONE_TWO = compactArray(
            compactObject(pair("one", number(1))),
            compactObject(pair("two", number(2))));
private static final byte[] ARRAY_ONE_ONE_TWO = compactArray(
            compactObject(pair("one", number(1))),
            compactObject(pair("one", number(1)), pair("two", number(2))));
private static final byte[] ARRAY_ONE_ONE_TWO_ONE = compactArray(
            compactObject(pair("one", number(1))),
            compactObject(pair("one", number(1)), pair("two", number(2))),
            compactObject(pair("one", number(1))));
private static final byte[] ARRAY_TWO_THREE = compactArray(
            compactObject(pair("two", number(2))),
            compactObject(pair("three", number(3))));
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
private static final class OnePropertyFilter1418Fixed extends TokenFilter {
        @Override
        public TokenFilter includeProperty(String name) {
            return "one".equals(name) ? this : null;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return true;
        }
    }
private static final class OnePropertyFilter1418Original extends TokenFilter {
        @Override
        public TokenFilter includeProperty(String name) {
            return "one".equals(name) ? this : null;
        }

        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return true;
        }
    }

    void __invoke_simplestWithPath() throws Exception {
        try {
            simplestWithPath();
        } finally {
        }
    }


    void __invoke_simplestNoPath() throws Exception {
        try {
            simplestNoPath();
        } finally {
        }
    }


    void __invoke_simpleWithPath() throws Exception {
        try {
            simpleWithPath();
        } finally {
        }
    }


    void __invoke_simpleNoPath() throws Exception {
        try {
            simpleNoPath();
        } finally {
        }
    }

}
