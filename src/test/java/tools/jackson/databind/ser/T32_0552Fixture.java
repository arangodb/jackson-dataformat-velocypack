package tools.jackson.databind.ser;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSerializeAs;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.NullSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0552Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectWriter WRITER = MAPPER.writer();
private static final byte[] ISSUE_294 = VPackWireFixtureTest.hex(
            "0b 18 02 43 62 61 72 45 62 61 72 49 64 "
          + "42 69 64 45 66 6f 6f 49 64 03 0d");
private static final byte[] NULL_SERIALIZER = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 18 03");

    void testIssue294Vpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new Foo294("fooId", "barId"));
        assertArrayEquals(ISSUE_294, encoded);
        assertEquals(Map.of("bar", "barId", "id", "fooId"),
                MAPPER.readValue(encoded, Map.class));
    }

    void testMixedTypingForClassVpack() throws Exception {
        Map<?, ?> result = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new WrapperClassForStaticTyping2()), Map.class);
        assertEquals(Map.of(
                "staticValue", Map.of("x", 3),
                "dynamicValue", Map.of("x", 3, "y", 5)), result);
    }

    void testNullSerializerVpack() throws Exception {
        byte[] encoded = MAPPER.writeValueAsBytes(new NullBean());
        assertArrayEquals(NULL_SERIALIZER, encoded);
        Map<?, ?> value = MAPPER.readValue(encoded, Map.class);
        assertEquals(1, value.size());
        assertEquals(null, value.get("value"));
    }

    void testSerializeAsInClassVpack() throws Exception {
        assertEquals(Map.of("foo", 42), valueOf(WRITER.writeValueAsBytes(new FooImpl())));
    }

    void testSerializeAsForArrayPropVpack() throws Exception {
        assertEquals(Map.of("foos", List.of(Map.of("foo", 42))),
                valueOf(WRITER.writeValueAsBytes(new Fooables())));
    }

    void testSerializeAsForSimplePropVpack() throws Exception {
        assertEquals(Map.of("foo", Map.of("foo", 42)),
                valueOf(WRITER.writeValueAsBytes(new FooableWrapper())));
    }

    void testLegacySerializeAsInClassVpack() throws Exception {
        assertEquals(Map.of("foo", 42), valueOf(WRITER.writeValueAsBytes(new LegacyFooImpl())));
    }

    void testLegacySerializeAsForArrayPropVpack() throws Exception {
        assertEquals(Map.of("foos", List.of(Map.of("foo", 42))),
                valueOf(WRITER.writeValueAsBytes(new LegacyFooables())));
    }

    void testLegacySerializeAsForSimplePropVpack() throws Exception {
        assertEquals(Map.of("foo", Map.of("foo", 42)),
                valueOf(WRITER.writeValueAsBytes(new LegacyFooableWrapper())));
    }

    void testLegacySerializeWithFieldAnnoVpack() throws Exception {
        assertEquals(Map.of("foo", Map.of("foo", 42)),
                valueOf(WRITER.writeValueAsBytes(new LegacyFooableWithFieldWrapper())));
    }

    void testLegacySpecializedContentAs1178Vpack() throws Exception {
        assertEquals(Map.of("values", List.of(Map.of("a", 1, "b", 2))),
                valueOf(WRITER.writeValueAsBytes(new Bean1178Wrapper(1))));
    }

    void testLegacySpecializedAsIntermediate1231Vpack() throws Exception {
        assertEquals(Map.of("value", Map.of("a", 1, "b", 2)),
                valueOf(WRITER.writeValueAsBytes(new Bean1178Holder())));
    }
@SuppressWarnings("unchecked")
    private static Map<?, ?> valueOf(byte[] encoded) throws Exception {
        return MAPPER.readValue(encoded, Map.class);
    }
interface ValueInterface { int getX(); }
static class ValueClass implements ValueInterface {
        @Override public int getX() { return 3; }
        public int getY() { return 5; }
    }
static class WrapperClassForStaticTyping2 {
        @JsonSerialize(typing = JsonSerialize.Typing.STATIC)
        public ValueInterface getStaticValue() { return new ValueClass(); }

        @JsonSerialize(typing = JsonSerialize.Typing.DYNAMIC)
        public ValueInterface getDynamicValue() { return new ValueClass(); }
    }
