package tools.jackson.databind.ext.javatime.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;
import tools.jackson.databind.ext.javatime.MockObjectConfiguration;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;

import java.time.Month;
import java.time.temporal.TemporalAccessor;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MonthSerializerTest
    extends DateTimeTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    static class Wrapper {
        public Month month;

        public Wrapper(Month m) { month = m; }
        public Wrapper() { }
    }

    static class ShapeIntWrapper {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public Month value;
        public ShapeIntWrapper() { }
        public ShapeIntWrapper(Month v) { value = v; }
    }

    static class NoShapeIntWrapper {
        public Month value;
        public NoShapeIntWrapper() { }
        public NoShapeIntWrapper(Month v) { value = v; }
    }

    static class FrBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM", locale = "fr")
        public Month value;
        public FrBean() { }
        public FrBean(Month v) { value = v; }
    }

    static class ShapeArrayBean {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public Month value;
        public ShapeArrayBean() { }
        public ShapeArrayBean(Month v) { value = v; }
    }

    @Test
    public void testSerializationFromEnum() throws Exception
    {
        assertEquals("1", VPackUtils.toJson(writerForOneBased()
            .writeValueAsBytes(Month.JANUARY)));
        assertEquals("0", VPackUtils.toJson(writerForZeroBased()
            .writeValueAsBytes(Month.JANUARY)));
    }

    @Test
    public void testSerializationWithTypeInfo() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .addMixIn(TemporalAccessor.class, MockObjectConfiguration.class)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(Month.MARCH));
        assertEquals("[\"" + Month.class.getName() + "\",3]", json);
    }

    @Test
    public void testDefaultSerialization() throws Exception
    {
        // default emits 1-based ordinal
        assertEquals("1", VPackUtils.toJson(MAPPER.writeValueAsBytes(Month.JANUARY)));
    }

    @ParameterizedTest(name = "oneBased={0}, expectedJson={1}, input={2}")
    @MethodSource("oneBasedVsIndex")
    public void testParameterizedOneBasedVsIndex(boolean oneBased, String expectedJson, Object input)
            throws Exception
    {
        VPackMapper.Builder builder = VPackMapper.builder();

        if (oneBased) { builder.enable(DateTimeFeature.ONE_BASED_MONTHS); }
        else { builder.disable(DateTimeFeature.ONE_BASED_MONTHS); }

        ObjectWriter writer = builder.build().writer();

        assertEquals(expectedJson, VPackUtils.toJson(writer.writeValueAsBytes(input)));
    }

    @Test
    public void testOneBasedSerialization() throws Exception
    {
        ObjectMapper disabled = mapperBuilder()
                .disable(DateTimeFeature.ONE_BASED_MONTHS)
                .build();

        assertEquals("{\"month\":0}", VPackUtils.toJson(disabled.writeValueAsBytes(new Wrapper(Month.JANUARY))));

        ObjectMapper enabled = mapperBuilder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build();

        assertEquals("{\"month\":1}", VPackUtils.toJson(enabled.writeValueAsBytes(new Wrapper(Month.JANUARY))));
    }

    // ShapeInt Test
    @Test
    public void testSerializationWithShapeInt() throws Exception
    {
        // One with shape
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new ShapeIntWrapper(Month.MARCH)));
        assertEquals("{\"value\":[3]}", json);

        // One without shape
        json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new NoShapeIntWrapper(Month.MARCH)));
        assertEquals("{\"value\":3}", json);
    }

    @Test
    public void testSerializationWithFrLocale() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new FrBean(Month.MARCH)));
        assertEquals("{\"value\":\"mars\"}", json);
    }

    @Test
    public void testSerializationWithShapeArray() throws Exception
    {
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(new ShapeArrayBean(Month.DECEMBER)));
        assertEquals("{\"value\":[12]}", json);
    }

    private static Stream<Arguments> oneBasedVsIndex() {
        return Stream.of(
                // oneBased, writeIndex, expectedJson
                Arguments.of(false, "0", Month.JANUARY),
                Arguments.of(true , "1", Month.JANUARY),
                Arguments.of(false, "{\"month\":0}", new Wrapper(Month.JANUARY)),
                Arguments.of(true , "{\"month\":1}", new Wrapper(Month.JANUARY))
        );
    }

    private ObjectWriter writerForZeroBased() {
        return VPackMapper.builder()
                .disable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .writer();
    }

    private ObjectWriter writerForOneBased() {
        return VPackMapper.builder()
                .enable(DateTimeFeature.ONE_BASED_MONTHS)
                .build()
                .writer();
    }
}