package tools.jackson.databind.deser.enums;

import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0228F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] TWENTY_PERCENT = VPackWireFixtureTest.hex(
            "43 32 30 25");
private static final byte[] OK_VALUE = VPackWireFixtureTest.hex(
            "14 0c 42 4f 4b 45 76 61 6c 75 65 01");
private static final byte[] LOWER_A_VALUE = VPackWireFixtureTest.hex(
            "14 0b 41 61 45 76 61 6c 75 65 01");
private static final byte[] RULES_WAVES = VPackWireFixtureTest.hex(
            "14 0f 45 52 55 4c 45 53 45 77 61 76 65 73 01");
private static final byte[] KEWL = VPackWireFixtureTest.hex(
            "44 6b 65 77 6c");
private static final byte[] FOO_BAR = VPackWireFixtureTest.hex(
            "14 0b 43 66 6f 6f 43 62 61 72 01");
private static final byte[] PROPERTIES_ENUM_MAP = VPackWireFixtureTest.hex(
            "14 21 41 61 28 0d 45 52 55 4c 45 53 47 6a 61 63 6b 73 6f 6e "
          + "41 62 21 25 fd 42 4f 4b 43 79 65 73 04");
private static final byte[] CASE_INSENSITIVE_MAP_HOLDER = VPackWireFixtureTest.hex(
            "14 1a 49 6d 61 70 48 6f 6c 64 65 72 14 0d 47 66 6f 6f 5f 62 61 72 "
          + "41 34 01 01");
private static final byte[] CASE_INSENSITIVE_ENUM_HOLDER = VPackWireFixtureTest.hex(
            "14 16 4a 65 6e 75 6d 48 6f 6c 64 65 72 47 66 6f 6f 5f 62 61 72 01");
private static final byte[] NULL_ENUM_MAP_FIELD = VPackWireFixtureTest.hex(
            "14 0f 43 6d 61 70 14 08 43 46 4f 4f 40 01 01");
private static final byte[] UNKNOWN_ENUM_MAP = VPackWireFixtureTest.hex(
            "14 11 47 75 6e 6b 6e 6f 77 6e 45 76 61 6c 75 65 01");

    // Provenance: EnumMapDeserializationTest#testCaseInsensitiveEnumsInMaps.
    void testCaseInsensitiveEnumsInMapsVpack() throws Exception {
        ObjectReader reader = VPackMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build()
                .readerFor(Holder1988.class);

        Holder1988 value = reader.readValue(CASE_INSENSITIVE_MAP_HOLDER);
        assertNull(value.enumHolder);
        assertNotNull(value.mapHolder);
        assertEquals(Integer.valueOf(4), value.mapHolder.get(Enum1988.FOO_BAR));

        value = reader.readValue(CASE_INSENSITIVE_ENUM_HOLDER);
        assertEquals(Enum1988.FOO_BAR, value.enumHolder);
        assertNull(value.mapHolder);
    }

    // Provenance: EnumMapDeserializationTest#testCustomEnumMapFromProps.
    void testCustomEnumMapFromPropsVpack() throws Exception {
        FromPropertiesEnumMap value = MAPPER.readValue(PROPERTIES_ENUM_MAP,
                FromPropertiesEnumMap.class);

        assertEquals(13, value.a0);
        assertEquals(-731, value.b0);
        assertEquals("jackson", value.get(TestEnum.RULES));
        assertEquals("yes", value.get(TestEnum.OK));
        assertEquals(2, value.size());
    }

    // Provenance: EnumMapDeserializationTest#testCustomEnumMapFromString.
    void testCustomEnumMapFromStringVpack() throws Exception {
        FromStringEnumMap value = MAPPER.readValue(KEWL, FromStringEnumMap.class);
        assertEquals(1, value.size());
        assertEquals("kewl", value.get(TestEnum.JACKSON));
    }

    // Provenance: EnumMapDeserializationTest#testCustomEnumMapWithDefaultCtor.
    void testCustomEnumMapWithDefaultCtorVpack() throws Exception {
        MySimpleEnumMap value = MAPPER.readValue(RULES_WAVES,
                MySimpleEnumMap.class);
        assertEquals(1, value.size());
        assertEquals("waves", value.get(TestEnum.RULES));
    }

    // Provenance: EnumMapDeserializationTest#testCustomEnumMapWithDelegate.
    void testCustomEnumMapWithDelegateVpack() throws Exception {
        FromDelegateEnumMap value = MAPPER.readValue(FOO_BAR,
                FromDelegateEnumMap.class);
        assertEquals(1, value.size());
        assertEquals("{foo=bar}", value.get(TestEnum.OK));
    }

    // Provenance: EnumMapDeserializationTest#testEnumMapAsPolymorphic.
    void testEnumMapAsPolymorphicVpack() throws Exception {
        EnumMap<Enum1859, String> enumMap = new EnumMap<>(Enum1859.class);
        enumMap.put(Enum1859.A, "Test");
        enumMap.put(Enum1859.B, "stuff");
        Pojo1859 input = new Pojo1859(enumMap);

        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType("").build(),
                        DefaultTyping.NON_FINAL, "@type")
                .build();

        Pojo1859 value = mapper.readValue(mapper.writeValueAsBytes(input),
                Pojo1859.class);
        assertNotNull(value);
        assertNotNull(value.values);
        assertEquals(2, value.values.size());
    }

    // Provenance: EnumMapDeserializationTest#testEnumMaps.
    void testEnumMapsVpack() throws Exception {
        EnumMap<TestEnum, String> value = MAPPER.readValue(OK_VALUE,
                new TypeReference<EnumMap<TestEnum, String>>() { });
        assertEquals("value", value.get(TestEnum.OK));
    }

    // Provenance: EnumMapDeserializationTest#testNullsFailEnumMap5165.
    void testNullsFailEnumMap5165Vpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.FAIL))
                .build();
        assertThrows(InvalidNullException.class,
                () -> mapper.readValue(NULL_ENUM_MAP_FIELD,
                        new TypeReference<Dst5165>() { }));
    }

    // Provenance: EnumMapDeserializationTest#testNullsSkipEnumMap5165.
    void testNullsSkipEnumMap5165Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> n.withContentNulls(Nulls.SKIP))
                .build();
        Dst5165 value = mapper.readValue(NULL_ENUM_MAP_FIELD,
                new TypeReference<Dst5165>() { });
        assertTrue(value.getMap().isEmpty());
    }

    // Provenance: EnumMapDeserializationTest#testToStringEnumMaps.
    void testToStringEnumMapsVpack() throws Exception {
        ObjectReader reader = MAPPER.reader()
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING)
                .forType(new TypeReference<EnumMap<LowerCaseEnum, String>>() { });
        EnumMap<LowerCaseEnum, String> value = reader.readValue(LOWER_A_VALUE);
        assertEquals("value", value.get(LowerCaseEnum.A));
    }

    // Provenance: EnumMapDeserializationTest#testUnknownKeyAsDefault.
    void testUnknownKeyAsDefaultVpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(
                new TypeReference<EnumMap<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        EnumMap<TestEnumWithDefault, String> value = reader.readValue(UNKNOWN_ENUM_MAP);
        assertEquals(1, value.size());
        assertEquals("value", value.get(TestEnumWithDefault.OK));

        Map<TestEnumWithDefault, String> value2 = MAPPER
                .readerFor(new TypeReference<Map<TestEnumWithDefault, String>>() { })
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .readValue(UNKNOWN_ENUM_MAP);
        assertEquals(1, value2.size());
        assertEquals("value", value2.get(TestEnumWithDefault.OK));
    }
