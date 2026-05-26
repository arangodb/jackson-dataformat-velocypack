package tools.jackson.databind.deser.filter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;

import java.beans.ConstructorProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static tools.jackson.databind.testutil.DatabindTestUtil.newVPackMapper;

public class VPackIgnoreCreatorProp1317Test
{
    static class Testing {
        @JsonIgnore
        public String ignore;

        String notIgnore;

        public Testing() {}

        @ConstructorProperties({"ignore", "notIgnore"})
        public Testing(String ignore, String notIgnore) {
            super();
            this.ignore = ignore;
            this.notIgnore = notIgnore;
        }

        public String getIgnore() {
            return ignore;
        }

        public void setIgnore(String ignore) {
            this.ignore = ignore;
        }

        public String getNotIgnore() {
            return notIgnore;
        }

        public void setNotIgnore(String notIgnore) {
            this.notIgnore = notIgnore;
        }
    }

    @Test
    public void testThatJsonIgnoreWorksWithConstructorProperties() throws Exception {
        ObjectMapper om = newVPackMapper();
        Testing testing = new Testing("shouldBeIgnored", "notIgnore");
        String json = VPackUtils.toJson(om.writeValueAsBytes(testing));
//        System.out.println(json);
        assertFalse(json.contains("shouldBeIgnored"));
    }
}
