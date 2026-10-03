package com.arangodb.jackson.dataformat.velocypack.parse;

import com.arangodb.jackson.dataformat.velocypack.*;
import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.io.ContentReference;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.util.BufferRecycler;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

import static com.arangodb.jackson.dataformat.velocypack.parse.ReaderRegressionTest.*;
import static org.junit.jupiter.api.Assertions.*;

class VPackPropertyNameCanonicalizationTest {
    static class Factory extends VPackFactory {
        Factory(VPackFactoryBuilder b) { super(b); }
        ByteQuadsCanonicalizer root() { return _byteSymbolCanonicalizer; }
    }
    static Factory factory(boolean canonical, boolean intern, boolean overflow) {
        return new Factory(VPackFactory.builder()
                .configure(TokenStreamFactory.Feature.CANONICALIZE_PROPERTY_NAMES, canonical)
                .configure(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES, intern)
                .configure(TokenStreamFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW, overflow));
    }
    static byte[] object(String... names) {
        byte[][] pairs = new byte[names.length * 2][];
        for (int i = 0; i < names.length; i++) {
            pairs[i * 2] = string(names[i]);
            pairs[i * 2 + 1] = bytes(0x31);
        }
        return indexed(true, false, 4, false, pairs);
    }
    static List<String> names(JsonParser parser) {
        try (parser) {
            List<String> names = new ArrayList<>();
            while (parser.nextToken() != null) {
                if (parser.currentToken() == JsonToken.PROPERTY_NAME) names.add(parser.currentName());
            }
            assertTrue(parser.isClosed(), "EOF closes parser");
            parser.close();
            return names;
        }
    }
    @Test void byteNamesAcrossQuadAndWireBoundariesAllSources() {
        List<String> distinct = new ArrayList<>(List.of("", "\0", "\0\0", "a\0", "a\0\0\0", "a\0b",
                "é", "東京", "😀", "a😀é\0", "x".repeat(126), "x".repeat(127), "é".repeat(63), "é".repeat(64)));
        for (int n = 1; n <= 20; n++) {
            distinct.add("a".repeat(n));
            distinct.add("a".repeat(n) + "\0");
        }
        for (int n = 4096; n <= 4099; n++) distinct.add("z".repeat(n));
        distinct = new ArrayList<>(new LinkedHashSet<>(distinct));
        // Independent encodings of equal names, with alternating scratch sizes.
        List<String> input = new ArrayList<>(distinct);
        for (String s : distinct) input.add(new String(s.toCharArray()));
        byte[] fixture = object(input.toArray(String[]::new));
        for (int source = 0; source < 5; source++) {
            Factory f = factory(true, false, true);
            JsonParser p;
            if (source == 0) p = f.createParser(ObjectReadContext.empty(), fixture);
            else if (source == 1) {
                byte[] slice = concat(bytes(0x17, 0x17, 0x17), fixture, bytes(0x17));
                p = f.createParser(ObjectReadContext.empty(), slice, 3, fixture.length);
            } else p = f.createParser(ObjectReadContext.empty(), new ShortReads(fixture, new int[]{1, 7, 8192}[source - 2]));
            List<String> actual = names(p);
            assertEquals(input, actual);
            for (int i = 0; i < distinct.size(); i++) assertSame(actual.get(i), actual.get(i + distinct.size()));
            assertEquals(distinct.size(), f.root().size());
            List<String> shared = names(f.createParser(ObjectReadContext.empty(), fixture.clone()));
            for (int i = 0; i < distinct.size(); i++) assertSame(actual.get(i), shared.get(i));
        }
        // Same name using either short or long wire encoding.
        byte[] longA = concat(bytes(0xbf), le(1, 8), bytes('a'));
        var f = factory(true, false, true);
        var actual = names(f.createParser(ObjectReadContext.empty(), indexed(true, false, 1, false,
                string("a"), bytes(0x31), longA, bytes(0x31))));
        assertSame(actual.get(0), actual.get(1));
    }
    @Test void featureMatrixAndCloseSharing() {
        String name = new String("independentPropertyName😀".toCharArray());
        for (boolean canonical : new boolean[]{false, true}) {
            for (boolean intern : new boolean[]{false, true}) {
                for (boolean overflow : new boolean[]{false, true}) {
                    Factory f = factory(canonical, intern, overflow);
                    JsonParser p = f.createParser(ObjectReadContext.empty(), object(name, name));
                    assertEquals(canonical && intern, p.willInternPropertyNames());
                    List<String> first = names(p);
                    List<String> second = names(f.createParser(ObjectReadContext.empty(), object(new String(name.toCharArray()))));
                    assertEquals(List.of(name, name), first);
                    assertEquals(name, second.get(0));
                    if (canonical) {
                        assertSame(first.get(0), first.get(1));
                        assertSame(first.get(0), second.get(0));
                        if (intern) assertSame(name.intern(), first.get(0));
                        assertEquals(1, f.root().size());
                    } else assertEquals(0, f.root().size());
                }
            }
        }
    }
    @Test void namesAfterMultipleRootBufferRefills() {
        String name = "key\0😀".repeat(60);
        byte[] row = object(name);
        byte[][] rows = new byte[80][];
        Arrays.fill(rows, row);
        Factory f = factory(true, false, true);
        var result = names(f.createParser(ObjectReadContext.empty(), new ShortReads(concat(rows), 7)));
        assertEquals(80, result.size());
        for (String actual : result) { assertEquals(name, actual); assertSame(result.get(0), actual); }
    }
    @Test void duplicateDetectionOnHitsAndMissesForAllFeatureCombinations() {
        for (boolean canonical : new boolean[]{false, true}) {
            for (boolean intern : new boolean[]{false, true}) {
                Factory f = new Factory(VPackFactory.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                        .configure(TokenStreamFactory.Feature.CANONICALIZE_PROPERTY_NAMES, canonical)
                        .configure(TokenStreamFactory.Feature.INTERN_PROPERTY_NAMES, intern));
                names(f.createParser(ObjectReadContext.empty(), object("duplicate😀")));
                for (String name : List.of("duplicate😀", "", "a\0", "z".repeat(127))) {
                    assertThrows(StreamReadException.class, () -> names(f.createParser(ObjectReadContext.empty(), object(name, name))));
                }
            }
        }
    }
    static IOContext context(StreamReadConstraints constraints) {
        return new IOContext(constraints, StreamWriteConstraints.defaults(), ErrorReportConfiguration.defaults(),
                new BufferRecycler(), ContentReference.rawReference("test"), false, null);
    }
    @Test void nameConstraintsApplyToSharedHitsAndDisabledCanonicalization() {
        for (boolean canonical : new boolean[]{false, true}) {
            Factory f = factory(canonical, false, true);
            String name = "é😀".repeat(30);
            names(f.createParser(ObjectReadContext.empty(), object(name)));
            byte[] fixture = object(name);
            var child = f.root().makeChildOrPlaceholder(TokenStreamFactory.Feature.collectDefaults());
            try (var p = new VPackParser(ObjectReadContext.empty(), context(StreamReadConstraints.builder().maxNameLength(100).build()),
                    StreamReadFeature.collectDefaults(), 0, null, fixture, 0, fixture.length, false, child)) {
                assertThrows(StreamConstraintsException.class, () -> names(p));
            }
            Factory limited = new Factory(VPackFactory.builder().streamReadConstraints(StreamReadConstraints.builder().maxNameLength(2).build())
                    .configure(TokenStreamFactory.Feature.CANONICALIZE_PROPERTY_NAMES, canonical));
            assertThrows(StreamConstraintsException.class, () -> names(limited.createParser(ObjectReadContext.empty(), object("abc"))));
        }
    }
    @Test void highCardinalityIsBoundedOnRelease() {
        for (boolean overflow : new boolean[]{true, false}) {
            Factory f = factory(true, false, overflow);
            String[] keys = new String[20000];
            for (int i = 0; i < keys.length; i++) keys[i] = "uniqueProperty" + i;
            assertEquals(Arrays.asList(keys), names(f.createParser(ObjectReadContext.empty(), object(keys))));
            assertEquals(0, f.root().size(), "3.2.0 discards children with >6000 names");
            var small = names(f.createParser(ObjectReadContext.empty(), object("small", "small")));
            assertSame(small.get(0), small.get(1));
            assertEquals(1, f.root().size());
        }
    }
    @Test void collisionDefenseHonorsOverflowFeature() {
        // Four-byte ASCII names hash as one full data quad. Force all
        // names into one primary bucket even after several table doublings.
        for (boolean overflow : new boolean[]{true, false}) {
            Factory f = factory(true, false, overflow);
            var hash = f.root().makeChild(TokenStreamFactory.Feature.collectDefaults());
            List<String> colliding = new ArrayList<>();
            for (int i = 0; i < 52 * 52 * 52 * 52 && colliding.size() < 620; i++) {
                int x = i, q = 0;
                for (int j = 0; j < 4; j++, x /= 52) q = (q << 8) | (x % 52 < 26 ? 'a' + x % 52 : 'A' + x % 52 - 26);
                if ((hash.calcHash(q) & 2047) == 0) {
                    colliding.add(new String(new byte[]{(byte)(q >>> 24), (byte)(q >>> 16), (byte)(q >>> 8), (byte)q}, StandardCharsets.UTF_8));
                }
            }
            hash.release();
            assertTrue(colliding.size() == 620, "enough colliders to overflow a 2048-slot table");
            byte[] input = object(colliding.toArray(String[]::new));
            if (overflow) assertThrows(StreamConstraintsException.class, () -> names(f.createParser(ObjectReadContext.empty(), input)),
                    () -> "size=" + f.root().size() + ", buckets=" + f.root().makeChild(TokenStreamFactory.Feature.collectDefaults()).bucketCount()
                            + ", spill=" + f.root().makeChild(TokenStreamFactory.Feature.collectDefaults()).spilloverCount());
            else assertEquals(colliding, names(f.createParser(ObjectReadContext.empty(), input)));
        }
    }
    @Test void concurrentlyOpenChildrenAreIndependentAndShareAfterRelease() throws Exception {
        Factory f = factory(true, false, true);
        String name = "concurrentProperty😀";
        ExecutorService pool = Executors.newFixedThreadPool(8);
        CyclicBarrier beforeRead = new CyclicBarrier(8), beforeClose = new CyclicBarrier(8);
        try {
            List<Future<String>> tasks = new ArrayList<>();
            for (int i = 0; i < 8; i++) tasks.add(pool.submit(() -> {
                try (JsonParser p = f.createParser(ObjectReadContext.empty(), object(name, name))) {
                    beforeRead.await(20, TimeUnit.SECONDS);
                    p.nextToken(); p.nextToken(); String first = p.currentName();
                    p.nextToken(); p.nextToken(); assertSame(first, p.currentName());
                    beforeClose.await(20, TimeUnit.SECONDS);
                    return first;
                }
            }));
            Set<String> identities = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Future<String> task : tasks) identities.add(task.get(30, TimeUnit.SECONDS));
            assertEquals(8, identities.size(), "open children must not mutate each other's tables");
            String shared = names(f.createParser(ObjectReadContext.empty(), object(name))).get(0);
            assertTrue(identities.contains(shared), "one released snapshot is published");
            assertEquals(1, f.root().size());
        } finally { pool.shutdownNow(); }
    }
    @Test void closeFailureStillReleasesAndRepeatedCloseIsSafe() {
        Factory f = factory(true, false, true);
        var in = new ByteArrayInputStream(object("closeFailure")) {
            @Override public void close() throws IOException { throw new IOException("close failure"); }
        };
        JsonParser p = f.createParser(ObjectReadContext.empty(), in);
        p.nextToken(); p.nextToken(); String first = p.currentName();
        assertThrows(JacksonException.class, p::close);
        p.close();
        assertNull(p.nextToken());
        assertSame(first, names(f.createParser(ObjectReadContext.empty(), object("closeFailure"))).get(0));
    }
    @Test void partialConstructionFailureReleasesChildAndIOBuffer() {
        class Root extends ByteQuadsCanonicalizer {
            Root() { super(64, 7); }
            boolean released;
            @Override public ByteQuadsCanonicalizer makeChildOrPlaceholder(int flags) {
                return new Root() { @Override public void release() { Root.this.released = true; } };
            }
        }
        Root root = new Root();
        IOContext ctxt = context(StreamReadConstraints.defaults());
        var in = new InputStream() {
            boolean closed;
            @Override public int read() throws IOException { throw new IOException("load failure"); }
            @Override public void close() throws IOException { closed = true; throw new IOException("close failure"); }
        };
        var bootstrapper = new VPackParserBootstrapper(ctxt, in);
        var error = assertThrows(JacksonException.class, () -> bootstrapper.constructParser(ObjectReadContext.empty(),
                TokenStreamFactory.Feature.collectDefaults(), StreamReadFeature.collectDefaults(), 0, root));
        assertTrue(root.released);
        assertTrue(in.closed);
        assertEquals(1, error.getSuppressed().length);
        // A failed bootstrapper must have returned the IO buffer to the context.
        byte[] next = ctxt.allocReadIOBuffer();
        ctxt.releaseReadIOBuffer(next);
    }
    @Test void integerAttributeKeysKeepCurrentBehavior() {
        var actual = names(factory(true, true, true).createParser(ObjectReadContext.empty(), indexed(true, false, 1, false,
                bytes(0x35), bytes(0x31), bytes(0x29, 0x00, 0x01), bytes(0x31))));
        assertEquals(List.of("5", "256"), actual);
    }
    static int consume(VPackFactory f, byte[] input) {
        int count = 0;
        try (JsonParser p = f.createParser(ObjectReadContext.empty(), input)) {
            while (p.nextToken() != null) if (p.currentToken() == JsonToken.PROPERTY_NAME) count += p.currentName().length();
        }
        return count;
    }
    @Test void warmRepeatedNamesAvoidPerOccurrenceStringAndBackingArray() {
        var bean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        assertTrue(bean.isThreadAllocatedMemorySupported());
        bean.setThreadAllocatedMemoryEnabled(true);
        int occurrences = 20000;
        String[] keys = new String[occurrences];
        Arrays.fill(keys, "repeatedPropertyName");
        byte[] input = object(keys);
        Factory enabled = factory(true, false, true), disabled = factory(false, false, true);
        for (int i = 0; i < 15; i++) { consume(enabled, input); consume(disabled, input); }
        long thread = Thread.currentThread().getId();
        long start = bean.getThreadAllocatedBytes(thread);
        assertEquals(occurrences * keys[0].length(), consume(enabled, input));
        long withSymbols = bean.getThreadAllocatedBytes(thread) - start;
        start = bean.getThreadAllocatedBytes(thread);
        assertEquals(occurrences * keys[0].length(), consume(disabled, input));
        long withoutSymbols = bean.getThreadAllocatedBytes(thread) - start;
        assertTrue(withoutSymbols - withSymbols >= occurrences * 40L,
                "warm symbols must remove String/backing allocations: on=" + withSymbols + ", off=" + withoutSymbols);
    }
}
