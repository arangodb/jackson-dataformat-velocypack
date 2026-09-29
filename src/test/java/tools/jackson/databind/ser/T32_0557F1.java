package tools.jackson.databind.ser;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0557F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ALPHA_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] SIMPLE_GETTER = VPackWireFixtureTest.hex(
            "0b 1c 03 46 6c 65 6e 67 74 68 20 ef 44 73 69 7a 65 33 "
          + "45 76 61 6c 75 65 30 03 0c 12");
private static final byte[] SIMPLE_GETTER_2 = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 33 03");
private static final byte[] SIMPLE_GETTER_3 = VPackWireFixtureTest.hex(
            "0b 07 01 41 79 38 03");
private static final byte[] MIXIN_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 62 32 41 61 31 41 63 33 41 64 34 06 03 09 0c");
private static final byte[] ORDER_268 = VPackWireFixtureTest.hex(
            "0b 17 04 41 61 41 61 41 62 41 62 41 78 41 78 41 7a 41 7a "
          + "03 07 0b 0f");
private static final byte[] ALPHABETIC_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 31 41 62 32 41 63 33 41 64 34 03 06 09 0c");
private static final byte[] STRICT_CREATOR_FIRST = VPackWireFixtureTest.hex(
            "0b 0f 03 41 63 32 41 61 33 41 62 30 06 09 03");
private static final byte[] STRICT_ALPHA = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 32 41 62 30 41 63 31 03 06 09");
private static final byte[] ANNOTATIONS_ENABLED = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06");
private static final byte[] ANNOTATIONS_DISABLED = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");
private static final byte[] CHARS_AS_STRING = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] CHARS_AS_ARRAY = VPackWireFixtureTest.hex(
            "02 08 41 61 41 62 41 63");

    // Provenance: SerializationFeaturesTest#testAnnotationsDisabled().
    void testAnnotationsDisabledVpack() throws Exception {
        assertTrue(MAPPER.isEnabled(MapperFeature.USE_ANNOTATIONS));
        assertArrayEquals(ANNOTATIONS_ENABLED, MAPPER.writeValueAsBytes(new AnnoBean()));

        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.USE_ANNOTATIONS, false)
                .build();
        assertArrayEquals(ANNOTATIONS_DISABLED, mapper.writeValueAsBytes(new AnnoBean()));
    }

    // Provenance: SerializationFeaturesTest#testCharArrays().
    void testCharArraysVpack() throws Exception {
        char[] chars = { 'a', 'b', 'c' };
        assertArrayEquals(CHARS_AS_STRING, MAPPER.writeValueAsBytes(chars));
        assertArrayEquals(CHARS_AS_ARRAY,
                MAPPER.writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsBytes(chars));
    }

    // Provenance: SerializationFeaturesTest#testCloseCloseable().
    void testCloseCloseableVpack() throws Exception {
        CloseableBean bean = new CloseableBean();
        MAPPER.writeValueAsBytes(bean);
        assertFalse(bean.wasClosed);

        bean = new CloseableBean();
        MAPPER.writer().writeValueAsBytes(bean);
        assertFalse(bean.wasClosed);

        ObjectMapper mapper = VPackMapper.builder()
                .enable(SerializationFeature.CLOSE_CLOSEABLE)
                .build();
        bean = new CloseableBean();
        mapper.writeValueAsBytes(bean);
        assertTrue(bean.wasClosed);

        bean = new CloseableBean();
        mapper.writer().writeValueAsBytes(bean);
        assertTrue(bean.wasClosed);

        bean = new CloseableBean();
        MAPPER.writerFor(CloseableBean.class)
                .with(SerializationFeature.CLOSE_CLOSEABLE)
                .writeValueAsBytes(bean);
        assertTrue(bean.wasClosed);
    }
private static Map<?, ?> readMap(byte[] bytes) throws Exception {
        return MAPPER.readValue(bytes, Map.class);
    }
static final class SizeClassGetter {
        @JsonProperty public int size() { return 3; }
        @JsonProperty("length") public int foobar() { return -17; }
        @JsonProperty protected int value() { return 0; }
    }
static final class SizeClassGetter2 {
        @JsonProperty protected int getX() { return 3; }
    }
static final class SizeClassGetter3 {
        @JsonSerialize protected int getY() { return 8; }
    }
static class BasePojo {
        @JsonProperty public int width() { return 3; }
        @JsonProperty public int length() { return 7; }
    }
static class PojoSubclass extends BasePojo {
        @Override public int width() { return 9; }
    }
interface PojoInterface {
        @JsonProperty int width();
        @JsonProperty int length();
    }
static class PojoImpl implements PojoInterface {
        @Override public int width() { return 1; }
        @Override public int length() { return 2; }
        public int getFoobar() { return 5; }
    }
@JsonPropertyOrder({ "c", "a", "b" })
    static class BeanWithOrder {
        public int d, b, a, c;
        BeanWithOrder(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }
@JsonPropertyOrder({ "b", "a", "foobar", "c" })
    static class OrderMixIn { }
@JsonPropertyOrder({ "a", "b", "x", "z" })
    static class BeanFor268 {
        @JsonProperty("a") public String xA = "a";
        @JsonProperty("z") public String aZ = "z";
        @JsonProperty("b") public String xB() { return "b"; }
        @JsonProperty("x") public String aX() { return "x"; }
    }
static class BeanFor459 {
        public int d = 4;
        public int c = 3;
        public int b = 2;
        public int a = 1;
    }
static class BeanForStrictOrdering {
        private final int a;
        private int b;
        private final int c;

        @JsonCreator
        BeanForStrictOrdering(@JsonProperty("c") int c, @JsonProperty("a") int a) {
            this.a = a;
            this.c = c;
        }

        public int getA() { return a; }
        public int getB() { return b; }
        public int getC() { return c; }
    }
static class AnnoBean {
        public int getX() { return 1; }
        @JsonProperty("y") private int getY() { return 2; }
    }
static class CloseableBean implements AutoCloseable {
        public int a = 3;
        boolean wasClosed;

        @Override
        public void close() throws IOException {
            wasClosed = true;
        }
    }

    void __invoke_testAnnotationsDisabledVpack() throws Exception {
        try {
            testAnnotationsDisabledVpack();
        } finally {
        }
    }


    void __invoke_testCharArraysVpack() throws Exception {
        try {
            testCharArraysVpack();
        } finally {
        }
    }


    void __invoke_testCloseCloseableVpack() throws Exception {
        try {
            testCloseCloseableVpack();
        } finally {
        }
    }

}
