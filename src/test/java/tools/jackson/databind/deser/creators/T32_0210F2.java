package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.introspect.PotentialCreator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0210F2 {
private static final byte[] PROPERTIES_CREATOR = VPackWireFixtureTest.hex(
            "14 2d 44 62 65 61 6e 14 25 44 6c 69 73 74 13 09 41 61 41 62 41 63 03 "
          + "45 69 6e 6e 65 72 14 0e 44 6e 61 6d 65 45 69 6e 6e 65 72 01 02 01");
private static final byte[] DELEGATING_CREATOR = VPackWireFixtureTest.hex(
            "14 11 44 62 65 61 6e 13 09 41 61 41 62 41 63 03 01");
private static final byte[] IMPLICIT_FACTORY = VPackWireFixtureTest.hex("28 2a");
private static final byte[] NO_PARAMS_PROPERTIES = VPackWireFixtureTest.hex(
            "14 17 49 70 72 6f 64 75 63 74 49 64 31 44 6e 61 6d 65 43 66 6f 6f 02");
private static final byte[] PRODUCT_ID_ONLY = VPackWireFixtureTest.hex(
            "14 0e 49 70 72 6f 64 75 63 74 49 64 31 01");
private static final byte[] POLYMORPHIC_DOG = VPackWireFixtureTest.hex(
            "14 2a 44 74 79 70 65 43 64 6f 67 44 6e 61 6d 65 44 46 69 64 6f "
          + "4a 62 61 72 6b 56 6f 6c 75 6d 65 1b 00 00 00 00 00 c0 57 40 03");
private static final byte[] POLYMORPHIC_CAT = VPackWireFixtureTest.hex(
            "14 2e 44 6e 61 6d 65 48 4d 61 63 61 76 69 74 79 44 74 79 70 65 43 63 61 74 "
          + "45 6c 69 76 65 73 28 12 4a 6c 69 6b 65 73 43 72 65 61 6d 19 04");
private static final byte[] POLYMORPHIC_CAT_REORDERED = VPackWireFixtureTest.hex(
            "14 23 4a 6c 69 6b 65 73 43 72 65 61 6d 1a 44 6e 61 6d 65 45 56 65 6e 6c 61 "
          + "44 74 79 70 65 43 63 61 74 03");
private static final byte[] POLYMORPHIC_NUMBERED = VPackWireFixtureTest.hex(
            "14 16 45 77 68 69 63 68 31 43 6f 70 74 47 6f 68 20 68 61 69 21 02");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper STRICT_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new NoParamsIntrospector())
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: PolymorphicPropsCreatorsTest#testManualPolymorphicDog.
    void testManualPolymorphicDog() throws Exception {
        Animal animal = MAPPER.readValue(POLYMORPHIC_DOG, Animal.class);
        assertEquals(Dog.class, animal.getClass());
        assertEquals("Fido", animal.name);
        assertEquals(95.0, ((Dog) animal).barkVolume);
    }

    // Provenance: PolymorphicPropsCreatorsTest#testManualPolymorphicCatBasic.
    void testManualPolymorphicCatBasic() throws Exception {
        Animal animal = MAPPER.readValue(POLYMORPHIC_CAT, Animal.class);
        assertEquals(Cat.class, animal.getClass());
        assertEquals("Macavity", animal.name);
        Cat cat = (Cat) animal;
        assertEquals(18, cat.lives);
        assertFalse(cat.likesCream);
    }

    // Provenance: PolymorphicPropsCreatorsTest#testManualPolymorphicCatWithReorder.
    void testManualPolymorphicCatWithReorder() throws Exception {
        Animal animal = MAPPER.readValue(POLYMORPHIC_CAT_REORDERED, Animal.class);
        assertEquals(Cat.class, animal.getClass());
        assertEquals("Venla", animal.name);
        assertTrue(((Cat) animal).likesCream);
    }

    // Provenance: PolymorphicPropsCreatorsTest#testManualPolymorphicWithNumbered.
    void testManualPolymorphicWithNumbered() throws Exception {
        AbstractRoot result = MAPPER.readValue(POLYMORPHIC_NUMBERED, AbstractRoot.class);
        assertNotNull(result);
        assertEquals("oh hai!", result.getOpt());
    }
static class OuterBean4602 {
        private final Bean4602 bean;

        @JsonCreator
        public OuterBean4602(@JsonProperty("bean") Bean4602 bean) {
            this.bean = bean;
        }

        public Bean4602 getBean() { return bean; }
    }
