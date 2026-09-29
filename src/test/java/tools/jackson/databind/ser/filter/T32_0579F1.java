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

class T32_0579F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    void mapEmptyAfterContentFilterVpack() throws Exception {
        Bean bean = new Bean().add("a", "foo").add("b", "foo");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(bean));
    }

    void mapKeepsSurvivingEntriesVpack() throws Exception {
        Bean bean = new Bean().add("a", "foo").add("b", "keep");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 45 73 74 75 66 66 0b 0b 01 41 62 44 6b 65 65 70 03 03"),
                MAPPER.writeValueAsBytes(bean));
    }

    void genuinelyEmptyMapOmittedVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new Bean()));
    }

    void dynamicValueMapEmptyAfterContentFilterVpack() throws Exception {
        DynBean bean = new DynBean().add("a", "foo").add("b", "foo");
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(bean));
    }

    void dynamicValueMapKeepsSurvivingEntriesVpack() throws Exception {
        DynBean bean = new DynBean().add("a", "foo").add("b", "keep");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 45 73 74 75 66 66 0b 0b 01 41 62 44 6b 65 65 70 03 03"),
                MAPPER.writeValueAsBytes(bean));
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

    void __invoke_mapEmptyAfterContentFilterVpack() throws Exception {
        try {
            mapEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_mapKeepsSurvivingEntriesVpack() throws Exception {
        try {
            mapKeepsSurvivingEntriesVpack();
        } finally {
        }
    }


    void __invoke_genuinelyEmptyMapOmittedVpack() throws Exception {
        try {
            genuinelyEmptyMapOmittedVpack();
        } finally {
        }
    }


    void __invoke_dynamicValueMapEmptyAfterContentFilterVpack() throws Exception {
        try {
            dynamicValueMapEmptyAfterContentFilterVpack();
        } finally {
        }
    }


    void __invoke_dynamicValueMapKeepsSurvivingEntriesVpack() throws Exception {
        try {
            dynamicValueMapKeepsSurvivingEntriesVpack();
        } finally {
        }
    }

}
