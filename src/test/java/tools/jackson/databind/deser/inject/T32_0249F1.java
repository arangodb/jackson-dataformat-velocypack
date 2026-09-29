package tools.jackson.databind.deser.inject;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MissingInjectableValueExcepion;
import tools.jackson.databind.exc.MissingInjectableValueException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0249F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FIELD1_ONLY = VPackWireFixtureTest.hex(
            "14 16 46 66 69 65 6c 64 31 4b 66 69 65 6c 64 31 76 61 6c 75 65 01");
private static final byte[] BOTH_FIELDS = VPackWireFixtureTest.hex(
            "14 29 46 66 69 65 6c 64 31 4b 66 69 65 6c 64 31 76 61 6c 75 65 "
                    + "46 66 69 65 6c 64 32 4b 66 69 65 6c 64 32 76 61 6c 75 65 02");
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder().build();
private final ObjectReader OPTIONAL_READER = DEFAULT_MAPPER.readerFor(DtoWithOptional.class);

    void testOptionalFieldFound() throws Exception {
        ObjectReader reader = OPTIONAL_READER
                .with(new InjectableValues.Std()
                        .addValue("id", "idValue")
                        .addValue("optionalField", "optionalFieldValue"));

        DtoWithOptional dto = reader.readValue(EMPTY_OBJECT);

        assertEquals("idValue", dto.id);
        assertEquals("optionalFieldValue", dto.optionalField);
    }

    void testOptionalFieldNotFound() throws Exception {
        ObjectReader reader = OPTIONAL_READER
                .with(new InjectableValues.Std().addValue("id", "idValue"));

        DtoWithOptional dto = reader.readValue(EMPTY_OBJECT);

        assertEquals("idValue", dto.id);
        assertNull(dto.optionalField);
    }

    void testMandatoryFieldNotFound() {
        MissingInjectableValueException exception = assertThrows(
                MissingInjectableValueException.class, () -> OPTIONAL_READER.readValue(EMPTY_OBJECT));

        assertTrue(exception.getMessage().startsWith(
                "No injectable value with id 'id' found (for property 'id')"));
    }

    void testRequiredAnnotatedField() throws Exception {
        ObjectReader reader = OPTIONAL_READER.forType(DtoWithRequired.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE);

        MissingInjectableValueException exception = assertThrows(
                MissingInjectableValueException.class, () -> reader.readValue(EMPTY_OBJECT));
        assertTrue(exception.getMessage().startsWith(
                "No injectable value with id 'requiredValue' found (for property 'requiredField')"));

        ObjectReader reader2 = reader.with(new InjectableValues.Std().addValue("id", "idValue"));
        exception = assertThrows(
                MissingInjectableValueException.class, () -> reader2.readValue(EMPTY_OBJECT));
        assertTrue(exception.getMessage().startsWith(
                "No injectable value with id 'requiredValue' found (for property 'requiredField')"));

        ObjectReader reader3 = reader.with(new InjectableValues.Std().addValue("requiredValue", "FOO"));
        DtoWithRequired req = reader3.readValue(EMPTY_OBJECT);
        assertEquals("FOO", req.requiredField);
    }

    void testMandatoryFieldNotFoundWithInjectableValues() {
        ObjectReader reader = OPTIONAL_READER.with(new InjectableValues.Std());

        MissingInjectableValueException exception = assertThrows(
                MissingInjectableValueException.class, () -> reader.readValue(EMPTY_OBJECT));

        assertTrue(exception.getMessage().startsWith(
                "No injectable value with id 'id' found (for property 'id')"));
    }

    void testMandatoryFieldNotFoundWithoutDeserializationFeature() throws Exception {
        ObjectReader reader = OPTIONAL_READER
                .with(new InjectableValues.Std().addValue("id", "idValue"))
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE);

        DtoWithOptional dto = reader.readValue(EMPTY_OBJECT);

        assertEquals("idValue", dto.id);
        assertNull(dto.optionalField);
    }

    void testMandatoryFieldNotFoundWithInjectableValuesWithoutDeserializationFeature() throws Exception {
        ObjectReader reader = OPTIONAL_READER
                .with(new InjectableValues.Std())
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE);

        DtoWithOptional dto = reader.readValue(EMPTY_OBJECT);

        assertNull(dto.id);
        assertNull(dto.optionalField);
    }

    void testOptionalFieldNotFoundWithoutInjectableValuesWithDeserializationFeature() throws Exception {
        ObjectReader reader = OPTIONAL_READER
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_INJECT_VALUE);

        DtoWithOptional dto = reader.readValue(EMPTY_OBJECT);

        assertNull(dto.id);
        assertNull(dto.optionalField);
    }

    
    void testBackwardCompatWithDeprecatedClassName() {
        MissingInjectableValueException exception = assertThrows(
                MissingInjectableValueException.class, () -> OPTIONAL_READER.readValue(EMPTY_OBJECT));
        assertTrue(exception instanceof MissingInjectableValueExcepion);
    }
