package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteCapability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0121F2 {

    void isClosedVpack() throws Exception {
        for (int mode = 0; mode < 2; ++mode) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            JsonGenerator generator;
            if (mode == 0) {
                generator = new VPackFactory().createGenerator(output);
            } else {
                generator = new VPackFactory().createGenerator(ObjectWriteContext.empty(),
                        (java.io.DataOutput) new DataOutputStream(output));
            }
            assertFalse(generator.isClosed());
            generator.writeStartArray();
            generator.writeNumber(-1);
            generator.writeEndArray();
            assertFalse(generator.isClosed());
            generator.close();
            assertTrue(generator.isClosed());
            generator.close();
            assertTrue(generator.isClosed());
        }
    }

    void longerObjectsVpack() throws Exception {
        for (int mode = 0; mode < 2; ++mode) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            JsonGenerator generator = mode == 0
                    ? new VPackFactory().createGenerator(output)
                    : new VPackFactory().createGenerator(ObjectWriteContext.empty(),
                            (java.io.DataOutput) new DataOutputStream(output));
            writeLongObject(generator);
            generator.close();

            try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                for (int i = 0; i < LONG_OBJECT_ENTRIES; ++i) {
                    assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                    assertEquals(longObjectName(i), parser.currentName());
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(i % 20 - 1, parser.getIntValue());
                }
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }
    }

    void capabilitiesAccessVpack() throws Exception {
        try (JsonGenerator generator = new VPackFactory().createGenerator(
                ObjectWriteContext.empty(), new ByteArrayOutputStream())) {
            assertTrue(generator.streamWriteCapabilities()
                    .isEnabled(StreamWriteCapability.CAN_WRITE_BINARY_NATIVELY));
            assertTrue(generator.has(StreamWriteCapability.CAN_WRITE_BINARY_NATIVELY));
            assertFalse(generator.streamWriteCapabilities()
                    .isEnabled(StreamWriteCapability.CAN_WRITE_FORMATTED_NUMBERS));
            assertFalse(generator.has(StreamWriteCapability.CAN_WRITE_FORMATTED_NUMBERS));
        }
    }
private static final int LONG_OBJECT_ENTRIES = 6_000;
private static void writeLongObject(JsonGenerator generator) {
        generator.writeStartObject(LONG_OBJECT_ENTRIES);
        for (int i = 0; i < LONG_OBJECT_ENTRIES; ++i) {
            generator.writeName(longObjectName(i));
            generator.writeNumber(i % 20 - 1);
        }
        generator.writeEndObject();
    }
private static String longObjectName(int index) {
        return "field" + index;
    }
private static void writeSimple0(JsonGenerator generator, String name) {
        generator.writeStartObject();
        generator.writeNumberProperty(name, 1);
        generator.writeNumberProperty(name, 2);
        generator.writeEndObject();
    }
private static void writeSimple1(JsonGenerator generator, String name) {
        generator.writeStartArray();
        generator.writeNumber(3);
        generator.writeStartObject();
        generator.writeNumberProperty("foo", 1);
        generator.writeNumberProperty("bar", 1);
        generator.writeNumberProperty(name, 1);
        generator.writeNumberProperty("bar2", 1);
        generator.writeNumberProperty(name, 2);
        generator.writeEndObject();
        generator.writeEndArray();
    }
private static void writeFailingSimple0(VPackFactory factory, String name) {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            writeSimple0(generator, name);
        }
    }
private static void writeFailingSimple1(VPackFactory factory, String name) {
        try (JsonGenerator generator = factory.createGenerator(new ByteArrayOutputStream())) {
            writeSimple1(generator, name);
        }
    }
private static byte[] hex(String value) {
        String[] parts = value.trim().split("\\s+");
        byte[] result = new byte[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return result;
    }

    void __invoke_isClosedVpack() throws Exception {
        try {
            isClosedVpack();
        } finally {
        }
    }


    void __invoke_longerObjectsVpack() throws Exception {
        try {
            longerObjectsVpack();
        } finally {
        }
    }


    void __invoke_capabilitiesAccessVpack() throws Exception {
        try {
            capabilitiesAccessVpack();
        } finally {
        }
    }

}
