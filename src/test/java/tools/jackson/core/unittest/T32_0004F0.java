package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonPointer;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0004F0 {

    void location() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(new byte[] { 0x18 })) {
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            TokenStreamLocation original = parser.currentLocation();

            TokenStreamLocation restored = deserialize(serialize(original));
            assertNotNull(restored);
            assertEquals(original.getLineNr(), restored.getLineNr());
            assertEquals(original.getColumnNr(), restored.getColumnNr());
            assertEquals(original.getByteOffset(), restored.getByteOffset());
        }
    }

    void parseException() throws Exception {
        StreamReadException exception = null;
        try (JsonParser parser = new VPackFactory().createParser(new byte[] { 0x15 })) {
            try {
                parser.nextToken();
                fail("Reserved VPack marker must fail");
            } catch (StreamReadException e) {
                exception = e;
            }
        }
        assertNotNull(exception);
        assertNotNull(deserialize(serialize(exception)));
    }

    void pointerSerializationEmpty() throws Exception {
        JsonPointer empty = JsonPointer.empty();
        JsonPointer restored = deserialize(serialize(empty));
        assertSame(empty, restored);
    }

    void pointerSerializationNonEmpty() throws Exception {
        JsonPointer original = JsonPointer.compile("/Image/15/name");
        JsonPointer copy = deserialize(serialize(original));
        assertNotSame(original, copy);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());

        JsonPointer branch = original.tail();
        assertEquals("/15/name", branch.toString());
        copy = deserialize(serialize(branch));
        assertEquals(branch, copy);
        assertEquals("/15/name", copy.toString());

        JsonPointer leaf = branch.tail();
        assertEquals("/name", leaf.toString());
        copy = deserialize(serialize(leaf));
        assertEquals(leaf, copy);
        assertEquals("/name", copy.toString());
    }

    void prettyPrinter() throws Exception {
        try (JsonGenerator generator = new VPackFactory()
                .createGenerator(new ByteArrayOutputStream())) {
            assertNull(generator.getPrettyPrinter());
        }
    }

    void recyclerPools() throws Exception {
        testRecyclerPoolGlobal(JsonRecyclerPools.nonRecyclingPool());
        testRecyclerPoolGlobal(JsonRecyclerPools.threadLocalPool());
        testRecyclerPoolGlobal(JsonRecyclerPools.sharedConcurrentDequePool());
        JsonRecyclerPools.BoundedPool bounded = (JsonRecyclerPools.BoundedPool)
                testRecyclerPoolGlobal(JsonRecyclerPools.sharedBoundedPool());
        assertEquals(RecyclerPool.BoundedPoolBase.DEFAULT_CAPACITY,
                bounded.capacity());

        testRecyclerPoolNonShared(JsonRecyclerPools.newConcurrentDequePool());
        bounded = (JsonRecyclerPools.BoundedPool)
                testRecyclerPoolNonShared(JsonRecyclerPools.newBoundedPool(250));
        assertEquals(250, bounded.capacity());
    }
private <T extends RecyclerPool<?>> T testRecyclerPoolGlobal(T pool) throws Exception {
        T result = deserialize(serialize(pool));
        assertNotNull(result);
        assertSame(pool.getClass(), result.getClass());
        return result;
    }
private <T extends RecyclerPool<?>> T testRecyclerPoolNonShared(T pool) throws Exception {
        T result = deserialize(serialize(pool));
        assertNotNull(result);
        assertEquals(pool.getClass(), result.getClass());
        assertNotSame(pool, result);
        return result;
    }

    void sourceReference() throws Exception {
        byte[] source = { 0x18 };
        ObjectReadContext context = includeSourceContext();
        ContentReference reference;
        try (JsonParser parser = new VPackFactory().createParser(context,
                new ByteArrayInputStream(source))) {
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
            reference = parser.currentTokenLocation().contentReference();
            assertSame(ByteArrayInputStream.class, reference.getRawContent().getClass());
        }

        ContentReference restored = deserialize(serialize(reference));
        assertNotNull(restored);
        assertSame(ContentReference.unknown(), restored);
    }
private static ObjectReadContext includeSourceContext() {
        return new ObjectReadContext.Base() {
            @Override
            public int getStreamReadFeatures(int defaults) {
                return defaults | StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.getMask();
            }
        };
    }
private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }
private static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            @SuppressWarnings("unchecked")
            T result = (T) input.readObject();
            return result;
        }
    }

    void __invoke_location() throws Exception {
        try {
            location();
        } finally {
        }
    }


    void __invoke_parseException() throws Exception {
        try {
            parseException();
        } finally {
        }
    }


    void __invoke_pointerSerializationEmpty() throws Exception {
        try {
            pointerSerializationEmpty();
        } finally {
        }
    }


    void __invoke_pointerSerializationNonEmpty() throws Exception {
        try {
            pointerSerializationNonEmpty();
        } finally {
        }
    }


    void __invoke_prettyPrinter() throws Exception {
        try {
            prettyPrinter();
        } finally {
        }
    }


    void __invoke_recyclerPools() throws Exception {
        try {
            recyclerPools();
        } finally {
        }
    }


    void __invoke_sourceReference() throws Exception {
        try {
            sourceReference();
        } finally {
        }
    }

}
