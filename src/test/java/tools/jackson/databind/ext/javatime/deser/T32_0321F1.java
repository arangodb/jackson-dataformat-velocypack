package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;
import java.time.MonthDay;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0321F1 {
private static final byte[] MONTH_DAY_AUGUST_21 = VPackWireFixtureTest.hex(
            "47 2d 2d 30 38 2d 32 31");
private static final byte[] MONTH_DAY_JANUARY_01 = VPackWireFixtureTest.hex(
            "47 2d 2d 30 31 2d 30 31");
private static final byte[] MONTH_DAY_JANUARY_01_ARRAY = VPackWireFixtureTest.hex(
            "13 0b 47 2d 2d 30 31 2d 30 31 01");
private static final byte[] MONTH_DAY_NOVEMBER_05 = VPackWireFixtureTest.hex(
            "13 1e 52 6a 61 76 61 2e 74 69 6d 65 2e 4d 6f 6e 74 68 44 61 79 "
          + "47 2d 2d 31 31 2d 30 35 02");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] MONTH_DAY_MAP_NULL = VPackWireFixtureTest.hex(
            "14 0d 48 6d 6f 6e 74 68 44 61 79 18 01");
private static final byte[] MONTH_DAY_MAP_EMPTY = VPackWireFixtureTest.hex(
            "14 0d 48 6d 6f 6e 74 68 44 61 79 40 01");
private static final byte[] MONTH_DAY_FORMAT_STRING = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 31 32 2f 32 38 01");
private static final byte[] MONTH_DAY_FORMAT_ARRAY = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 13 07 28 0c 28 1c 02 01");
private static final byte[] MONTH_DAY_FORMAT_STRING_OUTPUT = VPackWireFixtureTest.hex(
            "0b 10 01 45 76 61 6c 75 65 45 31 32 2f 32 38 03");
private static final byte[] MONTH_DAY_FORMAT_ARRAY_OUTPUT = VPackWireFixtureTest.hex(
            "0b 10 01 45 76 61 6c 75 65 02 06 28 0c 28 1c 03");
private static final byte[] NOT_A_MONTH = VPackWireFixtureTest.hex(
            "49 6e 6f 74 61 6d 6f 6e 74 68");
private static final byte[] TRUNCATED_MONTH_NAME = VPackWireFixtureTest.hex(
            "46 4a 41 4e 55 41 52");
private static final byte[] LOWER_CASE_MONTH = VPackWireFixtureTest.hex(
            "45 6d 61 72 63 68");
private static final byte[] ZERO_MONTH = VPackWireFixtureTest.hex("41 30");
private static final byte[] THIRTEEN_MONTH = VPackWireFixtureTest.hex("42 31 33");
private static final byte[] ZERO_BASED_12 = VPackWireFixtureTest.hex("42 31 32");
private static final byte[] ZERO_BASED_MINUS_1 = VPackWireFixtureTest.hex("42 2d 31");
private static final byte[] ZERO_BASED_100 = VPackWireFixtureTest.hex("43 31 30 30");
private static final TypeReference<Map<String, MonthDay>> MONTH_DAY_MAP_TYPE =
            new TypeReference<Map<String, MonthDay>>() { };

    
    // Provenance: MonthDeserializerTest#testBadDeserializationAsString01_oneBased(String,String).
    void testBadDeserializationAsString01OneBasedVpack(byte[] input, String expectedMessage) {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> VPackMapper.builder().enable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(input, Month.class));
        assertTrue(failure.getMessage().contains(expectedMessage), failure.getMessage());
    }

    
    // Provenance: MonthDeserializerTest#testBadDeserializationAsString_zeroBasedOutOfRange(String,String).
    void testBadDeserializationAsStringZeroBasedOutOfRangeVpack(byte[] input) {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> VPackMapper.builder().disable(DateTimeFeature.ONE_BASED_MONTHS).build()
                        .readValue(input, Month.class));
        assertTrue(failure.getMessage().contains("month number outside 0-11"),
                failure.getMessage());
    }
private static Stream<Arguments> oneBasedBadMonthValues() {
        return Stream.of(
                Arguments.of(NOT_A_MONTH, "not one of known `Month` values:"),
                Arguments.of(TRUNCATED_MONTH_NAME, "not one of known `Month` values:"),
                Arguments.of(LOWER_CASE_MONTH, "not one of known `Month` values:"),
                Arguments.of(ZERO_MONTH, "month number outside 1-12"),
                Arguments.of(THIRTEEN_MONTH, "month number outside 1-12"));
    }
private static Stream<Arguments> zeroBasedBadMonthValues() {
        return Stream.of(
                Arguments.of(ZERO_BASED_12),
                Arguments.of(ZERO_BASED_MINUS_1),
                Arguments.of(ZERO_BASED_100));
    }
static final class StringWrapper {
        @JsonFormat(pattern = "MM/dd")
        public MonthDay value;

        public StringWrapper() { }

        StringWrapper(MonthDay value) { this.value = value; }
    }
static final class ArrayWrapper {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public MonthDay value;

        public ArrayWrapper() { }

        ArrayWrapper(MonthDay value) { this.value = value; }
    }
@com.fasterxml.jackson.annotation.JsonTypeInfo(
            use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TypeInfo { }

    void __invoke_testBadDeserializationAsString01OneBasedVpack(byte[] input, String expectedMessage) throws Exception {
        try {
            testBadDeserializationAsString01OneBasedVpack(input, expectedMessage);
        } finally {
        }
    }


    void __invoke_testBadDeserializationAsStringZeroBasedOutOfRangeVpack(byte[] input) throws Exception {
        try {
            testBadDeserializationAsStringZeroBasedOutOfRangeVpack(input);
        } finally {
        }
    }

}
