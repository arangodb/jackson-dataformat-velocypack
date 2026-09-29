package tools.jackson.databind.ser.jdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0595Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[][] BIG_INTEGER_ROOTS = {
            VPackWireFixtureTest.hex("31"),
            VPackWireFixtureTest.hex("28 0a"),
            VPackWireFixtureTest.hex("30"),
            VPackWireFixtureTest.hex("2b d2 02 96 49"),
            VPackWireFixtureTest.hex(
                    "c8 0e 00 00 00 00 01 23 45 67 89 01 23 45 67 89"
                  + " 01 23 45 68"),
            VPackWireFixtureTest.hex(
                    "d0 10 00 00 00 00 01 25 00 00 12 43 26 90 45 97"
                  + " 09 03 47 54 74 57")
    };
private static final BigInteger[] BIG_INTEGER_VALUES = {
            BigInteger.ONE,
            BigInteger.TEN,
            BigInteger.ZERO,
            BigInteger.valueOf(1234567890L),
            new BigInteger("123456789012345678901234568"),
            new BigInteger("-1250000124326904597090347547457")
    };
private static final byte[][] DOUBLE_ROOTS = {
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 00 00"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 3f"),
            VPackWireFixtureTest.hex("1b 9a 99 99 99 99 99 b9 3f"),
            VPackWireFixtureTest.hex("1b e1 7a 14 ae 47 81 42 c0"),
            VPackWireFixtureTest.hex("1b 52 b8 1e 85 eb 3f 8f 40"),
            VPackWireFixtureTest.hex("1b 33 33 33 33 33 33 d3 3f"),
            VPackWireFixtureTest.hex("1b 66 66 66 66 66 a6 40 40"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f8 7f"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 7f"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 ff")
    };
private static final double[] DOUBLE_VALUES = {
            0.0, 1.0, 0.1, -37.01, 999.99, 0.3, 33.3,
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    };
private static final byte[][] FLOAT_ROOTS = {
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 00 00"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 3f"),
            VPackWireFixtureTest.hex("1b 00 00 00 a0 99 99 b9 3f"),
            VPackWireFixtureTest.hex("1b 00 00 00 a0 47 81 42 c0"),
            VPackWireFixtureTest.hex("1b 00 00 00 80 eb 3f 8f 40"),
            VPackWireFixtureTest.hex("1b 00 00 00 40 33 33 d3 3f"),
            VPackWireFixtureTest.hex("1b 00 00 00 60 66 a6 40 40"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f8 7f"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 7f"),
            VPackWireFixtureTest.hex("1b 00 00 00 00 00 00 f0 ff")
    };
