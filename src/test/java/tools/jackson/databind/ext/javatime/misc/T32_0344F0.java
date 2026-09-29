package tools.jackson.databind.ext.javatime.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Year;
import java.time.temporal.TemporalAdjuster;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0344F0 {
private static final byte[] YEAR_1986 = VPackWireFixtureTest.hex("29 c2 07");
private static final byte[] TEMPORAL_ADJUSTER = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 0a 03");
private static final byte[] ONE = VPackWireFixtureTest.hex("31");
private static final byte[] TWO = VPackWireFixtureTest.hex("32");
private static final byte[] TWENTY_FOUR = VPackWireFixtureTest.hex("28 18");
private static final byte[] ONE_THOUSAND = VPackWireFixtureTest.hex("29 e8 03");

    // Provenance: JDKSerializabilityTest#testJDKSerializability.
    void testJDKSerializabilityVpack() throws Exception {
        Year input = Year.of(1986);
        VPackMapper mapper = new VPackMapper();
        byte[] before = mapper.writeValueAsBytes(input);

        VPackMapper thawedMapper = (VPackMapper) deserialize(serialize(mapper));
        byte[] after = thawedMapper.writeValueAsBytes(input);

        assertArrayEquals(YEAR_1986, before);
        assertArrayEquals(before, after);
        assertEquals(input, thawedMapper.readValue(YEAR_1986, Year.class));
    }
private static ObjectMapper mapperForPattern(String pattern) {
        return VPackMapper.builder()
                .withConfigOverride(Duration.class,
                        override -> override.setFormat(JsonFormat.Value.forPattern(pattern)))
                .enable(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(DateTimeFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
    }
private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }
private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return input.readObject();
        }
    }
static class TAWrapper {
        public TemporalAdjuster a;

        TAWrapper(TemporalAdjuster a) {
            this.a = a;
        }
    }

    void __invoke_testJDKSerializabilityVpack() throws Exception {
        try {
            testJDKSerializabilityVpack();
        } finally {
        }
    }

}
