package tools.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import tools.jackson.core.JsonEncoding;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.util.BufferRecycler;

import org.junit.jupiter.api.Test;

import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.StreamWriteConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamWriteException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VPackGeneratorLifecycleTest {
    @Test
    void closeFinishesCompleteOpenRootsIteratively() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        for (int i = 0; i < 96; ++i) {
            generator.writeStartArray();
        }
        generator.writeNumber(1);

        assertEquals(1, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertEquals(0x02, out.toByteArray()[0] & 0xFF);
        assertDoesNotThrow(generator::close);
    }

    @Test
    void closeEmitsACompleteRootButFlushDoesNot() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);
        generator.flush();
        assertEquals(0, out.size());
        assertEquals(1, generator.streamWriteOutputBuffered());

        generator.close();
        assertArrayEquals(new byte[] { 0x02, 0x03, 0x31 }, out.toByteArray());
    }

    @Test
    void disabledAutoCloseDiscardsAnOpenRootAndCloseIsIdempotent() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) VPackFactory.builder()
                .disable(StreamWriteFeature.AUTO_CLOSE_CONTENT)
                .build().createGenerator(out);
        generator.writeStartArray();
        generator.writeNumber(1);

        assertThrows(StreamWriteException.class, generator::close);
        assertEquals(0, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
    }

    @Test
    void danglingNamesAreErrorsEvenWhenAutoCloseContentIsEnabled() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        VPackGenerator generator = (VPackGenerator) new VPackFactory().createGenerator(out);
        generator.writeStartObject();
        generator.writeName("dangling");

        assertThrows(StreamWriteException.class, generator::close);
        assertEquals(0, out.size());
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertDoesNotThrow(generator::close);
    }

    @Test
    void appliesEffectiveStreamWriteDepthConstraints() {
        VPackFactory factory = VPackFactory.builder()
                .streamWriteConstraints(StreamWriteConstraints.builder()
                        .maxNestingDepth(1).build())
                .build();
        VPackGenerator generator = (VPackGenerator) factory.createGenerator(
                new ByteArrayOutputStream());
        generator.writeStartArray();
        assertThrows(StreamConstraintsException.class, generator::writeStartArray);
        assertEquals(0, generator.streamWriteOutputBuffered());
        assertThrows(RuntimeException.class, generator::close);
        assertDoesNotThrow(generator::close);
    }

    @Test
    void recyclerPageIsReusedAcrossGeneratorsSharingAnIoRecycler() throws Exception {
        BufferRecycler recycler = new BufferRecycler();
        IOContext firstContext = ioContext(recycler);
        VPackGenerator first = generator(firstContext);
        first.writeStartArray();
        first.writeNumber(42);
        first.writeEndArray();
        byte[] firstPage = recyclerPage(first);
        assertTrue(firstPage.length >= VPackByteStore.PAGE_SIZE);
        first.close();

        IOContext secondContext = ioContext(recycler);
        VPackGenerator second = generator(secondContext);
        second.writeStartArray();
        second.writeNumber(43);
        second.writeEndArray();
        assertSame(firstPage, recyclerPage(second));
        second.close();
    }

    @Test
    void failedGeneratorReturnsRecyclerPageOnceWhenClosed() throws Exception {
        IOContext context = ioContext(new BufferRecycler());
        VPackGenerator generator = generator(context);
        generator.writeStartArray();
        generator.writeNumber(1);
        byte[] expectedPage = recyclerPage(generator);
        assertThrows(StreamWriteException.class, () -> generator.writeName("invalid"));
        assertThrows(StreamWriteException.class, generator::close);
        byte[] returnedPage = context.allocWriteEncodingBuffer(VPackByteStore.PAGE_SIZE);
        assertSame(expectedPage, returnedPage);
        context.releaseWriteEncodingBuffer(returnedPage);
    }

    private static IOContext ioContext(BufferRecycler recycler) {
        return new IOContext(tools.jackson.core.StreamReadConstraints.defaults(),
                tools.jackson.core.StreamWriteConstraints.defaults(),
                tools.jackson.core.ErrorReportConfiguration.defaults(), recycler,
                ContentReference.unknown(), false, JsonEncoding.UTF8).markBufferRecyclerReleased();
    }

    private static VPackGenerator generator(IOContext context) {
        return new VPackGenerator(ObjectWriteContext.empty(), context,
                StreamWriteFeature.collectDefaults(), VPackWriteFeature.collectDefaults(),
                new ByteArrayOutputStream(), VPackWriteConstraints.defaults());
    }

    private static byte[] recyclerPage(VPackGenerator generator) throws Exception {
        Field arenaField = VPackGenerator.class.getDeclaredField("_arena");
        arenaField.setAccessible(true);
        Object arena = arenaField.get(generator);
        Field supplierField = VPackOutputArena.class.getDeclaredField("pageSupplier");
        supplierField.setAccessible(true);
        VPackRecyclerPageSupplier supplier = (VPackRecyclerPageSupplier) supplierField.get(arena);
        Field pageField = VPackRecyclerPageSupplier.class.getDeclaredField("pooledPage");
        pageField.setAccessible(true);
        return (byte[]) pageField.get(supplier);
    }
}
