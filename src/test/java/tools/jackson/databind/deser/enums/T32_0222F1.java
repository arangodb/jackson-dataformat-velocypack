package tools.jackson.databind.deser.enums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.exc.InvalidFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0222F1 {
private static final ObjectMapper ENUM_MIXIN_MAPPER = VPackMapper.builder()
            .disable(EnumFeature.READ_ENUMS_USING_TO_STRING)
            .addMixIn(Enum2787.class, EnumMixin2787.class)
            .build();
private static final byte[] B_MIXIN_PROP = VPackWireFixtureTest.hex(
            "4c 42 5f 4d 49 58 49 4e 5f 50 52 4f 50");
private static final byte[] B_MIXIN_PROP_MIXED_CASE = VPackWireFixtureTest.hex(
            "4c 42 5f 6d 49 78 49 6e 5f 70 52 6f 70");
private static final byte[] ITEM_ORIGIN = VPackWireFixtureTest.hex(
            "4b 49 54 45 4d 5f 4f 52 49 47 49 4e");
private static final byte[] ITEM_MIXIN = VPackWireFixtureTest.hex(
            "4a 49 54 45 4d 5f 4d 49 58 49 4e");
private static final byte[] C_MIXIN_ALIAS_1 = VPackWireFixtureTest.hex(
            "4f 43 5f 4d 49 58 49 4e 5f 41 4c 49 41 53 5f 31");
private static final byte[] TAX10 = VPackWireFixtureTest.hex(
            "45 74 61 78 31 30");
private static final byte[] B_ORIGIN_PROP = VPackWireFixtureTest.hex(
            "4d 42 5f 4f 52 49 47 49 4e 5f 50 52 4f 50");
private static final byte[] TAX30 = VPackWireFixtureTest.hex(
            "45 74 61 78 33 30");
private static final byte[] SHOULD_NOT_EXIST = VPackWireFixtureTest.hex(
            "50 73 68 6f 75 6c 64 2d 6e 6f 74 2d 65 78 69 73 74");
private static final byte[] BEAN_MIXIN = VPackWireFixtureTest.hex(
            "14 11 47 78 5f 6d 69 78 69 6e 45 76 61 6c 75 65 01");
private static final byte[] WRAPPED_MIXIN_ALIAS = VPackWireFixtureTest.hex(
            "14 19 45 76 61 6c 75 65 4f 43 5f 4d 49 58 49 4e 5f 41 4c 49 41 53 5f 31 01");
private static final byte[] NUMERIC_ENUM_MAP_KEY = VPackWireFixtureTest.hex(
            "14 0b 41 37 45 6c 75 63 6b 79 01");

    // Provenance: EnumDeserNumberJsonProperty5330Test#shouldDeserializeEnumMapKeysUsingNumericJsonPropertyIndex.
    void shouldDeserializeEnumMapKeysUsingNumericJsonPropertyIndexVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        JavaType type = mapper.getTypeFactory().constructMapType(HashMap.class,
                MyEnum.class, String.class);
        Map<MyEnum, String> result = mapper.readValue(NUMERIC_ENUM_MAP_KEY, type);
        assertEquals(1, result.size());
        assertEquals("lucky", result.get(MyEnum.FOO));
    }
private void assertInvalidEnum(byte[] fixture, String value) {
        InvalidFormatException exception = assertThrows(InvalidFormatException.class,
                () -> ENUM_MIXIN_MAPPER.readValue(fixture, Enum2787.class));
        assertEquals(Enum2787.class, exception.getTargetType());
        assertTrue(exception.getMessage().contains(value));
        assertTrue(exception.getMessage().contains("not one of the values accepted"));
    }
enum Enum2787 {
        ITEM_A,
        @JsonAlias({"B_ORIGIN_ALIAS_1", "B_ORIGIN_ALIAS_2"})
        @JsonProperty("B_ORIGIN_PROP")
        ITEM_B,
        @JsonAlias("C_ORIGIN_ALIAS")
        @JsonProperty("C_ORIGIN_PROP")
        ITEM_C,
        ITEM_ORIGIN
    }
enum EnumMixin2787 {
        ITEM_A,
        @JsonProperty("B_MIXIN_PROP")
        ITEM_B,
        @JsonAlias({"C_MIXIN_ALIAS_1", "C_MIXIN_ALIAS_2"})
        @JsonProperty("C_MIXIN_PROP")
        ITEM_C,
        ITEM_MIXIN;

        @Override
        public String toString() {
            return "SHOULD NOT USE WITH TO STRING";
        }
    }
static class EnumWrapper {
        public Enum2787 value;
    }
static class Bean2787 {
        public String x;
    }
static class BeanMixin2787 {
        @JsonProperty("x_mixin")
        public String x;
    }
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
    enum MyEnum {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }

    void __invoke_shouldDeserializeEnumMapKeysUsingNumericJsonPropertyIndexVpack() throws Exception {
        try {
            shouldDeserializeEnumMapKeysUsingNumericJsonPropertyIndexVpack();
        } finally {
        }
    }

}
