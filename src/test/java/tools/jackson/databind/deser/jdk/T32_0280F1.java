package tools.jackson.databind.deser.jdk;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0280F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SHORT_KEY_MAP = VPackWireFixtureTest.hex(
            "14 14 43 6d 61 70 14 0d 42 31 33 46 66 6f 6f 62 61 72 01 01");
private static final byte[] LONG_KEY_MAP = VPackWireFixtureTest.hex(
            "14 14 43 6d 61 70 14 0d 42 34 32 46 66 6f 6f 62 61 72 01 01");
private static final byte[] CTOR_KEY_MAP = VPackWireFixtureTest.hex(
            "14 08 43 62 61 72 33 01");
private static final byte[] FACTORY_KEY_MAP = VPackWireFixtureTest.hex(
            "14 08 43 46 6f 6f 33 01");
private static final byte[] NORMALIZE_KEY_MAP = VPackWireFixtureTest.hex(
            "14 08 43 46 4f 4f 30 01");
private static final byte[] EMPTY_STRING_MAP = VPackWireFixtureTest.hex(
            "14 0f 43 6d 61 70 14 08 43 6b 65 79 40 01 01");
private static final byte[] EMPTY_STRING_LIST = VPackWireFixtureTest.hex(
            "14 0c 44 6c 69 73 74 13 04 40 01 01");
private static final byte[] EMPTY_STRING_ARRAY = VPackWireFixtureTest.hex(
            "14 0d 45 61 72 72 61 79 13 04 40 01 01");
private static final byte[] RAW_GENERIC_MAP = VPackWireFixtureTest.hex(
            "14 14 43 6d 61 70 14 0d 43 6b 65 79 45 76 61 6c 75 65 01 01");

    // Provenance: MapRawWithGeneric2846Test#testIssue2821Part2().
    void testIssue2821Part2Vpack() throws Exception {
        GenericEntity<SimpleEntity> entity = MAPPER.readValue(RAW_GENERIC_MAP,
                new TypeReference<GenericEntity<SimpleEntity>>() { });
        assertNotNull(entity);
    }
private static ObjectMapper mapperWithEmptyStringAsNull(Nulls contentNulls) {
        SimpleModule module = new SimpleModule("T32-0280-empty-string-null")
                .addDeserializer(String.class, new EmptyStringToNullDeserializer());
        return VPackMapper.builder()
                .addModule(module)
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(contentNulls))
                .build();
    }
private static final TypeReference<Map<DummyDto2158, Integer>> MAP_TYPE_2158 =
            new TypeReference<Map<DummyDto2158, Integer>>() { };
static class MapWrapper<K, V> {
        public Map<K, V> map;
    }
static class Key3143Factories {
        protected String value;

        private Key3143Factories(String value, boolean ignored) {
            this.value = value;
        }

        public static Key3143Factories cantUse() {
            throw new RuntimeException("Invalid factory");
        }

        @JsonCreator
        public static Key3143Factories create(String value) {
            return new Key3143Factories(value.toLowerCase(), true);
        }

        public static Key3143Factories valueOf(String value) {
            return new Key3143Factories(value.toUpperCase(), false);
        }
    }
static class Key3143FactoriesFail {
        @JsonCreator
        public static Key3143FactoriesFail create(String value) {
            throw new Error("Can't use");
        }

        @JsonCreator
        public static Key3143FactoriesFail valueOf(String value) {
            throw new Error("Can't use");
        }
    }
static class Key3143Ctor {
        protected String value;

        public static Key3143Ctor valueOf(String value) {
            return new Key3143Ctor(value.toUpperCase());
        }

        @JsonCreator
        private Key3143Ctor(String value) {
            this.value = value;
        }
    }
private static final class DummyDto2158 {
        private final String value;

        private DummyDto2158(String value) {
            this.value = value;
        }

        @JsonCreator
        static DummyDto2158 fromValue(String value) {
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Value must be nonempty");
            }
            return new DummyDto2158(value.toLowerCase(java.util.Locale.ROOT));
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof DummyDto2158 dto && dto.value.equals(value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
static class GenericEntity<T> {
        @SuppressWarnings("rawtypes")
        public Map map;
    }
static class SimpleEntity {
        public Integer number;
    }
static class CollectionDst {
        private List<Integer> list;

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }
    }
static class MapDst {
        private Map<String, Integer> map;

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }
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
        EmptyStringToNullDeserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) {
            String value = parser.getValueAsString();
            return value != null && value.isEmpty() ? null : value;
        }
    }

    void __invoke_testIssue2821Part2Vpack() throws Exception {
        try {
            testIssue2821Part2Vpack();
        } finally {
        }
    }

}
