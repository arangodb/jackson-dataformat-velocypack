package com.arangodb.jackson.dataformat.velocypack.mapper;

import org.junit.jupiter.api.Test;
import com.arangodb.jackson.dataformat.velocypack.BaseTestForVPack;
import com.arangodb.jackson.dataformat.velocypack.VPackMapper;
import com.arangodb.jackson.dataformat.velocypack.VPackWriteFeature;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Round-trip tests: serialize then deserialize, verifying the result equals the original.
 */
public class VPackRoundTripTest extends BaseTestForVPack
{
    private final VPackMapper mapper = new VPackMapper();

    // Helper
    private <T> T roundTrip(T value, Class<T> type) throws Exception {
        byte[] bytes = mapper.writeValueAsBytes(value);
        return mapper.readValue(bytes, type);
    }

    // =========================================================
    // SIMPLE VALUES
    // =========================================================

    @Test
    public void testRoundTripNull() throws Exception {
        byte[] bytes = mapper.writeValueAsBytes(null);
        Object result = mapper.readValue(bytes, Object.class);
        assertNull(result);
    }

    @Test
    public void testRoundTripBoolean_false() throws Exception {
        Boolean result = roundTrip(Boolean.FALSE, Boolean.class);
        assertEquals(Boolean.FALSE, result);
    }

    @Test
    public void testRoundTripBoolean_true() throws Exception {
        Boolean result = roundTrip(Boolean.TRUE, Boolean.class);
        assertEquals(Boolean.TRUE, result);
    }

    @Test
    public void testRoundTripInteger_0() throws Exception {
        assertEquals(Integer.valueOf(0), roundTrip(0, Integer.class));
    }

    @Test
    public void testRoundTripInteger_9() throws Exception {
        assertEquals(Integer.valueOf(9), roundTrip(9, Integer.class));
    }

    @Test
    public void testRoundTripInteger_negative1() throws Exception {
        assertEquals(Integer.valueOf(-1), roundTrip(-1, Integer.class));
    }

    @Test
    public void testRoundTripInteger_negative6() throws Exception {
        assertEquals(Integer.valueOf(-6), roundTrip(-6, Integer.class));
    }

    @Test
    public void testRoundTripInteger_maxValue() throws Exception {
        assertEquals(Integer.MAX_VALUE, (int) roundTrip(Integer.MAX_VALUE, Integer.class));
    }

    @Test
    public void testRoundTripInteger_minValue() throws Exception {
        assertEquals(Integer.MIN_VALUE, (int) roundTrip(Integer.MIN_VALUE, Integer.class));
    }

    @Test
    public void testRoundTripLong_maxValue() throws Exception {
        assertEquals(Long.MAX_VALUE, (long) roundTrip(Long.MAX_VALUE, Long.class));
    }

    @Test
    public void testRoundTripLong_minValue() throws Exception {
        assertEquals(Long.MIN_VALUE, (long) roundTrip(Long.MIN_VALUE, Long.class));
    }

    @Test
    public void testRoundTripDouble_zero() throws Exception {
        assertEquals(0.0, (double) roundTrip(0.0, Double.class), 0.0);
    }

    @Test
    public void testRoundTripDouble_pi() throws Exception {
        assertEquals(Math.PI, (double) roundTrip(Math.PI, Double.class), 0.0);
    }

    @Test
    public void testRoundTripString_empty() throws Exception {
        assertEquals("", roundTrip("", String.class));
    }

    @Test
    public void testRoundTripString_hello() throws Exception {
        assertEquals("hello", roundTrip("hello", String.class));
    }

    @Test
    public void testRoundTripString_126chars() throws Exception {
        String s = "A".repeat(126);
        assertEquals(s, roundTrip(s, String.class));
    }

    @Test
    public void testRoundTripString_127chars() throws Exception {
        String s = "B".repeat(127);
        assertEquals(s, roundTrip(s, String.class));
    }

    @Test
    public void testRoundTripString_unicode() throws Exception {
        String s = "Hello \u4e16\u754c!"; // "Hello 世界!"
        assertEquals(s, roundTrip(s, String.class));
    }

    // =========================================================
    // COLLECTIONS
    // =========================================================

