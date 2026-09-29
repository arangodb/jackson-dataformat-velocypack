package tools.jackson.databind.ext.jdk8;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import com.fasterxml.jackson.annotation.JsonMerge;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0373F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper MAPPER_WITHOUT_COERCION = MAPPER.rebuild()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();
private static final byte[] MERGE_A = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 61 01 01");
private static final byte[] MERGE_B = VPackWireFixtureTest.hex(
            "14 0d 44 6c 69 73 74 13 05 41 62 01 01");
private static final byte[] OPTIONAL_DOUBLE_EMPTY = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_DOUBLE_NULL = VPackWireFixtureTest.hex("18");
private static final byte[] OPTIONAL_DOUBLE_STRING_025 = VPackWireFixtureTest.hex(
            "44 30 2e 32 35");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] OPTIONAL_DOUBLE_BEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_DOUBLE_BEAN_STRING_05 = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 30 2e 35 03");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_ABSENT = VPackWireFixtureTest.hex(
            "02 03 18");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_SPECIAL_VALUES = VPackWireFixtureTest.hex(
            "13 22 18 "
          + "1b 00 00 00 00 00 00 f8 7f "
          + "1b 00 00 00 00 00 00 f0 7f "
          + "1b 00 00 00 00 00 00 f0 ff "
          + "31 41 32 06");
