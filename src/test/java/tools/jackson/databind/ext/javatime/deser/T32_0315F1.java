package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0315F1 {
private static final byte[] LOCAL_DATE_NUMBER_INT = VPackWireFixtureTest.hex(
            "14 0a 44 64 61 74 65 28 7b 01");
private static final byte[] LOCAL_DATE_STRING_SHAPE = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 7b 01");
private static final byte[] INVALID_LOCAL_DATE = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 32 30 31 39 2d 31 31 2d 33 31 01");
private static final byte[] LOCAL_DATE_TIME_STRING_01 = VPackWireFixtureTest.hex(
            "50 31 39 38 36 2d 30 31 2d 31 37 54 31 35 3a 34 33");
private static final byte[] LOCAL_DATE_TIME_STRING_01_SECOND = VPackWireFixtureTest.hex(
            "50 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30");
private static final byte[] LOCAL_DATE_TIME_STRING_02 = VPackWireFixtureTest.hex(
            "53 32 30 31 33 2d 30 38 2d 32 31 54 30 39 3a 32 32 3a 35 37");
private static final byte[] LOCAL_DATE_TIME_ZULU = VPackWireFixtureTest.hex(
            "58 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 35 30 34 5a");
private static final byte[] INVALID_LOCAL_DATE_TIME_STRING = VPackWireFixtureTest.hex(
            "51 6e 6f 74 61 6c 6f 63 61 6c 64 61 74 65 74 69 6d 65");
private static final byte[] HANDLED_LOCAL_DATE_TIME_STRING = VPackWireFixtureTest.hex(
            "43 6e 6f 77");
private static final byte[] SINGLE_LOCAL_DATE_TIME_ARRAY = VPackWireFixtureTest.hex(
            "13 14 50 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");

    // Provenance: LocalDateTimeDeserTest#testAllowZuluIfLenient.
    void testAllowZuluIfLenientVpack() throws Exception {
        LocalDateTime expected = LocalDateTime.of(2020, 10, 22, 4, 16, 20, 504_000_000);
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class);
        assertEquals(expected, reader.readValue(LOCAL_DATE_TIME_ZULU));
        assertEquals(expected, reader.with(java.util.TimeZone.getTimeZone("America/Chicago"))
                .readValue(LOCAL_DATE_TIME_ZULU));
        assertEquals(expected, reader.with(java.util.TimeZone.getTimeZone("Europe/Budapest"))
                .readValue(LOCAL_DATE_TIME_ZULU));
    }

    // Provenance: LocalDateTimeDeserTest#testBadDeserializationAsString01.
    void testBadDeserializationAsString01Vpack() {
        assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_LOCAL_DATE_TIME_STRING,
                        LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDateTimeExceptionIsHandled.
    void testDateTimeExceptionIsHandledVpack() throws Exception {
        LocalDateTime expected = LocalDateTime.of(2020, 10, 22, 4, 16, 20, 504_000_000);
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt,
                    Class<?> targetType, String valueToConvert, String failureMsg) {
                if (LocalDateTime.class == targetType && "now".equals(valueToConvert)) {
                    return expected;
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper mapper = VPackMapper.builder().addHandler(handler).build();
        assertEquals(expected, mapper.readValue(HANDLED_LOCAL_DATE_TIME_STRING,
                LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(SINGLE_LOCAL_DATE_TIME_ARRAY,
                        LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertEquals(LocalDateTime.of(2000, 1, 1, 12, 0),
                mapper.readValue(SINGLE_LOCAL_DATE_TIME_ARRAY, LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        assertNull(new VPackMapper().readValue(EMPTY_ARRAY, LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue(EMPTY_ARRAY, LocalDateTime.class));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(LocalDateTime.class);
        assertEquals(LocalDateTime.of(1986, 1, 17, 15, 43),
                reader.readValue(LOCAL_DATE_TIME_STRING_01));
        assertEquals(LocalDateTime.of(2000, 1, 1, 12, 0),
                reader.readValue(LOCAL_DATE_TIME_STRING_01_SECOND));
    }

    // Provenance: LocalDateTimeDeserTest#testDeserializationAsString02.
    void testDeserializationAsString02Vpack() throws Exception {
        assertEquals(LocalDateTime.of(2013, 8, 21, 9, 22, 57),
                new VPackMapper().readValue(LOCAL_DATE_TIME_STRING_02,
                        LocalDateTime.class));
    }
static final class StrictNumberIntWrapper {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public LocalDate date;

        public StrictNumberIntWrapper() { }
    }
static final class StrictDateWrapper {
        @JsonFormat(pattern = "yyyy_MM_dd'T'HH:mmZ",
                shape = JsonFormat.Shape.STRING)
        public LocalDate value;

        public StrictDateWrapper() { }
    }
static final class StrictFormatWrapper {
        @JsonFormat(pattern = "yyyy-MM-dd",
                lenient = com.fasterxml.jackson.annotation.OptBoolean.FALSE)
        public LocalDate value;

        public StrictFormatWrapper() { }
    }

    void __invoke_testAllowZuluIfLenientVpack() throws Exception {
        try {
            testAllowZuluIfLenientVpack();
        } finally {
        }
    }


    void __invoke_testBadDeserializationAsString01Vpack() throws Exception {
        try {
            testBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDateTimeExceptionIsHandledVpack() throws Exception {
        try {
            testDateTimeExceptionIsHandledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayEnabledVpack();
        } finally {
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


    void __invoke_testDeserializationAsString02Vpack() throws Exception {
        try {
            testDeserializationAsString02Vpack();
        } finally {
        }
    }

}