static class Some {
        private String field1;

        @JacksonInject(value = "defaultValueForField2", useInput = OptBoolean.TRUE)
        private String field2;

        @JsonCreator
        public Some(@JsonProperty("field1") final String field1,
                    @JsonProperty("field2")
                    @JacksonInject(value = "defaultValueForField2", useInput = OptBoolean.TRUE)
                    final String field2) {
            this.field1 = Objects.requireNonNull(field1);
            this.field2 = Objects.requireNonNull(field2);
        }

        public String getField1() { return field1; }
        public String getField2() { return field2; }
    }
static class DtoWithOptional {
        @JacksonInject("id")
        String id;

        @JacksonInject(value = "optionalField", optional = OptBoolean.TRUE)
        String optionalField;
    }
static class DtoWithRequired {
        @JacksonInject(value = "requiredValue", optional = OptBoolean.FALSE)
        public String requiredField;
    }
static class Dto {
        @JacksonInject("id")
        String id;

        @JsonCreator
        Dto(@JacksonInject("id") @JsonProperty("id") String id) {
            this.id = id;
        }
    }
static class MyInjectableValues extends InjectableValues.Std {
        private static final long serialVersionUID = 1L;

        int nextId = 1;

        @Override
        public Object findInjectableValue(DeserializationContext ctxt, Object valueId,
                BeanProperty forProperty, Object beanInstance, Boolean optional, Boolean useInput) {
            if (valueId.equals("id")) {
                return "id" + nextId++;
            }
            return super.findInjectableValue(ctxt, valueId, forProperty, beanInstance, optional, useInput);
        }
    }

    void __invoke_testOptionalFieldFound() throws Exception {
        try {
            testOptionalFieldFound();
        } finally {
        }
    }


    void __invoke_testOptionalFieldNotFound() throws Exception {
        try {
            testOptionalFieldNotFound();
        } finally {
        }
    }


    void __invoke_testMandatoryFieldNotFound() throws Exception {
        try {
            testMandatoryFieldNotFound();
        } finally {
        }
    }


    void __invoke_testRequiredAnnotatedField() throws Exception {
        try {
            testRequiredAnnotatedField();
        } finally {
        }
    }


    void __invoke_testMandatoryFieldNotFoundWithInjectableValues() throws Exception {
        try {
            testMandatoryFieldNotFoundWithInjectableValues();
        } finally {
        }
    }


    void __invoke_testMandatoryFieldNotFoundWithoutDeserializationFeature() throws Exception {
        try {
            testMandatoryFieldNotFoundWithoutDeserializationFeature();
        } finally {
        }
    }


    void __invoke_testMandatoryFieldNotFoundWithInjectableValuesWithoutDeserializationFeature() throws Exception {
        try {
            testMandatoryFieldNotFoundWithInjectableValuesWithoutDeserializationFeature();
        } finally {
        }
    }


    void __invoke_testOptionalFieldNotFoundWithoutInjectableValuesWithDeserializationFeature() throws Exception {
        try {
            testOptionalFieldNotFoundWithoutInjectableValuesWithDeserializationFeature();
        } finally {
        }
    }


    void __invoke_testBackwardCompatWithDeprecatedClassName() throws Exception {
        try {
            testBackwardCompatWithDeprecatedClassName();
        } finally {
        }
    }

}
