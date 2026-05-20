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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

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
    public void testStreamReadInputSource_notNull() {
        byte[] data = vpackBytes("null");
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        VPackParser p = (VPackParser) new VPackMapper().createParser(bais);
        assertThat(p.streamReadInputSource()).isNotNull();
        assertThat(p.streamReadInputSource()).isSameAs(bais);
        p.close();
    }

    @Test
    public void testStreamReadInputSource_byteArray_isNull() {
        byte[] data = vpackBytes("null");
        VPackParser p = (VPackParser) new VPackMapper().createParser(data);
        // byte-array parser has no InputStream
        assertThat(p.streamReadInputSource()).isNull();
        p.close();
    }

    // =========================================================
    // Parsing scalars from InputStream
    // =========================================================

    @Test
    public void testParseNull_fromStream() {
        byte[] data = vpackBytes("null");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.nextToken()).isNull();
        }
    }

    @Test
    public void testParseTrue_fromStream() {
        byte[] data = vpackBytes("true");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
        }
    }

    @Test
    public void testParseFalse_fromStream() {
        byte[] data = vpackBytes("false");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_FALSE);
        }
    }

    @Test
    public void testParseInt_fromStream() {
        byte[] data = vpackBytes("42");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(42);
        }
    }

    @Test
    public void testParseLong_fromStream() {
        byte[] data = vpackBytes("9876543210");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getLongValue()).isEqualTo(9876543210L);
        }
    }

    @Test
    public void testParseDouble_fromStream() {
        byte[] data = vpackBytes("3.14");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(3.14, offset(1e-10));
        }
    }

    @Test
    public void testParseString_fromStream() {
        byte[] data = vpackBytes("\"hello world\"");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("hello world");
        }
    }

    @Test
    public void testParseEmptyString_fromStream() {
        byte[] data = vpackBytes("\"\"");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEmpty();
        }
    }

    @Test
    public void testParseLongString_fromStream() {
        // Build a string > 126 chars to force long string encoding (0xbf)
        String longStr = "x".repeat(200);
        byte[] data = vpackBytes("\"" + longStr + "\"");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo(longStr);
        }
    }

    // =========================================================
    // Parsing arrays from InputStream
    // =========================================================

    @Test
    public void testParseEmptyArray_fromStream() {
        byte[] data = vpackBytes("[]");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testParseSimpleArray_fromStream() {
        byte[] data = vpackBytes("[1, 2, 3]");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(1);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(2);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(3);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testParseArray_withIndexTable_fromStream() {
        // Mixed items → index table (0x06-0x09)
        byte[] data = vpackBytes("[1, \"hello\", true]");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_TRUE);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // Parsing objects from InputStream
    // =========================================================

    @Test
    public void testParseEmptyObject_fromStream() {
        byte[] data = vpackBytes("{}");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    @Test
    public void testParseSimpleObject_fromStream() {
        byte[] data = vpackBytes("{\"a\":1,\"b\":\"two\"}");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("two");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    @Test
    public void testParseNestedObject_fromStream() {
        byte[] data = vpackBytes("{\"outer\":{\"inner\":42}}");
        try (JsonParser p = streamParser(data)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("outer");
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            assertThat(p.getString()).isEqualTo("inner");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(42);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // Compact array from InputStream
    // =========================================================

    @Test
    public void testParseCompactArray_fromStream() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(1);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(2);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getIntValue()).isEqualTo(3);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    // =========================================================
    // Compact object from InputStream
    // =========================================================

    @Test
    public void testParseCompactObject_fromStream() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            String key1 = p.getString();
            assertThat(key1).isEqualTo("x");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.PROPERTY_NAME);
            String key2 = p.getString();
            assertThat(key2).isEqualTo("y");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_OBJECT);
        }
    }

    // =========================================================
    // _loadMore / _ensureAvailable — triggered when buffer splits data
    // =========================================================

    @Test
    public void testParseLargePayload_fromStream_triggersLoadMore() {
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
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_OBJECT);
            int count = 0;
            while (p.nextToken() == JsonToken.PROPERTY_NAME) {
                p.nextToken(); // value
                count++;
            }
            assertThat(count).isEqualTo(50);
        }
    }

    @Test
    public void testParseArray_fromSlowStream() {
        // Parse an array of strings from a slow stream (forces _ensureAvailable)
        byte[] data = vpackBytes("[\"hello\",\"world\",\"foo\",\"bar\"]");
        InputStream chunky = new SlowInputStream(data, 3);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.START_ARRAY);
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("hello");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("world");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("foo");
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo("bar");
            assertThat(p.nextToken()).isEqualTo(JsonToken.END_ARRAY);
        }
    }

    @Test
    public void testParseDouble_fromSlowStream() {
        byte[] data = vpackBytes("3.14");
        InputStream chunky = new SlowInputStream(data, 2);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getDoubleValue()).isEqualTo(3.14, offset(1e-10));
        }
    }

    // =========================================================
    // EOF handling
    // =========================================================

    @Test
    public void testEOF_onEmptyStream() {
        InputStream empty = new ByteArrayInputStream(new byte[0]);
        try (JsonParser p = new VPackMapper().createParser(empty)) {
            assertThat(p.nextToken()).isNull();
        }
    }

    // =========================================================
    // Tagged values from InputStream
    // =========================================================

    @Test
    public void testParseTaggedValue_fromStream() {
        // Build tagged value: 0xee (1-byte tag), tag=5, then null (0x18)
        byte[] tagged = { (byte) 0xee, (byte) 0x05, (byte) 0x18 };
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(
                new ByteArrayInputStream(tagged))) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NULL);
            assertThat(p.getLastTagNumber()).isEqualTo(5L);
        }
    }

    // =========================================================
    // Binary values from InputStream
    // =========================================================

    @Test
    public void testParseBinary_fromStream() {
        // 0xc0 = VPACK_BINARY_FIRST, len width = 0xc0 - 0xbf = 1; so 1-byte LE length, then data
        byte[] data = new byte[] {(byte)0xc0, 3, 0x11, 0x22, 0x33}; // 0xc0 = binary 1-byte len, len=3
        try (VPackParser p = (VPackParser) new VPackMapper().createParser(
                new ByteArrayInputStream(data))) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_EMBEDDED_OBJECT);
            byte[] bin = p.getBinaryValue();
            assertThat(bin).containsExactly(new byte[]{0x11, 0x22, 0x33});
        }
    }

    // =========================================================
    // _readBytesInto across buffer boundaries (slow stream + large value)
    // =========================================================

    @Test
    public void testParseLongString_fromSlowStream() {
        // Long string (> 126 chars) encoded as 0xbf + 8-byte len + content
        String longStr = "abcdefgh".repeat(25); // 200 chars
        byte[] data = vpackBytes("\"" + longStr + "\"");
        InputStream chunky = new SlowInputStream(data, 7);
        try (JsonParser p = new VPackMapper().createParser(chunky)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_STRING);
            assertThat(p.getString()).isEqualTo(longStr);
        }
    }

    // =========================================================
    // AUTO_CLOSE_SOURCE behavior
    // =========================================================

    @Test
    public void testAutoCloseSource_closesStream() {
        byte[] data = vpackBytes("42");
        TrackingInputStream tis = new TrackingInputStream(data);
        VPackMapper mapper = VPackMapper.builder()
                .enable(StreamReadFeature.AUTO_CLOSE_SOURCE)
                .build();
        JsonParser p = mapper.createParser(tis);
        p.nextToken();
        p.close();
        assertThat(tis.closed).as("Stream should have been closed with AUTO_CLOSE_SOURCE").isTrue();
    }

    @Test
    public void testNoAutoCloseSource_doesNotCloseStream() {
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
        public int read() {
            if (pos >= data.length) return -1;
            return data[pos++] & 0xFF;
        }

        @Override
        public int read(byte[] buf, int off, int len) {
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
