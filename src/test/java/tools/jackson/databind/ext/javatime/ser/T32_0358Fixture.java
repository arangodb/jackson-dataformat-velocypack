package tools.jackson.databind.ext.javatime.ser;

import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0358Fixture {
private static final byte[] TIMESTAMP_01 = VPackWireFixtureTest.hex(
            "06 09 02 29 c2 07 31 03 06");
private static final byte[] TIMESTAMP_02 = VPackWireFixtureTest.hex(
            "06 09 02 29 dd 07 38 03 06");
private static final byte[] STRING_01 = VPackWireFixtureTest.hex(
            "47 31 39 38 36 2d 30 31");
private static final byte[] STRING_02 = VPackWireFixtureTest.hex(
            "47 32 30 31 33 2d 30 38");
private static final byte[] TYPE_INFO = VPackWireFixtureTest.hex(
            "06 21 02 53 6a 61 76 61 2e 74 69 6d 65 2e 59 65 61 72 4d 6f 6e 74 68 "
          + "47 32 30 30 35 2d 31 31 03 17");
private static final byte[] PATTERN = VPackWireFixtureTest.hex(
            "0b 13 01 49 79 65 61 72 4d 6f 6e 74 68 44 31 33 30 38 03");

    // Provenance: YearMonthSerializationTest#testSerializationAsTimestamp01.
    void testSerializationAsTimestamp01Vpack() throws Exception {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        assertArrayEquals(TIMESTAMP_01, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
                .writeValueAsBytes(yearMonth));
    }

    // Provenance: YearMonthSerializationTest#testSerializationAsTmestamp02.
    void testSerializationAsTmestamp02Vpack() throws Exception {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        assertArrayEquals(TIMESTAMP_02, VPackMapper.builder()
                .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
                .writeValueAsBytes(yearMonth));
    }

    // Provenance: YearMonthSerializationTest#testSerializationAsString01.
    void testSerializationAsString01Vpack() throws Exception {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        assertArrayEquals(STRING_01, VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
                .writeValueAsBytes(yearMonth));
    }

    // Provenance: YearMonthSerializationTest#testSerializationAsString02.
    void testSerializationAsString02Vpack() throws Exception {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        assertArrayEquals(STRING_02, VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build()
                .writeValueAsBytes(yearMonth));
    }

    // Provenance: YearMonthSerializationTest#testSerializationWithTypeInfo01.
    void testSerializationWithTypeInfo01Vpack() throws Exception {
        YearMonth yearMonth = YearMonth.of(2005, Month.NOVEMBER);
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addMixIn(Temporal.class, YearMonthTypeInfo.class)
                .build();
        assertArrayEquals(TYPE_INFO, mapper.writeValueAsBytes(yearMonth));
    }

    // Provenance: YearMonthSerializationTest#testDeserializationAsTimestamp01.
    void testDeserializationAsTimestamp01Vpack() throws Exception {
        assertEquals(YearMonth.of(1986, Month.JANUARY),
                new VPackMapper().readValue(TIMESTAMP_01, YearMonth.class));
    }

    // Provenance: YearMonthSerializationTest#testDeserializationAsTimestamp02.
    void testDeserializationAsTimestamp02Vpack() throws Exception {
        assertEquals(YearMonth.of(2013, Month.AUGUST),
                new VPackMapper().readValue(TIMESTAMP_02, YearMonth.class));
    }

    // Provenance: YearMonthSerializationTest#testDeserializationAsString01.
    void testDeserializationAsString01Vpack() throws Exception {
        YearMonth value = new VPackMapper().readValue(STRING_01, YearMonth.class);
        assertNotNull(value);
        assertEquals(YearMonth.of(1986, Month.JANUARY), value);
    }

    // Provenance: YearMonthSerializationTest#testDeserializationAsString02.
    void testDeserializationAsString02Vpack() throws Exception {
        assertEquals(YearMonth.of(2013, Month.AUGUST),
                new VPackMapper().readValue(STRING_02, YearMonth.class));
    }

    // Provenance: YearMonthSerializationTest#testDeserializationWithTypeInfo01.
    void testDeserializationWithTypeInfo01Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Temporal.class, YearMonthTypeInfo.class)
                .build();
        Temporal value = mapper.readValue(TYPE_INFO, Temporal.class);
        assertInstanceOf(YearMonth.class, value, "The value should be a YearMonth.");
        assertEquals(YearMonth.of(2005, Month.NOVEMBER), value);
    }

    // Provenance: YearMonthSerializationTest#testSerializationWithPattern01.
    void testSerializationWithPattern01Vpack() throws Exception {
        SimpleAggregate aggregate = new SimpleAggregate(YearMonth.of(2013, Month.AUGUST));
        assertArrayEquals(PATTERN, new VPackMapper().writeValueAsBytes(aggregate));
    }

    // Provenance: YearMonthSerializationTest#testDeserializationWithPattern01.
    void testDeserializationWithPattern01Vpack() throws Exception {
        SimpleAggregate value = new VPackMapper().readValue(PATTERN, SimpleAggregate.class);
        assertEquals(YearMonth.of(2013, Month.AUGUST), value.yearMonth);
    }
private static class SimpleAggregate {
        @JsonProperty("yearMonth")
        @JsonFormat(pattern = "yyMM")
        final YearMonth yearMonth;

        @JsonCreator
        SimpleAggregate(@JsonProperty("yearMonth") YearMonth yearMonth) {
            this.yearMonth = yearMonth;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY,
            property = "@class")
    private interface YearMonthTypeInfo { }

    void __invoke_testSerializationAsTimestamp01Vpack() throws Exception {
        try {
            testSerializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsTmestamp02Vpack() throws Exception {
        try {
            testSerializationAsTmestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsString01Vpack() throws Exception {
        try {
            testSerializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationAsString02Vpack() throws Exception {
        try {
            testSerializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithTypeInfo01Vpack() throws Exception {
        try {
            testSerializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp01Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsTimestamp02Vpack() throws Exception {
        try {
            testDeserializationAsTimestamp02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString01Vpack() throws Exception {
        try {
            testDeserializationAsString01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationAsString02Vpack() throws Exception {
        try {
            testDeserializationAsString02Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithTypeInfo01Vpack() throws Exception {
        try {
            testDeserializationWithTypeInfo01Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithPattern01Vpack() throws Exception {
        try {
            testSerializationWithPattern01Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithPattern01Vpack() throws Exception {
        try {
            testDeserializationWithPattern01Vpack();
        } finally {
        }
    }

}
