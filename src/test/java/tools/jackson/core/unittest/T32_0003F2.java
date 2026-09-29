package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0003F2 {

    void generationException() throws Exception {
        StreamWriteException exception = null;
        JsonGenerator generator = new VPackFactory()
                .createGenerator(new ByteArrayOutputStream());
        try {
            generator.writeStartObject();
            try {
                generator.writeNumber(4);
                fail("An object value without a property name must fail");
            } catch (StreamWriteException e) {
                exception = e;
            }
        } finally {
            try {
                generator.close();
            } catch (StreamWriteException ignored) {
                // Closing a failed VPack generator reports its stored failure.
            }
        }
        assertNotNull(exception);
        StreamWriteException restored = deserialize(serialize(exception));
        assertNotNull(restored);
    }

    void jsonFactorySerializable() throws Exception {
        VPackFactory originalFactory = new VPackFactory();
        byte[] original = { 0x02, 0x05, 0x31, 0x1a, 0x0a };

        assertArrayEquals(original, copyVPack(originalFactory, original));

        VPackFactory restoredFactory = (VPackFactory)
                deserialize(serialize(originalFactory));
        assertNotNull(restoredFactory);
        assertArrayEquals(original, copyVPack(restoredFactory, original));
    }
private static byte[] copyVPack(VPackFactory factory, byte[] input) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonParser parser = factory.createParser(input);
                JsonGenerator generator = factory.createGenerator(out)) {
            JsonToken token;
            while ((token = parser.nextToken()) != null) {
                switch (token) {
                case START_ARRAY -> generator.writeStartArray();
                case END_ARRAY -> generator.writeEndArray();
                case START_OBJECT -> generator.writeStartObject();
                case END_OBJECT -> generator.writeEndObject();
                case VALUE_NUMBER_INT -> generator.writeNumber(parser.getLongValue());
                case VALUE_TRUE -> generator.writeBoolean(true);
                case VALUE_FALSE -> generator.writeBoolean(false);
                case VALUE_NULL -> generator.writeNull();
                default -> throw new AssertionError("Unexpected token: " + token);
                }
            }
        }
        return out.toByteArray();
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

    void __invoke_generationException() throws Exception {
        try {
            generationException();
        } finally {
        }
    }


    void __invoke_jsonFactorySerializable() throws Exception {
        try {
            jsonFactorySerializable();
        } finally {
        }
    }

}
