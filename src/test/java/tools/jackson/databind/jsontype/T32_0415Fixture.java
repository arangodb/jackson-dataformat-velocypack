package tools.jackson.databind.jsontype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import tools.jackson.dataformat.velocypack.*;

class T32_0415Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] VALUES_COLLECTION = VPackWireFixtureTest.hex(
            "14 19 46 76 61 6c 75 65 73 13 0f "
          + "14 06 41 78 33 01 14 06 41 78 37 01 02 01");
private static final byte[] VALUES_MAP = VPackWireFixtureTest.hex(
            "14 26 46 76 61 6c 75 65 73 14 1c "
          + "45 66 69 72 73 74 14 06 41 78 33 01 "
          + "46 73 65 63 6f 6e 64 14 06 41 78 37 01 02 01");
private static final byte[] VALUE_X = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 78 28 2a 01 01");
private static final byte[] VALUE_V = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 76 28 2a 01 01");
private static final byte[] EMPTY_STUFF = VPackWireFixtureTest.hex(
            "14 0a 45 73 74 75 66 66 01 01");
private static final byte[] NO_TYPE_BEAN = VPackWireFixtureTest.hex(
            "14 06 41 61 36 01");

    // Provenance: NoTypeInfoTest#singleWithNoTypeInfoOverrideSer().
    void singleWithNoTypeInfoOverrideSerVpack() throws Exception {
        SingleValueUsingCustomSerDeserUntyped wrapper =
                new SingleValueUsingCustomSerDeserUntyped(new Value1654(42));

        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(wrapper), Map.class);
        assertEquals(Map.of("value", Map.of("v", 42)), encoded);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultSerCollection().
    void withNoTypeInfoDefaultSerCollectionVpack() throws Exception {
        Value1654UntypedContainer container = new Value1654UntypedContainer(
                new Value1654(3), new Value1654(7));

        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(container), Map.class);
        assertEquals(Map.of("values", List.of(Map.of("x", 3), Map.of("x", 7))), encoded);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultDeserCollection().
    void withNoTypeInfoDefaultDeserCollectionVpack() throws Exception {
        Value1654UntypedContainer result = MAPPER.readValue(
                VALUES_COLLECTION, Value1654UntypedContainer.class);

        assertEquals(2, result.values.size());
        assertEquals(3, result.values.get(0).x);
        assertEquals(7, result.values.get(1).x);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultSerMap().
    void withNoTypeInfoDefaultSerMapVpack() throws Exception {
        Value1654UntypedMapContainer container = new Value1654UntypedMapContainer(
                Map.of("first", new Value1654(3), "second", new Value1654(7)));

        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(container), Map.class);
        assertEquals(Map.of("values", Map.of("first", Map.of("x", 3),
                "second", Map.of("x", 7))), encoded);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultDeserMap().
    void withNoTypeInfoDefaultDeserMapVpack() throws Exception {
        Value1654UntypedMapContainer result = MAPPER.readValue(
                VALUES_MAP, Value1654UntypedMapContainer.class);

        assertEquals(2, result.values.size());
        assertEquals(3, result.values.get("first").x);
        assertEquals(7, result.values.get("second").x);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultSerOptional().
    void withNoTypeInfoDefaultSerOptionalVpack() throws Exception {
        Value1654UntypedOptionalContainer container =
                new Value1654UntypedOptionalContainer(new Value1654(42));

        Map<?, ?> encoded = MAPPER.readValue(MAPPER.writeValueAsBytes(container), Map.class);
        assertEquals(Map.of("value", Map.of("x", 42)), encoded);
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoDefaultDeserOptional().
    void withNoTypeInfoDefaultDeserOptionalVpack() throws Exception {
        Value1654UntypedOptionalContainer result = MAPPER.readValue(
                VALUE_X, Value1654UntypedOptionalContainer.class);

        assertInstanceOf(Value1654.class, result.value.orElseThrow());
        assertEquals(42, result.value.orElseThrow().x);
    }

    // Provenance: NoTypeInfoTest#singleWithNoTypeInfoOverrideSerMap().
    void singleWithNoTypeInfoOverrideSerMapVpack() throws Exception {
        // The original Map-section assertion is the same single-property
        // custom-serializer assertion as the Collection-section declaration.
        singleWithNoTypeInfoOverrideSerVpack();
    }

    // Provenance: NoTypeInfoTest#testWithIdNone().
    void testWithIdNoneVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Object.class).build())
                .build();

        Map<?, ?> encoded = MAPPER.readValue(mapper.writeValueAsBytes(new NoType()), Map.class);
        assertEquals(Map.of("a", 3), encoded);

        NoTypeInterface bean = mapper.readValue(NO_TYPE_BEAN, NoTypeInterface.class);
        assertInstanceOf(NoType.class, bean);
        assertEquals(6, ((NoType) bean).a);
    }

    // Provenance: NoTypeInfoTest#testCollectionWithOverride().
    void testCollectionWithOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .activateDefaultTypingAsProperty(BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class).build(),
                        DefaultTyping.OBJECT_AND_NON_CONCRETE, "$type")
                .build();

        Map<?, ?> encoded = MAPPER.readValue(
                mapper.writeValueAsBytes(new ListWrapper()), Map.class);
        assertEquals(Map.of("stuff", List.of()), encoded);

        ListWrapper result = mapper.readValue(EMPTY_STUFF, ListWrapper.class);
        assertEquals(0, result.stuff.size());
    }

    // Provenance: NoTypeInfoTest#withNoTypeInfoOverrideDeserAtomicRef().
    void withNoTypeInfoOverrideDeserAtomicRefVpack() throws Exception {
        Value1654UsingCustomSerDeserUntypedAtomicRefContainer result = MAPPER.readValue(
                VALUE_V, Value1654UsingCustomSerDeserUntypedAtomicRefContainer.class);

        assertEquals(42, result.value.get().x);
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    @JsonDeserialize(as = NoType.class)
    interface NoTypeInterface { }
static final class NoType implements NoTypeInterface {
        public int a = 3;
    }
static class ListWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Collection<String> stuff = Collections.emptyList();
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class Value1654 {
        public int x;

        protected Value1654() { }

        Value1654(int x) {
            this.x = x;
        }
    }
static class Value1654Deserializer extends ValueDeserializer<Value1654> {
        @Override
        public Value1654 deserialize(JsonParser parser, DeserializationContext ctxt)
                throws JacksonException {
            JsonNode node = ctxt.readTree(parser);
            if (!node.has("v")) {
                ctxt.reportInputMismatch(Value1654.class,
                        "Bad VPack input (no 'v'): " + node);
            }
            return new Value1654(node.path("v").intValue());
        }
    }
static class Value1654Serializer extends ValueSerializer<Value1654> {
        @Override
        public void serialize(Value1654 value, JsonGenerator generator,
                SerializationContext ctxt) throws JacksonException {
            generator.writeStartObject(value);
            generator.writeNumberProperty("v", value.x);
            generator.writeEndObject();
        }
    }
static class SingleValueUsingCustomSerDeserUntyped {
        @JsonDeserialize(using = Value1654Deserializer.class)
        @JsonSerialize(using = Value1654Serializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Value1654 value;

        SingleValueUsingCustomSerDeserUntyped(Value1654 value) {
            this.value = value;
        }
    }
static class Value1654UntypedContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public List<Value1654> values;

        protected Value1654UntypedContainer() { }

        Value1654UntypedContainer(Value1654... values) {
            this.values = Arrays.asList(values);
        }
    }
static class Value1654UntypedMapContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Map<String, Value1654> values;

        protected Value1654UntypedMapContainer() { }

        Value1654UntypedMapContainer(Map<String, Value1654> values) {
            this.values = values;
        }
    }
