package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0323Fixture {
private static final byte[][] MONTH_NUMBER_STRINGS = {
            VPackWireFixtureTest.hex("41 30"),
            VPackWireFixtureTest.hex("41 31"),
            VPackWireFixtureTest.hex("41 32"),
            VPackWireFixtureTest.hex("41 33"),
            VPackWireFixtureTest.hex("41 34"),
            VPackWireFixtureTest.hex("41 35"),
            VPackWireFixtureTest.hex("41 36"),
            VPackWireFixtureTest.hex("41 37"),
            VPackWireFixtureTest.hex("41 38"),
            VPackWireFixtureTest.hex("41 39"),
            VPackWireFixtureTest.hex("42 31 30"),
            VPackWireFixtureTest.hex("42 31 31"),
            VPackWireFixtureTest.hex("42 31 32")
    };
private static final byte[][] MONTH_NAMES = {
            VPackWireFixtureTest.hex("47 4a 41 4e 55 41 52 59"),
            VPackWireFixtureTest.hex("48 46 45 42 52 55 41 52 59"),
            VPackWireFixtureTest.hex("45 4d 41 52 43 48"),
            VPackWireFixtureTest.hex("45 41 50 52 49 4c"),
            VPackWireFixtureTest.hex("43 4d 41 59"),
            VPackWireFixtureTest.hex("44 4a 55 4e 45"),
            VPackWireFixtureTest.hex("44 4a 55 4c 59"),
            VPackWireFixtureTest.hex("46 41 55 47 55 53 54"),
            VPackWireFixtureTest.hex("49 53 45 50 54 45 4d 42 45 52"),
            VPackWireFixtureTest.hex("47 4f 43 54 4f 42 45 52"),
            VPackWireFixtureTest.hex("48 4e 4f 56 45 4d 42 45 52"),
            VPackWireFixtureTest.hex("48 44 45 43 45 4d 42 45 52")
    };
private static final byte[][] MONTH_NUMBER_INTEGERS = {
            VPackWireFixtureTest.hex("30"),
            VPackWireFixtureTest.hex("31"),
            VPackWireFixtureTest.hex("32"),
            VPackWireFixtureTest.hex("33"),
            VPackWireFixtureTest.hex("34"),
            VPackWireFixtureTest.hex("35"),
            VPackWireFixtureTest.hex("36"),
            VPackWireFixtureTest.hex("37"),
            VPackWireFixtureTest.hex("38"),
            VPackWireFixtureTest.hex("39"),
            VPackWireFixtureTest.hex("28 0a"),
            VPackWireFixtureTest.hex("28 0b"),
            VPackWireFixtureTest.hex("28 0c")
    };
private static final byte[] ARRAY_JANUARY = VPackWireFixtureTest.hex(
            "13 0b 47 4a 41 4e 55 41 52 59 01");
private static final byte[] ARRAY_TRUE = VPackWireFixtureTest.hex(
            "13 04 19 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");

    
    // Provenance: MonthDeserializerTest#testDeserializationAsString01_oneBased(Month).
    void testDeserializationAsString01OneBasedVpack(Month expectedMonth) throws Exception {
        assertEquals(expectedMonth,
                readerForOneBased().readValue(MONTH_NUMBER_STRINGS[expectedMonth.getValue()]));
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsString01_zeroBased(Month).
    void testDeserializationAsString01ZeroBasedVpack(Month expectedMonth) throws Exception {
        assertEquals(expectedMonth,
                readerForZeroBased().readValue(MONTH_NUMBER_STRINGS[expectedMonth.ordinal()]));
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsString02_oneBased(Month).
    void testDeserializationAsString02OneBasedVpack(Month expectedMonth) throws Exception {
        assertEquals(expectedMonth,
                readerForOneBased().readValue(MONTH_NAMES[expectedMonth.ordinal()]));
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsInt_oneBased(Month).
    void testDeserializationAsIntOneBasedVpack(Month expectedMonth) throws Exception {
        assertEquals(expectedMonth,
                readerForOneBased().readValue(MONTH_NUMBER_INTEGERS[expectedMonth.getValue()]));
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsInt_zeroBased(Month).
    void testDeserializationAsIntZeroBasedVpack(Month expectedMonth) throws Exception {
        assertEquals(expectedMonth,
                readerForZeroBased().readValue(MONTH_NUMBER_INTEGERS[expectedMonth.ordinal()]));
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsIntOutOfRange_oneBased(int).
    void testDeserializationAsIntOutOfRangeOneBasedVpack(int invalidValue) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readerForOneBased().readValue(integerFixture(invalidValue)));
        assertTrue(failure.getMessage().contains("month number outside 1-12 range"),
                failure.getMessage());
    }

    
    // Provenance: MonthDeserializerTest#testDeserializationAsIntOutOfRange_zeroBased(int).
    void testDeserializationAsIntOutOfRangeZeroBasedVpack(int invalidValue) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readerForZeroBased().readValue(integerFixture(invalidValue)));
        assertTrue(failure.getMessage().contains("month number outside 0-11 range"),
                failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsEmptyArray().
    void testDeserializationAsEmptyArrayVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readerForOneBased().readValue(EMPTY_ARRAY));
        assertTrue(failure.getMessage().contains("Cannot deserialize"), failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsEmptyArray_withFeatureEnabled().
    void testDeserializationAsEmptyArrayWithFeatureEnabledVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .build()
                .readerFor(Month.class);
        assertNull(reader.readValue(EMPTY_ARRAY));
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithWrongToken().
    void testDeserializationAsArrayWithWrongTokenVpack() {
        assertArrayValueRejected(ARRAY_TRUE);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapDisabled().
    void testDeserializationAsArrayWithStringUnwrapDisabledVpack() {
        assertArrayValueRejected(ARRAY_JANUARY);
    }

    // Provenance: MonthDeserializerTest#testDeserializationAsArrayWithStringUnwrapEnabled().
    void testDeserializationAsArrayWithStringUnwrapEnabledVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .readerFor(Month.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(Month.JANUARY, reader.readValue(ARRAY_JANUARY));
    }
private static byte[] integerFixture(int value) {
        return switch (value) {
        case -1 -> VPackWireFixtureTest.hex("20 ff");
        case 0 -> MONTH_NUMBER_INTEGERS[0];
        case 12 -> MONTH_NUMBER_INTEGERS[12];
        case 13 -> VPackWireFixtureTest.hex("28 0d");
        case 100 -> VPackWireFixtureTest.hex("28 64");
        default -> throw new IllegalArgumentException("unexpected test value: " + value);
        };
    }
private static ObjectReader readerForZeroBased() {
        return new VPackMapper().readerFor(Month.class)
                .without(DateTimeFeature.ONE_BASED_MONTHS);
    }
private static ObjectReader readerForOneBased() {
        return new VPackMapper().readerFor(Month.class)
                .with(DateTimeFeature.ONE_BASED_MONTHS);
    }
private static void assertArrayValueRejected(byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readerFor(Month.class).readValue(input));
        assertTrue(failure.getMessage().contains("Array"), failure.getMessage());
    }

    void __invoke_testDeserializationAsString01OneBasedVpack(Month expectedMonth) throws Exception {
        try {
            testDeserializationAsString01OneBasedVpack(expectedMonth);
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01ZeroBasedVpack(Month expectedMonth) throws Exception {
        try {
            testDeserializationAsString01ZeroBasedVpack(expectedMonth);
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02OneBasedVpack(Month expectedMonth) throws Exception {
        try {
            testDeserializationAsString02OneBasedVpack(expectedMonth);
        } finally {
        }
    }


    void __invoke_testDeserializationAsIntOneBasedVpack(Month expectedMonth) throws Exception {
        try {
            testDeserializationAsIntOneBasedVpack(expectedMonth);
        } finally {
        }
    }


    void __invoke_testDeserializationAsIntZeroBasedVpack(Month expectedMonth) throws Exception {
        try {
            testDeserializationAsIntZeroBasedVpack(expectedMonth);
        } finally {
        }
    }


    void __invoke_testDeserializationAsIntOutOfRangeOneBasedVpack(int invalidValue) throws Exception {
        try {
            testDeserializationAsIntOutOfRangeOneBasedVpack(invalidValue);
        } finally {
        }
    }


    void __invoke_testDeserializationAsIntOutOfRangeZeroBasedVpack(int invalidValue) throws Exception {
        try {
            testDeserializationAsIntOutOfRangeZeroBasedVpack(invalidValue);
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayWithFeatureEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayWithFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithWrongTokenVpack() throws Exception {
        try {
            testDeserializationAsArrayWithWrongTokenVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithStringUnwrapDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithStringUnwrapDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayWithStringUnwrapEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayWithStringUnwrapEnabledVpack();
        } finally {
        }
    }

}
