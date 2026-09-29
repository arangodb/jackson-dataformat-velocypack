package tools.jackson.core.unittest.constraints;

import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0009Fixture {

    void nonBlockingParserIsAnExplicitlyUnsupportedVpackCapability() {
        VPackFactory factory = new VPackFactory();

        assertFalse(factory.canParseAsync());
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteArrayParser(ObjectReadContext.empty()));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteBufferParser(ObjectReadContext.empty()));
    }

    void __invoke_nonBlockingParserIsAnExplicitlyUnsupportedVpackCapability() throws Exception {
        try {
            nonBlockingParserIsAnExplicitlyUnsupportedVpackCapability();
        } finally {
        }
    }

}
