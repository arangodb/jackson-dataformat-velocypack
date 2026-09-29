package tools.jackson.databind.deser.creators;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonParser;
import tools.jackson.core.Version;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0201F2 {
private static final byte[] ALL_NULL = VPackWireFixtureTest.hex(
            "14 0f 42 65 6e 18 42 64 65 18 42 66 72 18 03");
private static final byte[] ONE_NON_NULL = VPackWireFixtureTest.hex(
            "14 14 42 65 6e 45 48 65 6c 6c 6f 42 64 65 18 42 66 72 18 03");
private static final byte[] UNKNOWN_PROPERTIES = VPackWireFixtureTest.hex(
            "14 27 47 75 6e 6b 6e 6f 77 6e 18 42 65 6e 18 42 64 65 18 "
          + "42 66 72 18 48 75 6e 6b 6e 6f 77 6e 32 45 68 65 6c 6c 6f 05");
private static final byte[] EMPTY_DELEGATING_VALUE = VPackWireFixtureTest.hex(
            "14 0d 48 6e 6f 6e 45 6d 70 74 79 40 01");
private static final byte[] NULL_ENTITY = VPackWireFixtureTest.hex(
            "14 36 44 74 79 70 65 45 20 20 20 20 20 42 69 64 64 "
          + "30 30 30 63 30 66 66 62 2d 61 30 64 36 2d 34 64 32 65 "
          + "2d 61 33 37 39 2d 34 61 65 61 61 66 32 38 33 35 39 39 02");
private static final byte[] PREFIXED_VALUES = VPackWireFixtureTest.hex(
            "14 1c 49 70 72 65 5f 76 61 6c 75 65 41 61 "
          + "4a 70 6f 73 74 5f 76 61 6c 75 65 41 62 02");
private static final byte[] PREFIXED_HELLO_WORLD = VPackWireFixtureTest.hex(
            "0b 26 02 4a 70 6f 73 74 5f 76 61 6c 75 65 45 77 6f 72 6c 64 "
          + "49 70 72 65 5f 76 61 6c 75 65 45 68 65 6c 6c 6f 03 14");
private static final byte[] ONLY_PRE = VPackWireFixtureTest.hex(
            "14 16 49 70 72 65 5f 76 61 6c 75 65 48 6f 6e 6c 79 5f 70 72 65 01");
private static final byte[] UPPER_CAMEL_CREATOR = VPackWireFixtureTest.hex(
            "0b 22 02 45 4d 79 41 67 65 28 2a 46 4d 79 4e 61 6d 65 "
          + "4d 4e 6f 74 4d 79 52 65 61 6c 4e 61 6d 65 03 0b");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final ObjectMapper UNKNOWN_PROPERTIES_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: CreatorWithNamingStrategyTest#testRenameViaCtor.
    void testRenameViaCtor() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(tools.jackson.databind.PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .annotationIntrospector(new NamedParamIntrospector556())
                .build();
        RenamingCtorBean bean = mapper.readValue(UPPER_CAMEL_CREATOR, RenamingCtorBean.class);
        assertEquals(42, bean.myAge);
        assertEquals("NotMyRealName", bean.myName);
    }
protected static final NullContained NULL_CONTAINED = new NullContained();
static class Localized3 {
        public final String en;
        public final String de;
        public final String fr;

        @JsonCreator
        public static Localized3 of(@JsonProperty("en") String en,
                @JsonProperty("de") String de, @JsonProperty("fr") String fr) {
            if (en == null && de == null && fr == null) {
                return null;
            }
            return new Localized3(en, de, fr);
        }

        private Localized3(String en, String de, String fr) {
            this.en = en;
            this.de = de;
            this.fr = fr;
        }
    }
static class Localized4 {
        public final String en;
        public final String de;
        public final String fr;

        @JsonCreator
        public static Localized4 of(@JsonProperty("en") String en,
                @JsonProperty("de") String de, @JsonProperty("fr") String fr) {
            if (en == null && de == null && fr == null) {
                return null;
            }
            throw new IllegalStateException("Should not be called");
        }

        private Localized4(String en, String de, String fr) {
            this.en = en;
            this.de = de;
            this.fr = fr;
        }
    }
static class Localized5 {
        public final String en;
        public final String de;
        public final String fr;
        public final Map<String, Object> props = new HashMap<>();

        @JsonCreator
        public static Localized5 of(@JsonProperty("en") String en,
                @JsonProperty("de") String de, @JsonProperty("fr") String fr) {
            if (en == null && de == null && fr == null) {
                return null;
            }
            throw new IllegalStateException("Should not be called");
        }

        private Localized5(String en, String de, String fr) {
            this.en = en;
            this.de = de;
            this.fr = fr;
        }

        @com.fasterxml.jackson.annotation.JsonAnySetter
        public void addProperty(String key, Object value) {
            props.put(key, value);
        }
    }
static class NonEmpty5401 {
        public NonEmptyString5401 nonEmpty;
    }
static class NonEmptyString5401 {
        public final String value;

        @JsonCreator
        public static NonEmptyString5401 of(String value) {
            if (value == null || value.isEmpty()) {
                return null;
            }
            return new NonEmptyString5401(value);
        }

        private NonEmptyString5401(String value) {
            this.value = value;
        }
    }
static class JsonEntity {
        protected final String type;
        protected final UUID id;

        private JsonEntity(String type, UUID id) {
            this.type = type;
            this.id = id;
        }

        @JsonCreator
        public static JsonEntity create(@JsonProperty("type") String type,
                @JsonProperty("id") UUID id) {
            if (type != null && !type.contains(" ") && id != null) {
                return new JsonEntity(type, id);
            }
            return null;
        }
    }
protected static class Container {
        Contained<String> contained;

        @JsonCreator
        public Container(@JsonProperty("contained") Contained<String> contained) {
            this.contained = contained;
        }
    }
protected interface Contained<T> { }
protected static class NullContained implements Contained<Object> { }
protected static class ContainedDeserializer
            extends tools.jackson.databind.ValueDeserializer<Contained<?>> {
        @Override
        public Contained<?> deserialize(JsonParser parser, DeserializationContext ctxt) {
            return null;
        }

        @Override
        public Contained<?> getNullValue(DeserializationContext ctxt) {
            return NULL_CONTAINED;
        }
    }
protected static class ContainerDeserializerResolver extends Deserializers.Base {
        @Override
        public tools.jackson.databind.ValueDeserializer<?> findBeanDeserializer(
                tools.jackson.databind.JavaType type, tools.jackson.databind.DeserializationConfig config,
                tools.jackson.databind.BeanDescription.Supplier beanDescRef) {
            if (!Contained.class.isAssignableFrom(type.getRawClass())) {
                return null;
            }
            return new ContainedDeserializer();
        }

        @Override
        public boolean hasDeserializerFor(tools.jackson.databind.DeserializationConfig config,
                Class<?> valueType) {
            return false;
        }
    }
protected static class TestModule extends tools.jackson.databind.JacksonModule {
        @Override
        public String getModuleName() {
            return "ContainedModule";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(SetupContext setupContext) {
            setupContext.addDeserializers(new ContainerDeserializerResolver());
        }
    }
static class Unwrapped {
        @JsonProperty
        public String value;
    }
static class FullExample {
        @JsonUnwrapped(prefix = "pre_")
        public final Unwrapped pre;
        @JsonUnwrapped(prefix = "post_")
        public final Unwrapped post;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public FullExample(@JsonUnwrapped(prefix = "pre_") Unwrapped pre,
                @JsonUnwrapped(prefix = "post_") Unwrapped post) {
            this.pre = pre;
            this.post = post;
        }
    }
static class RenamingCtorBean {
        protected String myName;
        protected int myAge;

        @JsonCreator
        public RenamingCtorBean(int myAge, String myName) {
            this.myName = myName;
            this.myAge = myAge;
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

    void __invoke_testRenameViaCtor() throws Exception {
        try {
            testRenameViaCtor();
        } finally {
        }
    }

}
