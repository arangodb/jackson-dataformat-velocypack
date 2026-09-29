package tools.jackson.core.unittest.util;

import java.io.ByteArrayOutputStream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.Version;
import tools.jackson.core.util.VersionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0117Fixture {

    void versionPartParsingVpack() {
        assertEquals(13, VersionUtil.parseVersionPart("13"));
        assertEquals(27, VersionUtil.parseVersionPart("27.8"));
        assertEquals(0, VersionUtil.parseVersionPart("-3"));
    }

    void versionParsingVpack() {
        assertEquals(new Version(1, 2, 15, "foo", "group", "artifact"),
                VersionUtil.parseVersion("1.2.15-foo", "group", "artifact"));
        Version version = VersionUtil.parseVersion("1.2.3-SNAPSHOT", "group", "artifact");
        assertEquals("group/artifact/1.2.3-SNAPSHOT", version.toFullString());
    }
private static void assertIntArray(int elements, int pre, int post) throws Exception {
        int[] values = new int[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getIntValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertLongArray(int elements, int pre, int post) throws Exception {
        long[] values = new long[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                assertEquals(i, parser.getLongValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertDoubleArray(int elements, int pre, int post) throws Exception {
        double[] values = new double[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) values[i] = i - pre;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
                assertEquals((double) i, parser.getDoubleValue());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertStringArray(int elements, int pre, int post) throws Exception {
        String[] values = new String[elements + pre + post];
        for (int i = pre; i < pre + elements; ++i) {
            int value = i - pre;
            values[i] = (value & 1) == 0 ? "value-" + value : "é-" + value;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (JsonGenerator generator = new VPackFactory().createGenerator(output)) {
            generator.writeArray(values, pre, elements);
        }
        try (JsonParser parser = new VPackFactory().createParser(output.toByteArray())) {
            assertArrayHeader(parser, elements);
            for (int i = 0; i < elements; ++i) {
                assertEquals(JsonToken.VALUE_STRING, parser.nextToken());
                assertEquals(values[pre + i], parser.getString());
            }
            assertArrayEnd(parser);
        }
    }
private static void assertArrayHeader(JsonParser parser, int elements) throws Exception {
        assertEquals(JsonToken.START_ARRAY, parser.nextToken());
        if (elements == 0) return;
    }
private static void assertArrayEnd(JsonParser parser) throws Exception {
        assertEquals(JsonToken.END_ARRAY, parser.nextToken());
        assertNull(parser.nextToken());
    }
private static void assertTokens(byte[] input, JsonToken... expected) throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(input)) {
            for (JsonToken token : expected) assertEquals(token, parser.nextToken());
            assertNull(parser.nextToken());
        }
    }
private static byte[] bytes(int... values) {
        byte[] result = new byte[values.length];
        for (int i = 0; i < values.length; ++i) result[i] = (byte) values[i];
        return result;
    }

    void __invoke_versionPartParsingVpack() throws Exception {
        try {
            versionPartParsingVpack();
        } finally {
        }
    }


    void __invoke_versionParsingVpack() throws Exception {
        try {
            versionParsingVpack();
        } finally {
        }
    }

}
