package tools.jackson.databind.deser.jdk;

import java.nio.charset.StandardCharsets;

import tools.jackson.core.Base64Variants;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0266F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final byte[] LONG_VALUE = VPackWireFixtureTest.hex(
            "2d cb 04 fb 71 1f 01");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] SCIENTIFIC_UPPER = text("3E-8");
private static final byte[] SCIENTIFIC_LOWER = text("3e-8");
private static final byte[] DECIMAL_INTEGER = text("300000000");
private static final byte[] LONG_TEXT = text("123456789012");
private static final byte[] TEXTUAL_NULL = text("null");
private static final byte[] BOOLEAN_TRUE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 76 1a 01");
private static final byte[] BOOLEAN_NULL_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 76 18 01");
private static final byte[] BOOLEAN_ONE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 76 31 01");
private static final byte[] BOOLEAN_ARRAY = VPackWireFixtureTest.hex(
            "13 05 18 19 02");
private static final byte[] BOOLEAN_THREE_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 62 33 01");
private static final byte[] BOOLEAN_ZERO = VPackWireFixtureTest.hex("30");
private static final byte[] BOOLEAN_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] BOOLEAN_ONE_PROPERTY = VPackWireFixtureTest.hex(
            "14 06 41 62 31 01");
private static final byte[] BYTE_NEGATIVE_42 = VPackWireFixtureTest.hex(
            "20 d6");
private static final byte[] BYTE_STRING_NEGATIVE_12 = text("-12");
private static final byte[] BYTE_DOUBLE_39_07 = VPackWireFixtureTest.hex(
            "1b 29 5c 8f c2 f5 88 43 40");
private static final byte[] BYTE_DOUBLE_300_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 c8 72 40");
private static final byte[] BYTE_DOUBLE_NEGATIVE_200_5 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 10 69 c0");
private static final byte[] BYTE_300_5_PROPERTY = VPackWireFixtureTest.hex(
            "14 16 49 62 79 74 65 56 61 6c 75 65 "
          + "1b 00 00 00 00 00 c8 72 40 01");
private static final byte[] BYTE_NEGATIVE_200_5_PROPERTY = VPackWireFixtureTest.hex(
            "14 16 49 62 79 74 65 56 61 6c 75 65 "
          + "1b 00 00 00 00 00 10 69 c0 01");
private static final byte[] CHARACTER_A = text("a");
private static final byte[] CHARACTER_X_CODE = VPackWireFixtureTest.hex(
            "28 58");
private static final byte[] CHARACTER_SPACE = text(" ");
private static final byte[] CHARACTER_NULL_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 76 18 01");
private static final byte[] DOUBLE_016 = VPackWireFixtureTest.hex(
            "14 0e 41 76 1b fc a9 f1 d2 4d 62 90 3f 01");
private static final byte[] DOUBLE_NULL_OBJECT = VPackWireFixtureTest.hex(
            "14 06 41 76 18 01");
private static final byte[] DOUBLE_NULL_ARRAY = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final String BASE64_INPUT =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw"
          + "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA==";
private static final String BASE64_MIME =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw"
          + "YWJjZGVmZ2hpamtsbW5vcHFyc3R1\ndnd4eXoxMjM0NTY3ODkwWA==";
private static final String BASE64_URL =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw"
          + "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA";
