package com.fasterxml.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.*;

public class UnwrappedWithView1559Test extends BaseMapTest
{
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static final class Health {
        @JsonUnwrapped(prefix="xxx.")
        public Status status;
    }

    // NOTE: `final` is required to trigger [databind#1559]
    static final class Status {
        public String code;
    }

    /*
    /**********************************************************
    /* Tests methods
    /**********************************************************
     */

    // for [databind#1559]
    public void testCanSerializeSimpleWithDefaultView() throws Exception {
        String json = com.fasterxml.jackson.VPackUtils.toJson(jsonMapperBuilder().configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
                .build()
                .writeValueAsBytes(new Health()));
        assertEquals(aposToQuotes("{}"), json);
        // and just in case this, although won't matter wrt output
        json = com.fasterxml.jackson.VPackUtils.toJson(jsonMapperBuilder().configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true)
                .build()
                .writeValueAsBytes(new Health()));
        assertEquals(aposToQuotes("{}"), json);
    }
}
