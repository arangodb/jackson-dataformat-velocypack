package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class JavaUtilPropertiesDeserializationTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = sharedMapper();

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [databind#810]
    @Test
    public void testReadProperties() throws Exception
    {
        Properties props = MAPPER.readValue(VPackUtils.toVPack(a2q("{'a':'foo', 'b':123, 'c':true}")),
                Properties.class);
        assertEquals(3, props.size());
        assertEquals("foo", props.getProperty("a"));
        assertEquals("123", props.getProperty("b"));
        assertEquals("true", props.getProperty("c"));
    }
}
