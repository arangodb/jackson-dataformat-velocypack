package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.SerializedString;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HexFormat;
import static org.junit.jupiter.api.Assertions.*;
import static com.arangodb.jackson.dataformat.velocypack.WriterReferenceWire.*;
import static com.arangodb.jackson.dataformat.velocypack.VPackWriterDifferentialTest.*;

class VPackWriterPayloadTest {
    private static VPackGenerator generator(ByteArrayOutputStream out) {
        return new VPackGenerator(ObjectWriteContext.empty(), BaseTestForVPack.testIOContext(),
                StreamWriteFeature.collectDefaults(), DEFAULTS, out);
    }

    @Test void checkedArithmeticCoversUnallocatableWidthsAndOverflow() {
        assertEquals(255, VPackGenerator.framedSize(250, 2, 1, true));
        assertEquals(259, VPackGenerator.framedSize(250, 2, 2, true));
        assertEquals(2, VPackGenerator.containerWidth(251, 2, true));
        assertEquals(4, VPackGenerator.containerWidth(65527, 2, true));
        assertEquals(4294967337L, VPackGenerator.framedSize(4294967296L, 3, 8, true));
        assertEquals(4294967305L, VPackGenerator.framedSize(4294967296L, 3, 8, false));
        assertEquals(8, VPackGenerator.containerWidth(4294967296L, 3, true));
        assertEquals(8, VPackGenerator.containerWidth(4294967296L, 3, false));
        assertEquals(Integer.MAX_VALUE - 8, VPackGenerator.checkedArraySize(Integer.MAX_VALUE - 8L));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.checkedArraySize(Integer.MAX_VALUE - 7L));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.checkedArraySize(-1));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.checkedArraySize(4294967337L));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.framedSize(Long.MAX_VALUE, 1, 8, true));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.framedSize(1, Long.MAX_VALUE, 8, true));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.framedSize(-1, 1, 1, false));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.framedSize(1, -1, 1, true));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.framedSize(1, 1, 3, true));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.containerWidth(Long.MAX_VALUE, 1, true));
        assertEquals(Long.MAX_VALUE, VPackGenerator.compactSize(Long.MAX_VALUE - 11, 1));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.compactSize(Long.MAX_VALUE, 1));
        assertThrows(IllegalStateException.class, () -> VPackGenerator.compactSize(1, -1));
    }

    @Test void compactFixedPointIsMinimalAtAllJavaArrayBoundaries() {
        long[][] vectors = {{124,127}, {125,129}, {16379,16383}, {16380,16385},
                {2097146,2097151}, {2097147,2097153}, {268435449,268435455}, {268435450,268435457}};
        for (long[] v : vectors) assertEquals(v[1], VPackGenerator.compactSize(v[0], 1));
        assertEquals(132, VPackGenerator.compactSize(127, 128));
        assertEquals(16391, VPackGenerator.compactSize(16384, 16384));
        // Exercise actual 4-byte length encoding with only a 2 MB allocation.
        int binaryLength = 2097142; // c3 + four-byte length + payload = 2097147
        byte[] expected = compact(false, 1, binary(binaryLength));
        assertEquals(2097153, expected.length);
        assertArrayEquals(expected, WriterReplay.encode(false, DEFAULTS, w -> {
            w.g.writeStartArray(); w.g.writeBinary(new byte[binaryLength]); w.g.writeEndArray();
        }));
    }

    @Test void childSegmentsRetireAndRootStorageIsReusedWithoutOutputCopy() {
        var out = new TrackingOutput() {
            byte[] lastArray;
            @Override public synchronized void write(byte[] b, int off, int len) {
                lastArray = b;
                super.write(b, off, len);
            }
        };
        try (var g = generator(out)) {
            assertNull(g._payload);
            g.writeNull();
            g.writeStartArray();
            assertNotSame(g._outputBuffer, g._payload);
            assertEquals(9, g._payloadTail);
            assertEquals(1, g.streamWriteOutputBuffered());
            int rootStart = g._frameStarts[0];
            for (int i = 0; i < 24; ++i) {
                g.writeStartArray();
                for (int j = 0; j < 1000; ++j) g.writeNull();
                g.writeEndArray();
                assertEquals(i + 1, g._offsetTail);
                assertEquals(i + 1, g._frameCounts[0]);
                assertEquals(rootStart, g._frameStarts[0]);
                g.flush();
                assertEquals(0, out.size()); // preceding scalar also stays in its original output buffer
            }
            assertTrue(g._offsets.length < 2000); // not all 24,000 completed grandchildren
            g.writeEndArray();
            assertSame(g._payload, out.lastArray); // OutputStream receives arena range directly
            assertEquals(0, g._payloadTail);
            assertEquals(0, g._offsetTail);
            assertEquals(0, g._depth);
            assertEquals(0, g.streamWriteOutputBuffered());
            byte[] arena = g._payload;
            int firstRootEnd = out.size();
            g.writeStartObject(); g.writeEndObject();
            assertSame(arena, g._payload);
            assertSame(arena, out.lastArray);
            assertEquals(firstRootEnd + 1, out.size());
        }
    }

    @Test void deepFramesGrowAndAncestorsSurviveCompaction() {
        for (int mask : new int[]{0, DEFAULTS, 15}) same(mask, w -> {
            for (int i = 0; i < 128; ++i) {
                if ((i & 1) == 0) { w.g.writeStartArray(); w.g.writeNull(); }
                else { w.g.writeStartObject(); w.g.writeName("a"); }
            }
            w.g.writeString("leaf");
            for (int i = 127; i >= 0; --i) {
                if ((i & 1) == 0) { w.g.writeBoolean(true); w.g.writeEndArray(); }
                else w.g.writeEndObject();
            }
        });
    }

    @Test void stablePhysicalSortingWithLongUtf8KeysNumericIdsAndNestedValues() {
        for (int mask : new int[]{1, 5, 9, 13}) same(mask, w -> {
            w.g.writeStartArray();
            for (int round = 0; round < 3; ++round) {
                w.g.writeStartObject();
                for (String key : new String[]{"🚀", "z".repeat(127), "", "é", "a".repeat(127), "é", "a"}) {
                    w.g.writeName(key); w.g.writeStartArray(); w.g.writeNumber(round);
                    w.g.writeString(key); w.g.writeEndArray();
                }
                for (long id : new long[]{256, 1L << 32, 255, 0, 65536, 256}) {
                    w.g.writePropertyId(id); w.g.writeNull();
                }
                w.g.writeEndObject();
            }
            w.g.writeEndArray();
        });
    }

    @Test void primitiveShortcutsAdvanceEachItemContextOnce() {
        var out = new ByteArrayOutputStream();
        try (var g = new VPackGenerator(ObjectWriteContext.empty(), BaseTestForVPack.testIOContext(),
                0, DEFAULTS, out) {
            @Override protected void _valueFinished() {
                if (_depth > 0 && _streamWriteContext.inArray()) {
                    assertEquals(_frameCounts[_depth - 1], _streamWriteContext.getCurrentIndex());
                }
                super._valueFinished();
            }
        }) {
            g.writeStartArray();
            g.writeArray(new int[]{1, 2, 3}, 0, 3);
            g.writeArray(new long[]{Long.MIN_VALUE, Long.MAX_VALUE}, 0, 2);
            g.writeArray(new double[]{-0.0, Double.longBitsToDouble(0x7ff8000000000042L)}, 0, 2);
            assertEquals(3, g._frameCounts[0]);
            assertEquals(2, g.streamWriteContext().getCurrentIndex());
            g.writeEndArray();
        }
    }

    @Test void nestedRawFragmentsAreRejectedAndRootBlocksStillPassThrough() {
        for (boolean object : new boolean[]{false, true}) {
            var out = new ByteArrayOutputStream();
            try (var g = generator(out)) {
                if (object) { g.writeStartObject(); g.writeName("a"); }
                else g.writeStartArray();
                int index = g.streamWriteContext().getCurrentIndex();
                assertThrows(StreamWriteException.class, () -> g.writeRaw((byte) 0));
                assertThrows(StreamWriteException.class, () -> g.writeBytes(new byte[]{0x18, 0x1a}, 0, 2));
                assertEquals(index, g.streamWriteContext().getCurrentIndex());
                g.writeRawValue(new SerializedString("\u0018"));
                if (object) g.writeEndObject(); else g.writeEndArray();
            }
        }
        assertArrayEquals(new byte[]{0, 0x18, 0x1a}, WriterReplay.encode(false, DEFAULTS, w -> {
            w.raw((byte) 0); w.bytes(new byte[]{0x18, 0x1a}, 0, 2);
        }));
    }

    @Test void noneCannotBecomeArrayPaddingButValidPreencodedPaddingIsPreserved() {
        for (int mask : new int[]{0, DEFAULTS}) {
            assertThrows(StreamWriteException.class, () -> WriterReplay.encode(false, mask, w -> {
                w.g.writeStartArray(); w.g.writeRawValue(new SerializedString("\u0000"));
            }));
            // A valid no-index child with a nine-byte padded header, then null.
            byte[] padded = HexFormat.of().parseHex("020a0000000000000018");
            byte[] expected = (mask & 2) != 0 ? compact(false, 1, padded) : noIndex(padded);
            assertArrayEquals(expected, same(mask, w -> {
                w.g.writeStartArray();
                w.g.writeRawValue(new SerializedString(new String(padded, java.nio.charset.StandardCharsets.ISO_8859_1)));
                w.g.writeEndArray();
            }));
            var mapper = new VPackMapper();
            assertEquals(mapper.readTree(expected).toString(), "[[null]]");
        }
    }

    @Test void smallCustomBuffersKeepNumericBitsAndPrefixOwnership() {
        for (int size : new int[]{1, 2, 7, 8, 9, 16}) {
            var out = new ByteArrayOutputStream();
            byte[] buf = new byte[size]; Arrays.fill(buf, (byte) 0x18);
            try (var w = new WriterReplay(false, DEFAULTS, 0, out, buf, size)) {
                w.g.writeNumber(Long.MIN_VALUE);
                w.g.writeNumber(Double.longBitsToDouble(0x7ff8000000000042L));
                w.g.writeStartArray(); w.g.writeNumber(Long.MAX_VALUE); w.g.writeEndArray();
            }
            byte[] prefix = new byte[size]; Arrays.fill(prefix, (byte) 0x18);
            assertArrayEquals(cat(prefix, HexFormat.of().parseHex("2700000000000000801b420000000000f87f"),
                    compact(false, 1, HexFormat.of().parseHex("27ffffffffffffff7f"))), out.toByteArray());
        }
    }

    @Test void binaryStreamsReadIntoSharedStorageAndKeepRootVisibility() {
        var out = new ByteArrayOutputStream();
        try (var g = generator(out)) {
            g.writeStartArray();
            var stream = new ByteArrayInputStream(new byte[65536]) {
                @Override public synchronized int read(byte[] b, int off, int len) {
                    assertSame(g._payload, b);
                    return super.read(b, off, Math.min(7, len));
                }
            };
            assertEquals(65536, g.writeBinary(stream, 65536));
            g.flush(); assertEquals(0, out.size());
            g.writeEndArray();
        }
        assertArrayEquals(compact(false, 1, binary(65536)), out.toByteArray());
        for (int mask = 0; mask < 16; ++mask) same(mask, w -> {
            w.g.writeStartArray();
            for (int len : new int[]{0, 3, 255, 256, 8193, 65536}) {
                assertEquals(len, w.g.writeBinary(new ByteArrayInputStream(new byte[len]), len));
            }
            w.g.writeEndArray();
        });
        for (int offset : new int[]{0, 7, 255, 256}) for (int len : new int[]{0, 248, 249, 255, 256, 257, 1000}) {
            int[] visible = new int[2], buffered = new int[2];
            byte[][] encoded = new byte[2][];
            for (int writer = 0; writer < 2; ++writer) {
                var target = new ByteArrayOutputStream();
                byte[] buf = new byte[256]; Arrays.fill(buf, 0, offset, (byte) 0x18);
                try (var w = new WriterReplay(writer == 0, DEFAULTS, 0, target, buf, offset)) {
                    w.g.writeBinary(new ByteArrayInputStream(new byte[len]), len);
                    visible[writer] = target.size();
                    buffered[writer] = w.g.streamWriteOutputBuffered();
                }
                encoded[writer] = target.toByteArray();
            }
            assertEquals(visible[0], visible[1]);
            assertEquals(buffered[0], buffered[1]);
            assertArrayEquals(encoded[0], encoded[1]);
        }
    }

    @Test void unfinishedContainersAndDanglingTagsNeverExposeHeaders() {
        var out = new ByteArrayOutputStream();
        try (var g = generator(out)) {
            g.writeStartArray(); g.writeNull(); g.writeTaggedValuePrefix(7);
            assertThrows(StreamWriteException.class, g::writeEndArray);
            g.flush(); assertEquals(0, out.size());
            g.writeStartObject(); g.writeName("a"); g.writeNull(); g.writeEndObject(); g.writeEndArray();
        }
        assertArrayEquals(compact(false, 2, cat(new byte[]{0x18, (byte) 0xee, 7},
                compact(true, 1, new byte[]{0x41, 0x61, 0x18}))), out.toByteArray());
        var incomplete = new ByteArrayOutputStream();
        try (var g = generator(incomplete)) {
            g.writeStartObject(); g.writeName("a");
            assertThrows(StreamWriteException.class, g::writeEndObject);
        }
        assertEquals(0, incomplete.size());
    }
}
