package com.fasterxml.jackson.databind.objectid;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class TestObjectIdWithInjectables538 extends BaseMapTest
{
    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    public static class A {
        public B b;

        public A(@JacksonInject("i1") String injected) {
        }
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    public static class B {
        public A a;

        @JsonCreator
        public B(@JacksonInject("i2") String injected) {
        }
    } 

    /*
    /*****************************************************
    /* Test methods
    /*****************************************************
     */
    
    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testWithInjectables538() throws Exception
    {
        A a = new A("a");
        B b = new B("b");
        a.b = b;
        b.a = a;

        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(a));

        InjectableValues.Std inject = new InjectableValues.Std();
        inject.addValue("i1", "e1");
        inject.addValue("i2", "e2");
        A output = null;

        try {
            output = MAPPER.reader(inject).forType(A.class).readValue(com.fasterxml.jackson.VPackUtils.toVPack(json));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize from JSON '"+json+"'", e);
        }
        assertNotNull(output);
        assertNotNull(output.b);
        assertSame(output, output.b.a);
    }
}

