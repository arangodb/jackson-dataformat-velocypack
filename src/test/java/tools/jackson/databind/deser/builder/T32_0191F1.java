package tools.jackson.databind.deser.builder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0191F1 {
private static final byte[] ANIMALS = VPackWireFixtureTest.hex(
            "14 5e 47 61 6e 69 6d 61 6c 73 13 53 "
          + "14 28 44 6b 69 6e 64 44 62 69 72 64 "
          + "4a 70 72 6f 70 65 72 74 69 65 73 14 10 "
          + "45 63 6f 6c 6f 72 46 79 65 6c 6c 6f 77 01 02 "
          + "14 28 44 6b 69 6e 64 46 6d 61 6d 6d 61 6c "
          + "4a 70 72 6f 70 65 72 74 69 65 73 14 0e "
          + "49 6e 75 6d 5f 74 65 65 74 68 32 01 02 02 01");
private static final byte[] INDEX_OBJECT = VPackWireFixtureTest.hex(
            "14 0b 45 69 6e 64 65 78 28 7b 01");
private static final byte[] INDEX_ARRAY = VPackWireFixtureTest.hex(
            "13 05 28 7b 01");
private static final byte[] INDEX_SCALAR = VPackWireFixtureTest.hex("28 7b");
private static final byte[] MISSING_SECOND = VPackWireFixtureTest.hex(
            "14 07 41 61 41 31 01");
private final ObjectMapper mapper = new VPackMapper();
private final ObjectMapper wrappingMapper = VPackMapper.builder()
            .enable(DeserializationFeature.WRAP_EXCEPTIONS)
            .build();
private final ObjectMapper noWrappingMapper = VPackMapper.builder()
            .disable(DeserializationFeature.WRAP_EXCEPTIONS)
            .build();

    void testPOJOWithArrayCreatorFromArrayRepresentation() throws Exception {
        MyPOJOWithArrayCreator result = mapper.readValue(INDEX_ARRAY,
                MyPOJOWithArrayCreator.class);
        assertEquals(123, result.getIndex());
    }

    void testPOJOWithArrayCreatorFromObjectRepresentation() throws Exception {
        MyPOJOWithArrayCreator result = mapper.readValue(INDEX_OBJECT,
                MyPOJOWithArrayCreator.class);
        assertEquals(123, result.getIndex());
    }

    void testPOJOWithPrimitiveCreatorFromObjectRepresentation() throws Exception {
        MyPOJOWithPrimitiveCreator result = mapper.readValue(INDEX_OBJECT,
                MyPOJOWithPrimitiveCreator.class);
        assertEquals(123, result.index);
    }

    void testPOJOWithPrimitiveCreatorFromPrimitiveRepresentation() throws Exception {
        MyPOJOWithPrimitiveCreator result = mapper.readValue(INDEX_SCALAR,
                MyPOJOWithPrimitiveCreator.class);
        assertEquals(123, result.index);
    }

    void testPOJOBuilderWithArrayCreatorFromArrayRepresentation() throws Exception {
        MyPOJOWithArrayCreator.Builder result = mapper.readValue(INDEX_ARRAY,
                MyPOJOWithArrayCreator.Builder.class);
        assertEquals(123, result.index);
    }

    void testPOJOBuilderWithArrayCreatorFromObjectRepresentation() throws Exception {
        MyPOJOWithArrayCreator.Builder result = mapper.readValue(INDEX_OBJECT,
                MyPOJOWithArrayCreator.Builder.class);
        assertEquals(123, result.index);
    }

    void testPOJOBuilderWithPrimitiveCreatorFromObjectRepresentation() throws Exception {
        MyPOJOWithPrimitiveCreator.Builder result = mapper.readValue(INDEX_OBJECT,
                MyPOJOWithPrimitiveCreator.Builder.class);
        assertEquals(123, result.index);
    }

    void testPOJOBuilderWithPrimitiveCreatorFromPrimitiveRepresentation() throws Exception {
        MyPOJOWithPrimitiveCreator.Builder result = mapper.readValue(INDEX_SCALAR,
                MyPOJOWithPrimitiveCreator.Builder.class);
        assertEquals(123, result.index);
    }
static class Animals {
        @JsonProperty("animals")
        public List<Animal> animals;
    }
@JsonDeserialize(builder = Animal.Builder.class)
    static class Animal {
        @JsonProperty("kind")
        public String kind;
        @JsonProperty("properties")
        public AnimalProperties properties;

        static abstract class Builder {
            @JsonProperty("kind")
            public abstract Builder kind(String kind);

            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                    include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "kind")
            @JsonSubTypes({
                    @JsonSubTypes.Type(name = "bird", value = BirdProperties.class),
                    @JsonSubTypes.Type(name = "mammal", value = MammalProperties.class)
            })
            @JsonProperty("properties")
            public abstract Builder properties(AnimalProperties properties);

            @JsonCreator
            public static BuilderImpl create() {
                return new BuilderImpl();
            }

            public abstract Animal build();
        }

        static class BuilderImpl extends Builder {
            private String kind;
            private AnimalProperties properties;

            @Override
            public BuilderImpl kind(String value) {
                kind = value;
                return this;
            }

            @Override
            public BuilderImpl properties(AnimalProperties value) {
                properties = value;
                return this;
            }

            @Override
            public Animal build() {
                Animal result = new Animal();
                result.kind = kind;
                result.properties = properties;
                return result;
            }
        }
    }
