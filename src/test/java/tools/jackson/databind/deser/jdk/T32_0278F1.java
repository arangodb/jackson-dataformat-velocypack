package tools.jackson.databind.deser.jdk;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0278F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CACHED_MAP = VPackWireFixtureTest.hex(
            "14 23 44 64 61 74 61 14 1b 43 31 73 74 47 6f 6e 65 64 61 74 61 "
          + "43 32 6e 64 47 74 77 6f 64 61 74 61 02 01");
private static final byte[] INTEGER_KEY_ENTRY = VPackWireFixtureTest.hex(
            "14 0d 43 31 32 33 45 68 65 6c 6c 6f 01");
private static final byte[] NULL_VALUE_ENTRY = VPackWireFixtureTest.hex(
            "14 08 43 6b 65 79 18 01");
private static final byte[] ENTRY_IN_HOLDER = VPackWireFixtureTest.hex(
            "14 12 45 65 6e 74 72 79 14 09 43 61 62 63 28 63 01 01");
private static final byte[] ENTRY_WITH_BEAN_VALUE = VPackWireFixtureTest.hex(
            "14 16 43 6b 65 79 14 0f 43 73 74 72 47 77 72 61 70 70 65 64 01 01");
private static final byte[] ENTRY_WITH_LIST_VALUE = VPackWireFixtureTest.hex(
            "14 0e 44 6e 75 6d 73 13 06 31 32 33 03 01");
private static final byte[] LIST_OF_ENTRIES = VPackWireFixtureTest.hex(
            "13 15 14 06 41 61 31 01 14 06 41 62 32 01 14 06 41 63 33 01 03");
private static final byte[] MULTIPLE_ENTRIES = VPackWireFixtureTest.hex(
            "14 09 41 61 31 41 62 32 02");
private static final byte[] POJO_WRAPPED = VPackWireFixtureTest.hex(
            "14 1e 45 65 6e 74 72 79 14 15 43 6b 65 79 45 6d 79 4b 65 79 "
          + "45 76 61 6c 75 65 28 37 02 01");
private static final byte[] POJO_WRAPPED_NULL_KEY = VPackWireFixtureTest.hex(
            "14 19 45 65 6e 74 72 79 14 10 43 6b 65 79 18 45 76 61 6c 75 65 "
          + "28 0a 02 01");
