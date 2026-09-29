package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidNullException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0530F0 {
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

    // Provenance: RecordNullHandlingTest#testRecordNullHandlingValid().
    void testRecordNullHandlingValidVpack() throws Exception {
        PlainRecord value = NULL_MAPPER.readValue(RECORD_VALID, PlainRecord.class);
        assertEquals("value", value.fieldName());
    }

    // Provenance: RecordNullHandlingTest#testRecordNullHandlingNullValue().
    void testRecordNullHandlingNullValueVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> NULL_MAPPER.readValue(RECORD_NULL, PlainRecord.class));
        assertEquals(true, exception.getMessage().contains("fieldName"));
    }

    // Provenance: RecordNullHandlingTest#testRecordNullHandlingEmptyJson().
    void testRecordNullHandlingEmptyJsonVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> NULL_MAPPER.readValue(VPackWireFixtureTest.hex("0a"), PlainRecord.class));
        assertEquals(true, exception.getMessage().contains("fieldName"));
    }

    // Provenance: RecordNullHandlingTest#testRecordFixedNullHandlingValid().
    void testRecordFixedNullHandlingValidVpack() throws Exception {
        FixedRecord value = NULL_MAPPER.readValue(FIXED_VALID, FixedRecord.class);
        assertEquals("value", value.fieldName());
    }

    // Provenance: RecordNullHandlingTest#testRecordFixedNullHandlingNullValue().
    void testRecordFixedNullHandlingNullValueVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> NULL_MAPPER.readValue(FIXED_NULL, FixedRecord.class));
        assertEquals(true, exception.getMessage().contains("field_name"));
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

    void __invoke_testRecordNullHandlingValidVpack() throws Exception {
        try {
            testRecordNullHandlingValidVpack();
        } finally {
        }
    }


    void __invoke_testRecordNullHandlingNullValueVpack() throws Exception {
        try {
            testRecordNullHandlingNullValueVpack();
        } finally {
        }
    }


    void __invoke_testRecordNullHandlingEmptyJsonVpack() throws Exception {
        try {
            testRecordNullHandlingEmptyJsonVpack();
        } finally {
        }
    }


    void __invoke_testRecordFixedNullHandlingValidVpack() throws Exception {
        try {
            testRecordFixedNullHandlingValidVpack();
        } finally {
        }
    }


    void __invoke_testRecordFixedNullHandlingNullValueVpack() throws Exception {
        try {
            testRecordFixedNullHandlingNullValueVpack();
        } finally {
        }
    }

}
