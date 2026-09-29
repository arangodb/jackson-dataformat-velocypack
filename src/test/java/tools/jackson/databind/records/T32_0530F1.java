package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0530F1 {
private static final ObjectMapper NULL_MAPPER = VPackMapper.builder()
            .changeDefaultNullHandling(n -> n.withValueNulls(Nulls.FAIL)
                    .withContentNulls(Nulls.FAIL))
            .build();
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] RECORD_VALID = VPackWireFixtureTest.hex(
            "14 13 49 66 69 65 6c 64 4e 61 6d 65 45 76 61 6c 75 65 01");
private static final byte[] RECORD_NULL = VPackWireFixtureTest.hex(
            "14 0e 49 66 69 65 6c 64 4e 61 6d 65 18 01");
private static final byte[] FIXED_VALID = VPackWireFixtureTest.hex(
            "14 14 4a 66 69 65 6c 64 5f 6e 61 6d 65 45 76 61 6c 75 65 01");
private static final byte[] FIXED_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 66 69 65 6c 64 5f 6e 61 6d 65 18 01");

    // Provenance: RecordSerializationOrderTest#testSerializationOrder().
    void testSerializationOrderVpack() throws Exception {
        NestedRecordTwo nested = new NestedRecordTwo("2", "111110");
        NestedRecordOne value = new NestedRecordOne("1", "test@records.com", nested);
        assertWire(VPackWireFixtureTest.hex(
                "0b 4c 03 42 69 64 41 31 45 65 6d 61 69 6c 50 74 65 73 74 40 72 65 63 6f 72 64 73 2e 63 6f 6d "
              + "4f 6e 65 73 74 65 64 52 65 63 6f 72 64 54 77 6f 0b 1a 02 42 69 64 41 32 "
              + "48 70 61 73 73 70 6f 72 74 46 31 31 31 31 31 30 03 08 08 03 1f"),
                MAPPER.writeValueAsBytes(value));
    }

    // Provenance: RecordSerializationOrderTest#testBasicSerializationOrderWithJsonProperty().
    void testBasicSerializationOrderWithJsonPropertyVpack() throws Exception {
        assertWire(VPackWireFixtureTest.hex(
                "0b 0c 02 42 61 61 31 41 62 32 03 07"),
                MAPPER.writeValueAsBytes(new JsonPropertyRecord(1, 2)));
    }

    // Provenance: RecordSerializationOrderTest#testBasicSerializationOrderWithJsonProperty2().
    void testBasicSerializationOrderWithJsonProperty2Vpack() throws Exception {
        assertWire(VPackWireFixtureTest.hex(
                "0b 0c 02 41 61 31 42 62 62 32 03 06"),
                MAPPER.writeValueAsBytes(new JsonPropertyRecord2(1, 2)));
    }

    // Provenance: RecordSerializationOrderTest#testSerializationOrderWithJsonProperty().
    void testSerializationOrderWithJsonPropertyVpack() throws Exception {
        NestedRecordTwo nested = new NestedRecordTwo("2", "111110");
        NestedRecordOneWithJsonProperty value =
                new NestedRecordOneWithJsonProperty("1", "test@records.com", nested);
        assertWire(VPackWireFixtureTest.hex(
                "0b 4b 03 42 69 64 41 31 45 65 6d 61 69 6c 50 74 65 73 74 40 72 65 63 6f 72 64 73 2e 63 6f 6d "
              + "4e 6e 65 73 74 65 64 50 72 6f 70 65 72 74 79 0b 1a 02 42 69 64 41 32 "
              + "48 70 61 73 73 70 6f 72 74 46 31 31 31 31 31 30 03 08 08 03 1f"),
                MAPPER.writeValueAsBytes(value));
    }

    // Provenance: RecordSerializationOrderTest#testSerializationOrderWithJsonPropertyIndexes().
    void testSerializationOrderWithJsonPropertyIndexesVpack() throws Exception {
        NestedRecordTwo nested = new NestedRecordTwo("2", "111110");
        NestedRecordOneWithJsonPropertyIndex value =
                new NestedRecordOneWithJsonPropertyIndex("1", "test@records.com", nested);
        assertWire(VPackWireFixtureTest.hex(
                "0b 4b 03 45 65 6d 61 69 6c 50 74 65 73 74 40 72 65 63 6f 72 64 73 2e 63 6f 6d "
              + "4e 6e 65 73 74 65 64 50 72 6f 70 65 72 74 79 0b 1a 02 42 69 64 41 32 "
              + "48 70 61 73 73 70 6f 72 74 46 31 31 31 31 31 30 03 08 42 69 64 41 31 03 43 1a"),
                MAPPER.writeValueAsBytes(value));
    }

    // Provenance: RecordSerializationOrderTest#testSerializationOrderWithJsonPropertyOrder().
    void testSerializationOrderWithJsonPropertyOrderVpack() throws Exception {
        NestedRecordTwo nested = new NestedRecordTwo("2", "111110");
        NestedRecordOneWithJsonPropertyOrder value =
                new NestedRecordOneWithJsonPropertyOrder("1", "test@records.com", nested);
        assertWire(VPackWireFixtureTest.hex(
                "0b 4b 03 45 65 6d 61 69 6c 50 74 65 73 74 40 72 65 63 6f 72 64 73 2e 63 6f 6d "
              + "4e 6e 65 73 74 65 64 50 72 6f 70 65 72 74 79 0b 1a 02 42 69 64 41 32 "
              + "48 70 61 73 73 70 6f 72 74 46 31 31 31 31 31 30 03 08 42 69 64 41 31 03 43 1a"),
                MAPPER.writeValueAsBytes(value));
    }

    // Provenance: RecordSerializationOrderTest#testSerializationOrderWrtCreatorAlphabetic().
    void testSerializationOrderWrtCreatorAlphabeticVpack() throws Exception {
        assertWire(VPackWireFixtureTest.hex(
                "0b 12 03 41 63 41 63 41 61 41 61 41 62 41 62 07 0b 03"),
                MAPPER.writeValueAsBytes(new CABRecord("c", "a", "b")));
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 12 03 41 61 41 61 41 62 41 62 41 63 41 63 03 07 0b"),
                mapper.writeValueAsBytes(new CABRecord("c", "a", "b")));
    }
