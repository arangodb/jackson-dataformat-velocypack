package tools.jackson.databind.ext.javatime.key;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;

import java.time.YearMonth;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class YearMonthAsKeyTest extends DateTimeTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<YearMonth, String>>() {
    });

    @Test
    public void testSerialization() throws Exception {
        assertEquals(mapAsString("3141-05", "test"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(asMap(YearMonth.of(3141, 5), "test"))),
                "Value is incorrect");
    }

    @Test
    public void testDeserialization() throws Exception {
        assertEquals(asMap(YearMonth.of(3141, 5), "test"),
                READER.readValue(VPackUtils.toVPack(mapAsString("3141-05", "test"))),
                "Value is incorrect");
    }
}
