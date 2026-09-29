package tools.jackson.dataformat.velocypack;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class VPackAttributeCodecConfigurationTest {
    @Test
    void builderAndRebuildRetainIndependentCodecSnapshots() {
        VPackAttributeNameCodec firstCodec = new Codec("first");
        VPackAttributeNameCodec secondCodec = new Codec("second");
        VPackFactoryBuilder builder = VPackFactory.builder().attributeNameCodec(firstCodec);
        VPackFactory first = builder.build();

        builder.attributeNameCodec(secondCodec);
        assertSame(firstCodec, first.attributeNameCodec());
        assertSame(secondCodec, builder.build().attributeNameCodec());

        VPackFactory rebuilt = first.rebuild().attributeNameCodec(secondCodec).build();
        assertSame(firstCodec, first.attributeNameCodec());
        assertSame(secondCodec, rebuilt.attributeNameCodec());
        assertNull(first.rebuild().attributeNameCodec(null).build().attributeNameCodec());
    }

    private record Codec(String name) implements VPackAttributeNameCodec {
        @Override
        public String decode(BigInteger unsignedId) {
            return name;
        }

        @Override
        public BigInteger encode(String name) {
            return null;
        }
    }
}
