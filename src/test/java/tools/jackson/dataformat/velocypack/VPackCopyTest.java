package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.util.TokenBuffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackCopyTest {
    private final VPackFactory factory = new VPackFactory();

    @Test
    void directVPackCopyRetainsNativeMarkersRawDoubleAndExactNumbers() throws Exception {
        byte[] input = hex(
                "1c 15 cd 5b 07 00 00 00 00 "
                + "1e "
                + "c0 03 01 02 03 "
                + "1b 42 00 00 00 00 00 f8 7f "
                + "c8 03 ff ff ff ff 12 34 50 "
                + "2f ff ff ff ff ff ff ff ff");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = factory.createParser(input);
                VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            while (parser.nextToken() != null) {
                generator.copyCurrentEvent(parser);
            }
        }
        assertArrayEquals(input, output.toByteArray());
    }

    @Test
    void directStructureCopyIsIterativeAndLeavesParserAtLastEvent() throws Exception {
        // Independent literal: [[1, 2]].
        byte[] input = hex("02 06 02 04 31 32");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = factory.createParser(input);
                VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            generator.copyCurrentStructure(parser);
            assertEquals(JsonToken.END_ARRAY, parser.currentToken());
        }
        assertArrayEquals(input, output.toByteArray());
    }

    @Test
    void genericParserUsesCanonicalValuesAndRejectsUnsupportedEmbeddedObjects() throws Exception {
        JsonMapper json = JsonMapper.builder().build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = json.createParser("[9007199254740993, 1.25, true]")) {
            try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                generator.copyCurrentStructure(parser);
            }
        }
        try (JsonParser parser = factory.createParser(output.toByteArray())) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(new BigInteger("9007199254740993"), parser.getBigIntegerValue());
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(new BigDecimal("1.25"), parser.getDecimalValue());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
        }

        // TokenBuffer is a generic parser boundary: it intentionally does not
        // promise to retain VPack's physical markers or accept arbitrary
        // native objects as VPack values.
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeEmbeddedObject(new Object());
        try (JsonParser embedded = buffer.asParser()) {
            embedded.nextToken();
            assertThrows(StreamWriteException.class, () -> {
                try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(
                        new ByteArrayOutputStream())) {
                    generator.copyCurrentEvent(embedded);
                }
            });
        }
    }

    @Test
    void physicalDateAndSentinelCopyThroughCurrentEventOnly() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonParser parser = factory.createParser(hex("1c 01 00 00 00 00 00 00 00 1f"));
                VPackGenerator generator = (VPackGenerator) factory.createGenerator(output)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            generator.copyCurrentEventExact(parser);
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            generator.copyCurrentEvent(parser);
        }
        assertArrayEquals(hex("1c 01 00 00 00 00 00 00 00 1f"), output.toByteArray());
    }

    private static byte[] hex(String value) {
        String[] parts = value.split("\\s+");
        byte[] result = new byte[parts.length];
        for (int i = 0; i < parts.length; ++i) {
            result[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return result;
    }
}
