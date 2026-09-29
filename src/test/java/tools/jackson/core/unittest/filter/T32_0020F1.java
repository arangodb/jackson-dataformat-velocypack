package tools.jackson.core.unittest.filter;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.TreeNode;
import tools.jackson.core.filter.FilteringGeneratorDelegate;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import tools.jackson.core.util.JsonGeneratorDelegate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0020F1 {

    void issue609() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new FilteringGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output),
                TokenFilter.INCLUDE_ALL, Inclusion.INCLUDE_ALL_AND_PATH, true);
        generator = new StringTruncatingGeneratorDelegate(generator, 10);
        generator.writeStartObject();
        generator.writeName("message");
        generator.writeString("1234567890!");
        generator.writeEndObject();
        generator.close();
        assertEquals(Map.of("message", "1234567890"),
                new VPackMapper().readValue(output.toByteArray(), Object.class));
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

    void __invoke_issue609() throws Exception {
        try {
            issue609();
        } finally {
        }
    }

}
