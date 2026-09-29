package tools.jackson.databind.ser;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0551F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] WILDCARD_RESULT = VPackWireFixtureTest.hex(
            "0b 3a 02 45 66 69 72 73 74 "
          + "0b 14 02 42 69 64 31 44 6e 61 6d 65 45 6e 61 6d 65 31 03 07 "
          + "46 73 65 63 6f 6e 64 "
          + "0b 14 02 42 69 64 32 44 6e 61 6d 65 45 6e 61 6d 65 32 03 07 "
          + "03 1d");
private static final byte[] TYPED_OBJECT = VPackWireFixtureTest.hex(
            "0b 0c 02 41 61 41 61 41 62 33 03 07");
private static final byte[] INTERFACE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 62 33 03");
private static final byte[] TYPED_INTERFACE_OBJECT = VPackWireFixtureTest.hex(
            "0b 07 01 41 61 33 03");
private static final byte[] TYPED_ARRAY = VPackWireFixtureTest.hex(
            "02 09 0b 07 01 41 61 33 03");
private static final byte[] TYPED_MAP = VPackWireFixtureTest.hex(
            "0b 0d 01 41 61 0b 07 01 41 61 33 03 03");
private static final byte[] UNBOUND = VPackWireFixtureTest.hex(
            "0b 17 01 47 77 72 61 70 70 65 64 "
          + "0b 0b 01 45 76 61 6c 75 65 37 03 03");
private static final byte[] CONTENT_SERIALIZER = VPackWireFixtureTest.hex(
            "0b 0f 01 44 6c 69 73 74 02 06 43 62 61 72 03");
private static final byte[] EMPTY_LIST_PROPERTY = VPackWireFixtureTest.hex(
            "0b 0a 01 44 6c 69 73 74 01 03");

    void testBrokenAnnotationVpack() {
        InvalidDefinitionException failure = assertThrows(InvalidDefinitionException.class,
                () -> MAPPER.writeValueAsBytes(new BrokenClass()));
        assertEquals(true, failure.getMessage().contains("types not related"));
    }

    void testCustomContentSerializerVpack() throws Exception {
        MyObject object = new MyObject();
        object.list = List.of("foo");
        assertBytes(CONTENT_SERIALIZER, MAPPER.writeValueAsBytes(object));
    }

    void testEmptyInclusionContainersVpack() throws Exception {
        ObjectMapper included = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_EMPTY))
                .build();
        ListWrapper<String> list = new ListWrapper<>(List.of());
        assertArrayEquals(EMPTY_LIST_PROPERTY, MAPPER.writeValueAsBytes(list));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"), included.writeValueAsBytes(list));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                included.writeValueAsBytes(new ListWrapper<String>(null)));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                included.writeValueAsBytes(new MapWrapper<String, Integer>(Map.of())));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                included.writeValueAsBytes(new ArrayWrapper<Integer>(new Integer[0])));
    }
private static void assertBytes(byte[] expected, byte[] actual) {
        assertArrayEquals(expected, actual, "actual=" + hex(actual));
    }
private static String hex(byte[] value) {
        StringBuilder result = new StringBuilder();
        for (byte item : value) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(String.format("%02x", item & 0xff));
        }
        return result.toString();
    }
interface BaseInterface { int getB(); }
static class BaseType implements BaseInterface {
        public String a = "a";
        @Override public int getB() { return 3; }
    }
static class SubType extends BaseType {
        public String a2 = "x";
        public boolean getB2() { return true; }
    }
static interface Issue822Interface { int getA(); }
static class Issue822Impl implements Issue822Interface {
        @Override public int getA() { return 3; }
        public int getB() { return 9; }
    }
static class Account {
        private final Long id;
        private final String name;

        @JsonCreator
        Account(@JsonProperty("name") String name, @JsonProperty("id") Long id) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }

        @Override public boolean equals(Object value) {
            return value instanceof Account other && Objects.equals(id, other.id)
                    && Objects.equals(name, other.name);
        }

        @Override public int hashCode() { return Objects.hash(id, name); }
    }
static final class WildcardWrapperImpl<G, GG> {
        private final G first;
        private final GG second;

        WildcardWrapperImpl(G first, GG second) { this.first = first; this.second = second; }
        public G first() { return first; }
        public GG second() { return second; }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static <H, HH> WildcardWrapperImpl<H, ? extends HH>
        fromJson(JsonGenericWrapper<H, HH> value) {
            return new WildcardWrapperImpl<>(value.first(), value.second());
        }
    }
@JsonDeserialize
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
    static final class JsonGenericWrapper<D, DD> {
        @JsonProperty("first") private D first;
        @JsonProperty("second") private DD second;
        @JsonProperty("first") public D first() { return first; }
        @JsonProperty("second") public DD second() { return second; }
    }
static class GenericBogusWrapper<T> {
        public Element wrapped;
        GenericBogusWrapper(T value) { wrapped = new Element(value); }
        class Element { public T value; Element(T value) { this.value = value; } }
    }
static final class Wrapper2821 {
        final List<Entity2821<?>> entities;
        @JsonCreator Wrapper2821(List<Entity2821<?>> entities) { this.entities = entities; }
        public List<Entity2821<?>> getEntities() { return entities; }
    }
static class Entity2821<T> {
        @JsonIgnore final Attributes2821 attributes;
        final T data;
        Entity2821(Attributes2821 attributes, T data) { this.attributes = attributes; this.data = data; }
        @JsonUnwrapped public Attributes2821 getAttributes() { return attributes; }
        public T getData() { return data; }
        @JsonCreator static <T> Entity2821<T> create(
                @JsonProperty("attributes") Attributes2821 attributes, @JsonProperty("data") T data) {
            return new Entity2821<>(attributes, data);
        }
    }
static class Attributes2821 {
        public final String id;
        Attributes2821(String id) { this.id = id; }
        @JsonCreator static Attributes2821 create(@JsonProperty("id") String id) {
            return new Attributes2821(id);
        }
        @SuppressWarnings("rawtypes") public static Attributes2821 dummyMethod(Map attributes) {
            return null;
        }
    }
static class BrokenClass {
        @JsonSerialize(as = String.class)
        public Long getValue() { return 4L; }
    }
static class FooToBarSerializer extends tools.jackson.databind.ValueSerializer<String> {
        @Override public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeString("foo".equals(value) ? "bar" : value);
        }
    }
static class MyObject {
        @JsonSerialize(contentUsing = FooToBarSerializer.class)
        List<String> list;
    }
static class ListWrapper<T> {
        public final List<T> list;
        ListWrapper(List<T> list) { this.list = list; }
    }
static class MapWrapper<K, V> {
        public final Map<K, V> map;
        MapWrapper(Map<K, V> map) { this.map = map; }
    }
static class ArrayWrapper<T> {
        public final T[] array;
        ArrayWrapper(T[] array) { this.array = array; }
    }

    void __invoke_testBrokenAnnotationVpack() throws Exception {
        try {
            testBrokenAnnotationVpack();
        } finally {
        }
    }


    void __invoke_testCustomContentSerializerVpack() throws Exception {
        try {
            testCustomContentSerializerVpack();
        } finally {
        }
    }


    void __invoke_testEmptyInclusionContainersVpack() throws Exception {
        try {
            testEmptyInclusionContainersVpack();
        } finally {
        }
    }

}
