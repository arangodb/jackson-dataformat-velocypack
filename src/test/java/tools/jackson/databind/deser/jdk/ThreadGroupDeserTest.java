package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ThreadGroupDeserTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = sharedMapper();

    // [databind#4939]
    @Test
    public void deserThreadGroupFromEmpty() throws Exception {
        ThreadGroup tg = MAPPER.readValue(VPackUtils.toVPack("{}"), ThreadGroup.class);
        assertNotNull(tg);
    }

    @Test
    public void roundtripThreadGroup() throws Exception {
        ThreadGroup tg = new ThreadGroup("testTG");
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(tg));
        ThreadGroup tg2 = MAPPER.readValue(VPackUtils.toVPack(json), ThreadGroup.class);
        assertNotNull(tg2);
        assertEquals(tg.getName(), tg2.getName());
    }
}
