package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0303F0 {
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

    // Provenance: DefaultTypingTest#testZoneWithForcedBaseType.
    void testZoneWithForcedBaseTypeVpack() throws Exception {
        ZoneId expected = ZoneId.of("America/Chicago");

        byte[] encoded = TYPING_MAPPER.writerFor(ZoneId.class).writeValueAsBytes(expected);

        assertArrayEquals(TYPED_ZONE_ID, encoded);
        assertEquals(expected, TYPING_MAPPER.readValue(encoded, ZoneId.class));
        assertEquals(expected, TYPING_MAPPER.readValue(TYPED_ZONE_ID, ZoneId.class));
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

    void __invoke_testZoneWithForcedBaseTypeVpack() throws Exception {
        try {
            testZoneWithForcedBaseTypeVpack();
        } finally {
        }
    }

}
