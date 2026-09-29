package tools.jackson.core.unittest.constraints;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0011F0 {

    void largeRootDocumentLimitAppliesToInputStreamAndByteArray() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxDocumentLength(10_000L).build())
                .build();
        byte[] document = compactArrayWithScalarCount(12_000);
        assertEquals(12_005, document.length);

        try (JsonParser parser = constrained.createParser(
                new ByteArrayInputStream(document))) {
            assertThrows(StreamConstraintsException.class, () -> consume(parser));
        } catch (StreamConstraintsException e) {
            // The stream root is framed lazily, so its limit may be reported by
            // createParser() or on the first token depending on the source seam.
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        assertThrows(StreamConstraintsException.class,
                () -> constrained.createParser(document));
    }

    void tokenLimitAppliesToLiteralCompactArrayFromInputStream() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxTokenCount(1_000L).build())
                .build();
        byte[] document = compactArrayWithScalarCount(1_001);

        try (JsonParser parser = constrained.createParser(
                new ByteArrayInputStream(document))) {
            assertThrows(StreamConstraintsException.class, () -> consume(parser));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
private static void consume(JsonParser parser) throws Exception {
        while (parser.nextToken() != null) { }
    }
private static byte[] compactArrayWithScalarCount(int count) {
        int countWidth = varintLength(count);
        int lengthWidth = 1;
        int length;
        do {
            length = 1 + lengthWidth + count + countWidth;
            int required = varintLength(length);
            if (required == lengthWidth) {
                break;
            }
            lengthWidth = required;
        } while (true);

        byte[] result = new byte[length];
        result[0] = 0x13;
        writeForward(result, 1, length);
        Arrays.fill(result, 1 + lengthWidth, length - countWidth, (byte) 0x31);
        writeReverse(result, length - countWidth, count);
        return result;
    }
private static byte[] indexedObjectWithName(int nameLength) {
        byte[] body;
        if (nameLength <= 126) {
            body = new byte[1 + nameLength + 1];
            body[0] = (byte) (0x40 + nameLength);
            Arrays.fill(body, 1, 1 + nameLength, (byte) 'a');
            body[body.length - 1] = 0x31;
        } else {
            body = new byte[1 + 8 + nameLength + 1];
            body[0] = (byte) 0xBF;
            putLittleEndian(body, 1, 8, nameLength);
            Arrays.fill(body, 9, 9 + nameLength, (byte) 'a');
            body[body.length - 1] = 0x31;
        }

        int width = 4;
        int bodyStart = 1 + 2 * width;
        int indexStart = bodyStart + body.length;
        int length = indexStart + width;
        byte[] result = new byte[length];
        result[0] = 0x0D;
        putLittleEndian(result, 1, width, length);
        putLittleEndian(result, 1 + width, width, 1);
        System.arraycopy(body, 0, result, bodyStart, body.length);
        putLittleEndian(result, indexStart, width, bodyStart);
        return result;
    }
private static int varintLength(long value) {
        int groups = 1;
        while ((value >>>= 7) != 0L) {
            ++groups;
        }
        return groups;
    }
private static void writeForward(byte[] output, int offset, long value) {
        int at = offset;
        do {
            int group = (int) (value & 0x7F);
            value >>>= 7;
            output[at++] = (byte) (group | (value == 0L ? 0 : 0x80));
        } while (value != 0L);
    }
private static void writeReverse(byte[] output, int offset, long value) {
        int groups = varintLength(value);
        int at = offset + groups - 1;
        do {
            int group = (int) (value & 0x7F);
            value >>>= 7;
            output[at--] = (byte) (group | (at < offset ? 0 : 0x80));
        } while (value != 0L);
    }
private static void putLittleEndian(byte[] output, int offset, int width, long value) {
        for (int i = 0; i < width; ++i) {
            output[offset + i] = (byte) (value >>> (8 * i));
        }
    }

    void __invoke_largeRootDocumentLimitAppliesToInputStreamAndByteArray() throws Exception {
        try {
            largeRootDocumentLimitAppliesToInputStreamAndByteArray();
        } finally {
        }
    }


    void __invoke_tokenLimitAppliesToLiteralCompactArrayFromInputStream() throws Exception {
        try {
            tokenLimitAppliesToLiteralCompactArrayFromInputStream();
        } finally {
        }
    }

}
