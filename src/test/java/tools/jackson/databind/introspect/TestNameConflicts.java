package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

public class TestNameConflicts extends DatabindTestUtil
{
    @JsonAutoDetect
    (fieldVisibility= JsonAutoDetect.Visibility.NONE,getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, isGetterVisibility= JsonAutoDetect.Visibility.NONE)
    static class CoreBean158 {
        protected String bar = "x";

        @JsonProperty
        public String getBar() {
            return bar;
        }

        @JsonProperty
        public void setBar(String bar) {
            this.bar = bar;
        }

        public void setBar(java.io.Serializable bar) {
            this.bar = bar.toString();
        }
    }

    static class Bean193
    {
        @JsonProperty("val1")
        private int x;
        @JsonIgnore
        private int value2;

        public Bean193(@JsonProperty("val1")int value1,
                    @JsonProperty("val2")int value2)
        {
            this.x = value1;
            this.value2 = value2;
        }

        @JsonProperty("val2")
        int x()
        {
            return value2;
        }
    }

    /* We should only report an exception for cases where there is
     * real ambiguity as to how to rename things; but not when everything
     * has been explicitly defined
     */
    // [Issue#327]
    @JsonPropertyOrder({ "prop1", "prop2" })
    static class BogusConflictBean
    {
        @JsonProperty("prop1")
        public int a = 2;

        @JsonProperty("prop2")
        public int getA() {
            return 1;
        }
    }

    // Bean that should not have conflicts, but could be problematic
    static class MultipleTheoreticalGetters
    {
        public MultipleTheoreticalGetters() { }

        public MultipleTheoreticalGetters(@JsonProperty("a") int foo) {
            ;
        }

        @JsonProperty
        public int getA() { return 3; }

        public int a() { return 5; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // [Issue#193]
    @Test
    public void testIssue193() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Bean193(1, 2)));
        assertNotNull(json);
    }

    // [Issue#327]
    @Test
    public void testNonConflict() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new BogusConflictBean()));
        assertEquals(a2q("{'prop1':2,'prop2':1}"), json);
    }

    @Test
    public void testHypotheticalGetters() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new MultipleTheoreticalGetters()));
        assertEquals(a2q("{'a':3}"), json);
    }

    // for [jackson-core#158]
    @Test
    public void testOverrideName() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new CoreBean158()));
        assertEquals(a2q("{'bar':'x'}"), json);

        // and back
        CoreBean158 result = MAPPER.readValue(VPackUtils.toVPack(a2q("{'bar':'y'}")), CoreBean158.class);
        assertNotNull(result);
        assertEquals("y", result.bar);
    }
}
