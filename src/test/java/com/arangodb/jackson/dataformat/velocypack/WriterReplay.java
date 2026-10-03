package com.arangodb.jackson.dataformat.velocypack;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.io.IOContext;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.io.ByteArrayOutputStream;

/** Test-only dispatch. Both constructors receive identical arguments and calls. */
final class WriterReplay implements AutoCloseable {
    final JsonGenerator g;

    WriterReplay(boolean legacy, int features, int streamFeatures, OutputStream out,
                 byte[] buffer, int offset) {
        this(legacy, features, streamFeatures, out, buffer, offset, false, BaseTestForVPack.testIOContext());
    }

    WriterReplay(boolean legacy, int features, int streamFeatures, OutputStream out,
                 byte[] buffer, int offset, boolean recyclable, IOContext io) {
        var ctxt = ObjectWriteContext.empty();
        g = legacy
            ? buffer == null
                ? new LegacyVPackGenerator(ctxt, io, streamFeatures, features, out)
                : new LegacyVPackGenerator(ctxt, io, streamFeatures, features, out, buffer, offset, recyclable)
            : buffer == null
                ? new VPackGenerator(ctxt, io, streamFeatures, features, out)
                : new VPackGenerator(ctxt, io, streamFeatures, features, out, buffer, offset, recyclable);
    }

    void feature(VPackWriteFeature f, boolean state) {
        if (g instanceof LegacyVPackGenerator l) l.configure(f, state);
        else ((VPackGenerator) g).configure(f, state);
    }
    void tag(long n) {
        if (g instanceof LegacyVPackGenerator l) l.writeTaggedValuePrefix(n);
        else ((VPackGenerator) g).writeTaggedValuePrefix(n);
    }
    void raw(byte b) {
        if (g instanceof LegacyVPackGenerator l) l.writeRaw(b);
        else ((VPackGenerator) g).writeRaw(b);
    }
    void bytes(byte[] b, int offset, int length) {
        if (g instanceof LegacyVPackGenerator l) l.writeBytes(b, offset, length);
        else ((VPackGenerator) g).writeBytes(b, offset, length);
    }
    static byte[] encode(boolean legacy, int features, Consumer<WriterReplay> calls) {
        var out = new ByteArrayOutputStream();
        try (var w = new WriterReplay(legacy, features, StreamWriteFeature.collectDefaults(), out, null, 0)) {
            calls.accept(w);
        }
        return out.toByteArray();
    }
    @Override public void close() { g.close(); }
}
