package com.fasterxml.jackson.databind.exc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class BasicExceptionTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = newJsonMapper();
    private final JsonFactory JSON_F = MAPPER.getFactory();

    public void testBadDefinition() throws Exception
    {
        JavaType t = TypeFactory.defaultInstance().constructType(String.class);
        JsonParser p = JSON_F.createParser(com.fasterxml.jackson.VPackUtils.toVPack("[]"));
        InvalidDefinitionException e = new InvalidDefinitionException(p,
               "Testing", t);
        assertEquals("Testing", e.getOriginalMessage());
        assertEquals(String.class, e.getType().getRawClass());
        assertNull(e.getBeanDescription());
        assertNull(e.getProperty());
        assertSame(p, e.getProcessor());
        p.close();

        // and via factory method:
        BeanDescription beanDef = MAPPER.getSerializationConfig().introspectClassAnnotations(getClass());
        e = InvalidDefinitionException.from(p, "Testing",
                beanDef, (BeanPropertyDefinition) null);
        assertEquals(beanDef.getType(), e.getType());
        assertNotNull(e);
        
        // and the other constructor too
        JsonGenerator g = JSON_F.createGenerator(new ByteArrayOutputStream());
        e = new InvalidDefinitionException(p,
                "Testing", t);
        assertEquals("Testing", e.getOriginalMessage());
        assertEquals(String.class, e.getType().getRawClass());

        // and factory
        e = InvalidDefinitionException.from(g, "Testing",
                beanDef, (BeanPropertyDefinition) null);
        assertEquals(beanDef.getType(), e.getType());
        assertNotNull(e);
        
        g.close();
    }

    @SuppressWarnings("deprecation")
    public void testInvalidFormat() throws Exception
    {
        // deprecated methods should still work:
        InvalidFormatException e = new InvalidFormatException("Testing", Boolean.TRUE,
                String.class);
        assertSame(Boolean.TRUE, e.getValue());
        assertNull(e.getProcessor());
        assertNotNull(e);

        e = new InvalidFormatException("Testing", JsonLocation.NA,
                Boolean.TRUE, String.class);
        assertSame(Boolean.TRUE, e.getValue());
        assertNull(e.getProcessor());
        assertNotNull(e);
    }

    public void testIgnoredProperty() throws Exception
    {
        // first just construct valid instance with some variations
        JsonParser p = JSON_F.createParser(com.fasterxml.jackson.VPackUtils.toVPack("{ }"));
        IgnoredPropertyException e = IgnoredPropertyException.from(p,
                this, // to get class from
                "testProp", Collections.<Object>singletonList("x"));
        assertNotNull(e);

        e = IgnoredPropertyException.from(p,
                getClass(),
                "testProp", null);
        assertNotNull(e);
        assertNull(e.getKnownPropertyIds());
        p.close();

        // also, verify failure if null passed for "value"
        try {
            IgnoredPropertyException.from(p, null,
                    "testProp", Collections.<Object>singletonList("x"));
            fail("Should not pass");
        } catch (NullPointerException e2) {
        }
    }

    public void testUnrecognizedProperty() throws Exception
    {
        JsonParser p = JSON_F.createParser(com.fasterxml.jackson.VPackUtils.toVPack("{ }"));
        UnrecognizedPropertyException e = UnrecognizedPropertyException.from(p, this,
                "testProp", Collections.<Object>singletonList("y"));
        assertNotNull(e);
        assertEquals(getClass(), e.getReferringClass());
        Collection<Object> ids = e.getKnownPropertyIds();
        assertNotNull(ids);
        assertEquals(1, ids.size());
        assertTrue(ids.contains("y"));

        e = UnrecognizedPropertyException.from(p, getClass(),
                "testProp", Collections.<Object>singletonList("y"));

        assertEquals(getClass(), e.getReferringClass());
        p.close();
    }

    static class User {
        public String user;
    }

    static class Users {
        public ArrayList<User> userList;
    }
}
