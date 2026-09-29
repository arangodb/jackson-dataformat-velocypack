package tools.jackson.core.unittest.json.async;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0057F1 {
private static final byte[] POINTER_DOCUMENT = {
            0x14, 0x48,
            0x41, 'a', 0x28, 0x7B,
            0x45, 'a', 'r', 'r', 'a', 'y',
            0x13, 0x18,
            0x31, 0x32,
            0x13, 0x04, 0x33, 0x01,
            0x35,
            0x14, 0x0E, 0x49, 'o', 'b', 'I', 'n', 'A', 'r', 'r', 'a', 'y',
            0x34, 0x01,
            0x05,
            0x42, 'o', 'b',
            0x14, 0x1D,
            0x45, 'f', 'i', 'r', 's', 't',
            0x13, 0x05, 0x19, 0x1A, 0x02,
            0x46, 's', 'e', 'c', 'o', 'n', 'd',
            0x14, 0x08, 0x43, 's', 'u', 'b', 0x37, 0x01,
            0x02,
            0x41, 'b', 0x1A,
            0x04
    };

    void literalVpackRootIntegersRetainExactValues() throws Exception {
        assertInteger(new byte[] { 0x28, 0x0A }, 10);
        assertInteger(new byte[] { 0x30 }, 0);
        assertInteger(new byte[] { 0x21, 0x2E, (byte) 0xFB }, -1234);
    }

    void literalVpackSimpleDoublesRetainExactValues() throws Exception {
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0x24, 0x40 }, 10.0);
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0x49, (byte) 0x93, (byte) 0xC0 },
                -1234.25);
        assertDouble(new byte[] { 0x1B, 0x0A, (byte) 0xD7, (byte) 0xA3, 0x70,
                0x3D, 0x0A, (byte) 0xB7, 0x3F }, 0.09);
    }

    void literalVpackScientificValuesRetainExactDoubles() throws Exception {
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, (byte) 0x94, (byte) 0xC1, 0x40 },
                9000.0);
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, 0, 0 }, 0.0);
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0, (byte) 0xC0, (byte) 0xBF },
                -0.125);
        assertDouble(new byte[] { 0x1B, 0, 0, 0, 0, 0, 0x6A, (byte) 0xC8, (byte) 0xC0 },
                -12500.0);
    }
private static void assertTokenAndPath(JsonParser parser, JsonToken token,
            String path) throws Exception {
        assertEquals(token, parser.nextToken());
        assertEquals(path, parser.streamReadContext().pathAsPointer().toString());
    }
private static void assertSingleName(String name, String expected) throws Exception {
        byte[] key = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] input = new byte[key.length + 5];
        input[0] = 0x14;
        input[1] = (byte) input.length;
        input[2] = (byte) (0x40 + key.length);
        System.arraycopy(key, 0, input, 3, key.length);
        input[3 + key.length] = 0x1A;
        input[input.length - 1] = 0x01;
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(expected, parser.currentName());
            assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static void assertInteger(byte[] input, int expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(expected, parser.getIntValue());
            assertNull(parser.nextToken());
        }
    }
private static void assertDouble(byte[] input, double expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
            assertEquals(expected, parser.getDoubleValue());
            assertNull(parser.nextToken());
        }
    }

    void __invoke_literalVpackRootIntegersRetainExactValues() throws Exception {
        try {
            literalVpackRootIntegersRetainExactValues();
        } finally {
        }
    }


    void __invoke_literalVpackSimpleDoublesRetainExactValues() throws Exception {
        try {
            literalVpackSimpleDoublesRetainExactValues();
        } finally {
        }
    }


    void __invoke_literalVpackScientificValuesRetainExactDoubles() throws Exception {
        try {
            literalVpackScientificValuesRetainExactDoubles();
        } finally {
        }
    }

}
