package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing to verify that {@link JsonTypeInfo} works
 * for properties as well as types.
 */
@SuppressWarnings("serial")
public class TestPropertyTypeInfo extends DatabindTestUtil
{
    protected static class BooleanValue {
        public Boolean b;

        @JsonCreator
        public BooleanValue(Boolean value) { b = value; }

        @JsonValue public Boolean value() { return b; }
    }

    static class FieldWrapperBean
    {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object value;

        public FieldWrapperBean() { }
        public FieldWrapperBean(Object o) { value = o; }
    }

    static class FieldWrapperBeanList extends ArrayList<FieldWrapperBean> { }
    static class FieldWrapperBeanMap extends HashMap<String,FieldWrapperBean> { }
    static class FieldWrapperBeanArray {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public FieldWrapperBean[] beans;

        public FieldWrapperBeanArray() { }
        public FieldWrapperBeanArray(FieldWrapperBean[] beans) { this.beans = beans; }
    }

    static class MethodWrapperBean
    {
        protected Object value;

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object getValue() { return value; }

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(Object v) { value = v; }

        public MethodWrapperBean() { }
        public MethodWrapperBean(Object o) { value = o; }
    }

    static class MethodWrapperBeanList extends ArrayList<MethodWrapperBean> { }
    static class MethodWrapperBeanMap extends HashMap<String,MethodWrapperBean> { }
    static class MethodWrapperBeanArray {
        protected MethodWrapperBean[] beans;

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public MethodWrapperBean[] getValue() { return beans; }

        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_ARRAY)
        public void setValue(MethodWrapperBean[] v) { beans = v; }

        public MethodWrapperBeanArray() { }
        public MethodWrapperBeanArray(MethodWrapperBean[] beans) { this.beans = beans; }
    }

    static class OtherBean {
        public int x = 1, y = 1;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator())
            .build();

    @Test
    public void testSimpleField() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FieldWrapperBean(new StringWrapper("foo"))));
//System.out.println("JSON/field+object == "+json);
        FieldWrapperBean bean = MAPPER.readValue(VPackUtils.toVPack(json), FieldWrapperBean.class);
        assertNotNull(bean.value);
        assertEquals(StringWrapper.class, bean.value.getClass());
        assertEquals(((StringWrapper) bean.value).str, "foo");
    }

    @Test
    public void testSimpleMethod() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FieldWrapperBean(new IntWrapper(37))));
//System.out.println("JSON/method+object == "+json);
        FieldWrapperBean bean = MAPPER.readValue(VPackUtils.toVPack(json), FieldWrapperBean.class);
        assertNotNull(bean.value);
        assertEquals(IntWrapper.class, bean.value.getClass());
        assertEquals(((IntWrapper) bean.value).i, 37);
    }

    @Test
    public void testSimpleListField() throws Exception
    {
        FieldWrapperBeanList list = new FieldWrapperBeanList();
        list.add(new FieldWrapperBean(new OtherBean()));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(list));
//System.out.println("JSON/field+list == "+json);
        FieldWrapperBeanList result = MAPPER.readValue(VPackUtils.toVPack(json), FieldWrapperBeanList.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        FieldWrapperBean bean = list.get(0);
        assertEquals(OtherBean.class, bean.value.getClass());
        assertEquals(((OtherBean) bean.value).x, 1);
        assertEquals(((OtherBean) bean.value).y, 1);
    }

    @Test
    public void testSimpleListMethod() throws Exception
    {
        MethodWrapperBeanList list = new MethodWrapperBeanList();
        list.add(new MethodWrapperBean(new BooleanValue(true)));
        list.add(new MethodWrapperBean(new StringWrapper("x")));
        list.add(new MethodWrapperBean(new OtherBean()));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(list));
        MethodWrapperBeanList result = MAPPER.readValue(VPackUtils.toVPack(json), MethodWrapperBeanList.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        MethodWrapperBean bean = result.get(0);
        assertEquals(BooleanValue.class, bean.value.getClass());
        assertEquals(((BooleanValue) bean.value).b, Boolean.TRUE);
        bean = result.get(1);
        assertEquals(StringWrapper.class, bean.value.getClass());
        assertEquals(((StringWrapper) bean.value).str, "x");
        bean = result.get(2);
        assertEquals(OtherBean.class, bean.value.getClass());
    }

    @Test
    public void testSimpleArrayField() throws Exception
    {
        FieldWrapperBeanArray array = new FieldWrapperBeanArray(new
                FieldWrapperBean[] { new FieldWrapperBean(new BooleanValue(true)) });
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(array));
        FieldWrapperBeanArray result = MAPPER.readValue(VPackUtils.toVPack(json), FieldWrapperBeanArray.class);
        assertNotNull(result);
        FieldWrapperBean[] beans = result.beans;
        assertEquals(1, beans.length);
        FieldWrapperBean bean = beans[0];
        assertEquals(BooleanValue.class, bean.value.getClass());
        assertEquals(((BooleanValue) bean.value).b, Boolean.TRUE);
    }

    @Test
    public void testSimpleArrayMethod() throws Exception
    {
        MethodWrapperBeanArray array = new MethodWrapperBeanArray(new
                MethodWrapperBean[] { new MethodWrapperBean(new StringWrapper("A")) });
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(array));
        MethodWrapperBeanArray result = MAPPER.readValue(VPackUtils.toVPack(json), MethodWrapperBeanArray.class);
        assertNotNull(result);
        MethodWrapperBean[] beans = result.beans;
        assertEquals(1, beans.length);
        MethodWrapperBean bean = beans[0];
        assertEquals(StringWrapper.class, bean.value.getClass());
        assertEquals(((StringWrapper) bean.value).str, "A");
    }

    @Test
    public void testSimpleMapField() throws Exception
    {
        FieldWrapperBeanMap map = new FieldWrapperBeanMap();
        map.put("foop", new FieldWrapperBean(new IntWrapper(13)));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(map));
        FieldWrapperBeanMap result = MAPPER.readValue(VPackUtils.toVPack(json), FieldWrapperBeanMap.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        FieldWrapperBean bean = result.get("foop");
        assertNotNull(bean);
        Object ob = bean.value;
        assertEquals(IntWrapper.class, ob.getClass());
        assertEquals(((IntWrapper) ob).i, 13);
    }

    @Test
    public void testSimpleMapMethod() throws Exception
    {
        MethodWrapperBeanMap map = new MethodWrapperBeanMap();
        map.put("xyz", new MethodWrapperBean(new BooleanValue(true)));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(map));
        MethodWrapperBeanMap result = MAPPER.readValue(VPackUtils.toVPack(json), MethodWrapperBeanMap.class);
        assertNotNull(result);
        assertEquals(1, result.size());
        MethodWrapperBean bean = result.get("xyz");
        assertNotNull(bean);
        Object ob = bean.value;
        assertEquals(BooleanValue.class, ob.getClass());
        assertEquals(((BooleanValue) ob).b, Boolean.TRUE);
    }
}
