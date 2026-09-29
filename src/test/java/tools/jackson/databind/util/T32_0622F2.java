package tools.jackson.databind.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;
import tools.jackson.databind.util.ByteBufferBackedInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0622F2 {

    // Provenance: ByteBufferUtilsTest#testByteBufferInput().
    void byteBufferBackedInputVpack() throws Exception {
        byte[] input = { 1, 2, 3 };
        try (ByteBufferBackedInputStream wrapped =
                new ByteBufferBackedInputStream(ByteBuffer.wrap(input))) {
            assertEquals(3, wrapped.available());
            assertEquals(1, wrapped.read());
            byte[] buffer = new byte[10];
            assertEquals(2, wrapped.read(buffer, 0, 5));
            assertEquals(-1, wrapped.read(buffer, 0, 5));
        }

        try (JsonParser parser = new VPackFactory().createParser(ObjectReadContext.empty(),
                new ByteBufferBackedInputStream(ByteBuffer.wrap(
                        VPackWireFixtureTest.hex("41 61"))))) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("a", parser.getString());
        }
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

    void __invoke_byteBufferBackedInputVpack() throws Exception {
        try {
            byteBufferBackedInputVpack();
        } finally {
        }
    }

}
