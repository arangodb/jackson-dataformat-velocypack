package tools.jackson.databind;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.exc.MismatchedInputException;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.a2q;
import static tools.jackson.databind.testutil.DatabindTestUtil.verifyException;

/**
 * Unit tests dealing with handling of "root element wrapping",
 * including configuration of root name to use.
 */
public class RootNameTest
{
    @JsonRootName("rudy")
    static class Bean {
        public int a = 3;
    }

    @JsonRootName("")
    static class RootBeanWithEmpty {
        public int a = 2;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    @Test
    public void testRootViaMapper() throws Exception
    {
        ObjectMapper mapper = rootMapper();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Bean()));
        assertEquals("{\"rudy\":{\"a\":3}}", json);
        Bean bean = mapper.readValue(VPackUtils.toVPack(json), Bean.class);
        assertNotNull(bean);

        // also same with explicitly "not defined"...
        json = VPackUtils.toJson(mapper.writeValueAsBytes(new RootBeanWithEmpty()));
        assertEquals("{\"RootBeanWithEmpty\":{\"a\":2}}", json);
        RootBeanWithEmpty bean2 = mapper.readValue(VPackUtils.toVPack(json), RootBeanWithEmpty.class);
        assertNotNull(bean2);
        assertEquals(2, bean2.a);
    }

    @Test
    public void testRootViaMapperFails() throws Exception
    {
        final ObjectMapper mapper = rootMapper();
        // First kind of fail, wrong name
        try {
            mapper.readValue(VPackUtils.toVPack(a2q("{'notRudy':{'a':3}}")), Bean.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Root name ('notRudy') does not match expected ('rudy')");
        }

        // second: non-Object
        try {
            mapper.readValue(VPackUtils.toVPack(a2q("[{'rudy':{'a':3}}]")), Bean.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (`JsonToken.START_ARRAY`");
        }

        // Fourth, stuff after wrapped
        try {
            mapper.readValue(VPackUtils.toVPack(a2q("{'rudy':{'a':3}, 'extra':3}")), Bean.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token");
            verifyException(e, "Current token not `JsonToken.END_OBJECT` (to match wrapper");
        }
    }

    @Test
    public void testRootViaReaderFails() throws Exception
    {
        final ObjectReader reader = rootMapper().readerFor(Bean.class);
        // First kind of fail, wrong name
        try {
            reader.readValue(VPackUtils.toVPack(a2q("{'notRudy':{'a':3}}")));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Root name ('notRudy') does not match expected ('rudy')");
        }

        // second: non-Object
        try {
            reader.readValue(VPackUtils.toVPack(a2q("[{'rudy':{'a':3}}]")));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (`JsonToken.START_ARRAY`");
        }

        // Fourth, stuff after wrapped
        try {
            reader.readValue(VPackUtils.toVPack(a2q("{'rudy':{'a':3}, 'extra':3}")));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token");
            verifyException(e, "Current token not `JsonToken.END_OBJECT` (to match wrapper");
        }
    }

    @Test
    public void testRootViaWriterAndReader() throws Exception
    {
        ObjectMapper mapper = rootMapper();
        String json = VPackUtils.toJson(mapper.writer().writeValueAsBytes(new Bean()));
        assertEquals("{\"rudy\":{\"a\":3}}", json);
        Bean bean = mapper.readerFor(Bean.class).readValue(VPackUtils.toVPack(json));
        assertNotNull(bean);
    }

    @Test
    public void testReconfiguringOfWrapping() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        // default: no wrapping
        final Bean input = new Bean();
        String jsonUnwrapped = VPackUtils.toJson(mapper.writeValueAsBytes(input));
        assertEquals("{\"a\":3}", jsonUnwrapped);
        // secondary: wrapping
        String jsonWrapped = VPackUtils.toJson(mapper.writer(SerializationFeature.WRAP_ROOT_VALUE)
            .writeValueAsBytes(input));
        assertEquals("{\"rudy\":{\"a\":3}}", jsonWrapped);

        // and then similarly for readers:
        Bean result = mapper.readValue(VPackUtils.toVPack(jsonUnwrapped), Bean.class);
        assertNotNull(result);
        try { // must not have extra wrapping
            result = mapper.readerFor(Bean.class).with(DeserializationFeature.UNWRAP_ROOT_VALUE)
                .readValue(VPackUtils.toVPack(jsonUnwrapped));
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Root name ('a')");
        }
        // except wrapping may be expected:
        result = mapper.readerFor(Bean.class).with(DeserializationFeature.UNWRAP_ROOT_VALUE)
            .readValue(VPackUtils.toVPack(jsonWrapped));
        assertNotNull(result);
    }

    @Test
    public void testRootUsingExplicitConfig() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        ObjectWriter writer = mapper.writer().withRootName("wrapper");
        String json = VPackUtils.toJson(writer.writeValueAsBytes(new Bean()));
        assertEquals("{\"wrapper\":{\"a\":3}}", json);

        ObjectReader reader = mapper.readerFor(Bean.class).withRootName("wrapper");
        Bean bean = reader.readValue(VPackUtils.toVPack(json));
        assertNotNull(bean);

        // also: verify that we can override SerializationFeature as well:
        ObjectMapper wrapping = rootMapper();
        json = VPackUtils.toJson(wrapping.writer().withRootName("something").writeValueAsBytes(new Bean()));
        assertEquals("{\"something\":{\"a\":3}}", json);
        json = VPackUtils.toJson(wrapping.writer().withRootName("").writeValueAsBytes(new Bean()));
        assertEquals("{\"a\":3}", json);

        // 21-Apr-2015, tatu: Alternative available with 2.6 as well:
        json = VPackUtils.toJson(wrapping.writer().withoutRootName().writeValueAsBytes(new Bean()));
        assertEquals("{\"a\":3}", json);

        bean = wrapping.readerFor(Bean.class).withRootName("").readValue(VPackUtils.toVPack(json));
        assertNotNull(bean);
        assertEquals(3, bean.a);

        bean = wrapping.readerFor(Bean.class).withoutRootName().readValue(VPackUtils.toVPack("{\"a\":4}"));
        assertNotNull(bean);
        assertEquals(4, bean.a);

        // and back to defaults
        bean = wrapping.readerFor(Bean.class).readValue(VPackUtils.toVPack("{\"rudy\":{\"a\":7}}"));
        assertNotNull(bean);
        assertEquals(7, bean.a);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private final ObjectMapper ROOT_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.WRAP_ROOT_VALUE)
            .enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
            .build();

    private ObjectMapper rootMapper() {
        return ROOT_MAPPER;
    }
}
