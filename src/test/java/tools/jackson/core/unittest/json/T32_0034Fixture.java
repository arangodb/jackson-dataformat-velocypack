package tools.jackson.core.unittest.json;

import tools.jackson.core.exc.StreamReadException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0034Fixture {

    void additionalByteArrayBoundsCasesRemainReadErrors() {
        VPackFactory factory = new VPackFactory();
        byte[] data = new byte[10];

        // Literal source ranges from BoundsChecksWithJsonFactoryTest. The
        // VPack factory validates the range before any wire byte is read.
        assertThrows(StreamReadException.class,
                () -> factory.createParser(data, 9, 5));
        assertThrows(StreamReadException.class,
                () -> factory.createParser(data, Integer.MAX_VALUE, 4));
        assertThrows(StreamReadException.class,
                () -> factory.createParser(data, Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    void __invoke_additionalByteArrayBoundsCasesRemainReadErrors() throws Exception {
        try {
            additionalByteArrayBoundsCasesRemainReadErrors();
        } finally {
        }
    }

}
