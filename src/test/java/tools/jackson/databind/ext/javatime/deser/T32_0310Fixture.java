package tools.jackson.databind.ext.javatime.deser;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0310Fixture {
private static final Instant TYPE_INFO_DECIMAL =
            Instant.ofEpochSecond(123456789L, 183917322L);
private static final Instant TYPE_INFO_MILLIS =
            Instant.ofEpochSecond(123456789L, 422000000L);
private static final Instant OFFSET_BASE =
            Instant.parse("2024-05-06T07:08:09.123456789Z");
private static final byte[] TYPE_INFO_DECIMAL_VPACK = VPackWireFixtureTest.hex(
            "06 26 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22 03 15");
private static final byte[] TYPE_INFO_NANOS_VPACK = VPackWireFixtureTest.hex(
            "06 1c 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "2b 15 cd 5b 07 03 15");
private static final byte[] TYPE_INFO_MILLIS_VPACK = VPackWireFixtureTest.hex(
            "06 20 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "2f ae 1b 99 be 1c 00 00 00 03 15");
private static final byte[] TYPE_INFO_STRING_VPACK = VPackWireFixtureTest.hex(
            "06 36 02 51 6a 61 76 61 2e 74 69 6d 65 2e 49 6e 73 74 61 6e 74 "
          + "5e 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e "
          + "31 32 33 34 35 36 37 38 39 5a 03 15");
private static final byte[] OFFSET_MINUS_ZERO = VPackWireFixtureTest.hex(
            "63 32 30 32 34 2d 30 35 2d 30 36 54 30 37 3a 30 38 3a 30 39 2e "
          + "31 32 33 34 35 36 37 38 39 2d 30 30 3a 30 30");
private static final byte[] COLONLESS_OFFSET = VPackWireFixtureTest.hex(
            "55 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 30 30");
private static final byte[] COLONLESS_OFFSET_WITH_TZ = VPackWireFixtureTest.hex(
            "63 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 30 30 "
          + "5b 45 75 72 6f 70 65 2f 50 61 72 69 73 5d");
private static final byte[] NEGATIVE_YEAR_WITH_COLON = VPackWireFixtureTest.hex(
            "65 2d 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 3a "
          + "30 30 5b 45 75 72 6f 70 65 2f 50 61 72 69 73 5d");
private static final byte[] NEGATIVE_YEAR_WITH_COLONLESS = VPackWireFixtureTest.hex(
            "64 2d 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 2b 30 31 30 30 "
          + "5b 45 75 72 6f 70 65 2f 50 61 72 69 73 5d");
private static final byte[] INVALID_EPOCH_SECOND = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] MAP_WITH_NULL_INSTANT = VPackWireFixtureTest.hex(
            "0b 0d 01 47 69 6e 73 74 61 6e 74 18 03");
private static final byte[] MAP_WITH_EMPTY_INSTANT = VPackWireFixtureTest.hex(
            "0b 0d 01 47 69 6e 73 74 61 6e 74 40 03");
private static final byte[] NUMERIC_INTEGER_STRING = VPackWireFixtureTest.hex(
            "4a 31 32 33 34 35 36 37 38 39 30");
private static final byte[] NUMERIC_DECIMAL_STRING = VPackWireFixtureTest.hex(
            "54 31 32 33 34 35 36 37 38 39 30 2e 31 32 33 34 35 36 37 38 39");
private static final TypeReference<Map<String, Instant>> MAP_TYPE =
            new TypeReference<Map<String, Instant>>() { };
private static final ObjectMapper NUMERIC_STRING_MAPPER = VPackMapper.builder()
            .enable(DateTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS)
            .build();
private static final ObjectReader INSTANT_READER = new VPackMapper()
            .readerFor(Instant.class);

    // Provenance: InstantDeserTest#testDeserializationFromStringWithZeroZoneOffset06.
    void testDeserializationFromStringWithZeroZoneOffset06Vpack() throws Exception {
        assertEquals(OFFSET_BASE, INSTANT_READER.readValue(OFFSET_MINUS_ZERO));
    }

    // Provenance: InstantDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        Temporal value = typeInfoMapper().readValue(TYPE_INFO_DECIMAL_VPACK, Temporal.class);
        assertInstanceOf(Instant.class, value);
        assertEquals(TYPE_INFO_DECIMAL, value);
    }

    // Provenance: InstantDeserTest#testDeserializationWithTypeInfo02.
    void testDeserializationWithTypeInfo02Vpack() throws Exception {
        Temporal value = typeInfoMapper().readerFor(Temporal.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_NANOS_VPACK);
        assertInstanceOf(Instant.class, value);
        assertEquals(Instant.ofEpochSecond(123456789L), value);
    }

    // Provenance: InstantDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        Temporal value = typeInfoMapper().readerFor(Temporal.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPE_INFO_MILLIS_VPACK);
        assertInstanceOf(Instant.class, value);
        assertEquals(TYPE_INFO_MILLIS, value);
    }

    // Provenance: InstantDeserTest#testDeserializationWithTypeInfo04.
    void testDeserializationWithTypeInfo04Vpack() throws Exception {
        Temporal value = typeInfoMapper().readValue(TYPE_INFO_STRING_VPACK, Temporal.class);
        assertInstanceOf(Instant.class, value);
        assertEquals(OFFSET_BASE, value);
    }

    // Provenance: InstantDeserTest#testISO8601ColonlessRegexFindsOffset.
    void testISO8601ColonlessRegexFindsOffsetVpack() throws Exception {
        Matcher matcher = colonlessRegex().matcher(readString(COLONLESS_OFFSET));
        assertTrue(matcher.find(), "Matcher finds +0100 as a colonless offset");
        assertEquals("+0100", matcher.group());
    }

    // Provenance: InstantDeserTest#testISO8601ColonlessRegexFindsOffsetWithTZ.
    void testISO8601ColonlessRegexFindsOffsetWithTZVpack() throws Exception {
        Matcher matcher = colonlessRegex().matcher(readString(COLONLESS_OFFSET_WITH_TZ));
        assertTrue(matcher.find(), "Matcher finds +0100 as a colonless offset");
        assertEquals("+0100", matcher.group());
    }

    // Provenance: InstantDeserTest#testISO8601ColonlessRegexDoesNotAffectNegativeYears.
    void testISO8601ColonlessRegexDoesNotAffectNegativeYearsVpack() throws Exception {
        Matcher matcher = colonlessRegex().matcher(readString(NEGATIVE_YEAR_WITH_COLON));
        assertFalse(matcher.find(), "Matcher does not find -2000 as an offset without colon");
    }

    // Provenance: InstantDeserTest#testISO8601ColonlessRegexDoesNotAffectNegativeYearsWithColonless.
    void testISO8601ColonlessRegexDoesNotAffectNegativeYearsWithColonlessVpack()
            throws Exception {
        Matcher matcher = colonlessRegex().matcher(readString(NEGATIVE_YEAR_WITH_COLONLESS));
        assertTrue(matcher.find(), "Matcher finds +0100 as a colonless offset");
        assertEquals("+0100", matcher.group());
    }

    // Provenance: InstantDeserTest#testInvalidEpochSecondValue.
    void testInvalidEpochSecondValueVpack() {
        DateTimeParseException exception = assertThrows(DateTimeParseException.class,
                () -> INSTANT_READER.readValue(INVALID_EPOCH_SECOND));
        assertTrue(exception.getMessage().contains("Failed to deserialize"));
        assertTrue(exception.getMessage().contains("java.time.Instant"));
        assertTrue(exception.getMessage().contains("9223372036854775807"));
        assertTrue(exception.getMessage().contains("Instant exceeds minimum or maximum instant"));
    }

    // Provenance: InstantDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MAP_TYPE);
        Map<String, Instant> nullMap = reader.readValue(MAP_WITH_NULL_INSTANT);
        Map<String, Instant> emptyMap = reader.readValue(MAP_WITH_EMPTY_INSTANT);
        assertNull(nullMap.get("instant"));
        assertNull(emptyMap.get("instant"));
    }

    // Provenance: InstantDeserTest#testNumericStringRespectsStreamReadConstraints.
    void testNumericStringRespectsStreamReadConstraintsVpack() throws Exception {
        assertNotNull(NUMERIC_STRING_MAPPER.readValue(NUMERIC_INTEGER_STRING, Instant.class));
        assertNotNull(NUMERIC_STRING_MAPPER.readValue(NUMERIC_DECIMAL_STRING, Instant.class));

        int maxLength = StreamReadConstraints.DEFAULT_MAX_NUM_LEN;
        String longInteger = "1".repeat(maxLength + 1);
        String longDecimal = "1234." + "9".repeat(maxLength);
        StreamConstraintsException integerException = assertThrows(StreamConstraintsException.class,
                () -> NUMERIC_STRING_MAPPER.readValue(stringValue(longInteger), Instant.class));
        assertTrue(integerException.getMessage().contains("Date/time value length"),
                integerException.getMessage());
        assertTrue(integerException.getMessage().contains("exceeds the maximum allowed"));
        StreamConstraintsException decimalException = assertThrows(StreamConstraintsException.class,
                () -> NUMERIC_STRING_MAPPER.readValue(stringValue(longDecimal), Instant.class));
        assertTrue(decimalException.getMessage().contains("Date/time value length"));
        assertTrue(decimalException.getMessage().contains("exceeds the maximum allowed"));
    }
