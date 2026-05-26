package tools.jackson.databind.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CaseInsensitiveDeser953Test extends DatabindTestUtil
{
    static class Id953 {
        @JsonProperty("someId")
        public int someId;
    }

    @SuppressWarnings("deprecation") // Locale constructors deprecated in JDK 19
    private final Locale LOCALE_EN = new Locale("en", "US");

    private final ObjectMapper INSENSITIVE_MAPPER_EN = vpackMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .defaultLocale(LOCALE_EN)
            .build();

    @SuppressWarnings("deprecation") // Locale constructors deprecated in JDK 19
    private final Locale LOCALE_TR = new Locale("tr", "TR");

    private final ObjectMapper INSENSITIVE_MAPPER_TR = vpackMapperBuilder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .defaultLocale(LOCALE_TR)
            .build();

    @Test
    public void testTurkishILetterDeserializationWithEn() throws Exception {
        _testTurkishILetterDeserialization(INSENSITIVE_MAPPER_EN, LOCALE_EN);
    }

    @Test
    public void testTurkishILetterDeserializationWithTr() throws Exception {
        _testTurkishILetterDeserialization(INSENSITIVE_MAPPER_TR, LOCALE_TR);
    }

    private void _testTurkishILetterDeserialization(ObjectMapper mapper, Locale locale) throws Exception
    {
        // Sanity check first
        assertEquals(locale, mapper.deserializationConfig().getLocale());

        final String ORIGINAL_KEY = "someId";

        Id953 result;
        result = mapper.readValue(VPackUtils.toVPack("{\""+ORIGINAL_KEY+"\":1}"), Id953.class);
        assertEquals(1, result.someId);

        result = mapper.readValue(VPackUtils.toVPack("{\""+ORIGINAL_KEY.toUpperCase(locale)+"\":1}"), Id953.class);
        assertEquals(1, result.someId);

        result = mapper.readValue(VPackUtils.toVPack("{\""+ORIGINAL_KEY.toLowerCase(locale)+"\":1}"), Id953.class);
        assertEquals(1, result.someId);

        // and finally round-trip too...
        final Id953 input = new Id953();
        input.someId = 1;
        final String json = VPackUtils.toJson(mapper.writeValueAsBytes(input));

        result = mapper.readValue(VPackUtils.toVPack(json), Id953.class);
        assertEquals(1, result.someId);
    }
}
