package tools.jackson.databind.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0387F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] WRAPPED_OBJECT = VPackWireFixtureTest.hex(
            "0b 21 01 45 65 6e 74 72 79 "
          + "0b 17 02 43 6b 65 79 43 66 6f 6f 45 76 61 6c 75 65 43 62 61 72 "
          + "03 0b 03");
private static final byte[] WRAPPED_COMPLEX = VPackWireFixtureTest.hex(
            "0b 26 01 45 65 6e 74 72 79 "
          + "0b 1c 02 43 6b 65 79 02 04 28 2a "
          + "45 76 61 6c 75 65 02 09 46 61 6e 73 77 65 72 03 0b 03");
private static final byte[] MAP_POJO_AND_NATURAL = VPackWireFixtureTest.hex(
            "0b 28 02 41 61 0b 14 02 45 65 78 74 72 61 28 0d "
          + "45 65 6d 70 74 79 19 0b 03 41 62 0b 0b 01 45 76 61 6c 75 65 32 03 "
          + "03 19");
private static final byte[] MAP_POJO_PROPERTY = VPackWireFixtureTest.hex(
            "0b 31 02 41 61 0b 14 02 45 65 78 74 72 61 28 0d "
          + "45 65 6d 70 74 79 19 0b 03 41 63 0b 14 02 45 65 78 74 72 61 28 0d "
          + "45 65 6d 70 74 79 19 0b 03 03 19");
private static final byte[] MAP_POJO_FULL = VPackWireFixtureTest.hex(
            "0b 3f 03 41 61 0b 14 02 45 65 78 74 72 61 28 0d "
          + "45 65 6d 70 74 79 19 0b 03 41 62 0b 0b 01 45 76 61 6c 75 65 32 03 "
          + "41 63 0b 14 02 45 65 78 74 72 61 28 0d 45 65 6d 70 74 79 19 0b 03 "
          + "03 19 26");
private static final byte[] NATURAL_OVERRIDE = VPackWireFixtureTest.hex(
            "0b 16 01 45 73 74 75 66 66 0b 0c 01 45 76 61 6c 75 65 28 7b 03 03");
private static final byte[] MAP1540 = VPackWireFixtureTest.hex(
            "0b 20 02 48 70 72 6f 70 65 72 74 79 28 37 43 6d 61 70 "
          + "0b 0c 02 28 0c 28 2d 36 28 58 03 07 0e 03");
private static final byte[] POJO_EXTRA_ONLY = VPackWireFixtureTest.hex(
            "0b 0c 01 45 65 78 74 72 61 28 2a 03");
private static final byte[] BAD_SIMPLE = VPackWireFixtureTest.hex(
            "0b 08 01 41 78 41 42 03");
private static final byte[] BAD_NESTED = VPackWireFixtureTest.hex(
            "0b 18 01 4b 63 6c 61 73 73 54 6f 52 65 61 64 "
          + "0b 08 01 41 78 41 42 03 03");
private static final byte[] BAD_STRUCTURED = VPackWireFixtureTest.hex(
            "0b 26 01 4d 63 6c 61 73 73 65 73 54 6f 52 65 61 64 "
          + "06 14 02 0b 07 01 41 78 31 03 0b 08 01 41 78 41 42 03 03 0a 03");

    // Provenance: MapFormatShapeTest#testSerializeAsPOJOViaClass().
    void testSerializeAsPOJOViaClassVpack() throws Exception {
        assertTreeEquals(MAP_POJO_AND_NATURAL,
                MAPPER.writeValueAsBytes(new Bean476Container(1, 2, 0)));
    }

    // Provenance: MapFormatShapeTest#testSerializeAsPOJOViaProperty().
    void testSerializeAsPOJOViaPropertyVpack() throws Exception {
        assertTreeEquals(MAP_POJO_PROPERTY,
                MAPPER.writeValueAsBytes(new Bean476Container(1, 0, 3)));
    }

    // Provenance: MapFormatShapeTest#testSerializeAsPOJOViaFullProperty().
    void testSerializeAsPOJOViaFullPropertyVpack() throws Exception {
        assertTreeEquals(MAP_POJO_FULL,
                MAPPER.writeValueAsBytes(new Bean476Container(1, 2, 3)));
    }

    // Provenance: MapFormatShapeTest#testSerializeNaturalViaOverride().
    void testSerializeNaturalViaOverrideVpack() throws Exception {
        assertTreeEquals(NATURAL_OVERRIDE,
                MAPPER.writeValueAsBytes(new Bean476Override(123)));
    }

    // Provenance: MapFormatShapeTest#testRoundTrip().
    void testRoundTripVpack() throws Exception {
        Map1540Implementation input = new Map1540Implementation();
        input.property = 55;
        input.put(12, 45);
        input.put(6, 88);
        ObjectMapper mapper = VPackMapper.builder(NUMERIC_KEY_FACTORY)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .build();
        assertArrayEquals(MAP1540, mapper.writeValueAsBytes(input));
        Map1540Implementation result = mapper.readValue(MAP1540,
                Map1540Implementation.class);
        assertEquals(input.property, result.property);
        assertEquals(input.getMap(), result.getMap());
    }

    // Provenance: MapFormatShapeTest#testDeserializeAsPOJOViaClass().
    void testDeserializeAsPOJOViaClassVpack() throws Exception {
        Map476AsPOJO result = MAPPER.readValue(POJO_EXTRA_ONLY, Map476AsPOJO.class);
        assertEquals(0, result.size());
        assertEquals(42, result.extra);
    }
