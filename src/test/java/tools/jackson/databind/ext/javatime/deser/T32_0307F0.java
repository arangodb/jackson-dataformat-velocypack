package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0307F0 {
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

    // Provenance: InstantDeser291Test#testNormalNumericalString.
    void testNormalNumericalStringVpack() throws Exception {
        assertEquals(Instant.ofEpochSecond(3L),
                STRING_TIMESTAMP_READER.readValue(STRING_3_SECONDS));
    }

    // Provenance: InstantDeser291Test#testNegativeNumericalString.
    void testNegativeNumericalStringVpack() throws Exception {
        assertEquals(Instant.ofEpochSecond(-3L),
                STRING_TIMESTAMP_READER.readValue(STRING_MINUS_3_SECONDS));
    }

    // Provenance: InstantDeser291Test#testAllowedPlusSignNumericalString.
    void testAllowedPlusSignNumericalStringVpack() throws Exception {
        assertEquals(Instant.ofEpochSecond(3L),
                STRING_TIMESTAMP_READER.readValue(STRING_PLUS_3_SECONDS));
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

    void __invoke_testNormalNumericalStringVpack() throws Exception {
        try {
            testNormalNumericalStringVpack();
        } finally {
        }
    }


    void __invoke_testNegativeNumericalStringVpack() throws Exception {
        try {
            testNegativeNumericalStringVpack();
        } finally {
        }
    }


    void __invoke_testAllowedPlusSignNumericalStringVpack() throws Exception {
        try {
            testAllowedPlusSignNumericalStringVpack();
        } finally {
        }
    }

}
