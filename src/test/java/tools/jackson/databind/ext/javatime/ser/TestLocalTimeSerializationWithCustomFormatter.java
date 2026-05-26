package tools.jackson.databind.ext.javatime.ser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.module.SimpleModule;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalTimeSerializationWithCustomFormatter
{
    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertTrue(serializeWith(dateTime, formatter).contains(dateTime.format(formatter)));
    }

    private String serializeWith(LocalTime dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new LocalTimeSerializer(f)))
                .build();
        return VPackUtils.toJson(mapper.writeValueAsBytes(dateTime));
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertEquals(dateTime, deserializeWith(dateTime.format(formatter), formatter));
    }

    private LocalTime deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(LocalTime.class, new LocalTimeDeserializer(f)))
                .build();
        return mapper.readValue(VPackUtils.toVPack("\"" + json + "\""), LocalTime.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ISO_LOCAL_TIME,
                DateTimeFormatter.ISO_TIME
        );
    }
}