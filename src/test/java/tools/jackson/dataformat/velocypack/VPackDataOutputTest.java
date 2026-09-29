package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.io.OutputDecorator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackDataOutputTest {
    @Test
    void delegatesDataOutputFlushAndCloseAndRetainsCallerIdentity() throws Exception {
        TrackingDataOutput target = new TrackingDataOutput();
        VPackGenerator generator = (VPackGenerator) new VPackFactory()
                .createGenerator(ObjectWriteContext.empty(), (java.io.DataOutput) target);

        assertSame(target, generator.streamWriteOutputTarget());
        generator.writeNumber(1);
        generator.flush();
        generator.close();

        assertTrue(target.flushes > 0);
        assertEquals(1, target.closes);
        assertArrayEquals(new byte[] { 0x31 }, target.bytes.toByteArray());
    }

    @Test
    void honorsFlushAndAutoCloseTargetFlagsForDataOutput() {
        TrackingDataOutput noFlush = new TrackingDataOutput();
        VPackGenerator flushDisabled = (VPackGenerator) VPackFactory.builder()
                .disable(StreamWriteFeature.FLUSH_PASSED_TO_STREAM)
                .disable(StreamWriteFeature.AUTO_CLOSE_TARGET)
                .build().createGenerator(ObjectWriteContext.empty(), (java.io.DataOutput) noFlush);
        flushDisabled.writeNumber(1);
        flushDisabled.flush();
        flushDisabled.close();
        assertEquals(0, noFlush.flushes);
        assertEquals(0, noFlush.closes);

        TrackingDataOutput autoCloseDisabled = new TrackingDataOutput();
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_TARGET)
                .build().createGenerator(ObjectWriteContext.empty(),
                        (java.io.DataOutput) autoCloseDisabled);
        generator.writeStartArray();
        generator.writeNumber(1);
        assertEquals(1, generator.streamWriteOutputBuffered());
        generator.close();
        assertEquals(0, autoCloseDisabled.closes);
    }

    @Test
    void appliesOutputDecoratorExactlyOnceAndReportsEffectiveTarget() {
        TrackingDataOutput target = new TrackingDataOutput();
        CountingDecorator decorator = new CountingDecorator();
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .outputDecorator(decorator).build()
                .createGenerator(ObjectWriteContext.empty(), (java.io.DataOutput) target);

        assertEquals(1, decorator.streamCalls);
        assertFalse(generator.streamWriteOutputTarget() == target);
        generator.writeNumber(1);
        generator.close();
        assertArrayEquals(new byte[] { 0x31 }, target.bytes.toByteArray());
    }

    @Test
    void supportsOwnedFileAndPathTargets() throws Exception {
        Path file = Files.createTempFile("vpack-generator", ".bin");
        Path path = Files.createTempFile("vpack-generator-path", ".bin");
        try {
            try (JsonGenerator generator = new VPackFactory().createGenerator(
                    ObjectWriteContext.empty(), file, tools.jackson.core.JsonEncoding.UTF8)) {
                generator.writeNumber(1);
            }
            try (JsonGenerator generator = new VPackFactory().createGenerator(
                    ObjectWriteContext.empty(), path, tools.jackson.core.JsonEncoding.UTF8)) {
                generator.writeNumber(2);
            }
            assertArrayEquals(new byte[] { 0x31 }, Files.readAllBytes(file));
            assertArrayEquals(new byte[] { 0x32 }, Files.readAllBytes(path));
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(path);
        }
    }

    private static final class TrackingDataOutput extends DataOutputStream {
        final ByteArrayOutputStream bytes;
        int flushes;
        int closes;

        TrackingDataOutput() {
            this(new ByteArrayOutputStream());
        }

        private TrackingDataOutput(ByteArrayOutputStream bytes) {
            super(bytes);
            this.bytes = bytes;
        }

        @Override
        public void flush() throws IOException {
            ++flushes;
            super.flush();
        }

        @Override
        public void close() throws IOException {
            ++closes;
            super.close();
        }
    }

    private static final class CountingDecorator extends OutputDecorator {
        int streamCalls;

        @Override
        public OutputStream decorate(IOContext ctxt, OutputStream out) {
            ++streamCalls;
            return new FilterOutputStream(out);
        }

        @Override
        public java.io.Writer decorate(IOContext ctxt, java.io.Writer writer) {
            return writer;
        }
    }
}