static class Foo294 {
        @JsonProperty private String id;
        @JsonSerialize(using = Bar294Serializer.class)
        private Bar294 bar;

        Foo294(String id, String barId) {
            this.id = id;
            this.bar = new Bar294(barId);
        }
    }
static class Bar294 {
        private final String id;

        Bar294(String id) { this.id = id; }

        public String getId() { return id; }
    }
static class Bar294Serializer extends ValueSerializer<Bar294> {
        @Override
        public void serialize(Bar294 value, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeString(value.id);
        }
    }
static class NullBean {
        @JsonSerialize(using = NullSerializer.class)
        public String value = "abc";
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
static class Fooables {
        public FooImpl[] getFoos() { return new FooImpl[] { new FooImpl() }; }
    }
static class FooableWrapper {
        public FooImpl getFoo() { return new FooImpl(); }
    }
interface LegacyFooable { int getFoo(); }
@JsonSerialize(as = LegacyFooable.class)
    static class LegacyFooImpl implements LegacyFooable {
        @Override public int getFoo() { return 42; }
        public int getBar() { return 15; }
    }
static class LegacyFooImplNoAnno implements LegacyFooable {
        @Override public int getFoo() { return 42; }
        public int getBar() { return 15; }
    }
static class LegacyFooables {
        public LegacyFooImpl[] getFoos() {
            return new LegacyFooImpl[] { new LegacyFooImpl() };
        }
    }
static class LegacyFooableWrapper {
        public LegacyFooImpl getFoo() { return new LegacyFooImpl(); }
    }
static class LegacyFooableWithFieldWrapper {
        @JsonSerialize(as = LegacyFooable.class)
        public LegacyFooable getFoo() { return new LegacyFooImplNoAnno(); }
    }
interface Bean1178Base { int getA(); }
static abstract class Bean1178Abstract implements Bean1178Base {
        @Override public int getA() { return 1; }
        public int getB() { return 2; }
    }
static class Bean1178Impl extends Bean1178Abstract {
        public int getC() { return 3; }
    }
static class Bean1178Wrapper {
        @JsonSerialize(contentAs = Bean1178Abstract.class)
        public List<Bean1178Base> values;

        Bean1178Wrapper(int count) {
            values = new java.util.ArrayList<>();
            for (int i = 0; i < count; ++i) {
                values.add(new Bean1178Impl());
            }
        }
    }
static class Bean1178Holder {
        @JsonSerialize(as = Bean1178Abstract.class)
        public Bean1178Base value = new Bean1178Impl();
    }

    void __invoke_testIssue294Vpack() throws Exception {
        try {
            testIssue294Vpack();
        } finally {
        }
    }


    void __invoke_testMixedTypingForClassVpack() throws Exception {
        try {
            testMixedTypingForClassVpack();
        } finally {
        }
    }


    void __invoke_testNullSerializerVpack() throws Exception {
        try {
            testNullSerializerVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsInClassVpack() throws Exception {
        try {
            testSerializeAsInClassVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsForArrayPropVpack() throws Exception {
        try {
            testSerializeAsForArrayPropVpack();
        } finally {
        }
    }


    void __invoke_testSerializeAsForSimplePropVpack() throws Exception {
        try {
            testSerializeAsForSimplePropVpack();
        } finally {
        }
    }


    void __invoke_testLegacySerializeAsInClassVpack() throws Exception {
        try {
            testLegacySerializeAsInClassVpack();
        } finally {
        }
    }


    void __invoke_testLegacySerializeAsForArrayPropVpack() throws Exception {
        try {
            testLegacySerializeAsForArrayPropVpack();
        } finally {
        }
    }


    void __invoke_testLegacySerializeAsForSimplePropVpack() throws Exception {
        try {
            testLegacySerializeAsForSimplePropVpack();
        } finally {
        }
    }


    void __invoke_testLegacySerializeWithFieldAnnoVpack() throws Exception {
        try {
            testLegacySerializeWithFieldAnnoVpack();
        } finally {
        }
    }


    void __invoke_testLegacySpecializedContentAs1178Vpack() throws Exception {
        try {
            testLegacySpecializedContentAs1178Vpack();
        } finally {
        }
    }


    void __invoke_testLegacySpecializedAsIntermediate1231Vpack() throws Exception {
        try {
            testLegacySpecializedAsIntermediate1231Vpack();
        } finally {
        }
    }

}
