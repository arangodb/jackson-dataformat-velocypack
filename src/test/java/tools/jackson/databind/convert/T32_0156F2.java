package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.util.StdConverter;
import tools.jackson.databind.util.TokenBuffer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0156F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INTERFACE_CONVERTER_INPUT = VPackWireFixtureTest.hex(
            "14 0d 45 66 69 65 6c 64 43 66 6f 6f 01");
private static final byte[] ABSTRACT_CONVERTER_INPUT = VPackWireFixtureTest.hex(
            "14 1c 4b 63 75 73 74 6f 6d 46 69 65 6c 64 "
            + "4c 63 75 73 74 6f 6d 53 74 72 69 6e 67 01");

    void testAbstractTypeDeserialization() throws Exception {
        AbstractCustomTypeUser value = MAPPER.readValue(ABSTRACT_CONVERTER_INPUT,
                AbstractCustomTypeUser.class);
        assertNotNull(value);
    }
private static TokenBuffer beanToBuffer(int x, String name) throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeNumberProperty("x", x);
        buffer.writeStringProperty("name", name);
        buffer.writeEndObject();
        buffer.close();
        return buffer;
    }
static class SimpleBean {
        public int x;
        public String name;
        public SimpleBean() { }
    }
@JsonDeserialize(converter = FromConverter.class)
    static class Concrete {
        private String field;
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
    }
@JsonDeserialize(as = FromImpl.class)
    interface From {
        String field();
    }
static class FromImpl implements From {
        @JsonProperty
        private String field;
        @Override
        public String field() { return field; }
    }
static class FromConverter extends StdConverter<From, Concrete> {
        @Override
        public Concrete convert(From value) {
            Concrete result = new Concrete();
            result.setField(value.field());
            return result;
        }
    }
public static abstract class AbstractCustomType {
        final String value;
        public AbstractCustomType(String value) { this.value = value; }
    }
public static class ConcreteCustomType extends AbstractCustomType {
        public ConcreteCustomType(String value) { super(value); }
    }
public static class AbstractCustomTypeDeserializationConverter
            extends StdConverter<String, AbstractCustomType> {
        @Override
        public AbstractCustomType convert(String value) {
            return new ConcreteCustomType(value);
        }
    }
public static class AbstractCustomTypeUser {
        @JsonProperty
        @JsonDeserialize(converter = AbstractCustomTypeDeserializationConverter.class)
        protected AbstractCustomType customField;

        public AbstractCustomTypeUser(@JsonProperty("customField") AbstractCustomType value) {
            this.customField = value;
        }
    }

    void __invoke_testAbstractTypeDeserialization() throws Exception {
        try {
            testAbstractTypeDeserialization();
        } finally {
        }
    }

}
