package tools.jackson.databind.deser.creators;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0202F2 {
private static final byte[] SNAKE_CASE_CREATOR = VPackWireFixtureTest.hex(
            "14 13 4b 70 61 72 61 6d 5f 6e 61 6d 65 30 43 31 73 74 01");
private static final byte[] UPPER_CAMEL_FACTORY = VPackWireFixtureTest.hex(
            "0b 22 02 45 4d 79 41 67 65 28 2a 46 4d 79 4e 61 6d 65 "
          + "4d 4e 6f 74 4d 79 52 65 61 6c 4e 61 6d 65 03 0b");
private static final byte[] OBJECT_ID_A = VPackWireFixtureTest.hex(
            "14 11 42 69 64 43 31 32 33 44 6e 61 6d 65 41 41 02");
private static final byte[] CREATOR_IDENTITY_INFO = VPackWireFixtureTest.hex(
            "14 34 49 63 68 69 6c 64 50 72 6f 70 45 63 68 69 6c 64 "
          + "46 70 61 72 65 6e 74 14 1a 42 69 64 41 31 4a 70 61 72 65 "
          + "6e 74 50 72 6f 70 46 70 61 72 65 6e 74 02 02");
private static final byte[] UNWRAPPED_UNKNOWN = VPackWireFixtureTest.hex(
            "14 1d 45 66 69 72 73 74 43 77 6f 77 45 74 68 69 72 64 "
          + "49 57 4f 57 2c 20 4e 50 45 21 02");
private static final byte[] UNWRAPPED_NO_UNKNOWN = VPackWireFixtureTest.hex(
            "14 0d 45 66 69 72 73 74 43 77 6f 77 01");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper UNWRAPPED_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: CreatorWithUnwrapped2369Test#testCreatorWithUnwrappedAndUnknownProperty.
    void testCreatorWithUnwrappedAndUnknownProperty() throws Exception {
        Example2369 result = UNWRAPPED_MAPPER.readValue(UNWRAPPED_UNKNOWN,
                Example2369.class);
        assertNotNull(result);
        assertEquals("wow", result.first);
        assertNull(result.second);
    }

    // Provenance: CreatorWithUnwrapped2369Test#testCreatorWithUnwrappedNoUnknownProperty.
    void testCreatorWithUnwrappedNoUnknownProperty() throws Exception {
        Example2369 result = UNWRAPPED_MAPPER.readValue(UNWRAPPED_NO_UNKNOWN,
                Example2369.class);
        assertNotNull(result);
        assertEquals("wow", result.first);
        assertNull(result.second);
    }
@SuppressWarnings("serial")
    static class MyParamIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember param) {
            if (param instanceof AnnotatedParameter ap) {
                return "paramName" + ap.getIndex();
            }
            return super.findImplicitPropertyName(config, param);
        }
    }
static class OneProperty {
        public String paramName0;

        @JsonCreator
        public OneProperty(String bogus) {
            paramName0 = "CTOR:" + bogus;
        }
    }
static class RenamedFactoryBean {
        protected String myName;
        protected int myAge;

        private RenamedFactoryBean(int a, String n, boolean foo) {
            myAge = a;
            myName = n;
        }

        @JsonCreator
        public static RenamedFactoryBean create(int age, String name) {
            return new RenamedFactoryBean(age, name, true);
        }
    }
@SuppressWarnings("serial")
    static class NamedParamIntrospector556 extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember param) {
            if (param instanceof AnnotatedParameter ap) {
                switch (ap.getIndex()) {
                case 0: return "myAge";
                case 1: return "myName";
                default: return "param" + ap.getIndex();
                }
            }
            return super.findImplicitPropertyName(config, param);
        }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static class A {
        String id;
        String name;

        @ConstructorProperties({"id", "name"})
        public A(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id", scope = Parent.class)
    static class Parent {
        @JsonProperty("id")
        String id;

        @JsonProperty
        String parentProp;

        @JsonCreator
        public Parent(@JsonProperty("parentProp") String parentProp) {
            this.parentProp = parentProp;
        }
    }
static class Child {
        @JsonProperty
        Parent parent;

        @JsonProperty
        String childProp;

        @JsonCreator
        public Child(@JsonProperty("parent") Parent parent,
                @JsonProperty("childProp") String childProp) {
            this.parent = parent;
            this.childProp = childProp;
        }
    }
static class Example2369 {
        public String first;

        @JsonUnwrapped
        public Second2369 second;
    }
static class Second2369 {
        public String field;

        @JsonCreator
        public static Second2369 factory(@JsonProperty("field") String field) {
            if (field != null) {
                Second2369 result = new Second2369();
                result.field = field;
                return result;
            }
            return null;
        }
    }

    void __invoke_testCreatorWithUnwrappedAndUnknownProperty() throws Exception {
        try {
            testCreatorWithUnwrappedAndUnknownProperty();
        } finally {
        }
    }


    void __invoke_testCreatorWithUnwrappedNoUnknownProperty() throws Exception {
        try {
            testCreatorWithUnwrappedNoUnknownProperty();
        } finally {
        }
    }

}
