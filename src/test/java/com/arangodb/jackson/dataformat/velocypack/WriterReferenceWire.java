package com.arangodb.jackson.dataformat.velocypack;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/** Independent wire arithmetic: no generator helpers, constants or VPackUtil. */
final class WriterReferenceWire {
    static byte[] cat(byte[]... parts) {
        var out = new ByteArrayOutputStream();
        for (byte[] p : parts) out.writeBytes(p);
        return out.toByteArray();
    }
    static byte[] le(long n, int width) {
        byte[] b = new byte[width];
        for (int i = 0; i < width; i++) b[i] = (byte) (n >>> (8 * i));
        return b;
    }
    static byte[] vb(int n) {
        var b = new ByteArrayOutputStream();
        do { b.write((n & 127) | (n > 127 ? 128 : 0)); n >>>= 7; } while (n != 0);
        return b.toByteArray();
    }
    static byte[] compact(boolean object, int count, byte[] content) {
        byte[] c = vb(count);
        for (int i = 0, j = c.length - 1; i < j; i++, j--) {
            byte t = c[i]; c[i] = c[j]; c[j] = t;
        }
        int total = content.length + c.length + 2;
        while (total != 1 + vb(total).length + content.length + c.length)
            total = 1 + vb(total).length + content.length + c.length;
        return cat(new byte[]{(byte) (object ? 0x14 : 0x13)}, vb(total), content, c);
    }
    static byte[] indexed(boolean object, boolean sorted, byte[]... entries) {
        int contentLength = Arrays.stream(entries).mapToInt(b -> b.length).sum();
        int width = 1;
        while (1L + 2 * width + contentLength + (long) entries.length * width > ((1L << (8 * width)) - 1))
            width *= 2;
        int total = 1 + 2 * width + contentLength + entries.length * width;
        var out = new ByteArrayOutputStream();
        int base = object ? sorted ? 0x0b : 0x0f : 0x06;
        out.write(base + (width == 1 ? 0 : width == 2 ? 1 : 2));
        out.writeBytes(le(total, width)); out.writeBytes(le(entries.length, width));
        for (byte[] b : entries) out.writeBytes(b);
        int offset = 1 + 2 * width;
        for (byte[] b : entries) { out.writeBytes(le(offset, width)); offset += b.length; }
        return out.toByteArray();
    }
    static byte[] noIndex(byte[]... entries) {
        int content = Arrays.stream(entries).mapToInt(b -> b.length).sum();
        int width = content + 2 <= 255 ? 1 : content + 3 <= 65535 ? 2 : 4;
        return cat(new byte[]{(byte) (width == 1 ? 2 : width == 2 ? 3 : 4)}, le(content + 1 + width, width), cat(entries));
    }
    static byte[] binary(int length) {
        int width = length <= 255 ? 1 : length <= 65535 ? 2 : 4;
        return cat(new byte[]{(byte) (0xc0 + width - 1)}, le(length, width), new byte[length]);
    }
}
