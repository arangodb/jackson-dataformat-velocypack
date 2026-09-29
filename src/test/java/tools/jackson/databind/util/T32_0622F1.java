package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Predicate;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0622F1 {

    // Provenance: BufferRecyclersDatabindTest#testParserWithHybridPool().
    // Provenance: BufferRecyclersDatabindTest#testGeneratorWithHybridPool().
    void parserAndGeneratorWithHybridPoolVpack() throws Exception {
        RecyclerPool<BufferRecycler> pool = new HybridTestPool();
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();

        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                VPackWireFixtureTest.hex(
                        "14 10 41 61 28 7b 41 62 46 66 6f 6f 62 61 72 02"))) {
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

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(ObjectWriteContext.empty(), output)) {
            generator.writeStartObject();
            generator.writeNumberProperty("a", -42);
            generator.writeStringProperty("b", "bogus");
            generator.writeEndObject();
        }
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 02 41 61 20 d6 41 62 45 62 6f 67 75 73 03 07"),
                output.toByteArray());
    }
static class HybridTestPool implements RecyclerPool<BufferRecycler> {
        private static final long serialVersionUID = 1L;
        private static final Predicate<Thread> IS_VIRTUAL = findIsVirtualPredicate();

        private final RecyclerPool<BufferRecycler> nativePool = JsonRecyclerPools.threadLocalPool();
        private final RecyclerPool<BufferRecycler> virtualPool = JsonRecyclerPools.newConcurrentDequePool();

        @Override
        public BufferRecycler acquirePooled() {
            return (IS_VIRTUAL != null && IS_VIRTUAL.test(Thread.currentThread())
                    ? virtualPool : nativePool).acquirePooled();
        }

        @Override
        public void releasePooled(BufferRecycler pooled) {
            (IS_VIRTUAL != null && IS_VIRTUAL.test(Thread.currentThread())
                    ? virtualPool : nativePool).releasePooled(pooled);
        }

        private static Predicate<Thread> findIsVirtualPredicate() {
            try {
                MethodHandle isVirtual = MethodHandles.publicLookup().findVirtual(Thread.class,
                        "isVirtual", MethodType.methodType(boolean.class));
                return thread -> {
                    try {
                        return (boolean) isVirtual.invoke(thread);
                    } catch (Throwable e) {
                        throw new IllegalStateException(e);
                    }
                };
            } catch (Exception e) {
                return null;
            }
        }
    }

    void __invoke_parserAndGeneratorWithHybridPoolVpack() throws Exception {
        try {
            parserAndGeneratorWithHybridPoolVpack();
        } finally {
        }
    }

}
