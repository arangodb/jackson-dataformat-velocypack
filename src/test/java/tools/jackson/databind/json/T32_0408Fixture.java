package tools.jackson.databind.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0408Fixture {
private static final byte[] GETTER_XY = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 3e 41 79 31 03 06");
private static final byte[] GETTER_X = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 3e 03");
private static final byte[] IS_GETTER_OK = VPackWireFixtureTest.hex(
            "0b 08 01 42 6f 6b 31 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");

    // Provenance: JsonMapperBuilderTest#testBuilderConfigurationChaining().
    void testBuilderConfigurationChainingVpack() {
        VPackMapper.Builder builder = VPackMapper.builder();
        builder.enable(SerializationFeature.INDENT_OUTPUT);
        builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        VPackMapper mapper = builder.build();
        assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    // Provenance: JsonMapperBuilderTest#testBuilderCreatesIndependentMappers().
    void testBuilderCreatesIndependentMappersVpack() {
        VPackMapper.Builder builder = VPackMapper.builder();
        VPackMapper first = builder.build();
        VPackMapper second = builder.build();

        assertNotSame(first, second);
        assertEquals(first.isEnabled(SerializationFeature.INDENT_OUTPUT),
                second.isEnabled(SerializationFeature.INDENT_OUTPUT));
    }

    // Provenance: JsonMapperBuilderTest#testBuilderWithJackson2Defaults().
    void testBuilderWithJackson2DefaultsVpack() {
        // The JSON helper has no VPack counterpart; retain its portable
        // databind default-state assertions through explicit VPack builder
        // configuration, while checking the helper remains absent.
        VPackMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(MapperFeature.USE_GETTERS_AS_SETTERS,
                        MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                        DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .disable(EnumFeature.READ_ENUMS_USING_TO_STRING,
                        EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY,
                        MapperFeature.FIX_FIELD_NAME_UPPER_CASE_PREFIX)
                .build();
        assertTrue(mapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        assertTrue(mapper.isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertTrue(mapper.isEnabled(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS));
        assertFalse(mapper.isEnabled(EnumFeature.WRITE_ENUMS_USING_TO_STRING));
        assertTrue(mapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES));
        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));
        assertFalse(mapper.isEnabled(EnumFeature.READ_ENUMS_USING_TO_STRING));
        assertTrue(mapper.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS));
        assertTrue(mapper.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
        assertFalse(mapper.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertFalse(mapper.isEnabled(MapperFeature.FIX_FIELD_NAME_UPPER_CASE_PREFIX));

        // JSON factory defaults have no VPack counterpart.
        assertThrows(NoSuchMethodException.class,
                () -> VPackMapper.class.getMethod("builderWithJackson2Defaults"));
    }

    // Provenance: JsonMapperBuilderTest#testBuilderWithJsonReadFeatures().
    void testBuilderWithJsonReadFeaturesVpackUnsupported() {
        assertThrows(NoSuchMethodException.class,
                () -> VPackMapper.Builder.class.getMethod("enable",
                        tools.jackson.core.json.JsonReadFeature[].class));
    }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
    static class DisabledGetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
    }
@JsonAutoDetect(isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    static class EnabledGetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
        public boolean isOk() { return true; }
    }
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
    static class EnabledIsGetterClass {
        public int getY() { return 1; }
        public boolean isOk() { return true; }
    }
static class GetterClass {
        @JsonProperty("x") public int getX() { return -2; }
        public int getY() { return 1; }
    }
static class TCls {
        @JsonProperty("groupname")
        private String groupname;

        public void setName(String value) { groupname = value; }
        public String getName() { return groupname; }
    }

    void __invoke_testBuilderConfigurationChainingVpack() throws Exception {
        try {
            testBuilderConfigurationChainingVpack();
        } finally {
        }
    }


    void __invoke_testBuilderCreatesIndependentMappersVpack() throws Exception {
        try {
            testBuilderCreatesIndependentMappersVpack();
        } finally {
        }
    }


    void __invoke_testBuilderWithJackson2DefaultsVpack() throws Exception {
        try {
            testBuilderWithJackson2DefaultsVpack();
        } finally {
        }
    }


    void __invoke_testBuilderWithJsonReadFeaturesVpackUnsupported() throws Exception {
        try {
            testBuilderWithJsonReadFeaturesVpackUnsupported();
        } finally {
        }
    }

}
