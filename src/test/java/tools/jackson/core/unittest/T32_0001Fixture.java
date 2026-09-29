package tools.jackson.core.unittest;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.jackson.core.Base64Variant;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0001Fixture {
private static final String BASE64_ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
private static final String FDP_UNSHADED = "ch/randelshofer/fastdoubleparser/";
private static final String FDP_SHADED = "tools/jackson/core/internal/shaded/fdp/";

    void vpackStringBinaryDecodingHonorsConfiguredBase64Variant() throws Exception {
        Base64Variant variant = new Base64Variant(BASE64_ALPHABET, BASE64_ALPHABET,
                false, 'R', 4);

        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(
                new byte[] { 0x43, 'P', 'E', 'M' })) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertFalse(variant.usesPadding());
            assertEquals('R', variant.getPaddingChar());
            assertEquals(4, variant.getMaxLineLength());
            assertEquals(BASE64_ALPHABET, variant.toString());
            assertEquals(BASE64_ALPHABET, variant.getName());
            assertEquals((byte) 'R', variant.getPaddingByte());
            assertArrayEquals(new byte[] { 0x3C, 0x43 }, parser.getBinaryValue(variant));
        }
    }

    void vpackStringBinaryDecodingRejectsInvalidBase64() throws Exception {
        Base64Variant variant = new Base64Variant(BASE64_ALPHABET, BASE64_ALPHABET,
                false, 'x', 'x');
        byte[] invalid = new byte[] { 0x4C, '-', '%', '8', 'e', 'n', '$', '9', 'm', '=', '>', '$', 'm' };

        try (VPackParser parser = (VPackParser) new VPackFactory().createParser(invalid)) {
            assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
            assertFalse(variant.usesPadding());
            assertEquals('x', variant.getPaddingChar());
            assertEquals(120, variant.getMaxLineLength());
            assertEquals(BASE64_ALPHABET, variant.toString());
            assertEquals(BASE64_ALPHABET, variant.getName());
            assertEquals((byte) 'x', variant.getPaddingByte());
            assertThrows(StreamReadException.class, () -> parser.getBinaryValue(variant));
        }
    }

    void writeBinaryUsesNativeVpackBytesInsteadOfBase64Text() throws Exception {
        Base64Variant variant = new Base64Variant(BASE64_ALPHABET, BASE64_ALPHABET,
                false, 'L', 3);
        byte[] zeros = new byte[9];
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (VPackGenerator generator = (VPackGenerator) new VPackFactory()
                .createGenerator(output)) {
            assertFalse(variant.usesPadding());
            assertEquals('L', variant.getPaddingChar());
            assertEquals(3, variant.getMaxLineLength());
            assertEquals(BASE64_ALPHABET, variant.toString());
            assertEquals(BASE64_ALPHABET, variant.getName());
            assertEquals((byte) 'L', variant.getPaddingByte());
            generator.writeBinary(variant, zeros, 0, zeros.length);
        }

        assertArrayEquals(new byte[] {
                (byte) 0xC0, 0x09, 0, 0, 0, 0, 0, 0, 0, 0, 0
        }, output.toByteArray());
    }
private static Path packagedJar() {
        Path jar = Path.of("target", "jackson-dataformat-velocypack-5.0.0-t28-test.jar")
                .toAbsolutePath();
        if (!Files.isRegularFile(jar)) {
            throw new AssertionError("packaged VelocyPack JAR is missing: " + jar);
        }
        return jar;
    }

    void __invoke_vpackStringBinaryDecodingHonorsConfiguredBase64Variant() throws Exception {
        try {
            vpackStringBinaryDecodingHonorsConfiguredBase64Variant();
        } finally {
        }
    }


    void __invoke_vpackStringBinaryDecodingRejectsInvalidBase64() throws Exception {
        try {
            vpackStringBinaryDecodingRejectsInvalidBase64();
        } finally {
        }
    }


    void __invoke_writeBinaryUsesNativeVpackBytesInsteadOfBase64Text() throws Exception {
        try {
            writeBinaryUsesNativeVpackBytesInsteadOfBase64Text();
        } finally {
        }
    }

}
