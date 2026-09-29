package tools.jackson.core.unittest.base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0007Fixture {

    void nativeBinaryCanBeStreamedFromLiteralObjectAcrossBinarySources() throws Exception {
        byte[] payload = new byte[20_000];
        for (int i = 0; i < payload.length; ++i) {
            payload[i] = (byte) i;
        }
        byte[] document = objectWithBinary(payload);

        for (int source = 0; source < 3; ++source) {
            try (VPackParser parser = parserFor(document, source)) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals("b", parser.currentName());
                assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());

                ByteArrayOutputStream output = new ByteArrayOutputStream(payload.length);
                assertEquals(payload.length, parser.readBinaryValue(output));
                assertArrayEquals(payload, output.toByteArray());
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                if (source != 2) {
                    assertNull(parser.nextToken());
                }
            }
        }
    }

    void nativeBinaryValuesInLiteralIndexedArrayAreReadableInOrder() throws Exception {
        byte[][] expected = new byte[7][];
        for (int i = 0; i < expected.length; ++i) {
            expected[i] = new byte[i + 1];
            for (int j = 0; j < expected[i].length; ++j) {
                expected[i][j] = (byte) (i + j);
            }
        }

        try (VPackParser parser = (VPackParser) new VPackFactory()
                .createParser(indexedArrayWithBinary(expected))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (byte[] value : expected) {
                assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
                assertArrayEquals(value, parser.getBinaryValue());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void vpackTextBinaryDecodingAcceptsBoundaryWhitespace() throws Exception {
        byte[] expected = { 1, 2, 3, 4, 5 };
        for (String encoded : new String[] { "AQIDBAU=", "   AQIDBAU=", "AQIDBAU=   " }) {
            try (VPackParser parser = (VPackParser) new VPackFactory()
                    .createParser(shortString(encoded))) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertArrayEquals(expected, parser.getBinaryValue(Base64Variants.MIME));
            }
        }
    }

    void binaryAccessorRejectsLiteralArrayToken() throws Exception {
        try (VPackParser parser = (VPackParser) new VPackFactory()
                .createParser(new byte[] { 0x01 })) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamReadException.class, parser::getBinaryValue);
        }
    }

    void vpackTextBinaryDecodingRetainsMissingPaddingRules() throws Exception {
        for (String encoded : new String[] { "fQ", "A/A" }) {
            byte[] document = shortString(encoded);
            try (VPackParser parser = (VPackParser) new VPackFactory().createParser(document)) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertThrows(StreamReadException.class,
                        () -> parser.getBinaryValue(Base64Variants.MIME));
            }
            try (VPackParser parser = (VPackParser) new VPackFactory().createParser(document)) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                parser.getString();
                assertThrows(StreamReadException.class,
                        () -> parser.getBinaryValue(Base64Variants.MIME));
            }
        }
    }

    void vpackTextBinaryDecodingAcceptsUrlMissingPadding() throws Exception {
        assertTextBinary(new String[] { "rQ", "rNw" },
                new byte[][] { { (byte) 0xAD }, { (byte) 0xAC, (byte) 0xDC } });
    }

    void vpackTextBinaryDecodingRejectsInvalidCharactersAndPadding() throws Exception {
        for (String encoded : new String[] { "a===", "ab de", "ab#?" }) {
            try (VPackParser parser = (VPackParser) new VPackFactory()
                    .createParser(shortString(encoded))) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertThrows(StreamReadException.class,
                        () -> parser.getBinaryValue(Base64Variants.MIME));
            }
        }
    }

    void vpackStringPayloadDoesNotInterpretJsonEscapes() throws Exception {
        for (String encoded : new String[] { "VGVz\\ndCE=", "VGVzdCE\\u003d" }) {
            try (VPackParser parser = (VPackParser) new VPackFactory()
                    .createParser(shortString(encoded))) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertThrows(StreamReadException.class,
                        () -> parser.getBinaryValue(Base64Variants.MIME));
            }
        }
    }
