package tools.jackson.databind.convert;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.util.StdConverter;
import tools.jackson.databind.util.TokenBuffer;
import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0156F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] INTERFACE_CONVERTER_INPUT = VPackWireFixtureTest.hex(
            "14 0d 45 66 69 65 6c 64 43 66 6f 6f 01");
private static final byte[] ABSTRACT_CONVERTER_INPUT = VPackWireFixtureTest.hex(
            "14 1c 4b 63 75 73 74 6f 6d 46 69 65 6c 64 "
            + "4c 63 75 73 74 6f 6d 53 74 72 69 6e 67 01");

    void testConvertTokenBufferToBean() throws Exception {
        TokenBuffer buffer = beanToBuffer(42, "test");
        SimpleBean result = MAPPER.convertValue(buffer, SimpleBean.class);
        assertEquals(42, result.x);
        assertEquals("test", result.name);
    }

    void testConvertTokenBufferToJavaType() throws Exception {
        TokenBuffer buffer = beanToBuffer(123, "javatype");
        JavaType type = MAPPER.getTypeFactory().constructType(SimpleBean.class);
        SimpleBean result = MAPPER.convertValue(buffer, type);
        assertEquals(123, result.x);
        assertEquals("javatype", result.name);
    }

    void testConvertTokenBufferToTypeReference() throws Exception {
        TokenBuffer buffer = beanToBuffer(456, "foobar");
        SimpleBean result = MAPPER.convertValue(buffer,
                new TypeReference<SimpleBean>() { });
        assertEquals(456, result.x);
        assertEquals("foobar", result.name);
    }

    void testConvertTokenBufferToMap() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeStringProperty("key1", "value1");
        buffer.writeNumberProperty("key2", 42);
        buffer.writeEndObject();
        buffer.close();

        @SuppressWarnings("unchecked")
        Map<String, Object> result = MAPPER.convertValue(buffer, Map.class);

        assertNotNull(result);
        assertEquals("value1", result.get("key1"));
        assertEquals(42, result.get("key2"));
    }

    void testConvertNullTokenBuffer() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeNull();
        buffer.close();

        SimpleBean result = MAPPER.convertValue(buffer, SimpleBean.class);
        assertNull(result);
    }

    void testConvertTokenBufferArray() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartArray();
        buffer.writeStartObject();
        buffer.writeNumberProperty("x", 1);
        buffer.writeStringProperty("name", "first");
        buffer.writeEndObject();
        buffer.writeStartObject();
        buffer.writeNumberProperty("x", 2);
        buffer.writeStringProperty("name", "second");
        buffer.writeEndObject();
        buffer.writeEndArray();
        buffer.close();

        SimpleBean[] result = MAPPER.convertValue(buffer, SimpleBean[].class);

        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals(1, result[0].x);
        assertEquals("first", result[0].name);
        assertEquals(2, result[1].x);
        assertEquals("second", result[1].name);
    }

    void testTokenBufferReusableAfterConvert() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeNumberProperty("x", 99);
        buffer.writeStringProperty("name", "reusable");
        buffer.writeEndObject();
        buffer.close();

        SimpleBean first = MAPPER.convertValue(buffer, SimpleBean.class);
        assertNotNull(first);
        assertEquals(99, first.x);

        SimpleBean second = MAPPER.convertValue(buffer, SimpleBean.class);
        assertNotNull(second);
        assertEquals(99, second.x);
        assertEquals("reusable", second.name);
    }

    void testConvertTokenBufferWithBigDecimalFeature() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();

        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeStringProperty("value", "test");
        buffer.writeEndObject();
        buffer.close();

        @SuppressWarnings("unchecked")
        Map<String, Object> result = mapper.convertValue(buffer, Map.class);

        assertNotNull(result);
        assertEquals("test", result.get("value"));
    }

    void testConvertFromRegularObjectStillWorks() {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("x", 77);
        input.put("name", "regular");

        SimpleBean result = MAPPER.convertValue(input, SimpleBean.class);

        assertNotNull(result);
        assertEquals(77, result.x);
        assertEquals("regular", result.name);
    }

    void testConvertEmptyTokenBuffer() throws Exception {
        TokenBuffer buffer = TokenBuffer.forGeneration();
        buffer.writeStartObject();
        buffer.writeEndObject();
        buffer.close();

        SimpleBean result = MAPPER.convertValue(buffer, SimpleBean.class);

        assertNotNull(result);
        assertEquals(0, result.x);
        assertNull(result.name);
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

    void __invoke_testConvertTokenBufferToBean() throws Exception {
        try {
            testConvertTokenBufferToBean();
        } finally {
        }
    }


    void __invoke_testConvertTokenBufferToJavaType() throws Exception {
        try {
            testConvertTokenBufferToJavaType();
        } finally {
        }
    }


    void __invoke_testConvertTokenBufferToTypeReference() throws Exception {
        try {
            testConvertTokenBufferToTypeReference();
        } finally {
        }
    }


    void __invoke_testConvertTokenBufferToMap() throws Exception {
        try {
            testConvertTokenBufferToMap();
        } finally {
        }
    }


    void __invoke_testConvertNullTokenBuffer() throws Exception {
        try {
            testConvertNullTokenBuffer();
        } finally {
        }
    }


    void __invoke_testConvertTokenBufferArray() throws Exception {
        try {
            testConvertTokenBufferArray();
        } finally {
        }
    }


    void __invoke_testTokenBufferReusableAfterConvert() throws Exception {
        try {
            testTokenBufferReusableAfterConvert();
        } finally {
        }
    }


    void __invoke_testConvertTokenBufferWithBigDecimalFeature() throws Exception {
        try {
            testConvertTokenBufferWithBigDecimalFeature();
        } finally {
        }
    }


    void __invoke_testConvertFromRegularObjectStillWorks() throws Exception {
        try {
            testConvertFromRegularObjectStillWorks();
        } finally {
        }
    }


    void __invoke_testConvertEmptyTokenBuffer() throws Exception {
        try {
            testConvertEmptyTokenBuffer();
        } finally {
        }
    }

}
