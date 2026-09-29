package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0522F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: RecordCreatorsTest#testDeserializeWithCanonicalCtorOverride().
    void testDeserializeWithCanonicalCtorOverrideVpack() throws Exception {
        RecordWithCanonicalCtorOverride value = MAPPER.readValue(VPackWireFixtureTest.hex(
                "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02"),
                RecordWithCanonicalCtorOverride.class);
        assertEquals(123, value.id());
        assertEquals("name", value.name());
    }

    // Provenance: RecordCreatorsTest#testDeserializeWithAltCtor().
    void testDeserializeWithAltCtorVpack() throws Exception {
        RecordWithAltCtor value = MAPPER.readValue(VPackWireFixtureTest.hex(
                "14 09 42 69 64 29 fc 0a 01"), RecordWithAltCtor.class);
        assertEquals(2812, value.id());
        assertEquals("name2", value.name());

        UnrecognizedPropertyException failure = assertThrows(UnrecognizedPropertyException.class,
                () -> MAPPER.readValue(VPackWireFixtureTest.hex(
                        "14 12 42 69 64 29 fc 0a 44 6e 61 6d 65 43 42 6f 62 02"),
                        RecordWithAltCtor.class));
        String message = failure.getMessage();
        org.junit.jupiter.api.Assertions.assertTrue(message.contains("name"));
        org.junit.jupiter.api.Assertions.assertTrue(message.contains("RecordWithAltCtor"));
    }

    // Provenance: RecordCreatorsTest#testDeserializeWithCreatorAndJsonValue4724().
    void testDeserializeWithCreatorAndJsonValue4724Vpack() throws Exception {
        assertNull(MAPPER.readValue(VPackWireFixtureTest.hex("40"), Something.class));
    }

    // Provenance: RecordCreatorsTest#testDeserializeWithDelegatingCtor().
    void testDeserializeWithDelegatingCtorVpack() throws Exception {
        RecordWithDelegation value = MAPPER.readValue(VPackWireFixtureTest.hex(
                "46 66 6f 6f 62 61 72"), RecordWithDelegation.class);
        assertEquals("del:foobar", value.accessValueForTest());
        assertArrayEquals(VPackWireFixtureTest.hex("4e 76 61 6c 3a 64 65 6c 3a 66 6f 6f 62 61 72"),
                MAPPER.writeValueAsBytes(value));
    }

    // Provenance: RecordCreatorsTest#testFailingSetter3938().
    void testFailingSetter3938Vpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        assertEquals(new Command3938(1, "abc"), mapper.readValue(VPackWireFixtureTest.hex(
                "14 12 42 69 64 31 46 66 69 6c 74 65 72 43 61 62 63 02"),
                Command3938.class));
        assertEquals(new Command3938(2, "abc"), mapper.readValue(VPackWireFixtureTest.hex(
                "14 1b 42 69 64 32 46 66 69 6c 74 65 72 43 61 62 63 47 6f 70 74 69 6f 6e 73 18 03"),
                Command3938.class));
        assertThrows(DatabindException.class, () -> mapper.readValue(VPackWireFixtureTest.hex(
                "14 14 42 69 64 32 47 6f 70 74 69 6f 6e 73 13 05 28 7b 01 02"),
                Command3938.class));
    }

    // Provenance: RecordCreatorsTest#testJsonAnySetterOnRecord().
    void testJsonAnySetterOnRecordVpack() throws Exception {
        TestRecord3439 result = new VPackMapper().readValue(VPackWireFixtureTest.hex(
                "14 31 45 66 69 65 6c 64 45 76 61 6c 75 65 49 75 6e 6d 61 70 70 65 64 31 46 76 61 6c 75 65 31 49 75 6e 6d 61 70 70 65 64 32 46 76 61 6c 75 65 32 03"),
                TestRecord3439.class);
        assertEquals("value", result.field());
        assertEquals(Map.of("unmapped1", "value1", "unmapped2", "value2"), result.anySetter());
    }

    // Provenance: RecordCreatorsTest#testJsonIgnoreOnRecordComponentNotPassedToAnySetter5952().
    void testJsonIgnoreOnRecordComponentNotPassedToAnySetter5952Vpack() throws Exception {
        UserRecordWithAnySetter5952 result = new VPackMapper().readValue(VPackWireFixtureTest.hex(
                "14 2e 44 6e 61 6d 65 45 61 6c 69 63 65 4e 73 65 6e 73 69 74 69 76 65 46 69 65 6c 64 46 73 65 63 72 65 74 45 6f 74 68 65 72 43 76 61 6c 03"),
                UserRecordWithAnySetter5952.class);
        assertEquals("alice", result.name());
        assertEquals(Map.of("other", "val"), result.extras());
    }

    // Provenance: RecordCreatorsTest#testRecordWithAnySetterCtor().
    void testRecordWithAnySetterCtorVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        RecordWithAnySetterCtor562 result = mapper.readValue(VPackWireFixtureTest.hex(
                "14 0d 47 72 65 67 75 6c 61 72 28 0d 01"),
                RecordWithAnySetterCtor562.class);
        assertEquals(13, result.id());
        assertEquals(Map.of(), result.additionalProperties());

        result = mapper.readValue(VPackWireFixtureTest.hex(
                "14 1e 47 72 65 67 75 6c 61 72 28 0d 47 75 6e 6b 6e 6f 77 6e 28 63 45 65 78 74 72 61 3f 03"),
                RecordWithAnySetterCtor562.class);
        assertEquals(13, result.id());
        assertEquals(Map.of("unknown", 99, "extra", -1), result.additionalProperties());
    }

    // Provenance: RecordCreatorsTest#testRoundTrip5923().
    void testRoundTrip5923Vpack() throws Exception {
        Outer5923 original = new Outer5923(new Inner5923(true));
        Outer5923 roundTripped = MAPPER.readValue(MAPPER.writeValueAsBytes(original), Outer5923.class);
        assertEquals(original.bools().innerValue(), roundTripped.bools().innerValue());
    }

    // Provenance: RecordCreatorsTest#testSerialization5923ViaJsonValue().
    void testSerialization5923ViaJsonValueVpack() throws Exception {
        assertEquals(Map.of("renamed", true), MAPPER.readValue(MAPPER.writeValueAsBytes(
                new Outer5923(new Inner5923(true))), Map.class));
        assertEquals(Map.of("renamed", false), MAPPER.readValue(MAPPER.writeValueAsBytes(
                new Outer5923(new Inner5923(false))), Map.class));
    }
