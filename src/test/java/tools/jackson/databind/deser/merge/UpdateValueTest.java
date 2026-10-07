package tools.jackson.databind.deser.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tools.jackson.databind.testutil.DatabindTestUtil.a2q;
import static tools.jackson.databind.testutil.DatabindTestUtil.newVPackMapper;

public class UpdateValueTest
{
    static class Bean
    {
        private String a;
        private String b;

        @JsonCreator
        public Bean(@JsonProperty("a") String a, @JsonProperty("b") String b)
        {
            this.a = a;
            this.b = b;
        }

        String getA() {
            return a;
        }

        void setA(String a) {
            this.a = a;
        }

        String getB() {
            return b;
        }

        void setB(String b) {
            this.b = b;
        }
    }

    private final ObjectMapper MAPPER = newVPackMapper();

    // [databind#318] (and Scala module issue #83]
    @Test
    public void testValueUpdateWithCreator() throws Exception
    {
        Bean bean = new Bean("abc", "def");
        MAPPER.readerFor(Bean.class).withValueToUpdate(bean).readValue(VPackUtils.toVPack("{\"a\":\"ghi\",\"b\":\"jkl\"}"));
        assertEquals("ghi", bean.getA());
        assertEquals("jkl", bean.getB());
    }

    @Test
    public void testValueUpdateOther() throws Exception
    {
        Bean bean = new Bean("abc", "def");
        ObjectReader r = MAPPER.readerFor(Bean.class).withValueToUpdate(bean);
        // but, changed our minds, no update
        r = r.withValueToUpdate(null);
        // should be safe to read regardless
        Bean result = r.readValue(VPackUtils.toVPack(a2q("{'a':'x'}")));
        assertNotNull(result);
    }
}
