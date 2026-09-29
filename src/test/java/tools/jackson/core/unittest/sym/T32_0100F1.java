package tools.jackson.core.unittest.sym;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0100F1 {
private static final int SEVENTEEN_BYTE_NAME_COUNT = 50;
private static final int EXPANSION_NAME_COUNT = 200;
private static final String[] SHARED_PROPERTY_NAMES = {
            "a", "b", "c", "x", "y", "b13", "abcdefg", "a123",
            "a0", "b0", "c0", "d0", "e0", "f0", "g0", "h0",
            "x2", "aa", "ba", "ab", "b31", "___x", "aX", "xxx",
            "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
            "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
            "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
    };
private static final byte[] SHARED_WARMUP = {
            0x14, 0x09, 0x41, 'a', 0x31, 0x41, 'x', 0x01, 0x02
    };

    void simultaneousParsersShareByteSymbolsWithoutCrossContamination() throws Exception {
        VPackFactory factory = new VPackFactory();
        JsonParser warmup = factory.createParser(ObjectReadContext.empty(), SHARED_WARMUP);
        while (warmup.nextToken() != JsonToken.START_ARRAY) {
            // The array is deliberately left open so its child symbol table is in use.
        }

        byte[] forward = compactObjectWithSequentialIntegers(SHARED_PROPERTY_NAMES, false);
        byte[] reverse = compactObjectWithSequentialIntegers(SHARED_PROPERTY_NAMES, true);
        try (JsonParser first = factory.createParser(ObjectReadContext.empty(),
                new ByteArrayInputStream(forward));
                JsonParser second = factory.createParser(ObjectReadContext.empty(),
                        new ByteArrayInputStream(reverse))) {
            assertEquals(JsonToken.START_OBJECT, first.nextToken());
            assertEquals(JsonToken.START_OBJECT, second.nextToken());
            for (int i = 0; i < SHARED_PROPERTY_NAMES.length; ++i) {
                assertEquals(JsonToken.PROPERTY_NAME, first.nextToken());
                assertEquals(JsonToken.PROPERTY_NAME, second.nextToken());
                assertEquals(SHARED_PROPERTY_NAMES[i], first.currentName());
                assertEquals(SHARED_PROPERTY_NAMES[SHARED_PROPERTY_NAMES.length - i - 1],
                        second.currentName());
                assertEquals(JsonToken.VALUE_NUMBER_INT, first.nextToken());
                assertEquals(JsonToken.VALUE_NUMBER_INT, second.nextToken());
                assertEquals(i, first.getIntValue());
                assertEquals(i, second.getIntValue());
            }
            assertEquals(JsonToken.END_OBJECT, first.nextToken());
            assertEquals(JsonToken.END_OBJECT, second.nextToken());
        } finally {
            warmup.close();
        }
    }

    void auxiliaryByteCanonicalizerLookupsRemainUsableForVpack() {
        VPackFactory factory = new VPackFactory();
        ByteQuadsCanonicalizer symbols = VPackTestAccess.byteSymbolCanonicalizer(factory).makeChild(
                JsonFactory.Feature.collectDefaults());
        try {
            int aaaa = 0x41414141;
            int bbbb = 0x42424242;
            assertNull(symbols.findName(aaaa));
            assertNull(symbols.findName(aaaa, bbbb));

            symbols.addName("AAAA", new int[] { aaaa }, 1);
            assertEquals("AAAA", symbols.findName(aaaa));
            symbols.addName("AAAABBBB", new int[] { aaaa, bbbb }, 2);
            assertEquals("AAAABBBB", symbols.findName(aaaa, bbbb));
            assertNotNull(symbols.toString());
        } finally {
            symbols.release();
        }
    }

    void largeByteNameSetTraversesWithoutCanonicalizerCorruption() throws Exception {
        String[] names = new String[61];
        names[0] = "expectedGCperPosition";
        for (int i = 1; i < names.length; ++i) {
            names[i] = Integer.toString(i);
        }

        int propertyCount = 0;
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), compactObject(names, 0x18))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.currentToken());
                assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
                ++propertyCount;
            }
            assertNull(parser.nextToken());
        }
        assertEquals(names.length, propertyCount);
    }

    void byteCanonicalizerSpilloverRemainsSafeAfterRelease() {
        VPackFactory factory = new VPackFactory();
        ByteQuadsCanonicalizer root = VPackTestAccess.byteSymbolCanonicalizer(factory);
        int[] collisions = new int[25];
        ByteQuadsCanonicalizer symbols = root.makeChild(JsonFactory.Feature.collectDefaults());
        try {
            java.util.Random random = new java.util.Random(42);
            int target = (symbols.calcHash(random.nextInt()) & (2048 - 1)) << 2;
            for (int i = 0; i < collisions.length;) {
                int candidate = random.nextInt();
                int offset = (symbols.calcHash(candidate) & (2048 - 1)) << 2;
                if (offset == target) {
                    collisions[i++] = candidate;
                }
            }
            for (int i = 0; i < 22; ++i) {
                symbols.addName(Integer.toString(i), collisions[i]);
            }
        } finally {
            symbols.release();
        }

        ByteQuadsCanonicalizer reopened = root.makeChild(JsonFactory.Feature.collectDefaults());
        try {
            reopened.addName("22", collisions[22]);
        } finally {
            reopened.release();
        }
    }
