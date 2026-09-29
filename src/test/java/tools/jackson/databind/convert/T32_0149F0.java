package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0149F0 {
private static final byte[] INTEGER_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] NEG_TWO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 04 c0");
private static final byte[] THREE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 0c 40");
private static final byte[] TWENTY_TWO_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 80 36 40");
private static final byte[] NINETEEN_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 80 33 40");
private static final byte[] ONE_TWENTY_FOUR_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 20 5f 40");
private static final byte[] NINETY_FIVE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 e0 57 40");
private static final byte[] NEG_TWO_POINT_FIVE_I = VPackWireFixtureTest.hex(
            "14 0e 41 69 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] NEG_TWO_POINT_FIVE_L = VPackWireFixtureTest.hex(
            "14 0e 41 6c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] ONE_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 f8 3f 01");
private static final byte[] NEG_TWO_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 00 04 c0 01");
private static final byte[] TWENTY_TWO_POINT_FIVE_ARRAY = VPackWireFixtureTest.hex(
            "13 0c 1b 00 00 00 00 00 80 36 40 01");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper MAPPER_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_TO_NULL = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_TO_FAIL = VPackMapper.builder()
            .withCoercionConfig(LogicalType.Integer, cfg -> cfg.setCoercion(
                    CoercionInputShape.Float, CoercionAction.Fail))
            .build();
private static final EnumCoerce ENUM_DEFAULT = EnumCoerce.DEFAULT;

    void testLegacyDefaults() {
        assertFalse(DEFAULT_MAPPER.isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
    }

    void testEnumFromIntFailLegacy() throws Exception {
        assertEquals(EnumCoerce.B, DEFAULT_MAPPER.readValue(INTEGER_ONE, EnumCoerce.class));

        ObjectReader reader = DEFAULT_MAPPER.readerFor(EnumCoerce.class)
                .withFeatures(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(INTEGER_ONE));
        assertNotNull(failure.getLocation());
        assertTrue(failure.getMessage().contains("not allowed to deserialize Enum value out of"));
    }

    void testEnumFromIntAsNull() throws Exception {
        assertNull(readEnum(enumMapper(CoercionAction.AsNull), INTEGER_ONE));
        assertNull(readEnum(enumMapper(LogicalType.Enum, CoercionAction.AsNull), INTEGER_ONE));
        assertNull(readEnum(enumMapper(EnumCoerce.class, CoercionAction.AsNull), INTEGER_ONE));
    }

    void testEnumFromIntAsEmpty() throws Exception {
        assertEquals(ENUM_DEFAULT, readEnum(enumMapper(CoercionAction.AsEmpty), INTEGER_ONE));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(LogicalType.Enum, CoercionAction.AsEmpty), INTEGER_ONE));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(EnumCoerce.class, CoercionAction.AsEmpty), INTEGER_ONE));
    }

    void testEnumFromIntCoerce() throws Exception {
        assertEquals(EnumCoerce.B, readEnum(enumMapper(CoercionAction.TryConvert), INTEGER_ONE));
        assertEquals(EnumCoerce.B,
                readEnum(enumMapper(LogicalType.Enum, CoercionAction.TryConvert), INTEGER_ONE));
        assertEquals(EnumCoerce.B,
                readEnum(enumMapper(EnumCoerce.class, CoercionAction.TryConvert), INTEGER_ONE));
    }

    void testEnumFromIntFailCoercionConfig() {
        assertEnumIntegerFails(enumMapper(CoercionAction.Fail));
        assertEnumIntegerFails(enumMapper(LogicalType.Enum, CoercionAction.Fail));
        assertEnumIntegerFails(enumMapper(EnumCoerce.class, CoercionAction.Fail));
    }
private static EnumCoerce readEnum(ObjectMapper mapper, byte[] input) throws Exception {
        return mapper.readValue(input, EnumCoerce.class);
    }
private static ObjectMapper enumMapper(CoercionAction action) {
        return VPackMapper.builder().withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static ObjectMapper enumMapper(LogicalType type, CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(type, cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static ObjectMapper enumMapper(Class<?> type, CoercionAction action) {
        return VPackMapper.builder().withCoercionConfig(type, cfg -> cfg.setCoercion(
                CoercionInputShape.Integer, action)).build();
    }
private static void assertEnumIntegerFails(ObjectMapper mapper) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readEnum(mapper, INTEGER_ONE));
        assertNotNull(failure.getLocation());
        assertTrue(failure.getMessage().contains("Cannot coerce Integer value"));
        assertTrue(failure.getMessage().contains("but could if coercion was enabled"));
    }
private static void assertFloatFails(Class<?> type, byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> MAPPER_TO_FAIL.readValue(input, type));
        assertNotNull(failure.getLocation());
        assertTrue(failure.getMessage().contains("Cannot coerce Floating-point"));
    }
static class IntWrapper {
        public int i;
    }
static class LongWrapper {
        public long l;
    }
enum EnumCoerce {
        A, B, C,

        @JsonEnumDefaultValue
        DEFAULT
    }

    void __invoke_testLegacyDefaults() throws Exception {
        try {
            testLegacyDefaults();
        } finally {
        }
    }


    void __invoke_testEnumFromIntFailLegacy() throws Exception {
        try {
            testEnumFromIntFailLegacy();
        } finally {
        }
    }


    void __invoke_testEnumFromIntAsNull() throws Exception {
        try {
            testEnumFromIntAsNull();
        } finally {
        }
    }


    void __invoke_testEnumFromIntAsEmpty() throws Exception {
        try {
            testEnumFromIntAsEmpty();
        } finally {
        }
    }


    void __invoke_testEnumFromIntCoerce() throws Exception {
        try {
            testEnumFromIntCoerce();
        } finally {
        }
    }


    void __invoke_testEnumFromIntFailCoercionConfig() throws Exception {
        try {
            testEnumFromIntFailCoercionConfig();
        } finally {
        }
    }

}
