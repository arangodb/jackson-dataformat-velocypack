package tools.jackson.databind.convert;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

public class CoerceFloatToStringTest
{
    private final ObjectMapper DEFAULT_MAPPER = newVPackMapper();

    private final ObjectMapper MAPPER_TO_FAIL = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Float, CoercionAction.Fail))
            .build();

    private final ObjectMapper MAPPER_TRY_CONVERT = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Float, CoercionAction.TryConvert))
            .build();

    private final ObjectMapper MAPPER_TO_NULL = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Float, CoercionAction.AsNull))
            .build();

    private final ObjectMapper MAPPER_TO_EMPTY = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Float, CoercionAction.AsEmpty))
            .build();

    @Test
    public void testDefaultFloatToStringCoercion() throws Exception
    {
        assertSuccessfulFloatToStringCoercionWith(DEFAULT_MAPPER);
    }

    @Test
    public void testCoerceConfigToConvert() throws Exception
    {
        assertSuccessfulFloatToStringCoercionWith(MAPPER_TRY_CONVERT);
    }

    @Test
    public void testCoerceConfigToNull() throws Exception
    {
        assertNull(MAPPER_TO_NULL.readValue(VPackUtils.toVPack("1.2"), String.class));
        StringWrapper w = MAPPER_TO_NULL.readValue(VPackUtils.toVPack("{\"str\": -5.3}"), StringWrapper.class);
        assertNull(w.str);
        String[] arr = MAPPER_TO_NULL.readValue(VPackUtils.toVPack("[ 2.1 ]"), String[].class);
        assertEquals(1, arr.length);
        assertNull(arr[0]);
    }

    @Test
    public void testCoerceConfigToEmpty() throws Exception
    {
        assertEquals("", MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("3.5"), String.class));
        StringWrapper w = MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("{\"str\": -5.3}"), StringWrapper.class);
        assertEquals("", w.str);
        String[] arr = MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("[ 2.1 ]"), String[].class);
        assertEquals(1, arr.length);
        assertEquals("", arr[0]);
    }

    @Test
    public void testCoerceConfigToFail() throws Exception
    {
        _verifyCoerceFail(MAPPER_TO_FAIL, String.class, "3.5");
        _verifyCoerceFail(MAPPER_TO_FAIL, StringWrapper.class, "{\"str\": -5.3}", "string");
        _verifyCoerceFail(MAPPER_TO_FAIL, String[].class, "[ 2.1 ]",
                "to `java.lang.String` value");
    }

    /*
    /********************************************************
    /* Helper methods
    /********************************************************
     */

    private void assertSuccessfulFloatToStringCoercionWith(ObjectMapper objectMapper)
        throws Exception
    {
        assertEquals("3.0", objectMapper.readValue(VPackUtils.toVPack("3.0"), String.class));
        assertEquals("-2.0", objectMapper.readValue(VPackUtils.toVPack("-2.0"), String.class));
        {
            StringWrapper w = objectMapper.readValue(VPackUtils.toVPack("{\"str\": -5.0}"), StringWrapper.class);
            assertEquals("-5.0", w.str);
            String[] arr = objectMapper.readValue(VPackUtils.toVPack("[ 2.0 ]"), String[].class);
            assertEquals("2.0", arr[0]);
        }
    }

    private void _verifyCoerceFail(ObjectMapper m, Class<?> targetType,
                                   String doc) throws Exception
    {
        _verifyCoerceFail(m.reader(), targetType, doc, targetType.getName());
    }

    private void _verifyCoerceFail(ObjectMapper m, Class<?> targetType,
                                   String doc, String targetTypeDesc) throws Exception
    {
        _verifyCoerceFail(m.reader(), targetType, doc, targetTypeDesc);
    }

    private void _verifyCoerceFail(ObjectReader r, Class<?> targetType,
            String doc, String targetTypeDesc)
        throws Exception
    {
        try {
            r.forType(targetType).readValue(VPackUtils.toVPack(doc));
            fail("Should not accept Float for "+targetType.getName()+" when configured to fail.");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce Float");
            verifyException(e, targetTypeDesc);
        }
    }
}
