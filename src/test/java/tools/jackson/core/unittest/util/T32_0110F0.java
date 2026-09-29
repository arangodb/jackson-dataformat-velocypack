package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0110F0 {
private static final byte[] PARSER_INPUT = VPackWireFixtureTest.hex(
            "14 10 41 61 28 7b 41 62 46 66 6f 6f 62 61 72 02");
private static final byte[] GENERATOR_EXPECTED = VPackWireFixtureTest.hex(
            "0b 12 02 41 61 20 d6 41 62 46 62 61 72 66 6f 6f 03 07");

    void parserWithThreadLocalPoolVpack() throws Exception {
        testParser(JsonRecyclerPools.threadLocalPool(), -1, -1);
    }

    void parserWithNopLocalPoolVpack() throws Exception {
        testParser(JsonRecyclerPools.nonRecyclingPool(), 0, 0);
    }

    void parserWithDequeuPoolVpack() throws Exception {
        testParser(JsonRecyclerPools.newConcurrentDequePool(), 0, 1);
        testParser(JsonRecyclerPools.sharedConcurrentDequePool(), null, null);
    }

    void parserWithBoundedPoolVpack() throws Exception {
        testParser(JsonRecyclerPools.newBoundedPool(5), 0, 1);
        testParser(JsonRecyclerPools.sharedBoundedPool(), null, null);
    }
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

    void generatorWithThreadLocalPoolVpack() throws Exception {
        RecyclerPool<BufferRecycler> pool = JsonRecyclerPools.threadLocalPool();
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();
        assertEquals(-1, pool.pooledCount());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(
                ObjectWriteContext.empty(), output)) {
            generator.writeStartObject();
            generator.writeNumberProperty("a", -42);
            generator.writeStringProperty("b", "barfoo");
            generator.writeEndObject();
        }

        assertEquals(-1, pool.pooledCount());
        assertArrayEquals(GENERATOR_EXPECTED, output.toByteArray(),
                () -> "actual=" + Arrays.toString(output.toByteArray()));
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

    void __invoke_parserWithThreadLocalPoolVpack() throws Exception {
        try {
            parserWithThreadLocalPoolVpack();
        } finally {
        }
    }


    void __invoke_parserWithNopLocalPoolVpack() throws Exception {
        try {
            parserWithNopLocalPoolVpack();
        } finally {
        }
    }


    void __invoke_parserWithDequeuPoolVpack() throws Exception {
        try {
            parserWithDequeuPoolVpack();
        } finally {
        }
    }


    void __invoke_parserWithBoundedPoolVpack() throws Exception {
        try {
            parserWithBoundedPoolVpack();
        } finally {
        }
    }


    void __invoke_generatorWithThreadLocalPoolVpack() throws Exception {
        try {
            generatorWithThreadLocalPoolVpack();
        } finally {
        }
    }

}
