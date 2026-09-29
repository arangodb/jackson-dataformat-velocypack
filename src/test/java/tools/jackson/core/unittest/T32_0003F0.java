package tools.jackson.core.unittest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import tools.jackson.core.ErrorReportConfiguration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0003F0 {

    void zeroLengths() {
        ErrorReportConfiguration config = ErrorReportConfiguration.builder()
                .maxErrorTokenLength(0)
                .maxRawContentLength(0)
                .build();
        VPackFactory factory = VPackFactory.builder()
                .errorReportConfiguration(config).build();

        assertEquals(0, config.getMaxErrorTokenLength());
        assertEquals(0, config.getMaxRawContentLength());
        assertSame(config, factory.errorReportConfiguration());
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

    void __invoke_zeroLengths() throws Exception {
        try {
            zeroLengths();
        } finally {
        }
    }

}
