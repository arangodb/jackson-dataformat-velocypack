package com.fasterxml.jackson.databind.misc;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class CaseInsensitiveDeserTest extends BaseMapTest
{
    // [databind#1036]
    static class BaseResponse {
        public int errorCode;
        public String debugMessage;
    }

    static class Issue476Bean {
        public Issue476Type value1, value2;
    }
    static class Issue476Type {
        public String name, value;
    }

    // [databind#1232]: allow per-property case-insensitivity
    static class Role {
        public String ID;
        public String Name;
    }

    static class CaseInsensitiveRoleWrapper
    {
        @JsonFormat(with={ JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES })
        public Role role;
    }

    // [databind#1438]
    static class InsensitiveCreator
    {
        int v;

        @JsonCreator
        public InsensitiveCreator(@JsonProperty("value") int v0) {
            v = v0;
        }
    }

    // [databind#1854]
    static class Obj1854 {
        private final int id;

        private final List<ChildObj1854> items;

        public Obj1854(int id, List<ChildObj1854> items) {
            this.id = id;
            this.items = items;
        }

        @JsonCreator
        public static Obj1854 fromJson(@JsonProperty("ID") int id,
                @JsonProperty("Items") List<ChildObj1854> items) {
            return new Obj1854(id, items);
        }

        public int getId() {
            return id;
        }

        public List<ChildObj1854> getItems() {
            return items;
        }

    }

    // [databind#1854]
    static class ChildObj1854 {
        private final String childId;

        private ChildObj1854(String id) {
            this.childId = id;
        }

        @JsonCreator
        public static ChildObj1854 fromJson(@JsonProperty("ChildID") String cid) {
            return new ChildObj1854(cid);
        }

        public String getId() {
            return childId;
        }
    }

    /*
    /********************************************************
    /* Test methods
    /********************************************************
     */

    private final ObjectMapper MAPPER = new TestVelocypackMapper();
    private final ObjectMapper INSENSITIVE_MAPPER = jsonMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .build();

    // [databind#566]
    public void testCaseInsensitiveDeserialization() throws Exception
    {
        final String JSON = "{\"Value1\" : {\"nAme\" : \"fruit\", \"vALUe\" : \"apple\"}, \"valUE2\" : {\"NAME\" : \"color\", \"value\" : \"red\"}}";
        
        // first, verify default settings which do not accept improper case
        ObjectMapper mapper = new TestVelocypackMapper();
        assertFalse(mapper.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        try {
            mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack(JSON), Issue476Bean.class);
            
            fail("Should not accept improper case properties by default");
        } catch (JsonProcessingException e) {
            verifyException(e, "Unrecognized field");
        }

        // Definitely not OK to enable dynamically - the BeanPropertyMap (which is the consumer of this particular feature) gets cached.
        ObjectReader r = INSENSITIVE_MAPPER.readerFor(Issue476Bean.class);
        Issue476Bean result = r.readValue(com.fasterxml.jackson.VPackUtils.toVPack(JSON));
        assertEquals(result.value1.name, "fruit");
        assertEquals(result.value1.value, "apple");
    }

    // [databind#1036]
    public void testCaseInsensitive1036() throws Exception
    {
        final String json = "{\"ErrorCode\":2,\"DebugMessage\":\"Signature not valid!\"}";
//        final String json = "{\"errorCode\":2,\"debugMessage\":\"Signature not valid!\"}";

        BaseResponse response = INSENSITIVE_MAPPER.readValue(json, BaseResponse.class);
        assertEquals(2, response.errorCode);
        assertEquals("Signature not valid!", response.debugMessage);
    }

    // [databind#1232]: allow per-property case-insensitivity
    public void testCaseInsensitiveWithFormat() throws Exception {
        CaseInsensitiveRoleWrapper w = MAPPER.readValue
                (aposToQuotes("{'role':{'id':'12','name':'Foo'}}"),
                        CaseInsensitiveRoleWrapper.class);
        assertNotNull(w);
        assertEquals("12", w.role.ID);
        assertEquals("Foo", w.role.Name);
    }
    
    // [databind#1438]
    public void testCreatorWithInsensitive() throws Exception
    {
        final String json = aposToQuotes("{'VALUE':3}");
        InsensitiveCreator bean = INSENSITIVE_MAPPER.readValue(json, InsensitiveCreator.class);
        assertEquals(3, bean.v);
    }

    // And allow config overrides too
    public void testCaseInsensitiveWithClassFormat() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configOverride(Role.class)
            .setFormat(JsonFormat.Value.empty()
                    .withFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
        Role role = mapper.readValue
                (aposToQuotes("{'id':'12','name':'Foo'}"),
                        Role.class);
        assertNotNull(role);
        assertEquals("12", role.ID);
        assertEquals("Foo", role.Name);
    }

    public void testIssue1854() throws Exception
    {
        final String DOC = aposToQuotes("{'ID': 1, 'Items': [ { 'ChildID': 10 } ]}");
        Obj1854 result = INSENSITIVE_MAPPER.readValue(DOC, Obj1854.class);
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
    }
}
