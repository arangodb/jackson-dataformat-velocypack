package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TreeNode;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import tools.jackson.core.util.JsonGeneratorDelegate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0020F0 {

    void singleMatchFilteringWithoutPath() throws Exception {
        FilteredResult result = filtered(SIMPLE, new NameMatchFilter("value"),
                Inclusion.ONLY_INCLUDE_ALL, false);
        assertEquals(3, result.value());
        assertEquals(1, result.matchCount());
    }

    void singleMatchFilteringWithPath1() throws Exception {
        FilteredResult result = filtered(SIMPLE, new NameMatchFilter("a"),
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("a", 123), result.value());
        assertEquals(1, result.matchCount());
    }

    void singleMatchFilteringWithPath2() throws Exception {
        FilteredResult result = filtered(SIMPLE, new NameMatchFilter("value"),
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("value", 3)), result.value());
        assertEquals(1, result.matchCount());
    }

    void singleMatchFilteringWithPath3() throws Exception {
        FilteredResult result = filtered(SIMPLE, new NameMatchFilter("ob"),
                Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(Map.of("ob", Map.of("value0", 2, "value", 3,
                "value2", 0.25)), result.value());
        assertEquals(1, result.matchCount());
    }

    void notAllowMultipleMatchesWithoutPath1() throws Exception {
        FilteredResult result = filtered(WITHOUT_PATH_NAME, new NameMatchFilter("value"),
                Inclusion.ONLY_INCLUDE_ALL, false);
        assertEquals(3, result.value());
        assertEquals(1, result.matchCount());
    }

    void notAllowMultipleMatchesWithoutPath2() throws Exception {
        FilteredResult result = filtered(WITHOUT_PATH_INDEX, new IndexMatchFilter(1),
                Inclusion.ONLY_INCLUDE_ALL, false);
        assertEquals(2, result.value());
        assertEquals(1, result.matchCount());
    }

    void tokensSingleMatchWithPath() throws Exception {
        JsonParser p0 = new VPackFactory().createParser(ObjectReadContext.empty(), SIMPLE);
        FilteringParserDelegate p = new FilteringParserDelegate(p0,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, false);

        assertFalse(p.hasCurrentToken());
        assertNull(p.currentToken());
        assertEquals(JsonTokenId.ID_NO_TOKEN, p.currentTokenId());
        assertFalse(p.isExpectedStartObjectToken());
        assertFalse(p.isExpectedStartArrayToken());

        assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertEquals(JsonToken.START_OBJECT, p.currentToken());
        assertEquals(JsonTokenId.ID_START_OBJECT, p.currentTokenId());
        assertTrue(p.isExpectedStartObjectToken());
        assertFalse(p.isExpectedStartArrayToken());

        assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
        assertEquals(JsonToken.PROPERTY_NAME, p.currentToken());
        assertTrue(p.hasToken(JsonToken.PROPERTY_NAME));
        assertTrue(p.hasTokenId(JsonTokenId.ID_PROPERTY_NAME));
        assertEquals("ob", p.currentName());

        assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertEquals("ob", p.currentName());
        assertEquals(p0.currentLocation(), p.currentLocation());

        assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
        assertEquals("value", p.currentName());
        assertEquals("value", p.getString());

        assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
        assertEquals(JsonToken.VALUE_NUMBER_INT, p.currentToken());
        assertEquals(JsonParser.NumberType.INT, p.getNumberType());
        assertEquals(3, p.getIntValue());
        assertEquals(3, p.getValueAsInt());
        assertEquals(3, p.getValueAsInt(7));
        assertEquals(3L, p.getLongValue());
        assertEquals(3L, p.getValueAsLong());
        assertEquals(3L, p.getValueAsLong(6L));
        assertEquals(3D, p.getDoubleValue());
        assertEquals(3D, p.getValueAsDouble());
        assertEquals(3D, p.getValueAsDouble(0.5));
        assertEquals((short) 3, p.getShortValue());
        assertEquals((byte) 3, p.getByteValue());
        assertEquals(3F, p.getFloatValue());
        assertEquals(BigInteger.valueOf(3L), p.getBigIntegerValue());
        assertEquals(Integer.valueOf(3), p.getNumberValue());
        assertTrue(p.getValueAsBoolean());
        assertTrue(p.getValueAsBoolean(false));
        assertEquals("value", p.currentName());

        assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEquals(JsonToken.END_OBJECT, p.currentToken());
        assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEquals(JsonToken.END_OBJECT, p.currentToken());
        p.clearCurrentToken();
        assertNull(p.currentToken());
        p.close();
    }

    void skippingForSingleWithPath() throws Exception {
        JsonParser p0 = new VPackFactory().createParser(ObjectReadContext.empty(), SIMPLE);
        FilteringParserDelegate p = new FilteringParserDelegate(p0,
                new NameMatchFilter("value"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        assertEquals(JsonToken.START_OBJECT, p.nextToken());
        p.skipChildren();
        assertEquals(JsonToken.END_OBJECT, p.currentToken());
        assertNull(p.nextToken());
        p.close();
    }

    void readValueAsTreeRespectsFiltering() throws Exception {
        CountingReadContext context = new CountingReadContext();
        JsonParser p0 = new VPackFactory().createParser(context, READ_TREE_INPUT);
        FilteringParserDelegate p = new FilteringParserDelegate(p0,
                new NameMatchFilter("b"), Inclusion.INCLUDE_ALL_AND_PATH, false);
        p.readValueAsTree();
        assertEquals(4, context.tokenCount,
                "readValueAsTree() must read the filtered token stream, not the raw parser");
        p.close();
    }

    void valueOmitsFieldName1() throws Exception {
        FilteredResult result = filtered(VALUE_OMITS_ARRAY, new NoArraysFilter(),
                Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(Map.of("a", 123), result.value());
        assertEquals(1, result.matchCount());
    }

    void valueOmitsFieldName2() throws Exception {
        FilteredResult result = filtered(VALUE_OMITS_OBJECT, new NoObjectsFilter(),
                Inclusion.INCLUDE_NON_NULL, true);
        assertEquals(java.util.List.of("a", 123), result.value());
        assertEquals(2, result.matchCount());
    }
private static FilteredResult filtered(byte[] input, TokenFilter filter,
            Inclusion inclusion, boolean multipleMatches) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        FilteringParserDelegate parser = new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                filter, inclusion, multipleMatches);
        try (parser; JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        return new FilteredResult(new VPackMapper().readValue(output.toByteArray(), Object.class),
                parser.getMatchCount());
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
private static final byte[] WITHOUT_PATH_NAME = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] { 0x34 }),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("b", new byte[] { 0x1A }));
private static final byte[] WITHOUT_PATH_INDEX = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x31 }, new byte[] { 0x32 })),
            pair("array", compactArray(new byte[] { 0x33 }, new byte[] { 0x34 })),
            pair("ob", compactObject(
                    pair("value0", new byte[] { 0x32 }),
                    pair("value", new byte[] { 0x33 }),
                    pair("value2", new byte[] { 0x34 }),
                    pair("value", compactObject(pair("value0", new byte[] { 0x32 }))))),
            pair("value", new byte[] { 0x43, 'v', 'a', 'l' }),
            pair("b", new byte[] { 0x1A }));
