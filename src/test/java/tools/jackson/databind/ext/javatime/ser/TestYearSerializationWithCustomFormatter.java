package tools.jackson.databind.ext.javatime.ser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ext.javatime.deser.YearDeserializer;
import tools.jackson.databind.module.SimpleModule;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearSerializationWithCustomFormatter
{
    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        Year year = Year.now();
        String expected = "\"" + year.format(formatter) + "\"";
        assertEquals(expected, serializeWith(year, formatter));
    }

    private String serializeWith(Year dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new YearSerializer(f)))
                .build();
        return VPackUtils.toJson(mapper.writeValueAsBytes(dateTime));
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        Year year = Year.now();
        assertEquals(year, deserializeWith(year.format(formatter), formatter));
    }

    private Year deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(Year.class, new YearDeserializer(f)))
                .build();
        return mapper.readValue(VPackUtils.toVPack("\"" + json + "\""), Year.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ofPattern("yyyy"),
                DateTimeFormatter.ofPattern("yy")
        );
    }
}
