package tools.jackson.databind.objectid;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class AlwaysAsReferenceFirstTest extends DatabindTestUtil
{
    // [databind#1255]
    @JsonPropertyOrder({ "bar1", "bar2" })
    static class Foo {

        @JsonIdentityReference(alwaysAsId = true)
        public Bar bar1;

        @JsonIdentityReference
        public Bar bar2;
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
    static class Bar {
        public int value = 3;
    }

    // [databind#1607]

    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="id")
    static class Value1607
    {
        public int value;

        public Value1607() { this(0); }
        public Value1607(int v) {
            value = v;
        }
}
    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    static class Value1607ViaClass
    {
        public int value;

        public Value1607ViaClass() { this(0); }
        public Value1607ViaClass(int v) {
            value = v;
        }
    }

    @JsonPropertyOrder(alphabetic=true)
    static class ReallyAlwaysContainer
    {
        public Value1607ViaClass alwaysClass = new Value1607ViaClass(13);

        @JsonIdentityReference(alwaysAsId=true)
        public Value1607 alwaysProp = new Value1607(13);
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#1255]
    @Test
    public void testIssue1255() throws Exception
    {
        Foo mo = new Foo();
        mo.bar1 = new Bar();
        mo.bar2 = mo.bar1;

        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(mo));

        Foo result = MAPPER.readValue(VPackUtils.toVPack(json), Foo.class);
        assertNotNull(result);
    }

    // [databind#1607]
    @Test
    public void testIssue1607() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new ReallyAlwaysContainer()));
        assertEquals(a2q("{'alwaysClass':1,'alwaysProp':2}"), json);
    }
}
