package com.fasterxml.jackson.databind.ext;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class TestJava7Types extends BaseMapTest
{
    public void testPathRoundtrip() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();

        Path input = Paths.get("/tmp", "foo.txt");

        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        assertNotNull(json);

        Path p = mapper.readValue(json, Path.class);
        assertNotNull(p);
        
        assertEquals(input.toUri(), p.toUri());
        assertEquals(input, p);
    }

    // [databind#1688]:
    public void testPolymorphicPath() throws Exception
    {
        ObjectMapper mapper = jsonMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        Path input = Paths.get("/tmp", "foo.txt");

        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(new Object[] { input }));

        Object[] obs = mapper.readValue(json, Object[].class);
        assertEquals(1, obs.length);
        Object ob = obs[0];
        assertTrue(ob instanceof Path);

        assertEquals(input.toString(), ob.toString());
    }
}
