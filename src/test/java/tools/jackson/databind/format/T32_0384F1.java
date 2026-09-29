package tools.jackson.databind.format;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0384F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] COLLECTION_AS_POJO = VPackWireFixtureTest.hex(
            "0b 18 02 44 73 69 7a 65 32 46 76 61 6c 75 65 73 "
          + "02 06 41 61 41 62 03 09");
private static final byte[] DATE_GERMAN = VPackWireFixtureTest.hex(
            "0b 17 01 45 76 61 6c 75 65 4c 31 35 20 4f 6b 74 2e "
          + "20 32 30 32 32 03");
private static final byte[] DATE_ITALIAN = VPackWireFixtureTest.hex(
            "0b 16 01 45 76 61 6c 75 65 4b 31 35 20 6f 74 74 20 "
          + "32 30 32 32 03");
private static final byte[] DATE_FRENCH = VPackWireFixtureTest.hex(
            "0b 1a 01 45 76 61 6c 75 65 4f 31 35 20 6f 63 74 6f 62 72 65 "
          + "20 32 30 32 32 03");
private static final byte[] DATE_TYPE_DEFAULTS_INPUT = VPackWireFixtureTest.hex(
            "0b 14 01 45 76 61 6c 75 65 49 31 39 38 31 2e 31 33 2e 33 03");
private static final byte[] DATE_TYPE_DEFAULTS_OUTPUT = VPackWireFixtureTest.hex(
            "0b 15 01 45 76 61 6c 75 65 4a 31 39 37 30 2e 30 31 2e 30 31 03");

    // Provenance: DateFormatLocale3316Test#testSerializationWithUnderscoreLocale().
    void testSerializationWithUnderscoreLocaleVpack() throws Exception {
        DateWithLocaleUnderscore input = new DateWithLocaleUnderscore();
        input.value = date20221015();
        assertArrayEquals(DATE_GERMAN, MAPPER.writeValueAsBytes(input));
    }

    // Provenance: DateFormatLocale3316Test#testSerializationWithHyphenLocale().
    void testSerializationWithHyphenLocaleVpack() throws Exception {
        DateWithLocaleHyphen input = new DateWithLocaleHyphen();
        input.value = date20221015();
        assertArrayEquals(DATE_GERMAN, MAPPER.writeValueAsBytes(input));
    }

    // Provenance: DateFormatLocale3316Test#testSerializationWithLanguageOnlyLocale().
    void testSerializationWithLanguageOnlyLocaleVpack() throws Exception {
        DateWithLocaleLanguageOnly input = new DateWithLocaleLanguageOnly();
        input.value = date20221015();
        assertArrayEquals(DATE_GERMAN, MAPPER.writeValueAsBytes(input));
    }

    // Provenance: DateFormatLocale3316Test#testRoundTripWithCompoundLocale().
    void testRoundTripWithCompoundLocaleVpack() throws Exception {
        DateWithLocaleUnderscore input = new DateWithLocaleUnderscore();
        input.value = date20221015();

        assertArrayEquals(DATE_GERMAN, MAPPER.writeValueAsBytes(input));
        DateWithLocaleUnderscore result = MAPPER.readValue(DATE_GERMAN,
                DateWithLocaleUnderscore.class);
        assertDate20221015(result.value);
    }

    // Provenance: DateFormatLocale3316Test#testSerializationWithLocaleVariant().
    void testSerializationWithLocaleVariantVpack() throws Exception {
        DateWithLocaleVariant input = new DateWithLocaleVariant();
        input.value = date20221015();
        assertArrayEquals(DATE_ITALIAN, MAPPER.writeValueAsBytes(input));
    }

    // Provenance: DateFormatLocale3316Test#testLocalDateWithCompoundLocale().
    void testLocalDateWithCompoundLocaleVpack() throws Exception {
        LocalDateWithLocale input = new LocalDateWithLocale();
        input.value = LocalDate.of(2022, 10, 15);

        assertArrayEquals(DATE_GERMAN, MAPPER.writeValueAsBytes(input));
        LocalDateWithLocale result = MAPPER.readValue(DATE_GERMAN,
                LocalDateWithLocale.class);
        assertEquals(LocalDate.of(2022, 10, 15), result.value);
    }

    // Provenance: DateFormatLocale3316Test#testSerializationWithFrenchLocale().
    void testSerializationWithFrenchLocaleVpack() throws Exception {
        DateWithFrenchLocale input = new DateWithFrenchLocale();
        input.value = date20221015();
        assertArrayEquals(DATE_FRENCH, MAPPER.writeValueAsBytes(input));
    }

    // Provenance: DateFormatLocale3316Test#testDeserializationWithFrenchLocale().
    void testDeserializationWithFrenchLocaleVpack() throws Exception {
        DateWithFrenchLocale result = MAPPER.readValue(DATE_FRENCH,
                DateWithFrenchLocale.class);
        assertDate20221015(result.value);
    }
