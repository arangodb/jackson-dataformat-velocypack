package tools.jackson.databind.ext.jdk8;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0366Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] STREAM_LITERAL = VPackWireFixtureTest.hex(
            "13 09 41 61 41 62 41 63 03");
private static final byte[] STREAM_WRITE = VPackWireFixtureTest.hex(
            "02 08 41 61 41 62 41 63");
private static final byte[] OPTIONAL_INT_MAX = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 2b ff ff ff 7f 03");
private static final byte[] OPTIONAL_INT_MIN = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 23 00 00 00 80 03");
private static final byte[] OPTIONAL_DOUBLE_MAX = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b ff ff ff ff ff ff ef 7f 03");
private static final byte[] OPTIONAL_DOUBLE_MIN = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 01 00 00 00 00 00 00 00 03");
private static final byte[] OPTIONAL_DOUBLE_ZERO = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 00 00 03");
private static final byte[] OPTIONAL_DOUBLE_NEGATIVE_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 80");
private static final byte[] OPTIONAL_DOUBLE_1_5 = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 f8 3f 03");
private static final byte[] OPTIONAL_DOUBLE_2_5 = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 1b 00 00 00 00 00 00 04 40 03");
private static final byte[] OPTIONAL_DOUBLE_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_3_14 = VPackWireFixtureTest.hex(
            "13 0c 1b 1f 85 eb 51 b8 1e 09 40 01");
