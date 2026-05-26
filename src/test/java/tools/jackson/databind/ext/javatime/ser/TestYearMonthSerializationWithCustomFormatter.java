package tools.jackson.databind.ext.javatime.ser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ext.javatime.deser.YearMonthDeserializer;
import tools.jackson.databind.module.SimpleModule;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestYearMonthSerializationWithCustomFormatter
{
    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertTrue(serializeWith(dateTime, formatter).contains(dateTime.format(formatter)));
    }

    private String serializeWith(YearMonth dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new YearMonthSerializer(f)))
                .build();
        return VPackUtils.toJson(mapper.writeValueAsBytes(dateTime));
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertEquals(dateTime, deserializeWith(dateTime.format(formatter), formatter));
    }

    private YearMonth deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(YearMonth.class, new YearMonthDeserializer(f)))
                .build();
        return mapper.readValue(VPackUtils.toVPack("\"" + json + "\""), YearMonth.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ofPattern("uuuu-MM"),
                DateTimeFormatter.ofPattern("uu-M")
        );
    }
}