private static void assertWire(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual);
    }
record PlainRecord(String fieldName) { }
record FixedRecord(@JsonProperty("field_name") String fieldName) { }
record NestedRecordOne(String id, String email, NestedRecordTwo nestedRecordTwo) { }
record NestedRecordOneWithJsonProperty(String id, String email,
            @JsonProperty("nestedProperty") NestedRecordTwo nestedRecordTwo) { }
record NestedRecordOneWithJsonPropertyIndex(@JsonProperty(index = 2) String id,
            @JsonProperty(index = 0) String email,
            @JsonProperty(value = "nestedProperty", index = 1) NestedRecordTwo nestedRecordTwo) { }
@JsonPropertyOrder({"email", "nestedProperty", "id"})
    record NestedRecordOneWithJsonPropertyOrder(String id, String email,
            @JsonProperty(value = "nestedProperty") NestedRecordTwo nestedRecordTwo) { }
record NestedRecordTwo(String id, String passport) { }
record CABRecord(String c, String a, String b) { }
record JsonPropertyRecord(@JsonProperty("aa") int a, int b) { }
record JsonPropertyRecord2(int a, @JsonProperty("bb") int b) { }

    void __invoke_testSerializationOrderVpack() throws Exception {
        try {
            testSerializationOrderVpack();
        } finally {
        }
    }


    void __invoke_testBasicSerializationOrderWithJsonPropertyVpack() throws Exception {
        try {
            testBasicSerializationOrderWithJsonPropertyVpack();
        } finally {
        }
    }


    void __invoke_testBasicSerializationOrderWithJsonProperty2Vpack() throws Exception {
        try {
            testBasicSerializationOrderWithJsonProperty2Vpack();
        } finally {
        }
    }


    void __invoke_testSerializationOrderWithJsonPropertyVpack() throws Exception {
        try {
            testSerializationOrderWithJsonPropertyVpack();
        } finally {
        }
    }


    void __invoke_testSerializationOrderWithJsonPropertyIndexesVpack() throws Exception {
        try {
            testSerializationOrderWithJsonPropertyIndexesVpack();
        } finally {
        }
    }


    void __invoke_testSerializationOrderWithJsonPropertyOrderVpack() throws Exception {
        try {
            testSerializationOrderWithJsonPropertyOrderVpack();
        } finally {
        }
    }


    void __invoke_testSerializationOrderWrtCreatorAlphabeticVpack() throws Exception {
        try {
            testSerializationOrderWrtCreatorAlphabeticVpack();
        } finally {
        }
    }

}
