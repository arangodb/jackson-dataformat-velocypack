package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression tests for BigDecimal/BigInteger precision round-trips via VPack BCD encoding.
 */
public class BigDecimalPrecisionTest extends BaseTestForVPack
{
    private final VPackMapper mapper = new VPackMapper();

    // =========================================================
    // Round-trip BigDecimal values
    // =========================================================

    @Test
    public void testRoundTrip_3_14159() throws Exception {
        BigDecimal input = new BigDecimal("3.14159");
        byte[] vpack = mapper.writeValueAsBytes(input);
        BigDecimal output = mapper.readValue(vpack, BigDecimal.class);
        assertThat(output).isEqualByComparingTo(input);
    }

    @Test
    public void testRoundTrip_9999999999999999_99() throws Exception {
        BigDecimal input = new BigDecimal("9999999999999999.99");
        byte[] vpack = mapper.writeValueAsBytes(input);
        BigDecimal output = mapper.readValue(vpack, BigDecimal.class);
        assertThat(output).isEqualByComparingTo(input);
    }

    @Test
    public void testRoundTrip_negative_10000000000_0000000001() throws Exception {
        BigDecimal input = new BigDecimal("-10000000000.0000000001");
        byte[] vpack = mapper.writeValueAsBytes(input);
        BigDecimal output = mapper.readValue(vpack, BigDecimal.class);
        assertThat(output).isEqualByComparingTo(input);
    }

    @Test
    public void testRoundTrip_doubleMinValue() throws Exception {
        BigDecimal input = new BigDecimal(Double.MIN_VALUE);
        byte[] vpack = mapper.writeValueAsBytes(input);
        BigDecimal output = mapper.readValue(vpack, BigDecimal.class);
        assertThat(output).isEqualByComparingTo(input);
    }

    @Test
    public void testRoundTrip_largeInteger_1234567890123456789() throws Exception {
        BigDecimal input = new BigDecimal("1234567890123456789");
        byte[] vpack = mapper.writeValueAsBytes(input);
        BigDecimal output = mapper.readValue(vpack, BigDecimal.class);
        assertThat(output).isEqualByComparingTo(input);
    }

    // =========================================================
    // BigInteger round-trip via JsonNode.numberValue()
    // =========================================================

    @Test
    public void testBigIntegerRoundTripViaJsonNode() throws Exception {
        BigInteger input = new BigInteger("1234567890123456789012345678901234567890");
        byte[] vpack = mapper.writeValueAsBytes(input);
        JsonNode node = mapper.readTree(vpack);
        assertThat(node.isBigInteger()).isTrue();
        Number value = node.numberValue();
        assertThat(value).isInstanceOf(BigInteger.class);
        assertThat(value).isEqualTo(input);
    }

    @Test
    public void testBigIntegerViaParser() throws Exception {
        BigInteger input = new BigInteger("1234567890123456789012345678901234567890");
        byte[] vpack = mapper.writeValueAsBytes(input);
        try (JsonParser p = mapper.createParser(vpack)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_INT);
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_INTEGER);
            assertThat(p.getBigIntegerValue()).isEqualTo(input);
        }
    }

    // =========================================================
    // Fractional BigDecimal stays as BIG_DECIMAL (not BIG_INTEGER)
    // =========================================================

    @Test
    public void testFractionalBigDecimalStaysAsBigDecimal() throws Exception {
        BigDecimal input = new BigDecimal("1234567890123456789.0");
        byte[] vpack = mapper.writeValueAsBytes(input);
        try (JsonParser p = mapper.createParser(vpack)) {
            assertThat(p.nextToken()).isEqualTo(JsonToken.VALUE_NUMBER_FLOAT);
            assertThat(p.getNumberType()).isEqualTo(JsonParser.NumberType.BIG_DECIMAL);
            assertThat(p.getDecimalValue()).isEqualByComparingTo(input);
        }
    }
}
