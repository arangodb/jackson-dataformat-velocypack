package tools.jackson.databind.jsontype.deftyping;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0439F1 {
private static final byte[] ENUM_HOLDER = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 42 01");
private static final byte[] ENUM_ARRAY = VPackWireFixtureTest.hex(
            "13 05 41 42 01");

    // Provenance: TestDefaultForLists#testDateTypes().
    void testDateTypesVpack() throws Exception {
        ObjectListBean input = new ObjectListBean();
        input.values = new ArrayList<>();
        input.values.add(TimeZone.getTimeZone("EST"));
        input.values.add(Locale.CHINESE);

        ObjectMapper mapper = defaultTypingMapper();
        ObjectListBean output = mapper.readValue(
                mapper.writeValueAsBytes(input), ObjectListBean.class);
        assertEquals(2, output.values.size());
        assertInstanceOf(TimeZone.class, output.values.get(0));
        assertInstanceOf(Locale.class, output.values.get(1));
    }

    // Provenance: TestDefaultForLists#testJackson628().
    void testJackson628Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL).build();
        ArrayList<Foo> data = new ArrayList<>();
        List<?> output = mapper.readValue(mapper.writeValueAsBytes(data), List.class);
        assertEquals(0, output.size());
    }

    // Provenance: TestDefaultForLists#testJackson667().
    void testJackson667Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY).build();
        SetBean bean = mapper.readValue(
                mapper.writeValueAsBytes(new SetBean("abc")), SetBean.class);
        assertNotNull(bean);
        assertInstanceOf(HashSet.class, bean.names);
    }

    // Provenance: TestDefaultForLists#testListOfLongs().
    void testListOfLongsVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        ListOfLongs output = mapper.readValue(
                mapper.writeValueAsBytes(new ListOfLongs(1L, 2L, 3L)), ListOfLongs.class);
        assertNotNull(output.longs);
        assertEquals(List.of(1L, 2L, 3L), output.longs);
    }

    // Provenance: TestDefaultForLists#testListOfNumbers().
    void testListOfNumbersVpack() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        ListOfNumbers output = mapper.readValue(mapper.writeValueAsBytes(
                new ListOfNumbers(Long.valueOf(1L), Integer.valueOf(2), Double.valueOf(3.0))),
                ListOfNumbers.class);
        assertNotNull(output.nums);
        assertEquals(3, output.nums.size());
        assertEquals(Long.valueOf(1L), output.nums.get(0));
        assertEquals(Integer.valueOf(2), output.nums.get(1));
        assertEquals(Double.valueOf(3.0), output.nums.get(2));
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

    void __invoke_testDateTypesVpack() throws Exception {
        try {
            testDateTypesVpack();
        } finally {
        }
    }


    void __invoke_testJackson628Vpack() throws Exception {
        try {
            testJackson628Vpack();
        } finally {
        }
    }


    void __invoke_testJackson667Vpack() throws Exception {
        try {
            testJackson667Vpack();
        } finally {
        }
    }


    void __invoke_testListOfLongsVpack() throws Exception {
        try {
            testListOfLongsVpack();
        } finally {
        }
    }


    void __invoke_testListOfNumbersVpack() throws Exception {
        try {
            testListOfNumbersVpack();
        } finally {
        }
    }

}
