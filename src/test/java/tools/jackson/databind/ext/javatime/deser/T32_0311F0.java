package tools.jackson.databind.ext.javatime.deser;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0311F0 {
private static final Instant ISSUED_AT =
            Instant.ofEpochSecond(1_234_567_890L, 123_456_789L);
private static final byte[] BIG_DECIMAL_WRAPPER = VPackWireFixtureTest.hex(
            "14 19 45 76 61 6c 75 65 c8 0a f7 ff ff ff "
          + "01 23 45 67 89 01 23 45 67 89 01");
private static final byte[] NULL_INSTANT = VPackWireFixtureTest.hex(
            "14 0c 47 69 6e 73 74 61 6e 74 18 01");
private static final byte[] EMPTY_INSTANT = VPackWireFixtureTest.hex(
            "14 0c 47 69 6e 73 74 61 6e 74 40 01");
private static final byte[] NEGATIVE_ONE_SECONDS = VPackWireFixtureTest.hex(
            "d0 05 f7 ff ff ff 10 00 00 00 01");
private static final byte[] NEGATIVE_ONE_NANOSECOND = VPackWireFixtureTest.hex(
            "d0 01 f7 ff ff ff 01");

    // Provenance: InstantDeserTest#testRoundTripOfInstantAndJavaUtilDate.
    void testRoundTripOfInstantAndJavaUtilDateVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DateTimeFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .build();

        Instant givenInstant = LocalDate.of(2016, 1, 1).atStartOfDay()
                .atZone(ZoneOffset.UTC).toInstant();
        byte[] encodedDate = mapper.writeValueAsBytes(Date.from(givenInstant));

        assertEquals(givenInstant, mapper.readValue(encodedDate, Instant.class));
    }

    // Provenance: InstantDeserTest#testStrictDeserializeFromEmptyString.
    void testStrictDeserializeFromEmptyStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Instant.class,
                        override -> override.setFormat(
                                com.fasterxml.jackson.annotation.JsonFormat.Value.forLeniency(false)))
                .build();

        Map<String, Instant> nullMap = mapper.readValue(NULL_INSTANT,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Instant.class));
        assertNull(nullMap.get("instant"));
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(EMPTY_INSTANT,
                mapper.getTypeFactory().constructMapType(Map.class, String.class, Instant.class)));
    }
public static class Wrapper307 {
        public Instant value;

        public Wrapper307() { }
    }

    void __invoke_testRoundTripOfInstantAndJavaUtilDateVpack() throws Exception {
        try {
            testRoundTripOfInstantAndJavaUtilDateVpack();
        } finally {
        }
    }


    void __invoke_testStrictDeserializeFromEmptyStringVpack() throws Exception {
        try {
            testStrictDeserializeFromEmptyStringVpack();
        } finally {
        }
    }

}
