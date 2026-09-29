package tools.jackson.databind.jsontype.deftyping;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.jsontype.TypeResolverBuilder;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0439F2 {
private static final byte[] ENUM_HOLDER = VPackWireFixtureTest.hex(
            "14 0b 45 76 61 6c 75 65 41 42 01");
private static final byte[] ENUM_ARRAY = VPackWireFixtureTest.hex(
            "13 05 41 42 01");

    // Provenance: TestDefaultForMaps#testJackson428().
    void testJackson428Vpack() throws Exception {
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(
                NoCheckSubTypeValidator.INSTANCE, DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        ObjectMapper serializer = VPackMapper.builder().setDefaultTyping(typer).build();

        MapHolder holder = new MapHolder();
        holder.map = new HashMap<>();
        List<Object> values = new ArrayList<>();
        values.add(Integer.valueOf(3));
        holder.map.put(new MapKey("key"), values);

        ObjectMapper deserializer = VPackMapper.builder().setDefaultTyping(
                new DefaultTypeResolverBuilder(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)).build();
        MapHolder result = deserializer.readValue(
                serializer.writeValueAsBytes(holder), MapHolder.class);
        assertNotNull(result);
        assertEquals(1, result.map.size());
        Map.Entry<?, ?> entry = result.map.entrySet().iterator().next();
        assertEquals(MapKey.class, entry.getKey().getClass());
        List<?> list = assertInstanceOf(List.class, entry.getValue());
        assertEquals(1, list.size());
        assertEquals(Integer.class, list.get(0).getClass());
        assertEquals(Integer.valueOf(3), list.get(0));
    }

    // Provenance: TestDefaultForMaps#testList().
    void testListVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY).build();
        ItemList child = new ItemList();
        child.value = "I am child";
        ItemList parent = new ItemList();
        parent.value = "I am parent";
        parent.addChildItem(child);

        ItemList result = mapper.readValue(mapper.writeValueAsBytes(parent), ItemList.class);
        assertNotNull(result);
        assertEquals("I am parent", result.value);
        assertEquals("I am child", result.childItems.get(0).value);
    }

    // Provenance: TestDefaultForMaps#testMap().
    void testMapVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(NoCheckSubTypeValidator.INSTANCE,
                        DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY).build();
        ItemMap child = new ItemMap();
        child.value = "I am child";
        ItemMap parent = new ItemMap();
        parent.value = "I am parent";
        parent.addChildItem("child", child);

        ItemMap result = mapper.readValue(mapper.writeValueAsBytes(parent), ItemMap.class);
        assertNotNull(result);
        assertEquals("I am parent", result.value);
        assertEquals("I am child", result.childItems.get("child").get(0).value);
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

    void __invoke_testJackson428Vpack() throws Exception {
        try {
            testJackson428Vpack();
        } finally {
        }
    }


    void __invoke_testListVpack() throws Exception {
        try {
            testListVpack();
        } finally {
        }
    }


    void __invoke_testMapVpack() throws Exception {
        try {
            testMapVpack();
        } finally {
        }
    }

}
