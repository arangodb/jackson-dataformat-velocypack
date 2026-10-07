package tools.jackson.databind.jsontype;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for [databind#2761] (and [annotations#171]
public class TestMultipleTypeNames extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = vpackMapperBuilder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    // common classes
    static class MultiTypeName { }

    static class A extends MultiTypeName {
        long x;
        public long getX() { return x; }
    }

    static class B extends MultiTypeName {
        float y;
        public float getY() { return y; }
    }

    // data for test 1
    static class WrapperForNamesTest {
        List<BaseForNamesTest> base;
        public List<BaseForNamesTest> getBase() { return base; }
    }

    static class BaseForNamesTest {
        private String type;
        public String getType() { return type; }

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"
        )
        @JsonSubTypes(value = {
                @JsonSubTypes.Type(value = A.class, names = "a"),
                @JsonSubTypes.Type(value = B.class, names = {"b","c"}),
        })
        MultiTypeName data;
        public MultiTypeName getData() { return data; }
    }

    static class WrapperForNameAndNamesTest {
        List<BaseForNameAndNamesTest> base;
        public List<BaseForNameAndNamesTest> getBase() { return base; }
    }

    static class BaseForNameAndNamesTest {
        private String type;
        public String getType() { return type; }

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"
        )
        @JsonSubTypes(value = {
                @JsonSubTypes.Type(value = A.class, name = "a"),
                @JsonSubTypes.Type(value = B.class, names = {"b","c"}),
        })
        MultiTypeName data;
        public MultiTypeName getData() { return data; }
    }

    static class WrapperForNotUniqueNamesTest {
        List<BaseForNotUniqueNamesTest> base;
        public List<BaseForNotUniqueNamesTest> getBase() { return base; }
    }

    static class BaseForNotUniqueNamesTest {
        private String type;
        public String getType() { return type; }

        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type"
        )
        @JsonSubTypes(value = {
                @JsonSubTypes.Type(value = A.class, name = "a"),
                @JsonSubTypes.Type(value = B.class, names = {"b","a"}),
        }, failOnRepeatedNames = true)
        MultiTypeName data;
        public MultiTypeName getData() { return data; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    @Test
    public void testOnlyNames() throws Exception
    {
        String json;
        WrapperForNamesTest w;

        // TC 1 : all KV serialisation
        json = "{\"base\": [{\"type\":\"a\", \"data\": {\"x\": 5}}, {\"type\":\"b\", \"data\": {\"y\": 3.1}}, {\"type\":\"c\", \"data\": {\"y\": 33.8}}]}";
        w = MAPPER.readValue(VPackUtils.toVPack(json), WrapperForNamesTest.class);
        assertNotNull(w);
        assertEquals(3, w.base.size());
        assertInstanceOf(A.class, w.base.get(0).data);
        assertEquals(5l, ((A) w.base.get(0).data).x);
        assertInstanceOf(B.class, w.base.get(1).data);
        assertEquals(3.1f, ((B) w.base.get(1).data).y, 0);
        assertInstanceOf(B.class, w.base.get(2).data);
        assertEquals(33.8f, ((B) w.base.get(2).data).y, 0);
    }

    @Test
    public void testNameAndNames() throws Exception
    {
        String json;
        WrapperForNameAndNamesTest w;

        // TC 1 : all KV serialisation
        json = "{\"base\": [{\"type\":\"a\", \"data\": {\"x\": 5}}, {\"type\":\"b\", \"data\": {\"y\": 3.1}}, {\"type\":\"c\", \"data\": {\"y\": 33.8}}]}";
        w = MAPPER.readValue(VPackUtils.toVPack(json), WrapperForNameAndNamesTest.class);
        assertNotNull(w);
        assertEquals(3, w.base.size());
        assertInstanceOf(A.class, w.base.get(0).data);
        assertEquals(5l, ((A) w.base.get(0).data).x);
        assertInstanceOf(B.class, w.base.get(1).data);
        assertEquals(3.1f, ((B) w.base.get(1).data).y, 0);
        assertInstanceOf(B.class, w.base.get(2).data);
        assertEquals(33.8f, ((B) w.base.get(2).data).y, 0);

    }

    @Test
    public void testNotUniqueNameAndNames() throws Exception
    {
        String json = "{\"base\": [{\"type\":\"a\", \"data\": {\"x\": 5}}, {\"type\":\"b\", \"data\": {\"y\": 3.1}}, {\"type\":\"c\", \"data\": {\"y\": 33.8}}]}";

        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.readValue(VPackUtils.toVPack(json), WrapperForNotUniqueNamesTest.class));
        verifyException(e, "Annotated type [data] got repeated subtype name [a]");
    }

}