private static String[] seventeenByteNames() {
        String[] names = new String[SEVENTEEN_BYTE_NAME_COUNT];
        for (int i = 0; i < names.length; ++i) {
            names[i] = "lengthmatters" + (1001 + i);
            assertEquals(17, names[i].getBytes(StandardCharsets.UTF_8).length);
        }
        return names;
    }
private static byte[] compactObject(String[] names, int valueMarker) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (String name : names) {
            byte[] utf8 = name.getBytes(StandardCharsets.UTF_8);
            if (utf8.length >= 0x7F) {
                throw new IllegalArgumentException("test name is too long");
            }
            body.write(0x40 + utf8.length);
            body.writeBytes(utf8);
            if (valueMarker == 0x44) {
                body.write(0x44);
                body.writeBytes(new byte[] { 't', 'e', 's', 't' });
            } else {
                body.write(valueMarker);
            }
        }

        return compactDocument(body.toByteArray(), names.length);
    }
private static byte[] compactObjectWithSequentialIntegers(String[] names, boolean reverse) {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        for (int i = 0; i < names.length; ++i) {
            String name = reverse ? names[names.length - i - 1] : names[i];
            byte[] utf8 = name.getBytes(StandardCharsets.UTF_8);
            body.write(0x40 + utf8.length);
            body.writeBytes(utf8);
            if (i < 10) {
                body.write(0x30 + i);
            } else {
                body.write(0x28);
                body.write(i);
            }
        }
        return compactDocument(body.toByteArray(), names.length);
    }
private static byte[] compactDocument(byte[] bodyBytes, int count) {
        int lengthGroups = 1;
        int countGroups = VPackWireFixtureAssembler.reverseVarint(count).length;
        int length;
        do {
            length = 1 + lengthGroups + bodyBytes.length + countGroups;
            int nextGroups = VPackWireFixtureAssembler.forwardVarint(length).length;
            if (nextGroups == lengthGroups) {
                break;
            }
            lengthGroups = nextGroups;
        } while (true);

        byte[] lengthBytes = VPackWireFixtureAssembler.forwardVarint(length);
        byte[] countBytes = VPackWireFixtureAssembler.reverseVarint(count);
        byte[] result = new byte[length];
        int offset = 0;
        result[offset++] = 0x14;
        System.arraycopy(lengthBytes, 0, result, offset, lengthBytes.length);
        offset += lengthBytes.length;
        System.arraycopy(bodyBytes, 0, result, offset, bodyBytes.length);
        offset += bodyBytes.length;
        System.arraycopy(countBytes, 0, result, offset, countBytes.length);
        return result;
    }

    void __invoke_simultaneousParsersShareByteSymbolsWithoutCrossContamination() throws Exception {
        try {
            simultaneousParsersShareByteSymbolsWithoutCrossContamination();
        } finally {
        }
    }


    void __invoke_auxiliaryByteCanonicalizerLookupsRemainUsableForVpack() throws Exception {
        try {
            auxiliaryByteCanonicalizerLookupsRemainUsableForVpack();
        } finally {
        }
    }


    void __invoke_largeByteNameSetTraversesWithoutCanonicalizerCorruption() throws Exception {
        try {
            largeByteNameSetTraversesWithoutCanonicalizerCorruption();
        } finally {
        }
    }


    void __invoke_byteCanonicalizerSpilloverRemainsSafeAfterRelease() throws Exception {
        try {
            byteCanonicalizerSpilloverRemainsSafeAfterRelease();
        } finally {
        }
    }

}
