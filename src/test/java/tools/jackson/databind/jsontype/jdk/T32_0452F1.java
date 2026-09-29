package tools.jackson.databind.jsontype.jdk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0452F1 {
private static final VPackMapper MAPPER = new VPackMapper();
private static final byte[] VALUE_TEN = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 0a 03");
private static final byte[] FILE_VALUE = VPackWireFixtureTest.hex(
            "0b 27 01 45 76 61 6c 75 65 06 1d 02 "
          + "4c 6a 61 76 61 2e 69 6f 2e 46 69 6c 65 "
          + "4a 2f 74 6d 70 2f 73 74 75 66 66 03 10 03");
private static final byte[] STRING_VALUE = VPackWireFixtureTest.hex(
            "0b 26 01 45 76 61 6c 75 65 06 1c 02 "
          + "50 6a 61 76 61 2e 6c 61 6e 67 2e 53 74 72 69 6e 67 "
          + "45 73 74 75 66 66 03 14 03");

    // Provenance: TypedContainerSerTest#testPolymorphicWithContainer().
    void testPolymorphicWithContainerVpack() throws Exception {
        Dog dog = new Dog("medor");
        dog.setBoneCount(3);

        Container1 c1 = new Container1();
        c1.setAnimal(dog);
        Container1 result1 = MAPPER.readValue(MAPPER.writeValueAsBytes(c1), Container1.class);
        assertDog(result1.getAnimal(), "medor", 3);

        Container2<Animal> c2 = new Container2<>();
        c2.setAnimal(dog);
        Container2<?> result2 = MAPPER.readValue(MAPPER.writeValueAsBytes(c2),
                MAPPER.getTypeFactory().constructParametricType(Container2.class, Animal.class));
        assertDog(result2.getAnimal(), "medor", 3);
    }

    // Provenance: TypedContainerSerTest#testIssue329().
    void testIssue329Vpack() throws Exception {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog("Spot"));
        JavaType rootType = MAPPER.getTypeFactory().constructParametricType(Iterator.class, Animal.class);
        byte[] encoded = MAPPER.writerFor(rootType).writeValueAsBytes(animals.iterator());
        Animal[] result = MAPPER.readValue(encoded, Animal[].class);
        assertEquals(1, result.length);
        assertDog(result[0], "Spot", 0);
    }

    // Provenance: TypedContainerSerTest#testIssue508().
    void testIssue508Vpack() throws Exception {
        List<List<Issue508A>> input = new ArrayList<>();
        List<Issue508A> nested = new ArrayList<>();
        nested.add(new Issue508A());
        input.add(nested);
        TypeReference<List<List<Issue508A>>> typeRef = new TypeReference<>() { };

        List<List<Issue508A>> result = MAPPER.readValue(
                MAPPER.writerFor(typeRef).writeValueAsBytes(input), typeRef);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).size());
        assertEquals(Issue508A.class, result.get(0).get(0).getClass());
    }
private static void assertDog(Animal animal, String name, int bones) {
        assertInstanceOf(Dog.class, animal);
        Dog dog = (Dog) animal;
        assertEquals(name, dog.name);
        assertEquals(bones, dog.boneCount);
    }
@SuppressWarnings("serial")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    static class TypedList<T> extends ArrayList<T> { }
@SuppressWarnings("serial")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static class TypedListAsProp<T> extends ArrayList<T> { }
@SuppressWarnings("serial")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    static class TypedListAsWrapper<T> extends LinkedList<T> { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface WrapperMixIn { }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes(@JsonSubTypes.Type(B.class))
    interface A { }
@JsonTypeName("BB")
    static class B implements A { public int value = 2; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    @JsonTypeName("bean")
    static class Bean { public int x = 0; }
static class BeanListWrapper {
        public List<Bean> beans = new ArrayList<>();
        { beans.add(new Bean()); }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "object-type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog.class, name = "doggy"),
            @JsonSubTypes.Type(value = Cat.class, name = "kitty") })
    static abstract class Animal {
        public String name;
        protected Animal() { }
        protected Animal(String name) { this.name = name; }
    }
@JsonTypeName("doggie")
    static class Dog extends Animal {
        public int boneCount;
        public Dog() { }
        @JsonCreator
        public Dog(@JsonProperty("name") String name) { super(name); }
        public void setBoneCount(int value) { boneCount = value; }
    }
@JsonTypeName("kitty")
    static class Cat extends Animal {
        public String furColor;
        public Cat() { }
        @JsonCreator
        public Cat(@JsonProperty("furColor") String color) { furColor = color; }
        public void setName(String value) { name = value; }
    }
static class Container1 {
        Animal animal;
        public Animal getAnimal() { return animal; }
        public void setAnimal(Animal value) { animal = value; }
    }
static class Container2<T extends Animal> {
        T animal;
        public T getAnimal() { return animal; }
        public void setAnimal(T value) { animal = value; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
    static class Issue508A { }
static class Issue508B extends Issue508A { }
static class WrappedPolymorphicUntyped {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object value;
        protected WrappedPolymorphicUntyped() { }
    }
static class WrappedPolymorphicUntypedSer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Serializable value;
        protected WrappedPolymorphicUntypedSer() { }
    }
static class WrappedPolymorphicComparable {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Comparable<?> value;
        protected WrappedPolymorphicComparable() { }
    }
static class NumbersAreOkValidator extends DefaultBaseTypeLimitingValidator {
        private static final long serialVersionUID = 1L;
        @Override
        protected boolean isUnsafeBaseType(DatabindContext ctxt, JavaType baseType) {
            if (baseType.hasRawClass(Object.class)) return false;
            return super.isUnsafeBaseType(ctxt, baseType);
        }
        @Override
        protected boolean isSafeSubType(DatabindContext ctxt, JavaType baseType, JavaType subType) {
            return baseType.isTypeOrSubTypeOf(Number.class);
        }
    }
static class ComparablesAreOkValidator extends DefaultBaseTypeLimitingValidator {
        private static final long serialVersionUID = 1L;
        @Override
        protected boolean isUnsafeBaseType(DatabindContext ctxt, JavaType baseType) {
            if (baseType.hasRawClass(Comparable.class)) return false;
            return super.isUnsafeBaseType(ctxt, baseType);
        }
        @Override
        protected boolean isSafeSubType(DatabindContext ctxt, JavaType baseType, JavaType subType) {
            if (baseType.hasRawClass(Comparable.class)) return subType.hasRawClass(File.class);
            return super.isSafeSubType(ctxt, baseType, subType);
        }
    }

    void __invoke_testPolymorphicWithContainerVpack() throws Exception {
        try {
            testPolymorphicWithContainerVpack();
        } finally {
        }
    }


    void __invoke_testIssue329Vpack() throws Exception {
        try {
            testIssue329Vpack();
        } finally {
        }
    }


    void __invoke_testIssue508Vpack() throws Exception {
        try {
            testIssue508Vpack();
        } finally {
        }
    }

}
