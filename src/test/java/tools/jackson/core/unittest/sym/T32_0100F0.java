package tools.jackson.core.unittest.sym;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0100F0 {
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

    void byteParserRetainsFiftyDistinctSeventeenByteNames() throws Exception {
        String[] names = seventeenByteNames();
        byte[] document = compactObject(names, 0x1A);

        Set<String> symbols = new HashSet<>();
        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), document)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            for (String expected : names) {
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals(expected, parser.currentName());
                symbols.add(parser.currentName());
                assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            }
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
        assertEquals(SEVENTEEN_BYTE_NAME_COUNT, symbols.size());
    }

    void byteParserExpandsNamesAcrossTwoHundredIndependentRoots() throws Exception {
        VPackFactory factory = new VPackFactory();
        for (int i = 0; i < EXPANSION_NAME_COUNT; ++i) {
            String name = Integer.toString(i);
            try (JsonParser parser = factory.createParser(ObjectReadContext.empty(),
                    compactObject(new String[] { name }, 0x44))) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
                assertEquals(name, parser.currentName());
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals("test", parser.getString());
                assertEquals(JsonToken.END_OBJECT, parser.nextToken());
                assertNull(parser.nextToken());
            }
        }
        assertEquals(EXPANSION_NAME_COUNT, VPackTestAccess.byteSymbolCanonicalizer(factory).size());
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

    void __invoke_byteParserRetainsFiftyDistinctSeventeenByteNames() throws Exception {
        try {
            byteParserRetainsFiftyDistinctSeventeenByteNames();
        } finally {
        }
    }


    void __invoke_byteParserExpandsNamesAcrossTwoHundredIndependentRoots() throws Exception {
        try {
            byteParserExpandsNamesAcrossTwoHundredIndependentRoots();
        } finally {
        }
    }

}
