package tools.jackson.databind.jsontype.deftyping;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDefaultForLists
    extends DatabindTestUtil
{
    /*
    /**********************************************************
    /* Helper types
    /**********************************************************
     */

    /**
     * Wrapper bean needed before there is a way to force
     * type of the root value. Long is used because it is a final
     * type, but not one of "untypeable" ones.
     */
    static class ListOfLongs {
        public List<Long> longs;

        public ListOfLongs() { }
        public ListOfLongs(Long ... ls) {
            longs = new ArrayList<Long>();
            for (Long l: ls) {
                longs.add(l);
            }
        }
    }

    static class ListOfNumbers {
        public List<Number> nums;

        public ListOfNumbers() { }
        public ListOfNumbers(Number ... numbers) {
            nums = new ArrayList<Number>();
            for (Number n : numbers) {
                nums.add(n);
            }
        }
    }

    static class ObjectListBean {
        public List<Object> values;
    }

    interface Foo { }

    static class SetBean {
        public Set<String> names;

        public SetBean() { }
        public SetBean(String str) {
            names = new HashSet<String>();
            names.add(str);
        }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper POLY_MAPPER = vpackMapperBuilder()
            .activateDefaultTyping(NoCheckSubTypeValidator.instance)
            .build();

    @Test
    public void testListOfLongs() throws Exception
    {
        ListOfLongs input = new ListOfLongs(1L, 2L, 3L);
        String json = VPackUtils.toJson(POLY_MAPPER.writeValueAsBytes(input));
        assertEquals("{\"longs\":[\"java.util.ArrayList\",[1,2,3]]}", json);
        ListOfLongs output = POLY_MAPPER.readValue(VPackUtils.toVPack(json), ListOfLongs.class);

        assertNotNull(output.longs);
        assertEquals(3, output.longs.size());
        assertEquals(Long.valueOf(1L), output.longs.get(0));
        assertEquals(Long.valueOf(2L), output.longs.get(1));
        assertEquals(Long.valueOf(3L), output.longs.get(2));
    }

    /**
     * Then bit more heterogenous list; also tests mixing of
     * regular scalar types, and non-typed ones (int and double
     * will never have type info added; other numbers will if
     * necessary)
     */
    @Test
    public void testListOfNumbers() throws Exception
    {
        ListOfNumbers input = new ListOfNumbers(Long.valueOf(1L), Integer.valueOf(2), Double.valueOf(3.0));
        String json = VPackUtils.toJson(POLY_MAPPER.writeValueAsBytes(input));
        assertEquals("{\"nums\":[\"java.util.ArrayList\",[[\"java.lang.Long\",1],2,3.0]]}", json);
        ListOfNumbers output = POLY_MAPPER.readValue(VPackUtils.toVPack(json), ListOfNumbers.class);

        assertNotNull(output.nums);
        assertEquals(3, output.nums.size());
        assertEquals(Long.valueOf(1L), output.nums.get(0));
        assertEquals(Integer.valueOf(2), output.nums.get(1));
        assertEquals(Double.valueOf(3.0), output.nums.get(2));
    }

    @Test
    public void testDateTypes() throws Exception
    {
        ObjectListBean input = new ObjectListBean();
        List<Object> inputList = new ArrayList<Object>();
        inputList.add(TimeZone.getTimeZone("EST"));
        inputList.add(Locale.CHINESE);
        input.values = inputList;
        String json = VPackUtils.toJson(POLY_MAPPER.writeValueAsBytes(input));

        ObjectListBean output = POLY_MAPPER.readValue(VPackUtils.toVPack(json), ObjectListBean.class);
        List<Object> outputList = output.values;
        assertEquals(2, outputList.size());
        assertInstanceOf(TimeZone.class, outputList.get(0));
        assertInstanceOf(Locale.class, outputList.get(1));
    }

    @Test
    public void testJackson628() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL)
                .build();
        ArrayList<Foo> data = new ArrayList<Foo>();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(data));
        List<?> output = mapper.readValue(VPackUtils.toVPack(json), List.class);
        assertTrue(output.isEmpty());
    }

    @Test
    public void testJackson667() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new SetBean("abc")));
        SetBean bean = mapper.readValue(VPackUtils.toVPack(json), SetBean.class);
        assertNotNull(bean);
        assertInstanceOf(HashSet.class, bean.names);
    }
}
