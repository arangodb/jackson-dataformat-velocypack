package tools.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests covering the [databind#5398] follow-up: when a property's setter is
 * {@code @JsonIgnore}d (and the getter carries {@code @JsonProperty} that
 * keeps the property non-ignored as a whole), the inferred field mutator
 * retained via {@code MapperFeature.INFER_PROPERTY_MUTATORS} must NOT be
 * used to bypass the ignored setter. The property should remain
 * serialization-only (read-only).
 */
public class VPackIgnoreSetterFieldBypass5398Test extends DatabindTestUtil
{
    static class RenamedGetterIgnoredSetter {
        private String prop;

        @JsonProperty("renamedProp")
        public String getProp() {
            return prop;
        }

        @JsonIgnore
        public void setProp(String prop) {
            this.prop = prop;
        }
    }

    static class StandardGetterIgnoredSetter {
        private String prop;

        @JsonProperty
        public String getProp() {
            return prop;
        }

        @JsonIgnore
        public void setProp(String prop) {
            this.prop = prop;
        }
    }

    // Sibling shape: READ_ONLY access on getter rather than @JsonIgnore on setter.
    static class ReadOnlyRenamedGetter {
        private String prop;

        @JsonProperty(value = "renamedProp", access = JsonProperty.Access.READ_ONLY)
        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    @Test
    public void renamedGetterStillSerializes() throws Exception
    {
        RenamedGetterIgnoredSetter bean = new RenamedGetterIgnoredSetter();
        bean.setProp("someValue");
        assertEquals("{\"renamedProp\":\"someValue\"}",
                VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));
    }

    @Test
    public void standardGetterStillSerializes() throws Exception
    {
        StandardGetterIgnoredSetter bean = new StandardGetterIgnoredSetter();
        bean.setProp("someValue");
        assertEquals("{\"prop\":\"someValue\"}",
                VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));
    }

    // The renamed property must be read-only: @JsonIgnore on the setter
    // blocks the write, and the inferred field mutator must not bypass it.
    @Test
    public void renamedIgnoredSetterDoesNotBypassViaField() throws Exception
    {
        RenamedGetterIgnoredSetter result = MAPPER.readValue(
                VPackUtils.toVPack("{\"renamedProp\":\"someValue\"}"),
                RenamedGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    @Test
    public void standardIgnoredSetterDoesNotBypassViaField() throws Exception
    {
        StandardGetterIgnoredSetter result = MAPPER.readValue(
                VPackUtils.toVPack("{\"prop\":\"someValue\"}"),
                StandardGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Control: feeding the implicit field name (rather than the renamed
    // public name) also does not write the field.
    @Test
    public void implicitFieldNameDoesNotWriteWhenSetterIgnored() throws Exception
    {
        RenamedGetterIgnoredSetter result = MAPPER.readerFor(RenamedGetterIgnoredSetter.class)
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(VPackUtils.toVPack("{\"prop\":\"someValue\"}"));
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Bypass must also stay closed when INFER_PROPERTY_MUTATORS is disabled,
    // i.e. the fix is not just an artifact of inference being on.
    @Test
    public void inferMutatorsDisabledStillBlocksWrite() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .disable(MapperFeature.INFER_PROPERTY_MUTATORS)
                .build();
        RenamedGetterIgnoredSetter result = mapper.readValue(
                VPackUtils.toVPack("{\"renamedProp\":\"someValue\"}"),
                RenamedGetterIgnoredSetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

    // Sibling: @JsonProperty(access = READ_ONLY) on the renamed getter must
    // produce the same outcome — serialize under the new name, not write
    // back via the inferred field mutator.
    @Test
    public void readOnlyRenamedGetterIsSerializeOnly() throws Exception
    {
        ReadOnlyRenamedGetter bean = new ReadOnlyRenamedGetter();
        bean.setProp("someValue");
        assertEquals("{\"renamedProp\":\"someValue\"}",
                VPackUtils.toJson(MAPPER.writeValueAsBytes(bean)));

        ReadOnlyRenamedGetter result = MAPPER.readValue(
                VPackUtils.toVPack("{\"renamedProp\":\"someValue\"}"),
                ReadOnlyRenamedGetter.class);
        assertNotNull(result);
        assertNull(result.getProp());
    }

}
