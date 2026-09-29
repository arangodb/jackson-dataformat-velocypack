package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonTokenId;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonGeneratorDelegate;
import tools.jackson.core.util.JsonParserDelegate;
import tools.jackson.core.util.RecyclerPool;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0109F0 {
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

    void parserDelegateVpack() throws IOException {
        final int maxNumberLength = 200;
        StreamReadConstraints constraints = StreamReadConstraints.builder()
                .maxNumberLength(maxNumberLength)
                .build();
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(constraints)
                .build();
        JsonParser parser = factory.createParser(ObjectReadContext.empty(), PARSER_INPUT);
        JsonParserDelegate delegate = new JsonParserDelegate(parser);
        final String assignedValue = "foo";

        assertFalse(delegate.canParseAsync());
        assertFalse(delegate.canReadObjectId());
        assertFalse(delegate.canReadTypeId());
        assertEquals(parser.version(), delegate.version());
        assertSame(parser.streamReadConstraints(), delegate.streamReadConstraints());
        assertEquals(maxNumberLength, parser.streamReadConstraints().getMaxNumberLength());
        assertSame(parser.streamReadCapabilities(), delegate.streamReadCapabilities());
        assertEquals(parser.willInternPropertyNames(), delegate.willInternPropertyNames());

        assertFalse(delegate.isEnabled(StreamReadFeature.IGNORE_UNDEFINED));
        assertSame(parser, delegate.delegate());
        assertNull(delegate.getSchema());
        assertNull(delegate.currentToken());
        assertFalse(delegate.hasCurrentToken());
        assertFalse(delegate.hasStringCharacters());
        assertNull(delegate.currentValue());
        assertNull(delegate.currentName());
        assertNull(delegate.getLastClearedToken());

        assertEquals(JsonToken.START_ARRAY, delegate.nextToken());
        assertEquals(JsonTokenId.ID_START_ARRAY, delegate.currentTokenId());
        assertTrue(delegate.hasToken(JsonToken.START_ARRAY));
        assertFalse(delegate.hasToken(JsonToken.START_OBJECT));
        assertTrue(delegate.hasTokenId(JsonTokenId.ID_START_ARRAY));
        assertFalse(delegate.hasTokenId(JsonTokenId.ID_START_OBJECT));
        assertTrue(delegate.isExpectedStartArrayToken());
        assertFalse(delegate.isExpectedStartObjectToken());
        assertFalse(delegate.isExpectedNumberIntToken());
        assertEquals("[", delegate.getString());
        assertNotNull(delegate.streamReadContext());
        assertSame(parser.streamReadContext(), delegate.streamReadContext());

        assertEquals(JsonToken.VALUE_NUMBER_INT, delegate.nextToken());
        assertEquals(1, delegate.getIntValue());
        assertEquals(1, delegate.getValueAsInt());
        assertEquals(1, delegate.getValueAsInt(3));
        assertEquals(1L, delegate.getValueAsLong());
        assertEquals(1L, delegate.getValueAsLong(3L));
        assertEquals(1L, delegate.getLongValue());
        assertEquals(1d, delegate.getValueAsDouble());
        assertEquals(1d, delegate.getValueAsDouble(0.25));
        assertEquals(1d, delegate.getDoubleValue());
        assertTrue(delegate.getValueAsBoolean());
        assertTrue(delegate.getValueAsBoolean(false));
        assertEquals((byte) 1, delegate.getByteValue());
        assertEquals((short) 1, delegate.getShortValue());
        assertEquals(1f, delegate.getFloatValue());
        assertFalse(delegate.isNaN());
        assertTrue(delegate.isExpectedNumberIntToken());
        assertEquals(JsonParser.NumberType.INT, delegate.getNumberType());
        assertEquals(JsonParser.NumberTypeFP.UNKNOWN, delegate.getNumberTypeFP());
        assertEquals(Integer.valueOf(1), delegate.getNumberValue());
        assertNull(delegate.getEmbeddedObject());

        assertEquals(JsonToken.VALUE_TRUE, delegate.nextToken());
        assertTrue(delegate.getBooleanValue());
        assertEquals(parser.currentLocation(), delegate.currentLocation());
        assertNull(delegate.getTypeId());
        assertNull(delegate.getObjectId());

        assertEquals(JsonToken.VALUE_NULL, delegate.nextToken());
        assertNull(delegate.currentValue());
        delegate.assignCurrentValue(assignedValue);

        assertEquals(JsonToken.START_OBJECT, delegate.nextToken());
        assertNull(delegate.currentValue());
        assertEquals(JsonToken.PROPERTY_NAME, delegate.nextToken());
        assertEquals("a", delegate.currentName());
        assertEquals(JsonToken.VALUE_STRING, delegate.nextToken());
        delegate.finishToken();
        assertTrue(delegate.hasStringCharacters());
        assertEquals("foo", delegate.getString());
        assertEquals(3, delegate.getStringLength());
        assertEquals(JsonToken.END_OBJECT, delegate.nextToken());
        assertEquals(assignedValue, delegate.currentValue());

        assertEquals(JsonToken.VALUE_STRING, delegate.nextToken());
        assertArrayEquals(new byte[] { 1, 2 }, delegate.getBinaryValue());
        assertEquals(JsonToken.END_ARRAY, delegate.nextToken());

        delegate.close();
        assertTrue(delegate.isClosed());
        assertTrue(parser.isClosed());
        parser.close();
    }

    void notDelegateCopyMethodsVpack() throws IOException {
        JsonParser parser = new VPackFactory().createParser(ObjectReadContext.empty(), COPY_INPUT);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator delegate = new JsonGeneratorDelegate(
                new VPackFactory().createGenerator(ObjectWriteContext.empty(), output), false) {
            @Override
            public JsonGenerator writeName(String name) {
                super.writeName(name + "-test");
                super.writeBoolean(true);
                super.writeName(name);
                return this;
            }
        };

        parser.nextToken();
        delegate.copyCurrentStructure(parser);
        delegate.flush();
        delegate.close();
        parser.close();

        assertArrayEquals(COPY_EXPECTED, output.toByteArray(),
                () -> "actual=" + Arrays.toString(output.toByteArray()));
    }

    void generatorDelegateWriteStringVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));

        delegate.writeStartArray();
        delegate.writeString("test");
        delegate.writeString("hello world".toCharArray(), 0, 5);
        delegate.writeString(new SerializedString("serialized"));
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 1c 03 44 74 65 73 74 45 68 65 6c 6c 6f "
                + "4a 73 65 72 69 61 6c 69 7a 65 64 03 08 0e"),
                output.toByteArray());
    }

    void generatorDelegateWriteRawVpack() throws IOException {
        assertRawUnsupported(delegate -> delegate.writeRaw("123"));
        assertRawUnsupported(delegate -> delegate.writeRaw(','));
        assertRawUnsupported(delegate -> delegate.writeRaw("456789", 0, 3));
        assertRawUnsupported(delegate -> delegate.writeRaw("abc".toCharArray(), 0, 3));
    }

    void generatorDelegateWriteTreeWithDelegationVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output), true);

        delegate.writeStartArray();
        delegate.writeTree(null);
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex("02 03 18"), output.toByteArray());
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

    void __invoke_parserDelegateVpack() throws Exception {
        try {
            parserDelegateVpack();
        } finally {
        }
    }


    void __invoke_notDelegateCopyMethodsVpack() throws Exception {
        try {
            notDelegateCopyMethodsVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteStringVpack() throws Exception {
        try {
            generatorDelegateWriteStringVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteRawVpack() throws Exception {
        try {
            generatorDelegateWriteRawVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteTreeWithDelegationVpack() throws Exception {
        try {
            generatorDelegateWriteTreeWithDelegationVpack();
        } finally {
        }
    }

}
