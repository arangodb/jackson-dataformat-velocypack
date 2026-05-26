package tools.jackson.databind.deser.dos;

import org.junit.jupiter.api.Test;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

// [databind#3397], wrt JsonNode
public class DeepVPackNodeDeser3397Test
{
    // 28-Mar-2021, tatu: Used to fail at 5000 for tree/object,
    // 8000 for tree/array, before work on iterative JsonNode deserializer
    // ... currently gets a bit slow at 1M but passes.
    // But test with 100k as practical limit, to guard against regression
//    private final static int TOO_DEEP_NESTING = 1_000_000;
    private final static int TOO_DEEP_NESTING = StreamReadConstraints.DEFAULT_MAX_DEPTH * 10;

    private final VPackFactory jsonFactory = VPackFactory.builder()
            .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
            .build();
    private final ObjectMapper MAPPER = VPackMapper.builder(jsonFactory).build();

    @Test
    public void testTreeWithArray() throws Exception
    {
        final String doc = _nestedDoc(TOO_DEEP_NESTING, "[ ", "] ");
        JsonNode n = MAPPER.readTree(VPackUtils.toVPack(doc));
        assertTrue(n.isArray());
    }

    @Test
    public void testTreeWithObject() throws Exception
    {
        final String doc = _nestedDocObject(TOO_DEEP_NESTING);
        JsonNode n = MAPPER.readTree(VPackUtils.toVPack(doc));
        assertTrue(n.isObject());
    }

    private String _nestedDocObject(int nesting) {
        StringBuilder sb = new StringBuilder(nesting * 10);
        for (int i = 0; i < nesting; ++i) {
            sb.append("{\"a\": ");
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        sb.append("null");
        for (int i = 0; i < nesting; ++i) {
            sb.append("}");
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String _nestedDoc(int nesting, String open, String close) {
        StringBuilder sb = new StringBuilder(nesting * (open.length() + close.length()));
        for (int i = 0; i < nesting; ++i) {
            sb.append(open);
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        for (int i = 0; i < nesting; ++i) {
            sb.append(close);
            if ((i & 31) == 0) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
