package tools.jackson.databind.deser.creators;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.AnnotatedParameter;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0207F2 {
private static final byte[] MULTI_ARG_ENUM = VPackWireFixtureTest.hex(
            "14 0e 42 69 64 33 44 6e 61 6d 65 41 42 02");
private static final byte[] MULTI_ARG_ENUMS = VPackWireFixtureTest.hex(
            "13 2d "
          + "14 0e 42 69 64 33 44 6e 61 6d 65 41 42 02 "
          + "14 0e 42 69 64 33 44 6e 61 6d 65 41 41 02 "
          + "14 0e 42 69 64 33 44 6e 61 6d 65 41 42 02 03");
private static final byte[] NO_ARG_ENUM = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 62 6f 67 75 73 01");
private static final byte[] ENUM3280_B_ONLY = VPackWireFixtureTest.hex(
            "14 07 41 62 41 78 01");
private static final byte[] ENUM3280_A_STRING = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 31 41 62 41 78 02");
private static final byte[] ENUM3280_A_OBJECT = VPackWireFixtureTest.hex(
            "14 0a 41 62 41 79 41 61 0a 02");
private static final byte[] ENUM3280_A_ARRAY = VPackWireFixtureTest.hex(
            "14 0a 41 61 01 41 62 41 78 02");
private static final byte[] FACTORY_2894 = VPackWireFixtureTest.hex(
            "14 18 45 76 61 6c 75 65 13 0f "
          + "14 06 41 78 31 01 14 06 41 78 32 01 02 01");
private static final byte[] FACTORY_2895 = VPackWireFixtureTest.hex(
            "14 1c 46 6f 62 6a 65 63 74 "
          + "14 12 42 69 64 31 44 6e 61 6d 65 45 6e 61 6d 65 31 02 01");
private static final byte[] IMPLICIT_PARAMS = VPackWireFixtureTest.hex(
            "14 1b 4a 70 61 72 61 6d 4e 61 6d 65 30 31 "
          + "4a 70 61 72 61 6d 4e 61 6d 65 31 32 02");
private static final byte[] IMPLICIT_SNAKE_PARAMS = VPackWireFixtureTest.hex(
            "14 1d 4b 70 61 72 61 6d 5f 6e 61 6d 65 30 31 "
          + "4b 70 61 72 61 6d 5f 6e 61 6d 65 31 32 02");
private static final byte[] VALUE_123 = VPackWireFixtureTest.hex("28 7b");
private static final byte[] VALUE_345 = VPackWireFixtureTest.hex("29 59 01");
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .annotationIntrospector(new MyParamIntrospector())
            .build();

    // Provenance: ImplicitParamsForCreatorTest#delegatingInferFromJsonValue.
    void delegatingInferFromJsonValue() throws Exception {
        assertArrayEquals(VALUE_123, MAPPER.writeValueAsBytes(new XY3654(123)));
        XY3654 result = MAPPER.readValue(VALUE_345, XY3654.class);
        assertEquals(345, result.paramName0);
    }

    // Provenance: ImplicitParamsForCreatorTest#implicitNameWithNamingStrategy.
    void implicitNameWithNamingStrategy() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .annotationIntrospector(new MyParamIntrospector())
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        XY value = mapper.readValue(IMPLICIT_SNAKE_PARAMS, XY.class);
        assertNotNull(value);
        assertEquals(1, value.x);
        assertEquals(2, value.y);
    }

    // Provenance: ImplicitParamsForCreatorTest#jsonCreatorWithOtherAnnotations.
    void jsonCreatorWithOtherAnnotations() throws Exception {
        Bean2932 bean = MAPPER.readValue(IMPLICIT_PARAMS, Bean2932.class);
        assertNotNull(bean);
        assertEquals(1, bean._a);
        assertEquals(2, bean._b);
    }

    // Provenance: ImplicitParamsForCreatorTest#nonSingleArgCreator.
    void nonSingleArgCreator() throws Exception {
        XY value = MAPPER.readValue(IMPLICIT_PARAMS, XY.class);
        assertNotNull(value);
        assertEquals(1, value.x);
        assertEquals(2, value.y);
    }
private static final byte[] ENUM3280_A_OBJECT_WITH_X = VPackWireFixtureTest.hex(
            "14 0a 41 61 0a 41 62 41 78 02");
enum Enum929 {
        A, B, C;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        static Enum929 forValues(@JsonProperty("id") int intProp,
                @JsonProperty("name") String name) {
            return valueOf(name);
        }
    }
enum MyEnum960 {
        VALUE, BOGUS;

        @JsonCreator
        static MyEnum960 getInstance() { return VALUE; }
    }
enum Enum3280 {
        x("x"), y("y"), z("z");

        private final String value;

        Enum3280(String value) { this.value = value; }

        @JsonCreator
        static Enum3280 getByValue(@JsonProperty("b") String value) {
            for (Enum3280 candidate : values()) {
                if (candidate.value.equals(value)) return candidate;
            }
            return null;
        }
    }
static class Wrapper<T> {
        List<T> values;

        protected Wrapper(List<T> v) { values = v; }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        static <T> Wrapper<T> fromValues(@JsonProperty("value") List<T> values) {
            return new Wrapper<>(values);
        }
    }
static class Value {
        public int x;

        protected Value() { }
        protected Value(int x0) { x = x0; }

        @Override
        public boolean equals(Object other) {
            return other instanceof Value value && value.x == x;
        }
    }
static class SimpleWrapper2895<T> {
        final T value;

        SimpleWrapper2895(T value) { this.value = value; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        static <T> SimpleWrapper2895<T> fromJson(JsonSimpleWrapper2895<T> value) {
            return new SimpleWrapper2895<>(value.object);
        }
    }
static final class JsonSimpleWrapper2895<T> {
        @JsonProperty("object")
        public T object;
    }
static class Account2895 {
        private long id;
        private String name;

        @JsonCreator
        Account2895(@JsonProperty("name") String name, @JsonProperty("id") long id) {
            this.id = id;
            this.name = name;
        }

        public String getName() { return name; }
        public long getId() { return id; }
    }
static class XY {
        protected int x, y;

        public XY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
static class XY3654 {
        public int paramName0;

        @JsonCreator
        XY3654(int paramName0) { this.paramName0 = paramName0; }

        @JsonValue
        public int serializedAs() { return paramName0; }
    }
static class Bean2932 {
        int _a, _b;

        public Bean2932(@JsonDeserialize int a, int b) {
            _a = a;
            _b = b;
        }
    }
static class MyParamIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            if (member instanceof AnnotatedParameter parameter) {
                return "paramName" + parameter.getIndex();
            }
            return super.findImplicitPropertyName(config, member);
        }
    }

    void __invoke_delegatingInferFromJsonValue() throws Exception {
        try {
            delegatingInferFromJsonValue();
        } finally {
        }
    }


    void __invoke_implicitNameWithNamingStrategy() throws Exception {
        try {
            implicitNameWithNamingStrategy();
        } finally {
        }
    }


    void __invoke_jsonCreatorWithOtherAnnotations() throws Exception {
        try {
            jsonCreatorWithOtherAnnotations();
        } finally {
        }
    }


    void __invoke_nonSingleArgCreator() throws Exception {
        try {
            nonSingleArgCreator();
        } finally {
        }
    }

}
