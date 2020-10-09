package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class TestGenericFieldInSubtype extends BaseMapTest
{
    // [JACKSON-677]
    public void test677() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        // and bit more checking as per later comments
        JavaType t677 = mapper.constructType(Result677.Success677.class);
        assertNotNull(t677);
        Result677.Success677<Integer> s = new Result677.Success677<Integer>(Integer.valueOf(4));
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(s));
        assertEquals("{\"value\":4}", json);
    }

 // [JACKSON-887]
    public void testInnerType() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        BaseType.SubType<?> r = mapper.readValue(com.fasterxml.jackson.VPackUtils.toBytes("{}"), BaseType.SubType.class);
        assertNotNull(r);
    }

}

class Result677<T> {
    public static class Success677<K> extends Result677<K> {
     public K value;
     
     public Success677() { }
     public Success677(K k) { value = k; }
    }
}

abstract class BaseType<T> {
    public T value;

    public final static class SubType<T extends Number> extends BaseType<T>
    {
    }
}
