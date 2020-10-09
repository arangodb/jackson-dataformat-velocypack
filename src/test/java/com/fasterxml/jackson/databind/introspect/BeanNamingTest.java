package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

// Tests for [databind#653]
public class BeanNamingTest extends BaseMapTest
{
    static class URLBean {
        public String getURL() {
            return "http://foo";
        }
    }

    static class ABean {
        public int getA() {
            return 3;
        }
    }
    
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        assertFalse(mapper.isEnabled(MapperFeature.USE_STD_BEAN_NAMING));
        assertEquals(aposToQuotes("{'url':'http://foo'}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new URLBean())));
        assertEquals(aposToQuotes("{'a':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new ABean())));

        mapper = jsonMapperBuilder()
                .enable(MapperFeature.USE_STD_BEAN_NAMING)
                .build();
        assertEquals(aposToQuotes("{'URL':'http://foo'}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new URLBean())));
        assertEquals(aposToQuotes("{'a':3}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new ABean())));
    }
}
