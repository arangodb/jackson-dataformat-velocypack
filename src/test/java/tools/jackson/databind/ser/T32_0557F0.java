package tools.jackson.databind.ser;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0557F0 {
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

    // Provenance: SerializationAnnotationsTest#testSimpleGetter().
    void testSimpleGetterVpack() throws Exception {
        assertArrayEquals(SIMPLE_GETTER, MAPPER.writeValueAsBytes(new SizeClassGetter()));
        assertEquals(Map.of("size", 3, "length", -17, "value", 0),
                readMap(SIMPLE_GETTER));
    }

    // Provenance: SerializationAnnotationsTest#testSimpleGetter2().
    void testSimpleGetter2Vpack() throws Exception {
        assertArrayEquals(SIMPLE_GETTER_2, MAPPER.writeValueAsBytes(new SizeClassGetter2()));
        assertEquals(Map.of("x", 3), readMap(SIMPLE_GETTER_2));
    }

    // Provenance: SerializationAnnotationsTest#testSimpleGetter3().
    void testSimpleGetter3Vpack() throws Exception {
        assertArrayEquals(SIMPLE_GETTER_3, MAPPER.writeValueAsBytes(new SizeClassGetter3()));
        assertEquals(Map.of("y", 8), readMap(SIMPLE_GETTER_3));
    }

    // Provenance: SerializationAnnotationsTest#testSimpleGetterInheritance().
    void testSimpleGetterInheritanceVpack() throws Exception {
        assertEquals(Map.of("length", 7, "width", 9),
                readMap(MAPPER.writeValueAsBytes(new PojoSubclass())));
    }

    // Provenance: SerializationAnnotationsTest#testSimpleGetterInterfaceImpl().
    void testSimpleGetterInterfaceImplVpack() throws Exception {
        assertEquals(Map.of("foobar", 5, "width", 1, "length", 2),
                readMap(MAPPER.writeValueAsBytes(new PojoImpl())));
    }

    // Provenance: SerializationAnnotationsTest#testOrderWithMixins().
    void testOrderWithMixinsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(BeanWithOrder.class, OrderMixIn.class)
                .build();
        assertArrayEquals(MIXIN_ORDER, mapper.writeValueAsBytes(new BeanWithOrder(1, 2, 3, 4)));
    }

    // Provenance: SerializationAnnotationsTest#testOrderWrt268().
    void testOrderWrt268Vpack() throws Exception {
        assertArrayEquals(ORDER_268, MAPPER.writeValueAsBytes(new BeanFor268()));
    }

    // Provenance: SerializationAnnotationsTest#testOrderWithFeature().
    void testOrderWithFeatureVpack() throws Exception {
        assertArrayEquals(ALPHABETIC_ORDER, ALPHA_MAPPER.writeValueAsBytes(new BeanFor459()));
    }

    // Provenance: SerializationAnnotationsTest#testStrictAlphaAndCreatorOrdering().
    void testStrictAlphaAndCreatorOrderingVpack() throws Exception {
        assertTrue(ALPHA_MAPPER.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        assertTrue(ALPHA_MAPPER.isEnabled(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST));
        assertArrayEquals(STRICT_CREATOR_FIRST,
                ALPHA_MAPPER.writeValueAsBytes(new BeanForStrictOrdering(2, 3)));

        ObjectMapper strict = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                .build();
        assertArrayEquals(STRICT_ALPHA,
                strict.writeValueAsBytes(new BeanForStrictOrdering(1, 2)));
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

    void __invoke_testSimpleGetterVpack() throws Exception {
        try {
            testSimpleGetterVpack();
        } finally {
        }
    }


    void __invoke_testSimpleGetter2Vpack() throws Exception {
        try {
            testSimpleGetter2Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleGetter3Vpack() throws Exception {
        try {
            testSimpleGetter3Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleGetterInheritanceVpack() throws Exception {
        try {
            testSimpleGetterInheritanceVpack();
        } finally {
        }
    }


    void __invoke_testSimpleGetterInterfaceImplVpack() throws Exception {
        try {
            testSimpleGetterInterfaceImplVpack();
        } finally {
        }
    }


    void __invoke_testOrderWithMixinsVpack() throws Exception {
        try {
            testOrderWithMixinsVpack();
        } finally {
        }
    }


    void __invoke_testOrderWrt268Vpack() throws Exception {
        try {
            testOrderWrt268Vpack();
        } finally {
        }
    }


    void __invoke_testOrderWithFeatureVpack() throws Exception {
        try {
            testOrderWithFeatureVpack();
        } finally {
        }
    }


    void __invoke_testStrictAlphaAndCreatorOrderingVpack() throws Exception {
        try {
            testStrictAlphaAndCreatorOrderingVpack();
        } finally {
        }
    }

}
