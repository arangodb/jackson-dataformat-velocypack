package tools.jackson.core.unittest.constraints;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Arrays;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0011F1 {

    void largeNameBytesBelowVpackDefaultNameBudgetParsesFromInputStream() throws Exception {
        int nameLength = 100_000;
        byte[] document = indexedObjectWithName(nameLength);

        // Keep this source-adapted case below VPack's 16 MiB name-byte budget
        // while raising Jackson's independent default 50,000-character limit.
        VPackFactory factory = VPackFactory.builder().streamReadConstraints(
                StreamReadConstraints.builder().maxNameLength(nameLength).build()).build();
        try (JsonParser parser = factory.createParser(
                new ByteArrayInputStream(document))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(nameLength, parser.currentName().length());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.END_OBJECT, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }

    void largeNameWithSmallLimitBytesIsRejectedFromInputStream() {
        VPackFactory constrained = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootNameBytes(100L).build())
                .build();
        byte[] document = indexedObjectWithName(101);

        try (JsonParser parser = constrained.createParser(
                new ByteArrayInputStream(document))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    void largeNameWithSmallLimitDataInputIsRejected() {
        VPackFactory constrained = VPackFactory.builder()
                .vpackReadConstraints(VPackReadConstraints.builder()
                        .maxRootNameBytes(100L).build())
                .build();
        byte[] document = indexedObjectWithName(101);

        try (JsonParser parser = constrained.createParser(new DataInputStream(
                new ByteArrayInputStream(document)))) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertThrows(StreamConstraintsException.class, parser::nextToken);
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

    void __invoke_largeNameBytesBelowVpackDefaultNameBudgetParsesFromInputStream() throws Exception {
        try {
            largeNameBytesBelowVpackDefaultNameBudgetParsesFromInputStream();
        } finally {
        }
    }


    void __invoke_largeNameWithSmallLimitBytesIsRejectedFromInputStream() throws Exception {
        try {
            largeNameWithSmallLimitBytesIsRejectedFromInputStream();
        } finally {
        }
    }


    void __invoke_largeNameWithSmallLimitDataInputIsRejected() throws Exception {
        try {
            largeNameWithSmallLimitDataInputIsRejected();
        } finally {
        }
    }

}
