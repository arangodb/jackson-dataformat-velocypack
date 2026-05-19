package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link VPackMapper}: POJO serialization/deserialization,
 * builder API, type references, nested objects.
 */
public class VPackMapperTest extends BaseTestForVPack
{
    // =========================================================
    // Simple POJO for testing
    // =========================================================

    static class SimplePojo {
        public String name;
        public int value;

        public SimplePojo() {}
        public SimplePojo(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SimplePojo)) return false;
            SimplePojo s = (SimplePojo) o;
            return value == s.value && Objects.equals(name, s.name);
        }

        @Override
        public int hashCode() { return Objects.hash(name, value); }
    }

    static class NestedPojo {
        public SimplePojo inner;
        public List<String> tags;

        public NestedPojo() {}
        public NestedPojo(SimplePojo inner, List<String> tags) {
            this.inner = inner;
            this.tags = tags;
        }
    }

    static class AnnotatedPojo {
        public String fullName;
        public int count;

        public AnnotatedPojo() {}
        public AnnotatedPojo(String fullName, int count) {
            this.fullName = fullName;
            this.count = count;
        }
    }

    // =========================================================
    // VPackMapper instantiation
    // =========================================================

    @Test
    public void testDefaultConstructor() {
        VPackMapper m = new VPackMapper();
        assertNotNull(m);
    }

    @Test
    public void testBuilderConstruction() {
        VPackMapper m = VPackMapper.builder().build();
        assertNotNull(m);
    }

    @Test
    public void testBuilderWithWriteFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertNotNull(m);
    }

    @Test
    public void testBuilderWithReadFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertNotNull(m);
    }

    // =========================================================
    // Simple value round-trips
    // =========================================================

    @Test
    public void testWriteAndRead_null() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(null);
        Object result = m.readValue(bytes, Object.class);
        assertNull(result);
    }

    @Test
    public void testWriteAndRead_boolean_true() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Boolean.TRUE);
        Boolean result = m.readValue(bytes, Boolean.class);
        assertTrue(result);
    }

    @Test
    public void testWriteAndRead_boolean_false() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Boolean.FALSE);
        Boolean result = m.readValue(bytes, Boolean.class);
        assertFalse(result);
    }

    @Test
    public void testWriteAndRead_integer() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        Integer result = m.readValue(bytes, Integer.class);
        assertEquals(42, result);
    }

    @Test
    public void testWriteAndRead_string() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes("hello");
        String result = m.readValue(bytes, String.class);
        assertEquals("hello", result);
    }

    @Test
    public void testWriteAndRead_double() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        Double result = m.readValue(bytes, Double.class);
        assertEquals(3.14, result, 0.0001);
    }

    // =========================================================
    // POJO round-trips
    // =========================================================

    @Test
    public void testWriteAndRead_simplePojo() throws Exception {
        VPackMapper m = new VPackMapper();
        SimplePojo original = new SimplePojo("test", 123);
        byte[] bytes = m.writeValueAsBytes(original);
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertEquals(original, result);
    }

    @Test
    public void testWriteAndRead_annotatedPojo() throws Exception {
        VPackMapper m = new VPackMapper();
        AnnotatedPojo original = new AnnotatedPojo("John Doe", 42);
        byte[] bytes = m.writeValueAsBytes(original);
        AnnotatedPojo result = m.readValue(bytes, AnnotatedPojo.class);
        assertEquals(original.fullName, result.fullName);
        assertEquals(original.count, result.count);
    }

    @Test
    public void testWriteAndRead_nestedPojo() throws Exception {
        VPackMapper m = new VPackMapper();
        NestedPojo original = new NestedPojo(
                new SimplePojo("inner", 7),
                Arrays.asList("a", "b", "c"));
        byte[] bytes = m.writeValueAsBytes(original);
        NestedPojo result = m.readValue(bytes, NestedPojo.class);
        assertEquals(original.inner, result.inner);
        assertEquals(original.tags, result.tags);
    }

    // =========================================================
    // Collections
    // =========================================================

    @Test
    public void testWriteAndRead_listOfStrings() throws Exception {
        VPackMapper m = new VPackMapper();
        List<String> original = Arrays.asList("x", "y", "z");
        byte[] bytes = m.writeValueAsBytes(original);
        List<String> result = m.readValue(bytes, new TypeReference<List<String>>(){});
        assertEquals(original, result);
    }

    @Test
    public void testWriteAndRead_mapOfStrings() throws Exception {
        VPackMapper m = new VPackMapper();
        Map<String, Integer> original = new LinkedHashMap<>();
        original.put("a", 1);
        original.put("b", 2);
        original.put("c", 3);
        byte[] bytes = m.writeValueAsBytes(original);
        Map<String, Integer> result = m.readValue(bytes,
                new TypeReference<Map<String, Integer>>(){});
        assertEquals(original, result);
    }

    @Test
    public void testWriteAndRead_emptyList() throws Exception {
        VPackMapper m = new VPackMapper();
        List<Object> original = Collections.emptyList();
        byte[] bytes = m.writeValueAsBytes(original);
        List<?> result = m.readValue(bytes, List.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testWriteAndRead_emptyMap() throws Exception {
        VPackMapper m = new VPackMapper();
        Map<String, Object> original = Collections.emptyMap();
        byte[] bytes = m.writeValueAsBytes(original);
        Map<?, ?> result = m.readValue(bytes, Map.class);
        assertTrue(result.isEmpty());
    }

    // =========================================================
    // JsonNode
    // =========================================================

    @Test
    public void testWriteAndRead_jsonNode() throws Exception {
        VPackMapper m = new VPackMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode node = jsonMapper.readTree("{\"key\":\"value\",\"num\":42}");
        byte[] bytes = m.writeValueAsBytes(node);
        JsonNode result = m.readTree(bytes);
        assertEquals("value", result.get("key").asString());
        assertEquals(42, result.get("num").asInt());
    }

    // =========================================================
    // With write features
    // =========================================================

    @Test
    public void testCompactArrays_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5);
        byte[] bytes = m.writeValueAsBytes(original);
        // Compact array type byte should be 0x13
        assertEquals((byte) 0x13, bytes[0]);
        List<?> result = m.readValue(bytes, List.class);
        assertEquals(5, result.size());
        assertEquals(1, ((Number) result.get(0)).intValue());
    }

    @Test
    public void testCompactObjects_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        SimplePojo original = new SimplePojo("hello", 99);
        byte[] bytes = m.writeValueAsBytes(original);
        // Compact object type byte should be 0x14
        assertEquals((byte) 0x14, bytes[0]);
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertEquals(original, result);
    }

    @Test
    public void testUnsortedObjects_roundTrip() throws Exception {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        SimplePojo original = new SimplePojo("world", 7);
        byte[] bytes = m.writeValueAsBytes(original);
        // Unsorted object type bytes: 0x0f-0x12
        int tb = bytes[0] & 0xFF;
        assertTrue(tb >= 0x0f && tb <= 0x12, "Expected unsorted object type byte, got 0x" + Integer.toHexString(tb));
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertEquals(original, result);
    }

    // =========================================================
    // writeValueAsBytes overloads
    // =========================================================

    @Test
    public void testWriteValueAsBytes_andRead() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        Long result = m.readValue(bytes, Long.class);
        assertEquals(Long.MAX_VALUE, result);
    }

    // =========================================================
    // readTree / readValue from various sources
    // =========================================================

    @Test
    public void testReadValue_fromInputStream() throws Exception {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes("hello");
        String result = m.readValue(new ByteArrayInputStream(bytes), String.class);
        assertEquals("hello", result);
    }
}
