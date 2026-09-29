package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.util.RecyclerPool;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0004F1 {
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

    void getEscapeSequenceOne() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeString("\u2028");
        }
        assertArrayEquals(new byte[] { 0x43, (byte) 0xE2, (byte) 0x80, (byte) 0xA8 },
                out.toByteArray());
    }

    void getEscapeSequenceTwo() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeString("\u2029");
        }
        assertArrayEquals(new byte[] { 0x43, (byte) 0xE2, (byte) 0x80, (byte) 0xA9 },
                out.toByteArray());
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

    void __invoke_getEscapeSequenceOne() throws Exception {
        try {
            getEscapeSequenceOne();
        } finally {
        }
    }


    void __invoke_getEscapeSequenceTwo() throws Exception {
        try {
            getEscapeSequenceTwo();
        } finally {
        }
    }

}
