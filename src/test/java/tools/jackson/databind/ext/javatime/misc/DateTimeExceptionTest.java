package tools.jackson.databind.ext.javatime.misc;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;

import java.time.DateTimeException;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeExceptionTest extends DateTimeTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    // [modules-java#319]: should not fail to ser/deser DateTimeException
    @Test
    public void testDateTimeExceptionRoundtrip() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new DateTimeException("Test!")));
        DateTimeException result = MAPPER.readValue(VPackUtils.toVPack(json), DateTimeException.class);
        assertEquals("Test!", result.getMessage());
    }
}
