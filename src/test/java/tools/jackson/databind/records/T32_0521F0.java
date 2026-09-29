package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0521F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: RecordBasicsTest#testSerializePrivateTextRecord().
    void testSerializePrivateTextRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new PrivateTextRecord4175("anything")), Map.class);
        assertEquals(Map.of("text", "anything"), wire);
    }

    // Provenance: RecordBasicsTest#testSerializeRecordOfRecord().
    void testSerializeRecordOfRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordOfRecord(new SimpleRecord(123, "Bob"))),
                Map.class);
        assertEquals(Map.of("record", Map.of("id", 123, "name", "Bob")), wire);
    }

    // Provenance: RecordBasicsTest#testSerializeSimpleRecord().
    void testSerializeSimpleRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new SimpleRecord(123, "Bob")), Map.class);
        assertEquals(Map.of("id", 123, "name", "Bob"), wire);
    }

    // Provenance: RecordBasicsTest#testSerializeSimpleRecord_DisableAnnotationIntrospector().
    void testSerializeSimpleRecordDisableAnnotationIntrospectorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.USE_ANNOTATIONS, false).build();
        assertArrayEquals(MAPPER.writeValueAsBytes(new SimpleRecord(123, "Bob")),
                mapper.writeValueAsBytes(new SimpleRecord(123, "Bob")));
    }

    // Provenance: RecordBasicsTest#testSerializeWithSettersForGetters().
    void testSerializeWithSettersForGettersVpack() throws Exception {
        ObjectMapper mapperWithSetters = VPackMapper.builder()
                .configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true).build();
        SimpleRecord input = new SimpleRecord(123, "Bob");
        assertArrayEquals(MAPPER.writeValueAsBytes(input),
                mapperWithSetters.writeValueAsBytes(input));
    }

    // Provenance: RecordBasicsTest#testSerialize_AllWriteOnlyParameter().
    void testSerializeAllWriteOnlyParameterVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new RecordAllWriteOnly(123, "Bob", "bob@example.com")));
    }

    // Provenance: RecordBasicsTest#testSerialize_SingleWriteOnlyParameter().
    void testSerializeSingleWriteOnlyParameterVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new RecordSingleWriteOnly(123)));
    }

    // Provenance: RecordBasicsTest#testSerialize_SomeWriteOnlyParameter().
    void testSerializeSomeWriteOnlyParameterVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new RecordSomeWriteOnly(123, "Bob", "bob@example.com")), Map.class);
        assertEquals(Map.of("email", "bob@example.com"), wire);
    }
private static record PrivateTextRecord4175(String text) { }
record SimpleRecord(int id, String name) { }
record RecordOfRecord(SimpleRecord record) { }
record RecordSingleWriteOnly(
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) int id) { }
record RecordSomeWriteOnly(
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String name,
            String email) { }
record RecordAllWriteOnly(
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) int id,
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String name,
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String email) { }
public record PlainTestObject(
            @JsonProperty("strField") String testFieldName,
            @JsonProperty("intField") Integer testOtherField) { }
public record CreatorTestObject(
            @JsonProperty("strField") String testFieldName,
            @JsonProperty("intField") Integer testOtherField) {
        @JsonCreator
        public CreatorTestObject(
                @JsonProperty("strField") String testFieldName,
                @JsonProperty("someOtherIntField") Integer testOtherIntField,
                @JsonProperty("intField") Integer testOtherField) {
            this(testFieldName, testOtherField + testOtherIntField);
        }
    }
public record Inner5923(
            @JsonProperty(required = true, value = "innerValue") boolean innerValue) { }
public record Outer5923(Inner5923 bools) {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static Outer5923 fromJson(
                @JsonProperty(required = true, value = "renamed") boolean booleanValue) {
            return new Outer5923(new Inner5923(booleanValue));
        }

        @com.fasterxml.jackson.annotation.JsonValue
        public Map<String, Boolean> toJson() {
            return Map.of("renamed", bools.innerValue());
        }
    }

    void __invoke_testSerializePrivateTextRecordVpack() throws Exception {
        try {
            testSerializePrivateTextRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeRecordOfRecordVpack() throws Exception {
        try {
            testSerializeRecordOfRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeSimpleRecordVpack() throws Exception {
        try {
            testSerializeSimpleRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeSimpleRecordDisableAnnotationIntrospectorVpack() throws Exception {
        try {
            testSerializeSimpleRecordDisableAnnotationIntrospectorVpack();
        } finally {
        }
    }


    void __invoke_testSerializeWithSettersForGettersVpack() throws Exception {
        try {
            testSerializeWithSettersForGettersVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAllWriteOnlyParameterVpack() throws Exception {
        try {
            testSerializeAllWriteOnlyParameterVpack();
        } finally {
        }
    }


    void __invoke_testSerializeSingleWriteOnlyParameterVpack() throws Exception {
        try {
            testSerializeSingleWriteOnlyParameterVpack();
        } finally {
        }
    }


    void __invoke_testSerializeSomeWriteOnlyParameterVpack() throws Exception {
        try {
            testSerializeSomeWriteOnlyParameterVpack();
        } finally {
        }
    }

}
