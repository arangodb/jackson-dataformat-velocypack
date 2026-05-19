package tools.jackson.databind.deser.validate;

import org.junit.jupiter.api.Test;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for validating {@link tools.jackson.databind.DeserializationFeature#FAIL_ON_TRAILING_TOKENS}.
 */
public class FullStreamReadTest extends DatabindTestUtil
{
    private final static String JSON_OK_ARRAY = " [ 1, 2, 3]    ";
    private final static String JSON_OK_ARRAY_WITH_COMMENT = JSON_OK_ARRAY + " // stuff ";

    private final static String JSON_FAIL_ARRAY = JSON_OK_ARRAY + " [ ]";

    private final static String JSON_OK_NULL = " null  ";
    private final static String JSON_OK_NULL_WITH_COMMENT = " null /* stuff */ ";
    private final static String JSON_FAIL_NULL = JSON_OK_NULL + " false";

    /*
    /**********************************************************************
    /* Test methods, config
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();
    private final VPackMapper STRICT_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    private final ObjectReader STRICT_R = STRICT_MAPPER.reader();
    
    @Test
    public void testMapperAcceptTrailing()
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();

        assertFalse(mapper.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // by default, should be ok to read, all
        _verifyArray(mapper.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyArray(mapper.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
        _verifyArray(mapper.readTree(VPackUtils.toVPack(JSON_FAIL_ARRAY)));

        // and also via "untyped"
        _verifyCollection(mapper.readValue(VPackUtils.toVPack(JSON_OK_ARRAY), List.class));
        _verifyCollection(mapper.readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT), List.class));
        _verifyCollection(mapper.readValue(VPackUtils.toVPack(JSON_FAIL_ARRAY), List.class));

        // ditto for getting `null` and some other token

        assertTrue(mapper.readTree(VPackUtils.toVPack(JSON_OK_NULL)).isNull());
        assertTrue(mapper.readTree(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT)).isNull());
        assertTrue(mapper.readTree(VPackUtils.toVPack(JSON_FAIL_NULL)).isNull());

        assertNull(mapper.readValue(VPackUtils.toVPack(JSON_OK_NULL), Object.class));
        assertNull(mapper.readValue(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT), Object.class));
        assertNull(mapper.readValue(VPackUtils.toVPack(JSON_FAIL_NULL), Object.class));
    }

    @Test
    public void testMapperFailOnTrailing()
    {
        // but things change if we enforce checks
        final VPackMapper strict = VPackMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .build();
        assertTrue(strict.isEnabled(DeserializationFeature.FAIL_ON_TRAILING_TOKENS));

        // some still ok
        _verifyArray(strict.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyCollection(strict.readValue(VPackUtils.toVPack(JSON_OK_ARRAY), List.class));

        // but if real content exists, will fail
        try {
            strict.readTree(VPackUtils.toVPack(JSON_FAIL_ARRAY));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.START_ARRAY`)");
            verifyException(e, "value (bound as `tools.jackson.databind.JsonNode`)");
        }

        try {
            strict.readValue(VPackUtils.toVPack(JSON_FAIL_ARRAY), List.class);
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.START_ARRAY`)");
            verifyException(e, "value (bound as `java.util.List`)");
        }

        // others fail conditionally: will fail on comments unless enabled

        try {
            strict.readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT), List.class);
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            strict.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        ObjectReader strictWithComments = strict.reader();
        // NOTE: VPackReadFeature.ALLOW_JAVA_COMMENTS not applicable to VelocyPack
        _verifyArray(strictWithComments.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
        _verifyCollection((List<?>) strictWithComments.forType(List.class)
                .readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
    }

    @Test
    public void testMapperFailOnTrailingWithNull()
    {
        // some still ok
        JsonNode n = STRICT_MAPPER.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertNotNull(n);
        assertTrue(n.isNull());

        // but if real content exists, will fail
        try {
            STRICT_MAPPER.readTree(VPackUtils.toVPack(JSON_FAIL_NULL));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.VALUE_FALSE`)");
            verifyException(e, "value (bound as `tools.jackson.databind.JsonNode`)");
        }

        try {
            STRICT_MAPPER.readValue(VPackUtils.toVPack(JSON_FAIL_NULL), List.class);
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.VALUE_FALSE`)");
            verifyException(e, "value (bound as `java.util.List`)");
        }

        // others fail conditionally: will fail on comments unless enabled

        try {
            STRICT_MAPPER.readValue(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT), Object.class);
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            STRICT_MAPPER.readTree(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        ObjectReader strictWithComments = STRICT_R;
        // NOTE: VPackReadFeature.ALLOW_JAVA_COMMENTS not applicable to VelocyPack
        n = strictWithComments.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertNotNull(n);
        assertTrue(n.isNull());

        Object ob = strictWithComments.forType(List.class)
                .readValue(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT));
        assertNull(ob);
    }

    @Test
    public void testReaderAcceptTrailing()
    {
        ObjectReader R = MAPPER.reader()
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        _verifyArray(R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyArray(R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
        _verifyArray(R.readTree(VPackUtils.toVPack(JSON_FAIL_ARRAY)));
        ObjectReader rColl = R.forType(List.class);
        _verifyCollection((List<?>)rColl.readValue(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyCollection((List<?>)rColl.readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
        _verifyCollection((List<?>)rColl.readValue(VPackUtils.toVPack(JSON_FAIL_ARRAY)));
    }

    @Test
    public void testReaderFailOnTrailing()
    {
        ObjectReader strictRForList = STRICT_R.forType(List.class);
        _verifyArray(STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyCollection((List<?>)strictRForList.readValue(VPackUtils.toVPack(JSON_OK_ARRAY)));

        // Will fail hard if there is a trailing token
        try {
            strictRForList.readValue(VPackUtils.toVPack(JSON_FAIL_ARRAY));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.START_ARRAY`)");
            verifyException(e, "value (bound as `java.util.List`)");
        }
        try {
            STRICT_R.readTree(VPackUtils.toVPack(JSON_FAIL_ARRAY));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.START_ARRAY`)");
            verifyException(e, "value (bound as `tools.jackson.databind.JsonNode`)");
        }

        // ... also verify that same happens with "value to update"
        try {
            STRICT_R.withValueToUpdate(new ArrayList<Object>())
                .readValue(VPackUtils.toVPack(JSON_FAIL_ARRAY));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.START_ARRAY`)");
            verifyException(e, "value (bound as `java.util.ArrayList`)");
        }

        // others conditionally: will fail on comments unless enabled

        try {
            strictRForList.readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        // but works if comments enabled etc

        // NOTE: VPackReadFeature.ALLOW_JAVA_COMMENTS not applicable to VelocyPack
        ObjectReader strictRWithComments = STRICT_R; // Using base reader since ALLOW_JAVA_COMMENTS not available
        _verifyCollection((List<?>)strictRWithComments.forType(List.class).readValue(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
        _verifyArray(strictRWithComments.readTree(VPackUtils.toVPack(JSON_OK_ARRAY_WITH_COMMENT)));
    }

    @Test
    public void testReaderFailOnTrailingWithNull()
    {
        ObjectReader strictRForList = STRICT_R.forType(List.class);
        JsonNode n = STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertTrue(n.isNull());

        // Will fail hard if there is a trailing token
        try {
            strictRForList.readValue(VPackUtils.toVPack(JSON_FAIL_NULL));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.VALUE_FALSE`)");
            verifyException(e, "value (bound as `java.util.List`)");
        }

        try {
            STRICT_R.readTree(VPackUtils.toVPack(JSON_FAIL_NULL));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.VALUE_FALSE`)");
            verifyException(e, "value (bound as `tools.jackson.databind.JsonNode`)");
        }

        // others conditionally: will fail on comments unless enabled

        try {
            strictRForList.readValue(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }
        try {
            STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT));
            fail("Should not have passed");
        } catch (StreamReadException e) {
            verifyException(e, "Unexpected character");
            verifyException(e, "maybe a (non-standard) comment");
        }

        // but works if comments enabled etc
        // NOTE: VPackReadFeature.ALLOW_JAVA_COMMENTS not applicable to VelocyPack
        ObjectReader strictRWithComments = STRICT_R; // Using base reader since ALLOW_JAVA_COMMENTS not available
        Object ob = strictRWithComments.forType(List.class).readValue(VPackUtils.toVPack(JSON_OK_NULL_WITH_COMMENT));
        assertNull(ob);
    }

    @Test
    public void testReaderFailOnTrailingWithUpdateValue()
    {
        ObjectReader r = STRICT_R.withValueToUpdate(new HashMap<String,Object>());
        try {
            r.readTree(VPackUtils.toVPack("{ } false"));
            fail("Should not have passed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Trailing token (`JsonToken.VALUE_FALSE`)");
            verifyException(e, "value (bound as `java.util.HashMap`)");
        }

    }
    
    private void _verifyArray(JsonNode n)
    {
        assertTrue(n.isArray());
        assertEquals(3, n.size());
    }

    private void _verifyCollection(List<?> coll)
    {
        assertEquals(3, coll.size());
        assertEquals(Integer.valueOf(1), coll.get(0));
        assertEquals(Integer.valueOf(2), coll.get(1));
        assertEquals(Integer.valueOf(3), coll.get(2));
    }
}
