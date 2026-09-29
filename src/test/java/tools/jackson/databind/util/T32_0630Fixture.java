package tools.jackson.databind.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.util.SimpleBeanPropertyDefinition;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0630Fixture {
private final VPackMapper mapper = new VPackMapper();
private AnnotatedClass resolveAnnotatedClass(DeserializationConfig config, Class<?> cls) {
        JavaType type = mapper.constructType(cls);
        return AnnotatedClassResolver.resolve(config, type, config);
    }
private AnnotatedField fieldOf(DeserializationConfig config, Class<?> cls) {
        return resolveAnnotatedClass(config, cls).fields().iterator().next();
    }
private DeserializationConfig config() { return mapper.deserializationConfig(); }

    void getName() {
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("myProp"));
        assertEquals("myProp", prop.getName());
        assertEquals("myProp", prop.getInternalName());
    }

    void getFullName() {
        PropertyName name = PropertyName.construct("myProp");
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field, name);
        assertEquals(name, prop.getFullName());
    }

    void getPrimaryTypeWithMember() {
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        JavaType type = prop.getPrimaryType();
        assertEquals(String.class, type.getRawClass());
    }

    void getPrimaryTypeWithNullMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        JavaType type = prop.getPrimaryType();
        assertEquals(TypeFactory.unknownType(), type);
    }

    void getRawPrimaryTypeWithMember() {
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        assertEquals(String.class, prop.getRawPrimaryType());
    }

    void getRawPrimaryTypeWithNullMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        assertEquals(Object.class, prop.getRawPrimaryType());
    }

    void findAliasesNoMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        List<PropertyName> aliases = prop.findAliases();
        assertEquals(List.of(), aliases);
    }

    void findAliasesWithMember() {
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config(), field);
        List<PropertyName> aliases = prop.findAliases();
        assertEquals(List.of(), aliases);
    }

    void findInclusion() {
        AnnotatedField field = fieldOf(config(), SimpleBean.class);
        JsonInclude.Value inclusion = JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, null);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), field, PropertyName.construct("x"), null, inclusion);
        assertSame(inclusion, prop.findInclusion());
    }

    void findAliasesNullMember() {
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(
                config(), null, PropertyName.construct("virtual"), null, (JsonInclude.Value) null);
        assertTrue(prop.findAliases().isEmpty());
    }

    void findAliasesWithJsonAlias() {
        DeserializationConfig config = mapper.deserializationConfig();
        AnnotatedField field = fieldOf(config, AliasedBean.class);
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config, field);
        List<PropertyName> aliases = prop.findAliases();
        assertNotNull(aliases);
        assertEquals(2, aliases.size());
    }

    void findAliasesNullIntrospector() {
        VPackMapper noIntrMapper = VPackMapper.builder().annotationIntrospector(null).build();
        DeserializationConfig config = noIntrMapper.deserializationConfig();
        JavaType type = noIntrMapper.constructType(SimpleBean.class);
        AnnotatedClass annotatedClass = AnnotatedClassResolver.resolve(config, type, config);
        AnnotatedField field = annotatedClass.fields().iterator().next();
        SimpleBeanPropertyDefinition prop = SimpleBeanPropertyDefinition.construct(config, field);
        assertTrue(prop.findAliases().isEmpty());
    }
static class SimpleBean {
        public String name;
    }
static class AliasedBean {
        @JsonAlias({ "nm", "fullName" })
        public String name;
    }

    void __invoke_getName() throws Exception {
        try {
            getName();
        } finally {
        }
    }


    void __invoke_getFullName() throws Exception {
        try {
            getFullName();
        } finally {
        }
    }


    void __invoke_getPrimaryTypeWithMember() throws Exception {
        try {
            getPrimaryTypeWithMember();
        } finally {
        }
    }


    void __invoke_getPrimaryTypeWithNullMember() throws Exception {
        try {
            getPrimaryTypeWithNullMember();
        } finally {
        }
    }


    void __invoke_getRawPrimaryTypeWithMember() throws Exception {
        try {
            getRawPrimaryTypeWithMember();
        } finally {
        }
    }


    void __invoke_getRawPrimaryTypeWithNullMember() throws Exception {
        try {
            getRawPrimaryTypeWithNullMember();
        } finally {
        }
    }


    void __invoke_findAliasesNoMember() throws Exception {
        try {
            findAliasesNoMember();
        } finally {
        }
    }


    void __invoke_findAliasesWithMember() throws Exception {
        try {
            findAliasesWithMember();
        } finally {
        }
    }


    void __invoke_findInclusion() throws Exception {
        try {
            findInclusion();
        } finally {
        }
    }


    void __invoke_findAliasesNullMember() throws Exception {
        try {
            findAliasesNullMember();
        } finally {
        }
    }


    void __invoke_findAliasesWithJsonAlias() throws Exception {
        try {
            findAliasesWithJsonAlias();
        } finally {
        }
    }


    void __invoke_findAliasesNullIntrospector() throws Exception {
        try {
            findAliasesNullIntrospector();
        } finally {
        }
    }

}
