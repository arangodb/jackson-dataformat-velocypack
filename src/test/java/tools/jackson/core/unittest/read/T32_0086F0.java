package tools.jackson.core.unittest.read;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TreeNode;
import tools.jackson.core.util.JsonParserSequence;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0086F0 {
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

    void parserSequenceSimplePreservesContainerStateAndClosesSources() throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser p1 = factory.createParser(ARRAY_1);
                JsonParser p2 = factory.createParser(ARRAY_2);
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, p1, p2)) {
            assertEquals(2, sequence.containedParsersCount());
            assertFalse(p1.isClosed());
            assertFalse(p2.isClosed());
            assertFalse(sequence.isClosed());

            assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
            assertEquals(1, sequence.getIntValue());
            assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
            assertFalse(p1.isClosed());
            assertFalse(p2.isClosed());
            assertFalse(sequence.isClosed());

            assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
            assertTrue(p1.isClosed());
            assertFalse(p2.isClosed());
            assertFalse(sequence.isClosed());
            assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
            assertEquals(2, sequence.getIntValue());
            assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
            assertFalse(p2.isClosed());
            assertFalse(sequence.isClosed());

            assertNull(sequence.nextToken());
            assertTrue(p1.isClosed());
            assertTrue(p2.isClosed());
            assertTrue(sequence.isClosed());
        }
    }

    void parserSequenceMultiLevelFlattensNestedSequencesAndClosesEveryParser()
            throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser p1 = factory.createParser(ARRAY_1);
                JsonParser p2 = factory.createParser(new byte[] { 0x35 });
                JsonParser p3 = factory.createParser(new byte[] { 0x0A });
                JsonParserSequence first = JsonParserSequence.createFlattened(true, p1, p2);
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, first, p3)) {
            assertEquals(3, sequence.containedParsersCount());
            assertEquals(JsonToken.START_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
            assertEquals(JsonToken.END_ARRAY, sequence.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, sequence.nextToken());
            assertEquals(5, sequence.getIntValue());
            assertEquals(JsonToken.START_OBJECT, sequence.nextToken());
            assertEquals(JsonToken.END_OBJECT, sequence.nextToken());
            assertNull(sequence.nextToken());
            assertTrue(p1.isClosed());
            assertTrue(p2.isClosed());
            assertTrue(p3.isClosed());
            assertTrue(sequence.isClosed());
        }
    }

    void readValueAsTreeUsesEveryVpackParser() throws Exception {
        CountingReadContext context = new CountingReadContext();
        try (JsonParser p1 = new VPackFactory().createParser(context,
                new byte[] { 0x31, 0x32, 0x33 });
                JsonParser p2 = new VPackFactory().createParser(context,
                        new byte[] { 0x34, 0x35 });
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, p1, p2)) {
            sequence.readValueAsTree();
            assertEquals(5, context.tokenCount,
                    "readValueAsTree() must consume tokens from all VPack parsers");
        }
    }

    void readValueAsTreeUsesEveryVpackParserWithExistingToken() throws Exception {
        CountingReadContext context = new CountingReadContext();
        try (JsonParser p1 = new VPackFactory().createParser(context,
                new byte[] { 0x31, 0x32, 0x33 });
                JsonParser p2 = new VPackFactory().createParser(context,
                        new byte[] { 0x34, 0x35 });) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p1.nextToken());
            JsonParserSequence sequence = JsonParserSequence.createFlattened(true, p1, p2);
            try (sequence) {
                sequence.readValueAsTree();
                assertEquals(5, context.tokenCount,
                        "readValueAsTree() must consume an existing token and all remaining parsers");
            }
        }
    }

    void readValueAsUsesEveryVpackParser() throws Exception {
        CountingReadContext context = new CountingReadContext();
        try (JsonParser p1 = new VPackFactory().createParser(context,
                new byte[] { 0x31, 0x32, 0x33 });
                JsonParser p2 = new VPackFactory().createParser(context,
                        new byte[] { 0x34, 0x35 });
                JsonParserSequence sequence = JsonParserSequence.createFlattened(false, p1, p2)) {
            sequence.readValueAs(Object.class);
            assertEquals(5, context.tokenCount,
                    "readValueAs() must consume tokens from all VPack parsers");
        }
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

    void __invoke_parserSequenceSimplePreservesContainerStateAndClosesSources() throws Exception {
        try {
            parserSequenceSimplePreservesContainerStateAndClosesSources();
        } finally {
        }
    }


    void __invoke_parserSequenceMultiLevelFlattensNestedSequencesAndClosesEveryParser() throws Exception {
        try {
            parserSequenceMultiLevelFlattensNestedSequencesAndClosesEveryParser();
        } finally {
        }
    }


    void __invoke_readValueAsTreeUsesEveryVpackParser() throws Exception {
        try {
            readValueAsTreeUsesEveryVpackParser();
        } finally {
        }
    }


    void __invoke_readValueAsTreeUsesEveryVpackParserWithExistingToken() throws Exception {
        try {
            readValueAsTreeUsesEveryVpackParserWithExistingToken();
        } finally {
        }
    }


    void __invoke_readValueAsUsesEveryVpackParser() throws Exception {
        try {
            readValueAsUsesEveryVpackParser();
        } finally {
        }
    }

}
