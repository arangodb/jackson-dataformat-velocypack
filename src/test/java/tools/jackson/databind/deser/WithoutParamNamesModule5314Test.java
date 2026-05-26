package tools.jackson.databind.deser;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WithoutParamNamesModule5314Test
    extends DatabindTestUtil
{
    // Constructor can (and will) be auto-detected if (and only if!)
    // Implicit Parameter Names are detected (see
    // {@link MapperFeature#DETECT_PARAMETER_NAMES})
    static class Bean178
    {
        final String hiddenName;
        final int hiddenAge;

        public Bean178(String openName, int openAge) {
            hiddenName = openName;
            hiddenAge = openAge;
        }
    }

    private final String JSON = a2q("{'openName':'stu','openAge':22}");

    @Test
    public void testWorksByDefault()
    {
        // Passes... by default
        _runTestSuccess(VPackMapper.builder()
                .build());
        // Passes... when enabled
        _runTestSuccess(VPackMapper.builder()
                .enable(MapperFeature.DETECT_PARAMETER_NAMES).build());
        // Fails when...disabled
        _runTestFailure(VPackMapper.builder()
                .disable(MapperFeature.DETECT_PARAMETER_NAMES).build());
        // NOTE: VPackMapper.builderWithJackson2Defaults() not available for VelocyPack
        // Fails when...used with Jackson2Defaults - skipped for VelocyPack
    }

    private void _runTestSuccess(VPackMapper mapper)
    {
        Bean178 bean = mapper.readValue(VPackUtils.toVPack(JSON), Bean178.class);

        assertEquals("stu", bean.hiddenName);
        assertEquals(22, bean.hiddenAge);
    }


    private void _runTestFailure(VPackMapper mapper) {
        try {
            mapper.readValue(VPackUtils.toVPack(JSON), Bean178.class);
            fail("Should have thrown an exception");
        } catch (InvalidDefinitionException e) {
            assertThat(e.getMessage())
                .contains("Cannot construct instance of")
                .contains("no Creators, like default constructor, exist");
        }
    }
}
