package tools.jackson.databind.deser.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0227Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] OK = VPackWireFixtureTest.hex("42 4f 4b");
private static final byte[] RULES = VPackWireFixtureTest.hex(
            "45 52 55 4c 45 53");
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] JACKSON = VPackWireFixtureTest.hex(
            "47 4a 41 43 4b 53 4f 4e");
private static final byte[] INDEX_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] INDEX_TWO = VPackWireFixtureTest.hex("32");
private static final byte[] INDEX_THREE = VPackWireFixtureTest.hex("33");
private static final byte[] QUOTED_ONE = VPackWireFixtureTest.hex("41 31");
private static final byte[] QUOTED_THREE = VPackWireFixtureTest.hex("41 33");
private static final byte[] UNKNOWN = VPackWireFixtureTest.hex(
            "4d 4e 4f 2d 53 55 43 48 2d 56 41 4c 55 45");
private static final byte[] FOO = VPackWireFixtureTest.hex("43 66 6f 6f");
private static final byte[] BAR = VPackWireFixtureTest.hex("43 62 61 72");
private static final byte[] LOWER_C = VPackWireFixtureTest.hex("41 63");
private static final byte[] WRAPPED_JACKSON = VPackWireFixtureTest.hex(
            "02 0a 47 4a 41 43 4b 53 4f 4e");
private static final byte[] ENUM_SET_BAR = VPackWireFixtureTest.hex(
            "13 07 43 62 61 72 01");
private static final byte[] ENUM_MAP_FOO = VPackWireFixtureTest.hex(
            "14 09 43 66 6f 6f 28 0d 01");
