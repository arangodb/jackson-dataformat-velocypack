package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VPackObjectGeneratorTest {
    @Test
    void writesBodyInCallOrderAndOnlySortsTheIndexByUnsignedUtf8() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeNumber(1);
            generator.writeName("a");
            generator.writeNumber(2);
            generator.writeEndObject();
        }
        assertArrayEquals(new byte[] {
                0x0B, 0x0B, 0x02, 0x41, 0x62, 0x31, 0x41, 0x61, 0x32, 0x06, 0x03
        }, out.toByteArray());

        try (JsonParser parser = new VPackFactory().createParser(out.toByteArray())) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("b", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals("a", parser.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
        }
    }

    @Test
    void writesUnicodeNamesUsingUnsignedUtf8Order() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("é");
            generator.writeNumber(1);
            generator.writeName("e");
            generator.writeNumber(2);
            generator.writeEndObject();
        }
        byte[] bytes = out.toByteArray();
        assertEquals(0x0B, bytes[0] & 0xFF);
        assertEquals(0x07, bytes[bytes.length - 2] & 0xFF);
        assertEquals(0x03, bytes[bytes.length - 1] & 0xFF);
        assertEquals(0x42, bytes[3] & 0xFF);
        assertEquals(0xC3, bytes[4] & 0xFF);
    }

    @Test
    void writesResolvedAttributeIdsOnlyWhenTheCodecRoundTripsThem() throws Exception {
        BigInteger max = VPackBounds.UINT64_MAX;
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override
            public String decode(BigInteger id) {
                if (id.signum() == 0) return "b";
                if (id.equals(max)) return "a";
                return null;
            }

            @Override
            public BigInteger encode(String name) {
                return "b".equals(name) ? BigInteger.ZERO
                        : "a".equals(name) ? max : null;
            }
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder().attributeNameCodec(codec)
                .build().createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeNumber(1);
            generator.writeName("a");
            generator.writeNumber(2);
            generator.writeEndObject();
        }
        assertArrayEquals(new byte[] {
                0x0B, 0x11, 0x02, 0x30, 0x31,
                0x2F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x32,
                0x05, 0x03
        }, out.toByteArray());
    }

    @Test
    void choosesTheNextWidthFromTheCompleteObjectLength() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(out)) {
            generator.writeStartObject(64);
            for (int i = 0; i < 64; ++i) {
                generator.writeName("p" + i);
                generator.writeNull();
            }
            generator.writeEndObject();
        }
        assertEquals(0x0C, out.toByteArray()[0] & 0xFF);
        try (JsonParser parser = new VPackFactory().createParser(out.toByteArray())) {
            int properties = 0;
            while (parser.nextToken() != null) {
                if (parser.currentToken() == JsonToken.PROPERTY_NAME) ++properties;
            }
            assertEquals(64, properties);
        }
    }
}
