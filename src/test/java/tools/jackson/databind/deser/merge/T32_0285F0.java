package tools.jackson.databind.deser.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0285F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder()
            .disable(MapperFeature.IGNORE_MERGE_FOR_UNMERGEABLE)
            .build();
private static final byte[] ARRAY_TARGET = VPackWireFixtureTest.hex(
            "14 1e 45 61 72 72 61 79 13 0b 43 4d 72 2e 43 4d 73 2e 02 "
          + "46 6e 75 6d 62 65 72 29 78 03 02");
private static final byte[] ARRAY_UPDATE = VPackWireFixtureTest.hex(
            "14 18 45 61 72 72 61 79 13 0f 46 4d 69 73 74 65 72 44 4d 69 73 73 02 01");
private static final byte[] OBJECT_TARGET = VPackWireFixtureTest.hex(
            "14 2a 46 6f 62 6a 65 63 74 14 0b 41 61 41 31 41 62 41 32 02 "
          + "45 61 72 72 61 79 13 06 31 32 33 03 46 6e 75 6d 62 65 72 28 2a 03");
private static final byte[] OBJECT_UPDATE = VPackWireFixtureTest.hex(
            "14 13 46 6f 62 6a 65 63 74 14 09 41 62 43 78 79 7a 01 01");
private static final byte[] BAG_UPDATE = VPackWireFixtureTest.hex(
            "14 0c 43 62 61 67 13 05 41 62 01 01");
private static final byte[] VALUES_STRING_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 46 76 61 6c 75 65 73 13 05 41 78 01 01");
private static final byte[] VALUES_BAR_UPDATE = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 13 07 43 62 61 72 01 01");
private static final byte[] ABC_UPDATE = VPackWireFixtureTest.hex(
            "14 0c 43 61 62 63 13 05 41 41 01 01");
private static final byte[] VALUES_LONG_UPDATE = VPackWireFixtureTest.hex(
            "14 0f 46 76 61 6c 75 65 73 13 05 28 07 01 01");
private static final byte[] VALUES_CUSTOM_UPDATE = VALUES_STRING_UPDATE;
private static final byte[] NON_MERGE_INTERFACE = VALUES_STRING_UPDATE;
private static final byte[] EMPTY_ARRAY = VPackWireFixtureTest.hex("01");

    // Provenance: ArrayNode3338MergeTest#testEnabledArrayNodeMerge().
    void testEnabledArrayNodeMergeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        JsonNode mergeTarget = mapper.readTree(ARRAY_TARGET);
        JsonNode merged = mapper.readerForUpdating(mergeTarget).readValue(ARRAY_UPDATE);

        ObjectNode expected = mapper.createObjectNode();
        expected.put("number", 888);
        ArrayNode array = expected.putArray("array");
        array.add("Mr.");
        array.add("Ms.");
        array.add("Mister");
        array.add("Miss");

        assertEquals(expected, merged);
    }

    // Provenance: ArrayNode3338MergeTest#testDisabledArrayNodeMerge().
    void testDisabledArrayNodeMergeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(ArrayNode.class, cfg -> cfg.setMergeable(false))
                .build();
        JsonNode mergeTarget = mapper.readTree(ARRAY_TARGET);
        JsonNode merged = mapper.readerForUpdating(mergeTarget).readValue(ARRAY_UPDATE);

        ObjectNode expected = mapper.createObjectNode();
        ArrayNode array = expected.putArray("array");
        array.add("Mister");
        array.add("Miss");
        expected.put("number", 888);
        assertEquals(expected, merged);

        ObjectMapper jsonNodeMapper = VPackMapper.builder()
                .withConfigOverride(JsonNode.class, cfg -> cfg.setMergeable(false))
                .build();
        JsonNode merged2 = jsonNodeMapper.readerForUpdating(
                jsonNodeMapper.readTree(ARRAY_TARGET)).readValue(ARRAY_UPDATE);
        assertEquals(expected, merged2);
    }

    // Provenance: ArrayNode3338MergeTest#testEnabledObjectNodeMerge().
    void testEnabledObjectNodeMergeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().build();
        JsonNode merged = mapper.readerForUpdating(mapper.readTree(OBJECT_TARGET))
                .readValue(OBJECT_UPDATE);

        ObjectNode expected = mapper.createObjectNode();
        ObjectNode object = expected.putObject("object");
        object.put("a", "1");
        object.put("b", "xyz");
        expected.put("number", 42);
        ArrayNode array = expected.putArray("array");
        array.add(1);
        array.add(2);
        array.add(3);
        assertEquals(expected, merged);
    }

    // Provenance: ArrayNode3338MergeTest#testDisabledObjectNodeMerge().
    void testDisabledObjectNodeMergeVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(ObjectNode.class, cfg -> cfg.setMergeable(false))
                .build();
        JsonNode merged = mapper.readerForUpdating(mapper.readTree(OBJECT_TARGET))
                .readValue(OBJECT_UPDATE);

        ObjectNode expected = mapper.createObjectNode();
        expected.putObject("object").put("b", "xyz");
        expected.put("number", 42);
        ArrayNode array = expected.putArray("array");
        array.add(1);
        array.add(2);
        array.add(3);
        assertEquals(expected, merged);

        ObjectMapper jsonNodeMapper = VPackMapper.builder()
                .withConfigOverride(JsonNode.class, cfg -> cfg.setMergeable(false))
                .build();
        JsonNode merged2 = jsonNodeMapper.readerForUpdating(
                jsonNodeMapper.readTree(OBJECT_TARGET)).readValue(OBJECT_UPDATE);
        assertEquals(expected, merged2);
    }
static class CollectionWrapper {
        @JsonMerge
        public Collection<String> bag = new TreeSet<>();
        { bag.add("a"); }
    }
static class MergedList {
        @JsonMerge
        public List<String> values = new ArrayList<>();
        { values.add("a"); }
    }
static class MergedEnumSet {
        @JsonMerge
        public EnumSet<ABC> abc = EnumSet.of(ABC.B);
    }
static class MergedX<T> {
        @JsonMerge
        T value;

        MergedX(T value) { this.value = value; }
        protected MergedX() { }
    }
interface MyListCustom<T> extends List<T> { }
static class MyArrayListCustom<T> extends ArrayList<T> implements MyListCustom<T> { }
static abstract class MyAbstractStringList extends ArrayList<String> {
        MyAbstractStringList() { super(); }
        MyAbstractStringList(int size) { super(size); }
    }
static class MergeCustomStringList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<String> values = new MyArrayListCustom<>();
        { values.add("a"); }
    }
static class MergeMyCustomLongList {
        @JsonMerge
        @JsonProperty
        public MyListCustom<Long> values = new MyArrayListCustom<>();
        { values.add(1L); }
    }
static class NonMergeCustomStringList {
        public MyListCustom<String> values;
    }
private enum ABC { A, B }

    void __invoke_testEnabledArrayNodeMergeVpack() throws Exception {
        try {
            testEnabledArrayNodeMergeVpack();
        } finally {
        }
    }


    void __invoke_testDisabledArrayNodeMergeVpack() throws Exception {
        try {
            testDisabledArrayNodeMergeVpack();
        } finally {
        }
    }


    void __invoke_testEnabledObjectNodeMergeVpack() throws Exception {
        try {
            testEnabledObjectNodeMergeVpack();
        } finally {
        }
    }


    void __invoke_testDisabledObjectNodeMergeVpack() throws Exception {
        try {
            testDisabledObjectNodeMergeVpack();
        } finally {
        }
    }

}
