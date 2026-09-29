package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSerializeAs;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0553Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectWriter WRITER = MAPPER.writer();
private static final byte[] SIMPLE_VALUE_DEFINITION = VPackWireFixtureTest.hex(
            "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 33 03 03");
private static final byte[] LIST_CLASS_ANNOTATIONS = VPackWireFixtureTest.hex(
            "02 10 0b 0e 01 45 76 61 6c 75 65 43 66 6f 6f 03");

    void testSimpleValueDefinitionVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new WrapperClassForAs());
        assertArrayEquals(SIMPLE_VALUE_DEFINITION, encoded);
        assertEquals(Map.of("value", Map.of("x", 3)), valueOf(encoded));
    }

    void testSerializedAsListWithClassAnnotationsVpack() throws Exception {
        SimpleValueList list = new SimpleValueList();
        list.add(new ActualValue("foo"));
        byte[] encoded = MAPPER.writeValueAsBytes(list);
        assertArrayEquals(LIST_CLASS_ANNOTATIONS, encoded);
        assertEquals(List.of(Map.of("value", "foo")), valueOf(encoded));
    }

    void testSerializedAsListWithClassSerializerVpack() throws Exception {
        SimpleValueListWithSerializer list = new SimpleValueListWithSerializer();
        list.add(new ActualValue("foo"));
        assertEquals(List.of("value foo"), valueOf(WRITER.writeValueAsBytes(list)));
    }

    void testSerializedAsListWithPropertyAnnotationsVpack() throws Exception {
        ListWrapperSimple input = new ListWrapperSimple("bar");
        assertEquals(Map.of("values", List.of(Map.of("value", "bar"))),
                valueOf(WRITER.writeValueAsBytes(input)));
    }

    void testSerializedAsListWithPropertyAnnotations2Vpack() throws Exception {
        ListWrapperWithSerializer input = new ListWrapperWithSerializer("abc");
        assertEquals(Map.of("values", List.of("value abc")),
                valueOf(WRITER.writeValueAsBytes(input)));
    }

    void testSerializedAsMapWithClassAnnotationsVpack() throws Exception {
        SimpleValueMap map = new SimpleValueMap();
        map.put(new SimpleKey("x"), new ActualValue("y"));
        assertEquals(Map.of("toString:x", Map.of("value", "y")),
                valueOf(WRITER.writeValueAsBytes(map)));
    }

    void testSerializedAsMapWithClassSerializerVpack() throws Exception {
        SimpleValueMapWithSerializer map = new SimpleValueMapWithSerializer();
        map.put(new SimpleKey("abc"), new ActualValue("123"));
        assertEquals(Map.of("key abc", "value 123"),
                valueOf(WRITER.writeValueAsBytes(map)));
    }

    void testSerializedAsMapWithPropertyAnnotationsVpack() throws Exception {
        MapWrapperSimple input = new MapWrapperSimple("a", "b");
        assertEquals(Map.of("values", Map.of("toString:a", Map.of("value", "b"))),
                valueOf(WRITER.writeValueAsBytes(input)));
    }

    void testSerializedAsMapWithPropertyAnnotations2Vpack() throws Exception {
        MapWrapperWithSerializer input = new MapWrapperWithSerializer("foo", "b");
        assertEquals(Map.of("values", Map.of("key foo", "value b")),
                valueOf(WRITER.writeValueAsBytes(input)));
    }

    void testSerializeWithFieldAnnoVpack() throws Exception {
        assertEquals(Map.of("foo", Map.of("foo", 42)),
                valueOf(WRITER.writeValueAsBytes(new FooableWithFieldWrapper())));
    }

    void testSpecializedContentAsVpack() throws Exception {
        assertEquals(Map.of("values", List.of(Map.of("a", 1, "b", 2))),
                valueOf(WRITER.writeValueAsBytes(new Bean5476Wrapper(1))));
    }

    void testSpecializedAsIntermediateVpack() throws Exception {
        assertEquals(Map.of("value", Map.of("a", 1, "b", 2)),
                valueOf(WRITER.writeValueAsBytes(new Bean5476Holder())));
    }
private static Object valueOf(byte[] encoded) throws Exception {
        return MAPPER.readValue(encoded, Object.class);
    }
interface ValueInterface { int getX(); }
static class ValueClass implements ValueInterface {
        @Override public int getX() { return 3; }
        public int getY() { return 5; }
    }
static class WrapperClassForAs {
        @JsonSerialize(as = ValueInterface.class)
        public ValueClass getValue() { return new ValueClass(); }
    }
static class SimpleKey {
        protected final String key;

        SimpleKey(String key) { this.key = key; }

        @Override public String toString() { return "toString:" + key; }
    }
static class SimpleValue {
        public final String value;

        SimpleValue(String value) { this.value = value; }
    }
static class ActualValue extends SimpleValue {
        public final String other = "123";

        ActualValue(String value) { super(value); }
    }
@JsonSerialize(contentAs = SimpleValue.class)
    static class SimpleValueList extends ArrayList<ActualValue> {
        private static final long serialVersionUID = 1L;
    }
