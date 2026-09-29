package tools.jackson.core.unittest.json.async;

import tools.jackson.core.ObjectReadContext;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0045Fixture {

    void assignedAsyncSourcesAreExplicitlyUnsupportedByVpack() {
        VPackFactory factory = new VPackFactory();

        assertFalse(factory.canParseAsync());
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteArrayParser(ObjectReadContext.empty()));
        assertThrows(UnsupportedOperationException.class,
                () -> factory.createNonBlockingByteBufferParser(ObjectReadContext.empty()));
    }

    void __invoke_assignedAsyncSourcesAreExplicitlyUnsupportedByVpack() throws Exception {
        try {
            assignedAsyncSourcesAreExplicitlyUnsupportedByVpack();
        } finally {
        }
    }

}
