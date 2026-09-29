package tools.jackson.dataformat.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.LongNode;
import tools.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tree construction, traversal, precision, and generic-tree metadata behavior. */
class VPackTreeTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    // Equivalent coverage for MapperViaParserTest#testTreeReadingOk and
    // ObjectReaderTest#testReadTreeVariants; the bytes remain independent literals.
    void readsIndependentObjectTreeWritesItBackAndTraversesTokens() throws Exception {
        // Literal indexed object: {"a": 1, "b": 2}; no encoder is involved in setup.
        JsonNode object = mapper.readTree(hex("14 0a 41 61 31 41 62 28 10 02"));
        assertTrue(object.isObject());
        assertEquals(1, object.get("a").intValue());
        assertEquals(16, object.get("b").intValue());

        assertEquals(List.of("a", "b"), new ArrayList<>(object.propertyNames()));
        assertEquals(object, mapper.readTree(mapper.writeValueAsBytes(object)));

        try (JsonParser parser = mapper.tokenStreamFactory().createParser(hex("02 03 31"))) {
            assertEquals(JsonToken.START_ARRAY, parser.nextToken());
            assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
            assertEquals(1, parser.getIntValue());
            assertEquals(JsonToken.END_ARRAY, parser.nextToken());
            assertEquals(null, parser.nextToken());
        }
    }

    @Test
    // Equivalent coverage for JsonNodeConversionsTest#testTreeToValue and its null case.
    void treeToValueUsesNormalDatabindForPojoTargets() throws Exception {
        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(new TreePojo("tree", 11)));

        assertEquals(new TreePojo("tree", 11), mapper.treeToValue(tree, TreePojo.class));
        assertEquals(null, mapper.treeToValue(null, TreePojo.class));
    }

    @Test
    void treeNodesKeepExactIntegersAndConfiguredDecimalScale() throws Exception {
        JsonNode maxUnsigned = mapper.readTree(hex(
                "2f ff ff ff ff ff ff ff ff"));
        assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE),
                maxUnsigned.bigIntegerValue());
        assertTrue(maxUnsigned.isBigInteger());

        JsonNode defaultDecimal = mapper.readTree(bcd(false, -1, 0x12, 0x34, 0x50));
        assertInstanceOf(DecimalNode.class, defaultDecimal);
        assertEquals(new BigDecimal("12345.0"), defaultDecimal.decimalValue());

        VPackMapper decimalMapper = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();
        JsonNode exactDecimal = decimalMapper.readTree(bcd(false, -1, 0x12, 0x34, 0x50));
        assertInstanceOf(DecimalNode.class, exactDecimal);
        assertEquals(new BigDecimal("12345.0"), exactDecimal.decimalValue());
        assertEquals(1, exactDecimal.decimalValue().scale());

        VPackMapper normalized = VPackMapper.builder()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
                .build();
        assertEquals(new BigDecimal("12345"),
                normalized.readTree(bcd(false, -1, 0x12, 0x34, 0x50)).decimalValue());
    }

    @Test
    void genericTreeDoesNotClaimNativeDateMetadata() throws Exception {
        byte[] date = hex("1c 15 cd 5b 07 00 00 00 00");
        JsonNode tree = mapper.readTree(date);

        assertEquals(123456789L, tree.longValue());
        assertInstanceOf(LongNode.class, tree);
        try (VPackParser parser = (VPackParser) mapper.tokenStreamFactory()
                .createParser(mapper.writeValueAsBytes(tree))) {
            parser.nextToken();
            assertNotEquals(VPackType.DATE, parser.currentVPackType());
        }
    }

    @Test
    void treeWriteContextCanOmitNullProperties() throws Exception {
        ObjectNode input = mapper.valueToTree(Map.of("present", 1));
        input.set("missing", mapper.createObjectNode().nullNode());
        assertNotNull(input.get("missing"));

        VPackMapper omitNulls = VPackMapper.builder()
                .disable(JsonNodeFeature.WRITE_NULL_PROPERTIES)
                .build();
        JsonNode output = omitNulls.readTree(omitNulls.writeValueAsBytes(input));
        assertEquals(1, output.get("present").intValue());
        assertTrue(output.get("missing") == null);
    }

    private static byte[] bcd(boolean negative, int exponent, int... mantissa) {
        byte[] result = new byte[1 + 1 + 4 + mantissa.length];
        result[0] = (byte) ((negative ? 0xD0 : 0xC8));
        result[1] = (byte) mantissa.length;
        result[2] = (byte) exponent;
        result[3] = (byte) (exponent >>> 8);
        result[4] = (byte) (exponent >>> 16);
        result[5] = (byte) (exponent >>> 24);
        for (int i = 0; i < mantissa.length; ++i) result[6 + i] = (byte) mantissa[i];
        return result;
    }

    private static byte[] hex(String value) {
        String compact = value.replace(" ", "");
        byte[] result = new byte[compact.length() / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) Integer.parseInt(compact.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    record TreePojo(String name, int count) { }
}
