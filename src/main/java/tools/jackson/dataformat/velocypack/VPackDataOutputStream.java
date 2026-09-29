package tools.jackson.dataformat.velocypack;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * OutputStream adapter used for DataOutput targets.
 *
 * <p>The core adapter intentionally does not delegate lifecycle methods. VPack
 * needs the target's lifecycle semantics, while still leaving DataOutput
 * implementations that are not Closeable or Flushable as no-ops.</p>
 */
final class VPackDataOutputStream extends OutputStream {
    private final DataOutput _output;

    VPackDataOutputStream(DataOutput output) {
        _output = Objects.requireNonNull(output, "output");
    }

    DataOutput target() {
        return _output;
    }

    @Override
    public void write(int b) throws IOException {
        _output.write(b);
    }

    @Override
    @SuppressWarnings("NullableProblems") // OutputStream's byte-array overloads require non-null input.
    public void write(byte[] b) throws IOException {
        Objects.requireNonNull(b, "b");
        _output.write(b, 0, b.length);
    }

    @Override
    @SuppressWarnings("NullableProblems") // OutputStream's byte-array overloads require non-null input.
    public void write(byte[] b, int off, int len) throws IOException {
        Objects.requireNonNull(b, "b");
        _output.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        if (_output instanceof Flushable flushable) {
            flushable.flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (_output instanceof Closeable closeable) {
            closeable.close();
        }
    }
}
