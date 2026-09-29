package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.TokenStreamLocation;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.util.RecyclerPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0004F2 {
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

    void basicToString() {
        assertEquals("[Source: UNKNOWN; byte offset: #10]",
                new TokenStreamLocation(null, 10L, 10L, 3, 2).toString());

        byte[] source = { 'b', 'y', 't', 'e', 's' };
        ContentReference reference = ContentReference.construct(false, source,
                ErrorReportConfiguration.defaults());
        assertEquals("[Source: (byte[])[5 bytes]; byte offset: #10]",
                new TokenStreamLocation(reference, 10L, 10L, -1, -1).toString());

        ContentReference streamReference = ContentReference.construct(false,
                new ByteArrayInputStream(new byte[0]), ErrorReportConfiguration.defaults());
        assertEquals("[Source: (ByteArrayInputStream); byte offset: #10]",
                new TokenStreamLocation(streamReference, 10L, 10L, -1, -1).toString());
    }

    void basics() {
        byte[] source = { 0x18 };
        ContentReference reference = ContentReference.construct(false, source,
                ErrorReportConfiguration.defaults());
        TokenStreamLocation first = new TokenStreamLocation(reference, 10L, 10L, 3, 2);
        TokenStreamLocation second = new TokenStreamLocation(null, 10L, 10L, 3, 2);

        assertEquals(first, first);
        assertFalse(first.equals(null));
        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
        assertTrue(first.hashCode() != 0);
        assertTrue(second.hashCode() != 0);
    }

    void disableSourceInclusion() throws Exception {
        byte[] valid = { 0x18 };
        for (Object source : new Object[] { valid, new ByteArrayInputStream(valid) }) {
            try (JsonParser parser = source instanceof byte[] bytes
                    ? new VPackFactory().createParser(bytes)
                    : new VPackFactory().createParser((ByteArrayInputStream) source)) {
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                TokenStreamLocation location = parser.currentTokenLocation();
                assertNull(location.contentReference().getRawContent());
                assertTrue(location.sourceDescription().startsWith("REDACTED"));
            }
        }
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

    void __invoke_basicToString() throws Exception {
        try {
            basicToString();
        } finally {
        }
    }


    void __invoke_basics() throws Exception {
        try {
            basics();
        } finally {
        }
    }


    void __invoke_disableSourceInclusion() throws Exception {
        try {
            disableSourceInclusion();
        } finally {
        }
    }

}
