package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0314Fixture {
private static final TypeReference<Map<String, LocalDate>> DATE_MAP_TYPE =
            new TypeReference<Map<String, LocalDate>>() { };
private static final byte[] DATE_MAP_WITH_NULL = VPackWireFixtureTest.hex(
            "14 09 44 64 61 74 65 18 01");
private static final byte[] DATE_MAP_WITH_EMPTY = VPackWireFixtureTest.hex(
            "14 09 44 64 61 74 65 40 01");
private static final byte[] EPOCH_DAY_TWO = VPackWireFixtureTest.hex("32");
private static final byte[] EPOCH_DAY_FORTY = VPackWireFixtureTest.hex("28 28");
private static final byte[] EPOCH_DAY_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] INVALID_DATE = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 32 30 31 39 2d 31 31 2d 33 31 01");
private static final byte[] VALID_DATE = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 32 30 31 39 2d 31 31 2d 33 30 01");
private static final byte[] VALID_DATE_WITH_ERA = VPackWireFixtureTest.hex(
            "14 17 45 76 61 6c 75 65 4d 32 30 31 39 2d 31 31 2d 33 30 20 41 44 01");
private static final byte[] INVALID_DATE_WITH_ERA = VPackWireFixtureTest.hex(
            "14 17 45 76 61 6c 75 65 4d 32 30 31 39 2d 31 31 2d 33 31 20 41 44 01");

    // Provenance: LocalDateDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(DATE_MAP_TYPE);
        Map<String, LocalDate> nullMap = reader.readValue(DATE_MAP_WITH_NULL);
        Map<String, LocalDate> emptyMap = reader.readValue(DATE_MAP_WITH_EMPTY);
        assertNull(nullMap.get("date"));
        assertNull(emptyMap.get("date"));
    }

    // Provenance: LocalDateDeserTest#testLenientDeserializeFromInt.
    void testLenientDeserializeFromIntVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDate.class);
        assertEquals(LocalDate.of(1970, Month.JANUARY, 3),
                reader.readValue(EPOCH_DAY_TWO));
        assertEquals(LocalDate.of(1970, Month.FEBRUARY, 10),
                reader.readValue(EPOCH_DAY_FORTY));
    }

    // Provenance: LocalDateDeserTest#testLenientDeserializeFromNumberInt.
    void testLenientDeserializeFromNumberIntVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER_INT)))
                .build();
        assertEquals(LocalDate.of(1970, Month.MAY, 4),
                mapper.readValue(EPOCH_DAY_123, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testStricDeserializeFromInt.
    void testStricDeserializeFromIntVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(EPOCH_DAY_TWO, LocalDate.class));
        assertTrue(failure.getMessage().contains("Cannot deserialize instance of"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("not allowed because 'strict' mode set"),
                failure.getMessage());
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForInvalidDateWithEra.
    void testStrictCustomFormatForInvalidDateWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE_WITH_ERA,
                        StrictWrapperWithYearOfEra.class));
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForInvalidDateWithoutEra.
    void testStrictCustomFormatForInvalidDateWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForInvalidFormat.
    void testStrictCustomFormatForInvalidFormatVpack() {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(VALID_DATE, StrictWrapperWithFormat.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.LocalDate` from String"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains("\"2019-11-30\""),
                failure.getMessage());
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForInvalidFormatWithEra.
    void testStrictCustomFormatForInvalidFormatWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(VALID_DATE,
                        StrictWrapperWithYearOfEra.class));
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForInvalidFormatWithoutEra.
    void testStrictCustomFormatForInvalidFormatWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(VALID_DATE_WITH_ERA,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForValidDateWithEra.
    void testStrictCustomFormatForValidDateWithEraVpack() throws Exception {
        StrictWrapperWithYearOfEra wrapper = new VPackMapper().readValue(
                VALID_DATE_WITH_ERA, StrictWrapperWithYearOfEra.class);
        assertEquals(LocalDate.of(2019, 11, 30), wrapper.value);
    }

    // Provenance: LocalDateDeserTest#testStrictCustomFormatForValidDateWithoutEra.
    void testStrictCustomFormatForValidDateWithoutEraVpack() throws Exception {
        StrictWrapperWithYearWithoutEra wrapper = new VPackMapper().readValue(
                VALID_DATE, StrictWrapperWithYearWithoutEra.class);
        assertEquals(LocalDate.of(2019, 11, 30), wrapper.value);
    }

    // Provenance: LocalDateDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(DATE_MAP_TYPE);
        Map<String, LocalDate> nullMap = reader.readValue(DATE_MAP_WITH_NULL);
        assertNull(nullMap.get("date"));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(DATE_MAP_WITH_EMPTY));
    }
static final class StrictWrapperWithFormat {
        @JsonFormat(pattern = "yyyy-MM-dd", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDate value;

        public StrictWrapperWithFormat() { }
    }
static final class StrictWrapperWithYearOfEra {
        @JsonFormat(pattern = "yyyy-MM-dd G", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDate value;

        public StrictWrapperWithYearOfEra() { }
    }
static final class StrictWrapperWithYearWithoutEra {
        @JsonFormat(pattern = "uuuu-MM-dd", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDate value;

        public StrictWrapperWithYearWithoutEra() { }
    }

    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromIntVpack() throws Exception {
        try {
            testLenientDeserializeFromIntVpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromNumberIntVpack() throws Exception {
        try {
            testLenientDeserializeFromNumberIntVpack();
        } finally {
        }
    }


    void __invoke_testStricDeserializeFromIntVpack() throws Exception {
        try {
            testStricDeserializeFromIntVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidDateWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidDateWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidFormatVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidFormatVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidFormatWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidFormatWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidFormatWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidFormatWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForValidDateWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForValidDateWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForValidDateWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForValidDateWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
