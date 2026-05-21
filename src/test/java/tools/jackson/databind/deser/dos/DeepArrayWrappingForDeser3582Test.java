package tools.jackson.databind.deser.dos;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.fail;
import static tools.jackson.databind.testutil.DatabindTestUtil.verifyException;
import static tools.jackson.databind.testutil.DatabindTestUtil.vpackMapperBuilder;

public class DeepArrayWrappingForDeser3582Test
{
    // 23-Aug-2022, tatu: Before fix, failed with 5000
    private final static int TOO_DEEP_NESTING = 9999;

    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            .build();

    @Test
    @Disabled
    public void testArrayWrapping() throws Exception
    {
        final String doc = _nestedDoc(TOO_DEEP_NESTING, "[ ", "] ", "{}");
        try {
            MAPPER.readValue(VPackUtils.toVPack(doc), DatabindTestUtil.Point.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "nested Array");
            verifyException(e, "only single");
        }
    }

    private String _nestedDoc(int nesting, String open, String close, String content) {
        StringBuilder sb = new StringBuilder(nesting * (open.length() + close.length()));
        for (int i = 0; i < nesting; ++i) {
            sb.append(open);
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        sb.append("\n").append(content).append("\n");
        for (int i = 0; i < nesting; ++i) {
            sb.append(close);
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
