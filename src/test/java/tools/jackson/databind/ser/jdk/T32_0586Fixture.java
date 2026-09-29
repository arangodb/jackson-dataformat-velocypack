package tools.jackson.databind.ser.jdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0586Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper STATIC_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.USE_STATIC_TYPING)
            .build();

    void testEnumMapVpack() throws Exception {
        EnumMap<Key, String> map = new EnumMap<>(Key.class);
        map.put(Key.B, "xyz");
        map.put(Key.C, "abc");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 02 41 42 43 78 79 7a 41 43 43 61 62 63 03 09"),
                MAPPER.writeValueAsBytes(map));
    }

    void testNullBeanCollectionVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0c 01 46 76 61 6c 75 65 73 18 03"),
                MAPPER.writeValueAsBytes(new CollectionBean(null)));
    }

    void testNullBeanEnumMapVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 09 01 43 6d 61 70 18 03"),
                MAPPER.writeValueAsBytes(new EnumMapBean(null)));
    }

    void testListSerializerVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "4c 5b 61 62 2c 20 63 64 2c 20 65 66 5d"),
                MAPPER.writeValueAsBytes(new PseudoList("ab", "cd", "ef")));
        assertArrayEquals(VPackWireFixtureTest.hex("42 5b 5d"),
                MAPPER.writeValueAsBytes(new PseudoList()));
    }

    void testEmptyListOrArrayVpack() throws Exception {
        EmptyListBean list = new EmptyListBean();
        EmptyArrayBean array = new EmptyArrayBean();
        assertTrue(MAPPER.isEnabled(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 65 6d 70 74 79 01 03"),
                MAPPER.writeValueAsBytes(list));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 65 6d 70 74 79 01 03"),
                MAPPER.writeValueAsBytes(array));

        ObjectMapper mapper = VPackMapper.builder()
                .configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false)
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(list));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(array));
    }

    void testStaticListVpack() throws Exception {
        StaticListWrapper wrapper = new StaticListWrapper("a", "b", "c");
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 44 6c 69 73 74 02 08 41 61 41 62 41 63 03"),
                MAPPER.writeValueAsBytes(wrapper));

        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(new AllowAllTypes(), DefaultTyping.NON_FINAL)
                .build();
        List<?> typed = MAPPER.readValue(mapper.writeValueAsBytes(wrapper), List.class);
        assertEquals(wrapper.getClass().getName(), typed.get(0));
        assertEquals(2, typed.size());
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> typedBody =
                (java.util.Map<String, Object>) typed.get(1);
        List<?> typedList = (List<?>) typedBody.get("list");
        assertEquals(ArrayList.class.getName(), typedList.get(0));
        assertEquals(List.of("a", "b", "c"), typedList.get(1));
    }

    void testIteratorVpack() throws Exception {
        ArrayList<Integer> values = new ArrayList<>(Arrays.asList(1, null, -9, 0));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 0c 04 31 18 20 f7 30 03 04 05 07"),
                MAPPER.writeValueAsBytes(values.iterator()));
        values.clear();
        assertArrayEquals(VPackWireFixtureTest.hex("01"),
                MAPPER.writeValueAsBytes(values.iterator()));
    }

    void testIterableVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                MAPPER.writeValueAsBytes(new IterableWrapper(new int[] { 1, 2, 3 })));
    }

    void testWithIterableVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 02 08 45 76 61 6c 75 65 03"),
                STATIC_MAPPER.writeValueAsBytes(new BeanWithIterable()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                STATIC_MAPPER.writeValueAsBytes(new IntIterable()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 02 08 45 76 61 6c 75 65 03"),
                MAPPER.writeValueAsBytes(new BeanWithIterable()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                MAPPER.writeValueAsBytes(new IntIterable()));

        ObjectMapper freshMapper = new VPackMapper();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 13 01 46 76 61 6c 75 65 73 02 08 45 76 61 6c 75 65 03"),
                freshMapper.writeValueAsBytes(new BeanWithIterable()));
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                freshMapper.writeValueAsBytes(new IntIterable()));
    }

    void testWithIteratorVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 46 76 61 6c 75 65 73 02 0a 47 69 74 56 61 6c 75 65 03"),
                STATIC_MAPPER.writeValueAsBytes(new BeanWithIterator()));

        ArrayList<Number> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(0.25);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "06 0f 02 31 1b 00 00 00 00 00 00 d0 3f 03 04"),
                MAPPER.writeValueAsBytes(numbers.iterator()));
    }

    void testIterable358Vpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1a 01 44 6c 69 73 74 02 11 02 0f 4c 48 65 6c 6c 6f 20 "
              + "77 6f 72 6c 64 2e 03"),
                MAPPER.writeValueAsBytes(new IterB()));
    }

    void testIterableWithAnnotationVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("02 05 31 32 33"),
                STATIC_MAPPER.writeValueAsBytes(new IntIterable2390()));
    }
