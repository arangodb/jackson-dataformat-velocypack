package tools.jackson.databind.deser.jdk;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JDK7TypesTest extends DatabindTestUtil
{
    private boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    @Test
    public void testPathRoundTrip() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        Path input = Paths.get(isWindows() ? "c:/tmp" : "/tmp", "foo.txt");
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(input));
        assertNotNull(json);

        Path p = mapper.readValue(VPackUtils.toVPack(json), Path.class);
        assertNotNull(p);

        assertEquals(input.toUri(), p.toUri());
        assertEquals(input.toAbsolutePath(), p.toAbsolutePath());
    }

    // [databind#1688]
    @Test
    public void testPolymorphicPath() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
            .activateDefaultTyping(NoCheckSubTypeValidator.instance,
                    DefaultTyping.NON_FINAL)
            .build();
        Path input = Paths.get(isWindows() ? "c:/tmp" : "/tmp", "foo.txt");

        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Object[]{input}));

        Object[] obs = mapper.readValue(VPackUtils.toVPack(json), Object[].class);
        assertEquals(1, obs.length);
        Object ob = obs[0];
        if (!(ob instanceof Path)) {
            fail("Should deserialize as `Path`, got: `" + ob.getClass().getName() + "`");
        }

        assertEquals(input.toAbsolutePath().toString(), ob.toString());
    }
}
