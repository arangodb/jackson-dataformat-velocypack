package tools.jackson.databind.ext.javatime.deser;

import java.time.Year;
import java.time.temporal.Temporal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0333F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final TypeReference<Map<String, Year>> YEAR_MAP =
            new TypeReference<Map<String, Year>>() { };
private static final byte[] YEAR_2000 = VPackWireFixtureTest.hex(
            "44 32 30 30 30");
private static final byte[] YEAR_EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] YEAR_TYPE_INFO = VPackWireFixtureTest.hex(
            "13 15 4e 6a 61 76 61 2e 74 69 6d 65 2e 59 65 61 72 29 d5 07 02");
private static final byte[] YEAR_NULL_PROPERTY = VPackWireFixtureTest.hex(
            "14 09 44 59 65 61 72 18 01");
private static final byte[] YEAR_EMPTY_PROPERTY = VPackWireFixtureTest.hex(
            "14 09 44 64 61 74 65 40 01");
private static final byte[] CUSTOM_FORMAT_WRAPPER = VPackWireFixtureTest.hex(
            "0b 10 01 45 76 61 6c 75 65 45 59 32 30 31 38 03");
private static final byte[] CONFIG_OVERRIDE_FORMAT = VPackWireFixtureTest.hex(
            "45 58 32 30 31 38");
private static final byte[] UNTYPED_TIMESTAMP_STRING = VPackWireFixtureTest.hex(
            "45 31 32 33 34 35");
private static final byte[] UNTYPED_TIMESTAMP_TOO_BIG = VPackWireFixtureTest.hex(
            "4b 31 32 33 34 35 36 37 38 39 30 31");
private static final byte[] INVALID_YEAR_MONTH = VPackWireFixtureTest.hex(
            "4d 6e 6f 74 61 79 65 61 72 6d 6f 6e 74 68");
private static final byte[] YEAR_MONTH_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 0b 47 32 30 30 30 2d 30 31 01");

    // Provenance: YearDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() {
        ObjectReader reader = MAPPER.readerFor(Year.class);
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(YEAR_EMPTY_ARRAY));
        assertThrows(MismatchedInputException.class,
                () -> reader.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                        .readValue(YEAR_EMPTY_ARRAY));
    }

    // Provenance: YearDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        Year value = MAPPER.readerFor(Year.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(YEAR_EMPTY_ARRAY);
        assertNull(value);
    }

    // Provenance: YearDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        assertEquals(Year.of(2000), MAPPER.readValue(YEAR_2000, Year.class));
    }

    // Provenance: YearDeserTest#testDeserializationWithTypeInfo.
    void testDeserializationWithTypeInfoVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class)
                .build();
        Temporal value = mapper.readValue(YEAR_TYPE_INFO, Temporal.class);
        assertInstanceOf(Year.class, value);
        assertEquals(Year.of(2005), value);
    }

    // Provenance: YearDeserTest#testDeserializeFromEmptyString.
    void testDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(YEAR_MAP);
        Map<String, Year> fromNull = reader.readValue(YEAR_NULL_PROPERTY);
        assertNull(fromNull.get("Year"));

        Map<String, Year> fromEmpty = reader.readValue(YEAR_EMPTY_PROPERTY);
        assertNotNull(fromEmpty);

        ObjectMapper strictMapper = VPackMapper.builder()
                .withConfigOverride(Year.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> strictMapper.readerFor(YEAR_MAP).readValue(YEAR_EMPTY_PROPERTY));
    }

    // Provenance: YearDeserTest#testWithCustomFormat.
    void testWithCustomFormatVpack() throws Exception {
        FormattedYear input = new FormattedYear(Year.of(2018));
        assertArrayEquals(CUSTOM_FORMAT_WRAPPER, MAPPER.writeValueAsBytes(input));
        FormattedYear result = MAPPER.readValue(CUSTOM_FORMAT_WRAPPER, FormattedYear.class);
        assertEquals(input.value, result.value);
    }

    // Provenance: YearDeserTest#testWithCustomFormat78.
    void testWithCustomFormat78Vpack() throws Exception {
        Issue78Object input = new Issue78Object(Year.of(2018));
        assertArrayEquals(CUSTOM_FORMAT_WRAPPER, MAPPER.writeValueAsBytes(input));
        Issue78Object result = MAPPER.readValue(CUSTOM_FORMAT_WRAPPER, Issue78Object.class);
        assertEquals(input.value, result.value);
    }

    // Provenance: YearDeserTest#testWithFormatViaConfigOverride.
    void testWithFormatViaConfigOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Year.class,
                        o -> o.setFormat(JsonFormat.Value.forPattern("'X'yyyy")))
                .build();
        Year input = Year.of(2018);
        assertArrayEquals(CONFIG_OVERRIDE_FORMAT, mapper.writeValueAsBytes(input));
        assertEquals(input, mapper.readValue(CONFIG_OVERRIDE_FORMAT, Year.class));
    }

    // Provenance: YearDeserTest#untypedScalarTimestampAsString.
    void untypedScalarTimestampAsStringVpack() throws Exception {
        assertEquals(Year.of(12345), MAPPER.readValue(UNTYPED_TIMESTAMP_STRING, Year.class));
    }

    // Provenance: YearDeserTest#untypedScalarTimestampTooBig.
    void untypedScalarTimestampTooBigIsUnsupportedForVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(UNTYPED_TIMESTAMP_TOO_BIG, Year.class));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }
static class FormattedYear {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'Y'yyyy")
        public Year value;

        public FormattedYear() { }

        FormattedYear(Year value) {
            this.value = value;
        }
    }
static class Issue78Object {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'Y'yyyy")
        public Year value;

        public Issue78Object() { }

        Issue78Object(Year value) {
            this.value = value;
        }
    }

    void __invoke_testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01Vpack() throws Exception {
        try {
            testDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfoVpack() throws Exception {
        try {
            testDeserializationWithTypeInfoVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testWithCustomFormatVpack() throws Exception {
        try {
            testWithCustomFormatVpack();
        } finally {
        }
    }


    void __invoke_testWithCustomFormat78Vpack() throws Exception {
        try {
            testWithCustomFormat78Vpack();
        } finally {
        }
    }


    void __invoke_testWithFormatViaConfigOverrideVpack() throws Exception {
        try {
            testWithFormatViaConfigOverrideVpack();
        } finally {
        }
    }


    void __invoke_untypedScalarTimestampAsStringVpack() throws Exception {
        try {
            untypedScalarTimestampAsStringVpack();
        } finally {
        }
    }


    void __invoke_untypedScalarTimestampTooBigIsUnsupportedForVpack() throws Exception {
        try {
            untypedScalarTimestampTooBigIsUnsupportedForVpack();
        } finally {
        }
    }

}