private static final String BASE64_PEM =
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw"
          + "YWJjZGVmZ2hpamts\nbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA==";

    // Provenance: JDKScalarsDeserTest#testBase64Variants().
    void testBase64Variants() throws Exception {
        byte[] input = ("abcdefghijklmnopqrstuvwxyz1234567890"
                + "abcdefghijklmnopqrstuvwxyz1234567890X").getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(input, MAPPER.readValue(text(BASE64_INPUT), byte[].class));

        ObjectReader reader = MAPPER.readerFor(byte[].class);
        assertArrayEquals(input, (byte[]) reader.with(Base64Variants.MIME_NO_LINEFEEDS)
                .readValue(text(BASE64_INPUT)));
        assertArrayEquals(input, (byte[]) reader.with(Base64Variants.MIME)
                .readValue(text(BASE64_MIME)));
        assertArrayEquals(input, (byte[]) reader.with(Base64Variants.MODIFIED_FOR_URL)
                .readValue(text(BASE64_URL)));
        assertArrayEquals(input, (byte[]) reader.with(Base64Variants.PEM)
                .readValue(text(BASE64_PEM)));
    }

    // Provenance: JDKScalarsDeserTest#testBooleanPrimitive().
    void testBooleanPrimitive() throws Exception {
        BooleanBean result = MAPPER.readValue(BOOLEAN_TRUE_OBJECT, BooleanBean.class);
        assertTrue(result.v);
        result = MAPPER.readValue(BOOLEAN_NULL_OBJECT, BooleanBean.class);
        assertNotNull(result);
        assertFalse(result.v);
        result = MAPPER.readValue(BOOLEAN_ONE_OBJECT, BooleanBean.class);
        assertNotNull(result);
        assertTrue(result.v);

        boolean[] array = MAPPER.readValue(BOOLEAN_ARRAY, boolean[].class);
        assertNotNull(array);
        assertEquals(2, array.length);
        assertFalse(array[0]);
        assertFalse(array[1]);
    }

    // Provenance: JDKScalarsDeserTest#testBooleanWrapper().
    void testBooleanWrapper() throws Exception {
        assertEquals(Boolean.TRUE, MAPPER.readValue(VPackWireFixtureTest.hex("1a"), Boolean.class));
        assertEquals(Boolean.FALSE, MAPPER.readValue(VPackWireFixtureTest.hex("19"), Boolean.class));
    }

    // Provenance: JDKScalarsDeserTest#testByteFromFloatOverflow().
    void testByteFromFloatOverflow() throws Exception {
        assertByteOverflow(BYTE_DOUBLE_300_5, Byte.class);
        assertByteOverflow(BYTE_DOUBLE_NEGATIVE_200_5, Byte.class);
        assertByteOverflow(BYTE_300_5_PROPERTY, PrimitivesBean.class);
        assertByteOverflow(BYTE_NEGATIVE_200_5_PROPERTY, PrimitivesBean.class);
    }

    // Provenance: JDKScalarsDeserTest#testByteWrapper().
    void testByteWrapper() throws Exception {
        assertEquals(Byte.valueOf((byte) -42), MAPPER.readValue(BYTE_NEGATIVE_42, Byte.class));
        assertEquals(Byte.valueOf((byte) -12), MAPPER.readValue(BYTE_STRING_NEGATIVE_12, Byte.class));
        assertEquals(Byte.valueOf((byte) 39), MAPPER.readValue(BYTE_DOUBLE_39_07, Byte.class));
    }

    // Provenance: JDKScalarsDeserTest#testCharacterWrapper().
    void testCharacterWrapper() throws Exception {
        assertEquals(Character.valueOf('a'), MAPPER.readValue(CHARACTER_A, Character.class));
        assertEquals(Character.valueOf('X'), MAPPER.readValue(CHARACTER_X_CODE, Character.class));
        assertEquals(Character.valueOf(' '), MAPPER.readValue(CHARACTER_SPACE, Character.class));

        CharacterWrapperBean wrapper = MAPPER.readValue(CHARACTER_NULL_OBJECT,
                CharacterWrapperBean.class);
        assertNotNull(wrapper);
        assertNull(wrapper.v);

        ObjectReader strict = MAPPER.readerFor(CharacterBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        assertThrows(MismatchedInputException.class,
                () -> strict.readValue(CHARACTER_NULL_OBJECT));
        CharacterBean charBean = MAPPER.readerFor(CharacterBean.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(CHARACTER_NULL_OBJECT);
        assertNotNull(charBean);
        assertEquals('\u0000', charBean.v);
    }

    // Provenance: JDKScalarsDeserTest#testDoublePrimitive().
    void testDoublePrimitive() throws Exception {
        DoubleBean result = MAPPER.readValue(DOUBLE_016, DoubleBean.class);
        assertEquals(0.016, result.v);
        result = MAPPER.readValue(DOUBLE_NULL_OBJECT, DoubleBean.class);
        assertNotNull(result);
        assertEquals(0.0, result.v);

        double[] array = MAPPER.readValue(DOUBLE_NULL_ARRAY, double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0.0, array[0]);
    }
private static void assertByteOverflow(byte[] input, Class<?> type) {
        InputCoercionException failure = assertThrows(InputCoercionException.class,
                () -> MAPPER.readValue(input, type));
        assertTrue(failure.getMessage().contains("out of range of `byte`"),
                failure.getMessage());
    }
private static byte[] text(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
static class BooleanBean {
        public boolean v;
    }
static class BooleanWrapper {
        public Boolean b;
    }
static class PrimitivesBean {
        public byte byteValue;
    }
static class CharacterBean {
        public char v;
    }
static class CharacterWrapperBean {
        public Character v;
    }
static class DoubleBean {
        public double v;
    }

    void __invoke_testBase64Variants() throws Exception {
        try {
            testBase64Variants();
        } finally {
        }
    }


    void __invoke_testBooleanPrimitive() throws Exception {
        try {
            testBooleanPrimitive();
        } finally {
        }
    }


    void __invoke_testBooleanWrapper() throws Exception {
        try {
            testBooleanWrapper();
        } finally {
        }
    }


    void __invoke_testByteFromFloatOverflow() throws Exception {
        try {
            testByteFromFloatOverflow();
        } finally {
        }
    }


    void __invoke_testByteWrapper() throws Exception {
        try {
            testByteWrapper();
        } finally {
        }
    }


    void __invoke_testCharacterWrapper() throws Exception {
        try {
            testCharacterWrapper();
        } finally {
        }
    }


    void __invoke_testDoublePrimitive() throws Exception {
        try {
            testDoublePrimitive();
        } finally {
        }
    }

}
