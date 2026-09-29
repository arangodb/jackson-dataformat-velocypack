package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0221F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .build();
private static final byte[] ZERO = VPackWireFixtureTest.hex("44 5a 45 52 4f");
private static final byte[] ONE = VPackWireFixtureTest.hex("43 4f 4e 45");
private static final byte[] TWO = VPackWireFixtureTest.hex("43 54 57 4f");
private static final byte[] STRING_ZERO = VPackWireFixtureTest.hex("41 30");
private static final byte[] STRING_ONE = VPackWireFixtureTest.hex("41 31");
private static final byte[] STRING_TWO = VPackWireFixtureTest.hex("41 32");
private static final byte[] COLOR_MODE = VPackWireFixtureTest.hex(
            "14 13 4a 63 6f 6c 6f 72 5f 6d 6f 64 65 44 52 47 42 61 01");
private static final byte[] RGB_A = VPackWireFixtureTest.hex("44 52 47 42 61");
private static final byte[] RGBA = VPackWireFixtureTest.hex("44 52 47 42 41");
private static final byte[] INT_101 = VPackWireFixtureTest.hex("28 65");
private static final byte[] INT_202 = VPackWireFixtureTest.hex("28 ca");
private static final byte[] INT_NEGATIVE_13 = VPackWireFixtureTest.hex("20 f3");
private static final byte[] INT_29 = VPackWireFixtureTest.hex("28 1d");

    // Provenance: EnumDefaultReadTest#testWithoutCustomFeatures.
    void testWithoutCustomFeaturesVpack() throws Exception {
        ObjectReader reader = MAPPER.reader();

        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(ONE));
        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(STRING_ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, TWO, SimpleEnum.class);
        assertInvalid(reader, STRING_TWO, SimpleEnum.class);

        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(ONE));
        assertInvalid(reader, TWO, SimpleEnumWithDefault.class);
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, SimpleEnumWithDefault.class);

        assertInvalid(reader, ZERO, CustomEnum.class);
        assertInvalid(reader, ONE, CustomEnum.class);
        assertInvalid(reader, TWO, CustomEnum.class);
        assertEquals(CustomEnum.ZERO, reader.forType(CustomEnum.class).readValue(STRING_ZERO));
        assertEquals(CustomEnum.ONE, reader.forType(CustomEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnum.class);

        assertInvalid(reader, ZERO, CustomEnumWithDefault.class);
        assertInvalid(reader, ONE, CustomEnumWithDefault.class);
        assertInvalid(reader, TWO, CustomEnumWithDefault.class);
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(CustomEnumWithDefault.ONE,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnumWithDefault.class);
    }

    // Provenance: EnumDefaultReadTest#testWithFailOnNumbers.
    void testWithFailOnNumbersVpack() throws Exception {
        ObjectReader reader = MAPPER.reader().with(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);

        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(ONE));
        assertInvalid(reader, TWO, SimpleEnum.class);
        assertInvalid(reader, STRING_ZERO, SimpleEnum.class);
        assertInvalid(reader, STRING_ONE, SimpleEnum.class);
        assertInvalid(reader, STRING_TWO, SimpleEnum.class);

        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(ONE));
        assertInvalid(reader, TWO, SimpleEnumWithDefault.class);
        assertInvalid(reader, STRING_ZERO, SimpleEnumWithDefault.class);
        assertInvalid(reader, STRING_ONE, SimpleEnumWithDefault.class);
        assertInvalid(reader, STRING_TWO, SimpleEnumWithDefault.class);

        assertInvalid(reader, ZERO, CustomEnum.class);
        assertInvalid(reader, ONE, CustomEnum.class);
        assertInvalid(reader, TWO, CustomEnum.class);
        assertEquals(CustomEnum.ZERO, reader.forType(CustomEnum.class).readValue(STRING_ZERO));
        assertEquals(CustomEnum.ONE, reader.forType(CustomEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnum.class);

        assertInvalid(reader, ZERO, CustomEnumWithDefault.class);
        assertInvalid(reader, ONE, CustomEnumWithDefault.class);
        assertInvalid(reader, TWO, CustomEnumWithDefault.class);
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(CustomEnumWithDefault.ONE,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnumWithDefault.class);
    }

    // Provenance: EnumDefaultReadTest#testWithReadUnknownAsDefault.
    void testWithReadUnknownAsDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(ONE));
        assertInvalid(reader, TWO, SimpleEnum.class);
        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(STRING_ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, SimpleEnum.class);

        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(ONE));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(TWO));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ONE));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_TWO));

        assertInvalid(reader, ZERO, CustomEnum.class);
        assertInvalid(reader, ONE, CustomEnum.class);
        assertInvalid(reader, TWO, CustomEnum.class);
        assertEquals(CustomEnum.ZERO, reader.forType(CustomEnum.class).readValue(STRING_ZERO));
        assertEquals(CustomEnum.ONE, reader.forType(CustomEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnum.class);

        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(ZERO));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(ONE));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(TWO));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(CustomEnumWithDefault.ONE,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ONE));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_TWO));
    }

    // Provenance: EnumDefaultReadTest#testWithFailOnNumbersAndReadUnknownAsDefault.
    void testWithFailOnNumbersAndReadUnknownAsDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .with(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        assertEquals(SimpleEnum.ZERO, reader.forType(SimpleEnum.class).readValue(ZERO));
        assertEquals(SimpleEnum.ONE, reader.forType(SimpleEnum.class).readValue(ONE));
        assertInvalid(reader, TWO, SimpleEnum.class);
        assertInvalid(reader, STRING_ZERO, SimpleEnum.class);
        assertInvalid(reader, STRING_ONE, SimpleEnum.class);
        assertInvalid(reader, STRING_TWO, SimpleEnum.class);

        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(ZERO));
        assertEquals(SimpleEnumWithDefault.ONE,
                reader.forType(SimpleEnumWithDefault.class).readValue(ONE));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(TWO));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_ONE));
        assertEquals(SimpleEnumWithDefault.ZERO,
                reader.forType(SimpleEnumWithDefault.class).readValue(STRING_TWO));

        assertInvalid(reader, ZERO, CustomEnum.class);
        assertInvalid(reader, ONE, CustomEnum.class);
        assertInvalid(reader, TWO, CustomEnum.class);
        assertEquals(CustomEnum.ZERO, reader.forType(CustomEnum.class).readValue(STRING_ZERO));
        assertEquals(CustomEnum.ONE, reader.forType(CustomEnum.class).readValue(STRING_ONE));
        assertInvalid(reader, STRING_TWO, CustomEnum.class);

        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(ZERO));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(ONE));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(TWO));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ZERO));
        assertEquals(CustomEnumWithDefault.ONE,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_ONE));
        assertEquals(CustomEnumWithDefault.ZERO,
                reader.forType(CustomEnumWithDefault.class).readValue(STRING_TWO));
    }
