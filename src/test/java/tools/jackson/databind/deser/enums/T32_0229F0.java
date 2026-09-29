package tools.jackson.databind.deser.enums;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0229F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper CI_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
private static final byte[] UNKNOWN_ONLY = VPackWireFixtureTest.hex(
            "14 11 47 75 6e 6b 6e 6f 77 6e 45 76 61 6c 75 65 01");
private static final byte[] UNKNOWN_AND_OK = VPackWireFixtureTest.hex(
            "14 1a 47 75 6e 6b 6e 6f 77 6e 45 76 61 6c 75 65 "
          + "42 4f 4b 45 76 61 6c 69 64 02");
private static final byte[] INTERLEAVED_UNKNOWN = VPackWireFixtureTest.hex(
            "14 22 44 62 61 64 31 41 78 42 4f 4b 42 76 31 "
          + "44 62 61 64 32 41 79 47 4a 41 43 4b 53 4f 4e 42 76 32 04");
private static final byte[] PROPERTY_CREATOR_SCALAR = VPackWireFixtureTest.hex(
            "14 2b 44 4e 4f 50 45 44 73 6b 69 70 41 61 28 0d "
          + "45 52 55 4c 45 53 47 6a 61 63 6b 73 6f 6e "
          + "41 62 21 25 fd 42 4f 4b 43 79 65 73 05");
private static final byte[] PROPERTY_CREATOR_OBJECT = VPackWireFixtureTest.hex(
            "14 36 44 4e 4f 50 45 14 10 46 6e 65 73 74 65 64 "
          + "45 76 61 6c 75 65 01 41 61 28 0d 45 52 55 4c 45 53 "
          + "47 6a 61 63 6b 73 6f 6e 41 62 21 25 fd 42 4f 4b 43 "
          + "79 65 73 05");
private static final byte[] PROPERTY_CREATOR_ARRAY = VPackWireFixtureTest.hex(
            "14 2c 44 4e 4f 50 45 13 06 31 32 33 03 41 61 28 0d "
          + "45 52 55 4c 45 53 47 6a 61 63 6b 73 6f 6e "
          + "41 62 21 25 fd 42 4f 4b 43 79 65 73 05");
private static final byte[] ANNOTATED_HASH_MAP = VPackWireFixtureTest.hex(
            "14 24 46 76 61 6c 75 65 73 14 1a 47 75 6e 6b 6e 6f 77 6e "
          + "45 76 61 6c 75 65 42 4f 4b 45 76 61 6c 69 64 02 01");
private static final byte[] NAMED_KEY = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 47 6b 65 74 63 68 55 70 "
          + "43 76 61 6c 01 01");
private static final byte[] NAMED_KEY_MIXED_CASE = VPackWireFixtureTest.hex(
            "14 16 43 6d 61 70 14 0f 47 4b 65 54 63 48 75 50 "
          + "43 76 61 6c 01 01");
private static final byte[] NAMED_VALUES = VPackWireFixtureTest.hex(
            "14 31 43 6d 61 70 14 2a 4a 6c 6f 77 65 72 53 61 75 63 65 "
          + "47 6b 65 74 63 68 55 70 4a 75 70 70 65 72 53 61 75 63 65 "
          + "48 6d 61 79 6f 4e 65 7a 7a 02 01");
