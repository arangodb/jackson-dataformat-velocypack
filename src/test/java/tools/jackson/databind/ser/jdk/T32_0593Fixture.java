package tools.jackson.databind.ser.jdk;

import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.Base64Variants;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0593Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: MapSerializationTest#testOrderByKey().
    void testOrderByKeyVpack() throws Exception {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 3);
        map.put("a", 6);
        assertFalse(MAPPER.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS));
        byte[] insertionOrder = VPackWireFixtureTest.hex(
                "0b 0b 02 41 62 33 41 61 36 06 03");
        assertArrayEquals(insertionOrder,
                MAPPER.writeValueAsBytes(map));
        Map<?, ?> decoded = MAPPER.readValue(insertionOrder, Map.class);
        assertEquals(3, decoded.get("b"));
        assertEquals(6, decoded.get("a"));

        byte[] sorted = VPackWireFixtureTest.hex(
                "0b 0b 02 41 61 36 41 62 33 03 06");
        assertArrayEquals(sorted,
                MAPPER.writer(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                        .writeValueAsBytes(map));
        assertEquals(Map.of("a", 6, "b", 3), MAPPER.readValue(sorted, Map.class));
    }

    // Provenance: MapSerializationTest#testOrderByWithNulls().
    void testOrderByWithNullsVpack() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put(null, 1);
        map.put("b", 2);
        ObjectMapper sorting = VPackMapper.builder()
                .disable(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)
                .build();
        assertThrows(DatabindException.class, () -> sorting.writer(
                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS).writeValueAsBytes(map));
    }

    // Provenance: MapSerializationTest#testOrderByKeyViaProperty().
    void testOrderByKeyViaPropertyVpack() throws Exception {
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 17 01 43 6d 61 70 0b 0f 03 41 63 31 41 62 32 41 61 33 09 06 03 03");
        byte[] actual = MAPPER.writeValueAsBytes(new MapOrderingBean("c", "b", "a"));
        assertArrayEquals(expected, actual);
    }

    // Provenance: MapSerializationTest#testNullJsonMapping691().
    void testNullJsonMapping691Vpack() throws Exception {
        MapWithTypedValues input = new MapWithTypedValues();
        input.put("id", "Test");
        input.put("NULL", null);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 20 03 45 40 74 79 70 65 45 6d 79 6d 61 70"
              + "42 69 64 44 54 65 73 74 44 4e 55 4c 4c 18 03 17 0f"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: MapSerializationTest#testNullJsonInTypedMap691().
    void testNullJsonInTypedMap691Vpack() throws Exception {
        Map<String, String> input = new HashMap<>();
        input.put("NULL", null);
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(Object.class, Mixin691.class).build();
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 24 02 46 40 63 6c 61 73 73 51 6a 61 76 61 2e 75 74 69 6c"
              + "2e 48 61 73 68 4d 61 70 44 4e 55 4c 4c 18 03 1c");
        byte[] actual = mapper.writeValueAsBytes(input);
        assertArrayEquals(expected, actual);
    }

    // Provenance: MapSerializationTest#testNoKeyOuter().
    void testNoKeyOuterVpack() throws Exception {
        Map<String, NoKeyOuter> input = Map.of(
                "key", new NoKeyOuter(new Inner2871("innerKey", "innerValue")));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 43 6b 65 79 4a 69 6e 6e 65 72 56 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(input));
    }

    // Provenance: MapSerializationTest#testNotKarl().
    void testNotKarlVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 16 01 43 6d 61 70 0b 0e 01 48 4e 6f 74 20 4b 61 72 6c 31 03 03"),
                MAPPER.writeValueAsBytes(new NotKarlBean()));
    }

    // Provenance: MapSerializationTest#testMapsWithBinaryKeys().
    void testMapsWithBinaryKeysVpack() throws Exception {
        byte[] binary = { 1, 2, 3, 4, 5 };
        String key = Base64Variants.MIME.encode(binary);
        Map<String, String> inner = new LinkedHashMap<>();
        inner.put(key, "stuff");
        Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put("map", inner);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1b 01 43 6d 61 70 0b 13 01 48 41 51 49 44 42 41 55 3d"
              + "45 73 74 75 66 66 03 03"),
                MAPPER.writeValueAsBytes(wrapper));

        Map<String, String> dynamic = new LinkedHashMap<>();
        dynamic.put(key, "xyz");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 48 41 51 49 44 42 41 55 3d 43 78 79 7a 03"),
                MAPPER.writeValueAsBytes(dynamic));
    }

    // Provenance: MapSerializationTest#testSerializationFailureWhenEnabledWithIncomparableKeys().
    void testSerializationFailureWhenEnabledWithIncomparableKeysVpack() {
        Map<Currency, String> input = new LinkedHashMap<>();
        input.put(Currency.getInstance("GBP"), "GBP_TEXT");
        input.put(Currency.getInstance("AUD"), "AUD_TEXT");
        assertThrows(InvalidDefinitionException.class, () -> MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .with(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)
                .writeValueAsBytes(input));
    }

    // Provenance: MapSerializationTest#testSerializationWithGenericObjectKeys().
    void testSerializationWithGenericObjectKeysVpack() throws Exception {
        ObjectContainer4773 input = new ObjectContainer4773();
        input.exampleMap.put(5, "N_TEXT");
        input.exampleMap.put(1, "GBP_TEXT");
        input.exampleMap.put(3, "T_TEXT");
        input.exampleMap.put(4, "AUD_TEXT");
        input.exampleMap.put(2, "KRW_TEXT");
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 45 01 4a 65 78 61 6d 70 6c 65 4d 61 70"
              + "0b 36 05 31 48 47 42 50 5f 54 45 58 54 32 48 4b 52 57"
              + "5f 54 45 58 54 33 46 54 5f 54 45 58 54 34 48 41 55 44"
              + "5f 54 45 58 54 35 46 4e 5f 54 45 58 54 03 0d 17 1f 29 03");
        byte[] actual = VPackMapper.builder(VPackFactory.builder()
                        .attributeNameCodec(NUMERIC_KEY_CODEC).build()).build()
                        .writer(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                        .writeValueAsBytes(input);
        assertArrayEquals(expected, actual);
    }

    // Provenance: MapSerializationTest#testSerWithNullType().
    void testSerWithNullTypeVpack() {
        ObjectContainer4773 input = new ObjectContainer4773();
        input.exampleMap.put(null, "AUD_TEXT");
        assertThrows(InvalidDefinitionException.class, () -> MAPPER.writer()
                .with(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .with(SerializationFeature.FAIL_ON_ORDER_MAP_BY_INCOMPARABLE_KEY)
                .writeValueAsBytes(input));
    }

    // Provenance: MapSerializationTest#testUnWrappedMapWithDefaultType().
    void testUnWrappedMapWithDefaultTypeVpack() throws Exception {
        SimpleModule module = new SimpleModule("test");
        module.addKeySerializer(ABCKey.class, new ABCKeySerializer());
        DefaultTypeResolverBuilder typer = new DefaultTypeResolverBuilder(
                BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY,
                JsonTypeInfo.Id.NAME, null).typeIdVisibility(true);
        ObjectMapper mapper = VPackMapper.builder().addModule(module)
                .setDefaultTyping(typer).build();
        Map<ABCKey, String> input = new HashMap<>();
        input.put(ABCKey.B, "bar");
        byte[] expected = VPackWireFixtureTest.hex(
                "0b 1c 02 45 40 74 79 70 65 47 48 61 73 68 4d 61 70"
              + "44 78 78 78 42 43 62 61 72 03 11");
        byte[] actual = mapper.writeValueAsBytes(input);
        assertArrayEquals(expected, actual);
    }
private static final VPackAttributeNameCodec NUMERIC_KEY_CODEC =
            new VPackAttributeNameCodec() {
                @Override
                public String decode(BigInteger unsignedId) { return unsignedId.toString(); }

                @Override
                public BigInteger encode(String name) {
                    try { return new BigInteger(name); }
                    catch (NumberFormatException e) { return null; }
                }
            };
@JsonPropertyOrder(alphabetic = true)
    static class MapOrderingBean {
        public LinkedHashMap<String, Integer> map;

        MapOrderingBean(String... keys) {
            map = new LinkedHashMap<>();
            int value = 1;
            for (String key : keys) map.put(key, value++);
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonTypeName("mymap")
    static class MapWithTypedValues extends LinkedHashMap<String, String> { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static class Mixin691 { }
static class Inner2871 {
        @JsonValue
        String value;

        Inner2871(String key, String value) { this.value = value; }
    }
static class NoKeyOuter {
        @JsonValue
        Inner2871 inner;

        NoKeyOuter(Inner2871 inner) { this.inner = inner; }
    }
static class NotKarlBean {
        public Map<String, Integer> map = new LinkedHashMap<>();

        NotKarlBean() { map.put("Not Karl", 1); }
    }
static class ObjectContainer4773 {
        public Map<Object, String> exampleMap = new LinkedHashMap<>();
    }
enum ABCKey { A, B, C }
static class ABCKeySerializer extends StdSerializer<ABCKey> {
        ABCKeySerializer() { super(ABCKey.class); }

        @Override
        public void serialize(ABCKey value, tools.jackson.core.JsonGenerator g,
                tools.jackson.databind.SerializationContext ctxt) {
            g.writeName("xxx" + value);
        }
    }

    void __invoke_testOrderByKeyVpack() throws Exception {
        try {
            testOrderByKeyVpack();
        } finally {
        }
    }


    void __invoke_testOrderByWithNullsVpack() throws Exception {
        try {
            testOrderByWithNullsVpack();
        } finally {
        }
    }


    void __invoke_testOrderByKeyViaPropertyVpack() throws Exception {
        try {
            testOrderByKeyViaPropertyVpack();
        } finally {
        }
    }


    void __invoke_testNullJsonMapping691Vpack() throws Exception {
        try {
            testNullJsonMapping691Vpack();
        } finally {
        }
    }


    void __invoke_testNullJsonInTypedMap691Vpack() throws Exception {
        try {
            testNullJsonInTypedMap691Vpack();
        } finally {
        }
    }


    void __invoke_testNoKeyOuterVpack() throws Exception {
        try {
            testNoKeyOuterVpack();
        } finally {
        }
    }


    void __invoke_testNotKarlVpack() throws Exception {
        try {
            testNotKarlVpack();
        } finally {
        }
    }


    void __invoke_testMapsWithBinaryKeysVpack() throws Exception {
        try {
            testMapsWithBinaryKeysVpack();
        } finally {
        }
    }


    void __invoke_testSerializationFailureWhenEnabledWithIncomparableKeysVpack() throws Exception {
        try {
            testSerializationFailureWhenEnabledWithIncomparableKeysVpack();
        } finally {
        }
    }


    void __invoke_testSerializationWithGenericObjectKeysVpack() throws Exception {
        try {
            testSerializationWithGenericObjectKeysVpack();
        } finally {
        }
    }


    void __invoke_testSerWithNullTypeVpack() throws Exception {
        try {
            testSerWithNullTypeVpack();
        } finally {
        }
    }


    void __invoke_testUnWrappedMapWithDefaultTypeVpack() throws Exception {
        try {
            testUnWrappedMapWithDefaultTypeVpack();
        } finally {
        }
    }

}
