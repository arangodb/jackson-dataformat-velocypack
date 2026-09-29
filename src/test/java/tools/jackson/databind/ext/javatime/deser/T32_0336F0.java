package tools.jackson.databind.ext.javatime.deser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0336F0 {
private static final TypeReference<Map<String, ZoneOffset>> OFFSET_MAP =
            new TypeReference<Map<String, ZoneOffset>>() { };
private static final TypeReference<Map<String, ZonedDateTime>> ZONED_MAP =
            new TypeReference<Map<String, ZonedDateTime>>() { };
private static final byte[] OFFSET_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 18 01");
private static final byte[] OFFSET_EMPTY = VPackWireFixtureTest.hex(
            "14 0f 4a 7a 6f 6e 65 4f 66 66 73 65 74 40 01");
private static final byte[] OFFSET_TWO_SPACES = VPackWireFixtureTest.hex("42 20 20");
private static final byte[] ZONED_2000_UTC = VPackWireFixtureTest.hex(
            "51 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 5a");
private static final byte[] ZONED_BAD = VPackWireFixtureTest.hex(
            "48 6e 6f 74 61 7a 6f 6e 65");
private static final byte[] ZONED_MILLIS = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 29 e9 03 01");
private static final byte[] ZONED_SECONDS = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 31 01");
private static final byte[] ZONED_NULL = VPackWireFixtureTest.hex(
            "14 12 4d 7a 6f 6e 65 64 44 61 74 65 54 69 6d 65 18 01");
private static final byte[] ZONED_EMPTY = VPackWireFixtureTest.hex(
            "14 12 4d 7a 6f 6e 65 64 44 61 74 65 54 69 6d 65 40 01");
private static final byte[] ZONED_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 15 51 32 30 30 30 2d 30 31 2d 30 31 54 31 32 3a 30 30 5a 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] OFFSET_PLUS_HOUR = VPackWireFixtureTest.hex(
            "5a 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31");
private static final byte[] OFFSET_MINUS_HOUR = VPackWireFixtureTest.hex(
            "5a 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31");
private static final byte[] OFFSET_PLUS_COMPACT = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 30 30");
private static final byte[] OFFSET_MINUS_COMPACT = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 30 30");
private static final byte[] OFFSET_PLUS_COLON = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 3a 30 30");
private static final byte[] OFFSET_MINUS_COLON = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 3a 30 30");
private static final byte[] OFFSET_PLUS_MINUTES = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 33 30");
private static final byte[] OFFSET_MINUS_MINUTES = VPackWireFixtureTest.hex(
            "5c 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 33 30");
private static final byte[] OFFSET_PLUS_COLON_MINUTES = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2b 30 31 3a 33 30");
private static final byte[] OFFSET_MINUS_COLON_MINUTES = VPackWireFixtureTest.hex(
            "5d 32 30 31 35 2d 30 37 2d 32 34 54 31 32 3a 32 33 3a 33 34 2e 31 38 34 2d 30 31 3a 33 30");

    // Provenance: ZoneOffsetDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.DateTime,
                        cfg -> cfg.setCoercion(CoercionInputShape.EmptyString,
                                CoercionAction.Fail))
                .build();
        ObjectReader reader = mapper.readerFor(OFFSET_MAP);
        Map<String, ZoneOffset> fromNull = reader.readValue(OFFSET_NULL);
        assertNull(fromNull.get("zoneOffset"));
        assertThrows(MismatchedInputException.class, () -> reader.readValue(OFFSET_EMPTY));
    }

    // Provenance: ZoneOffsetDeserTest#testZoneOffsetDeserFromEmpty.
    void testZoneOffsetDeserFromEmptyVpack() throws Exception {
        assertNull(new VPackMapper().readValue(OFFSET_TWO_SPACES, ZoneOffset.class));
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.DateTime,
                        cfg -> cfg.setCoercion(CoercionInputShape.EmptyString,
                                CoercionAction.Fail))
                .build();
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(OFFSET_TWO_SPACES, ZoneOffset.class));
    }
static class MillisDisabledWrapper {
        @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public ZonedDateTime value;

        public MillisDisabledWrapper() { }
    }
static class NanosEnabledWrapper {
        @JsonFormat(with = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public ZonedDateTime value;

        public NanosEnabledWrapper() { }
    }

    void __invoke_testStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testZoneOffsetDeserFromEmptyVpack() throws Exception {
        try {
            testZoneOffsetDeserFromEmptyVpack();
        } finally {
        }
    }

}