private static final byte[] NAMED_VALUES_MIXED_CASE = VPackWireFixtureTest.hex(
            "14 31 43 6d 61 70 14 2a 4a 6c 6f 77 65 72 53 61 75 63 65 "
          + "47 6b 65 74 63 68 75 70 4a 75 70 70 65 72 53 61 75 63 65 "
          + "48 4d 41 59 4f 4e 45 5a 5a 02 01");

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNull.
    void testUnknownKeyAsNullVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(
                new TypeReference<EnumMap<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        EnumMap<TestEnumWithDefault, String> value = reader.readValue(UNKNOWN_ONLY);
        assertTrue(value.isEmpty());

        Map<TestEnumWithDefault, String> value2 = MAPPER
                .readerFor(new TypeReference<Map<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(UNKNOWN_ONLY);
        assertTrue(value2.isEmpty());
        assertEquals(EnumMap.class, value2.getClass());
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithPlainMap.
    void testUnknownKeyAsNullWithPlainMapVpack() throws Exception {
        Map<TestEnumWithDefault, String> value = MAPPER
                .readerFor(new TypeReference<Map<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(UNKNOWN_AND_OK);
        assertEquals(1, value.size());
        assertEquals("valid", value.get(TestEnumWithDefault.OK));
        assertFalse(value.containsKey(null));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithHashMap.
    void testUnknownKeyAsNullWithHashMapVpack() throws Exception {
        HashMap<TestEnumWithDefault, String> value = MAPPER
                .readerFor(new TypeReference<HashMap<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(UNKNOWN_AND_OK);
        assertEquals(1, value.size());
        assertEquals("valid", value.get(TestEnumWithDefault.OK));
        assertFalse(value.containsKey(null));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithLinkedHashMap.
    void testUnknownKeyAsNullWithLinkedHashMapVpack() throws Exception {
        LinkedHashMap<TestEnumWithDefault, String> value = MAPPER
                .readerFor(new TypeReference<LinkedHashMap<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(UNKNOWN_AND_OK);
        assertEquals(1, value.size());
        assertEquals("valid", value.get(TestEnumWithDefault.OK));
        assertFalse(value.containsKey(null));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithInterleavedUnknowns.
    void testUnknownKeyAsNullWithInterleavedUnknownsVpack() throws Exception {
        LinkedHashMap<TestEnumWithDefault, String> value = MAPPER
                .readerFor(new TypeReference<LinkedHashMap<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(INTERLEAVED_UNKNOWN);
        assertEquals(2, value.size());
        assertEquals("v1", value.get(TestEnumWithDefault.OK));
        assertEquals("v2", value.get(TestEnumWithDefault.JACKSON));
        assertFalse(value.containsKey(null));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithJsonDeserializeAnnotation.
    void testUnknownKeyAsNullWithJsonDeserializeAnnotationVpack() throws Exception {
        BeanWithHashMapEnumKey result = MAPPER.reader()
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .forType(BeanWithHashMapEnumKey.class)
                .readValue(ANNOTATED_HASH_MAP);
        assertEquals(1, result.values.size());
        assertEquals("valid", result.values.get(TestEnumWithDefault.OK));
        assertFalse(result.values.containsKey(null));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsNullWithPropertyBasedCreator.
    void testUnknownKeyAsNullWithPropertyBasedCreatorVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(FromPropertiesEnumMap.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertProperties(reader.readValue(PROPERTY_CREATOR_SCALAR));
        assertProperties(reader.readValue(PROPERTY_CREATOR_OBJECT));
        assertProperties(reader.readValue(PROPERTY_CREATOR_ARRAY));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyFailsWithHashMapByDefault.
    void testUnknownKeyFailsWithHashMapByDefaultVpack() {
        DatabindException exception = assertThrows(DatabindException.class,
                () -> MAPPER.readerFor(
                        new TypeReference<HashMap<TestEnumWithDefault, String>>() { })
                        .readValue(UNKNOWN_ONLY));
        assertTrue(exception.getMessage().contains("Cannot deserialize Map key"));
    }
private static void assertProperties(FromPropertiesEnumMap value) {
        assertEquals(13, value.a0);
        assertEquals(-731, value.b0);
        assertEquals("jackson", value.get(TestEnum.RULES));
        assertEquals("yes", value.get(TestEnum.OK));
        assertEquals(2, value.size());
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumSauceC {
        KETCH_UP, MAYO_NEZZ
    }
enum TestEnum { JACKSON, RULES, OK }
enum TestEnumWithDefault {
        JACKSON, RULES,
        @JsonEnumDefaultValue
        OK
    }
static class FromPropertiesEnumMap extends EnumMap<TestEnum, String> {
        int a0, b0;

        @JsonCreator
        FromPropertiesEnumMap(@JsonProperty("a") int a,
                @JsonProperty("b") int b) {
            super(TestEnum.class);
            a0 = a;
            b0 = b;
        }
    }
static class BeanWithHashMapEnumKey {
        @JsonDeserialize(as = HashMap.class)
        public Map<TestEnumWithDefault, String> values;
    }
static class ClassWithEnumMapSauceKey {
        @JsonProperty
        Map<EnumSauceC, String> map;
    }
static class ClassWithEnumMapSauceValue {
        @JsonProperty
        Map<String, EnumSauceC> map;
    }

    void __invoke_testUnknownKeyAsNullVpack() throws Exception {
        try {
            testUnknownKeyAsNullVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithPlainMapVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithPlainMapVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithHashMapVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithHashMapVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithLinkedHashMapVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithLinkedHashMapVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithInterleavedUnknownsVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithInterleavedUnknownsVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithJsonDeserializeAnnotationVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithJsonDeserializeAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsNullWithPropertyBasedCreatorVpack() throws Exception {
        try {
            testUnknownKeyAsNullWithPropertyBasedCreatorVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyFailsWithHashMapByDefaultVpack() throws Exception {
        try {
            testUnknownKeyFailsWithHashMapByDefaultVpack();
        } finally {
        }
    }

}
