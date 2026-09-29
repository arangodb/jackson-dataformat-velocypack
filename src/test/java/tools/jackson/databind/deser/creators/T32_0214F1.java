package tools.jackson.databind.deser.creators;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0214F1 {
private static final byte[] BUSTED_CTOR = VPackWireFixtureTest.hex(
            "0a");
private static final byte[] HASH = VPackWireFixtureTest.hex(
            "14 19 44 74 79 70 65 46 63 75 73 74 6f 6d 45 62 79 74 65 73 43 61 62 63 02");
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] JACKSON_431 = VPackWireFixtureTest.hex(
            "14 22 45 69 74 65 6d 73 13 19 14 16 43 62 61 72 30 42 69 64 45 69 64 31 32 33 "
          + "43 66 6f 6f 31 03 01 01");
private static final byte[] JACKSON_438 = VPackWireFixtureTest.hex(
            "14 0f 44 6e 61 6d 65 46 66 6f 6f 62 61 72 01");
private static final byte[] DUPLICATE_CREATOR_NAME = VPackWireFixtureTest.hex(
            "14 09 43 62 61 72 41 78 01");
private static final byte[] IGNORED_STRING = VPackWireFixtureTest.hex(
            "43 61 62 63");
private static final byte[] CREATOR_PROPERTY = VPackWireFixtureTest.hex(
            "14 0c 44 69 74 65 6d 43 66 6f 6f 01");
private static final byte[] PROPS_CONSTRUCTOR_EXPLICIT = VPackWireFixtureTest.hex(
            "14 07 41 78 28 2a 01");
private static final byte[] PROPS_CONSTRUCTOR_NAME = VPackWireFixtureTest.hex(
            "14 07 41 78 28 1c 01");
private static final byte[] PROPS_FACTORY_EXPLICIT = VPackWireFixtureTest.hex(
            "14 0e 41 66 1b 00 00 00 00 00 00 e0 3f 01");
private static final byte[] FINAL_VALUE_5253 = VPackWireFixtureTest.hex(
            "14 16 41 61 31 41 62 32 41 63 14 0b 45 76 61 6c 75 65 28 2a 01 03");
private static final ObjectMapper MAPPER = VPackMapper.builder().build();

    // Provenance: TestCreators3#finalValue5253.
    void finalValue5253() throws Exception {
        ObjectMapper mapper = MAPPER.rebuild()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        Value5253 value = mapper.readValue(FINAL_VALUE_5253, Value5253.class);
        assertEquals(1, value.a);
        assertEquals(2, value.b);
        assertEquals(Map.of("value", 42), value.c);
        assertTrue(value.ctorCalled);
    }
static class HashTest {
        final byte[] bytes;
        final String type;

        @JsonCreator
        HashTest(@JsonProperty("bytes") @JsonDeserialize(using = BytesDeserializer.class)
                byte[] bytes, @JsonProperty("type") String type) {
            this.bytes = bytes;
            this.type = type;
        }
    }
static class BytesDeserializer extends ValueDeserializer<byte[]> {
        @Override
        public byte[] deserialize(JsonParser parser, tools.jackson.databind.DeserializationContext ctxt)
        {
            return parser.getString().getBytes(StandardCharsets.UTF_8);
        }
    }
static class Primitives {
        protected int x = 3;
        protected double d = -0.5;
        protected boolean b = true;

        @JsonCreator
        Primitives(@JsonProperty("x") int x, @JsonProperty("d") double d,
                @JsonProperty("b") boolean b) {
            this.x = x;
            this.d = d;
            this.b = b;
        }
    }
protected static class Test431Container {
        protected final List<Item431> items;

        @JsonCreator
        Test431Container(@JsonProperty("items") List<Item431> items) {
            this.items = items;
        }
    }
@JsonIgnoreProperties(ignoreUnknown = true)
    protected static class Item431 {
        protected final String id;

        @JsonCreator
        Item431(@JsonProperty("id") String id) {
            this.id = id;
        }
    }
static class BeanFor438 {
        @JsonCreator
        BeanFor438(@JsonProperty("name") String value) {
            throw new IllegalArgumentException("I don't like that name!");
        }
    }
static class BrokenCreatorBean {
        @JsonCreator
        BrokenCreatorBean(@JsonProperty("bar") String first,
                @JsonProperty("bar") String second) { }
    }
static class BustedCtor {
        @JsonCreator
        BustedCtor(@JsonProperty("a") String value) {
            throw new IllegalArgumentException("foobar");
        }
    }
static class IgnoredCtor {
        @JsonIgnore
        public IgnoredCtor(String value) {
            throw new RuntimeException("Should never use this constructor");
        }

        public IgnoredCtor() { }
    }
static interface Issue700Set extends java.util.Set<Object> { }
static class Issue700Bean {
        protected Issue700Set item;

        @JsonCreator
        Issue700Bean(@JsonProperty("item") String item) { }

        public String getItem() { return null; }
    }
static class ConstructorBeanPropsExplicit4515 {
        int x;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        ConstructorBeanPropsExplicit4515(@JsonProperty("x") int x) {
            this.x = x;
        }
    }
static class ConstructorBeanPropsWithName4515 {
        int x;

        @JsonCreator
        ConstructorBeanPropsWithName4515(@JsonProperty("x") int x) {
            this.x = x;
        }
    }
static class FactoryBeanPropsExplicit4515 {
        double d;

        private FactoryBeanPropsExplicit4515(double value, boolean dummy) {
            d = value;
        }

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        static FactoryBeanPropsExplicit4515 createIt(@JsonProperty("f") double value) {
            return new FactoryBeanPropsExplicit4515(value, true);
        }
    }
static class Value5253 {
        int a, b;
        public final Map<String, Integer> c;

        boolean ctorCalled;

        @JsonCreator
        public Value5253(@JsonProperty("a") int a, @JsonProperty("b") int b,
                @JsonProperty("c") final Map<String, Integer> c) {
            this.a = a;
            this.b = b;
            this.c = new LinkedHashMap<>(c);
            ctorCalled = true;
        }
    }

    void __invoke_finalValue5253() throws Exception {
        try {
            finalValue5253();
        } finally {
        }
    }

}
