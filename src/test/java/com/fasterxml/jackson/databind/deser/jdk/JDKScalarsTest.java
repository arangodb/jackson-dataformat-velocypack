package com.fasterxml.jackson.databind.deser.jdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;

import static com.fasterxml.jackson.TestUtils.isAtLeastVersion;

/**
 * Unit tests for verifying handling of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class JDKScalarsTest
    extends BaseMapTest
{
    final static String NAN_STRING = "NaN";

    final static class BooleanBean {
        boolean _v;
        void setV(boolean v) { _v = v; }
    }

    static class BooleanWrapper {
        public Boolean wrapper;
        public boolean primitive;
        
        protected Boolean ctor;
        
        @JsonCreator
        public BooleanWrapper(@JsonProperty("ctor") Boolean foo) {
            ctor = foo;
        }
    }

    static class IntBean {
        int _v;
        void setV(int v) { _v = v; }
    }

    static class LongBean {
        long _v;
        void setV(long v) { _v = v; }
    }
    
    final static class DoubleBean {
        double _v;
        void setV(double v) { _v = v; }
    }

    final static class FloatBean {
        float _v;
        void setV(float v) { _v = v; }
    }
    
    final static class CharacterBean {
        char _v;
        void setV(char v) { _v = v; }
        char getV() { return _v; }
    }
    
    final static class CharacterWrapperBean {
        Character _v;
        void setV(Character v) { _v = v; }
        Character getV() { return _v; }
    }

    /**
     * Also, let's ensure that it's ok to override methods.
     */
    static class IntBean2
        extends IntBean
    {
        @Override
        void setV(int v2) { super.setV(v2+1); }
    }

    static class PrimitivesBean
    {
        public boolean booleanValue = true;
        public byte byteValue = 3;
        public char charValue = 'a';
        public short shortValue = 37;
        public int intValue = 1;
        public long longValue = 100L;
        public float floatValue = 0.25f;
        public double doubleValue = -1.0;
    }

    static class WrappersBean
    {
        public Boolean booleanValue;
        public Byte byteValue;
        public Character charValue;
        public Short shortValue;
        public Integer intValue;
        public Long longValue;
        public Float floatValue;
        public Double doubleValue;
    }

    // [databind#2101]
    static class PrimitiveCreatorBean
    {
        @JsonCreator
        public PrimitiveCreatorBean(@JsonProperty(value="a",required=true) int a,
                @JsonProperty(value="b",required=true) int b) { }
    }

    // [databind#2197]
    static class VoidBean {
        public Void value;
    }

    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    /*
    /**********************************************************
    /* Scalar tests for boolean
    /**********************************************************
     */

    public void testBooleanPrimitive() throws Exception
    {
        // first, simple case:
        BooleanBean result = MAPPER.readValue("{\"v\":true}", BooleanBean.class);
        assertTrue(result._v);
        result = MAPPER.readValue("{\"v\":null}", BooleanBean.class);
        assertNotNull(result);
        assertFalse(result._v);
        result = MAPPER.readValue("{\"v\":1}", BooleanBean.class);
        assertNotNull(result);
        assertTrue(result._v);

        // should work with arrays too..
        boolean[] array = MAPPER.readValue("[ null, false ]", boolean[].class);
        assertNotNull(array);
        assertEquals(2, array.length);
        assertFalse(array[0]);
        assertFalse(array[1]);
    }

    /**
     * Simple unit test to verify that we can map boolean values to
     * java.lang.Boolean.
     */
    public void testBooleanWrapper() throws Exception
    {
        Boolean result = MAPPER.readValue("true", Boolean.class);
        assertEquals(Boolean.TRUE, result);
        result = MAPPER.readValue("false", Boolean.class);
        assertEquals(Boolean.FALSE, result);

        // should accept ints too, (0 == false, otherwise true)
        result = MAPPER.readValue("0", Boolean.class);
        assertEquals(Boolean.FALSE, result);
        result = MAPPER.readValue("1", Boolean.class);
        assertEquals(Boolean.TRUE, result);
    }

    // Test for verifying that Long values are coerced to boolean correctly as well
    public void testLongToBoolean() throws Exception
    {
        long value = 1L + Integer.MAX_VALUE;
        BooleanWrapper b = MAPPER.readValue("{\"primitive\" : "+value+", \"wrapper\":"+value+", \"ctor\":"+value+"}",
                BooleanWrapper.class);
        assertEquals(Boolean.TRUE, b.wrapper);
        assertTrue(b.primitive);
        assertEquals(Boolean.TRUE, b.ctor);

        // but ensure we can also get `false`
        b = MAPPER.readValue("{\"primitive\" : 0 , \"wrapper\":0, \"ctor\":0}",
                BooleanWrapper.class);
        assertEquals(Boolean.FALSE, b.wrapper);
        assertFalse(b.primitive);
        assertEquals(Boolean.FALSE, b.ctor);

        boolean[] boo = MAPPER.readValue("[ 0, 15, \"\", \"false\", \"True\" ]",
                boolean[].class);
        assertEquals(5, boo.length);
        assertFalse(boo[0]);
        assertTrue(boo[1]);
        assertFalse(boo[2]);
        assertFalse(boo[3]);
        assertTrue(boo[4]);
    }

    /*
    /**********************************************************
    /* Scalar tests for integral types
    /**********************************************************
     */

    public void testByteWrapper() throws Exception
    {
        Byte result = MAPPER.readValue("   -42\t", Byte.class);
        assertEquals(Byte.valueOf((byte)-42), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-12\"", Byte.class);
        assertEquals(Byte.valueOf((byte)-12), result);

        result = MAPPER.readValue(" 39.07", Byte.class);
        assertEquals(Byte.valueOf((byte)39), result);
    }

    public void testShortWrapper() throws Exception
    {
        Short result = MAPPER.readValue("37", Short.class);
        assertEquals(Short.valueOf((short)37), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-1009\"", Short.class);
        assertEquals(Short.valueOf((short)-1009), result);

        result = MAPPER.readValue("-12.9", Short.class);
        assertEquals(Short.valueOf((short)-12), result);
    }

    public void testCharacterWrapper() throws Exception
    {
        // First: canonical value is 1-char string
        Character result = MAPPER.readValue("\"a\"", Character.class);
        assertEquals(Character.valueOf('a'), result);

        // But can also pass in ascii code
        result = MAPPER.readValue(" "+((int) 'X'), Character.class);
        assertEquals(Character.valueOf('X'), result);
        
        final CharacterWrapperBean wrapper = MAPPER.readValue("{\"v\":null}", CharacterWrapperBean.class);
        assertNotNull(wrapper);
        assertNull(wrapper.getV());

        try {
            MAPPER.readerFor(CharacterBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"v\":null}"));
            fail("Attempting to deserialize a 'null' JSON reference into a 'char' property did not throw an exception");
        } catch (MismatchedInputException e) {
            verifyException(e, "cannot map `null`");
        }
        final CharacterBean charBean = MAPPER.readerFor(CharacterBean.class)
                .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"v\":null}"));
        assertNotNull(wrapper);
        assertEquals('\u0000', charBean.getV());
    }

    public void testIntWrapper() throws Exception
    {
        Integer result = MAPPER.readValue("   -42\t", Integer.class);
        assertEquals(Integer.valueOf(-42), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-1200\"", Integer.class);
        assertEquals(Integer.valueOf(-1200), result);

        result = MAPPER.readValue(" 39.07", Integer.class);
        assertEquals(Integer.valueOf(39), result);
    }

    public void testIntPrimitive() throws Exception
    {
        // first, simple case:
        IntBean result = MAPPER.readValue("{\"v\":3}", IntBean.class);
        assertEquals(3, result._v);

        result = MAPPER.readValue("{\"v\":null}", IntBean.class);
        assertNotNull(result);
        assertEquals(0, result._v);

        // should work with arrays too..
        int[] array = MAPPER.readValue("[ null ]", int[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0, array[0]);
        
        // [databind#381]
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        try {
            mapper.readValue("{\"v\":[3]}", IntBean.class);
            fail("Did not throw exception when reading a value from a single value array with the UNWRAP_SINGLE_VALUE_ARRAYS feature disabled");
        } catch (MismatchedInputException exp) {
            //Correctly threw exception
        }
        
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        
        result = mapper.readValue("{\"v\":[3]}", IntBean.class);
        assertEquals(3, result._v);
        
        result = mapper.readValue("[{\"v\":[3]}]", IntBean.class);
        assertEquals(3, result._v);
        
        try {
            mapper.readValue("[{\"v\":[3,3]}]", IntBean.class);
            fail("Did not throw exception while reading a value from a multi value array with UNWRAP_SINGLE_VALUE_ARRAY feature enabled");
        } catch (MismatchedInputException exp) {
            //threw exception as required
        }
        
        result = mapper.readValue("{\"v\":[null]}", IntBean.class);
        assertNotNull(result);
        assertEquals(0, result._v);

        array = mapper.readValue("[ [ null ] ]", int[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0, array[0]);
    }

    public void testLongWrapper() throws Exception
    {
        Long result = MAPPER.readValue("12345678901", Long.class);
        assertEquals(Long.valueOf(12345678901L), result);

        // Also: should be able to coerce floats, strings:
        result = MAPPER.readValue(" \"-9876\"", Long.class);
        assertEquals(Long.valueOf(-9876), result);

        result = MAPPER.readValue("1918.3", Long.class);
        assertEquals(Long.valueOf(1918), result);
    }

    public void testLongPrimitive() throws Exception
    {
        // first, simple case:
        LongBean result = MAPPER.readValue("{\"v\":3}", LongBean.class);
        assertEquals(3, result._v);
        result = MAPPER.readValue("{\"v\":null}", LongBean.class);
        assertNotNull(result);
        assertEquals(0, result._v);

        // should work with arrays too..
        long[] array = MAPPER.readValue("[ null ]", long[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0, array[0]);
        
        // [databind#381]
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        try {
            mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"v\":[3]}"), LongBean.class);
            fail("Did not throw exception when reading a value from a single value array with the UNWRAP_SINGLE_VALUE_ARRAYS feature disabled");
        } catch (MismatchedInputException exp) {
            //Correctly threw exception
        }
        
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        
        result = mapper.readValue("{\"v\":[3]}", LongBean.class);
        assertEquals(3, result._v);
        
        result = mapper.readValue("[{\"v\":[3]}]", LongBean.class);
        assertEquals(3, result._v);
        
        try {
            mapper.readValue("[{\"v\":[3,3]}]", LongBean.class);
            fail("Did not throw exception while reading a value from a multi value array with UNWRAP_SINGLE_VALUE_ARRAY feature enabled");
        } catch (MismatchedInputException exp) {
            //threw exception as required
        }
        
        result = mapper.readValue("{\"v\":[null]}", LongBean.class);
        assertNotNull(result);
        assertEquals(0, result._v);

        array = mapper.readValue("[ [ null ] ]", long[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0, array[0]);
    }

    /**
     * Beyond simple case, let's also ensure that method overriding works as
     * expected.
     */
    public void testIntWithOverride() throws Exception
    {
        IntBean2 result = MAPPER.readValue("{\"v\":8}", IntBean2.class);
        assertEquals(9, result._v);
    }

    /*
    /**********************************************************
    /* Scalar tests for floating point types
    /**********************************************************
     */

    public void testDoublePrimitive() throws Exception
    {
        // first, simple case:
        // bit tricky with binary fps but...
        final double value = 0.016;
        DoubleBean result = MAPPER.readValue("{\"v\":"+value+"}", DoubleBean.class);
        assertEquals(value, result._v);
        // then [JACKSON-79]:
        result = MAPPER.readValue("{\"v\":null}", DoubleBean.class);
        assertNotNull(result);
        assertEquals(0.0, result._v);

        // should work with arrays too..
        double[] array = MAPPER.readValue("[ null ]", double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0.0, array[0]);
    }

    /* Note: dealing with floating-point values is tricky; not sure if
     * we can really use equality tests here... JDK does have decent
     * conversions though, to retain accuracy and round-trippability.
     * But still...
     */
    public void testFloatWrapper() throws Exception
    {
        // Also: should be able to coerce floats, strings:
        String[] STRS = new String[] {
            "1.0", "0.0", "-0.3", "0.7", "42.012", "-999.0", NAN_STRING
        };

        for (String str : STRS) {
            Float exp = Float.valueOf(str);
            Float result;

            if (NAN_STRING != str) {
                // First, as regular floating point value
                result = MAPPER.readValue(str, Float.class);
                assertEquals(exp, result);
            }

            // and then as coerced String:
            result = MAPPER.readValue(" \""+str+"\"", Float.class);
            assertEquals(exp, result);
        }
    }

    public void testDoubleWrapper() throws Exception
    {
        // Also: should be able to coerce doubles, strings:
        String[] STRS = new String[] {
            "1.0", "0.0", "-0.3", "0.7", "42.012", "-999.0", NAN_STRING
        };

        for (String str : STRS) {
            Double exp = Double.valueOf(str);
            Double result;

            // First, as regular double value
            if (NAN_STRING != str) {
                result = MAPPER.readValue(str, Double.class);
               assertEquals(exp, result);
            }
            // and then as coerced String:
            result = MAPPER.readValue(" \""+str+"\"", Double.class);
            assertEquals(exp, result);
        }
    }

    public void testDoubleAsArray() throws Exception
    {
        final ObjectMapper mapper = new TestVelocypackMapper();
        mapper.disable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        final double value = 0.016;
        try {
            mapper.readValue("{\"v\":[" + value + "]}", DoubleBean.class);
            fail("Did not throw exception when reading a value from a single value array with the UNWRAP_SINGLE_VALUE_ARRAYS feature disabled");
        } catch (JsonMappingException exp) {
            //Correctly threw exception
        }
        
        mapper.enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
        
        DoubleBean result = mapper.readValue("{\"v\":[" + value + "]}",
                DoubleBean.class);
        assertEquals(value, result._v);
        
        result = mapper.readValue("[{\"v\":[" + value + "]}]", DoubleBean.class);
        assertEquals(value, result._v);
        
        try {
            mapper.readValue("[{\"v\":[" + value + "," + value + "]}]", DoubleBean.class);
            fail("Did not throw exception while reading a value from a multi value array with UNWRAP_SINGLE_VALUE_ARRAY feature enabled");
        } catch (MismatchedInputException exp) {
            //threw exception as required
        }
        
        result = mapper.readValue("{\"v\":[null]}", DoubleBean.class);
        assertNotNull(result);
        assertEquals(0d, result._v);

        double[] array = mapper.readValue("[ [ null ] ]", double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(0d, array[0]);
    }

    public void testDoublePrimitiveNonNumeric() throws Exception
    {
        // first, simple case:
        // bit tricky with binary fps but...
        double value = Double.POSITIVE_INFINITY;
        DoubleBean result = MAPPER.readValue("{\"v\":\""+value+"\"}", DoubleBean.class);
        assertEquals(value, result._v);
        
        // should work with arrays too..
        double[] array = MAPPER.readValue("[ \"Infinity\" ]", double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Double.POSITIVE_INFINITY, array[0]);
    }
    
    public void testFloatPrimitiveNonNumeric() throws Exception
    {
        // bit tricky with binary fps but...
        float value = Float.POSITIVE_INFINITY;
        FloatBean result = MAPPER.readValue("{\"v\":\""+value+"\"}", FloatBean.class);
        assertEquals(value, result._v);
        
        // should work with arrays too..
        float[] array = MAPPER.readValue("[ \"Infinity\" ]", float[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Float.POSITIVE_INFINITY, array[0]);
    }

    /*
    /**********************************************************
    /* Scalar tests, other
    /**********************************************************
     */

    public void testBase64Variants() throws Exception
    {
        final byte[] INPUT = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890X".getBytes("UTF-8");
        
        // default encoding is "MIME, no linefeeds", so:
        Assert.assertArrayEquals(INPUT, MAPPER.readValue(
                quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="),
                byte[].class));
        ObjectReader reader = MAPPER.readerFor(byte[].class);
        Assert.assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MIME_NO_LINEFEEDS).readValue(
                com.fasterxml.jackson.VPackUtils.toVPack(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="))
        ));

        // but others should be slightly different
        Assert.assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MIME).readValue(
                com.fasterxml.jackson.VPackUtils.toVPack(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1\\ndnd4eXoxMjM0NTY3ODkwWA=="))
        ));
        Assert.assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.MODIFIED_FOR_URL).readValue(
                com.fasterxml.jackson.VPackUtils.toVPack(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA"))
        ));
        // PEM mandates 64 char lines:
        Assert.assertArrayEquals(INPUT, (byte[]) reader.with(Base64Variants.PEM).readValue(
                com.fasterxml.jackson.VPackUtils.toVPack(quote("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamts\\nbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwWA=="))
        ));
    }    

    /*
    /**********************************************************
    /* Sequence tests
    /**********************************************************
     */

    /*
    /**********************************************************
    /* Empty String coercion, handling
    /**********************************************************
     */

    // by default, should return nulls, n'est pas?
    public void testEmptyStringForWrappers() throws IOException
    {
        WrappersBean bean;

        bean = MAPPER.readValue("{\"booleanValue\":\"\"}", WrappersBean.class);
        assertNull(bean.booleanValue);
        bean = MAPPER.readValue("{\"byteValue\":\"\"}", WrappersBean.class);
        assertNull(bean.byteValue);

        // char/Character is different... not sure if this should work or not:
        bean = MAPPER.readValue("{\"charValue\":\"\"}", WrappersBean.class);
        assertNull(bean.charValue);

        bean = MAPPER.readValue("{\"shortValue\":\"\"}", WrappersBean.class);
        assertNull(bean.shortValue);
        bean = MAPPER.readValue("{\"intValue\":\"\"}", WrappersBean.class);
        assertNull(bean.intValue);
        bean = MAPPER.readValue("{\"longValue\":\"\"}", WrappersBean.class);
        assertNull(bean.longValue);
        bean = MAPPER.readValue("{\"floatValue\":\"\"}", WrappersBean.class);
        assertNull(bean.floatValue);
        bean = MAPPER.readValue("{\"doubleValue\":\"\"}", WrappersBean.class);
        assertNull(bean.doubleValue);
    }

    public void testEmptyStringForPrimitives() throws IOException
    {
        PrimitivesBean bean;
        bean = MAPPER.readValue("{\"booleanValue\":\"\"}", PrimitivesBean.class);
        assertFalse(bean.booleanValue);
        bean = MAPPER.readValue("{\"byteValue\":\"\"}", PrimitivesBean.class);
        assertEquals((byte) 0, bean.byteValue);
        bean = MAPPER.readValue("{\"charValue\":\"\"}", PrimitivesBean.class);
        assertEquals((char) 0, bean.charValue);
        bean = MAPPER.readValue("{\"shortValue\":\"\"}", PrimitivesBean.class);
        assertEquals((short) 0, bean.shortValue);
        bean = MAPPER.readValue("{\"intValue\":\"\"}", PrimitivesBean.class);
        assertEquals(0, bean.intValue);
        bean = MAPPER.readValue("{\"longValue\":\"\"}", PrimitivesBean.class);
        assertEquals(0L, bean.longValue);
        bean = MAPPER.readValue("{\"floatValue\":\"\"}", PrimitivesBean.class);
        assertEquals(0.0f, bean.floatValue);
        bean = MAPPER.readValue("{\"doubleValue\":\"\"}", PrimitivesBean.class);
        assertEquals(0.0, bean.doubleValue);
    }

    /*
    /**********************************************************
    /* Null handling for scalars in POJO
    /**********************************************************
     */

    public void testNullForPrimitives() throws IOException
    {
        if(!isAtLeastVersion(2, 12)) return;

        // by default, ok to rely on defaults
        PrimitivesBean bean = MAPPER.readValue(
                "{\"intValue\":null, \"booleanValue\":null, \"doubleValue\":null}",
                PrimitivesBean.class);
        assertNotNull(bean);
        assertEquals(0, bean.intValue);
        assertEquals(false, bean.booleanValue);
        assertEquals(0.0, bean.doubleValue);

        bean = MAPPER.readValue("{\"byteValue\":null, \"longValue\":null, \"floatValue\":null}",
                PrimitivesBean.class);
        assertNotNull(bean);
        assertEquals((byte) 0, bean.byteValue);
        assertEquals(0L, bean.longValue);
        assertEquals(0.0f, bean.floatValue);

        // but not when enabled
        final ObjectReader reader = MAPPER
                .readerFor(PrimitivesBean.class)
                .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        // boolean
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"booleanValue\":null}"));
            fail("Expected failure for boolean + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `boolean`");
            verifyPath(e, "booleanValue");
        }
        // byte/char/short/int/long
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"byteValue\":null}"));
            fail("Expected failure for byte + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `byte`");
            verifyPath(e, "byteValue");
        }
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"charValue\":null}"));
            fail("Expected failure for char + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `char`");
            verifyPath(e, "charValue");
        }
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"shortValue\":null}"));
            fail("Expected failure for short + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `short`");
            verifyPath(e, "shortValue");
        }
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"intValue\":null}"));
            fail("Expected failure for int + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `int`");
            verifyPath(e, "intValue");
        }
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"longValue\":null}"));
            fail("Expected failure for long + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `long`");
            verifyPath(e, "longValue");
        }

        // float/double
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"floatValue\":null}"));
            fail("Expected failure for float + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `float`");
            verifyPath(e, "floatValue");
        }
        try {
            reader.readValue(com.fasterxml.jackson.VPackUtils.toVPack("{\"doubleValue\":null}"));
            fail("Expected failure for double + null");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `double`");
            verifyPath(e, "doubleValue");
        }
    }

    // [databind#2101]
    public void testNullForPrimitivesViaCreator() throws IOException
    {
        if(!isAtLeastVersion(2, 12)) return;

        try {
            /*PrimitiveCreatorBean bean =*/ MAPPER
                    .readerFor(PrimitiveCreatorBean.class)
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue(com.fasterxml.jackson.VPackUtils.toVPack(aposToQuotes("{'a': null}")));
            fail("Expected failure for `int` and `null`");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot map `null` into type `int`");
            verifyPath(e, "a");
        }
    }

    private void verifyPath(MismatchedInputException e, String propName) {
        final List<Reference> path = e.getPath();
        assertEquals(1, path.size());
        assertEquals(propName, path.get(0).getFieldName());
    }

    // [databind#2197], [databind#2679]
    public void testVoidDeser() throws Exception
    {
        // First, `Void` as bean property
        VoidBean bean = MAPPER.readValue(aposToQuotes("{'value' : 123 }"),
                VoidBean.class);
        assertNull(bean.value);

        // Then `Void` and `void` (Void.TYPE) as root values
        assertNull(MAPPER.readValue("{}", Void.class));
        assertNull(MAPPER.readValue("1234", Void.class));
        assertNull(MAPPER.readValue("[ 1, true ]", Void.class));

        assertNull(MAPPER.readValue("{}", Void.TYPE));
        assertNull(MAPPER.readValue("1234", Void.TYPE));
        assertNull(MAPPER.readValue("[ 1, true ]", Void.TYPE));
    }

    /*
    /**********************************************************
    /* Test for invalid String values
    /**********************************************************
     */

    public void testInvalidStringCoercionFail() throws IOException
    {
        if(!isAtLeastVersion(2, 12)) return;

        _testInvalidStringCoercionFail(boolean[].class, "boolean");
        _testInvalidStringCoercionFail(byte[].class, "byte[]");

        // char[] is special, cannot use generalized test here
//        _testInvalidStringCoercionFail(char[].class);
        _testInvalidStringCoercionFail(short[].class, "short");
        _testInvalidStringCoercionFail(int[].class, "int");
        _testInvalidStringCoercionFail(long[].class, "long");
        _testInvalidStringCoercionFail(float[].class, "float");
        _testInvalidStringCoercionFail(double[].class, "double");
    }

    private void _testInvalidStringCoercionFail(Class<?> cls, String clsSimpleName) throws IOException
    {
        final String JSON = "[ \"foobar\" ]";
        try {
            MAPPER.readerFor(cls).readValue(com.fasterxml.jackson.VPackUtils.toVPack(JSON));
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize value of type `"+clsSimpleName+"` from String \"foobar\"");
        }
    }
}