private void assertSerializableFailure(byte[] input, Class<?> type) throws Exception {
        MismatchedInputException exception = org.junit.jupiter.api.Assertions.assertThrows(
                MismatchedInputException.class, () -> MAPPER.readValue(input, type));
        assertTrue(exception.getMessage().contains("not a valid"));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(1000);
        try (ObjectOutputStream stream = new ObjectOutputStream(bytes)) {
            stream.writeObject(exception);
        }
        try (ObjectInputStream stream = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            assertNotNull(stream.readObject());
        }
    }
private void assertTreeEquals(byte[] expected, byte[] actual) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(actual));
    }
private static final VPackFactory NUMERIC_KEY_FACTORY = VPackFactory.builder()
            .attributeNameCodec(new VPackAttributeNameCodec() {
                @Override public String decode(BigInteger id) { return id.toString(); }
                @Override public BigInteger encode(String name) {
                    try { return new BigInteger(name); }
                    catch (NumberFormatException e) { return null; }
                }
            }).build();
static class BeanWithMapEntryAsPOJO {
        @JsonFormat(shape = JsonFormat.Shape.POJO)
        public Map.Entry<String, String> entry;

        public BeanWithMapEntryAsPOJO() { }
        BeanWithMapEntryAsPOJO(String key, String value) {
            entry = Map.entry(key, value);
        }
    }
static class BeanWithComplexMapEntryAsPOJO {
        @JsonFormat(shape = JsonFormat.Shape.POJO)
        public Map.Entry<List<Integer>, String[]> entry;

        public BeanWithComplexMapEntryAsPOJO() { }
        BeanWithComplexMapEntryAsPOJO(int key, String value) {
            entry = Map.entry(List.of(key), new String[] { value });
        }
    }
@JsonPropertyOrder({ "extra" })
    static class Map476Base extends LinkedHashMap<String, Integer> {
        public int extra = 13;
    }
@JsonFormat(shape = JsonFormat.Shape.POJO)
    static class Map476AsPOJO extends Map476Base { }
@JsonPropertyOrder({ "a", "b", "c" })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class Bean476Container {
        public Map476AsPOJO a;
        public Map476Base b;
        @JsonFormat(shape = JsonFormat.Shape.POJO)
        public Map476Base c;

        Bean476Container(int forA, int forB, int forC) {
            if (forA != 0) { a = new Map476AsPOJO(); a.put("value", forA); }
            if (forB != 0) { b = new Map476Base(); b.put("value", forB); }
            if (forC != 0) { c = new Map476Base(); c.put("value", forC); }
        }
    }
static class Bean476Override {
        @JsonFormat(shape = JsonFormat.Shape.NATURAL)
        public Map476AsPOJO stuff;

        Bean476Override(int value) {
            stuff = new Map476AsPOJO();
            stuff.put("value", value);
        }
    }
@JsonFormat(shape = JsonFormat.Shape.POJO)
    @JsonPropertyOrder({ "property", "map" })
    static class Map1540Implementation implements Map<Integer, Integer> {
        public int property;
        public Map<Integer, Integer> map = new LinkedHashMap<>();

        public Map<Integer, Integer> getMap() { return map; }
        public void setMap(Map<Integer, Integer> map) { this.map = map; }
        @Override public Integer put(Integer key, Integer value) { return map.put(key, value); }
        @Override public int size() { return map.size(); }
        @JsonIgnore @Override public boolean isEmpty() { return map.isEmpty(); }
        @Override public boolean containsKey(Object key) { return map.containsKey(key); }
        @Override public boolean containsValue(Object value) { return map.containsValue(value); }
        @Override public Integer get(Object key) { return map.get(key); }
        @Override public Integer remove(Object key) { return map.remove(key); }
        @Override public void putAll(Map<? extends Integer, ? extends Integer> m) { map.putAll(m); }
        @Override public void clear() { map.clear(); }
        @Override public Set<Integer> keySet() { return map.keySet(); }
        @Override public Collection<Integer> values() { return map.values(); }
        @Override public Set<Map.Entry<Integer, Integer>> entrySet() { return map.entrySet(); }
    }
static class ClassToRead { public int x; }
static class ContainerClassToRead { public ClassToRead classToRead; }
static class ContainerClassesToRead { public List<ClassToRead> classesToRead; }

    void __invoke_testSerializeAsPOJOViaClassVpack() throws Exception {
        try {
            testSerializeAsPOJOViaClassVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsPOJOViaPropertyVpack() throws Exception {
        try {
            testSerializeAsPOJOViaPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsPOJOViaFullPropertyVpack() throws Exception {
        try {
            testSerializeAsPOJOViaFullPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSerializeNaturalViaOverrideVpack() throws Exception {
        try {
            testSerializeNaturalViaOverrideVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripVpack() throws Exception {
        try {
            testRoundTripVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeAsPOJOViaClassVpack() throws Exception {
        try {
            testDeserializeAsPOJOViaClassVpack();
        } finally {
        }
    }

}
