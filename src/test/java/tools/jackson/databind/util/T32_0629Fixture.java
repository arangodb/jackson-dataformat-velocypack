package tools.jackson.databind.util;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyMetadata;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.util.SimpleBeanPropertyDefinition;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0629Fixture {
private final VPackMapper mapper = new VPackMapper();
private AnnotatedClass resolveAnnotatedClass(Class<?> cls) {
        DeserializationConfig config = mapper.deserializationConfig();
        JavaType type = mapper.constructType(cls);
        return AnnotatedClassResolver.resolve(config, type, config);
    }
private AnnotatedField fieldOf(Class<?> cls) {
        return resolveAnnotatedClass(cls).fields().iterator().next();
    }
private AnnotatedMethod getterOf(Class<?> cls, String name) {
        return resolveAnnotatedClass(cls).findMethod(name, new Class<?>[0]);
    }
private AnnotatedMethod setterOf(Class<?> cls, String name, Class<?>... paramTypes) {
        return resolveAnnotatedClass(cls).findMethod(name, paramTypes);
    }
private AnnotatedParameter constructorParamOf(Class<?> cls, int index) {
        return resolveAnnotatedClass(cls).getConstructors().get(0).getParameter(index);
    }
private DeserializationConfig config() { return mapper.deserializationConfig(); }

    void constructWithMember() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        assertEquals("name", prop.getName());
        assertSame(field, prop.getPrimaryMember());
    }

    void constructWithName() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("foo"));
        assertEquals("foo", prop.getName());
    }

    void constructWithIncludeNull() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, (JsonInclude.Include) null);
        assertNotNull(prop.findInclusion());
    }

    void constructWithIncludeUseDefaults() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, JsonInclude.Include.USE_DEFAULTS);
        assertNotNull(prop.findInclusion());
    }

    void constructWithIncludeNonNull() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, JsonInclude.Include.NON_NULL);
        assertEquals(JsonInclude.Include.NON_NULL, prop.findInclusion().getValueInclusion());
    }

    void constructWithNullMetadata() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, (JsonInclude.Value) null);
        assertEquals(PropertyMetadata.STD_OPTIONAL, prop.getMetadata());
    }

    void constructWithExplicitMetadata() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), PropertyMetadata.STD_REQUIRED,
                (JsonInclude.Value) null);
        assertEquals(PropertyMetadata.STD_REQUIRED, prop.getMetadata());
    }

    void accessorsWithFieldMember() {
        AnnotatedField field = fieldOf(SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        assertTrue(prop.hasField());
        assertFalse(prop.hasGetter());
        assertFalse(prop.hasSetter());
        assertFalse(prop.hasConstructorParameter());
        assertSame(field, prop.getField());
        assertNull(prop.getGetter());
        assertNull(prop.getSetter());
        assertNull(prop.getConstructorParameter());
        Iterator<AnnotatedParameter> it = prop.getConstructorParameters();
        assertFalse(it.hasNext());
    }

    void accessorsWithGetterMember() {
        AnnotatedMethod getter = getterOf(SimpleBean.class, "getName");
        assertNotNull(getter);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), getter);
        assertTrue(prop.hasGetter());
        assertFalse(prop.hasSetter());
        assertFalse(prop.hasField());
        assertFalse(prop.hasConstructorParameter());
        assertSame(getter, prop.getGetter());
        assertNull(prop.getSetter());
        assertNull(prop.getField());
        assertNull(prop.getConstructorParameter());
    }

    void accessorsWithSetterMember() {
        AnnotatedMethod setter = setterOf(SimpleBean.class, "setName", String.class);
        assertNotNull(setter);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), setter);
        assertTrue(prop.hasSetter());
        assertFalse(prop.hasGetter());
        assertFalse(prop.hasField());
        assertFalse(prop.hasConstructorParameter());
        assertSame(setter, prop.getSetter());
        assertNull(prop.getGetter());
        assertNull(prop.getField());
        assertNull(prop.getConstructorParameter());
    }

    void accessorsWithConstructorParameter() {
        AnnotatedParameter param = constructorParamOf(SimpleBean.class, 0);
        assertNotNull(param);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), param);
        assertTrue(prop.hasConstructorParameter());
        assertFalse(prop.hasGetter());
        assertFalse(prop.hasSetter());
        assertFalse(prop.hasField());
        assertSame(param, prop.getConstructorParameter());
        assertNull(prop.getGetter());
        assertNull(prop.getSetter());
        assertNull(prop.getField());
        Iterator<AnnotatedParameter> it = prop.getConstructorParameters();
        assertTrue(it.hasNext());
        assertSame(param, it.next());
        assertFalse(it.hasNext());
    }

    void accessorsWithNullMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        assertFalse(prop.hasGetter());
        assertFalse(prop.hasSetter());
        assertFalse(prop.hasField());
        assertFalse(prop.hasConstructorParameter());
        assertNull(prop.getGetter());
        assertNull(prop.getSetter());
        assertNull(prop.getField());
        assertNull(prop.getConstructorParameter());
        assertNull(prop.getPrimaryMember());
    }
static class SimpleBean {
        public String name;

        public SimpleBean(String name) { this.name = name; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    void __invoke_constructWithMember() throws Exception {
        try {
            constructWithMember();
        } finally {
        }
    }


    void __invoke_constructWithName() throws Exception {
        try {
            constructWithName();
        } finally {
        }
    }


    void __invoke_constructWithIncludeNull() throws Exception {
        try {
            constructWithIncludeNull();
        } finally {
        }
    }


    void __invoke_constructWithIncludeUseDefaults() throws Exception {
        try {
            constructWithIncludeUseDefaults();
        } finally {
        }
    }


    void __invoke_constructWithIncludeNonNull() throws Exception {
        try {
            constructWithIncludeNonNull();
        } finally {
        }
    }


    void __invoke_constructWithNullMetadata() throws Exception {
        try {
            constructWithNullMetadata();
        } finally {
        }
    }


    void __invoke_constructWithExplicitMetadata() throws Exception {
        try {
            constructWithExplicitMetadata();
        } finally {
        }
    }


    void __invoke_accessorsWithFieldMember() throws Exception {
        try {
            accessorsWithFieldMember();
        } finally {
        }
    }


    void __invoke_accessorsWithGetterMember() throws Exception {
        try {
            accessorsWithGetterMember();
        } finally {
        }
    }


    void __invoke_accessorsWithSetterMember() throws Exception {
        try {
            accessorsWithSetterMember();
        } finally {
        }
    }


    void __invoke_accessorsWithConstructorParameter() throws Exception {
        try {
            accessorsWithConstructorParameter();
        } finally {
        }
    }


    void __invoke_accessorsWithNullMember() throws Exception {
        try {
            accessorsWithNullMember();
        } finally {
        }
    }

}
