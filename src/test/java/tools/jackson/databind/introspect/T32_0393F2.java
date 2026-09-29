package tools.jackson.databind.introspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedConstructor;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedMethod;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;
import tools.jackson.databind.introspect.PotentialCreator;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0393F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] SETTER_INPUT = VPackWireFixtureTest.hex(
            "0b 09 01 43 74 65 72 31 03");
private static final byte[] FOOBAR_INPUT = VPackWireFixtureTest.hex(
            "0b 15 02 43 62 61 72 43 62 61 72 43 66 6f 6f 43 66 6f 6f 03 0b");
private static final byte[] LIST_PROPERTIES_INPUT = VPackWireFixtureTest.hex(
            "0b 0d 01 44 6c 69 73 74 02 04 31 32 03");
private static final byte[] TWO_PROPERTIES_INPUT = VPackWireFixtureTest.hex(
            "0b 13 02 45 62 6f 67 75 73 28 0c 41 76 43 61 62 63 03 0b");
private static final byte[] INT_INPUT = VPackWireFixtureTest.hex("28 2a");
private static final byte[] LIST_INPUT = VPackWireFixtureTest.hex(
            "02 05 31 32 33");
private static final byte[] ARRAY_INPUT = VPackWireFixtureTest.hex(
            "02 03 1a");
private static final byte[] EXPLICIT_INPUT = VPackWireFixtureTest.hex(
            "0b 12 02 41 76 43 61 62 63 45 62 6f 67 75 73 33 09 03");

    // Provenance: DefaultCreatorDetection4584Test#testCanonicalConstructor1ArgPropertiesCreator().
    void testCanonicalConstructor1ArgPropertiesCreatorVpack() throws Exception {
        assertEquals("List[2]", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.PROPERTIES, List.class)), LIST_PROPERTIES_INPUT).value);
        assertEquals("List[-1]", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.PROPERTIES, List.class)), new byte[] { 0x0a }).value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testCanonicalConstructor2ArgPropertiesCreator().
    void testCanonicalConstructor2ArgPropertiesCreatorVpack() throws Exception {
        assertEquals("abc", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.PROPERTIES, String.class, Integer.TYPE)),
                TWO_PROPERTIES_INPUT).value);
        ObjectReader reader = readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.PROPERTIES, String.class, Integer.TYPE));
        assertEquals(null, reader.without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .<POJO4584>readValue(new byte[] { 0x0a }).value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testCanonicalConstructorDelegatingIntCreator().
    void testCanonicalConstructorDelegatingIntCreatorVpack() throws Exception {
        assertEquals("int[42]", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.DELEGATING, Integer.TYPE)), INT_INPUT).value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testCanonicalConstructorDelegatingListCreator().
    void testCanonicalConstructorDelegatingListCreatorVpack() throws Exception {
        assertEquals("List[3]", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.DELEGATING, List.class)), LIST_INPUT).value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testCanonicalConstructorDelegatingArrayCreator().
    void testCanonicalConstructorDelegatingArrayCreatorVpack() throws Exception {
        assertEquals("Array[1]", read(readerWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.DELEGATING, Object[].class)), ARRAY_INPUT).value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testDelegatingVsExplicit().
    void testDelegatingVsExplicitVpack() throws Exception {
        POJO4584Annotated result = mapperWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.DELEGATING, "wrongInt"))
                .readerFor(POJO4584Annotated.class).readValue(EXPLICIT_INPUT);
        assertEquals("abc", result.value);
    }

    // Provenance: DefaultCreatorDetection4584Test#testPropertiesBasedVsExplicit().
    void testPropertiesBasedVsExplicitVpack() throws Exception {
        POJO4584Annotated result = mapperWith(new PrimaryCreatorFindingIntrospector(
                JsonCreator.Mode.PROPERTIES, Integer.TYPE, String.class))
                .readerFor(POJO4584Annotated.class).readValue(EXPLICIT_INPUT);
        assertEquals("abc", result.value);
    }
private Map<?, ?> asMap(Object value) throws Exception {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(value), Map.class);
    }
private ObjectReader readerWith(AnnotationIntrospector introspector) {
        return mapperWith(introspector).readerFor(POJO4584.class);
    }
private static POJO4584 read(ObjectReader reader, byte[] input) throws Exception {
        return reader.readValue(input);
    }
private ObjectMapper mapperWith(AnnotationIntrospector introspector) {
        return VPackMapper.builder().annotationIntrospector(introspector).build();
    }
static class URLBean {
        public String getURL() { return "http:"; }
    }
static class ABean {
        public int getA() { return 3; }
    }
static class Bean2882 {
        public boolean island() { return true; }
        public boolean is_bad() { return true; }
        public int get_value() { return -2; }
        public int getter() { return -3; }
        public int getX() { return 1; }
        public void setter(int x) {
            throw new IllegalStateException("Should not get called");
        }
    }
@Target({ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Field1756 {
        String value() default "";
    }
interface Foobar {
        @JsonIgnore
        String foo();

        @JsonDeserialize(using = CustomStringDeserializer.class)
        String bar();
    }
private static class CustomStringDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            return p.getString();
        }
    }
public static class FoobarImpl implements Foobar {
        private final String foo;
        private final String bar;

