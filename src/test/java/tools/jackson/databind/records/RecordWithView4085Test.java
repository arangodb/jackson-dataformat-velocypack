package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonView;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#4085]
public class RecordWithView4085Test extends DatabindTestUtil
{
    static class View4085Default { }
    static class View4085Field { }
    
    @JsonView(View4085Default.class)
    public record Record4085(int total, @JsonView(View4085Field.class) int current) { }

    @Test
    public void testRecordWithView4085() throws Exception
    {
        final Record4085 input = new Record4085(1, 2);
        final String EXP = a2q("{'total':1,'current':2}");
        final ObjectWriter w = newVPackMapper().writer();

        // by default, all properties included, without view
        assertEquals(EXP, VPackUtils.toJson(w.writeValueAsBytes(input)));

        // with non-inclusive view, nothing included:
        assertEquals("{}", VPackUtils.toJson(w.withView(Void.class).writeValueAsBytes(input)));

        // But other combinations exist
        assertEquals(a2q("{'total':1}"),
                VPackUtils.toJson(w.withView(View4085Default.class).writeValueAsBytes(input)));
        assertEquals(a2q("{'current':2}"),
                VPackUtils.toJson(w.withView(View4085Field.class).writeValueAsBytes(input)));
    }
}
