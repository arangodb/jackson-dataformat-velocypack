package tools.jackson.databind.ext.javatime.deser;

import java.time.OffsetTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0332F1 {
private static final TypeReference<Map<String, OffsetTime>> OFFSET_TIME_MAP =
            new TypeReference<Map<String, OffsetTime>>() { };
private static final TypeReference<Map<String, Period>> PERIOD_MAP =
            new TypeReference<Map<String, Period>>() { };
private static final byte[] OFFSET_TIME_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 4f 66 66 73 65 74 54 69 6d 65 18 01");
private static final byte[] OFFSET_TIME_EMPTY = VPackWireFixtureTest.hex(
            "14 0f 4a 4f 66 66 73 65 74 54 69 6d 65 40 01");
private static final byte[] STRICT_PATTERN_INVALID = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 4e 31 35 3a 33 30 3a 34 35 2b 30 32 3a 30 30 01");
private static final byte[] PERIOD_NULL = VPackWireFixtureTest.hex(
            "14 0b 46 70 65 72 69 6f 64 18 01");
private static final byte[] PERIOD_EMPTY = VPackWireFixtureTest.hex(
            "14 0b 46 70 65 72 69 6f 64 40 01");
private static final byte[] PERIOD_01 = VPackWireFixtureTest.hex(
            "48 50 31 59 36 4d 31 35 44");
private static final byte[] PERIOD_02 = VPackWireFixtureTest.hex(
            "44 50 32 31 44");
private static final byte[] PERIOD_TYPE_INFO = VPackWireFixtureTest.hex(
            "13 1d 50 6a 61 76 61 2e 74 69 6d 65 2e 50 65 72 69 6f 64 "
          + "48 50 35 59 31 4d 31 32 44 02");
private static final byte[] YEAR_INVALID = VPackWireFixtureTest.hex(
            "48 6e 6f 74 61 79 65 61 72");
private static final byte[] YEAR_1986 = VPackWireFixtureTest.hex("29 c2 07");
private static final byte[] YEAR_2013 = VPackWireFixtureTest.hex("29 dd 07");
private static final byte[] YEAR_SINGLE_ARRAY = VPackWireFixtureTest.hex(
            "13 08 44 32 30 30 30 01");
private static final byte[] YEAR_TYPE_INFO = VPackWireFixtureTest.hex(
            "13 15 4e 6a 61 76 61 2e 74 69 6d 65 2e 59 65 61 72 29 cd 07 02");

    // Provenance: OffsetTimeDeserTest#testLenientDeserializeFromEmptyString.
    void testLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(OFFSET_TIME_MAP);
        Map<String, OffsetTime> fromNull = reader.readValue(OFFSET_TIME_NULL);
        Map<String, OffsetTime> fromEmpty = reader.readValue(OFFSET_TIME_EMPTY);
        assertNull(fromNull.get("OffsetTime"));
        assertNull(fromEmpty.get("OffsetTime"),
                "empty string failed to deserialize to null with lenient setting");
    }

    // Provenance: OffsetTimeDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(OffsetTime.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        ObjectReader reader = mapper.readerFor(OFFSET_TIME_MAP);
        Map<String, OffsetTime> fromNull = reader.readValue(OFFSET_TIME_NULL);
        assertNull(fromNull.get("OffsetTime"));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(OFFSET_TIME_EMPTY));
    }

    // Provenance: PeriodDeserTest#testDeserialization01.
    void testDeserialization01Vpack() throws Exception {
        assertEquals(Period.of(1, 6, 15),
                new VPackMapper().readValue(PERIOD_01, Period.class));
    }

    // Provenance: PeriodDeserTest#testDeserialization02.
    void testDeserialization02Vpack() throws Exception {
        assertEquals(Period.of(0, 0, 21),
                new VPackMapper().readValue(PERIOD_02, Period.class));
    }

    // Provenance: PeriodDeserTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(TemporalAmount.class, TemporalAmountTypeInfo.class)
                .build();
        TemporalAmount value = mapper.readValue(PERIOD_TYPE_INFO, TemporalAmount.class);
        assertEquals(Period.of(5, 1, 12), assertInstanceOf(Period.class, value));
    }

    // Provenance: PeriodDeserTest#testLenientDeserializeFromEmptyString.
    void testPeriodLenientDeserializeFromEmptyStringVpack() throws Exception {
        ObjectReader reader = new VPackMapper().readerFor(PERIOD_MAP);
        Map<String, Period> fromNull = reader.readValue(PERIOD_NULL);
        Map<String, Period> fromEmpty = reader.readValue(PERIOD_EMPTY);
        assertNull(fromNull.get("period"));
        assertNull(fromEmpty.get("period"),
                "empty string failed to deserialize to null with lenient setting");
    }

    // Provenance: PeriodDeserTest#testStrictDeserializeFromEmptyString.
    void testPeriodStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.DateTime,
                        cfg -> cfg.setCoercion(CoercionInputShape.EmptyString,
                                CoercionAction.Fail))
                .build();
        ObjectReader reader = mapper.readerFor(PERIOD_MAP);
        Map<String, Period> fromNull = reader.readValue(PERIOD_NULL);
        assertNull(fromNull.get("period"));
        assertThrows(MismatchedInputException.class,
                () -> reader.readValue(PERIOD_EMPTY));
    }
static class StrictOffsetTimeWrapper {
        @JsonFormat(pattern = "HH:mmXXX", lenient = OptBoolean.FALSE)
        public OffsetTime value;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface TemporalAmountTypeInfo { }

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


    void __invoke_testDeserialization01Vpack() throws Exception {
        try {
            testDeserialization01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserialization02Vpack() throws Exception {
        try {
            testDeserialization02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testPeriodLenientDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testPeriodLenientDeserializeFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testPeriodStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testPeriodStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
