package tools.jackson.databind.deser.dos;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.testutil.DatabindTestUtil.newVPackMapper;

/**
 * Tests for <a href="https://github.com/FasterXML/jackson-core/issues/863">databind#863</a>"
 */
public class StreamReadStringConstraintsTest
{
    final static class StringWrapper
    {
        String string;

        StringWrapper() { }

        StringWrapper(String string) { this.string = string; }

        void setString(String string) {
            this.string = string;
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final static int TOO_LONG_STRING_VALUE = StreamReadConstraints.DEFAULT_MAX_STRING_LEN + 100;
    
    private final ObjectMapper MAPPER = newVPackMapper();

    private ObjectMapper newVPackMapperWithUnlimitedStringSizeSupport() {
        VPackFactory jsonFactory = VPackFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder().maxStringLength(Integer.MAX_VALUE).build())
                .build();
        return VPackMapper.builder(jsonFactory).build();
    }

    @Test
    public void testBigString() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack(generateJson("string", TOO_LONG_STRING_VALUE)), StringWrapper.class);
            fail("expected DatabindException");
        } catch (StreamConstraintsException e) {
            final String message = e.getMessage();
            assertTrue(message.startsWith("String value length"), "unexpected exception message: " + message);
            assertTrue(message.contains("exceeds the maximum allowed ("), "unexpected exception message: " + message);
        }
    }

    @Test
    public void testBiggerString() throws Exception
    {
        try {
            MAPPER.readValue(VPackUtils.toVPack(generateJson("string", TOO_LONG_STRING_VALUE)), StringWrapper.class);
            fail("expected JsonMappingException");
        } catch (StreamConstraintsException e) {
            final String message = e.getMessage();
            // this test fails when the TextBuffer is being resized, so we don't yet know just how big the string is
            // so best not to assert that the String length value in the message is the full 6000000 value
            assertTrue(message.startsWith("String value length"), "unexpected exception message: " + message);
            assertTrue(message.contains("exceeds the maximum allowed ("), "unexpected exception message: " + message);
        }
    }

    @Test
    public void testUnlimitedString() throws Exception
    {
        final int len = TOO_LONG_STRING_VALUE;
        StringWrapper sw = newVPackMapperWithUnlimitedStringSizeSupport()
                .readValue(VPackUtils.toVPack(generateJson("string", len)), StringWrapper.class);
        assertEquals(len, sw.string.length());
    }


    private String generateJson(final String fieldName, final int len) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{\"")
                .append(fieldName)
                .append("\": \"");
        for (int i = 0; i < len; i++) {
            sb.append('a');
        }
        sb.append("\"}");
        return sb.toString();
    }
}