static class Bean4602 {
        private final List<String> list;
        private final InnerBean4602 inner;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Bean4602(@JsonProperty("list") List<String> list,
                @JsonProperty("inner") InnerBean4602 inner) {
            this.list = list;
            this.inner = inner;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        private static Bean4602 of(List<String> list) {
            return new Bean4602(list, new InnerBean4602("default"));
        }

        public List<String> getList() { return list; }
        public InnerBean4602 getInner() { return inner; }
    }
static class InnerBean4602 {
        private final String name;

        @JsonCreator
        public InnerBean4602(@JsonProperty("name") String name) { this.name = name; }

        public String getName() { return name; }
    }
static class ExampleDto2962 {
        final int version;

        ExampleDto2962(int version) { this.version = version; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static ExampleDto2962 fromJson(Json2962 json) {
            return new ExampleDto2962(json.version);
        }

        static class Json2962 {
            public int version;
        }
    }
static class Pojo5318Working {
        final int productId;
        final String name;

        public Pojo5318Working() { this(0, null); }

        public Pojo5318Working(int productId, String name) {
            this.productId = productId;
            this.name = name;
        }
    }
static class Pojo5318Annotated {
        @JsonCreator
        public Pojo5318Annotated() { }

        public Pojo5318Annotated(int productId, String name) {
            throw new IllegalStateException("Should not be called");
        }
    }
static class Pojo5318Ignore {
        protected Pojo5318Ignore() { }

        @JsonIgnore
        public Pojo5318Ignore(int productId, String name) {
            throw new IllegalStateException("Should not be called");
        }
    }
static class NoParamsIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter
                    && member.getDeclaringClass() == Pojo5318Working.class) {
                return switch (parameter.getIndex()) {
                case 0 -> "productId";
                case 1 -> "name";
                default -> null;
                };
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
static class User5045 {
        public int age;

        public User5045(@ImplicitName("age") int age) {
            throw new IllegalStateException("Should not be called");
        }

        @JsonCreator
        public User5045() { age = -1; }

        public int getAge() { return age; }
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
@SuppressWarnings("serial")
    static class AI5045 extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            ImplicitName annotation = member.getAnnotation(ImplicitName.class);
            return annotation == null ? null : annotation.value();
        }

        @Override
        public PotentialCreator findPreferredCreator(MapperConfig<?> config,
                AnnotatedClass valueClass,
                List<PotentialCreator> declaredConstructors,
                List<PotentialCreator> declaredFactories,
                Optional<PotentialCreator> zeroParamsConstructor) {
            for (PotentialCreator creator : declaredConstructors) {
                if (creator.paramCount() != 0) {
                    return creator;
                }
            }
            return null;
        }
    }
static class Animal {
        public String name;

        protected Animal() { }

        @JsonCreator
        public static Animal create(@JsonProperty("type") String type) {
            if ("dog".equals(type)) return new Dog();
            if ("cat".equals(type)) return new Cat();
            throw new IllegalArgumentException("No such animal type ('" + type + "')");
        }
    }
static class Dog extends Animal {
        double barkVolume;

        public Dog() { }
        public void setBarkVolume(double value) { barkVolume = value; }
    }
static class Cat extends Animal {
        boolean likesCream;
        public int lives;

        public Cat() { }
        public void setLikesCream(boolean value) { likesCream = value; }
    }
abstract static class AbstractRoot {
        protected final String opt;

        protected AbstractRoot(String opt) { this.opt = opt; }

        @JsonCreator
        public static final AbstractRoot make(@JsonProperty("which") int which,
                @JsonProperty("opt") String opt) {
            if (which == 1) return new One(opt);
            throw new RuntimeException("cannot instantiate " + which);
        }

        public abstract int getWhich();
        public final String getOpt() { return opt; }
    }
static final class One extends AbstractRoot {
        protected One(String opt) { super(opt); }
        @Override public int getWhich() { return 1; }
    }

    void __invoke_testManualPolymorphicDog() throws Exception {
        try {
            testManualPolymorphicDog();
        } finally {
        }
    }


    void __invoke_testManualPolymorphicCatBasic() throws Exception {
        try {
            testManualPolymorphicCatBasic();
        } finally {
        }
    }


    void __invoke_testManualPolymorphicCatWithReorder() throws Exception {
        try {
            testManualPolymorphicCatWithReorder();
        } finally {
        }
    }


    void __invoke_testManualPolymorphicWithNumbered() throws Exception {
        try {
            testManualPolymorphicWithNumbered();
        } finally {
        }
    }

}
