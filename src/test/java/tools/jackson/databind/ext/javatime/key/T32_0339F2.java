package tools.jackson.databind.ext.javatime.key;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0339F2 {
private static final LocalDateTime DATE_TIME_0 = LocalDateTime.of(1970, 1, 1, 0, 0);
private static final LocalDateTime DATE_TIME = LocalDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1_000_000);
private static final LocalTime TIME_0 = LocalTime.of(0, 0);
private static final LocalTime TIME = LocalTime.of(3, 14, 15, 920 * 1_000_000);
private static final MonthDay MONTH_DAY = MonthDay.of(3, 14);
private static final LocalDateTime HANDLED_DATE_TIME = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
private static final TypeReference<Map<LocalDateTime, String>> DATE_TIME_KEY_MAP =
            new TypeReference<Map<LocalDateTime, String>>() { };
private static final TypeReference<Map<LocalTime, String>> TIME_KEY_MAP =
            new TypeReference<Map<LocalTime, String>>() { };
private static final TypeReference<Map<MonthDay, String>> MONTH_DAY_KEY_MAP =
            new TypeReference<Map<MonthDay, String>>() { };
private static final byte[] DATE_TIME_0_KEY = VPackWireFixtureTest.hex(
            "0b 1a 01 50 31 39 37 30 2d 30 31 2d 30 31 54 30 30 3a 30 30"
                    + "44 74 65 73 74 03");
private static final byte[] DATE_TIME_KEY = VPackWireFixtureTest.hex(
            "0b 21 01 57 32 30 31 35 2d 30 33 2d 31 34 54 30 39 3a 32 36 3a 35 33 2e 35 39 30"
                    + "44 74 65 73 74 03");
private static final byte[] TIME_0_KEY = VPackWireFixtureTest.hex(
            "0b 0f 01 45 30 30 3a 30 30 44 74 65 73 74 03");
private static final byte[] TIME_KEY = VPackWireFixtureTest.hex(
            "0b 16 01 4c 30 33 3a 31 34 3a 31 35 2e 39 32 30 44 74 65 73 74 03");
private static final byte[] MONTH_DAY_KEY = VPackWireFixtureTest.hex(
            "0b 11 01 47 2d 2d 30 33 2d 31 34 44 74 65 73 74 03");
private static final byte[] HANDLED_DATE_TIME_KEY = VPackWireFixtureTest.hex(
            "0b 0d 01 43 6e 6f 77 44 74 65 73 74 03");

    // Provenance: MonthDayAsKeyTest#testSerialization.
    void monthDayAsKeySerializationVpack() throws Exception {
        assertArrayEquals(MONTH_DAY_KEY,
                new VPackMapper().writeValueAsBytes(Collections.singletonMap(MONTH_DAY, "test")));
    }

    // Provenance: MonthDayAsKeyTest#testDeserialization.
    void monthDayAsKeyDeserializationVpack() throws Exception {
        assertEquals(Collections.singletonMap(MONTH_DAY, "test"),
                new VPackMapper().readerFor(MONTH_DAY_KEY_MAP).readValue(MONTH_DAY_KEY));
    }

    void __invoke_monthDayAsKeySerializationVpack() throws Exception {
        try {
            monthDayAsKeySerializationVpack();
        } finally {
        }
    }


    void __invoke_monthDayAsKeyDeserializationVpack() throws Exception {
        try {
            monthDayAsKeyDeserializationVpack();
        } finally {
        }
    }

}
