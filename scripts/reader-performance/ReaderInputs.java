package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.*;
import tools.jackson.core.io.IOContext;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Field;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.*;

/** Diagnostic-only subclass: no counter fields or branches in the production/timing parser. */
public class ReaderInputs {
    static class AccountingParser extends VPackParser {
        long copyBytes, containerStarts, names, nameDecodes;
        final Set<ParseFrame> frames = Collections.newSetFromMap(new IdentityHashMap<>());
        final byte[] caller;
        int maxDepth;
        long rootBuffers;
        byte[] stableRoot;
        final Set<String> uniqueNames = new HashSet<>();
        AccountingParser(ObjectReadContext c, IOContext io, int features, int format, byte[] b, int offset, int len, tools.jackson.core.sym.ByteQuadsCanonicalizer symbols) {
            super(c, io, features, format, null, b, offset, offset + len, false, symbols);
            caller = b;
        }
        AccountingParser(ObjectReadContext c, IOContext io, int features, int format, InputStream in,
                tools.jackson.core.sym.ByteQuadsCanonicalizer symbols) {
            super(c, io, features, format, in, io.allocReadIOBuffer(), 0, 0, true, symbols);
            caller = null;
        }
        @Override protected byte[] _readRootContainer(int tb) {
            byte[] result = super._readRootContainer(tb);
            int header = 1;
            if (tb == VPackConstants.VPACK_ARRAY_COMPACT || tb == VPackConstants.VPACK_OBJECT_COMPACT) {
                while ((result[header++] & 128) != 0) { }
            } else {
                header += tb <= VPackConstants.VPACK_ARRAY_IDX_LAST ? widthFromTypeByte_NoIdx(tb) : widthFromTypeByte_Obj(tb);
            }
            copyBytes += header;
            rootBuffers++;
            return result;
        }
        @Override protected byte[] _growContainerBuffer(byte[] buffer, int length) {
            copyBytes += buffer.length;
            return super._growContainerBuffer(buffer, length);
        }
        @Override protected void _copyContainerBytes(byte[] dest, int pos, int count) {
            copyBytes += count;
            super._copyContainerBytes(dest, pos, count);
        }
        @Override protected String _readPropertyName(byte[] b, int pos) {
            String name = super._readPropertyName(b, pos);
            names++;
            uniqueNames.add(name);
            return name;
        }
        @Override protected String _decodePropertyName(byte[] b, int start, int len) {
            nameDecodes++;
            return super._decodePropertyName(b, start, len);
        }
        @Override public JsonToken nextToken() {
            JsonToken t = super.nextToken();
            if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) {
                containerStarts++;
                ParseFrame frame = _parseStack.peek();
                frames.add(frame);
                if (caller != null) {
                    if (frame.buf != caller) throw new IllegalStateException("Container did not share caller storage");
                } else if (_parseStack.size() == 1) stableRoot = frame.buf;
                else if (frame.buf != stableRoot) throw new IllegalStateException("Nested stream container copied/refill storage");
                maxDepth = Math.max(maxDepth, _parseStack.size());
            }
            return t;
        }
        Map<String, Object> counts() {
            return Map.of("containerCopyBytes", copyBytes, "frames", frames.size(), "containerStarts", containerStarts, "nameOccurrences", names, "nameDecodes", nameDecodes,
                    "uniqueNames", uniqueNames.size(), "maximumDepth", maxDepth, "rootBuffers", rootBuffers);
        }
    }
    static class AccountingFactory extends VPackFactory {
        @Override protected JsonParser _createParser(ObjectReadContext c, IOContext io, byte[] b, int offset, int len) {
            return new AccountingParser(c, io, c.getStreamReadFeatures(_streamReadFeatures),
                    c.getFormatReadFeatures(_formatReadFeatures), b, offset, len,
                    _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures));
        }
        @Override protected JsonParser _createParser(ObjectReadContext c, IOContext io, InputStream in) {
            return new AccountingParser(c, io, c.getStreamReadFeatures(_streamReadFeatures),
                    c.getFormatReadFeatures(_formatReadFeatures), in,
                    _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures));
        }
    }
    static String hash(byte[] b) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(b));
    }
    public static void main(String[] args) throws Exception {
        Path out = Files.createDirectories(Path.of(args[0]));
        Map<String, Object> manifest = new LinkedHashMap<>();
        for (Bench.Format format : Bench.Format.values()) {
            Bench bench = new Bench();
            bench.format = format;
            bench.batchSize = 1000;
            bench.setup(); // All original setup correctness checks still execute.
            if (!bench.treeReadCursor().equals(bench.treeReadCursorInputStream())) {
                throw new IllegalStateException("cursor InputStream tree differs: " + format);
            }
            Map<String, Object> entries = new LinkedHashMap<>();
            for (String input : List.of("documentBytes", "cursorBytes", "sequenceBytes")) {
                Field field = Bench.class.getDeclaredField(input);
                field.setAccessible(true);
                byte[] bytes = (byte[]) field.get(bench);
                String file = format + "-" + input + ".bin";
                Files.write(out.resolve(file), bytes);
                entries.put(input, Map.of("bytes", bytes.length, "sha256", hash(bytes), "file", file));
                if (!input.equals("cursorBytes")) continue;
                List<String> tokens = new ArrayList<>();
                Set<String> unique = new HashSet<>();
                long names = 0, containers = 0;
                int depth = 0, maximum = 0;
                try (JsonParser p = format.newMapper().createParser(bytes)) {
                    JsonToken t;
                    while ((t = p.nextToken()) != null) {
                        String value = t.name();
                        if (t == JsonToken.PROPERTY_NAME) { names++; unique.add(p.currentName()); value += ":" + p.currentName(); }
                        else if (t.isScalarValue()) value += ":" + p.getString();
                        if (t == JsonToken.START_ARRAY || t == JsonToken.START_OBJECT) { containers++; maximum = Math.max(maximum, ++depth); }
                        if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) depth--;
                        tokens.add(value);
                    }
                }
                byte[] transcript = JsonMapper.shared().writeValueAsBytes(tokens);
                Files.write(out.resolve(format + "-cursor-tokens.json"), transcript);
                entries.put("shape", Map.of("names", names, "uniqueNames", unique.size(), "containers", containers, "maximumDepth", maximum,
                        "tokenCount", tokens.size(), "tokenSha256", hash(transcript)));
                if (format == Bench.Format.VPACK) {
                    AccountingFactory factory = new AccountingFactory();
                    try (AccountingParser p = (AccountingParser) factory.createParser(bytes)) {
                        while (p.nextToken() != null) { }
                        Map<String, Object> counts = new LinkedHashMap<>(p.counts());
                        try (AccountingParser stream = (AccountingParser) factory.createParser(new java.io.ByteArrayInputStream(bytes))) {
                            while (stream.nextToken() != null) { }
                            counts.put("stream", stream.counts());
                        }
                        try (AccountingParser warm = (AccountingParser) factory.createParser(bytes)) {
                            while (warm.nextToken() != null) { }
                            counts.put("warm", warm.counts());
                        }
                        entries.put("accounting", counts);
                    }
                }
            }
            manifest.put(format.name(), entries);
        }
        Files.write(out.resolve("manifest.json"), JsonMapper.shared().writerWithDefaultPrettyPrinter().writeValueAsBytes(manifest));
        Map<String, Object> supplemental = new LinkedHashMap<>();
        for (Bench.Format format : Bench.Format.values()) {
            for (String naming : List.of("repeated", "unique")) {
                ReaderDiagnosticBench.Names state = new ReaderDiagnosticBench.Names();
                state.format = format; state.naming = naming; state.setup();
                supplemental.put(format + "-names-" + naming, inspect(state.bytes, format));
            }
            for (int depth : new int[]{1, 4, 7, 16, 32}) {
                ReaderDiagnosticBench.Depth state = new ReaderDiagnosticBench.Depth();
                state.format = format; state.depth = depth; state.setup();
                supplemental.put(format + "-depth-" + depth, inspect(state.bytes, format));
            }
        }
        Files.write(out.resolve("supplemental.json"), JsonMapper.shared().writerWithDefaultPrettyPrinter().writeValueAsBytes(supplemental));
    }
    static Map<String, Object> inspect(byte[] bytes, Bench.Format format) throws Exception {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("bytes", bytes.length); entry.put("sha256", hash(bytes));
        if (format == Bench.Format.VPACK) {
            try (AccountingParser p = (AccountingParser) new AccountingFactory().createParser(bytes)) {
                while (p.nextToken() != null) { }
                Map<String, Object> counts = new LinkedHashMap<>(p.counts());
                try (AccountingParser stream = (AccountingParser) new AccountingFactory().createParser(new java.io.ByteArrayInputStream(bytes))) {
                    while (stream.nextToken() != null) { }
                    counts.put("stream", stream.counts());
                }
                entry.put("accounting", counts);
            }
        }
        return entry;
    }
}
