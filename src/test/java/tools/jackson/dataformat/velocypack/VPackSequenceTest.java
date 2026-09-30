package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class VPackSequenceTest {
    @Test
    void longMultiRootStreamMatchesConcatenatedRootEncodings() throws Exception {
        VPackMapper mapper = new VPackMapper();
        String value = "v".repeat(VPackByteStore.PAGE_SIZE + 257);
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        try (SequenceWriter writer = mapper.writer().withRootValueSeparator((String) null)
                .writeValues(actual)) {
            for (int i = 0; i < 8; ++i) {
                writer.write(value + i);
                expected.write(mapper.writeValueAsBytes(value + i));
            }
        }
        assertArrayEquals(expected.toByteArray(), actual.toByteArray());
    }

    @Test
    void sequenceWriterEmitsSelfDelimitingScalarAndContainerRoots() throws Exception {
        VPackMapper mapper = new VPackMapper();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = mapper.writer().withRootValueSeparator((String) null)
                .writeValues(output)) {
            writer.write(1);
            writer.write(List.of(2, 3));
            writer.write("done");
        }

        List<Object> values = new ArrayList<>();
        try (MappingIterator<Object> iterator = mapper.readerFor(Object.class)
                .readValues(new ByteArrayInputStream(output.toByteArray()))) {
            while (iterator.hasNextValue()) {
                values.add(iterator.nextValue());
            }
        }
        assertEquals(3, values.size());
        assertEquals(1, values.get(0));
        assertEquals(List.of(2, 3), values.get(1));
        assertEquals("done", values.get(2));
    }

    @Test
    void sequenceWriterDoesNotCloseCallerOwnedTargetWhenAutoCloseIsDisabled() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .disable(tools.jackson.core.StreamWriteFeature.AUTO_CLOSE_TARGET).build();
        VPackMapper mapper = new VPackMapper(factory);
        TrackingOutputStream output = new TrackingOutputStream();
        try (SequenceWriter writer = mapper.writer().writeValues(output)) {
            writer.write(1).write(2);
        }
        assertFalse(output.closed);
        assertEquals(2, output.toByteArray().length);
    }

    @Test
    void rootBudgetResetsBetweenSequenceValues() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .vpackWriteConstraints(VPackWriteConstraints.builder()
                        .maxRootValueBytes(2).build())
                .build();
        VPackMapper mapper = new VPackMapper(factory);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (SequenceWriter writer = mapper.writer().writeValues(output)) {
            writer.write("a").write("b");
        }
        assertEquals(4, output.size());
    }

    @Test
    void mappingIteratorReadsIndependentLiteralRootsAndStopsAtBoundary() throws Exception {
        VPackMapper mapper = new VPackMapper();
        byte[] input = { 0x31, 0x02, 0x04, 0x31, 0x32, 0x18 };
        try (MappingIterator<Object> iterator = mapper.readerFor(Object.class).readValues(input)) {
            assertTrue(iterator.hasNextValue());
            assertEquals(1, iterator.nextValue());
            assertTrue(iterator.hasNextValue());
            assertEquals(List.of(1, 2), iterator.nextValue());
            assertTrue(iterator.hasNextValue());
            assertEquals(null, iterator.nextValue());
            assertFalse(iterator.hasNextValue());
        }
    }

    @Test
    void mapperDocumentLimitAppliesAcrossUnwrappedRoots() throws Exception {
        VPackFactory factory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxDocumentLength(1).build())
                .build();
        VPackMapper mapper = new VPackMapper(factory);
        try (MappingIterator<Integer> iterator = mapper.readerFor(Integer.class)
                .readValues(new ByteArrayInputStream(new byte[] { 0x31, 0x32 }))) {
            assertEquals(1, iterator.nextValue());
            assertThrows(StreamConstraintsException.class, iterator::hasNextValue);
        }
    }

    @Test
    void forwardParserDoesNotReadPastAnExactFramedRoot() throws Exception {
        TrackingInputStream input = new TrackingInputStream(new byte[] { 0x31, 0x55 });
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            assertEquals(0, input.position());
            assertEquals(tools.jackson.core.JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, input.position());
            assertEquals(1, parser.getIntValue());
            assertEquals(0L, parser.currentTokenLocation().getByteOffset());
            assertEquals(1L, parser.currentLocation().getByteOffset());
        }
        assertEquals(1, input.position());
    }

    private static final class TrackingOutputStream extends ByteArrayOutputStream {
        private boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        private TrackingInputStream(byte[] data) {
            super(data);
        }

        private int position() {
            return pos;
        }
    }
}
