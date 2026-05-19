package tools.jackson.databind.ext.jdk8;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.VPackUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

public class LongStreamSerializerTest extends StreamTestBase
{
    static class LongStreamWrapper {
        public LongStream value;

        public LongStreamWrapper() { }
        LongStreamWrapper(LongStream v) { value = v; }
    }

    private final long[] single = { 1L };

    private final long[] multipleValues = { Long.MIN_VALUE, Long.MAX_VALUE, 1L, 0L, 6L, -3L };

    @Test
    public void testEmptyStream() throws Exception {

        assertArrayEquals(new long[0], roundTrip(LongStream.empty()));
    }

    @Test
    public void testSingleElement() throws Exception {

        assertArrayEquals(single, roundTrip(LongStream.of(single)));
    }

    @Test
    public void testMultiElements() throws Exception {

        assertArrayEquals(multipleValues, roundTrip(LongStream.of(multipleValues)));
    }

    @Test
    public void testLongStreamCloses() throws Exception {

        assertClosesOnSuccess(LongStream.of(multipleValues), this::roundTrip);
    }

    @Test
    public void testLongStreamInWrapper() throws Exception
    {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(
                new LongStreamWrapper(LongStream.of(100L, 200L, 300L))));
        assertEquals("{\"value\":[100,200,300]}", json);
    }

    @Test
    public void testLongStreamSingleNegative() throws Exception
    {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(LongStream.of(-1L)));
        assertEquals("[-1]", json);
    }

    @Test
    public void testLongStreamBoundaryValues() throws Exception
    {
        long[] values = { Long.MIN_VALUE, 0L, Long.MAX_VALUE };
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(LongStream.of(values)));
        long[] result = objectMapper.readValue(VPackUtils.toVPack(json), long[].class);
        assertArrayEquals(values, result);
    }

    @Test
    public void testLongStreamInWrapperCloses() throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean(false);
        LongStream stream = LongStream.of(1L, 2L).onClose(() -> closed.set(true));
        VPackUtils.toJson(objectMapper.writeValueAsBytes(new LongStreamWrapper(stream)));
        assertTrue(closed.get());
    }

    private long[] roundTrip(LongStream stream) {
        return objectMapper.readValue(objectMapper.writeValueAsBytes(stream), long[].class);
    }
}
