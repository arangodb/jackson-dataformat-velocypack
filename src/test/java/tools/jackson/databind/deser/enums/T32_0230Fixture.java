package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.EnumNamingStrategy;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0230Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper CI_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();
private static final byte[] SALTED_CARAMEL = VPackWireFixtureTest.hex(
            "4d 73 61 6c 74 65 64 43 61 72 61 6d 65 6c");
private static final byte[] UNKNOWN_SALTED_CARAMEL = VPackWireFixtureTest.hex(
            "50 5f 5f 73 61 6c 74 65 64 5f 63 61 72 61 6d 65 6c");
private static final byte[] ENUM_ORDINAL_ONE = VPackWireFixtureTest.hex("31");
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] PEANUT_BUTTER = VPackWireFixtureTest.hex(
            "4d 50 45 41 4e 55 54 5f 42 55 54 54 45 52");
private static final byte[] PEANUT_BUTTER_CAMEL = VPackWireFixtureTest.hex(
            "4c 70 65 61 6e 75 74 42 75 74 74 65 72");
private static final byte[] CHOCOLATE = VPackWireFixtureTest.hex(
            "49 63 68 6f 63 6f 6c 61 74 65");
private static final byte[] MILK_CHOCOLATE = VPackWireFixtureTest.hex(
            "4d 6d 69 6c 6b 43 68 6f 63 6f 6c 61 74 65");
private static final byte[] CARAMEL = VPackWireFixtureTest.hex(
            "47 63 61 72 61 6d 65 6c");
private static final byte[] SALTED_CARAMEL_CAMEL = SALTED_CARAMEL;
private static final byte[] KETCH_UP = VPackWireFixtureTest.hex(
            "47 6b 65 74 63 68 55 70");
private static final byte[] MAYO_NEZZ = VPackWireFixtureTest.hex(
            "48 6d 61 79 6f 4e 65 7a 7a");
private static final byte[] SRIRACHA_MAYO = VPackWireFixtureTest.hex(
            "4d 53 52 49 52 41 43 48 41 5f 4d 41 59 4f");
private static final byte[] REAL_NAME = VPackWireFixtureTest.hex(
            "48 72 65 61 6c 4e 61 6d 65");
private static final byte[] WRAPPER_LOWER = VPackWireFixtureTest.hex(
            "14 11 45 73 61 75 63 65 47 6b 65 74 63 68 75 70 01");
private static final byte[] WRAPPER_UPPER = VPackWireFixtureTest.hex(
            "14 11 45 73 61 75 63 65 47 4b 45 54 43 48 55 50 01");
