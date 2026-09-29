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

class T32_0583F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testAnyGetterFilteringVpack() throws Exception {
        FilterProvider provider = new SimpleFilterProvider().addFilter("anyFilter",
                SimpleBeanPropertyFilter.filterOutAllExcept("b"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 08 01 41 62 41 32 03"),
                MAPPER.writer(provider).writeValueAsBytes(new AnyBean()));
    }

    void testAnyGetterIgnoreVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 02 41 61 41 31 41 62 41 33 03 07"),
                MAPPER.writeValueAsBytes(new AnyBeanWithIgnores()));
    }

    void testAnyGetterIgnoreProperties1281Vpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 17 03 44 6e 61 6d 65 43 62 6f 62 41 61 41 31 41 62 41 32 0c 10 03"),
                MAPPER.writeValueAsBytes(new AnyBeanWithMultipleIgnores()));
    }

    void testAnyGetterPojo1655Vpack() throws Exception {
        FilterProvider filters = new SimpleFilterProvider().addFilter("CustomFilter",
                new CustomFilter());
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 3d 02 50 65 78 70 6c 69 63 69 74 50 72 6f 70 65 72 74 79 28 2a "
              + "4f 64 79 6e 61 6d 69 63 50 72 6f 70 65 72 74 79 54 49 20 77 69 6c 6c 20 "
              + "6e 6f 74 20 73 65 72 69 61 6c 69 7a 65 16 03"),
                MAPPER.writer(filters).writeValueAsBytes(new OuterObject()));
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

    void __invoke_testAnyGetterFilteringVpack() throws Exception {
        try {
            testAnyGetterFilteringVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterIgnoreVpack() throws Exception {
        try {
            testAnyGetterIgnoreVpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterIgnoreProperties1281Vpack() throws Exception {
        try {
            testAnyGetterIgnoreProperties1281Vpack();
        } finally {
        }
    }


    void __invoke_testAnyGetterPojo1655Vpack() throws Exception {
        try {
            testAnyGetterPojo1655Vpack();
        } finally {
        }
    }

}
