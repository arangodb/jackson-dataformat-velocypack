package tools.jackson.core.unittest.sym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0101Fixture {
private static final int DEFAULT_FEATURES = JsonFactory.Feature.collectDefaults();
private static final byte[] BYTE_BASED_SYMBOL_OBJECT = {
            0x14, 0x4E,
            0x43, 'a', 'b', 'c', 0x31,
            0x44, 'a', 'b', 'c', 0x00, 0x32,
            0x44, 0x00, 'a', 'b', 'c', 0x33,
            0x46, 'a', 'b', 'c', '1', '2', '3', 0x34,
            0x48, 'a', 'b', 'c', 'd', '1', '2', '3', '4', 0x35,
            0x49, 'a', 'b', 'c', 'd', '1', '2', '3', '4', 'a', 0x36,
            0x4C, 'a', 'b', 'c', 'd', '1', '2', '3', '4', 'a', 'b', 'c', 'd', 0x37,
            0x4D, 'a', 'b', 'c', 'd', '1', '2', '3', '4', 'a', 'b', 'c', 'd', '1', 0x38,
            0x08
    };

    void byteBasedSymbolTableRetainsShortMediumAndLongNames() throws Exception {
        VPackFactory factory = new VPackFactory();
        for (int pass = 0; pass < 3; ++pass) {
            try (VPackParser parser = (VPackParser) factory.createParser(
                    ObjectReadContext.empty(), BYTE_BASED_SYMBOL_OBJECT)) {
                while (parser.nextToken() != null) {
                    // Drain the complete root so its child symbols can merge.
                }
            }
            assertEquals(8, VPackTestAccess.byteSymbolCanonicalizer(factory).size());
        }
    }

    void syntheticByteSymbolsRetainSourceDistribution() {
        ByteQuadsCanonicalizer symbols = seededChild(33333);
        assertTrue(symbols.isCanonicalizing());
        for (int i = 0; i < 12000; ++i) {
            String id = fieldNameFor(i);
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(12000, symbols.size());
        assertEquals(16384, symbols.bucketCount());
        assertEquals(8534, symbols.primaryCount());
        assertEquals(2534, symbols.secondaryCount());
        assertEquals(932, symbols.tertiaryCount());
        assertEquals(0, symbols.spilloverCount());
    }

    void numericByteCollisions187aRetainSourceDistribution() {
        ByteQuadsCanonicalizer symbols = seededChild(1);
        for (int i = 0; i < 43000; ++i) {
            String id = Integer.toString(10000 + i);
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(43000, symbols.size());
        assertEquals(65536, symbols.bucketCount());
        assertEquals(32342, symbols.primaryCount());
        assertEquals(8863, symbols.secondaryCount());
        assertEquals(1795, symbols.tertiaryCount());
        assertEquals(0, symbols.spilloverCount());
    }

    void numericByteCollisions187bRetainSourceDistribution() {
        ByteQuadsCanonicalizer symbols = seededChild(1);
        for (int i = 0; i < 10000; ++i) {
            String id = Integer.toString(i);
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(10000, symbols.size());
        assertEquals(16384, symbols.bucketCount());
        assertEquals(5402, symbols.primaryCount());
        assertEquals(2744, symbols.secondaryCount());
        assertEquals(1834, symbols.tertiaryCount());
        assertEquals(20, symbols.spilloverCount());
    }

    void shortNameCollisionsViaByteParserRetainAllNames() throws Exception {
        byte[] document = shortNameDocument();
        assertShortNameDocument(new VPackFactory().createParser(document));
        assertShortNameDocument(new VPackFactory().createParser(
                ObjectReadContext.empty(), new OneByteInputStream(document)));
    }

    void shortQuotedByteSymbolsRetainSourceDistribution() {
        ByteQuadsCanonicalizer symbols = seededChild(123);
        for (int i = 0; i < 400; ++i) {
            String id = String.format("\\u%04x", i);
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(400, symbols.size());
        assertEquals(512, symbols.bucketCount());
        assertEquals(285, symbols.primaryCount());
        assertEquals(90, symbols.secondaryCount());
        assertEquals(25, symbols.tertiaryCount());
        assertEquals(0, symbols.spilloverCount());
    }

    void shortDirectByteSymbolsRetainSourceDistribution() {
        ByteQuadsCanonicalizer symbols = seededChild(333);
        for (int i = 0; i < 700; ++i) {
            String id = String.valueOf((char) i);
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(700, symbols.size());
        assertEquals(1024, symbols.bucketCount());
        assertEquals(564, symbols.primaryCount());
        assertEquals(122, symbols.secondaryCount());
        assertEquals(14, symbols.tertiaryCount());
        assertEquals(0, symbols.spilloverCount());
        assertEquals(700, symbols.primaryCount() + symbols.secondaryCount()
                + symbols.tertiaryCount() + symbols.spilloverCount());
    }

    void seventeenByteSymbolsRetainAllNames() {
        ByteQuadsCanonicalizer symbols = seededChild(3);
        for (int i = 1001; i <= 1050; ++i) {
            String id = "lengthmatters" + i;
            int[] quads = calcQuads(id.getBytes(StandardCharsets.UTF_8));
            symbols.addName(id, quads, quads.length);
        }
        assertEquals(50, symbols.size());
    }
private static ByteQuadsCanonicalizer seededChild(int seed) {
        return new SeededCanonicalizer(seed).makeChild(DEFAULT_FEATURES);
    }
private static void assertShortNameDocument(JsonParser parser) throws Exception {
        try (parser) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (int i = 0; i < 400; ++i) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals(String.valueOf((char) i), parser.currentName());
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getIntValue());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static byte[] shortNameDocument() {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (int i = 0; i < 400; ++i) {
            byte[] name = String.valueOf((char) i).getBytes(StandardCharsets.UTF_8);
            body.write(0x40 + name.length);
            body.writeBytes(name);
            if (i < 10) {
                body.write(0x30 + i);
            } else if (i < 256) {
                body.write(0x28);
                body.write(i);
            } else {
                body.write(0x29);
                body.write(i);
                body.write(i >>> 8);
            }
        }
        byte[] bodyBytes = body.toByteArray();
        byte[] length = VPackWireFixtureAssembler.forwardVarint(
                1L + VPackWireFixtureAssembler.forwardVarint(bodyBytes.length + 3L).length
                        + bodyBytes.length
                        + VPackWireFixtureAssembler.reverseVarint(400).length);
        int totalLength = 1 + length.length + bodyBytes.length
                + VPackWireFixtureAssembler.reverseVarint(400).length;
        while (length.length != VPackWireFixtureAssembler.forwardVarint(totalLength).length) {
            length = VPackWireFixtureAssembler.forwardVarint(totalLength);
            totalLength = 1 + length.length + bodyBytes.length
                    + VPackWireFixtureAssembler.reverseVarint(400).length;
        }
        byte[] count = VPackWireFixtureAssembler.reverseVarint(400);
        byte[] result = new byte[totalLength];
        int offset = 0;
        result[offset++] = 0x14;
        System.arraycopy(length, 0, result, offset, length.length);
        offset += length.length;
        System.arraycopy(bodyBytes, 0, result, offset, bodyBytes.length);
        offset += bodyBytes.length;
        System.arraycopy(count, 0, result, offset, count.length);
        return result;
    }
private static String fieldNameFor(int index) {
        StringBuilder sb = new StringBuilder(16);
        sb.append('f').append(index);
        if (index > 50) {
            sb.append('.');
            if (index > 200) {
                sb.append(index);
                if (index > 4000) {
                    sb.append('.').append(index);
                }
            } else {
                sb.append(index >> 3);
            }
        }
        return sb.toString();
    }
private static int[] calcQuads(byte[] bytes) {
        int[] result = new int[(bytes.length + 3) / 4];
        for (int i = 0; i < bytes.length; ++i) {
            int value = bytes[i] & 0xFF;
            if (++i < bytes.length) {
                value = (value << 8) | (bytes[i] & 0xFF);
                if (++i < bytes.length) {
                    value = (value << 8) | (bytes[i] & 0xFF);
                    if (++i < bytes.length) {
                        value = (value << 8) | (bytes[i] & 0xFF);
                    }
                }
            }
            result[i >> 2] = value;
        }
        return result;
    }
private static final class SeededCanonicalizer extends ByteQuadsCanonicalizer {
        SeededCanonicalizer(int seed) {
            super(64, seed);
        }
    }
private static final class OneByteInputStream extends InputStream {
        private final ByteArrayInputStream delegate;

        OneByteInputStream(byte[] input) {
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

    void __invoke_byteBasedSymbolTableRetainsShortMediumAndLongNames() throws Exception {
        try {
            byteBasedSymbolTableRetainsShortMediumAndLongNames();
        } finally {
        }
    }


    void __invoke_syntheticByteSymbolsRetainSourceDistribution() throws Exception {
        try {
            syntheticByteSymbolsRetainSourceDistribution();
        } finally {
        }
    }


    void __invoke_numericByteCollisions187aRetainSourceDistribution() throws Exception {
        try {
            numericByteCollisions187aRetainSourceDistribution();
        } finally {
        }
    }


    void __invoke_numericByteCollisions187bRetainSourceDistribution() throws Exception {
        try {
            numericByteCollisions187bRetainSourceDistribution();
        } finally {
        }
    }


    void __invoke_shortNameCollisionsViaByteParserRetainAllNames() throws Exception {
        try {
            shortNameCollisionsViaByteParserRetainAllNames();
        } finally {
        }
    }


    void __invoke_shortQuotedByteSymbolsRetainSourceDistribution() throws Exception {
        try {
            shortQuotedByteSymbolsRetainSourceDistribution();
        } finally {
        }
    }


    void __invoke_shortDirectByteSymbolsRetainSourceDistribution() throws Exception {
        try {
            shortDirectByteSymbolsRetainSourceDistribution();
        } finally {
        }
    }


    void __invoke_seventeenByteSymbolsRetainAllNames() throws Exception {
        try {
            seventeenByteSymbolsRetainAllNames();
        } finally {
        }
    }

}
