package tools.jackson.databind.introspect;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.core.Version;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotationIntrospectorPair;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0397Fixture {
private static final AnnotationIntrospector NO_ANNOTATIONS =
            AnnotationIntrospector.nopInstance();

    // Provenance: IntrospectorPairTest#testVersion().
    void testVersionVpack() {
        Version version = new Version(1, 2, 3, null,
                "tools.jackson", "IntrospectorPairTest");
        IntrospectorWithMap withVersion = new IntrospectorWithMap().version(version);
        assertEquals(version,
                new AnnotationIntrospectorPair(withVersion, NO_ANNOTATIONS).version());
        IntrospectorWithMap noVersion = new IntrospectorWithMap();
        assertEquals(Version.unknownVersion(),
                new AnnotationIntrospectorPair(noVersion, withVersion).version());
    }

    // Provenance: IntrospectorPairTest#testPropertyIgnorals().
    void testPropertyIgnoralsVpack() {
        JsonIgnoreProperties.Value included =
                JsonIgnoreProperties.Value.forIgnoredProperties("foo");
        IntrospectorWithMap intr = new IntrospectorWithMap()
                .add("findPropertyIgnoralByName", included);
        IntrospectorWithMap empty = new IntrospectorWithMap()
                .add("findPropertyIgnoralByName", JsonIgnoreProperties.Value.empty());
        assertEquals(JsonIgnoreProperties.Value.empty(),
                new AnnotationIntrospectorPair(empty, empty)
                        .findPropertyIgnoralByName(null, null));
        assertEquals(included,
                new AnnotationIntrospectorPair(empty, intr)
                        .findPropertyIgnoralByName(null, null));
        assertEquals(included,
                new AnnotationIntrospectorPair(intr, empty)
                        .findPropertyIgnoralByName(null, null));
    }

    // Provenance: IntrospectorPairTest#testIsIgnorableType().
    void testIsIgnorableTypeVpack() {
        IntrospectorWithMap trueIntrospector = new IntrospectorWithMap()
                .add("isIgnorableType", Boolean.TRUE);
        IntrospectorWithMap falseIntrospector = new IntrospectorWithMap()
                .add("isIgnorableType", Boolean.FALSE);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .isIgnorableType(null, null));
        assertEquals(Boolean.TRUE,
                new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                        .isIgnorableType(null, null));
        assertEquals(Boolean.FALSE,
                new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                        .isIgnorableType(null, null));
    }

    // Provenance: IntrospectorPairTest#testFindWrapperName().
    void testFindWrapperNameVpack() {
        PropertyName withNamespace = PropertyName.construct("simple", "ns");
        PropertyName withoutNamespace = PropertyName.construct("other", null);
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findWrapperName", withNamespace);
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findWrapperName", withoutNamespace);

        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findClassDescription(null, null));
        assertSame(withNamespace,
                new AnnotationIntrospectorPair(intr1, intr2).findWrapperName(null, null));
        assertEquals(PropertyName.construct("other", "ns"),
                new AnnotationIntrospectorPair(intr2, intr1).findWrapperName(null, null));

        intr1 = new IntrospectorWithMap().add("findWrapperName", PropertyName.NO_NAME);
        intr2 = new IntrospectorWithMap().add("findWrapperName", withNamespace);
        assertSame(PropertyName.NO_NAME,
                new AnnotationIntrospectorPair(intr1, intr2).findWrapperName(null, null));
        assertSame(withNamespace,
                new AnnotationIntrospectorPair(intr2, intr1).findWrapperName(null, null));
    }

    // Provenance: IntrospectorPairTest#testHasAsValue().
    void testHasAsValueVpack() {
        assertBooleanPair("hasAsValue");
    }

    // Provenance: IntrospectorPairTest#testHasAsKey().
    void testHasAsKeyVpack() {
        assertBooleanPair("hasAsKey");
    }

    // Provenance: IntrospectorPairTest#testHasAnyGetter().
    void testHasAnyGetterVpack() {
        assertBooleanPair("hasAnyGetter");
    }

    // Provenance: IntrospectorPairTest#testFindTypeResolver().
    void testFindTypeResolverVpack() {
        IntrospectorWithMap intr1 = new IntrospectorWithMap()
                .add("findTypeResolverBuilder", "resolver1");
        IntrospectorWithMap intr2 = new IntrospectorWithMap()
                .add("findTypeResolverBuilder", "resolver2");
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .findTypeResolverBuilder(null, null));
        assertEquals("resolver1",
                new AnnotationIntrospectorPair(intr1, intr2)
                        .findTypeResolverBuilder(null, null));
        assertEquals("resolver2",
                new AnnotationIntrospectorPair(intr2, intr1)
                        .findTypeResolverBuilder(null, null));
        assertEquals("resolver2",
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, intr2)
                        .findTypeResolverBuilder(null, null));
    }

    // Provenance: IntrospectorPairTest#testIsTypeId().
    void testIsTypeIdVpack() {
        IntrospectorWithMap trueIntrospector = new IntrospectorWithMap()
                .add("isTypeId", Boolean.TRUE);
        IntrospectorWithMap falseIntrospector = new IntrospectorWithMap()
                .add("isTypeId", Boolean.FALSE);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .isTypeId(null, null));
        assertEquals(Boolean.TRUE,
                new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                        .isTypeId(null, null));
        assertEquals(Boolean.FALSE,
                new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                        .isTypeId(null, null));
        assertEquals(Boolean.TRUE,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, trueIntrospector)
                        .isTypeId(null, null));
        assertEquals(Boolean.FALSE,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, falseIntrospector)
                        .isTypeId(null, null));
    }

    // Provenance: IntrospectorPairTest#testHasAnySetter().
    void testHasAnySetterVpack() {
        IntrospectorWithMap trueIntrospector = new IntrospectorWithMap()
                .add("hasAnySetter", Boolean.TRUE);
        IntrospectorWithMap falseIntrospector = new IntrospectorWithMap()
                .add("hasAnySetter", Boolean.FALSE);
        assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                .hasAnySetter(null, null));
        assertEquals(Boolean.TRUE,
                new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                        .hasAnySetter(null, null));
        assertEquals(Boolean.TRUE,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, trueIntrospector)
                        .hasAnySetter(null, null));
        assertEquals(Boolean.FALSE,
                new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                        .hasAnySetter(null, null));
        assertEquals(Boolean.FALSE,
                new AnnotationIntrospectorPair(NO_ANNOTATIONS, falseIntrospector)
                        .hasAnySetter(null, null));
    }

    // Provenance: IntrospectorPairTest#testInclusionMerging().
    void testInclusionMergingVpack() {
        IntrospectorWithMap primary = new IntrospectorWithMap()
                .add("findPropertyInclusion", JsonInclude.Value.empty()
                        .withContentInclusion(JsonInclude.Include.ALWAYS)
                        .withValueInclusion(JsonInclude.Include.NON_ABSENT));
        IntrospectorWithMap secondary = new IntrospectorWithMap()
                .add("findPropertyInclusion", JsonInclude.Value.empty()
                        .withContentInclusion(JsonInclude.Include.NON_EMPTY)
                        .withValueInclusion(JsonInclude.Include.USE_DEFAULTS));
        JsonInclude.Value v12 = new AnnotationIntrospectorPair(primary, secondary)
                .findPropertyInclusion(null, null);
        JsonInclude.Value v21 = new AnnotationIntrospectorPair(secondary, primary)
                .findPropertyInclusion(null, null);
        assertEquals(JsonInclude.Include.ALWAYS, v12.getContentInclusion());
        assertEquals(JsonInclude.Include.NON_ABSENT, v12.getValueInclusion());
        assertEquals(JsonInclude.Include.NON_EMPTY, v21.getContentInclusion());
        assertEquals(JsonInclude.Include.NON_ABSENT, v21.getValueInclusion());
    }

    // Provenance: IntrospectorPairTest#testMergingIntrospectorsForInjection().
    void testMergingIntrospectorsForInjectionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .injectableValues(new TestInjector())
                .annotationIntrospector(new AnnotationIntrospectorPair(
                        new TestIntrospector(), new tools.jackson.databind.introspect.JacksonAnnotationIntrospector()))
                .build();
        byte[] input = VPackWireFixtureTest.hex("14 0b 43 66 6f 6f 43 62 6f 62 01");

        ReadableInjectedBean bean = mapper.readValue(input, ReadableInjectedBean.class);
        assertEquals("bob", bean.foo);
        assertEquals(SimpleEnum.TWO, bean.injectBean.value);

        boolean successReadingUnreadableInjectedBean;
        try {
            mapper.readValue(input, UnreadableInjectedBean.class);
            successReadingUnreadableInjectedBean = true;
        } catch (DatabindException e) {
            successReadingUnreadableInjectedBean = false;
            assertTrue(e.getMessage().contains("Conflicting setter definitions"));
        }
        assertFalse(successReadingUnreadableInjectedBean);
    }
