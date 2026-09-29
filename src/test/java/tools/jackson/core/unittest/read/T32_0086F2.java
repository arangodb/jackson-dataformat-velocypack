package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.TreeNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0086F2 {
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

    void nextValueTraversesLiteralVpackArraysObjectsAndMixedNesting() throws Exception {
        byte[] object = {
                0x14, 0x0C,
                0x41, 0x33, 0x33, 0x41, 0x34, 0x34, 0x41, 0x35, 0x35,
                0x03
        };
        byte[] mixed = {
                0x06, 0x0E, 0x03,
                0x1A, 0x01, 0x14, 0x06, 0x41, 0x61, 0x33, 0x01,
                0x03, 0x04, 0x05
        };
        assertNextValue(new VPackFactory().createParser(
                new byte[] { 0x02, 0x06, 0x31, 0x32, 0x33, 0x34 }));
        assertNextValue(new VPackFactory().createParser(new ByteArrayInputStream(
                new byte[] { 0x02, 0x06, 0x31, 0x32, 0x33, 0x34 })));
        assertObjectNextValue(new VPackFactory().createParser(object));
        assertObjectNextValue(new VPackFactory().createParser(new ByteArrayInputStream(object)));
        assertMixedNextValue(new VPackFactory().createParser(mixed));
        assertMixedNextValue(new VPackFactory().createParser(new ByteArrayInputStream(mixed)));
    }

    void nextValueTracksNamesAcrossLiteralVpackNestedObjectsAndArrays() throws Exception {
        byte[] nestedObject = {
                0x14, 0x11,
                0x41, 0x61,
                0x14, 0x09, 0x41, 0x62, 0x1A, 0x41, 0x63, 0x19, 0x02,
                0x41, 0x64, 0x33,
                0x02
        };
        byte[] nestedArray = { 0x14, 0x08, 0x41, 0x61, 0x02, 0x03, 0x19, 0x01 };
        assertNestedNextValue(new VPackFactory().createParser(nestedObject));
        assertNestedNextValue(new VPackFactory().createParser(new ByteArrayInputStream(
                nestedObject)));
        assertNestedArrayNextValue(new VPackFactory().createParser(nestedArray));
        assertNestedArrayNextValue(new VPackFactory().createParser(new ByteArrayInputStream(
                nestedArray)));
    }

    void parserIsClosedAfterExplicitCloseOrCompleteLiteralVpackInput() throws Exception {
        for (int source = 0; source < 2; ++source) {
            for (boolean partial : new boolean[] { true, false }) {
                JsonParser parser = source == 0
                        ? new VPackFactory().createParser(ARRAY_123)
                        : new VPackFactory().createParser(new ByteArrayInputStream(ARRAY_123));
                try {
                    assertFalse(parser.isClosed());
                    assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertFalse(parser.isClosed());
                    if (partial) {
                        parser.close();
                        assertTrue(parser.isClosed());
                    } else {
                        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
                        assertNull(parser.nextToken());
                        assertTrue(parser.isClosed());
                    }
                } finally {
                    parser.close();
                }
            }
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

    void __invoke_nextValueTraversesLiteralVpackArraysObjectsAndMixedNesting() throws Exception {
        try {
            nextValueTraversesLiteralVpackArraysObjectsAndMixedNesting();
        } finally {
        }
    }


    void __invoke_nextValueTracksNamesAcrossLiteralVpackNestedObjectsAndArrays() throws Exception {
        try {
            nextValueTracksNamesAcrossLiteralVpackNestedObjectsAndArrays();
        } finally {
        }
    }


    void __invoke_parserIsClosedAfterExplicitCloseOrCompleteLiteralVpackInput() throws Exception {
        try {
            parserIsClosedAfterExplicitCloseOrCompleteLiteralVpackInput();
        } finally {
        }
    }

}
