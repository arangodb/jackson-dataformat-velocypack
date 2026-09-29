package tools.jackson.databind.ser.enums;

import java.util.Collection;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.TokenStreamContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0568F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumsToLCvsJsonProperty4788Test#shouldUseJsonPropertySimple().
    void shouldUseJsonPropertySimpleVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "47 4b 65 74 63 68 75 70"),
                mapper.writeValueAsBytes(SauceA.KETCHUP));
    }

    // Provenance: EnumsToLCvsJsonProperty4788Test#shouldUseJsonPropertyOverEnumNaming().
    void shouldUseJsonPropertyOverEnumNamingVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "55 50 52 4f 50 45 52 54 59 5f 4d 41 59 4f 5f 4e 41 49 5a 5a 5a 5a"),
                mapper.writeValueAsBytes(SauceB.MAYO_NAIZZZZ));
    }

    // Provenance: EnumsToLCvsJsonProperty4788Test#shouldUseJsonPropertyOverToString().
    void shouldUseJsonPropertyOverToStringVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("49 49 73 2d 41 2d 50 72 6f 70"),
                mapper.writer().with(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                        .writeValueAsBytes(SauceC.IS_MY_NAME));
    }
private static final ObjectMapper MAPPER3160 = new VPackMapper();
enum EnumWithSubClass {
        A { @Override public void foobar() { } },
        B { @Override public void foobar() { } };

        public abstract void foobar();
    }
enum LowerCaseEnum {
        A, B, C;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
enum SauceA {
        @JsonProperty("Ketchup")
        KETCHUP
    }
@EnumNaming(EnumNamingStrategies.LowerCamelCaseStrategy.class)
    enum SauceB {
        @JsonProperty("PROPERTY_MAYO_NAIZZZZ")
        MAYO_NAIZZZZ
    }
enum SauceC {
        @JsonProperty("Is-A-Prop")
        IS_MY_NAME
    }
@JsonFilter("myFilter")
    @JsonPropertyOrder({ "id", "strategy", "set" })
    static class Item3160 {
        public Collection<String> set;
        public Strategy strategy = new Foo(42);
        public String id;

        Item3160(Collection<String> set, String id) {
            this.set = set;
            this.id = id;
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class) })
    interface Strategy { }
static class Foo implements Strategy {
        public int foo;

        @JsonCreator
        Foo(@JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }
static class MyFilter3160 extends SimpleBeanPropertyFilter {
        @Override
        public void serializeAsProperty(Object pojo, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider, PropertyWriter writer)
                throws JacksonException {
            TokenStreamContext context = generator.streamWriteContext();
            Object current = context.currentValue();
            if (!(current instanceof Item3160)) {
                throw new RuntimeException("current value is not Item3160: " + current);
            }
            try {
                super.serializeAsProperty(pojo, generator, provider, writer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    void __invoke_shouldUseJsonPropertySimpleVpack() throws Exception {
        try {
            shouldUseJsonPropertySimpleVpack();
        } finally {
        }
    }


    void __invoke_shouldUseJsonPropertyOverEnumNamingVpack() throws Exception {
        try {
            shouldUseJsonPropertyOverEnumNamingVpack();
        } finally {
        }
    }


    void __invoke_shouldUseJsonPropertyOverToStringVpack() throws Exception {
        try {
            shouldUseJsonPropertyOverToStringVpack();
        } finally {
        }
    }

}
