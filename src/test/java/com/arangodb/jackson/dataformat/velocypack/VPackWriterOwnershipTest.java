package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.StreamWriteException;
import tools.jackson.core.io.*;
import tools.jackson.core.util.BufferRecycler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static com.arangodb.jackson.dataformat.velocypack.VPackWriterDifferentialTest.*;
import static com.arangodb.jackson.dataformat.velocypack.WriterReferenceWire.*;

class VPackWriterOwnershipTest {
    private static byte[] string(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        return cat(bytes.length <= 126 ? new byte[]{(byte) (0x40 + bytes.length)}
                : cat(new byte[]{(byte) 0xbf}, le(bytes.length, 8)), bytes);
    }

    @Test void utf8HeadersUseBytesForEveryTextOverloadAndLayout() {
        for (int mask = 0; mask < 32; ++mask) {
            for (String s : new String[]{"", "a".repeat(126), "a".repeat(127),
                    "é".repeat(63), "é".repeat(63) + "x", "東".repeat(42),
                    "東".repeat(42) + "x", "🚀".repeat(31) + "é", "🚀".repeat(31) + "東",
                    "\u0000\u007f\u0080\u07ff\u0800\uffff", "é東京🚀".repeat(1000),
                    "\ud800", "\udc00", "\ud800x\udc00", "\ud800\ud800\udc00\udc00"}) {
                byte[] value = string(s);
                assertArrayEquals(cat(value, value, value), same(mask, w -> {
                    w.g.writeString(s);
                    char[] chars = ("!" + s + "!").toCharArray();
                    w.g.writeString(chars, 1, chars.length - 2);
                    w.g.writeString(new SerializedString(s));
                }));
                same(mask, w -> {
                    w.g.writeStartObject();
                    w.g.writeName(s); w.g.writeString(s);
                    w.g.writeName(new SerializedString(s));
                    w.g.writeString(s.toCharArray(), 0, s.length());
                    w.g.writeEndObject();
                });
            }
        }
    }

    @Test void charRangesDoNotPairAcrossSliceBoundariesAndAreCopied() {
        char[] source = {'!', '\ud83d', '\ude80', '\ud800', '\udc00', 'x', '\ud800', '!'};
        for (int off = 0; off <= source.length; ++off) {
            for (int len = 0; len <= source.length - off; ++len) {
                int start = off, size = len;
                byte[] expected = string(new String(source, off, len));
                assertArrayEquals(expected, same(DEFAULTS, w -> w.g.writeString(source, start, size)));
            }
        }
        for (int mask : new int[]{0, DEFAULTS, 31}) same(mask, w -> {
            char[] input = source.clone();
            w.g.writeStartArray(); w.g.writeString(input, 1, 6);
            Arrays.fill(input, 'z'); w.g.writeEndArray();
        });
    }

    @Test void arbitraryUtf16MatchesLegacyReplacementIncludingAllSingleChars() {
        StringBuilder all = new StringBuilder();
        // Separators ensure each surrogate is independently malformed.
        for (int c = 0; c <= 0xffff; ++c) all.append((char) c).append('x');
        Random random = new Random(0x3388);
        for (int round = 0; round < 30; ++round) {
            char[] chars = new char[1000];
            for (int i = 0; i < chars.length; ++i) chars[i] = (char) random.nextInt(65536);
            all.append(chars);
        }
        String s = all.toString();
        for (int mask : new int[]{0, DEFAULTS, 31}) same(mask, w -> {
            w.g.writeString(s); w.g.writeStartArray();
            w.g.writeString(s); w.g.writeString(s.toCharArray(), 1, s.length() - 2);
            w.g.writeEndArray();
        });
    }

    @Test void serializableUtf8HasDifferentErrorPolicyAndNormalWritesUseValue() {
        for (String s : new String[]{"\ud800", "\udc00", "\ud800x"}) {
            SerializedString serial = new SerializedString(s);
            assertThrows(IllegalArgumentException.class, serial::asUnquotedUTF8);
            for (int mask : new int[]{DEFAULTS, DEFAULTS | 16}) {
                assertArrayEquals(string(s), same(mask, w -> w.g.writeString(serial)));
                same(mask, w -> {
                    w.g.writeStartObject(); w.g.writeName(serial); w.g.writeNull(); w.g.writeEndObject();
                });
            }
        }
    }

