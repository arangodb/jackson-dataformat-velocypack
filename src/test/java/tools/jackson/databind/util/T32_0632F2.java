package tools.jackson.databind.util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.introspect.AnnotatedField;
import tools.jackson.databind.util.StdDateFormat;
import static org.junit.jupiter.api.Assertions.*;

import tools.jackson.dataformat.velocypack.*;

class T32_0632F2 {
private final VPackMapper mapper = new VPackMapper();
private DeserializationConfig config() { return mapper.deserializationConfig(); }
private AnnotatedField fieldOf() {
        var type = mapper.constructType(SimpleBean.class);
        var annotatedClass = tools.jackson.databind.introspect.AnnotatedClassResolver
                .resolve(config(), type, config());
        return annotatedClass.fields().iterator().next();
    }

    void dateFormatLenientDefaults() {
        StdDateFormat format = StdDateFormat.instance;
        assertTrue(format.isLenient());
        StdDateFormat clone = format.clone();
        assertTrue(clone.isLenient());
        clone.setLenient(false);
        assertFalse(clone.isLenient());
        clone.setLenient(true);
        assertTrue(clone.isLenient());
        clone.setLenient(false);
        assertFalse(clone.isLenient());
        assertFalse(clone.clone().isLenient());
    }

    void dateFormatIso8601DateOnlyPattern() {
        Pattern pattern = new TestStdDateFormat().plainPattern();
        Matcher matcher = pattern.matcher("1997-07-16");
        assertTrue(matcher.matches());
    }

    void dateFormatIso8601FullPattern() {
        Pattern pattern = new TestStdDateFormat().iso8601Pattern();
        Matcher matcher = pattern.matcher("1997-07-16T19:20:00+01:00");
        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertNull(matcher.group(1));
        assertEquals("+01:00", matcher.group(2));

        matcher = pattern.matcher("1997-07-16T19:20:00Z");
        assertTrue(matcher.matches());
        assertNull(matcher.group(1));
        assertEquals("Z", matcher.group(2));

        matcher = pattern.matcher("1997-07-16T19:20+01:00");
        assertTrue(matcher.matches());
        assertNull(matcher.group(1));
        assertEquals("+01:00", matcher.group(2));

        matcher = pattern.matcher("1997-07-16T19:20:00.2+03:00");
        assertTrue(matcher.matches());
        assertEquals(2, matcher.groupCount());
        assertEquals(".2", matcher.group(1));
        assertEquals("+03:00", matcher.group(2));

        matcher = pattern.matcher("1972-12-28T00:00:00.01-0300");
        assertTrue(matcher.matches());
        assertEquals(".01", matcher.group(1));
        assertEquals("-0300", matcher.group(2));

        matcher = pattern.matcher("1972-12-28T00:00:00.400+00");
        assertTrue(matcher.matches());
        assertEquals(".400", matcher.group(1));
        assertEquals("+00", matcher.group(2));

        matcher = pattern.matcher("1972-12-28T04:15");
        assertTrue(matcher.matches());
        assertNull(matcher.group(1));
        assertNull(matcher.group(2));
    }

    void dateFormatLenientParsing() throws Exception {
        StdDateFormat format = StdDateFormat.instance.clone();
        format.setLenient(false);
        assertNotNull(format.parse("2015-11-30"));
        try {
            format.parse("2015-11-32");
            fail("Should not pass");
        } catch (ParseException e) {
            assertTrue(e.getMessage().contains("Cannot parse date"));
        }
        format.setLenient(true);
        assertNotNull(format.parse("2015-11-32"));
    }

    void dateFormatInvalidInput() {
        StdDateFormat format = new StdDateFormat();
        try {
            format.parse("foobar");
            fail("Should not pass");
        } catch (ParseException e) {
            assertTrue(e.getMessage().contains("Cannot parse"));
        }
    }

    void compactNumericDateInputIsTimestamp() throws Exception {
        TestStdDateFormat format = new TestStdDateFormat();
        format.setLenient(false);
        assertFalse(format.looksLikeIso8601("20250305"));
        assertEquals(20250305L, format.parse("20250305").getTime());
        byte[] vpackString = VPackWireFixtureTest.hex(
                "48 32 30 32 35 30 33 30 35");
        assertEquals(new Date(20250305L), mapper.readValue(vpackString, Date.class));
    }

    void negativeTimestampParsing() throws Exception {
        StdDateFormat format = StdDateFormat.instance.clone();
        format.setLenient(false);
        Date parsed = format.parse("-1383043669935");
        assertEquals(-1383043669935L, parsed.getTime());
        byte[] vpackString = VPackWireFixtureTest.hex(
                "4e 2d 31 33 38 33 30 34 33 36 36 39 39 33 35");
        assertEquals(new Date(-1383043669935L), mapper.readValue(vpackString, Date.class));
    }
static class TestStdDateFormat extends StdDateFormat {
        Pattern plainPattern() { return PATTERN_PLAIN; }
        Pattern iso8601Pattern() { return PATTERN_ISO8601; }
        boolean looksLikeIso8601(String input) { return looksLikeISO8601(input); }
    }
static class SimpleBean {
        public String name;
    }

    void __invoke_dateFormatLenientDefaults() throws Exception {
        try {
            dateFormatLenientDefaults();
        } finally {
        }
    }


    void __invoke_dateFormatIso8601DateOnlyPattern() throws Exception {
        try {
            dateFormatIso8601DateOnlyPattern();
        } finally {
        }
    }


    void __invoke_dateFormatIso8601FullPattern() throws Exception {
        try {
            dateFormatIso8601FullPattern();
        } finally {
        }
    }


    void __invoke_dateFormatLenientParsing() throws Exception {
        try {
            dateFormatLenientParsing();
        } finally {
        }
    }


    void __invoke_dateFormatInvalidInput() throws Exception {
        try {
            dateFormatInvalidInput();
        } finally {
        }
    }


    void __invoke_compactNumericDateInputIsTimestamp() throws Exception {
        try {
            compactNumericDateInputIsTimestamp();
        } finally {
        }
    }


    void __invoke_negativeTimestampParsing() throws Exception {
        try {
            negativeTimestampParsing();
        } finally {
        }
    }

}
