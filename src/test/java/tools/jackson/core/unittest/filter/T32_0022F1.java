package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

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
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0022F1 {

    void filterArray1Corrected() throws Exception {
        try (FilteringParserDelegate parser = filteredParser(ARRAY_ONE_TWO)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertEmptyObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArray2Corrected() throws Exception {
        try (FilteringParserDelegate parser = filteredParser(ARRAY_ONE_ONE_TWO)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertOneObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArray3Corrected() throws Exception {
        try (FilteringParserDelegate parser = filteredParser(ARRAY_ONE_ONE_TWO_ONE)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertOneObject(parser);
            assertOneObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArray4Corrected() throws Exception {
        try (FilteringParserDelegate parser = filteredParser(ARRAY_TWO_THREE)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertEmptyObject(parser);
            assertEmptyObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArrayWithObjectsEndingWithFilteredProperty1() throws Exception {
        try (FilteringParserDelegate parser = originalFilteredParser(ARRAY_ONE_TWO)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArrayWithObjectsEndingWithFilteredProperty2() throws Exception {
        try (FilteringParserDelegate parser = originalFilteredParser(ARRAY_ONE_ONE_TWO)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertOneObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void filterArrayWithObjectsEndingWithFilteredProperty3() throws Exception {
        try (FilteringParserDelegate parser = originalFilteredParser(ARRAY_ONE_ONE_TWO_ONE)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertOneObject(parser);
            assertOneObject(parser);
            assertOneObject(parser);
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
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

    void __invoke_filterArray1Corrected() throws Exception {
        try {
            filterArray1Corrected();
        } finally {
        }
    }


    void __invoke_filterArray2Corrected() throws Exception {
        try {
            filterArray2Corrected();
        } finally {
        }
    }


    void __invoke_filterArray3Corrected() throws Exception {
        try {
            filterArray3Corrected();
        } finally {
        }
    }


    void __invoke_filterArray4Corrected() throws Exception {
        try {
            filterArray4Corrected();
        } finally {
        }
    }


    void __invoke_filterArrayWithObjectsEndingWithFilteredProperty1() throws Exception {
        try {
            filterArrayWithObjectsEndingWithFilteredProperty1();
        } finally {
        }
    }


    void __invoke_filterArrayWithObjectsEndingWithFilteredProperty2() throws Exception {
        try {
            filterArrayWithObjectsEndingWithFilteredProperty2();
        } finally {
        }
    }


    void __invoke_filterArrayWithObjectsEndingWithFilteredProperty3() throws Exception {
        try {
            filterArrayWithObjectsEndingWithFilteredProperty3();
        } finally {
        }
    }

}
