package tools.jackson.databind.ext.jdk8;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.VPackUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class IntStreamSerializerTest extends StreamTestBase
{
    private final int[] single = { 1 };

    private final int[] multipleValues = { Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 0, 6, -3 };

    static class IntStreamWrapper {
        public IntStream value;

        public IntStreamWrapper() { }
        IntStreamWrapper(IntStream v) { value = v; }
    }
    
    @Test
    public void testEmptyStream() throws Exception {
        assertArrayEquals(new int[0], roundTrip(IntStream.empty()));
    }

    @Test
    public void testSingleElement() throws Exception {
        assertArrayEquals(single, roundTrip(IntStream.of(single)));
    }

    @Test
    public void testMultiElements() throws Exception {
        assertArrayEquals(multipleValues, roundTrip(IntStream.of(multipleValues)));
    }

    @Test
    public void testIntStreamCloses() throws Exception {
        assertClosesOnSuccess(IntStream.of(multipleValues), this::roundTrip);
    }

    @Test
    public void testIntStreamInWrapper() throws Exception
    {
        assertEquals("{\"value\":[10,20,30]}", VPackUtils.toJson(objectMapper.writeValueAsBytes(
                new IntStreamWrapper(IntStream.of(10, 20, 30)))));
    }

    @Test
    public void testIntStreamSingleNegative() throws Exception
    {
        assertEquals("[-1]", VPackUtils.toJson(objectMapper.writeValueAsBytes(IntStream.of(-1))));
    }

    @Test
    public void testIntStreamBoundaryValues() throws Exception
    {
        int[] values = { Integer.MIN_VALUE, 0, Integer.MAX_VALUE };
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(IntStream.of(values)));
        int[] result = objectMapper.readValue(VPackUtils.toVPack(json), int[].class);
        assertArrayEquals(values, result);
    }

    @Test
    public void testIntStreamInWrapperCloses() throws Exception
    {
        final AtomicBoolean closed = new AtomicBoolean(false);
        IntStream stream = IntStream.of(1, 2, 3).onClose(() -> closed.set(true));
        VPackUtils.toJson(objectMapper.writeValueAsBytes(new IntStreamWrapper(stream)));
        assertTrue(closed.get());
    }

    private int[] roundTrip(IntStream stream) {
        return objectMapper.readValue(objectMapper.writeValueAsBytes(stream), int[].class);
    }
}