private static final byte[] POJO_WRAPPED_NULL_VALUE = VPackWireFixtureTest.hex(
            "14 19 45 65 6e 74 72 79 14 10 43 6b 65 79 41 6b 45 76 61 6c 75 65 "
          + "18 02 01");

    // Provenance: MapEntryDeserializationTest#testEmptyObjectFails().
    void testEmptyObjectFailsVpack() throws Exception {
        try {
            MAPPER.readValue(VPackWireFixtureTest.hex("0a"),
                    new TypeReference<Map.Entry<String, Integer>>() { });
            fail("Should not pass with empty object");
        } catch (MismatchedInputException e) {
            assertTrue(e.getMessage().contains("Cannot deserialize"));
        }
    }

    // Provenance: MapEntryDeserializationTest#testEntryInHolder().
    void testEntryInHolderVpack() throws Exception {
        EntryHolder result = MAPPER.readValue(ENTRY_IN_HOLDER, EntryHolder.class);
        assertNotNull(result);
        assertNotNull(result.entry);
        assertEquals("abc", result.entry.getKey());
        assertEquals(Integer.valueOf(99), result.entry.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testEntryWithBeanValue().
    void testEntryWithBeanValueVpack() throws Exception {
        Map.Entry<String, StringWrapper> result = MAPPER.readValue(
                ENTRY_WITH_BEAN_VALUE,
                new TypeReference<Map.Entry<String, StringWrapper>>() { });
        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertNotNull(result.getValue());
        assertEquals("wrapped", result.getValue().str);
    }

    // Provenance: MapEntryDeserializationTest#testEntryWithListValue().
    void testEntryWithListValueVpack() throws Exception {
        Map.Entry<String, List<Integer>> result = MAPPER.readValue(
                ENTRY_WITH_LIST_VALUE,
                new TypeReference<Map.Entry<String, List<Integer>>>() { });
        assertNotNull(result);
        assertEquals("nums", result.getKey());
        assertEquals(Arrays.asList(1, 2, 3), result.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testIntegerKeyEntry().
    void testIntegerKeyEntryVpack() throws Exception {
        Map.Entry<Integer, String> result = MAPPER.readValue(INTEGER_KEY_ENTRY,
                new TypeReference<Map.Entry<Integer, String>>() { });
        assertNotNull(result);
        assertEquals(Integer.valueOf(123), result.getKey());
        assertEquals("hello", result.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testListOfEntries().
    void testListOfEntriesVpack() throws Exception {
        List<Map.Entry<String, Integer>> result = MAPPER.readValue(LIST_OF_ENTRIES,
                new TypeReference<List<Map.Entry<String, Integer>>>() { });
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("a", result.get(0).getKey());
        assertEquals(Integer.valueOf(1), result.get(0).getValue());
        assertEquals("c", result.get(2).getKey());
        assertEquals(Integer.valueOf(3), result.get(2).getValue());
    }

    // Provenance: MapEntryDeserializationTest#testMultipleEntriesFails().
    void testMultipleEntriesFailsVpack() throws Exception {
        try {
            MAPPER.readValue(MULTIPLE_ENTRIES,
                    new TypeReference<Map.Entry<String, Integer>>() { });
            fail("Should not pass with multiple entries");
        } catch (MismatchedInputException e) {
            assertTrue(e.getMessage().contains("more than one entry"));
        }
    }

    // Provenance: MapEntryDeserializationTest#testNullValueEntry().
    void testNullValueEntryVpack() throws Exception {
        Map.Entry<String, String> result = MAPPER.readValue(NULL_VALUE_ENTRY,
                new TypeReference<Map.Entry<String, String>>() { });
        assertNotNull(result);
        assertEquals("key", result.getKey());
        assertNull(result.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testPojoWrappedFormat().
    void testPojoWrappedFormatVpack() throws Exception {
        PojoFormatEntryHolder result = MAPPER.readValue(POJO_WRAPPED,
                PojoFormatEntryHolder.class);
        assertNotNull(result);
        assertNotNull(result.entry);
        assertEquals("myKey", result.entry.getKey());
        assertEquals(Integer.valueOf(55), result.entry.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testPojoWrappedFormatNullKey().
    void testPojoWrappedFormatNullKeyVpack() throws Exception {
        PojoFormatEntryHolder result = MAPPER.readValue(POJO_WRAPPED_NULL_KEY,
                PojoFormatEntryHolder.class);
        assertNotNull(result);
        assertNotNull(result.entry);
        assertNull(result.entry.getKey());
        assertEquals(Integer.valueOf(10), result.entry.getValue());
    }

    // Provenance: MapEntryDeserializationTest#testPojoWrappedFormatNullValue().
    void testPojoWrappedFormatNullValueVpack() throws Exception {
        PojoFormatEntryHolder result = MAPPER.readValue(POJO_WRAPPED_NULL_VALUE,
                PojoFormatEntryHolder.class);
        assertNotNull(result);
        assertNotNull(result.entry);
        assertEquals("k", result.entry.getKey());
        assertNull(result.entry.getValue());
    }
static class NonAnnotatedMapHolderClass {
        public Map<String, String> data = new TreeMap<>();
    }
static class MapHolder {
        @JsonDeserialize(keyUsing = MyKeyDeserializer.class)
        public Map<String, String> data = new TreeMap<>();
    }
static class MyKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, tools.jackson.databind.DeserializationContext ctxt) {
            return key + " (CUSTOM)";
        }
    }
static class EntryHolder {
        public Map.Entry<String, Integer> entry;
    }
static class PojoFormatEntryHolder {
        @JsonFormat(shape = JsonFormat.Shape.POJO)
        public Map.Entry<String, Integer> entry;
    }
static class StringWrapper {
        public String str;
    }

    void __invoke_testEmptyObjectFailsVpack() throws Exception {
        try {
            testEmptyObjectFailsVpack();
        } finally {
        }
    }


    void __invoke_testEntryInHolderVpack() throws Exception {
        try {
            testEntryInHolderVpack();
        } finally {
        }
    }


    void __invoke_testEntryWithBeanValueVpack() throws Exception {
        try {
            testEntryWithBeanValueVpack();
        } finally {
        }
    }


    void __invoke_testEntryWithListValueVpack() throws Exception {
        try {
            testEntryWithListValueVpack();
        } finally {
        }
    }


    void __invoke_testIntegerKeyEntryVpack() throws Exception {
        try {
            testIntegerKeyEntryVpack();
        } finally {
        }
    }


    void __invoke_testListOfEntriesVpack() throws Exception {
        try {
            testListOfEntriesVpack();
        } finally {
        }
    }


    void __invoke_testMultipleEntriesFailsVpack() throws Exception {
        try {
            testMultipleEntriesFailsVpack();
        } finally {
        }
    }


    void __invoke_testNullValueEntryVpack() throws Exception {
        try {
            testNullValueEntryVpack();
        } finally {
        }
    }


    void __invoke_testPojoWrappedFormatVpack() throws Exception {
        try {
            testPojoWrappedFormatVpack();
        } finally {
        }
    }


    void __invoke_testPojoWrappedFormatNullKeyVpack() throws Exception {
        try {
            testPojoWrappedFormatNullKeyVpack();
        } finally {
        }
    }


    void __invoke_testPojoWrappedFormatNullValueVpack() throws Exception {
        try {
            testPojoWrappedFormatNullValueVpack();
        } finally {
        }
    }

}
