package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0403F1 {
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

    // Provenance: TestAutoDetect#testAnnotatedFieldIssue2789().
    void testAnnotatedFieldIssue2789Vpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(TYPE_CLASS_A_CANONICAL,
                mapper.writeValueAsBytes(new DataClassA()));
        DataParent2789 copy = mapper.readValue(TYPE_CLASS_A, DataParent2789.class);
        assertEquals(DataType2789.CLASS_A, copy.getType());
    }

    // Provenance: TestAutoDetect#testPrivateDelegatingCtor().
    void testPrivateDelegatingCtorVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        PrivateBeanAnnotated bean = mapper.readValue(STRING_ABC, PrivateBeanAnnotated.class);
        assertEquals("abc", bean.a);

        JacksonException exception = assertThrows(JacksonException.class,
                () -> mapper.readValue(STRING_ABC, PrivateBeanNonAnnotated.class));
        assertTrue(exception.getMessage().contains("no String-argument constructor/factory"));

        ObjectMapper anyVisibility = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.withScalarConstructorVisibility(
                        JsonAutoDetect.Visibility.ANY))
                .build();
        bean = anyVisibility.readValue(STRING_XYZ, PrivateBeanAnnotated.class);
        assertEquals("xyz", bean.a);
    }

    // Provenance: TestAutoDetect#testProtectedDelegatingCtor().
    void testProtectedDelegatingCtorVpack() throws Exception {
        ProtectedBean bean = new VPackMapper().readValue(STRING_ABC, ProtectedBean.class);
        assertEquals("abc", bean.a);

        ObjectMapper publicOnly = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.withScalarConstructorVisibility(
                        JsonAutoDetect.Visibility.PUBLIC_ONLY))
                .build();
        JacksonException exception = assertThrows(JacksonException.class,
                () -> publicOnly.readValue(STRING_ABC, ProtectedBean.class));
        assertInstanceOf(InvalidDefinitionException.class, exception);
        assertTrue(exception.getMessage().contains("no String-argument constructor/factory"));
    }

    // Provenance: TestAutoDetect#testVisibilityConfigOverridesForDeser().
    void testVisibilityConfigOverridesForDeserVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JacksonException exception = assertThrows(JacksonException.class,
                () -> mapper.readValue(VALUE_3, Feature1347DeserBean.class));
        assertTrue(exception.getMessage().contains("Should NOT get called"));

        ObjectMapper setterHidden = VPackMapper.builder()
                .withConfigOverride(Feature1347DeserBean.class,
                        o -> o.setVisibility(JsonAutoDetect.Value.construct(
                                com.fasterxml.jackson.annotation.PropertyAccessor.SETTER,
                                JsonAutoDetect.Visibility.NONE)))
                .build();
        Feature1347DeserBean result = setterHidden.readValue(VALUE_3,
                Feature1347DeserBean.class);
        assertEquals(3, result.value);
    }

    // Provenance: TestAutoDetect#testVisibilityConfigOverridesForSer().
    void testVisibilityConfigOverridesForSerVpack() throws Exception {
        Feature1347SerBean input = new Feature1347SerBean();
        ObjectMapper mapper = new VPackMapper();
        assertArrayEquals(SER_BOTH, mapper.writeValueAsBytes(input));

        ObjectMapper getterHidden = VPackMapper.builder()
                .withConfigOverride(Feature1347SerBean.class,
                        o -> o.setVisibility(JsonAutoDetect.Value.construct(
                                com.fasterxml.jackson.annotation.PropertyAccessor.GETTER,
                                JsonAutoDetect.Visibility.NONE)))
                .build();
        assertArrayEquals(SER_FIELD_ONLY, getterHidden.writeValueAsBytes(input));
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

    void __invoke_testAnnotatedFieldIssue2789Vpack() throws Exception {
        try {
            testAnnotatedFieldIssue2789Vpack();
        } finally {
        }
    }


    void __invoke_testPrivateDelegatingCtorVpack() throws Exception {
        try {
            testPrivateDelegatingCtorVpack();
        } finally {
        }
    }


    void __invoke_testProtectedDelegatingCtorVpack() throws Exception {
        try {
            testProtectedDelegatingCtorVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityConfigOverridesForDeserVpack() throws Exception {
        try {
            testVisibilityConfigOverridesForDeserVpack();
        } finally {
        }
    }


    void __invoke_testVisibilityConfigOverridesForSerVpack() throws Exception {
        try {
            testVisibilityConfigOverridesForSerVpack();
        } finally {
        }
    }

}
