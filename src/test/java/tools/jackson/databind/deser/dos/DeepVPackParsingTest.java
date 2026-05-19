package tools.jackson.databind.deser.dos;

import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadConstraints;
import tools.jackson.core.exc.StreamConstraintsException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackFactory;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class DeepVPackParsingTest extends DatabindTestUtil
{
    private final static int DEFAULT_MAX_DEPTH = StreamReadConstraints.DEFAULT_MAX_DEPTH;
    private final static int TOO_DEEP_NESTING = DEFAULT_MAX_DEPTH + 1;

    private final VPackFactory unconstrainedFactory = VPackFactory.builder()
            .streamReadConstraints(StreamReadConstraints.builder().maxNestingDepth(Integer.MAX_VALUE).build())
            .build();
    private final ObjectMapper unconstrainedMapper = VPackMapper.builder(unconstrainedFactory).build();
    private final ObjectMapper defaultMapper = VPackMapper.builder().build();

    public void testParseWithArrayWithDefaultConfig() throws Exception
    {
        final String doc = _nestedDoc(TOO_DEEP_NESTING, "[ ", "] ");
        try (JsonParser jp = defaultMapper.createParser(VPackUtils.toVPack(doc))) {
            while (jp.nextToken() != null) { }
            fail("expected StreamConstraintsException");

        } catch (StreamConstraintsException e) {
            assertThat(e.getMessage())
                .startsWith("Document nesting depth ("+(DEFAULT_MAX_DEPTH+1)
                    +") exceeds the maximum allowed ("+DEFAULT_MAX_DEPTH);
        }
    }

    public void testParseWithObjectWithDefaultConfig() throws Exception
    {
        final String doc = "{"+_nestedDoc(TOO_DEEP_NESTING, "\"x\":{", "} ") + "}";
        try (JsonParser jp = defaultMapper.createParser(VPackUtils.toVPack(doc))) {
            while (jp.nextToken() != null) { }
            fail("expected StreamConstraintsException");
        } catch (StreamConstraintsException e) {
            assertThat(e.getMessage())
                .startsWith("Document nesting depth ("+(DEFAULT_MAX_DEPTH+1)
                    +") exceeds the maximum allowed ("+DEFAULT_MAX_DEPTH);
        }
    }

    public void testParseWithArrayWithUnconstrainedConfig() throws Exception
    {
        final String doc = _nestedDoc(TOO_DEEP_NESTING, "[ ", "] ");
        try (JsonParser jp = unconstrainedMapper.createParser(VPackUtils.toVPack(doc))) {
            while (jp.nextToken() != null) { }
        }
    }

    public void testParseWithObjectWithUnconstrainedConfig() throws Exception
    {
        final String doc = "{"+_nestedDoc(TOO_DEEP_NESTING, "\"x\":{", "} ") + "}";
        try (JsonParser jp = unconstrainedMapper.createParser(VPackUtils.toVPack(doc))) {
            while (jp.nextToken() != null) { }
        }
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
