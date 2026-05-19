package tools.jackson.databind.jsontype.deftyping;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify that Java/JSON scalar values (non-structured values)
 * are handled properly with respect to additional type information.
 */
public class TestDefaultForScalars
    extends DatabindTestUtil
{
    static class Jackson417Bean {
        public String foo = "bar";
        public java.io.Serializable bar = Integer.valueOf(13);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    static class Data {
        public long key;
    }

    // Basic `ObjectWrapper` from base uses delegating ctor, won't work well; should
    // figure out why, but until then we'll use separate impl
    protected static class ObjectWrapperForPoly {
        Object object;

        protected ObjectWrapperForPoly() { }
        public ObjectWrapperForPoly(final Object o) {
            object = o;
        }
        public Object getObject() { return object; }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper DEFAULT_TYPING_MAPPER = vpackMapperBuilder()
                    .activateDefaultTyping(NoCheckSubTypeValidator.instance)
                    .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();

    /**
     * Unit test to verify that limited number of core types do NOT include
     * type information, even if declared as Object. This is only done for types
     * that JSON scalar values natively map to: String, Integer and Boolean (and
     * nulls never have type information)
     */
    @Test
    public void testNumericScalars() throws Exception
    {
        // no typing for Integer, Double, yes for others
        assertEquals("[123]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Integer.valueOf(123) })));
        assertEquals("[[\"java.lang.Long\",37]]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Long.valueOf(37) })));
        assertEquals("[0.25]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Double.valueOf(0.25) })));
        assertEquals("[[\"java.lang.Float\",0.5]]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { Float.valueOf(0.5f) })));
    }

    @Test
    public void testDateScalars() throws Exception
    {
        long ts = 12345678L;
        assertEquals("[[\"java.util.Date\","+ts+"]]",
                VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { new Date(ts) })));

        // Calendar is trickier... hmmh. Need to ensure round-tripping
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        String json = VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { c }));
        assertEquals("[[\""+c.getClass().getName()+"\","+ts+"]]", json);
        // and let's make sure it also comes back same way:
        Object[] result = DEFAULT_TYPING_MAPPER.readValue(VPackUtils.toVPack(json), Object[].class);
        assertEquals(1, result.length);
        assertInstanceOf(Calendar.class, result[0]);
        assertEquals(ts, ((Calendar) result[0]).getTimeInMillis());
    }

    @Test
    public void testMiscScalars() throws Exception
    {
        // no typing for Strings, booleans
        assertEquals("[\"abc\"]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Object[] { "abc" })));
        assertEquals("[true,null,false]", VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(new Boolean[] { true, null, false })));
    }

    /**
     * Test for verifying that contents of "untyped" homogenous arrays are properly
     * handled,
     */
    @Test
    public void testScalarArrays() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.JAVA_LANG_OBJECT)
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        Object[] input = new Object[] {
                "abc", new Date(1234567), null, Integer.valueOf(456)
        };
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(input));
        assertEquals("[\"abc\",[\"java.util.Date\",1234567],null,456]", json);

        // and should deserialize back as well:
        Object[] output = mapper.readValue(VPackUtils.toVPack(json), Object[].class);
        assertArrayEquals(input, output);
    }

    // Loosely scalar
    @Test
    public void test417() throws Exception
    {
        Jackson417Bean input = new Jackson417Bean();
        String json = VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(input));
        Jackson417Bean result = DEFAULT_TYPING_MAPPER.readValue(VPackUtils.toVPack(json), Jackson417Bean.class);
        assertEquals(input.foo, result.foo);
        assertEquals(input.bar, result.bar);
    }

    // [databind#1395]: prevent attempts at including type info for primitives
    @Test
    public void testDefaultTypingWithLong() throws Exception
    {
        Data data = new Data();
        data.key = 1L;
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("longInMap", 2L);
        mapData.put("longAsField", data);

        // Configure Jackson to preserve types
        StdTypeResolverBuilder resolver = new StdTypeResolverBuilder(JsonTypeInfo.Id.CLASS,
                JsonTypeInfo.As.PROPERTY, "__t", null);
        ObjectMapper mapper = vpackMapperBuilder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .setDefaultTyping(resolver)
                .build();

        // Serialize
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(mapData));

        // Deserialize
        Map<?,?> result = mapper.readValue(VPackUtils.toVPack(json), Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // [databind#2236]: do need type info for NaN
    @Test
    public void testDefaultTypingWithNaN() throws Exception
    {
        final ObjectWrapperForPoly INPUT = new ObjectWrapperForPoly(Double.POSITIVE_INFINITY);
        final String json = VPackUtils.toJson(DEFAULT_TYPING_MAPPER.writeValueAsBytes(INPUT));
        final ObjectWrapperForPoly result = DEFAULT_TYPING_MAPPER.readValue(VPackUtils.toVPack(json), ObjectWrapperForPoly.class);
        assertEquals(Double.class, result.getObject().getClass());
        assertEquals(INPUT.getObject().toString(), result.getObject().toString());
        assertTrue(((Double) result.getObject()).isInfinite());
    }
}
