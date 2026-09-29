package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeParseException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0307F1 {
private static final byte[] STRING_3_SECONDS = VPackWireFixtureTest.hex(
            "4b 33 2e 30 30 30 30 30 30 30 30 30");
private static final byte[] STRING_PLUS_3_SECONDS = VPackWireFixtureTest.hex(
            "4c 2b 33 2e 30 30 30 30 30 30 30 30 30");
private static final byte[] STRING_MINUS_3_SECONDS = VPackWireFixtureTest.hex(
            "4c 2d 33 2e 30 30 30 30 30 30 30 30 30");
private static final byte[] ZERO_SECONDS = VPackWireFixtureTest.hex(
            "c8 01 f7 ff ff ff 00");
private static final byte[] ONE_HUNDRED_TWENTY_THREE_MILLION_SECONDS =
            VPackWireFixtureTest.hex(
                    "c8 09 f7 ff ff ff 12 34 56 78 91 83 91 73 22");
private static final byte[] NINE_HUNDRED_EIGHTY_SEVEN_MILLION_SECONDS =
            VPackWireFixtureTest.hex(
                    "c8 09 f7 ff ff ff 98 76 54 32 11 23 45 67 89");
private static final byte[] INSTANT_MAX = VPackWireFixtureTest.hex(
            "c8 0d f7 ff ff ff 31 55 68 89 86 44 03 19 99 99 99 99 99");
private static final byte[] INSTANT_MIN = VPackWireFixtureTest.hex(
            "d0 09 ff ff ff ff 31 55 70 14 16 72 19 20 00");
private static final byte[] BELOW_INSTANT_MIN = VPackWireFixtureTest.hex(
            "d0 09 ff ff ff ff 31 55 70 14 16 72 19 20 01");
private static final byte[] ABOVE_INSTANT_MAX = VPackWireFixtureTest.hex(
            "c8 09 ff ff ff ff 31 55 68 89 86 44 03 20 00");
private static final byte[] ANNOTATED_INSTANT_STRING = VPackWireFixtureTest.hex(
            "0b 1f 01 45 76 61 6c 75 65 54 31 39 37 30 2d 30 31 2d 30 31 "
          + "54 30 30 3a 30 30 3a 30 30 5a 03");
private static final byte[] ANNOTATED_CUSTOM_PATTERN = VPackWireFixtureTest.hex(
            "0b 23 01 4a 76 61 6c 75 65 49 6e 55 54 43 53 31 39 37 31 2d "
          + "30 31 2d 30 31 20 30 30 3a 30 30 3a 30 30 03");
private static final ObjectReader STRING_TIMESTAMP_READER = VPackMapper.builder()
            .enable(DateTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS)
            .build()
            .readerFor(Instant.class);
private static final VPackMapper MAPPER = new VPackMapper();

    // Provenance: InstantDeserTest#testCustomPatternWithAnnotations01.
    void testCustomPatternWithAnnotations01Vpack() throws Exception {
        Wrapper input = new Wrapper(Instant.ofEpochMilli(0L));
        assertArrayEquals(ANNOTATED_INSTANT_STRING, MAPPER.writeValueAsBytes(input));

        Wrapper result = MAPPER.readValue(ANNOTATED_INSTANT_STRING, Wrapper.class);
        assertEquals(input.value, result.value);
    }

    // Provenance: InstantDeserTest#testCustomPatternWithAnnotations02.
    void testCustomPatternWithAnnotations02Vpack() throws Exception {
        Instant instant = Instant.parse("1971-01-01T00:00:00Z");
        WrapperWithCustomPattern input = new WrapperWithCustomPattern(instant);
        assertArrayEquals(ANNOTATED_CUSTOM_PATTERN, MAPPER.writeValueAsBytes(input));

        WrapperWithCustomPattern result = MAPPER.readValue(
                ANNOTATED_CUSTOM_PATTERN, WrapperWithCustomPattern.class);
        assertEquals(input.valueInUTC, result.valueInUTC);
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloat01.
    void testDeserializationAsFloat01Vpack() throws Exception {
        assertEquals(Instant.ofEpochSecond(0L),
                MAPPER.readValue(ZERO_SECONDS, Instant.class));
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloat02.
    void testDeserializationAsFloat02Vpack() throws Exception {
        assertEquals(Instant.ofEpochSecond(123456789L, 183917322L),
                MAPPER.readValue(ONE_HUNDRED_TWENTY_THREE_MILLION_SECONDS, Instant.class));
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloat03.
    void testDeserializationAsFloat03Vpack() throws Exception {
        Instant expected = Instant.ofEpochSecond(987654321L, 123456789L);
        assertEquals(expected,
                MAPPER.readValue(NINE_HUNDRED_EIGHTY_SEVEN_MILLION_SECONDS, Instant.class));
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase01.
    void testDeserializationAsFloatEdgeCase01Vpack() throws Exception {
        Instant value = MAPPER.readValue(INSTANT_MAX, Instant.class);
        assertEquals(Instant.MAX, value);
        assertEquals(Instant.MAX.getEpochSecond(), value.getEpochSecond());
        assertEquals(999999999, value.getNano());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase02.
    void testDeserializationAsFloatEdgeCase02Vpack() throws Exception {
        Instant value = MAPPER.readValue(INSTANT_MIN, Instant.class);
        assertEquals(Instant.MIN, value);
        assertEquals(Instant.MIN.getEpochSecond(), value.getEpochSecond());
        assertEquals(0, value.getNano());
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase03.
    void testDeserializationAsFloatEdgeCase03Vpack() {
        assertThrows(DateTimeParseException.class,
                () -> MAPPER.readValue(BELOW_INSTANT_MIN, Instant.class));
    }

    // Provenance: InstantDeserTest#testDeserializationAsFloatEdgeCase04.
    void testDeserializationAsFloatEdgeCase04Vpack() {
        assertThrows(DateTimeParseException.class,
                () -> MAPPER.readValue(ABOVE_INSTANT_MAX, Instant.class));
    }
static final class Wrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Instant value;

        public Wrapper() { }

        Wrapper(Instant value) {
            this.value = value;
        }
    }
static final class WrapperWithCustomPattern {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING,
                timezone = "UTC")
        public Instant valueInUTC;

        public WrapperWithCustomPattern() { }

        WrapperWithCustomPattern(Instant value) {
            valueInUTC = value;
        }
    }

    void __invoke_testCustomPatternWithAnnotations01Vpack() throws Exception {
        try {
            testCustomPatternWithAnnotations01Vpack();
        } finally {
        }
    }


    void __invoke_testCustomPatternWithAnnotations02Vpack() throws Exception {
        try {
            testCustomPatternWithAnnotations02Vpack();
        } finally {
        }
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


    void __invoke_testDeserializationAsFloatEdgeCase03Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase03Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsFloatEdgeCase04Vpack() throws Exception {
        try {
            testDeserializationAsFloatEdgeCase04Vpack();
        } finally {
        }
    }

}
