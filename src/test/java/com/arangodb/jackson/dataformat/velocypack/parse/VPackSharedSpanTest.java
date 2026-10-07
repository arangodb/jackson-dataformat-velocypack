package com.arangodb.jackson.dataformat.velocypack.parse;

import com.arangodb.jackson.dataformat.velocypack.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.IOContext;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import static com.arangodb.jackson.dataformat.velocypack.parse.ReaderRegressionTest.*;
import static org.junit.jupiter.api.Assertions.*;

/** Span ownership and allocation instrumentation lives only in tests. Fixtures
 * are independently assembled; no production writer supplies wire expectations. */
class VPackSharedSpanTest {
    static byte[] vbyte(long n) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        do {
            int b = (int) (n & 127); n >>>= 7;
            out.write(b | (n == 0 ? 0 : 128));
        } while (n != 0);
        return out.toByteArray();
    }
    static byte[] compactLarge(boolean object, byte[]... items) {
        byte[] body = concat(items);
        byte[] reverse = vbyte(object ? items.length / 2 : items.length);
        for (int a = 0, b = reverse.length - 1; a < b; a++, b--) {
            byte t = reverse[a]; reverse[a] = reverse[b]; reverse[b] = t;
        }
        int total = body.length + reverse.length + 2;
        while (1 + vbyte(total).length + body.length + reverse.length != total)
            total = 1 + vbyte(total).length + body.length + reverse.length;
        return concat(bytes(object ? 0x14 : 0x13), vbyte(total), body, reverse);
    }
    static Object field(Object object, String name) {
        try {
            Field f = object.getClass().getDeclaredField(name); f.setAccessible(true);
            return f.get(object);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }
    static class AccountingParser extends VPackParser {
        long copied, roots, containers;
        int depth;
        final Set<Object> frames = Collections.newSetFromMap(new IdentityHashMap<>());
        final byte[] caller;
        byte[] stableRoot;
        AccountingParser(ObjectReadContext c, IOContext io, int f, int vf, InputStream in,
                byte[] b, int start, int end, boolean recyclable, tools.jackson.core.sym.ByteQuadsCanonicalizer symbols) {
            super(c, io, f, vf, in, b, start, end, recyclable, symbols);
            caller = recyclable ? null : b;
        }
        @Override protected byte[] _readRootContainer(int tb) {
            long before = copied;
            byte[] result = super._readRootContainer(tb);
            // Count exact header transfer separately from refill transfers/growth.
            int header = 1;
            if (tb == 0x13 || tb == 0x14) { while ((result[header++] & 128) != 0) { } }
            else header += tb <= 9 ? widthFromTypeByte_NoIdx(tb) : widthFromTypeByte_Obj(tb);
            copied += header;
            assertTrue(copied > before);
            roots++;
            return result;
        }
        @Override protected byte[] _growContainerBuffer(byte[] b, int len) {
            copied += b.length;
            return super._growContainerBuffer(b, len);
        }
        @Override protected void _copyContainerBytes(byte[] b, int pos, int count) {
            copied += count;
            super._copyContainerBytes(b, pos, count);
        }
        @Override public JsonToken nextToken() {
            JsonToken t = super.nextToken();
            if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) {
                containers++;
                Object frame = _parseStack.peek(); frames.add(frame);
                depth = Math.max(depth, _parseStack.size());
                byte[] backing = (byte[]) field(frame, "buf");
                if (caller != null) assertSame(caller, backing, "even empty containers share the input");
                else if (_parseStack.size() == 1) stableRoot = backing;
                else assertSame(stableRoot, backing, "nested stream frames share the stable root");
            }
            return t;
        }
        void released() {
            for (Object frame : frames) assertNull(field(frame, "buf"));
            assertNull(_inputBuffer);
            assertTrue(_parseStack.isEmpty());
        }
    }
    static class AccountingFactory extends VPackFactory {
        @Override protected JsonParser _createParser(ObjectReadContext c, IOContext io, byte[] b, int off, int len) {
            return new AccountingParser(c, io, c.getStreamReadFeatures(_streamReadFeatures),
                    c.getFormatReadFeatures(_formatReadFeatures), null, b, off, off + len, false,
                    _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures));
        }
        @Override protected JsonParser _createParser(ObjectReadContext c, IOContext io, InputStream in) {
            return new AccountingParser(c, io, c.getStreamReadFeatures(_streamReadFeatures),
                    c.getFormatReadFeatures(_formatReadFeatures), in, io.allocReadIOBuffer(), 0, 0, true,
                    _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures));
        }
    }

    @Test void zeroByteArrayCopiesAndStreamCopiesIndependentOfNestedSubtreeSum() {
        byte[] scalar = string("x".repeat(256 * 1024));
        AccountingFactory factory = new AccountingFactory();
        long previousCopy = 0;
        for (int depth : new int[]{1, 4, 7, 16, 32}) {
            byte[] fixture = scalar;
            for (int i = 0; i < depth; i++) fixture = compactLarge(false, fixture);
            byte[] slice = concat(bytes(0x17, 0), fixture, bytes(0x17, 0));
            AccountingParser p = (AccountingParser) factory.createParser(ObjectReadContext.empty(), slice, 2, fixture.length);
            transcript(p);
            assertEquals(0, p.copied); assertEquals(depth, p.depth); p.released();
            p = (AccountingParser) factory.createParser(ObjectReadContext.empty(), new ShortReads(fixture, 7));
            transcript(p);
            assertEquals(1, p.roots); assertEquals(depth, p.depth); p.released();
            assertTrue(p.copied < fixture.length * 3L, "linear root storage; no subtree copies");
            if (previousCopy != 0) assertTrue(p.copied - previousCopy < 1024, "depth adds only small encoded headers");
            previousCopy = p.copied;
        }
    }
    @Test void multiByteReverseCountsUseOwnContainerEndForArraysAndObjects() {
        ReaderRegressionTest suite = new ReaderRegressionTest();
        for (boolean object : new boolean[]{false, true}) {
            byte[][] items = new byte[object ? 260 : 130][];
            for (int i = 0; i < items.length; i++) items[i] = object && i % 2 == 0 ? string("k" + i) : bytes(0x31);
            byte[] fixture = compactLarge(object, items);
            List<String> expected = new ArrayList<>(List.of(object ? "START_OBJECT" : "START_ARRAY"));
            for (int i = 0; i < 130; i++) {
                if (object) expected.add("PROPERTY_NAME:k" + (2 * i));
                expected.add("VALUE_NUMBER_INT:INT:1");
            }
            expected.add(object ? "END_OBJECT" : "END_ARRAY");
            suite.allSources(fixture, expected);
            expected = new ArrayList<>(expected); expected.add("VALUE_TRUE");
            suite.allSources(concat(fixture, bytes(0x1a)), expected);
            List<String> nested = new ArrayList<>(List.of("START_ARRAY")); nested.addAll(expected); nested.add("END_ARRAY");
            suite.allSources(compactLarge(false, fixture, bytes(0x1a)), nested);
        }
    }
    @Test void tagsAroundEveryContainerFormAndMultipleRoots() {
        ReaderRegressionTest suite = new ReaderRegressionTest();
        List<byte[]> containers = new ArrayList<>(List.of(bytes(1), bytes(10), compact(false, bytes(0x31)), compact(true, string("k"), bytes(0x31))));
        for (int width : new int[]{1, 2, 4, 8}) for (boolean pad : new boolean[]{false, true}) {
            containers.add(noIndex(width, pad));
            containers.add(indexed(false, false, width, pad, bytes(0x31)));
            containers.add(indexed(true, true, width, pad, string("z"), bytes(0x31), string("a"), bytes(0x32)));
            containers.add(indexed(true, false, width, pad, string("k"), bytes(0x31)));
        }
        VPackMapper m = new VPackMapper();
        for (byte[] fixture : containers) {
            byte[] tag = concat(bytes(0xef), le(123, 8), bytes(0xee, 7), fixture);
            List<String> expected = transcript(m.createParser(fixture));
            suite.allSources(tag, expected);
            List<String> nested = new ArrayList<>(List.of("START_ARRAY")); nested.addAll(expected); nested.add("VALUE_TRUE"); nested.addAll(expected); nested.add("END_ARRAY");
            suite.allSources(compactLarge(false, tag, bytes(0x1a), tag), nested);
            try (VPackParser p = (VPackParser) m.createParser(tag)) { p.nextToken(); assertEquals(7, p.getLastTagNumber()); }
        }
    }
    @Test void truncationNeverReadsSuffixOrReturnsZeroPaddedPayload() {
        VPackMapper m = new VPackMapper();
        List<byte[]> fixtures = new ArrayList<>(List.of(string("hello"), string("x".repeat(129)),
                concat(bytes(0xc7), le(3, 8), bytes(1, 2, 3)), compact(true, string("k"), bytes(0x31)),
                compact(false, bytes(0x31)), concat(bytes(0xef), le(1, 8), bytes(1)), concat(bytes(0x27), le(1, 8))));
        for (int width : new int[]{1, 2, 4, 8}) {
            fixtures.add(indexed(false, false, width, true, bytes(0x31)));
            fixtures.add(indexed(true, true, width, true, string("k"), compact(false, bytes(0x31))));
            fixtures.add(noIndex(width, true));
        }
        for (byte[] fixture : fixtures) for (int cut = 1; cut < fixture.length; cut++) {
            int length = cut;
            assertThrows(StreamReadException.class, () -> transcript(m.createParser(fixture, 0, length)), "slice " + Arrays.toString(fixture) + " at " + cut);
            assertThrows(StreamReadException.class, () -> transcript(m.createParser(new ShortReads(Arrays.copyOf(fixture, length), 1))));
        }
    }
    @Test void maliciousLengthsCountsAndOffsetsAreControlled() {
        List<byte[]> bad = new ArrayList<>();
        for (long length : new long[]{-1L, Long.MIN_VALUE, Long.MAX_VALUE, 0xffff_ffffL, Integer.MAX_VALUE}) {
            bad.add(concat(bytes(0x09), le(length, 8)));
            bad.add(concat(bytes(0xbf), le(length, 8)));
            bad.add(concat(bytes(0xc7), le(length, 8)));
            bad.add(concat(bytes(0xff), le(length, 8)));
            bad.add(concat(bytes(0xcf), le(length, 8), le(0, 4)));
        }
        bad.add(bytes(0x13, 3, 0x80)); // unterminated reverse count
        bad.add(bytes(0x13, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0));
        for (int width : new int[]{1, 2, 4, 8}) {
            byte[] b = indexed(false, false, width, false, bytes(0x31));
            int index = b.length - width - (width == 8 ? 8 : 0);
            for (long off : new long[]{0, 1, b.length, -1L}) {
                byte[] copy = b.clone(); System.arraycopy(le(off, width), 0, copy, index, width); bad.add(copy);
            }
            b = indexed(true, false, width, false, string("k"), bytes(0x31));
            System.arraycopy(le(-1, width), 0, b, width == 8 ? b.length - 8 : 1 + width, width); bad.add(b);
        }
        // Original legacy LEN8 fixture misplaced the payload in the length field.
        bad.add(bytes(2, 13, 0xfd, 2, 0, 0, 0, 0, 0, 0, 0x55, 0x66, 0));
        // Child declares bytes extending into parent's metadata/suffix.
        bad.add(bytes(0x13, 6, 0x02, 4, 0x31, 1));
        VPackMapper m = new VPackMapper();
        for (byte[] fixture : bad) {
            assertControlled(() -> transcript(m.createParser(fixture)), fixture);
            assertControlled(() -> transcript(m.createParser(new ShortReads(fixture, 1))), fixture);
        }
    }
    static void assertControlled(Runnable operation, byte[] fixture) {
        JacksonException failure = assertThrows(JacksonException.class, operation::run, Arrays.toString(fixture));
        assertTrue(failure instanceof StreamReadException || failure instanceof StreamConstraintsException,
                "Expected controlled read/constraint failure: " + failure);
    }
    @Test void depthAndDocumentLimitsAlsoApplyToStreamsAndRootScalars() {
        byte[] fixture = bytes(0x31);
        for (int i = 0; i < 256; i++) fixture = compactLarge(false, fixture);
        VPackMapper depth = constrained(StreamReadConstraints.builder().maxNestingDepth(128).build());
        byte[] deep = fixture;
        assertThrows(StreamConstraintsException.class, () -> transcript(depth.createParser(deep)));
        assertThrows(StreamConstraintsException.class, () -> transcript(depth.createParser(new ShortReads(deep, 1))));
        VPackMapper okay = constrained(StreamReadConstraints.builder().maxNestingDepth(256).build());
        assertEquals(513, transcript(okay.createParser(deep)).size());
        for (byte[] value : new byte[][]{compact(false, bytes(0x31)), string("abc"), concat(bytes(0x27), le(0, 8)), bytes(0xee, 1, 0x31), bytes(0x31, 0x32, 0x33)}) {
            VPackMapper limit = constrained(StreamReadConstraints.builder().maxDocumentLength(2).build());
            assertThrows(StreamConstraintsException.class, () -> transcript(limit.createParser(new ShortReads(value, 1))));
        }
        byte[][] tags = new byte[10000][]; Arrays.fill(tags, bytes(0xee, 1));
        byte[] wrapped = concat(concat(tags), bytes(0x31));
        assertEquals(List.of("VALUE_NUMBER_INT:INT:1"), transcript(okay.createParser(wrapped)));
        assertEquals(3, transcript(okay.createParser(compactLarge(false, wrapped))).size());
    }
    @Test void ownedValuesSurviveMovementRefillAndClose() {
        String text = "😀".repeat(4000);
        byte[] binary = concat(bytes(0xc1), le(20000, 2), new byte[20000]); binary[binary.length - 1] = 42;
        byte[] custom = concat(bytes(0xfa), le(20000, 4), new byte[20000]); custom[custom.length - 1] = 43;
        byte[] fixture = compactLarge(false, string(text), binary, custom);
        byte[] sequence = concat(fixture, fixture, string("tail"));
        VPackMapper m = new VPackMapper();
        for (boolean stream : new boolean[]{false, true}) {
            try (JsonParser p = stream ? m.createParser(new ShortReads(sequence, 7)) : m.createParser(sequence)) {
                p.nextToken(); p.nextToken(); String retained = p.getString(); p.nextToken(); byte[] owned = p.getBinaryValue();
                assertSame(owned, p.getEmbeddedObject()); owned[0] = 99;
                p.nextToken(); VPackCustomValue value = (VPackCustomValue) p.getEmbeddedObject();
                while (p.nextToken() != null) { }
                Arrays.fill(sequence, (byte) 0);
                assertEquals(text, retained); assertEquals(99, owned[0]); assertEquals(42, owned[owned.length - 1]);
                assertEquals(43, value.getPayload()[19999]);
            }
            sequence = concat(fixture, fixture, string("tail"));
        }
    }
    @Test void framesAndJacksonContextsFollowDepthRatherThanContainerCount() {
        byte[][] roots = new byte[2000][];
        for (int i = 0; i < roots.length; i++) {
            roots[i] = switch (i % 5) {
                case 0 -> compact(false, compact(true, string("k"), bytes(0x31)), bytes(1));
                case 1 -> indexed(true, true, 8, false, string("k"), compact(false, bytes(0x32)), string("j"), bytes(10));
                case 2 -> bytes(1);
                case 3 -> indexed(false, false, 2, true, bytes(10), noIndex(4, true));
                default -> compact(true, string("k"), compact(true, string("j"), bytes(1)));
            };
        }
        byte[] fixture = concat(roots);
        AccountingFactory f = new AccountingFactory();
        for (boolean stream : new boolean[]{false, true}) {
            AccountingParser p = (AccountingParser) (stream ? f.createParser(ObjectReadContext.empty(), new ShortReads(fixture, 7)) : f.createParser(ObjectReadContext.empty(), fixture));
            Map<Integer, TokenStreamContext> contexts = new HashMap<>();
            long starts = 0;
            while (p.nextToken() != null) {
                if (p.currentToken() == JsonToken.START_ARRAY || p.currentToken() == JsonToken.START_OBJECT) {
                    TokenStreamContext c = p.streamReadContext();
                    int depth = c.getNestingDepth();
                    if (contexts.containsKey(depth)) assertSame(contexts.get(depth), c);
                    else contexts.put(depth, c);
                    assertNull(p.currentValue(), "reused context clears user value");
                    p.assignCurrentValue(new Object());
                    starts++;
                }
            }
            assertTrue(starts > 5000);
            assertEquals(3, p.depth);
            assertEquals(3, p.frames.size(), "one frame per high-water depth across all roots and siblings");
            assertEquals(3, contexts.size(), "Jackson already reuses contexts");
            p.released();
        }
        // Duplicate detector state must reset at sibling/root reuse boundaries.
        VPackMapper strict = VPackMapper.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        assertEquals(transcript(new VPackMapper().createParser(fixture)), transcript(strict.createParser(fixture)));
    }

    @Test void onlyTheOriginalIOBufferIsReturnedToRecyclerEvenOnCloseFailure() {
        class Recycler extends tools.jackson.core.util.BufferRecycler {
            byte[] released;
            int releases;
            @Override public void releaseByteBuffer(int ix, byte[] b) {
                if (ix == BYTE_READ_IO_BUFFER) { released = b; releases++; }
                super.releaseByteBuffer(ix, b);
            }
        }
        byte[] fixture = compactLarge(false, string("x".repeat(20000)), bytes(1));
        for (boolean stream : new boolean[]{false, true}) {
            Recycler recycler = new Recycler();
            IOContext io = new IOContext(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(),
                    ErrorReportConfiguration.defaults(), recycler, tools.jackson.core.io.ContentReference.rawReference("test"), false, null);
            byte[] buffer = stream ? io.allocReadIOBuffer() : fixture;
            InputStream source = stream ? new ByteArrayInputStream(fixture) {
                @Override public void close() throws IOException { throw new IOException("close failure"); }
            } : null;
            VPackParser p = new VPackParser(ObjectReadContext.empty(), io, StreamReadFeature.collectDefaults(),
                    VPackReadFeature.collectDefaults(), source, buffer, 0, stream ? 0 : fixture.length, stream);
            p.nextToken(); p.nextToken();
            if (stream) assertThrows(JacksonIOException.class, p::close); else p.close();
            p.close();
            assertEquals(stream ? 1 : 0, recycler.releases);
            if (stream) assertSame(buffer, recycler.released);
        }
    }

    @Test void earlyCloseClearsAllFrameBuffersAndStreamFailureIsControlled() {
        byte[] fixture = compact(false, compact(true, string("k"), compact(false, bytes(1))));
        AccountingFactory f = new AccountingFactory();
        for (boolean stream : new boolean[]{false, true}) {
            AccountingParser p = (AccountingParser) (stream ? f.createParser(ObjectReadContext.empty(), new ShortReads(fixture, 1)) : f.createParser(ObjectReadContext.empty(), fixture));
            p.nextToken(); p.nextToken(); p.nextToken(); p.nextToken(); p.close(); p.close(); p.released();
        }
        InputStream failing = new InputStream() {
            int n;
            @Override public int read() throws IOException { if (n == 3) throw new IOException("failure after header"); return fixture[n++]; }
        };
        assertThrows(JacksonIOException.class, () -> transcript(new VPackMapper().createParser(failing)));
        InputStream zero = new InputStream() {
            @Override public int read() { return 0; }
            @Override public int read(byte[] b, int off, int len) { return 0; }
        };
        assertThrows(StreamReadException.class, () -> transcript(new VPackMapper().createParser(zero)));
    }
}
