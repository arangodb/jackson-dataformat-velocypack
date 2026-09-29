package tools.jackson.databind.records;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0534F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] IDENTITY_REFERENCE_INPUT = VPackWireFixtureTest.hex(
            "14 47 47 64 65 76 69 63 65 73 13 0f 14 0c 42 69 64 45 41 72 72 69 73 01 01 "
          + "4a 61 63 74 69 76 69 74 69 65 73 13 22 14 1f 42 69 64 42 54 56 "
          + "4c 70 61 72 74 69 63 69 70 61 6e 74 73 13 09 45 41 72 72 69 73 01 02 01 02");
private static final byte[] IGNORED_NAME_INPUT = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] IGNORED_ACCESSOR_ID_INPUT = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 7b 01");
private static final byte[] IGNORED_ACCESSOR_NULL_INPUT = VPackWireFixtureTest.hex(
            "14 0e 42 69 64 28 7b 44 6e 61 6d 65 18 02");
private static final byte[] IGNORED_ACCESSOR_NAME_INPUT = IGNORED_NAME_INPUT;
private static final byte[] HIDDEN_ABSENT_INPUT = VPackWireFixtureTest.hex(
            "14 0e 44 74 65 78 74 45 68 65 6c 6c 6f 01");
private static final byte[] HIDDEN_NULL_INPUT = VPackWireFixtureTest.hex(
            "14 16 44 74 65 78 74 45 68 65 6c 6c 6f 46 68 69 64 64 65 6e 18 02");
private static final byte[] HIDDEN_OBJECT_INPUT = VPackWireFixtureTest.hex(
            "14 1f 44 74 65 78 74 45 68 65 6c 6c 6f 46 68 69 64 64 65 6e "
          + "14 0a 43 61 6c 6c 13 03 00 01 02");
private static final byte[] TEST_PROPERTY_INPUT = VPackWireFixtureTest.hex(
            "14 1c 4d 74 65 73 74 5f 70 72 6f 70 65 72 74 79 4a 74 65 73 74 20 76 61 6c 75 65 01");
private static final byte[] WRONG_PROPERTY_INPUT = VPackWireFixtureTest.hex(
            "14 14 45 76 61 6c 75 65 4a 74 65 73 74 20 76 61 6c 75 65 01");

    void testDeserializeIgnoresWrongPropertyName5184Vpack() throws Exception {
        TestData5184 value = MAPPER.readValue(WRONG_PROPERTY_INPUT, TestData5184.class);
        assertNull(value.value());
    }

    void testDeserializeJsonIgnoreAccessorRecordVpack() throws Exception {
        RecordWithIgnoreAccessor expected = new RecordWithIgnoreAccessor(123, null);

        assertEquals(expected, MAPPER.readValue(IGNORED_ACCESSOR_ID_INPUT, RecordWithIgnoreAccessor.class));
        assertEquals(expected, MAPPER.readValue(IGNORED_ACCESSOR_NULL_INPUT, RecordWithIgnoreAccessor.class));
        assertEquals(expected, MAPPER.readValue(IGNORED_ACCESSOR_NAME_INPUT, RecordWithIgnoreAccessor.class));
    }

    void testDeserializeJsonIgnoreAndJsonPropertyRecordVpack() throws Exception {
        RecordWithIgnoreJsonProperty value = MAPPER.readValue(IGNORED_NAME_INPUT,
                RecordWithIgnoreJsonProperty.class);
        assertEquals(new RecordWithIgnoreJsonProperty(123, null), value);
    }

    void testDeserializeJsonIgnorePrimitiveTypeRecordVpack() throws Exception {
        RecordWithIgnorePrimitiveType value = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build()
                .readValue(IGNORED_NAME_INPUT, RecordWithIgnorePrimitiveType.class);
        assertEquals(new RecordWithIgnorePrimitiveType(0, "Bob"), value);
    }

    void testDeserializeJsonIgnoreRecordVpack() throws Exception {
        RecordWithIgnore value = MAPPER.readValue(IGNORED_NAME_INPUT, RecordWithIgnore.class);
        assertEquals(new RecordWithIgnore(123, null), value);
    }

    void testDeserializeJsonIgnoreRecordWithDifferentNameVpack() throws Exception {
        RecordWithIgnoreJsonPropertyDifferentName value = MAPPER.readValue(IGNORED_NAME_INPUT,
                RecordWithIgnoreJsonPropertyDifferentName.class);
        assertEquals(new RecordWithIgnoreJsonPropertyDifferentName(123, null), value);
    }

    void testDeserializeJsonIgnoreWithOverride4626Vpack() throws Exception {
        HelloRecord expected = new HelloRecord("hello", null);

        assertEquals(expected, MAPPER.readValue(HIDDEN_ABSENT_INPUT, HelloRecord.class));
        assertEquals(expected, MAPPER.readValue(HIDDEN_NULL_INPUT, HelloRecord.class));
        assertEquals(expected, MAPPER.readValue(HIDDEN_OBJECT_INPUT, HelloRecord.class));
    }

    void testDeserializeWithIgnoredAlternateNonComponentGetter5184Vpack() throws Exception {
        TestData5184Alternate value = MAPPER.readValue(TEST_PROPERTY_INPUT,
                TestData5184Alternate.class);
        assertEquals("test value", value.value());
    }

    void testDeserializeWithIgnoredNonComponentGetter5184Vpack() throws Exception {
        TestData5184 value = MAPPER.readValue(TEST_PROPERTY_INPUT, TestData5184.class);
        assertEquals("test value", value.value());
    }

    void testDeserializeWithIgnoredNonComponentGetterOnClass5184Vpack() throws Exception {
        TestData5184Class value = MAPPER.readValue(TEST_PROPERTY_INPUT, TestData5184Class.class);
        assertEquals(Optional.of("test value"), value.getValue());
    }
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
    record Device(String id) { }
