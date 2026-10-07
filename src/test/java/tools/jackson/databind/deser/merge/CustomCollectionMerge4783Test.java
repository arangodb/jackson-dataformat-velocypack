package tools.jackson.databind.deser.merge;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// [databind#4783] Test to verify that JsonMerge also works for custom list
@SuppressWarnings("serial")
public class CustomCollectionMerge4783Test
    extends DatabindTestUtil
{
    static class MyArrayListJDK<T> extends ArrayList<T> { }

    static class MergeListJDK {
        @JsonMerge
        @JsonProperty
        public List<String> values = new MyArrayListJDK<>();
        { values.add("a");}
    }

    interface MyListCustom<T> extends List<T> { }

    static class MyArrayListCustom<T> extends ArrayList<T> implements MyListCustom<T> { }

    static abstract class MyAbstractStringList extends ArrayList<String> {
        MyAbstractStringList() { super(); }
        MyAbstractStringList(int i) { super(); }
    }

    static class MergeCustomStringList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<String> values = new MyArrayListCustom<>();
        { values.add("a"); }
    }

    static class MergeMyCustomLongList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<Long> values = new MyArrayListCustom<>();
        { values.add(1L); }
    }

    static class MergeMyCustomPojoList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<CustomPojo> values = new MyArrayListCustom<>();
        {
            values.add(CustomPojo.create("a", 1));
            values.add(CustomPojo.create("b", 2));
        }
    }

    // And then non-merging case too
    static class NonMergeCustomStringList {
        public MyListCustom<String> values;
    }

    public static class CustomPojo {
        public String name;
        public int age;

        public static CustomPojo create(String name, int age) {
            CustomPojo pojo = new CustomPojo();
            pojo.name = name;
            pojo.age = age;
            return pojo;
        }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    void testJDKMapperReading() throws Exception {
        MergeListJDK result = MAPPER.readValue(VPackUtils.toVPack("{\"values\":[\"x\"]}"), MergeListJDK.class);

        assertEquals(2, result.values.size());
        assertTrue(result.values.contains("x"));
        assertTrue(result.values.contains("a"));
    }

    @Test
    void testCustomMapperReading() throws Exception {
        MergeCustomStringList result = MAPPER.readValue(VPackUtils.toVPack("{\"values\":[\"x\"]}"),
                MergeCustomStringList.class);

        assertEquals(2, result.values.size());
        assertTrue(result.values.contains("x"));
        assertTrue(result.values.contains("a"));
    }

    @Test
    void testCustomMapperReadingLongArrayList() throws Exception {
        MergeMyCustomLongList result = MAPPER.readValue(VPackUtils.toVPack("{\"values\":[7]}"),
                MergeMyCustomLongList.class);

        assertEquals(2, result.values.size());
        assertTrue(result.values.contains(1L));
        assertTrue(result.values.contains(7L));
    }

    @Test
    void testCustomMapperReadingPojoArrayList() throws Exception {
        MergeMyCustomPojoList result = MAPPER.readValue(VPackUtils.toVPack("{\"values\":[{\"name\":\"c\",\"age\":3}]}"),
                MergeMyCustomPojoList.class);

        assertEquals(3, result.values.size());
    }

    // // // And then failure cases

    // Fail can't construct Collection interface unless there's maaping
    @Test
    void failNonMergeInterfaceList() throws Exception {
        try {
            MAPPER.readValue(VPackUtils.toVPack("{\"values\":[\"x\"]}"), NonMergeCustomStringList.class);
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, String.format(
                    "Cannot construct instance of `%s` (no Creators",
                    MyListCustom.class.getName()));
        }
    }

    // Fail can't construct abstract types
    @Test
    void failNonMergeAbstractList() throws Exception {
        try {
            MAPPER.readValue(VPackUtils.toVPack("[]"), MyAbstractStringList.class);
            fail("Should not pass");
        } catch (InvalidDefinitionException e) {
            verifyException(e, String.format(
                    "Cannot construct instance of `%s` (no Creators",
                    MyAbstractStringList.class.getName()));
        }
    }
}