    @Test
    public void testRoundTripEmptyArray() throws Exception {
        List<Object> list = new ArrayList<>();
        byte[] bytes = mapper.writeValueAsBytes(list);
        List<?> result = mapper.readValue(bytes, List.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRoundTripIntArray() throws Exception {
        List<Integer> original = Arrays.asList(1, 2, 3, 4, 5);
        byte[] bytes = mapper.writeValueAsBytes(original);
        List<?> result = mapper.readValue(bytes, List.class);
        assertEquals(5, result.size());
        assertEquals(1, ((Number) result.get(0)).intValue());
        assertEquals(5, ((Number) result.get(4)).intValue());
    }

    @Test
    public void testRoundTripMixedArray() throws Exception {
        List<Object> original = Arrays.asList("hello", 42, true, null);
        byte[] bytes = mapper.writeValueAsBytes(original);
        List<?> result = mapper.readValue(bytes, List.class);
        assertEquals(4, result.size());
        assertEquals("hello", result.get(0));
        assertEquals(42, ((Number) result.get(1)).intValue());
        assertEquals(Boolean.TRUE, result.get(2));
        assertNull(result.get(3));
    }

    @Test
    public void testRoundTripEmptyObject() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        byte[] bytes = mapper.writeValueAsBytes(map);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRoundTripSimpleObject() throws Exception {
        Map<String, Object> original = new LinkedHashMap<>();
        original.put("name", "Alice");
        original.put("age", 30);
        original.put("active", true);
        byte[] bytes = mapper.writeValueAsBytes(original);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertEquals(3, result.size());
        assertEquals("Alice", result.get("name"));
        assertEquals(30, ((Number) result.get("age")).intValue());
        assertEquals(Boolean.TRUE, result.get("active"));
    }

    @Test
    public void testRoundTripNestedObject() throws Exception {
        Map<String, Object> inner = new LinkedHashMap<>();
        inner.put("x", 1);
        inner.put("y", 2);
        Map<String, Object> outer = new LinkedHashMap<>();
        outer.put("point", inner);
        outer.put("label", "origin");

        byte[] bytes = mapper.writeValueAsBytes(outer);
        Map<?, ?> result = mapper.readValue(bytes, Map.class);
        assertEquals(2, result.size());
        assertEquals("origin", result.get("label"));
        Map<?, ?> pointResult = (Map<?, ?>) result.get("point");
        assertNotNull(pointResult);
        assertEquals(1, ((Number) pointResult.get("x")).intValue());
        assertEquals(2, ((Number) pointResult.get("y")).intValue());
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
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return x == p.x && y == p.y;
        }
    }

    @Test
    public void testRoundTripPojo() throws Exception {
        Point original = new Point(3, 7);
        byte[] bytes = mapper.writeValueAsBytes(original);
        Point result = mapper.readValue(bytes, Point.class);
        assertEquals(original, result);
    }

    // =========================================================
    // OBJECT KEY SORTING
    // =========================================================

    @Test
    public void testObjectKeysSortedByDefault() throws Exception {
        // Write {b:1, a:2} and assert the wire bytes have 'a' before 'b'
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 1);
        map.put("a", 2);
        byte[] bytes = mapper.writeValueAsBytes(map);
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
        assertTrue(posA >= 0, "Key 'a' not found in wire bytes");
        assertTrue(posB >= 0, "Key 'b' not found in wire bytes");
        assertTrue(posA < posB, "Expected 'a' before 'b' in sorted wire format");
    }

    @Test
    public void testObjectKeysUnsortedWhenFeatureDisabled() throws Exception {
        VPackMapper unsortedMapper = VPackMapper.builder()
                .disable(VPackWriteFeature.WRITE_OBJECT_KEYS_SORTED)
                .build();
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("b", 1);
        map.put("a", 2);
        byte[] bytes = unsortedMapper.writeValueAsBytes(map);
        // With unsorted: type byte should be 0x0f (unsorted 1-byte width)
        // rather than 0x0b (sorted 1-byte width)
        assertEquals((byte) 0x0f, bytes[0], "Expected unsorted object type 0x0f");
    }
}