private static final byte[] WRAPPER_MIXED = VPackWireFixtureTest.hex(
            "14 11 45 73 61 75 63 65 47 6b 45 74 43 68 55 70 01");

    // Provenance: EnumNamingDeserializationTest#testEnumMixInDeserializationTest.
    void testEnumMixInDeserializationTestVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(BaseEnum.class, MixInEnum.class)
                .build();

        assertArrayEquals(REAL_NAME, mapper.writeValueAsBytes(BaseEnum.REAL_NAME));
        assertEquals(BaseEnum.REAL_NAME, mapper.readValue(REAL_NAME, BaseEnum.class));
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingStrategyConflictWithUnderScores.
    void testEnumNamingStrategyConflictWithUnderScoresVpack() throws Exception {
        EnumFlavorE flavor = MAPPER.readValue(PEANUT_BUTTER_CAMEL, EnumFlavorE.class);
        assertEquals(EnumFlavorE.PEANUT__BUTTER, flavor);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingStrategyInterfaceIsNotApplied.
    void testEnumNamingStrategyInterfaceIsNotAppliedVpack() throws Exception {
        EnumSauceD sauce = MAPPER.readValue(SRIRACHA_MAYO, EnumSauceD.class);
        assertEquals(EnumSauceD.SRIRACHA_MAYO, sauce);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingStrategySymmetryReadThenWrite.
    void testEnumNamingStrategySymmetryReadThenWriteVpack() throws Exception {
        EnumSauceC result = MAPPER.readValue(KETCH_UP, EnumSauceC.class);
        assertEquals(EnumSauceC.KETCH_UP, result);
        assertArrayEquals(KETCH_UP, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingStrategySymmetryWriteThenRead.
    void testEnumNamingStrategySymmetryWriteThenReadVpack() throws Exception {
        assertArrayEquals(MAYO_NEZZ, MAPPER.writeValueAsBytes(EnumSauceC.MAYO_NEZZ));
        EnumSauceC result = MAPPER.readValue(MAYO_NEZZ, EnumSauceC.class);
        assertEquals(EnumSauceC.MAYO_NEZZ, result);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingToDefaultEmptyString.
    void testEnumNamingToDefaultEmptyStringVpack() throws Exception {
        EnumFlavorA result = MAPPER.readerFor(EnumFlavorA.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .readValue(EMPTY_STRING);
        assertEquals(EnumFlavorA.VANILLA, result);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingToDefaultNumber.
    void testEnumNamingToDefaultNumberVpack() throws Exception {
        EnumFlavorA result = MAPPER.readerFor(EnumFlavorA.class)
                .without(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
                .readValue(ENUM_ORDINAL_ONE);
        assertEquals(EnumFlavorA.SALTED_CARAMEL, result);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingTranslateUnknownValueToDefault.
    void testEnumNamingTranslateUnknownValueToDefaultVpack() throws Exception {
        EnumFlavorA result = MAPPER.readerFor(EnumFlavorA.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .readValue(UNKNOWN_SALTED_CARAMEL);
        assertEquals(EnumFlavorA.VANILLA, result);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingWithAliasOrProperty.
    void testEnumNamingWithAliasOrPropertyVpack() throws Exception {
        assertEquals(EnumFlavorF.PEANUT_BUTTER,
                MAPPER.readValue(PEANUT_BUTTER_CAMEL, EnumFlavorF.class));
        assertEquals(EnumFlavorF.CHOCOLATE, MAPPER.readValue(CHOCOLATE, EnumFlavorF.class));
        assertEquals(EnumFlavorF.CHOCOLATE,
                MAPPER.readValue(MILK_CHOCOLATE, EnumFlavorF.class));
        assertEquals(EnumFlavorF.SALTED_CARAMEL,
                MAPPER.readValue(CARAMEL, EnumFlavorF.class));

        EnumFlavorF badCaramel = MAPPER.readerFor(EnumFlavorF.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(SALTED_CARAMEL_CAMEL);
        assertNull(badCaramel);
    }

    // Provenance: EnumNamingDeserializationTest#testEnumNamingWithLowerCamelCaseStrategy.
    void testEnumNamingWithLowerCamelCaseStrategyVpack() throws Exception {
        EnumFlavorA result = MAPPER.readValue(SALTED_CARAMEL, EnumFlavorA.class);
        assertEquals(EnumFlavorA.SALTED_CARAMEL, result);
        assertArrayEquals(SALTED_CARAMEL, MAPPER.writeValueAsBytes(result));
    }

    // Provenance: EnumNamingDeserializationTest#testOriginalEnamValueShouldNotBeFoundWithEnumNamingStrategy.
    void testOriginalEnamValueShouldNotBeFoundWithEnumNamingStrategyVpack() throws Exception {
        EnumFlavorB result = MAPPER.readerFor(EnumFlavorB.class)
                .with(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .readValue(PEANUT_BUTTER);
        assertNull(result);
    }

    // Provenance: EnumNamingDeserializationTest#testReadWrapperValueWithCaseInsensitiveEnumNamingStrategy.
    void testReadWrapperValueWithCaseInsensitiveEnumNamingStrategyVpack() throws Exception {
        ObjectReader reader = CI_MAPPER.readerFor(EnumSauceWrapperBean.class);
        EnumSauceWrapperBean lower = reader.readValue(WRAPPER_LOWER);
        EnumSauceWrapperBean upper = reader.readValue(WRAPPER_UPPER);
        EnumSauceWrapperBean mixed = reader.readValue(WRAPPER_MIXED);
        assertEquals(EnumSauceC.KETCH_UP, lower.sauce);
        assertEquals(EnumSauceC.KETCH_UP, upper.sauce);
        assertEquals(EnumSauceC.KETCH_UP, mixed.sauce);
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorA {
        PEANUT_BUTTER,
        SALTED_CARAMEL,
        @JsonEnumDefaultValue
        VANILLA
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorB {
        PEANUT_BUTTER
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumSauceC {
        KETCH_UP,
        MAYO_NEZZ
    }
@EnumNaming(EnumNamingStrategy.class)
    enum EnumSauceD {
        BARBEQ_UE,
        SRIRACHA_MAYO
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorE {
        _PEANUT_BUTTER,
        PEANUT__BUTTER,
        PEANUT_BUTTER
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorF {
        PEANUT_BUTTER,
        @JsonProperty("caramel")
        SALTED_CARAMEL,
        @JsonAlias({"darkChocolate", "milkChocolate", "whiteChocolate"})
        CHOCOLATE
    }
static class EnumSauceWrapperBean {
        public EnumSauceC sauce;

        @JsonCreator
        public EnumSauceWrapperBean(@JsonProperty("sce") EnumSauceC sce) {
            this.sauce = sce;
        }
    }
enum BaseEnum {
        REAL_NAME
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum MixInEnum {
        REAL_NAME
    }

    void __invoke_testEnumMixInDeserializationTestVpack() throws Exception {
        try {
            testEnumMixInDeserializationTestVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingStrategyConflictWithUnderScoresVpack() throws Exception {
        try {
            testEnumNamingStrategyConflictWithUnderScoresVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingStrategyInterfaceIsNotAppliedVpack() throws Exception {
        try {
            testEnumNamingStrategyInterfaceIsNotAppliedVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingStrategySymmetryReadThenWriteVpack() throws Exception {
        try {
            testEnumNamingStrategySymmetryReadThenWriteVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingStrategySymmetryWriteThenReadVpack() throws Exception {
        try {
            testEnumNamingStrategySymmetryWriteThenReadVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingToDefaultEmptyStringVpack() throws Exception {
        try {
            testEnumNamingToDefaultEmptyStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingToDefaultNumberVpack() throws Exception {
        try {
            testEnumNamingToDefaultNumberVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingTranslateUnknownValueToDefaultVpack() throws Exception {
        try {
            testEnumNamingTranslateUnknownValueToDefaultVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingWithAliasOrPropertyVpack() throws Exception {
        try {
            testEnumNamingWithAliasOrPropertyVpack();
        } finally {
        }
    }


    void __invoke_testEnumNamingWithLowerCamelCaseStrategyVpack() throws Exception {
        try {
            testEnumNamingWithLowerCamelCaseStrategyVpack();
        } finally {
        }
    }


    void __invoke_testOriginalEnamValueShouldNotBeFoundWithEnumNamingStrategyVpack() throws Exception {
        try {
            testOriginalEnamValueShouldNotBeFoundWithEnumNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testReadWrapperValueWithCaseInsensitiveEnumNamingStrategyVpack() throws Exception {
        try {
            testReadWrapperValueWithCaseInsensitiveEnumNamingStrategyVpack();
        } finally {
        }
    }

}
