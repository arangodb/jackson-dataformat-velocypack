package tools.jackson.databind.deser.inject;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#4218]
class JacksonInject4218Test extends DatabindTestUtil
{
    static class Dto {
        @JacksonInject("id")
        String id;

        @JsonCreator
        Dto(@JacksonInject("id")
            @JsonProperty("id")
            String id
        ) {
            this.id = id;
        }
    }

    class MyInjectableValues extends InjectableValues.Std
    {
        private static final long serialVersionUID = 1L;

        int nextId = 1; // count up if injected

        @Override
        public Object findInjectableValue(
                DeserializationContext ctxt,
                Object valueId,
                BeanProperty forProperty,
                Object beanInstance,
                Boolean optional, Boolean useInput) {
            if (valueId.equals("id")) {
                return "id" + nextId++;
            } else {
                return super.findInjectableValue(ctxt, valueId, forProperty, beanInstance, optional, useInput);
            }
        }
    }

    // [databind#4218]
    @Test
    void injectNoDups4218() throws Exception
    {
        ObjectReader reader = newVPackMapper()
                .readerFor(Dto.class)
                .with(new MyInjectableValues());

        Dto dto = reader.readValue(VPackUtils.toVPack("{}"));
        String actual = dto.id;

        assertEquals("id1", actual);
    }
}
