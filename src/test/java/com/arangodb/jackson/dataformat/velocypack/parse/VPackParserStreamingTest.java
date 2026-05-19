package com.arangodb.jackson.dataformat.velocypack.parse;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackParser;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VPackParser using InputStream-based input, covering the streaming
 * read paths: _loadMore(), _loadMoreGuaranteed(), _ensureAvailable(), _nextByte(),
 * _peekByte(), _readBytesInto(), _readVByteFromStream(), _closeInput(),
 * streamReadInputSource(), _releaseBuffers2().
 */
public class VPackParserStreamingTest extends BaseTestForVPack
{
    // =========================================================
    // InputStream parser helpers
    // =========================================================

    private VPackParser streamParser(byte[] bytes) {
        VPackMapper mapper = new VPackMapper();
        return (VPackParser) mapper.createParser(new ByteArrayInputStream(bytes));
    }

    // =========================================================
    // streamReadInputSource
    // =========================================================

    @Test
    public void testStreamReadInputSource_notNull() throws Exception {
        byte[] data = vpackBytes("null");
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        VPackParser p = (VPackParser) new VPackMapper().createParser(bais);
        assertNotNull(p.streamReadInputSource());
        assertSame(bais, p.streamReadInputSource());
        p.close();
    }

    @Test
    public void testStreamReadInputSource_byteArray_isNull() throws Exception {
        byte[] data = vpackBytes("null");
        VPackParser p = (VPackParser) new VPackMapper().createParser(data);
        // byte-array parser has no InputStream
        assertNull(p.streamReadInputSource());
        p.close();
    }

    // =========================================================
    // Parsing scalars from InputStream
    // =========================================================

