package tools.jackson.databind.jsontype.jdk;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * For [databind#292]
 */
@SuppressWarnings("serial")
public class AbstractContainerTypingTest extends DatabindTestUtil
{
    // Polymorphic abstract Map type, wrapper

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = MapWrapper.class, name = "wrapper"),
    })
    static class MapWrapper {
        public IDataValueMap map = new DataValueMap();     // This does NOT work
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="_type_")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DataValueMap.class,  name = "DataValueMap")
    })
    public interface IDataValueMap extends Map<String, String> { }

    static class DataValueMap extends HashMap<String, String> implements IDataValueMap { }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = ListWrapper.class, name = "wrapper"),
    })
    static class ListWrapper {
        public IDataValueList list = new DataValueList();     // This does NOT work
    }

    @JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = DataValueList.class,  name = "list")
    })
    public interface IDataValueList extends List<String> { }

    static class DataValueList extends LinkedList<String> implements IDataValueList { }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void testAbstractLists() throws Exception
    {
        ListWrapper w = new ListWrapper();
        w.list.add("x");

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(w));
        Object o = MAPPER.readValue(VPackUtils.toVPack(json), ListWrapper.class);
        assertEquals(ListWrapper.class, o.getClass());
        ListWrapper out = (ListWrapper) o;
        assertNotNull(out.list);
        assertEquals(1, out.list.size());
        assertEquals("x", out.list.get(0));
    }

    @Test
    public void testAbstractMaps() throws Exception
    {
        MapWrapper w = new MapWrapper();
        w.map.put("key1", "name1");

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(w));
        Object o = MAPPER.readValue(VPackUtils.toVPack(json), MapWrapper.class);
        assertEquals(MapWrapper.class, o.getClass());
        MapWrapper out = (MapWrapper) o;
        assertEquals(1, out.map.size());
   }
}
