package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0303F2 {
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

    // Provenance: DurationDeserTest#shouldDeserializeInNanos_whenNanosUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInNanos_whenNanosUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("NANOS", Duration.ofNanos(25));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInMicros_whenMicrosUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInMicros_whenMicrosUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("MICROS", Duration.of(25, ChronoUnit.MICROS));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInMillis_whenMillisUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInMillis_whenMillisUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("MILLIS", Duration.ofMillis(25));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInSeconds_whenSecondsUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInSeconds_whenSecondsUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("SECONDS", Duration.ofSeconds(25));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInMinutes_whenMinutesUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInMinutes_whenMinutesUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("MINUTES", Duration.ofMinutes(25));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInHours_whenHoursUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInHours_whenHoursUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("HOURS", Duration.ofHours(25));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInHalfDays_whenHalfDaysUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInHalfDays_whenHalfDaysUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("HALF_DAYS", Duration.of(25, ChronoUnit.HALF_DAYS));
    }

    // Provenance: DurationDeserTest#shouldDeserializeInDays_whenDaysUnitAsPattern_andValueIsInteger.
    void shouldDeserializeInDays_whenDaysUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        assertPatternDuration("DAYS", Duration.ofDays(25));
    }

    // Provenance: DurationDeserTest#shouldFailForInvalidPattern.
    void shouldFailForInvalidPatternVpack() throws Exception {
        ObjectMapper mapper = mapperForPattern("Nanos");
        ObjectReader reader = mapper.readerFor(Wrapper.class);

        try {
            reader.readValue(DURATION_25_OBJECT);
            fail("Should not allow invalid 'pattern'");
        } catch (InvalidDefinitionException e) {
            assertTrue(e.getMessage().contains("Bad 'pattern' definition (\"Nanos\")"),
                    e.getMessage());
            assertTrue(e.getMessage().contains("expected one of ["), e.getMessage());
        }
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

    void __invoke_shouldDeserializeInNanos_whenNanosUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInNanos_whenNanosUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInMicros_whenMicrosUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInMicros_whenMicrosUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInMillis_whenMillisUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInMillis_whenMillisUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInSeconds_whenSecondsUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInSeconds_whenSecondsUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInMinutes_whenMinutesUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInMinutes_whenMinutesUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInHours_whenHoursUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInHours_whenHoursUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInHalfDays_whenHalfDaysUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInHalfDays_whenHalfDaysUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldDeserializeInDays_whenDaysUnitAsPattern_andValueIsIntegerVpack() throws Exception {
        try {
            shouldDeserializeInDays_whenDaysUnitAsPattern_andValueIsIntegerVpack();
        } finally {
        }
    }


    void __invoke_shouldFailForInvalidPatternVpack() throws Exception {
        try {
            shouldFailForInvalidPatternVpack();
        } finally {
        }
    }

}
