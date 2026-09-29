package tools.jackson.databind.ser.filter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0579F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testWithMapperConfigurationVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(inclusion -> inclusion
                        .withContentInclusion(JsonInclude.Include.NON_EMPTY)
                        .withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();

        JacksonAsEmptyModel model = new JacksonAsEmptyModel();
        model.setName("");
        model.setDescription("");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(model));
    }
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.CUSTOM,
            contentFilter = FooFilter.class)
    static class Bean {
        public Map<String, String> stuff = new LinkedHashMap<>();
        Bean add(String key, String value) { stuff.put(key, value); return this; }
    }
static class DynBean {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.CUSTOM,
                contentFilter = FooFilter.class)
        public Map<String, Object> stuff = new LinkedHashMap<>();
        DynBean add(String key, Object value) { stuff.put(key, value); return this; }
    }
static class FooFilter {
        @Override
        public boolean equals(Object other) { return "foo".equals(other); }
        @Override
        public int hashCode() { return 0; }
    }
static class JacksonAsEmptyModel {
        String name;
        String description;
        String familyName;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Optional<String> getDescription() { return Optional.ofNullable(description); }
        public void setDescription(String description) { this.description = description; }
        public Optional<String> getFamilyName() { return Optional.ofNullable(familyName); }
        public void setFamilyName(String familyName) { this.familyName = familyName; }
    }
@JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeAlwaysBean {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String annotated;
        public String plain;
    }
@JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeNonNullBean {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num;
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public String annotated;
        public String plain;
    }
@JsonPropertyOrder({"list", "map"})
    static class EmptyListMapBean {
        public List<String> list = Collections.emptyList();
        public Map<String, String> map = Collections.emptyMap();
    }

    void __invoke_testWithMapperConfigurationVpack() throws Exception {
        try {
            testWithMapperConfigurationVpack();
        } finally {
        }
    }

}
