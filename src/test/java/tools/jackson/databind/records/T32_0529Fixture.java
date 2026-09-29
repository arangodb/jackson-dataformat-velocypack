package tools.jackson.databind.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidNullException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0529Fixture {
private static final ObjectMapper NULL_MAPPER = VPackMapper.builder()
            .changeDefaultNullHandling(n -> n.withValueNulls(Nulls.FAIL)
                    .withContentNulls(Nulls.FAIL))
            .build();
private static final ObjectMapper DEFAULT_MAPPER = VPackMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();
private static final byte[] POJO_VALID = VPackWireFixtureTest.hex(
            "14 13 49 66 69 65 6c 64 4e 61 6d 65 45 76 61 6c 75 65 01");
private static final byte[] POJO_NULL = VPackWireFixtureTest.hex(
            "14 0e 49 66 69 65 6c 64 4e 61 6d 65 18 01");
private static final byte[] FIXED_NULL = VPackWireFixtureTest.hex(
            "14 0f 4a 66 69 65 6c 64 5f 6e 61 6d 65 18 01");
private static final byte[] FOO_WITH_LIST = VPackWireFixtureTest.hex(
            "14 1d 44 6c 69 73 74 13 15 14 12 44 6e 61 6d 65 41 61 "
          + "45 76 61 6c 75 65 41 62 02 01 01");
private static final byte[] NON_NULL_DEFS = VPackWireFixtureTest.hex(
            "14 25 45 6e 61 6d 65 73 13 07 43 62 6f 62 01 "
          + "4b 61 67 65 73 42 79 4e 61 6d 65 73 14 09 43 62 6f 62 28 27 01 02");
private static final byte[] NAMES_ABSENT = VPackWireFixtureTest.hex(
            "14 18 4b 61 67 65 73 42 79 4e 61 6d 65 73 "
          + "14 09 43 62 6f 62 28 2a 01 01");
private static final byte[] AGES_ABSENT = VPackWireFixtureTest.hex(
            "14 10 45 6e 61 6d 65 73 13 07 43 62 6f 62 01 01");

    // Provenance: RecordNullHandlingTest#testPojoNullHandlingValid().
    void testPojoNullHandlingValidVpack() throws Exception {
        Pojo3847 value = NULL_MAPPER.readValue(POJO_VALID, Pojo3847.class);
        assertEquals("value", value.fieldName);
    }

    // Provenance: RecordNullHandlingTest#testPojoNullHandlingNullValue().
    void testPojoNullHandlingNullValueVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> NULL_MAPPER.readValue(POJO_NULL, Pojo3847.class));
        assertTrue(exception.getMessage().contains("fieldName"));
    }

    // Provenance: RecordNullHandlingTest#testPojoNullHandlingEmptyJson().
    void testPojoNullHandlingEmptyJsonVpack() throws Exception {
        assertNotNull(NULL_MAPPER.readValue(VPackWireFixtureTest.hex("0a"), Pojo3847.class));
    }

    // Provenance: RecordNullHandlingTest#testRecordDefaultNullDeserialization().
    void testRecordDefaultNullDeserializationVpack() throws Exception {
        PlainRecord value = DEFAULT_MAPPER.readValue(VPackWireFixtureTest.hex("0a"), PlainRecord.class);
        assertNull(value.fieldName());
    }

    // Provenance: RecordNullHandlingTest#testIntRecordDefaultNullDeserialization().
    void testIntRecordDefaultNullDeserializationVpack() throws Exception {
        IntRecord value = DEFAULT_MAPPER.readerFor(IntRecord.class)
                .readValue(VPackWireFixtureTest.hex("0a"));
        assertNull(value.description());
        assertEquals(0, value.value());
    }

    // Provenance: RecordNullHandlingTest#testRecordFixedNullHandlingEmptyJson().
    void testRecordFixedNullHandlingEmptyJsonVpack() {
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> NULL_MAPPER.readValue(VPackWireFixtureTest.hex("0a"), FixedRecord.class));
        assertTrue(exception.getMessage().contains("field_name"));
    }

    // Provenance: RecordNullHandlingTest#testEmptyObjectIntoRecordWithListOfRecord().
    void testEmptyObjectIntoRecordWithListOfRecordVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(h -> h.withContentNulls(Nulls.AS_EMPTY))
                .build();
        Foo value = mapper.readValue(VPackWireFixtureTest.hex("0a"), Foo.class);
        assertNull(value.list());
    }

    // Provenance: RecordNullHandlingTest#testNonEmptyListIntoRecordWithListOfRecord().
    void testNonEmptyListIntoRecordWithListOfRecordVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(h -> h.withContentNulls(Nulls.AS_EMPTY))
                .build();
        Foo value = mapper.readValue(FOO_WITH_LIST, Foo.class);
        assertNotNull(value.list());
        assertEquals(1, value.list().size());
        assertEquals("a", value.list().get(0).name());
        assertEquals("b", value.list().get(0).value());
    }

    // Provenance: RecordNullHandlingTest#testNonAbsentInclusionViaDefaultConfig5418().
    void testNonAbsentInclusionViaDefaultConfig5418Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT))
                .build();

        JsonNode oneNull = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418("test subject", null)));
        assertEquals(1, oneNull.size());
        assertEquals("test subject", oneNull.get("subject").textValue());
        assertNull(oneNull.get("body"));

        JsonNode bothNull = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418(null, null)));
        assertEquals(0, bothNull.size());

        JsonNode bothPresent = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418("test subject", "test body")));
        assertEquals(2, bothPresent.size());
        assertEquals("test subject", bothPresent.get("subject").textValue());
        assertEquals("test body", bothPresent.get("body").textValue());
    }

    // Provenance: RecordNullHandlingTest#testNonNullInclusionViaDefaultConfig5418().
    void testNonNullInclusionViaDefaultConfig5418Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> JsonInclude.Value.construct(
                        JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .build();

        JsonNode oneNull = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418("test subject", null)));
        assertEquals(1, oneNull.size());
        assertEquals("test subject", oneNull.get("subject").textValue());
        assertNull(oneNull.get("body"));

        JsonNode bothNull = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418(null, null)));
        assertEquals(0, bothNull.size());

        JsonNode bothPresent = mapper.readTree(mapper.writeValueAsBytes(
                new TestRecord5418("test subject", "test body")));
        assertEquals(2, bothPresent.size());
        assertEquals("test subject", bothPresent.get("subject").textValue());
        assertEquals("test body", bothPresent.get("body").textValue());
    }

    // Provenance: RecordNullHandlingTest#testDeserializeWithNullAsEmpty().
    void testDeserializeWithNullAsEmptyVpack() throws Exception {
        ObjectReader reader = DEFAULT_MAPPER.readerFor(RecordWithNonNullDefs2974.class);
        RecordWithNonNullDefs2974 regular = reader.readValue(NON_NULL_DEFS);
        assertEquals(1, regular.names().size());
        assertEquals("bob", regular.names().get(0));
        assertEquals(1, regular.agesByNames().size());
        assertEquals(Integer.valueOf(39), regular.agesByNames().get("bob"));

        RecordWithNonNullDefs2974 value = reader.readValue(NAMES_ABSENT);
        assertNotNull(value.names());
        assertEquals(0, value.names().size());
        assertNotNull(value.agesByNames());
        assertEquals(1, value.agesByNames().size());
        assertEquals(Integer.valueOf(42), value.agesByNames().get("bob"));
    }

    // Provenance: RecordNullHandlingTest#testDeserializeWithFailForNull().
    void testDeserializeWithFailForNullVpack() {
        ObjectReader reader = DEFAULT_MAPPER.readerFor(RecordWithNonNullDefs2974.class);
        InvalidNullException exception = assertThrows(InvalidNullException.class,
                () -> reader.readValue(AGES_ABSENT));
        assertTrue(exception.getMessage().contains("agesByNames"));
    }