private static VPackMapper typeInfoMapper() {
        return VPackMapper.builder()
                .addMixIn(Temporal.class, InstantTypeInfo.class)
                .build();
    }
private static String readString(byte[] input) throws Exception {
        return new VPackMapper().readValue(input, String.class);
    }
private static Pattern colonlessRegex() {
        return InstantDeserializerAccess.regex();
    }
private static byte[] stringValue(String value) {
        byte[] utf8 = value.getBytes(StandardCharsets.UTF_8);
        if (utf8.length <= 126) {
            byte[] result = new byte[utf8.length + 1];
            result[0] = (byte) (0x40 + utf8.length);
            System.arraycopy(utf8, 0, result, 1, utf8.length);
            return result;
        }
        byte[] result = new byte[utf8.length + 9];
        result[0] = (byte) 0xbf;
        for (int i = 0; i < 8; ++i) {
            result[1 + i] = (byte) ((long) utf8.length >>> (8 * i));
        }
        System.arraycopy(utf8, 0, result, 9, utf8.length);
        return result;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface InstantTypeInfo { }
private static final class InstantDeserializerAccess
            extends tools.jackson.databind.ext.javatime.deser.InstantDeserializer<Instant> {
        private InstantDeserializerAccess() {
            super(tools.jackson.databind.ext.javatime.deser.InstantDeserializer.INSTANT,
                    DateTimeFormatter.ISO_INSTANT);
        }

        private static Pattern regex() {
            return ISO8601_COLONLESS_OFFSET_REGEX;
        }
    }

    void __invoke_testDeserializationFromStringWithZeroZoneOffset06Vpack() throws Exception {
        try {
            testDeserializationFromStringWithZeroZoneOffset06Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo02Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo03Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo04Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo04Vpack();
        } finally {
        }
    }


    void __invoke_testISO8601ColonlessRegexFindsOffsetVpack() throws Exception {
        try {
            testISO8601ColonlessRegexFindsOffsetVpack();
        } finally {
        }
    }


    void __invoke_testISO8601ColonlessRegexFindsOffsetWithTZVpack() throws Exception {
        try {
            testISO8601ColonlessRegexFindsOffsetWithTZVpack();
        } finally {
        }
    }


    void __invoke_testISO8601ColonlessRegexDoesNotAffectNegativeYearsVpack() throws Exception {
        try {
            testISO8601ColonlessRegexDoesNotAffectNegativeYearsVpack();
        } finally {
        }
    }


    void __invoke_testISO8601ColonlessRegexDoesNotAffectNegativeYearsWithColonlessVpack() throws Exception {
        try {
            testISO8601ColonlessRegexDoesNotAffectNegativeYearsWithColonlessVpack();
        } finally {
        }
    }


    void __invoke_testInvalidEpochSecondValueVpack() throws Exception {
        try {
            testInvalidEpochSecondValueVpack();
        } finally {
        }
    }


    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testNumericStringRespectsStreamReadConstraintsVpack() throws Exception {
        try {
            testNumericStringRespectsStreamReadConstraintsVpack();
        } finally {
        }
    }

}
