package tools.jackson.databind.jsontype;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.exc.InvalidDefinitionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0416F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] CUSTOM_COLLECTION = VPackWireFixtureTest.hex(
            "14 19 46 76 61 6c 75 65 73 13 0f "
          + "14 06 41 76 33 01 14 06 41 76 37 01 02 01");
private static final byte[] CUSTOM_MAP = VPackWireFixtureTest.hex(
            "14 26 46 76 61 6c 75 65 73 14 1c "
          + "45 66 69 72 73 74 14 06 41 76 33 01 "
          + "46 73 65 63 6f 6e 64 14 06 41 76 37 01 02 01");
private static final byte[] CUSTOM_VALUE = VPackWireFixtureTest.hex(
            "14 10 45 76 61 6c 75 65 14 07 41 76 28 2a 01 01");
private static final byte[] TYPED_COLLECTION = VPackWireFixtureTest.hex(
            "0b 53 01 46 76 61 6c 75 65 73 02 48 0b 23 02 45" +
                "40 74 79 70 65 54 54 33 32 5f 30 34 31 36 46 31" +
                "24 56 61 6c 75 65 31 36 35 34 41 78 31 03 1e 0b" +
                "23 02 45 40 74 79 70 65 54 54 33 32 5f 30 34 31" +
                "36 46 31 24 56 61 6c 75 65 31 36 35 34 41 78 32" +
                "03 1e 03");
private static final byte[] TYPED_MAP = VPackWireFixtureTest.hex(
            "0b 63 01 46 76 61 6c 75 65 73 0b 58 02 45 66 69" +
                "72 73 74 0b 23 02 45 40 74 79 70 65 54 54 33 32" +
                "5f 30 34 31 36 46 31 24 56 61 6c 75 65 31 36 35" +
                "34 41 78 31 03 1e 46 73 65 63 6f 6e 64 0b 23 02" +
                "45 40 74 79 70 65 54 54 33 32 5f 30 34 31 36 46" +
                "31 24 56 61 6c 75 65 31 36 35 34 41 78 32 03 1e" +
                "03 2c 03");
private static final byte[] TYPED_VALUE = VPackWireFixtureTest.hex(
            "0b 2e 01 45 76 61 6c 75 65 0b 24 02 45 40 74 79" +
                "70 65 54 54 33 32 5f 30 34 31 36 46 31 24 56 61" +
                "6c 75 65 31 36 35 34 41 78 28 2a 03 1e 03");

    // Provenance: PolymorphicDeserErrorHandlingTest#testBlockingOfRunnable().
    void testBlockingOfRunnableVpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        AnimalInfo value = new AnimalInfo();
        value.thisType = new Dog5016();
        byte[] encoded = mapper.writeValueAsBytes(value);
        AnimalInfo roundTrip = mapper.readValue(encoded, AnimalInfo.class);
        assertEquals("animal", roundTrip.thisType.name);
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(encoded, RunnableInfo.class));
        assertNotNull(error.getMessage());
        assertTrue(error.getMessage().contains("PolymorphicTypeValidator"));
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    static class Value1654 {
        public int x;

        protected Value1654() { }

        Value1654(int x) { this.x = x; }
    }
static class Value1654Deserializer extends ValueDeserializer<Value1654> {
        @Override
        public Value1654 deserialize(JsonParser parser, DeserializationContext ctxt)
                throws JacksonException {
            JsonNode node = ctxt.readTree(parser);
            if (!node.has("v")) {
                ctxt.reportInputMismatch(Value1654.class, "Bad VPack input (no 'v'): " + node);
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
static class Value1654UsingCustomSerDeserUntypedContainer {
        @JsonDeserialize(contentUsing = Value1654Deserializer.class)
        @JsonSerialize(contentUsing = Value1654Serializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public List<Value1654> values;

        protected Value1654UsingCustomSerDeserUntypedContainer() { }

        Value1654UsingCustomSerDeserUntypedContainer(Value1654... values) {
            this.values = Arrays.asList(values);
        }
    }
static class Value1654UsingCustomSerDeserUntypedMapContainer {
        @JsonDeserialize(contentUsing = Value1654Deserializer.class)
        @JsonSerialize(contentUsing = Value1654Serializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Map<String, Value1654> values;

        protected Value1654UsingCustomSerDeserUntypedMapContainer() { }

        Value1654UsingCustomSerDeserUntypedMapContainer(Map<String, Value1654> values) {
            this.values = values;
        }
    }
static class Value1654UsingCustomSerDeserUntypedOptionalContainer {
        @JsonDeserialize(contentUsing = Value1654Deserializer.class)
        @JsonSerialize(contentUsing = Value1654Serializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public Optional<Value1654> value;

        protected Value1654UsingCustomSerDeserUntypedOptionalContainer() { }

        Value1654UsingCustomSerDeserUntypedOptionalContainer(Value1654 value) {
            this.value = Optional.ofNullable(value);
        }
    }
static class Value1654UsingCustomSerDeserUntypedAtomicRefContainer {
        @JsonDeserialize(contentUsing = Value1654Deserializer.class)
        @JsonSerialize(contentUsing = Value1654Serializer.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
        public AtomicReference<Value1654> value;

        protected Value1654UsingCustomSerDeserUntypedAtomicRefContainer() { }

        Value1654UsingCustomSerDeserUntypedAtomicRefContainer(Value1654 value) {
            this.value = new AtomicReference<>(value);
        }
    }
static class Value1654TypedContainer {
        public List<Value1654> values;

        protected Value1654TypedContainer() { }

        Value1654TypedContainer(Value1654... values) { this.values = Arrays.asList(values); }
    }
static class Value1654TypedMapContainer {
        public Map<String, Value1654> values;

        protected Value1654TypedMapContainer() { }

        Value1654TypedMapContainer(Map<String, Value1654> values) { this.values = values; }
    }
static class Value1654TypedOptionalContainer {
        public Optional<Value1654> value;

        protected Value1654TypedOptionalContainer() { }

        Value1654TypedOptionalContainer(Value1654 value) { this.value = Optional.ofNullable(value); }
    }
static class Value1654TypedAtomicRefContainer {
        public AtomicReference<Value1654> value;

        protected Value1654TypedAtomicRefContainer() { }

        Value1654TypedAtomicRefContainer(Value1654 value) { this.value = new AtomicReference<>(value); }
    }
abstract static class Animal5016 {
        public String name = "animal";
    }
static class Dog5016 extends Animal5016 implements Runnable {
        public String name = "dog";

        @Override
        public void run() { }
    }
static class AnimalInfo {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Animal5016 thisType;
    }
static class RunnableInfo {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
                property = "@class")
        public Runnable thisType;
    }

    void __invoke_testBlockingOfRunnableVpack() throws Exception {
        try {
            testBlockingOfRunnableVpack();
        } finally {
        }
    }

}
