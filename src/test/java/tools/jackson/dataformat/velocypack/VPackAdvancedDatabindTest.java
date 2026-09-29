package tools.jackson.dataformat.velocypack;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import tools.jackson.core.JsonToken;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdScalarDeserializer;
import tools.jackson.databind.ser.std.StdScalarSerializer;
import tools.jackson.databind.PropertyNamingStrategies;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Portable property-level databind behavior; assertions do not depend on JSON text. */
public class VPackAdvancedDatabindTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    // Equivalent family: databind/type/GenericTypeTest generic collection handling.
    void typeReferenceAndJavaTypeReadNestedCollections() throws Exception {
        List<Map<String, List<Integer>>> input = List.of(
                Map.of("values", List.of(1, 2), "more", List.of(3)));
        byte[] encoded = mapper.writeValueAsBytes(input);

        List<Map<String, List<Integer>>> fromReference = mapper.readValue(encoded,
                new TypeReference<List<Map<String, List<Integer>>>>() { });

        JavaType integers = mapper.getTypeFactory().constructCollectionType(List.class,
                Integer.class);
        JavaType map = mapper.getTypeFactory().constructMapType(Map.class,
                mapper.getTypeFactory().constructType(String.class), integers);
        JavaType nested = mapper.getTypeFactory().constructCollectionType(List.class, map);
        List<Map<String, List<Integer>>> fromJavaType = mapper.readValue(encoded, nested);

        assertEquals(input, fromReference);
        assertEquals(input, fromJavaType);

        // Independent literal bytes: compact array [1, 2], not an encoder round trip.
        assertEquals(List.of(1, 2), mapper.readValue(new byte[] { 0x02, 0x04, 0x31, 0x32 },
                new TypeReference<List<Integer>>() { }));
    }

    @Test
    // Equivalent families: CreatorWithNamingStrategyTest and JacksonAnnotationIntrospectorTest.
    void creatorPropertiesNamingAndCustomPropertyHandlersRemainPortable() throws Exception {
        VPackMapper snake = VPackMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
        ConfiguredRecord input = new ConfiguredRecord("Ada", "display", "abc", null);

        Map<?, ?> wire = snake.readValue(snake.writeValueAsBytes(input), Map.class);
        assertEquals("Ada", wire.get("first_name"));
        assertEquals("display", wire.get("display_name"));
        assertEquals("wire:abc", wire.get("code"));
        assertFalse(wire.containsKey("optional_value"));

        ConfiguredRecord output = snake.readValue(snake.writeValueAsBytes(input),
                ConfiguredRecord.class);
        assertEquals(input, output);

        CreatorBean created = snake.readValue(snake.writeValueAsBytes(
                Map.of("first_name", "Grace", "item_count", 8)), CreatorBean.class);
        assertEquals(new CreatorBean("Grace", 8), created);
    }

    @Test
    // Equivalent families: BasicViewDeserializationTest and ViewSerializationTest.
    void viewsAndInclusionApplyToVPackProperties() throws Exception {
        ViewBean input = new ViewBean("public", "secret", null);

        byte[] publicBytes = mapper.writerWithView(PublicView.class).writeValueAsBytes(input);
        Map<?, ?> publicWire = mapper.readValue(publicBytes, Map.class);
        assertEquals("public", publicWire.get("publicValue"));
        assertFalse(publicWire.containsKey("secretValue"));
        assertFalse(publicWire.containsKey("optionalValue"));

        byte[] adminBytes = mapper.writerWithView(AdminView.class).writeValueAsBytes(input);
        ViewBean publicRead = mapper.readerFor(ViewBean.class).withView(PublicView.class)
                .readValue(adminBytes);
        assertEquals("public", publicRead.publicValue);
        assertNull(publicRead.secretValue);
    }

    @Test
    // EnumFeature is configured at the databind layer; map keys are ordinary VPack names.
    void configuredEnumValuesAndKeysRoundTripWithoutTextTokenAssumptions() throws Exception {
        EnumPayload input = new EnumPayload(Mode.SLOW, Map.of(Mode.FAST, "quick"));
        VPackMapper indexed = VPackMapper.builder()
                .enable(EnumFeature.WRITE_ENUMS_USING_INDEX,
                        EnumFeature.WRITE_ENUM_KEYS_USING_INDEX,
                        EnumFeature.READ_ENUM_KEYS_USING_INDEX)
                .build();

        Map<?, ?> wire = indexed.readValue(indexed.writeValueAsBytes(input), Map.class);
        assertEquals(1, wire.get("mode"));
        assertEquals("quick", ((Map<?, ?>) wire.get("labels")).get("0"));
        assertEquals(input, indexed.readValue(indexed.writeValueAsBytes(input), EnumPayload.class));
    }

    @Test
    void binaryPropertiesUseEmbeddedBytesAndDuplicateNamesHonorReadConfiguration() throws Exception {
        BinaryPayload input = new BinaryPayload(new byte[] { 0, 1, (byte) 0xFF });
        byte[] encoded = mapper.writeValueAsBytes(input);
        BinaryPayload output = mapper.readValue(encoded, BinaryPayload.class);
        assertArrayEquals(input.payload, output.payload);

        Map<?, ?> untyped = mapper.readValue(encoded, Map.class);
        assertArrayEquals(input.payload, (byte[]) untyped.get("payload"));
        try (VPackParser parser = (VPackParser) mapper.tokenStreamFactory().createParser(encoded)) {
            assertEquals(JsonToken.START_OBJECT, parser.nextToken());
            assertEquals(JsonToken.PROPERTY_NAME, parser.nextToken());
            assertEquals(JsonToken.VALUE_EMBEDDED_OBJECT, parser.nextToken());
            assertArrayEquals(input.payload, parser.getBinaryValue());
        }

        byte[] duplicate = VPackObjectParserTest.object(1, true,
                VPackObjectParserTest.body(
                        VPackObjectParserTest.pair("value", new byte[] { 0x31 }),
                        VPackObjectParserTest.pair("value", new byte[] { 0x32 })),
                new long[] { 3, 10 });
        assertEquals(2, ((Map<?, ?>) mapper.readValue(duplicate, Map.class)).get("value"));
        VPackMapper strict = VPackMapper.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build();
        assertThrows(StreamReadException.class, () -> strict.readValue(duplicate, Map.class));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record ConfiguredRecord(
            String firstName,
            @JsonProperty("display_name") String displayName,
            @JsonSerialize(using = PrefixSerializer.class)
            @JsonDeserialize(using = PrefixDeserializer.class) String code,
            String optionalValue) { }

    static final class CreatorBean {
        final String firstName;
        final int itemCount;

        @JsonCreator
        CreatorBean(@JsonProperty("first_name") String firstName,
                @JsonProperty("item_count") int itemCount) {
            this.firstName = firstName;
            this.itemCount = itemCount;
        }

        @JsonProperty("first_name")
        public String firstName() { return firstName; }

        @JsonProperty("item_count")
        public int itemCount() { return itemCount; }

        @Override
        public boolean equals(Object other) {
            return other instanceof CreatorBean that
                    && itemCount == that.itemCount && firstName.equals(that.firstName);
        }

        @Override
        public int hashCode() { return 31 * firstName.hashCode() + itemCount; }
    }

    static final class PrefixSerializer extends StdScalarSerializer<String> {
        PrefixSerializer() { super(String.class); }

        @Override
        public void serialize(String value, tools.jackson.core.JsonGenerator generator,
                tools.jackson.databind.SerializationContext ctxt) {
            generator.writeString("wire:" + value);
        }
    }

    static final class PrefixDeserializer extends StdScalarDeserializer<String> {
        PrefixDeserializer() { super(String.class); }

        @Override
        public String deserialize(tools.jackson.core.JsonParser parser,
                tools.jackson.databind.DeserializationContext ctxt) {
            String value = parser.getString();
            return value.startsWith("wire:") ? value.substring(5) : value;
        }
    }

    static final class ViewBean {
        @JsonView(PublicView.class)
        public String publicValue;
        @JsonView(AdminView.class)
        public String secretValue;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public List<String> optionalValue;

        ViewBean() { }
        ViewBean(String publicValue, String secretValue, List<String> optionalValue) {
            this.publicValue = publicValue;
            this.secretValue = secretValue;
            this.optionalValue = optionalValue;
        }
    }

    static class PublicView { }
    static final class AdminView extends PublicView { }

    enum Mode {
        FAST, SLOW;

        @Override
        public String toString() { return name().toLowerCase(); }
    }

    record EnumPayload(Mode mode, Map<Mode, String> labels) { }
    record BinaryPayload(byte[] payload) { }
}
