package tools.jackson.databind.deser.inject;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0249F2 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] FIELD1_ONLY = VPackWireFixtureTest.hex(
            "14 16 46 66 69 65 6c 64 31 4b 66 69 65 6c 64 31 76 61 6c 75 65 01");
private static final byte[] BOTH_FIELDS = VPackWireFixtureTest.hex(
            "14 29 46 66 69 65 6c 64 31 4b 66 69 65 6c 64 31 76 61 6c 75 65 "
                    + "46 66 69 65 6c 64 32 4b 66 69 65 6c 64 32 76 61 6c 75 65 02");
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder().build();
private final ObjectReader OPTIONAL_READER = DEFAULT_MAPPER.readerFor(DtoWithOptional.class);

    void injectNoDups4218() throws Exception {
        ObjectReader reader = DEFAULT_MAPPER.readerFor(Dto.class)
                .with(new MyInjectableValues());

        Dto dto = reader.readValue(EMPTY_OBJECT);
        assertEquals("id1", dto.id);
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

    void __invoke_injectNoDups4218() throws Exception {
        try {
            injectNoDups4218();
        } finally {
        }
    }

}