private static final double[] FLOAT_SOURCE_VALUES = {
            0.0, 1.0, 0.1, -37.01, 999.99, 0.3, 33.3,
            Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    };

    // Provenance: NumberSerTest#testBigDecimalAsString2519Typed().
    void testBigDecimalAsString2519TypedVpack() throws Exception {
        Bean2519Typed input = new Bean2519Typed();
        input.values.add(new BigDecimal("2.34"));
        ObjectMapper mapper = stringBigDecimalMapper();
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 02 07 44 32 2e 33 34 03");

        assertArrayEquals(expected, mapper.writeValueAsBytes(input));
        Bean2519Typed result = mapper.readValue(expected, Bean2519Typed.class);
        assertEquals(List.of(new BigDecimal("2.34")), result.values);
    }

    // Provenance: NumberSerTest#testBigDecimalAsString2519Untyped().
    void testBigDecimalAsString2519UntypedVpack() throws Exception {
        Bean2519Untyped input = new Bean2519Untyped();
        input.values.add(new BigDecimal("2.34"));
        ObjectMapper mapper = stringBigDecimalMapper();
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 02 07 44 32 2e 33 34 03");

        assertArrayEquals(expected, mapper.writeValueAsBytes(input));
        Bean2519Untyped result = mapper.readValue(expected, Bean2519Untyped.class);
        assertEquals(SetOf.decimal234(), new HashSet<>(result.values));
    }

    // Provenance: NumberSerTest#testBigInteger().
    void testBigIntegerVpack() throws Exception {
        for (int i = 0; i < BIG_INTEGER_VALUES.length; ++i) {
            BigInteger value = BIG_INTEGER_VALUES[i];
            byte[] expected = BIG_INTEGER_ROOTS[i];
            assertArrayEquals(expected, MAPPER.writeValueAsBytes(value));
            assertEquals(value, MAPPER.readValue(expected, BigInteger.class));
        }
    }

    // Provenance: NumberSerTest#testBigIntegerAsPlainTest().
    void testBigIntegerAsPlainTestVpack() throws Exception {
        BigDecimal value = new BigDecimal("0.0000000005");
        BigDecimalAsString input = new BigDecimalAsString(value);
        byte[] defaultExpected = VPackWireFixtureTest.hex(
                "0b 10 01 45 76 61 6c 75 65 45 35 45 2d 31 30 03");
        byte[] plainExpected = VPackWireFixtureTest.hex(
                "0b 17 01 45 76 61 6c 75 65 4c 30 2e 30 30 30 30 30 30 30 30 30 35 03");

        assertArrayEquals(defaultExpected, MAPPER.writeValueAsBytes(input));
        ObjectMapper plain = VPackMapper.builder()
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN).build();
        assertArrayEquals(plainExpected, plain.writeValueAsBytes(input));
    }

    // Provenance: NumberSerTest#testConfigOverrideJdkNumber().
    void testConfigOverrideJdkNumberVpack() throws Exception {
        ObjectMapper mapper = stringBigDecimalMapper();
        byte[] expected = VPackWireFixtureTest.hex("47 31 32 33 2e 34 35 36");
        assertArrayEquals(expected, mapper.writeValueAsBytes(new BigDecimal("123.456")));
        assertEquals("123.456", mapper.readValue(expected, String.class));
    }

    // Provenance: NumberSerTest#testConfigOverrideNonJdkNumber().
    void testConfigOverrideNonJdkNumberVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(MyBigDecimal.class,
                        c -> c.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING)))
                .build();
        byte[] expected = VPackWireFixtureTest.hex("47 31 32 33 2e 34 35 36");
        assertArrayEquals(expected, mapper.writeValueAsBytes(new MyBigDecimal("123.456")));
        assertEquals("123.456", mapper.readValue(expected, String.class));
    }

    // Provenance: NumberSerTest#testConfigOverridesForNumbers().
    void testConfigOverridesForNumbersVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withAllConfigOverrides(all -> {
                    all.findOrCreateOverride(Integer.TYPE).setFormat(
                            JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
                    all.findOrCreateOverride(Double.TYPE).setFormat(
                            JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
                    all.findOrCreateOverride(BigDecimal.class).setFormat(
                            JsonFormat.Value.forShape(JsonFormat.Shape.STRING));
                }).build();

        assertArrayEquals(VPackWireFixtureTest.hex("0b 08 01 41 69 41 33 03"),
                mapper.writeValueAsBytes(new IntWrapper(3)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 30 2e 37 35 03"),
                mapper.writeValueAsBytes(new DoubleWrapper(0.75)));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 45 76 61 6c 75 65 44 2d 30 2e 35 03"),
                mapper.writeValueAsBytes(new BigDecimalWrapper(BigDecimal.valueOf(-0.5))));
    }

    // Provenance: NumberSerTest#testCustomSerializationBigDecimalAsNumber().
    void testCustomSerializationBigDecimalAsNumberVpack() throws Exception {
        SimpleModule module = new SimpleModule("number-ser-test");
        module.addSerializer(BigDecimal.class, new BigDecimalAsNumberSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 c8 01 ff ff ff ff 20 03");
        assertArrayEquals(expected, mapper.writeValueAsBytes(new BigDecimalHolder("2")));
    }

    // Provenance: NumberSerTest#testCustomSerializationBigDecimalAsString().
    void testCustomSerializationBigDecimalAsStringVpack() throws Exception {
        SimpleModule module = new SimpleModule("string-ser-test");
        module.addSerializer(BigDecimal.class, new BigDecimalAsStringSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 0e 01 45 76 61 6c 75 65 43 32 2e 30 03");
        assertArrayEquals(expected, mapper.writeValueAsBytes(new BigDecimalHolder("2")));
    }

    // Provenance: NumberSerTest#testDouble().
    void testDoubleVpack() throws Exception {
        for (int i = 0; i < DOUBLE_VALUES.length; ++i) {
            double value = DOUBLE_VALUES[i];
            byte[] expected = DOUBLE_ROOTS[i];
            assertArrayEquals(expected, MAPPER.writeValueAsBytes(Double.valueOf(value)));
            assertEquals(value, MAPPER.readValue(expected, Double.class));
        }
    }

    // Provenance: NumberSerTest#testFloat().
    void testFloatVpack() throws Exception {
        for (int i = 0; i < FLOAT_SOURCE_VALUES.length; ++i) {
            float value = (float) FLOAT_SOURCE_VALUES[i];
            byte[] expected = FLOAT_ROOTS[i];
            assertArrayEquals(expected, MAPPER.writeValueAsBytes(Float.valueOf(value)));
            assertEquals(value, MAPPER.readValue(expected, Float.class));
        }
    }

    // Provenance: NumberSerTest#testIntArray().
    void testIntArrayVpack() throws Exception {
        byte[] primitive = VPackWireFixtureTest.hex("02 04 30 3d");
        byte[] boxed = VPackWireFixtureTest.hex("06 08 02 28 0d 39 03 05");
        assertArrayEquals(primitive, MAPPER.writeValueAsBytes(new int[] { 0, -3 }));
        assertArrayEquals(boxed, MAPPER.writeValueAsBytes(new Integer[] { 13, 9 }));
        assertArrayEquals(new int[] { 0, -3 }, MAPPER.readValue(primitive, int[].class));
        assertArrayEquals(new Integer[] { 13, 9 }, MAPPER.readValue(boxed, Integer[].class));
    }
private static ObjectMapper stringBigDecimalMapper() {
        return VPackMapper.builder().withConfigOverride(BigDecimal.class,
                c -> c.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING))).build();
    }
static class Bean2519Typed {
        public List<BigDecimal> values = new ArrayList<>();
    }
static class Bean2519Untyped {
        public Collection<BigDecimal> values = new HashSet<>();
    }
static class BigDecimalAsString {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public BigDecimal value;
        BigDecimalAsString(BigDecimal value) { this.value = value; }
    }
static class BigDecimalHolder {
        public BigDecimal value;
        BigDecimalHolder(String value) { this.value = new BigDecimal(value); }
    }
static class BigDecimalWrapper {
        public BigDecimal value;
        BigDecimalWrapper(BigDecimal value) { this.value = value; }
    }
static class IntWrapper {
        public int i;
        IntWrapper(int value) { i = value; }
    }
static class DoubleWrapper {
        public double value;
        DoubleWrapper(double value) { this.value = value; }
    }
static class MyBigDecimal extends BigDecimal {
        MyBigDecimal(String value) { super(value); }
    }
static class BigDecimalAsStringSerializer extends ValueSerializer<BigDecimal> {
        private final DecimalFormat df = new DecimalFormat("0.0",
                new DecimalFormatSymbols(Locale.ENGLISH));

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeString(df.format(value));
        }
    }
