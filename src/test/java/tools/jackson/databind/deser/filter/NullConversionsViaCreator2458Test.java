package tools.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tools.jackson.databind.testutil.DatabindTestUtil.vpackMapperBuilder;

public class NullConversionsViaCreator2458Test
{
    // [databind#2458]
    static class Pojo {
        List<String> _value;

        @JsonCreator
        public Pojo(@JsonProperty("value") List<String> v) {
            this._value = Objects.requireNonNull(v, "value");
        }

        protected Pojo() { }

        public List<String> value() {
            return _value;
        }

        public void setOther(List<String> v) { }
    }

    private final ObjectMapper MAPPER_WITH_AS_EMPTY = vpackMapperBuilder()
            .changeDefaultNullHandling(h -> JsonSetter.Value.construct(Nulls.AS_EMPTY,
                    Nulls.AS_EMPTY))
            .build();

    // [databind#2458]
    @Test
    public void testMissingToEmptyViaCreator() throws Exception {
        Pojo pojo = MAPPER_WITH_AS_EMPTY.readValue(VPackUtils.toVPack("{}"), Pojo.class);
        assertNotNull(pojo);
        assertNotNull(pojo.value());
        assertEquals(0, pojo.value().size());
    }

    // [databind#2458]
    @Test
    public void testNullToEmptyViaCreator() throws Exception {
        Pojo pojo = MAPPER_WITH_AS_EMPTY.readValue(VPackUtils.toVPack("{\"value\":null}"), Pojo.class);
        assertNotNull(pojo);
        assertNotNull(pojo.value());
        assertEquals(0, pojo.value().size());
    }
}
