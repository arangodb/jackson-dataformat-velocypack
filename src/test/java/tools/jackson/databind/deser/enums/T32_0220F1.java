package tools.jackson.databind.deser.enums;

import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0220F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .build();
private static final ObjectMapper IGNORE_CASE_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
private static final byte[] JACKSON = VPackWireFixtureTest.hex(
            "47 4a 61 63 6b 73 6f 6e");
private static final byte[] UPPER_A = VPackWireFixtureTest.hex("41 41");
private static final byte[] EMPTY = VPackWireFixtureTest.hex("40");
private static final byte[] BLANK = VPackWireFixtureTest.hex(
            "44 20 20 20 20");
private static final byte[] ONE = VPackWireFixtureTest.hex("43 4f 4e 45");
private static final byte[] ZERO = VPackWireFixtureTest.hex("44 5a 65 72 6f");
private static final byte[] UNKNOWN = VPackWireFixtureTest.hex(
            "47 55 4e 4b 4e 4f 57 4e");
private static final byte[] OOPS = VPackWireFixtureTest.hex(
            "45 4f 4f 50 53 21");
private static final byte[] BRAND_005 = VPackWireFixtureTest.hex("43 30 30 35");
private static final byte[] BRAND_006 = VPackWireFixtureTest.hex("43 30 30 36");
private static final byte[] BRAND_001 = VPackWireFixtureTest.hex("43 30 30 31");
private static final byte[] BRAND_X = VPackWireFixtureTest.hex("41 78");
private static final byte[] VALUE_OK = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 42 6f 6b 01");
private static final byte[] MIXED_ENUMS = VPackWireFixtureTest.hex(
            "13 11 47 6a 61 63 6b 73 4f 4e 45 72 75 4c 65 73 02");
private static final byte[] JACKSON_ENUM_SET = VPackWireFixtureTest.hex(
            "13 0b 47 6a 61 63 6b 73 6f 6e 01");
private static final byte[] VALUE_UNKNOWN_AND_B = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 13 08 "
            + "42 6f 6b 41 42 02 01");
private static final byte[] TYPE_OOPS = VPackWireFixtureTest.hex(
            "14 0e 44 74 79 70 65 45 4f 4f 50 53 21 01");

    // Provenance: EnumDefaultReadTest#testEmptyStringAsDefault.
    void testEmptyStringAsDefaultVpack() throws Exception {
        var defaultReader = MAPPER.readerFor(SimpleEnumWithDefault.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        assertEquals(SimpleEnumWithDefault.ONE, defaultReader.readValue(ONE));
        assertEquals(SimpleEnumWithDefault.ZERO, defaultReader.readValue(ZERO));
        assertEquals(SimpleEnumWithDefault.ZERO, defaultReader.readValue(EMPTY));
        assertEquals(SimpleEnumWithDefault.ZERO, defaultReader.readValue(BLANK));

        var nullReader = MAPPER.readerFor(SimpleEnumWithDefault.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        assertNull(nullReader.readValue(EMPTY));
        assertNull(nullReader.readValue(BLANK));
    }

    // Provenance: EnumDefaultReadTest#testEnumDefaultValueViaMixin.
    void testEnumDefaultValueViaMixinVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .addMixIn(BaseEnumDefault.class, MixinEnumDefault.class)
                .build();
        assertEquals(BaseEnumDefault.Z, mapper.readValue(UNKNOWN, BaseEnumDefault.class));
    }

    // Provenance: EnumDefaultReadTest#testFirstEnumDefaultValueViaMixin.
    void testFirstEnumDefaultValueViaMixinVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .addMixIn(BaseOverloaded.class, MixinOverloadedDefault.class)
                .build();
        assertEquals(BaseOverloaded.A, mapper.readValue(UNKNOWN, BaseOverloaded.class));
    }

    // Provenance: EnumDefaultReadTest#testReadFromDefault4403.
    void testReadFromDefault4403Vpack() throws Exception {
        var reader = MAPPER.readerFor(Brand4403.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        assertEquals(Brand4403.SEAT, reader.readValue(BRAND_005));
        assertEquals(Brand4403.HYUNDAI, reader.readValue(BRAND_006));
        assertEquals(Brand4403.OTHER, reader.readValue(BRAND_X));
        assertEquals(Brand4403.OTHER, reader.readValue(BRAND_001));
    }
enum TestEnum { JACKSON, RULES, OK }
enum LowerCaseEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum SimpleEnumWithDefault {
        @JsonEnumDefaultValue
        ZERO,
        ONE
    }
enum BaseEnumDefault { A, B, C, Z }
enum MixinEnumDefault {
        A, B, C,
        @JsonEnumDefaultValue
        Z
    }
enum BaseOverloaded { A, B, C, Z }
enum MixinOverloadedDefault {
        @JsonEnumDefaultValue
        A,
        @JsonEnumDefaultValue
        B,
        C,
        @JsonEnumDefaultValue
        Z
    }
enum Brand4403 {
        @JsonProperty("005")
        SEAT,
        @JsonProperty("006")
        HYUNDAI,
        @JsonEnumDefaultValue
        OTHER
    }
enum Types {
        @JsonEnumDefaultValue
        DEFAULT_TYPE,
        FAST,
        SLOW
    }
static class EnumBean {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public TestEnum value;
    }
static class StrictCaseBean {
        @JsonFormat(without = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        public TestEnum value;
    }
static class EnumBeanWithCaseInsensitiveValues {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
        public TestEnum value;
    }
static class NullEnumSetBean {
        @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        public EnumSet<MyEnum2352_3> value;
    }
enum MyEnum2352_3 {
        A,
        @JsonEnumDefaultValue
        B,
        C
    }
static class SpeedWithoutDefaultOverride {
        @JsonFormat(without = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        public Types type;
    }
static class SpeedWithDefaultOverride {
        @JsonFormat(with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        public Types type;
    }

    void __invoke_testEmptyStringAsDefaultVpack() throws Exception {
        try {
            testEmptyStringAsDefaultVpack();
        } finally {
        }
    }


    void __invoke_testEnumDefaultValueViaMixinVpack() throws Exception {
        try {
            testEnumDefaultValueViaMixinVpack();
        } finally {
        }
    }


    void __invoke_testFirstEnumDefaultValueViaMixinVpack() throws Exception {
        try {
            testFirstEnumDefaultValueViaMixinVpack();
        } finally {
        }
    }


    void __invoke_testReadFromDefault4403Vpack() throws Exception {
        try {
            testReadFromDefault4403Vpack();
        } finally {
        }
    }

}