@JsonSerialize(contentUsing = SimpleValueSerializer.class)
    static class SimpleValueListWithSerializer extends ArrayList<ActualValue> {
        private static final long serialVersionUID = 1L;
    }
@JsonSerialize(contentAs = SimpleValue.class)
    static class SimpleValueMap extends HashMap<SimpleKey, ActualValue> {
        private static final long serialVersionUID = 1L;
    }
@JsonSerialize(keyUsing = SimpleKeySerializer.class, contentUsing = SimpleValueSerializer.class)
    static class SimpleValueMapWithSerializer extends HashMap<SimpleKey, ActualValue> {
        private static final long serialVersionUID = 1L;
    }
static class SimpleKeySerializer extends ValueSerializer<SimpleKey> {
        @Override
        public void serialize(SimpleKey key, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeName("key " + key.key);
        }
    }
static class SimpleValueSerializer extends ValueSerializer<SimpleValue> {
        @Override
        public void serialize(SimpleValue value, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeString("value " + value.value);
        }
    }
static class ListWrapperSimple {
        @JsonSerialize(contentAs = SimpleValue.class)
        public final ArrayList<ActualValue> values = new ArrayList<>();

        ListWrapperSimple(String value) { values.add(new ActualValue(value)); }
    }
static class ListWrapperWithSerializer {
        @JsonSerialize(contentUsing = SimpleValueSerializer.class)
        public final ArrayList<ActualValue> values = new ArrayList<>();

        ListWrapperWithSerializer(String value) { values.add(new ActualValue(value)); }
    }
static class MapWrapperSimple {
        @JsonSerialize(contentAs = SimpleValue.class)
        public final HashMap<SimpleKey, ActualValue> values = new HashMap<>();

        MapWrapperSimple(String key, String value) {
            values.put(new SimpleKey(key), new ActualValue(value));
        }
    }
static class MapWrapperWithSerializer {
        @JsonSerialize(keyUsing = SimpleKeySerializer.class, contentUsing = SimpleValueSerializer.class)
        public final HashMap<SimpleKey, ActualValue> values = new HashMap<>();

        MapWrapperWithSerializer(String key, String value) {
            values.put(new SimpleKey(key), new ActualValue(value));
        }
    }
interface Fooable { int getFoo(); }
@JsonSerializeAs(Fooable.class)
    static class FooImpl implements Fooable {
        @Override public int getFoo() { return 42; }
        public int getBar() { return 15; }
    }
static class FooImplNoAnno implements Fooable {
        @Override public int getFoo() { return 42; }
        public int getBar() { return 15; }
    }
static class FooableWithFieldWrapper {
        @JsonSerializeAs(Fooable.class)
        public Fooable getFoo() { return new FooImplNoAnno(); }
    }
interface Bean5476Base { int getA(); }
static abstract class Bean5476Abstract implements Bean5476Base {
        @Override public int getA() { return 1; }
        public int getB() { return 2; }
    }
static class Bean5476Impl extends Bean5476Abstract {
        public int getC() { return 3; }
    }
static class Bean5476Wrapper {
        @JsonSerializeAs(content = Bean5476Abstract.class)
        public List<Bean5476Base> values;

        Bean5476Wrapper(int count) {
            values = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                values.add(new Bean5476Impl());
            }
        }
    }
static class Bean5476Holder {
        @JsonSerializeAs(Bean5476Abstract.class)
        public Bean5476Base value = new Bean5476Impl();
    }

    void __invoke_testSimpleValueDefinitionVpack() throws Exception {
        try {
            testSimpleValueDefinitionVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsListWithClassAnnotationsVpack() throws Exception {
        try {
            testSerializedAsListWithClassAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsListWithClassSerializerVpack() throws Exception {
        try {
            testSerializedAsListWithClassSerializerVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsListWithPropertyAnnotationsVpack() throws Exception {
        try {
            testSerializedAsListWithPropertyAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsListWithPropertyAnnotations2Vpack() throws Exception {
        try {
            testSerializedAsListWithPropertyAnnotations2Vpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsMapWithClassAnnotationsVpack() throws Exception {
        try {
            testSerializedAsMapWithClassAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsMapWithClassSerializerVpack() throws Exception {
        try {
            testSerializedAsMapWithClassSerializerVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsMapWithPropertyAnnotationsVpack() throws Exception {
        try {
            testSerializedAsMapWithPropertyAnnotationsVpack();
        } finally {
        }
    }


    void __invoke_testSerializedAsMapWithPropertyAnnotations2Vpack() throws Exception {
        try {
            testSerializedAsMapWithPropertyAnnotations2Vpack();
        } finally {
        }
    }


    void __invoke_testSerializeWithFieldAnnoVpack() throws Exception {
        try {
            testSerializeWithFieldAnnoVpack();
        } finally {
        }
    }


    void __invoke_testSpecializedContentAsVpack() throws Exception {
        try {
            testSpecializedContentAsVpack();
        } finally {
        }
    }


    void __invoke_testSpecializedAsIntermediateVpack() throws Exception {
        try {
            testSpecializedAsIntermediateVpack();
        } finally {
        }
    }

}
