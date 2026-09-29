package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0403F2 {
private static final byte[] SHARED_NAME_6 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 36 03");
private static final byte[] SHARED_NAME_1 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");
private static final byte[] VALUE_3 = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 33 01");
private static final byte[] VALUE_2 = VPackWireFixtureTest.hex(
            "14 0a 45 76 61 6c 75 65 32 01");
private static final byte[] STRING_ABC = VPackWireFixtureTest.hex("43 61 62 63");
private static final byte[] STRING_XYZ = VPackWireFixtureTest.hex("43 78 79 7a");
private static final byte[] INFERRED_X_2 = VPackWireFixtureTest.hex(
            "14 06 41 78 32 01");
private static final byte[] TYPE_CLASS_A = VPackWireFixtureTest.hex(
            "14 10 44 74 79 70 65 47 43 4c 41 53 53 5f 41 01");
private static final byte[] TYPE_CLASS_A_CANONICAL = VPackWireFixtureTest.hex(
            "0b 11 01 44 74 79 70 65 47 43 4c 41 53 53 5f 41 03");
private static final byte[] WRAPPER_LONG = VPackWireFixtureTest.hex(
            "0b 20 01 45 76 61 6c 75 65 06 16 02 4e 6a 61 76 61 2e 6c 61 6e 67 2e 4c 6f 6e 67 "
          + "28 0d 03 12 03");
private static final byte[] WRAPPER_LONG_CANONICAL = VPackWireFixtureTest.hex(
            "0b 20 01 45 76 61 6c 75 65 06 16 02 4e 6a 61 76 61 2e 6c 61 6e 67 2e 4c 6f 6e 67 "
          + "28 0d 03 12 03");
private static final byte[] SER_BOTH = VPackWireFixtureTest.hex(
            "0b 13 02 45 66 69 65 6c 64 32 45 76 61 6c 75 65 33 03 0a");
private static final byte[] SER_FIELD_ONLY = VPackWireFixtureTest.hex(
            "0b 0b 01 45 66 69 65 6c 64 32 03");

    // Provenance: TestInferredMutators#testDeserializationInference().
    void testDeserializationInferenceVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        assertTrue(mapper.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS));
        assertEquals(2, mapper.readValue(INFERRED_X_2, Point.class).x);

        ObjectMapper noInference = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.INFER_PROPERTY_MUTATORS)
                .build();
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> noInference.readValue(INFERRED_X_2, Point.class));
        assertTrue(exception.getMessage().contains("Unrecognized property \"x\""));
    }

    // Provenance: TestInferredMutators#testFinalFieldIgnoral().
    void testFinalFieldIgnoralVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
                .build();
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> mapper.readValue(INFERRED_X_2, FixedPoint.class));
        assertTrue(exception.getMessage().contains("Unrecognized property \"x\""));
    }
private static ObjectMapper typedMapper() {
        return VPackMapper.builder()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();
    }
static class Wrapper {
        protected Object value;

        public Wrapper() { }
        public Wrapper(Object value) { this.value = value; }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object getValue() { return value; }

        public void setValue(Object value) { this.value = value; }
    }
static class SharedName {
        @JsonProperty("x")
        protected int value;

        SharedName(int value) { this.value = value; }
        public int getValue() { return value; }
    }
static class SharedName2 {
        @JsonProperty("x")
        public int getValue() { return 1; }
        public void setValue(int value) { }
    }
static class TypeWrapper {
        protected Object value;

        @JsonCreator
        public TypeWrapper(@JsonProperty("value")
                @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) Object value) {
            this.value = value;
        }

        public Object getValue() { return value; }
    }
static class ProtectedBean {
        String a;
        protected ProtectedBean(String value) { this.a = value; }
    }
static class PrivateBeanAnnotated {
        String a;
        @JsonCreator
        private PrivateBeanAnnotated(String value) { this.a = value; }
    }
static class PrivateBeanNonAnnotated {
        String a;
        private PrivateBeanNonAnnotated(String value) { this.a = value; }
    }
@JsonPropertyOrder(alphabetic = true)
    static class Feature1347SerBean {
        public int field = 2;
        public int getValue() { return 3; }
    }
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NON_PRIVATE)
    static class Feature1347DeserBean {
        int value;
        public void setValue(int value) {
            throw new IllegalArgumentException("Should NOT get called");
        }
    }
@JsonAutoDetect(
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            creatorVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE,
            fieldVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type", visible = true)
    @JsonSubTypes({ @JsonSubTypes.Type(name = "CLASS_A", value = DataClassA.class) })
    private static abstract class DataParent2789 {
        @JsonProperty("type")
        @JsonTypeId
        private final DataType2789 type;

        DataParent2789() { this.type = null; }
        DataParent2789(DataType2789 type) { this.type = type; }
        public DataType2789 getType() { return type; }
    }
private static final class DataClassA extends DataParent2789 {
        DataClassA() { super(DataType2789.CLASS_A); }
    }
private enum DataType2789 { CLASS_A }
public static class Point {
        protected int x;
        public int getX() { return x; }
    }
public static class FixedPoint {
        protected final int x;
        public FixedPoint() { x = 0; }
        public int getX() { return x; }
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testDeserializationInferenceVpack() throws Exception {
        try {
            testDeserializationInferenceVpack();
        } finally {
        }
    }


    void __invoke_testFinalFieldIgnoralVpack() throws Exception {
        try {
            testFinalFieldIgnoralVpack();
        } finally {
        }
    }

}
