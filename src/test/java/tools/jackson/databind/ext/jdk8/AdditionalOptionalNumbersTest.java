package tools.jackson.databind.ext.jdk8;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for OptionalInt, OptionalLong, and OptionalDouble
 * covering UNWRAP_SINGLE_VALUE_ARRAYS, boundary values, serialization
 * filters, and coercion edge cases.
 */
public class AdditionalOptionalNumbersTest
    extends DatabindTestUtil
{
    static class OptionalIntBean {
        public OptionalInt value;

        public OptionalIntBean() { value = OptionalInt.empty(); }
        OptionalIntBean(int v) { this(OptionalInt.of(v)); }
        OptionalIntBean(OptionalInt v) { value = v; }
    }

    static class OptionalLongBean {
        public OptionalLong value;

        public OptionalLongBean() { value = OptionalLong.empty(); }
        OptionalLongBean(long v) { this(OptionalLong.of(v)); }
        OptionalLongBean(OptionalLong v) { value = v; }
    }

    static class OptionalDoubleBean {
        public OptionalDouble value;

        public OptionalDoubleBean() { value = OptionalDouble.empty(); }
        OptionalDoubleBean(double v) { this(OptionalDouble.of(v)); }
        OptionalDoubleBean(OptionalDouble v) { value = v; }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    /*
    /**********************************************************
    /* OptionalInt: boundary values
    /**********************************************************
     */

    @Test
    public void testOptionalIntBoundaryValues() throws Exception
    {
        OptionalIntBean maxBean = new OptionalIntBean(Integer.MAX_VALUE);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(maxBean));
        OptionalIntBean result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalIntBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(Integer.MAX_VALUE, result.value.getAsInt());

        OptionalIntBean minBean = new OptionalIntBean(Integer.MIN_VALUE);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(minBean));
        result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalIntBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(Integer.MIN_VALUE, result.value.getAsInt());
    }

    @Test
    public void testOptionalIntZero() throws Exception
    {
        OptionalIntBean bean = new OptionalIntBean(0);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(bean));
        OptionalIntBean result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalIntBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(0, result.value.getAsInt());
    }

    /*
    /**********************************************************
    /* OptionalInt: serialization filter
    /**********************************************************
     */

    @Test
    public void testOptionalIntSerializeFilter() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertEquals(a2q("{'value':123}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalIntBean(123))));
        // absent is not strictly null so still serialized
        assertEquals(a2q("{'value':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalIntBean())));

        mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals(a2q("{'value':456}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalIntBean(456))));
        assertEquals(a2q("{}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalIntBean())));
    }

    /*
    /**********************************************************
    /* OptionalInt: UNWRAP_SINGLE_VALUE_ARRAYS
    /**********************************************************
     */

    @Test
    public void testOptionalIntUnwrapSingleValueArrays() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalInt result = mapper.readValue(VPackUtils.toVPack("[42]"), OptionalInt.class);
        assertTrue(result.isPresent());
        assertEquals(42, result.getAsInt());
    }

    @Test
    public void testOptionalIntUnwrapSingleValueArraysDisabled() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("[42]"), OptionalInt.class));
    }

    /*
    /**********************************************************
    /* OptionalInt: float-to-int coercion
    /**********************************************************
     */

    @Test
    public void testOptionalIntFromFloat() throws Exception
    {
        OptionalInt result = MAPPER.readValue(VPackUtils.toVPack("2.0"), OptionalInt.class);
        assertTrue(result.isPresent());
        assertEquals(2, result.getAsInt());
    }

    /*
    /**********************************************************
    /* OptionalLong: UNWRAP_SINGLE_VALUE_ARRAYS
    /**********************************************************
     */

    @Test
    public void testOptionalLongUnwrapSingleValueArrays() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalLong result = mapper.readValue(VPackUtils.toVPack("[99]"), OptionalLong.class);
        assertTrue(result.isPresent());
        assertEquals(99L, result.getAsLong());
    }

    @Test
    public void testOptionalLongUnwrapSingleValueArraysDisabled() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("[99]"), OptionalLong.class));
    }

    /*
    /**********************************************************
    /* OptionalLong: float-to-long coercion
    /**********************************************************
     */

    @Test
    public void testOptionalLongFromFloat() throws Exception
    {
        OptionalLong result = MAPPER.readValue(VPackUtils.toVPack("3.0"), OptionalLong.class);
        assertTrue(result.isPresent());
        assertEquals(3L, result.getAsLong());
    }

    /*
    /**********************************************************
    /* OptionalDouble: boundary values
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleBoundaryValues() throws Exception
    {
        OptionalDoubleBean maxBean = new OptionalDoubleBean(Double.MAX_VALUE);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(maxBean));
        OptionalDoubleBean result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalDoubleBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(Double.MAX_VALUE, result.value.getAsDouble());

        OptionalDoubleBean minBean = new OptionalDoubleBean(Double.MIN_VALUE);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(minBean));
        result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalDoubleBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(Double.MIN_VALUE, result.value.getAsDouble());
    }

    @Test
    public void testOptionalDoubleZero() throws Exception
    {
        OptionalDoubleBean bean = new OptionalDoubleBean(0.0);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(bean));
        OptionalDoubleBean result = MAPPER.readValue(VPackUtils.toVPack(json), OptionalDoubleBean.class);
        assertTrue(result.value.isPresent());
        assertEquals(0.0, result.value.getAsDouble());
    }

    @Test
    public void testOptionalDoubleNegativeZero() throws Exception
    {
        OptionalDouble result = MAPPER.readValue(VPackUtils.toVPack("-0.0"), OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(-0.0, result.getAsDouble());
    }

    /*
    /**********************************************************
    /* OptionalDouble: serialization filter
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleSerializeFilter() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
        assertEquals(a2q("{'value':1.5}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalDoubleBean(1.5))));
        // absent is not strictly null so still serialized
        assertEquals(a2q("{'value':null}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalDoubleBean())));

        mapper = vpackMapperBuilder()
                .changeDefaultPropertyInclusion(
                        incl -> incl.withValueInclusion(JsonInclude.Include.NON_ABSENT))
                .build();
        assertEquals(a2q("{'value':2.5}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalDoubleBean(2.5))));
        assertEquals(a2q("{}"),
                VPackUtils.toJson(mapper.writeValueAsBytes(new OptionalDoubleBean())));
    }

    /*
    /**********************************************************
    /* OptionalDouble: UNWRAP_SINGLE_VALUE_ARRAYS
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleUnwrapSingleValueArrays() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        OptionalDouble result = mapper.readValue(VPackUtils.toVPack("[3.14]"), OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(3.14, result.getAsDouble(), 0.001);
    }

    @Test
    public void testOptionalDoubleUnwrapSingleValueArraysDisabled() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("[3.14]"), OptionalDouble.class));
    }

    /*
    /**********************************************************
    /* OptionalDouble: integer-to-double coercion
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleFromInteger() throws Exception
    {
        OptionalDouble result = MAPPER.readValue(VPackUtils.toVPack("42"), OptionalDouble.class);
        assertTrue(result.isPresent());
        assertEquals(42.0, result.getAsDouble());
    }

    /*
    /**********************************************************
    /* OptionalDouble: special values in bean context
    /**********************************************************
     */

    @Test
    public void testOptionalDoubleSpecialValuesRoundTrip() throws Exception
    {
        // NaN
        OptionalDouble nan = MAPPER.readValue(VPackUtils.toVPack(q("NaN")), OptionalDouble.class);
        assertTrue(nan.isPresent());
        assertTrue(Double.isNaN(nan.getAsDouble()));

        // Positive Infinity
        OptionalDouble posInf = MAPPER.readValue(VPackUtils.toVPack(q("Infinity")), OptionalDouble.class);
        assertTrue(posInf.isPresent());
        assertEquals(Double.POSITIVE_INFINITY, posInf.getAsDouble());

        // Negative Infinity
        OptionalDouble negInf = MAPPER.readValue(VPackUtils.toVPack(q("-Infinity")), OptionalDouble.class);
        assertTrue(negInf.isPresent());
        assertEquals(Double.NEGATIVE_INFINITY, negInf.getAsDouble());
    }

    /*
    /**********************************************************
    /* Cross-type: unexpected token handling
    /**********************************************************
     */

    @Test
    public void testOptionalIntFromBooleanFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("true"), OptionalInt.class));
    }

    @Test
    public void testOptionalLongFromBooleanFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("true"), OptionalLong.class));
    }

    @Test
    public void testOptionalDoubleFromBooleanFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("true"), OptionalDouble.class));
    }

    @Test
    public void testOptionalIntFromObjectFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("{}"), OptionalInt.class));
    }

    @Test
    public void testOptionalLongFromObjectFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("{}"), OptionalLong.class));
    }

    @Test
    public void testOptionalDoubleFromObjectFails() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack("{}"), OptionalDouble.class));
    }
}
