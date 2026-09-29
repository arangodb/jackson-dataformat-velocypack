package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0303F1 {
private static final byte[] TYPED_ZONE_ID = VPackWireFixtureTest.hex(
            "06 26 02 50 6a 61 76 61 2e 74 69 6d 65 2e 5a 6f 6e 65 49 64 "
          + "4f 41 6d 65 72 69 63 61 2f 43 68 69 63 61 67 6f 03 14");
private static final byte[] DURATION_TIMESTAMP = VPackWireFixtureTest.hex(
            "d0 06 f7 ff ff ff 04 36 36 00 00 00");
private static final byte[] DURATION_STRING = VPackWireFixtureTest.hex(
            "4a 50 54 2d 34 33 2e 36 33 36 53");
private static final byte[] DURATION_25_OBJECT = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 28 19 01");
private static final VPackMapper TYPING_MAPPER = VPackMapper.builder()
            .activateDefaultTyping(new NoCheckSubTypeValidator())
            .build();

    // Provenance: DurationDeser337Test#testWithDurationsAsTimestamps.
    void testWithDurationsAsTimestampsVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .build();
        Duration expected = Duration.parse("PT-43.636S");

        byte[] encoded = mapper.writeValueAsBytes(expected);

        assertArrayEquals(DURATION_TIMESTAMP, encoded);
        Duration actual = mapper.readValue(DURATION_TIMESTAMP, Duration.class);
        assertEquals(expected, actual);
        assertEquals("PT-43.636S", actual.toString());
    }

    // Provenance: DurationDeser337Test#testWithoutDurationsAsTimestamps.
    void testWithoutDurationsAsTimestampsVpack() throws Exception {
        VPackMapper mapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .build();
        Duration expected = Duration.parse("PT-43.636S");

        byte[] encoded = mapper.writeValueAsBytes(expected);

        assertArrayEquals(DURATION_STRING, encoded);
        assertEquals(expected, mapper.readValue(DURATION_STRING, Duration.class));
    }
private static void assertPatternDuration(String pattern, Duration expected) throws Exception {
        ObjectReader reader = mapperForPattern(pattern).readerFor(Wrapper.class);
        Wrapper actual = reader.readValue(DURATION_25_OBJECT);
        assertEquals(expected, actual.value);
    }
private static ObjectMapper mapperForPattern(String pattern) {
        return VPackMapper.builder()
                .withConfigOverride(Duration.class,
                        o -> o.setFormat(JsonFormat.Value.forPattern(pattern)))
                .build();
    }
static final class Wrapper {
        public Duration value;

        public Wrapper() { }
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testWithDurationsAsTimestampsVpack() throws Exception {
        try {
            testWithDurationsAsTimestampsVpack();
        } finally {
        }
    }


    void __invoke_testWithoutDurationsAsTimestampsVpack() throws Exception {
        try {
            testWithoutDurationsAsTimestampsVpack();
        } finally {
        }
    }

}
