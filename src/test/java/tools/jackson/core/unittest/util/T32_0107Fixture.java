package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamWriteCapability;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.util.JsonGeneratorDelegate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0107Fixture {

    void generatorDelegateVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertTrue(delegate.canOmitProperties());
        assertFalse(delegate.canWriteObjectId());
        assertFalse(delegate.canWriteTypeId());
        assertFalse(delegate.canWriteComments());
        assertEquals(generator.version(), delegate.version());
        assertFalse(delegate.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));
        assertSame(generator, delegate.delegate());
        assertNull(delegate.getSchema());

        delegate.writeStartArray();
        assertEquals(0, delegate.streamWriteOutputBuffered());
        delegate.writeNumber(13);
        delegate.writeNumber(BigInteger.ONE);
        delegate.writeNumber(new BigDecimal(0.5));
        delegate.writeNumber("137");
        delegate.writeNull();
        delegate.writeBoolean(false);
        delegate.writeString("foo");
        assertNull(delegate.currentValue());
        delegate.assignCurrentValue("foo");
        delegate.writeStartObject(null, 0);
        assertNull(delegate.currentValue());
        delegate.writeEndObject();
        assertEquals("foo", delegate.currentValue());
        delegate.writeStartArray(0);
        delegate.writeEndArray();
        delegate.writeEndArray();
        delegate.flush();
        delegate.close();

        assertTrue(delegate.isClosed());
        assertTrue(generator.isClosed());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 20 09 28 0d 31 c8 01 ff ff ff ff 05 28 89 18 19 "
                + "43 66 6f 6f 0a 01 03 05 06 0d 0f 10 11 15 16"),
                output.toByteArray());
    }

    void generatorDelegateArraysVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);
        Object marker = new Object();

        delegate.writeStartArray(marker);
        assertSame(marker, delegate.currentValue());
        delegate.writeArray(new int[] { 1, 2, 3 }, 0, 3);
        delegate.writeArray(new long[] { 1, 123456, 2 }, 1, 1);
        delegate.writeArray(new double[] { 0.25, 0.5, 0.75 }, 0, 2);
        delegate.writeArray(new String[] { "Aa", "Bb", "Cc" }, 1, 2);
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 2e 04 02 05 31 32 33 02 06 2a 40 e2 01 "
                + "02 14 1b 00 00 00 00 00 00 d0 3f "
                + "1b 00 00 00 00 00 00 e0 3f "
                + "02 08 42 42 62 42 43 63 03 08 0e 22"),
                output.toByteArray());
    }

    void generatorDelegateCommentsVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);
        Object marker = new Object();

        delegate.writeStartArray(marker, 5);
        assertSame(marker, delegate.currentValue());
        delegate.writeNumber((short) 1);
        delegate.writeNumber(12L);
        delegate.writeNumber(0.25);
        delegate.writeNumber(0.5f);
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 1c 04 31 28 0c "
                + "1b 00 00 00 00 00 00 d0 3f "
                + "1b 00 00 00 00 00 00 e0 3f 03 04 06 0f"),
                output.toByteArray());

        ByteArrayOutputStream rawOutput = new ByteArrayOutputStream();
        JsonGenerator rawGenerator = new VPackFactory().createGenerator(rawOutput);
        JsonGenerator rawDelegate = new JsonGeneratorDelegate(rawGenerator);
        try {
            assertThrows(UnsupportedOperationException.class,
                    () -> rawDelegate.writeRawValue("/*foo*/"));
            assertThrows(UnsupportedOperationException.class,
                    () -> rawDelegate.writeRaw("  "));
        } finally {
            try {
                rawDelegate.close();
            } catch (RuntimeException ignored) {
                // Failed VPack generators retain the primary unsupported-operation failure.
            }
        }
    }

    void delegateCopyMethodsVpack() throws IOException {
        JsonParser parser = new VPackFactory().createParser(VPackWireFixtureTest.hex(
                "06 0b 02 28 7b 02 04 1a 19 03 05"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new VPackFactory().createGenerator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        delegate.copyCurrentEvent(parser);
        assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
        delegate.copyCurrentStructure(parser);
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
        delegate.copyCurrentEvent(parser);
        generator.writeEndArray();

        delegate.close();
        parser.close();
        assertArrayEquals(VPackWireFixtureTest.hex("06 08 02 28 7b 19 03 05"),
                output.toByteArray());
    }

    void generatorDelegateCapabilitiesVpack() throws IOException {
        JsonGenerator generator = new VPackFactory().createGenerator(
                new ByteArrayOutputStream());
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertSame(generator.streamWriteCapabilities(), delegate.streamWriteCapabilities());
        for (StreamWriteCapability capability : StreamWriteCapability.values()) {
            assertEquals(generator.has(capability), delegate.has(capability),
                    "Capability " + capability + " should match");
        }
        delegate.close();
    }

    void __invoke_generatorDelegateVpack() throws Exception {
        try {
            generatorDelegateVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateArraysVpack() throws Exception {
        try {
            generatorDelegateArraysVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateCommentsVpack() throws Exception {
        try {
            generatorDelegateCommentsVpack();
        } finally {
        }
    }


    void __invoke_delegateCopyMethodsVpack() throws Exception {
        try {
            delegateCopyMethodsVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateCapabilitiesVpack() throws Exception {
        try {
            generatorDelegateCapabilitiesVpack();
        } finally {
        }
    }

}
