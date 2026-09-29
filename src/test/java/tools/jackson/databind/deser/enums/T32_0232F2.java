package tools.jackson.databind.deser.enums;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0232F2 {
private static final byte[] SET_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0b 43 73 65 74 13 04 40 01 01");
private static final byte[] SET_FOO_EMPTY_STRING = VPackWireFixtureTest.hex(
            "14 0f 43 73 65 74 13 08 43 46 4f 4f 40 02 01");

    // Provenance: EnumWithNullToString4355Test#testWithNullToString.
    void testWithNullToStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(tools.jackson.databind.cfg.EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .build();

        assertArrayEquals(VPackWireFixtureTest.hex("45 41 4c 50 48 41"),
                mapper.writeValueAsBytes(Enum4355.ALPHA));
        assertArrayEquals(VPackWireFixtureTest.hex("44 42 45 54 41"),
                mapper.writeValueAsBytes(Enum4355.BETA));
        assertArrayEquals(VPackWireFixtureTest.hex("49 55 4e 44 45 46 49 4e 45 44"),
                mapper.writeValueAsBytes(Enum4355.UNDEFINED));
    }
private static ObjectMapper createMapperWithCustomDeserializer(Nulls nullHandling) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MyEnum.class, new EmptyStringToNullDeserializer());
        return VPackMapper.builder()
                .addModule(module)
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(nullHandling))
                .build();
    }
enum MyEnum { FOO }
static class Dst {
        private EnumSet<MyEnum> set;

        public EnumSet<MyEnum> getSet() { return set; }
        public void setSet(EnumSet<MyEnum> set) { this.set = set; }
    }
static class EmptyStringToNullDeserializer extends StdDeserializer<MyEnum> {
        EmptyStringToNullDeserializer() { super(MyEnum.class); }

        @Override
        public MyEnum deserialize(JsonParser p, DeserializationContext ctxt) {
            String value = p.getValueAsString();
            if (value != null && value.isEmpty()) {
                return null;
            }
            return MyEnum.valueOf(value);
        }
    }
enum MyEnum4214 { ITEM_A, ITEM_B }
static class EnumSetHolder {
        public Set<MyEnum4214> enumSet;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EnumSetHolder other)) {
                return false;
            }
            return Objects.equals(enumSet, other.enumSet);
        }
    }
enum Enum4355 {
        ALPHA("A"),
        BETA("B"),
        UNDEFINED(null);

        private final String value;

        Enum4355(String value) { this.value = value; }

        @Override
        public String toString() { return value; }
    }

    void __invoke_testWithNullToStringVpack() throws Exception {
        try {
            testWithNullToStringVpack();
        } finally {
        }
    }

}
