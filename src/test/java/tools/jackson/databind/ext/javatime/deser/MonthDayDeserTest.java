package tools.jackson.databind.ext.javatime.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.ext.javatime.DateTimeTestBase;
import tools.jackson.databind.ext.javatime.MockObjectConfiguration;

import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MonthDayDeserTest extends DateTimeTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(MonthDay.class);
    private final TypeReference<Map<String, MonthDay>> MAP_TYPE_REF = new TypeReference<Map<String, MonthDay>>() { };

    static class Wrapper {
        @JsonFormat(pattern="MM/dd")
        public MonthDay value;

        public Wrapper(MonthDay v) { value = v; }
        public Wrapper() { }
    }

    static class WrapperAsArray {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public MonthDay value;

        public WrapperAsArray(MonthDay v) { value = v; }
        public WrapperAsArray() { }
    }

    
    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(MonthDay.of(Month.JANUARY, 1), "'--01-01'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue(VPackUtils.toVPack(q("notamonthday")));
            fail("Should nae pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.MonthDay` from String");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['--01-01']");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            // expecting array-of-ints
            verifyException(e, "Unexpected token");
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // since 2.10, empty array taken as `null`
        
        MonthDay value = READER.readValue(VPackUtils.toVPack("[]"));
        assertNull(value);

        value = newMapper()
                .readerFor(MonthDay.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(VPackUtils.toVPack("[]"));
        assertNull(value);
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        MonthDay value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue(VPackUtils.toVPack(a2q("['--01-01']")));
        expect(MonthDay.of(Month.JANUARY, 1), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        MonthDay value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue(VPackUtils.toVPack("[]"));
        assertNull(value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        assertEquals(MonthDay.of(Month.JANUARY, 17), MAPPER.readValue(VPackUtils.toVPack("\"--01-17\""), MonthDay.class),
                "The value is not correct.");
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        assertEquals(MonthDay.of(Month.AUGUST, 21),
                MAPPER.readValue(VPackUtils.toVPack("\"--08-21\""), MonthDay.class),
                "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = mapperBuilder()
            .addMixIn(TemporalAccessor.class, MockObjectConfiguration.class)
            .build();
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);
        TemporalAccessor value = mapper.readValue(VPackUtils.toVPack("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]"), TemporalAccessor.class);
        assertEquals(monthDay, value, "The value is not correct.");
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        final Wrapper input = new Wrapper(MonthDay.of(12, 28));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        assertEquals("{\"value\":\"12/28\"}", json);

        Wrapper output = MAPPER.readValue(VPackUtils.toVPack(json), Wrapper.class);
        assertEquals(input.value, output.value);
    }

    @Test
    public void testFormatAnnotationArray() throws Exception
    {
        final WrapperAsArray input = new WrapperAsArray(MonthDay.of(12, 28));
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        assertEquals("{\"value\":[12,28]}", json);

        // 13-May-2019, tatu: [modules-java#107] not fully implemented so can't yet test
        WrapperAsArray output = MAPPER.readValue(VPackUtils.toVPack(json), WrapperAsArray.class);
        assertEquals(input.value, output.value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    // minor changes in 2.12
    @Test
    public void testDeserializeFromEmptyString() throws Exception
    {
        final String key = "monthDay";

        // First: by default, lenient, so empty String fine
        final ObjectReader objectReader = MAPPER.readerFor(MAP_TYPE_REF);

        String doc = VPackUtils.toJson(MAPPER.writeValueAsBytes(asMap(key, null)));
        Map<String, MonthDay> actualMapFromNullStr = objectReader.readValue(VPackUtils.toVPack(doc));
        assertNull(actualMapFromNullStr.get(key));

        doc = VPackUtils.toJson(MAPPER.writeValueAsBytes(asMap(key, "")));
        assertNotNull(objectReader.readValue(VPackUtils.toVPack(doc)));

        // But can make strict:
        final ObjectMapper strictMapper = mapperBuilder()
                .withConfigOverride(MonthDay.class, o -> o.setFormat(
                        JsonFormat.Value.forLeniency(false)))
        .build();
        doc = VPackUtils.toJson(strictMapper.writeValueAsBytes(asMap("date", "")));
        try {
            strictMapper.readerFor(MAP_TYPE_REF)
                    .readValue(VPackUtils.toVPack(doc));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "not allowed because 'strict' mode set for");
        }
    }

    private void expectSuccess(Object exp, String aposJson) throws Exception {
        final MonthDay value = read(aposJson);
        notNull(value);
        expect(exp, value);
    }

    private MonthDay read(final String aposJson) throws Exception {
        return READER.readValue(VPackUtils.toVPack(a2q(aposJson)));
    }

    private static void notNull(Object value) {
        assertNotNull(value, "The value should not be null.");
    }

    private static void expect(Object exp, Object value) {
        assertEquals(exp,  value, "The value is not correct.");
    }
}
