package com.arangodb.jackson.dataformat.velocypack.mapper;

import org.junit.jupiter.api.Test;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round-trip tests: serialize then deserialize, verifying the result equals the original.
 */
public class VPackRoundTripTest extends BaseTestForVPack
{
    private final VPackMapper mapper = new VPackMapper();

    // Helper
    private <T> T roundTrip(T value, Class<T> type) {
        byte[] bytes = mapper.writeValueAsBytes(value);
        return mapper.readValue(bytes, type);
    }

    // =========================================================
    // SIMPLE VALUES
    // =========================================================

    @Test
    public void testRoundTripNull() {
        byte[] bytes = mapper.writeValueAsBytes(null);
        Object result = mapper.readValue(bytes, Object.class);
        assertThat(result).isNull();
    }

    @Test
    public void testRoundTripBoolean_false() {
        Boolean result = roundTrip(Boolean.FALSE, Boolean.class);
        assertThat(result).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void testRoundTripBoolean_true() {
        Boolean result = roundTrip(Boolean.TRUE, Boolean.class);
        assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testRoundTripInteger_0() {
        assertThat(roundTrip(0, Integer.class)).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void testRoundTripInteger_9() {
        assertThat(roundTrip(9, Integer.class)).isEqualTo(Integer.valueOf(9));
    }

    @Test
    public void testRoundTripInteger_negative1() {
        assertThat(roundTrip(-1, Integer.class)).isEqualTo(Integer.valueOf(-1));
    }

    @Test
    public void testRoundTripInteger_negative6() {
        assertThat(roundTrip(-6, Integer.class)).isEqualTo(Integer.valueOf(-6));
    }

    @Test
    public void testRoundTripInteger_maxValue() {
        assertThat(roundTrip(Integer.MAX_VALUE, Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testRoundTripInteger_minValue() {
        assertThat(roundTrip(Integer.MIN_VALUE, Integer.class)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void testRoundTripLong_maxValue() {
        assertThat(roundTrip(Long.MAX_VALUE, Long.class)).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void testRoundTripLong_minValue() {
        assertThat(roundTrip(Long.MIN_VALUE, Long.class)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void testRoundTripDouble_zero() {
        assertThat(roundTrip(0.0, Double.class)).isEqualTo(0.0);
    }

    @Test
    public void testRoundTripDouble_pi() {
        assertThat(roundTrip(Math.PI, Double.class)).isEqualTo(Math.PI);
    }

    @Test
    public void testRoundTripString_empty() {
        assertThat(roundTrip("", String.class)).isEmpty();
    }

    @Test
    public void testRoundTripString_hello() {
        assertThat(roundTrip("hello", String.class)).isEqualTo("hello");
    }

    @Test
    public void testRoundTripString_126chars() {
        String s = "A".repeat(126);
        assertThat(roundTrip(s, String.class)).isEqualTo(s);
    }

    @Test
    public void testRoundTripString_127chars() {
        String s = "B".repeat(127);
        assertThat(roundTrip(s, String.class)).isEqualTo(s);
    }

    @Test
    public void testRoundTripString_unicode() {
        String s = "Hello \u4e16\u754c!"; // "Hello 世界!"
        assertThat(roundTrip(s, String.class)).isEqualTo(s);
    }

    // =========================================================
    // COLLECTIONS
    // =========================================================

    @Test
    public void testRoundTripEmptyArray() {
        List<Object> list = new ArrayList<>();
        byte[] bytes = mapper.writeValueAsBytes(list);
        List<?> result = mapper.readValue(bytes, List.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void testRoundTripIntArray() {
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5);
        byte[] bytes = mapper.writeValueAsBytes(original);
        List<?> result = mapper.readValue(bytes, List.class);
        assertThat(result).hasSize(5);
        assertThat(((Number) result.get(0)).intValue()).isEqualTo(1);
        assertThat(((Number) result.get(4)).intValue()).isEqualTo(5);
    }

    @Test
    public void testRoundTripMixedArray() {
        List<Object> original = Arrays.asList("hello", 42, true, null);
        byte[] bytes = mapper.writeValueAsBytes(original);
        List<?> result = mapper.readValue(bytes, List.class);
        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo("hello");
        assertThat(((Number) result.get(1)).intValue()).isEqualTo(42);
        assertThat(result.get(2)).isEqualTo(Boolean.TRUE);
        assertThat(result.get(3)).isNull();
    }

    @Test
    public void testRoundTripEmptyObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        byte[] bytes = mapper.writeValueAsBytes(map);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void testRoundTripSimpleObject() {
        Map<String, Object> original = new LinkedHashMap<>();
        original.put("name", "Alice");
        original.put("age", 30);
        original.put("active", true);
        byte[] bytes = mapper.writeValueAsBytes(original);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertThat(result).hasSize(3);
        assertThat(result.get("name")).isEqualTo("Alice");
        assertThat(((Number) result.get("age")).intValue()).isEqualTo(30);
        assertThat(result.get("active")).isEqualTo(Boolean.TRUE);
    }

    @Test
    public void testRoundTripNestedObject() {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("x", 1);
        inner.put("y", 2);
        Map<String, Object> outer = new LinkedHashMap<>();
        outer.put("point", inner);
        outer.put("label", "origin");

        byte[] bytes = mapper.writeValueAsBytes(outer);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertThat(result).hasSize(2);
        assertThat(result.get("label")).isEqualTo("origin");
        Map<?, ?> pointResult = (Map<?, ?>) result.get("point");
        assertThat(pointResult).isNotNull();
        assertThat(((Number) pointResult.get("x")).intValue()).isEqualTo(1);
        assertThat(((Number) pointResult.get("y")).intValue()).isEqualTo(2);
    }

    // =========================================================
    // POJO ROUND-TRIP
    // =========================================================

    public static class Point {
        public int x;
        public int y;

        public Point() {}
        public Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point p)) return false;
            return x == p.x && y == p.y;
        }
    }

    @Test
    public void testRoundTripPojo() {
        Point original = new Point(3, 7);
        byte[] bytes = mapper.writeValueAsBytes(original);
        Point result = mapper.readValue(bytes, Point.class);
        assertThat(result).isEqualTo(original);
    }

    // =========================================================
    // OBJECT KEY SORTING
    // =========================================================

    @Test
    public void testObjectKeysSorted() {
        VPackMapper vPackMapper = VPackMapper.builder().enable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED).build();
        // Write {b:1, a:2} and assert the wire bytes have 'a' before 'b'
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 1);
        map.put("a", 2);
        byte[] bytes = vPackMapper.writeValueAsBytes(map);
        // Parse back and verify order in the bytes
        // We can check this by looking at the serialized bytes for 'a' and 'b'
        // 'a' as VPack short string: 0x41 0x61
        // 'b' as VPack short string: 0x41 0x62
        // In the sorted wire format, 'a' should appear before 'b'
        int posA = -1, posB = -1;
        for (int i = 0; i < bytes.length - 1; i++) {
            if ((bytes[i] & 0xFF) == 0x41 && bytes[i + 1] == 0x61 && posA < 0) posA = i;
            if ((bytes[i] & 0xFF) == 0x41 && bytes[i + 1] == 0x62 && posB < 0) posB = i;
        }
        assertThat(posA).as("Key 'a' not found in wire bytes").isGreaterThanOrEqualTo(0);
        assertThat(posB).as("Key 'b' not found in wire bytes").isGreaterThanOrEqualTo(0);
        assertThat(posA).as("Expected 'a' before 'b' in sorted wire format").isLessThan(posB);
    }

    @Test
    public void testObjectKeysUnsortedWhenFeatureDisabled() {
        VPackMapper unsortedMapper = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .disable(VPackWriteFeature.WRITE_COMPACT_OBJECTS)
                .build();
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 1);
        map.put("a", 2);
        byte[] bytes = unsortedMapper.writeValueAsBytes(map);
        // With unsorted: type byte should be 0x0f (unsorted 1-byte width)
        // rather than 0x0b (sorted 1-byte width)
        assertThat(bytes[0]).as("Expected unsorted object type 0x0f").isEqualTo((byte) 0x0f);
    }
}
