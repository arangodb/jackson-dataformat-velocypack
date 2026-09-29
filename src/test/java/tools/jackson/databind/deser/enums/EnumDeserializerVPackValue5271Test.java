package tools.jackson.databind.deser.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EnumDeserializerVPackValue5271Test extends DatabindTestUtil
{
    enum Enum5271 {
        T10("10%"), T20("20%"), T30("30%");

        private final String code;

        Enum5271(String code) {
            this.code = code;
        }

        @JsonValue
        public String getCode() {
            return code;
        }
    }

    private final ObjectReader ENUM_READER = newVPackMapper().readerFor(Enum5271.class);

    // [databind#5271]
    @Test
    void convertStringToEnum() throws Exception {
        _testConvert(ENUM_READER.without(EnumFeature.READ_ENUMS_USING_TO_STRING));
        _testConvert(ENUM_READER.with(EnumFeature.READ_ENUMS_USING_TO_STRING));
    }

    private void _testConvert(ObjectReader reader) throws Exception {
        assertEquals(Enum5271.T20, reader.readValue(VPackUtils.toVPack(q("20%"))));
    }
}
