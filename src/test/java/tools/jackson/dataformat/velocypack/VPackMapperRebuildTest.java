package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackMapperRebuildTest {
    @Test
    void independentBuildersDoNotShareFormatFeatureMutation() {
        VPackMapper.Builder firstBuilder = VPackMapper.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS);
        VPackMapper second = VPackMapper.builder().build();
        VPackMapper first = firstBuilder.build();

        assertEquals(false, first.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertEquals(true, second.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
    }

    @Test
    void rebuildRetainsFactoryAndNativeModule() {
        VPackFactory factory = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootEntries(7).build())
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxNumberDigits(13).build())
                .build();
        VPackMapper original = VPackMapper.builder(factory)
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS)
                .build();

        VPackMapper rebuilt = original.rebuild().build();

        assertNotSame(original, rebuilt);
        assertSame(factory, rebuilt.tokenStreamFactory());
        assertEquals(factory.vpackReadConstraints(), rebuilt.tokenStreamFactory().vpackReadConstraints());
        assertEquals(factory.vpackWriteConstraints(), rebuilt.tokenStreamFactory().vpackWriteConstraints());
        assertEquals(false, rebuilt.isEnabled(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS));
        assertEquals(1, rebuilt.registeredModules().size());
    }

    @Test
    void mapperJavaSerializationRestoresNativeModuleAndFactory() throws Exception {
        VPackMapper original = new VPackMapper();

        VPackMapper restored = (VPackMapper) deserialize(serialize(original));

        assertEquals(1, restored.registeredModules().size());
        assertEquals(new VPackDate(11L),
                restored.readValue(restored.writeValueAsBytes(new VPackDate(11L)), VPackDate.class));
    }

    @Test
    void nonserializableAttributeCodecFailsInsteadOfBeingDropped() {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override
            public String decode(BigInteger unsignedId) { return "name"; }

            @Override
            public BigInteger encode(String name) { return BigInteger.ONE; }
        };
        VPackMapper mapper = VPackMapper.builder(VPackFactory.builder()
                .attributeNameCodec(codec).build()).build();

        assertThrows(NotSerializableException.class, () -> serialize(mapper));
    }

    @Test
    void serializableAttributeCodecSurvivesMapperSerialization() throws Exception {
        VPackMapper original = VPackMapper.builder(VPackFactory.builder()
                .attributeNameCodec(new SerializableCodec()).build()).build();

        VPackMapper restored = (VPackMapper) deserialize(serialize(original));

        assertEquals("name", restored.tokenStreamFactory().attributeNameCodec()
                .decode(BigInteger.ONE));
    }

    private static final class SerializableCodec
            implements VPackAttributeNameCodec, java.io.Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public String decode(BigInteger unsignedId) { return "name"; }

        @Override
        public BigInteger encode(String name) { return BigInteger.ONE; }
    }

    private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return input.readObject();
        }
    }
}
