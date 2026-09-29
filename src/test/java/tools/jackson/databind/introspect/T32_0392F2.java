package tools.jackson.databind.introspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.AnnotatedClassResolver;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0392F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] DELEGATING_INPUT = VPackWireFixtureTest.hex("28 11");
private static Map<?, ?> asMap(ObjectMapper mapper, Object value) throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), Map.class);
    }
private static final String CLASS_DESC = "Description, yay!";

    // Provenance: BeanDescriptionTest#testClassDesc().
    void testClassDescVpack() {
        DeserializationConfig config = MAPPER.deserializationConfig();
        JavaType type = MAPPER.constructType(DocumentedBean.class);
        AnnotatedClass classInfo = AnnotatedClassResolver.resolve(config, type, config);
        assertEquals(CLASS_DESC, config.getAnnotationIntrospector()
                .findClassDescription(config, classInfo));
    }
static class SomeBean {
        private String value;

        public SomeBean(String value) {
            this.value = value;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
@Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonIgnore
    private @interface MyIgnoral { }
@Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonProperty("foobar")
    private @interface MyRename { }
protected final static class Bean {
        @MyIgnoral
        public String getIgnored() { return "foo"; }

        @MyRename
        public int renamed = 13;
    }
@Retention(RetentionPolicy.RUNTIME)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE)
    @JacksonAnnotationsInside
    public @interface JsonAutoDetectOff { }
@JsonAutoDetectOff
    public class NoAutoDetect {
        public int getA() { return 13; }

        @JsonProperty
        public int getB() { return 5; }
    }
@Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonProperty("_id")
    public @interface Bundle92 { }
public class Bean92 {
        @Bundle92
        protected String id = "abc";
    }
@HolderB
    @JacksonAnnotationsInside
    @Retention(RetentionPolicy.RUNTIME)
    static @interface HolderA { }
@HolderA
    @JacksonAnnotationsInside
    @Retention(RetentionPolicy.RUNTIME)
    static @interface HolderB { }
static class RecursiveHolder {
        @HolderA public int unimportant = 42;
    }
static class RecursiveHolder2 {
        @HolderA public int getValue() { return 28; }
    }
static class RecursiveHolder3 {
        public int x;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @HolderA
        public RecursiveHolder3(int x) { this.x = x; }
    }
@JsonProperty
    @JacksonAnnotationsInside
    @Retention(RetentionPolicy.RUNTIME)
    static @interface InformativeHolder {
        boolean important() default true;
    }
static class InformingHolder {
        @InformativeHolder public int unimportant = 42;
    }
@SuppressWarnings("serial")
    static class BundleAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public PropertyName findNameForSerialization(MapperConfig<?> config, tools.jackson.databind.introspect.Annotated a) {
            InformativeHolder informativeHolder = a.getAnnotation(InformativeHolder.class);
            if ((informativeHolder != null) && informativeHolder.important()) {
                return PropertyName.construct("important");
            }
            return super.findNameForSerialization(config, a);
        }
    }
@JsonClassDescription(CLASS_DESC)
    static class DocumentedBean {
        public int x;
    }

    void __invoke_testClassDescVpack() throws Exception {
        try {
            testClassDescVpack();
        } finally {
        }
    }

}
