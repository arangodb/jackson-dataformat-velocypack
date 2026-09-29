package tools.jackson.databind.ext.javatime.key;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0338F1 {
private static final Duration DURATION = Duration.ofMinutes(13).plusSeconds(37)
            .plusNanos(120 * 1000 * 1000L);
private static final Instant INSTANT_0 = Instant.ofEpochMilli(0);
private static final Instant INSTANT = Instant.ofEpochSecond(1426325213L, 590000000L);
private static final LocalDate DATE = LocalDate.of(2015, 3, 14);
private static final TypeReference<Map<Duration, String>> DURATION_KEY_MAP =
            new TypeReference<Map<Duration, String>>() { };
private static final TypeReference<Map<Instant, String>> INSTANT_KEY_MAP =
            new TypeReference<Map<Instant, String>>() { };
private static final TypeReference<Map<LocalDate, String>> DATE_KEY_MAP =
            new TypeReference<Map<LocalDate, String>>() { };
private static final byte[] DURATION_KEY = VPackWireFixtureTest.hex(
            "0b 15 01 4b 50 54 31 33 4d 33 37 2e 31 32 53 44 74 65 73 74 03");
private static final byte[] INSTANT_0_KEY = VPackWireFixtureTest.hex(
            "0b 1e 01 54 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30 3a 30 30 5a"
                    + "44 74 65 73 74 03");
private static final byte[] INSTANT_KEY = VPackWireFixtureTest.hex(
            "0b 22 01 58 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39 30 5a"
                    + "44 74 65 73 74 03");
private static final byte[] DATE_KEY = VPackWireFixtureTest.hex(
            "0b 14 01 4a 32 30 31 35 2d 30 33 2d 31 34 44 74 65 73 74 03");

    // Provenance: InstantAsKeyTest#testSerialization0.
    void instantAsKeySerialization0Vpack() throws Exception {
        assertArrayEquals(INSTANT_0_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(INSTANT_0, "test")));
    }

    // Provenance: InstantAsKeyTest#testSerialization1.
    void instantAsKeySerialization1Vpack() throws Exception {
        assertArrayEquals(INSTANT_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(INSTANT, "test")));
    }

    // Provenance: InstantAsKeyTest#testDeserialization0.
    void instantAsKeyDeserialization0Vpack() throws Exception {
        assertEquals(Collections.singletonMap(INSTANT_0, "test"),
                new VPackMapper().readerFor(INSTANT_KEY_MAP).readValue(INSTANT_0_KEY));
    }

    // Provenance: InstantAsKeyTest#testDeserialization1.
    void instantAsKeyDeserialization1Vpack() throws Exception {
        assertEquals(Collections.singletonMap(INSTANT, "test"),
                new VPackMapper().readerFor(INSTANT_KEY_MAP).readValue(INSTANT_KEY));
    }

    void __invoke_instantAsKeySerialization0Vpack() throws Exception {
        try {
            instantAsKeySerialization0Vpack();
        } finally {
        }
    }


    void __invoke_instantAsKeySerialization1Vpack() throws Exception {
        try {
            instantAsKeySerialization1Vpack();
        } finally {
        }
    }


    void __invoke_instantAsKeyDeserialization0Vpack() throws Exception {
        try {
            instantAsKeyDeserialization0Vpack();
        } finally {
        }
    }


    void __invoke_instantAsKeyDeserialization1Vpack() throws Exception {
        try {
            instantAsKeyDeserialization1Vpack();
        } finally {
        }
    }

}
