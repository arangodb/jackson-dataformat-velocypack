package tools.jackson.core.unittest.json.async;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0060F0 {

    void literalVpackStringArrayRetainsShortAndLongAsciiAndUnicodeValues() throws Exception {
        String longAscii = "abcdefghijklmnopqrstuvwxyz0123456789".repeat(320);
        String longUnicode = "prefix-€-" + "é😀".repeat(90) + "-suffix";
        String[] expected = {
                "Test", "", "1", "1234567890123456789012345678901234567890",
                "short-é-€-😀", longAscii, longUnicode
        };

        try (JsonParser parser = new VPackFactory().createParser(indexedStringArray(expected))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            for (String value : expected) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(value, parser.getString());
            }
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }

    void literalVpackStringArrayHonorsSmallStringConstraint() throws Exception {
        String longAscii = "x".repeat(12_000);
        byte[] fixture = indexedStringArray(longAscii, longAscii, longAscii,
                longAscii, longAscii);
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(100).build())
                .build();

        try (JsonParser parser = constrained.createParser(fixture)) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        }
    }
private static byte[] indexedStringArray(String... values) {
        byte[][] encoded = new byte[values.length][];
        int bodyLength = 0;
        for (int i = 0; i < values.length; ++i) {
            encoded[i] = string(values[i]);
            bodyLength += encoded[i].length;
        }
        int length = 1 + 4 + 4 + bodyLength + 4 * values.length;
        byte[] result = new byte[length];
        result[0] = 0x08;
        putLittleEndian(result, 1, length);
        putLittleEndian(result, 5, values.length);
        int cursor = 9;
        int[] starts = new int[values.length];
        for (int i = 0; i < encoded.length; ++i) {
            starts[i] = cursor;
            System.arraycopy(encoded[i], 0, result, cursor, encoded[i].length);
            cursor += encoded[i].length;
        }
        for (int start : starts) {
            putLittleEndian(result, cursor, start);
            cursor += 4;
        }
        return result;
    }
private static byte[] indexedObject(String[] names, String[] values) {
        if (names.length != values.length) throw new IllegalArgumentException();
        byte[][] encodedNames = new byte[names.length][];
        byte[][] encodedValues = new byte[values.length][];
        int bodyLength = 0;
        for (int i = 0; i < names.length; ++i) {
            encodedNames[i] = string(names[i]);
            encodedValues[i] = string(values[i]);
            bodyLength += encodedNames[i].length + encodedValues[i].length;
        }
        int length = 1 + 4 + 4 + bodyLength + 4 * names.length;
        byte[] result = new byte[length];
        result[0] = 0x0D;
        putLittleEndian(result, 1, length);
        putLittleEndian(result, 5, names.length);
        int cursor = 9;
        int[] nameStarts = new int[names.length];
        for (int i = 0; i < names.length; ++i) {
            nameStarts[i] = cursor;
            System.arraycopy(encodedNames[i], 0, result, cursor, encodedNames[i].length);
            cursor += encodedNames[i].length;
            System.arraycopy(encodedValues[i], 0, result, cursor, encodedValues[i].length);
            cursor += encodedValues[i].length;
        }
        Integer[] order = new Integer[names.length];
        for (int i = 0; i < order.length; ++i) order[i] = i;
        Arrays.sort(order, (left, right) -> compareUnsigned(
                encodedNames[left], encodedNames[right], 1));
        for (int index : order) {
            putLittleEndian(result, cursor, nameStarts[index]);
            cursor += 4;
        }
        return result;
    }
private static byte[] string(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length <= 126) {
            byte[] result = new byte[payload.length + 1];
            result[0] = (byte) (0x40 + payload.length);
            System.arraycopy(payload, 0, result, 1, payload.length);
            return result;
        }
        byte[] result = new byte[payload.length + 9];
        result[0] = (byte) 0xBF;
        putLittleEndian(result, 1, payload.length, 8);
        System.arraycopy(payload, 0, result, 9, payload.length);
        return result;
    }
private static int compareUnsigned(byte[] left, byte[] right, int offset) {
        int leftLength = left.length - offset;
        int rightLength = right.length - offset;
        int common = Math.min(leftLength, rightLength);
        for (int i = 0; i < common; ++i) {
            int comparison = Byte.toUnsignedInt(left[offset + i])
                    - Byte.toUnsignedInt(right[offset + i]);
            if (comparison != 0) return comparison;
        }
        return leftLength - rightLength;
    }
private static void putLittleEndian(byte[] target, int offset, long value) {
        putLittleEndian(target, offset, value, 4);
    }
private static void putLittleEndian(byte[] target, int offset, long value, int width) {
        for (int i = 0; i < width; ++i) {
            target[offset + i] = (byte) (value >>> (8 * i));
        }
    }

    void __invoke_literalVpackStringArrayRetainsShortAndLongAsciiAndUnicodeValues() throws Exception {
        try {
            literalVpackStringArrayRetainsShortAndLongAsciiAndUnicodeValues();
        } finally {
        }
    }


    void __invoke_literalVpackStringArrayHonorsSmallStringConstraint() throws Exception {
        try {
            literalVpackStringArrayHonorsSmallStringConstraint();
        } finally {
        }
    }

}
