package tools.jackson.databind.deser.jdk;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;

import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0278F0 {
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

    // Provenance: MapDeserializerCachingTest#testCachedSerialize().
    void testCachedSerializeVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();

        NonAnnotatedMapHolderClass ignored = mapper.readValue(
                CACHED_MAP, NonAnnotatedMapHolderClass.class);
        assertTrue(ignored.data.containsKey("1st"));
        assertTrue(ignored.data.containsKey("2nd"));

        MapHolder result = mapper.readValue(CACHED_MAP, MapHolder.class);
        assertTrue(result.data.containsKey("1st (CUSTOM)"));
        assertTrue(result.data.containsKey("2nd (CUSTOM)"));
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

    void __invoke_testCachedSerializeVpack() throws Exception {
        try {
            testCachedSerializeVpack();
        } finally {
        }
    }

}
