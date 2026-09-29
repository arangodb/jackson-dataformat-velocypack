package tools.jackson.databind.records;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0528F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] STRING_123_4 = VPackWireFixtureTest.hex(
            "45 31 32 33 2e 34");
private static final byte[] SNAKE_RECORD = VPackWireFixtureTest.hex(
            "14 1d 42 69 64 28 7b 4d 74 6f 5f 73 6e 61 6b 65 5f 63 61 73 65 "
          + "46 73 6e 61 6b 65 79 02");
private static final byte[] NAMED_RECORD = VPackWireFixtureTest.hex(
            "14 1b 45 6d 79 5f 69 64 42 69 64 48 6d 79 5f 76 61 6c 75 65 "
          + "45 76 61 6c 75 65 02");

    // Provenance: RecordMapKeyAnnotationsTest#testRecordAsKey().
    void testRecordAsKeyVpack() throws Exception {
        OuterRecord outer = new OuterRecord(new InnerRecord("innerKey", "innerValue"));
        Map<OuterRecord, String> map = Collections.singletonMap(outer, "value");

        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(map));
        assertEquals(1, tree.size());
        assertEquals("value", tree.get("innerKey").textValue());
    }

    // Provenance: RecordMapKeyAnnotationsTest#testRecordAsValue().
    void testRecordAsValueVpack() throws Exception {
        Map<String, OuterRecord> map = Collections.singletonMap("key",
                new OuterRecord(new InnerRecord("innerKey", "innerValue")));

        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(map));
        assertEquals("innerValue", tree.get("key").textValue());
    }

    // Provenance: RecordMapKeyAnnotationsTest#testNoKeyOuterRecord().
    void testNoKeyOuterRecordVpack() throws Exception {
        Map<String, NoKeyOuterRecord> map = Collections.singletonMap("key",
                new NoKeyOuterRecord(new InnerRecord("innerKey", "innerValue")));

        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(map));
        assertEquals("innerValue", tree.get("key").textValue());
    }

    // Provenance: RecordMapKeyAnnotationsTest#testRecordWithJsonValue3063().
    void testRecordWithJsonValue3063Vpack() throws Exception {
        Map<String, String> locations = Collections.singletonMap("a", "locationA");

        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(
                new GetLocations3063(locations)));
        assertNotNull(tree);
        assertEquals("locationA", tree.get("a").textValue());
    }

    // Provenance: RecordMapKeyAnnotationsTest#testSimpleRecordAsKey().
    void testSimpleRecordAsKeyVpack() throws Exception {
        SimpleKeyRecord keyRecord = new SimpleKeyRecord("myKey");
        Map<SimpleKeyRecord, String> map = Collections.singletonMap(keyRecord, "myValue");

        JsonNode tree = MAPPER.readTree(MAPPER.writeValueAsBytes(map));
        assertEquals("myValue", tree.get("myKey").textValue());
    }
record RecordWithImplicitFactoryMethods(java.math.BigDecimal id, String name) {
        public static RecordWithImplicitFactoryMethods valueOf(String id) {
            return new RecordWithImplicitFactoryMethods(
                    java.math.BigDecimal.valueOf(Double.parseDouble(id)), "StringFactoryMethod");
        }
    }
record InnerRecord(@JsonKey String key, @JsonValue String value) { }
record OuterRecord(@JsonKey @JsonValue InnerRecord inner) { }
record NoKeyOuterRecord(@JsonValue InnerRecord inner) { }
record SimpleKeyRecord(@JsonKey String id) { }
record GetLocations3063(@JsonValue Map<String, String> nameToLocation) {
        @JsonCreator
        public GetLocations3063(Map<String, String> nameToLocation) {
            this.nameToLocation = nameToLocation;
        }
    }
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record Record2992(String myId, String myValue) { }
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record SnakeRecord(int id, String toSnakeCase) {
        @JsonCreator
        public SnakeRecord(int id, String toSnakeCase) {
            this.id = id;
            this.toSnakeCase = toSnakeCase;
        }
    }

    void __invoke_testRecordAsKeyVpack() throws Exception {
        try {
            testRecordAsKeyVpack();
        } finally {
        }
    }


    void __invoke_testRecordAsValueVpack() throws Exception {
        try {
            testRecordAsValueVpack();
        } finally {
        }
    }


    void __invoke_testNoKeyOuterRecordVpack() throws Exception {
        try {
            testNoKeyOuterRecordVpack();
        } finally {
        }
    }


    void __invoke_testRecordWithJsonValue3063Vpack() throws Exception {
        try {
            testRecordWithJsonValue3063Vpack();
        } finally {
        }
    }


    void __invoke_testSimpleRecordAsKeyVpack() throws Exception {
        try {
            testSimpleRecordAsKeyVpack();
        } finally {
        }
    }

}
