package tools.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonAppend;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0560F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] GLOBAL_ALPHA_INDEX_DISABLED = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 30 41 62 30 41 63 30 03 06 09");
private static final byte[] IMPLICIT_CREATOR_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 63 31 41 61 32 41 62 30 06 09 03");
private static final byte[] ORDER_BY_INDEX = VPackWireFixtureTest.hex(
            "0b 17 05 41 66 30 41 75 30 41 62 30 41 61 30 41 72 30 "
          + "0c 09 03 0f 06");
private static final byte[] ALPHABETIC_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 61 31 41 62 32 41 63 33 41 64 34 03 06 09 0c");
private static final byte[] MIXIN_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 62 32 41 61 31 41 63 33 41 64 34 06 03 09 0c");
private static final byte[] ORDER_268 = VPackWireFixtureTest.hex(
            "0b 17 04 41 61 41 61 41 62 41 62 41 78 41 78 41 7a 41 7a "
          + "03 07 0b 0f");
private static final byte[] CREATOR_ALPHA_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 63 32 41 61 33 41 62 30 06 09 03");
private static final byte[] STRICT_ALPHA_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 32 41 62 30 41 63 31 03 06 09");
private static final byte[] ATTRIBUTES = VPackWireFixtureTest.hex(
            "0b 2a 03 45 76 61 6c 75 65 28 0d 42 69 64 46 61 62 63 31 32 33 "
          + "45 65 78 74 72 61 0b 0c 02 41 78 33 41 79 41 42 03 06 15 0b 03");
private static final byte[] ATTRIBUTE_DESC = VPackWireFixtureTest.hex(
            "0b 17 02 45 76 61 6c 75 65 28 1c 44 64 65 73 63 44 6e 69 63 65 0b 03");
private static final byte[] ATTRIBUTE_OMITTED = VPackWireFixtureTest.hex(
            "0b 0c 01 45 76 61 6c 75 65 28 1c 03");
private static final byte[] BASIC_SETUP = VPackWireFixtureTest.hex(
            "0b 28 03 42 70 31 46 70 75 62 6c 69 63 42 70 32 49 70 72 6f 74 65 63 74 65 64 "
          + "42 70 33 47 70 72 69 76 61 74 65 03 0d 1a");
private static final byte[] SMALL_INT_ARRAY = VPackWireFixtureTest.hex(
            "02 06 30 31 32 33");
private static final byte[] NATIVE_BINARY = VPackWireFixtureTest.hex(
            "c0 49 "
          + "61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 71 72 73 74 75 76 77 78 79 7a "
          + "31 32 33 34 35 36 37 38 39 30 "
          + "61 62 63 64 65 66 67 68 69 6a 6b 6c 6d 6e 6f 70 71 72 73 74 75 76 77 78 79 7a "
          + "31 32 33 34 35 36 37 38 39 30 58");

    // Provenance: SerializationOrderTest#testGlobalAlphabeticWithIndexDisabled().
    void testGlobalAlphabeticWithIndexDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_PROPERTIES_BY_INDEX)
                .build();
        assertArrayEquals(GLOBAL_ALPHA_INDEX_DISABLED,
                mapper.writeValueAsBytes(new AlphaWithIndexBean()));
    }

    // Provenance: SerializationOrderTest#testImplicitOrderByCreator().
    void testImplicitOrderByCreatorVpack() throws Exception {
        assertArrayEquals(IMPLICIT_CREATOR_ORDER,
                MAPPER.writeValueAsBytes(new BeanWithCreator(1, 2)));
    }

    // Provenance: SerializationOrderTest#testOrderByIndexEtc().
    void testOrderByIndexEtcVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        assertArrayEquals(ORDER_BY_INDEX,
                mapper.writeValueAsBytes(new OrderingByIndexBean()));
    }

    // Provenance: SerializationOrderTest#testOrderWithFeature().
    void testOrderWithFeatureVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        assertArrayEquals(ALPHABETIC_ORDER,
                mapper.writeValueAsBytes(new BeanFor459()));
    }

    // Provenance: SerializationOrderTest#testOrderWithMixins().
    void testOrderWithMixinsVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(BeanWithOrder.class, OrderMixIn.class)
                .build();
        assertArrayEquals(MIXIN_ORDER,
                mapper.writeValueAsBytes(new BeanWithOrder(1, 2, 3, 4)));
    }

    // Provenance: SerializationOrderTest#testOrderWrt268().
    void testOrderWrt268Vpack() throws Exception {
        assertArrayEquals(ORDER_268, MAPPER.writeValueAsBytes(new BeanFor268()));
    }

    // Provenance: SerializationOrderTest#testStrictAlphaAndCreatorOrdering().
    void testStrictAlphaAndCreatorOrderingVpack() throws Exception {
        ObjectMapper alpha = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        assertArrayEquals(CREATOR_ALPHA_ORDER,
                alpha.writeValueAsBytes(new BeanForStrictOrdering(2, 3)));

        ObjectMapper strict = VPackMapper.builder()
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                .build();
        assertArrayEquals(STRICT_ALPHA_ORDER,
                strict.writeValueAsBytes(new BeanForStrictOrdering(1, 2)));
    }
@JsonPropertyOrder(alphabetic = true)
    static class AlphaWithIndexBean {
        @JsonProperty(index = 2)
        public int c;
        @JsonProperty(index = 0)
        public int a;
        public int b;
    }
static class BeanWithCreator {
        public int a;
        public int b;
        public int c;

        @JsonCreator
        BeanWithCreator(@JsonProperty("c") int c, @JsonProperty("a") int a) {
            this.a = a;
            this.c = c;
        }
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
@JsonPropertyOrder({ "f" })
    static class OrderingByIndexBean {
        public int r;
        public int a;
        @JsonProperty(index = 1) public int b;
        @JsonProperty(index = 0) public int u;
        public int f;
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
@JsonAppend(attrs={ @JsonAppend.Attr("id"),
            @JsonAppend.Attr(value="internal", propName="extra", required=true) })
    static class VPropSimpleBean {
        public int value = 13;
    }
enum VPropABC { A, B, C }
@JsonAppend(attrs=@JsonAppend.Attr(value="desc", include=JsonInclude.Include.NON_EMPTY))
    static class OptionalsBean {
        public int value = 28;
    }
static class FieldBean {
        public String p1 = "public";
        protected String p2 = "protected";
        @SuppressWarnings("unused")
        private String p3 = "private";
    }

    void __invoke_testGlobalAlphabeticWithIndexDisabledVpack() throws Exception {
        try {
            testGlobalAlphabeticWithIndexDisabledVpack();
        } finally {
        }
    }


    void __invoke_testImplicitOrderByCreatorVpack() throws Exception {
        try {
            testImplicitOrderByCreatorVpack();
        } finally {
        }
    }


    void __invoke_testOrderByIndexEtcVpack() throws Exception {
        try {
            testOrderByIndexEtcVpack();
        } finally {
        }
    }


    void __invoke_testOrderWithFeatureVpack() throws Exception {
        try {
            testOrderWithFeatureVpack();
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


    void __invoke_testStrictAlphaAndCreatorOrderingVpack() throws Exception {
        try {
            testStrictAlphaAndCreatorOrderingVpack();
        } finally {
        }
    }

}
