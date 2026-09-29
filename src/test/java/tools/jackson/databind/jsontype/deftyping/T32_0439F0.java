package tools.jackson.databind.jsontype.deftyping;

import java.util.*;

import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0439F0 {
private static final byte[] ENUM_HOLDER = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 42 01");
private static final byte[] ENUM_ARRAY = VPackWireFixtureTest.hex(
            "13 05 41 42 01");

    // Provenance: TestDefaultForEnums#testSimpleEnumsAsField().
    void testSimpleEnumsAsFieldVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        EnumHolder literal = plain.readValue(ENUM_HOLDER, EnumHolder.class);
        assertEquals("B", literal.value);

        ObjectMapper typed = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE).build();
        EnumHolder result = typed.readValue(
                typed.writeValueAsBytes(new EnumHolder(TestEnum.B)), EnumHolder.class);
        assertSame(TestEnum.B, result.value);
    }

    // Provenance: TestDefaultForEnums#testSimpleEnumsInObjectArray().
    void testSimpleEnumsInObjectArrayVpack() throws Exception {
        ObjectMapper plain = VPackMapper.builder().build();
        TestEnum[] literal = plain.readValue(ENUM_ARRAY, TestEnum[].class);
        assertEquals(1, literal.length);
        assertSame(TestEnum.B, literal[0]);

        ObjectMapper typed = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE).build();
        Object[] result = typed.readValue(
                typed.writeValueAsBytes(new Object[] { TestEnum.A }), Object[].class);
        assertEquals(1, result.length);
        assertSame(TestEnum.A, result[0]);
    }
private static ObjectMapper defaultTypingMapper() {
        return VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE).build();
    }
enum TestEnum { A, B }
static final class EnumHolder {
        public Object value;
        public EnumHolder() { }
        EnumHolder(TestEnum value) { this.value = value; }
    }
static class ObjectListBean {
        public List<Object> values;
    }
static class ListOfLongs {
        public List<Long> longs;
        public ListOfLongs() { }
        ListOfLongs(Long... values) { longs = new ArrayList<>(List.of(values)); }
    }
static class ListOfNumbers {
        public List<Number> nums;
        public ListOfNumbers() { }
        ListOfNumbers(Number... values) { nums = new ArrayList<>(List.of(values)); }
    }
interface Foo { }
static class SetBean {
        public Set<String> names;
        public SetBean() { }
        SetBean(String value) { names = new HashSet<>(); names.add(value); }
    }
static class MapKey {
        public String key;
        MapKey(String key) { this.key = key; }
        @Override public String toString() { return key; }
    }
static class MapKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return new MapKey(key);
        }
    }
static class MapHolder {
        @JsonDeserialize(keyAs = MapKey.class, keyUsing = MapKeyDeserializer.class)
        public Map<MapKey, List<Object>> map;
    }
static class ItemList {
        public String value;
        public List<ItemList> childItems = new LinkedList<>();
        public void addChildItem(ItemList item) { childItems.add(item); }
    }
static class ItemMap {
        public String value;
        public Map<String, List<ItemMap>> childItems = new HashMap<>();
        public void addChildItem(String key, ItemMap item) {
            childItems.computeIfAbsent(key, ignored -> new ArrayList<>()).add(item);
        }
    }
static final class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;
        static final NoCheckSubTypeValidator INSTANCE = new NoCheckSubTypeValidator();

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testSimpleEnumsAsFieldVpack() throws Exception {
        try {
            testSimpleEnumsAsFieldVpack();
        } finally {
        }
    }


    void __invoke_testSimpleEnumsInObjectArrayVpack() throws Exception {
        try {
            testSimpleEnumsInObjectArrayVpack();
        } finally {
        }
    }

}
