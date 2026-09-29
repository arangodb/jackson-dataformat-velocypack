package tools.jackson.databind.ext.javatime.deser;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0334F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final TypeReference<Map<String, YearMonth>> YEAR_MONTH_MAP =
            new TypeReference<Map<String, YearMonth>>() { };
private static final TypeReference<Map<String, ZoneId>> ZONE_ID_MAP =
            new TypeReference<Map<String, ZoneId>>() { };
private static final byte[] YEAR_MONTH_2000 = VPackWireFixtureTest.hex(
            "47 32 30 30 30 2d 30 31");
private static final byte[] YEAR_MONTH_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 0b 47 32 30 30 30 2d 30 31 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] YEAR_MONTH_ABOVE_10K = VPackWireFixtureTest.hex(
            "48 31 30 30 30 30 2d 30 31");
private static final byte[] YEAR_MONTH_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 49 79 65 61 72 4d 6f 6e 74 68 18 01");
private static final byte[] YEAR_MONTH_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0e 49 79 65 61 72 4d 6f 6e 74 68 40 01");
private static final byte[] ZONE_ID_CHICAGO = VPackWireFixtureTest.hex(
            "4f 41 6d 65 72 69 63 61 2f 43 68 69 63 61 67 6f");
private static final byte[] ZONE_ID_ANCHORAGE = VPackWireFixtureTest.hex(
            "51 41 6d 65 72 69 63 61 2f 41 6e 63 68 6f 72 61 67 65");
private static final byte[] TYPED_ZONE_ID = VPackWireFixtureTest.hex(
            "06 25 02 50 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 49 64 "
          + "4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03 14");
private static final byte[] TYPED_ZONE_REGION = VPackWireFixtureTest.hex(
            "06 29 02 54 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 52 65 67 69 6f 6e "
          + "4e 41 6d 65 72 69 63 61 2f 44 65 6e 76 65 72 03 18");
private static final byte[] ZONE_ID_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 46 7a 6f 6e 65 49 64 18 01");
private static final byte[] ZONE_ID_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 0b 46 7a 6f 6e 65 49 64 40 01");

    // Provenance: YearMonthDeserTest#testDeserializationAsArrayEnabled.
    void testYearMonthDeserializationAsArrayEnabledVpack() throws Exception {
        YearMonth value = MAPPER.readerFor(YearMonth.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(YEAR_MONTH_SINGLE_ARRAY);
        assertEquals(YearMonth.of(2000, 1), value);
    }

    // Provenance: YearMonthDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testYearMonthDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        // YearMonth accepts an empty array as null even without unwrapping enabled.
        assertNull(MAPPER.readValue(EMPTY_ARRAY, YearMonth.class));
    }

    // Provenance: YearMonthDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testYearMonthDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        YearMonth value = MAPPER.readerFor(YearMonth.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(EMPTY_ARRAY);
        assertNull(value);
    }

    // Provenance: YearMonthDeserTest#testDeserializationAsString01.
    void testYearMonthDeserializationAsString01Vpack() throws Exception {
        assertEquals(YearMonth.of(2000, 1), MAPPER.readValue(YEAR_MONTH_2000,
                YearMonth.class));
    }

    // Provenance: YearMonthDeserTest#testLenientDeserializeFromEmptyString.
    void testYearMonthLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        ObjectReader reader = mapper.readerFor(YEAR_MONTH_MAP);

        Map<String, YearMonth> fromNull = reader.readValue(YEAR_MONTH_NULL_PROPERTY);
        Map<String, YearMonth> fromEmpty = reader.readValue(YEAR_MONTH_EMPTY_PROPERTY);
        assertNull(fromNull.get("yearMonth"));
        assertNull(fromEmpty.get("yearMonth"));
    }

    // Provenance: YearMonthDeserTest#testStrictDeserializeFromEmptyString.
    void testYearMonthStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(YearMonth.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(YEAR_MONTH_MAP);

        Map<String, YearMonth> fromNull = reader.readValue(YEAR_MONTH_NULL_PROPERTY);
        assertNull(fromNull.get("yearMonth"));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(YEAR_MONTH_EMPTY_PROPERTY));
    }

    // Provenance: YearMonthDeserTest#testYearAbove10k.
    void testYearMonthYearAbove10kVpack() throws Exception {
        YearMonth input = YearMonth.of(10000, 1);
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        assertArrayEquals(YEAR_MONTH_ABOVE_10K, mapper.writeValueAsBytes(input));
        assertEquals(input, mapper.readValue(YEAR_MONTH_ABOVE_10K, YearMonth.class));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface ZoneIdTypeInfo { }

    void __invoke_testYearMonthDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testYearMonthDeserializationAsArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testYearMonthDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testYearMonthDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthDeserializationAsString01Vpack() throws Exception {
        try {
            testYearMonthDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testYearMonthLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testYearMonthLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testYearMonthStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testYearMonthYearAbove10kVpack() throws Exception {
        try {
            testYearMonthYearAbove10kVpack();
        } finally {
        }
    }

}
