package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0520Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: RecordBasicsTest#testDeserializeRecordOfRecord().
    void testDeserializeRecordOfRecordVpack() throws Exception {
        // Independent compact fixture for {record:{id:123,name:"Bob"}}.
        assertEquals(new RecordOfRecord(new SimpleRecord(123, "Bob")),
                MAPPER.readValue(VPackWireFixtureTest.hex(
                        "14 1b 46 72 65 63 6f 72 64 14 11 42 69 64 28 7b "
                      + "44 6e 61 6d 65 43 42 6f 62 02 01"), RecordOfRecord.class));
    }

    // Provenance: RecordBasicsTest#testDeserializeSimpleRecord().
    void testDeserializeSimpleRecordVpack() throws Exception {
        assertEquals(new SimpleRecord(123, "Bob"), MAPPER.readValue(
                simpleRecordFixture(), SimpleRecord.class));
    }

    // Provenance: RecordBasicsTest#testDeserializeSimpleRecord_DisableAnnotationIntrospector().
    void testDeserializeSimpleRecordDisableAnnotationIntrospectorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .configure(MapperFeature.USE_ANNOTATIONS, false).build();
        assertEquals(new SimpleRecord(123, "Bob"), mapper.readValue(
                simpleRecordFixture(), SimpleRecord.class));
    }

    // Provenance: RecordBasicsTest#testDeserialize_AllWriteOnlyParameter().
    void testDeserializeAllWriteOnlyParameterVpack() throws Exception {
        assertEquals(new RecordAllWriteOnly(123, "Bob", "bob@example.com"), MAPPER.readValue(
                writeOnlyRecordFixture(), RecordAllWriteOnly.class));
    }

    // Provenance: RecordBasicsTest#testDeserialize_SingleWriteOnlyParameter().
    void testDeserializeSingleWriteOnlyParameterVpack() throws Exception {
        assertEquals(new RecordSingleWriteOnly(123), MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 09 01 42 69 64 28 7b 03"),
                RecordSingleWriteOnly.class));
    }

    // Provenance: RecordBasicsTest#testDeserialize_SomeWriteOnlyParameter().
    void testDeserializeSomeWriteOnlyParameterVpack() throws Exception {
        assertEquals(new RecordSomeWriteOnly(123, "Bob", "bob@example.com"), MAPPER.readValue(
                writeOnlyRecordFixture(), RecordSomeWriteOnly.class));
    }

    // Provenance: RecordBasicsTest#testJsonDeserializeOnRecord().
    void testJsonDeserializeOnRecordVpack() throws Exception {
        Animal188 result = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 14 02 44 6e 61 6d 65 43 63 61 74 43 61 67 65 28 04 0c 03"),
                Animal188.class);
        assertEquals("custom-desercat", result.name());
        assertEquals(4, result.age());
    }

    // Provenance: RecordBasicsTest#testJsonSerializeOnRecord().
    void testJsonSerializeOnRecordVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new Animal188("dog", 3)), Map.class);
        assertEquals("custom dog", wire.get("name"));
        assertEquals(3, wire.get("age"));
    }

    // Provenance: RecordBasicsTest#testNamingStrategy().
    void testNamingStrategyVpack() throws Exception {
        SnakeRecord input = new SnakeRecord("123", "value");
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(input), Map.class);
        assertEquals("123", wire.get("my_id"));
        assertEquals("value", wire.get("my_value"));
        assertEquals(2, wire.size());

        assertEquals(input, MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 1e 02 45 6d 79 5f 69 64 43 31 32 33 48 6d 79 5f 76 61 6c 75 65 "
              + "45 76 61 6c 75 65 03 0d"), SnakeRecord.class));
    }

    // Provenance: RecordBasicsTest#testRecordJavaType().
    void testRecordJavaTypeVpack() {
        assertFalse(MAPPER.constructType(T32_0520Fixture.class).isRecordType());
        assertTrue(MAPPER.constructType(SimpleRecord.class).isRecordType());
        assertTrue(MAPPER.constructType(RecordOfRecord.class).isRecordType());
        assertTrue(MAPPER.constructType(RecordWithRename.class).isRecordType());
    }

    // Provenance: RecordBasicsTest#testSerializeEmptyRecord().
    void testSerializeEmptyRecordVpack() throws Exception {
        org.junit.jupiter.api.Assertions.assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new EmptyRecord()));
    }

    // Provenance: RecordBasicsTest#testSerializeJsonRename().
    void testSerializeJsonRenameVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new RecordWithRename(123, "Bob")), Map.class);
        assertEquals(123, wire.get("id"));
        assertEquals("Bob", wire.get("rename"));
        assertEquals(2, wire.size());
    }
