package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

public class TestInnerClass extends BaseMapTest
{
    static class Dog
    {
      public String name;
      public Brain brain;

      public Dog() { }
      public Dog(String n, boolean thinking) {
          name = n;
          brain = new Brain();
          brain.isThinking = thinking;
      }

      // note: non-static
      public class Brain {
          @JsonProperty("brainiac")
          public boolean isThinking;

          public String parentName() { return name; }
      }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    public void testSimpleNonStaticInner() throws Exception
    {
        // Let's actually verify by first serializing, then deserializing back
        ObjectMapper mapper = new TestVelocypackMapper();
        Dog input = new Dog("Smurf", true);
        String json = com.fasterxml.jackson.VPackUtils.toJson( mapper.writeValueAsBytes(input));
        Dog output = mapper.readValue(json, Dog.class);
        assertEquals("Smurf", output.name);
        assertNotNull(output.brain);
        assertTrue(output.brain.isThinking);
        // and verify correct binding...
        assertEquals("Smurf", output.brain.parentName());
        output.name = "Foo";
        assertEquals("Foo", output.brain.parentName());

        // also, null handling
        input.brain = null;

        output = mapper.readValue(mapper.writeValueAsBytes(input), Dog.class);
        assertNull(output.brain);
    }
}
