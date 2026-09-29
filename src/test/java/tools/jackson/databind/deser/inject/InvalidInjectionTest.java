package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;

import static org.junit.jupiter.api.Assertions.fail;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

public class InvalidInjectionTest
{
    static class BadBean1 {
        @JacksonInject protected String prop1;
        @JacksonInject protected String prop2;
    }

    static class BadBean2 {
        @JacksonInject("x") protected String prop1;
        @JacksonInject("x") protected String prop2;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testInvalidDup() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack("{}"), BadBean1.class);
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Duplicate injectable value");
        }
        try {
            MAPPER.readValue(VPackUtils.toVPack("{}"), BadBean2.class);
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Duplicate injectable value");
        }
    }

}
