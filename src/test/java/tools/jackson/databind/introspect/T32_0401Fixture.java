package tools.jackson.databind.introspect;

import java.beans.ConstructorProperties;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AccessorNamingStrategy;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.BasicBeanDescription;
import tools.jackson.databind.introspect.BeanPropertyDefinition;
import tools.jackson.databind.introspect.POJOPropertiesCollector;
import tools.jackson.databind.introspect.POJOPropertyBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0401Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: POJOPropertiesCollectorTest#testSimple().
    void testSimpleVpack() {
        BeanPropertyDefinition prop = property(deserializationDescription(Simple.class), "value");
        assertNotNull(prop);
        assertTrue(prop.hasSetter());
        assertTrue(prop.hasGetter());
        assertTrue(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testSimpleFieldVisibility().
    void testSimpleFieldVisibilityVpack() {
        BeanPropertyDefinition prop = property(
                deserializationDescription(SimpleFieldDeser.class), "values");
        assertNotNull(prop);
        assertFalse(prop.hasSetter());
        assertFalse(prop.hasGetter());
        assertTrue(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testEmpty().
    void testEmptyVpack() {
        assertEquals(0, serializationDescription(Empty.class).findProperties().size());
    }

    // Provenance: POJOPropertiesCollectorTest#testPartialIgnore().
    void testPartialIgnoreVpack() {
        BeanPropertyDefinition prop = property(
                serializationDescription(IgnoredSetter.class), "value");
        assertNotNull(prop);
        assertFalse(prop.hasSetter());
        assertTrue(prop.hasGetter());
        assertTrue(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testMergeWithRename().
    void testMergeWithRenameVpack() throws Exception {
        POJOPropertyBuilder prop = (POJOPropertyBuilder) exactCollector(
                MAPPER, MergedProperties.class, true).properties().get("x");
        assertNotNull(prop);
        assertFalse(prop.hasGetter());
        // The VPack mapper keeps an explicitly annotated mutator in the
        // introspection model; the portable result is the same logical
        // serialization property, with no getter and the public field retained.
        assertTrue(prop.hasSetter());
        assertTrue(prop.hasField());
    }

    // Provenance: POJOPropertiesCollectorTest#testGlobalVisibilityForGetters().
    void testGlobalVisibilityForGettersVpack() {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.withVisibility(
                        PropertyAccessor.GETTER,
                        JsonAutoDetect.Visibility.NONE))
                .build();
        assertEquals(0, serializationDescription(mapper,
                SimpleGetterVisibility.class).findProperties().size());
    }

    // Provenance: POJOPropertiesCollectorTest#testDuplicateGettersCreator().
    void testDuplicateGettersCreatorVpack() throws Exception {
        List<BeanPropertyDefinition> props = serializationDescription(
                DuplicateGetterCreatorBean.class).findProperties();
        assertEquals(1, props.size());
        BeanPropertyDefinition prop = props.get(0);
        assertEquals("bloop", prop.getName());

        // Calling getGetter() resolves the duplicate. Inspect the collector's
        // retained linked getter nodes, matching the upstream test's intent.
        Field getters = prop.getClass().getDeclaredField("_getters");
        getters.setAccessible(true);
        Object first = getters.get(prop);
        assertNotNull(first);
        Field value = first.getClass().getDeclaredField("value");
        Field next = first.getClass().getDeclaredField("next");
        value.setAccessible(true);
        next.setAccessible(true);
        assertTrue(((tools.jackson.databind.introspect.AnnotatedMember) value.get(first))
                .hasAnnotation(A.class));
        Object second = next.get(first);
        assertNotNull(second);
        assertTrue(((tools.jackson.databind.introspect.AnnotatedMember) value.get(second))
                .hasAnnotation(A.class));
    }

    // Provenance: POJOPropertiesCollectorTest#testIgnoredPropertyNamesIncludesClassLevel().
    void testIgnoredPropertyNamesIncludesClassLevelVpack() {
        assertIgnoredNames(deserializationDescription(IgnoredMixed.class), "first", "second");
        assertIgnoredNames(serializationDescription(IgnoredMixed.class), "first", "second");
    }

    // Provenance: POJOPropertiesCollectorTest#testMultiRescueSnapshotCapturesAllNames5952().
    void testMultiRescueSnapshotCapturesAllNames5952Vpack() {
        BeanDescription desc = deserializationDescription(MultiRescueCreator5952.class);
        Set<String> rescued = desc.getIgnoredPropertyNames();
        assertFalse(rescued.contains("alpha"));
        assertFalse(rescued.contains("beta"));
        Set<String> unrescued = desc.getNonRescuedIgnoredPropertyNames();
        assertTrue(unrescued.contains("alpha"));
        assertTrue(unrescued.contains("beta"));
    }

    // Provenance: POJOPropertiesCollectorTest#testNonRescuedAliasIgnoredPropertyNames6031().
    void testNonRescuedAliasIgnoredPropertyNames6031Vpack() {
        BeanDescription desc = deserializationDescription(AliasIgnoredCreator6031.class);
        assertFalse(desc.getIgnoredPropertyNames().contains("oldName"));
        assertTrue(desc.getNonRescuedIgnoredPropertyNames().contains("oldName"));
    }

    // Provenance: POJOPropertiesCollectorTest#testNonRescuedIgnoredPropertyNames5952().
    void testNonRescuedIgnoredPropertyNames5952Vpack() {
        BeanDescription desc = deserializationDescription(PerPropertyIgnoredCreator5952.class);
        assertFalse(desc.getIgnoredPropertyNames().contains("query"));
        assertTrue(desc.getNonRescuedIgnoredPropertyNames().contains("query"),
                "unrescued=" + desc.getNonRescuedIgnoredPropertyNames()
                        + ", rescued=" + desc.getIgnoredPropertyNames());
    }

    
    // Provenance: POJOPropertiesCollectorTest#testFormatOverridesDeprecated().
    void testFormatOverridesDeprecatedVpack() throws Exception {
        BeanDescription desc = deserializationDescription(Simple.class);
        POJOPropertiesCollector collector = collectorFor(desc);
        JsonFormat.Value format = collector.getFormatOverrides();
        assertNotNull(format);
        assertEquals(JsonFormat.Shape.ANY, format.getShape());
    }
private static BeanPropertyDefinition property(BeanDescription desc, String name) {
        for (BeanPropertyDefinition prop : desc.findProperties()) {
            if (name.equals(prop.getName())) {
                return prop;
            }
        }
        return null;
    }
private static void assertIgnoredNames(BeanDescription desc, String... names) {
        Set<String> ignored = desc.getIgnoredPropertyNames();
        assertEquals(names.length, ignored.size());
        for (String name : names) {
            assertTrue(ignored.contains(name));
        }
    }
private static POJOPropertiesCollector collectorFor(BeanDescription desc) throws Exception {
        Field field = BasicBeanDescription.class.getDeclaredField("_propCollector");
        field.setAccessible(true);
        return (POJOPropertiesCollector) field.get(desc);
    }
private static ExactCollector exactCollector(ObjectMapper mapper, Class<?> type,
            boolean forSerialization) {
        MapperConfig<?> config = forSerialization
                ? mapper.serializationConfig() : mapper.deserializationConfig();
        JavaType javaType = mapper.constructType(type);
        AnnotatedClass classDef = AnnotatedClassResolver.resolve(config, javaType, config);
        return new ExactCollector(config, forSerialization, javaType, classDef,
                config.getAccessorNaming());
    }
private static BeanDescription serializationDescription(Class<?> type) {
        return serializationDescription(MAPPER, type);
    }
private static BeanDescription serializationDescription(ObjectMapper mapper, Class<?> type) {
        return mapper._serializationContext().introspectBeanDescription(mapper.constructType(type));
    }
private static BeanDescription deserializationDescription(Class<?> type) {
        return MAPPER._deserializationContext().introspectBeanDescription(MAPPER.constructType(type));
    }
static class Simple {
        public int value;

        @JsonProperty("value")
        public void valueSetter(int v) { value = v; }

        @JsonProperty("value")
        public int getFoobar() { return value; }
    }
static class SimpleFieldDeser {
        @JsonDeserialize String[] values;
    }
static class SimpleGetterVisibility {
        public int getA() { return 0; }
        protected int getB() { return 1; }
        @SuppressWarnings("unused")
        private int getC() { return 2; }
    }
static class Empty {
        public int value;
        public void setValue(int v) { value = v; }

        @JsonIgnore
        public int getValue() { return value; }
    }
static class IgnoredSetter {
        @JsonProperty
        public int value;

        @JsonIgnore
        public void setValue(int v) { value = v; }

        public int getValue() { return value; }
    }
static class MergedProperties {
        public int x;

        @JsonProperty("x")
        public void setFoobar(int v) { x = v; }
    }
@JsonIgnoreProperties("second")
    static class IgnoredMixed {
        @JsonIgnore
        public String first;
        public String second;
        public String third;
    }
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD,
            ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    @interface A { }
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD,
            ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotation
    @interface B { }
static class DuplicateGetterCreatorBean {
        public DuplicateGetterCreatorBean(@JsonProperty("bloop") @A boolean bloop) { }

        public boolean isBloop() { return true; }
        public boolean getBloop() { return true; }
    }
@JsonIgnoreProperties("name")
    static class ClassIgnoredCreator5952 {
        final int id;
        final String name;

        @JsonCreator
        public ClassIgnoredCreator5952(@JsonProperty("id") int id,
                @JsonProperty("name") String name) {
            this.id = id;
            this.name = name;
        }
    }
static class PerPropertyIgnoredCreator5952 {
        @JsonIgnore
        public String query;

        @JsonCreator
        @ConstructorProperties({"rawQuery"})
        public PerPropertyIgnoredCreator5952(@JsonProperty("query") String rawQuery) {
            this.query = rawQuery;
        }
    }
static class MultiRescueCreator5952 {
        @JsonIgnore
        public String alpha;

        @JsonIgnore
        public String beta;

        @JsonCreator
        @ConstructorProperties({"rawAlpha", "rawBeta"})
        public MultiRescueCreator5952(@JsonProperty("alpha") String alpha,
                @JsonProperty("beta") String beta) {
            this.alpha = alpha;
            this.beta = beta;
        }
    }
static class AliasIgnoredCreator6031 {
        @JsonProperty("newName")
        private final String value;

        @JsonCreator
        public AliasIgnoredCreator6031(
                @JsonProperty("newName") @JsonAlias("oldName") String value) {
            this.value = value;
        }

        public String getValue() { return value; }

        @JsonIgnore
        public String getOldName() { return value; }
    }
static class ExactCollector extends POJOPropertiesCollector {
        ExactCollector(MapperConfig<?> config, boolean forSerialization, JavaType type,
                AnnotatedClass classDef, AccessorNamingStrategy.Provider accessorNaming) {
            super(config, forSerialization, type, classDef,
                    accessorNaming.forPOJO(config, classDef));
        }

        Map<String, POJOPropertyBuilder> properties() {
            return getPropertyMap();
        }
    }

    void __invoke_testSimpleVpack() throws Exception {
        try {
            testSimpleVpack();
        } finally {
        }
    }


    void __invoke_testSimpleFieldVisibilityVpack() throws Exception {
        try {
            testSimpleFieldVisibilityVpack();
        } finally {
        }
    }


    void __invoke_testEmptyVpack() throws Exception {
        try {
            testEmptyVpack();
        } finally {
        }
    }


    void __invoke_testPartialIgnoreVpack() throws Exception {
        try {
            testPartialIgnoreVpack();
        } finally {
        }
    }


    void __invoke_testMergeWithRenameVpack() throws Exception {
        try {
            testMergeWithRenameVpack();
        } finally {
        }
    }


    void __invoke_testGlobalVisibilityForGettersVpack() throws Exception {
        try {
            testGlobalVisibilityForGettersVpack();
        } finally {
        }
    }


    void __invoke_testDuplicateGettersCreatorVpack() throws Exception {
        try {
            testDuplicateGettersCreatorVpack();
        } finally {
        }
    }


    void __invoke_testIgnoredPropertyNamesIncludesClassLevelVpack() throws Exception {
        try {
            testIgnoredPropertyNamesIncludesClassLevelVpack();
        } finally {
        }
    }


    void __invoke_testMultiRescueSnapshotCapturesAllNames5952Vpack() throws Exception {
        try {
            testMultiRescueSnapshotCapturesAllNames5952Vpack();
        } finally {
        }
    }


    void __invoke_testNonRescuedAliasIgnoredPropertyNames6031Vpack() throws Exception {
        try {
            testNonRescuedAliasIgnoredPropertyNames6031Vpack();
        } finally {
        }
    }


    void __invoke_testNonRescuedIgnoredPropertyNames5952Vpack() throws Exception {
        try {
            testNonRescuedIgnoredPropertyNames5952Vpack();
        } finally {
        }
    }


    void __invoke_testFormatOverridesDeprecatedVpack() throws Exception {
        try {
            testFormatOverridesDeprecatedVpack();
        } finally {
        }
    }

}
