package tools.jackson.databind.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DateFormatTest extends DatabindTestUtil
{
    protected static class DateWrapper {
        public Date value;

        public DateWrapper() { }
        public DateWrapper(long l) { value = new Date(l); }
        public DateWrapper(Date v) { value = v; }
    }

    @Test
    public void testTypeDefaults() throws Exception
    {
        ObjectMapper mapper = vpackMapperBuilder()
                .withConfigOverride(Date.class,
                        o -> o.setFormat(JsonFormat.Value.forPattern("yyyy.dd.MM")))
                .build();
        // First serialize, should result in this (in UTC):
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(new DateWrapper(0L)));
        assertEquals(a2q("{'value':'1970.01.01'}"), json);

        // and then read back
        DateWrapper w = mapper.readValue(VPackUtils.toVPack(a2q("{'value':'1981.13.3'}")), DateWrapper.class);
        assertNotNull(w);
        // arbitrary TimeZone, but good enough to ensure year is right
        Calendar c = Calendar.getInstance();
        c.setTime(w.value);
        assertEquals(1981, c.get(Calendar.YEAR));
        assertEquals(Calendar.MARCH, c.get(Calendar.MONTH));
    }
}
