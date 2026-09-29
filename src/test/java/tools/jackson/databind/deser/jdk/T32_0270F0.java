package tools.jackson.databind.deser.jdk;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0270F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] URI_VALUE = VPackWireFixtureTest.hex(
            "4e 68 74 74 70 3a 2f 2f 66 6f 6f 2e 63 6f 6d");
private static final byte[] URI_INVALID = VPackWireFixtureTest.hex(
            "43 61 20 62");
private static final byte[] BIG_INTEGER = VPackWireFixtureTest.hex(
            "c8 0a 00 00 00 00 12 34 56 78 90 12 34 56 78 90");
private static final byte[] DECIMAL_PI = VPackWireFixtureTest.hex(
            "c8 03 fb ff ff ff 31 41 59");
private static final byte[] DOUBLE_ONE_POINT_FIVE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 3f");
private static final byte[] DOUBLE_E = VPackWireFixtureTest.hex(
            "1b 9b 91 04 8b 0a bf 05 40");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 42 00 00 00 00 00 f8 7f");
private static final byte[] MIXED = mixedArrayFixture();
private static final byte[] NESTED = nestedObjectFixture();

    // Provenance: JDKStringLikeTypeDeserTest#testURI().
    void testURI() throws Exception {
        ObjectReader reader = MAPPER.readerFor(URI.class);
        URI value = new URI("http://foo.com");
        assertEquals(value, reader.readValue(URI_VALUE));

        InvalidFormatException failure = assertThrows(InvalidFormatException.class,
                () -> reader.readValue(URI_INVALID));
        assertTrue(failure.getMessage().contains("not a valid textual representation"));
    }

    // Provenance: JDKStringLikeTypeDeserTest#testURL().
    void testURL() throws Exception {
        URL expected = new URL("http://foo.com");
        assertEquals(expected, MAPPER.readValue(URI_VALUE, URL.class));
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("18"), URL.class));

        // Arbitrary embedded Java objects have no representation in the local
        // VPack subset; the native embedded MinKey is therefore rejected by
        // URL databinding rather than being mistaken for a URL instance.
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackWireFixtureTest.hex("1e"), URL.class));
    }
private static void assertNotNullBigInteger(Object value) {
        assertTrue(value instanceof BigInteger, "expected an exact BigInteger result");
    }
@SuppressWarnings("unchecked")
    private static Map<?, ?> mapValue(Map<?, ?> map, String key) {
        return (Map<?, ?>) map.get(key);
    }
private static byte[] nestedUntypedFixture() {
        return compactObject(
                field("a", new byte[] { 0x33 }),
                field("b", compactArray(new byte[][] { { 0x31 }, { 0x32 } })),
                field("c", compactArray(new byte[][] { { 0x33 } })));
    }
private static byte[] mixedArrayFixture() {
        return compactArray(new byte[][] {
                { 0x31 },
                text("text"),
                { 0x1a },
                { 0x18 },
                { 0x1b, 0x1f, (byte) 0x85, (byte) 0xeb, 0x51, (byte) 0xb8, 0x1e, 0x09, 0x40 },
                compactObject(field("nested", text("object"))),
                compactArray(new byte[][] { { 0x31 }, { 0x32 }, { 0x33 } })
        });
    }
private static byte[] nestedObjectFixture() {
        byte[] value = compactObject(field("value", text("deep")));
        value = compactObject(field("level4", value));
        value = compactObject(field("level3", value));
        value = compactObject(field("level2", value));
        return compactObject(field("level1", value));
    }
private static byte[] largeArrayFixture() {
        byte[][] values = new byte[100][];
        for (int i = 0; i < values.length; ++i) {
            values[i] = i < 10 ? new byte[] { (byte) (0x30 + i) }
                    : new byte[] { 0x28, (byte) i };
        }
        return compactArray(values);
    }
private static byte[] largeObjectFixture() {
        byte[][] fields = new byte[50][];
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = field("key" + i, i < 10 ? new byte[] { (byte) (0x30 + i) }
                    : new byte[] { 0x28, (byte) i });
        }
        return compactObject(fields);
    }
private static byte[] field(String name, byte[] value) {
        return concat(text(name), value);
    }
private static byte[] text(String value) {
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        if (payload.length > 126) {
            throw new IllegalArgumentException("fixture is not a short string");
        }
        byte[] result = new byte[payload.length + 1];
        result[0] = (byte) (0x40 + payload.length);
        System.arraycopy(payload, 0, result, 1, payload.length);
        return result;
    }
private static byte[] compactArray(byte[]... values) {
        int bodyLength = 0;
        for (byte[] value : values) bodyLength += value.length;
        return compact(0x13, values, values.length);
    }
private static byte[] compactObject(byte[]... fields) {
        int bodyLength = 0;
        for (byte[] field : fields) bodyLength += field.length;
        return compact(0x14, fields, fields.length);
    }
private static byte[] compact(int marker, byte[][] body, int count) {
        int bodyLength = 0;
        for (byte[] value : body) bodyLength += value.length;
        int length = 1 + varintLength(1 + 1 + bodyLength + varintLength(count))
                + bodyLength + varintLength(count);
        while (length != 1 + varintLength(length) + bodyLength + varintLength(count)) {
            length = 1 + varintLength(length) + bodyLength + varintLength(count);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(length);
        output.write(marker);
        writeForward(output, length);
        for (byte[] value : body) output.writeBytes(value);
        writeReverse(output, count);
        return output.toByteArray();
    }
private static int varintLength(int value) {
        int length = 1;
        while ((value >>>= 7) != 0) ++length;
        return length;
    }
private static void writeForward(ByteArrayOutputStream output, int value) {
        do {
            int group = value & 0x7f;
            value >>>= 7;
            output.write(group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
    }
private static void writeReverse(ByteArrayOutputStream output, int value) {
        byte[] groups = new byte[varintLength(value)];
        int offset = 0;
        do {
            int group = value & 0x7f;
            value >>>= 7;
            groups[offset++] = (byte) (group | (value == 0 ? 0 : 0x80));
        } while (value != 0);
        for (int i = groups.length - 1; i >= 0; --i) output.write(groups[i]);
    }
private static byte[] concat(byte[]... values) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] value : values) output.writeBytes(value);
        return output.toByteArray();
    }

    void __invoke_testURI() throws Exception {
        try {
            testURI();
        } finally {
        }
    }


    void __invoke_testURL() throws Exception {
        try {
            testURL();
        } finally {
        }
    }

}
