package tools.jackson.databind.misc;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.introspect.DefaultAccessorNamingStrategy;
import tools.jackson.databind.testutil.DatabindTestUtil;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#5335], due to [databind#2882]
public class IPhoneStyleProperty5335Test
    extends DatabindTestUtil
{
    @JsonPropertyOrder({ "aProp" })
    static class TestPojo {
        private String aProp;
        private String anotherProp;

        // NOTE: non-compliant naming; see VPackMapper configuration
        public String getaProp() {
            return aProp;
        }

        // NOTE: non-compliant naming; see VPackMapper configuration
        public void setaProp(String aProp) {
            this.aProp = aProp;
        }

        public String getAnotherProp() {
            return anotherProp;
        }

        public void setAnotherProp(String anotherProp) {
            this.anotherProp = anotherProp;
        }
    }

    @Test
    public void featureEnabledTest()
    {
        ObjectMapper mapper = VPackMapper.builder()
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              .enable(MapperFeature.FIX_FIELD_NAME_UPPER_CASE_PREFIX)
              .accessorNaming(new DefaultAccessorNamingStrategy.Provider()
                      .withFirstCharAcceptance(true, false))
              .build();

        String json = "{\"aProp\":\"aPropValue\",\"prop1\":\"prop1Value\"}";
        TestPojo result = mapper.readValue(VPackUtils.toVPack(json), TestPojo.class);
        assertEquals("aPropValue", result.getaProp());
        String serialized = VPackUtils.toJson(mapper.writeValueAsBytes(result));
        assertEquals("{\"aProp\":\"aPropValue\",\"anotherProp\":null}",
                serialized);
    }
}
