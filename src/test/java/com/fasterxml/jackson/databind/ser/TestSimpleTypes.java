package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import java.text.DecimalFormat;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit tests for verifying serialization of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class TestSimpleTypes
    extends BaseMapTest
{
    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    
    public void testBoolean() throws Exception
    {
        assertEquals("true", serializeAsString(MAPPER, Boolean.TRUE));
        assertEquals("false", serializeAsString(MAPPER, Boolean.FALSE));
    }

    public void testBooleanArray() throws Exception
    {
        assertEquals("[true,false]", serializeAsString(MAPPER, new boolean[] { true, false} ));
        assertEquals("[true,false]", serializeAsString(MAPPER, new Boolean[] { Boolean.TRUE, Boolean.FALSE} ));
    }

    public void testByteArray() throws Exception
    {
        byte[] data = { 1, 17, -3, 127, -128 };
        Byte[] data2 = new Byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            data2[i] = data[i]; // auto-boxing
        }
        // For this we need to deserialize, to get base64 codec
        String str1 = serializeAsString(MAPPER, data);
        String str2 = serializeAsString(MAPPER, data2);
        assertArrayEquals(data, MAPPER.readValue(str1, byte[].class));
        assertArrayEquals(data2, MAPPER.readValue(str2, Byte[].class));
    }

    // as per [Issue#42], allow Base64 variant use as well
    public void testBase64Variants() throws Exception {
        final byte[] INPUT = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890X".getBytes("UTF-8");

        // default encoding is "MIME, no linefeeds", so:
        assertEquals(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="), com.fasterxml.jackson.VPackUtils.toJson(MAPPER.writeValueAsBytes(INPUT)));
        assertEquals(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writer(Base64Variants.MIME_NO_LINEFEEDS).writeValueAsBytes(INPUT)));

        // but others should be slightly different
        assertEquals("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1\\ndnd4eXoxMjM0NTY3ODkwWA==",
                MAPPER.readValue(MAPPER.writer(Base64Variants.MIME).writeValueAsBytes(INPUT), String.class));
        assertEquals(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA"), // no padding or LF
                com.fasterxml.jackson.VPackUtils.toJson(MAPPER.writer(Base64Variants.MODIFIED_FOR_URL).writeValueAsBytes(INPUT)));
        // PEM mandates 64 char lines:
        assertEquals("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamts\\nbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA==",
                MAPPER.readValue(MAPPER.writer(Base64Variants.PEM).writeValueAsBytes(INPUT), String.class)
        );
    }
    
    public void testShortArray() throws Exception
    {
        assertEquals("[0,1]", serializeAsString(MAPPER, new short[] { 0, 1 }));
        assertEquals("[2,3]", serializeAsString(MAPPER, new Short[] { 2, 3 }));
    }

    public void testIntArray() throws Exception
    {
        assertEquals("[0,-3]", serializeAsString(MAPPER, new int[] { 0, -3 }));
        assertEquals("[13,9]", serializeAsString(MAPPER, new Integer[] { 13, 9 }));
    }

    /* Note: dealing with floating-point values is tricky; not sure if
     * we can really use equality tests here... JDK does have decent
     * conversions though, to retain accuracy and round-trippability.
     * But still...
     */
    public void testFloat() throws Exception {
        double[] values = new double[]{
                0.0, 1.0, 0.1, -37.01, 999.99, 0.3, 33.3, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };
        for (double d : values) {
            float f = (float) d;
            String expected = String.valueOf(f);

            String actual = serializeAsString(MAPPER, roundTwoDecimals(f));
            assertEquals(expected, actual);
        }
    }

    double roundTwoDecimals(float f) {
        if (Float.isNaN(f) || Float.isInfinite(f))
            return f;
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(f));
    }

    public void testClass() throws Exception {
        String result = com.fasterxml.jackson.VPackUtils.toJson(MAPPER.writeValueAsBytes(java.util.List.class));
        assertEquals("\"java.util.List\"", result);
    }
}
