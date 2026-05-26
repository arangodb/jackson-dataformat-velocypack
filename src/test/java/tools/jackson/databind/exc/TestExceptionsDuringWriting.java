package tools.jackson.databind.exc;

import org.junit.jupiter.api.Test;
import tools.jackson.core.*;
import tools.jackson.databind.*;
import tools.jackson.databind.module.SimpleModule;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static tools.jackson.databind.testutil.DatabindTestUtil.verifyException;
import static tools.jackson.databind.testutil.DatabindTestUtil.vpackMapperBuilder;

/**
 * Unit test for verifying that exceptions are properly handled (caught,
 * re-thrown or wrapped, depending)
 * with Object serialization.
 */
public class TestExceptionsDuringWriting
{
    /*
    /**********************************************************
    /* Helper classes
    /**********************************************************
     */

    static class Bean {
        // no methods, we'll use our custom serializer
    }

    static class SerializerWithErrors
        extends ValueSerializer<Bean>
    {
        @Override
        public void serialize(Bean value, JsonGenerator jgen, SerializationContext provider)
        {
            throw new IllegalArgumentException("test string");
        }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    /**
     * Unit test that verifies that by default all exceptions except for
     * JacksonExceptions are caught and wrapped.
     */
    @Test
    public void testCatchAndRethrow()
        throws Exception
    {
        SimpleModule module = new SimpleModule("test-exceptions", Version.unknownVersion());
        module.addSerializer(Bean.class, new SerializerWithErrors());
        ObjectMapper mapper = vpackMapperBuilder()
                .addModule(module)
                .build();
        try {
            StringWriter sw = new StringWriter();
            // And just to make things more interesting, let's create a nested data struct...
            Bean[] b = { new Bean() };
            List<Bean[]> l = new ArrayList<Bean[]>();
            l.add(b);
            mapper.writeValue(sw, l);
            fail("Should have gotten an exception");
        } catch (JacksonException e) { // too generic but will do for now
            // should contain original message somewhere
            verifyException(e, "test string");
            Throwable root = e.getCause();
            assertNotNull(root);

            if (!(root instanceof IllegalArgumentException)) {
                fail("Wrapped exception not IAE, but "+root.getClass());
            }
        }
    }

}

