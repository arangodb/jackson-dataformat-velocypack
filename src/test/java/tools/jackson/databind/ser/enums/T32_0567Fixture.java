package tools.jackson.databind.ser.enums;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.JacksonSerializable;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.ser.std.ToStringSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0567Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumSerializationTest#testSimple().
    void testSimpleVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"),
                MAPPER.writeValueAsBytes(TestEnum.B));
    }

    // Provenance: EnumSerializationTest#testEnumUsingToString().
    void testEnumUsingToStringVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("42 63 32"),
                MAPPER.writeValueAsBytes(AnnotatedTestEnum.C2));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonValue().
    void testEnumsWithJsonValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "49 76 61 6c 75 65 3a 62 61 72"),
                MAPPER.writeValueAsBytes(EnumWithJsonValue.B));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonValueUsingMixin().
    void testEnumsWithJsonValueUsingMixinVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(TestEnum.class, ToStringMixin.class)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("41 62"),
                mapper.writeValueAsBytes(TestEnum.B));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonValueInMap().
    void testEnumsWithJsonValueInMapVpack() throws Exception {
        EnumMap<EnumWithJsonValue, String> input =
                new EnumMap<>(EnumWithJsonValue.class);
        input.put(EnumWithJsonValue.B, "x");
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 10 01 49 76 61 6c 75 65 3a 62 61 72 41 78 03"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testSerializableEnum().
    void testSerializableEnumVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("43 66 6f 6f"),
                MAPPER.writeValueAsBytes(SerializableEnum.A));
    }

    // Provenance: EnumSerializationTest#testGenericEnumSerializer().
    void testGenericEnumSerializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("foobar");
        module.addSerializer(Enum.class, new LowerCasingEnumSerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(VPackWireFixtureTest.hex("41 62"),
                mapper.writeValueAsBytes(TestEnum.B));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonProperty().
    void testEnumsWithJsonPropertyVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("45 61 6c 65 70 68"),
                MAPPER.writeValueAsBytes(EnumWithJsonProperty.A));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonPropertyEnableToString().
    void testEnumsWithJsonPropertyEnableToStringVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("45 61 6c 65 70 68"),
                MAPPER.writerFor(EnumWithJsonProperty.class)
                .with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .writeValueAsBytes(EnumWithJsonProperty.A));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonPropertyInSet().
    void testEnumsWithJsonPropertyInSetVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "02 08 45 61 6c 65 70 68"),
                MAPPER.writeValueAsBytes(EnumSet.of(EnumWithJsonProperty.A)));
    }

    // Provenance: EnumSerializationTest#testEnumsWithJsonPropertyAsKey().
    void testEnumsWithJsonPropertyAsKeyVpack() throws Exception {
        EnumMap<EnumWithJsonProperty, String> input =
                new EnumMap<>(EnumWithJsonProperty.class);
        input.put(EnumWithJsonProperty.A, "b");
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 45 61 6c 65 70 68 41 62 03"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: EnumSerializationTest#testEnumWithJsonKey().
    void testEnumWithJsonKeyVpack() throws Exception {
        EnumMap<EnumWithJsonKey, EnumWithJsonKey> input1 =
                new EnumMap<>(EnumWithJsonKey.class);
        input1.put(EnumWithJsonKey.A, EnumWithJsonKey.B);
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 01 45 6b 65 79 3a 61 47 76 61 6c 75 65 3a 62 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input1));

        Map<EnumWithJsonKey, EnumWithJsonKey> input2 =
                Collections.singletonMap(EnumWithJsonKey.A, EnumWithJsonKey.B);
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input2));
    }
enum TestEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
@JsonSerialize(using = ToStringSerializer.class)
    enum AnnotatedTestEnum {
        A2, B2, C2;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
enum EnumWithJsonValue {
        A("foo"), B("bar");

        private final String name;

        EnumWithJsonValue(String name) {
            this.name = name;
        }

        @JsonValue
        public String external() {
            return "value:" + name;
        }
    }
interface ToStringMixin {
        @JsonValue
        String toString();
    }
enum SerializableEnum implements JacksonSerializable {
        A, B, C;

        @Override
        public void serialize(JsonGenerator generator, SerializationContext provider) {
            generator.writeString("foo");
        }

        @Override
        public void serializeWithType(JsonGenerator generator,
                SerializationContext provider, TypeSerializer typeSer) {
            serialize(generator, provider);
        }
    }
@SuppressWarnings("rawtypes")
    static class LowerCasingEnumSerializer extends StdSerializer<Enum> {
        LowerCasingEnumSerializer() {
            super(Enum.class);
        }

        @Override
        public void serialize(Enum value, JsonGenerator generator,
                SerializationContext provider) {
            generator.writeString(value.name().toLowerCase());
        }
    }
enum EnumWithJsonProperty {
        @JsonProperty("aleph")
        A
    }
enum EnumWithJsonKey {
        A("a"), B("b");

        private final String name;

        EnumWithJsonKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        @JsonKey
        public String externalKey() {
            return "key:" + name;
        }

        @JsonValue
        public String externalValue() {
            return "value:" + name;
        }
    }

    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }


    void __invoke_testEnumUsingToStringVpack() throws Exception {
        try {
            testEnumUsingToStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonValueVpack() throws Exception {
        try {
            testEnumsWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonValueUsingMixinVpack() throws Exception {
        try {
            testEnumsWithJsonValueUsingMixinVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonValueInMapVpack() throws Exception {
        try {
            testEnumsWithJsonValueInMapVpack();
        } finally {
        }
    }


    void __invoke_testSerializableEnumVpack() throws Exception {
        try {
            testSerializableEnumVpack();
        } finally {
        }
    }


    void __invoke_testGenericEnumSerializerVpack() throws Exception {
        try {
            testGenericEnumSerializerVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonPropertyVpack() throws Exception {
        try {
            testEnumsWithJsonPropertyVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonPropertyEnableToStringVpack() throws Exception {
        try {
            testEnumsWithJsonPropertyEnableToStringVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonPropertyInSetVpack() throws Exception {
        try {
            testEnumsWithJsonPropertyInSetVpack();
        } finally {
        }
    }


    void __invoke_testEnumsWithJsonPropertyAsKeyVpack() throws Exception {
        try {
            testEnumsWithJsonPropertyAsKeyVpack();
        } finally {
        }
    }


    void __invoke_testEnumWithJsonKeyVpack() throws Exception {
        try {
            testEnumWithJsonKeyVpack();
        } finally {
        }
    }

}