private static void assertTextBinary(String[] encoded, byte[][] expected) throws Exception {
        for (int i = 0; i < encoded.length; ++i) {
            try (VPackParser parser = (VPackParser) new VPackFactory()
                    .createParser(shortString(encoded[i]))) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertArrayEquals(expected[i],
                        parser.getBinaryValue(Base64Variants.MODIFIED_FOR_URL));
            }
        }
    }
private static VPackParser parserFor(byte[] document, int source) {
        VPackFactory factory = new VPackFactory();
        return switch (source) {
        case 0 -> (VPackParser) factory.createParser(document);
        case 1 -> (VPackParser) factory.createParser(new ByteArrayInputStream(document));
        default -> (VPackParser) factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(document)));
        };
    }
private static byte[] shortString(String value) {
        byte[] payload = value.getBytes(StandardCharsets.US_ASCII);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not short");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] objectWithBinary(byte[] payload) {
        int bodyLength = 2 + 3 + payload.length;
        int length = 1 + 2 + 2 + bodyLength + 2;
        byte[] result = new byte[length];
        result[0] = 0x0C;
        putLittleEndian(result, 1, 2, length);
        putLittleEndian(result, 3, 2, 1);
        int body = 5;
        result[body] = 0x41;
        result[body + 1] = 'b';
        int binary = body + 2;
        result[binary] = (byte) 0xC1;
        putLittleEndian(result, binary + 1, 2, payload.length);
        System.arraycopy(payload, 0, result, binary + 3, payload.length);
        putLittleEndian(result, binary + 3 + payload.length, 2, body);
        return result;
    }
private static byte[] indexedArrayWithBinary(byte[][] values) {
        int bodyLength = 0;
        for (byte[] value : values) {
            bodyLength += 2 + value.length;
        }
        int length = 1 + 2 + 2 + bodyLength + 2 * values.length;
        byte[] result = new byte[length];
        result[0] = 0x07;
        putLittleEndian(result, 1, 2, length);
        putLittleEndian(result, 3, 2, values.length);

        int body = 5;
        int position = body;
        int index = body + bodyLength;
        for (byte[] value : values) {
            putLittleEndian(result, index, 2, position);
            index += 2;
            result[position] = (byte) 0xC0;
            result[position + 1] = (byte) value.length;
            System.arraycopy(value, 0, result, position + 2, value.length);
            position += 2 + value.length;
        }
        return result;
    }
private static void putLittleEndian(byte[] target, int offset, int width, int value) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }

    void __invoke_nativeBinaryCanBeStreamedFromLiteralObjectAcrossBinarySources() throws Exception {
        try {
            nativeBinaryCanBeStreamedFromLiteralObjectAcrossBinarySources();
        } finally {
        }
    }


    void __invoke_nativeBinaryValuesInLiteralIndexedArrayAreReadableInOrder() throws Exception {
        try {
            nativeBinaryValuesInLiteralIndexedArrayAreReadableInOrder();
        } finally {
        }
    }


    void __invoke_vpackTextBinaryDecodingAcceptsBoundaryWhitespace() throws Exception {
        try {
            vpackTextBinaryDecodingAcceptsBoundaryWhitespace();
        } finally {
        }
    }


    void __invoke_binaryAccessorRejectsLiteralArrayToken() throws Exception {
        try {
            binaryAccessorRejectsLiteralArrayToken();
        } finally {
        }
    }


    void __invoke_vpackTextBinaryDecodingRetainsMissingPaddingRules() throws Exception {
        try {
            vpackTextBinaryDecodingRetainsMissingPaddingRules();
        } finally {
        }
    }


    void __invoke_vpackTextBinaryDecodingAcceptsUrlMissingPadding() throws Exception {
        try {
            vpackTextBinaryDecodingAcceptsUrlMissingPadding();
        } finally {
        }
    }


    void __invoke_vpackTextBinaryDecodingRejectsInvalidCharactersAndPadding() throws Exception {
        try {
            vpackTextBinaryDecodingRejectsInvalidCharactersAndPadding();
        } finally {
        }
    }


    void __invoke_vpackStringPayloadDoesNotInterpretJsonEscapes() throws Exception {
        try {
            vpackStringPayloadDoesNotInterpretJsonEscapes();
        } finally {
        }
    }

}
