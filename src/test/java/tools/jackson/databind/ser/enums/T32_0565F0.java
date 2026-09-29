package tools.jackson.databind.ser.enums;

import java.util.EnumMap;
import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.EnumNamingStrategy;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0565F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();
private static final ObjectMapper INDEX_MAPPER = VPackMapper.builder()
            .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
            .build();
private static final byte[] HOT_CHOCOLATE = VPackWireFixtureTest.hex(
            "5b 68 6f 74 43 68 6f 63 6f 6c 61 74 65 43 68 65 65 74 6f 73 41 6e 64 43 68 69 70 73");
private static final byte[] SRIRACHA = VPackWireFixtureTest.hex(
            "4d 53 52 49 52 41 43 48 41 5f 4d 41 59 4f");
private static final byte[] PEANUT_UPPER = VPackWireFixtureTest.hex(
            "4c 50 65 61 6e 75 74 42 75 74 74 65 72");
private static final byte[] PEANUT_LOWER = VPackWireFixtureTest.hex(
            "4c 70 65 61 6e 75 74 42 75 74 74 65 72");
private static final byte[] KETCH_UP = VPackWireFixtureTest.hex(
            "47 6b 65 74 63 68 55 70");
private static final byte[] ALMOND = VPackWireFixtureTest.hex(
            "46 61 6c 6d 6f 6e 64");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "0b 13 01 48 6d 61 79 6f 4e 65 7a 7a 45 76 61 6c 75 65 03");
private static final byte[] NUMERIC_BEAN = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 2a 03");
private static final byte[] NUMERIC_LIST = VPackWireFixtureTest.hex(
            "06 08 02 37 28 2a 03 04");
private static final byte[] NUMERIC_SET = VPackWireFixtureTest.hex(
            "02 03 37");
private static final byte[] NUMERIC_MAP_KEY = VPackWireFixtureTest.hex(
            "0b 0c 01 41 37 45 6c 75 63 6b 79 03");
private static final byte[] ORDINAL_LIST = VPackWireFixtureTest.hex(
            "02 04 30 31");
private static final byte[] ORDINAL_SET = VPackWireFixtureTest.hex(
            "02 03 31");
private static final byte[] NON_NUMERIC = VPackWireFixtureTest.hex(
            "4c 4e 4f 54 5f 41 5f 4e 55 4d 42 45 52");

    // Provenance: EnumNamingSerializationTest#enumNamingShouldOverrideToStringFeature().
    void enumNamingShouldOverrideToStringFeatureVpack() throws Exception {
        assertArrayEquals(HOT_CHOCOLATE, MAPPER.writer()
                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(EnumFlavorA.CHOCOLATE_CHIPS));
    }

    // Provenance: EnumNamingSerializationTest#enumNamingStrategyNotApplied().
    void enumNamingStrategyNotAppliedVpack() throws Exception {
        assertArrayEquals(SRIRACHA,
                MAPPER.writeValueAsBytes(EnumSauceC.SRIRACHA_MAYO));
    }

    // Provenance: EnumNamingSerializationTest#enumNamingStrategyStartingUnderscoreBecomesUpperCase().
    void enumNamingStrategyStartingUnderscoreBecomesUpperCaseVpack() throws Exception {
        assertArrayEquals(PEANUT_UPPER,
                MAPPER.writeValueAsBytes(EnumFlavorD._PEANUT_BUTTER));
    }

    // Provenance: EnumNamingSerializationTest#enumNamingStrategyNonPrefixContiguousUnderscoresBecomeOne().
    void enumNamingStrategyNonPrefixContiguousUnderscoresBecomeOneVpack() throws Exception {
        assertArrayEquals(PEANUT_LOWER,
                MAPPER.writeValueAsBytes(EnumFlavorD.PEANUT__BUTTER));
        assertArrayEquals(PEANUT_LOWER,
                MAPPER.writeValueAsBytes(EnumFlavorD.PEANUT_BUTTER));
    }

    // Provenance: EnumNamingSerializationTest#enumSetWrite().
    void enumSetWriteVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 0a 47 6b 65 74 63 68 55 70"),
                MAPPER.writeValueAsBytes(EnumSet.of(EnumSauceB.KETCH_UP)));
    }

    // Provenance: EnumNamingSerializationTest#enumMapWrite().
    void enumMapWriteVpack() throws Exception {
        EnumMap<EnumSauceB, String> enums = new EnumMap<>(EnumSauceB.class);
        enums.put(EnumSauceB.MAYO_NEZZ, "value");

        assertArrayEquals(ENUM_MAP, MAPPER.writeValueAsBytes(enums));
    }

    // Provenance: EnumNamingSerializationTest#enumNamingStrategyWithOverride().
    void enumNamingStrategyWithOverrideVpack() throws Exception {
        assertArrayEquals(ALMOND,
                MAPPER.writeValueAsBytes(EnumFlavorE.ALMOND_BUTTER));
        assertArrayEquals(PEANUT_LOWER,
                MAPPER.writeValueAsBytes(EnumFlavorE.PEANUT_BUTTER));
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorA {
        CHOCOLATE_CHIPS,
        HOT_CHEETOS;

        @Override
        public String toString() {
            return "HOT_CHOCOLATE_CHEETOS_AND_CHIPS";
        }
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumSauceB {
        KETCH_UP,
        MAYO_NEZZ;
    }
@EnumNaming(EnumNamingStrategy.class)
    enum EnumSauceC {
        BARBEQ_UE,
        SRIRACHA_MAYO;
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorD {
        _PEANUT_BUTTER,
        PEANUT__BUTTER,
        PEANUT_BUTTER
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum EnumFlavorE {
        PEANUT_BUTTER,
        @JsonProperty("almond")
        ALMOND_BUTTER
    }
public enum MyEnumNoFormat {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum MyEnum {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum NonNumericEnum {
        @JsonProperty("NOT_A_NUMBER")
        VALUE
    }
static class EnumBean {
        public MyEnum value = MyEnum.BAR;
    }

    void __invoke_enumNamingShouldOverrideToStringFeatureVpack() throws Exception {
        try {
            enumNamingShouldOverrideToStringFeatureVpack();
        } finally {
        }
    }


    void __invoke_enumNamingStrategyNotAppliedVpack() throws Exception {
        try {
            enumNamingStrategyNotAppliedVpack();
        } finally {
        }
    }


    void __invoke_enumNamingStrategyStartingUnderscoreBecomesUpperCaseVpack() throws Exception {
        try {
            enumNamingStrategyStartingUnderscoreBecomesUpperCaseVpack();
        } finally {
        }
    }


    void __invoke_enumNamingStrategyNonPrefixContiguousUnderscoresBecomeOneVpack() throws Exception {
        try {
            enumNamingStrategyNonPrefixContiguousUnderscoresBecomeOneVpack();
        } finally {
        }
    }


    void __invoke_enumSetWriteVpack() throws Exception {
        try {
            enumSetWriteVpack();
        } finally {
        }
    }


    void __invoke_enumMapWriteVpack() throws Exception {
        try {
            enumMapWriteVpack();
        } finally {
        }
    }


    void __invoke_enumNamingStrategyWithOverrideVpack() throws Exception {
        try {
            enumNamingStrategyWithOverrideVpack();
        } finally {
        }
    }

}
