package tools.jackson.databind.util;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamWriteException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0628F0 {
private static final int[] SIZES = { 3, 19, 99, 1007, 19999, 99001 };
private final ObjectMapper mapper = VPackMapper.builder().build();

    // Provenance: NumberUtilTest#testSimpleDigits through #testWhitespaceAndSpecialChars().
    // Text spelling is not wire data: legal VPack numbers get literal encodings and
    // Java-only integer spellings / malformed text are rejected by the text writer.
    void jdkIntegerLexemesMapToVpackNumbersOrTextRejections() throws Exception {
        assertTextNumber("0", 0x30);
        assertTextNumber("1", 0x31);
        assertTextNumber("42", 0x28, 0x2a);
        assertTextNumber("1234567890", 0x2b, 0xd2, 0x02, 0x96, 0x49);
        assertTextNumber("-1", 0x3f);
        assertTextNumber("-0", 0x30);
        assertTextNumber("-1234567890", 0x23, 0x2e, 0xfd, 0x69, 0xb6);
        assertTextNumber("1.0", 0xc8, 1, 0xff, 0xff, 0xff, 0xff, 0x10);
        assertTextNumber("3.14", 0xc8, 2, 0xfe, 0xff, 0xff, 0xff, 0x03, 0x14);
        assertTextNumber("1e10", 0xc8, 1, 0x0a, 0, 0, 0, 1);
        assertTextNumber("1E10", 0xc8, 1, 0x0a, 0, 0, 0, 1);

        for (String text : new String[] { "007", "00", "+1", "+42", "", "-", "+",
                "abc", "12a", "a12", "NOT_A_NUMBER", " 1", "1 ", " ", "1,000" }) {
            assertThrows(StreamWriteException.class, () -> writeTextNumber(text), text);
        }
    }
private static Integer[] values(int size) {
        Integer[] values = new Integer[size];
        Random random = new Random(size);
        for (int i = 0; i < size; ++i) {
            values[i] = random.nextInt();
        }
        return values;
    }
private static void assertTextNumber(String text, int... expected) throws Exception {
        assertArrayEquals(bytes(expected), writeTextNumber(text), text);
    }
private static byte[] writeTextNumber(String text) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeNumber(text);
        }
        return output.toByteArray();
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_jdkIntegerLexemesMapToVpackNumbersOrTextRejections() throws Exception {
        try {
            jdkIntegerLexemesMapToVpackNumbersOrTextRejections();
        } finally {
        }
    }

}
