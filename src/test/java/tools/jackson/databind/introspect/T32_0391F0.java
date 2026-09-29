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

import tools.jackson.dataformat.velocypack.*;

class T32_0391F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] FIELD_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 28 1c 03");
private static final byte[] SETTER_INPUT = VPackWireFixtureTest.hex(
            "0b 08 01 41 59 28 2a 03");

    // Provenance: AccessorNamingStrategyTest#testGetterNaming().
    void testGetterNamingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new AccNaming2800Provider()).build();
        assertEquals(Map.of("X", 3, "Z", true),
                mapper.readValue(mapper.writeValueAsBytes(new GetterBean2800_XZ()), Map.class));
    }

    // Provenance: AccessorNamingStrategyTest#testSetterNaming().
    void testSetterNamingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new AccNaming2800Provider()).build();
        SetterBean2800_Y result = mapper.readValue(SETTER_INPUT, SetterBean2800_Y.class);
        assertEquals(42, result.yyy);
    }

    // Provenance: AccessorNamingStrategyTest#testFieldNaming().
    void testFieldNamingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new AccNaming2800Provider()).build();
        assertEquals(Map.of("x", 1),
                mapper.readValue(mapper.writeValueAsBytes(new FieldBean2800_X()), Map.class));

        FieldBean2800_X result = mapper.readValue(FIELD_INPUT, FieldBean2800_X.class);
        assertEquals(28, result._x);
        assertEquals(2, result.y);
        assertEquals(3, result.__z);
    }

    // Provenance: AccessorNamingStrategyTest#testFirstLetterConfigs().
    void testFirstLetterConfigsVpack() throws Exception {
        final FirstLetterVariesBean input = new FirstLetterVariesBean();

        assertEquals(Map.of("value", 31337), asMap(MAPPER, input));

        ObjectMapper mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withFirstCharAcceptance(true, true)).build();
        assertEquals(Map.of("4Roses", 42, "land", true, "value", 31337),
                asMap(mapper, input));

        mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withFirstCharAcceptance(true, false)).build();
        assertEquals(Map.of("land", true, "value", 31337), asMap(mapper, input));

        mapper = VPackMapper.builder()
                .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                        .withFirstCharAcceptance(false, true)).build();
        assertEquals(Map.of("4Roses", 42, "value", 31337), asMap(mapper, input));
    }
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

    void __invoke_testGetterNamingVpack() throws Exception {
        try {
            testGetterNamingVpack();
        } finally {
        }
    }


    void __invoke_testSetterNamingVpack() throws Exception {
        try {
            testSetterNamingVpack();
        } finally {
        }
    }


    void __invoke_testFieldNamingVpack() throws Exception {
        try {
            testFieldNamingVpack();
        } finally {
        }
    }


    void __invoke_testFirstLetterConfigsVpack() throws Exception {
        try {
            testFirstLetterConfigsVpack();
        } finally {
        }
    }

}
