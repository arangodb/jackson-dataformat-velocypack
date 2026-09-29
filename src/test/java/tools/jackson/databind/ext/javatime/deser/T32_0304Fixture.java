package tools.jackson.databind.ext.javatime.deser;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0304Fixture {
private static final byte[] SIXTY_POINT_ZERO = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 4e 40");
private static final byte[] THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT =
            VPackWireFixtureTest.hex("1b 07 3f 46 00 00 5d ca 40");
private static final byte[] MAX_DURATION = VPackWireFixtureTest.hex(
            "c8 0e f7 ff ff ff 92 23 37 20 36 85 47 75 80 79 99 99 99 99");
private static final byte[] MIN_DURATION = VPackWireFixtureTest.hex(
            "d0 0a ff ff ff ff 92 23 37 20 36 85 47 75 80 80");
private static final byte[] SINGLE_DURATION_ARRAY = VPackWireFixtureTest.hex(
            "13 18 54 50 54 33 48 34 34 4d 35 38 2e 30 30 30 30 30 38 33 37 34 53 01");
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");
private static final byte[] PATTERN_FLOAT = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 1b 00 00 00 00 00 80 39 40 01");
private static final byte[] PATTERN_STRING = VPackWireFixtureTest.hex(
            "14 0f 45 76 61 6c 75 65 45 50 54 32 35 53 01");

    // Provenance: DurationDeserTest#testDeserializationAsFloat01.
    void testDeserializationAsFloat01Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(SIXTY_POINT_ZERO);
        assertEquals(Duration.ofSeconds(60L, 0), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloat02.
    void testDeserializationAsFloat02Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(SIXTY_POINT_ZERO);
        assertEquals(Duration.ofSeconds(60L, 0), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloat03.
    void testDeserializationAsFloat03Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .with(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT);
        assertEquals(Duration.ofSeconds(13498L, 8374), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloat04.
    void testDeserializationAsFloat04Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(THIRTEEN_THOUSAND_FOUR_HUNDRED_NINETY_EIGHT);
        assertEquals(Duration.ofSeconds(13498L, 8374), value);
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase01.
    void testDeserializationAsFloatEdgeCase01Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(MAX_DURATION);
        assertEquals(Long.MAX_VALUE, value.getSeconds());
        assertEquals(999999999, value.getNano());
    }

    // Provenance: DurationDeserTest#testDeserializationAsFloatEdgeCase02.
    void testDeserializationAsFloatEdgeCase02Vpack() throws Exception {
        Duration value = new VPackMapper().readerFor(Duration.class)
                .without(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(MIN_DURATION);
        assertEquals(Long.MIN_VALUE, value.getSeconds());
        assertEquals(0, value.getNano());
    }

    // Provenance: DurationDeserTest#testDeserializationAsArrayDisabled.
    void testDeserializationAsArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(SINGLE_DURATION_ARRAY, Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationAsEmptyArrayDisabled.
    void testDeserializationAsEmptyArrayDisabledVpack() {
        assertThrows(MismatchedInputException.class,
                () -> new VPackMapper().readValue(EMPTY_ARRAY, Duration.class));
        ObjectMapper unwrappingMapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertThrows(MismatchedInputException.class,
                () -> unwrappingMapper.readValue(EMPTY_ARRAY, Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationAsArrayEnabled.
    void testDeserializationAsArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertEquals(Duration.ofSeconds(13498L, 8374),
                mapper.readValue(SINGLE_DURATION_ARRAY, Duration.class));
    }

    // Provenance: DurationDeserTest#testDeserializationAsEmptyArrayEnabled.
    void testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue(EMPTY_ARRAY, Duration.class));
    }

    // Provenance: DurationDeserTest#shouldIgnoreUnitPattern_whenValueIsFloat.
    void shouldIgnoreUnitPattern_whenValueIsFloatVpack() throws Exception {
        ObjectReader reader = mapperForPattern("MINUTES").readerFor(Wrapper.class);
        Wrapper wrapper = reader.readValue(PATTERN_FLOAT);
        assertEquals(Duration.parse("PT25.5S"), wrapper.value);
    }

    // Provenance: DurationDeserTest#shouldIgnoreUnitPattern_whenValueIsString.
    void shouldIgnoreUnitPattern_whenValueIsStringVpack() throws Exception {
        ObjectReader reader = mapperForPattern("MINUTES").readerFor(Wrapper.class);
        Wrapper wrapper = reader.readValue(PATTERN_STRING);
        assertEquals(Duration.parse("PT25S"), wrapper.value);
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

    void __invoke_testDeserializationAsFloat01Vpack() throws Exception {
        try {
            testDeserializationAsFloat01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat02Vpack() throws Exception {
        try {
            testDeserializationAsFloat02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat03Vpack() throws Exception {
        try {
            testDeserializationAsFloat03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloat04Vpack() throws Exception {
        try {
            testDeserializationAsFloat04Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase01Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase02Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayDisabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayDisabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsEmptyArrayEnabledVpack() throws Exception {
        try {
            testDeserializationAsEmptyArrayEnabledVpack();
        } finally {
        }
    }


    void __invoke_shouldIgnoreUnitPattern_whenValueIsFloatVpack() throws Exception {
        try {
            shouldIgnoreUnitPattern_whenValueIsFloatVpack();
        } finally {
        }
    }


    void __invoke_shouldIgnoreUnitPattern_whenValueIsStringVpack() throws Exception {
        try {
            shouldIgnoreUnitPattern_whenValueIsStringVpack();
        } finally {
        }
    }

}
