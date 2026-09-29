package tools.jackson.databind.util;

import java.util.LinkedHashMap;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.EnumFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0625F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: CompactStringObjectMapTest#testBig().
    void compactStringObjectMapBigVpack() throws Exception {
        Map<String, String> expected = new LinkedHashMap<>();
        for (int i = 0; i < 1000; ++i) {
            String key = "key" + i;
            expected.put(key, key);
        }
        byte[] encoded = MAPPER.writeValueAsBytes(expected);
        Map<?, ?> decoded = MAPPER.readValue(encoded, Map.class);
        assertEquals(1000, decoded.size());
        for (String key : expected.keySet()) {
            assertEquals(key, decoded.get(key));
        }
        assertNull(decoded.get("key1000"));
        assertNull(decoded.get("keyXXX"));
        assertNull(decoded.get(""));
    }
enum ABC {
        A("A"), B("b"), C("C");

        private final String desc;

        ABC(String desc) { this.desc = desc; }

        @Override
        public String toString() { return desc; }
    }
enum LocaleSensitiveABC { IS_ADMIN }

    void __invoke_compactStringObjectMapBigVpack() throws Exception {
        try {
            compactStringObjectMapBigVpack();
        } finally {
        }
    }

}
