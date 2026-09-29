package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonTypeIdResolver;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.TypeIdResolverBase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0413F2 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.REQUIRE_TYPE_ID_FOR_SUBTYPES).build();
private static final byte[] MIXED_CASE_TYPE_ID = VPackWireFixtureTest.hex(
            "14 13 49 4f 70 65 72 61 74 69 6f 6e 45 4e 6f 54 65 51 01");
private static final byte[] CUSTOM_RESOLVER_WITHOUT_ID = VPackWireFixtureTest.hex(
            "14 2b 44 6e 61 6d 65 45 6b 61 6d 69 6c 47 76 65 68 69 63 6c 65 "
          + "14 15 46 77 68 65 65 6c 73 34 45 63 6f 6c 6f 72 43 72 65 64 02 02");
private static final byte[] ANIMAL_WITH_TYPE_ID = VPackWireFixtureTest.hex(
            "14 23 46 5f 63 6c 61 73 73 44 5f 63 61 74 44 6e 61 6d 65 "
          + "4e 43 61 74 2d 69 6e 2d 74 68 65 2d 68 61 74 02");
private static final byte[] ANIMAL_WITHOUT_TYPE_ID = VPackWireFixtureTest.hex(
            "14 0c 44 6e 61 6d 65 43 63 61 74 01");
private static final byte[] DEFAULT_TYPED_WRAPPER = VPackWireFixtureTest.hex(
            "0b 50 01 45 76 61 6c 75 65 06 46 02 75 74 6f 6f" +
                "6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61 74 61 62" +
                "69 6e 64 2e 6a 73 6f 6e 74 79 70 65 2e 54 33 32" +
                "5f 30 34 31 33 46 32 24 42 61 73 6b 65 74 42 61" +
                "6c 6c 0b 0b 01 44 73 69 7a 65 28 2a 03 03 39 03");
private static final byte[] SIMPLE_BALL_WITHOUT_TYPE_ID = VPackWireFixtureTest.hex(
            "14 0a 44 73 69 7a 65 28 2a 01");

    // Provenance: JsonTypeInfoIgnored2968Test#testDeserializeParentPositiveWithTypeId().
    void testDeserializeParentPositiveWithTypeIdVpack() throws Exception {
        Animal cat = MAPPER.readValue(ANIMAL_WITH_TYPE_ID, Animal.class);
        assertEquals("Cat-in-the-hat", cat.name);
        assertEquals(Cat.class, cat.getClass());
    }

    // Provenance: JsonTypeInfoIgnored2968Test#testDeserializeParentNegativeWithOutTypeId().
    void testDeserializeParentNegativeWithOutTypeIdVpack() {
        InvalidTypeIdException failure = assertThrows(InvalidTypeIdException.class,
                () -> MAPPER.readValue(ANIMAL_WITHOUT_TYPE_ID, Animal.class));
        assertTrue(failure.getMessage().contains("missing type id property"));
    }

    // Provenance: JsonTypeInfoIgnored2968Test#testDeserializedAsConcreteTypeSuccessfulWithOutPropertySet().
    void testDeserializedAsConcreteTypeSuccessfulWithOutPropertySetVpack() throws Exception {
        Cat cat = MAPPER.readValue(ANIMAL_WITHOUT_TYPE_ID, Cat.class);
        assertEquals("cat", cat.name);
    }

    // Provenance: JsonTypeInfoIgnored2968Test#testDeserializationWrapperWithDefaultTyping().
    void testDeserializationWrapperWithDefaultTypingVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        BallValueWrapper wrapper = mapper.readValue(DEFAULT_TYPED_WRAPPER, BallValueWrapper.class);
        assertEquals(42, wrapper.value.size);
        assertEquals(BasketBall.class, wrapper.value.getClass());
    }

    // Provenance: JsonTypeInfoIgnored2968Test#testDeserializationBaseClassWithDefaultTyping().
    void testDeserializationBaseClassWithDefaultTypingVpack() {
        ObjectMapper mapper = defaultTypingMapper();
        MismatchedInputException failure = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(SIMPLE_BALL_WITHOUT_TYPE_ID, SimpleBall.class));
        assertTrue(failure.getMessage().contains("START_OBJECT"));
    }
private static ObjectMapper defaultTypingMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(SimpleBall.class).build(), DefaultTyping.NON_FINAL)
                .build();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "Operation")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Equal.class, name = "eq"),
            @JsonSubTypes.Type(value = NotEqual.class, name = "notEq")
    })
    static abstract class Filter { }
static class Equal extends Filter { }
static class NotEqual extends Filter { }
interface Vehicle { }
static class Car implements Vehicle {
        public int wheels;
        public String color;
    }
static class Bicycle implements Vehicle {
        public int wheels;
        public String bicycleType;
    }
static class Person<T extends Vehicle> {
        public String name;
        public VehicleType vehicleType;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "vehicleType", defaultImpl = Car.class)
        @JsonTypeIdResolver(VehicleTypeResolver.class)
        public T vehicle;
    }
enum VehicleType {
        CAR(Car.class), BICYCLE(Bicycle.class);

        public final Class<? extends Vehicle> vehicleClass;

        VehicleType(Class<? extends Vehicle> vehicleClass) {
            this.vehicleClass = vehicleClass;
        }
    }
static class VehicleTypeResolver extends TypeIdResolverBase {
        private static final long serialVersionUID = 1L;
        private JavaType superType;

        @Override
        public void init(JavaType baseType) {
            superType = baseType;
        }

        @Override
        public String idFromValue(DatabindContext ctxt, Object value) {
            return idFromValueAndType(ctxt, value, value.getClass());
        }

        @Override
        public String idFromValueAndType(DatabindContext ctxt, Object value,
                Class<?> suggestedType) {
            return suggestedType.getSimpleName().toUpperCase();
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) {
            return context.constructSpecializedType(superType, VehicleType.valueOf(id).vehicleClass);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.NAME;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "_class")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Dog.class, name = "_dog"),
            @JsonSubTypes.Type(value = Cat.class, name = "_cat")
    })
    static abstract class Animal {
        public String name;
    }
static class Cat extends Animal { }
static class Dog extends Animal { }
static abstract class SimpleBall {
        public int size = 3;
    }
static class BasketBall extends SimpleBall {
        protected BasketBall() { }
    }
static final class BallValueWrapper {
        public SimpleBall value;
    }

    void __invoke_testDeserializeParentPositiveWithTypeIdVpack() throws Exception {
        try {
            testDeserializeParentPositiveWithTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeParentNegativeWithOutTypeIdVpack() throws Exception {
        try {
            testDeserializeParentNegativeWithOutTypeIdVpack();
        } finally {
        }
    }


    void __invoke_testDeserializedAsConcreteTypeSuccessfulWithOutPropertySetVpack() throws Exception {
        try {
            testDeserializedAsConcreteTypeSuccessfulWithOutPropertySetVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWrapperWithDefaultTypingVpack() throws Exception {
        try {
            testDeserializationWrapperWithDefaultTypingVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationBaseClassWithDefaultTypingVpack() throws Exception {
        try {
            testDeserializationBaseClassWithDefaultTypingVpack();
        } finally {
        }
    }

}
