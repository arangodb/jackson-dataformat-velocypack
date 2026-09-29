package tools.jackson.databind.deser.creators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.ConstructorDetector;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.introspect.JacksonAnnotationIntrospector;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0197F1 {
private static final byte[] ABSTRACT_TYPED_DATA = VPackWireFixtureTest.hex(
            "14 20 45 76 61 6c 75 65 4a 31 32 33 34 35 36 37 38 39 30 "
          + "44 74 79 70 65 46 73 74 72 69 6e 67 02");
private static final byte[] NULL_FIELD = VPackWireFixtureTest.hex(
            "14 0a 45 66 69 65 6c 64 18 01");
private static final byte[] VALUE_137 = VPackWireFixtureTest.hex(
            "14 0c 45 76 61 6c 75 65 29 89 00 01");
private static final byte[] VALUE_NEGATIVE_99 = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 20 9d 01");
private static final byte[] VALUE_LONG_MAX = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 27 ff ff ff ff ff ff ff 7f 01");
private static final byte[] VALUE_FLOAT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 1b 48 e1 7a 14 ae 1f 61 40 01");
private static final byte[] VALUE_DOUBLE = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 1b 00 c8 4e 67 6d c1 ab 43 01");
private static final byte[] BAR_404 = VPackWireFixtureTest.hex(
            "14 0a 43 62 61 72 29 94 01 01");
private static final ObjectMapper PROPERTIES_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .build();
private static final ObjectMapper DELEGATING_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.USE_DELEGATING)
            .build();
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.DEFAULT)
            .build();