    @Test void nullAndEmptyTextKeepOverloadBehavior() {
        assertArrayEquals(new byte[]{0x18, 0x40, 0x40, 0x40}, same(DEFAULTS, w -> {
            w.g.writeString((String) null); w.g.writeString("");
            w.g.writeString(new char[0], 0, 0); w.g.writeString(new SerializedString(""));
        }));
        for (boolean legacy : new boolean[]{false, true}) {
            assertThrows(NullPointerException.class, () -> WriterReplay.encode(legacy, DEFAULTS,
                    w -> w.g.writeString((char[]) null, 0, 0)));
            assertThrows(NullPointerException.class, () -> WriterReplay.encode(legacy, DEFAULTS,
                    w -> w.g.writeString((SerializableString) null)));
            assertThrows(NullPointerException.class, () -> WriterReplay.encode(legacy, DEFAULTS,
                    w -> { w.g.writeStartObject(); w.g.writeName((String) null); }));
        }
    }

    @Test void rawUtf8IsCopiedWithoutValidationOrReencoding() {
        byte[] raw = {(byte) 0xff, (byte) 0xc0, (byte) 0x80, (byte) 0xed, (byte) 0xa0, (byte) 0x80};
        for (int mask : new int[]{0, DEFAULTS, 31}) same(mask, w -> {
            byte[] input = raw.clone();
            w.g.writeStartArray();
            w.g.writeUTF8String(input, 0, input.length);
            w.g.writeRawUTF8String(input, 1, input.length - 1);
            Arrays.fill(input, (byte) 0);
            w.g.writeEndArray();
        });
    }

    @Test void rawCustomValuesKeepExactBytesAndCallerArrayOwnership() {
        byte[] custom = {(byte) 0xf0, (byte) 0xff}; // one fixed-length custom value
        for (int mask = 0; mask < 16; ++mask) {
            assertArrayEquals(custom, same(mask, w -> w.g.writeRawValue(raw(custom))));
            same(mask, w -> {
                byte[] input = custom.clone();
                w.g.writeStartArray(); w.g.writeRawValue(raw(input));
                w.g.writeStartObject(); w.g.writeName("z"); w.g.writeRawValue(raw(input));
                w.g.writeName("a"); w.g.writeNull(); w.g.writeEndObject();
                Arrays.fill(input, (byte) 0); w.g.writeEndArray();
            });
            byte[] item = cat(new byte[]{(byte) 0xee, 7}, custom);
            byte[] expected = (mask & 2) != 0 ? compact(false, 2, cat(item, new byte[]{0x18}))
                    : indexed(false, false, item, new byte[]{0x18});
            assertArrayEquals(expected, WriterReplay.encode(false, mask, w -> {
                w.g.writeStartArray(); w.tag(7); w.g.writeRawValue(raw(custom));
                w.g.writeNull(); w.g.writeEndArray();
            }));
        }
    }

