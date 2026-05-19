package tools.jackson.databind.deser.builder;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException.Reference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidFormatException;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class BuilderWithNestedFieldsTest {

    private final ObjectMapper objectMapper = new VPackMapper();

    @JsonDeserialize(
            builder = Child.ChildBuilder.class
    )
    static final class Child {
        private final Integer field;

        Child(final Integer field) {
            this.field = field;
        }

        public Integer getField() {
            return this.field;
        }

        public static class ChildBuilder {
            private Integer field;

            public ChildBuilder withField(final Integer field) {
                this.field = field;
                return this;
            }

            public Child build() {
                return new Child(this.field);
            }
        }
    }

    @JsonDeserialize(
            builder = Parent.ParentBuilder.class
    )
    static final class Parent {
        private final Child child;

        Parent(final Child child) {
            this.child = child;
        }

        public Child getChild() {
            return this.child;
        }

        public static class ParentBuilder {
            private Child child;

            public ParentBuilder withChild(final Child child) {
                this.child = child;
                return this;
            }

            public Parent build() {
                return new Parent(this.child);
            }
        }
    }

    @Test
    void shouldBuildCorrectPathForNestedFields() {
        String json = """
                {
                    "child": {
                        "field": "invalid"
                    }
                }
                """;

        InvalidFormatException invalidFormatException = assertThrowsExactly(
                InvalidFormatException.class,
                () -> objectMapper.readValue(VPackUtils.toVPack(json), Parent.class));

        String fieldPath = fieldName(invalidFormatException.getPath());

       assertEquals("child.field", fieldPath);
    }

    private String fieldName(List<Reference> path) {
        return path.stream()
                .map(Reference::getPropertyName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));
    }
}