private static void assertBooleanPair(String method) {
        IntrospectorWithMap trueIntrospector = new IntrospectorWithMap()
                .add(method, Boolean.TRUE);
        IntrospectorWithMap falseIntrospector = new IntrospectorWithMap()
                .add(method, Boolean.FALSE);
        if (method.equals("hasAsValue")) {
            assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                    .hasAsValue(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, NO_ANNOTATIONS)
                    .hasAsValue(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, trueIntrospector)
                    .hasAsValue(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, NO_ANNOTATIONS)
                    .hasAsValue(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, falseIntrospector)
                    .hasAsValue(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                    .hasAsValue(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                    .hasAsValue(null, null));
        } else if (method.equals("hasAsKey")) {
            assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                    .hasAsKey(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, NO_ANNOTATIONS)
                    .hasAsKey(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, trueIntrospector)
                    .hasAsKey(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, NO_ANNOTATIONS)
                    .hasAsKey(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, falseIntrospector)
                    .hasAsKey(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                    .hasAsKey(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                    .hasAsKey(null, null));
        } else {
            assertNull(new AnnotationIntrospectorPair(NO_ANNOTATIONS, NO_ANNOTATIONS)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, NO_ANNOTATIONS)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, trueIntrospector)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, NO_ANNOTATIONS)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(NO_ANNOTATIONS, falseIntrospector)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.TRUE, new AnnotationIntrospectorPair(trueIntrospector, falseIntrospector)
                    .hasAnyGetter(null, null));
            assertEquals(Boolean.FALSE, new AnnotationIntrospectorPair(falseIntrospector, trueIntrospector)
                    .hasAnyGetter(null, null));
        }
    }