static class BigDecimalAsNumberSerializer extends ValueSerializer<BigDecimal> {
        private final DecimalFormat df = new DecimalFormat("0.0",
                new DecimalFormatSymbols(Locale.ENGLISH));

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext serializers) {
            gen.writeNumber(df.format(value));
        }
    }
private static final class SetOf {
        static HashSet<BigDecimal> decimal234() {
            HashSet<BigDecimal> values = new HashSet<>();
            values.add(new BigDecimal("2.34"));
            return values;
        }
    }

    void __invoke_testBigDecimalAsString2519TypedVpack() throws Exception {
        try {
            testBigDecimalAsString2519TypedVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalAsString2519UntypedVpack() throws Exception {
        try {
            testBigDecimalAsString2519UntypedVpack();
        } finally {
        }
    }


    void __invoke_testBigIntegerVpack() throws Exception {
        try {
            testBigIntegerVpack();
        } finally {
        }
    }


    void __invoke_testBigIntegerAsPlainTestVpack() throws Exception {
        try {
            testBigIntegerAsPlainTestVpack();
        } finally {
        }
    }


    void __invoke_testConfigOverrideJdkNumberVpack() throws Exception {
        try {
            testConfigOverrideJdkNumberVpack();
        } finally {
        }
    }


    void __invoke_testConfigOverrideNonJdkNumberVpack() throws Exception {
        try {
            testConfigOverrideNonJdkNumberVpack();
        } finally {
        }
    }


    void __invoke_testConfigOverridesForNumbersVpack() throws Exception {
        try {
            testConfigOverridesForNumbersVpack();
        } finally {
        }
    }


    void __invoke_testCustomSerializationBigDecimalAsNumberVpack() throws Exception {
        try {
            testCustomSerializationBigDecimalAsNumberVpack();
        } finally {
        }
    }


    void __invoke_testCustomSerializationBigDecimalAsStringVpack() throws Exception {
        try {
            testCustomSerializationBigDecimalAsStringVpack();
        } finally {
        }
    }


    void __invoke_testDoubleVpack() throws Exception {
        try {
            testDoubleVpack();
        } finally {
        }
    }


    void __invoke_testFloatVpack() throws Exception {
        try {
            testFloatVpack();
        } finally {
        }
    }


    void __invoke_testIntArrayVpack() throws Exception {
        try {
            testIntArrayVpack();
        } finally {
        }
    }

}
