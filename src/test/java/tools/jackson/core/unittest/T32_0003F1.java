package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.TokenStreamLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0003F1 {

    void originalMesssage() {
        TokenStreamLocation loc = new TokenStreamLocation(null, -1L, 1, 1);
        StreamReadException exc = new StreamReadException(null, "Foobar", loc);
        assertEquals("Foobar", exc.getOriginalMessage());
        assertTrue(exc.getMessage().length() > exc.getOriginalMessage().length());

        StreamReadException exc2 = new StreamReadException((JsonParser) null,
                "Second", loc, exc);
        assertSame(exc, exc2.getCause());
        exc2.clearLocation();
        assertEquals(null, exc2.getLocation());

        StreamReadException exc3 = new StreamReadException((JsonParser) null,
                null, exc);
        assertEquals(null, exc3.getOriginalMessage());
        assertEquals("N/A\n at [No location information]", exc3.getMessage());
        assertTrue(exc3.toString().startsWith(StreamReadException.class.getName() + ": N/A"));
    }

    void accessToParser() throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(new byte[] { 0x0a })) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            StreamReadException e = new StreamReadException(parser, "Test!");
            assertSame(parser, e.processor());
            assertEquals("Test!", e.getOriginalMessage());
            assertNotNull(e.getLocation());
            assertEquals(1L, e.getLocation().getByteOffset());
        }
    }

    void accessToGenerator() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartObject();
            StreamWriteException e = new StreamWriteException(generator, "Test!");
            assertSame(generator, e.processor());
            assertEquals("Test!", e.getOriginalMessage());
        }
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

    void __invoke_originalMesssage() throws Exception {
        try {
            originalMesssage();
        } finally {
        }
    }


    void __invoke_accessToParser() throws Exception {
        try {
            accessToParser();
        } finally {
        }
    }


    void __invoke_accessToGenerator() throws Exception {
        try {
            accessToGenerator();
        } finally {
        }
    }

}
