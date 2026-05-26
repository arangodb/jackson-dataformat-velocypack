package tools.jackson.databind.format;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.*;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying serialization of simple basic non-structured
 * types; primitives (and/or their wrappers), Strings.
 */
public class EnumFormatShapeTest
    extends DatabindTestUtil
{
    @JsonFormat(shape= Shape.POJO)
    static enum PoNUM {
        A("a1"), B("b2");

        @JsonProperty
        protected final String value;

        private PoNUM(String v) { value = v; }

        public String getValue() { return value; }
    }

    static enum OK {
        V1("v1");
        protected String key;
        OK(String key) { this.key = key; }
    }

    static class PoNUMContainer {
        @JsonFormat(shape=Shape.NUMBER)
        public OK text = OK.V1;
    }

    @JsonFormat(shape= Shape.ARRAY) // alias for 'number', as of 2.5
    static enum PoAsArray
    {
        A, B;
    }

    // for [databind#572]
    static class PoOverrideAsString
    {
        @JsonFormat(shape=Shape.STRING)
        public PoNUM value = PoNUM.B;
    }

    static class PoOverrideAsNumber
    {
        @JsonFormat(shape=Shape.NUMBER)
        public PoNUM value = PoNUM.B;
    }

    // for [databind#1543]
    @JsonFormat(shape = Shape.NUMBER_INT)
    enum Color {
        RED,
        YELLOW,
        GREEN
    }

    static class ColorWrapper {
        public final Color color;

        ColorWrapper(Color color) {
            this.color = color;
        }
    }

    // [databind#2365]
    @JsonFormat(shape = Shape.OBJECT)
    public enum Enum2365 {
        A, B, C;

        public String getMainValue() { return name()+"-x"; }
    }

    // [databind#2576]
    @JsonFormat(shape = Shape.OBJECT)
    public enum Enum2576 {
        DEFAULT("default"),
        ATTRIBUTES("attributes") {
            @Override
            public String toString() {
                return name();
            }
        };

        private final String key;
        private Enum2576(String key) {
            this.key = key;
        }
        public String getKey() { return this.key; }
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = newVPackMapper();

    // Tests for JsonFormat.shape

    @Test
    public void testEnumAsObjectValid() throws Exception {
        assertEquals("{\"value\":\"a1\"}", VPackUtils.toJson(MAPPER.writeValueAsBytes(PoNUM.A)));
    }

    @Test
    public void testEnumAsIndexViaAnnotations() throws Exception {
        assertEquals("{\"text\":0}", VPackUtils.toJson(MAPPER.writeValueAsBytes(new PoNUMContainer())));
    }

    // As of 2.5, use of Shape.ARRAY is legal alias for "write as number"
    @Test
    public void testEnumAsObjectBroken() throws Exception
    {
        assertEquals("0", VPackUtils.toJson(MAPPER.writeValueAsBytes(PoAsArray.A)));
    }

    // [databind#572]
    @Test
    public void testOverrideEnumAsString() throws Exception {
        assertEquals("{\"value\":\"B\"}", VPackUtils.toJson(MAPPER.writeValueAsBytes(new PoOverrideAsString())));
    }

    @Test
    public void testOverrideEnumAsNumber() throws Exception {
        assertEquals("{\"value\":1}", VPackUtils.toJson(MAPPER.writeValueAsBytes(new PoOverrideAsNumber())));
    }

    // for [databind#1543]
    @Test
    public void testEnumValueAsNumber() throws Exception {
        assertEquals(String.valueOf(Color.GREEN.ordinal()),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(Color.GREEN)));
    }

    @Test
    public void testEnumPropertyAsNumber() throws Exception {
        assertEquals(String.format(a2q("{'color':%s}"), Color.GREEN.ordinal()),
                VPackUtils.toJson(MAPPER.writeValueAsBytes(new ColorWrapper(Color.GREEN))));
    }

    // [databind#2365]
    @Test
    public void testEnumWithNamingStrategy() throws Exception {
        final ObjectMapper mapper = vpackMapperBuilder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        String json = VPackUtils.toJson(mapper.writeValueAsBytes(Enum2365.B));
        assertEquals(a2q("{'main_value':'B-x'}"), json);
    }

    // [databind#2576]
    @Test
    public void testEnumWithMethodOverride() throws Exception {
        String stringResult = VPackUtils.toJson(MAPPER.writeValueAsBytes(Enum2576.ATTRIBUTES));
        Map<?,?> result = MAPPER.readValue(VPackUtils.toVPack(stringResult), Map.class);
        Map<String,String> exp = Collections.singletonMap("key", "attributes");
        assertEquals(exp, result);
    }
}
