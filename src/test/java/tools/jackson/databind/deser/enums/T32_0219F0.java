package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0219F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper IGNORE_CASE_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
private static final byte[] ENUM_A = VPackWireFixtureTest.hex("41 41");
private static final byte[] ENUM_a = VPackWireFixtureTest.hex("41 61");
private static final byte[] ENUM_JACKSON_LOWER = VPackWireFixtureTest.hex(
            "47 6a 61 63 6b 73 6f 6e");
private static final byte[] ALIAS_SINGLE = VPackWireFixtureTest.hex(
            "4b 73 69 6e 67 6c 65 41 6c 69 61 73");
private static final byte[] ALIAS_MULTIPLE_1 = VPackWireFixtureTest.hex(
            "50 6d 75 6c 74 69 70 6c 65 41 6c 69 61 73 65 73 31");
private static final byte[] ALIAS_MULTIPLE_2 = VPackWireFixtureTest.hex(
            "50 6d 75 6c 74 69 70 6c 65 41 6c 69 61 73 65 73 32");
private static final byte[] UNKNOWN_VALUE = VPackWireFixtureTest.hex(
            "4c 75 6e 6b 6e 6f 77 6e 56 61 6c 75 65");
private static final byte[] VALUE_OK_OBJECT = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 42 6f 6b 01");
private static final byte[] COLOR_WHITE_OBJECT = VPackWireFixtureTest.hex(
            "14 0f 45 63 6f 6c 6f 72 45 57 48 49 54 45 01");

    // Provenance: EnumAliasDeser2352Test#testEnumWithAlias and
    // EnumAltIdTest#testEnumWithAlias.
    void testEnumWithAliasVpack() throws Exception {
        assertEquals(MyEnum2352_1.A, MAPPER.readValue(ENUM_A, MyEnum2352_1.class));
        assertEquals(MyEnum2352_1.B, MAPPER.readValue(ALIAS_SINGLE, MyEnum2352_1.class));
        assertEquals(MyEnum2352_1.C,
                MAPPER.readValue(ALIAS_MULTIPLE_1, MyEnum2352_1.class));
        assertEquals(MyEnum2352_1.C,
                MAPPER.readValue(ALIAS_MULTIPLE_2, MyEnum2352_1.class));
    }

    // Provenance: EnumAliasDeser2352Test#testEnumWithAliasAndToStringSupported and
    // EnumAltIdTest#testEnumWithAliasAndToStringSupported.
    void testEnumWithAliasAndToStringSupportedVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        assertEquals(MyEnum2352_2.A, mapper.readerFor(MyEnum2352_2.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING).readValue(ENUM_a));
        assertEquals(MyEnum2352_2.B, mapper.readerFor(MyEnum2352_2.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING).readValue(ALIAS_SINGLE));
        assertEquals(MyEnum2352_2.C, mapper.readerFor(MyEnum2352_2.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING).readValue(ALIAS_MULTIPLE_1));
        assertEquals(MyEnum2352_2.C, mapper.readerFor(MyEnum2352_2.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING).readValue(ALIAS_MULTIPLE_2));
    }

    // Provenance: EnumAliasDeser2352Test#testEnumWithAliasAndDefaultForUnknownValueEnabled
    // and EnumAltIdTest#testEnumWithAliasAndDefaultForUnknownValueEnabled.
    void testEnumWithAliasAndDefaultForUnknownValueEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        var reader = mapper.readerFor(MyEnum2352_3.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        assertEquals(MyEnum2352_3.A, reader.readValue(ENUM_A));
        assertEquals(MyEnum2352_3.B, reader.readValue(ALIAS_SINGLE));
        assertEquals(MyEnum2352_3.B, reader.readValue(UNKNOWN_VALUE));
        assertEquals(MyEnum2352_3.C, reader.readValue(ALIAS_MULTIPLE_1));
        assertEquals(MyEnum2352_3.C, reader.readValue(ALIAS_MULTIPLE_2));
    }
enum MyEnum2352_1 {
        A,
        @JsonAlias({ "singleAlias" })
        B,
        @JsonAlias({ "multipleAliases1", "multipleAliases2" })
        C
    }
enum MyEnum2352_2 {
        A,
        @JsonAlias({ "singleAlias" })
        B,
        @JsonAlias({ "multipleAliases1", "multipleAliases2" })
        C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum MyEnum2352_3 {
        A,
        @JsonEnumDefaultValue
        @JsonAlias({ "singleAlias" })
        B,
        @JsonAlias({ "multipleAliases1", "multipleAliases2" })
        C
    }
enum TestEnum { JACKSON, RULES, OK }
enum LowerCaseEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum Color { RED, BLUE }
static class DefaultEnumBean {
        @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        public MyEnum2352_3 value;
    }
static class DefaultEnumSetBean {
        @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        public java.util.EnumSet<MyEnum2352_3> value;
    }
static class NullValueEnumBean {
        @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        public MyEnum2352_3 value;
    }
static class Book4481 {
        @JsonFormat(without = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        public Color color;
    }

    void __invoke_testEnumWithAliasVpack() throws Exception {
        try {
            testEnumWithAliasVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithAliasAndToStringSupportedVpack() throws Exception {
        try {
            testEnumWithAliasAndToStringSupportedVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithAliasAndDefaultForUnknownValueEnabledVpack() throws Exception {
        try {
            testEnumWithAliasAndDefaultForUnknownValueEnabledVpack();
        } finally {
        }
    }

}
