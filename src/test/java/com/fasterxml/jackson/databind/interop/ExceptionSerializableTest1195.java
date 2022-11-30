package com.fasterxml.jackson.databind.interop;

import java.io.*;
import java.util.List;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

import static com.fasterxml.jackson.TestUtils.isAtLeastVersion;

public class ExceptionSerializableTest1195 extends BaseMapTest
{
    static class ClassToRead {
        public int x;
    }

    static class ContainerClassToRead {
        public ClassToRead classToRead;
    }

    static class ContainerClassesToRead {
        public List<ClassToRead> classesToRead;
    }

    final ObjectMapper MAPPER = new TestVelocypackMapper();

    public void testExceptionSerializabilitySimple() throws Exception
    {
        if(!isAtLeastVersion(2, 12)) return;

        try {
            MAPPER.readValue("{\"x\": \"B\"}", ClassToRead.class);
            fail("Should not have passed");
        } catch (JsonMappingException e) {
            verifyException(e, "not a valid `int` value");
            _testSerializability(e);
        }
        try {
            MAPPER.readValue("{\"classToRead\": {\"x\": \"B\"}}", ContainerClassToRead.class);
            fail("Should not have passed");
        } catch (JsonMappingException e) {
            verifyException(e, "not a valid `int`");
            _testSerializability(e);
        }
    }

    public void testExceptionSerializabilityStructured() throws Exception
    {
        if(!isAtLeastVersion(2, 12)) return;

        try {
            MAPPER.readValue("{\"classesToRead\": [{\"x\": 1}, {\"x\": \"B\"}]}",
                    ContainerClassesToRead.class);
            fail("Should not have passed");
        } catch (JsonMappingException e) {
            verifyException(e, "not a valid `int` value");
            _testSerializability(e);
        }
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void _testSerializability(Exception e) throws IOException
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(1000);
        ObjectOutputStream stream = new ObjectOutputStream(bytes);
        try {
            stream.writeObject(e);
            stream.close();
        } catch (Exception e2) {
            fail("Failed to JDK serialize "+e.getClass().getName()+": "+e2);
        }
        // and then back...
        byte[] b = bytes.toByteArray();
        ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(b));
        Object result = null;
        try {
            result = objIn.readObject();
        } catch (Exception e2) {
            fail("Failed to JDK deserialize "+e.getClass().getName()+": "+e2);
        }
        objIn.close();
        assertNotNull(result);
    }
}
