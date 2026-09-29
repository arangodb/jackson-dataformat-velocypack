package tools.jackson.databind.deser.enums;

import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0224F0 {
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

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownUsingDefaultBeforeAsNull.
    void testDeserUnknownUsingDefaultBeforeAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumFruit.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        assertEquals(EnumFruit.LEMON, reader.readValue(EMPTY_STRING));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownUsingDefaultBeforeAsNullFlip.
    void testDeserUnknownUsingDefaultBeforeAsNullFlipVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumFruit.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        assertEquals(EnumFruit.LEMON, reader.readValue(EMPTY_STRING));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownAsNull.
    void testDeserUnknownAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumFruit.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        assertNull(reader.readValue(EMPTY_STRING));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserWithAliasUsingDefault.
    void testDeserWithAliasUsingDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumLetter.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        assertEquals(EnumLetter.B, reader.readValue(UNKNOWN_ENUM_VALUE));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserWithAliasAsNull.
    void testDeserWithAliasAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(EnumLetter.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        assertNull(reader.readValue(UNKNOWN_ENUM_VALUE));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownEnumMapKeyUsingDefault.
    void testDeserUnknownEnumMapKeyUsingDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(new TypeReference<EnumMap<EnumFruit, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);

        EnumMap<EnumFruit, String> result = reader.readValue(UNKNOWN_ENUM_MAP_ENTRY);

        assertTrue(result.containsKey(EnumFruit.LEMON));
        assertEquals("fresh!", result.get(EnumFruit.LEMON));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownEnumMapKeyAsNull.
    void testDeserUnknownEnumMapKeyAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(new TypeReference<EnumMap<EnumFruit, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        EnumMap<EnumFruit, String> result = reader.readValue(UNKNOWN_ENUM_MAP_ENTRY);

        assertEquals(EnumMap.class, result.getClass());
        assertTrue(result.isEmpty());
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownMapKeyUsingDefault.
    void testDeserUnknownMapKeyUsingDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(new TypeReference<Map<EnumFruit, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        Map<EnumFruit, String> result = reader.readValue(UNKNOWN_ENUM_MAP_ENTRY);

        assertTrue(result.containsKey(EnumFruit.LEMON));
        assertEquals("fresh!", result.get(EnumFruit.LEMON));
    }

    // Provenance: EnumDeserializationFeatureOrderTest#testDeserUnknownMapKeyAsNull.
    void testDeserUnknownMapKeyAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(new TypeReference<Map<EnumFruit, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

        Map<EnumFruit, String> result = reader.readValue(UNKNOWN_ENUM_MAP_ENTRY);

        assertFalse(result.containsKey(EnumFruit.LEMON));
        assertEquals(0, result.size());
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

    void __invoke_testDeserUnknownUsingDefaultBeforeAsNullVpack() throws Exception {
        try {
            testDeserUnknownUsingDefaultBeforeAsNullVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownUsingDefaultBeforeAsNullFlipVpack() throws Exception {
        try {
            testDeserUnknownUsingDefaultBeforeAsNullFlipVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownAsNullVpack() throws Exception {
        try {
            testDeserUnknownAsNullVpack();
        } finally {
        }
    }


    void __invoke_testDeserWithAliasUsingDefaultVpack() throws Exception {
        try {
            testDeserWithAliasUsingDefaultVpack();
        } finally {
        }
    }


    void __invoke_testDeserWithAliasAsNullVpack() throws Exception {
        try {
            testDeserWithAliasAsNullVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownEnumMapKeyUsingDefaultVpack() throws Exception {
        try {
            testDeserUnknownEnumMapKeyUsingDefaultVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownEnumMapKeyAsNullVpack() throws Exception {
        try {
            testDeserUnknownEnumMapKeyAsNullVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownMapKeyUsingDefaultVpack() throws Exception {
        try {
            testDeserUnknownMapKeyUsingDefaultVpack();
        } finally {
        }
    }


    void __invoke_testDeserUnknownMapKeyAsNullVpack() throws Exception {
        try {
            testDeserUnknownMapKeyAsNullVpack();
        } finally {
        }
    }

}
