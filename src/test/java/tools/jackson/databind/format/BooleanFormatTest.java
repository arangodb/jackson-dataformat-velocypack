package tools.jackson.databind.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#1480]
public class BooleanFormatTest extends DatabindTestUtil
{
    @JsonPropertyOrder({ "b1", "b2", "b3" })
    static class BeanWithBoolean
    {
        @JsonFormat(shape=JsonFormat.Shape.NUMBER)
        public boolean b1;

        @JsonFormat(shape=JsonFormat.Shape.NUMBER)
        public Boolean b2;

        public boolean b3;

        public BeanWithBoolean() { }
        public BeanWithBoolean(boolean b1, Boolean b2, boolean b3) {
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
        }
    }

    /**
     * Simple wrapper around boolean types, usually to test value
     * conversions or wrapping
     */
    protected static class BooleanWrapper {
        public Boolean b;

        public BooleanWrapper() { }
        public BooleanWrapper(Boolean value) { b = value; }
    }

    // [databind#3080]
    protected static class PrimitiveBooleanWrapper {
        public boolean b;

        public PrimitiveBooleanWrapper() { }
        public PrimitiveBooleanWrapper(boolean value) { b = value; }
    }

    static class AltBoolean extends BooleanWrapper
    {
        public AltBoolean() { }
        public AltBoolean(Boolean b) { super(b); }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final static ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testShapeViaDefaults() throws Exception
    {
        assertEquals(a2q("{'b':true}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new BooleanWrapper(true))));
        ObjectMapper m = vpackMapperBuilder()
                .withConfigOverride(Boolean.class,
                        cfg -> cfg.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER)
        )).build();
        assertEquals(a2q("{'b':1}"),
                VPackUtils.toJson(m.writeValueAsBytes(new BooleanWrapper(true))));

        m = vpackMapperBuilder()
                .withConfigOverride(Boolean.class,
                        cfg -> cfg.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING)
        )).build();
        assertEquals(a2q("{'b':'true'}"),
                VPackUtils.toJson(m.writeValueAsBytes(new BooleanWrapper(true))));
    }

    // [databind#3080]
    @Test
    public void testPrimitiveShapeViaDefaults() throws Exception
    {
        assertEquals(a2q("{'b':true}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new PrimitiveBooleanWrapper(true))));
        ObjectMapper m = vpackMapperBuilder()
                .withConfigOverride(Boolean.TYPE, cfg ->
                    cfg.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER))
        ).build();
        assertEquals(a2q("{'b':1}"),
                VPackUtils.toJson(m.writeValueAsBytes(new PrimitiveBooleanWrapper(true))));

        m = vpackMapperBuilder()
                .withConfigOverride(Boolean.TYPE, cfg ->
                    cfg.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.STRING))
        ).build();
        assertEquals(a2q("{'b':'true'}"),
                VPackUtils.toJson(m.writeValueAsBytes(new PrimitiveBooleanWrapper(true))));
    }

    @Test
    public void testShapeOnProperty() throws Exception
    {
        assertEquals(a2q("{'b1':1,'b2':0,'b3':true}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new BeanWithBoolean(true, false, true))));
    }
}
