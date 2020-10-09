package com.fasterxml.jackson.databind.seq;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

@SuppressWarnings("resource")
public class ReadValuesTest extends BaseMapTest
{
    static class Bean {
        public int a;

        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != getClass()) return false;
            Bean other = (Bean) o;
            return other.a == this.a;
        }
        @Override public int hashCode() { return a; }
    }

    /*
    /**********************************************************
    /* Unit tests; root-level value sequences via Mapper
    /**********************************************************
     */

    private enum Source {
        STRING,
        INPUT_STREAM,
        READER,
        BYTE_ARRAY,
        BYTE_ARRAY_OFFSET
        ;
    }
    
    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    private <T> MappingIterator<T> _iterator(ObjectReader r,
            String json,
            Source srcType) throws IOException
    {
        switch (srcType) {
        case BYTE_ARRAY:
            return r.readValues(json.getBytes("UTF-8"));
        case BYTE_ARRAY_OFFSET:
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(json.getBytes("UTF-8"));
                out.write(0);
                out.write(0);
                out.write(0);
                byte[] b = out.toByteArray();
                return r.readValues(b, 3, b.length-6);
            }
        case INPUT_STREAM:
            return r.readValues(new ByteArrayInputStream(json.getBytes("UTF-8")));
        case READER:
            return r.readValues(new StringReader(json));
        case STRING:
        default:
            return r.readValues(com.fasterxml.jackson.VPackUtils.toBytes(json));
        }
    }

    /*
    /**********************************************************
    /* Unit tests; root-level value sequences via JsonParser
    /**********************************************************
     */

    public void testRootBeansWithParser() throws Exception
    {
        final String JSON = "{\"a\":3}";
        JsonParser jp = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        
        Iterator<Bean> it = jp.readValuesAs(Bean.class);

        assertTrue(it.hasNext());
        Bean b = it.next();
        assertEquals(3, b.a);
        assertFalse(it.hasNext());
    }

    public void testHasNextWithEndArray() throws Exception {
        final String JSON = "[1,3]";
        JsonParser jp = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toBytes(JSON));

        // NOTE: We must point JsonParser to the first element; if we tried to
        // use "managed" accessor, it would try to advance past START_ARRAY.
        assertToken(JsonToken.START_ARRAY, jp.nextToken());
        jp.nextToken();
        
        Iterator<Integer> it = MAPPER.readerFor(Integer.class).readValues(jp);
        assertTrue(it.hasNext());
        int value = it.next();
        assertEquals(1, value);
        assertTrue(it.hasNext());
        value = it.next();
        assertEquals(3, value);
        assertFalse(it.hasNext());
    }

    /*
    /**********************************************************
    /* Unit tests; non-root arrays
    /**********************************************************
     */

    public void testNonRootBeans() throws Exception
    {
        final String JSON = "{\"leaf\":[{\"a\":3},{\"a\":27}]}";
        JsonParser jp = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        assertToken(JsonToken.START_OBJECT, jp.nextToken());
        assertToken(JsonToken.FIELD_NAME, jp.nextToken());
        assertToken(JsonToken.START_ARRAY, jp.nextToken());
        // can either advance to first START_OBJECT, or clear current token;
        // explicitly passed JsonParser MUST point to the first token of
        // the first element
        assertToken(JsonToken.START_OBJECT, jp.nextToken());
        jp.close();
    }

    public void testNonRootArraysUsingParser() throws Exception
    {
        final String JSON = "[[1],[3]]";
        JsonParser p = MAPPER.getFactory().createParser(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        assertToken(JsonToken.START_ARRAY, p.nextToken());
        
        // Important: as of 2.1, START_ARRAY can only be skipped if the
        // target type is NOT a Collection or array Java type.
        // So we have to explicitly skip it in this particular case.
        assertToken(JsonToken.START_ARRAY, p.nextToken());
    }

    public void testEmptyIterator() throws Exception
    {
        MappingIterator<Object> empty = MappingIterator.emptyIterator();

        assertFalse(empty.hasNext());
        assertFalse(empty.hasNextValue());

        empty.close();
    }
}
