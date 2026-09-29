package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ByteBufferDeserializer}.
 */
public class ByteBufferDeserializerTest extends DatabindTestUtil
{
    static class ByteBufferBean {
        public ByteBuffer data;
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testSimpleByteBufferDeser() throws Exception
    {
        // Base64 for "hello" is "aGVsbG8="
        ByteBuffer result = MAPPER.readValue(VPackUtils.toVPack(q("aGVsbG8=")), ByteBuffer.class);
        assertNotNull(result);
        byte[] bytes = new byte[result.remaining()];
        result.get(bytes);
        assertEquals("hello", new String(bytes, StandardCharsets.UTF_8));
    }

    @Test
    public void testEmptyByteBuffer() throws Exception
    {
        // Base64 for empty byte array is ""
        ByteBuffer result = MAPPER.readValue(VPackUtils.toVPack(q("")), ByteBuffer.class);
        assertNotNull(result);
        assertEquals(0, result.remaining());
    }

    @Test
    public void testByteBufferRoundTrip() throws Exception
    {
        byte[] original = "ByteBuffer round-trip test data".getBytes(StandardCharsets.UTF_8);
        ByteBuffer input = ByteBuffer.wrap(original);

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        ByteBuffer result = MAPPER.readValue(VPackUtils.toVPack(json), ByteBuffer.class);

        assertNotNull(result);
        byte[] resultBytes = new byte[result.remaining()];
        result.get(resultBytes);
        assertArrayEquals(original, resultBytes);
    }

    @Test
    public void testByteBufferAsProperty() throws Exception
    {
        // Base64 for [1,2,3] is "AQID"
        ByteBufferBean bean = MAPPER.readValue(
                VPackUtils.toVPack(a2q("{'data':'AQID'}")), ByteBufferBean.class);
        assertNotNull(bean);
        assertNotNull(bean.data);
        assertEquals(3, bean.data.remaining());
        assertEquals(1, bean.data.get());
        assertEquals(2, bean.data.get());
        assertEquals(3, bean.data.get());
    }

    @Test
    public void testByteBufferPropertyRoundTrip() throws Exception
    {
        ByteBufferBean input = new ByteBufferBean();
        input.data = ByteBuffer.wrap(new byte[] { 10, 20, 30, 40 });

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        ByteBufferBean result = MAPPER.readValue(VPackUtils.toVPack(json), ByteBufferBean.class);

        assertNotNull(result);
        assertNotNull(result.data);
        assertEquals(4, result.data.remaining());
        assertEquals(10, result.data.get());
        assertEquals(20, result.data.get());
    }

    @Test
    public void testLargeByteBuffer() throws Exception
    {
        byte[] largeData = new byte[10000];
        for (int i = 0; i < largeData.length; i++) {
            largeData[i] = (byte) (i & 0xFF);
        }
        ByteBuffer input = ByteBuffer.wrap(largeData);

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        ByteBuffer result = MAPPER.readValue(VPackUtils.toVPack(json), ByteBuffer.class);

        assertNotNull(result);
        byte[] resultBytes = new byte[result.remaining()];
        result.get(resultBytes);
        assertArrayEquals(largeData, resultBytes);
    }

    // [databind#239]
    @Test
    public void testByteBuffer() throws Exception
    {
        byte[] INPUT = new byte[] { 1, 3, 9, -1, 6 };
        String exp = VPackUtils.toJson(MAPPER.writeValueAsBytes(INPUT));
        ByteBuffer result = MAPPER.readValue(VPackUtils.toVPack(exp),  ByteBuffer.class);
        assertNotNull(result);
        assertEquals(INPUT.length, result.remaining());
        for (int i = 0; i < INPUT.length; ++i) {
            assertEquals(INPUT[i], result.get());
        }
        assertEquals(0, result.remaining());
    }
}
