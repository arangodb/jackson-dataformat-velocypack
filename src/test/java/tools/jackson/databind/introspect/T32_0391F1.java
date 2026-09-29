package tools.jackson.databind.introspect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AccessorNamingStrategy;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0391F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FIELD_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 1c 03");
private static final byte[] SETTER_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 59 28 2a 03");
private static Map<?, ?> asMap(ObjectMapper mapper, Object value) throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), Map.class);
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesForSimpleClass().
    void testGetSuperTypesForSimpleClassVpack() {
        AnnotatedClass ac = resolve(SimpleBean.class);
        List<JavaType> superTypes = ac.getSuperTypes();

        assertNotNull(superTypes);
        assertTrue(superTypes.isEmpty(),
                "SimpleBean should have no super types (Object is excluded)");
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesForChildClass().
    void testGetSuperTypesForChildClassVpack() {
        assertExactOrder(resolve(ChildBean.class).getSuperTypes(),
                Serializable.class, SimpleBean.class);
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesForMultiLevel().
    void testGetSuperTypesForMultiLevelVpack() {
        assertExactOrder(resolve(MultiLevel.class).getSuperTypes(),
                MarkerB.class, MarkerA.class,
                ChildBean.class, Serializable.class, SimpleBean.class);
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesForObjectClass().
    void testGetSuperTypesForObjectClassVpack() {
        AnnotatedClass ac = resolve(Object.class);
        List<JavaType> superTypes = ac.getSuperTypes();

        assertNotNull(superTypes);
        assertTrue(superTypes.isEmpty(),
                "Object class itself should have no super types");
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesForInterface().
    void testGetSuperTypesForInterfaceVpack() {
        assertExactOrder(resolve(MarkerB.class).getSuperTypes(), MarkerA.class);
    }

    // Provenance: AnnotatedClassGetSuperTypesTest#testGetSuperTypesReturnsUnmodifiableList().
    void testGetSuperTypesReturnsUnmodifiableListVpack() {
        List<JavaType> superTypes = resolve(ChildBean.class).getSuperTypes();
        assertThrows(UnsupportedOperationException.class, () -> superTypes.add(null));
    }
private static AnnotatedClass resolve(Class<?> cls) {
        DeserializationConfig config = MAPPER.deserializationConfig();
        JavaType type = MAPPER.constructType(cls);
        return AnnotatedClassResolver.resolve(config, type, config);
    }
private static void assertExactOrder(List<JavaType> actual, Class<?>... expected) {
        List<Class<?>> actualClasses = new ArrayList<>();
        for (JavaType type : actual) {
            actualClasses.add(type.getRawClass());
        }
        assertEquals(Arrays.asList(expected), actualClasses,
                "Super types should match in exact order");
    }
static class GetterBean2800_XZ {
        public int GetX() { return 3; }
        public int getY() { return 5; }
        public boolean IsZ() { return true; }
    }
static class SetterBean2800_Y {
        int yyy;

        public void PutY(int y) { yyy = y; }

        public void y(int y) { throw new Error(); }
        public void setY(int y) { throw new Error(); }
    }
static class FieldBean2800_X {
        public int _x = 1;
        public int y = 2;
        public int __z = 3;
    }
static class AccNaming2800Underscore extends AccessorNamingStrategy {
        @Override
        public String findNameForIsGetter(tools.jackson.databind.introspect.AnnotatedMethod method,
                String name) {
            if (name.startsWith("Is")) {
                return name.substring(2);
            }
            return null;
        }

        @Override
        public String findNameForRegularGetter(tools.jackson.databind.introspect.AnnotatedMethod method,
                String name) {
            if (name.startsWith("Get")) {
                return name.substring(3);
            }
            return null;
        }

        @Override
        public String findNameForMutator(tools.jackson.databind.introspect.AnnotatedMethod method,
                String name) {
            if (name.startsWith("Put")) {
                return name.substring(3);
            }
            return null;
        }

        @Override
        public String modifyFieldName(tools.jackson.databind.introspect.AnnotatedField field,
                String name) {
            if (name.startsWith("_") && !name.startsWith("__")) {
                return name.substring(1);
            }
            return null;
        }
    }
static class AccNaming2800Provider extends DefaultAccessorNamingStrategy.Provider {
        @Override
        public AccessorNamingStrategy forPOJO(MapperConfig<?> config, AnnotatedClass valueClass) {
            return new AccNaming2800Underscore();
        }
    }
static class FirstLetterVariesBean {
        public boolean island() { return true; }
        public int get4Roses() { return 42; }
        public int getValue() { return 31337; }
    }
static class SimpleBean {
        public int value;
    }
static class ChildBean extends SimpleBean implements Serializable {
        public String name;
    }
interface MarkerA { }
interface MarkerB extends MarkerA { }
static class MultiLevel extends ChildBean implements MarkerB { }
static class SomeBean {
        private String value;

        public SomeBean(String value) {
            this.value = value;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    void __invoke_testGetSuperTypesForSimpleClassVpack() throws Exception {
        try {
            testGetSuperTypesForSimpleClassVpack();
        } finally {
        }
    }


    void __invoke_testGetSuperTypesForChildClassVpack() throws Exception {
        try {
            testGetSuperTypesForChildClassVpack();
        } finally {
        }
    }


    void __invoke_testGetSuperTypesForMultiLevelVpack() throws Exception {
        try {
            testGetSuperTypesForMultiLevelVpack();
        } finally {
        }
    }


    void __invoke_testGetSuperTypesForObjectClassVpack() throws Exception {
        try {
            testGetSuperTypesForObjectClassVpack();
        } finally {
        }
    }


    void __invoke_testGetSuperTypesForInterfaceVpack() throws Exception {
        try {
            testGetSuperTypesForInterfaceVpack();
        } finally {
        }
    }


    void __invoke_testGetSuperTypesReturnsUnmodifiableListVpack() throws Exception {
        try {
            testGetSuperTypesReturnsUnmodifiableListVpack();
        } finally {
        }
    }

}
