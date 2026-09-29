package tools.jackson.databind.deser.jdk;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0281F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] EMPTY_STRING_LIST = VPackWireFixtureTest.hex(
            "14 0c 44 6c 69 73 74 13 04 40 01 01");
private static final byte[] EMPTY_STRING_MAP = VPackWireFixtureTest.hex(
            "14 0f 43 6d 61 70 14 08 43 6b 65 79 40 01 01");
private static final byte[] EMPTY_STRING_ARRAY = VPackWireFixtureTest.hex(
            "14 0d 45 61 72 72 61 79 13 04 40 01 01");
private static final byte[] EMPTY_STRING_ARRAY_VANILLA = VPackWireFixtureTest.hex("01");
private static final byte[] NULL_STRING_ARRAY_VANILLA = VPackWireFixtureTest.hex(
            "13 04 18 01");
private static final byte[] VALUE_AND_NULL_STRING_ARRAY_VANILLA = VPackWireFixtureTest.hex(
            "13 08 43 61 62 63 18 02");
private static final byte[] ARRAY_STORE_EXCEPTION = VPackWireFixtureTest.hex(
            "13 3c 53 6a 61 76 61 2e 75 74 69 6c 2e 41 72 72 61 79 4c 69 73 74 "
          + "13 25 13 22 5d 5b 4c 6a 61 76 61 2e 75 74 69 6c 2e 41 72 72 61 79 73 24 41 72 72 61 79 4c 69 73 74 3b 36 02 01 02");
private static final byte[] BASIC_STACK_TRACE = VPackWireFixtureTest.hex(
            "14 4f 49 63 6c 61 73 73 4e 61 6d 65 4f 63 6f 6d 2e 65 78 61 6d 70 6c 65 2e 46 6f 6f "
          + "4a 6d 65 74 68 6f 64 4e 61 6d 65 47 64 6f 53 74 75 66 66 48 66 69 6c 65 4e 61 6d 65 "
          + "48 46 6f 6f 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 28 2a 04");
private static final byte[] MIXIN_STACK_TRACE = VPackWireFixtureTest.hex(
            "14 3d 45 63 6c 61 73 73 4f 63 6f 6d 2e 65 78 61 6d 70 6c 65 2e 46 6f 6f "
          + "46 6d 65 74 68 6f 64 47 64 6f 53 74 75 66 66 44 66 69 6c 65 48 46 6f 6f 2e 6a 61 76 61 "
          + "44 6c 69 6e 65 28 2a 04");
