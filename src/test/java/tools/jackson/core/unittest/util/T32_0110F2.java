package tools.jackson.core.unittest.util;

import java.util.Arrays;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.RecyclerPool;
import tools.jackson.core.util.TextBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0110F2 {
private static final byte[] PARSER_INPUT = VPackWireFixtureTest.hex(
            "14 10 41 61 28 7b 41 62 46 66 6f 6f 62 61 72 02");
private static final byte[] GENERATOR_EXPECTED = VPackWireFixtureTest.hex(
            "0b 12 02 41 61 20 d6 41 62 46 62 61 72 66 6f 6f 03 07");
private static void testParser(RecyclerPool<BufferRecycler> pool,
            Integer expectedBefore, Integer expectedAfter) throws Exception {
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();
        if (expectedBefore != null) {
            assertEquals(expectedBefore, pool.pooledCount());
        }

        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), PARSER_INPUT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(123, parser.getIntValue());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("foobar", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }

        if (expectedAfter != null) {
            assertEquals(expectedAfter, pool.pooledCount());
        }
    }

    void appendCharArrayVpack() throws Exception {
        try (IOContext ioContext = makeConstrainedContext(TextBuffer.MIN_SEGMENT_LEN)) {
            TextBuffer constrained = ioContext.constructReadConstrainedTextBuffer();
            char[] chars = repeatedChars(TextBuffer.MIN_SEGMENT_LEN);
            constrained.append(chars, 0, chars.length);
            assertThrows(StreamConstraintsException.class, () -> {
                constrained.append(chars, 0, chars.length);
                constrained.contentsAsString();
            });
        }
    }

    void appendStringVpack() throws Exception {
        try (IOContext ioContext = makeConstrainedContext(TextBuffer.MIN_SEGMENT_LEN)) {
            TextBuffer constrained = ioContext.constructReadConstrainedTextBuffer();
            String chars = new String(repeatedChars(TextBuffer.MIN_SEGMENT_LEN));
            constrained.append(chars, 0, chars.length());
            assertThrows(StreamConstraintsException.class, () -> {
                constrained.append(chars, 0, chars.length());
                constrained.contentsAsString();
            });
        }
    }

    void appendSingleVpack() throws Exception {
        try (IOContext ioContext = makeConstrainedContext(TextBuffer.MIN_SEGMENT_LEN)) {
            TextBuffer constrained = ioContext.constructReadConstrainedTextBuffer();
            char[] chars = repeatedChars(TextBuffer.MIN_SEGMENT_LEN);
            constrained.append(chars, 0, chars.length);
            assertThrows(StreamConstraintsException.class, () -> {
                constrained.append('x');
                constrained.contentsAsString();
            });
        }
    }
private static char[] repeatedChars(int length) {
        char[] chars = new char[length];
        Arrays.fill(chars, 'A');
        return chars;
    }
private static IOContext makeConstrainedContext(int maxStringLength) {
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxStringLength(maxStringLength)
                .build();
        return new IOContext(
                constraints,
                StreamWriteConstraints.defaults(),
                ErrorReportConfiguration.defaults(),
                new BufferRecycler(),
                ContentReference.rawReference("N/A"), true,
                JsonEncoding.UTF8);
    }

    void __invoke_appendCharArrayVpack() throws Exception {
        try {
            appendCharArrayVpack();
        } finally {
        }
    }


    void __invoke_appendStringVpack() throws Exception {
        try {
            appendStringVpack();
        } finally {
        }
    }


    void __invoke_appendSingleVpack() throws Exception {
        try {
            appendSingleVpack();
        } finally {
        }
    }

}
