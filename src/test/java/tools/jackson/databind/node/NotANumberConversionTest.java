package tools.jackson.databind.node;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class NotANumberConversionTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    @Test
    public void testBigDecimalWithNaN() throws Exception
    {
        JsonNode tree = MAPPER.valueToTree(new DoubleWrapper(Double.NaN));
        assertNotNull(tree);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(tree));
        assertNotNull(json);

        tree = MAPPER.valueToTree(new DoubleWrapper(Double.NEGATIVE_INFINITY));
        assertNotNull(tree);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(tree));
        assertNotNull(json);

        tree = MAPPER.valueToTree(new DoubleWrapper(Double.POSITIVE_INFINITY));
        assertNotNull(tree);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(tree));
        assertNotNull(json);
    }

    // for [databind#1315]: no accidental coercion to DoubleNode
    @Test
    public void testBigDecimalWithoutNaN() throws Exception
    {
        BigDecimal input = new BigDecimal(Double.MIN_VALUE).divide(new BigDecimal(10L));
        JsonNode tree = MAPPER.readTree(VPackUtils.toVPack(input.toString()));
        assertTrue(tree.isBigDecimal());
        BigDecimal output = tree.decimalValue();
        assertEquals(input, output);
    }
}
