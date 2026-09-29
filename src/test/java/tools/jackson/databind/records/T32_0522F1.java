package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0522F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: RecordDeserializationTest#testDuplicatePropertyDeserialization().
    void testDuplicatePropertyDeserializationVpack() throws Exception {
        DuplicatePropRecord4690 result = new VPackMapper().readValue(VPackWireFixtureTest.hex(
                "14 1c 45 66 69 72 73 74 45 76 61 6c 75 65 45 66 69 72 73 74 46 76 61 6c 75 65 32 02"),
                DuplicatePropRecord4690.class);
        assertEquals("value2", result.first());
    }

    // Provenance: RecordDeserializationTest#testDuplicatePropertyClassDeserialization().
    void testDuplicatePropertyClassDeserializationVpack() throws Exception {
        DuplicatePropPojo4690 result = new VPackMapper().readValue(VPackWireFixtureTest.hex(
                "14 29 45 66 69 72 73 74 45 76 61 6c 75 65 46 73 65 63 6f 6e 64 45 74 65 73 74 31 45 66 69 72 73 74 46 76 61 6c 75 65 32 03"),
                DuplicatePropPojo4690.class);
        assertEquals("value2", result.getFirst());
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

    void __invoke_testDuplicatePropertyDeserializationVpack() throws Exception {
        try {
            testDuplicatePropertyDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testDuplicatePropertyClassDeserializationVpack() throws Exception {
        try {
            testDuplicatePropertyClassDeserializationVpack();
        } finally {
        }
    }

}