private static Date date20221015() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ROOT);
        calendar.clear();
        calendar.set(2022, Calendar.OCTOBER, 15);
        return calendar.getTime();
    }
private static void assertDate20221015(Date value) {
        assertNotNull(value);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(value);
        assertEquals(2022, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, calendar.get(Calendar.MONTH));
        assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
    }
@JsonPropertyOrder({ "size", "value" })
    @JsonFormat(shape = JsonFormat.Shape.POJO)
    @JsonIgnoreProperties({ "empty", "first", "last" })
    static class CollectionAsPOJO extends ArrayList<String> {
        private static final long serialVersionUID = 1L;

        @JsonProperty("size")
        public int foo() {
            return size();
        }

        public List<String> getValues() {
            return new ArrayList<>(this);
        }

        public void setValues(List<String> values) {
            addAll(values);
        }

        public void setSize(int ignored) { }
    }
static class DateWithLocaleUnderscore {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy",
                locale = "de_DE", timezone = "UTC")
        public Date value;
    }
static class DateWithLocaleHyphen {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy",
                locale = "de-DE", timezone = "UTC")
        public Date value;
    }
static class DateWithLocaleLanguageOnly {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy",
                locale = "de", timezone = "UTC")
        public Date value;
    }
static class DateWithLocaleVariant {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy",
                locale = "it_IT_POSIX", timezone = "UTC")
        public Date value;
    }
static class LocalDateWithLocale {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy",
                locale = "de_DE")
        public LocalDate value;
    }
static class DateWithFrenchLocale {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy",
                locale = "fr_FR", timezone = "UTC")
        public Date value;
    }
static class DateWrapper {
        public Date value;

        DateWrapper() { }

        DateWrapper(long timestamp) {
            value = new Date(timestamp);
        }
    }

    void __invoke_testSerializationWithUnderscoreLocaleVpack() throws Exception {
        try {
            testSerializationWithUnderscoreLocaleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithHyphenLocaleVpack() throws Exception {
        try {
            testSerializationWithHyphenLocaleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithLanguageOnlyLocaleVpack() throws Exception {
        try {
            testSerializationWithLanguageOnlyLocaleVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripWithCompoundLocaleVpack() throws Exception {
        try {
            testRoundTripWithCompoundLocaleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithLocaleVariantVpack() throws Exception {
        try {
            testSerializationWithLocaleVariantVpack();
        } finally {
        }
    }


    void __invoke_testLocalDateWithCompoundLocaleVpack() throws Exception {
        try {
            testLocalDateWithCompoundLocaleVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithFrenchLocaleVpack() throws Exception {
        try {
            testSerializationWithFrenchLocaleVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationWithFrenchLocaleVpack() throws Exception {
        try {
            testDeserializationWithFrenchLocaleVpack();
        } finally {
        }
    }

}
