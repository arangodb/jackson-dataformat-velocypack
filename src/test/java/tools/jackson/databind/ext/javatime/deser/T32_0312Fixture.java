package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0312Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] DATE_1986 = VPackWireFixtureTest.hex(
            "06 0c 03 29 c2 07 31 28 11 03 06 07");
private static final byte[] DATE_2013 = VPackWireFixtureTest.hex(
            "06 0c 03 29 dd 07 38 28 15 03 06 07");
private static final byte[] DATE_2000 = VPackWireFixtureTest.hex(
            "4a 32 30 30 30 2d 30 31 2d 30 31");
private static final byte[] DATE_1986_STRING = VPackWireFixtureTest.hex(
            "4a 31 39 38 36 2d 30 31 2d 31 37");
private static final byte[] DATE_2013_STRING = VPackWireFixtureTest.hex(
            "4a 32 30 31 33 2d 30 38 2d 32 31");
private static final byte[] DATE_TIME_STRING = VPackWireFixtureTest.hex(
            "53 32 30 31 33 2d 30 38 2d 32 31 54 31 32 3a 33 34 3a 35 36");
private static final byte[] INVALID_STRING_01 = VPackWireFixtureTest.hex(
            "4d 6e 6f 74 61 6c 6f 63 61 6c 64 61 74 65");
private static final byte[] INVALID_STRING_02 = VPackWireFixtureTest.hex(
            "59 32 30 31 35 2d 30 36 2d 31 39 54 53 68 6f 75 6c 64 4e 6f 74 50 61 72 73 65");
private static final byte[] SINGLE_DATE_ARRAY = VPackWireFixtureTest.hex(
            "13 0e 4a 32 30 30 30 2d 30 31 2d 30 31 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] CUSTOM_FORMAT = VPackWireFixtureTest.hex(
            "14 1f 45 76 61 6c 75 65 55 32 30 31 35 5f 30 37 5f 32 38 54 31 33 3a 35 33 2b 30 33 30 30 01");
private static final byte[] CASE_INSENSITIVE_CANONICAL = VPackWireFixtureTest.hex(
            "4b 30 31 2d 4a 61 6e 2d 32 30 30 30");

    // Provenance: LocalDateDeserTest#testDeserializationAsTimestamp01.
    void testDeserializationAsTimestamp01Vpack() throws Exception {
        assertEquals(LocalDate.of(1986, 1, 17),
                MAPPER.readValue(DATE_1986, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsTimestamp02.
    void testDeserializationAsTimestamp02Vpack() throws Exception {
        assertEquals(LocalDate.of(2013, 8, 21),
                MAPPER.readValue(DATE_2013, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        assertEquals(LocalDate.of(2000, 1, 1),
                MAPPER.readValue(DATE_2000, LocalDate.class));
        assertEquals(LocalDate.of(1986, 1, 17),
                MAPPER.readValue(DATE_1986_STRING, LocalDate.class));
        assertEquals(LocalDate.of(2013, 8, 21),
                MAPPER.readValue(DATE_2013_STRING, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsString02.
    void testDeserializationAsString02Vpack() throws Exception {
        assertEquals(LocalDate.of(2013, 8, 21),
                MAPPER.readValue(DATE_TIME_STRING, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testBadDeserializationAsString01.
    void testBadDeserializationAsString01Vpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(INVALID_STRING_01, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testBadDeserializationAsString02.
    void testBadDeserializationAsString02Vpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(INVALID_STRING_02, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(SINGLE_DATE_ARRAY, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        assertNull(MAPPER.readValue(EMPTY_ARRAY, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertEquals(LocalDate.of(2000, 1, 1),
                mapper.readValue(SINGLE_DATE_ARRAY, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue(EMPTY_ARRAY, LocalDate.class));
    }

    // Provenance: LocalDateDeserTest#testCustomFormat.
    void testCustomFormatVpack() throws Exception {
        CustomFormatWrapper wrapper = MAPPER.readValue(CUSTOM_FORMAT,
                CustomFormatWrapper.class);
        assertEquals(28, wrapper.value.getDayOfMonth());
    }

    // Provenance: LocalDateDeserTest#testDeserializationCaseInsensitiveDisabled.
    void testDeserializationCaseInsensitiveDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)
                .withConfigOverride(LocalDate.class, o -> o.setFormat(
                        JsonFormat.Value.forPattern("dd-MMM-yyyy")))
                .build();
        assertEquals(LocalDate.of(2000, 1, 1),
                mapper.readValue(CASE_INSENSITIVE_CANONICAL, LocalDate.class));
    }
static final class CustomFormatWrapper {
        @JsonFormat(pattern = "yyyy_MM_dd'T'HH:mmZ",
                shape = JsonFormat.Shape.STRING)
        public LocalDate value;

        public CustomFormatWrapper() { }
    }

    void __invoke_testDeserializationAsTimestamp01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp02Vpack();
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


    void __invoke_testBadDeserializationAsString01Vpack() throws Exception {
        try {
            testBadDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testBadDeserializationAsString02Vpack() throws Exception {
        try {
            testBadDeserializationAsString02Vpack();
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


    void __invoke_testCustomFormatVpack() throws Exception {
        try {
            testCustomFormatVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationCaseInsensitiveDisabledVpack() throws Exception {
        try {
            testDeserializationCaseInsensitiveDisabledVpack();
        } finally {
        }
    }

}
