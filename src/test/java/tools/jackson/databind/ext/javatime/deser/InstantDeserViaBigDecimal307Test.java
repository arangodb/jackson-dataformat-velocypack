package tools.jackson.databind.ext.javatime.deser;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.JsonNodeFeature;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

// [modules-java8#307]: Loss of precision via JsonNode for BigDecimal-valued
// things (like Instant)
public class InstantDeserViaBigDecimal307Test extends DateTimeTestBase
{
    public static class Wrapper307 {
        public Instant value;

        public Wrapper307(Instant v) { value = v; }
        public Wrapper307() { }
    }

    private final Instant ISSUED_AT = Instant.ofEpochSecond(1234567890).plusNanos(123456789);

    private ObjectMapper MAPPER = mapperBuilder()
            .enable(JsonNodeFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void instantViaReadValue() throws Exception {
         String serialized = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Wrapper307(ISSUED_AT)));
         Wrapper307 deserialized = MAPPER.readValue(VPackUtils.toVPack(serialized), Wrapper307.class);
         assertEquals(ISSUED_AT, deserialized.value);
    }

    @Test
    public void instantViaReadTree() throws Exception {
        String serialized = VPackUtils.toJson(MAPPER.writeValueAsBytes(new Wrapper307(ISSUED_AT)));
        JsonNode tree = MAPPER.readTree(VPackUtils.toVPack(serialized));
        Wrapper307 deserialized = MAPPER.treeToValue(tree, Wrapper307.class);
        assertEquals(ISSUED_AT, deserialized.value);
    }
}
