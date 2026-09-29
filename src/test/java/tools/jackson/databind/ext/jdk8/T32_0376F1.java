package tools.jackson.databind.ext.jdk8;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.ser.BeanSerializerFactory;
import tools.jackson.databind.ser.SerializationContextExt;
import tools.jackson.databind.ser.SerializerCache;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0376F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] NULL = VPackWireFixtureTest.hex("18");
private static final byte[] STRING_TEST = VPackWireFixtureTest.hex(
            "44 74 65 73 74");
private static final byte[] STRING_BEAN_XYZ = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 78 79 7a 03");
private static final byte[] STRING_BEAN_FOO = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 66 6f 6f 03");
private static final byte[] STRING_BEAN_FOOBAR = VPackWireFixtureTest.hex(
            "0b 11 01 45 76 61 6c 75 65 46 46 6f 6f 62 61 52 03");
private static final byte[] CUSTOM_STRING_FOO = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 46 4f 4f 03");
private static final byte[] STRING_BEAN_NULL = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] ATOMIC_REFERENCE_FOO = VPackWireFixtureTest.hex(
            "43 66 6f 6f");
private static final byte[] UNWRAPPED_PERSON = VPackWireFixtureTest.hex(
            "0b 24 02 47 61 64 64 72 65 73 73 4b 53 70 72 69 6e 67 66 69 65 6c 64 "
          + "44 6e 61 6d 65 45 48 6f 6d 65 72 03 17");
private static final byte[] UNWRAPPED_PERSON_NO_CHILD_PROPS =
            VPackWireFixtureTest.hex(
                    "0b 0f 01 44 6e 61 6d 65 45 48 6f 6d 65 72 03");
private static final byte[] UNWRAPPED_ID_ONLY = VPackWireFixtureTest.hex(
            "0b 0b 01 42 69 64 43 66 6f 6f 03");

    // Provenance: OptionalUnwrappedTest#testShouldSerializeUnwrappedOptional().
    void testShouldSerializeUnwrappedOptionalVpack() throws Exception {
        assertArrayEquals(UNWRAPPED_ID_ONLY,
                MAPPER.writeValueAsBytes(new Bean("foo", Optional.empty())));
    }

    // Provenance: OptionalUnwrappedTest#testDeserializeUnwrappedOptional().
    void testDeserializeUnwrappedOptionalVpack() throws Exception {
        Person2736 expected = new Person2736();
        expected.mainData = new MainData2736();
        expected.mainData.name = "Homer";
        expected.additionalData = Optional.of(new AdditionalData2736());
        expected.additionalData.get().address = "Springfield";

        assertArrayEquals(UNWRAPPED_PERSON,
                MAPPER.writeValueAsBytes(expected));
        Person2736 actual = MAPPER.readValue(UNWRAPPED_PERSON, Person2736.class);
        assertNotNull(actual.mainData);
        assertEquals("Homer", actual.mainData.name);
        assertNotNull(actual.additionalData);
        assertTrue(actual.additionalData.isPresent());
        assertEquals("Springfield", actual.additionalData.get().address);
    }

    // Provenance: OptionalUnwrappedTest#testDeserializeUnwrappedOptionalNoChildProps().
    void testDeserializeUnwrappedOptionalNoChildPropsVpack() throws Exception {
        Person2736 actual = MAPPER.readValue(UNWRAPPED_PERSON_NO_CHILD_PROPS,
                Person2736.class);
        assertNotNull(actual.mainData);
        assertEquals("Homer", actual.mainData.name);
        assertNotNull(actual.additionalData);
        assertTrue(actual.additionalData.isPresent());
        assertNull(actual.additionalData.get().address);
    }

    // Provenance: OptionalUnwrappedTest#testPropogatePrefixToSchema().
    void testPropogatePrefixToSchemaVpack() throws Exception {
        AtomicReference<String> propertyName = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(OptionalParent.class,
                new JsonFormatVisitorWrapper.Base(new SerializationContextExt.Impl(
                        new VPackFactory(), MAPPER.serializationConfig(), null,
                        BeanSerializerFactory.instance, new SerializerCache())) {
                    @Override
                    public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
                        return new JsonObjectFormatVisitor.Base(getContext()) {
                            @Override
                            public void optionalProperty(BeanProperty prop) {
                                propertyName.set(prop.getName());
                            }
                        };
                    }
                });
        assertEquals("XX.name", propertyName.get());
    }
private static ObjectMapper inclusionMapper(JsonInclude.Include inclusion) {
        return VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(inclusion))
                .build();
    }
static class OptionalStringBean {
        public Optional<String> value;

        public OptionalStringBean() { }

        OptionalStringBean(String value) {
            this.value = Optional.ofNullable(value);
        }
    }
static class CaseChangingStringWrapper {
        @JsonSerialize(contentUsing = UpperCasingSerializer.class)
        @JsonDeserialize(contentUsing = LowerCasingDeserializer.class)
        public Optional<String> value;

        CaseChangingStringWrapper() { }

        CaseChangingStringWrapper(String value) {
            this.value = Optional.ofNullable(value);
        }
    }
public static class UpperCasingSerializer extends StdScalarSerializer<String> {
        public UpperCasingSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                tools.jackson.databind.SerializationContext provider) {
            gen.writeString(value.toUpperCase());
        }
    }
public static class LowerCasingDeserializer extends StdScalarDeserializer<String> {
        public LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            return parser.getString().toLowerCase();
        }
    }
static class Bean {
        public String id;

        @JsonUnwrapped(prefix = "child")
        public Optional<Bean2> bean2;

        Bean(String id, Optional<Bean2> bean2) {
            this.id = id;
            this.bean2 = bean2;
        }
    }
static class Bean2 {
        public String name;
    }
static class OptionalParent {
        @JsonUnwrapped(prefix = "XX.")
        public Optional<Child> child = Optional.of(new Child());
    }
static class Child {
        public String name = "Bob";
    }
static class Person2736 {
        @JsonUnwrapped
        public MainData2736 mainData;

        @JsonUnwrapped
        public Optional<AdditionalData2736> additionalData;
    }
static class MainData2736 {
        public String name;
    }
static class AdditionalData2736 {
        public String address;
    }

    void __invoke_testShouldSerializeUnwrappedOptionalVpack() throws Exception {
        try {
            testShouldSerializeUnwrappedOptionalVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUnwrappedOptionalVpack() throws Exception {
        try {
            testDeserializeUnwrappedOptionalVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUnwrappedOptionalNoChildPropsVpack() throws Exception {
        try {
            testDeserializeUnwrappedOptionalNoChildPropsVpack();
        } finally {
        }
    }


    void __invoke_testPropogatePrefixToSchemaVpack() throws Exception {
        try {
            testPropogatePrefixToSchemaVpack();
        } finally {
        }
    }

}
