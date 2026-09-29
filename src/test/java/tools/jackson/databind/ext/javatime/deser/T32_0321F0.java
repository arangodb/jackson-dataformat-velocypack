package tools.jackson.databind.ext.javatime.deser;

import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0321F0 {
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

    // Provenance: MonthDayDeserTest#testDeserialization02.
    void testDeserialization02Vpack() throws Exception {
        assertEquals(MonthDay.of(Month.AUGUST, 21),
                new VPackMapper().readValue(MONTH_DAY_AUGUST_21, MonthDay.class));
    }

    // Provenance: MonthDayDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        assertEquals(MonthDay.of(Month.JANUARY, 1),
                new VPackMapper().readValue(MONTH_DAY_JANUARY_01, MonthDay.class));
    }

    // Provenance: MonthDayDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(MONTH_DAY_JANUARY_01_ARRAY, MonthDay.class));
        assertTrue(failure.getMessage().contains("Unexpected token"), failure.getMessage());
    }

    // Provenance: MonthDayDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MonthDay.class);
        assertNull(reader.readValue(EMPTY_ARRAY));
        assertNull(reader.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(EMPTY_ARRAY));
    }

    // Provenance: MonthDayDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MonthDay.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(MonthDay.of(Month.JANUARY, 1),
                reader.readValue(MONTH_DAY_JANUARY_01_ARRAY));
    }

    // Provenance: MonthDayDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MonthDay.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        assertNull(reader.readValue(EMPTY_ARRAY));
    }

    // Provenance: MonthDayDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(TemporalAccessor.class, TypeInfo.class)
                .build();
        TemporalAccessor value = mapper.readValue(MONTH_DAY_NOVEMBER_05, TemporalAccessor.class);
        assertEquals(MonthDay.of(Month.NOVEMBER, 5), value);
    }

    // Provenance: MonthDayDeserTest#testFormatAnnotation.
    void testFormatAnnotationVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        StringWrapper input = new StringWrapper(MonthDay.of(12, 28));
        assertArrayEquals(MONTH_DAY_FORMAT_STRING_OUTPUT, mapper.writeValueAsBytes(input));
        StringWrapper output = mapper.readValue(MONTH_DAY_FORMAT_STRING, StringWrapper.class);
        assertEquals(input.value, output.value);
    }

    // Provenance: MonthDayDeserTest#testFormatAnnotationArray.
    void testFormatAnnotationArrayVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ArrayWrapper input = new ArrayWrapper(MonthDay.of(12, 28));
        assertArrayEquals(MONTH_DAY_FORMAT_ARRAY_OUTPUT, mapper.writeValueAsBytes(input));
        ArrayWrapper output = mapper.readValue(MONTH_DAY_FORMAT_ARRAY, ArrayWrapper.class);
        assertEquals(input.value, output.value);
    }

    // Provenance: MonthDayDeserTest#testDeserializeFromEmptyString.
    void testDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MONTH_DAY_MAP_TYPE);
        Map<String, MonthDay> nullMap = reader.readValue(MONTH_DAY_MAP_NULL);
        assertNull(nullMap.get("monthDay"));
        Map<String, MonthDay> emptyMap = reader.readValue(MONTH_DAY_MAP_EMPTY);
        assertNotNull(emptyMap);

        ObjectMapper strictMapper = VPackMapper.builder()
                .withConfigOverride(MonthDay.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> strictMapper.readerFor(MONTH_DAY_MAP_TYPE)
                        .readValue(MONTH_DAY_MAP_EMPTY));
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

    void __invoke_testDeserialization02Vpack() throws Exception {
        try {
            testDeserialization02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01Vpack() throws Exception {
        try {
            testDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testFormatAnnotationVpack() throws Exception {
        try {
            testFormatAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testFormatAnnotationArrayVpack() throws Exception {
        try {
            testFormatAnnotationArrayVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
