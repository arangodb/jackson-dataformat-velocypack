package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TreeNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0086F1 {
private static final byte[] ARRAY_1 = { 0x02, 0x03, 0x31 };
private static final byte[] ARRAY_2 = { 0x02, 0x03, 0x32 };
private static final byte[] ARRAY_123 = { 0x02, 0x05, 0x31, 0x32, 0x33 };
private static final byte[] NULL_MIXED_OBJECT = {
            0x14, 0x0E,
            0x44, 0x00, 0x61, 0x62, 0x63, 0x31,
            0x43, 0x61, 0x62, 0x63, 0x32,
            0x02
    };
private static final byte[] NULL_ONLY_OBJECT = {
            0x14, 0x15,
            0x41, 0x00, 0x31,
            0x42, 0x00, 0x00, 0x32,
            0x43, 0x00, 0x00, 0x00, 0x33,
            0x44, 0x00, 0x00, 0x00, 0x00, 0x34,
            0x04
    };

    void literalVpackObjectNamesPreserveEmbeddedNulBytesAcrossFactoryReuse()
            throws Exception {
        VPackFactory factory = new VPackFactory();
        assertNullNames(factory, NULL_MIXED_OBJECT,
                new String[] { "\u0000abc", "abc" }, new int[] { 1, 2 });
        assertNullNames(factory, NULL_MIXED_OBJECT,
                new String[] { "\u0000abc", "abc" }, new int[] { 1, 2 });
    }

    void literalVpackObjectNamesPreserveOneThroughFourNulsAcrossFactoryReuse()
            throws Exception {
        VPackFactory factory = new VPackFactory();
        String[] names = { "\u0000", "\u0000\u0000", "\u0000\u0000\u0000",
                "\u0000\u0000\u0000\u0000" };
        int[] values = { 1, 2, 3, 4 };
        assertNullNames(factory, NULL_ONLY_OBJECT, names, values);
        assertNullNames(factory, NULL_ONLY_OBJECT, names, values);
    }
private static void assertNullNames(VPackFactory factory, byte[] input,
            String[] names, int[] values) throws Exception {
        try (JsonParser parser = factory.createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (int i = 0; i < names.length; ++i) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals(names[i], parser.currentName());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(values[i], parser.getIntValue());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertNextValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_ARRAY, parser.nextValue());
            for (int i = 1; i <= 4; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
                assertEquals(i, parser.getIntValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextValue());
            assertNull(parser.nextValue());
        }
    }
private static void assertObjectNextValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextValue());
            for (int i = 3; i <= 5; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
                assertEquals(String.valueOf(i), parser.currentName());
                assertEquals(i, parser.getIntValue());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextValue());
            assertNull(parser.nextValue());
        }
    }
private static void assertMixedNextValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_ARRAY, parser.nextValue());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextValue());
            assertEquals(JsonToken.START_ARRAY, parser.nextValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextValue());
            assertEquals(JsonToken.START_OBJECT, parser.nextValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextValue());
            assertNull(parser.nextValue());
        }
    }
private static void assertNestedNextValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextValue());
            assertNull(parser.currentName());
            assertEquals(JsonToken.START_OBJECT, parser.nextValue());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextValue());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextValue());
            assertEquals("c", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextValue());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextValue());
            assertEquals("d", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextValue());
            assertNull(parser.currentName());
            assertNull(parser.nextValue());
        }
    }
private static void assertNestedArrayNextValue(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextValue());
            assertNull(parser.currentName());
            assertEquals(JsonToken.START_ARRAY, parser.nextValue());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_FALSE, parser.nextValue());
            assertNull(parser.currentName());
            assertEquals(JsonToken.END_ARRAY, parser.nextValue());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.END_OBJECT, parser.nextValue());
            assertNull(parser.currentName());
            assertNull(parser.nextValue());
        }
    }
private static final class CountingReadContext extends ObjectReadContext.Base {
        int tokenCount;

        @Override
        public <T extends TreeNode> T readTree(JsonParser parser) {
            drain(parser);
            return null;
        }

        @Override
        public <T> T readValue(JsonParser parser, Class<T> valueType) {
            drain(parser);
            return null;
        }

        private void drain(JsonParser parser) {
            tokenCount = 0;
            while (parser.nextToken() != null) {
                ++tokenCount;
            }
        }
    }

    void __invoke_literalVpackObjectNamesPreserveEmbeddedNulBytesAcrossFactoryReuse() throws Exception {
        try {
            literalVpackObjectNamesPreserveEmbeddedNulBytesAcrossFactoryReuse();
        } finally {
        }
    }


    void __invoke_literalVpackObjectNamesPreserveOneThroughFourNulsAcrossFactoryReuse() throws Exception {
        try {
            literalVpackObjectNamesPreserveOneThroughFourNulsAcrossFactoryReuse();
        } finally {
        }
    }

}
