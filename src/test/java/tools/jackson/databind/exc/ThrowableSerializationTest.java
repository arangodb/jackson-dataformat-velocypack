package tools.jackson.databind.exc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.*;

/**
 * Unit tests for verifying that simple exceptions can be serialized.
 *
 * NOTE: renamed in 3.1 from "ExceptionSerializationTest"
 */
public class ThrowableSerializationTest
{
    @SuppressWarnings("serial")
    @JsonIgnoreProperties({ "bogus1" })
    static class ExceptionWithIgnoral extends RuntimeException
    {
        public int bogus1 = 3;

        public int bogus2 = 5;

        protected ExceptionWithIgnoral() { }
        public ExceptionWithIgnoral(String msg) {
            super(msg);
        }
    }

    // [databind#1368]
    static class NoSerdeConstructor {
        private String strVal;
        public String getVal() { return strVal; }
        public NoSerdeConstructor( String strVal ) {
            this.strVal = strVal;
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testSimple() throws Exception
    {
        String TEST = "test exception";
        Map<String,Object> result = writeAndMap(MAPPER, new Exception(TEST));
        // JDK 7 has introduced a new property 'suppressed' to Throwable
        Object ob = result.get("suppressed");
        if (ob != null) {
            assertEquals(5, result.size());
        } else {
            assertEquals(4, result.size());
        }

        assertEquals(TEST, result.get("message"));
        assertNull(result.get("cause"));
        assertEquals(TEST, result.get("localizedMessage"));

        // hmmh. what should we get for stack traces?
        Object traces = result.get("stackTrace");
        if (!(traces instanceof List<?>)) {
            fail("Expected a List for exception member 'stackTrace', got: "+traces);
        }
    }

    // to double-check [databind#1413]
    @Test
    public void testSimpleOther() throws Exception
    {
        JsonParser p = MAPPER.createParser(VPackUtils.toVPack("{ }"));
        InvalidFormatException exc = InvalidFormatException.from(p, "Test", getClass(), String.class);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(exc));
        p.close();
        assertNotNull(json);
    }

    // for [databind#877]
    @SuppressWarnings("unchecked")
    @Test
    public void testIgnorals() throws Exception
    {
        ExceptionWithIgnoral input = new ExceptionWithIgnoral("foobar");
        input.initCause(new IOException("surprise!"));

        // First, should ignore anything with class annotations
        String json = VPackUtils.toJson(MAPPER
                .writer()
                .writeValueAsBytes(input));

        Map<String,Object> result = MAPPER.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals("foobar", result.get("message"));

        assertNull(result.get("bogus1"));
        assertNotNull(result.get("bogus2"));

        // and then also remova second property with config overrides
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(ExceptionWithIgnoral.class,
                        o -> o.setIgnorals(JsonIgnoreProperties.Value.forIgnoredProperties("bogus2")))
                .build();
        String json2 = VPackUtils.toJson(mapper
                .writeValueAsBytes(new ExceptionWithIgnoral("foobar")));

        Map<String,Object> result2 = mapper.readValue(VPackUtils.toVPack(json2), Map.class);
        assertNull(result2.get("bogus1"));
        assertNull(result2.get("bogus2"));

        // and try to deserialize as well
        ExceptionWithIgnoral output = mapper.readValue(VPackUtils.toVPack(json2), ExceptionWithIgnoral.class);
        assertNotNull(output);
        assertEquals("foobar", output.getMessage());
    }

    // [databind#1368]
    @Test
    public void testDatabindExceptionSerialization() throws IOException {
        Exception e = null;
        // cant deserialize due to unexpected constructor
        try {
            MAPPER.readValue( VPackUtils.toVPack("{ \"val\": \"foo\" }"), NoSerdeConstructor.class );
            fail("Should not pass");
        } catch (MismatchedInputException e0) {
            verifyException(e0, "cannot deserialize from Object");
            e = e0;
        }
        // but should be able to serialize new exception we got
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(e));
        JsonNode root = MAPPER.readTree(VPackUtils.toVPack(json));
        String msg = root.path("message").asString();
        String MATCH = "cannot construct instance";
        if (!msg.toLowerCase().contains(MATCH)) {
            fail("Exception should contain '"+MATCH+"', does not: '"+msg+"'");
        }
    }

    // [databind#3275]
    @Test
    public void testSerializeWithNamingStrategy() throws IOException {
        final ObjectMapper mapper = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Exception("message!")));
        Map<?,?> map = mapper.readValue(VPackUtils.toVPack(json), Map.class);
        assertEquals(new HashSet<>(Arrays.asList("Cause", "StackTrace", "Message", "Suppressed", "LocalizedMessage")),
                map.keySet());
    }

    // [databind#3244]: StackOverflow for basic JsonProcessingException?
    @Test
    public void testJacksonExceptionSerialization() throws Exception {
        JacksonException e = null;
        try {
            MAPPER.readValue(VPackUtils.toVPack("{ foo "), Map.class);
            fail("Should not pass");
        } catch (JacksonException e0) {
            e = e0;
        }
        String json = VPackUtils.toJson(MAPPER.writer().writeValueAsBytes(e));

        // Could try proper validation, but for now just ensure we won't crash
        assertNotNull(json);
        JsonNode n = MAPPER.readTree(VPackUtils.toVPack(json));
        assertTrue(n.isObject());
    }
}
