package tools.jackson.databind.convert;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;

import static org.junit.jupiter.api.Assertions.*;

import static tools.jackson.databind.testutil.DatabindTestUtil.*;

public class CoerceNaNStringToNumberTest
{
    static class DoubleBean {
        double _v;
        public void setV(double v) { _v = v; }
    }

    static class FloatBean {
        float _v;
        public void setV(float v) { _v = v; }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    private final ObjectMapper MAPPER_NO_COERCION = vpackMapperBuilder()
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .build();

    /*
    /**********************************************************************
    /* Test methods for coercing from "NaN Strings": tricky edge case as
    /* NaNs are not legal JSON tokens by default (although Jackson has options
    /* to allow)... so with 2.12 we consider String as natural representation
    /* and not coercible. This may need to be resolved in future but for now
    /* this is needed for backwards-compatibility.
    /**********************************************************************
     */

    @Test
    public void testDoublePrimitiveNonNumeric() throws Exception
    {
        // first, simple case:
        // bit tricky with binary fps but...
        double value = Double.POSITIVE_INFINITY;
        DoubleBean result = MAPPER.readValue(VPackUtils.toVPack("{\"v\":\""+value+"\"}"), DoubleBean.class);
        assertEquals(value, result._v);

        // should work with arrays too..
        double[] array = MAPPER.readValue(VPackUtils.toVPack("[ \"Infinity\" ]"), double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Double.POSITIVE_INFINITY, array[0]);
    }

    @Test
    public void testDoublePrimFromNaNCoercionDisabled() throws Exception
    {
        // first, simple case:
        double value = Double.POSITIVE_INFINITY;
        DoubleBean result = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack("{\"v\":\""+value+"\"}"), DoubleBean.class);
        assertEquals(value, result._v);

        // should work with arrays too..
        double[] array = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack("[ \"Infinity\" ]"), double[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Double.POSITIVE_INFINITY, array[0]);
    }

    @Test
    public void testDoubleWrapperFromNaNCoercionDisabled() throws Exception
    {
        double value = Double.POSITIVE_INFINITY;
        Double dv = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack(q(String.valueOf(value))), Double.class);
        assertTrue(dv.isInfinite());
    }

    @Test
    public void testFloatPrimitiveNonNumeric() throws Exception
    {
        // bit tricky with binary fps but...
        float value = Float.POSITIVE_INFINITY;
        FloatBean result = MAPPER.readValue(VPackUtils.toVPack("{\"v\":\""+value+"\"}"), FloatBean.class);
        assertEquals(value, result._v);

        // should work with arrays too..
        float[] array = MAPPER.readValue(VPackUtils.toVPack("[ \"Infinity\" ]"), float[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Float.POSITIVE_INFINITY, array[0]);
    }

    @Test
    public void testFloatPriFromNaNCoercionDisabled() throws Exception
    {
        // first, simple case:
        float value = Float.POSITIVE_INFINITY;
        FloatBean result = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack("{\"v\":\""+value+"\"}"), FloatBean.class);
        assertEquals(value, result._v);

        // should work with arrays too..
        float[] array = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack("[ \"Infinity\" ]"), float[].class);
        assertNotNull(array);
        assertEquals(1, array.length);
        assertEquals(Float.POSITIVE_INFINITY, array[0]);
    }

    @Test
    public void testFloatWrapperFromNaNCoercionDisabled() throws Exception
    {
        float value = Float.POSITIVE_INFINITY;
        Float dv = MAPPER_NO_COERCION.readValue(VPackUtils.toVPack(q(String.valueOf(value))), Float.class);
        assertTrue(dv.isInfinite());
    }
}
