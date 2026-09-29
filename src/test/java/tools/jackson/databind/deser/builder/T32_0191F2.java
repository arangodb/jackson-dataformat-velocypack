package tools.jackson.databind.deser.builder;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.ValueInstantiationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0191F2 {
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

    void testFailingValidatingBuilderWithExceptionWrapping() throws Exception {
        try {
            wrappingMapper.readValue(MISSING_SECOND, ValidatingValue.class);
        } catch (ValueInstantiationException e) {
            assertTrue(e.getMessage().contains("Missing second"));
            assertInstanceOf(ValidatingValue.ValidationException.class, e.getCause());
            return;
        }
        fail("Expected an exception");
    }

    void testFailingValidatingBuilderWithExceptionWrappingFromTree() throws Exception {
        try {
            JsonNode tree = wrappingMapper.readTree(MISSING_SECOND);
            wrappingMapper.treeToValue(tree, ValidatingValue.class);
        } catch (ValueInstantiationException e) {
            assertTrue(e.getMessage().contains("Missing second"));
            assertInstanceOf(ValidatingValue.ValidationException.class, e.getCause());
            return;
        }
        fail("Expected an exception");
    }

    void testFailingValidatingBuilderWithoutExceptionWrapping() throws Exception {
        try {
            noWrappingMapper.readValue(MISSING_SECOND, ValidatingValue.class);
        } catch (ValidatingValue.ValidationException e) {
            assertEquals("Missing second", e.getMessage());
            return;
        }
        fail("Expected an exception");
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

    void __invoke_testFailingValidatingBuilderWithExceptionWrapping() throws Exception {
        try {
            testFailingValidatingBuilderWithExceptionWrapping();
        } finally {
        }
    }


    void __invoke_testFailingValidatingBuilderWithExceptionWrappingFromTree() throws Exception {
        try {
            testFailingValidatingBuilderWithExceptionWrappingFromTree();
        } finally {
        }
    }


    void __invoke_testFailingValidatingBuilderWithoutExceptionWrapping() throws Exception {
        try {
            testFailingValidatingBuilderWithoutExceptionWrapping();
        } finally {
        }
    }

}
