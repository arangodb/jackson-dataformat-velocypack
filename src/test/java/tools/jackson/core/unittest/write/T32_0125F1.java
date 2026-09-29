package tools.jackson.core.unittest.write;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.type.WritableTypeId;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0125F1 {

    void noNativeTypeIdForVpack() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            assertFalse(generator.canWriteTypeId());
            StreamWriteException exception = assertThrows(StreamWriteException.class,
                    () -> generator.writeTypeId("whatever"));
            assertMessageContains(exception, "No native support for writing Type Ids");
        }
    }

    void basicTypeIdWriteForObjectVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 19 02 44 74 79 70 65 46 74 79 70 65 49 64 "
                + "45 76 61 6c 75 65 28 0d 03 0f"),
                writeObjectTypeId(WritableTypeId.Inclusion.METADATA_PROPERTY,
                        "type", false));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 18 02 46 74 79 70 65 49 64 0b 0c 01 45 76 61 6c 75 65 "
                + "28 0d 03 03 0a"),
                writeObjectTypeId(WritableTypeId.Inclusion.WRAPPER_ARRAY,
                        null, false));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 01 46 74 79 70 65 49 64 0b 0c 01 45 76 61 6c 75 65 "
                + "28 0d 03 03"),
                writeObjectTypeId(WritableTypeId.Inclusion.WRAPPER_OBJECT,
                        null, false));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 25 02 45 76 61 6c 75 65 0b 0d 01 46 6e 75 6d 62 65 72 "
                + "28 2a 03 45 65 78 74 49 64 46 74 79 70 65 49 64 16 03"),
                writeObjectTypeId(WritableTypeId.Inclusion.PARENT_PROPERTY,
                        "extId", true));
    }

    void basicTypeIdWriteForArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 74 79 70 65 49 64 02 06 28 0d 28 2a 03"),
                writeArrayTypeId(WritableTypeId.Inclusion.WRAPPER_OBJECT));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 12 02 46 74 79 70 65 49 64 02 06 28 0d 28 2a 03 0a"),
                writeArrayTypeId(WritableTypeId.Inclusion.PAYLOAD_PROPERTY));
    }
private static byte[] writeObjectTypeId(WritableTypeId.Inclusion inclusion,
            String asProperty, boolean parentProperty) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            WritableTypeId typeId = new WritableTypeId(new Object(), JsonToken.START_OBJECT,
                    "typeId");
            typeId.include = inclusion;
            typeId.asProperty = asProperty;
            if (parentProperty) {
                generator.writeStartObject();
                generator.writeName("value");
            }
            generator.writeTypePrefix(typeId);
            generator.writeNumberProperty(parentProperty ? "number" : "value",
                    parentProperty ? 42 : 13);
            generator.writeTypeSuffix(typeId);
            if (parentProperty) {
                generator.writeEndObject();
            }
        }
        return output.toByteArray();
    }
private static byte[] writeArrayTypeId(WritableTypeId.Inclusion inclusion) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = generator(output)) {
            WritableTypeId typeId = new WritableTypeId(new Object(), JsonToken.START_ARRAY,
                    "typeId");
            typeId.include = inclusion;
            typeId.asProperty = "type";
            generator.writeTypePrefix(typeId);
            generator.writeNumber(13);
            generator.writeNumber(42);
            generator.writeTypeSuffix(typeId);
        }
        return output.toByteArray();
    }
private static JsonGenerator generator(ByteArrayOutputStream output) {
        return new VPackFactory().createGenerator(ObjectWriteContext.empty(), output);
    }
private static void assertMessageContains(Exception exception, String expected) {
        String message = exception.getMessage();
        if (message == null || !message.contains(expected)) {
            throw new AssertionError("Expected message containing " + expected
                    + ", got: " + message, exception);
        }
    }

    void __invoke_noNativeTypeIdForVpack() throws Exception {
        try {
            noNativeTypeIdForVpack();
        } finally {
        }
    }


    void __invoke_basicTypeIdWriteForObjectVpack() throws Exception {
        try {
            basicTypeIdWriteForObjectVpack();
        } finally {
        }
    }


    void __invoke_basicTypeIdWriteForArrayVpack() throws Exception {
        try {
            basicTypeIdWriteForArrayVpack();
        } finally {
        }
    }

}
