package tools.jackson.databind.deser.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0192F1 {
private static final byte[] UNKNOWN_PROPERTY = VPackWireFixtureTest.hex(
            "14 0c 41 78 31 41 7a 32 41 79 34 03");
private static final byte[] WRONG_SHAPE = VPackWireFixtureTest.hex("28 7b");
private static final byte[] COMPLETE_VALIDATING_VALUE = VPackWireFixtureTest.hex(
            "14 0b 41 61 41 31 41 62 41 32 02");
private static final byte[] MISSING_SECOND = VPackWireFixtureTest.hex(
            "14 07 41 61 41 31 01");
private static final byte[] INVALID_BUILD_TYPE = VPackWireFixtureTest.hex(
            "14 06 41 78 31 01");
private static final byte[] EXTRA_FIELDS = VPackWireFixtureTest.hex(
            "14 0c 41 78 31 41 79 32 41 7a 33 03");
private static final byte[] UNWRAPPED_BUILDER = VPackWireFixtureTest.hex(
            "14 1f 47 73 75 62 2e 65 6c 31 28 22 "
          + "47 73 75 62 2e 65 6c 32 49 73 6f 6d 65 20 74 65 78 74 02");
private final ObjectMapper mapper = new VPackMapper();
private final ObjectMapper strictMapper = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
private final ObjectMapper noWrappingMapper = VPackMapper.builder()
            .disable(DeserializationFeature.WRAP_EXCEPTIONS)
            .build();

    void testBuilderMethodReturnInvalidType() {
        InvalidDefinitionException exception = assertThrows(
                InvalidDefinitionException.class,
                () -> mapper.readValue(INVALID_BUILD_TYPE, ValueClassWrongBuildType.class));
        assertTrue(exception.getMessage().contains("Build method"));
        assertTrue(exception.getMessage().contains("build()"));
        assertTrue(exception.getMessage().contains("wrong return type"));
    }

    void testExtraFields() {
        UnrecognizedPropertyException exception = assertThrows(
                UnrecognizedPropertyException.class,
                () -> strictMapper.readValue(EXTRA_FIELDS, ValueClassXY.class));
        assertTrue(exception.getMessage().contains("Unrecognized property \"z\""));
    }
@JsonDeserialize(builder = SimpleBuilderXY.class)
    static class ValueClassXY {
        final int x;
        final int y;

        protected ValueClassXY(int x, int y) {
            this.x = x + 1;
            this.y = y + 1;
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
@JsonDeserialize(builder = ValueBuilderWrongBuildType.class)
    static class ValueClassWrongBuildType { }
static class ValueBuilderWrongBuildType {
        public int x;

        public ValueBuilderWrongBuildType withX(int value) {
            x = value;
            return this;
        }

        public ValueClassXY build() {
            return null;
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
@JsonDeserialize(builder = Builder.class)
    static class Bean {
        Integer id;
        SubBean thing;

        public Bean(Integer id) {
            this.id = id;
        }

        public SubBean getThing() {
            return thing;
        }

        public void setThing(SubBean value) {
            thing = value;
        }
    }
static class Builder {
        private SubBean temp;
        private Integer id;

        Builder(@JsonProperty("beanId") Integer beanId) {
            id = beanId;
        }

        @JsonUnwrapped(prefix = "sub.")
        public Builder withThing(SubBean value) {
            temp = value;
            return this;
        }

        public Bean build() {
            Bean bean = new Bean(id);
            bean.setThing(temp);
            return bean;
        }
    }
@JsonDeserialize(builder = SubBuilder.class)
    static class SubBean {
        public int element1;
        public String element2;
    }
static class SubBuilder {
        private int element1;
        private String element2;

        @JsonProperty("el1")
        public SubBuilder withElement1(int value) {
            element1 = value;
            return this;
        }

        public SubBuilder withElement2(String value) {
            element2 = value;
            return this;
        }

        public SubBean build() {
            SubBean bean = new SubBean();
            bean.element1 = element1;
            bean.element2 = element2;
            return bean;
        }
    }

    void __invoke_testBuilderMethodReturnInvalidType() throws Exception {
        try {
            testBuilderMethodReturnInvalidType();
        } finally {
        }
    }


    void __invoke_testExtraFields() throws Exception {
        try {
            testExtraFields();
        } finally {
        }
    }

}
