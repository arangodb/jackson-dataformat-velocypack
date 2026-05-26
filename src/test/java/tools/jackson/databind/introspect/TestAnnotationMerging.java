package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that annotations are shared and merged between members
 * of a property (getter and setter and so on)
 */
public class TestAnnotationMerging extends DatabindTestUtil
{
    static class Wrapper
    {
        protected Object value;

        public Wrapper() { }
        public Wrapper(Object o) { value = o; }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object getValue() { return value; }

        public void setValue(Object o) { value = o; }
    }

    static class SharedName {
        @JsonProperty("x")
        protected int value;

        public SharedName(int v) { value = v; }

        public int getValue() { return value; }
    }

    static class SharedName2
    {
        @JsonProperty("x")
        public int getValue() { return 1; }
        public void setValue(int x) { }
    }

    // Testing to ensure that ctor param and getter can "share" @JsonTypeInfo stuff
    static class TypeWrapper
    {
        protected Object value;

        @JsonCreator
        public TypeWrapper(
                @JsonProperty("value")
                @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS) Object o) {
            value = o;
        }
        public Object getValue() { return value; }
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    @Test
    public void testSharedNames() throws Exception
    {
        ObjectMapper mapper = newVPackMapper();
        assertEquals("{\"x\":6}", VPackUtils.toJson(mapper.writeValueAsBytes(new SharedName(6))));
    }

    @Test
    public void testSharedNamesFromGetterToSetter() throws Exception
    {
        ObjectMapper mapper = newVPackMapper();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new SharedName2()));
        assertEquals("{\"x\":1}", json);
        SharedName2 result = mapper.readValue(VPackUtils.toVPack(json), SharedName2.class);
        assertNotNull(result);
    }

    @Test
    public void testSharedTypeInfo() throws Exception
    {
        final ObjectMapper mapper = vpackMapperBuilder()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Wrapper(13L)));
        Wrapper result = mapper.readValue(VPackUtils.toVPack(json), Wrapper.class);
        assertEquals(Long.class, result.value.getClass());
    }

    @Test
    public void testSharedTypeInfoWithCtor() throws Exception
    {
        final ObjectMapper mapper = vpackMapperBuilder()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new TypeWrapper(13L)));
        TypeWrapper result = mapper.readValue(VPackUtils.toVPack(json), TypeWrapper.class);
        assertEquals(Long.class, result.value.getClass());
    }
}
