package tools.jackson.databind.ser.enums;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0566F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumSerializationMixinTest#testSerialization().
    void testSerializationVpack() throws Exception {
        ObjectMapper mixinMapper = VPackMapper.builder()
                .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .addMixIn(EnumBaseA.class, EnumMixinA.class)
                .build();

        assertArrayEquals(VPackWireFixtureTest.hex("46 49 54 45 4d 5f 41"),
                mixinMapper.writeValueAsBytes(EnumBaseA.ITEM_A));
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "4c 42 5f 4d 49 58 49 4e 5f 50 52 4f 50"),
                mixinMapper.writeValueAsBytes(EnumBaseA.ITEM_B));
        assertArrayEquals(VPackWireFixtureTest.hex("48 43 5f 43 4f 4d 4d 4f 4e"),
                mixinMapper.writeValueAsBytes(EnumBaseA.ITEM_C_BASE));
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "4b 49 54 45 4d 5f 4f 52 49 47 49 4e"),
                mixinMapper.writeValueAsBytes(EnumBaseA.ITEM_ORIGIN));
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

    void __invoke_testSerializationVpack() throws Exception {
        try {
            testSerializationVpack();
        } finally {
        }
    }

}