private static final byte[] OPTIONAL_DOUBLE_ARRAY_SPECIAL_STRINGS = VPackWireFixtureTest.hex(
            "13 1c 18 "
          + "43 4e 61 4e "
          + "48 49 6e 66 69 6e 69 74 79 "
          + "49 2d 49 6e 66 69 6e 69 74 79 "
          + "31 05");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");

    // Provenance: OptionalNumbersTest#testOptionalDoubleAbsent().
    void testOptionalDoubleAbsentVpack() throws Exception {
        assertArrayEquals(OPTIONAL_DOUBLE_EMPTY,
                MAPPER.writeValueAsBytes(OptionalDouble.empty()));
        assertFalse(MAPPER.readValue(OPTIONAL_DOUBLE_EMPTY, OptionalDouble.class).isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleBeanSpecialValuesWithoutCoercion_nan().
    void testOptionalDoubleBeanSpecialValuesWithoutCoercionNanVpack() throws Exception {
        byte[] input = VPackWireFixtureTest.hex(
                "0b 0e 01 45 76 61 6c 75 65 "
              + "43 4e 61 4e 03");
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(input,
                OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.NaN), bean.value);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleBeanSpecialValuesWithoutCoercion_negativeInfinity().
    void testOptionalDoubleBeanSpecialValuesWithoutCoercionNegativeInfinityVpack()
        throws Exception {
        byte[] input = VPackWireFixtureTest.hex(
                "0b 14 01 45 76 61 6c 75 65 "
              + "49 2d 49 6e 66 69 6e 69 74 79 03");
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(input,
                OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.NEGATIVE_INFINITY), bean.value);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleBeanSpecialValuesWithoutCoercion_null().
    void testOptionalDoubleBeanSpecialValuesWithoutCoercionNullVpack() throws Exception {
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(
                OPTIONAL_DOUBLE_BEAN_NULL, OptionalDoubleBean.class);
        assertEquals(OptionalDouble.empty(), bean.value);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleBeanSpecialValuesWithoutCoercion_positiveInfinity().
    void testOptionalDoubleBeanSpecialValuesWithoutCoercionPositiveInfinityVpack()
        throws Exception {
        byte[] input = VPackWireFixtureTest.hex(
                "0b 13 01 45 76 61 6c 75 65 "
              + "48 49 6e 66 69 6e 69 74 79 03");
        OptionalDoubleBean bean = MAPPER_WITHOUT_COERCION.readValue(input,
                OptionalDoubleBean.class);
        assertEquals(OptionalDouble.of(Double.POSITIVE_INFINITY), bean.value);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleCoerceFromString().
    void testOptionalDoubleCoerceFromStringVpack() throws Exception {
        OptionalDouble opt = MAPPER.readValue(OPTIONAL_DOUBLE_STRING_025, OptionalDouble.class);
        assertEquals(0.25, opt.getAsDouble());

        opt = MAPPER.readValue(EMPTY_STRING, OptionalDouble.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());

        OptionalDoubleBean bean = MAPPER.readValue(OPTIONAL_DOUBLE_BEAN_NULL,
                OptionalDoubleBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(OPTIONAL_DOUBLE_BEAN_STRING_05, OptionalDoubleBean.class);
        assertNotNull(bean.value);
        assertEquals(0.5, bean.value.getAsDouble());
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleInArrayAbsent().
    void testOptionalDoubleInArrayAbsentVpack() throws Exception {
        OptionalDouble[] arr = MAPPER.readValue(OPTIONAL_DOUBLE_ARRAY_ABSENT,
                OptionalDouble[].class);
        assertEquals(1, arr.length);
        assertNotNull(arr[0]);
        assertFalse(arr[0].isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleInArraySpecialValues().
    void testOptionalDoubleInArraySpecialValuesVpack() throws Exception {
        OptionalDouble[] actual = MAPPER.readValue(OPTIONAL_DOUBLE_ARRAY_SPECIAL_VALUES,
                OptionalDouble[].class);
        OptionalDouble[] expected = new OptionalDouble[] {
                OptionalDouble.empty(),
                OptionalDouble.of(Double.NaN),
                OptionalDouble.of(Double.POSITIVE_INFINITY),
                OptionalDouble.of(Double.NEGATIVE_INFINITY),
                OptionalDouble.of(1D),
                OptionalDouble.of(2D)
        };
        assertArrayEquals(expected, actual);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleInArraySpecialValuesWithoutCoercion().
    void testOptionalDoubleInArraySpecialValuesWithoutCoercionVpack() throws Exception {
        OptionalDouble[] actual = MAPPER_WITHOUT_COERCION.readValue(
                OPTIONAL_DOUBLE_ARRAY_SPECIAL_STRINGS, OptionalDouble[].class);
        OptionalDouble[] expected = new OptionalDouble[] {
                OptionalDouble.empty(),
                OptionalDouble.of(Double.NaN),
                OptionalDouble.of(Double.POSITIVE_INFINITY),
                OptionalDouble.of(Double.NEGATIVE_INFINITY),
                OptionalDouble.of(1D)
        };
        assertArrayEquals(expected, actual);
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleInvalid().
    void testOptionalDoubleInvalidVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalDouble.class));
    }

    // Provenance: OptionalNumbersTest#testOptionalDoubleNull().
    void testOptionalDoubleNullVpack() throws Exception {
        assertFalse(MAPPER.readValue(OPTIONAL_DOUBLE_NULL, OptionalDouble.class).isPresent());
    }
static class OptionalListWrapper {
        @JsonMerge
        public Optional<List<String>> list = Optional.empty();
    }
static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() {
            value = OptionalDouble.empty();
        }
    }

    void __invoke_testOptionalDoubleAbsentVpack() throws Exception {
        try {
            testOptionalDoubleAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleBeanSpecialValuesWithoutCoercionNanVpack() throws Exception {
        try {
            testOptionalDoubleBeanSpecialValuesWithoutCoercionNanVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleBeanSpecialValuesWithoutCoercionNegativeInfinityVpack() throws Exception {
        try {
            testOptionalDoubleBeanSpecialValuesWithoutCoercionNegativeInfinityVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleBeanSpecialValuesWithoutCoercionNullVpack() throws Exception {
        try {
            testOptionalDoubleBeanSpecialValuesWithoutCoercionNullVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleBeanSpecialValuesWithoutCoercionPositiveInfinityVpack() throws Exception {
        try {
            testOptionalDoubleBeanSpecialValuesWithoutCoercionPositiveInfinityVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleCoerceFromStringVpack() throws Exception {
        try {
            testOptionalDoubleCoerceFromStringVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleInArrayAbsentVpack() throws Exception {
        try {
            testOptionalDoubleInArrayAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleInArraySpecialValuesVpack() throws Exception {
        try {
            testOptionalDoubleInArraySpecialValuesVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleInArraySpecialValuesWithoutCoercionVpack() throws Exception {
        try {
            testOptionalDoubleInArraySpecialValuesWithoutCoercionVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleInvalidVpack() throws Exception {
        try {
            testOptionalDoubleInvalidVpack();
        } finally {
        }
    }


    void __invoke_testOptionalDoubleNullVpack() throws Exception {
        try {
            testOptionalDoubleNullVpack();
        } finally {
        }
    }

}
