package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

/**
 * Tests to ensure one can disable {@link JsonCreator} annotations.
 */
public class DisablingCreatorsTest
{
     static class ConflictingCreators {
          @JsonCreator(mode=JsonCreator.Mode.PROPERTIES)
          public ConflictingCreators(@JsonProperty("foo") String foo) { }
          @JsonCreator(mode=JsonCreator.Mode.PROPERTIES)
          public ConflictingCreators(@JsonProperty("foo") String foo,
                    @JsonProperty("value") int value) { }
     }

     static class NonConflictingCreators {
          public String _value;

          @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
          public NonConflictingCreators(String foo) { _value = foo; }

          @JsonCreator(mode=JsonCreator.Mode.DISABLED)
          public NonConflictingCreators(String foo, int value) { }
     }

     /*
     /**********************************************************
     /* Helper methods
     /**********************************************************
      */

     @Test
     public void testDisabling() throws Exception
     {
          final ObjectMapper mapper = newVPackMapper();

          // first, non-problematic case
          NonConflictingCreators value = mapper.readValue(VPackUtils.toVPack(q("abc")), NonConflictingCreators.class);
          assertNotNull(value);
          assertEquals("abc", value._value);

          // then something that ought to fail
          try {
               /*ConflictingCreators value =*/ mapper.readValue(VPackUtils.toVPack(q("abc")), ConflictingCreators.class);
               fail("Should have failed with JsonCreator conflict");
          } catch (InvalidDefinitionException e) {
               verifyException(e, "Conflicting property-based creators");
          }
     }
}
