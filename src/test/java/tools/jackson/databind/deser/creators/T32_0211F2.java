package tools.jackson.databind.deser.creators;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0211F2 {
private static final byte[] SUBTYPES_113 = VPackWireFixtureTest.hex(
            "0b 5c 01 46 61 6e 69 6d 61 6c 0b 51 02 46 5f 63" +
                "6c 61 73 73 77 74 6f 6f 6c 73 2e 6a 61 63 6b 73" +
                "6f 6e 2e 64 61 74 61 62 69 6e 64 2e 64 65 73 65" +
                "72 2e 63 72 65 61 74 6f 72 73 2e 54 33 32 5f 30" +
                "32 31 31 46 32 24 44 6f 67 31 31 33 42 69 64 49" +
                "6e 69 63 65 20 64 6f 67 79 03 42 03");
private static final byte[] NAMED_STRING = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 46 66 6f 6f 62 61 72 01");
private static final byte[] IMPLICITLY_NAMED = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 78 01");
private static final byte[] IMPLICIT_DELEGATING = VPackWireFixtureTest.hex(
            "14 09 41 78 31 41 79 32 02");
private static final byte[] MULTIPLE_DOUBLE_CREATORS = VPackWireFixtureTest.hex(
            "14 27 45 65 6c 65 6d 73 13 1e "
          + "1b 00 00 00 00 00 00 f0 3f "
          + "1b 00 00 00 00 00 00 00 40 "
          + "1b 00 00 00 00 00 00 08 40 03 01");
private static final byte[] IMMUTABLE_ID_13 = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 0d 01");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: SingleImmutableFieldCreatorTest#testSetterlessProperty.
    void testSetterlessProperty() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new IdIntrospector())
                .build();
        ImmutableId output = mapper.readValue(IMMUTABLE_ID_13, ImmutableId.class);
        assertNotNull(output);
        assertEquals(13, output.id);
    }

    // Provenance: SingleImmutableFieldCreatorTest#testSetterlessPropertyWithJsonCreator.
    void testSetterlessPropertyWithJsonCreator() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new IdIntrospector())
                .build();
        ImmutableIdWithJsonCreatorAnnotation output = mapper.readValue(IMMUTABLE_ID_13,
                ImmutableIdWithJsonCreatorAnnotation.class);
        assertNotNull(output);
        assertEquals(13, output.id);
    }

    // Provenance: SingleImmutableFieldCreatorTest#testSetterlessPropertyWithJsonPropertyConstructor.
    void testSetterlessPropertyWithJsonPropertyConstructor() throws Exception {
        ImmutableIdWithJsonPropertyConstructorAnnotation output = MAPPER.readValue(
                IMMUTABLE_ID_13, ImmutableIdWithJsonPropertyConstructorAnnotation.class);
        assertNotNull(output);
        assertEquals(13, output.id);
    }
static class SingleNamedStringBean {
        final String _ss;

        @JsonCreator
        public SingleNamedStringBean(@JsonProperty("value") String ss) { _ss = ss; }
    }
static class SingleNamedButStillDelegating {
        protected final String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public SingleNamedButStillDelegating(@JsonProperty("foobar") String v) {
            value = v;
        }
    }
static class StringyBean {
        public final String value;

        protected StringyBean(String value) { this.value = value; }

        public String getValue() { return value; }
    }
static class StringyBeanWithProps {
        public final String value;

        @JsonCreator
        private StringyBeanWithProps(String v) { value = v; }

        public String getValue() { return value; }
    }
static class ExplicitFactoryBeanA {
        private String value;

        private ExplicitFactoryBeanA(String str) {
            throw new IllegalStateException("Should not get called!");
        }

        private ExplicitFactoryBeanA(String str, boolean b) { value = str; }

        @JsonCreator
        public static ExplicitFactoryBeanA create(String str) {
            ExplicitFactoryBeanA bean = new ExplicitFactoryBeanA(str, false);
            bean.value = str;
            return bean;
        }

        public String value() { return value; }
    }
static class ExplicitFactoryBeanB {
        private String value;

        @JsonCreator
        private ExplicitFactoryBeanB(String str) { value = str; }

        public static ExplicitFactoryBeanB valueOf(String str) {
            return new ExplicitFactoryBeanB(null);
        }

        public String value() { return value; }
    }
static class XY {
        public int x, y;
    }
static class SingleArgWithImplicit {
        protected XY _value;

        private SingleArgWithImplicit() {
            throw new Error("Should not get called");
        }

        private SingleArgWithImplicit(XY v, boolean bogus) { _value = v; }

        @JsonCreator
        public static SingleArgWithImplicit from(XY v) {
            return new SingleArgWithImplicit(v, true);
        }

        public XY getFoobar() { return _value; }
    }
static class DecVector3062 {
        List<Double> elems;

        public DecVector3062() { }

        public List<Double> getElems() { return elems; }
    }
static class ImmutableId {
        final int id;

        public ImmutableId(int id) { this.id = id; }

        public int getId() { return id; }
    }
static class ImmutableIdWithJsonCreatorAnnotation {
        final int id;

        @JsonCreator
        public ImmutableIdWithJsonCreatorAnnotation(int id) { this.id = id; }

        public int getId() { return id; }
    }
static class ImmutableIdWithJsonPropertyConstructorAnnotation {
        final int id;

        public ImmutableIdWithJsonPropertyConstructorAnnotation(
                @JsonProperty("id") int id) { this.id = id; }

        public int getId() { return id; }
    }
@SuppressWarnings("serial")
    static class MyParamIntrospector extends JacksonAnnotationIntrospector {
        private final String name;

        MyParamIntrospector(String name) { this.name = name; }

        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return parameter.getIndex() == 0 ? name : "param" + parameter.getIndex();
            }
            return super.findImplicitPropertyName(config, member);
        }
    }
@SuppressWarnings("serial")
    static class IdIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            return "id";
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "_class")
    @JsonSubTypes({ @JsonSubTypes.Type(Dog113.class) })
    static abstract class Animal113 {
        static final String ID = "id";

        private String id;

        @JsonCreator
        public Animal113(@JsonProperty(ID) String id) { this.id = id; }

        @JsonProperty(ID)
        public String getId() { return id; }
    }
static class Dog113 extends Animal113 {
        @JsonCreator
        public Dog113(@JsonProperty(ID) String id) { super(id); }
    }
static class AnimalWrapper113 {
        private Animal113 animal;

        @JsonCreator
        public AnimalWrapper113(@JsonProperty("animal") Animal113 animal) {
            this.animal = animal;
        }

        public Animal113 getAnimal() { return animal; }
    }

    void __invoke_testSetterlessProperty() throws Exception {
        try {
            testSetterlessProperty();
        } finally {
        }
    }


    void __invoke_testSetterlessPropertyWithJsonCreator() throws Exception {
        try {
            testSetterlessPropertyWithJsonCreator();
        } finally {
        }
    }


    void __invoke_testSetterlessPropertyWithJsonPropertyConstructor() throws Exception {
        try {
            testSetterlessPropertyWithJsonPropertyConstructor();
        } finally {
        }
    }

}