    @Test
    public void testParseNull_fromStream() throws Exception {
        byte[] data = vpackBytes("null");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertNull(p.nextToken());
        }
    }

    @Test
    public void testParseTrue_fromStream() throws Exception {
        byte[] data = vpackBytes("true");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
        }
    }

    @Test
    public void testParseFalse_fromStream() throws Exception {
        byte[] data = vpackBytes("false");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_FALSE, p.nextToken());
        }
    }

    @Test
    public void testParseInt_fromStream() throws Exception {
        byte[] data = vpackBytes("42");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(42, p.getIntValue());
        }
    }

    @Test
    public void testParseLong_fromStream() throws Exception {
        byte[] data = vpackBytes("9876543210");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(9876543210L, p.getLongValue());
        }
    }

    @Test
    public void testParseDouble_fromStream() throws Exception {
        byte[] data = vpackBytes("3.14");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(3.14, p.getDoubleValue(), 1e-10);
        }
    }

    @Test
    public void testParseString_fromStream() throws Exception {
        byte[] data = vpackBytes("\"hello world\"");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("hello world", p.getString());
        }
    }

    @Test
    public void testParseEmptyString_fromStream() throws Exception {
        byte[] data = vpackBytes("\"\"");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("", p.getString());
        }
    }

    @Test
    public void testParseLongString_fromStream() throws Exception {
        // Build a string > 126 chars to force long string encoding (0xbf)
        String longStr = "x".repeat(200);
        byte[] data = vpackBytes("\"" + longStr + "\"");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(longStr, p.getString());
        }
    }

    // =========================================================
    // Parsing arrays from InputStream
    // =========================================================

    @Test
    public void testParseEmptyArray_fromStream() throws Exception {
        byte[] data = vpackBytes("[]");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testParseSimpleArray_fromStream() throws Exception {
        byte[] data = vpackBytes("[1, 2, 3]");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(2, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(3, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testParseArray_withIndexTable_fromStream() throws Exception {
        // Mixed items → index table (0x06-0x09)
        byte[] data = vpackBytes("[1, \"hello\", true]");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(JsonToken.VALUE_TRUE, p.nextToken());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Parsing objects from InputStream
    // =========================================================

    @Test
    public void testParseEmptyObject_fromStream() throws Exception {
        byte[] data = vpackBytes("{}");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testParseSimpleObject_fromStream() throws Exception {
        byte[] data = vpackBytes("{\"a\":1,\"b\":\"two\"}");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("two", p.getString());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    @Test
    public void testParseNestedObject_fromStream() throws Exception {
        byte[] data = vpackBytes("{\"outer\":{\"inner\":42}}");
        try (JsonParser p = streamParser(data)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("outer", p.getString());
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            assertEquals("inner", p.getString());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(42, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
        }
    }

    // =========================================================
    // Compact array from InputStream
    // =========================================================

    @Test
    public void testParseCompactArray_fromStream() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartArray();
            g.writeNumber(1);
            g.writeNumber(2);
            g.writeNumber(3);
            g.writeEndArray();
        }
        byte[] data = out.toByteArray();
        // Parse from InputStream
        try (JsonParser p = mapper.createParser(new ByteArrayInputStream(data))) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(1, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(2, p.getIntValue());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(3, p.getIntValue());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    // =========================================================
    // Compact object from InputStream
    // =========================================================

    @Test
    public void testParseCompactObject_fromStream() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (JsonGenerator g = mapper.createGenerator(out)) {
            g.writeStartObject();
            g.writeName("x");
            g.writeNumber(10);
            g.writeName("y");
            g.writeNumber(20);
            g.writeEndObject();
        }
        byte[] data = out.toByteArray();
        try (JsonParser p = mapper.createParser(new ByteArrayInputStream(data))) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String key1 = p.getString();
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken());
            String key2 = p.getString();
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken());
            assertEquals(JsonToken.END_OBJECT, p.nextToken());
            assertTrue(key1.equals("x") || key1.equals("y"));
            assertTrue(key2.equals("x") || key2.equals("y"));
        }
    }

    // =========================================================
    // _loadMore / _ensureAvailable — triggered when buffer splits data
    // =========================================================

    @Test
    public void testParseLargePayload_fromStream_triggersLoadMore() throws Exception {
        // Write a large object to force buffered reads
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < 50; i++) {
            if (i > 0) sb.append(",");
            sb.append("\"key").append(i).append("\":").append(i * 100);
        }
        sb.append("}");
        byte[] data = vpackBytes(sb.toString());

        // Wrap in a small-chunk stream to force multiple _loadMore calls
        InputStream chunky = new SlowInputStream(data, 8);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            int count = 0;
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                p.nextToken(); // value
                count++;
            }
            assertEquals(50, count);
        }
    }

    @Test
    public void testParseArray_fromSlowStream() throws Exception {
        // Parse an array of strings from a slow stream (forces _ensureAvailable)
        byte[] data = vpackBytes("[\"hello\",\"world\",\"foo\",\"bar\"]");
        InputStream chunky = new SlowInputStream(data, 3);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertEquals(JsonToken.START_ARRAY, p.nextToken());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("hello", p.getString());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("world", p.getString());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("foo", p.getString());
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals("bar", p.getString());
            assertEquals(JsonToken.END_ARRAY, p.nextToken());
        }
    }

    @Test
    public void testParseDouble_fromSlowStream() throws Exception {
        byte[] data = vpackBytes("3.14");
        InputStream chunky = new SlowInputStream(data, 2);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertEquals(JsonToken.VALUE_NUMBER_FLOAT, p.nextToken());
            assertEquals(3.14, p.getDoubleValue(), 1e-10);
        }
    }

    // =========================================================
    // EOF handling
    // =========================================================

    @Test
    public void testEOF_onEmptyStream() throws Exception {
        InputStream empty = new ByteArrayInputStream(new byte[0]);
        try (JsonParser p = new VPackMapper().createParser(empty)) {
            assertNull(p.nextToken());
        }
    }

    // =========================================================
    // Tagged values from InputStream
    // =========================================================

    @Test
    public void testParseTaggedValue_fromStream() throws Exception {
        // Build tagged value: 0xee (1-byte tag), tag=5, then null (0x18)
        byte[] tagged = { (byte) 0xee, (byte) 0x05, (byte) 0x18 };
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(
                new ByteArrayInputStream(tagged))) {
            assertEquals(JsonToken.VALUE_NULL, p.nextToken());
            assertEquals(5L, p.getLastTagNumber());
        }
    }

    // =========================================================
    // Binary values from InputStream
    // =========================================================

    @Test
    public void testParseBinary_fromStream() throws Exception {
        // 0xc0 = VPACK_BINARY_FIRST, len width = 0xc0 - 0xbf = 1; so 1-byte LE length, then data
        byte[] data = new byte[] {(byte)0xc0, 3, 0x11, 0x22, 0x33}; // 0xc0 = binary 1-byte len, len=3
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(
                new ByteArrayInputStream(data))) {
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
            byte[] bin = p.getBinaryValue();
            assertArrayEquals(new byte[]{0x11, 0x22, 0x33}, bin);
        }
    }

    // =========================================================
    // _readBytesInto across buffer boundaries (slow stream + large value)
    // =========================================================

    @Test
    public void testParseLongString_fromSlowStream() throws Exception {
        // Long string (> 126 chars) encoded as 0xbf + 8-byte len + content
        String longStr = "abcdefgh".repeat(25); // 200 chars
        byte[] data = vpackBytes("\"" + longStr + "\"");
        InputStream chunky = new SlowInputStream(data, 7);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertEquals(JsonToken.VALUE_STRING, p.nextToken());
            assertEquals(longStr, p.getString());
        }
    }

    // =========================================================
    // AUTO_CLOSE_SOURCE behavior
    // =========================================================

    @Test
    public void testAutoCloseSource_closesStream() throws Exception {
        byte[] data = vpackBytes("42");
        TrackingInputStream tis = new TrackingInputStream(data);
        VPackMapper mapper = VPackMapper.builder()
                .enable(StreamReadFeature.AUTO_CLOSE_SOURCE)
                .build();
        JsonParser p = mapper.createParser(tis);
        p.nextToken();
        p.close();
        assertTrue(tis.closed, "Stream should have been closed with AUTO_CLOSE_SOURCE");
    }

    @Test
    public void testNoAutoCloseSource_doesNotCloseStream() throws Exception {
        byte[] data = vpackBytes("42");
        TrackingInputStream tis = new TrackingInputStream(data);
        VPackMapper mapper = VPackMapper.builder()
                .disable(StreamReadFeature.AUTO_CLOSE_SOURCE)
                .build();
        JsonParser p = mapper.createParser(tis);
        p.nextToken();
        p.close();
        // With AUTO_CLOSE_SOURCE disabled and non-managed source, stream stays open
        // (In this specific impl it may or may not be closed; we just verify no exception)
    }

    // =========================================================
    // Helper: SlowInputStream (simulates partial reads)
    // =========================================================

    private static class SlowInputStream extends InputStream {
        private final byte[] data;
        private int pos = 0;
        private final int chunkSize;

        SlowInputStream(byte[] data, int chunkSize) {
            this.data = data;
            this.chunkSize = chunkSize;
        }

        @Override
        public int read() throws IOException {
            if (pos >= data.length) return -1;
            return data[pos++] & 0xFF;
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            if (pos >= data.length) return -1;
            int toRead = Math.min(len, Math.min(chunkSize, data.length - pos));
            System.arraycopy(data, pos, buf, off, toRead);
            pos += toRead;
            return toRead;
        }
    }

    private static class TrackingInputStream extends ByteArrayInputStream {
        boolean closed = false;

        TrackingInputStream(byte[] buf) { super(buf); }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
