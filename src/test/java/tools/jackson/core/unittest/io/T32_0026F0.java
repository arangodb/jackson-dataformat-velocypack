package tools.jackson.core.unittest.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0026F0 {

    void noOp() throws Exception {
        checkBufferRecyclerPoolImpl(JsonRecyclerPools.nonRecyclingPool(), false, true);
    }

    void threadLocal() throws Exception {
        checkBufferRecyclerPoolImpl(JsonRecyclerPools.threadLocalPool(), true, false);
    }

    void concurrentDequeue() throws Exception {
        checkBufferRecyclerPoolImpl(JsonRecyclerPools.newConcurrentDequePool(), true, true);
    }

    void bounded() throws Exception {
        checkBufferRecyclerPoolImpl(JsonRecyclerPools.newBoundedPool(1), true, true);
    }

    void pluggingPool() throws Exception {
        checkBufferRecyclerPoolImpl(new TestPool(), true, true);
    }
private static void checkBufferRecyclerPoolImpl(RecyclerPool<BufferRecycler> pool,
            boolean checkPooledResource, boolean implementsClear) throws Exception {
        VPackFactory factory = VPackFactory.builder().recyclerPool(pool).build();
        BufferRecycler used = write("test", factory, 5);

        if (checkPooledResource) {
            BufferRecycler pooled = pool.acquireAndLinkPooled();
            assertSame(used, pooled);
            pooled.releaseToPool();
        }

        if (implementsClear) {
            assertTrue(pool.clear());
            BufferRecycler replacement = pool.acquireAndLinkPooled();
            assertNotNull(replacement);
            assertNotSame(used, replacement);
            replacement.releaseToPool();
        } else {
            assertFalse(pool.clear());
        }
    }
private static BufferRecycler write(String value, VPackFactory factory,
            int expectedSize) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferRecycler used;
        try (JsonGenerator generator = factory.createGenerator(
                ObjectWriteContext.empty(), out)) {
            used = ((GeneratorBase) generator).ioContext().bufferRecycler();
            generator.writeString(value);
        }
        assertEquals(expectedSize, out.size());
        return used;
    }
private static InputStream splitAfter(byte[] input, int firstChunk) {
        return new ByteArrayInputStream(input) {
            private int remaining = firstChunk;

            @Override
            public int read() {
                int value = super.read();
                if (value >= 0 && remaining > 0) {
                    --remaining;
                }
                return value;
            }

            @Override
            public int read(byte[] buffer, int offset, int length) {
                if (remaining > 0) {
                    length = Math.min(length, remaining);
                }
                int count = super.read(buffer, offset, length);
                if (count > 0) {
                    remaining = Math.max(0, remaining - count);
                }
                return count;
            }
        };
    }
@SuppressWarnings("serial")
    private static class TestPool implements RecyclerPool<BufferRecycler> {
        private BufferRecycler bufferRecycler;

        @Override
        public BufferRecycler acquirePooled() {
            if (bufferRecycler != null) {
                BufferRecycler result = bufferRecycler;
                bufferRecycler = null;
                return result;
            }
            return new BufferRecycler();
        }

        @Override
        public void releasePooled(BufferRecycler recycler) {
            if (bufferRecycler == recycler) {
                throw new IllegalStateException("BufferRecycler released more than once");
            }
            bufferRecycler = recycler;
        }

        @Override
        public int pooledCount() {
            return bufferRecycler == null ? 0 : 1;
        }

        @Override
        public boolean clear() {
            bufferRecycler = null;
            return true;
        }
    }

    void __invoke_noOp() throws Exception {
        try {
            noOp();
        } finally {
        }
    }


    void __invoke_threadLocal() throws Exception {
        try {
            threadLocal();
        } finally {
        }
    }


    void __invoke_concurrentDequeue() throws Exception {
        try {
            concurrentDequeue();
        } finally {
        }
    }


    void __invoke_bounded() throws Exception {
        try {
            bounded();
        } finally {
        }
    }


    void __invoke_pluggingPool() throws Exception {
        try {
            pluggingPool();
        } finally {
        }
    }

}
