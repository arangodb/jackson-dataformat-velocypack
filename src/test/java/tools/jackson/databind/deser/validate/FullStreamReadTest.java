package tools.jackson.databind.deser.validate;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for validating {@link tools.jackson.databind.DeserializationFeature#FAIL_ON_TRAILING_TOKENS}.
 */
public class FullStreamReadTest extends DatabindTestUtil
{
    private final static String JSON_OK_ARRAY = " [ 1, 2, 3]    ";
    private final static String JSON_OK_NULL = " null  ";

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

        // and also via "untyped"
        _verifyCollection(mapper.readValue(VPackUtils.toVPack(JSON_OK_ARRAY), List.class));

        // ditto for getting `null` and some other token

        assertTrue(mapper.readTree(VPackUtils.toVPack(JSON_OK_NULL)).isNull());

        assertNull(mapper.readValue(VPackUtils.toVPack(JSON_OK_NULL), Object.class));
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


    }

    @Test
    public void testMapperFailOnTrailingWithNull()
    {
        // some still ok
        JsonNode n = STRICT_MAPPER.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertNotNull(n);
        assertTrue(n.isNull());


        ObjectReader strictWithComments = STRICT_R;
        // NOTE: VPackReadFeature.ALLOW_JAVA_COMMENTS not applicable to VelocyPack
        n = strictWithComments.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertNotNull(n);
        assertTrue(n.isNull());

    }

    @Test
    public void testReaderAcceptTrailing()
    {
        ObjectReader R = MAPPER.reader()
                .without(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

        _verifyArray(R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        ObjectReader rColl = R.forType(List.class);
        _verifyCollection((List<?>)rColl.readValue(VPackUtils.toVPack(JSON_OK_ARRAY)));
    }

    @Test
    public void testReaderFailOnTrailing()
    {
        ObjectReader strictRForList = STRICT_R.forType(List.class);
        _verifyArray(STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_ARRAY)));
        _verifyCollection((List<?>)strictRForList.readValue(VPackUtils.toVPack(JSON_OK_ARRAY)));

    }

    @Test
    public void testReaderFailOnTrailingWithNull()
    {
        ObjectReader strictRForList = STRICT_R.forType(List.class);
        JsonNode n = STRICT_R.readTree(VPackUtils.toVPack(JSON_OK_NULL));
        assertTrue(n.isNull());

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
