package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

// [databind#2675]: Void-valued "properties"
public class VoidValuedPropertiesDeserializationTest
{
    static class VoidBean {
        protected Void value;

        public Void getValue() { return null; }

//        public void setValue(Void v) { }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper VOID_MAPPER = vpackMapperBuilder()
            .enable(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

    private final ObjectMapper NO_VOID_MAPPER = vpackMapperBuilder()
            .disable(MapperFeature.ALLOW_VOID_VALUED_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

    @Test
    public void testVoidBeanSerialization() throws Exception
    {
        // with 3.x enabled by default, but may disable
        assertEquals("{\"value\":null}", VPackUtils.toJson(VOID_MAPPER.writeValueAsBytes(new VoidBean())));
        try {
            String json = VPackUtils.toJson(NO_VOID_MAPPER.writeValueAsBytes(new VoidBean()));
            fail("Should not pass; got: "+json);
        } catch (InvalidDefinitionException e) {
            verifyException(e, "no properties discovered");
        }
    }

    @Test
    public void testVoidBeanDeserialization() throws Exception {
        final String DOC = "{\"value\":null}";
        VoidBean result = VOID_MAPPER.readValue(VPackUtils.toVPack(DOC), VoidBean.class);
        assertNotNull(result);
        assertNull(result.getValue());

        // By default (2.x), not enabled:
        try {
            result = NO_VOID_MAPPER.readValue(VPackUtils.toVPack(DOC), VoidBean.class);
            fail("Should not pass");
        } catch (UnrecognizedPropertyException e) {
            verifyException(e, "Unrecognized property \"value\"");
        }
    }
}
