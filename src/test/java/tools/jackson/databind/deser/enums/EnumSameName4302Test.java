package tools.jackson.databind.deser.enums;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.VPackUtils;
import tools.jackson.databind.testutil.DatabindTestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [databind#4302]
public class EnumSameName4302Test
    extends DatabindTestUtil
{
    enum Field4302Enum {
        FOO(0);

        public final int foo;

        Field4302Enum(int foo) {
            this.foo = foo;
        }
    }

    enum Getter4302Enum {
        BAR("bar");

        public String bar;

        Getter4302Enum(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return "bar";
        }
    }

    enum Setter4302Enum {
        CAT("dog");

        public String cat;

        Setter4302Enum(String cat) {
            this.cat = cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }
    }

    static class Field4302Wrapper {
        public Field4302Enum wrapped;

        Field4302Wrapper() { }
        public Field4302Wrapper(Field4302Enum w) {
            wrapped = w;
        }
    }

    private final ObjectMapper MAPPER = vpackMapperBuilder()
        .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE)
        .build();

    @Test
    void testStandaloneShouldWork() throws Exception
    {
        // First, try roundtrip with same-ignore-case name field
        assertEquals(Field4302Enum.FOO,
            MAPPER.readValue(VPackUtils.toVPack("\"FOO\""), Field4302Enum.class));
        assertEquals(q("FOO"),
            VPackUtils.toJson(MAPPER.writeValueAsBytes(Field4302Enum.FOO)));

        // Now, try roundtrip with same-ignore-case name getter
        assertEquals(Getter4302Enum.BAR,
            MAPPER.readValue(VPackUtils.toVPack("\"BAR\""), Getter4302Enum.class));
        assertEquals(q("BAR"),
            VPackUtils.toJson(MAPPER.writeValueAsBytes(Getter4302Enum.BAR)));

        // Now, try roundtrip with same-ignore-case name setter
        Setter4302Enum.CAT.setCat("cat");
        assertEquals(Setter4302Enum.CAT,
            MAPPER.readValue(VPackUtils.toVPack("\"CAT\""), Setter4302Enum.class));
        assertEquals(q("CAT"),
            VPackUtils.toJson(MAPPER.writeValueAsBytes(Setter4302Enum.CAT)));
    }

    @Test
    void testWrappedShouldWork() throws Exception
    {
        // First, try roundtrip with same-ignore-case name field
        Field4302Wrapper input = new Field4302Wrapper(Field4302Enum.FOO);
        String json = VPackUtils.toJson(MAPPER.writeValueAsBytes(input));
        assertEquals(a2q("{'wrapped':'FOO'}"), json);

        Field4302Wrapper result = MAPPER.readValue(VPackUtils.toVPack(json), Field4302Wrapper.class);
        assertEquals(Field4302Enum.FOO, result.wrapped);
    }
}

