package tools.jackson.databind.mixins;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestMixinDeserForClass
    extends DatabindTestUtil
{
    static class BaseClass
    {
        /* property that is always found; but has lower priority than
         * setter method if both found
         */
        @JsonProperty
        public String a;

        // setter that may or may not be auto-detected
        public void setA(String v) { a = "XXX"+v; }
    }

    @JsonAutoDetect(setterVisibility=Visibility.ANY, fieldVisibility=Visibility.ANY)
    static class LeafClass
        extends BaseClass { }

    @JsonAutoDetect(setterVisibility=Visibility.NONE, fieldVisibility=Visibility.NONE)
    interface MixIn { }

    // [databind#1990]
    public interface HashCodeMixIn {
        @Override
        @JsonProperty
        public int hashCode();
    }

    public class Bean1990WithoutHashCode {
    }

    public class Bean1990WithHashCode {
        @Override
        public int hashCode() { return 13; }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    @Test
    public void testClassMixInsTopLevel() throws IOException
    {
        ObjectMapper m = new VPackMapper();
        // First: test default behavior: should use setter
        LeafClass result = m.readValue(VPackUtils.toVPack("{\"a\":\"value\"}"), LeafClass.class);
        assertEquals("XXXvalue", result.a);

        // Then with leaf-level mix-in; without (method) auto-detect,
        // should use field
        m = vpackMapperBuilder()
                .addMixIn(LeafClass.class, MixIn.class)
                .build();
        result = m.readValue(VPackUtils.toVPack("{\"a\":\"value\"}"), LeafClass.class);
        assertEquals("value", result.a);
    }

    // and then a test for mid-level mixin; should have no effect
    // when deserializing leaf (but will if deserializing base class)
    @Test
    public void testClassMixInsMidLevel() throws IOException
    {
        ObjectMapper m = vpackMapperBuilder()
                .addMixIn(BaseClass.class, MixIn.class)
                .build();
        {
            BaseClass result = m.readValue(VPackUtils.toVPack("{\"a\":\"value\"}"), BaseClass.class);
            assertEquals("value", result.a);
        }

        // whereas with leaf class, reverts to default
        {
            LeafClass result = m.readValue(VPackUtils.toVPack("{\"a\":\"value\"}"), LeafClass.class);
            assertEquals("XXXvalue", result.a);
        }
    }

    /* Also: when mix-in attached to Object.class, will work, if
     * visible (similar to mid-level, basically)
     */
    @Test
    public void testClassMixInsForObjectClass() throws IOException
    {
        ObjectMapper m = vpackMapperBuilder()
                .addMixIn(Object.class, MixIn.class)
                .build();
        // will be seen for BaseClass
        {
            BaseClass result = m.readValue(VPackUtils.toVPack("{\"a\":\"\"}"), BaseClass.class);
            assertEquals("", result.a);
        }

        // but LeafClass still overrides
        {
            LeafClass result = m.readValue(VPackUtils.toVPack("{\"a\":\"\"}"), LeafClass.class);
            assertEquals("XXX", result.a);
        }
    }

    // [databind#1990]: can apply mix-in to `Object#hashCode()` to force serialization
    @Test
    public void testHashCodeViaObject() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .addMixIn(Object.class, HashCodeMixIn.class)
                .build();

        // First, with something that overrides hashCode()
        assertEquals( "{\"hashCode\":13}",
                VPackUtils.toJson(mapper.writeValueAsBytes(new Bean1990WithHashCode())));

        // and then special case of accessing Object#hashCode()
        String prefix = "{\"hashCode\":";
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Bean1990WithoutHashCode()));
        if (!json.startsWith(prefix)) {
            fail("Should start with ["+prefix+"], does not: ["+json+"]");
        }
    }
}
