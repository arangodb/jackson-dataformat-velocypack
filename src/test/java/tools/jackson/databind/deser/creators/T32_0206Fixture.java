package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EnumSet;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.ValueInstantiationException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0206Fixture {
private static final byte[] PROPERTIES_ENUM_TEST1 = VPackWireFixtureTest.hex(
            "14 1f 44 6e 61 6d 65 45 54 45 53 54 31 "
            + "4b 64 65 73 63 72 69 70 74 69 6f 6e 44 54 45 53 54 02");
private static final byte[] PROPERTIES_ENUM_TEST3 = VPackWireFixtureTest.hex(
            "14 1f 44 6e 61 6d 65 45 54 45 53 54 33 "
            + "4b 64 65 73 63 72 69 70 74 69 6f 6e 44 54 45 53 54 02");
private static final byte[] CHANNEL_CODE = VPackWireFixtureTest.hex(
            "14 09 44 63 6f 64 65 31 01");
private static final byte[] CHANNEL_NO_CODE = VPackWireFixtureTest.hex(
            "14 0f 44 64 65 73 63 46 41 6c 69 70 61 79 01");
private static final byte[] ENUM_A = VPackWireFixtureTest.hex(
            "45 65 6e 75 6d 41");
private static final byte[] ENUM_MAP = VPackWireFixtureTest.hex(
            "14 0f 45 65 6e 75 6d 41 45 76 61 6c 75 65 01");
private static final byte[] ENUM_SET = VPackWireFixtureTest.hex(
            "13 09 45 65 6e 75 6d 41 01");
private static final byte[] ENUM_INT = VPackWireFixtureTest.hex("31");
private static final byte[] ENUM_INT_ARRAY = VPackWireFixtureTest.hex(
            "13 04 31 01");
private static final byte[] ENUM_STRING_ARRAY = VPackWireFixtureTest.hex(
            "13 0a 46 45 4e 55 4d 5f 41 01");
private static final byte[] ENUM_BAD_TEXT = VPackWireFixtureTest.hex(
            "43 78 79 7a");
private static final byte[] IMPLICIT_ENUM_OBJECT = VPackWireFixtureTest.hex(
            "14 09 44 64 61 74 61 30 01");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: EnumCreatorTest#testJsonCreatorPropertiesWithEnum.
    void testJsonCreatorPropertiesWithEnum() throws Exception {
        EnumWithPropertiesModeJsonCreator type1 = MAPPER.readValue(
                PROPERTIES_ENUM_TEST1, EnumWithPropertiesModeJsonCreator.class);
        assertSame(EnumWithPropertiesModeJsonCreator.TEST1, type1);

        EnumWithPropertiesModeJsonCreator type3 = MAPPER.readValue(
                PROPERTIES_ENUM_TEST3, EnumWithPropertiesModeJsonCreator.class);
        assertSame(EnumWithPropertiesModeJsonCreator.TEST3, type3);
    }

    // Provenance: EnumCreatorTest#testJsonCreatorDelagateWithEnum.
    void testJsonCreatorDelagateWithEnum() throws Exception {
        EnumWithDelegateModeJsonCreator type1 = MAPPER.readValue(
                PROPERTIES_ENUM_TEST1, EnumWithDelegateModeJsonCreator.class);
        assertSame(EnumWithDelegateModeJsonCreator.TEST1, type1);

        EnumWithDelegateModeJsonCreator type3 = MAPPER.readValue(
                PROPERTIES_ENUM_TEST3, EnumWithDelegateModeJsonCreator.class);
        assertSame(EnumWithDelegateModeJsonCreator.TEST3, type3);
    }

    // Provenance: EnumCreatorTest#testEnumWithCreatorMaps.
    void testEnumWithCreatorMaps() throws Exception {
        HashMap<EnumWithCreator, String> result = MAPPER.readValue(ENUM_MAP,
                new tools.jackson.core.type.TypeReference<HashMap<EnumWithCreator, String>>() { });
        assertEquals("value", result.get(EnumWithCreator.A));
    }

    // Provenance: EnumCreatorTest#testEnumWithCreatorEnumSets.
    void testEnumWithCreatorEnumSets() throws Exception {
        EnumSet<EnumWithCreator> result = MAPPER.readValue(ENUM_SET,
                new tools.jackson.core.type.TypeReference<EnumSet<EnumWithCreator>>() { });
        assertTrue(result.contains(EnumWithCreator.A));
    }

    // Provenance: EnumCreatorTest#testEnumsFromInts.
    void testEnumsFromInts() throws Exception {
        Object result = MAPPER.readValue(ENUM_INT, TestEnumFromInt.class);
        assertEquals(TestEnumFromInt.class, result.getClass());
        assertSame(TestEnumFromInt.ENUM_A, result);
    }

    // Provenance: EnumCreatorTest#testExceptionFromCreator.
    void testExceptionFromCreator() throws Exception {
        ValueInstantiationException exception = assertThrows(ValueInstantiationException.class,
                () -> MAPPER.readValue(ENUM_BAD_TEXT, TestEnum324.class));
        assertTrue(exception.getMessage().contains("Foobar"));
    }

    // Provenance: EnumCreatorTest#testEnumsFromIntsUnwrapped.
    void testEnumsFromIntsUnwrapped() throws Exception {
        Object result = MAPPER.readerFor(TestEnumFromInt.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(ENUM_INT_ARRAY);
        assertEquals(TestEnumFromInt.class, result.getClass());
        assertSame(TestEnumFromInt.ENUM_A, result);
    }

    // Provenance: EnumCreatorTest#testEnumsFromStringUnwrapped.
    void testEnumsFromStringUnwrapped() throws Exception {
        Object result = MAPPER.readerFor(TestEnumFromString.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(ENUM_STRING_ARRAY);
        assertEquals(TestEnumFromString.class, result.getClass());
        assertSame(TestEnumFromString.ENUM_A, result);
    }

    // Provenance: EnumCreatorTest#testEnumsWithImplicitNames4544.
    void testEnumsWithImplicitNames4544() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new ImplicitNameIntrospector())
                .build();
        DataClass4544 data = mapper.readValue(IMPLICIT_ENUM_OBJECT, DataClass4544.class);
        assertEquals(DataEnum4544.TEST, data.data);
    }

    // Provenance: EnumCreatorTest#testEnumDualModeProperties5395.
    void testEnumDualModeProperties5395() throws Exception {
        ChannelEnum channel = MAPPER.readValue(CHANNEL_CODE, ChannelEnum.class);
        assertSame(ChannelEnum.WECHAT, channel);
    }

    // Provenance: EnumCreatorTest#testEnumDualModeNoCode5395.
    void testEnumDualModeNoCode5395() throws Exception {
        ChannelEnum channel = MAPPER.readValue(CHANNEL_NO_CODE, ChannelEnum.class);
        assertNull(channel);
    }
enum EnumWithCreator {
        A, B;

        @JsonCreator
        static EnumWithCreator fromEnum(String value) {
            if ("enumA".equals(value)) return A;
            if ("enumB".equals(value)) return B;
            return null;
        }
    }
enum EnumWithPropertiesModeJsonCreator {
        TEST1, TEST2, TEST3;

        @JsonGetter("name")
        public String getName() { return name(); }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        static EnumWithPropertiesModeJsonCreator create(@JsonProperty("name") String name) {
            return valueOf(name);
        }
    }
enum EnumWithDelegateModeJsonCreator {
        TEST1, TEST2, TEST3;

        @JsonGetter("name")
        public String getName() { return name(); }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static EnumWithDelegateModeJsonCreator create(JsonNode json) {
            return valueOf(json.get("name").asString());
        }
    }
enum TestEnumFromInt {
        ENUM_A(1), ENUM_B(2), ENUM_C(3);

        private final int id;

        TestEnumFromInt(int id) { this.id = id; }

        @JsonCreator
        static TestEnumFromInt fromId(int id) {
            for (TestEnumFromInt value : values()) {
                if (value.id == id) return value;
            }
            return null;
        }
    }
enum TestEnumFromString {
        ENUM_A, ENUM_B, ENUM_C;

        @JsonCreator
        static TestEnumFromString fromId(String id) { return valueOf(id); }
    }
enum TestEnum324 {
        A, B;

        @JsonCreator
        static TestEnum324 creator(String value) {
            throw new RuntimeException("Foobar!");
        }
    }
static class DataClass4544 {
        public DataEnum4544 data;
    }
enum DataEnum4544 {
        TEST(0);

        private final int data;

        DataEnum4544(int data) { this.data = data; }

        @JsonIgnore
        public int getData() { return data; }

        @JsonCreator
        static DataEnum4544 of(@ImplicitName("data") int data) {
            return TEST;
        }
    }
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @JsonIncludeProperties({ "code", "desc" })
    enum ChannelEnum {
        ALIPAY(0, "Alipay"), WECHAT(1, "WeChat");

        private final int code;
        private final String desc;

        ChannelEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @JsonProperty("code")
        public int getCode() { return code; }

        @JsonProperty("desc")
        public String getDesc() { return desc; }

        @JsonCreator
        private static ChannelEnum ofCode(@JsonProperty("code") String code) {
            if (code == null) return null;
            int intCode = Integer.parseInt(code);
            for (ChannelEnum value : values()) {
                if (value.code == intCode) return value;
            }
            return null;
        }
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                ImplicitName annotation = parameter.getAnnotation(ImplicitName.class);
                if (annotation != null) return annotation.value();
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_testJsonCreatorPropertiesWithEnum() throws Exception {
        try {
            testJsonCreatorPropertiesWithEnum();
        } finally {
        }
    }


    void __invoke_testJsonCreatorDelagateWithEnum() throws Exception {
        try {
            testJsonCreatorDelagateWithEnum();
        } finally {
        }
    }


    void __invoke_testEnumWithCreatorMaps() throws Exception {
        try {
            testEnumWithCreatorMaps();
        } finally {
        }
    }


    void __invoke_testEnumWithCreatorEnumSets() throws Exception {
        try {
            testEnumWithCreatorEnumSets();
        } finally {
        }
    }


    void __invoke_testEnumsFromInts() throws Exception {
        try {
            testEnumsFromInts();
        } finally {
        }
    }


    void __invoke_testExceptionFromCreator() throws Exception {
        try {
            testExceptionFromCreator();
        } finally {
        }
    }


    void __invoke_testEnumsFromIntsUnwrapped() throws Exception {
        try {
            testEnumsFromIntsUnwrapped();
        } finally {
        }
    }


    void __invoke_testEnumsFromStringUnwrapped() throws Exception {
        try {
            testEnumsFromStringUnwrapped();
        } finally {
        }
    }


    void __invoke_testEnumsWithImplicitNames4544() throws Exception {
        try {
            testEnumsWithImplicitNames4544();
        } finally {
        }
    }


    void __invoke_testEnumDualModeProperties5395() throws Exception {
        try {
            testEnumDualModeProperties5395();
        } finally {
        }
    }


    void __invoke_testEnumDualModeNoCode5395() throws Exception {
        try {
            testEnumDualModeNoCode5395();
        } finally {
        }
    }

}
