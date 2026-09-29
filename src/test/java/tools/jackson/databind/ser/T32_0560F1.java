package tools.jackson.databind.ser;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.Base64Variants;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.annotation.JsonAppend;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0560F1 {
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

    // Provenance: SimpleTypeSerializationTest#testAttributePropInclusion().
    void testAttributePropInclusionVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer();
        assertArrayEquals(ATTRIBUTE_DESC,
                writer.withAttribute("desc", "nice").writeValueAsBytes(new OptionalsBean()));
        assertArrayEquals(ATTRIBUTE_OMITTED,
                writer.writeValueAsBytes(new OptionalsBean()));
        assertArrayEquals(ATTRIBUTE_OMITTED,
                writer.withAttribute("desc", "").writeValueAsBytes(new OptionalsBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testAttributeProperties().
    void testAttributePropertiesVpack() throws Exception {
        java.util.LinkedHashMap<String, Object> stuff = new java.util.LinkedHashMap<>();
        stuff.put("x", 3);
        stuff.put("y", VPropABC.B);
        assertArrayEquals(ATTRIBUTES,
                MAPPER.writer().withAttribute("id", "abc123")
                        .withAttribute("internal", stuff)
                        .writeValueAsBytes(new VPropSimpleBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testBase64Variants().
    void testBase64VariantsVpack() throws Exception {
        byte[] input = ("abcdefghijklmnopqrstuvwxyz1234567890"
                + "abcdefghijklmnopqrstuvwxyz1234567890X").getBytes(StandardCharsets.UTF_8);
        assertArrayEquals(NATIVE_BINARY, MAPPER.writeValueAsBytes(input));
        assertArrayEquals(NATIVE_BINARY,
                MAPPER.writer().with(Base64Variants.MIME_NO_LINEFEEDS).writeValueAsBytes(input));
        assertArrayEquals(NATIVE_BINARY,
                MAPPER.writer().with(Base64Variants.MIME).writeValueAsBytes(input));
        assertArrayEquals(NATIVE_BINARY,
                MAPPER.writer().with(Base64Variants.MODIFIED_FOR_URL).writeValueAsBytes(input));
        assertArrayEquals(NATIVE_BINARY,
                MAPPER.writer().with(Base64Variants.PEM).writeValueAsBytes(input));
    }

    // Provenance: SimpleTypeSerializationTest#testBasicSetup().
    void testBasicSetupVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc.with(JsonAutoDetect.Visibility.ANY))
                .build();
        assertArrayEquals(BASIC_SETUP, mapper.writeValueAsBytes(new FieldBean()));
    }

    // Provenance: SimpleTypeSerializationTest#testBigIntArray().
    void testBigIntArrayVpack() throws Exception {
        assertArrayEquals(new int[] { 0, 1, 2, 3 },
                MAPPER.readValue(SMALL_INT_ARRAY, int[].class));

        final int size = 99999;
        int[] ints = new int[size];
        for (int i = 0; i < size; ++i) {
            ints[i] = i;
        }
        for (int round = 0; round < 3; ++round) {
            byte[] data = MAPPER.writeValueAsBytes(ints);
            try (JsonParser parser = MAPPER.createParser(data)) {
                assertEquals(JsonToken.START_ARRAY, parser.nextToken());
                for (int i = 0; i < size; ++i) {
                    assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
                    assertEquals(i, parser.getIntValue());
                }
                assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            }
        }
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

    void __invoke_testAttributePropInclusionVpack() throws Exception {
        try {
            testAttributePropInclusionVpack();
        } finally {
        }
    }


    void __invoke_testAttributePropertiesVpack() throws Exception {
        try {
            testAttributePropertiesVpack();
        } finally {
        }
    }


    void __invoke_testBase64VariantsVpack() throws Exception {
        try {
            testBase64VariantsVpack();
        } finally {
        }
    }


    void __invoke_testBasicSetupVpack() throws Exception {
        try {
            testBasicSetupVpack();
        } finally {
        }
    }


    void __invoke_testBigIntArrayVpack() throws Exception {
        try {
            testBigIntArrayVpack();
        } finally {
        }
    }

}
