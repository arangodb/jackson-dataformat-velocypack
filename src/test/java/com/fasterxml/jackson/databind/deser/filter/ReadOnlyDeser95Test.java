package com.fasterxml.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Failing test related to [databind#95]
 */
public class ReadOnlyDeser95Test extends BaseMapTest
{
    @JsonIgnoreProperties(value={ "computed" }, allowGetters=true)
    static class ReadOnlyBean
    {
        public int value = 3;
        
        public int getComputed() { return 32; }
    }
    
    public void testReadOnlyProp() throws Exception
    {
        ObjectMapper m = new TestVelocypackMapper();
        String json = com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(new ReadOnlyBean()));
        if (json.indexOf("computed") < 0) {
            fail("Should have property 'computed', didn't: "+json);
        }
        ReadOnlyBean bean = m.readValue(json, ReadOnlyBean.class);
        assertNotNull(bean);
    }
}
