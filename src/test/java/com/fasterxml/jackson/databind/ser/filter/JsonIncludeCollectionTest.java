package com.fasterxml.jackson.databind.ser.filter;

import java.util.Arrays;
import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class JsonIncludeCollectionTest extends BaseMapTest
{
    static class NonEmptyEnumSet {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public EnumSet<ABC> v;

        public NonEmptyEnumSet(ABC...values) {
            if (values.length == 0) {
                v = EnumSet.noneOf(ABC.class);
            } else {
                v = EnumSet.copyOf(Arrays.asList(values));
            }
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    final private ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testEnumSet() throws Exception
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyEnumSet())));
        assertEquals("{\"v\":[\"B\"]}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new NonEmptyEnumSet(ABC.B))));
    }
}
