package tools.jackson.core.unittest.constraints;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;

import tools.jackson.core.JsonParser;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0010F2 {

    void dataInputRootLengthLimitIsEnforcedBeforeTheRootIsRetained() {
        VPackFactory constrained = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxDocumentLength(10_000L).build())
                .build();
        byte[] document = compactArrayWithScalarCount(12_000);
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));

        try (JsonParser parser = constrained.createParser(ObjectReadContext.empty(), input)) {
            assertThrows(StreamConstraintsException.class, () -> consume(parser));
        }
    }

    void dataInputRootWithoutDocumentLengthLimitWorks() throws Exception {
        byte[] document = compactArrayWithScalarCount(20_000);
        DataInput input = new DataInputStream(new ByteArrayInputStream(document));

        try (JsonParser parser = new VPackFactory().createParser(
                ObjectReadContext.empty(), input)) {
            assertEquals(tools.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
            int values = 0;
            while (parser.nextToken() == tools.jackson.core.JsonToken.VALUE_NUMBER_INT) {
                ++values;
            }
            assertEquals(20_000, values);
            assertEquals(tools.jackson.core.JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(null, parser.nextToken());
        }
    }

    void largeBinaryRootFromInputStreamWorksUnderDefaultLimits() throws Exception {
        byte[] document = compactArrayWithScalarCount(50_000);

        try (JsonParser parser = new VPackFactory().createParser(
                new ByteArrayInputStream(document))) {
            assertEquals(tools.jackson.core.JsonToken.START_ARRAY, parser.nextToken());
            int values = 0;
            while (parser.nextToken() == tools.jackson.core.JsonToken.VALUE_NUMBER_INT) {
                ++values;
            }
            assertEquals(50_000, values);
            assertEquals(tools.jackson.core.JsonToken.END_ARRAY, parser.currentToken());
            assertEquals(null, parser.nextToken());
        }
    }
private static void consume(JsonParser parser) throws Exception {
        while (parser.nextToken() != null) { }
    }
private static byte[] nestedArrays(int depth) {
        byte[] result = new byte[] { 0x01 };
        for (int i = 0; i < depth; ++i) {
            result = compactArray(result);
        }
        return result;
    }
private static byte[] compactArray(byte[] child) {
        int lengthWidth = 1;
        int length;
        do {
            length = 1 + lengthWidth + child.length + 1;
            int required = varintLength(length);
            if (required == lengthWidth) {
                break;
            }
            lengthWidth = required;
        } while (true);

        byte[] result = new byte[length];
        result[0] = 0x13;
        writeForward(result, 1, length);
        System.arraycopy(child, 0, result, 1 + lengthWidth, child.length);
        result[length - 1] = 0x01;
        return result;
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
        int bodyStart = 1 + lengthWidth;
        for (int i = 0; i < count; ++i) {
            result[bodyStart + i] = 0x31;
        }
        writeReverse(result, length - countWidth, count);
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

    void __invoke_dataInputRootLengthLimitIsEnforcedBeforeTheRootIsRetained() throws Exception {
        try {
            dataInputRootLengthLimitIsEnforcedBeforeTheRootIsRetained();
        } finally {
        }
    }


    void __invoke_dataInputRootWithoutDocumentLengthLimitWorks() throws Exception {
        try {
            dataInputRootWithoutDocumentLengthLimitWorks();
        } finally {
        }
    }


    void __invoke_largeBinaryRootFromInputStreamWorksUnderDefaultLimits() throws Exception {
        try {
            largeBinaryRootFromInputStreamWorksUnderDefaultLimits();
        } finally {
        }
    }

}
