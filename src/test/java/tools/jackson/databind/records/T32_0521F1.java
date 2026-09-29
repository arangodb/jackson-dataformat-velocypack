package tools.jackson.databind.records;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0521F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: RecordCreatorsTest#testCreatorSerializationWithJsonProperty4452Plain().
    void testCreatorSerializationWithJsonProperty4452PlainVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new PlainTestObject("test", 1)), Map.class);
        assertEquals(Map.of("strField", "test", "intField", 1), wire);
    }

    // Provenance: RecordCreatorsTest#testCreatorSerializationWithJsonProperty4452WithCreator().
    void testCreatorSerializationWithJsonProperty4452WithCreatorVpack() throws Exception {
        Map<?, ?> wire = MAPPER.readValue(MAPPER.writeValueAsBytes(
                new CreatorTestObject("test", 2, 1)), Map.class);
        assertEquals(Set.of("intField", "strField"), wire.keySet());
    }

    // Provenance: RecordCreatorsTest#testDeserialization5923False().
    void testDeserialization5923FalseVpack() throws Exception {
        Outer5923 value = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 0d 01 47 72 65 6e 61 6d 65 64 19 03"), Outer5923.class);
        assertEquals(false, value.bools().innerValue());
    }

    // Provenance: RecordCreatorsTest#testDeserialization5923True().
    void testDeserialization5923TrueVpack() throws Exception {
        Outer5923 value = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 0d 01 47 72 65 6e 61 6d 65 64 1a 03"), Outer5923.class);
        assertEquals(true, value.bools().innerValue());
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

    void __invoke_testCreatorSerializationWithJsonProperty4452PlainVpack() throws Exception {
        try {
            testCreatorSerializationWithJsonProperty4452PlainVpack();
        } finally {
        }
    }


    void __invoke_testCreatorSerializationWithJsonProperty4452WithCreatorVpack() throws Exception {
        try {
            testCreatorSerializationWithJsonProperty4452WithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testDeserialization5923FalseVpack() throws Exception {
        try {
            testDeserialization5923FalseVpack();
        } finally {
        }
    }


    void __invoke_testDeserialization5923TrueVpack() throws Exception {
        try {
            testDeserialization5923TrueVpack();
        } finally {
        }
    }

}
