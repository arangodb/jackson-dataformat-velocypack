package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;
import java.time.temporal.TemporalAccessor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0324Fixture {
private static final byte[] TYPE_INFO_ONE_BASED = VPackWireFixtureTest.hex(
            "13 15 4f 6a 61 76 61 2e 74 69 6d 65 2e 4d 6f 6e 74 68 28 0b 02");
private static final byte[] TYPE_INFO_ZERO_BASED = VPackWireFixtureTest.hex(
            "13 16 4f 6a 61 76 61 2e 74 69 6d 65 2e 4d 6f 6e 74 68 42 31 31 02");
private static final byte[] MONTH_NAME_WITH_WHITESPACE = VPackWireFixtureTest.hex(
            "49 20 4a 41 4e 55 41 52 59 20");
private static final byte[] MONTH_NUMBER_WITH_WHITESPACE = VPackWireFixtureTest.hex(
            "43 20 36 20");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");
private static final byte[] FLOAT_ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] CUSTOM_FORMAT_JANUARY = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 43 4a 61 6e 01");
private static final byte[] CUSTOM_FORMAT_MARCH = VPackWireFixtureTest.hex(
            "14 0d 45 76 61 6c 75 65 43 4d 61 72 01");
private static final byte[] CUSTOM_FORMAT_INVALID = VPackWireFixtureTest.hex(
            "14 13 45 76 61 6c 75 65 49 4e 6f 74 41 4d 6f 6e 74 68 01");
private static final byte[] FULL_MONTH_FORMAT = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 47 4a 61 6e 75 61 72 79 01");

    // Provenance: MonthDeserializerTest#testDeserializationWithTypeInfo01_oneBased.
    void testDeserializationWithTypeInfo01OneBasedVpack() throws Exception {
        ObjectMapper mapper = typeInfoMapper(true);
        TemporalAccessor value = mapper.readValue(TYPE_INFO_ONE_BASED, TemporalAccessor.class);
        assertInstanceOf(Month.class, value);
        assertEquals(Month.NOVEMBER, value);
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithTypeInfo01_zeroBased.
    void testDeserializationWithTypeInfo01ZeroBasedVpack() throws Exception {
        ObjectMapper mapper = typeInfoMapper(false);
        TemporalAccessor value = mapper.readValue(TYPE_INFO_ZERO_BASED, TemporalAccessor.class);
        assertInstanceOf(Month.class, value);
        assertEquals(Month.DECEMBER, value);
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithWhitespace.
    void testDeserializationWithWhitespaceVpack() throws Exception {
        assertEquals(Month.JANUARY,
                VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(MONTH_NAME_WITH_WHITESPACE, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithWhitespaceNumeric.
    void testDeserializationWithWhitespaceNumericVpack() throws Exception {
        assertEquals(Month.JUNE,
                VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(MONTH_NUMBER_WITH_WHITESPACE, Month.class));
    }

    // Provenance: MonthDeserializerTest#testDeserializationFromBoolean.
    void testDeserializationFromBooleanVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(TRUE, Month.class));
        assertTrue(failure.getMessage().contains("Unexpected token (VALUE_TRUE)"),
                failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationFromFloat.
    void testDeserializationFromFloatVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(FLOAT_ONE_POINT_FIVE, Month.class));
        assertTrue(failure.getMessage().contains("Unexpected token (VALUE_NUMBER_FLOAT)"),
                failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationFromObject.
    void testDeserializationFromObjectVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(EMPTY_OBJECT, Month.class));
        assertTrue(failure.getMessage().contains("Unexpected token (START_OBJECT)"),
                failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormat.
    void testDeserializationWithCustomFormatVpack() throws Exception {
        assertEquals(Month.JANUARY,
                new VPackMapper().readValue(CUSTOM_FORMAT_JANUARY, CustomFormatWrapper.class).value);
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormatMarch.
    void testDeserializationWithCustomFormatMarchVpack() throws Exception {
        assertEquals(Month.MARCH,
                new VPackMapper().readValue(CUSTOM_FORMAT_MARCH, CustomFormatWrapper.class).value);
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithCustomFormatInvalid.
    void testDeserializationWithCustomFormatInvalidVpack() {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(CUSTOM_FORMAT_INVALID, CustomFormatWrapper.class));
        assertTrue(failure.getMessage().contains("could not be parsed"), failure.getMessage());
    }

    // Provenance: MonthDeserializerTest#testDeserializationWithFullMonthFormat.
    void testDeserializationWithFullMonthFormatVpack() throws Exception {
        assertEquals(Month.JANUARY,
                new VPackMapper().readValue(FULL_MONTH_FORMAT, FullMonthFormatWrapper.class).value);
    }
private static ObjectMapper typeInfoMapper(boolean oneBased) {
        return VPackMapper.builder()
                .addMixIn(TemporalAccessor.class, TemporalTypeInfo.class)
                .configure(DateTimeFeature.ONE_BASED_MONTHS, oneBased)
                .build();
    }
static class CustomFormatWrapper {
        @JsonFormat(pattern = "MMM", locale = "en")
        public Month value;
    }
static class FullMonthFormatWrapper {
        @JsonFormat(pattern = "MMMM", locale = "en")
        public Month value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testDeserializationWithTypeInfo01OneBasedVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01OneBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01ZeroBasedVpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01ZeroBasedVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithWhitespaceVpack() throws Exception {
        try {
            testDeserializationWithWhitespaceVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithWhitespaceNumericVpack() throws Exception {
        try {
            testDeserializationWithWhitespaceNumericVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromBooleanVpack() throws Exception {
        try {
            testDeserializationFromBooleanVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromFloatVpack() throws Exception {
        try {
            testDeserializationFromFloatVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationFromObjectVpack() throws Exception {
        try {
            testDeserializationFromObjectVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithCustomFormatVpack() throws Exception {
        try {
            testDeserializationWithCustomFormatVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithCustomFormatMarchVpack() throws Exception {
        try {
            testDeserializationWithCustomFormatMarchVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithCustomFormatInvalidVpack() throws Exception {
        try {
            testDeserializationWithCustomFormatInvalidVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithFullMonthFormatVpack() throws Exception {
        try {
            testDeserializationWithFullMonthFormatVpack();
        } finally {
        }
    }

}