        public FoobarImpl(@Field1756("foo") String foo,
                @Field1756("bar") String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public String foo() { return foo; }

        @Override
        public String bar() { return bar; }
    }
static class FoobarAnnotationIntrospector extends NopAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config,
                AnnotatedMember member) {
            if (member instanceof AnnotatedParameter) {
                Field1756 field = member.getAnnotation(Field1756.class);
                return field == null ? null : field.value();
            }
            if (member instanceof AnnotatedMethod) {
                return member.getName();
            }
            return null;
        }

        @Override
        public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
            AnnotatedConstructor ctor = (AnnotatedConstructor) a;
            if (ctor.getParameterCount() > 0
                    && ctor.getParameter(0).getAnnotation(Field1756.class) != null) {
                return JsonCreator.Mode.PROPERTIES;
            }
            return null;
        }
    }
static class Issue1756Module extends SimpleModule {
        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            context.appendAnnotationIntrospector(new FoobarAnnotationIntrospector());
        }
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitName4584 {
        String value();
    }
static class ImplicitName4584Introspector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config,
                AnnotatedMember member) {
            ImplicitName4584 annotation = member.getAnnotation(ImplicitName4584.class);
            return annotation == null ? null : annotation.value();
        }
    }
static class POJO4584 {
        final String value;

        POJO4584(@ImplicitName4584("v") String v,
                @ImplicitName4584("bogus") int bogus) {
            value = v;
        }

        public POJO4584(@ImplicitName4584("list") List<Object> list) {
            value = "List[" + ((list == null) ? -1 : list.size()) + "]";
        }

        public POJO4584(@ImplicitName4584("array") Object[] array) {
            value = "Array[" + ((array == null) ? -1 : array.length) + "]";
        }

        public static POJO4584 factoryInt(@ImplicitName4584("i") int i) {
            return new POJO4584("int[" + i + "]", 0);
        }

        public static POJO4584 factoryString(@ImplicitName4584("v") String v) {
            return new POJO4584(v, 0);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof POJO4584 pojo) && Objects.equals(pojo.value, value);
        }
    }
static class POJO4584Annotated {
        String value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        POJO4584Annotated(@ImplicitName4584("v") String v,
                @ImplicitName4584("bogus") int bogus) {
            value = v;
        }

        POJO4584Annotated(@ImplicitName4584("i") int i,
                @ImplicitName4584("foobar") String f) {
            throw new Error("Should NOT get called!");
        }

        public static POJO4584Annotated wrongInt(@ImplicitName4584("i") int i) {
            throw new Error("Should NOT get called!");
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static POJO4584Annotated factoryString(String v) {
            return new POJO4584Annotated(v, 0);
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof POJO4584Annotated pojo) && Objects.equals(pojo.value, value);
        }
    }
static class PrimaryCreatorFindingIntrospector extends ImplicitName4584Introspector {
        private static final long serialVersionUID = 1L;
        private final Class<?>[] argTypes;
        private final JsonCreator.Mode mode;
        private final String factoryName;

        PrimaryCreatorFindingIntrospector(JsonCreator.Mode mode, Class<?>... argTypes) {
            this.mode = mode;
            this.factoryName = null;
            this.argTypes = argTypes;
        }

        PrimaryCreatorFindingIntrospector(JsonCreator.Mode mode, String factoryName) {
            this.mode = mode;
            this.factoryName = factoryName;
            this.argTypes = new Class<?>[0];
        }

        @Override
        public PotentialCreator findPreferredCreator(MapperConfig<?> config,
                AnnotatedClass valueClass,
                List<PotentialCreator> declaredConstructors,
                List<PotentialCreator> declaredFactories,
                Optional<PotentialCreator> zeroParamsConstructor) {
            if (!valueClass.getRawType().toString().contains("4584")) {
                return null;
            }
            if (factoryName != null) {
                for (PotentialCreator creator : declaredFactories) {
                    if (creator.creator().getName().equals(factoryName)) {
                        return creator;
                    }
                }
                return null;
            }
            List<PotentialCreator> creators = new ArrayList<>(declaredConstructors);
            creators.addAll(declaredFactories);
            for (PotentialCreator creator : creators) {
                if (creator.paramCount() != argTypes.length) {
                    continue;
                }
                int i = 0;
                for (; i < argTypes.length; ++i) {
                    if (argTypes[i] != creator.param(i).getRawType()) {
                        break;
                    }
                }
                if (i == argTypes.length) {
                    creator.overrideMode(mode);
                    return creator;
                }
            }
            return null;
        }
    }

    void __invoke_testCanonicalConstructor1ArgPropertiesCreatorVpack() throws Exception {
        try {
            testCanonicalConstructor1ArgPropertiesCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCanonicalConstructor2ArgPropertiesCreatorVpack() throws Exception {
        try {
            testCanonicalConstructor2ArgPropertiesCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCanonicalConstructorDelegatingIntCreatorVpack() throws Exception {
        try {
            testCanonicalConstructorDelegatingIntCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCanonicalConstructorDelegatingListCreatorVpack() throws Exception {
        try {
            testCanonicalConstructorDelegatingListCreatorVpack();
        } finally {
        }
    }


    void __invoke_testCanonicalConstructorDelegatingArrayCreatorVpack() throws Exception {
        try {
            testCanonicalConstructorDelegatingArrayCreatorVpack();
        } finally {
        }
    }


    void __invoke_testDelegatingVsExplicitVpack() throws Exception {
        try {
            testDelegatingVsExplicitVpack();
        } finally {
        }
    }


    void __invoke_testPropertiesBasedVsExplicitVpack() throws Exception {
        try {
            testPropertiesBasedVsExplicitVpack();
        } finally {
        }
    }

}
