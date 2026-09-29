package tools.jackson.core.unittest.constraints;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.StreamWriteConstraints;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0013F1 {
private static final byte[] ARRAY_DOCUMENT = {
            0x14, 0x16,
            0x44, 'n', 'u', 'm', 's',
            0x13, 0x0E,
            0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
            0x28, 0x0A,
            0x0A,
            0x01
    };
private static final byte[] SHORT_ARRAY_DOCUMENT = {
            0x14, 0x0E,
            0x44, 'n', 'u', 'm', 's',
            0x13, 0x06, 0x31, 0x32, 0x33, 0x03,
            0x01
    };
private static final byte[] SAMPLE_DOCUMENT = {
            0x14, 0x58,
            0x45, 'I', 'm', 'a', 'g', 'e',
            0x14, 0x4F,
            0x45, 'W', 'i', 'd', 't', 'h', 0x31,
            0x46, 'H', 'e', 'i', 'g', 'h', 't', 0x32,
            0x45, 'T', 'i', 't', 'l', 'e', 0x41, 'x',
            0x49, 'T', 'h', 'u', 'm', 'b', 'n', 'a', 'i', 'l',
            0x14, 0x19,
            0x43, 'U', 'r', 'l', 0x41, 'u',
            0x46, 'H', 'e', 'i', 'g', 'h', 't', 0x33,
            0x45, 'W', 'i', 'd', 't', 'h', 0x41, '4',
            0x03,
            0x43, 'I', 'D', 's',
            0x13, 0x0E,
            0x28, 116,
            0x29, (byte) 0xAF, 0x03,
            0x29, (byte) 0xEA, 0x00,
            0x29, (byte) 0x89, (byte) 0x97,
            0x04,
            0x05,
            0x01
    };

    void streamWriteDefaultsOverrideIsUsedByVpackFactoryAndGenerator() throws Exception {
        StreamWriteConstraints previous = StreamWriteConstraints.defaults();
        final int depth = 123;
        StreamWriteConstraints constraints = StreamWriteConstraints.builder()
                .maxNestingDepth(depth)
                .build();
        try {
            StreamWriteConstraints.overrideDefaultStreamWriteConstraints(constraints);
            assertSame(constraints, StreamWriteConstraints.defaults());
            VPackFactory factory = new VPackFactory();
            assertSame(constraints, factory.streamWriteConstraints());
            try (JsonGenerator generator = factory.createGenerator(
                    ObjectWriteContext.empty(), new java.io.ByteArrayOutputStream())) {
                assertSame(constraints, generator.streamWriteConstraints());
            }
        } finally {
            StreamWriteConstraints.overrideDefaultStreamWriteConstraints(null);
            assertEquals(StreamWriteConstraints.DEFAULT_MAX_DEPTH,
                    StreamWriteConstraints.defaults().getMaxNestingDepth());
            if (previous != StreamWriteConstraints.defaults()) {
                StreamWriteConstraints.overrideDefaultStreamWriteConstraints(previous);
            }
        }
    }
private static void assertTokenCount(byte[] input, long expected) throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxTokenCount(Long.MAX_VALUE).build())
                .build();

        try (JsonParser parser = factory.createParser(input)) {
            assertEquals(0L, parser.currentTokenCount());
            consume(parser);
            assertEquals(expected, parser.currentTokenCount());
        }
        try (JsonParser parser = factory.createParser(oneByteAtATime(input))) {
            assertEquals(0L, parser.currentTokenCount());
            consume(parser);
            assertEquals(expected, parser.currentTokenCount());
        }
        try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                (DataInput) new DataInputStream(new ByteArrayInputStream(input)))) {
            assertEquals(0L, parser.currentTokenCount());
            consume(parser);
            assertEquals(expected, parser.currentTokenCount());
        }
    }
private static void consume(JsonParser parser) throws Exception {
        while (parser.nextToken() != null) {
            // Consume the complete root so END tokens are counted too.
        }
    }
private static InputStream oneByteAtATime(byte[] input) {
        return new ByteArrayInputStream(input) {
            @Override
            public int read(byte[] buffer, int offset, int length) {
                return super.read(buffer, offset, Math.min(length, 1));
            }
        };
    }

    void __invoke_streamWriteDefaultsOverrideIsUsedByVpackFactoryAndGenerator() throws Exception {
        try {
            streamWriteDefaultsOverrideIsUsedByVpackFactoryAndGenerator();
        } finally {
        }
    }

}
