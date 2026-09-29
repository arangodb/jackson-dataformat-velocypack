package tools.jackson.databind.ext.jdk8;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0374Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY = VPackWireFixtureTest.hex("18");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] OPTIONAL_INT_FIVE = VPackWireFixtureTest.hex("35");
private static final byte[] OPTIONAL_INT_STRING_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] OPTIONAL_INT_BEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_INT_BEAN_STRING_NEGATIVE_37 = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 2d 33 37 03");
private static final byte[] OPTIONAL_INT_ARRAY_ABSENT = VPackWireFixtureTest.hex(
            "02 03 18");
private static final byte[] OPTIONAL_DOUBLE_MIN = VPackWireFixtureTest.hex(
            "1b 01 00 00 00 00 00 00 00");
private static final byte[] OPTIONAL_LONG_MAX = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 2f ff ff ff ff ff ff ff 7f 03");
private static final byte[] OPTIONAL_LONG_MIN = VPackWireFixtureTest.hex(
            "0b 13 01 45 76 61 6c 75 65 27 00 00 00 00 00 00 00 80 03");
private static final byte[] OPTIONAL_LONG_STRING_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] OPTIONAL_LONG_BEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_LONG_BEAN_STRING_19 = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 42 31 39 03");
private static final byte[] OPTIONAL_LONG_ARRAY_ABSENT = VPackWireFixtureTest.hex(
            "02 03 18");

    // Provenance: OptionalNumbersTest#testOptionalDoublePresent().
    void testOptionalDoublePresentVpack() throws Exception {
        assertArrayEquals(OPTIONAL_DOUBLE_MIN,
                MAPPER.writeValueAsBytes(OptionalDouble.of(Double.MIN_VALUE)));
        assertEquals(Double.MIN_VALUE,
                MAPPER.readValue(OPTIONAL_DOUBLE_MIN, OptionalDouble.class).getAsDouble());
    }

    // Provenance: OptionalNumbersTest#testOptionalIntAbsent().
    void testOptionalIntAbsentVpack() throws Exception {
        assertArrayEquals(EMPTY, MAPPER.writeValueAsBytes(OptionalInt.empty()));
        assertFalse(MAPPER.readValue(EMPTY, OptionalInt.class).isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalIntNull().
    void testOptionalIntNullVpack() throws Exception {
        assertFalse(MAPPER.readValue(EMPTY, OptionalInt.class).isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalIntCoerceFromString().
    void testOptionalIntCoerceFromStringVpack() throws Exception {
        OptionalInt opt = MAPPER.readValue(OPTIONAL_INT_STRING_123, OptionalInt.class);
        assertEquals(123, opt.getAsInt());

        opt = MAPPER.readValue(EMPTY_STRING, OptionalInt.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());

        OptionalIntBean bean = MAPPER.readValue(OPTIONAL_INT_BEAN_NULL,
                OptionalIntBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(OPTIONAL_INT_BEAN_STRING_NEGATIVE_37,
                OptionalIntBean.class);
        assertNotNull(bean.value);
        assertEquals(-37, bean.value.getAsInt());
    }

    // Provenance: OptionalNumbersTest#testOptionalIntInArrayAbsent().
    void testOptionalIntInArrayAbsentVpack() throws Exception {
        OptionalInt[] ints = MAPPER.readValue(OPTIONAL_INT_ARRAY_ABSENT,
                OptionalInt[].class);
        assertEquals(1, ints.length);
        assertNotNull(ints[0]);
        assertFalse(ints[0].isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalIntInvalid().
    void testOptionalIntInvalidVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalInt.class));
    }

    // Provenance: OptionalNumbersTest#testOptionalIntPresent().
    void testOptionalIntPresentVpack() throws Exception {
        assertArrayEquals(OPTIONAL_INT_FIVE,
                MAPPER.writeValueAsBytes(OptionalInt.of(5)));
        assertEquals(5, MAPPER.readValue(OPTIONAL_INT_FIVE, OptionalInt.class).getAsInt());
    }

    // Provenance: OptionalNumbersTest#testOptionalLongAbsent().
    void testOptionalLongAbsentVpack() throws Exception {
        assertArrayEquals(EMPTY, MAPPER.writeValueAsBytes(OptionalLong.empty()));
        assertFalse(MAPPER.readValue(EMPTY, OptionalLong.class).isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalLongBoundaryValues().
    void testOptionalLongBoundaryValuesVpack() throws Exception {
        assertArrayEquals(OPTIONAL_LONG_MAX,
                MAPPER.writeValueAsBytes(new OptionalLongBean(Long.MAX_VALUE)));
        OptionalLongBean max = MAPPER.readValue(OPTIONAL_LONG_MAX, OptionalLongBean.class);
        assertTrue(max.value.isPresent());
        assertEquals(Long.MAX_VALUE, max.value.getAsLong());

        assertArrayEquals(OPTIONAL_LONG_MIN,
                MAPPER.writeValueAsBytes(new OptionalLongBean(Long.MIN_VALUE)));
        OptionalLongBean min = MAPPER.readValue(OPTIONAL_LONG_MIN, OptionalLongBean.class);
        assertTrue(min.value.isPresent());
        assertEquals(Long.MIN_VALUE, min.value.getAsLong());
    }

    // Provenance: OptionalNumbersTest#testOptionalLongCoerceFromString().
    void testOptionalLongCoerceFromStringVpack() throws Exception {
        OptionalLong opt = MAPPER.readValue(OPTIONAL_LONG_STRING_123, OptionalLong.class);
        assertEquals(123L, opt.getAsLong());

        opt = MAPPER.readValue(EMPTY_STRING, OptionalLong.class);
        assertNotNull(opt);
        assertFalse(opt.isPresent());

        OptionalLongBean bean = MAPPER.readValue(OPTIONAL_LONG_BEAN_NULL,
                OptionalLongBean.class);
        assertNotNull(bean.value);
        assertFalse(bean.value.isPresent());

        bean = MAPPER.readValue(OPTIONAL_LONG_BEAN_STRING_19, OptionalLongBean.class);
        assertNotNull(bean.value);
        assertEquals(19L, bean.value.getAsLong());
    }

    // Provenance: OptionalNumbersTest#testOptionalLongInArrayAbsent().
    void testOptionalLongInArrayAbsentVpack() throws Exception {
        OptionalLong[] arr = MAPPER.readValue(OPTIONAL_LONG_ARRAY_ABSENT,
                OptionalLong[].class);
        assertEquals(1, arr.length);
        assertNotNull(arr[0]);
        assertFalse(arr[0].isPresent());
    }

    // Provenance: OptionalNumbersTest#testOptionalLongInvalid().
    void testOptionalLongInvalidVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalLong.class));
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
static class OptionalLongBean {
        public OptionalLong value;

        public OptionalLongBean() {
            value = OptionalLong.empty();
        }

        OptionalLongBean(long value) {
            this.value = OptionalLong.of(value);
        }
    }

    void __invoke_testOptionalDoublePresentVpack() throws Exception {
        try {
            testOptionalDoublePresentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntAbsentVpack() throws Exception {
        try {
            testOptionalIntAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntNullVpack() throws Exception {
        try {
            testOptionalIntNullVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntCoerceFromStringVpack() throws Exception {
        try {
            testOptionalIntCoerceFromStringVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntInArrayAbsentVpack() throws Exception {
        try {
            testOptionalIntInArrayAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntInvalidVpack() throws Exception {
        try {
            testOptionalIntInvalidVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntPresentVpack() throws Exception {
        try {
            testOptionalIntPresentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongAbsentVpack() throws Exception {
        try {
            testOptionalLongAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongBoundaryValuesVpack() throws Exception {
        try {
            testOptionalLongBoundaryValuesVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongCoerceFromStringVpack() throws Exception {
        try {
            testOptionalLongCoerceFromStringVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongInArrayAbsentVpack() throws Exception {
        try {
            testOptionalLongInArrayAbsentVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongInvalidVpack() throws Exception {
        try {
            testOptionalLongInvalidVpack();
        } finally {
        }
    }

}
