package tools.jackson.databind.ser.jdk;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0594F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: NumberSerTest#testBigDecimal().
    void testBigDecimalVpack() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("pi", new BigDecimal("3.14159265"));
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 01 42 70 69 c8 05 f8 ff ff ff 03 14 15 92 65 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        Map<?, ?> decoded = MAPPER.readValue(expected, Map.class);
        assertEquals(new BigDecimal("3.14159265"), decoded.get("pi"));
        assertEquals(8, ((BigDecimal) decoded.get("pi")).scale());
    }

    // Provenance: NumberSerTest#testBigDecimalAsPlainString().
    void testBigDecimalAsPlainStringVpack() throws Exception {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("pi", new BigDecimal("3.00000000"));
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 12 01 42 70 69 c8 05 f8 ff ff ff 03 00 00 00 00 03");
        ObjectMapper plain = VPackMapper.builder()
                .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN).build();
        assertArrayEquals(expected, plain.writeValueAsBytes(input));
        Map<?, ?> decoded = MAPPER.readValue(expected, Map.class);
        assertEquals(new BigDecimal("3.00000000"), decoded.get("pi"));
        assertEquals(8, ((BigDecimal) decoded.get("pi")).scale());
    }

    // Provenance: NumberSerTest#defaultFloatVectorSerialization().
    void defaultFloatVectorSerializationVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "02 1d 1b 00 00 00 00 00 00 f0 3f"
              + "1b 00 00 00 00 00 00 e0 3f"
              + "1b 00 00 00 00 00 00 f4 bf");
        float[] input = { 1.0f, 0.5f, -1.25f };
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(input, MAPPER.readValue(expected, float[].class));
    }

    // Provenance: NumberSerTest#asArrayFloatVectorSerialization().
    void asArrayFloatVectorSerializationVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 28 01 46 76 65 63 74 6f 72"
              + "02 1d 1b 00 00 00 00 00 00 f0 3f"
              + "1b 00 00 00 00 00 00 e0 3f"
              + "1b 00 00 00 00 00 00 f4 bf 03");
        BeanWithArrayFloatVector input = new BeanWithArrayFloatVector(
                new float[] { 1.0f, 0.5f, -1.25f });
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(input.vector,
                MAPPER.readValue(expected, BeanWithArrayFloatVector.class).vector);

        ObjectMapper binaryOverride = VPackMapper.builder()
                .withConfigOverride(float[].class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.BINARY)))
                .build();
        assertArrayEquals(expected, binaryOverride.writeValueAsBytes(input));
    }

    // Provenance: NumberSerTest#defaultDoubleVectorSerialization().
    void defaultDoubleVectorSerializationVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "02 1d 1b 00 00 00 00 00 00 f0 bf"
              + "1b 00 00 00 00 00 00 f8 3f"
              + "1b 9a 99 99 99 99 99 89 3f");
        double[] input = { -1.0, 1.5, 0.0125 };
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(input, MAPPER.readValue(expected, double[].class));
    }

    // Provenance: NumberSerTest#asArrayDoubleVectorSerialization().
    void asArrayDoubleVectorSerializationVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 28 01 46 76 65 63 74 6f 72"
              + "02 1d 1b 00 00 00 00 00 00 f0 bf"
              + "1b 00 00 00 00 00 00 f8 3f"
              + "1b 9a 99 99 99 99 99 89 3f 03");
        BeanWithArrayDoubleVector input = new BeanWithArrayDoubleVector(
                new double[] { -1.0, 1.5, 0.0125 });
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(input.vector,
                MAPPER.readValue(expected, BeanWithArrayDoubleVector.class).vector);

        ObjectMapper binaryOverride = VPackMapper.builder()
                .withConfigOverride(double[].class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.BINARY)))
                .build();
        assertArrayEquals(expected, binaryOverride.writeValueAsBytes(input));
    }

    // Provenance: NumberSerTest#asBinaryFloatVectorSerializationRoot().
    void asBinaryFloatVectorSerializationRootVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "c0 0c 3f 80 00 00 3f 00 00 00 bf a0 00 00");
        ObjectMapper binary = VPackMapper.builder()
                .withConfigOverride(float[].class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.BINARY)))
                .build();
        assertArrayEquals(expected, binary.writeValueAsBytes(
                new float[] { 1.0f, 0.5f, -1.25f }));
        assertArrayEquals(new float[] { 1.0f, 0.5f, -1.25f },
                binary.readValue(expected, float[].class));
    }

    // Provenance: NumberSerTest#asBinaryFloatVectorSerializationPOJO().
    void asBinaryFloatVectorSerializationPOJOVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 19 01 46 76 65 63 74 6f 72"
              + "c0 0c 3f 80 00 00 3f 00 00 00 bf a0 00 00 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(
                new BeanWithBinaryFloatVector(new float[] { 1.0f, 0.5f, -1.25f })));
        assertArrayEquals(new float[] { 1.0f, 0.5f, -1.25f },
                MAPPER.readValue(expected, BeanWithArrayFloatVector.class).vector);
    }

    // Provenance: NumberSerTest#asBinaryDoubleVectorSerializationRoot().
    void asBinaryDoubleVectorSerializationRootVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "c0 18 bf f0 00 00 00 00 00 00"
              + "3f f8 00 00 00 00 00 00 3f 89 99 99 99 99 99 9a");
        ObjectMapper binary = VPackMapper.builder()
                .withConfigOverride(double[].class, cfg -> cfg.setFormat(
                        JsonFormat.Value.forShape(JsonFormat.Shape.BINARY)))
                .build();
        assertArrayEquals(expected, binary.writeValueAsBytes(
                new double[] { -1.0, 1.5, 0.0125 }));
        assertArrayEquals(new double[] { -1.0, 1.5, 0.0125 },
                binary.readValue(expected, double[].class));
    }

    // Provenance: NumberSerTest#asBinaryDoubleVectorSerializationPOJO().
    void asBinaryDoubleVectorSerializationPOJOVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 25 01 46 76 65 63 74 6f 72"
              + "c0 18 bf f0 00 00 00 00 00 00"
              + "3f f8 00 00 00 00 00 00 3f 89 99 99 99 99 99 9a 03");
        assertArrayEquals(expected, MAPPER.writeValueAsBytes(
                new BeanWithBinaryDoubleVector(new double[] { -1.0, 1.5, 0.0125 })));
        assertArrayEquals(new double[] { -1.0, 1.5, 0.0125 },
                MAPPER.readValue(expected, BeanWithBinaryDoubleVector.class).vector);
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

    void __invoke_testBigDecimalVpack() throws Exception {
        try {
            testBigDecimalVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalAsPlainStringVpack() throws Exception {
        try {
            testBigDecimalAsPlainStringVpack();
        } finally {
        }
    }


    void __invoke_defaultFloatVectorSerializationVpack() throws Exception {
        try {
            defaultFloatVectorSerializationVpack();
        } finally {
        }
    }


    void __invoke_asArrayFloatVectorSerializationVpack() throws Exception {
        try {
            asArrayFloatVectorSerializationVpack();
        } finally {
        }
    }


    void __invoke_defaultDoubleVectorSerializationVpack() throws Exception {
        try {
            defaultDoubleVectorSerializationVpack();
        } finally {
        }
    }


    void __invoke_asArrayDoubleVectorSerializationVpack() throws Exception {
        try {
            asArrayDoubleVectorSerializationVpack();
        } finally {
        }
    }


    void __invoke_asBinaryFloatVectorSerializationRootVpack() throws Exception {
        try {
            asBinaryFloatVectorSerializationRootVpack();
        } finally {
        }
    }


    void __invoke_asBinaryFloatVectorSerializationPOJOVpack() throws Exception {
        try {
            asBinaryFloatVectorSerializationPOJOVpack();
        } finally {
        }
    }


    void __invoke_asBinaryDoubleVectorSerializationRootVpack() throws Exception {
        try {
            asBinaryDoubleVectorSerializationRootVpack();
        } finally {
        }
    }


    void __invoke_asBinaryDoubleVectorSerializationPOJOVpack() throws Exception {
        try {
            asBinaryDoubleVectorSerializationPOJOVpack();
        } finally {
        }
    }

}
