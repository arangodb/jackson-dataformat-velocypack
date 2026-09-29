package tools.jackson.databind.deser.merge;

import com.fasterxml.jackson.annotation.JsonMerge;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("serial")
public class CustomMapMerge4922Test
    extends DatabindTestUtil
{
    // [databind#4922]
    interface MyMap4922<K, V> extends Map<K, V> {}

    static class MapImpl<K, V> extends HashMap<K, V> implements MyMap4922<K, V> {}

    static class MergeMap4922 {
        @JsonMerge // either here
        public MyMap4922<Integer, String> map = new MapImpl<>();
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#4922]: Merge for custom maps fails
    @Test
    void testJDKMapperReading() throws Exception {
        MergeMap4922 input = new MergeMap4922();
        input.map.put(3, "ADS");

        String json = VPackUtils.toJson(MAPPER.writer().writeValueAsBytes(input));
        MergeMap4922 merge2 = MAPPER.readValue(VPackUtils.toVPack(json), MergeMap4922.class);
        assertNotNull(merge2);
    }

}
