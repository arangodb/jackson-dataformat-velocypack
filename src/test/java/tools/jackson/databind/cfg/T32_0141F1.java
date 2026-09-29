package tools.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.core.json.JsonReadFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0141F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testBasicFeatures() {
        DeserializationConfig config = MAPPER.deserializationConfig();
        assertTrue(config.hasDeserializationFeatures(
                DeserializationFeature.EAGER_DESERIALIZER_FETCH.getMask()));
        assertFalse(config.hasDeserializationFeatures(
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY.getMask()));
        assertTrue(config.hasSomeOfFeatures(
                DeserializationFeature.EAGER_DESERIALIZER_FETCH.getMask()
                        + DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY.getMask()));
        assertFalse(config.hasSomeOfFeatures(
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY.getMask()));

        assertNotSame(config, config.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES));
    }

    void testEnumIndexes() {
        int max = 0;
        for (DeserializationFeature feature : DeserializationFeature.values()) {
            max = Math.max(max, feature.ordinal());
        }
        if (max >= 31) {
            fail("Max number of DeserializationFeature enums reached: " + max);
        }
    }

    void testFeatureDefaults() {
        DeserializationConfig config = new VPackMapper().deserializationConfig();
        assertTrue(config.isEnabled(MapperFeature.USE_ANNOTATIONS));
        assertFalse(config.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS));
        assertTrue(config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS));

        assertFalse(config.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS));
        assertFalse(config.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS));
        assertFalse(config.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    void testJsonReadFeatures() {
        final JsonReadFeature disabledByDefault = JsonReadFeature.ALLOW_JAVA_COMMENTS;
        final JsonReadFeature disabledByDefault2 = JsonReadFeature.ALLOW_MISSING_VALUES;
        DeserializationConfig config = MAPPER.deserializationConfig();
        DeserializationConfig config2 = config.with(disabledByDefault);
        assertNotSame(config, config2);
        DeserializationConfig config3 = config.withFeatures(disabledByDefault2,
                disabledByDefault);
        assertNotSame(config, config3);

        assertNotSame(config3, config3.without(disabledByDefault));
        assertNotSame(config3, config3.withoutFeatures(disabledByDefault2,
                disabledByDefault));
    }

    void testMisc() {
        DeserializationConfig config = MAPPER.deserializationConfig();
        assertEquals(defaultInclusion(), config.getDefaultPropertyInclusion());
        assertEquals(defaultInclusion(), config.getDefaultPropertyInclusion(String.class));

        assertSame(config, config.withRootName((PropertyName) null));

        DeserializationConfig newConfig = config.withRootName(PropertyName.construct("foobar"));
        assertNotSame(config, newConfig);
        config = newConfig;
        assertSame(config, config.withRootName(PropertyName.construct("foobar")));

        assertSame(config, config.with(config.getAttributes()));
        ContextAttributes attrs = ContextAttributes.getEmpty()
                .withSharedAttribute("a", "b");
        assertNotSame(config, config.with(attrs));
    }

    void testStreamReadFeatures() {
        DeserializationConfig config = MAPPER.deserializationConfig();
        assertNotSame(config, config.with(StreamReadFeature.IGNORE_UNDEFINED));
        assertNotSame(config, config.withFeatures(StreamReadFeature.IGNORE_UNDEFINED,
                StreamReadFeature.STRICT_DUPLICATE_DETECTION));

        assertSame(config, config.without(StreamReadFeature.IGNORE_UNDEFINED));
        assertSame(config, config.withoutFeatures(StreamReadFeature.IGNORE_UNDEFINED,
                StreamReadFeature.STRICT_DUPLICATE_DETECTION));
    }
private static JsonInclude.Value defaultInclusion() {
        return JsonInclude.Value.construct(JsonInclude.Include.USE_DEFAULTS,
                JsonInclude.Include.USE_DEFAULTS);
    }

    void __invoke_testBasicFeatures() throws Exception {
        try {
            testBasicFeatures();
        } finally {
        }
    }


    void __invoke_testEnumIndexes() throws Exception {
        try {
            testEnumIndexes();
        } finally {
        }
    }


    void __invoke_testFeatureDefaults() throws Exception {
        try {
            testFeatureDefaults();
        } finally {
        }
    }


    void __invoke_testJsonReadFeatures() throws Exception {
        try {
            testJsonReadFeatures();
        } finally {
        }
    }


    void __invoke_testMisc() throws Exception {
        try {
            testMisc();
        } finally {
        }
    }


    void __invoke_testStreamReadFeatures() throws Exception {
        try {
            testStreamReadFeatures();
        } finally {
        }
    }

}
