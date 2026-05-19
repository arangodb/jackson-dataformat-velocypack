package tools.jackson.databind.jsontype.vld;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for verifying that "unsafe" base type(s) for polymorphic deserialization
 * are correctly handled
 */
public class AnnotatedPolymorphicValidationTest
    extends DatabindTestUtil
{
    static class WrappedPolymorphicUntyped {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
        public Object value;

        protected WrappedPolymorphicUntyped() { }
    }

    static class WrappedPolymorphicUntypedSer {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
        public Serializable value;

        protected WrappedPolymorphicUntypedSer() { }
    }

    static class NumbersAreOkValidator extends DefaultBaseTypeLimitingValidator
    {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean isUnsafeBaseType(DatabindContext ctxt, JavaType baseType)
        {
            // only override handling for `Object`
            if (baseType.hasRawClass(Object.class)) {
                return false;
            }
            return super.isUnsafeBaseType(ctxt, baseType);
        }

        @Override
        protected boolean isSafeSubType(DatabindContext ctxt,
                JavaType baseType, JavaType subType) {
            return baseType.isTypeOrSubTypeOf(Number.class);
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testPolymorphicWithUnsafeBaseType() throws IOException
    {
        final String JSON = a2q("{'value':10}");
        // by default, we should NOT be allowed to deserialize due to unsafe base type
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack(JSON), WrappedPolymorphicUntyped.class));
        verifyException(e, "Configured");
        verifyException(e, "all subtypes of base type");

        // but may with proper validator
        ObjectMapper customMapper = vpackMapperBuilder()
                .polymorphicTypeValidator(new NumbersAreOkValidator())
                .build();

        WrappedPolymorphicUntyped w = customMapper.readValue(VPackUtils.toVPack(JSON), WrappedPolymorphicUntyped.class);
        assertEquals(Integer.valueOf(10), w.value);

        // but yet again, it is not opening up all types (just as an example)

        InvalidDefinitionException e2 = assertThrows(InvalidDefinitionException.class,
                () -> customMapper.readValue(VPackUtils.toVPack(JSON), WrappedPolymorphicUntypedSer.class));
        verifyException(e2, "Configured");
        verifyException(e2, "all subtypes of base type");
        verifyException(e2, "java.io.Serializable");

    }
}
