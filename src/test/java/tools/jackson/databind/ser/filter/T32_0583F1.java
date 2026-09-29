package tools.jackson.databind.ser.filter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ser.FilterProvider;
import tools.jackson.databind.ser.PropertyFilter;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.jdk.MapProperty;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0583F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testMapFilteringViaPropsVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("filterX",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 0b 07 01 41 62 35 03 03"),
                MAPPER.writer(provider).writeValueAsBytes(new MapBean()));
    }

    void testMapFilteringViaClassVpack() throws Exception {
        FilteredBean bean = new FilteredBean();
        bean.put("a", 4);
        bean.put("b", 3);
        FilterProvider provider = new SimpleFilterProvider().addFilter("filterForMaps",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 07 01 41 62 33 03"),
                MAPPER.writer(provider).writeValueAsBytes(bean));
    }

    void testMapFilteringWithAnnotationsVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("filterX",
                new TestMapFilter());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 0b 07 01 41 61 32 03 03"),
                MAPPER.writer(provider).writeValueAsBytes(new MapBean()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 01 46 76 61 6c 75 65 73 0b 07 01 41 61 31 03 03"),
                MAPPER.writer(provider).writeValueAsBytes(new MapBeanNoOffset()));
    }

    void testMapNonNullValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 02 41 61 43 66 6f 6f 41 63 43 62 61 72 03 09"),
                MAPPER.writeValueAsBytes(new NoNullsStringMap()
                        .add("a", "foo").add("b", null).add("c", "bar")));
    }

    void testMapNonEmptyValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 02 41 61 43 66 6f 6f 41 62 43 62 61 72 03 09"),
                MAPPER.writeValueAsBytes(new NoEmptyStringsMap()
                        .add("a", "foo").add("b", "bar").add("c", "")));
    }

    void testMapAbsentValueVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0a 01 41 61 43 66 6f 6f 03"),
                MAPPER.writeValueAsBytes(new NoAbsentStringMap()
                        .add("a", "foo").add("b", null)));
    }

    void testMapViaGlobalNonEmptyVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .changeDefaultPropertyInclusion(incl -> incl
                        .withContentInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 08 01 41 61 41 62 03"),
                mapper.writeValueAsBytes(new StringMap497()
                        .add("x", "").add("a", "b")));
    }

    void testMapViaTypeOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .withConfigOverride(Map.class,
                        o -> o.setInclude(JsonInclude.Value.empty()
                                .withContentInclusion(JsonInclude.Include.NON_EMPTY)))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 08 01 41 61 41 62 03"),
                mapper.writeValueAsBytes(new StringMap497()
                        .add("foo", "").add("a", "b")));
    }
@JsonFilter("anyFilter")
    static class AnyBean {
        private final Map<String, String> properties = new HashMap<>();

        AnyBean() {
            properties.put("a", "1");
            properties.put("b", "2");
        }

        @JsonAnyGetter
        public Map<String, String> anyProperties() {
            return properties;
        }
    }
static class AnyBeanWithIgnores {
        private final Map<String, String> properties = new LinkedHashMap<>();

        AnyBeanWithIgnores() {
            properties.put("a", "1");
            properties.put("bogus", "2");
            properties.put("b", "3");
        }

        @JsonAnyGetter
        @JsonIgnoreProperties({ "bogus" })
        public Map<String, String> anyProperties() {
            return properties;
        }
    }
static class AnyBeanWithMultipleIgnores {
        public String name = "bob";
        private final Map<String, String> properties = new LinkedHashMap<>();

        AnyBeanWithMultipleIgnores() {
            properties.put("a", "1");
            properties.put("secret", "s");
            properties.put("b", "2");
            properties.put("internal", "i");
        }

        @JsonAnyGetter
        @JsonIgnoreProperties({ "secret", "internal" })
        public Map<String, String> anyProperties() {
            return properties;
        }
    }
@JsonFilter("CustomFilter")
    static class OuterObject {
        public int getExplicitProperty() {
            return 42;
        }

        @JsonAnyGetter
        public Map<String, Object> getAny() {
            Map<String, Object> extra = new LinkedHashMap<>();
            extra.put("dynamicProperty", "I will not serialize");
            return extra;
        }
    }
