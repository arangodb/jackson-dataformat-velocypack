package tools.jackson.databind.jsontype.jdk;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScalarTypingTest extends DatabindTestUtil
{
    private static class DynamicWrapper {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public Object value;

        @SuppressWarnings("unused")
        public DynamicWrapper() { }
        public DynamicWrapper(Object v) { value = v; }
    }

    static enum TestEnum { A, B; }

    private static class AbstractWrapper {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public Serializable value;

        @SuppressWarnings("unused")
        public AbstractWrapper() { }
        public AbstractWrapper(Serializable v) { value = v; }
    }

    static class ScalarList {
        @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY)
        public List<Object> values = new ArrayList<Object>();

        public ScalarList() { }

        public ScalarList add(Object v) {
            values.add(v);
            return this;
        }
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    final ObjectMapper MAPPER = vpackMapperBuilder()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator())
            .build();

    /**
     * Ensure that per-property dynamic types work, both for "native" types
     * and others
     */
    @Test
    public void testScalarsWithTyping() throws Exception
    {
        String json;
        DynamicWrapper result;
        ObjectMapper m = MAPPER;

        // first, check "native" types
        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper(Integer.valueOf(3))));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals(Integer.valueOf(3), result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper("abc")));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals("abc", result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper("abc")));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals("abc", result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper(Boolean.TRUE)));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals(Boolean.TRUE, result.value);

        // then verify other scalars
        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper(Long.valueOf(7L))));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals(Long.valueOf(7), result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new DynamicWrapper(TestEnum.B)));
        result = m.readValue(VPackUtils.toVPack(json), DynamicWrapper.class);
        assertEquals(TestEnum.B, result.value);
    }

    @Test
    public void testScalarsViaAbstractType() throws Exception
    {
        ObjectMapper m = MAPPER;
        String json;
        AbstractWrapper result;

        // first, check "native" types
        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper(Integer.valueOf(3))));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals(Integer.valueOf(3), result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper("abc")));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals("abc", result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper("abc")));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals("abc", result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper(Boolean.TRUE)));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals(Boolean.TRUE, result.value);

        // then verify other scalars
        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper(Long.valueOf(7L))));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals(Long.valueOf(7), result.value);

        json = VPackUtils.toJson(m.writeValueAsBytes(new AbstractWrapper(TestEnum.B)));
        result = m.readValue(VPackUtils.toVPack(json), AbstractWrapper.class);
        assertEquals(TestEnum.B, result.value);
    }

    // Test inspired by [databind#1104]
    @Test
    public void testHeterogenousStringScalars() throws Exception
    {
        final UUID NULL_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
        ScalarList input = new ScalarList()
                .add("Test")
                .add(Object.class)
                .add(NULL_UUID)
                ;
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));

        ScalarList result = MAPPER.readValue(VPackUtils.toVPack(json), ScalarList.class);
        assertNotNull(result.values);
        assertEquals(3, result.values.size());
        assertEquals("Test", result.values.get(0));
        assertEquals(Object.class, result.values.get(1));
        assertEquals(NULL_UUID, result.values.get(2));
    }
}
