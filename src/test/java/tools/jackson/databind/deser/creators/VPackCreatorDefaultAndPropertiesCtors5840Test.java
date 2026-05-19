package tools.jackson.databind.deser.creators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

// For [databind#5840]: no-arg @JsonCreator and multi-arg @JsonCreator should co-exist
public class VPackCreatorDefaultAndPropertiesCtors5840Test extends DatabindTestUtil
{
    // Class with both a no-arg @JsonCreator (default values) and a multi-arg @JsonCreator
    static final class AllDefaultsBean
    {
        private final String name;
        private final Integer count;

        @JsonCreator
        public AllDefaultsBean(
                @JsonProperty("name") String name,
                @JsonProperty("count") Integer count
        ) {
            this.name = name;
            this.count = count;
        }

        @JsonCreator
        public AllDefaultsBean() {
            this("default-name", 42);
        }

        public String getName() { return name; }
        public Integer getCount() { return count; }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#5840]: verify no exception is thrown and multi-arg creator is used
    @Test
    public void testDeserWithPropertiesCreator5840() throws Exception
    {
        AllDefaultsBean result = MAPPER.readValue(
                VPackUtils.toVPack("{\"name\":\"hello\", \"count\":7}"), AllDefaultsBean.class);
        assertNotNull(result);
        assertEquals("hello", result.getName());
        assertEquals(7, result.getCount());
    }

    // [databind#5840]: verify no exception is thrown with empty JSON (uses multi-arg creator
    // with null/0 defaults, same behavior as pre-2.21)
    @Test
    public void testDeserWithEmptyJson5840() throws Exception
    {
        AllDefaultsBean result = MAPPER.readValue(VPackUtils.toVPack("{}"), AllDefaultsBean.class);
        assertNotNull(result);
        // Multi-arg @JsonCreator is used with missing properties defaulting to null/0
        assertNull(result.getName());
        assertNull(result.getCount());
    }
}
