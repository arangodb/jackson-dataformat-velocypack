package tools.jackson.databind.jsontype.deftyping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonTypeResolver;
import tools.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DefaultTypeResolverForLong2753Test extends DatabindTestUtil
{
    static class Data {
        private Long key;

        @JsonCreator
        Data(@JsonProperty("key") Long key) {
            this.key = key;
        }

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        @JsonTypeResolver(MyTypeResolverBuilder.class)
        public long key() {
            return key;
        }
    }

    static class MyTypeResolverBuilder extends StdTypeResolverBuilder {
        @Override
        protected boolean allowPrimitiveTypes(DatabindContext ctxt,
                JavaType baseType) {
            return true;
        }
    }

    @Test
    public void testDefaultTypingWithLong() throws Exception
    {
        Data data = new Data(1L);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("longInMap", 2L);
        mapData.put("longAsField", data);

        // Configure Jackson to preserve types
//        StdTypeResolverBuilder resolver = new MyTypeResolverBuilder();
//        resolver.init(JsonTypeInfo.Id.CLASS, null);
//        resolver.inclusion(JsonTypeInfo.As.PROPERTY);
//        resolver.typeProperty("__t");
        ObjectMapper mapper = vpackMapperBuilder()
//                .setDefaultTyping(resolver)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        // Serialize
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(mapData));
//System.err.println("JSON:\n"+json);
        Map<?,?> result = mapper.readValue(VPackUtils.toVPack(json), Map.class);
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
