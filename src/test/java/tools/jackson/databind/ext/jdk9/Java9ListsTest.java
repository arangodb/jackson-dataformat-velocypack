package tools.jackson.databind.ext.jdk9;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import tools.jackson.databind.testutil.NoCheckSubTypeValidator;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

// for [databind#2900]
public class Java9ListsTest extends DatabindTestUtil
{
    private final ObjectMapper MAPPER = VPackMapper.builder()
            .activateDefaultTypingAsProperty(
                    NoCheckSubTypeValidator.instance,
                    DefaultTyping.NON_FINAL,
                 "@class"
            ).build();

    @Test
    public void testUnmodifiableList() throws Exception
    {
         final List<String> list = Collections.unmodifiableList(Collections.singletonList("a"));
         final String actualJson = VPackUtils.toJson(MAPPER.writeValueAsBytes(list));
         final List<?> output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(1, output.size());
    }

    @Test
    public void testJava9ListOf() throws Exception
    {
         List<String> list = List.of("a");
/*         {
             Class<?> cls = list.getClass();
             tools.jackson.databind.JavaType type = MAPPER.constructType(cls);
System.err.println("LIST type: "+type);
System.err.println(" final? "+type.isFinal());
         }
         */
         ObjectWriter w = MAPPER.writerFor(List.class);
         String actualJson = VPackUtils.toJson(w.writeValueAsBytes(list));
         List<?>  output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(1, output.size());

         // and couple of alternatives:
         list = List.of("a", "b");
         actualJson = VPackUtils.toJson(w.writeValueAsBytes(list));
         output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(2, output.size());

         list = List.of("a", "b", "c");
         actualJson = VPackUtils.toJson(w.writeValueAsBytes(list));
         output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(3, output.size());

         list = List.of();
         actualJson = VPackUtils.toJson(w.writeValueAsBytes(list));
         output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(0, output.size());
    }

    @Test
    public void testJava9MapOf() throws Exception
    {
        ObjectWriter w = MAPPER.writerFor(Map.class);
        Map<String,String> map = Map.of("key", "value");
        String actualJson = VPackUtils.toJson(w.writeValueAsBytes(map));
        Map<?,?>  output = MAPPER.readValue(VPackUtils.toVPack(actualJson), Map.class);
        assertEquals(1, output.size());

        // and alternatives
        map = Map.of("key", "value", "foo", "bar");
        actualJson = VPackUtils.toJson(w.writeValueAsBytes(map));
        output = MAPPER.readValue(VPackUtils.toVPack(actualJson), Map.class);
        assertEquals(2, output.size());

        map = Map.of("key", "value", "foo", "bar", "last", "one");
        actualJson = VPackUtils.toJson(w.writeValueAsBytes(map));
        output = MAPPER.readValue(VPackUtils.toVPack(actualJson), Map.class);
        assertEquals(3, output.size());

        map = Map.of();
        actualJson = VPackUtils.toJson(w.writeValueAsBytes(map));
        output = MAPPER.readValue(VPackUtils.toVPack(actualJson), Map.class);
        assertEquals(0, output.size());
    }

    // [databind#3344]
    @Test
    public void testJava9SetOf() throws Exception
    {
        ObjectWriter w = MAPPER.writerFor(Set.class);
        Set<?> set = Set.of("a", "b", "c");
        String actualJson = VPackUtils.toJson(w.writeValueAsBytes(set));
        Set<?> output = MAPPER.readValue(VPackUtils.toVPack(actualJson), Set.class);
        assertInstanceOf(Set.class, output);
        assertEquals(set, output);
    }

    @Test
    public void testJava9ListWrapped() throws Exception
    {
         final List<String> list = Collections.unmodifiableList(List.of("a"));
         final String actualJson = VPackUtils.toJson(MAPPER.writeValueAsBytes(list));
         final List<?>  output = MAPPER.readValue(VPackUtils.toVPack(actualJson), List.class);
         assertEquals(1, output.size());
    }
}
