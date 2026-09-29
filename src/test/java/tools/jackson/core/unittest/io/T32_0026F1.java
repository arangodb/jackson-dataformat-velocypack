package tools.jackson.core.unittest.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.base.GeneratorBase;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.RecyclerPool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0026F1 {

    void parameterValidation() {
        VPackFactory factory = new VPackFactory();
        assertThrows(NullPointerException.class,
                () -> factory.createParser((byte[]) null));
        assertThrows(StreamReadException.class,
                () -> factory.createParser(new byte[3], -1, 1));
        assertThrows(StreamReadException.class,
                () -> factory.createParser(new byte[3], 0, -1));
        assertThrows(StreamReadException.class,
                () -> factory.createParser(new byte[3], 2, 2));
    }

    void simple() throws Exception {
        // Independent literal roots: [1, 2, 3], followed by the string "FGHIJ".
        byte[] roots = {
                0x02, 0x05, 0x31, 0x32, 0x33,
                0x45, 0x46, 0x47, 0x48, 0x49, 0x4A
        };
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), splitAfter(roots, 5))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(1, parser.nextIntValue(-1));
            assertEquals(2, parser.nextIntValue(-1));
            assertEquals(3, parser.nextIntValue(-1));
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("FGHIJ", parser.getString());
            assertNull(parser.nextToken());
        }
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

    void __invoke_parameterValidation() throws Exception {
        try {
            parameterValidation();
        } finally {
        }
    }


    void __invoke_simple() throws Exception {
        try {
            simple();
        } finally {
        }
    }

}
