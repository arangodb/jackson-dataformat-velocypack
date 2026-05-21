package tools.jackson.databind.ext.javatime;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeTestBase
    extends tools.jackson.databind.testutil.DatabindTestUtil
{
    protected static final ZoneId UTC = ZoneId.of("UTC");

    protected static final ZoneId Z_CHICAGO = ZoneId.of("America/Chicago");
    protected static final ZoneId Z_BUDAPEST = ZoneId.of("Europe/Budapest");

    // 14-Mar-2016, tatu: Serialization of trailing zeroes may change [datatype-jsr310#67]
    //   Note, tho, that "0.0" itself is special case; need to avoid scientific notation:
    final protected static String NO_NANOSECS_SER = "0.0";
    final protected static String NO_NANOSECS_SUFFIX = ".000000000";

    protected static ObjectMapper newMapper() {
        return newMapperBuilder().build();
    }

    protected static MapperBuilder<?,?> newMapperBuilder() {
        return vpackMapperBuilder()
                .defaultLocale(Locale.ENGLISH); // NOTE: VPackWriteFeature.ESCAPE_FORWARD_SLASHES not applicable
    }
    protected static MapperBuilder<?,?> newMapperBuilder(TimeZone tz) {
        return vpackMapperBuilder()
                .defaultLocale(Locale.ENGLISH)
                .defaultTimeZone(tz); // NOTE: VPackWriteFeature.ESCAPE_FORWARD_SLASHES not applicable
    }

    protected static ObjectMapper newMapper(TimeZone tz) {
        return newMapperBuilder(tz).build();
    }

    protected static VPackMapper.Builder mapperBuilder() {
        return VPackMapper.builder()
                .defaultLocale(Locale.ENGLISH); // NOTE: VPackWriteFeature.ESCAPE_FORWARD_SLASHES not applicable
    }

    protected static <T> Map<T, String> asMap(T key, String value) {
        return Collections.singletonMap(key, value);
    }

    protected static String mapAsString(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

    protected static void assertNumericEquals(String expected, String actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(new BigDecimal(actual)),
                "Expected numeric value <" + expected + "> but was <" + actual + ">");
    }

    protected static void assertJsonNumericEquals(String expected, String actual) {
        // Compare JSON strings that contain a single numeric value for a field,
        // e.g. {"t1":1651053600000,"t2":1651053600.000000000}
        // Parse both as BigDecimal for numeric tokens, compare structure otherwise
        assertEquals(normalizeJsonNumbers(expected), normalizeJsonNumbers(actual));
    }

    private static String normalizeJsonNumbers(String json) {
        // Replace each JSON number token with its BigDecimal canonical form
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '"') {
                sb.append(c);
                i++;
                while (i < json.length()) {
                    char q = json.charAt(i);
                    sb.append(q);
                    i++;
                    if (q == '\\') { if (i < json.length()) { sb.append(json.charAt(i)); i++; } }
                    else if (q == '"') break;
                }
            } else if (c == '-' || (c >= '0' && c <= '9')) {
                int start = i;
                if (c == '-') i++;
                while (i < json.length() && (json.charAt(i) >= '0' && json.charAt(i) <= '9')) i++;
                if (i < json.length() && json.charAt(i) == '.') {
                    i++;
                    while (i < json.length() && (json.charAt(i) >= '0' && json.charAt(i) <= '9')) i++;
                }
                if (i < json.length() && (json.charAt(i) == 'e' || json.charAt(i) == 'E')) {
                    i++;
                    if (i < json.length() && (json.charAt(i) == '+' || json.charAt(i) == '-')) i++;
                    while (i < json.length() && (json.charAt(i) >= '0' && json.charAt(i) <= '9')) i++;
                }
                String numStr = json.substring(start, i);
                try {
                    sb.append(new BigDecimal(numStr).stripTrailingZeros().toPlainString());
                } catch (NumberFormatException e) {
                    sb.append(numStr);
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}
