package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;

import static org.junit.jupiter.api.Assertions.*;

/** Deterministic ownership checks: no weak references, GC timing or heap guesses. */
class VPackParserRetentionRegressionTest {
    // Indexed object {"a":"YQ=="}, with one index entry pointing to offset 3.
    private static final byte[] OBJECT = {
            0x0B, 11, 1, 0x41, 'a', 0x44, 'Y', 'Q', '=', '=', 3
    };
    private static final byte[] UINT64 = {
            0x2F, -1, -1, -1, -1, -1, -1, -1, -1
    };

    @Test
    void earlyCloseDropsFramesContextsAndDecodedCaches() throws Exception {
        TrackingInput input = new TrackingInput(OBJECT, false);
        VPackParser parser = (VPackParser) new VPackFactory().createParser(input);
        parser.assignCurrentValue(new Object());
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        parser.assignCurrentValue(new Object());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        assertArrayEquals("YQ==".toCharArray(), parser.getStringCharacters());
        assertArrayEquals(new byte[] { 'a' }, parser.getBinaryValue(Base64Variants.getDefaultVariant()));
        assertNotNull(field(parser, "_byteArrayBuilder"));
        assertTrue(parser.retainedContainerDepth() > 0);
        VPackByteStore store = rootStore(parser);

        parser.close();
        assertReleased(parser, store);
        assertEquals("", parser.streamReadContext().pathAsPointer(true).toString());
        assertNull(parser.currentToken());
        assertEquals(1, input.closes);
        parser.close();
        assertEquals(1, input.closes);
    }

    @Test
    void normalEofKeepsLastRootPathAfterReleasingOwnedState() throws Exception {
        // Two scalar roots followed by [1,2].
        byte[] input = { 0x18, 0x19, 0x02, 0x04, 0x31, 0x32 };
        TrackingInput source = new TrackingInput(input, false);
        VPackParser parser = (VPackParser) new VPackFactory().createParser(source);
        parser.assignCurrentValue(new Object());
        assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        parser.assignCurrentValue(new Object());
        VPackByteStore store = rootStore(parser);
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());

