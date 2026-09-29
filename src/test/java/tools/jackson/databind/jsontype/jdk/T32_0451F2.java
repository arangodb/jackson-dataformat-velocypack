package tools.jackson.databind.jsontype.jdk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0451F2 {
private static final ObjectMapper TYPED_MAPPER = VPackMapper.builder()
            .polymorphicTypeValidator(BasicPolymorphicTypeValidator.builder()
                    .allowIfBaseType(Object.class)
                    .allowIfBaseType(Serializable.class)
                    .allowIfSubType(T32_0451F2.class.getPackageName())
                    .build())
            .build();
private static final byte[] DATA_FIXTURE = VPackWireFixtureTest.hex(
            "14 6e 42 69 64 64 33 61 36 33 38 33 64 34 2d 38 31 32 33 2d 34 63 34 33 "
          + "2d 38 62 38 64 2d 37 63 65 64 66 33 65 35 39 34 30 34 45 69 74 65 6d 73 "
          + "13 3d 14 3a 42 69 64 64 38 31 63 33 64 39 37 38 2d 39 30 63 34 2d 34 62 "
          + "30 30 2d 38 64 61 31 2d 31 63 33 39 66 66 63 61 62 30 32 63 48 70 72 6f "
          + "70 65 72 74 79 45 76 61 6c 75 65 02 01 02");
private static final byte[] TYPED_INT_LIST = VPackWireFixtureTest.hex(
            "0b 4b 01 81 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 6a 64 6b 2e 54 33 32 5f 30 34 35 31" +
                "46 32 24 54 79 70 65 64 4c 69 73 74 41 73 57 72" +
                "61 70 70 65 72 02 05 34 35 36 03");
private static final byte[] TYPED_BOOLEAN_LIST = VPackWireFixtureTest.hex(
            "06 48 02 7e 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 6a 64 6b 2e 54 33 32 5f 30 34 35 31" +
                "46 32 24 54 79 70 65 64 4c 69 73 74 41 73 50 72" +
                "6f 70 02 04 1a 19 03 42");
private static final byte[] TYPED_LONG_LIST = VPackWireFixtureTest.hex(
            "0b 4a 01 81 74 6f 6f 6c 73 2e 6a 61 63 6b 73 6f" +
                "6e 2e 64 61 74 61 62 69 6e 64 2e 6a 73 6f 6e 74" +
                "79 70 65 2e 6a 64 6b 2e 54 33 32 5f 30 34 35 31" +
                "46 32 24 54 79 70 65 64 4c 69 73 74 41 73 57 72" +
                "61 70 70 65 72 02 04 31 33 03");
private static final byte[] TYPED_LONG_ARRAY = VPackWireFixtureTest.hex(
            "14 0b 42 5b 4a 02 05 35 36 37 01");

    // Provenance: TypedArrayDeserTest#testBooleanListAsProp().
    void testBooleanListAsPropVpack() throws Exception {
        TypedListAsProp<Boolean> result = new VPackMapper().readValue(
                TYPED_BOOLEAN_LIST,
                new VPackMapper().getTypeFactory().constructCollectionType(
                        TypedListAsProp.class, Boolean.class));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Boolean.TRUE, result.get(0));
        assertEquals(Boolean.FALSE, result.get(1));
    }

    // Provenance: TypedArrayDeserTest#testIntList().
    void testIntListVpack() throws Exception {
        TypedListAsWrapper<Integer> result = new VPackMapper().readValue(
                TYPED_INT_LIST,
                new VPackMapper().getTypeFactory().constructCollectionType(
                        TypedListAsWrapper.class, Integer.class));
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Integer.valueOf(4), result.get(0));
        assertEquals(Integer.valueOf(5), result.get(1));
        assertEquals(Integer.valueOf(6), result.get(2));
    }

    // Provenance: TypedArrayDeserTest#testLongArray().
    void testLongArrayVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .addMixIn(long[].class, WrapperMixIn.class)
                .build();
        long[] value = mapper.readValue(TYPED_LONG_ARRAY, long[].class);
        assertNotNull(value);
        assertEquals(3, value.length);
        assertEquals(5L, value[0]);
        assertEquals(6L, value[1]);
        assertEquals(7L, value[2]);
    }

    // Provenance: TypedArrayDeserTest#testLongListAsWrapper().
    void testLongListAsWrapperVpack() throws Exception {
        TypedListAsWrapper<Long> result = new VPackMapper().readValue(
                TYPED_LONG_LIST,
                new VPackMapper().getTypeFactory().constructCollectionType(
                        TypedListAsWrapper.class, Long.class));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(Long.class, result.get(0).getClass());
        assertEquals(Long.valueOf(1), result.get(0));
        assertEquals(Long.valueOf(3), result.get(1));
    }
