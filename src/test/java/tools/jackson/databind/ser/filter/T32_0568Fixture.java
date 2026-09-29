package tools.jackson.databind.ser.filter;

import java.util.Collection;
import java.util.Collections;
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
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.EnumNamingStrategies;
import tools.jackson.databind.annotation.EnumNaming;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.ser.PropertyWriter;
import tools.jackson.databind.ser.std.SimpleBeanPropertyFilter;
import tools.jackson.databind.ser.std.SimpleFilterProvider;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0568Fixture {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(EnumFeature.WRITE_ENUMS_USING_TO_STRING)
            .build();

    // Provenance: CurrentObject3160Test#testIssue2475().
    void testIssue2475Vpack() throws Exception {
        SimpleFilterProvider provider = new SimpleFilterProvider().addFilter(
                "myFilter", new MyFilter3160());
        ObjectWriter writer = MAPPER3160.writer(provider);
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 30 03 42 69 64 44 49 44 2d 31 48 73 74 72 61 74 65 67 79 "
                        + "0b 14 02 44 74 79 70 65 43 46 6f 6f 43 66 6f 6f 28 2a 0c 03 "
                        + "43 73 65 74 01 03 28 0b"),
                writer.writeValueAsBytes(new Item3160(Collections.emptyList(), "ID-1")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                        "0b 30 03 42 69 64 44 49 44 2d 32 48 73 74 72 61 74 65 67 79 "
                        + "0b 14 02 44 74 79 70 65 43 46 6f 6f 43 66 6f 6f 28 2a 0c 03 "
                        + "43 73 65 74 01 03 28 0b"),
                writer.writeValueAsBytes(new Item3160(Collections.emptySet(), "ID-2")));
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

    void __invoke_testIssue2475Vpack() throws Exception {
        try {
            testIssue2475Vpack();
        } finally {
        }
    }

}
