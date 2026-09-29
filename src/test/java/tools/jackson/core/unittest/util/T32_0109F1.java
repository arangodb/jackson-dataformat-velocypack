package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonGeneratorDelegate;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0109F1 {
private static final byte[] PARSER_INPUT = VPackWireFixtureTest.hex(
            "06 1a 05 31 1a 18 0b 0a 01 41 61 43 66 6f 6f 03 "
            + "44 41 51 49 3d 03 04 05 06 10");
private static final byte[] COPY_INPUT = VPackWireFixtureTest.hex(
            "13 1e 14 14 41 61 13 0b 31 32 14 06 41 62 33 01 03 "
            + "41 63 41 64 02 14 06 41 65 19 01 18 03");
private static final byte[] SIMPLE_COPY_INPUT = VPackWireFixtureTest.hex(
            "0b 12 02 41 61 20 d6 41 62 46 66 6f 6f 62 61 72 03 07");
private static final byte[] COPY_EXPECTED = VPackWireFixtureTest.hex(
            "06 4c 03 "
            + "0b 35 04 "
            + "46 61 2d 74 65 73 74 1a 41 61 "
            + "06 18 03 31 32 "
            + "0b 10 02 46 62 2d 74 65 73 74 1a 41 62 33 0b 03 03 04 05 "
            + "46 63 2d 74 65 73 74 1a 41 63 41 64 "
            + "0b 03 2d 25 "
            + "0b 10 02 46 65 2d 74 65 73 74 1a 41 65 19 0b 03 "
            + "18 "
            + "03 38 48");
private static final byte[] SIMPLE_GENERATOR_EXPECTED = VPackWireFixtureTest.hex(
            "0b 12 02 41 61 20 d6 41 62 46 62 61 72 66 6f 6f 03 07");

    void copyWithThreadLocalPoolVpack() throws Exception {
        testCopy(JsonRecyclerPools.threadLocalPool());
    }

    void copyWithNopLocalPoolVpack() throws Exception {
        testCopy(JsonRecyclerPools.nonRecyclingPool());
    }

    void copyWithDequeuPoolVpack() throws Exception {
        testCopy(JsonRecyclerPools.newConcurrentDequePool());
        testCopy(JsonRecyclerPools.sharedConcurrentDequePool());
    }

    void copyWithBoundedPoolVpack() throws Exception {
        testCopy(JsonRecyclerPools.newBoundedPool(5));
        testCopy(JsonRecyclerPools.sharedBoundedPool());
    }

    void generatorWithNopLocalPoolVpack() throws Exception {
        testGenerator(JsonRecyclerPools.nonRecyclingPool(), 0, 0);
    }

    void generatorWithDequeuPoolVpack() throws Exception {
        testGenerator(JsonRecyclerPools.newConcurrentDequePool(), 0, 1);
        testGenerator(JsonRecyclerPools.sharedConcurrentDequePool(), null, null);
    }

    void generatorWithBoundedPoolVpack() throws Exception {
        testGenerator(JsonRecyclerPools.newBoundedPool(5), 0, 1);
        testGenerator(JsonRecyclerPools.sharedBoundedPool(), null, null);
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertRawUnsupported(RawCall call) throws IOException {
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(
                generator(new ByteArrayOutputStream()));
        delegate.writeStartArray();
        try {
            assertThrows(UnsupportedOperationException.class, () -> call.run(delegate));
        } finally {
            try {
                delegate.close();
            } catch (RuntimeException ignored) {
                // The failed generator retains its primary unsupported-operation failure.
            }
        }
    }
private static void testCopy(RecyclerPool<BufferRecycler> pool) throws Exception {
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();
        JsonParser parser = factory.createParser(ObjectReadContext.empty(), SIMPLE_COPY_INPUT);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = factory.createGenerator(ObjectWriteContext.empty(), output);

        while (parser.nextToken() != null) {
            generator.copyCurrentEvent(parser);
        }

        parser.close();
        generator.close();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 02 41 61 20 d6 41 62 46 66 6f 6f 62 61 72 03 07"),
                output.toByteArray());
    }
private static void testGenerator(RecyclerPool<BufferRecycler> pool,
            Integer expectedBefore, Integer expectedAfter) throws Exception {
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();
        if (expectedBefore != null) {
            assertEquals(expectedBefore, pool.pooledCount());
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(
                ObjectWriteContext.empty(), output)) {
            generator.writeStartObject();
            generator.writeNumberProperty("a", -42);
            generator.writeStringProperty("b", "barfoo");
            generator.writeEndObject();
        }

        if (expectedAfter != null) {
            assertEquals(expectedAfter, pool.pooledCount());
        }
        assertArrayEquals(SIMPLE_GENERATOR_EXPECTED, output.toByteArray());
    }
private static void assertNotNull(Object value) {
        if (value == null) {
            throw new AssertionError("expected non-null value");
        }
    }
@FunctionalInterface
    private interface RawCall {
        void run(JsonGeneratorDelegate delegate) throws IOException;
    }

    void __invoke_copyWithThreadLocalPoolVpack() throws Exception {
        try {
            copyWithThreadLocalPoolVpack();
        } finally {
        }
    }


    void __invoke_copyWithNopLocalPoolVpack() throws Exception {
        try {
            copyWithNopLocalPoolVpack();
        } finally {
        }
    }


    void __invoke_copyWithDequeuPoolVpack() throws Exception {
        try {
            copyWithDequeuPoolVpack();
        } finally {
        }
    }


    void __invoke_copyWithBoundedPoolVpack() throws Exception {
        try {
            copyWithBoundedPoolVpack();
        } finally {
        }
    }


    void __invoke_generatorWithNopLocalPoolVpack() throws Exception {
        try {
            generatorWithNopLocalPoolVpack();
        } finally {
        }
    }


    void __invoke_generatorWithDequeuPoolVpack() throws Exception {
        try {
            generatorWithDequeuPoolVpack();
        } finally {
        }
    }


    void __invoke_generatorWithBoundedPoolVpack() throws Exception {
        try {
            generatorWithBoundedPoolVpack();
        } finally {
        }
    }

}