record RecordWithCanonicalCtorOverride(int id, String name) {
        public RecordWithCanonicalCtorOverride(int id, String name) {
            this.id = id;
            this.name = "name";
        }
    }
record RecordWithAltCtor(int id, String name) {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RecordWithAltCtor(@JsonProperty("id") int id) {
            this(id, "name2");
        }
    }
record RecordWithDelegation(String value) {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public RecordWithDelegation(String value) {
            this.value = "del:" + value;
        }

        @JsonValue
        public String getValue() {
            return "val:" + value;
        }

        public String accessValueForTest() { return value; }
    }
record Something(String value) {
        @JsonCreator
        public static Something of(String value) {
            if (value.isEmpty()) {
                return null;
            }
            return new Something(value);
        }

        @JsonValue
        @Override
        public String toString() { return value; }
    }
interface NoOptionsCommand3938 {
        @JsonProperty("options")
        default void setOptions(JsonNode value) {
            if (value.isNull()) {
                return;
            }
            throw new IllegalArgumentException("Non-null 'options' not allowed for "
                    + getClass().getName());
        }
    }
record Command3938(int id, String filter) implements NoOptionsCommand3938 { }
record RecordWithAnySetterCtor562(int id, Map<String, Integer> additionalProperties) {
        @JsonCreator
        public RecordWithAnySetterCtor562(@JsonProperty("regular") int id,
                @JsonAnySetter Map<String, Integer> additionalProperties) {
            this.id = id;
            this.additionalProperties = additionalProperties;
        }
    }
record TestRecord3439(@JsonProperty String field,
            @JsonAnySetter Map<String, Object> anySetter) { }
record UserRecordWithAnySetter5952(String name,
            @JsonIgnore String sensitiveField,
            @JsonAnySetter Map<String, Object> extras) { }
record Inner5923(@JsonProperty(required = true, value = "innerValue") boolean innerValue) { }
record Outer5923(Inner5923 bools) {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static Outer5923 fromJson(
                @JsonProperty(required = true, value = "renamed") boolean booleanValue) {
            return new Outer5923(new Inner5923(booleanValue));
        }

        @JsonValue
        public Map<String, Boolean> toJson() {
            return Map.of("renamed", bools.innerValue());
        }
    }
record DuplicatePropRecord4690(String first) { }
static class DuplicatePropPojo4690 {
        private String first;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        DuplicatePropPojo4690(@JsonProperty("first") String first) { this.first = first; }

        public void setFirst(String first) { this.first = first; }

        public String getFirst() { return first; }
    }

    void __invoke_testDeserializeWithCanonicalCtorOverrideVpack() throws Exception {
        try {
            testDeserializeWithCanonicalCtorOverrideVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithAltCtorVpack() throws Exception {
        try {
            testDeserializeWithAltCtorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithCreatorAndJsonValue4724Vpack() throws Exception {
        try {
            testDeserializeWithCreatorAndJsonValue4724Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithDelegatingCtorVpack() throws Exception {
        try {
            testDeserializeWithDelegatingCtorVpack();
        } finally {
        }
    }


    void __invoke_testFailingSetter3938Vpack() throws Exception {
        try {
            testFailingSetter3938Vpack();
        } finally {
        }
    }


    void __invoke_testJsonAnySetterOnRecordVpack() throws Exception {
        try {
            testJsonAnySetterOnRecordVpack();
        } finally {
        }
    }


    void __invoke_testJsonIgnoreOnRecordComponentNotPassedToAnySetter5952Vpack() throws Exception {
        try {
            testJsonIgnoreOnRecordComponentNotPassedToAnySetter5952Vpack();
        } finally {
        }
    }


    void __invoke_testRecordWithAnySetterCtorVpack() throws Exception {
        try {
            testRecordWithAnySetterCtorVpack();
        } finally {
        }
    }


    void __invoke_testRoundTrip5923Vpack() throws Exception {
        try {
            testRoundTrip5923Vpack();
        } finally {
        }
    }


    void __invoke_testSerialization5923ViaJsonValueVpack() throws Exception {
        try {
            testSerialization5923ViaJsonValueVpack();
        } finally {
        }
    }

}