        assertNull(parser.nextToken());
        assertReleased(parser, store);
        assertTrue(parser.isClosed());
        assertEquals(1, source.closes);
        assertEquals(3, parser.streamReadContext().getEntryCount());
        assertEquals("/2", parser.streamReadContext().pathAsPointer(true).toString());
        assertNull(parser.nextToken());
        assertEquals("/2", parser.streamReadContext().pathAsPointer(true).toString());
        parser.close();
    }

    @Test
    void cleanupStillRunsWhenSourceCloseThrows() throws Exception {
        TrackingInput input = new TrackingInput(OBJECT, true);
        VPackParser parser = (VPackParser) new VPackFactory().createParser(input);
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        parser.assignCurrentValue(new Object());
        VPackByteStore store = rootStore(parser);
        assertThrows(JacksonException.class, parser::close);
        assertReleased(parser, store);
        assertTrue(parser.isClosed());
        parser.close();
        assertEquals(1, input.closes);
    }

    @Test
    void clearNumberDropsCachesButPreservesLastClearedTokenAndTraversal() throws Exception {
        byte[] input = java.util.Arrays.copyOf(UINT64, UINT64.length + 1);
        input[input.length - 1] = 0x18;
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            BigInteger exact = new BigInteger("18446744073709551615");
            assertEquals(exact, parser.getBigIntegerValue());
            assertEquals(exact, parser.getDecimalValue().toBigIntegerExact());
            assertNotNull(field(parser, "_canonicalNumber"));
            parser.clearCurrentToken();
            assertNull(parser.currentToken());
            assertNull(field(parser, "_canonicalNumber"));
            assertNull(field(parser, "_numberBigInt"));
            assertNull(field(parser, "_numberBigDecimal"));
            assertNotNull(field(parser, "_root"));
            parser.clearCurrentToken();
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.getLastClearedToken());
            assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
        }
    }

    @Test
    void clearWithinContainerDoesNotDiscardTraversal() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(OBJECT)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            parser.clearCurrentToken();
            parser.clearCurrentToken();
            assertEquals(JsonToken.PROPERTY_NAME, parser.getLastClearedToken());
            assertEquals(1, parser.retainedContainerDepth());
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertEquals("YQ==", parser.getString());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    @Test
    void terminalHandoffReleasesOwnedRootOnSuccessAndWriteFailure() throws Exception {
        for (boolean fail : new boolean[] { false, true }) {
            // Compact [1,2] followed by a root that must remain unread on the source.
            TrackingInput input = new TrackingInput(new byte[] { 0x13, 5, 0x31, 0x32, 2, 0x18 }, false);
            VPackParser parser = (VPackParser) new VPackFactory().createParser(input);
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            parser.assignCurrentValue(new Object());
            VPackByteStore store = rootStore(parser);
            if (fail) {
                assertThrows(JacksonException.class, () -> parser.releaseBuffered(new OutputStream() {
                    @Override public void write(int value) throws IOException {
                        throw new IOException("deliberate output failure");
                    }
                }));
            } else {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                assertEquals(3, parser.releaseBuffered(out));
                assertArrayEquals(new byte[] { 0x31, 0x32, 2 }, out.toByteArray());
            }
            assertReleased(parser, store);
            assertEquals(0, input.closes);
            assertEquals(0x18, input.read());
            assertEquals(0, parser.releaseBuffered(new ByteArrayOutputStream()));
            assertNull(parser.nextToken());
            parser.close();
            assertEquals(1, input.closes);
        }
    }

    @Test
    void scalarCloseAndZeroByteHandoffDropNativeCaches() throws Exception {
        byte[][] values = {
                UINT64,
                { (byte) 0xC0, 3, 1, 2, 3 },
                { 0x1C, 0, 0, 0, 0, 0, 0, 0, 0 }
        };
        for (byte[] value : values) {
            for (boolean handoff : new boolean[] { false, true }) {
                TrackingInput input = new TrackingInput(value, false);
                VPackParser parser = (VPackParser) new VPackFactory().createParser(input);
                JsonToken token = parser.nextToken();
                if (token == JsonToken.VALUE_NUMBER_INT) {
                    parser.getBigIntegerValue();
                    parser.getDecimalValue();
                } else {
                    assertNotNull(parser.getEmbeddedObject());
                }
                VPackByteStore store = rootStore(parser);
                if (handoff) {
                    assertEquals(0, parser.releaseBuffered(new ByteArrayOutputStream()));
                    assertEquals(0, input.closes);
                } else {
                    parser.close();
                }
                assertReleased(parser, store);
                parser.close();
                assertEquals(1, input.closes);
            }
        }
    }

    @Test
    void malformedTraversalDropsContextWithoutChangingSourceOwnership() throws Exception {
        byte[] invalid = OBJECT.clone();
        invalid[invalid.length - 1] = 4; // Not the beginning of a key.
        TrackingInput input = new TrackingInput(invalid, false);
        VPackParser parser = (VPackParser) new VPackFactory().createParser(input);
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
        assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
        parser.assignCurrentValue(new Object());
        VPackByteStore store = rootStore(parser);
        assertThrows(JacksonException.class, parser::nextToken);
        assertReleased(parser, store);
        assertNull(parser.currentToken());
        assertEquals(0, input.closes);
        parser.close();
        assertEquals(1, input.closes);
    }

    @Test
    void cleanupPreservesDisabledClearCurrentTokenOnCloseFeature() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .disable(StreamReadFeature.CLEAR_CURRENT_TOKEN_ON_CLOSE)
                .disable(StreamReadFeature.AUTO_CLOSE_SOURCE).build();
        TrackingInput input = new TrackingInput(OBJECT, false);
        VPackParser parser = (VPackParser) factory.createParser(input);
        assertEquals(JsonToken.START_OBJECT, parser.nextToken());
        VPackByteStore store = rootStore(parser);
        parser.close();
        assertEquals(JsonToken.START_OBJECT, parser.currentToken());
        assertEquals(0, input.closes);
        assertReleased(parser, store);
    }

    private static VPackByteStore rootStore(VPackParser parser) throws Exception {
        VPackByteStore store = ((VPackRootReader.Root) field(parser, "_root")).store();
        assertTrue(store.pageCount() > 0);
        return store;
    }

    private static void assertReleased(VPackParser parser, VPackByteStore store) throws Exception {
        assertTrue(store.isReleased());
        assertEquals(0, store.pageCount());
        assertEquals(0, parser.retainedContainerDepth());
        assertEquals(0, parser.retainedRootEntries());
        assertEquals(0L, parser.retainedRootNameBytes());
        assertNull(parser.streamReadContext().getParent());
        assertNull(parser.currentValue());
        for (String name : new String[] { "_root", "_rootBudget", "_currentVPackType",
                "_currentAttributeId", "_embeddedValue", "_canonicalNumber", "_numberBigInt",
                "_numberBigDecimal", "_numberString", "_stringValue", "_stringChars",
                "_binaryValue", "_byteArrayBuilder" }) {
            assertNull(field(parser, name), name);
        }
    }

    private static Object field(Object target, String name) throws Exception {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                // Jackson stores some caches in its parser base class.
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static final class TrackingInput extends ByteArrayInputStream {
        private final boolean failClose;
        int closes;

        TrackingInput(byte[] bytes, boolean failClose) {
            super(bytes);
            this.failClose = failClose;
        }

        @Override public void close() throws IOException {
            ++closes;
            if (failClose) throw new IOException("deliberate close failure");
            super.close();
        }
    }
}
