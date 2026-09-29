package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VPackCompactGeneratorTest {
    @Test
    void emitsTheIndependentCompactArrayAndObjectExamples() throws Exception {
        assertArrayEquals(bytes("13 06 31 28 10 02"), writeArray(true));
        assertArrayEquals(bytes("14 0a 41 61 31 41 62 28 10 02"), writeObject(true));
    }

    @Test
    void emptyMarkersWinOverCompactFeatures() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        assertArrayEquals(new byte[] { 0x01 }, writeEmptyArray(factory));
        assertArrayEquals(new byte[] { 0x0A }, writeEmptyObject(factory));
    }

    @Test
    void compactArraysTakePrecedenceOverEqualArrays() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS).build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeNumber(2);
            generator.writeEndArray();
        }
        assertArrayEquals(bytes("13 05 31 32 02"), out.toByteArray());
    }

    @Test
    void compactObjectsKeepCallOrderWithoutAnIndex() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS).build()
                .createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeNumber(1);
            generator.writeName("a");
            generator.writeNumber(2);
            generator.writeEndObject();
        }
        assertArrayEquals(bytes("14 09 41 62 31 41 61 32 02"), out.toByteArray());
    }

    @Test
    void compactObjectsKeepResolvedDuplicatePolicy() throws Exception {
        assertThrows(StreamWriteException.class, () -> {
            try (JsonGenerator generator = VPackFactory.builder()
                    .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                    .enable(StreamWriteFeature.STRICT_DUPLICATE_DETECTION).build()
                    .createGenerator(new ByteArrayOutputStream())) {
                generator.writeStartObject();
                generator.writeName("a");
                generator.writeNumber(1);
                generator.writeName("a");
            }
        });
    }

    @Test
    void noncompactFeatureCombinationsUseEqualIndexedAndSortedDefaults() throws Exception {
        assertArrayEquals(bytes("06 08 02 31 28 10 03 04"), writeArray(false));

        ByteArrayOutputStream indexed = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .disable(VPackWriteFeature.USE_EQUAL_LENGTH_ARRAYS).build()
                .createGenerator(indexed)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeBoolean(true);
            generator.writeEndArray();
        }
        assertArrayEquals(bytes("06 07 02 31 1A 03 04"), indexed.toByteArray());

        ByteArrayOutputStream object = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(object)) {
            generator.writeStartObject();
            generator.writeName("b");
            generator.writeNumber(1);
            generator.writeName("a");
            generator.writeNumber(2);
            generator.writeEndObject();
        }
        assertArrayEquals(bytes("0B 0B 02 41 62 31 41 61 32 06 03"), object.toByteArray());
    }

    @Test
    void compactLengthUsesItsSelfWidthAtTheBoundary() throws Exception {
        String value = "a".repeat(124);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS).build()
                .createGenerator(out)) {
            generator.writeStartArray();
            generator.writeString(value);
            generator.writeEndArray();
        }
        byte[] actual = out.toByteArray();
        assertEquals(129, actual.length);
        assertArrayEquals(new byte[] { 0x13, (byte) 0x81, 0x01, (byte) 0xBC },
                Arrays.copyOf(actual, 4));
        assertEquals(0x01, actual[actual.length - 1] & 0xFF);
    }

    @Test
    void compactCountsUseMinimalReverseGroupsAt127128And129() throws Exception {
        for (int count : new int[] { 127, 128, 129 }) {
            byte[] actual = writeCompactArray(count);
            int expectedLength = count + (count < 128 ? 4 : 5);
            assertEquals(expectedLength, actual.length);
            assertEquals(0x13, actual[0] & 0xFF);
            assertEquals(count < 128 ? 0x83 : count == 128 ? 0x85 : 0x86,
                    actual[1] & 0xFF);
            if (count < 128) {
                assertEquals(0x7F, actual[actual.length - 1] & 0xFF);
            } else {
                assertEquals(0x01, actual[actual.length - 2] & 0xFF);
                assertEquals(count, actual[actual.length - 1] & 0xFF);
            }
            for (int i = 3; i < actual.length - (count < 128 ? 1 : 2); ++i) {
                assertEquals(0x31, actual[i] & 0xFF);
            }
        }
    }

    private static byte[] writeArray(boolean compact) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackFactory factory = compact ? VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS).build() : new VPackFactory();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeStartArray();
            generator.writeNumber(1);
            generator.writeNumber(16);
            generator.writeEndArray();
        }
        return out.toByteArray();
    }

    private static byte[] writeObject(boolean compact) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackFactory factory = compact ? VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS).build() : new VPackFactory();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeStartObject();
            generator.writeName("a");
            generator.writeNumber(1);
            generator.writeName("b");
            generator.writeNumber(16);
            generator.writeEndObject();
        }
        return out.toByteArray();
    }

    private static byte[] writeEmptyArray(VPackFactory factory) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeStartArray();
            generator.writeEndArray();
        }
        return out.toByteArray();
    }

    private static byte[] writeEmptyObject(VPackFactory factory) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = factory.createGenerator(out)) {
            generator.writeStartObject();
            generator.writeEndObject();
        }
        return out.toByteArray();
    }

    private static byte[] writeCompactArray(int count) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator generator = VPackFactory.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS).build()
                .createGenerator(out)) {
            generator.writeStartArray();
            for (int i = 0; i < count; ++i) generator.writeNumber(1);
            generator.writeEndArray();
        }
        return out.toByteArray();
    }

    private static byte[] bytes(String text) {
        String compact = text.replace(" ", "");
        byte[] result = new byte[compact.length() / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) Integer.parseInt(compact.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
