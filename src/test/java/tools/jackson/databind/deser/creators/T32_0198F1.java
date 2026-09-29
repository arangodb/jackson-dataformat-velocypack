package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0198F1 {
private static final byte[] VALUE_137 = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 29 89 00 01");
private static final byte[] VALUE_137_DECIMAL = VPackWireFixtureTest.hex(
            "14 11 45 76 61 6c 75 65 c8 02 ff ff ff ff 13 70 01");
private static final byte[] VALUE_A3_B4 = VPackWireFixtureTest.hex(
            "14 09 41 61 33 41 62 34 02");
private static final byte[] SCALAR_2812 = VPackWireFixtureTest.hex(
            "29 fc 0a");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FOO_EMPTY = VPackWireFixtureTest.hex(
            "14 03 00");
private static final byte[] FOO_ID = VPackWireFixtureTest.hex(
            "14 16 42 69 64 49 73 6f 6d 65 74 68 69 6e 67 44 6e 61 6d 65 18 02");
private static final byte[] FOO_ID_AND_NAME = VPackWireFixtureTest.hex(
            "14 1a 42 69 64 49 73 6f 6d 65 74 68 69 6e 67 "
          + "44 6e 61 6d 65 44 6e 61 6d 65 02");
private static final byte[] FOO_OUTPUT_EMPTY = VPackWireFixtureTest.hex(
            "0b 0f 02 42 69 64 18 44 6e 61 6d 65 18 03 07");
private static final byte[] FOO_OUTPUT_ID = VPackWireFixtureTest.hex(
            "0b 18 02 42 69 64 49 73 6f 6d 65 74 68 69 6e 67 "
          + "44 6e 61 6d 65 18 03 10");
private static final byte[] FOO_OUTPUT_ID_AND_NAME = VPackWireFixtureTest.hex(
            "0b 1c 02 42 69 64 49 73 6f 6d 65 74 68 69 6e 67 "
          + "44 6e 61 6d 65 44 6e 61 6d 65 03 10");
private static final byte[] IMPLICIT_CREATOR_OUTPUT = VPackWireFixtureTest.hex(
            "0b 14 02 45 66 69 72 73 74 41 61 45 6f 74 68 65 72 33 03 0b");
private static final ObjectMapper PROPERTIES_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .build();
private static final ObjectMapper EXPLICIT_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.EXPLICIT_ONLY)
            .build();
private static final ObjectMapper MUST_ANNOTATE_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.DEFAULT.withRequireAnnotation(true))
            .build();

    // Provenance: CreatorImplicitNameTest#testBindingOfImplicitCreatorNames.
    void testBindingOfImplicitCreatorNames() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new ConstructorNameAI())
                .build();
        assertArrayEquals(IMPLICIT_CREATOR_OUTPUT,
                mapper.writeValueAsBytes(new Issue792Bean("a", "b")));
    }
private static <T> T assertReadEmpty(Class<T> type) {
        try {
            return MUST_ANNOTATE_MAPPER.readValue(EMPTY_OBJECT, type);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
private static void testFoo4860With(ObjectMapper mapper) throws Exception {
        assertArrayEquals(FOO_OUTPUT_EMPTY,
                mapper.writeValueAsBytes(mapper.readValue(FOO_EMPTY, Foo4860.class)));
        assertArrayEquals(FOO_OUTPUT_ID,
                mapper.writeValueAsBytes(mapper.readValue(FOO_ID, Foo4860.class)));
        assertArrayEquals(FOO_OUTPUT_ID_AND_NAME,
                mapper.writeValueAsBytes(mapper.readValue(FOO_ID_AND_NAME, Foo4860.class)));
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            ImplicitName annotation = member.getAnnotation(ImplicitName.class);
            return annotation == null ? null : annotation.value();
        }
    }
private static class SingleArgNotAnnotated {
        protected int v;

        SingleArgNotAnnotated() { v = -1; }

        public SingleArgNotAnnotated(@ImplicitName("value") int value) { v = value; }
    }
private static class SingleArgShort {
        protected short v;

        SingleArgShort() { v = -1; }

        public SingleArgShort(@ImplicitName("value") short value) { v = value; }
    }
private static class SingleArgNoMode {
        protected int v;

        SingleArgNoMode() { v = -1; }

        @JsonCreator
        public SingleArgNoMode(@ImplicitName("value") int value) { v = value; }
    }
private static class SingleArg2CtorsNotAnnotated {
        protected int v;

        SingleArg2CtorsNotAnnotated() { v = -1; }

        public SingleArg2CtorsNotAnnotated(@ImplicitName("value") int value) { v = value; }

        public SingleArg2CtorsNotAnnotated(@ImplicitName("value") long value) {
            v = (int) (value * 2);
        }
    }
private static class SingleArg1498 {
        final int v;

        SingleArg1498(@ImplicitName("bar") int value) { v = value; }
    }
private static class TwoArgsNotAnnotated {
        protected int _a, _b;

        public TwoArgsNotAnnotated(@ImplicitName("a") int a,
                @ImplicitName("b") int b) {
            _a = a;
            _b = b;
        }
    }
@JsonPropertyOrder({ "id", "name "})
    private static class Foo4860 {
        public String id;
        public String name;

        public Foo4860() { }

        public Foo4860(String id) {
            throw new IllegalStateException("Should not auto-detect args-taking constructor");
        }
    }
@JsonPropertyOrder({ "first", "second", "other" })
    private static class Issue792Bean {
        String value;

        public Issue792Bean(@JsonProperty("first") String a,
                @JsonProperty("second") String b) {
            value = a;
        }

        public String getCtor0() { return value; }

        public int getOther() { return 3; }
    }
private static class ConstructorNameAI extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return "ctor%d".formatted(parameter.getIndex());
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_testBindingOfImplicitCreatorNames() throws Exception {
        try {
            testBindingOfImplicitCreatorNames();
        } finally {
        }
    }

}
