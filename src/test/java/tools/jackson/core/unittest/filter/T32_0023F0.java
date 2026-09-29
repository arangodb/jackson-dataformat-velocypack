package tools.jackson.core.unittest.filter;

import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.filter.FilteringParserDelegate;
import tools.jackson.core.filter.TokenFilter;
import tools.jackson.core.filter.TokenFilter.Inclusion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0023F0 {

    void filterWithEmptyArray() throws Exception {
        try (FilteringParserDelegate parser = originalEmptyArrayFilter(FILTERED_EMPTY_ARRAY)) {
            assertToken(JsonToken.START_ARRAY, parser.nextToken());
            assertToken(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertSkippingForSingleWithPath(boolean useNextName)
            throws Exception {
        try (FilteringParserDelegate parser = filteredParser(PATH_FILTERED)) {
            assertToken(JsonToken.START_OBJECT, parser.nextToken());
            assertTrue(parser.isExpectedStartObjectToken());

            if (useNextName) {
                assertEquals("value", parser.nextName());
                assertEquals("value", parser.getString());
                assertToken(JsonToken.START_OBJECT, parser.nextToken());
                assertTrue(parser.isExpectedStartObjectToken());
                assertEquals("a", parser.nextName());
                assertEquals("a", parser.getString());
                assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(99, parser.getIntValue());
                assertNull(parser.nextName());
                assertEquals(JsonToken.END_OBJECT, parser.currentToken());
            } else {
                assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("value", parser.currentName());
                assertEquals("value", parser.getString());
                assertToken(JsonToken.START_OBJECT, parser.nextToken());
                assertTrue(parser.isExpectedStartObjectToken());
                assertToken(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("a", parser.currentName());
                assertEquals("a", parser.getString());
                assertToken(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(99, parser.getIntValue());
                assertToken(JsonToken.END_OBJECT, parser.nextToken());
            }

            assertToken(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static FilteringParserDelegate filteredParser(byte[] input) {
        return new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new NoTypeFilter(), Inclusion.INCLUDE_ALL_AND_PATH, true);
    }
private static FilteringParserDelegate originalEmptyArrayFilter(byte[] input) {
        return new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new OnePropertyFilter1418Original(), Inclusion.INCLUDE_ALL_AND_PATH, true);
    }
private static FilteringParserDelegate includeAllParser(byte[] input) {
        return new FilteringParserDelegate(
                new VPackFactory().createParser(ObjectReadContext.empty(), input),
                new IncludeAllFilter(), Inclusion.INCLUDE_ALL_AND_PATH, true);
    }
private static void assertToken(JsonToken expected, JsonToken actual) {
        assertEquals(expected, actual);
    }
private static final byte[] EMPTY_ARRAY = { 0x01 };
private static final byte[] EMPTY_OBJECT = { 0x0a };
private static final byte[] FILTERED_EMPTY_ARRAY = {
            0x13, 0x15,
            0x14, 0x08, 0x43, 't', 'w', 'o', 0x32, 0x01,
            0x14, 0x0a, 0x45, 't', 'h', 'r', 'e', 'e', 0x33, 0x01,
            0x02
    };
private static final byte[] ROOT_FILTERED = {
            0x14, 0x15,
            0x45, '@', 't', 'y', 'p', 'e', 0x43, 'y', 'y', 'y',
            0x45, 'v', 'a', 'l', 'u', 'e', 0x28, 0x0c,
            0x02
    };
private static final byte[] NESTED_FILTERED = {
            0x14, 0x1a,
            0x45, 'v', 'a', 'l', 'u', 'e',
            0x14, 0x11,
            0x45, '@', 't', 'y', 'p', 'e', 0x43, 'y', 'y', 'y',
            0x41, 'a', 0x28, 0x0c, 0x02,
            0x01
    };
private static final byte[] PATH_FILTERED = {
            0x14, 0x24,
            0x45, '@', 't', 'y', 'p', 'e', 0x43, 'x', 'x', 'x',
            0x45, 'v', 'a', 'l', 'u', 'e',
            0x14, 0x11,
            0x45, '@', 't', 'y', 'p', 'e', 0x43, 'y', 'y', 'y',
            0x41, 'a', 0x28, 0x63, 0x02,
            0x02
    };
private static final class IncludeAllFilter extends TokenFilter {
        @Override
        public TokenFilter includeProperty(String name) {
            return TokenFilter.INCLUDE_ALL;
        }

        @Override
        public boolean includeEmptyArray(boolean contentsFiltered) {
            return true;
        }

        @Override
        public boolean includeEmptyObject(boolean contentsFiltered) {
            return true;
        }
    }
private static final class NoTypeFilter extends TokenFilter {
        @Override
        public TokenFilter includeProperty(String name) {
            return "@type".equals(name) ? null : this;
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

    void __invoke_filterWithEmptyArray() throws Exception {
        try {
            filterWithEmptyArray();
        } finally {
        }
    }

}