private static <T> void assertInvalid(ObjectReader reader, byte[] input,
            Class<T> type) throws Exception {
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> reader.forType(type).readValue(input));
        assertTrue(exception.getMessage().contains("Cannot deserialize value of type"));
    }
enum SimpleEnum { ZERO, ONE }
enum SimpleEnumWithDefault {
        @com.fasterxml.jackson.annotation.JsonEnumDefaultValue
        ZERO,
        ONE
    }
enum CustomEnum {
        ZERO(0),
        ONE(1);

        private final int number;

        CustomEnum(int number) { this.number = number; }

        @JsonValue
        int getNumber() { return number; }
    }
enum CustomEnumWithDefault {
        @com.fasterxml.jackson.annotation.JsonEnumDefaultValue
        ZERO(0),
        ONE(1);

        private final int number;

        CustomEnumWithDefault(int number) { this.number = number; }

        @JsonValue
        int getNumber() { return number; }
    }
enum ColorMode {
        RGB,
        RGBa,
        RGBA
    }
static class Bug {
        public ColorMode colorMode;
    }
enum ColorMode4409Snake {
        RGB,
        RGBa,
        @JsonProperty("RGBA")
        RGBA
    }
enum Bean1850IntMethod {
        A(101);

        private final int x;

        Bean1850IntMethod(int x) { this.x = x; }

        @JsonValue
        public int code() { return x; }
    }
enum Bean1850IntField {
        A(202);

        @JsonValue
        public final int x;

        Bean1850IntField(int x) { this.x = x; }
    }
enum Bean1850LongMethod {
        A(-13L);

        private final long x;

        Bean1850LongMethod(long x) { this.x = x; }

        @JsonValue
        public long code() { return x; }
    }
enum Bean1850LongField {
        A(29L);

        @JsonValue
        public final long x;

        Bean1850LongField(long x) { this.x = x; }
    }

    void __invoke_testWithoutCustomFeaturesVpack() throws Exception {
        try {
            testWithoutCustomFeaturesVpack();
        } finally {
        }
    }


    void __invoke_testWithFailOnNumbersVpack() throws Exception {
        try {
            testWithFailOnNumbersVpack();
        } finally {
        }
    }


    void __invoke_testWithReadUnknownAsDefaultVpack() throws Exception {
        try {
            testWithReadUnknownAsDefaultVpack();
        } finally {
        }
    }


    void __invoke_testWithFailOnNumbersAndReadUnknownAsDefaultVpack() throws Exception {
        try {
            testWithFailOnNumbersAndReadUnknownAsDefaultVpack();
        } finally {
        }
    }

}
