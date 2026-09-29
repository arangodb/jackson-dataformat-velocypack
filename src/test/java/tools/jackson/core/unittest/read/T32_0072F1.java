package tools.jackson.core.unittest.read;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.exc.StreamReadException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0072F1 {
private static final byte[] NEXT_TEXT = {
            0x14, 0x16,
            0x41, 'a', 0x43, '1', '2', '3',
            0x41, 'b', 0x35,
            0x41, 'c', 0x13, 0x08, 0x19, 0x43, 'f', 'o', 'o', 0x02,
            0x03
    };
private static final byte[][] SINGLE_QUOTED_JSON = {
            {'[', ' ', '\'', 't', 'e', 'x', 't', '\'', ' ', ']'},
            {'{', ' ', '\'', 'a', '\'', ':', '1', ' ', '}'},
            {'[', ' ', '\'', '1', '6', '\\', '\'', '\'', ' ', ']'},
            {'{', ' ', '\'', '"', '"', '\'', ':', ' ', '\'', 'v', 'a', 'l', 'u', 'e', '\'', '}'},
            {'{', '"', 'b', 'a', 'r', '"', ':', ' ', '\'', '"', 's', 't', 'u', 'f', 'f', '"', '\'', '}' }
    };

    void singleQuoteJsonSyntaxIsRejectedAsNonVpackInput() throws Exception {
        VPackFactory factory = new VPackFactory();
        for (byte[] text : SINGLE_QUOTED_JSON) {
            try (JsonParser parser = factory.createParser(text)) {
                assertThrows(StreamReadException.class, parser::nextToken);
            }
        }
    }
private static void forEachBinarySource(byte[] document, ParserAssertions assertions)
            throws Exception {
        VPackFactory factory = new VPackFactory();
        try (JsonParser parser = factory.createParser(document)) {
            assertions.check(parser);
        }
        try (JsonParser parser = factory.createParser(new OneByteInputStream(document))) {
            assertions.check(parser);
        }
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(), input)) {
            assertions.check(parser);
        }
    }
@FunctionalInterface
    private interface ParserAssertions {
        void check(JsonParser parser) throws Exception;
    }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        private OneByteInputStream(byte[] input) {
            delegate = new ByteArrayInputStream(input);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public int read(byte[] target, int offset, int length) {
            if (length == 0) {
                return 0;
            }
            int value = delegate.read();
            if (value < 0) {
                return -1;
            }
            target[offset] = (byte) value;
            return 1;
        }
    }

    void __invoke_singleQuoteJsonSyntaxIsRejectedAsNonVpackInput() throws Exception {
        try {
            singleQuoteJsonSyntaxIsRejectedAsNonVpackInput();
        } finally {
        }
    }

}
