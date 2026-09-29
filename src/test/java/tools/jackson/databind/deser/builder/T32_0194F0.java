package tools.jackson.databind.deser.builder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException.Reference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0194F0 {
private static final byte[] X_1 = VPackWireFixtureTest.hex(
            "14 06 41 78 31 01");
private static final byte[] VALUE_2 = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 32 01");
private static final byte[] CREATOR_PROPERTIES = VPackWireFixtureTest.hex(
            "14 0c 41 61 31 41 63 33 41 62 32 03");
private static final byte[] DELEGATING_INT_139 = VPackWireFixtureTest.hex(
            "28 8b");
private static final byte[] DELEGATING_DOUBLE_NEGATIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 0e c0");
private static final byte[] DELEGATING_BOOLEAN_TRUE = VPackWireFixtureTest.hex(
            "1a");
private static final byte[] NESTED_INVALID_FIELD = VPackWireFixtureTest.hex(
            "14 1a 45 63 68 69 6c 64 14 11 45 66 69 65 6c 64 "
          + "47 69 6e 76 61 6c 69 64 01 01");
private final ObjectMapper mapper = new VPackMapper();

    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testBuilderUpdateWithValue.
    // Builder-backed immutable values cannot be updated from the built instance;
    // preserve the source's InvalidDefinitionException contract and hint.
    void testBuilderUpdateWithValue() throws Exception {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> mapper.readerFor(ValueClassXY.class)
                        .withValueToUpdate(new ValueClassXY(6, 7))
                        .readValue(X_1));
        assertEquals(true, exception.getMessage().contains("Deserialization of"));
        assertEquals(true, exception.getMessage().contains("by passing existing instance"));
        assertEquals(true, exception.getMessage().contains("ValueClassXY"));
        assertEquals(true, exception.getMessage().contains("pass a Builder"));
        assertEquals(true, exception.getMessage().contains("SimpleBuilderXY"));
    }

    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testBuilderUpdateWithBuilder.
    void testBuilderUpdateWithBuilder() throws Exception {
        SimpleBuilderXY builder = new SimpleBuilderXY();
        builder.x = 10;
        builder.y = 20;
        ValueClassXY result = mapper.readerFor(ValueClassXY.class)
                .withValueToUpdate(builder)
                .readValue(X_1);

        assertEquals(1, builder.x);
        assertEquals(20, builder.y);
        assertNotNull(result);
        assertEquals(1, result.x);
        assertEquals(20, result.y);
    }

    // Provenance: databind/deser/builder/BuilderViaUpdateTest#testIssue2100Reproducer.
    void testIssue2100Reproducer() throws Exception {
        POJO2100.Builder builder = new POJO2100.Builder().withId(1).withValue("1");
        POJO2100 result = mapper.readerFor(POJO2100.class)
                .withValueToUpdate(builder)
                .readValue(VALUE_2);

        assertEquals(1, builder.id);
        assertEquals("2", builder.value);
        assertNotNull(result);
        assertEquals(1, result.id);
        assertEquals("2", result.value);
    }
private String fieldName(List<Reference> path) {
        return path.stream()
                .map(Reference::getPropertyName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));
    }
@JsonDeserialize(builder = SimpleBuilderXY.class)
    static class ValueClassXY {
        public final int x;
        public final int y;

        ValueClassXY(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
static class SimpleBuilderXY {
        public int x;
        public int y;

        public SimpleBuilderXY withX(int value) {
            x = value;
            return this;
        }

        public SimpleBuilderXY withY(int value) {
            y = value;
            return this;
        }

        public ValueClassXY build() {
            return new ValueClassXY(x, y);
        }
    }
@JsonDeserialize(builder = POJO2100.Builder.class)
    static class POJO2100 {
        public final int id;
        public final String value;

        POJO2100(int id, String value) {
            this.id = id;
            this.value = value;
        }

        static class Builder {
            int id;
            String value;

            public Builder withId(int value) {
                id = value;
                return this;
            }

            public Builder withValue(String value) {
                this.value = value;
                return this;
            }

            public POJO2100 build() {
                return new POJO2100(id, value);
            }
        }
    }
@JsonDeserialize(builder = PropertyCreatorBuilder.class)
    static class PropertyCreatorValue {
        final int a;
        final int b;
        final int c;

        PropertyCreatorValue(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
static class PropertyCreatorBuilder {
        private final int a;
        private final int b;
        private int c;

        @JsonCreator
        public PropertyCreatorBuilder(@JsonProperty("a") int a,
                @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }

        public PropertyCreatorBuilder withC(int value) {
            c = value;
            return this;
        }

        public PropertyCreatorValue build() {
            return new PropertyCreatorValue(a, b, c);
        }
    }
@JsonDeserialize(builder = IntCreatorBuilder.class)
    static class IntCreatorValue {
        final int value;

        IntCreatorValue(int value) {
            this.value = value;
        }
    }
static class IntCreatorBuilder {
        private final int value;

        @JsonCreator
        public IntCreatorBuilder(int value) {
            this.value = value;
        }

        public IntCreatorValue build() {
            return new IntCreatorValue(value);
        }
    }
@JsonDeserialize(builder = DoubleCreatorBuilder.class)
    static class DoubleCreatorValue {
        final double value;

        DoubleCreatorValue(double value) {
            this.value = value;
        }
    }
static class DoubleCreatorBuilder {
        private final double value;

        @JsonCreator
        public DoubleCreatorBuilder(double value) {
            this.value = value;
        }

        public DoubleCreatorValue build() {
            return new DoubleCreatorValue(value);
        }
    }
@JsonDeserialize(builder = BooleanCreatorBuilder.class)
    static class BooleanCreatorValue {
        final boolean value;

        BooleanCreatorValue(boolean value) {
            this.value = value;
        }
    }
static class BooleanCreatorBuilder {
        private final boolean value;

        @JsonCreator
        public BooleanCreatorBuilder(boolean value) {
            this.value = value;
        }

        public BooleanCreatorValue build() {
            return new BooleanCreatorValue(value);
        }
    }
@JsonDeserialize(builder = Child.ChildBuilder.class)
    static final class Child {
        private final Integer field;

        Child(Integer field) {
            this.field = field;
        }

        static class ChildBuilder {
            private Integer field;

            public ChildBuilder withField(Integer value) {
                field = value;
                return this;
            }

            public Child build() {
                return new Child(field);
            }
        }
    }
@JsonDeserialize(builder = Parent.ParentBuilder.class)
    static final class Parent {
        private final Child child;

        Parent(Child child) {
            this.child = child;
        }

        static class ParentBuilder {
            private Child child;

            public ParentBuilder withChild(Child value) {
                child = value;
                return this;
            }

            public Parent build() {
                return new Parent(child);
            }
        }
    }

    void __invoke_testBuilderUpdateWithValue() throws Exception {
        try {
            testBuilderUpdateWithValue();
        } finally {
        }
    }


    void __invoke_testBuilderUpdateWithBuilder() throws Exception {
        try {
            testBuilderUpdateWithBuilder();
        } finally {
        }
    }


    void __invoke_testIssue2100Reproducer() throws Exception {
        try {
            testIssue2100Reproducer();
        } finally {
        }
    }

}
