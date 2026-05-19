package tools.jackson.databind.ext.jdk8;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalBooleanTest
    extends DatabindTestUtil
{
    static class BooleanBean {
        public Optional<Boolean> value;

        public BooleanBean() { }
        public BooleanBean(Boolean b) {
            value = Optional.ofNullable(b);
        }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    // for [datatype-jdk8#23]
    @Test
    public void testBoolean() throws Exception
    {
        // First, serialization
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new BooleanBean(true)));
        assertEquals(a2q("{'value':true}"), json);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new BooleanBean()));
        assertEquals(a2q("{'value':null}"), json);
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new BooleanBean(null)));
        assertEquals(a2q("{'value':null}"), json);

        // then deser
        BooleanBean b = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':null}")), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());

        b = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':false}")), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertFalse(b.value.get().booleanValue());

        b = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':true}")), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertTrue(b.value.get().booleanValue());

        // and looks like a special, somewhat non-conforming case is what a user had
        // issues with
        b = MAPPER.readValue(VPackUtils.toVPack(a2q("{'value':''}")), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());
    }
}
