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

class T32_0579F2 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void testOverrideForIncludeAsPropertyAlwaysVpack() throws Exception {
        MixedTypeNonNullBean nullValues = new MixedTypeNonNullBean();
        ObjectMapper mapper = VPackMapper.builder().build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 01 49 61 6e 6e 6f 74 61 74 65 64 18 03"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 02 49 61 6e 6e 6f 74 61 74 65 64 18 45 70 6c 61 69 6e 18 03 0e"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 02 43 6e 75 6d 18 49 61 6e 6e 6f 74 61 74 65 64 18 08 03"),
                mapper.writeValueAsBytes(nullValues));
    }

    void testOverrideForIncludeAsPropertyNonNullVpack() throws Exception {
        MixedTypeAlwaysBean nullValues = new MixedTypeAlwaysBean();
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6e 75 6d 18 03"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 70 6c 61 69 6e 18 03"),
                mapper.writeValueAsBytes(nullValues));
    }

    void testOverridesForIncludeAndIncludeAsPropertyAlwaysVpack() throws Exception {
        MixedTypeAlwaysBean nullValues = new MixedTypeAlwaysBean();
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 70 6c 61 69 6e 18 03"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeAlwaysBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6e 75 6d 18 03"),
                mapper.writeValueAsBytes(nullValues));
    }

    void testOverridesForIncludeAndIncludeAsPropertyNonNullVpack() throws Exception {
        MixedTypeNonNullBean nullValues = new MixedTypeNonNullBean();
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1d 03 43 6e 75 6d 18 49 61 6e 6e 6f 74 61 74 65 64 18 "
              + "45 70 6c 61 69 6e 18 08 03 13"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .withConfigOverride(String.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 02 43 6e 75 6d 18 49 61 6e 6e 6f 74 61 74 65 64 18 08 03"),
                mapper.writeValueAsBytes(nullValues));

        mapper = VPackMapper.builder()
                .withConfigOverride(MixedTypeNonNullBean.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.ALWAYS, null)))
                .withConfigOverride(Integer.class,
                        o -> o.setIncludeAsProperty(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_NULL, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 02 49 61 6e 6e 6f 74 61 74 65 64 18 45 70 6c 61 69 6e 18 "
              + "03 0e"),
                mapper.writeValueAsBytes(nullValues));
    }

    void testPropConfigOverridesForIncludeVpack() throws Exception {
        EmptyListMapBean empty = new EmptyListMapBean();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 02 44 6c 69 73 74 01 43 6d 61 70 0a 03 09"),
                MAPPER.writeValueAsBytes(empty));

        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Map.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_EMPTY, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 44 6c 69 73 74 01 03"),
                mapper.writeValueAsBytes(empty));

        mapper = VPackMapper.builder()
                .withConfigOverride(List.class,
                        o -> o.setInclude(JsonInclude.Value.construct(
                                JsonInclude.Include.NON_EMPTY, null)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6d 61 70 0a 03"),
                mapper.writeValueAsBytes(empty));
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

    void __invoke_testOverrideForIncludeAsPropertyAlwaysVpack() throws Exception {
        try {
            testOverrideForIncludeAsPropertyAlwaysVpack();
        } finally {
        }
    }


    void __invoke_testOverrideForIncludeAsPropertyNonNullVpack() throws Exception {
        try {
            testOverrideForIncludeAsPropertyNonNullVpack();
        } finally {
        }
    }


    void __invoke_testOverridesForIncludeAndIncludeAsPropertyAlwaysVpack() throws Exception {
        try {
            testOverridesForIncludeAndIncludeAsPropertyAlwaysVpack();
        } finally {
        }
    }


    void __invoke_testOverridesForIncludeAndIncludeAsPropertyNonNullVpack() throws Exception {
        try {
            testOverridesForIncludeAndIncludeAsPropertyNonNullVpack();
        } finally {
        }
    }


    void __invoke_testPropConfigOverridesForIncludeVpack() throws Exception {
        try {
            testPropConfigOverridesForIncludeVpack();
        } finally {
        }
    }

}
