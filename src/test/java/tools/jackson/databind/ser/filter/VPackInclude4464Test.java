package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.annotation.JsonSerialize;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#4464] Since 2.15, NON_DEFAULT should be extension of NON_EMPTY
public class VPackInclude4464Test {

    public static class BarSerializer extends ValueSerializer<Bar> {

        public BarSerializer() {
        }

        @Override
        public void serialize(Bar value, JsonGenerator gen, SerializationContext provider) {
            gen.writePOJO(value);
        }

        @Override
        public boolean isEmpty(SerializationContext provider, Bar value) {
            return "I_AM_EMPTY".equals(value.getName());
        }
    }

    public static class Bar {
        public String getName() {
            return "I_AM_EMPTY";
        }
    }

    public static class Foo {
        @JsonSerialize(using = BarSerializer.class)
        public Bar getBar() {
            return new Bar();
        }
    }

    @Test
    public void test86() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new Foo()));
        assertEquals("{}", json);
    }
}
