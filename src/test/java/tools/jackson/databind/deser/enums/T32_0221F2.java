package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0221F2 {
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

    // Provenance: EnumDeserFromIntJsonValueTest#testEnumFromInt1850Method.
    void testEnumFromInt1850MethodVpack() throws Exception {
        assertEquals(Bean1850IntMethod.A,
                MAPPER.readValue(INT_101, Bean1850IntMethod.class));
        assertArrayEquals(INT_101, MAPPER.writeValueAsBytes(Bean1850IntMethod.A));
    }

    // Provenance: EnumDeserFromIntJsonValueTest#testEnumFromInt1850Field.
    void testEnumFromInt1850FieldVpack() throws Exception {
        assertEquals(Bean1850IntField.A,
                MAPPER.readValue(INT_202, Bean1850IntField.class));
        assertArrayEquals(INT_202, MAPPER.writeValueAsBytes(Bean1850IntField.A));
    }

    // Provenance: EnumDeserFromIntJsonValueTest#testEnumFromLong1850Method.
    void testEnumFromLong1850MethodVpack() throws Exception {
        assertEquals(Bean1850LongMethod.A,
                MAPPER.readValue(INT_NEGATIVE_13, Bean1850LongMethod.class));
        assertArrayEquals(INT_NEGATIVE_13,
                MAPPER.writeValueAsBytes(Bean1850LongMethod.A));
    }

    // Provenance: EnumDeserFromIntJsonValueTest#testEnumFromLong1850Field.
    void testEnumFromLong1850FieldVpack() throws Exception {
        assertEquals(Bean1850LongField.A,
                MAPPER.readValue(INT_29, Bean1850LongField.class));
        assertArrayEquals(INT_29, MAPPER.writeValueAsBytes(Bean1850LongField.A));
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

    void __invoke_testEnumFromInt1850MethodVpack() throws Exception {
        try {
            testEnumFromInt1850MethodVpack();
        } finally {
        }
    }


    void __invoke_testEnumFromInt1850FieldVpack() throws Exception {
        try {
            testEnumFromInt1850FieldVpack();
        } finally {
        }
    }


    void __invoke_testEnumFromLong1850MethodVpack() throws Exception {
        try {
            testEnumFromLong1850MethodVpack();
        } finally {
        }
    }


    void __invoke_testEnumFromLong1850FieldVpack() throws Exception {
        try {
            testEnumFromLong1850FieldVpack();
        } finally {
        }
    }

}
