package tools.jackson.databind.ext.jdk8;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.VPackUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleStreamSerializerTest extends StreamTestBase
{
    private final double[] single = { 1.0 };

    private final double[] multipleValues = { Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 0.0, 6.0, -3.0 };

    static class DoubleStreamWrapper {
        public DoubleStream value;

        public DoubleStreamWrapper() { }
        DoubleStreamWrapper(DoubleStream v) { value = v; }
    }

    @Test
    public void testEmptyStream() throws Exception {

        assertArrayEquals(new double[0], roundTrip(DoubleStream.empty()), 0.0);
    }

    @Test
    public void testSingleElement() throws Exception {

        assertArrayEquals(single, roundTrip(DoubleStream.of(single)), 0.0);
    }

    @Test
    public void testMultiElements() throws Exception {

        assertArrayEquals(multipleValues, roundTrip(DoubleStream.of(multipleValues)), 0.0);
    }

    @Test
    public void testDoubleStreamCloses() throws Exception {

        assertClosesOnSuccess(DoubleStream.of(multipleValues), this::roundTrip);
    }

    @Test
    public void testDoubleStreamInWrapper() throws Exception
    {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(
                new DoubleStreamWrapper(DoubleStream.of(1.1, 2.2, 3.3))));
        assertEquals("{\"value\":[1.1,2.2,3.3]}", json);
    }

    @Test
    public void testDoubleStreamSingleNegative() throws Exception
    {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(DoubleStream.of(-1.5)));
        assertEquals("[-1.5]", json);
    }

    @Test
    public void testDoubleStreamBoundaryValues() throws Exception
    {
        double[] values = { Double.MIN_VALUE, 0.0, Double.MAX_VALUE };
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(DoubleStream.of(values)));
        double[] result = objectMapper.readValue(VPackUtils.toVPack(json), double[].class);
        assertArrayEquals(values, result, 0.0);
    }

    @Test
    public void testDoubleStreamEmpty() throws Exception
    {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(
                new DoubleStreamWrapper(DoubleStream.empty())));
        assertEquals("{\"value\":[]}", json);
    }

    @Test
    public void testDoubleStreamInWrapperCloses() throws Exception
    {
        AtomicBoolean closed = new AtomicBoolean(false);
        DoubleStream stream = DoubleStream.of(1.0, 2.0).onClose(() -> closed.set(true));
        VPackUtils.toJson(objectMapper.writeValueAsBytes(new DoubleStreamWrapper(stream)));
        assertTrue(closed.get());
    }

    private double[] roundTrip(DoubleStream stream) {
        String json = VPackUtils.toJson(objectMapper.writeValueAsBytes(stream));
        return objectMapper.readValue(VPackUtils.toVPack(json), double[].class);
    }
}
