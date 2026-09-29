package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0148F1 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] BLANK_STRING = VPackWireFixtureTest.hex("44 20 20 20 20");
private static final byte[] EMPTY_VALUE = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 40 01");
private static final byte[] BLANK_VALUE = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 20 01");
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final ObjectReader READER_INT_BASIC = MAPPER.readerFor(BasicIntWrapper.class);
private static final ObjectReader READER_LONG_BASIC = MAPPER.readerFor(BasicLongWrapper.class);
private static final ObjectReader READER_DOUBLE_BASIC = MAPPER.readerFor(BasicDoubleWrapper.class);
private static final EnumCoerce ENUM_DEFAULT = EnumCoerce.DEFAULT;

    void testEnumFromEmptyGlobalConfig() throws Exception {
        testEnumFromEmptyGlobalConfig(EMPTY_STRING, null);
    }

    void testEnumFromEmptyLogicalTypeConfig() throws Exception {
        testEnumFromEmptyLogicalTypeConfig(EMPTY_STRING, null);
    }

    void testEnumFromEmptyPhysicalTypeConfig() throws Exception {
        testEnumFromEmptyPhysicalTypeConfig(EMPTY_STRING, null);
    }

    void testEnumFromBlankGlobalConfig() throws Exception {
        testEnumFromEmptyGlobalConfig(BLANK_STRING, Boolean.TRUE);
    }

    void testEnumFromBlankLogicalTypeConfig() throws Exception {
        testEnumFromEmptyLogicalTypeConfig(BLANK_STRING, Boolean.TRUE);
    }

    void testEnumFromBlankPhysicalTypeConfig() throws Exception {
        testEnumFromEmptyPhysicalTypeConfig(BLANK_STRING, Boolean.TRUE);
    }
private static void testEnumFromEmptyGlobalConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        assertNull(readEnum(enumMapper(CoercionAction.AsNull, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(CoercionAction.AsEmpty, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(CoercionAction.TryConvert, allowBlank), input));
    }
private static void testEnumFromEmptyLogicalTypeConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        assertNull(readEnum(enumMapper(LogicalType.Enum, CoercionAction.AsNull, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(LogicalType.Enum, CoercionAction.AsEmpty, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(LogicalType.Enum, CoercionAction.TryConvert, allowBlank), input));

        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(allowBlank))
                .withCoercionConfig(LogicalType.Enum,
                        cfg -> cfg.setCoercion(
                                CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        assertEmptyStringFails(mapper, input);
    }
private static void testEnumFromEmptyPhysicalTypeConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        assertNull(readEnum(enumMapper(EnumCoerce.class, CoercionAction.AsNull, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(EnumCoerce.class, CoercionAction.AsEmpty, allowBlank), input));
        assertEquals(ENUM_DEFAULT,
                readEnum(enumMapper(EnumCoerce.class, CoercionAction.TryConvert, allowBlank), input));

        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.Enum,
                        cfg -> cfg.setCoercion(
                                CoercionInputShape.EmptyString, CoercionAction.AsEmpty)
                                .setAcceptBlankAsEmpty(allowBlank))
                .withCoercionConfig(EnumCoerce.class,
                        cfg -> cfg.setCoercion(
                                CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        assertEmptyStringFails(mapper, input);
    }
private static ObjectMapper enumMapper(CoercionAction action, Boolean allowBlank) {
        return VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, action)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
    }
private static ObjectMapper enumMapper(LogicalType type, CoercionAction action,
            Boolean allowBlank) {
        return VPackMapper.builder()
                .withCoercionConfig(type, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, action)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
    }
private static ObjectMapper enumMapper(Class<?> type, CoercionAction action,
            Boolean allowBlank) {
        return VPackMapper.builder()
                .withCoercionConfig(type, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, action)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
    }
private static EnumCoerce readEnum(ObjectMapper mapper, byte[] input) throws Exception {
        return mapper.readValue(input, EnumCoerce.class);
    }
private static void assertEmptyStringFails(ObjectMapper mapper, byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(input, EnumCoerce.class));
        assertNotNull(failure.getLocation());
    }
static class BasicIntWrapper {
        public int value = 13;
    }
static class BasicLongWrapper {
        public long value = 7L;
    }
static class BasicDoubleWrapper {
        public double value = -1.25;
    }
enum EnumCoerce {
        A, B, C,

        @JsonEnumDefaultValue
        DEFAULT
    }

    void __invoke_testEnumFromEmptyGlobalConfig() throws Exception {
        try {
            testEnumFromEmptyGlobalConfig();
        } finally {
        }
    }


    void __invoke_testEnumFromEmptyLogicalTypeConfig() throws Exception {
        try {
            testEnumFromEmptyLogicalTypeConfig();
        } finally {
        }
    }


    void __invoke_testEnumFromEmptyPhysicalTypeConfig() throws Exception {
        try {
            testEnumFromEmptyPhysicalTypeConfig();
        } finally {
        }
    }


    void __invoke_testEnumFromBlankGlobalConfig() throws Exception {
        try {
            testEnumFromBlankGlobalConfig();
        } finally {
        }
    }


    void __invoke_testEnumFromBlankLogicalTypeConfig() throws Exception {
        try {
            testEnumFromBlankLogicalTypeConfig();
        } finally {
        }
    }


    void __invoke_testEnumFromBlankPhysicalTypeConfig() throws Exception {
        try {
            testEnumFromBlankPhysicalTypeConfig();
        } finally {
        }
    }

}