private static byte[] simpleRecordFixture() {
        // Indexed object body: {id:123,name:"Bob"}; offsets are 3 and 8.
        return VPackWireFixtureTest.hex(
                "0b 13 02 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 03 08");
    }
private static byte[] writeOnlyRecordFixture() {
        // Indexed object body: {id:123,name:"Bob",email:"bob@example.com"}.
        return VPackWireFixtureTest.hex(
                "0b 2a 03 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 "
              + "45 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d "
              + "11 03 08");
    }
record EmptyRecord() { }
record SimpleRecord(int id, String name) { }
record RecordOfRecord(SimpleRecord record) { }
record RecordWithRename(int id, @JsonProperty("rename") String name) { }
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record SnakeRecord(String myId, String myValue) { }
record Animal188(
            @JsonDeserialize(using = PrefixStringDeserializer.class)
            @JsonSerialize(using = PrefixStringSerializer.class)
            String name,
            Integer age) { }
static class PrefixStringSerializer extends StdScalarSerializer<String> {
        PrefixStringSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator generator, SerializationContext provider) {
            generator.writeString("custom " + value);
        }
    }
static class PrefixStringDeserializer extends StdScalarDeserializer<String> {
        PrefixStringDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext ctxt) {
            return "custom-deser" + parser.getString();
        }
    }

    void __invoke_testDeserializeRecordOfRecordVpack() throws Exception {
        try {
            testDeserializeRecordOfRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSimpleRecordVpack() throws Exception {
        try {
            testDeserializeSimpleRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSimpleRecordDisableAnnotationIntrospectorVpack() throws Exception {
        try {
            testDeserializeSimpleRecordDisableAnnotationIntrospectorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeAllWriteOnlyParameterVpack() throws Exception {
        try {
            testDeserializeAllWriteOnlyParameterVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSingleWriteOnlyParameterVpack() throws Exception {
        try {
            testDeserializeSingleWriteOnlyParameterVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeSomeWriteOnlyParameterVpack() throws Exception {
        try {
            testDeserializeSomeWriteOnlyParameterVpack();
        } finally {
        }
    }


    void __invoke_testJsonDeserializeOnRecordVpack() throws Exception {
        try {
            testJsonDeserializeOnRecordVpack();
        } finally {
        }
    }


    void __invoke_testJsonSerializeOnRecordVpack() throws Exception {
        try {
            testJsonSerializeOnRecordVpack();
        } finally {
        }
    }


    void __invoke_testNamingStrategyVpack() throws Exception {
        try {
            testNamingStrategyVpack();
        } finally {
        }
    }


    void __invoke_testRecordJavaTypeVpack() throws Exception {
        try {
            testRecordJavaTypeVpack();
        } finally {
        }
    }


    void __invoke_testSerializeEmptyRecordVpack() throws Exception {
        try {
            testSerializeEmptyRecordVpack();
        } finally {
        }
    }


    void __invoke_testSerializeJsonRenameVpack() throws Exception {
        try {
            testSerializeJsonRenameVpack();
        } finally {
        }
    }

}
