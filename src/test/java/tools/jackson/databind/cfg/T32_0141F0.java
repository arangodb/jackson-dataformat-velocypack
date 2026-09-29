package tools.jackson.databind.cfg;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ConfigOverrides;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0141F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testSnapshot() {
        ConfigOverrides overrides = new ConfigOverrides();
        overrides.findOrCreateOverride(String.class)
                .setVisibility(JsonAutoDetect.Value.construct(PropertyAccessor.SETTER,
                        Visibility.NONE));
        assertEquals(overrides.toString(), overrides.snapshot().toString());
    }
private static JsonInclude.Value defaultInclusion() {
        return JsonInclude.Value.construct(JsonInclude.Include.USE_DEFAULTS,
                JsonInclude.Include.USE_DEFAULTS);
    }

    void __invoke_testSnapshot() throws Exception {
        try {
            testSnapshot();
        } finally {
        }
    }

}