record Activity(String id,
            @JsonIdentityReference(alwaysAsId = true) List<Device> participants) { }
record Configuration(List<Device> devices, List<Activity> activities) { }
record RecordWithIgnore(int id, @JsonIgnore String name) { }
record RecordWithIgnoreJsonProperty(int id, @JsonIgnore @JsonProperty("name") String name) { }
record RecordWithIgnoreJsonPropertyDifferentName(int id,
            @JsonIgnore @JsonProperty("name2") String name) { }
record RecordWithIgnoreAccessor(int id, String name) {
        @JsonIgnore
        @Override
        public String name() {
            return name;
        }
    }
record RecordWithIgnorePrimitiveType(@JsonIgnore int id, String name) { }
public record HelloRecord(String text, @JsonIgnore Recursion hidden) {
        @Override
        public Recursion hidden() {
            return hidden;
        }
    }
static class Recursion {
        public List<Recursion> all = List.of();
    }
record TestData5184(@JsonProperty("test_property") String value) {
        @JsonIgnore
        public Optional<String> getValue() {
            return Optional.ofNullable(value);
        }
    }
record TestData5184Alternate(@JsonProperty("test_property") String value) {
        @JsonIgnore
        public Optional<String> optionalValue() {
            return Optional.ofNullable(value);
        }
    }
static final class TestData5184Class {
        private final String value;

        TestData5184Class(@JsonProperty("test_property") String value) {
            this.value = value;
        }

        @JsonIgnore
        public Optional<String> getValue() {
            return Optional.ofNullable(value);
        }
    }

    void __invoke_testDeserializeIgnoresWrongPropertyName5184Vpack() throws Exception {
        try {
            testDeserializeIgnoresWrongPropertyName5184Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnoreAccessorRecordVpack() throws Exception {
        try {
            testDeserializeJsonIgnoreAccessorRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnoreAndJsonPropertyRecordVpack() throws Exception {
        try {
            testDeserializeJsonIgnoreAndJsonPropertyRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnorePrimitiveTypeRecordVpack() throws Exception {
        try {
            testDeserializeJsonIgnorePrimitiveTypeRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnoreRecordVpack() throws Exception {
        try {
            testDeserializeJsonIgnoreRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnoreRecordWithDifferentNameVpack() throws Exception {
        try {
            testDeserializeJsonIgnoreRecordWithDifferentNameVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonIgnoreWithOverride4626Vpack() throws Exception {
        try {
            testDeserializeJsonIgnoreWithOverride4626Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithIgnoredAlternateNonComponentGetter5184Vpack() throws Exception {
        try {
            testDeserializeWithIgnoredAlternateNonComponentGetter5184Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithIgnoredNonComponentGetter5184Vpack() throws Exception {
        try {
            testDeserializeWithIgnoredNonComponentGetter5184Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithIgnoredNonComponentGetterOnClass5184Vpack() throws Exception {
        try {
            testDeserializeWithIgnoredNonComponentGetterOnClass5184Vpack();
        } finally {
        }
    }

}