static class CollectionBean {
        @JsonProperty
        public Collection<Object> values;

        CollectionBean(Collection<?> values) {
            @SuppressWarnings("unchecked")
            Collection<Object> cast = (Collection<Object>) values;
            this.values = cast;
        }
    }
enum Key { A, B, C }
static class EnumMapBean {
        EnumMap<Key, String> map;

        EnumMapBean(EnumMap<Key, String> map) {
            this.map = map;
        }

        public EnumMap<Key, String> getMap() {
            return map;
        }
    }
@SuppressWarnings("serial")
    @JsonSerialize(using = ListSerializer.class)
    static class PseudoList extends ArrayList<String> {
        PseudoList(String... values) {
            super(Arrays.asList(values));
        }
    }
static class ListSerializer extends ValueSerializer<List<String>> {
        @Override
        public void serialize(List<String> value, tools.jackson.core.JsonGenerator generator,
                SerializationContext context) {
            generator.writeString(value.toString());
        }
    }
static class EmptyListBean {
        public List<String> empty = new ArrayList<>();
    }
static class EmptyArrayBean {
        public String[] empty = new String[0];
    }
static class StaticListWrapper {
        protected List<String> list;

        StaticListWrapper(String... values) {
            list = new ArrayList<>(Arrays.asList(values));
        }

        public List<String> getList() {
            return list;
        }
    }
static final class IterableWrapper implements Iterable<Integer> {
        private final List<Integer> values = new ArrayList<>();

        IterableWrapper(int[] input) {
            for (int value : input) {
                values.add(value);
            }
        }

        @Override
        public Iterator<Integer> iterator() {
            return values.iterator();
        }
    }
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    static class BeanWithIterable {
        private final ArrayList<String> values = new ArrayList<>(List.of("value"));

        public Iterable<String> getValues() {
            return values;
        }
    }
static class BeanWithIterator {
        private final ArrayList<String> values = new ArrayList<>(List.of("itValue"));

        public Iterator<String> getValues() {
            return values.iterator();
        }
    }
static class IntIterable implements Iterable<Integer> {
        @Override
        public Iterator<Integer> iterator() {
            return new IntIterator(1, 3);
        }
    }
static class IntIterator implements Iterator<Integer> {
        private int value;
        private final int last;

        IntIterator(int first, int last) {
            value = first;
            this.last = last;
        }

        @Override
        public boolean hasNext() {
            return value <= last;
        }

        @Override
        public Integer next() {
            return value++;
        }
    }
static class IterA {
        public String unexpected = "Bye.";
    }
static class IterB {
        @JsonSerialize(as = Iterable.class, contentUsing = IterASerializer.class)
        public List<IterA> list = Arrays.asList(new IterA());
    }
static class IterASerializer extends ValueSerializer<IterA> {
        @Override
        public void serialize(IterA value, tools.jackson.core.JsonGenerator generator,
                SerializationContext context) {
            generator.writeStartArray();
            generator.writeString("Hello world.");
            generator.writeEndArray();
        }
    }
@JsonFilter("default")
    static class IntIterable2390 extends IntIterable { }
static final class AllowAllTypes extends PolymorphicTypeValidator.Base {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(tools.jackson.databind.DatabindContext context,
                tools.jackson.databind.JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    void __invoke_testEnumMapVpack() throws Exception {
        try {
            testEnumMapVpack();
        } finally {
        }
    }


    void __invoke_testNullBeanCollectionVpack() throws Exception {
        try {
            testNullBeanCollectionVpack();
        } finally {
        }
    }


    void __invoke_testNullBeanEnumMapVpack() throws Exception {
        try {
            testNullBeanEnumMapVpack();
        } finally {
        }
    }


    void __invoke_testListSerializerVpack() throws Exception {
        try {
            testListSerializerVpack();
        } finally {
        }
    }


    void __invoke_testEmptyListOrArrayVpack() throws Exception {
        try {
            testEmptyListOrArrayVpack();
        } finally {
        }
    }


    void __invoke_testStaticListVpack() throws Exception {
        try {
            testStaticListVpack();
        } finally {
        }
    }


    void __invoke_testIteratorVpack() throws Exception {
        try {
            testIteratorVpack();
        } finally {
        }
    }


    void __invoke_testIterableVpack() throws Exception {
        try {
            testIterableVpack();
        } finally {
        }
    }


    void __invoke_testWithIterableVpack() throws Exception {
        try {
            testWithIterableVpack();
        } finally {
        }
    }


    void __invoke_testWithIteratorVpack() throws Exception {
        try {
            testWithIteratorVpack();
        } finally {
        }
    }


    void __invoke_testIterable358Vpack() throws Exception {
        try {
            testIterable358Vpack();
        } finally {
        }
    }


    void __invoke_testIterableWithAnnotationVpack() throws Exception {
        try {
            testIterableWithAnnotationVpack();
        } finally {
        }
    }

}