private static void assertDynamicScalar(Object value) throws Exception {
        DynamicWrapper result = TYPED_MAPPER.readValue(
                TYPED_MAPPER.writeValueAsBytes(new DynamicWrapper(value)), DynamicWrapper.class);
        assertEquals(value, result.value);
        assertEquals(value.getClass(), result.value.getClass());
    }
static class DynamicWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
        public Object value;
        DynamicWrapper() { }
        DynamicWrapper(Object value) { this.value = value; }
    }
enum TestEnum { A, B }
static class Item implements HasUniqueId<String> {
        public String id;
        public String property;

        @Override
        public String getId() { return id; }
    }
static class Data {
        public String id;

        @JsonDeserialize(as = MyHashMap.class)
        public Map<String, Item> items;
    }
@SuppressWarnings("serial")
    static class MyHashMap<K, V extends HasUniqueId<K>> extends LinkedHashMap<K, V> {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        MyHashMap(V[] values) {
            for (V value : values) {
                put(value.getId(), value);
            }
        }
    }
interface HasUniqueId<K> {
        K getId();
    }
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static final class TestClass {
        @JsonProperty("mapProperty")
        @JsonSerialize(keyUsing = CompoundKeySerializer.class)
        @JsonDeserialize(keyUsing = CompoundKeyDeserializer.class)
        final Map<CompoundKey, String> mapProperty;

        @JsonCreator
        TestClass(@JsonProperty("mapProperty") Map<CompoundKey, String> mapProperty) {
            this.mapProperty = mapProperty;
        }
    }
static final class CompoundKey {
        private final String part0;
        private final String part1;

        CompoundKey(String part0, String part1) {
            this.part0 = part0;
            this.part1 = part1;
        }

        public String getPart0() { return part0; }
        public String getPart1() { return part1; }
    }
static final class CompoundKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String value, tools.jackson.databind.DeserializationContext ctxt) {
            String[] parts = value.split("\\|");
            return new CompoundKey(parts[0], parts[1]);
        }
    }
static final class CompoundKeySerializer extends StdSerializer<CompoundKey> {
        CompoundKeySerializer() { super(CompoundKey.class); }

        @Override
        public void serialize(CompoundKey value, tools.jackson.core.JsonGenerator generator,
                tools.jackson.databind.SerializationContext ctxt) {
            generator.writeName(value.getPart0() + '|' + value.getPart1());
        }
    }
@SuppressWarnings("serial")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    static class TypedListAsWrapper<T> extends java.util.LinkedList<T> { }
@SuppressWarnings("serial")
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    static class TypedListAsProp<T> extends ArrayList<T> { }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    interface WrapperMixIn { }

    void __invoke_testBooleanListAsPropVpack() throws Exception {
        try {
            testBooleanListAsPropVpack();
        } finally {
        }
    }


    void __invoke_testIntListVpack() throws Exception {
        try {
            testIntListVpack();
        } finally {
        }
    }


    void __invoke_testLongArrayVpack() throws Exception {
        try {
            testLongArrayVpack();
        } finally {
        }
    }


    void __invoke_testLongListAsWrapperVpack() throws Exception {
        try {
            testLongListAsWrapperVpack();
        } finally {
        }
    }

}
