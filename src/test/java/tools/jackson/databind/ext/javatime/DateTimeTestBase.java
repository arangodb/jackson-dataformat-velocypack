package tools.jackson.databind.ext.javatime;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
}
