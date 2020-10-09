package com.fasterxml.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class ImplicitNameMatch792Test extends BaseMapTest
{
    // Simple introspector that gives generated "ctorN" names for constructor
    // parameters
    static class ConstructorNameAI extends JacksonAnnotationIntrospector
    {
        private static final long serialVersionUID = 1L;

        @Override
        public String findImplicitPropertyName(AnnotatedMember member) {
            if (member instanceof AnnotatedParameter) {
                return String.format("ctor%d", ((AnnotatedParameter) member).getIndex());
            }
            return super.findImplicitPropertyName(member);
        }
    }
    
    @JsonPropertyOrder({ "first" ,"second", "other" })
    static class Issue792Bean
    {
        String value;

        public Issue792Bean(@JsonProperty("first") String a,
                @JsonProperty("second") String b) {
            value = a;
            // ignore second arg
        }

        public String getCtor0() { return value; }
        
        public int getOther() { return 3; }
    }

    static class Bean2
    {
        int x = 3;
        
        @JsonProperty("stuff")
        private void setValue(int i) { x = i; }

        public int getValue() { return x; }
    }

    static class ReadWriteBean
    {
        private int value;

        private ReadWriteBean(@JsonProperty(value="value",
                access=JsonProperty.Access.READ_WRITE) int v) {
            value = v;
        }

        public int testValue() { return value; }

        // Let's also add setter to ensure conflict resolution works
        public void setValue(int v) {
            throw new RuntimeException("Should have used constructor for 'value' not setter");
        }
    }

    // Bean that should only serialize 'value', but deserialize both
    static class PasswordBean
    {
        @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
        private String password;

        private int value;

        public int getValue() { return value; }
        public String getPassword() { return password; }

        public String asString() {
            return String.format("[password='%s',value=%d]", password, value);
        }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = sharedMapper();
    
    public void testBindingOfImplicitCreatorNames() throws Exception
    {
        ObjectMapper m = new TestVelocypackMapper();
        m.setAnnotationIntrospector(new ConstructorNameAI());
        String json = com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(new Issue792Bean("a", "b")));
        assertEquals(aposToQuotes("{'first':'a','other':3}"), json);
    }

    public void testImplicitWithSetterGetter() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new Bean2()));
        assertEquals(aposToQuotes("{'stuff':3}"), json);
    }

    public void testReadWriteWithPrivateField() throws Exception
    {
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ReadWriteBean(3)));
        assertEquals("{\"value\":3}", json);
    }

    public void testWriteOnly() throws Exception
    {
        PasswordBean bean = MAPPER.readValue(aposToQuotes("{'value':7,'password':'foo'}"),
                PasswordBean.class);
        assertEquals("[password='foo',value=7]", bean.asString());
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(bean));
        assertEquals("{\"value\":7}", json);
    }
}
