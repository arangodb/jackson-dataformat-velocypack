package tools.jackson.databind.ext.javatime.key;

import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0341F0 {
private static final Period PERIOD_0 = Period.of(0, 0, 0);
private static final Period PERIOD = Period.of(3, 1, 4);
private static final Year YEAR = Year.of(3141);
private static final Year YEAR_1 = Year.of(1);
private static final YearMonth YEAR_MONTH = YearMonth.of(3141, 5);
private static final TypeReference<Map<Period, String>> PERIOD_KEY_MAP =
            new TypeReference<Map<Period, String>>() { };
private static final TypeReference<Map<Year, String>> YEAR_KEY_MAP =
            new TypeReference<Map<Year, String>>() { };
private static final TypeReference<Map<Year, Float>> YEAR_FLOAT_KEY_MAP =
            new TypeReference<Map<Year, Float>>() { };
private static final TypeReference<Map<YearMonth, String>> YEAR_MONTH_KEY_MAP =
            new TypeReference<Map<YearMonth, String>>() { };
private static final byte[] PERIOD_0_KEY = VPackWireFixtureTest.hex(
            "0b 0d 01 43 50 30 44 44 74 65 73 74 03");
private static final byte[] PERIOD_KEY = VPackWireFixtureTest.hex(
            "0b 11 01 47 50 33 59 31 4d 34 44 44 74 65 73 74 03");
private static final byte[] YEAR_KEY = VPackWireFixtureTest.hex(
            "0b 0e 01 44 33 31 34 31 44 74 65 73 74 03");
private static final byte[] YEAR_PADDED_KEY = VPackWireFixtureTest.hex(
            "0b 0e 01 44 30 34 37 36 44 74 65 73 74 03");
private static final byte[] YEAR_UNPADDED_KEY = VPackWireFixtureTest.hex(
            "0b 0d 01 43 34 37 36 44 74 65 73 74 03");
private static final byte[] YEAR_NOT_NUMBER_KEY = VPackWireFixtureTest.hex(
            "0b 11 01 47 31 30 30 30 30 42 43 44 74 65 73 74 03");
private static final byte[] YEAR_NOT_YEAR_KEY = VPackWireFixtureTest.hex(
            "0b 14 01 4a 31 30 30 30 30 30 30 30 30 30 44 74 65 73 74 03");
private static final byte[] YEAR_ONE_FLOAT_KEY = VPackWireFixtureTest.hex(
            "0b 0f 01 41 31 1b 00 00 00 00 00 00 f0 3f 03");
private static final byte[] YEAR_ONE_PADDED_FLOAT_KEY = VPackWireFixtureTest.hex(
            "0b 12 01 44 30 30 30 31 1b 00 00 00 00 00 00 f0 3f 03");
private static final byte[] YEAR_MONTH_KEY = VPackWireFixtureTest.hex(
            "0b 11 01 47 33 31 34 31 2d 30 35 44 74 65 73 74 03");

    // Provenance: PeriodAsKeyTest#testSerialization0.
    void periodAsKeySerialization0Vpack() throws Exception {
        assertArrayEquals(PERIOD_0_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(PERIOD_0, "test")));
    }

    // Provenance: PeriodAsKeyTest#testSerialization1.
    void periodAsKeySerialization1Vpack() throws Exception {
        assertArrayEquals(PERIOD_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(PERIOD, "test")));
    }

    // Provenance: PeriodAsKeyTest#testDeserialization0.
    void periodAsKeyDeserialization0Vpack() throws Exception {
        assertEquals(Collections.singletonMap(PERIOD_0, "test"),
                new VPackMapper().readerFor(PERIOD_KEY_MAP).readValue(PERIOD_0_KEY));
    }

    // Provenance: PeriodAsKeyTest#testDeserialization1.
    void periodAsKeyDeserialization1Vpack() throws Exception {
        assertEquals(Collections.singletonMap(PERIOD, "test"),
                new VPackMapper().readerFor(PERIOD_KEY_MAP).readValue(PERIOD_KEY));
    }

    void __invoke_periodAsKeySerialization0Vpack() throws Exception {
        try {
            periodAsKeySerialization0Vpack();
        } finally {
        }
    }


    void __invoke_periodAsKeySerialization1Vpack() throws Exception {
        try {
            periodAsKeySerialization1Vpack();
        } finally {
        }
    }


    void __invoke_periodAsKeyDeserialization0Vpack() throws Exception {
        try {
            periodAsKeyDeserialization0Vpack();
        } finally {
        }
    }


    void __invoke_periodAsKeyDeserialization1Vpack() throws Exception {
        try {
            periodAsKeyDeserialization1Vpack();
        } finally {
        }
    }

}
