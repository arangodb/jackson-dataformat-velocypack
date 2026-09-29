package tools.jackson.databind.ext.javatime.deser;

import java.time.Year;
import java.time.YearMonth;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0333F1 {
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

    // Provenance: YearMonthDeserTest#testBadDeserializationAsString01.
    void testYearMonthBadDeserializationAsString01Vpack() {
        assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(INVALID_YEAR_MONTH, YearMonth.class));
    }

    // Provenance: YearMonthDeserTest#testDeserializationAsArrayDisabled.
    void testYearMonthDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(YEAR_MONTH_SINGLE_ARRAY, YearMonth.class));
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

    void __invoke_testYearMonthBadDeserializationAsString01Vpack() throws Exception {
        try {
            testYearMonthBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testYearMonthDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testYearMonthDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }

}
