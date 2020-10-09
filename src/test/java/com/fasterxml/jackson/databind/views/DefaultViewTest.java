package com.fasterxml.jackson.databind.views;

import java.io.IOException;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

// for [databind#507], supporting default views
public class DefaultViewTest extends BaseMapTest
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }

    @JsonView(ViewA.class)
    @JsonPropertyOrder({ "a", "b" })
    static class Defaulting {
        public int a = 3;

        @JsonView(ViewB.class)
        public int b = 5;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */    

    private final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testDeserialization() throws IOException
    {
        final String JSON = aposToQuotes("{'a':1,'b':2}");

        // first: no views:
        Defaulting result = MAPPER.readerFor(Defaulting.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        assertEquals(result.a, 1);
        assertEquals(result.b, 2);

        // Then views; first A, then B(B)
        result = MAPPER.readerFor(Defaulting.class)
                .withView(ViewA.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        assertEquals(result.a, 1);
        assertEquals(result.b, 5);

        result = MAPPER.readerFor(Defaulting.class)
                .withView(ViewBB.class)
                .readValue(com.fasterxml.jackson.VPackUtils.toBytes(JSON));
        assertEquals(result.a, 3);
        assertEquals(result.b, 2);
    }

    public void testSerialization() throws IOException
    {
        assertEquals(aposToQuotes("{'a':3,'b':5}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new Defaulting())));

        assertEquals(aposToQuotes("{'a':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writerWithView(ViewA.class)
                    .writeValueAsBytes(new Defaulting())));
        assertEquals(aposToQuotes("{'b':5}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writerWithView(ViewB.class)
                    .writeValueAsBytes(new Defaulting())));
    }
}
