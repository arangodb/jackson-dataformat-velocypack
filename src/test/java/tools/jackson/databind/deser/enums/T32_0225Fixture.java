package tools.jackson.databind.deser.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.InvalidFormatException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0225Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .build();
private static final byte[] SECONDS = VPackWireFixtureTest.hex(
            "47 53 45 43 4f 4e 44 53");
private static final byte[] JACKSON = VPackWireFixtureTest.hex(
            "47 4a 41 43 4b 53 4f 4e");
private static final byte[] UNKNOWN = VPackWireFixtureTest.hex(
            "4d 4e 4f 2d 53 55 43 48 2d 56 41 4c 55 45");
private static final byte[] INTEGER_4343 = VPackWireFixtureTest.hex(
            "29 f7 10");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] YES = VPackWireFixtureTest.hex(
            "43 79 65 73");
private static final byte[] UNKNOWN_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 1c 43 6d 61 70 14 15 4d 4e 4f 2d 53 55 43 48 2d 56 41 4c 55 45 "
            + "43 76 61 6c 01 01");
private static final byte[] INDEX_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 1a 43 6d 61 70 14 13 41 30 4d 49 20 41 4d 20 46 4f 52 20 52 45 41 4c "
            + "01 01");

    // Provenance: EnumDeserializationTest#testComplexEnum.
    void testComplexEnumVpack() throws Exception {
        assertArrayEquals(SECONDS, MAPPER.writeValueAsBytes(TimeUnit.SECONDS));
        assertSame(TimeUnit.SECONDS, MAPPER.readValue(SECONDS, TimeUnit.class));
    }

    // Provenance: EnumDeserializationTest#testAnnotated.
    void testAnnotatedVpack() throws Exception {
        assertEquals(AnnotatedTestEnum.OK,
                MAPPER.readValue(JACKSON, AnnotatedTestEnum.class));
    }

    // Provenance: EnumDeserializationTest#testAllowUnknownEnumValuesReadAsNull.
    void testAllowUnknownEnumValuesReadAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.reader(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertNull(reader.forType(TestEnum.class).readValue(UNKNOWN));
        assertNull(reader.forType(TestEnum.class).readValue(INTEGER_4343));
    }

    // Provenance: EnumDeserializationTest#testAllowUnknownEnumValuesReadAsNullWithCreatorMethod.
    void testAllowUnknownEnumValuesReadAsNullWithCreatorMethodVpack() throws Exception {
        ObjectReader reader = MAPPER.reader(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertNull(reader.forType(StrictEnumCreator.class).readValue(UNKNOWN));
        assertNull(reader.forType(StrictEnumCreator.class).readValue(INTEGER_4343));
    }

    // Provenance: EnumDeserializationTest#testAllowUnknownEnumValuesReadAsDefaultWithCreatorMethod4979.
    void testAllowUnknownEnumValuesReadAsDefaultWithCreatorMethod4979Vpack()
            throws Exception {
        ObjectReader reader = MAPPER.reader(
                EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        assertEquals(StrictEnumCreator.UNKNOWN,
                reader.forType(StrictEnumCreator.class).readValue(UNKNOWN));
    }

    // Provenance: EnumDeserializationTest#testDoNotAllowUnknownEnumValuesAsMapKeysWhenReadAsNullDisabled.
    void testDoNotAllowUnknownEnumValuesAsMapKeysWhenReadAsNullDisabledVpack() {
        assertFalse(MAPPER.isEnabled(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL));
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(UNKNOWN_ENUM_MAP_FIELD, ClassWithEnumMapKey.class));
        assertTrue(exception.getMessage().contains("not one of the values accepted"));
    }

    // Provenance: EnumDeserializationTest#testEnumFeature_EnumIndexAsKey.
    void testEnumFeature_EnumIndexAsKeyVpack() throws Exception {
        ClassWithEnumMapKey result = MAPPER.reader()
                .forType(ClassWithEnumMapKey.class)
                .with(EnumFeature.READ_ENUM_KEYS_USING_INDEX)
                .readValue(INDEX_ENUM_MAP_FIELD);
        assertEquals("I AM FOR REAL", result.map.get(TestEnum.JACKSON));
    }

    // Provenance: EnumDeserializationTest#testEnumFeature_symmetric_to_writing.
    void testEnumFeature_symmetric_to_writingVpack() throws Exception {
        ClassWithEnumMapKey input = new ClassWithEnumMapKey();
        input.map = new HashMap<>();
        input.map.put(TestEnum.JACKSON, "I AM FOR REAL");

        byte[] encoded = MAPPER.writer()
                .with(EnumFeature.WRITE_ENUM_KEYS_USING_INDEX)
                .writeValueAsBytes(input);
        ClassWithEnumMapKey result = MAPPER.reader()
                .forType(ClassWithEnumMapKey.class)
                .with(EnumFeature.READ_ENUM_KEYS_USING_INDEX)
                .readValue(encoded);

        assertTrue(input != result);
        assertTrue(input.map != result.map);
        assertEquals("I AM FOR REAL", result.map.get(TestEnum.JACKSON));
    }

    // Provenance: EnumDeserializationTest#testEnumFeature_READ_ENUM_KEYS_USING_INDEX_isDisabledByDefault.
    void testEnumFeature_READ_ENUM_KEYS_USING_INDEX_isDisabledByDefaultVpack() {
        ObjectReader reader = MAPPER.reader();
        assertFalse(reader.isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
        assertFalse(reader.without(EnumFeature.READ_ENUM_KEYS_USING_INDEX)
                .isEnabled(EnumFeature.READ_ENUM_KEYS_USING_INDEX));
    }

    // Provenance: EnumDeserializationTest#testEnumReadFromEmptyString.
    void testEnumReadFromEmptyStringVpack() throws Exception {
        assertEquals(YesOrNoOrEmpty.EMPTY,
                MAPPER.readValue(EMPTY_STRING, YesOrNoOrEmpty.class));
        assertEquals(YesOrNoOrEmpty.YES, MAPPER.readValue(YES, YesOrNoOrEmpty.class));
    }

    // Provenance: EnumDeserializationTest#testEnumToStringNull2309.
    void testEnumToStringNull2309Vpack() throws Exception {
        Enum2309 value = MAPPER.readerFor(Enum2309.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(VPackWireFixtureTest.hex(
                        "48 4e 4f 4e 5f 4e 55 4c 4c"));
        assertEquals(Enum2309.NON_NULL, value);
    }

    // Provenance: EnumDeserializationTest#testDeserWithToString1161.
    void testDeserWithToString1161Vpack() throws Exception {
        Enum1161 result = MAPPER.readerFor(Enum1161.class)
                .without(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(JACKSON_A);
        assertSame(Enum1161.A, result);

        result = MAPPER.readerFor(Enum1161.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(LOWER_A);
        assertSame(Enum1161.A, result);

        result = MAPPER.readerFor(Enum1161.class)
                .without(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .readValue(JACKSON_A);
        assertSame(Enum1161.A, result);
    }
private static final byte[] JACKSON_A = VPackWireFixtureTest.hex("41 41");
private static final byte[] LOWER_A = VPackWireFixtureTest.hex("41 61");
@JsonDeserialize(using = DummyDeserializer.class)
    enum AnnotatedTestEnum {
        JACKSON, RULES, OK
    }
static class DummyDeserializer extends StdDeserializer<Object> {
        DummyDeserializer() {
            super(Object.class);
        }

        @Override
        public Object deserialize(JsonParser parser, DeserializationContext context) {
            return AnnotatedTestEnum.OK;
        }
    }
enum TestEnum { JACKSON, RULES, OK }
enum StrictEnumCreator {
        A, B, @JsonEnumDefaultValue UNKNOWN;

        @JsonCreator
        public static StrictEnumCreator fromId(String value) {
            for (StrictEnumCreator candidate : values()) {
                if (candidate.name().toLowerCase().equals(value)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException(value);
        }
    }
enum Enum1161 {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum Enum2309 {
        NON_NULL("NON_NULL"),
        NULL(null),
        OTHER("OTHER");

        private final String value;

        Enum2309(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
enum YesOrNoOrEmpty {
        @JsonProperty("")
        EMPTY,
        @JsonProperty("yes")
        YES,
        @JsonProperty("no")
        NO
    }
static class ClassWithEnumMapKey {
        @JsonProperty
        Map<TestEnum, String> map;
    }

    void __invoke_testComplexEnumVpack() throws Exception {
        try {
            testComplexEnumVpack();
        } finally {
        }
    }


    void __invoke_testAnnotatedVpack() throws Exception {
        try {
            testAnnotatedVpack();
        } finally {
        }
    }


    void __invoke_testAllowUnknownEnumValuesReadAsNullVpack() throws Exception {
        try {
            testAllowUnknownEnumValuesReadAsNullVpack();
        } finally {
        }
    }


    void __invoke_testAllowUnknownEnumValuesReadAsNullWithCreatorMethodVpack() throws Exception {
        try {
            testAllowUnknownEnumValuesReadAsNullWithCreatorMethodVpack();
        } finally {
        }
    }


    void __invoke_testAllowUnknownEnumValuesReadAsDefaultWithCreatorMethod4979Vpack() throws Exception {
        try {
            testAllowUnknownEnumValuesReadAsDefaultWithCreatorMethod4979Vpack();
        } finally {
        }
    }


    void __invoke_testDoNotAllowUnknownEnumValuesAsMapKeysWhenReadAsNullDisabledVpack() throws Exception {
        try {
            testDoNotAllowUnknownEnumValuesAsMapKeysWhenReadAsNullDisabledVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_EnumIndexAsKeyVpack() throws Exception {
        try {
            testEnumFeature_EnumIndexAsKeyVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_symmetric_to_writingVpack() throws Exception {
        try {
            testEnumFeature_symmetric_to_writingVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_READ_ENUM_KEYS_USING_INDEX_isDisabledByDefaultVpack() throws Exception {
        try {
            testEnumFeature_READ_ENUM_KEYS_USING_INDEX_isDisabledByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testEnumReadFromEmptyStringVpack() throws Exception {
        try {
            testEnumReadFromEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumToStringNull2309Vpack() throws Exception {
        try {
            testEnumToStringNull2309Vpack();
        } finally {
        }
    }


    void __invoke_testDeserWithToString1161Vpack() throws Exception {
        try {
            testDeserWithToString1161Vpack();
        } finally {
        }
    }

}
