package com.fasterxml.jackson.databind.deser;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

@SuppressWarnings("serial")
public class TestGenericCollectionDeser
    extends BaseMapTest
{
    static class ListSubClass extends ArrayList<StringWrapper> { }

    /**
     * Map class that should behave like {@link ListSubClass}, but by
     * using annotations.
     */
    @JsonDeserialize(contentAs=StringWrapper.class)
    static class AnnotatedStringList extends ArrayList<Object> { }

    @JsonDeserialize(contentAs=BooleanElement.class)
    static class AnnotatedBooleanList extends ArrayList<Object> { }

    protected static class BooleanElement {
        public Boolean b;

        @JsonCreator
        public BooleanElement(Boolean value) { b = value; }

        @JsonValue public Boolean value() { return b; }
    }
    
    /*
    /**********************************************************
    /* Tests for sub-classing
    /**********************************************************
     */

    /**
     * Verifying that sub-classing works ok wrt generics information
     */
    public void testListSubClass() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        ListSubClass result = mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack("[ \"123\" ]"), ListSubClass.class);
        assertEquals(1, result.size());
        Object value = result.get(0);
        assertEquals(StringWrapper.class, value.getClass());
        StringWrapper bw = (StringWrapper) value;
        assertEquals("123", bw.str);
    }

    /*
    /**********************************************************
    /* Tests for annotations
    /**********************************************************
     */

    // Verifying that sub-classing works ok wrt generics information
    public void testAnnotatedLStringist() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        AnnotatedStringList result = mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack("[ \"...\" ]"), AnnotatedStringList.class);
        assertEquals(1, result.size());
        Object ob = result.get(0);
        assertEquals(StringWrapper.class, ob.getClass());
        assertEquals("...", ((StringWrapper) ob).str);
    }

    public void testAnnotatedBooleanList() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        AnnotatedBooleanList result = mapper.readValue(com.fasterxml.jackson.VPackUtils.toVPack("[ false ]"), AnnotatedBooleanList.class);
        assertEquals(1, result.size());
        Object ob = result.get(0);
        assertEquals(BooleanElement.class, ob.getClass());
        assertFalse(((BooleanElement) ob).b);
    }
}
