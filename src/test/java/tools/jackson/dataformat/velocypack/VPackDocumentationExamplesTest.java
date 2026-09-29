package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** Compiles and exercises the public examples in the standalone module README. */
class VPackDocumentationExamplesTest {
    @Test
    void factoryMapperConstraintsAndRootSequence() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootValueBytes(8L * 1024 * 1024).build())
                .build();
        VPackMapper mapper = VPackMapper.builder(factory).build();
        byte[] first = mapper.writeValueAsBytes("hello");
        byte[] second = mapper.writeValueAsBytes(42);
        assertEquals("hello", mapper.readValue(first, Object.class));

        List<Integer> roots = new ArrayList<>();
        try (VPackGenerator generator = (VPackGenerator) factory.createGenerator(
                new java.io.ByteArrayOutputStream())) {
            generator.writeNumber(1);
            generator.writeNumber(2);
        }
        byte[] sequence = new byte[first.length + second.length];
        System.arraycopy(first, 0, sequence, 0, first.length);
        System.arraycopy(second, 0, sequence, first.length, second.length);
        try (VPackParser parser = (VPackParser) factory.createParser(sequence)) {
            while (parser.nextToken() != null) {
                if (parser.currentToken().isNumeric()) roots.add(parser.getIntValue());
            }
        }
        assertEquals(List.of(42), roots);
    }

    @Test
    void featureCodecAndNativeWrappers() throws Exception {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) { return id.equals(BigInteger.ONE) ? "name" : null; }
            @Override public BigInteger encode(String name) { return "name".equals(name) ? BigInteger.ONE : null; }
        };
        VPackFactory factory = VPackFactory.builder().attributeNameCodec(codec)
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS).build();
        VPackMapper mapper = new VPackMapper(factory);
        VPackDate date = new VPackDate(123456789L);
        assertEquals(date, mapper.readValue(mapper.writeValueAsBytes(date), VPackDate.class));
        assertEquals(VPackSpecialValue.MAX_KEY,
                mapper.readValue(mapper.writeValueAsBytes(VPackSpecialValue.MAX_KEY), VPackSpecialValue.class));
        assertInstanceOf(VPackFactory.class, mapper.tokenStreamFactory());
        assertEquals("name", codec.decode(codec.encode("name")));
    }
}
