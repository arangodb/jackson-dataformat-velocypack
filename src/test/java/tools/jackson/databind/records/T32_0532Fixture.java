package tools.jackson.databind.records;

import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0532Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] ID_137 = VPackWireFixtureTest.hex(
            "14 08 42 69 64 28 89 01");
private static final byte[] ID_NAME_GARY = VPackWireFixtureTest.hex(
            "14 13 42 69 64 29 c8 01 44 6e 61 6d 65 44 47 61 72 79 02");
private static final byte[] NESTED_VALUE = VPackWireFixtureTest.hex(
            "14 1b 45 76 61 6c 75 65 14 12 42 69 64 28 c8 44 6e 61 6d 65 "
          + "44 47 61 72 79 02 01");
private static final byte[] ID_AND_UNKNOWN = VPackWireFixtureTest.hex(
            "14 16 42 69 64 28 89 47 75 6e 6b 6e 6f 77 6e 45 76 61 6c 75 65 02");
private static final byte[] UPPER_CASE_PROPERTIES = VPackWireFixtureTest.hex(
            "14 13 42 49 44 29 c8 01 44 4e 41 4d 45 44 47 61 72 79 02");

    // Provenance: RecordUpdate3079Test#testDirectRecordUpdate().
    void testDirectRecordUpdateVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.updateValue(orig,
                Collections.singletonMap("id", 137));
        assertNotNull(result);
        assertEquals(137, result.id());
        assertEquals("Bob", result.name());
        assertNotSame(orig, result);
    }

    // Provenance: RecordUpdate3079Test#testDirectRecordUpdateAllProperties().
    void testDirectRecordUpdateAllPropertiesVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.updateValue(orig,
                Collections.singletonMap("name", "Gary"));
        assertNotNull(result);
        assertNotSame(orig, result);
        assertEquals(123, result.id());
        assertEquals("Gary", result.name());
    }

    // Provenance: RecordUpdate3079Test#testDirectRecordUpdateNoProperties().
    void testDirectRecordUpdateNoPropertiesVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.updateValue(orig, Collections.emptyMap());
        assertNotNull(result);
        assertNotSame(orig, result);
        assertEquals(123, result.id());
        assertEquals("Bob", result.name());

        result = MAPPER.updateValue(orig, null);
        assertNotNull(result);
        assertSame(orig, result);
    }

    // Provenance: RecordUpdate3079Test#testRecordAsPropertyUpdate().
    void testRecordAsPropertyUpdateVpack() throws Exception {
        IdNameRecord origRecord = new IdNameRecord(123, "Bob");
        IdNameWrapper orig = new IdNameWrapper(origRecord);
        IdNameWrapper delta = new IdNameWrapper(new IdNameRecord(200, "Gary"));

        IdNameWrapper result = MAPPER.updateValue(orig, delta);
        assertEquals(200, result.value.id());
        assertEquals("Gary", result.value.name());
        assertSame(orig, result);
        assertNotSame(origRecord, result.value);
    }

    // Provenance: RecordUpdate3079Test#testIgnoreAllUnknown().
    void testIgnoreAllUnknownVpack() throws Exception {
        IgnoreAllRecord orig = new IgnoreAllRecord(1);
        IgnoreAllRecord updated = MAPPER.updateValue(orig,
                Collections.singletonMap("value", 123));
        assertNotNull(updated);
        assertNotSame(orig, updated);
        assertEquals(1, updated.id());
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordPartial().
    void testReaderForUpdatingRecordPartialVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.readerForUpdating(orig).readValue(ID_137);
        assertNotNull(result);
        assertEquals(137, result.id());
        assertEquals("Bob", result.name());
        assertNotSame(orig, result);
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordAllProps().
    void testReaderForUpdatingRecordAllPropsVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.readerForUpdating(orig).readValue(ID_NAME_GARY);
        assertNotNull(result);
        assertEquals(456, result.id());
        assertEquals("Gary", result.name());
        assertNotSame(orig, result);
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordEmpty().
    void testReaderForUpdatingRecordEmptyVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = MAPPER.readerForUpdating(orig).readValue(
                VPackWireFixtureTest.hex("0a"));
        assertNotNull(result);
        assertEquals(123, result.id());
        assertEquals("Bob", result.name());

        result = MAPPER.readerForUpdating(orig).readValue(
                VPackWireFixtureTest.hex("18"));
        assertNotNull(result);
        assertSame(orig, result);
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordOrigUnchanged().
    void testReaderForUpdatingRecordOrigUnchangedVpack() throws Exception {
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        MAPPER.readerForUpdating(orig).readValue(ID_NAME_GARY);
        assertEquals(123, orig.id());
        assertEquals("Bob", orig.name());
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordProperty().
    void testReaderForUpdatingRecordPropertyVpack() throws Exception {
        IdNameRecord origRecord = new IdNameRecord(123, "Bob");
        IdNameWrapper orig = new IdNameWrapper(origRecord);

        IdNameWrapper result = MAPPER.readerForUpdating(orig).readValue(NESTED_VALUE);
        assertEquals(200, result.value.id());
        assertEquals("Gary", result.value.name());
        assertSame(orig, result);
        assertNotSame(origRecord, result.value);
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordUnknownIgnored().
    void testReaderForUpdatingRecordUnknownIgnoredVpack() throws Exception {
        ObjectMapper lenientMapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        IdNameRecord orig = new IdNameRecord(123, "Bob");
        IdNameRecord result = lenientMapper.readerForUpdating(orig)
                .readValue(ID_AND_UNKNOWN);
        assertNotNull(result);
        assertEquals(137, result.id());
        assertEquals("Bob", result.name());
    }

    // Provenance: RecordUpdate3079Test#testReaderForUpdatingRecordWithAnySetter().
    void testReaderForUpdatingRecordWithAnySetterVpack() throws Exception {
        AnySetterRecord orig = new AnySetterRecord(123, "Bob",
                Map.of("ID", 999, "NAME", "Old"));
        AnySetterRecord result = MAPPER.readerForUpdating(orig)
                .readValue(UPPER_CASE_PROPERTIES);
        assertNotNull(result);
        assertEquals(123, result.id());
        assertEquals("Bob", result.name());
        assertEquals(456, result.extra().get("ID"));
        assertEquals("Gary", result.extra().get("NAME"));
    }
public record IdNameRecord(int id, String name) { }
@JsonIgnoreProperties(ignoreUnknown = true)
    public record IgnoreAllRecord(int id) { }
public record AnySetterRecord(int id, String name,
            @JsonAnySetter Map<String, Object> extra) { }
static class IdNameWrapper {
        public IdNameRecord value;

        protected IdNameWrapper() { }
        IdNameWrapper(IdNameRecord value) { this.value = value; }
    }

    void __invoke_testDirectRecordUpdateVpack() throws Exception {
        try {
            testDirectRecordUpdateVpack();
        } finally {
        }
    }


    void __invoke_testDirectRecordUpdateAllPropertiesVpack() throws Exception {
        try {
            testDirectRecordUpdateAllPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testDirectRecordUpdateNoPropertiesVpack() throws Exception {
        try {
            testDirectRecordUpdateNoPropertiesVpack();
        } finally {
        }
    }


    void __invoke_testRecordAsPropertyUpdateVpack() throws Exception {
        try {
            testRecordAsPropertyUpdateVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreAllUnknownVpack() throws Exception {
        try {
            testIgnoreAllUnknownVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordPartialVpack() throws Exception {
        try {
            testReaderForUpdatingRecordPartialVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordAllPropsVpack() throws Exception {
        try {
            testReaderForUpdatingRecordAllPropsVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordEmptyVpack() throws Exception {
        try {
            testReaderForUpdatingRecordEmptyVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordOrigUnchangedVpack() throws Exception {
        try {
            testReaderForUpdatingRecordOrigUnchangedVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordPropertyVpack() throws Exception {
        try {
            testReaderForUpdatingRecordPropertyVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordUnknownIgnoredVpack() throws Exception {
        try {
            testReaderForUpdatingRecordUnknownIgnoredVpack();
        } finally {
        }
    }


    void __invoke_testReaderForUpdatingRecordWithAnySetterVpack() throws Exception {
        try {
            testReaderForUpdatingRecordWithAnySetterVpack();
        } finally {
        }
    }

}
