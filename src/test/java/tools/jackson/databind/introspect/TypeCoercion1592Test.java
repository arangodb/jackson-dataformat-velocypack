package tools.jackson.databind.introspect;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// [databind#1592]: allow "coercion" between primitive/wrapper (mostly just ignoring)
public class TypeCoercion1592Test extends DatabindTestUtil
{
    static class Bean1592
    {
        @JsonSerialize(as=Integer.class)
        public int i;

        @JsonDeserialize(as=Long.class)
        public long l;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testTypeCoercion1592() throws Exception
    {
        // first, serialize
        VPackUtils.toJson(MAPPER.writeValueAsBytes(new Bean1592()));
        Bean1592 result = MAPPER.readValue(VPackUtils.toVPack("{}"), Bean1592.class);
        assertNotNull(result);
    }
}
