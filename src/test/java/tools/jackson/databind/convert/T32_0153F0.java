package tools.jackson.databind.convert;

import java.util.UUID;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0153F0 {
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] BLANK_STRING = VPackWireFixtureTest.hex(
            "44 20 20 20 20");
private static final byte[] TEXT_INFINITY = VPackWireFixtureTest.hex(
            "48 49 6e 66 69 6e 69 74 79");
private static final byte[] NATIVE_POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper MAPPER_EMPTY_TO_EMPTY = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_TRY_CONVERT = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.TryConvert))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_NULL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.AsNull))
            .build();
private static final ObjectMapper MAPPER_EMPTY_TO_FAIL = VPackMapper.builder()
            .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                    CoercionInputShape.EmptyString, CoercionAction.Fail))
            .build();

    void testUUIDCoercions() throws Exception {
        assertNull(DEFAULT_MAPPER.readValue(EMPTY_STRING, UUID.class));
        assertNull(MAPPER_EMPTY_TO_NULL.readValue(EMPTY_STRING, UUID.class));
        assertNull(MAPPER_EMPTY_TO_TRY_CONVERT.readValue(EMPTY_STRING, UUID.class));

        assertEquals(new UUID(0L, 0L),
                MAPPER_EMPTY_TO_EMPTY.readValue(EMPTY_STRING, UUID.class));
        assertScalarToFail(MAPPER_EMPTY_TO_FAIL, UUID.class);

        ObjectMapper failMapper = VPackMapper.builder()
                .withCoercionConfig(UUID.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        assertScalarToFail(failMapper, UUID.class);
    }

    void testStringBuilderCoercions() throws Exception {
        assertEmptyStringBuilder(DEFAULT_MAPPER.readValue(EMPTY_STRING, StringBuilder.class));
        assertEmptyStringBuilder(
                MAPPER_EMPTY_TO_EMPTY.readValue(EMPTY_STRING, StringBuilder.class));
        assertEmptyStringBuilder(
                MAPPER_EMPTY_TO_TRY_CONVERT.readValue(EMPTY_STRING, StringBuilder.class));
        assertEmptyStringBuilder(
                MAPPER_EMPTY_TO_NULL.readValue(EMPTY_STRING, StringBuilder.class));
        assertEmptyStringBuilder(
                MAPPER_EMPTY_TO_FAIL.readValue(EMPTY_STRING, StringBuilder.class));
    }
private static void assertEmptyStringBuilder(StringBuilder value) {
        assertNotNull(value);
        assertEquals(0, value.length());
    }
private static void assertScalarToFail(ObjectMapper mapper, Class<?> target) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(EMPTY_STRING, target));
        assertTrue(failure.getMessage().contains("Cannot coerce empty String"),
                failure.getMessage());
    }
private static void testPOJOFromEmptyGlobalConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsEmpty)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNotNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.TryConvert)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNull(readBean(mapper, input));
    }
private static void testPOJOFromEmptyLogicalTypeConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.POJO, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.POJO, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsEmpty)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNotNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfigDefaults(cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(allowBlank))
                .withCoercionConfig(LogicalType.POJO, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        assertPOJOCoercionFails(mapper, input);
    }
private static void testPOJOFromEmptyPhysicalTypeConfig(byte[] input, Boolean allowBlank)
            throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withCoercionConfig(Bean.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfig(Bean.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsEmpty)
                        .setAcceptBlankAsEmpty(allowBlank))
                .build();
        assertNotNull(readBean(mapper, input));

        mapper = VPackMapper.builder()
                .withCoercionConfig(LogicalType.POJO, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.AsEmpty)
                        .setAcceptBlankAsEmpty(allowBlank))
                .withCoercionConfig(Bean.class, cfg -> cfg.setCoercion(
                        CoercionInputShape.EmptyString, CoercionAction.Fail))
                .build();
        assertPOJOCoercionFails(mapper, input);
    }
private static Bean readBean(ObjectMapper mapper, byte[] input) throws Exception {
        return mapper.readerFor(Bean.class).readValue(input);
    }
private static void assertPOJOCoercionFails(ObjectMapper mapper, byte[] input) {
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> readBean(mapper, input));
        assertTrue(failure.getMessage().contains("Cannot coerce empty String"),
                failure.getMessage());
        assertNotNull(failure.getLocation());
    }
static class Bean {
        public String a;
    }

    void __invoke_testUUIDCoercions() throws Exception {
        try {
            testUUIDCoercions();
        } finally {
        }
    }


    void __invoke_testStringBuilderCoercions() throws Exception {
        try {
            testStringBuilderCoercions();
        } finally {
        }
    }

}
