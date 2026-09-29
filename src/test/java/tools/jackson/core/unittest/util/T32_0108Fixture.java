package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.core.io.SerializedString;
import tools.jackson.core.util.JsonGeneratorDelegate;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0108Fixture {

    void generatorDelegateConfigureVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = generator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertFalse(delegate.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
        assertSame(delegate, delegate.configure(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN, true));
        assertTrue(delegate.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));
        assertTrue(generator.isEnabled(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN));

        delegate.close();
    }

    void generatorDelegateContextsVpack() throws IOException {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertContextMatches(generator.streamWriteContext(), delegate.streamWriteContext());
        delegate.writeStartArray();
        assertContextMatches(generator.streamWriteContext(), delegate.streamWriteContext());
        assertSame(generator.objectWriteContext(), delegate.objectWriteContext());

        delegate.close();
    }

    void generatorDelegateFlushBehaviorVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = generator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        delegate.writeStartArray();
        delegate.writeNumber(1);
        assertFalse(delegate.isClosed());

        delegate.flush();
        assertFalse(delegate.isClosed());
        assertFalse(generator.isClosed());
        // VPack must retain an open root until its final length/index is known;
        // flush forwards to the target without emitting a partial root.
        assertEquals(0, output.size());

        delegate.close();
        assertTrue(delegate.isClosed());
        assertTrue(generator.isClosed());
    }

    void generatorDelegateGettersAndSettersVpack() throws IOException {
        JsonGenerator generator = generator(new ByteArrayOutputStream());
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertEquals(generator.getHighestNonEscapedChar(), delegate.getHighestNonEscapedChar());
        assertEquals(generator.getCharacterEscapes(), delegate.getCharacterEscapes());
        assertEquals(generator.getPrettyPrinter(), delegate.getPrettyPrinter());
        assertEquals(generator.streamWriteFeatures(), delegate.streamWriteFeatures());
        assertNull(delegate.getPrettyPrinter());

        delegate.close();
    }

    void generatorDelegateNumberVariationsVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));

        delegate.writeStartArray();
        delegate.writeNumber((short) 1);
        delegate.writeNumber(2);
        delegate.writeNumber(3L);
        delegate.writeNumber(BigInteger.valueOf(4));
        delegate.writeNumber(5.0);
        delegate.writeNumber(6.0f);
        delegate.writeNumber(new BigDecimal("7.5"));
        delegate.writeNumber("8");
        delegate.writeNumber("123".toCharArray(), 0, 3);
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 2c 09 31 32 33 34 "
                + "1b 00 00 00 00 00 00 14 40 "
                + "1b 00 00 00 00 00 00 18 40 "
                + "c8 01 ff ff ff ff 75 38 28 7b "
                + "03 04 05 06 07 10 19 20 21"), output.toByteArray());
    }

    void generatorDelegateOutputInfoVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = generator(output);
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator);

        assertSame(generator.streamWriteOutputTarget(), delegate.streamWriteOutputTarget());
        delegate.writeStartArray();
        delegate.writeNumber(123);
        assertEquals(generator.streamWriteOutputBuffered(), delegate.streamWriteOutputBuffered());

        delegate.close();
    }

    void generatorDelegateReturnValuesVpack() throws IOException {
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(
                generator(new ByteArrayOutputStream()));

        assertSame(delegate, delegate.writeStartArray());
        assertSame(delegate, delegate.writeNumber(1));
        assertSame(delegate, delegate.writeString("test"));
        assertSame(delegate, delegate.writeBoolean(true));
        assertSame(delegate, delegate.writeNull());
        assertSame(delegate, delegate.writeEndArray());
        assertSame(delegate, delegate.writeStartObject());
        assertSame(delegate, delegate.writeName("field"));
        assertSame(delegate, delegate.writeNumber(2));
        assertSame(delegate, delegate.writeEndObject());

        delegate.close();
    }

    void generatorDelegateUTF8MethodsVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));
        byte[] utf8 = "hello".getBytes(StandardCharsets.UTF_8);

        delegate.writeStartArray();
        delegate.writeUTF8String(utf8, 0, utf8.length);
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex("02 08 45 68 65 6c 6c 6f"),
                output.toByteArray());
    }

    void generatorDelegateWriteBinaryVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));

        delegate.writeStartArray();
        delegate.writeBinary(new byte[] { 1, 2, 3, 4, 5 }, 1, 3);
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex("02 07 c0 03 02 03 04"),
                output.toByteArray());
    }

    void generatorDelegateWriteNameVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));

        delegate.writeStartObject();
        delegate.writeName("field1");
        delegate.writeNumber(1);
        delegate.writeName(new SerializedString("field2"));
        delegate.writeString("value");
        delegate.writeEndObject();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1a 02 46 66 69 65 6c 64 31 31 "
                + "46 66 69 65 6c 64 32 45 76 61 6c 75 65 03 0b"),
                output.toByteArray());
    }

    void generatorDelegateWriteOmittedPropertyVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(generator(output));

        delegate.writeStartObject();
        delegate.writeName("visible");
        delegate.writeNumber(1);
        assertSame(delegate, delegate.writeOmittedProperty("omitted"));
        delegate.writeEndObject();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 01 47 76 69 73 69 62 6c 65 31 03"), output.toByteArray());
    }

    void generatorDelegateWritePOJOWithDelegationVpack() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGeneratorDelegate delegate = new JsonGeneratorDelegate(
                generator(output), true);

        delegate.writeStartArray();
        delegate.writePOJO(null);
        delegate.writeEndArray();
        delegate.close();

        assertArrayEquals(VPackWireFixtureTest.hex("02 03 18"), output.toByteArray());
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertContextMatches(TokenStreamContext expected,
            TokenStreamContext actual) {
        assertEquals(expected.typeDesc(), actual.typeDesc());
        assertEquals(expected.getCurrentIndex(), actual.getCurrentIndex());
        assertEquals(expected.getNestingDepth(), actual.getNestingDepth());
        assertEquals(expected.currentName(), actual.currentName());
        assertEquals(expected.currentValue(), actual.currentValue());
    }

    void __invoke_generatorDelegateConfigureVpack() throws Exception {
        try {
            generatorDelegateConfigureVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateContextsVpack() throws Exception {
        try {
            generatorDelegateContextsVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateFlushBehaviorVpack() throws Exception {
        try {
            generatorDelegateFlushBehaviorVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateGettersAndSettersVpack() throws Exception {
        try {
            generatorDelegateGettersAndSettersVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateNumberVariationsVpack() throws Exception {
        try {
            generatorDelegateNumberVariationsVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateOutputInfoVpack() throws Exception {
        try {
            generatorDelegateOutputInfoVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateReturnValuesVpack() throws Exception {
        try {
            generatorDelegateReturnValuesVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateUTF8MethodsVpack() throws Exception {
        try {
            generatorDelegateUTF8MethodsVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteBinaryVpack() throws Exception {
        try {
            generatorDelegateWriteBinaryVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteNameVpack() throws Exception {
        try {
            generatorDelegateWriteNameVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWriteOmittedPropertyVpack() throws Exception {
        try {
            generatorDelegateWriteOmittedPropertyVpack();
        } finally {
        }
    }


    void __invoke_generatorDelegateWritePOJOWithDelegationVpack() throws Exception {
        try {
            generatorDelegateWritePOJOWithDelegationVpack();
        } finally {
        }
    }

}
