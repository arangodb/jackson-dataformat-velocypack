package tools.jackson.databind.ser.enums;

import java.util.EnumMap;
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

class T32_0568F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: EnumSerializationTest#testSubclassedEnums().
    void testSubclassedEnumsVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"),
                MAPPER.writeValueAsBytes(EnumWithSubClass.B));
    }

    // Provenance: EnumSerializationTest#testToStringEnum().
    void testToStringEnumVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(EnumFeature.WRITE_ENUMS_USING_TO_STRING, true)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("41 62"),
                mapper.writeValueAsBytes(LowerCaseEnum.B));
        assertArrayEquals(VPackWireFixtureTest.hex("41 42"),
                mapper.writer().without(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                        .writeValueAsBytes(LowerCaseEnum.B));
    }

    // Provenance: EnumSerializationTest#testToStringEnumWithEnumMap().
    void testToStringEnumWithEnumMapVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
                .build();
        EnumMap<LowerCaseEnum, String> values = new EnumMap<>(LowerCaseEnum.class);
        values.put(LowerCaseEnum.C, "value");
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 0c 01 41 63 45 76 61 6c 75 65 03"),
                mapper.writeValueAsBytes(values));
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

    void __invoke_testSubclassedEnumsVpack() throws Exception {
        try {
            testSubclassedEnumsVpack();
        } finally {
        }
    }


    void __invoke_testToStringEnumVpack() throws Exception {
        try {
            testToStringEnumVpack();
        } finally {
        }
    }


    void __invoke_testToStringEnumWithEnumMapVpack() throws Exception {
        try {
            testToStringEnumWithEnumMapVpack();
        } finally {
        }
    }

}
