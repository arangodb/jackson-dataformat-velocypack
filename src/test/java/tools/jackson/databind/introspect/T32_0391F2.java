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
import tools.jackson.databind.introspect.AnnotatedConstructor;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0391F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FIELD_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 1c 03");
private static final byte[] SETTER_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 59 28 2a 03");
private static Map<?, ?> asMap(ObjectMapper mapper, Object value) throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), Map.class);
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

    // Provenance: AnnotatedMemberEqualityTest#testAnnotatedConstructorEquality().
    void testAnnotatedConstructorEqualityVpack() {
        DeserializationConfig context = MAPPER.deserializationConfig();
        JavaType beanType = MAPPER.constructType(SomeBean.class);

        AnnotatedClass instance1 = AnnotatedClassResolver.resolve(context, beanType, context);
        AnnotatedClass instance2 = AnnotatedClassResolver.resolve(context, beanType, context);

        AnnotatedConstructor constructor1 = instance1.getConstructors().get(0);
        AnnotatedConstructor constructor2 = instance2.getConstructors().get(0);

        assertEquals(instance1, instance2);
        assertEquals(constructor1.getAnnotated(), constructor2.getAnnotated());
        assertEquals(constructor1, constructor2);
        assertEquals(constructor1.getParameter(0), constructor2.getParameter(0));
    }

    // Provenance: AnnotatedMemberEqualityTest#testAnnotatedFieldEquality().
    void testAnnotatedFieldEqualityVpack() {
        DeserializationConfig context = MAPPER.deserializationConfig();
        JavaType beanType = MAPPER.constructType(SomeBean.class);

        AnnotatedClass instance1 = AnnotatedClassResolver.resolve(context, beanType, context);
        AnnotatedClass instance2 = AnnotatedClassResolver.resolve(context, beanType, context);

        AnnotatedField field1 = instance1.fields().iterator().next();
        AnnotatedField field2 = instance2.fields().iterator().next();

        assertEquals(instance1, instance2);
        assertEquals(field1.getAnnotated(), field2.getAnnotated());
        assertEquals(field1, field2);
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

    void __invoke_testAnnotatedConstructorEqualityVpack() throws Exception {
        try {
            testAnnotatedConstructorEqualityVpack();
        } finally {
        }
    }


    void __invoke_testAnnotatedFieldEqualityVpack() throws Exception {
        try {
            testAnnotatedFieldEqualityVpack();
        } finally {
        }
    }

}
