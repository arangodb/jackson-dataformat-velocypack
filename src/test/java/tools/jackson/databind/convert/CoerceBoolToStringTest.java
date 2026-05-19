package tools.jackson.databind.convert;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

public class CoerceBoolToStringTest
{
    private final ObjectMapper DEFAULT_MAPPER = newVPackMapper();

    private final ObjectMapper MAPPER_TO_FAIL = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail))
            .build();

    private final ObjectMapper MAPPER_TRY_CONVERT = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.TryConvert))
            .build();

    private final ObjectMapper MAPPER_TO_NULL = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.AsNull))
            .build();

    private final ObjectMapper MAPPER_TO_EMPTY = vpackMapperBuilder()
            .withCoercionConfig(LogicalType.Textual, cfg ->
                    cfg.setCoercion(CoercionInputShape.Boolean, CoercionAction.AsEmpty))
            .build();

    @Test
    public void testDefaultBooleanToStringCoercion() throws Exception
    {
        assertSuccessfulBooleanToStringCoercionWith(DEFAULT_MAPPER);
    }

    @Test
    public void testCoerceConfigToConvert() throws Exception
    {
        assertSuccessfulBooleanToStringCoercionWith(MAPPER_TRY_CONVERT);
    }

    @Test
    public void testCoerceConfigToNull() throws Exception
    {
        assertNull(MAPPER_TO_NULL.readValue(VPackUtils.toVPack("true"), String.class));
        StringWrapper w = MAPPER_TO_NULL.readValue(VPackUtils.toVPack("{\"str\": false}"), StringWrapper.class);
        assertNull(w.str);
        String[] arr = MAPPER_TO_NULL.readValue(VPackUtils.toVPack("[ true ]"), String[].class);
        assertEquals(1, arr.length);
        assertNull(arr[0]);
    }

    @Test
    public void testCoerceConfigToEmpty() throws Exception
    {
        assertEquals("", MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("true"), String.class));
        StringWrapper w = MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("{\"str\": false}"), StringWrapper.class);
        assertEquals("", w.str);
        String[] arr = MAPPER_TO_EMPTY.readValue(VPackUtils.toVPack("[ true ]"), String[].class);
        assertEquals(1, arr.length);
        assertEquals("", arr[0]);
    }

    @Test
    public void testCoerceConfigToFail() throws Exception
    {
        _verifyCoerceFail(MAPPER_TO_FAIL, String.class, "true");
        _verifyCoerceFail(MAPPER_TO_FAIL, StringWrapper.class, "{\"str\": false}", "string");
        _verifyCoerceFail(MAPPER_TO_FAIL, String[].class, "[ true ]", "to `java.lang.String` value");
    }

    /*
    /********************************************************
    /* Helper methods
    /********************************************************
     */

    private void assertSuccessfulBooleanToStringCoercionWith(ObjectMapper objectMapper)
        throws Exception
    {
        assertEquals("false", objectMapper.readValue(VPackUtils.toVPack("false"), String.class));
        assertEquals("true", objectMapper.readValue(VPackUtils.toVPack("true"), String.class));
        {
            StringWrapper w = objectMapper.readValue(VPackUtils.toVPack("{\"str\": false}"), StringWrapper.class);
            assertEquals("false", w.str);
            String[] arr = objectMapper.readValue(VPackUtils.toVPack("[ true ]"), String[].class);
            assertEquals("true", arr[0]);
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
            fail("Should not accept Boolean for "+targetType.getName()+" when configured to fail.");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce Boolean");
            verifyException(e, targetTypeDesc);
        }
    }
}
