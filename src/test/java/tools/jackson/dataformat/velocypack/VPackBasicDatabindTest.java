package tools.jackson.dataformat.velocypack;

import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Ordinary databind coverage, deliberately separate from native VPack values. */
class VPackBasicDatabindTest {
    private final VPackMapper vpack = new VPackMapper();

    @Test
    // Equivalent coverage for databind ArrayDeserializationTest#testIntArray and
    // ObjectMapperTest#test_readValue_ByteArray, re-expressed through VPack.
    void mapsScalarsWrappersContainersEnumsPojoAndRecord() throws Exception {
        PortableRecord input = sample();

        PortableRecord output = vpack.readValue(vpack.writeValueAsBytes(input),
                PortableRecord.class);

        assertPortableEquals(input, output);
        assertArrayEquals(input.bytes(), output.bytes());
        assertEquals(Flavor.GREEN, output.flavor());
        assertEquals(new Child("nested", List.of(2, 3)), output.child());
    }

    @Test
    void untypedMappingRetainsLogicalContainersAndBinaryBytes() throws Exception {
        PortableRecord input = sample();

        Map<?, ?> value = vpack.readValue(vpack.writeValueAsBytes(input), Map.class);

        assertEquals("Ada", value.get("text"));
        assertEquals(37, value.get("intValue"));
        assertEquals(List.of("a", "b"), value.get("labels"));
        assertEquals(Map.of("answer", 42), value.get("scores"));
        assertEquals("GREEN", value.get("flavor"));
        assertArrayEquals(input.bytes(), (byte[]) value.get("bytes"));
    }

    @Test
    void JsonMapperAndVPackMapperAgreeOnPortableTypedValues() throws Exception {
        PortableRecord input = sample();
        JsonMapper json = JsonMapper.builder().build();

        PortableRecord fromVPack = vpack.readValue(vpack.writeValueAsBytes(input),
                PortableRecord.class);
        PortableRecord fromJson = json.readValue(json.writeValueAsBytes(input),
                PortableRecord.class);

        assertPortableEquals(input, fromVPack);
        assertPortableEquals(input, fromJson);
        assertPortableEquals(fromJson, fromVPack);
    }

    @Test
    void nullRootsAndNullablePropertiesFollowDatabindSemantics() throws Exception {
        assertEquals(null, vpack.readValue(vpack.writeValueAsBytes(null), String.class));

        NullableRecord input = new NullableRecord(null, null,
                Arrays.asList("present", null));
        NullableRecord output = vpack.readValue(vpack.writeValueAsBytes(input),
                NullableRecord.class);
        assertEquals(input, output);
        assertEquals(null, output.number());
        assertEquals(null, output.child());
    }

    private static PortableRecord sample() {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("answer", 42);
        return new PortableRecord(37, 9_007_199_254_740_993L, 2.5d, true,
                Integer.valueOf(12), Boolean.TRUE, "Ada", new byte[] { 1, 2, 3 },
                new int[] { 4, 5 }, List.of("a", "b"), scores, Flavor.GREEN,
                new Child("nested", List.of(2, 3)));
    }

    private static void assertPortableEquals(PortableRecord expected, PortableRecord actual) {
        assertEquals(expected.intValue(), actual.intValue());
        assertEquals(expected.longValue(), actual.longValue());
        assertEquals(expected.doubleValue(), actual.doubleValue());
        assertEquals(expected.boolValue(), actual.boolValue());
        assertEquals(expected.boxedInt(), actual.boxedInt());
        assertEquals(expected.boxedBool(), actual.boxedBool());
        assertEquals(expected.text(), actual.text());
        assertArrayEquals(expected.bytes(), actual.bytes());
        assertArrayEquals(expected.ints(), actual.ints());
        assertEquals(expected.labels(), actual.labels());
        assertEquals(expected.scores(), actual.scores());
        assertEquals(expected.flavor(), actual.flavor());
        assertEquals(expected.child(), actual.child());
    }

    enum Flavor { RED, GREEN }

    record Child(String label, List<Integer> values) { }

    record PortableRecord(int intValue, long longValue, double doubleValue,
            boolean boolValue, Integer boxedInt, Boolean boxedBool, String text,
            byte[] bytes, int[] ints, List<String> labels, Map<String, Integer> scores,
            Flavor flavor, Child child) { }

    record NullableRecord(Integer number, Child child, List<String> values) { }
}