private static final byte[] STRING_LINE_STACK_TRACE = VPackWireFixtureTest.hex(
            "14 3e 49 63 6c 61 73 73 4e 61 6d 65 43 43 6c 73 4a 6d 65 74 68 6f 64 4e 61 6d 65 41 6d "
          + "48 66 69 6c 65 4e 61 6d 65 48 43 6c 73 2e 6a 61 76 61 4a 6c 69 6e 65 4e 75 6d 62 65 72 "
          + "42 37 37 04");

    // Provenance: NullContentHandling5165Test#testNullsSkip_Collection().
    void testNullsSkip_CollectionVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                .build();
        CollectionDst dst = mapper.readValue(EMPTY_STRING_LIST,
                new TypeReference<CollectionDst>() { });
        assertTrue(dst.getList().isEmpty());
    }

    // Provenance: NullContentHandling5165Test#testNullsSkip_Map().
    void testNullsSkip_MapVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                .build();
        MapDst dst = mapper.readValue(EMPTY_STRING_MAP, new TypeReference<MapDst>() { });
        assertTrue(dst.getMap().isEmpty());
    }

    // Provenance: NullContentHandling5165Test#testNullsSkip_ObjectArray().
    void testNullsSkip_ObjectArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                .build();
        ObjectArrayDst dst = mapper.readValue(EMPTY_STRING_ARRAY, ObjectArrayDst.class);
        assertEquals(0, dst.array.length, "Null values should be skipped");
    }

    // Provenance: NullContentHandling5165Test#testNullsSkip_StringArray().
    void testNullsSkip_StringArrayVpack() throws Exception {
        ObjectMapper mapper = mapperWithEmptyStringAsNull(Nulls.SKIP);
        StringArrayDst dst = mapper.readValue(EMPTY_STRING_ARRAY, StringArrayDst.class);
        assertEquals(0, dst.array.length, "Null values should be skipped");
    }

    // Provenance: NullContentHandling5165Test#testNullsSkip_StringCollection().
    void testNullsSkip_StringCollectionVpack() throws Exception {
        ObjectMapper mapper = mapperWithEmptyStringAsNull(Nulls.SKIP);
        StringCollectionDst dst = mapper.readValue(EMPTY_STRING_LIST, StringCollectionDst.class);
        assertTrue(dst.list.isEmpty(), "Null values should be skipped");
    }

    // Provenance: NullContentHandling5165Test#testNullsVanilla_StringArray().
    void testNullsVanilla_StringArrayVpack() throws Exception {
        String[] arr = MAPPER.readValue(EMPTY_STRING_ARRAY_VANILLA, String[].class);
        assertEquals(0, arr.length);

        arr = MAPPER.readValue(NULL_STRING_ARRAY_VANILLA, String[].class);
        assertEquals(1, arr.length);
        assertNull(arr[0]);

        arr = MAPPER.readValue(VALUE_AND_NULL_STRING_ARRAY_VANILLA, String[].class);
        assertEquals(2, arr.length);
        assertEquals("abc", arr[0]);
        assertNull(arr[1]);
    }
private static ObjectMapper mapperWithEmptyStringAsNull(Nulls contentNulls) {
        SimpleModule module = new SimpleModule("T32-0281-empty-string-null")
                .addDeserializer(String.class, new EmptyStringToNullDeserializer());
        return VPackMapper.builder()
                .addModule(module)
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(contentNulls))
                .build();
    }
static class CollectionDst {
        private List<Integer> list;

        public List<Integer> getList() { return list; }
        public void setList(List<Integer> list) { this.list = list; }
    }
static class MapDst {
        private Map<String, Integer> map;

        public Map<String, Integer> getMap() { return map; }
        public void setMap(Map<String, Integer> map) { this.map = map; }
    }
static class ObjectArrayDst {
        public Integer[] array;
    }
static class StringArrayDst {
        public String[] array;
    }
static class StringCollectionDst {
        public List<String> list;
    }
static class EmptyStringToNullDeserializer extends StdDeserializer<String> {
        EmptyStringToNullDeserializer() { super(String.class); }

        @Override
        public String deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext context) {
            String value = parser.getValueAsString();
            return value != null && value.isEmpty() ? null : value;
        }
    }
abstract static class StackTraceElementMixIn {
        @JsonProperty("class")
        public abstract String getClassName();

        @JsonProperty("method")
        public abstract String getMethodName();

        @JsonProperty("file")
        public abstract String getFileName();

        @JsonProperty("line")
        public abstract int getLineNumber();
    }

    void __invoke_testNullsSkip_CollectionVpack() throws Exception {
        try {
            testNullsSkip_CollectionVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkip_MapVpack() throws Exception {
        try {
            testNullsSkip_MapVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkip_ObjectArrayVpack() throws Exception {
        try {
            testNullsSkip_ObjectArrayVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkip_StringArrayVpack() throws Exception {
        try {
            testNullsSkip_StringArrayVpack();
        } finally {
        }
    }


    void __invoke_testNullsSkip_StringCollectionVpack() throws Exception {
        try {
            testNullsSkip_StringCollectionVpack();
        } finally {
        }
    }


    void __invoke_testNullsVanilla_StringArrayVpack() throws Exception {
        try {
            testNullsVanilla_StringArrayVpack();
        } finally {
        }
    }

}
