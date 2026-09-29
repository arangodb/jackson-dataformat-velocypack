package tools.jackson.databind.ext.javatime.deser;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0315F0 {
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

    // Provenance: LocalDateDeserTest#testStrictDeserializeFromNumberInt.
    void testStrictDeserializeFromNumberIntVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        StrictNumberIntWrapper wrapper = mapper.readValue(LOCAL_DATE_NUMBER_INT,
                StrictNumberIntWrapper.class);
        assertEquals(LocalDate.of(1970, 5, 4), wrapper.date);
    }

    // Provenance: LocalDateDeserTest#testStrictDeserializeFromString.
    void testStrictDeserializeFromStringVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(LOCAL_DATE_STRING_SHAPE,
                        StrictDateWrapper.class));
    }

    // Provenance: LocalDateDeserTest#testStrictWithCustomFormat.
    void testStrictWithCustomFormatVpack() {
        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> new VPackMapper().readValue(INVALID_LOCAL_DATE,
                        StrictFormatWrapper.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.LocalDate` from String"),
                failure.getMessage());
        assertTrue(failure.getMessage().contains(
                "\"2019-11-31\""), failure.getMessage());
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

    void __invoke_testStrictDeserializeFromNumberIntVpack() throws Exception {
        try {
            testStrictDeserializeFromNumberIntVpack();
        } finally {
        }
    }


    void __invoke_testStrictDeserializeFromStringVpack() throws Exception {
        try {
            testStrictDeserializeFromStringVpack();
        } finally {
        }
    }


    void __invoke_testStrictWithCustomFormatVpack() throws Exception {
        try {
            testStrictWithCustomFormatVpack();
        } finally {
        }
    }

}