private static final byte[] CUSTOM_KEY_FAILURE = VPackWireFixtureTest.hex(
            "14 10 44 54 57 4f 48 64 75 6d 70 6c 69 6e 67 01");

    // Provenance: EnumDeserializationTest#testEnumsWithJsonValue.
    void testEnumsWithJsonValueVpack() throws Exception {
        assertSame(EnumWithJsonValue.A, MAPPER.readValue(FOO,
                EnumWithJsonValue.class));
        assertSame(EnumWithJsonValue.B, MAPPER.readValue(BAR,
                EnumWithJsonValue.class));

        EnumSet<EnumWithJsonValue> set = MAPPER.readValue(ENUM_SET_BAR,
                new TypeReference<EnumSet<EnumWithJsonValue>>() { });
        assertNotNull(set);
        assertEquals(1, set.size());
        assertTrue(set.contains(EnumWithJsonValue.B));
        assertFalse(set.contains(EnumWithJsonValue.A));

        EnumMap<EnumWithJsonValue, Integer> map = MAPPER.readValue(ENUM_MAP_FOO,
                new TypeReference<EnumMap<EnumWithJsonValue, Integer>>() { });
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals(Integer.valueOf(13), map.get(EnumWithJsonValue.A));
    }

    // Provenance: EnumDeserializationTest#testExceptionFromCustomEnumKeyDeserializer.
    void testExceptionFromCustomEnumKeyDeserializerVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new EnumModule())
                .build();
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(CUSTOM_KEY_FAILURE,
                        new TypeReference<Map<AnEnum, String>>() { }));
        assertTrue(exception.getMessage().contains("Undefined AnEnum"));
    }

    // Provenance: EnumDeserializationTest#testGenericEnumDeserialization.
    void testGenericEnumDeserializationVpack() throws Exception {
        SimpleModule module = new SimpleModule("foobar");
        module.addDeserializer(Enum.class, new LowerCaseEnumDeserializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertEquals(TestEnum.JACKSON, mapper.readValue(
                VPackWireFixtureTest.hex("47 6a 61 63 6b 73 6f 6e"),
                TestEnum.class));
    }

    // Provenance: EnumDeserializationTest#testIndexAsString.
    void testIndexAsStringVpack() throws Exception {
        assertSame(TestEnum.OK, MAPPER.readValue(INDEX_TWO, TestEnum.class));
        assertSame(TestEnum.RULES, MAPPER.readValue(QUOTED_ONE, TestEnum.class));

        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> VPackMapper.builder()
                        .disable(tools.jackson.databind.MapperFeature.ALLOW_COERCION_OF_SCALARS)
                        .build()
                        .readerFor(TestEnum.class)
                        .readValue(QUOTED_ONE));
        assertTrue(exception.getMessage().contains("quoted Enum index"));
    }

    // Provenance: EnumDeserializationTest#testIssue3006.
    void testIssue3006Vpack() throws Exception {
        assertEquals(Operation3006.ONE, MAPPER.readValue(INDEX_ONE,
                Operation3006.class));
        assertEquals(Operation3006.ONE, MAPPER.readValue(QUOTED_ONE,
                Operation3006.class));
        assertEquals(Operation3006.THREE, MAPPER.readValue(INDEX_THREE,
                Operation3006.class));
        assertEquals(Operation3006.THREE, MAPPER.readValue(QUOTED_THREE,
                Operation3006.class));
    }

    // Provenance: EnumDeserializationTest#testNumbersToEnums.
    void testNumbersToEnumsVpack() throws Exception {
        assertFalse(MAPPER.deserializationConfig()
                .isEnabled(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS));
        assertSame(TestEnum.RULES, MAPPER.readValue(INDEX_ONE, TestEnum.class));

        ObjectReader reader = MAPPER.readerFor(TestEnum.class)
                .with(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS);
        MismatchedInputException numeric = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(INDEX_ONE));
        assertTrue(numeric.getMessage().contains("Cannot deserialize"));
        MismatchedInputException quoted = assertThrows(MismatchedInputException.class,
                () -> reader.readValue(QUOTED_ONE));
        assertTrue(quoted.getMessage().contains("Cannot deserialize"));
    }

    // Provenance: EnumDeserializationTest#testSimple.
    void testSimpleVpack() throws Exception {
        byte[] sequence = new byte[OK.length + RULES.length + NULL.length];
        System.arraycopy(OK, 0, sequence, 0, OK.length);
        System.arraycopy(RULES, 0, sequence, OK.length, RULES.length);
        System.arraycopy(NULL, 0, sequence, OK.length + RULES.length, NULL.length);
        ObjectMapper sequenceMapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        try (JsonParser parser = sequenceMapper.createParser(sequence)) {
            assertSame(TestEnum.OK, sequenceMapper.readValue(parser, TestEnum.class));
            assertSame(TestEnum.RULES, sequenceMapper.readValue(parser, TestEnum.class));
            assertNull(sequenceMapper.readValue(parser, TestEnum.class));
            assertFalse(parser.hasCurrentToken());
        }

        assertSame(TestEnum.JACKSON, MAPPER.readValue(
                VPackWireFixtureTest.hex("30"), TestEnum.class));
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(UNKNOWN, TestEnum.class));
        assertTrue(exception.getMessage().contains("not one of the values accepted"));
    }

    // Provenance: EnumDeserializationTest#testSubclassedEnums.
    void testSubclassedEnumsVpack() throws Exception {
        assertEquals(EnumWithSubClass.A,
                MAPPER.readValue(VPackWireFixtureTest.hex("41 41"),
                        EnumWithSubClass.class));
    }

    // Provenance: EnumDeserializationTest#testToStringEnums.
    void testToStringEnumsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .build();
        assertEquals(LowerCaseEnum.C, mapper.readValue(LOWER_C,
                LowerCaseEnum.class));
    }

    // Provenance: EnumDeserializationTest#testUnwrappedEnum.
    void testUnwrappedEnumVpack() throws Exception {
        assertEquals(TestEnum.JACKSON,
                MAPPER.readerFor(TestEnum.class)
                        .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                        .readValue(WRAPPED_JACKSON));
    }

    // Provenance: EnumDeserializationTest#testUnwrappedEnumException.
    void testUnwrappedEnumExceptionVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        MismatchedInputException exception = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(WRAPPED_JACKSON, TestEnum.class));
        assertTrue(exception.getMessage().contains("Cannot deserialize"));
    }

    // Provenance: EnumDeserializationTest#testWrapExceptions.
    void testWrapExceptionsVpack() {
        ValueInstantiationException wrapped = assertThrows(ValueInstantiationException.class,
                () -> MAPPER.readerFor(TestEnum2164.class).readValue(
                        VPackWireFixtureTest.hex("41 42")));
        assertTrue(wrapped.getMessage().contains("2164"));

        IllegalArgumentException unwrapped = assertThrows(IllegalArgumentException.class,
                () -> MAPPER.readerFor(TestEnum2164.class)
                        .without(DeserializationFeature.WRAP_EXCEPTIONS)
                        .readValue(VPackWireFixtureTest.hex("41 42")));
        assertTrue(unwrapped.getMessage().contains("2164"));
    }