static class CustomFilter extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsProperty(Object pojo, JsonGenerator generator,
                SerializationContext provider, PropertyWriter writer) throws Exception {
            if (pojo instanceof OuterObject) {
                writer.serializeAsProperty(pojo, generator, provider);
            }
        }
    }
@JsonFilter("filterForMaps")
    static class FilteredBean extends LinkedHashMap<String, Integer> { }
static class MapBean {
        @JsonFilter("filterX")
        @CustomOffset(1)
        public Map<String, Integer> values = new LinkedHashMap<>();

        MapBean() {
            values.put("a", 1);
            values.put("b", 5);
            values.put("c", 9);
        }
    }
static class MapBeanNoOffset {
        @JsonFilter("filterX")
        public Map<String, Integer> values = new LinkedHashMap<>();

        MapBeanNoOffset() {
            values.put("a", 1);
            values.put("b", 2);
            values.put("c", 3);
        }
    }
@java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface CustomOffset {
        int value();
    }
static class TestMapFilter implements PropertyFilter {
        @Override
        public PropertyFilter snapshot() {
            return this;
        }

        @Override
        public void serializeAsProperty(Object bean, JsonGenerator generator,
                SerializationContext provider, PropertyWriter writer) throws Exception {
            String name = writer.getName();
            if (!"a".equals(name)) {
                return;
            }
            CustomOffset annotation = writer.findAnnotation(CustomOffset.class);
            int offset = (annotation == null) ? 0 : annotation.value();
            MapProperty property = (MapProperty) writer;
            Integer old = (Integer) property.getValue();
            property.setValue(Integer.valueOf(offset + old.intValue()));
            writer.serializeAsProperty(bean, generator, provider);
        }

        @Override
        public void serializeAsElement(Object elementValue, JsonGenerator generator,
                SerializationContext provider, PropertyWriter writer) throws Exception {
        }

        @Override
        public void depositSchemaProperty(PropertyWriter writer,
                JsonObjectFormatVisitor objectVisitor, SerializationContext provider) {
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_NULL)
    static class NoNullsStringMap extends LinkedHashMap<String, String> {
        NoNullsStringMap add(String key, String value) {
            put(key, value);
            return this;
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
    static class NoEmptyStringsMap extends LinkedHashMap<String, String> {
        NoEmptyStringsMap add(String key, String value) {
            put(key, value);
            return this;
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_ABSENT)
    static class NoAbsentStringMap extends LinkedHashMap<String, AtomicReference<?>> {
        NoAbsentStringMap add(String key, Object value) {
            put(key, new AtomicReference<>(value));
            return this;
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_EMPTY)
    static class StringMap497 extends LinkedHashMap<String, String> {
        StringMap497 add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    void __invoke_testMapFilteringViaPropsVpack() throws Exception {
        try {
            testMapFilteringViaPropsVpack();
        } finally {
        }
    }


    void __invoke_testMapFilteringViaClassVpack() throws Exception {
        try {
            testMapFilteringViaClassVpack();
        } finally {
        }
    }


    void __invoke_testMapFilteringWithAnnotationsVpack() throws Exception {
        try {
            testMapFilteringWithAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testMapNonNullValueVpack() throws Exception {
        try {
            testMapNonNullValueVpack();
        } finally {
        }
    }


    void __invoke_testMapNonEmptyValueVpack() throws Exception {
        try {
            testMapNonEmptyValueVpack();
        } finally {
        }
    }


    void __invoke_testMapAbsentValueVpack() throws Exception {
        try {
            testMapAbsentValueVpack();
        } finally {
        }
    }


    void __invoke_testMapViaGlobalNonEmptyVpack() throws Exception {
        try {
            testMapViaGlobalNonEmptyVpack();
        } finally {
        }
    }


    void __invoke_testMapViaTypeOverrideVpack() throws Exception {
        try {
            testMapViaTypeOverrideVpack();
        } finally {
        }
    }

}
