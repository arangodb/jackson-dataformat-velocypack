package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0306Fixture {
private static final byte[] THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT_MILLIS =
            VPackWireFixtureTest.hex("2b 90 f6 cd 00");
private static final byte[] PT_ONE_MINUTE = VPackWireFixtureTest.hex(
            "44 50 54 31 4d");
private static final byte[] PT_THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT =
            VPackWireFixtureTest.hex(
                    "54 50 54 33 48 34 34 4d 35 38 2e 30 30 30 30 30 38 33 37 34 53");
private static final byte[] TYPED_DURATION_AS_DECIMAL = VPackWireFixtureTest.hex(
            "06 25 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "c8 07 f7 ff ff ff 13 49 80 00 00 83 74 03 16");
private static final byte[] TYPED_DURATION_AS_NANOS = VPackWireFixtureTest.hex(
            "06 1b 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "29 ba 34 03 16");
private static final byte[] TYPED_DURATION_AS_MILLIS = VPackWireFixtureTest.hex(
            "06 1d 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "2b d5 f9 cd 00 03 16");
private static final byte[] TYPED_DURATION_AS_STRING = VPackWireFixtureTest.hex(
            "06 2d 02 52 6a 61 76 61 2e 74 69 6d 65 2e 44 75 72 61 74 69 6f 6e "
          + "54 50 54 33 48 34 34 4d 35 38 2e 30 30 30 30 30 38 33 37 34 53 03 16");
private static final byte[] MAP_WITH_NULL_DURATION = VPackWireFixtureTest.hex(
            "0b 0e 01 48 64 75 72 61 74 69 6f 6e 18 03");
private static final byte[] MAP_WITH_EMPTY_DURATION = VPackWireFixtureTest.hex(
            "0b 0e 01 48 64 75 72 61 74 69 6f 6e 40 03");
private static final byte[] WRAPPER_WITH_NANOS_DURATION = VPackWireFixtureTest.hex(
            "0b 0d 01 45 76 61 6c 75 65 29 ba 34 03");
private static final byte[] WRAPPER_WITH_MILLIS_DURATION = VPackWireFixtureTest.hex(
            "0b 0f 01 45 76 61 6c 75 65 2b 90 f6 cd 00 03");
private static final TypeReference<Map<String, Duration>> MAP_TYPE =
            new TypeReference<Map<String, Duration>>() { };

    // Provenance: DurationDeserTest#testDeserializationAsInt04.
    void testDeserializationAsInt04Vpack() throws Exception {
        Duration value = reader().without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT_MILLIS);
        assertEquals(Duration.ofSeconds(13498L), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsInt05.
    void testDeserializationAsInt05Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(WrapperWithReadTimestampsAsNanosEnabled.class);
        WrapperWithReadTimestampsAsNanosEnabled actual = reader.readValue(WRAPPER_WITH_NANOS_DURATION);
        assertEquals(Duration.ofSeconds(13498L), actual.value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsInt06.
    void testDeserializationAsInt06Vpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(WrapperWithReadTimestampsAsNanosDisabled.class);
        WrapperWithReadTimestampsAsNanosDisabled actual = reader.readValue(WRAPPER_WITH_MILLIS_DURATION);
        assertEquals(Duration.ofSeconds(13498L), actual.value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        assertEquals(Duration.ofSeconds(60L), new VPackMapper().readValue(PT_ONE_MINUTE, Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationAsString02.
    void testDeserializationAsString02Vpack() throws Exception {
        assertEquals(Duration.ofSeconds(13498L, 8374),
                new VPackMapper().readValue(PT_THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT,
                        Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationAsString03.
    void testDeserializationAsString03Vpack() throws Exception {
        assertNull(new VPackMapper().readValue(VPackWireFixtureTest.hex("43 20 20 20"), Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        TemporalAmount value = typeInfoMapper().readerFor(TemporalAmount.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPED_DURATION_AS_DECIMAL);
        assertInstanceOf(Duration.class, value);
        assertEquals(Duration.ofSeconds(13498L, 8374), value);
    }

    // Provenance: DurationDeserTest#testDeserializationWithTypeInfo02.
    void testDeserializationWithTypeInfo02Vpack() throws Exception {
        TemporalAmount value = typeInfoMapper().readerFor(TemporalAmount.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPED_DURATION_AS_NANOS);
        assertInstanceOf(Duration.class, value);
        assertEquals(Duration.ofSeconds(13498L), value);
    }

    // Provenance: DurationDeserTest#testDeserializationWithTypeInfo03.
    void testDeserializationWithTypeInfo03Vpack() throws Exception {
        TemporalAmount value = typeInfoMapper().readerFor(TemporalAmount.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(TYPED_DURATION_AS_MILLIS);
        assertInstanceOf(Duration.class, value);
        assertEquals(Duration.ofSeconds(13498L, 837000000), value);
    }

    // Provenance: DurationDeserTest#testDeserializationWithTypeInfo04.
    void testDeserializationWithTypeInfo04Vpack() throws Exception {
        TemporalAmount value = typeInfoMapper().readerFor(TemporalAmount.class)
                .readValue(TYPED_DURATION_AS_STRING);
        assertInstanceOf(Duration.class, value);
        assertEquals(Duration.ofSeconds(13498L, 8374), value);
    }

    // Provenance: DurationDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(MAP_TYPE);
        Map<String, Duration> nullMap = reader.readValue(MAP_WITH_NULL_DURATION);
        Map<String, Duration> emptyMap = reader.readValue(MAP_WITH_EMPTY_DURATION);
        assertNull(nullMap.get("duration"));
        assertNull(emptyMap.get("duration"));
    }

    // Provenance: DurationDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Duration.class,
                        o -> o.setFormat(com.fasterxml.jackson.annotation.JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(MAP_TYPE);
        Map<String, Duration> nullMap = reader.readValue(MAP_WITH_NULL_DURATION);
        assertNull(nullMap.get("duration"));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(MAP_WITH_EMPTY_DURATION));
    }
private static ObjectReader reader() {
        return new VPackMapper().readerFor(Duration.class);
    }
private static VPackMapper typeInfoMapper() {
        return VPackMapper.builder()
                .addMixIn(TemporalAmount.class, DurationTypeInfo.class)
                .build();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface DurationTypeInfo { }
static final class WrapperWithReadTimestampsAsNanosEnabled {
        @com.fasterxml.jackson.annotation.JsonFormat(with = com.fasterxml.jackson.annotation.JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public Duration value;
    }
static final class WrapperWithReadTimestampsAsNanosDisabled {
        @com.fasterxml.jackson.annotation.JsonFormat(without = com.fasterxml.jackson.annotation.JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        public Duration value;
    }

    void __invoke_testDeserializationAsInt04Vpack() throws Exception {
        try {
            testDeserializationAsInt04Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt05Vpack() throws Exception {
        try {
            testDeserializationAsInt05Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsInt06Vpack() throws Exception {
        try {
            testDeserializationAsInt06Vpack();
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


    void __invoke_testDeserializationAsString03Vpack() throws Exception {
        try {
            testDeserializationAsString03Vpack();
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


    void __invoke_testLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testLenientDeserializeFromEmptyStringVpack();
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
