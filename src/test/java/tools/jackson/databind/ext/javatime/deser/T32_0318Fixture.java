package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0318Fixture {
private static final TypeReference<Map<String, LocalDateTime>> MAP_TYPE_REF =
            new TypeReference<Map<String, LocalDateTime>>() { };
private static final byte[] INVALID_FORMAT = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 30 20 31 35 3a 34 35 01");
private static final byte[] INVALID_FORMAT_WITH_ERA = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 30 20 31 35 3a 34 35 01");
private static final byte[] INVALID_DATE_WITH_ERA = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 53 32 30 31 39 2d 31 31 2d 33 31 20 31 35 3a 34 35 20 41 44 01");
private static final byte[] INVALID_TIME_WITH_ERA = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 53 32 30 31 39 2d 31 31 2d 33 30 20 32 35 3a 34 35 20 41 44 01");
private static final byte[] VALID_DATE_TIME_WITH_ERA = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 53 32 30 31 39 2d 31 31 2d 33 30 20 32 30 3a 34 35 20 41 44 01");
private static final byte[] INVALID_FORMAT_WITHOUT_ERA = VPackWireFixtureTest.hex(
            "14 1d 45 76 61 6c 75 65 53 32 30 31 39 2d 31 31 2d 33 30 20 31 35 3a 34 35 20 41 44 01");
private static final byte[] INVALID_TIME_WITHOUT_ERA = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 30 20 32 35 3a 34 35 01");
private static final byte[] INVALID_DATE_WITHOUT_ERA = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 31 20 31 35 3a 34 35 01");
private static final byte[] INVALID_DATE_TIME_WITHOUT_ERA = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 31 20 32 35 3a 34 35 01");
private static final byte[] VALID_DATE_TIME_WITHOUT_ERA = VPackWireFixtureTest.hex(
            "14 1a 45 76 61 6c 75 65 50 32 30 31 39 2d 31 31 2d 33 30 20 32 30 3a 34 35 01");
private static final byte[] STRICT_NULL = VPackWireFixtureTest.hex(
            "14 0d 48 64 61 74 65 74 69 6d 65 18 01");
private static final byte[] STRICT_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0d 48 64 61 74 65 74 69 6d 65 40 01");
private static final byte[] TRUE = VPackWireFixtureTest.hex("1a");

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidFormat.
    void testStrictCustomFormatForInvalidFormatVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_FORMAT, StrictWrapper.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidFormatWithEra.
    void testStrictCustomFormatForInvalidFormatWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_FORMAT_WITH_ERA,
                        StrictWrapperWithYearOfEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidDateWithEra.
    void testStrictCustomFormatForInvalidDateWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE_WITH_ERA,
                        StrictWrapperWithYearOfEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidTimeWithEra.
    void testStrictCustomFormatForInvalidTimeWithEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_TIME_WITH_ERA,
                        StrictWrapperWithYearOfEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatValidDateAndTimeWithEra.
    void testStrictCustomFormatValidDateAndTimeWithEraVpack() throws Exception {
        StrictWrapperWithYearOfEra wrapper = new VPackMapper().readValue(
                VALID_DATE_TIME_WITH_ERA, StrictWrapperWithYearOfEra.class);
        assertEquals(LocalDateTime.of(2019, 11, 30, 20, 45), wrapper.value);
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidFormatWithoutEra.
    void testStrictCustomFormatForInvalidFormatWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_FORMAT_WITHOUT_ERA,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidTimeWithoutEra.
    void testStrictCustomFormatForInvalidTimeWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_TIME_WITHOUT_ERA,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidDateWithoutEra.
    void testStrictCustomFormatForInvalidDateWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE_WITHOUT_ERA,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForInvalidDateAndTimeWithoutEra.
    void testStrictCustomFormatForInvalidDateAndTimeWithoutEraVpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_DATE_TIME_WITHOUT_ERA,
                        StrictWrapperWithYearWithoutEra.class));
    }

    // Provenance: LocalDateTimeDeserTest#testStrictCustomFormatForValidDateAndTimeWithoutEra.
    void testStrictCustomFormatForValidDateAndTimeWithoutEraVpack() throws Exception {
        StrictWrapperWithYearWithoutEra wrapper = new VPackMapper().readValue(
                VALID_DATE_TIME_WITHOUT_ERA, StrictWrapperWithYearWithoutEra.class);
        assertEquals(LocalDateTime.of(2019, 11, 30, 20, 45), wrapper.value);
    }

    // Provenance: LocalDateTimeDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDateTime.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(MAP_TYPE_REF);
        Map<String, LocalDateTime> nullValue = reader.readValue(STRICT_NULL);
        assertNull(nullValue.get("datetime"));
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(STRICT_EMPTY_STRING));
        org.junit.jupiter.api.Assertions.assertTrue(failure.getMessage().contains(
                "Cannot deserialize instance of `java.time.LocalDateTime` out of "),
                failure.getMessage());
    }

    // Provenance: LocalDateTimeDeserTest#testUnexpectedTokenIsHandled.
    void testUnexpectedTokenIsHandledVpack() throws Exception {
        LocalDateTime expected = LocalDateTime.of(2020, 10, 22, 4, 16, 20, 504_000_000);
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleUnexpectedToken(DeserializationContext ctxt, JavaType targetType,
                    JsonToken token, JsonParser parser, String failureMsg) {
                if (targetType.hasRawClass(LocalDateTime.class) && token.isBoolean()) {
                    return expected;
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();
        assertEquals(expected, mapper.readValue(TRUE, LocalDateTime.class));
    }
static final class StrictWrapper {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDateTime value;

        public StrictWrapper() { }
    }
static final class StrictWrapperWithYearOfEra {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm G", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDateTime value;

        public StrictWrapperWithYearOfEra() { }
    }
static final class StrictWrapperWithYearWithoutEra {
        @JsonFormat(pattern = "uuuu-MM-dd HH:mm", lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDateTime value;

        public StrictWrapperWithYearWithoutEra() { }
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


    void __invoke_testStrictCustomFormatForInvalidDateWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidTimeWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidTimeWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatValidDateAndTimeWithEraVpack() throws Exception {
        try {
            testStrictCustomFormatValidDateAndTimeWithEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidFormatWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidFormatWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidTimeWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidTimeWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidDateWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForInvalidDateAndTimeWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForInvalidDateAndTimeWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictCustomFormatForValidDateAndTimeWithoutEraVpack() throws Exception {
        try {
            testStrictCustomFormatForValidDateAndTimeWithoutEraVpack();
        } finally {
        }
    }


    void __invoke_testStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testUnexpectedTokenIsHandledVpack() throws Exception {
        try {
            testUnexpectedTokenIsHandledVpack();
        } finally {
        }
    }

}
