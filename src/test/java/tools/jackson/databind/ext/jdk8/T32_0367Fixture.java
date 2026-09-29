package tools.jackson.databind.ext.jdk8;

import java.util.OptionalInt;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0367Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] OPTIONAL_INT_ZERO = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 30 03");
private static final byte[] OPTIONAL_INT_123 = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 7b 03");
private static final byte[] OPTIONAL_INT_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] OPTIONAL_INT_456 = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 29 c8 01 03");
private static final byte[] OPTIONAL_INT_ARRAY_42 = VPackWireFixtureTest.hex(
            "02 04 28 2a");
private static final byte[] OPTIONAL_INT_FLOAT_2 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 00 40");
private static final byte[] OPTIONAL_LONG_ARRAY_99 = VPackWireFixtureTest.hex(
            "02 04 28 63");
private static final byte[] OPTIONAL_LONG_FLOAT_3 = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 08 40");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntZero().
    void testOptionalIntZeroVpack() throws Exception {
        assertArrayEquals(OPTIONAL_INT_ZERO,
                MAPPER.writeValueAsBytes(new OptionalIntBean(0)));
        OptionalIntBean result = MAPPER.readValue(OPTIONAL_INT_ZERO,
                OptionalIntBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(0, result.value.getAsInt());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntSerializeFilter().
    void testOptionalIntSerializeFilterVpack() throws Exception {
        ObjectMapper nonNull = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertArrayEquals(OPTIONAL_INT_123,
                nonNull.writeValueAsBytes(new OptionalIntBean(123)));
        assertArrayEquals(OPTIONAL_INT_NULL,
                nonNull.writeValueAsBytes(new OptionalIntBean()));

        ObjectMapper nonAbsent = VPackMapper.builder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertArrayEquals(OPTIONAL_INT_456,
                nonAbsent.writeValueAsBytes(new OptionalIntBean(456)));
        assertArrayEquals(EMPTY_OBJECT,
                nonAbsent.writeValueAsBytes(new OptionalIntBean()));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntUnwrapSingleValueArrays().
    void testOptionalIntUnwrapSingleValueArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalInt result = mapper.readValue(OPTIONAL_INT_ARRAY_42, OptionalInt.class);
        assertTrue(result.isPresent());
        assertEquals(42, result.getAsInt());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntUnwrapSingleValueArraysDisabled().
    void testOptionalIntUnwrapSingleValueArraysDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(OPTIONAL_INT_ARRAY_42, OptionalInt.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntFromFloat().
    void testOptionalIntFromFloatVpack() throws Exception {
        OptionalInt result = MAPPER.readValue(OPTIONAL_INT_FLOAT_2, OptionalInt.class);
        assertTrue(result.isPresent());
        assertEquals(2, result.getAsInt());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalLongUnwrapSingleValueArrays().
    void testOptionalLongUnwrapSingleValueArraysVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalLong result = mapper.readValue(OPTIONAL_LONG_ARRAY_99, OptionalLong.class);
        assertTrue(result.isPresent());
        assertEquals(99L, result.getAsLong());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalLongUnwrapSingleValueArraysDisabled().
    void testOptionalLongUnwrapSingleValueArraysDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(OPTIONAL_LONG_ARRAY_99, OptionalLong.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalLongFromFloat().
    void testOptionalLongFromFloatVpack() throws Exception {
        OptionalLong result = MAPPER.readValue(OPTIONAL_LONG_FLOAT_3, OptionalLong.class);
        assertTrue(result.isPresent());
        assertEquals(3L, result.getAsLong());
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntFromBooleanFails().
    void testOptionalIntFromBooleanFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalInt.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalLongFromBooleanFails().
    void testOptionalLongFromBooleanFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(TRUE, OptionalLong.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalIntFromObjectFails().
    void testOptionalIntFromObjectFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, OptionalInt.class));
    }

    // Provenance: AdditionalOptionalNumbersTest#testOptionalLongFromObjectFails().
    void testOptionalLongFromObjectFailsVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(EMPTY_OBJECT, OptionalLong.class));
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

    void __invoke_testOptionalIntZeroVpack() throws Exception {
        try {
            testOptionalIntZeroVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntSerializeFilterVpack() throws Exception {
        try {
            testOptionalIntSerializeFilterVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntUnwrapSingleValueArraysVpack() throws Exception {
        try {
            testOptionalIntUnwrapSingleValueArraysVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntUnwrapSingleValueArraysDisabledVpack() throws Exception {
        try {
            testOptionalIntUnwrapSingleValueArraysDisabledVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntFromFloatVpack() throws Exception {
        try {
            testOptionalIntFromFloatVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongUnwrapSingleValueArraysVpack() throws Exception {
        try {
            testOptionalLongUnwrapSingleValueArraysVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongUnwrapSingleValueArraysDisabledVpack() throws Exception {
        try {
            testOptionalLongUnwrapSingleValueArraysDisabledVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongFromFloatVpack() throws Exception {
        try {
            testOptionalLongFromFloatVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntFromBooleanFailsVpack() throws Exception {
        try {
            testOptionalIntFromBooleanFailsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongFromBooleanFailsVpack() throws Exception {
        try {
            testOptionalLongFromBooleanFailsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalIntFromObjectFailsVpack() throws Exception {
        try {
            testOptionalIntFromObjectFailsVpack();
        } finally {
        }
    }


    void __invoke_testOptionalLongFromObjectFailsVpack() throws Exception {
        try {
            testOptionalLongFromObjectFailsVpack();
        } finally {
        }
    }

}
