package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0162F1 {
private static final byte[] UPDATE_OBJECT = VPackWireFixtureTest.hex(
            "0b 10 03 41 78 33 41 79 34 41 77 28 6f 09 03 06");
private static final byte[] LOWERCASE_INPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 58 79 5a 03");
private static final byte[] LOWERCASE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testConvertValueNullPrimitive() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertEquals(Byte.valueOf((byte) 0), mapper.convertValue(null, Byte.TYPE));
        assertEquals(Short.valueOf((short) 0), mapper.convertValue(null, Short.TYPE));
        assertEquals(Integer.valueOf(0), mapper.convertValue(null, Integer.TYPE));
        assertEquals(Long.valueOf(0L), mapper.convertValue(null, Long.TYPE));
        assertEquals(Float.valueOf(0f), mapper.convertValue(null, Float.TYPE));
        assertEquals(Double.valueOf(0d), mapper.convertValue(null, Double.TYPE));
        assertEquals(Character.valueOf('\0'), mapper.convertValue(null, Character.TYPE));
        assertEquals(Boolean.FALSE, mapper.convertValue(null, Boolean.TYPE));
    }

    void testConvertValueNullBoxed() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertNull(mapper.convertValue(null, Byte.class));
        assertNull(mapper.convertValue(null, Short.class));
        assertNull(mapper.convertValue(null, Integer.class));
        assertNull(mapper.convertValue(null, Long.class));
        assertNull(mapper.convertValue(null, Float.class));
        assertNull(mapper.convertValue(null, Double.class));
        assertNull(mapper.convertValue(null, Character.class));
        assertNull(mapper.convertValue(null, Boolean.class));
    }
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_ARRAY,
            use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = Child.class))
    abstract static class Parent {
        public int x;
        public int y;
    }
@com.fasterxml.jackson.annotation.JsonTypeName("child")
    public static class Child extends Parent {
        public int w;
        public int h;
    }
static class LCConverter extends StdConverter<String, String> {
        @Override
        public String convert(String value) {
            return value.toLowerCase();
        }
    }
static class StringWrapperWithConvert {
        @JsonSerialize(converter = LCConverter.class)
        @JsonDeserialize(converter = LCConverter.class)
        public String value;

        protected StringWrapperWithConvert() { }

        StringWrapperWithConvert(String value) {
            this.value = value;
        }
    }

    void __invoke_testConvertValueNullPrimitive() throws Exception {
        try {
            testConvertValueNullPrimitive();
        } finally {
        }
    }


    void __invoke_testConvertValueNullBoxed() throws Exception {
        try {
            testConvertValueNullBoxed();
        } finally {
        }
    }

}