private static final byte[] OPTIONAL_DOUBLE_NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] OPTIONAL_DOUBLE_POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final byte[] OPTIONAL_DOUBLE_NEGATIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 ff");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INTEGER_42 = VPackWireFixtureTest.hex("28 2a");

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntBoundaryValues().
    void testOptionalIntBoundaryValuesVpack() throws Exception {
        assertArrayEquals(OPTIONAL_INT_MAX,
                MAPPER.writeValueAsBytes(new OptionalIntBean(Integer.MAX_VALUE)));
        OptionalIntBean max = MAPPER.readValue(OPTIONAL_INT_MAX, OptionalIntBean.class);
        assertTrue(max.value.isPresent());
        assertEquals(Integer.MAX_VALUE, max.value.getAsInt());

        assertArrayEquals(OPTIONAL_INT_MIN,
                MAPPER.writeValueAsBytes(new OptionalIntBean(Integer.MIN_VALUE)));
        OptionalIntBean min = MAPPER.readValue(OPTIONAL_INT_MIN, OptionalIntBean.class);
        assertTrue(min.value.isPresent());
        assertEquals(Integer.MIN_VALUE, min.value.getAsInt());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleBoundaryValues().
    void testOptionalDoubleBoundaryValuesVpack() throws Exception {
        assertArrayEquals(OPTIONAL_DOUBLE_MAX,
                MAPPER.writeValueAsBytes(new OptionalDoubleBean(Double.MAX_VALUE)));
        OptionalDoubleBean max = MAPPER.readValue(OPTIONAL_DOUBLE_MAX, OptionalDoubleBean.class);
        assertTrue(max.value.isPresent());
        assertEquals(Double.MAX_VALUE, max.value.getAsDouble());

        assertArrayEquals(OPTIONAL_DOUBLE_MIN,
                MAPPER.writeValueAsBytes(new OptionalDoubleBean(Double.MIN_VALUE)));
        OptionalDoubleBean min = MAPPER.readValue(OPTIONAL_DOUBLE_MIN, OptionalDoubleBean.class);
        assertTrue(min.value.isPresent());
        assertEquals(Double.MIN_VALUE, min.value.getAsDouble());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleFromBooleanFails().
    void testOptionalDoubleFromBooleanFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalDouble.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleFromInteger().
    void testOptionalDoubleFromIntegerVpack() throws Exception {
        OptionalDouble result = MAPPER.readValue(INTEGER_42, OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(42.0, result.getAsDouble());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleFromObjectFails().
    void testOptionalDoubleFromObjectFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, OptionalDouble.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleNegativeZero().
    void testOptionalDoubleNegativeZeroVpack() throws Exception {
        OptionalDouble result = MAPPER.readValue(OPTIONAL_DOUBLE_NEGATIVE_ZERO,
                OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(Double.doubleToRawLongBits(-0.0),
                Double.doubleToRawLongBits(result.getAsDouble()));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleSerializeFilter().
    void testOptionalDoubleSerializeFilterVpack() throws Exception {
        ObjectMapper nonNull = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertArrayEquals(OPTIONAL_DOUBLE_1_5,
                nonNull.writeValueAsBytes(new OptionalDoubleBean(1.5)));
        assertArrayEquals(OPTIONAL_DOUBLE_NULL,
                nonNull.writeValueAsBytes(new OptionalDoubleBean()));

        ObjectMapper nonAbsent = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertArrayEquals(OPTIONAL_DOUBLE_2_5,
                nonAbsent.writeValueAsBytes(new OptionalDoubleBean(2.5)));
        assertArrayEquals(EMPTY_OBJECT,
                nonAbsent.writeValueAsBytes(new OptionalDoubleBean()));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleSpecialValuesRoundTrip().
    void testOptionalDoubleSpecialValuesRoundTripVpack() throws Exception {
        OptionalDouble nan = MAPPER.readValue(OPTIONAL_DOUBLE_NAN, OptionalDouble.class);
        assertTrue(nan.isPresent());
        assertTrue(Double.isNaN(nan.getAsDouble()));

        OptionalDouble posInf = MAPPER.readValue(OPTIONAL_DOUBLE_POSITIVE_INFINITY,
                OptionalDouble.class);
        assertTrue(posInf.isPresent());
        assertEquals(Double.POSITIVE_INFINITY, posInf.getAsDouble());

        OptionalDouble negInf = MAPPER.readValue(OPTIONAL_DOUBLE_NEGATIVE_INFINITY,
                OptionalDouble.class);
        assertTrue(negInf.isPresent());
        assertEquals(Double.NEGATIVE_INFINITY, negInf.getAsDouble());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleUnwrapSingleValueArrays().
    void testOptionalDoubleUnwrapSingleValueArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalDouble result = mapper.readValue(OPTIONAL_DOUBLE_ARRAY_3_14,
                OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(3.14, result.getAsDouble(), 0.001);
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleUnwrapSingleValueArraysDisabled().
    void testOptionalDoubleUnwrapSingleValueArraysDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(OPTIONAL_DOUBLE_ARRAY_3_14, OptionalDouble.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalDoubleZero().
    void testOptionalDoubleZeroVpack() throws Exception {
        assertArrayEquals(OPTIONAL_DOUBLE_ZERO,
                MAPPER.writeValueAsBytes(new OptionalDoubleBean(0.0)));
        OptionalDoubleBean result = MAPPER.readValue(OPTIONAL_DOUBLE_ZERO,
                OptionalDoubleBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(0.0, result.value.getAsDouble());
    }
static class OptionalIntBean {
        public OptionalInt value;

        public OptionalIntBean() {
            value = OptionalInt.empty();
        }

        OptionalIntBean(int value) {
            this.value = OptionalInt.of(value);
        }
    }
static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() {
            value = OptionalDouble.empty();
        }

        OptionalDoubleBean(double value) {
            this.value = OptionalDouble.of(value);
        }
    }

    void __invoke_testOptionalIntBoundaryValuesVpack() throws Exception {
        try {
            testOptionalIntBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleBoundaryValuesVpack() throws Exception {
        try {
            testOptionalDoubleBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleFromBooleanFailsVpack() throws Exception {
        try {
            testOptionalDoubleFromBooleanFailsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleFromIntegerVpack() throws Exception {
        try {
            testOptionalDoubleFromIntegerVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleFromObjectFailsVpack() throws Exception {
        try {
            testOptionalDoubleFromObjectFailsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleNegativeZeroVpack() throws Exception {
        try {
            testOptionalDoubleNegativeZeroVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleSerializeFilterVpack() throws Exception {
        try {
            testOptionalDoubleSerializeFilterVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleSpecialValuesRoundTripVpack() throws Exception {
        try {
            testOptionalDoubleSpecialValuesRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleUnwrapSingleValueArraysVpack() throws Exception {
        try {
            testOptionalDoubleUnwrapSingleValueArraysVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleUnwrapSingleValueArraysDisabledVpack() throws Exception {
        try {
            testOptionalDoubleUnwrapSingleValueArraysDisabledVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleZeroVpack() throws Exception {
        try {
            testOptionalDoubleZeroVpack();
        } finally {
        }
    }

}