enum Enum5271 {
        T10("10%"), T20("20%"), T30("30%");

        private final String code;

        Enum5271(String code) {
            this.code = code;
        }

        @JsonValue
        public String getCode() {
            return code;
        }
    }
enum TestEnum { JACKSON, RULES, OK }
enum TestEnumWithDefault {
        JACKSON, RULES,
        @JsonEnumDefaultValue
        OK
    }
enum LowerCaseEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
static class MySimpleEnumMap extends EnumMap<TestEnum, String> {
        MySimpleEnumMap() {
            super(TestEnum.class);
        }
    }
static class FromStringEnumMap extends EnumMap<TestEnum, String> {
        @JsonCreator
        FromStringEnumMap(String value) {
            super(TestEnum.class);
            put(TestEnum.JACKSON, value);
        }
    }
static class FromDelegateEnumMap extends EnumMap<TestEnum, String> {
        @JsonCreator
        FromDelegateEnumMap(Map<Object, Object> value) {
            super(TestEnum.class);
            put(TestEnum.OK, String.valueOf(value));
        }
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
enum Enum1859 { A, B, C }
static class Pojo1859 {
        public EnumMap<Enum1859, String> values;

        public Pojo1859() { }

        Pojo1859(EnumMap<Enum1859, String> value) {
            values = value;
        }
    }
enum Enum1988 { FOO_BAR, FOO_BAZ }
static class Holder1988 {
        public Map<Enum1988, Number> mapHolder;
        public Enum1988 enumHolder;
    }
enum Enum5165 { FOO }
static class Dst5165 {
        private EnumMap<Enum5165, Integer> map;

        public EnumMap<Enum5165, Integer> getMap() {
            return map;
        }

        public void setMap(EnumMap<Enum5165, Integer> value) {
            map = value;
        }
    }

    void __invoke_testCaseInsensitiveEnumsInMapsVpack() throws Exception {
        try {
            testCaseInsensitiveEnumsInMapsVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumMapFromPropsVpack() throws Exception {
        try {
            testCustomEnumMapFromPropsVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumMapFromStringVpack() throws Exception {
        try {
            testCustomEnumMapFromStringVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumMapWithDefaultCtorVpack() throws Exception {
        try {
            testCustomEnumMapWithDefaultCtorVpack();
        } finally {
        }
    }


    void __invoke_testCustomEnumMapWithDelegateVpack() throws Exception {
        try {
            testCustomEnumMapWithDelegateVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapAsPolymorphicVpack() throws Exception {
        try {
            testEnumMapAsPolymorphicVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapsVpack() throws Exception {
        try {
            testEnumMapsVpack();
        } finally {
        }
    }


    void __invoke_testNullsFailEnumMap5165Vpack() throws Exception {
        try {
            testNullsFailEnumMap5165Vpack();
        } finally {
        }
    }


    void __invoke_testNullsSkipEnumMap5165Vpack() throws Exception {
        try {
            testNullsSkipEnumMap5165Vpack();
        } finally {
        }
    }


    void __invoke_testToStringEnumMapsVpack() throws Exception {
        try {
            testToStringEnumMapsVpack();
        } finally {
        }
    }


    void __invoke_testUnknownKeyAsDefaultVpack() throws Exception {
        try {
            testUnknownKeyAsDefaultVpack();
        } finally {
        }
    }

}
