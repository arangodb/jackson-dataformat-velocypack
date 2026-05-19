package tools.jackson.databind.ext.javatime.deser;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationDeser337Test extends DateTimeTestBase
{
    @Test
    public void testWithDurationsAsTimestamps() throws Exception
    {
        final ObjectMapper MAPPER_DURATION_TIMESTAMPS = mapperBuilder()
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .build();

        Duration duration = Duration.parse("PT-43.636S");

        String ser = VPackUtils.toJson(MAPPER_DURATION_TIMESTAMPS.writeValueAsBytes(duration));

        assertEquals("-43.636000000", ser);

        Duration deser = MAPPER_DURATION_TIMESTAMPS.readValue(VPackUtils.toVPack(ser), Duration.class);

        assertEquals(duration, deser);
        assertEquals(deser.toString(), "PT-43.636S");
    }

    @Test
    public void testWithoutDurationsAsTimestamps() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .build();

        Duration duration = Duration.parse("PT-43.636S");

        String ser = VPackUtils.toJson(mapper.writeValueAsBytes(duration));
        assertEquals(q("PT-43.636S"), ser);

        Duration deser = mapper.readValue(VPackUtils.toVPack(ser), Duration.class);
        assertEquals(duration, deser);
    }
}
