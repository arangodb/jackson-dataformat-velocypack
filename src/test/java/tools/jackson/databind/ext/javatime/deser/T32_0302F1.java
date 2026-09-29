package tools.jackson.databind.ext.javatime.deser;

import java.time.ZoneId;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0302F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] LOCAL_DATE_TIME_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 0e 29 e7 07 28 0d 28 0f 28 0c 28 1e 05");
private static final byte[] LOCAL_DATE_TIME_INVALID_TIME = VPackWireFixtureTest.hex(
            "13 0e 29 e7 07 28 02 28 0f 28 19 28 1e 05");
private static final byte[] LOCAL_DATE_TIME_INVALID_DATE_STRING = VPackWireFixtureTest.hex(
            "53 32 30 32 35 2d 30 32 2d 33 30 54 31 32 3a 30 30 3a 30 30");
private static final byte[] LOCAL_TIME_INVALID_HOUR = VPackWireFixtureTest.hex(
            "13 07 28 19 28 1e 02");
private static final byte[] LOCAL_TIME_INVALID_MINUTE = VPackWireFixtureTest.hex(
            "13 07 28 0c 28 3c 02");
private static final byte[] LOCAL_TIME_INVALID_MINUTE_STRING = VPackWireFixtureTest.hex(
            "48 31 32 3a 36 39 3a 30 30");
private static final byte[] MONTH_DAY_INVALID_DATE = VPackWireFixtureTest.hex(
            "13 07 28 02 28 1e 02");
private static final byte[] MONTH_DAY_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 07 28 0d 28 0f 02");
private static final byte[] YEAR_MONTH_INVALID_MONTH = VPackWireFixtureTest.hex(
            "13 07 29 e7 07 30 02");
private static final byte[] YEAR_MONTH_INVALID_MONTH_13 = VPackWireFixtureTest.hex(
            "13 08 29 e7 07 28 0d 02");
private static final byte[] YEAR_OUT_OF_RANGE = VPackWireFixtureTest.hex(
            "2b 00 ca 9a 3b");
private static final ObjectMapper TYPING_MAPPER = VPackMapper.builder()
            .activateDefaultTyping(new NoCheckSubTypeValidator())
            .build();

    // Provenance: DefaultTypingTest#testZoneIdAsIs.
    void testZoneIdAsIsVpack() throws Exception {
        ZoneId expected = ZoneId.of("America/Chicago");
        byte[] encoded = TYPING_MAPPER.writeValueAsBytes(expected);
        ZoneId actual = TYPING_MAPPER.readValue(encoded, ZoneId.class);
        assertEquals(expected, actual);
    }
static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testZoneIdAsIsVpack() throws Exception {
        try {
            testZoneIdAsIsVpack();
        } finally {
        }
    }

}
