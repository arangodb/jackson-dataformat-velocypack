package tools.jackson.databind.ext.jdk17;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Java17CollectionsTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = VPackMapper.builder()
            .activateDefaultTypingAsProperty(
                 new NoCheckSubTypeValidator(),
                 DefaultTyping.NON_FINAL,
                 "@class"
            ).build();

    // [databind#3404]
    @Test
    public void testJava9StreamOf() throws Exception
    {
        ObjectWriter w = MAPPER.writerFor(List.class);
        List<String> input = Stream.of("a", "b", "c").collect(Collectors.toList());
        String actualJson = VPackUtils.toJson(w.writeValueAsBytes(input));
        List<?> result = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
        assertEquals(input, result);

        input = Stream.of("a", "b", "c").toList();
        actualJson = VPackUtils.toJson(w.writeValueAsBytes(input));
        result = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
        assertEquals(input, result);
    }
}
