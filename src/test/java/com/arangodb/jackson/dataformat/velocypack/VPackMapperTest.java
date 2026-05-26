package com.arangodb.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

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
            if (!(o instanceof SimplePojo s)) return false;
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
        assertThat(m).isNotNull();
    }

    @Test
    public void testBuilderConstruction() {
        VPackMapper m = VPackMapper.builder().build();
        assertThat(m).isNotNull();
    }

    @Test
    public void testBuilderWithWriteFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        assertThat(m).isNotNull();
    }

    @Test
    public void testBuilderWithReadFeature() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackReadFeature.FAIL_ON_CUSTOM_TYPES)
                .build();
        assertThat(m).isNotNull();
    }

    // =========================================================
    // Simple value round-trips
    // =========================================================

    @Test
    public void testWriteAndRead_null() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(null);
        Object result = m.readValue(bytes, Object.class);
        assertThat(result).isNull();
    }

    @Test
    public void testWriteAndRead_boolean_true() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Boolean.TRUE);
        Boolean result = m.readValue(bytes, Boolean.class);
        assertThat(result).isTrue();
    }

    @Test
    public void testWriteAndRead_boolean_false() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Boolean.FALSE);
        Boolean result = m.readValue(bytes, Boolean.class);
        assertThat(result).isFalse();
    }

    @Test
    public void testWriteAndRead_integer() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(42);
        Integer result = m.readValue(bytes, Integer.class);
        assertThat(result).isEqualTo(42);
    }

    @Test
    public void testWriteAndRead_string() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes("hello");
        String result = m.readValue(bytes, String.class);
        assertThat(result).isEqualTo("hello");
    }

    @Test
    public void testWriteAndRead_double() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(3.14);
        Double result = m.readValue(bytes, Double.class);
        assertThat(result).isEqualTo(3.14, org.assertj.core.data.Offset.offset(0.0001));
    }

    // =========================================================
    // POJO round-trips
    // =========================================================

    @Test
    public void testWriteAndRead_simplePojo() {
        VPackMapper m = new VPackMapper();
        SimplePojo original = new SimplePojo("test", 123);
        byte[] bytes = m.writeValueAsBytes(original);
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertThat(result).isEqualTo(original);
    }

    @Test
    public void testWriteAndRead_annotatedPojo() {
        VPackMapper m = new VPackMapper();
        AnnotatedPojo original = new AnnotatedPojo("John Doe", 42);
        byte[] bytes = m.writeValueAsBytes(original);
        AnnotatedPojo result = m.readValue(bytes, AnnotatedPojo.class);
        assertThat(result.fullName).isEqualTo(original.fullName);
        assertThat(result.count).isEqualTo(original.count);
    }

    @Test
    public void testWriteAndRead_nestedPojo() {
        VPackMapper m = new VPackMapper();
        NestedPojo original = new NestedPojo(
                new SimplePojo("inner", 7),
                Arrays.asList("a", "b", "c"));
        byte[] bytes = m.writeValueAsBytes(original);
        NestedPojo result = m.readValue(bytes, NestedPojo.class);
        assertThat(result.inner).isEqualTo(original.inner);
        assertThat(result.tags).isEqualTo(original.tags);
    }

    // =========================================================
    // Collections
    // =========================================================

    @Test
    public void testWriteAndRead_listOfStrings() {
        VPackMapper m = new VPackMapper();
        List<String> original = Arrays.asList("x", "y", "z");
        byte[] bytes = m.writeValueAsBytes(original);
        List<String> result = m.readValue(bytes, new TypeReference<>() {
        });
        assertThat(result).isEqualTo(original);
    }

    @Test
    public void testWriteAndRead_mapOfStrings() {
        VPackMapper m = new VPackMapper();
        Map<String, Integer> original = new LinkedHashMap<>();
        original.put("a", 1);
        original.put("b", 2);
        original.put("c", 3);
        byte[] bytes = m.writeValueAsBytes(original);
        Map<String, Integer> result = m.readValue(bytes,
                new TypeReference<>() {
                });
        assertThat(result).isEqualTo(original);
    }

    @Test
    public void testWriteAndRead_emptyList() {
        VPackMapper m = new VPackMapper();
        List<Object> original = Collections.emptyList();
        byte[] bytes = m.writeValueAsBytes(original);
        List<?> result = m.readValue(bytes, List.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void testWriteAndRead_emptyMap() {
        VPackMapper m = new VPackMapper();
        Map<String, Object> original = Collections.emptyMap();
        byte[] bytes = m.writeValueAsBytes(original);
        Map<?, ?> result = m.readValue(bytes, Map.class);
        assertThat(result).isEmpty();
    }

    // =========================================================
    // JsonNode
    // =========================================================

    @Test
    public void testWriteAndRead_jsonNode() {
        VPackMapper m = new VPackMapper();
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode node = jsonMapper.readTree("{\"key\":\"value\",\"num\":42}");
        byte[] bytes = m.writeValueAsBytes(node);
        JsonNode result = m.readTree(bytes);
        assertThat(result.get("key").asString()).isEqualTo("value");
        assertThat(result.get("num").asInt()).isEqualTo(42);
    }

    // =========================================================
    // With write features
    // =========================================================

    @Test
    public void testCompactArrays_roundTrip() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_ARRAYS)
                .build();
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5);
        byte[] bytes = m.writeValueAsBytes(original);
        // Compact array type byte should be 0x13
        assertThat(bytes[0]).isEqualTo((byte) 0x13);
        List<?> result = m.readValue(bytes, List.class);
        assertThat(result).hasSize(5);
        assertThat(((Number) result.get(0)).intValue()).isEqualTo(1);
    }

    @Test
    public void testCompactObjects_roundTrip() {
        VPackMapper m = VPackMapper.builder()
                .enable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        SimplePojo original = new SimplePojo("hello", 99);
        byte[] bytes = m.writeValueAsBytes(original);
        // Compact object type byte should be 0x14
        assertThat(bytes[0]).isEqualTo((byte) 0x14);
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertThat(result).isEqualTo(original);
    }

    @Test
    public void testUnsortedObjects_roundTrip() {
        VPackMapper m = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .disable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        SimplePojo original = new SimplePojo("world", 7);
        byte[] bytes = m.writeValueAsBytes(original);
        // Unsorted object type bytes: 0x0f-0x12
        int tb = bytes[0] & 0xFF;
        assertThat(tb >= 0x0f && tb <= 0x12).as("Expected unsorted object type byte, got 0x" + Integer.toHexString(tb)).isTrue();
        SimplePojo result = m.readValue(bytes, SimplePojo.class);
        assertThat(result).isEqualTo(original);
    }

    // =========================================================
    // writeValueAsBytes overloads
    // =========================================================

    @Test
    public void testWriteValueAsBytes_andRead() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes(Long.MAX_VALUE);
        Long result = m.readValue(bytes, Long.class);
        assertThat(result).isEqualTo(Long.MAX_VALUE);
    }

    // =========================================================
    // readTree / readValue from various sources
    // =========================================================

    @Test
    public void testReadValue_fromInputStream() {
        VPackMapper m = new VPackMapper();
        byte[] bytes = m.writeValueAsBytes("hello");
        String result = m.readValue(new ByteArrayInputStream(bytes), String.class);
        assertThat(result).isEqualTo("hello");
    }
}
