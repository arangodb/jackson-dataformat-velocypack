package tools.jackson.databind.misc;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.databind.util.TokenBuffer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0460F1 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] MINIMAL_ARRAY = VPackWireFixtureTest.hex(
            "02 04 28 2a");
private static final byte[] MINIMAL_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 46 61 6e 73 77 65 72 28 2a 01");
private static final byte[] FULL_DOCUMENT = VPackWireFixtureTest.hex(
            "14 48"
          + "41 61 28 7b"
          + "45 61 72 72 61 79"
          + "13 17 31 32 02 03 33 35"
          + "14 0e 49 6f 62 49 6e 41 72 72 61 79 34 01 05"
          + "42 6f 62"
          + "14 1e 45 66 69 72 73 74 13 05 19 1a 02"
          + "46 73 65 63 6f 6e 64 14 09 43 73 75 62 28 25 01 02"
          + "41 62 1a"
          + "04");
private static final byte[] PROPERTY_MISMATCH = VPackWireFixtureTest.hex(
            "14 24"
          + "4c 61 61 61 61 62 62 62 62 63 63 63 63 42 76 33"
          + "4d 61 61 61 61 62 62 62 62 63 63 63 63 32 42 76 34"
          + "02");

    // Provenance: PropertyMismatch5372Test#testIssue5372Bytes() and
    // PropertyMismatch5372Test#testIssue5372Chars().
    void testIssue5372PropertyNamesVpack() throws Exception {
        assertProperties(MAPPER.readValue(PROPERTY_MISMATCH, TestObject5372.class));
        assertProperties(MAPPER.readValue(new ByteArrayInputStream(PROPERTY_MISMATCH),
                TestObject5372.class));
    }
private TokenBuffer readAsTokenBuffer(byte[] document) throws IOException {
        try (JsonParser parser = MAPPER.tokenStreamFactory().createParser(document)) {
            parser.nextToken();
            try (TokenBuffer buffer = TokenBuffer.forBuffering(parser, ObjectReadContext.empty())) {
                buffer.copyCurrentStructure(parser);
                return buffer.overrideParentContext(null);
            }
        }
    }
private void testSimpleArrayUsingPathAsPointer(JsonParser parser) throws Exception {
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());

        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inArray());

        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals("/0", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());
        assertNull(parser.nextToken());
    }
private void testSimpleObjectUsingPathAsPointer(JsonParser parser) throws Exception {
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inObject());

        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals("/answer", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(42, parser.getIntValue());
        assertEquals("/answer", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());
        assertNull(parser.nextToken());
    }
private void testFullDocumentUsingPathAsPointer(JsonParser parser) throws Exception {
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());

        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inObject());

        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // a
        assertEquals("/a", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals("/a", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // array
        assertEquals("/array", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals("/array", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken()); // 1
        assertEquals("/array/0", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken()); // 2
        assertEquals("/array/1", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals("/array/2", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken()); // 3
        assertEquals("/array/2/0", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals("/array/2", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken()); // 5
        assertEquals("/array/3", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals("/array/4", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // obInArray
        assertEquals("/array/4/obInArray", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals("/array/4/obInArray", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals("/array/4", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals("/array", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // ob
        assertEquals("/ob", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals("/ob", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // first
        assertEquals("/ob/first", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals("/ob/first", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        assertEquals("/ob/first/0", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        assertEquals("/ob/first/1", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertEquals("/ob/first", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // second
        assertEquals("/ob/second", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals("/ob/second", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // sub
        assertEquals("/ob/second/sub", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals("/ob/second/sub", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals("/ob/second", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertEquals("/ob", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken()); // b
        assertEquals("/b", parser.streamReadContext().pathAsPointer().toString());
        assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        assertEquals("/b", parser.streamReadContext().pathAsPointer().toString());

        assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        assertSame(JsonPointer.empty(), parser.streamReadContext().pathAsPointer());
        assertTrue(parser.streamReadContext().inRoot());
        assertNull(parser.nextToken());
    }
private void assertProperties(TestObject5372 result) {
        assertEquals("v3", result.getAaaabbbbcccc());
        assertEquals("v4", result.getAaaabbbbcccc2());
    }
static class TestObject5372 {
        private String aaaabbbbcccc;
        private String aaaabbbbcccc2;

        public String getAaaabbbbcccc2() { return aaaabbbbcccc2; }
        public void setAaaabbbbcccc2(String value) { aaaabbbbcccc2 = value; }
        public String getAaaabbbbcccc() { return aaaabbbbcccc; }
        public void setAaaabbbbcccc(String value) { aaaabbbbcccc = value; }
    }

    void __invoke_testIssue5372PropertyNamesVpack() throws Exception {
        try {
            testIssue5372PropertyNamesVpack();
        } finally {
        }
    }

}