enum TestEnum { JACKSON, RULES, OK }
enum LowerCaseEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum EnumWithJsonValue {
        A("foo"), B("bar");

        private final String value;

        EnumWithJsonValue(String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return value;
        }
    }
enum EnumWithSubClass {
        A,
        B {
            @Override
            public String toString() {
                return "b";
            }
        }
    }
enum Operation3006 {
        ONE(1L), TWO(2L), THREE(3L);

        private static final Map<Long, Operation3006> MAPPING = Map.of(
                1L, ONE, 2L, TWO, 3L, THREE);
        private final long id;

        Operation3006(long id) {
            this.id = id;
        }

        @JsonCreator
        public static Operation3006 forValue(String id) {
            Operation3006 result = MAPPING.get(Long.parseLong(id));
            if (result == null) {
                throw new IllegalArgumentException("Unable to find: " + id);
            }
            return result;
        }
    }
enum TestEnum2164 {
        A, B;

        @JsonCreator
        public static TestEnum2164 fromString(String input) {
            throw new IllegalArgumentException("2164");
        }
    }
static class LowerCaseEnumDeserializer
            extends tools.jackson.databind.deser.std.StdDeserializer<TestEnum> {
        LowerCaseEnumDeserializer() {
            super(TestEnum.class);
        }

        @Override
        public TestEnum deserialize(JsonParser parser,
                tools.jackson.databind.DeserializationContext context) {
            return TestEnum.valueOf(parser.getString().toUpperCase());
        }
    }
enum AnEnum { ZERO, ONE }
static class AnEnumKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key,
                tools.jackson.databind.DeserializationContext context) {
            try {
                return AnEnum.valueOf(key);
            } catch (IllegalArgumentException exception) {
                return context.handleWeirdKey(AnEnum.class, key,
                        "Undefined AnEnum code");
            }
        }
    }
@JsonDeserialize(keyUsing = AnEnumKeyDeserializer.class)
    static class AnEnumMixin { }
static class EnumModule extends SimpleModule {
        @Override
        public void setupModule(SetupContext context) {
            context.setMixIn(AnEnum.class, AnEnumMixin.class);
        }
    }

    void __invoke_testEnumsWithJsonValueVpack() throws Exception {
        try {
            testEnumsWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testExceptionFromCustomEnumKeyDeserializerVpack() throws Exception {
        try {
            testExceptionFromCustomEnumKeyDeserializerVpack();
        } finally {
        }
    }


    void __invoke_testGenericEnumDeserializationVpack() throws Exception {
        try {
            testGenericEnumDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testIndexAsStringVpack() throws Exception {
        try {
            testIndexAsStringVpack();
        } finally {
        }
    }


    void __invoke_testIssue3006Vpack() throws Exception {
        try {
            testIssue3006Vpack();
        } finally {
        }
    }


    void __invoke_testNumbersToEnumsVpack() throws Exception {
        try {
            testNumbersToEnumsVpack();
        } finally {
        }
    }


    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }


    void __invoke_testSubclassedEnumsVpack() throws Exception {
        try {
            testSubclassedEnumsVpack();
        } finally {
        }
    }


    void __invoke_testToStringEnumsVpack() throws Exception {
        try {
            testToStringEnumsVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedEnumVpack() throws Exception {
        try {
            testUnwrappedEnumVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedEnumExceptionVpack() throws Exception {
        try {
            testUnwrappedEnumExceptionVpack();
        } finally {
        }
    }


    void __invoke_testWrapExceptionsVpack() throws Exception {
        try {
            testWrapExceptionsVpack();
        } finally {
        }
    }

}