private static final ObjectMapper NULL_FAIL_MAPPER = VPackMapper.builder()
            .annotationIntrospector(new ImplicitNameIntrospector())
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .changeDefaultNullHandling(value -> value.withValueNulls(Nulls.FAIL))
            .build();

    // Provenance: ConstructorDetectorTest#nullHandlingCreator3241.
    void nullHandlingCreator3241() {
        assertThrows(InvalidNullException.class,
                () -> NULL_FAIL_MAPPER.readValue(NULL_FIELD, Input3241.class));
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToDelegatingNoAnnotation.
    void test1ArgDefaultsToDelegatingNoAnnotation() throws Exception {
        SingleArgNotAnnotated value = DELEGATING_MAPPER.readValue(
                VPackWireFixtureTest.hex("29 b4 07"), SingleArgNotAnnotated.class);
        assertEquals(1972, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToDelegatingNoMode.
    void test1ArgDefaultsToDelegatingNoMode() throws Exception {
        SingleArgNoMode value = DELEGATING_MAPPER.readValue(
                VPackWireFixtureTest.hex("29 fc 0a"), SingleArgNoMode.class);
        assertEquals(2812, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToHeuristics.
    void test1ArgDefaultsToHeuristics() throws Exception {
        byte[] scalar = VPackWireFixtureTest.hex("29 3d 33");
        SingleArgNotAnnotated first = DEFAULT_MAPPER.readValue(scalar,
                SingleArgNotAnnotated.class);
        SingleArgNoMode second = DEFAULT_MAPPER.readValue(scalar,
                SingleArgNoMode.class);
        assertEquals(13117, first.v);
        assertEquals(13117, second.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesByte.
    void test1ArgDefaultsToPropertiesByte() throws Exception {
        SingleArgByte value = PROPERTIES_MAPPER.readValue(VALUE_NEGATIVE_99,
                SingleArgByte.class);
        assertEquals(-99, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesDouble.
    void test1ArgDefaultsToPropertiesDouble() throws Exception {
        SingleArgDouble value = PROPERTIES_MAPPER.readValue(VALUE_DOUBLE,
                SingleArgDouble.class);
        assertEquals(999999999999999999.99, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesFloat.
    void test1ArgDefaultsToPropertiesFloat() throws Exception {
        SingleArgFloat value = PROPERTIES_MAPPER.readValue(VALUE_FLOAT,
                SingleArgFloat.class);
        assertEquals(136.99f, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesIssue1498.
    void test1ArgDefaultsToPropertiesIssue1498() throws Exception {
        SingleArg1498 value = PROPERTIES_MAPPER.readValue(BAR_404,
                SingleArg1498.class);
        assertEquals(404, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesLong.
    void test1ArgDefaultsToPropertiesLong() throws Exception {
        SingleArgLong value = PROPERTIES_MAPPER.readValue(VALUE_LONG_MAX,
                SingleArgLong.class);
        assertEquals(Long.MAX_VALUE, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesNoMode.
    void test1ArgDefaultsToPropertiesNoMode() throws Exception {
        SingleArgNoMode value = PROPERTIES_MAPPER.readValue(VALUE_137,
                SingleArgNoMode.class);
        assertEquals(137, value.v);
    }

    // Provenance: ConstructorDetectorTest#test1ArgDefaultsToPropertiesNonAnnotated.
    void test1ArgDefaultsToPropertiesNonAnnotated() throws Exception {
        SingleArgNotAnnotated value = PROPERTIES_MAPPER.readValue(VALUE_137,
                SingleArgNotAnnotated.class);
        assertEquals(137, value.v);
    }
@Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ImplicitName {
        String value();
    }
private static final class ImplicitNameIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public String findImplicitPropertyName(MapperConfig<?> config, AnnotatedMember member) {
            ImplicitName annotation = member.getAnnotation(ImplicitName.class);
            return annotation == null ? null : annotation.value();
        }
    }
interface TypedData {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
        @JsonTypeIdResolver(ValueTypeIdResolver.class)
        Value getValue();

        String getType();

        @JsonCreator
        static TypedData immutableOf(@JsonProperty("value") Value value,
                @JsonProperty("type") String type) {
            return new Immutable(value, type);
        }

        final class Immutable implements TypedData {
            private final Value value;
            private final String type;

            public Immutable(Value value, String type) {
                this.value = value;
                this.type = type;
            }

            @Override
            public Value getValue() { return value; }

            @Override
            public String getType() { return type; }
        }

        @SuppressWarnings("serial")
        final class ValueTypeIdResolver extends TypeIdResolverBase {
            @Override
            public String idFromValue(DatabindContext ctxt, Object value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String idFromValueAndType(DatabindContext ctxt, Object value,
                    Class<?> suggestedType) {
                throw new UnsupportedOperationException();
            }

            @Override
            public JsonTypeInfo.Id getMechanism() {
                return JsonTypeInfo.Id.CUSTOM;
            }

            @Override
            public JavaType typeFromId(DatabindContext context, String id) {
                if (!"string".equals(id)) {
                    throw new IllegalArgumentException("Unexpected type id: " + id);
                }
                return context.constructType(StringValue.class);
            }
        }
    }
interface Value { }
static final class StringValue implements Value {
        private final String value;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public StringValue(String value) { this.value = value; }

        @JsonValue
        public String getValue() { return value; }
    }
static class SingleArgNotAnnotated {
        protected int v;

        SingleArgNotAnnotated() { v = -1; }

        public SingleArgNotAnnotated(@ImplicitName("value") int value) { v = value; }
    }
static class SingleArgByte {
        protected byte v;

        SingleArgByte() { v = -1; }

        public SingleArgByte(@ImplicitName("value") byte value) { v = value; }
    }
static class SingleArgLong {
        protected long v;

        SingleArgLong() { v = -1; }

        public SingleArgLong(@ImplicitName("value") long value) { v = value; }
    }
static class SingleArgFloat {
        protected float v;

        SingleArgFloat() { v = -1.0f; }

        public SingleArgFloat(@ImplicitName("value") float value) { v = value; }
    }
static class SingleArgDouble {
        protected double v;

        SingleArgDouble() { v = -1.0; }

        public SingleArgDouble(@ImplicitName("value") double value) { v = value; }
    }
static class SingleArgNoMode {
        protected int v;

        SingleArgNoMode() { v = -1; }

        @JsonCreator
        public SingleArgNoMode(@ImplicitName("value") int value) { v = value; }
    }
static class SingleArg1498 {
        final int v;

        SingleArg1498(@ImplicitName("bar") int value) { v = value; }
    }
static class Input3241 {
        private final Boolean field;

        public Input3241(@ImplicitName("field") Boolean field) {
            if (field == null) {
                throw new NullPointerException("Field should not remain null!");
            }
            this.field = field;
        }

        public Boolean field() { return field; }
    }

    void __invoke_nullHandlingCreator3241() throws Exception {
        try {
            nullHandlingCreator3241();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToDelegatingNoAnnotation() throws Exception {
        try {
            test1ArgDefaultsToDelegatingNoAnnotation();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToDelegatingNoMode() throws Exception {
        try {
            test1ArgDefaultsToDelegatingNoMode();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToHeuristics() throws Exception {
        try {
            test1ArgDefaultsToHeuristics();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesByte() throws Exception {
        try {
            test1ArgDefaultsToPropertiesByte();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesDouble() throws Exception {
        try {
            test1ArgDefaultsToPropertiesDouble();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesFloat() throws Exception {
        try {
            test1ArgDefaultsToPropertiesFloat();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesIssue1498() throws Exception {
        try {
            test1ArgDefaultsToPropertiesIssue1498();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesLong() throws Exception {
        try {
            test1ArgDefaultsToPropertiesLong();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesNoMode() throws Exception {
        try {
            test1ArgDefaultsToPropertiesNoMode();
        } finally {
        }
    }


    void __invoke_test1ArgDefaultsToPropertiesNonAnnotated() throws Exception {
        try {
            test1ArgDefaultsToPropertiesNonAnnotated();
        } finally {
        }
    }

}