private static final byte[] READ_TREE_INPUT = compactObject(
            pair("a", new byte[] { 0x31 }), pair("b", new byte[] { 0x32 }));
private static final byte[] VALUE_OMITS_ARRAY = compactObject(
            pair("a", new byte[] { 0x28, 0x7B }),
            pair("array", compactArray(new byte[] { 0x41, 'a' }, new byte[] { 0x41, 'b' })));
private static final byte[] VALUE_OMITS_OBJECT = compactArray(
            new byte[] { 0x41, 'a' },
            compactObject(pair("value0", new byte[] { 0x33 }),
                    pair("b", compactObject(pair("value", new byte[] { 0x34 })))),
            new byte[] { 0x28, 0x7B });
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
private static final class NoArraysFilter extends TokenFilter {
        @Override
        public TokenFilter filterStartArray() {
            return null;
        }
    }
private static final class NoObjectsFilter extends TokenFilter {
        @Override
        public TokenFilter filterStartObject() {
            return null;
        }
    }
private static final class StringTruncatingGeneratorDelegate extends JsonGeneratorDelegate {
        private final int maxStringLength;

        StringTruncatingGeneratorDelegate(JsonGenerator generator, int maxStringLength) {
            super(generator);
            this.maxStringLength = maxStringLength;
        }

        @Override
        public JsonGenerator writeString(String text) {
            if (text == null) {
                writeNull();
            } else if (maxStringLength <= 0 || maxStringLength >= text.length()) {
                super.writeString(text);
            } else {
                super.writeString(new StringReader(text), maxStringLength);
            }
            return this;
        }

        @Override
        public JsonGenerator writeName(String name) {
            if (maxStringLength <= 0 || maxStringLength >= name.length()) {
                super.writeName(name);
            } else {
                super.writeName(name.substring(0, maxStringLength));
            }
            return this;
        }
    }
private static final class CountingReadContext extends ObjectReadContext.Base {
        int tokenCount;

        @Override
        public <T extends TreeNode> T readTree(JsonParser parser) {
            tokenCount = 0;
            while (parser.nextToken() != null) {
                ++tokenCount;
            }
            return null;
        }
    }
private record FilteredResult(Object value, int matchCount) { }

    void __invoke_singleMatchFilteringWithoutPath() throws Exception {
        try {
            singleMatchFilteringWithoutPath();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPath1() throws Exception {
        try {
            singleMatchFilteringWithPath1();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPath2() throws Exception {
        try {
            singleMatchFilteringWithPath2();
        } finally {
        }
    }


    void __invoke_singleMatchFilteringWithPath3() throws Exception {
        try {
            singleMatchFilteringWithPath3();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithoutPath1() throws Exception {
        try {
            notAllowMultipleMatchesWithoutPath1();
        } finally {
        }
    }


    void __invoke_notAllowMultipleMatchesWithoutPath2() throws Exception {
        try {
            notAllowMultipleMatchesWithoutPath2();
        } finally {
        }
    }


    void __invoke_tokensSingleMatchWithPath() throws Exception {
        try {
            tokensSingleMatchWithPath();
        } finally {
        }
    }


    void __invoke_skippingForSingleWithPath() throws Exception {
        try {
            skippingForSingleWithPath();
        } finally {
        }
    }


    void __invoke_readValueAsTreeRespectsFiltering() throws Exception {
        try {
            readValueAsTreeRespectsFiltering();
        } finally {
        }
    }


    void __invoke_valueOmitsFieldName1() throws Exception {
        try {
            valueOmitsFieldName1();
        } finally {
        }
    }


    void __invoke_valueOmitsFieldName2() throws Exception {
        try {
            valueOmitsFieldName2();
        } finally {
        }
    }

}
