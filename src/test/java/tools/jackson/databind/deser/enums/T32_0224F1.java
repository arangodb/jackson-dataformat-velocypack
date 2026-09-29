package tools.jackson.databind.deser.enums;

import java.util.EnumSet;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidNullException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0224F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] UNKNOWN_ENUM_MAP_ENTRY = VPackWireFixtureTest.hex(
            "14 1a 4f 55 4e 6b 6e 6f 77 6e 57 68 61 74 45 76 65 72 "
            + "46 66 72 65 73 68 21 01");
private static final byte[] UNKNOWN_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 1c 43 6d 61 70 14 15 4d 4e 4f 2d 53 55 43 48 2d 56 41 4c 55 45 "
            + "43 76 61 6c 01 01");
private static final byte[] MIXED_CASE_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 47 4a 41 43 6b 73 6f 6e 43 76 61 6c 01 01");
private static final byte[] UNKNOWN_ENUM_SET = VPackWireFixtureTest.hex(
            "13 11 4d 4e 4f 2d 53 55 43 48 2d 56 41 4c 55 45 01");

    // Provenance: EnumDeserializationTest#testAllowUnknownEnumValuesAsMapKeysReadAsNull.
    void testAllowUnknownEnumValuesAsMapKeysReadAsNullVpack() throws Exception {
        ClassWithEnumMapKey result = MAPPER.reader(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .forType(ClassWithEnumMapKey.class)
                .readValue(UNKNOWN_ENUM_MAP_FIELD);

        assertEquals(0, result.map.size());
    }

    // Provenance: EnumDeserializationTest#testAllowUnknownEnumValuesForEnumSets.
    void testAllowUnknownEnumValuesForEnumSetsVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> MAPPER.reader(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                        .forType(new TypeReference<EnumSet<TestEnum>>() { })
                        .readValue(UNKNOWN_ENUM_SET));
        assertTrue(exception.getMessage().contains("Invalid `null` value encountered"));
    }

    // Provenance: EnumDeserializationTest#testAllowCaseInsensitiveEnumValues.
    void testAllowCaseInsensitiveEnumValuesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
        ClassWithEnumMapKey result = mapper.readerFor(ClassWithEnumMapKey.class)
                .readValue(MIXED_CASE_ENUM_MAP_FIELD);

        assertEquals(1, result.map.size());
    }
private static final byte[] UNKNOWN_ENUM_VALUE = VPackWireFixtureTest.hex(
            "4c 75 6e 6b 6e 6f 77 6e 56 61 6c 75 65");
enum EnumFruit {
        APPLE,
        BANANA,
        @JsonEnumDefaultValue
        LEMON
    }
enum EnumLetter {
        A,
        @JsonEnumDefaultValue
        @JsonAlias({ "singleAlias" })
        B,
        @JsonAlias({ "multipleAliases1", "multipleAliases2" })
        C
    }
enum TestEnum { JACKSON, RULES, OK }
static class ClassWithEnumMapKey {
        @JsonProperty
        Map<TestEnum, String> map;
    }

    void __invoke_testAllowUnknownEnumValuesAsMapKeysReadAsNullVpack() throws Exception {
        try {
            testAllowUnknownEnumValuesAsMapKeysReadAsNullVpack();
        } finally {
        }
    }


    void __invoke_testAllowUnknownEnumValuesForEnumSetsVpack() throws Exception {
        try {
            testAllowUnknownEnumValuesForEnumSetsVpack();
        } finally {
        }
    }


    void __invoke_testAllowCaseInsensitiveEnumValuesVpack() throws Exception {
        try {
            testAllowCaseInsensitiveEnumValuesVpack();
        } finally {
        }
    }

}