interface AnimalProperties { }
static class MammalProperties implements AnimalProperties {
        public int num_teeth;
    }
static class BirdProperties implements AnimalProperties {
        public String color;
    }
@JsonDeserialize(builder = MyPOJOWithArrayCreator.Builder.class)
    static class MyPOJOWithArrayCreator {
        private final int index;

        MyPOJOWithArrayCreator(int value) {
            index = value;
        }

        public int getIndex() {
            return index;
        }

        static class Builder {
            int index;

            public Builder() { }

            @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
            public Builder(List<Object> values) {
                withIndex((int) values.get(0));
            }

            public Builder withIndex(int value) {
                index = value;
                return this;
            }

            public Builder setIndex(int value) {
                index = value;
                return this;
            }

            public MyPOJOWithArrayCreator build() {
                return new MyPOJOWithArrayCreator(index);
            }
        }
    }
@JsonDeserialize(builder = MyPOJOWithPrimitiveCreator.Builder.class)
    static class MyPOJOWithPrimitiveCreator {
        private final int index;

        MyPOJOWithPrimitiveCreator(int value) {
            index = value;
        }

        public int getIndex() {
            return index;
        }

        static class Builder {
            int index;

            public Builder() { }

            @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
            public Builder(int value) {
                withIndex(value);
            }

            public Builder withIndex(int value) {
                index = value;
                return this;
            }

            public Builder setIndex(int value) {
                index = value;
                return this;
            }

            public MyPOJOWithPrimitiveCreator build() {
                return new MyPOJOWithPrimitiveCreator(index);
            }
        }
    }
@JsonDeserialize(builder = ValidatingValue.Builder.class)
    static class ValidatingValue {
        final String first;
        final String second;

        ValidatingValue(String firstValue, String secondValue) {
            first = firstValue;
            second = secondValue;
        }

        @SuppressWarnings("serial")
        static class ValidationException extends RuntimeException {
            ValidationException(String message) {
                super(message);
            }
        }

        static class Builder {
            private String first;
            private String second;

            @JsonSetter("a")
            Builder first(String value) {
                first = value;
                return this;
            }

            @JsonSetter("b")
            Builder second(String value) {
                second = value;
                return this;
            }

            ValidatingValue build() {
                if (first == null) {
                    throw new ValidationException("Missing first");
                }
                if (second == null) {
                    throw new ValidationException("Missing second");
                }
                return new ValidatingValue(first, second);
            }
        }
    }

    void __invoke_testPOJOWithArrayCreatorFromArrayRepresentation() throws Exception {
        try {
            testPOJOWithArrayCreatorFromArrayRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOWithArrayCreatorFromObjectRepresentation() throws Exception {
        try {
            testPOJOWithArrayCreatorFromObjectRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOWithPrimitiveCreatorFromObjectRepresentation() throws Exception {
        try {
            testPOJOWithPrimitiveCreatorFromObjectRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOWithPrimitiveCreatorFromPrimitiveRepresentation() throws Exception {
        try {
            testPOJOWithPrimitiveCreatorFromPrimitiveRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOBuilderWithArrayCreatorFromArrayRepresentation() throws Exception {
        try {
            testPOJOBuilderWithArrayCreatorFromArrayRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOBuilderWithArrayCreatorFromObjectRepresentation() throws Exception {
        try {
            testPOJOBuilderWithArrayCreatorFromObjectRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOBuilderWithPrimitiveCreatorFromObjectRepresentation() throws Exception {
        try {
            testPOJOBuilderWithPrimitiveCreatorFromObjectRepresentation();
        } finally {
        }
    }


    void __invoke_testPOJOBuilderWithPrimitiveCreatorFromPrimitiveRepresentation() throws Exception {
        try {
            testPOJOBuilderWithPrimitiveCreatorFromPrimitiveRepresentation();
        } finally {
        }
    }

}
