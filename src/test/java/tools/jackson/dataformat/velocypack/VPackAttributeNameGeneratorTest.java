package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackAttributeNameGeneratorTest {
    @Test
    void rejectsAnAttributeIdThatDoesNotResolveBackToTheName() {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) { return "different"; }
            @Override public BigInteger encode(String name) { return BigInteger.ONE; }
        };
        assertThrows(StreamWriteException.class, () -> {
            try (JsonGenerator generator = VPackFactory.builder().attributeNameCodec(codec)
                    .build().createGenerator(new ByteArrayOutputStream())) {
                generator.writeStartObject();
                generator.writeName("name");
            }
        });
    }

    @Test
    void fallsBackToAStringWhenTheCodecReturnsNull() throws Exception {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) { return null; }
            @Override public BigInteger encode(String name) { return null; }
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder().attributeNameCodec(codec)
                .build().createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("name");
            generator.writeNull();
            generator.writeEndObject();
        }
        org.junit.jupiter.api.Assertions.assertEquals(0x44, out.toByteArray()[3] & 0xFF);
    }

    @Test
    void rejectsOutOfRangeIdsBeforeWritingAKey() {
        VPackAttributeNameCodec codec = new VPackAttributeNameCodec() {
            @Override public String decode(BigInteger id) { return "name"; }
            @Override public BigInteger encode(String name) {
                return BigInteger.ONE.shiftLeft(64);
            }
        };
        assertThrows(StreamWriteException.class, () -> {
            try (JsonGenerator generator = VPackFactory.builder().attributeNameCodec(codec)
                    .build().createGenerator(new ByteArrayOutputStream())) {
                generator.writeStartObject();
                generator.writeName("name");
            }
        });
    }

    @Test
    void chargesResolvedNameBytesBeforeAppendingTheNextKey() throws Exception {
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder().maxRootNameBytes(3).build())
                .build().createGenerator(new ByteArrayOutputStream());
        generator.writeStartObject();
        generator.writeName("ab");
        generator.writeNull();
        assertThrows(StreamConstraintsException.class, () -> generator.writeName("cd"));
        assertThrows(StreamConstraintsException.class, generator::close);
    }
}
