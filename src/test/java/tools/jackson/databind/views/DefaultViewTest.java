package tools.jackson.databind.views;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

// for [databind#507], supporting default views
public class DefaultViewTest extends DatabindTestUtil
{
    // Classes that represent views
    static class ViewA { }
    static class ViewAA extends ViewA { }
    static class ViewB { }
    static class ViewBB extends ViewB { }

    @JsonView(ViewA.class)
    @JsonPropertyOrder({ "a", "b" })
    static class Defaulting {
        public int a = 3;

        @JsonView(ViewB.class)
        public int b = 5;
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = vpackMapperBuilder()
        .disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
        .build();

    @Test
    public void testDeserialization() throws IOException
    {
        final String JSON = a2q("{'a':1,'b':2}");

        // first: no views:
        Defaulting result = MAPPER.readerFor(Defaulting.class)
                .readValue(VPackUtils.toVPack(JSON));
        assertEquals(result.a, 1);
        assertEquals(result.b, 2);

        // Then views; first A, then B(B)
        result = MAPPER.readerFor(Defaulting.class)
                .withView(ViewA.class)
                .readValue(VPackUtils.toVPack(JSON));
        assertEquals(result.a, 1);
        assertEquals(result.b, 5);

        result = MAPPER.readerFor(Defaulting.class)
                .withView(ViewBB.class)
                .readValue(VPackUtils.toVPack(JSON));
        assertEquals(result.a, 3);
        assertEquals(result.b, 2);
    }

    @Test
    public void testSerialization() throws IOException
    {
        assertEquals(a2q("{'a':3,'b':5}"),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new Defaulting())));

        assertEquals(a2q("{'a':3}"),
                VPackUtils.toJson(MAPPER.writerWithView(ViewA.class)
                    .writeValueAsBytes(new Defaulting())));
        assertEquals(a2q("{'b':5}"),
                VPackUtils.toJson(MAPPER.writerWithView(ViewB.class)
                    .writeValueAsBytes(new Defaulting())));
    }
}
