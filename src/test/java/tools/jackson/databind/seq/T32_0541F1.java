package tools.jackson.databind.seq;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0541F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] BEAN_A1 = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 31 03");
private static final byte[] BEAN_A2 = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 32 03");
private static final byte[] EXPLICIT_TYPE_SEQUENCE = VPackWireFixtureTest.hex(
            "0b 0b 02 41 61 31 41 62 32 03 06"
          + "0b 07 01 41 61 31 03"
          + "0b 07 01 41 61 31 03");
private static final byte[] UPDATED_X = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 48 75 70 64 61 74 65 64 58 01");
private static final byte[] UPDATED_Y = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 48 75 70 64 61 74 65 64 59 01");
private static final byte[] ANY_DISABLED = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 2a 03");
private static final byte[] ANY_SORTED = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 32 41 62 31 41 79 34 41 78 33 03 06 0c 09");
private static final byte[] ANY_MAPPER_NON_EMPTY = VPackWireFixtureTest.hex(
            "0b 17 01 49 6e 6f 6e 2d 65 6d 70 74 79 48 70 72 6f 70 65 72 74 79 03");
private static final byte[] ANY_ID_NAME = VPackWireFixtureTest.hex(
            "0b 13 02 42 69 64 31 44 6e 61 6d 65 44 74 65 73 74 03 07");
private static final byte[] ANY_ID_ONLY = VPackWireFixtureTest.hex(
            "0b 08 01 42 69 64 31 03");
private static final byte[] DYNAMIC_SORTED = VPackWireFixtureTest.hex(
            "0b 1c 05 41 61 41 31 41 6a 41 32 41 6c 41 33 "
          + "41 62 41 34 41 7a 41 35 03 0f 07 0b 13");

    void testReaderForVpack() throws Exception {
        X x = new X("dummy");
        MAPPER.readerForUpdating(x).readValue(UPDATED_X);
        assertEquals("updatedX", x.getValue());

        Y y = new Y("dummy");
        MAPPER.readerForUpdating(y).readValue(UPDATED_Y);
        assertEquals("updatedY", y.getValue());
    }
private static Bean2592NoAnnotations nonEmptyBean() {
        Bean2592NoAnnotations bean = new Bean2592NoAnnotations();
        bean.add("non-empty", "property");
        bean.add("empty", "");
        bean.add("null", null);
        return bean;
    }
private static byte[] concat(byte[]... values) {
        int length = 0;
        for (byte[] value : values) {
            length += value.length;
        }
        byte[] result = new byte[length];
        int offset = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, result, offset, value.length);
            offset += value.length;
        }
        return result;
    }
private static void assertVpack(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + toHex(actual));
    }
private static String toHex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte b : value) {
            if (result.length() != 0) result.append(' ');
            result.append(String.format("%02x", b & 0xff));
        }
        return result.toString();
    }
static class Bean {
        public int a;

        Bean(int value) { a = value; }
    }
static class BareBase {
        public int a = 1;
    }
@JsonPropertyOrder({ "a", "b" })
    static class BareBaseExt extends BareBase {
        public int b = 2;
    }
static class BareBaseCloseable extends BareBase implements Closeable {
        public int c = 3;
        boolean closed;

        @Override
        public void close() throws IOException {
            closed = true;
        }
    }
class X {
        private String value;

        X(String value) { this.value = value; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
class Y extends X {
        Y(@JsonProperty("value") String value) { super(value); }
    }
@JsonPropertyOrder(alphabetic = true)
    static class Bean518 {
        public int b;
        protected Map<String, Object> extra = new HashMap<>();
        public int a;

        Bean518(int a, int b, Map<String, Object> extra) {
            this.a = a;
            this.b = b;
            this.extra = extra;
        }

        @JsonAnyGetter
        public Map<String, Object> getExtra() { return extra; }
    }
static class DynaBean5215 {
        public String l;
        public String j;
        public String a;
        protected Map<String, Object> extensions = new LinkedHashMap<>();

        @JsonAnyGetter
        public Map<String, Object> getExtensions() { return extensions; }

        public void addExtension(String name, Object value) {
            extensions.put(name, value);
        }
    }
static class AnyOnlyBean {
        @JsonAnyGetter
        public Map<String, Integer> any() {
            return Map.of("a", 3);
        }
    }
static class NotEvenAnyBean extends AnyOnlyBean {
        @JsonAnyGetter(enabled = false)
        @Override
        public Map<String, Integer> any() {
            throw new IllegalStateException("disabled any-getter invoked");
        }

        public int getValue() { return 42; }
    }
static class Bean2592NoAnnotations {
        protected Map<String, String> properties = new LinkedHashMap<>();

        @JsonAnyGetter
        public Map<String, String> getProperties() { return properties; }
        public void add(String key, String value) { properties.put(key, value); }
    }
@JsonFilter("Bean2592")
    static class Bean2592WithFilter extends Bean2592NoAnnotations {
        Bean2592WithFilter(Bean2592NoAnnotations source) {
            properties.putAll(source.properties);
        }
    }
static class ObjectNodeAnyGetterFieldBean {
        public int id;
        @JsonAnyGetter
        public ObjectNode extra;
    }
static class JsonNodeAnyGetterFieldBean {
        public int id;
        @JsonAnyGetter
        public tools.jackson.databind.JsonNode extra;
    }

    void __invoke_testReaderForVpack() throws Exception {
        try {
            testReaderForVpack();
        } finally {
        }
    }

}
