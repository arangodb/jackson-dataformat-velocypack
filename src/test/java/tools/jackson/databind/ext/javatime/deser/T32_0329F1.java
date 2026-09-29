package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0329F1 {
private static final TypeReference<Map<String, OffsetDateTime>> OFFSET_DATE_TIME_MAP =
            new TypeReference<Map<String, OffsetDateTime>>() { };
private static final byte[] TYPE_INFO_03 = VPackWireFixtureTest.hex(
            "06 27 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "2f ae 1b 99 be 1c 00 00 00 03 1c");
private static final byte[] TYPE_INFO_04 = VPackWireFixtureTest.hex(
            "06 42 02 58 6a 61 76 61 2e 74 69 6d 65 2e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 "
          + "63 32 30 32 30 2d 31 30 2d 32 32 54 30 34 3a 31 36 3a 32 30 2e 31 32 33 34 35 36 37 38 39 2d 30 37 3a 30 30 03 1c");
private static final byte[] OFFSET_DATE_TIME_NULL = VPackWireFixtureTest.hex(
            "14 13 4e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 18 01");
private static final byte[] OFFSET_DATE_TIME_EMPTY = VPackWireFixtureTest.hex(
            "14 13 4e 4f 66 66 73 65 74 44 61 74 65 54 69 6d 65 40 01");
private static final byte[] INVALID_OFFSET_TIME = VPackWireFixtureTest.hex(
            "4f 6e 6f 74 61 6e 6f 66 66 73 65 74 74 69 6d 65");
private static final byte[] SINGLE_OFFSET_TIME = VPackWireFixtureTest.hex(
            "13 0a 46 31 32 3a 30 30 5a 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] NESTED_OFFSET_TIME_POJO = VPackWireFixtureTest.hex(
            "14 6b 44 6e 61 6d 65 44 74 65 73 74 47 6f 62 6a 65 63 74 73 13 56 "
          + "14 53 48 70 61 72 74 44 61 74 65 13 0a 29 df 07 28 0a 28 0d 03 "
          + "49 73 74 61 72 74 74 69 6d 65 13 09 28 0f 37 42 2b 30 03 "
          + "47 65 6e 64 74 69 6d 65 13 09 32 28 0e 42 2b 30 03 "
          + "48 63 6f 6d 6d 65 6e 74 73 4f 69 6e 20 74 68 65 20 63 6f 6d 6d 65 6e 74 73 04 "
          + "01 02");
private static final OffsetDateTime TYPE_INFO_03_DATE = OffsetDateTime.ofInstant(
            Instant.ofEpochSecond(123456789L, 422_000_000L), ZoneOffset.UTC);
private static final OffsetDateTime TYPE_INFO_04_DATE = OffsetDateTime.of(
            2020, 10, 22, 4, 16, 20, 123_456_789, ZoneOffset.of("-07:00"));

    // Provenance: OffsetTimeDeserTest#testBadDeserializationFromString01.
    void testBadDeserializationFromString01Vpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(INVALID_OFFSET_TIME, OffsetTime.class));
        assertTrue(failure.getMessage().contains(
                "Cannot deserialize value of type `java.time.OffsetTime` from String"),
                failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserOfArrayOf.
    void testDeserOfArrayOfVpack() throws Exception {
        NestedPojo result = new VPackMapper().readValue(NESTED_OFFSET_TIME_POJO,
                NestedPojo.class);
        assertNotNull(result);
        assertNotNull(result.objects);
        assertEquals(1, result.objects.size());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(SINGLE_OFFSET_TIME, OffsetTime.class));
        assertTrue(failure.getMessage().contains("Unexpected token (VALUE_STRING) within Array"),
                failure.getMessage());
    }

    // Provenance: OffsetTimeDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OffsetTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        assertEquals(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC),
                reader.readValue(SINGLE_OFFSET_TIME));
    }
private static ObjectMapper typeInfoMapper(TimeZone timeZone) {
        VPackMapper.Builder builder = VPackMapper.builder()
                .addMixIn(Temporal.class, TemporalTypeInfo.class);
        if (timeZone != null) {
            builder.defaultTimeZone(timeZone);
        }
        return builder.build();
    }
private static OffsetDateTime roundTrip(ObjectMapper mapper, OffsetDateTime value)
            throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), OffsetDateTime.class);
    }
private static void assertTimestamp(Instant expected, ZoneOffset expectedOffset,
            OffsetDateTime actual) {
        assertEquals(expected, actual.toInstant());
        assertEquals(expectedOffset, actual.getOffset());
    }
private static ZoneOffset defaultOffset(OffsetDateTime date) {
        return java.time.ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }
static class NestedPojo {
        public String name;
        public List<NestedObject> objects;
    }
static class NestedObject {
        public LocalDate partDate;
        public OffsetTime starttime;
        public OffsetTime endtime;
        public String comments;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalTypeInfo { }

    void __invoke_testBadDeserializationFromString01Vpack() throws Exception {
        try {
            testBadDeserializationFromString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserOfArrayOfVpack() throws Exception {
        try {
            testDeserOfArrayOfVpack();
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

}