static class Value1654UntypedOptionalContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Optional<Value1654> value;

        protected Value1654UntypedOptionalContainer() { }

        Value1654UntypedOptionalContainer(Value1654 value) {
            this.value = Optional.ofNullable(value);
        }
    }
static class Value1654UsingCustomSerDeserUntypedAtomicRefContainer {
        @JsonDeserialize(contentUsing = Value1654Deserializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public AtomicReference<Value1654> value;

        protected Value1654UsingCustomSerDeserUntypedAtomicRefContainer() { }
    }

    void __invoke_singleWithNoTypeInfoOverrideSerVpack() throws Exception {
        try {
            singleWithNoTypeInfoOverrideSerVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultSerCollectionVpack() throws Exception {
        try {
            withNoTypeInfoDefaultSerCollectionVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultDeserCollectionVpack() throws Exception {
        try {
            withNoTypeInfoDefaultDeserCollectionVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultSerMapVpack() throws Exception {
        try {
            withNoTypeInfoDefaultSerMapVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultDeserMapVpack() throws Exception {
        try {
            withNoTypeInfoDefaultDeserMapVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultSerOptionalVpack() throws Exception {
        try {
            withNoTypeInfoDefaultSerOptionalVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoDefaultDeserOptionalVpack() throws Exception {
        try {
            withNoTypeInfoDefaultDeserOptionalVpack();
        } finally {
        }
    }


    void __invoke_singleWithNoTypeInfoOverrideSerMapVpack() throws Exception {
        try {
            singleWithNoTypeInfoOverrideSerMapVpack();
        } finally {
        }
    }


    void __invoke_testWithIdNoneVpack() throws Exception {
        try {
            testWithIdNoneVpack();
        } finally {
        }
    }


    void __invoke_testCollectionWithOverrideVpack() throws Exception {
        try {
            testCollectionWithOverrideVpack();
        } finally {
        }
    }


    void __invoke_withNoTypeInfoOverrideDeserAtomicRefVpack() throws Exception {
        try {
            withNoTypeInfoOverrideDeserAtomicRefVpack();
        } finally {
        }
    }

}
