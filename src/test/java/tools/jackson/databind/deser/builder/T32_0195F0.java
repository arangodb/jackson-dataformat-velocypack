package tools.jackson.databind.deser.builder;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonPOJOBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0195F0 {
private static final byte[] GENERIC_DATA = VPackWireFixtureTest.hex(
            "14 16 44 64 61 74 61 13 0e 14 0b 41 78 41 78 41 79 41 79 02 01 01");
private static final byte[] SINGLE_VALUE_ARRAY = VPackWireFixtureTest.hex(
            "14 1c 45 76 61 6c 75 65 13 13 14 10 48 73 75 62 56 61 6c 75 65 43 31 32 33 01 01 01");
private static final byte[] PERSON_ID_FIRST = VPackWireFixtureTest.hex(
            "14 42 49 70 65 72 73 6f 6e 5f 69 64 29 d2 04 "
          + "4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e "
          + "46 6c 69 76 69 6e 67 1a 05");
private static final byte[] PERSON_NAME_FIRST = VPackWireFixtureTest.hex(
            "14 42 4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 70 65 72 73 6f 6e 5f 69 64 29 d2 04 "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e "
          + "46 6c 69 76 69 6e 67 1a 05");
private static final byte[] PERSON_LIVING_LAST = VPackWireFixtureTest.hex(
            "14 42 4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e "
          + "46 6c 69 76 69 6e 67 1a "
          + "49 70 65 72 73 6f 6e 5f 69 64 29 d2 04 05");
private static final byte[] ANIMAL_ID_FIRST = VPackWireFixtureTest.hex(
            "14 42 49 61 6e 69 6d 61 6c 5f 69 64 29 d2 04 "
          + "46 6c 69 76 69 6e 67 1a "
          + "4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e 05");
private static final byte[] ANIMAL_ID_MIDDLE = VPackWireFixtureTest.hex(
            "14 42 4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 61 6e 69 6d 61 6c 5f 69 64 29 d2 04 "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "46 6c 69 76 69 6e 67 1a "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e 05");
private static final byte[] ANIMAL_ID_LAST = VPackWireFixtureTest.hex(
            "14 42 4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 79 65 61 72 73 5f 6f 6c 64 28 1e "
          + "46 6c 69 76 69 6e 67 1a "
          + "49 61 6e 69 6d 61 6c 5f 69 64 29 d2 04 05");
private static final byte[] ANIMAL_NO_CREATOR = VPackWireFixtureTest.hex(
            "14 3b 4a 66 69 72 73 74 5f 6e 61 6d 65 44 4a 6f 68 6e "
          + "49 6c 61 73 74 5f 6e 61 6d 65 43 44 6f 65 "
          + "49 79 65 61 72 73 5f 6f 6c 64 42 33 30 "
          + "49 61 6e 69 6d 61 6c 5f 69 64 29 d2 04 04");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: databind/deser/builder/BuilderWithTypeParametersTest#testWithBuilderInferringBindings.
    void testWithBuilderInferringBindings() throws Exception {
        ObjectMapper inferringMapper = VPackMapper.builder()
                .enable(MapperFeature.INFER_BUILDER_TYPE_BINDINGS)
                .build();
        MyGenericPOJO<MyPOJO> deserialized = inferringMapper.readValue(
                GENERIC_DATA, new TypeReference<MyGenericPOJO<MyPOJO>>() { });
        assertEquals(1, deserialized.data.size());
        Object value = deserialized.data.get(0);
        assertNotNull(value);
        assertEquals(MyPOJO.class, value.getClass());
    }

    // Provenance: databind/deser/builder/BuilderWithTypeParametersTest#testWithBuilderWithoutInferringBindings.
    void testWithBuilderWithoutInferringBindings() throws Exception {
        ObjectMapper nonInferringMapper = VPackMapper.builder()
                .disable(MapperFeature.INFER_BUILDER_TYPE_BINDINGS)
                .build();
        MyGenericPOJO<MyPOJO> deserialized = nonInferringMapper.readValue(
                GENERIC_DATA, new TypeReference<MyGenericPOJO<MyPOJO>>() { });
        assertEquals(1, deserialized.data.size());
        Object value = deserialized.data.get(0);
        assertNotNull(value);
        assertEquals(LinkedHashMap.class, value.getClass());
    }
private static void assertPerson(Person person) {
        assertEquals(1234, person.getId());
        assertNotNull(person.getName());
        assertEquals("John", person.getName().getFirst());
        assertEquals("Doe", person.getName().getLast());
        assertEquals(30, person.getAge());
        assertEquals(true, person.isAlive());
    }
private static void assertAnimal(Animal animal) {
        assertEquals(1234, animal.getId());
        assertNotNull(animal.getName());
        assertEquals("John", animal.getName().getFirst());
        assertEquals("Doe", animal.getName().getLast());
        assertEquals(30, animal.getAge());
        assertEquals(true, animal.isAlive());
    }
public static class MyPOJO {
        public String x;
        public String y;

        @JsonCreator
        public MyPOJO(@JsonProperty("x") String x, @JsonProperty("y") String y) {
            this.x = x;
            this.y = y;
        }
    }
@JsonDeserialize(builder = MyGenericPOJO.Builder.class)
    public static class MyGenericPOJO<T> {
        List<T> data;

        MyGenericPOJO(List<T> data) {
            this.data = data;
        }

        public List<T> getData() {
            return data;
        }

        public static class Builder<T> {
            private List<T> data;

            public Builder<T> withData(List<T> data) {
                this.data = data;
                return this;
            }

            public MyGenericPOJO<T> build() {
                return new MyGenericPOJO<T>(data);
            }
        }
    }
@JsonDeserialize(builder = ExamplePOJO2608.ExamplePOJOBuilder.class)
    static class ExamplePOJO2608 {
        public int id;
        public POJOValue2608 value;

        public ExamplePOJO2608(int id, POJOValue2608 value) {
            this.id = id;
            this.value = value;
        }

        @JsonPOJOBuilder(withPrefix = "")
        static class ExamplePOJOBuilder {
            int id;
            POJOValue2608 value;

            public ExamplePOJOBuilder id(int id) {
                this.id = id;
                return this;
            }

            public ExamplePOJOBuilder value(POJOValue2608 value) {
                this.value = value;
                return this;
            }

            public ExamplePOJO2608 build() {
                return new ExamplePOJO2608(id, value);
            }
        }
    }
@JsonDeserialize(builder = POJOValue2608.POJOValueBuilder.class)
    static class POJOValue2608 {
        public String subValue;

        public POJOValue2608(String value) {
            subValue = value;
        }

        @JsonPOJOBuilder(withPrefix = "")
        public static class POJOValueBuilder {
            String value;

            public POJOValueBuilder subValue(String value) {
                this.value = value;
                return this;
            }

            public POJOValue2608 build() {
                return new POJOValue2608(value);
            }
        }
    }
static class Name {
        private final String first;
        private final String last;

        @JsonCreator
        Name(@JsonProperty("first_name") String first,
                @JsonProperty("last_name") String last) {
            this.first = first;
            this.last = last;
        }

        String getFirst() {
            return first;
        }

        String getLast() {
            return last;
        }
    }
@JsonDeserialize(builder = Person.Builder.class)
    static class Person {
        private final long id;
        private final Name name;
        private final int age;
        private final boolean alive;

        Person(Builder builder) {
            id = builder.id;
            name = builder.name;
            age = builder.age;
            alive = builder.alive;
        }

        long getId() {
            return id;
        }

        Name getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        boolean isAlive() {
            return alive;
        }

        @JsonPOJOBuilder(withPrefix = "set")
        static class Builder {
            final long id;
            Name name;
            int age;
            boolean alive;

            @JsonCreator
            public Builder(@JsonProperty("person_id") long id) {
                this.id = id;
            }

            @JsonUnwrapped
            void setName(Name name) {
                this.name = name;
            }

            @JsonProperty("years_old")
            void setAge(int age) {
                this.age = age;
            }

            @JsonProperty("living")
            void setAlive(boolean alive) {
                this.alive = alive;
            }

            Person build() {
                return new Person(this);
            }
        }
    }
@JsonDeserialize(builder = Animal.Builder.class)
    static class Animal {
        private final long id;
        private final Name name;
        private final int age;
        private final boolean alive;

        Animal(Builder builder) {
            id = builder.id;
            name = builder.name;
            age = builder.age;
            alive = builder.alive;
        }

        long getId() {
            return id;
        }

        Name getName() {
            return name;
        }

        int getAge() {
            return age;
        }

        boolean isAlive() {
            return alive;
        }

        @JsonPOJOBuilder(withPrefix = "set")
        static class Builder {
            final long id;
            Name name;
            int age;
            final boolean alive;

            @JsonCreator
            public Builder(@JsonProperty("animal_id") long id,
                    @JsonProperty("living") boolean alive) {
                this.id = id;
                this.alive = alive;
            }

            @JsonUnwrapped
            void setName(Name name) {
                this.name = name;
            }

            @JsonProperty("years_old")
            void setAge(int age) {
                this.age = age;
            }

            Animal build() {
                return new Animal(this);
            }
        }
    }
@JsonDeserialize(builder = AnimalNoCreator.Builder.class)
    static class AnimalNoCreator {
        private final long id;
        private final Name name;
        private final String age;

        AnimalNoCreator(Builder builder) {
            id = builder.id;
            name = builder.name;
            age = builder.age;
        }

        long getId() {
            return id;
        }

        Name getName() {
            return name;
        }

        String getAge() {
            return age;
        }

        @JsonPOJOBuilder(withPrefix = "set")
        static class Builder {
            long id;
            Name name;
            String age;

            Builder() { }

            @JsonProperty("animal_id")
            public void setId(long id) {
                this.id = id;
            }

            @JsonUnwrapped
            void setName(Name name) {
                this.name = name;
            }

            @JsonProperty("years_old")
            void setAge(String age) {
                this.age = age;
            }

            AnimalNoCreator build() {
                return new AnimalNoCreator(this);
            }
        }
    }

    void __invoke_testWithBuilderInferringBindings() throws Exception {
        try {
            testWithBuilderInferringBindings();
        } finally {
        }
    }


    void __invoke_testWithBuilderWithoutInferringBindings() throws Exception {
        try {
            testWithBuilderWithoutInferringBindings();
        } finally {
        }
    }

}
