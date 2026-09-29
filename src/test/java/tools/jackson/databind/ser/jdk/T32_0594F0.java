package tools.jackson.databind.ser.jdk;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0594F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: MapSerializationTest#testUsingObjectWriter().
    void testUsingObjectWriterVpack() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        byte[] expected = VPackWireFixtureTest.hex("0b 07 01 41 61 31 03");

        assertArrayEquals(expected, MAPPER.writerFor(Object.class).writeValueAsBytes(map));
        Map<?, ?> decoded = MAPPER.readValue(expected, Map.class);
        assertEquals(1, decoded.get("a"));
    }

    // Provenance: MapSerializationTest#testUnWrappedMapWithKeySerializer().
    void testUnWrappedMapWithKeySerializerVpack() throws Exception {
        SimpleModule module = new SimpleModule("test");
        module.addKeySerializer(ABCKey.class, new ABCKeySerializer());
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        Map<ABCKey, BAR<?>> input = new LinkedHashMap<>();
        input.put(ABCKey.B, new BAR<String>("bar"));

        byte[] expected = VPackWireFixtureTest.hex(
                "0b 0d 01 44 78 78 78 42 43 62 61 72 03");
        assertArrayEquals(expected, mapper.writerFor(
                new TypeReference<Map<ABCKey, BAR<?>>>() { }).writeValueAsBytes(input));
        Map<?, ?> decoded = MAPPER.readValue(expected, Map.class);
        assertEquals("bar", decoded.get("xxxB"));
    }
enum ABCKey { A, B, C }
static class ABCKeySerializer extends ValueSerializer<ABCKey> {
        @Override
        public void serialize(ABCKey value, JsonGenerator generator,
                SerializationContext context) {
            generator.writeName("xxx" + value);
        }
    }
static class BAR<T> {
        private final T value;

        BAR(T value) { this.value = value; }

        @com.fasterxml.jackson.annotation.JsonValue
        public T getValue() { return value; }
    }
static class BeanWithArrayFloatVector {
        @JsonFormat(shape = JsonFormat.Shape.NATURAL)
        public float[] vector;

        BeanWithArrayFloatVector() { }
        BeanWithArrayFloatVector(float[] vector) { this.vector = vector; }
    }
static class BeanWithArrayDoubleVector {
        @JsonFormat(shape = JsonFormat.Shape.NATURAL)
        public double[] vector;

        BeanWithArrayDoubleVector() { }
        BeanWithArrayDoubleVector(double[] vector) { this.vector = vector; }
    }
static class BeanWithBinaryFloatVector {
        @JsonFormat(shape = JsonFormat.Shape.BINARY)
        public float[] vector;

        BeanWithBinaryFloatVector() { }
        BeanWithBinaryFloatVector(float[] vector) { this.vector = vector; }
    }
static class BeanWithBinaryDoubleVector {
        @JsonFormat(shape = JsonFormat.Shape.BINARY)
        public double[] vector;

        BeanWithBinaryDoubleVector() { }
        BeanWithBinaryDoubleVector(double[] vector) { this.vector = vector; }
    }

    void __invoke_testUsingObjectWriterVpack() throws Exception {
        try {
            testUsingObjectWriterVpack();
        } finally {
        }
    }


    void __invoke_testUnWrappedMapWithKeySerializerVpack() throws Exception {
        try {
            testUnWrappedMapWithKeySerializerVpack();
        } finally {
        }
    }

}
