package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BigNumbersDeserTest
    extends DatabindTestUtil
{
    static class BigIntegerWrapper {
        public BigInteger number;
    }

    static class BigDecimalWrapper {
        public BigDecimal number;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    private ObjectMapper newVPackMapperWithUnlimitedNumberSizeSupport() {
        VPackFactory jsonFactory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxNumberLength(Integer.MAX_VALUE).build())
                .build();
        return VPackMapper.builder(jsonFactory).build();
    }

    @Test
    public void testDouble() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack(generateJson("d")), DoubleWrapper.class);
            fail("expected StreamReadException");
        } catch (StreamConstraintsException e) {
            verifyException(e, "Number value length", "exceeds the maximum allowed");
        }
    }

    @Test
    public void testDoubleUnlimited() throws Exception
    {
        DoubleWrapper dw =
            newVPackMapperWithUnlimitedNumberSizeSupport().readValue(VPackUtils.toVPack(generateJson("d")), DoubleWrapper.class);
        assertNotNull(dw);
    }

    @Test
    public void testBigDecimal() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack(generateJson("number")), BigDecimalWrapper.class);
            fail("expected StreamReadException");
        } catch (StreamConstraintsException e) {
            verifyException(e, "Number value length ", "exceeds the maximum allowed");
        }
    }

    @Test
    public void testBigDecimalUnlimited() throws Exception
    {
        BigDecimalWrapper bdw =
                newVPackMapperWithUnlimitedNumberSizeSupport()
                        .readValue(VPackUtils.toVPack(generateJson("number")), BigDecimalWrapper.class);
        assertNotNull(bdw);
    }

    @Test
    public void testBigInteger() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack(generateJson("number")), BigIntegerWrapper.class);
            fail("expected StreamReadException");
        } catch (StreamConstraintsException e) {
            verifyException(e, "Number value length", "exceeds the maximum allowed");
        }
    }

    @Test
    public void testBigIntegerUnlimited() throws Exception
    {
        BigIntegerWrapper bdw =
                newVPackMapperWithUnlimitedNumberSizeSupport()
                        .readValue(VPackUtils.toVPack(generateJson("number")), BigIntegerWrapper.class);
        assertNotNull(bdw);
    }

    // [databind#4435]
    @Test
    public void testNumberStartingWithDot() throws Exception {
        _testNumberWith(".555555555555555555555555555555");
        _testNumberWith("-.555555555555555555555555555555");
        _testNumberWith("+.555555555555555555555555555555");
    }

    // [databind#4577]
    @Test
    public void testNumberEndingWithDot() throws Exception {
        _testNumberWith("55.");
        _testNumberWith("-55.");
        _testNumberWith("+55.");
    }
    
    private void _testNumberWith(String num) throws Exception
    {
        BigDecimal exp = new BigDecimal(num);
        BigDecimalWrapper w = MAPPER.readValue(VPackUtils.toVPack("{\"number\":\"" + num + "\"}"), BigDecimalWrapper.class);
        assertEquals(exp, w.number);
    }

    private String generateJson(final String fieldName) {
        final int len = 1200;
        final StringBuilder sb = new StringBuilder();
        sb.append("{\"")
                .append(fieldName)
                .append("\": ");
        for (int i = 0; i < len; i++) {
            sb.append(1);
        }
        sb.append("}");
        return sb.toString();
    }
}
