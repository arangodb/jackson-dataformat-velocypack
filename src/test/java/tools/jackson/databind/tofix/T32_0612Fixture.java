package tools.jackson.databind.tofix;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0612Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FILTERED_JSON_VALUE = VPackWireFixtureTest.hex(
            "0b 0e 01 47 70 72 65 73 65 6e 74 41 78 03");

    
    // Provenance: JsonValueIgnoresJsonInclude4762Test#jsonValueShouldHonorJsonIncludeOnField().
    void jsonValueShouldHonorJsonIncludeOnFieldVpack() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("present", "x");
        map.put("missing", null);
        assertArrayEquals(FILTERED_JSON_VALUE,
                MAPPER.writeValueAsBytes(new JsonValueWithInclude(map)));
    }
static class JsonValueWithInclude {
        @JsonValue
        @JsonInclude(value = JsonInclude.Include.NON_NULL,
                content = JsonInclude.Include.NON_NULL)
        public final Map<String, Object> value;

        JsonValueWithInclude(Map<String, Object> value) { this.value = value; }
    }
@SuppressWarnings("unused")
    static class FieldBean {
        public static boolean DUMMY;
        private long bar;
        @JsonProperty private String props;
    }
static class Bean1005 {
        Bean1005(int ignored) { }
    }
static abstract class LongList implements List<Long> { }
static abstract class StringLongMap implements Map<String, Long> { }

    void __invoke_jsonValueShouldHonorJsonIncludeOnFieldVpack() throws Exception {
        try {
            jsonValueShouldHonorJsonIncludeOnFieldVpack();
        } finally {
        }
    }

}