    private static SerializableString raw(byte[] bytes) {
        return (SerializableString) java.lang.reflect.Proxy.newProxyInstance(
                SerializableString.class.getClassLoader(), new Class<?>[]{SerializableString.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("asUnquotedUTF8")) return bytes;
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    @Test void rootStringsAndBinaryUseBoundedStorageAndLegacyVisibility() {
        for (int bufferSize : new int[]{1, 2, 7, 256}) {
            for (String s : new String[]{"x".repeat(20000), "é東京🚀".repeat(10000)}) {
                var out = new ByteArrayOutputStream();
                byte[] buffer = new byte[bufferSize]; Arrays.fill(buffer, (byte) 0x18);
                try (var g = new VPackGenerator(ObjectWriteContext.empty(), BaseTestForVPack.testIOContext(),
                        0, DEFAULTS, out, buffer, bufferSize, false)) {
                    g.writeString(s);
                    assertEquals(0, g.streamWriteOutputBuffered());
                    assertNull(g._payload);
                    assertSame(buffer, g._outputBuffer);
                    assertArrayEquals(cat(bufferPrefix(bufferSize), string(s)), out.toByteArray());
                    g.writeBinary(new ByteArrayInputStream(new byte[20000]), 20000);
                    assertEquals(0, g.streamWriteOutputBuffered());
                    assertNull(g._payload);
                }
                assertArrayEquals(cat(bufferPrefix(bufferSize), string(s), binary(20000)), out.toByteArray());
            }
        }
        for (int size : new int[]{1, 2, 7, 256}) for (int offset : new int[]{0, size}) {
            for (int len : new int[]{0, 1, size, size + 1}) {
                int[] visible = new int[2], buffered = new int[2];
                for (int writer = 0; writer < 2; ++writer) {
                    var out = new ByteArrayOutputStream();
                    try (var w = new WriterReplay(writer == 0, DEFAULTS, 0, out, new byte[size], offset)) {
                        w.g.writeString("é".repeat(len));
                        visible[writer] = out.size(); buffered[writer] = w.g.streamWriteOutputBuffered();
                    }
                }
                assertEquals(visible[0], visible[1]); assertEquals(buffered[0], buffered[1]);
            }
        }
    }

    private static byte[] bufferPrefix(int size) {
        byte[] prefix = new byte[size]; Arrays.fill(prefix, (byte) 0x18); return prefix;
    }

    @Test void longAsciiContainerReservesExactBytesRatherThanThreeTimesCharacters() {
        var out = new ByteArrayOutputStream();
        try (var g = generator(new CountingIO(false), out, 0, null)) {
            g.writeStartArray();
            g.writeString("x".repeat(2_000_000));
            assertEquals(2_000_018, g._payload.length); // exact requirement dominates geometric growth
            assertEquals(2_000_018, g._payloadTail);
            g.writeEndArray();
        }
    }

    @Test void binaryStreamsMakeProgressAndDoNotTakeCallerOwnership() {
        for (boolean nested : new boolean[]{false, true}) for (int length : new int[]{0, 3, 65536}) {
            var out = new ByteArrayOutputStream();
            try (var g = generator(new CountingIO(false), out, 0, null)) {
                if (nested) g.writeStartArray();
                var stream = new ByteArrayInputStream(new byte[length]) {
                    boolean zero = true;
                    @Override public synchronized int read(byte[] b, int off, int len) {
                        assertSame(nested ? g._payload : g._outputBuffer, b);
                        zero = !zero;
                        return zero ? 0 : super.read(b, off, Math.min(5, len));
                    }
                    @Override public void close() { fail("Caller owns input stream"); }
                };
                assertEquals(length, g.writeBinary(stream, length));
                if (nested) g.writeEndArray();
            }
            assertArrayEquals(nested ? compact(false, 1, binary(length)) : binary(length), out.toByteArray());
        }
    }

    @Test void binaryStreamsKeepKnownLengthAndPrematureEofErrors() {
        for (boolean nested : new boolean[]{false, true}) for (int expected : new int[]{3, 20000}) {
            var out = new ByteArrayOutputStream();
            try (var g = generator(new CountingIO(false), out, 0, null)) {
                if (nested) g.writeStartArray();
                var error = assertThrows(StreamWriteException.class,
                        () -> g.writeBinary(new ByteArrayInputStream(new byte[expected - 1]), expected));
                assertTrue(error.getMessage().contains("expected " + expected + " bytes, got " + (expected - 1)));
                if (nested) assertEquals(0, out.size());
            }
        }
        assertThrows(UnsupportedOperationException.class, () -> WriterReplay.encode(false, DEFAULTS,
                w -> w.g.writeBinary(new ByteArrayInputStream(new byte[0]), -1)));
        try (var g = generator(new CountingIO(false), new ByteArrayOutputStream(), 0, null)) {
            assertThrows(JacksonException.class, () -> g.writeBinary(new InputStream() {
                @Override public int read() throws IOException { throw new IOException("read failed"); }
            }, 1));
        }
    }

    @Test void largeThenSmallRootsReuseCapacityAndReturnedArraysAreIndependent() {
        var out = new ByteArrayOutputStream();
        var io = new CountingIO(false);
        try (var g = generator(io, out, 0, null)) {
            byte[] outputBuffer = g._outputBuffer;
            g.writeStartArray(); g.writeBinary(new byte[2_000_000]); g.writeEndArray();
            byte[] arena = g._payload, first = out.toByteArray(), unchanged = first.clone();
            int offsetCapacity = g._offsets.length;
            for (int i = 0; i < 10000; ++i) {
                g.writeStartArray(); g.writeNull(); g.writeEndArray();
                assertSame(arena, g._payload);
                assertEquals(0, g._payloadTail); assertEquals(0, g._offsetTail);
            }
            assertEquals(offsetCapacity, g._offsets.length);
            assertArrayEquals(unchanged, first);
            byte[] second = out.toByteArray(); second[0] ^= 1;
            assertArrayEquals(unchanged, first);
            assertSame(outputBuffer, g._outputBuffer);
        }
        assertEquals(1, io.releases);
        assertTrue(io.released.length < 2_000_000);
        var mapper = new VPackMapper();
        byte[] first = mapper.writeValueAsBytes(new String[]{"a"});
        byte[] saved = first.clone();
        for (int i = 0; i < 20; ++i) mapper.writeValueAsBytes(new String[]{"b"});
        assertArrayEquals(saved, first);
    }

    @Test void buffersReleaseOnceForBothConstructorsOnWriteFlushAndCloseFailure() throws Exception {
        for (String failure : new String[]{"write", "flush", "close"}) {
            for (int constructor = 0; constructor < 3; ++constructor) {
                var io = new CountingIO(false);
                var out = new FailingOutput(failure);
                byte[] custom = constructor == 0 ? null : constructor == 1
                        ? io.allocWriteEncodingBuffer() : new byte[256];
                var g = generator(io, out, StreamWriteFeature.AUTO_CLOSE_TARGET.getMask()
                        | StreamWriteFeature.FLUSH_PASSED_TO_STREAM.getMask(), custom, constructor == 1);
                byte[] output = g._outputBuffer;
                // Ensure arena and sort scratch have been allocated before failure.
                g.enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED);
                out.armed = false;
                g.writeStartObject(); g.writeName("z"); g.writeNull();
                g.writeName("a"); g.writeNull(); g.writeEndObject();
                out.armed = true;
                g.writeNull();
                if (failure.equals("flush")) assertThrows(JacksonException.class, g::flush);
                if (failure.equals("close") || failure.equals("write")) assertThrows(JacksonException.class, g::close);
                else g.close();
                g.close();
                assertTrue(g.isClosed());
                assertEquals(constructor == 2 ? 0 : 1, io.releases);
                if (constructor != 2) assertSame(output, io.released);
                assertEquals(1, io.closes); assertEquals(1, out.closes);
                assertNull(g._payload); assertNull(g._outputBuffer);
                for (String fieldName : new String[]{"_sortScratch", "_sortOrder", "_sortWork"}) {
                    var field = VPackGenerator.class.getDeclaredField(fieldName);
                    field.setAccessible(true); assertNull(field.get(g));
                }
                assertNull(g._offsets); assertNull(g._frameStarts);
                assertEquals(0, g._payloadTail); assertEquals(0, g.streamWriteOutputBuffered());
                assertEquals(0, g._depth);
            }
        }
    }

    @Test void closePreservesWriteFailureAndSuppressesTargetCloseFailure() {
        var io = new CountingIO(false);
        var out = new FailingOutput("write") {
            @Override public void close() throws IOException {
                ++closes; throw new IOException("close also failed");
            }
        };
        var g = generator(io, out, StreamWriteFeature.AUTO_CLOSE_TARGET.getMask(), null);
        g.writeNull();
        var failure = assertThrows(JacksonException.class, g::close);
        assertTrue(failure.getMessage().contains("partial write failed"));
        assertEquals(1, failure.getSuppressed().length);
        assertTrue(failure.getSuppressed()[0].getMessage().contains("close also failed"));
        g.close(); assertEquals(1, out.closes); assertEquals(1, io.releases); assertEquals(1, io.closes);
    }

    @Test void flushOnCloseFailureStillReleasesAndDoesNotCloseCallerTarget() {
        var io = new CountingIO(false); var out = new FailingOutput("flush");
        var g = generator(io, out, StreamWriteFeature.FLUSH_PASSED_TO_STREAM.getMask(), null);
        g.writeNull();
        assertThrows(JacksonException.class, g::close);
        g.close(); assertEquals(0, out.closes); assertEquals(1, out.flushes);
        assertEquals(1, io.releases); assertEquals(1, io.closes);
    }

    @Test void partialRootWritesCannotBeReplayedOrFollowedByAnotherValue() {
        for (String kind : new String[]{"buffer", "container", "string", "binary", "bytes"}) {
            var io = new CountingIO(false); var out = new FailingOutput("write");
            var g = generator(io, out, 0, null);
            if (kind.equals("buffer")) g.writeNull();
            assertThrows(JacksonException.class, () -> {
                switch (kind) {
                    case "buffer" -> g.flush();
                    case "container" -> { g.writeStartArray(); g.writeNull(); g.writeEndArray(); }
                    case "string" -> g.writeString("x".repeat(20000));
                    case "binary" -> g.writeBinary(new ByteArrayInputStream(new byte[20000]), 20000);
                    case "bytes" -> g.writeBytes(new byte[20000], 0, 20000);
                }
            });
            int writes = out.writes, visible = out.bytes.size();
            assertThrows(StreamWriteException.class, g::writeNull);
            assertThrows(StreamWriteException.class, () -> g.writeTaggedValuePrefix(7));
            g.close(); g.close();
            assertEquals(writes, out.writes); assertEquals(visible, out.bytes.size());
            assertEquals(1, io.releases); assertEquals(1, io.closes);
        }
    }

    @Test void targetOwnershipAndPrefixBytesSurviveCloseAndFailure() {
        for (boolean managed : new boolean[]{false, true}) for (int flags : new int[]{0,
                StreamWriteFeature.AUTO_CLOSE_TARGET.getMask(), StreamWriteFeature.FLUSH_PASSED_TO_STREAM.getMask()}) {
            var io = new CountingIO(managed); var out = new FailingOutput("none");
            byte[] buffer = io.allocWriteEncodingBuffer();
            Arrays.fill(buffer, 0, 7, (byte) 0x18);
            var g = new VPackGenerator(ObjectWriteContext.empty(), io, flags, DEFAULTS, out, buffer, 7, true);
            // Custom prefix is established by the constructor, not by seeking.
            g.writeNull(); g.close(); g.close();
            assertArrayEquals(bufferPrefix(8), out.bytes.toByteArray());
            boolean owned = managed || StreamWriteFeature.AUTO_CLOSE_TARGET.enabledIn(flags);
            assertEquals(owned ? 1 : 0, out.closes);
            assertEquals(!owned && StreamWriteFeature.FLUSH_PASSED_TO_STREAM.enabledIn(flags) ? 1 : 0, out.flushes);
            assertEquals(1, io.releases);
        }
    }

    private static VPackGenerator generator(CountingIO io, OutputStream out, int flags, byte[] custom) {
        return generator(io, out, flags, custom, false);
    }

    private static VPackGenerator generator(CountingIO io, OutputStream out, int flags, byte[] custom, boolean recyclable) {
        return custom == null ? new VPackGenerator(ObjectWriteContext.empty(), io, flags, DEFAULTS, out)
                : new VPackGenerator(ObjectWriteContext.empty(), io, flags, DEFAULTS, out,
                        custom, 0, recyclable);
    }

    static class CountingIO extends IOContext {
        int releases, closes;
        byte[] released;
        CountingIO(boolean managed) {
            super(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                    ErrorReportConfiguration.defaults(), new BufferRecycler(),
                    ContentReference.unknown(), managed, JsonEncoding.UTF8);
        }
        @Override public void releaseWriteEncodingBuffer(byte[] buffer) {
            ++releases; released = buffer; super.releaseWriteEncodingBuffer(buffer);
        }
        @Override public void close() { ++closes; super.close(); }
    }

    static class FailingOutput extends OutputStream {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final String failure;
        boolean armed = true;
        int writes, flushes, closes;
        FailingOutput(String failure) { this.failure = failure; }
        @Override public void write(int b) throws IOException { write(new byte[]{(byte) b}, 0, 1); }
        @Override public void write(byte[] b, int off, int len) throws IOException {
            ++writes;
            if (armed && failure.equals("write")) {
                bytes.write(b, off, Math.min(len, 3));
                throw new IOException("partial write failed");
            }
            bytes.write(b, off, len);
        }
        @Override public void flush() throws IOException {
            ++flushes;
            if (armed && failure.equals("flush")) throw new IOException("flush failed");
        }
        @Override public void close() throws IOException {
            ++closes;
            if (armed && failure.equals("close")) throw new IOException("close failed");
        }
    }
}
