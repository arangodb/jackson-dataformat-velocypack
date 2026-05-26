package tools.jackson.databind.deser;

import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CachingOfDeserTest
{
    // For [databind#735]
    public static class TestMapNoCustom {

        public Map<String, Integer> map;
    }

    public static class TestMapWithCustom {

        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public Map<String, Integer> map;
    }

    public static class TestListWithCustom {
        @JsonDeserialize(contentUsing = CustomDeserializer735.class)
        public List<Integer> list;
    }

    public static class TestListNoCustom {
        public List<Integer> list;
    }

    public static class CustomDeserializer735 extends StdDeserializer<Integer> {
        public CustomDeserializer735() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) {
            return 100 * p.getValueAsInt();
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    final static String MAP_INPUT = "{\"map\":{\"a\":1}}";
    final static String LIST_INPUT = "{\"list\":[1]}";


    // Ok: first, use custom-annotated instance first, then standard
    @Test
    public void testCustomMapCaching1() throws Exception
    {

        ObjectMapper mapper = new VPackMapper();
        TestMapWithCustom mapC = mapper.readValue(VPackUtils.toVPack(MAP_INPUT), TestMapWithCustom.class);
        TestMapNoCustom mapStd = mapper.readValue(VPackUtils.toVPack(MAP_INPUT), TestMapNoCustom.class);

        assertNotNull(mapC.map);
        assertNotNull(mapStd.map);
        assertEquals(Integer.valueOf(100), mapC.map.get("a"));
        assertEquals(Integer.valueOf(1), mapStd.map.get("a"));
    }

    // And then standard first, custom next
    @Test
    public void testCustomMapCaching2() throws Exception
    {
        ObjectMapper mapper = new VPackMapper();
        TestMapNoCustom mapStd = mapper.readValue(VPackUtils.toVPack(MAP_INPUT), TestMapNoCustom.class);
        TestMapWithCustom mapC = mapper.readValue(VPackUtils.toVPack(MAP_INPUT), TestMapWithCustom.class);

        assertNotNull(mapStd.map);
        assertNotNull(mapC.map);
        assertEquals(Integer.valueOf(1), mapStd.map.get("a"));
        assertEquals(Integer.valueOf(100), mapC.map.get("a"));
    }

    // Ok: first, use custom-annotated instance first, then standard
    @Test
    public void testCustomListCaching1() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        TestListWithCustom listC = mapper.readValue(VPackUtils.toVPack(LIST_INPUT), TestListWithCustom.class);
        TestListNoCustom listStd = mapper.readValue(VPackUtils.toVPack(LIST_INPUT), TestListNoCustom.class);

        assertNotNull(listC.list);
        assertNotNull(listStd.list);
        assertEquals(Integer.valueOf(100), listC.list.get(0));
        assertEquals(Integer.valueOf(1), listStd.list.get(0));
    }

    // First custom-annotated, then standard
    @Test
    public void testCustomListCaching2() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        TestListNoCustom listStd = mapper.readValue(VPackUtils.toVPack(LIST_INPUT), TestListNoCustom.class);
        TestListWithCustom listC = mapper.readValue(VPackUtils.toVPack(LIST_INPUT), TestListWithCustom.class);

        assertNotNull(listC.list);
        assertNotNull(listStd.list);
        assertEquals(Integer.valueOf(100), listC.list.get(0));
        assertEquals(Integer.valueOf(1), listStd.list.get(0));
    }
}