private static final class IntrospectorWithMap extends AnnotationIntrospector {
        private final Map<String, Object> values = new HashMap<>();
        private Version version = Version.unknownVersion();

        IntrospectorWithMap add(String name, Object value) {
            values.put(name, value);
            return this;
        }

        IntrospectorWithMap version(Version value) {
            version = value;
            return this;
        }

        @Override
        public Version version() {
            return version;
        }

        @Override
        public JsonIgnoreProperties.Value findPropertyIgnoralByName(MapperConfig<?> config,
                Annotated ann) {
            return (JsonIgnoreProperties.Value) values.get("findPropertyIgnoralByName");
        }

        @Override
        public Boolean isIgnorableType(MapperConfig<?> config, AnnotatedClass ac) {
            return (Boolean) values.get("isIgnorableType");
        }

        @Override
        public PropertyName findWrapperName(MapperConfig<?> config, Annotated ann) {
            return (PropertyName) values.get("findWrapperName");
        }

        @Override
        public Boolean hasAsValue(MapperConfig<?> config, Annotated ann) {
            return (Boolean) values.get("hasAsValue");
        }

        @Override
        public Boolean hasAsKey(MapperConfig<?> config, Annotated ann) {
            return (Boolean) values.get("hasAsKey");
        }

        @Override
        public Boolean hasAnyGetter(MapperConfig<?> config, Annotated ann) {
            return (Boolean) values.get("hasAnyGetter");
        }

