package tools.jackson.databind.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.util.SimpleBeanPropertyDefinition;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0631Fixture {
private final VPackMapper mapper = new VPackMapper();
private DeserializationConfig config() { return mapper.deserializationConfig(); }
private AnnotatedField fieldOf(DeserializationConfig config) {
        var type = mapper.constructType(SimpleBean.class);
        var annotatedClass = tools.jackson.databind.introspect.AnnotatedClassResolver
                .resolve(config, type, config);
        return annotatedClass.fields().iterator().next();
    }

    void withSimpleNameDifferentName() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("foo"));
        var renamed = prop.withSimpleName("bar");
        assertNotSame(prop, renamed);
        assertEquals("bar", renamed.getName());
    }

    void withNameSameName() {
        AnnotatedField field = fieldOf(config());
        PropertyName name = PropertyName.construct("foo");
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field, name);
        assertSame(prop, prop.withName(name));
    }

    void withNameDifferentName() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("foo"));
        var renamed = prop.withName(PropertyName.construct("bar"));
        assertNotSame(prop, renamed);
        assertEquals("bar", renamed.getName());
    }

    void withMetadataSame() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), PropertyMetadata.STD_REQUIRED,
                (JsonInclude.Value) null);
        assertSame(prop, prop.withMetadata(PropertyMetadata.STD_REQUIRED));
    }

    void withMetadataDifferent() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), PropertyMetadata.STD_REQUIRED,
                (JsonInclude.Value) null);
        assertNotSame(prop, prop.withMetadata(PropertyMetadata.STD_OPTIONAL));
    }

    void withInclusionSame() {
        AnnotatedField field = fieldOf(config());
        JsonInclude.Value inclusion = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, null);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, inclusion);
        assertSame(prop, prop.withInclusion(inclusion));
    }

    void withInclusionDifferent() {
        AnnotatedField field = fieldOf(config());
        JsonInclude.Value first = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, null);
        JsonInclude.Value second = JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, first);
        assertNotSame(prop, prop.withInclusion(second));
    }

    void hasName() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("myProp"));
        assertTrue(prop.hasName(PropertyName.construct("myProp")));
        assertFalse(prop.hasName(PropertyName.construct("other")));
    }

    void isExplicitlyIncludedAndNamed() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        assertFalse(prop.isExplicitlyIncluded());
        assertFalse(prop.isExplicitlyNamed());
    }

    void getWrapperNameWithMember() {
        AnnotatedField field = fieldOf(config());
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        assertNull(prop.getWrapperName());
    }

    void getWrapperNameNullMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        assertNull(prop.getWrapperName());
    }

    void getWrapperNameNullIntrospector() {
        VPackMapper noIntrMapper = VPackMapper.builder().annotationIntrospector(null).build();
        DeserializationConfig config = noIntrMapper.deserializationConfig();
        var type = noIntrMapper.constructType(SimpleBean.class);
        var annotatedClass = tools.jackson.databind.introspect.AnnotatedClassResolver
                .resolve(config, type, config);
        AnnotatedField field = annotatedClass.fields().iterator().next();
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config, field);
        assertNull(prop.getWrapperName());
    }
static class SimpleBean {
        public String name;
    }

    void __invoke_withSimpleNameDifferentName() throws Exception {
        try {
            withSimpleNameDifferentName();
        } finally {
        }
    }


    void __invoke_withNameSameName() throws Exception {
        try {
            withNameSameName();
        } finally {
        }
    }


    void __invoke_withNameDifferentName() throws Exception {
        try {
            withNameDifferentName();
        } finally {
        }
    }


    void __invoke_withMetadataSame() throws Exception {
        try {
            withMetadataSame();
        } finally {
        }
    }


    void __invoke_withMetadataDifferent() throws Exception {
        try {
            withMetadataDifferent();
        } finally {
        }
    }


    void __invoke_withInclusionSame() throws Exception {
        try {
            withInclusionSame();
        } finally {
        }
    }


    void __invoke_withInclusionDifferent() throws Exception {
        try {
            withInclusionDifferent();
        } finally {
        }
    }


    void __invoke_hasName() throws Exception {
        try {
            hasName();
        } finally {
        }
    }


    void __invoke_isExplicitlyIncludedAndNamed() throws Exception {
        try {
            isExplicitlyIncludedAndNamed();
        } finally {
        }
    }


    void __invoke_getWrapperNameWithMember() throws Exception {
        try {
            getWrapperNameWithMember();
        } finally {
        }
    }


    void __invoke_getWrapperNameNullMember() throws Exception {
        try {
            getWrapperNameNullMember();
        } finally {
        }
    }


    void __invoke_getWrapperNameNullIntrospector() throws Exception {
        try {
            getWrapperNameNullIntrospector();
        } finally {
        }
    }

}
