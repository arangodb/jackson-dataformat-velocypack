package tools.jackson.databind.ser.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#4788] 2.18
public class EnumsToLCvsVPackProperty4788Test
    extends DatabindTestUtil
{

    public enum SauceA {
        @JsonProperty("Ketchup")
        KETCHUP,
    }

    @EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    public enum SauceB {
        @JsonProperty("PROPERTY_MAYO_NAIZZZZ")
        MAYO_NAIZZZZ
    }

    public enum SauceC {
        @JsonProperty("Is-A-Prop")
        IS_MY_NAME
    }

    ObjectMapper objectMapper = vpackMapperBuilder()
            .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
            .build();

    @Test
    void shouldUseJsonPropertySimple()
            throws Exception
    {
        assertEquals(
                "\"Ketchup\"",
                VPackUtils.toJson(objectMapper.writeValueAsBytes(SauceA.KETCHUP))
        );

    }

    @Test
    void shouldUseJsonPropertyOverEnumNaming()
        throws Exception
    {
        assertEquals(
                "\"PROPERTY_MAYO_NAIZZZZ\"",
                VPackUtils.toJson(objectMapper.writeValueAsBytes(SauceB.MAYO_NAIZZZZ))
        );
    }

    @Test
    void shouldUseJsonPropertyOverToString()
        throws Exception
    {
        assertEquals(
                "\"Is-A-Prop\"",
                VPackUtils.toJson(objectMapper.writer().with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                        .writeValueAsBytes(SauceC.IS_MY_NAME))
        );
    }
}
