package tools.jackson.databind.deser.jdk;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0259Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] ANNOTATED_DATE = VPackWireFixtureTest.hex(
            "14 15 44 64 61 74 65 4c 2f 32 30 30 35 2f 30 35 2f 32 35 2f 01");
private static final byte[] NEGATIVE_DATE = VPackWireFixtureTest.hex(
            "14 11 44 64 61 74 65 27 51 60 2c fc bd fe ff ff 01");
private static final byte[] AMBIGUOUS_DATE = VPackWireFixtureTest.hex(
            "14 0d 44 64 61 74 65 23 d1 25 35 01 01");
private static final byte[] DATE_LONG = VPackWireFixtureTest.hex(
            "2b 15 cd 5b 07");
private static final byte[] DATE_TEXT = VPackWireFixtureTest.hex(
            "58 31 39 37 30 2d 30 31 2d 30 32 54 31 30 3a 31 37 3a 33 36 2e 37 38 39 5a");
private static final byte[] DATE_MAX = VPackWireFixtureTest.hex(
            "2f ff ff ff ff ff ff ff 7f");
private static final byte[] DATE_MIN = VPackWireFixtureTest.hex(
            "27 00 00 00 00 00 00 00 80");

    // Provenance: DateDeserializationTest#testDateUtil.
    void testDateUtil() throws Exception {
        assertEquals(123456789L, MAPPER.readValue(DATE_LONG, Date.class).getTime());
        assertEquals(123456789L, MAPPER.readValue(DATE_TEXT, Date.class).getTime());
    }

    // Provenance: DateDeserializationTest#testCustomDateWithAnnotation.
    void testCustomDateWithAnnotation() throws Exception {
        DateAsStringBean result = MAPPER.readValue(ANNOTATED_DATE, DateAsStringBean.class);
        assertNotNull(result);
        assertNotNull(result.date);
        assertDateFields(result.date, 2005, Calendar.MAY, 25);

        result = MAPPER.readerFor(DateAsStringBean.class)
                .with(Locale.GERMANY)
                .readValue(ANNOTATED_DATE);
        assertNotNull(result.date);
        assertDateFields(result.date, 2005, Calendar.MAY, 25);

        DateAsStringBeanGermany german = MAPPER.readerFor(DateAsStringBeanGermany.class)
                .readValue(ANNOTATED_DATE);
        assertNotNull(german.date);
        assertDateFields(german.date, 2005, Calendar.MAY, 25);
    }

    // Provenance: DateDeserializationTest#testDateAsInteger.
    void testDateAsInteger() throws Exception {
        DateShapeNumberIntBean result = MAPPER.readValue(NEGATIVE_DATE,
                DateShapeNumberIntBean.class);
        assertEquals(-1383043669935L, result.date.getTime());
        assertDateFields(result.date, 1926, Calendar.MARCH, 5);
    }

    // Provenance: DateDeserializationTest#testDateAsIntegerAmbiguous.
    void testDateAsIntegerAmbiguous() throws Exception {
        DateShapeNumberIntBean result = MAPPER.readValue(AMBIGUOUS_DATE,
                DateShapeNumberIntBean.class);
        assertEquals(20260305L, result.date.getTime());
    }

    // Provenance: DateDeserializationTest#testDateAsNumber.
    void testDateAsNumber() throws Exception {
        DateShapeNumberBean result = MAPPER.readValue(NEGATIVE_DATE,
                DateShapeNumberBean.class);
        assertEquals(-1383043669935L, result.date.getTime());
        assertDateFields(result.date, 1926, Calendar.MARCH, 5);
    }

    // Provenance: DateDeserializationTest#testDateRoundTripWithMaxValue.
    void testDateRoundTripWithMaxValue() throws Exception {
        assertDateRoundTrip(Long.MAX_VALUE, DATE_MAX);
    }

    // Provenance: DateDeserializationTest#testDateRoundTripWithMinValue.
    void testDateRoundTripWithMinValue() throws Exception {
        assertDateRoundTrip(Long.MIN_VALUE, DATE_MIN);
    }
private static void assertDateRoundTrip(long epochMillis, byte[] literal) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals(epochMillis, mapper.readValue(literal, Date.class).getTime());
        Date parsed = mapper.readValue(mapper.writeValueAsBytes(new Date(epochMillis)), Date.class);
        assertEquals(epochMillis, parsed.getTime());
    }
private static void assertDateFields(Date date, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        calendar.setTime(date);
        assertEquals(year, calendar.get(Calendar.YEAR));
        assertEquals(month, calendar.get(Calendar.MONTH));
        assertEquals(day, calendar.get(Calendar.DAY_OF_MONTH));
    }
static class DateAsStringBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "/yyyy/MM/dd/", locale = "fr_FR")
        public Date date;
    }
static class DateAsStringBeanGermany {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "/yyyy/MM/dd/", locale = "fr_FR")
        public Date date;
    }
static class DateShapeNumberBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        public Date date;
    }
static class DateShapeNumberIntBean {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Date date;
    }

    void __invoke_testDateUtil() throws Exception {
        try {
            testDateUtil();
        } finally {
        }
    }


    void __invoke_testCustomDateWithAnnotation() throws Exception {
        try {
            testCustomDateWithAnnotation();
        } finally {
        }
    }


    void __invoke_testDateAsInteger() throws Exception {
        try {
            testDateAsInteger();
        } finally {
        }
    }


    void __invoke_testDateAsIntegerAmbiguous() throws Exception {
        try {
            testDateAsIntegerAmbiguous();
        } finally {
        }
    }


    void __invoke_testDateAsNumber() throws Exception {
        try {
            testDateAsNumber();
        } finally {
        }
    }


    void __invoke_testDateRoundTripWithMaxValue() throws Exception {
        try {
            testDateRoundTripWithMaxValue();
        } finally {
        }
    }


    void __invoke_testDateRoundTripWithMinValue() throws Exception {
        try {
            testDateRoundTripWithMinValue();
        } finally {
        }
    }

}
