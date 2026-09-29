package tools.jackson.databind.jsontype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0430Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: TestTypedSerialization#testCollectionWithTypeInfo().
    void testCollectionWithTypeInfoVpack() throws Exception {
        List<List1451A> input = new ArrayList<>();
        List1451A a = new List1451A();
        a.a = "a1";
        input.add(a);

        List1451B b = new List1451B();
        b.a = "a2";
        b.b = "b";
        input.add(b);

        TypeReference<Collection<List1451A>> type = new TypeReference<>() { };
        ObjectWriter writer = MAPPER.writerFor(type);
        byte[] encoded = writer.writeValueAsBytes(input);

        List<?> portable = MAPPER.readValue(encoded, List.class);
        assertEquals(2, portable.size());
        Map<?, ?> first = assertInstanceOf(Map.class, portable.get(0));
        assertEquals("a1", first.get("a"));
        assertEquals("." + T32_0430Fixture.class.getSimpleName()
                + "$List1451A", first.get("@class"));
        Map<?, ?> second = assertInstanceOf(Map.class, portable.get(1));
        assertEquals("a2", second.get("a"));
        assertEquals("b", second.get("b"));
        assertEquals("." + T32_0430Fixture.class.getSimpleName()
                + "$List1451B", second.get("@class"));

        Collection<List1451A> output = MAPPER.readValue(encoded, type);
        assertEquals(2, output.size());
        List<List1451A> outputList = new ArrayList<>(output);
        assertEquals(List1451A.class, outputList.get(0).getClass());
        assertEquals(List1451B.class, outputList.get(1).getClass());
    }

    // Provenance: TestTypedSerialization#testEmptyBean().
    void testEmptyBeanVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .build();
        Map<?, ?> encoded = mapper.readValue(mapper.writeValueAsBytes(new Empty()), Map.class);
        assertEquals(1, encoded.size());
        assertEquals("empty", encoded.get("@type"));
    }

    // Provenance: TestTypedSerialization#testInArray().
    void testInArrayVpack() throws Exception {
        Animal[] animals = new Animal[] {
                new Cat("Miuku", "white"), new Dog("Murre", 9)
        };
        Map<String, Object> input = new HashMap<>();
        input.put("a", animals);

        Map<?, ?> result = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals(1, result.size());
        List<?> values = assertInstanceOf(List.class, result.get("a"));
        assertEquals(2, values.size());
        Map<?, ?> cat = assertInstanceOf(Map.class, values.get(0));
        assertEquals(3, cat.size());
        assertEquals(Cat.class.getName(), cat.get("@class"));
        Map<?, ?> dog = assertInstanceOf(Map.class, values.get(1));
        assertEquals(3, dog.size());
        assertEquals(Dog.class.getName(), dog.get("@class"));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static abstract class Animal {
        public String name;
        Animal() { }
        Animal(String name) { this.name = name; }
    }
static final class Cat extends Animal {
        public String furColor;
        Cat() { }
        Cat(String name, String furColor) {
            super(name);
            this.furColor = furColor;
        }
    }
static final class Dog extends Animal {
        public int boneCount;
        Dog() { }
        Dog(String name, int boneCount) {
            super(name);
            this.boneCount = boneCount;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "@class")
    static class List1451A {
        public String a;
    }
static class List1451B extends List1451A {
        public String b;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("empty")
    static class Empty { }

    void __invoke_testCollectionWithTypeInfoVpack() throws Exception {
        try {
            testCollectionWithTypeInfoVpack();
        } finally {
        }
    }


    void __invoke_testEmptyBeanVpack() throws Exception {
        try {
            testEmptyBeanVpack();
        } finally {
        }
    }


    void __invoke_testInArrayVpack() throws Exception {
        try {
            testInArrayVpack();
        } finally {
        }
    }

}
