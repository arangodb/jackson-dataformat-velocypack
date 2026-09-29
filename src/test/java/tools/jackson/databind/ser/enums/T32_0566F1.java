package tools.jackson.databind.ser.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0566F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumSerializationTest#testAnnotationsOnEnumCtor().
    void testAnnotationsOnEnumCtorVpack() throws Exception {
        byte[] v1 = VPackWireFixtureTest.hex("42 56 31");
        assertArrayEquals(v1, MAPPER.writeValueAsBytes(OK.V1));
        assertArrayEquals(v1, MAPPER.writeValueAsBytes(NOT_OK.V1));
        assertArrayEquals(VPackWireFixtureTest.hex("42 56 32"),
                MAPPER.writeValueAsBytes(NOT_OK2.V2));
    }

    // Provenance: EnumSerializationTest#testAsIndex().
    void testAsIndexVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"),
                MAPPER.writeValueAsBytes(TestEnum.B));
        ObjectMapper indexed = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("31"),
                indexed.writeValueAsBytes(TestEnum.B));
    }

    // Provenance: EnumSerializationTest#testEnumFeature_WRITE_ENUMS_TO_LOWERCASE().
    void testEnumFeature_WRITE_ENUMS_TO_LOWERCASEVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("41 62"),
                mapper.writeValueAsBytes(TestEnum.B));
    }

    
    // Provenance: EnumSerializationTest#testEnumFeature_WRITE_ENUMS_TO_LOWERCASEUsesRootLocale().
    void testEnumFeature_WRITE_ENUMS_TO_LOWERCASEUsesRootLocaleVpack() throws Exception {
        Locale old = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertEquals("\u0131", "I".toLowerCase());

            ObjectMapper mapper = VPackMapper.builder()
                    .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
                    .build();
            assertArrayEquals(VPackWireFixtureTest.hex(
                            "48 69 73 5f 61 64 6d 69 6e"),
                    mapper.writeValueAsBytes(LocaleSensitiveEnum.IS_ADMIN));
        } finally {
            Locale.setDefault(old);
        }
    }

    // Provenance: EnumSerializationTest#testEnumFeature_WRITE_ENUMS_TO_LOWERCASE_isDisabledByDefault().
    void testEnumFeature_WRITE_ENUMS_TO_LOWERCASE_isDisabledByDefaultVpack() {
        ObjectReader reader = MAPPER.reader();
        assertFalse(reader.isEnabled(EnumFeature.WRITE_ENUMS_TO_LOWERCASE));
        assertFalse(reader.without(EnumFeature.WRITE_ENUMS_TO_LOWERCASE)
                .isEnabled(EnumFeature.WRITE_ENUMS_TO_LOWERCASE));
    }

    // Provenance: EnumSerializationTest#testEnumKeysWithJsonProperty().
    void testEnumKeysWithJsonPropertyVpack() throws Exception {
        Map<EnumWithJsonProperty, Integer> input = new HashMap<>();
        input.put(EnumWithJsonProperty.A, 13);
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 45 61 6c 65 70 68 28 0d 03"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testEnumMapSerDefault().
    void testEnumMapSerDefaultVpack() throws Exception {
        EnumMap<LC749Enum, String> input = new EnumMap<>(LC749Enum.class);
        input.put(LC749Enum.A, "value");
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 41 41 45 76 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testEnumMapSerDisableToString().
    void testEnumMapSerDisableToStringVpack() throws Exception {
        EnumMap<LC749Enum, String> input = new EnumMap<>(LC749Enum.class);
        input.put(LC749Enum.A, "value");
        ObjectMapper mapper = VPackMapper.builder().build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 41 41 45 76 61 6c 75 65 03"),
                mapper.writer().without(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                        .writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testEnumMapSerEnableToString().
    void testEnumMapSerEnableToStringVpack() throws Exception {
        EnumMap<LC749Enum, String> input = new EnumMap<>(LC749Enum.class);
        input.put(LC749Enum.A, "value");
        ObjectMapper mapper = VPackMapper.builder().build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 41 61 45 76 61 6c 75 65 03"),
                mapper.writer().with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                        .writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testEnumSet().
    void testEnumSetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("02 04 41 42"),
                MAPPER.writeValueAsBytes(EnumSet.of(TestEnum.B)));
    }
enum TestEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum LocaleSensitiveEnum {
        IS_ADMIN
    }
enum LC749Enum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum EnumWithJsonProperty {
        @JsonProperty("aleph")
        A
    }
enum EnumBaseA {
        ITEM_A {
            @Override
            public String toString() {
                return "A_base";
            }
        },

        @JsonAlias({"B_ORIGIN_ALIAS_1", "B_ORIGIN_ALIAS_2"})
        @JsonProperty("B_ORIGIN_PROP")
        ITEM_B,

        @JsonAlias("C_ORIGIN_ALIAS")
        @JsonProperty("C_COMMON")
        ITEM_C_BASE,

        ITEM_ORIGIN
    }
enum EnumMixinA {
        ITEM_A {
            @Override
            public String toString() {
                return "A_mixin";
            }
        },

        @JsonProperty("B_MIXIN_PROP")
        ITEM_B,

        @JsonAlias({"C_MIXIN_ALIAS_1", "C_MIXIN_ALIAS_2"})
        @JsonProperty("C_COMMON")
        ITEM_C_MIXIN,

        ITEM_MIXIN;

        @Override
        public String toString() {
            return "SHOULD NOT USE WITH TO STRING";
        }
    }
enum OK {
        V1("v1");

        OK(String key) { }
    }
enum NOT_OK {
        V1("v1");

        NOT_OK(@JsonProperty String key) { }
    }
enum NOT_OK2 {
        V2("v2");

        NOT_OK2(@JsonProperty String key) { }
    }

    void __invoke_testAnnotationsOnEnumCtorVpack() throws Exception {
        try {
            testAnnotationsOnEnumCtorVpack();
        } finally {
        }
    }


    void __invoke_testAsIndexVpack() throws Exception {
        try {
            testAsIndexVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_WRITE_ENUMS_TO_LOWERCASEVpack() throws Exception {
        try {
            testEnumFeature_WRITE_ENUMS_TO_LOWERCASEVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_WRITE_ENUMS_TO_LOWERCASEUsesRootLocaleVpack() throws Exception {
        try {
            testEnumFeature_WRITE_ENUMS_TO_LOWERCASEUsesRootLocaleVpack();
        } finally {
        }
    }


    void __invoke_testEnumFeature_WRITE_ENUMS_TO_LOWERCASE_isDisabledByDefaultVpack() throws Exception {
        try {
            testEnumFeature_WRITE_ENUMS_TO_LOWERCASE_isDisabledByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testEnumKeysWithJsonPropertyVpack() throws Exception {
        try {
            testEnumKeysWithJsonPropertyVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapSerDefaultVpack() throws Exception {
        try {
            testEnumMapSerDefaultVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapSerDisableToStringVpack() throws Exception {
        try {
            testEnumMapSerDisableToStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumMapSerEnableToStringVpack() throws Exception {
        try {
            testEnumMapSerEnableToStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumSetVpack() throws Exception {
        try {
            testEnumSetVpack();
        } finally {
        }
    }

}
