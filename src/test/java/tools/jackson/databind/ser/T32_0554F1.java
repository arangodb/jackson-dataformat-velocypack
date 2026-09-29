package tools.jackson.databind.ser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonSerializeAs;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0554F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper STATIC_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.USE_STATIC_TYPING)
            .build();
private static final ObjectWriter WRITER = MAPPER.writer();
private static final byte[] STATIC_CLASS = VPackWireFixtureTest.hex(
            "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 33 03 03");
private static final byte[] IS_GETTER = VPackWireFixtureTest.hex(
            "0b 14 02 41 61 41 78 49 73 6f 6d 65 74 68 69 6e 67 1a 03 07");
private static final byte[] JSON_VALUE_OVERRIDE = VPackWireFixtureTest.hex("28 2a");

    void testCollectionViaJsonValueVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new Bean1806());
        assertEquals(List.of(Map.of("impl", Map.of("value", 1))), valueOf(encoded));
    }

    void testDisablingVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new DisabledJsonValue());
        assertEquals(Map.of("x", 1, "y", 2), valueOf(encoded));
    }

    void testFormatWithJsonValueVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new A2822("desc", new B2822(BigDecimal.ONE)));
        assertEquals(Map.of("description", "desc", "b", "1"), valueOf(encoded));
    }

    void testInListVpack() throws Exception {
        IntExtBean bean = new IntExtBean();
        bean.add(1);
        bean.add(2);
        byte[] encoded = MAPPER.writeValueAsBytes(bean);
        assertEquals(Map.of("values", List.of(Map.of("i", 1), Map.of("i", 2))), valueOf(encoded));
    }

    void testJsonValueWithCustomOverrideVpack() throws Exception {
        assertEquals("value", valueOf(MAPPER.writeValueAsBytes(new Bean838())));
        ObjectMapper mapper = VPackMapper.builder()
                .addModule(new SimpleModule().addSerializer(Bean838.class, new Bean838Serializer()))
                .build();
        byte[] encoded = mapper.writeValueAsBytes(new Bean838());
        assertArrayEquals(JSON_VALUE_OVERRIDE, encoded);
        assertEquals(42, valueOf(encoded));
    }
private static Object valueOf(byte[] encoded) throws Exception {
        return MAPPER.readValue(encoded, Object.class);
    }
interface ValueInterface { int getX(); }
static class ValueClass implements ValueInterface {
        @Override public int getX() { return 3; }
        public int getY() { return 5; }
    }
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
    static class WrapperClassForStaticTyping {
        public ValueInterface getValue() { return new ValueClass(); }
    }
static class ValueMap extends LinkedHashMap<String, ValueInterface> { }
static class ValueList extends ArrayList<ValueInterface> { }
static class ValueLinkedList extends LinkedList<ValueInterface> { }
@JsonPropertyOrder({ "a", "something" })
    static class Response {
        public String a = "x";
        @JsonProperty
        public boolean isSomething() { return true; }
    }
interface MapKeyBase { String getId(); }
@JsonPropertyOrder({ "id" })
    static abstract class MapKeyAbstract implements MapKeyBase {
        @Override public String getId() { return "key"; }
    }
static class MapKeyImpl extends MapKeyAbstract {
        public String getExtra() { return "extra"; }
    }
static class MapKeyWrapper {
        @JsonSerializeAs(key = MapKeyAbstract.class)
        public Map<MapKeyBase, String> values = new LinkedHashMap<>();

        MapKeyWrapper() { values.put(new MapKeyImpl(), "value1"); }
    }
static class DisabledJsonValue {
        @JsonValue(false) public int x = 1;
        @JsonValue(false) public int getY() { return 2; }
    }
static class IntExtBean {
        public List<Internal> values = new ArrayList<>();
        public void add(int value) { values.add(new Internal(value)); }
    }
static class Internal {
        public int value;
        Internal(int value) { this.value = value; }
        @JsonValue public External asExternal() { return new External(this); }
    }
static class External {
        public int i;
        External(Internal value) { i = value.value; }
    }
static class Bean838 {
        @JsonValue public String value() { return "value"; }
    }
static class Bean838Serializer extends StdScalarSerializer<Bean838> {
        Bean838Serializer() { super(Bean838.class); }
        @Override public void serialize(Bean838 value, tools.jackson.core.JsonGenerator gen,
                tools.jackson.databind.SerializationContext provider) {
            gen.writeNumber(42);
        }
    }
static class Bean1806 {
        @JsonValue public List<Elem1806> getThings() {
            return List.of(new Elem1806.Impl());
        }
    }
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME,
            include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT)
    @com.fasterxml.jackson.annotation.JsonSubTypes(
            @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = Elem1806.Impl.class, name = "impl"))
    interface Elem1806 {
        class Impl implements Elem1806 { public int value = 1; }
    }
@JsonPropertyOrder({ "description", "b" })
    static class A2822 {
        public final String description;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public final B2822 b;
        A2822(String description, B2822 b) { this.description = description; this.b = b; }
    }
static class B2822 {
        @JsonValue private final BigDecimal value;
        B2822(BigDecimal value) { this.value = value; }
    }

    void __invoke_testCollectionViaJsonValueVpack() throws Exception {
        try {
            testCollectionViaJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testDisablingVpack() throws Exception {
        try {
            testDisablingVpack();
        } finally {
        }
    }


    void __invoke_testFormatWithJsonValueVpack() throws Exception {
        try {
            testFormatWithJsonValueVpack();
        } finally {
        }
    }


    void __invoke_testInListVpack() throws Exception {
        try {
            testInListVpack();
        } finally {
        }
    }


    void __invoke_testJsonValueWithCustomOverrideVpack() throws Exception {
        try {
            testJsonValueWithCustomOverrideVpack();
        } finally {
        }
    }

}
