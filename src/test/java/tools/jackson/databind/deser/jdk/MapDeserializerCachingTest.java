package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tools.jackson.databind.testutil.DatabindTestUtil.a2q;

// for [databind#1807]
public class MapDeserializerCachingTest
{
    public static class NonAnnotatedMapHolderClass {
        public Map<String, String> data = new TreeMap<String, String>();
    }

    public static class MapHolder {
        @JsonDeserialize(keyUsing = MyKeyDeserializer.class)
        public Map<String, String> data = new TreeMap<String, String>();
    }

    public static class MyKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return key + " (CUSTOM)";
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    @Test
    public void testCachedSerialize() throws IOException {
        ObjectMapper mapper = new VPackMapper();
        String json = a2q("{'data':{'1st':'onedata','2nd':'twodata'}}");

        // Do deserialization with non-annotated map property
        NonAnnotatedMapHolderClass ignored = mapper.readValue(VPackUtils.toVPack(json), NonAnnotatedMapHolderClass.class);
        assertTrue(ignored.data.containsKey("1st"));
        assertTrue(ignored.data.containsKey("2nd"));

//mapper = new VPackMapper();

        MapHolder model2 = mapper.readValue(VPackUtils.toVPack(json), MapHolder.class);
        if (!model2.data.containsKey("1st (CUSTOM)")
            || !model2.data.containsKey("2nd (CUSTOM)")) {
            fail("Not using custom key deserializer for input: "+json+"; resulted in: "+model2.data);
        }
    }
}
