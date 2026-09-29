package tools.jackson.databind.node;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.DecimalNode;
import tools.jackson.databind.node.DoubleNode;
import tools.jackson.databind.node.FloatNode;
import tools.jackson.databind.node.JsonNodeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0496F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] INT_NEGATIVE_90184 = VPackWireFixtureTest.hex(
            "22 b8 9f fe");
private static final byte[] LONG_12345678_SHIFTED = VPackWireFixtureTest.hex(
            "2f 00 00 00 00 4e 61 bc 00");
private static final byte[] BCD_TEN = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 10");
private static final byte[] BCD_QUARTER = VPackWireFixtureTest.hex(
            "c8 01 fe ff ff ff 25");
private static final byte[] NAN = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f8 7f");
private static final byte[] POSITIVE_INFINITY = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 f0 7f");
private final VPackMapper mapper = new VPackMapper();

    // Provenance: NumberToIntegralConversionTest#testFloatToIntegrals().
    void testFloatToIntegralsVpack() throws Exception {
        JsonNodeFactory nodes = mapper.getNodeFactory();
        assertTrue(nodes.numberNode(0f).canConvertToExactIntegral());
        assertTrue(nodes.numberNode((float) 0).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(0.000f).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(0.001f).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(0.25f).canConvertToExactIntegral());
        assertEquals(FloatNode.class, nodes.numberNode(0.25f).getClass());
        assertEquals(0.25f, nodes.numberNode(0.25f).floatValue());
    }

    // Provenance: NumberToIntegralConversionTest#testDoubleToIntegrals().
    void testDoubleToIntegralsVpack() throws Exception {
        JsonNodeFactory nodes = mapper.getNodeFactory();
        assertTrue(nodes.numberNode(0d).canConvertToExactIntegral());
        assertTrue(nodes.numberNode((double) 0).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(0.000d).canConvertToExactIntegral());
        assertTrue(nodes.numberNode((double) Integer.MAX_VALUE).canConvertToExactIntegral());
        assertTrue(nodes.numberNode((double) Integer.MIN_VALUE).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(0.001d).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(0.25d).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(12000000.5d).canConvertToExactIntegral());
        assertEquals(DoubleNode.class, nodes.numberNode(0.25d).getClass());
    }

    // Provenance: NumberToIntegralConversionTest#testNaNsToIntegrals().
    void testNaNsToIntegralsVpack() throws Exception {
        JsonNodeFactory nodes = mapper.getNodeFactory();
        assertFalse(nodes.numberNode(Float.NaN).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(Float.NEGATIVE_INFINITY).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(Float.POSITIVE_INFINITY).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(Double.NaN).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(Double.NEGATIVE_INFINITY).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(Double.POSITIVE_INFINITY).canConvertToExactIntegral());
        assertTrue(mapper.readTree(NAN).isDouble());
        assertFalse(mapper.readTree(NAN).canConvertToExactIntegral());
        assertEquals(Double.POSITIVE_INFINITY,
                mapper.readTree(POSITIVE_INFINITY).doubleValue());
    }

    // Provenance: NumberToIntegralConversionTest#testBigDecimalToIntegrals().
    void testBigDecimalToIntegralsVpack() throws Exception {
        JsonNodeFactory nodes = mapper.getNodeFactory();
        assertTrue(nodes.numberNode(BigDecimal.ZERO).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(BigDecimal.TEN).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(BigDecimal.valueOf(Integer.MAX_VALUE)).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(BigDecimal.valueOf(Integer.MIN_VALUE)).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(BigDecimal.valueOf(Long.MAX_VALUE)).canConvertToExactIntegral());
        assertTrue(nodes.numberNode(BigDecimal.valueOf(Long.MIN_VALUE)).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(BigDecimal.valueOf(0.001)).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(BigDecimal.valueOf(0.25)).canConvertToExactIntegral());
        assertFalse(nodes.numberNode(BigDecimal.valueOf(12000000.5)).canConvertToExactIntegral());

        JsonNode exact = mapper.readTree(BCD_TEN);
        assertTrue(exact.canConvertToExactIntegral());
        assertEquals(BigDecimal.TEN, exact.decimalValue());
        JsonNode fractional = mapper.readTree(BCD_QUARTER);
        assertEquals(DecimalNode.class, fractional.getClass());
        assertFalse(fractional.canConvertToExactIntegral());
    }
@JsonDeserialize(as = DataImpl.class)
    public interface Data {
    }
public static class DataImpl implements Data {
        private final JsonNode root;

        @JsonCreator
        public DataImpl(JsonNode root) {
            this.root = root;
        }

        @JsonValue
        public JsonNode value() {
            return root;
        }
    }

    void __invoke_testFloatToIntegralsVpack() throws Exception {
        try {
            testFloatToIntegralsVpack();
        } finally {
        }
    }


    void __invoke_testDoubleToIntegralsVpack() throws Exception {
        try {
            testDoubleToIntegralsVpack();
        } finally {
        }
    }


    void __invoke_testNaNsToIntegralsVpack() throws Exception {
        try {
            testNaNsToIntegralsVpack();
        } finally {
        }
    }


    void __invoke_testBigDecimalToIntegralsVpack() throws Exception {
        try {
            testBigDecimalToIntegralsVpack();
        } finally {
        }
    }

}
