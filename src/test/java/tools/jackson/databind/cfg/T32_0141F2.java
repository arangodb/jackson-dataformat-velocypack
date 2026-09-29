package tools.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.ContextAttributes;
import tools.jackson.core.json.JsonWriteFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0141F2 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testFormatFeatures() {
        final JsonWriteFeature disabledByDefault = JsonWriteFeature.ESCAPE_NON_ASCII;
        final JsonWriteFeature enabledByDefault = JsonWriteFeature.QUOTE_PROPERTY_NAMES;

        SerializationConfig config = MAPPER.serializationConfig();
        SerializationConfig config2 = config.with(disabledByDefault);
        assertNotSame(config, config2);
        SerializationConfig config3 = config.withFeatures(disabledByDefault, enabledByDefault);
        assertNotSame(config, config3);

        assertNotSame(config3, config3.without(enabledByDefault));
        assertNotSame(config3, config3.withoutFeatures(disabledByDefault, enabledByDefault));
    }

    void testSerConfig() {
        SerializationConfig config = MAPPER.serializationConfig();
        assertFalse(config.hasSerializationFeatures(SerializationFeature.FAIL_ON_EMPTY_BEANS.getMask()));
        assertFalse(config.hasSerializationFeatures(SerializationFeature.CLOSE_CLOSEABLE.getMask()));
        assertEquals(defaultInclusion(), config.getDefaultPropertyInclusion());
        assertEquals(defaultInclusion(), config.getDefaultPropertyInclusion(String.class));
        assertFalse(config.useRootWrapping());

        assertNotSame(config, config.with(SerializationFeature.INDENT_OUTPUT,
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));

        assertSame(config, config.withRootName((PropertyName) null));

        SerializationConfig newConfig = config.withRootName(PropertyName.construct("foobar"));
        assertNotSame(config, newConfig);
        assertTrue(newConfig.useRootWrapping());

        assertSame(config, config.with(config.getAttributes()));
        ContextAttributes attrs = ContextAttributes.getEmpty()
                .withSharedAttribute("a", "b");
        assertNotSame(config, config.with(attrs));
    }

    void testStreamWriteFeatures() {
        SerializationConfig config = MAPPER.serializationConfig();
        assertFalse(config.hasFormatFeature(JsonWriteFeature.ESCAPE_NON_ASCII));
        assertNotSame(config, config.with(JsonWriteFeature.ESCAPE_NON_ASCII));
        SerializationConfig newConfig = config.withFeatures(StreamWriteFeature.IGNORE_UNKNOWN);
        assertNotSame(config, newConfig);
        assertTrue(newConfig.isEnabled(StreamWriteFeature.IGNORE_UNKNOWN));

        assertSame(config, config.without(JsonWriteFeature.ESCAPE_NON_ASCII));
        assertSame(config, config.withoutFeatures(StreamWriteFeature.IGNORE_UNKNOWN));
    }
private static JsonInclude.Value defaultInclusion() {
        return JsonInclude.Value.construct(JsonInclude.Include.USE_DEFAULTS,
                JsonInclude.Include.USE_DEFAULTS);
    }

    void __invoke_testFormatFeatures() throws Exception {
        try {
            testFormatFeatures();
        } finally {
        }
    }


    void __invoke_testSerConfig() throws Exception {
        try {
            testSerConfig();
        } finally {
        }
    }


    void __invoke_testStreamWriteFeatures() throws Exception {
        try {
            testStreamWriteFeatures();
        } finally {
        }
    }

}
