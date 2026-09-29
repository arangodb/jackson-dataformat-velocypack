package tools.jackson.databind.format;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0385F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] VALUE_HEX_A = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 41 61 03");
private static final byte[] VALUE_DECIMAL_TEN = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 42 31 30 03");
private static final byte[] VALUE_HEX_INVALID = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 58 59 5a 03");
private static final byte[] VALUE_DEFAULT_RADIX_INVALID = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 42 5f 78 03");
private static final byte[] ALL_INTEGRAL_BINARY = VPackWireFixtureTest.hex(
            "0b 8d 09 "
          + "49 42 79 74 65 56 61 6c 75 65 42 31 30 "
          + "4c 49 6e 74 65 67 65 72 56 61 6c 75 65 43 31 31 30 "
          + "49 4c 6f 6e 67 56 61 6c 75 65 44 31 30 30 30 "
          + "4a 53 68 6f 72 74 56 61 6c 75 65 43 31 30 30 "
          + "4a 62 69 67 49 6e 74 65 67 65 72 44 31 30 30 31 "
          + "49 62 79 74 65 56 61 6c 75 65 41 31 "
          + "48 69 6e 74 56 61 6c 75 65 43 31 30 31 "
          + "49 6c 6f 6e 67 56 61 6c 75 65 43 31 31 31 "
          + "4a 73 68 6f 72 74 56 61 6c 75 65 42 31 31 "
          + "03 10 21 30 3f 4f 5b 68 76");
private static final byte[] ENUM_INDEX_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0a 01 44 74 65 78 74 30 03");
private static final byte[] ENUM_POJO = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 42 61 31 03");
private static final byte[] ENUM_NUMBER_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0b 01 45 63 6f 6c 6f 72 32 03");
private static final byte[] ENUM_METHOD_OVERRIDE = VPackWireFixtureTest.hex(
            "0b 13 01 43 6b 65 79 4a 61 74 74 72 69 62 75 74 65 73 03");

    // Provenance: EnumFormatShapeTest#testEnumAsIndexViaAnnotations().
    void testEnumAsIndexViaAnnotationsVpack() throws Exception {
        assertArrayEquals(ENUM_INDEX_PROPERTY,
                MAPPER.writeValueAsBytes(new PoNUMContainer()));
    }

    // Provenance: EnumFormatShapeTest#testEnumAsObjectBroken().
    void testEnumAsObjectBrokenVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("30"),
                MAPPER.writeValueAsBytes(PoAsArray.A));
    }

    // Provenance: EnumFormatShapeTest#testEnumAsObjectValid().
    void testEnumAsObjectValidVpack() throws Exception {
        assertArrayEquals(ENUM_POJO, MAPPER.writeValueAsBytes(PoNUM.A));
    }

    // Provenance: EnumFormatShapeTest#testEnumPropertyAsNumber().
    void testEnumPropertyAsNumberVpack() throws Exception {
        assertArrayEquals(ENUM_NUMBER_PROPERTY,
                MAPPER.writeValueAsBytes(new ColorWrapper(Color.GREEN)));
    }

    // Provenance: EnumFormatShapeTest#testEnumValueAsNumber().
    void testEnumValueAsNumberVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("32"),
                MAPPER.writeValueAsBytes(Color.GREEN));
    }

    // Provenance: EnumFormatShapeTest#testEnumWithMethodOverride().
    void testEnumWithMethodOverrideVpack() throws Exception {
        assertArrayEquals(ENUM_METHOD_OVERRIDE,
                MAPPER.writeValueAsBytes(Enum2576.ATTRIBUTES));
    }
static class IntegerWrapper {
        public Integer value;

        public IntegerWrapper() { }
        public IntegerWrapper(Integer v) { value = v; }
    }
static class IntWrapper {
        public int value;

        public IntWrapper() { }
        public IntWrapper(int v) { value = v; }
    }
static class AnnotatedMethodIntWrapper {
        private int value;

        public AnnotatedMethodIntWrapper() { }
        public AnnotatedMethodIntWrapper(int v) { value = v; }

        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 16)
        public int getValue() { return value; }
    }
static class IncorrectlyAnnotatedMethodIntWrapper {
        private int value;

        public IncorrectlyAnnotatedMethodIntWrapper() { }
        public IncorrectlyAnnotatedMethodIntWrapper(int v) { value = v; }

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public int getValue() { return value; }
    }
static class AllIntegralTypeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public byte byteValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public Byte ByteValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public short shortValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public Short ShortValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public int intValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public Integer IntegerValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public long longValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public Long LongValue;
        @JsonFormat(shape = JsonFormat.Shape.STRING, radix = 2)
        public BigInteger bigInteger;

        public AllIntegralTypeWrapper() { }

        public AllIntegralTypeWrapper(byte byteValue, Byte ByteValue, short shortValue,
                Short ShortValue, int intValue, Integer IntegerValue, long longValue,
                Long LongValue, BigInteger bigInteger) {
            this.byteValue = byteValue;
            this.ByteValue = ByteValue;
            this.shortValue = shortValue;
            this.ShortValue = ShortValue;
            this.intValue = intValue;
            this.IntegerValue = IntegerValue;
            this.longValue = longValue;
            this.LongValue = LongValue;
            this.bigInteger = bigInteger;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.POJO)
    enum PoNUM {
        A("a1"), B("b2");

        @com.fasterxml.jackson.annotation.JsonProperty
        protected final String value;

        PoNUM(String v) { value = v; }

        public String getValue() { return value; }
    }
static class PoNUMContainer {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public OK text = OK.V1;
    }
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
    enum PoAsArray { A, B }
enum OK { V1("v1");
        protected String key;
        OK(String key) { this.key = key; }
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    enum Color { RED, YELLOW, GREEN }
static class ColorWrapper {
        public final Color color;

        ColorWrapper(Color color) { this.color = color; }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum Enum2576 {
        DEFAULT("default"),
        ATTRIBUTES("attributes") {
            @Override
            public String toString() { return name(); }
        };

        private final String key;

        Enum2576(String key) { this.key = key; }
        public String getKey() { return key; }
    }

    void __invoke_testEnumAsIndexViaAnnotationsVpack() throws Exception {
        try {
            testEnumAsIndexViaAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testEnumAsObjectBrokenVpack() throws Exception {
        try {
            testEnumAsObjectBrokenVpack();
        } finally {
        }
    }


    void __invoke_testEnumAsObjectValidVpack() throws Exception {
        try {
            testEnumAsObjectValidVpack();
        } finally {
        }
    }


    void __invoke_testEnumPropertyAsNumberVpack() throws Exception {
        try {
            testEnumPropertyAsNumberVpack();
        } finally {
        }
    }


    void __invoke_testEnumValueAsNumberVpack() throws Exception {
        try {
            testEnumValueAsNumberVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithMethodOverrideVpack() throws Exception {
        try {
            testEnumWithMethodOverrideVpack();
        } finally {
        }
    }

}
