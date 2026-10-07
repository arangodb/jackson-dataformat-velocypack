package com.arangodb.jackson.dataformat.velocypack.parse;

import com.arangodb.jackson.dataformat.velocypack.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Independently assembled valid wire fixtures, with explicit token order assertions.
 * No production writer is used to create the container fixtures. */
public class ReaderRegressionTest {
    private final VPackMapper mapper = new VPackMapper();

    static byte[] bytes(int... values) {
        byte[] out = new byte[values.length];
        for (int i = 0; i < values.length; i++) out[i] = (byte) values[i];
        return out;
    }
    static byte[] concat(byte[]... parts) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (byte[] p : parts) out.writeBytes(p);
        return out.toByteArray();
    }
    static byte[] string(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        if (b.length <= 126) return concat(bytes(0x40 + b.length), b);
        return concat(bytes(0xbf), le(b.length, 8), b);
    }
    static byte[] le(long value, int width) {
        byte[] b = new byte[width];
        for (int i = 0; i < width; i++) b[i] = (byte) (value >>> (8 * i));
        return b;
    }
    static int widthIndex(int width) { return Integer.numberOfTrailingZeros(width); }

    // Small compact fixtures need one forward length byte and one reverse count byte.
    static byte[] compact(boolean object, byte[]... content) {
        byte[] body = concat(content);
        int count = object ? content.length / 2 : content.length;
        assertTrue(body.length + 3 < 128);
        return concat(bytes(object ? 0x14 : 0x13, body.length + 3), body, bytes(count));
    }
    static byte[] indexed(boolean object, boolean sorted, int width, boolean padding, byte[]... content) {
        int count = object ? content.length / 2 : content.length;
        int header = width == 8 ? 9 : 1 + 2 * width;
        int pad = padding ? 9 - header : 0;
        byte[] body = concat(content);
        int total = header + pad + body.length + count * width + (width == 8 ? 8 : 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write((object ? (sorted ? 0x0b : 0x0f) : 0x06) + widthIndex(width));
        out.writeBytes(le(total, width));
        if (width != 8) out.writeBytes(le(count, width));
        out.writeBytes(new byte[pad]);
        out.writeBytes(body);
        List<Integer> offsets = new ArrayList<>();
        int position = header + pad;
        for (int i = 0; i < content.length; i += object ? 2 : 1) {
            offsets.add(position);
            position += content[i].length + (object ? content[i + 1].length : 0);
        }
        // Tests deliberately supply physical z,a pairs; sorted index traverses a,z.
        if (object && sorted && count == 2) Collections.reverse(offsets);
        for (int offset : offsets) out.writeBytes(le(offset, width));
        if (width == 8) out.writeBytes(le(count, 8));
        return out.toByteArray();
    }
    static byte[] noIndex(int width, boolean padding) {
        int header = 1 + width;
        int pad = padding ? 9 - header : 0;
        return concat(bytes(0x02 + widthIndex(width)), le(header + pad + 2, width), new byte[pad], bytes(0x31, 0x32));
    }
    static List<String> transcript(JsonParser p) {
        try (p) {
            List<String> tokens = new ArrayList<>();
            JsonToken t;
            while ((t = p.nextToken()) != null) {
                String token = t.name();
                if (t == JsonToken.PROPERTY_NAME || t == JsonToken.VALUE_STRING) token += ":" + p.getString();
                if (t.isNumeric()) token += ":" + p.getNumberType() + ":" + p.getNumberValue();
                tokens.add(token);
            }
            return tokens;
        }
    }
    void allSources(byte[] fixture, List<String> expected) {
        assertEquals(expected, transcript(mapper.createParser(fixture)), "byte[]");
        byte[] wrapped = concat(bytes(0, 0, 0, 0, 0), fixture, bytes(0, 0x17, 0));
        assertEquals(expected, transcript(mapper.createParser(wrapped, 5, fixture.length)), "slice excludes poison prefix/trailer");
        for (int chunk : new int[]{1, 2, 3, 7}) {
            assertEquals(expected, transcript(mapper.createParser(new ShortReads(fixture, chunk))), "short reads " + chunk);
        }
    }
    static class ShortReads extends ByteArrayInputStream {
        final int chunk;
        boolean closed;
        ShortReads(byte[] b, int n) { super(b); chunk = n; }
        @Override public synchronized int read(byte[] b, int offset, int len) {
            return super.read(b, offset, Math.min(len, chunk));
        }
        @Override public void close() { closed = true; }
    }

    @Test void sortedIndexesTraverseIndexOrderAcrossWidthsAndPadding() {
        List<String> sorted = List.of("START_OBJECT", "PROPERTY_NAME:a", "START_ARRAY", "VALUE_NUMBER_INT:INT:2", "END_ARRAY",
                "PROPERTY_NAME:z", "START_OBJECT", "PROPERTY_NAME:é東京", "VALUE_STRING:Größe😀", "END_OBJECT", "END_OBJECT");
        List<String> physical = List.of("START_OBJECT", "PROPERTY_NAME:z", "START_OBJECT", "PROPERTY_NAME:é東京", "VALUE_STRING:Größe😀", "END_OBJECT",
                "PROPERTY_NAME:a", "START_ARRAY", "VALUE_NUMBER_INT:INT:2", "END_ARRAY", "END_OBJECT");
        for (int width : new int[]{1, 2, 4, 8}) {
            for (boolean pad : new boolean[]{false, true}) {
                byte[] obj = compact(true, string("é東京"), string("Größe😀"));
                byte[] arr = compact(false, bytes(0x32));
                allSources(indexed(true, true, width, pad, string("z"), obj, string("a"), arr), sorted);
                allSources(indexed(true, false, width, pad, string("z"), obj, string("a"), arr), physical);
            }
        }
    }
    @Test void nestedCompactIndexedAndNoIndexArraysAndRootSequences() {
        List<String> expected = List.of("START_ARRAY", "START_OBJECT", "PROPERTY_NAME:k", "START_ARRAY",
                "VALUE_NUMBER_INT:INT:1", "VALUE_NUMBER_INT:INT:2", "END_ARRAY", "END_OBJECT", "START_ARRAY", "END_ARRAY", "END_ARRAY",
                "VALUE_TRUE", "VALUE_NULL", "START_OBJECT", "END_OBJECT");
        for (int width : new int[]{1, 2, 4, 8}) {
            for (boolean pad : new boolean[]{false, true}) {
                byte[] obj = indexed(true, true, width, pad, string("k"), noIndex(width, pad));
                byte[] array = compact(false, obj, bytes(1));
                if (array.length >= 128) fail("fixture exceeded compact helper limit");
                allSources(concat(array, bytes(0x1a, 0x18, 0x0a)), expected);
                allSources(indexed(false, false, width, pad, obj, bytes(1)), expected.subList(0, 11));
            }
        }
    }
    @Test void numericTypesAndValuesAtRootAndInsideContainers() {
        byte[] signed = concat(bytes(0x27), le(Long.MIN_VALUE, 8));
        byte[] unsigned = concat(bytes(0x2f), le(-1L, 8));
        byte[] date = concat(bytes(0x1c), le(12345678901L, 8));
        byte[] floating = concat(bytes(0x1b), le(Double.doubleToRawLongBits(-0.0), 8));
        byte[] decimal = bytes(0xc8, 3, 0xfe, 0xff, 0xff, 0xff, 1, 0x23, 0x45);
        byte[] integralBcd = bytes(0xc8, 1, 0, 0, 0, 0, 0x42);
        List<String> scalar = List.of("VALUE_NUMBER_INT:INT:-6", "VALUE_NUMBER_INT:LONG:-9223372036854775808",
                "VALUE_NUMBER_INT:BIG_INTEGER:18446744073709551615", "VALUE_NUMBER_INT:LONG:12345678901",
                "VALUE_NUMBER_FLOAT:DOUBLE:-0.0", "VALUE_NUMBER_FLOAT:BIG_DECIMAL:123.45", "VALUE_NUMBER_INT:BIG_INTEGER:42");
        byte[][] values = {bytes(0x3a), signed, unsigned, date, floating, decimal, integralBcd};
        allSources(concat(values), scalar);
        List<String> nested = new ArrayList<>(List.of("START_ARRAY"));
        nested.addAll(scalar); nested.add("END_ARRAY");
        allSources(compact(false, values), nested);
        try (JsonParser p = mapper.createParser(unsigned)) {
            p.nextToken();
            assertEquals(new BigInteger("18446744073709551615"), p.getBigIntegerValue());
            assertThrows(tools.jackson.core.exc.InputCoercionException.class, p::getLongValue);
        }
    }
    @Test void contextNamesValuesAndSkipChildren() {
        byte[] b = compact(true, string("outer"), compact(false, compact(true, string("inner"), bytes(0x31))), string("tail"), bytes(0x32));
        try (JsonParser p = mapper.createParser(b)) {
            assertEquals(JsonToken.START_OBJECT, p.nextToken());
            Object parent = new Object(); p.assignCurrentValue(parent);
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken()); assertEquals("outer", p.currentName());
            assertEquals(JsonToken.START_ARRAY, p.nextToken()); assertEquals("outer", p.currentName());
            assertTrue(p.streamReadContext().inArray());
            assertSame(parent, p.streamReadContext().getParent().currentValue());
            Object child = new Object(); p.assignCurrentValue(child); assertSame(child, p.currentValue());
            p.skipChildren();
            assertEquals(JsonToken.END_ARRAY, p.currentToken()); assertSame(parent, p.currentValue());
            assertEquals(JsonToken.PROPERTY_NAME, p.nextToken()); assertEquals("tail", p.currentName());
            assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken()); assertEquals(2, p.getIntValue());
            assertEquals(JsonToken.END_OBJECT, p.nextToken()); assertTrue(p.streamReadContext().inRoot());
            assertNull(p.nextToken()); assertTrue(p.isClosed());
        }
    }
    @Test void duplicateDetectionFromIndependentBytesAndDefaultTokenOrder() {
        byte[] duplicate = compact(true, string("same"), bytes(0x31), string("same"), bytes(0x32));
        allSources(duplicate, List.of("START_OBJECT", "PROPERTY_NAME:same", "VALUE_NUMBER_INT:INT:1", "PROPERTY_NAME:same", "VALUE_NUMBER_INT:INT:2", "END_OBJECT"));
        VPackMapper strict = VPackMapper.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        for (byte[] fixture : new byte[][]{duplicate, compact(false, duplicate)}) {
            assertThrows(StreamReadException.class, () -> transcript(strict.createParser(fixture)));
        }
    }
    @Test void taggedCustomAndBinaryOwnershipWithOrdinarySiblings() {
        byte[] tag = bytes(0xee, 7, 0x31), custom = bytes(0xf4, 2, 0x55, 0x66), binary = bytes(0xc0, 3, 1, 2, 3);
        for (boolean nested : new boolean[]{false, true}) {
            byte[] source = nested ? compact(false, tag, custom, binary, bytes(0x32)) : concat(tag, custom, binary, bytes(0x32));
            try (VPackParser p = (VPackParser) mapper.createParser(source)) {
                if (nested) assertEquals(JsonToken.START_ARRAY, p.nextToken());
                assertEquals(JsonToken.VALUE_NUMBER_INT, p.nextToken()); assertEquals(7, p.getLastTagNumber());
                assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken()); assertEquals(-1, p.getLastTagNumber());
                VPackCustomValue value = (VPackCustomValue) p.getEmbeddedObject();
                assertEquals(0xf4, value.getTypeByte()); assertArrayEquals(bytes(0x55, 0x66), value.getPayload());
                assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, p.nextToken());
                byte[] owned = p.getBinaryValue(); assertSame(owned, p.getEmbeddedObject());
                Arrays.fill(source, (byte) 0);
                assertArrayEquals(bytes(1, 2, 3), owned);
                p.close(); assertArrayEquals(bytes(1, 2, 3), owned);
                byte[] copy = value.getPayload(); copy[0] = 0; assertArrayEquals(bytes(0x55, 0x66), value.getPayload());
            }
        }
        VPackMapper rejectTags = VPackMapper.builder().enable(VPackReadFeature.FAIL_ON_TAGGED_VALUES).build();
        VPackMapper rejectCustom = VPackMapper.builder().enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES).build();
        assertThrows(StreamReadException.class, () -> transcript(rejectTags.createParser(compact(false, tag))));
        assertThrows(StreamReadException.class, () -> transcript(rejectCustom.createParser(compact(false, custom))));
    }
    @Test void closeOwnershipAndEof() {
        for (boolean autoClose : new boolean[]{false, true}) {
            VPackMapper m = VPackMapper.builder().configure(StreamReadFeature.AUTO_CLOSE_SOURCE, autoClose).build();
            ShortReads stream = new ShortReads(compact(false, bytes(0x31)), 1);
            JsonParser p = m.createParser(stream);
            assertSame(stream, p.streamReadInputSource());
            p.nextToken(); p.close(); p.close(); assertTrue(p.isClosed()); assertEquals(autoClose, stream.closed);
            stream = new ShortReads(bytes(0x18), 1);
            p = m.createParser(stream); p.nextToken(); assertNull(p.nextToken()); assertTrue(p.isClosed()); assertEquals(autoClose, stream.closed);
        }
    }
    @Test void enforcedReadConstraintsAndIllegalTypeRejection() {
        byte[] deep = compact(false, compact(false, bytes(0x31)));
        VPackMapper depth = constrained(StreamReadConstraints.builder().maxNestingDepth(1).build());
        assertThrows(StreamConstraintsException.class, () -> transcript(depth.createParser(deep)));
        VPackMapper size = constrained(StreamReadConstraints.builder().maxDocumentLength(2).build());
        assertThrows(StreamConstraintsException.class, () -> size.createParser(deep));
        VPackMapper text = constrained(StreamReadConstraints.builder().maxStringLength(128).build());
        assertThrows(StreamConstraintsException.class, () -> transcript(text.createParser(string("x".repeat(129)))));
        for (int illegal : new int[]{0, 0x17, 0x1d, 0x15, 0xd8}) {
            assertThrows(StreamReadException.class, () -> transcript(mapper.createParser(bytes(illegal))));
        }
    }
    static VPackMapper constrained(StreamReadConstraints c) {
        return VPackMapper.builder(VPackFactory.builder().streamReadConstraints(c).build()).build();
    }
}