        @Override
        public Object findTypeResolverBuilder(MapperConfig<?> config, Annotated ann) {
            return values.get("findTypeResolverBuilder");
        }

        @Override
        public Boolean isTypeId(MapperConfig<?> config, AnnotatedMember member) {
            return (Boolean) values.get("isTypeId");
        }

        @Override
        public Boolean hasAnySetter(MapperConfig<?> config, Annotated ann) {
            return (Boolean) values.get("hasAnySetter");
        }

        @Override
        public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Annotated ann) {
            return (JsonInclude.Value) values.get("findPropertyInclusion");
        }
    }
private static final class TestIntrospector extends NopAnnotationIntrospector {
        @Override
        public JacksonInject.Value findInjectableValue(MapperConfig<?> config,
                AnnotatedMember member) {
            if (member.getRawType() == UnreadableBean.class) {
                return JacksonInject.Value.forId("jjj");
            }
            return null;
        }
    }
private static final class TestInjector extends InjectableValues {
        @Override
        public Object findInjectableValue(DeserializationContext ctxt, Object valueId,
                BeanProperty forProperty, Object beanInstance, Boolean optional,
                Boolean useInput) {
            if ("jjj".equals(valueId)) {
                UnreadableBean bean = new UnreadableBean();
                bean.setValue(1);
                return bean;
            }
            return null;
        }

        @Override
        public InjectableValues snapshot() {
            return this;
        }
    }
private enum SimpleEnum { ONE, TWO }
private static final class UnreadableBean {
        public SimpleEnum value;

        public void setValue(SimpleEnum value) {
            this.value = value;
        }

        public void setValue(Integer intValue) {
            this.value = SimpleEnum.values()[intValue];
        }

        public SimpleEnum getValue() {
            return value;
        }
    }
private static final class ReadableInjectedBean {
        public ReadableInjectedBean(
                @JacksonInject(useInput = OptBoolean.FALSE) UnreadableBean injectBean) {
            this.injectBean = injectBean;
        }

        @JsonProperty
        String foo;

        @JsonIgnore
        UnreadableBean injectBean;
    }
private static final class UnreadableInjectedBean {
        public UnreadableInjectedBean(@JacksonInject UnreadableBean injectBean) {
            this.injectBean = injectBean;
        }

        @JsonProperty
        private String foo;

        @JsonIgnore
        private UnreadableBean injectBean;
    }

    void __invoke_testVersionVpack() throws Exception {
        try {
            testVersionVpack();
        } finally {
        }
    }


    void __invoke_testPropertyIgnoralsVpack() throws Exception {
        try {
            testPropertyIgnoralsVpack();
        } finally {
        }
    }


    void __invoke_testIsIgnorableTypeVpack() throws Exception {
        try {
            testIsIgnorableTypeVpack();
        } finally {
        }
    }


    void __invoke_testFindWrapperNameVpack() throws Exception {
        try {
            testFindWrapperNameVpack();
        } finally {
        }
    }


    void __invoke_testHasAsValueVpack() throws Exception {
        try {
            testHasAsValueVpack();
        } finally {
        }
    }


    void __invoke_testHasAsKeyVpack() throws Exception {
        try {
            testHasAsKeyVpack();
        } finally {
        }
    }


    void __invoke_testHasAnyGetterVpack() throws Exception {
        try {
            testHasAnyGetterVpack();
        } finally {
        }
    }


    void __invoke_testFindTypeResolverVpack() throws Exception {
        try {
            testFindTypeResolverVpack();
        } finally {
        }
    }


    void __invoke_testIsTypeIdVpack() throws Exception {
        try {
            testIsTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testHasAnySetterVpack() throws Exception {
        try {
            testHasAnySetterVpack();
        } finally {
        }
    }


    void __invoke_testInclusionMergingVpack() throws Exception {
        try {
            testInclusionMergingVpack();
        } finally {
        }
    }


    void __invoke_testMergingIntrospectorsForInjectionVpack() throws Exception {
        try {
            testMergingIntrospectorsForInjectionVpack();
        } finally {
        }
    }

}
