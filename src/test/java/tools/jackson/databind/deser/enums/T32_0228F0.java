package tools.jackson.databind.deser.enums;

import java.util.EnumMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0228F0 {
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

    // Provenance: EnumDeserializerJsonValue5271Test#convertStringToEnum.
    void convertStringToEnumVpack() throws Exception {
        ObjectReader byJsonValue = MAPPER.readerFor(Enum5271.class)
                .without(EnumFeature.READ_ENUMS_USING_TO_STRING);
        ObjectReader byToString = MAPPER.readerFor(Enum5271.class)
                .with(EnumFeature.READ_ENUMS_USING_TO_STRING);

        assertSame(Enum5271.T20, byJsonValue.readValue(TWENTY_PERCENT));
        assertSame(Enum5271.T20, byToString.readValue(TWENTY_PERCENT));
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

    void __invoke_convertStringToEnumVpack() throws Exception {
        try {
            convertStringToEnumVpack();
        } finally {
        }
    }

}
