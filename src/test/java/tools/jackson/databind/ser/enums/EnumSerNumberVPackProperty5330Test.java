package tools.jackson.databind.ser.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * [databind#5330] Serialization: {@code @JsonProperty} value used as numeric index
 * for Enums with {@code Shape.NUMBER}
 */
public class EnumSerNumberVPackProperty5330Test extends DatabindTestUtil
{
    // no JsonFormat override: used to verify that global WRITE_ENUMS_USING_INDEX keeps ordinal semantics.
    public enum MyEnumNoFormat {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum MyEnum {
        @JsonProperty("7")
        FOO,
        @JsonProperty("42")
        BAR
    }

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    public enum NonNumericEnum {
        @JsonProperty("NOT_A_NUMBER")
        VALUE
    }

    static class EnumBean {
        public MyEnum value = MyEnum.BAR;
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void shouldSerializeUsingNumericJsonPropertyAsIndex() throws Exception {
        assertEquals("7", VPackUtils.toJson(MAPPER.writeValueAsBytes(MyEnum.FOO)));

        assertEquals(a2q("{'value':42}"), VPackUtils.toJson(MAPPER.writeValueAsBytes(new EnumBean())));
        assertEquals("[7,42]", VPackUtils.toJson(MAPPER.writeValueAsBytes(Arrays.asList(MyEnum.FOO, MyEnum.BAR))));
        assertEquals("[7]", VPackUtils.toJson(MAPPER.writeValueAsBytes(EnumSet.of(MyEnum.FOO))));
    }

    @Test
    public void shouldSerializeEnumMapKeysUsingNumericJsonPropertyIndex() throws Exception {
        Map<MyEnum, String> map = new HashMap<>();
        map.put(MyEnum.FOO, "lucky");

        assertEquals(a2q("{'7':'lucky'}"), VPackUtils.toJson(MAPPER.writeValueAsBytes(map)));
    }

    @Test
    public void shouldOverrideGlobalIndexFeatureDisable() throws Exception {
        ObjectMapper mapper = vpackMapperBuilder()
            .disable(EnumFeature.WRITE_ENUMS_USING_INDEX)
            .build();

        assertEquals("7", VPackUtils.toJson(mapper.writeValueAsBytes(MyEnum.FOO)));
    }

    @Test
    public void shouldKeepOrdinalWhenGlobalIndexFeatureIsEnabledWithoutFormatOverride() throws Exception {
        ObjectMapper mapper = vpackMapperBuilder()
            .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
            .build();

        // ordinal semantics: FOO=0, BAR=1 (NOT @JsonProperty("7"/"42"))
        assertEquals("0", VPackUtils.toJson(mapper.writeValueAsBytes(MyEnumNoFormat.FOO)));
        assertEquals("1", VPackUtils.toJson(mapper.writeValueAsBytes(MyEnumNoFormat.BAR)));

        // also verify container use-cases (same serializer path)
        assertEquals("[0,1]", VPackUtils.toJson(mapper.writeValueAsBytes(Arrays.asList(MyEnumNoFormat.FOO, MyEnumNoFormat.BAR))));
        assertEquals("[1]", VPackUtils.toJson(mapper.writeValueAsBytes(EnumSet.of(MyEnumNoFormat.BAR))));
    }

    @Test
    public void shouldUseJsonPropertyStringWhenNotNumericWithNumberShape() throws Exception {
        // Non-numeric @JsonProperty with Shape.NUMBER: use @JsonProperty value as-is (String)
        assertEquals(q("NOT_A_NUMBER"), VPackUtils.toJson(MAPPER.writeValueAsBytes(NonNumericEnum.VALUE)));
    }

    @Test
    public void shouldUseOrdinalForNonNumericJsonPropertyWithGlobalIndexFeature() throws Exception {
        // Enum WITHOUT @JsonFormat — global feature uses ordinal, ignores @JsonProperty
        ObjectMapper mapper = vpackMapperBuilder()
            .enable(EnumFeature.WRITE_ENUMS_USING_INDEX)
            .build();

        assertEquals("0", VPackUtils.toJson(mapper.writeValueAsBytes(MyEnumNoFormat.FOO)));
        assertEquals("1", VPackUtils.toJson(mapper.writeValueAsBytes(MyEnumNoFormat.BAR)));
    }
}
