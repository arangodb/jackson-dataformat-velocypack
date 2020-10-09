package com.fasterxml.jackson.databind.ser.filter;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class JsonIncludeArrayTest extends BaseMapTest
{
    static class NonEmptyByteArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public byte[] value;

        public NonEmptyByteArray(byte... v) { value = v; }
    }

    static class NonEmptyShortArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public short[] value;

        public NonEmptyShortArray(short... v) { value = v; }
    }

    static class NonEmptyCharArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public char[] value;

        public NonEmptyCharArray(char... v) { value = v; }
    }
    
    static class NonEmptyIntArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public int[] value;

        public NonEmptyIntArray(int... v) { value = v; }
    }

    static class NonEmptyLongArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public long[] value;

        public NonEmptyLongArray(long... v) { value = v; }
    }

    static class NonEmptyBooleanArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public boolean[] value;

        public NonEmptyBooleanArray(boolean... v) { value = v; }
    }

    static class NonEmptyDoubleArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public double[] value;

        public NonEmptyDoubleArray(double... v) { value = v; }
    }

    static class NonEmptyFloatArray {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public float[] value;

        public NonEmptyFloatArray(float... v) { value = v; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    final private ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testByteArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyByteArray())));
    }

    public void testShortArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyShortArray())));
        assertEquals("{\"value\":[1]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyShortArray((short) 1))));
    }

    public void testCharArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyCharArray())));
        // by default considered to be serialized as String
        assertEquals("{\"value\":\"ab\"}", com.fasterxml.jackson.VPackUtils.toJson(MAPPER.writeValueAsBytes(new NonEmptyCharArray('a', 'b'))));
        // but can force as sparse (real) array too
        assertEquals("{\"value\":[\"a\",\"b\"]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER
                .writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                .writeValueAsBytes(new NonEmptyCharArray('a', 'b'))));
    }

    public void testIntArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyIntArray())));
        assertEquals("{\"value\":[2]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyIntArray(2))));
    }

    public void testLongArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyLongArray())));
        assertEquals("{\"value\":[3,4]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyLongArray(3, 4))));
    }

    public void testBooleanArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyBooleanArray())));
        assertEquals("{\"value\":[true,false]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyBooleanArray(true,false))));
    }

    public void testDoubleArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyDoubleArray())));
        assertEquals("{\"value\":[0.25,-1.0]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyDoubleArray(0.25,-1.0))));
    }

    public void testFloatArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyFloatArray())));
        assertEquals("{\"value\":[0.5]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyFloatArray(0.5f))));
    }
}