static class Pojo3847 {
        public String fieldName;
    }
record PlainRecord(String fieldName) { }
record IntRecord(String description, int value) { }
record FixedRecord(@JsonProperty("field_name") String fieldName) { }
record Bar(String name, String value) { }
record Foo(java.util.List<Bar> list) { }
record TestRecord5418(String subject, String body) { }
record RecordWithNonNullDefs2974(
            @JsonSetter(nulls = Nulls.AS_EMPTY) java.util.List<String> names,
            @JsonSetter(nulls = Nulls.FAIL) Map<String, Integer> agesByNames) { }

    void __invoke_testPojoNullHandlingValidVpack() throws Exception {
        try {
            testPojoNullHandlingValidVpack();
        } finally {
        }
    }


    void __invoke_testPojoNullHandlingNullValueVpack() throws Exception {
        try {
            testPojoNullHandlingNullValueVpack();
        } finally {
        }
    }


    void __invoke_testPojoNullHandlingEmptyJsonVpack() throws Exception {
        try {
            testPojoNullHandlingEmptyJsonVpack();
        } finally {
        }
    }


    void __invoke_testRecordDefaultNullDeserializationVpack() throws Exception {
        try {
            testRecordDefaultNullDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testIntRecordDefaultNullDeserializationVpack() throws Exception {
        try {
            testIntRecordDefaultNullDeserializationVpack();
        } finally {
        }
    }


    void __invoke_testRecordFixedNullHandlingEmptyJsonVpack() throws Exception {
        try {
            testRecordFixedNullHandlingEmptyJsonVpack();
        } finally {
        }
    }


    void __invoke_testEmptyObjectIntoRecordWithListOfRecordVpack() throws Exception {
        try {
            testEmptyObjectIntoRecordWithListOfRecordVpack();
        } finally {
        }
    }


    void __invoke_testNonEmptyListIntoRecordWithListOfRecordVpack() throws Exception {
        try {
            testNonEmptyListIntoRecordWithListOfRecordVpack();
        } finally {
        }
    }


    void __invoke_testNonAbsentInclusionViaDefaultConfig5418Vpack() throws Exception {
        try {
            testNonAbsentInclusionViaDefaultConfig5418Vpack();
        } finally {
        }
    }


    void __invoke_testNonNullInclusionViaDefaultConfig5418Vpack() throws Exception {
        try {
            testNonNullInclusionViaDefaultConfig5418Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithNullAsEmptyVpack() throws Exception {
        try {
            testDeserializeWithNullAsEmptyVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeWithFailForNullVpack() throws Exception {
        try {
            testDeserializeWithFailForNullVpack();
        } finally {
        }
    }

}
