package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class LocalTypeTest extends BaseMapTest
{
    // [databind#609]
    static class EntityContainer {
        RuleForm entity;
        
        @SuppressWarnings("unchecked")
        public <T extends RuleForm> T getEntity() { return (T) entity; }
        public <T extends RuleForm> void setEntity(T e) { entity = e; }
    }

    static class RuleForm {
        public int value;

        public RuleForm() { }
        public RuleForm(int v) { value = v; }
    }

    // [databind#609]
    public void testLocalPartialType609() throws Exception {
        ObjectMapper mapper = new TestVelocypackMapper();
        
        EntityContainer input = new EntityContainer(); 
        input.entity = new RuleForm(12);
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        
        EntityContainer output = mapper.readValue(json, EntityContainer.class);
        assertEquals(12, output.getEntity().value);
    }
}

