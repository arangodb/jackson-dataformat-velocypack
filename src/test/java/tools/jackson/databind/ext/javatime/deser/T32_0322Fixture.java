package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0322Fixture {
private static final byte[] INTEGER_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] STRING_EIGHT = VPackWireFixtureTest.hex("41 38");
private static final byte[] ARRAY_INTEGER_THREE = VPackWireFixtureTest.hex(
            "13 04 33 01");
private static final byte[] ARRAY_INTEGER_ONE_TWO = VPackWireFixtureTest.hex(
            "13 05 31 32 02");
private static final byte[] ARRAY_FLOAT_ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 f8 3f 01");
private static final byte[] ARRAY_EMPTY_OBJECT = VPackWireFixtureTest.hex(
            "13 04 0a 01");
private static final byte[] ARRAY_NUMERIC_STRING_FIVE = VPackWireFixtureTest.hex(
            "13 05 41 35 01");
private static final byte[] ARRAY_TWO_MONTH_STRINGS = VPackWireFixtureTest.hex(
            "13 14 47 4a 41 4e 55 41 52 59 48 46 45 42 52 55 41 52 59 02");

    // Provenance: MonthDeserializerTest#testDeserialization01_oneBased.
    void testDeserialization01OneBasedVpack() throws Exception {
        assertEquals(Month.JANUARY,
                VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(INTEGER_ONE, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserialization01_zeroBased.
    void testDeserialization01ZeroBasedVpack() throws Exception {
        assertEquals(Month.FEBRUARY,
                VPackMapper.builder().disable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(INTEGER_ONE, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserialization02_oneBased.
    void testDeserialization02OneBasedVpack() throws Exception {
        assertEquals(Month.AUGUST,
                VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(STRING_EIGHT, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserialization02_zeroBased.
    void testDeserialization02ZeroBasedVpack() throws Exception {
        assertEquals(Month.SEPTEMBER,
                VPackMapper.builder().disable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(STRING_EIGHT, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithFloatUnwrapDisabled.
    void testDeserializationAsArrayWithFloatUnwrapDisabledVpack() {
        assertArrayValueRejected(ARRAY_FLOAT_ONE_POINT_FIVE);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue.
    void testDeserializationAsArrayWithIntValueVpack() {
        assertArrayValueRejected(ARRAY_INTEGER_THREE);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue_withFeatureEnabled.
    void testDeserializationAsArrayWithIntValueWithFeatureEnabledVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .readerFor(Month.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(Month.MARCH, reader.readValue(ARRAY_INTEGER_THREE));
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithIntValue_zeroBased.
    void testDeserializationAsArrayWithIntValueZeroBasedVpack() {
        assertArrayValueRejected(ARRAY_INTEGER_THREE);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneElement.
    void testDeserializationAsArrayWithMoreThanOneElementVpack() {
        assertArrayValueRejected(ARRAY_INTEGER_ONE_TWO);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithMoreThanOneString.
    void testDeserializationAsArrayWithMoreThanOneStringVpack() {
        ObjectReader reader = new VPackMapper().readerFor(Month.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(ARRAY_TWO_MONTH_STRINGS));
        assertTrue(failure.getMessage().contains("unwrap"), failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithNumericStringUnwrapEnabled.
    void testDeserializationAsArrayWithNumericStringUnwrapEnabledVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .readerFor(Month.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(Month.MAY, reader.readValue(ARRAY_NUMERIC_STRING_FIVE));
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithObjectUnwrapDisabled.
    void testDeserializationAsArrayWithObjectUnwrapDisabledVpack() {
        assertArrayValueRejected(ARRAY_EMPTY_OBJECT);
    }
private static void assertArrayValueRejected(byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readerFor(Month.class).readValue(input));
        assertTrue(failure.getMessage().contains("Array"), failure.getMessage());
    }

    void __invoke_testDeserialization01OneBasedVpack() throws Exception {
        try {
            testDeserialization01OneBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserialization01ZeroBasedVpack() throws Exception {
        try {
            testDeserialization01ZeroBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserialization02OneBasedVpack() throws Exception {
        try {
            testDeserialization02OneBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserialization02ZeroBasedVpack() throws Exception {
        try {
            testDeserialization02ZeroBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithFloatUnwrapDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithFloatUnwrapDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithIntValueVpack() throws Exception {
        try {
            testDeserializationAsArrayWithIntValueVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithIntValueWithFeatureEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithIntValueWithFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithIntValueZeroBasedVpack() throws Exception {
        try {
            testDeserializationAsArrayWithIntValueZeroBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithMoreThanOneElementVpack() throws Exception {
        try {
            testDeserializationAsArrayWithMoreThanOneElementVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithMoreThanOneStringVpack() throws Exception {
        try {
            testDeserializationAsArrayWithMoreThanOneStringVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithNumericStringUnwrapEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithNumericStringUnwrapEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithObjectUnwrapDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithObjectUnwrapDisabledVpack();
        } finally {
        }
    }

}
