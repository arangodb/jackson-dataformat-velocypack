package tools.jackson.databind.records;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0524F1 {
private static final byte[] ID_ONLY_123 = VPackWireFixtureTest.hex(
            "0b 0e 01 47 69 64 5f 6f 6e 6c 79 28 7b 03");
private static final byte[] TWO_PROPERTIES_123 = VPackWireFixtureTest.hex(
            "0b 28 02 46 74 68 65 5f 69 64 28 7b "
          + "49 74 68 65 5f 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d "
          + "0c 03");
private static final byte[] CANONICAL_PROPERTIES = VPackWireFixtureTest.hex(
            "0b 15 02 42 69 64 28 7b 44 6e 61 6d 65 45 42 6f 62 62 79 03 08");
private static final byte[] CANONICAL_PROPERTIES_WITH_EMAIL = VPackWireFixtureTest.hex(
            "0b 2e 03 42 69 64 28 7b 44 6e 61 6d 65 45 42 6f 62 62 79 "
          + "45 65 6d 61 69 6c 51 62 6f 62 62 79 40 65 78 61 6d 70 6c 65 2e 63 6f 6d "
          + "13 03 08");
private static final byte[] DECIMAL_123_4 = VPackWireFixtureTest.hex(
            "c8 02 ff ff ff ff 12 34");
private static final byte[] CANONICAL_DECIMAL_RECORD = VPackWireFixtureTest.hex(
            "0b 2a 02 42 69 64 c8 02 ff ff ff ff 12 34 "
          + "44 6e 61 6d 65 54 43 61 6e 6f 6e 69 63 61 6c 43 6f 6e 73 74 72 75 63 74 6f 72 "
          + "03 0e");
private static final byte[] STRING_123 = VPackWireFixtureTest.hex(
            "43 31 32 33");
private static final byte[] STRING_123_4 = VPackWireFixtureTest.hex(
            "45 31 32 33 2e 34");
private static final byte[] PERSON_WITH_HELPER = VPackWireFixtureTest.hex(
            "14 22 44 6e 61 6d 65 43 62 6f 62 43 61 67 65 28 1e "
          + "4b 64 69 73 70 6c 61 79 4e 61 6d 65 43 42 4f 42 03");
private static final ObjectMapper CREATOR_MAPPER = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Provenance: RecordIgnoreGettersTest#testComponentAccessor_AlwaysWorks().
    void testComponentAccessorAlwaysWorksVpack() throws Exception {
        MixedRecord record = new MixedRecord(42);
        JsonNode restricted = CREATOR_MAPPER.readTree(VPackMapper.builder()
                .enable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
                .build().writeValueAsBytes(record));
        JsonNode defaulted = CREATOR_MAPPER.readTree(new VPackMapper().writeValueAsBytes(record));

        assertEquals(42, restricted.get("value").intValue());
        assertFalse(restricted.has("doubleValue"));
        assertEquals(42, defaulted.get("value").intValue());
        assertEquals(84, defaulted.get("doubleValue").intValue());
    }

    // Provenance: RecordIgnoreGettersTest#testDeserialization_IgnoresNonComponentProperties().
    void testDeserializationIgnoresNonComponentPropertiesVpack() throws Exception {
        PersonRecord result = VPackMapper.builder()
                .enable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
                .build().readValue(PERSON_WITH_HELPER, PersonRecord.class);
        assertNotNull(result);
        assertEquals("bob", result.name());
        assertEquals(30, result.age());
    }
record RecordWithOneJsonPropertyWithoutJsonCreator(int id, String name) {
        public RecordWithOneJsonPropertyWithoutJsonCreator(@JsonProperty("id_only") int id) {
            this(id, "JsonPropertyConstructor");
        }

        public static RecordWithOneJsonPropertyWithoutJsonCreator valueOf(int id) {
            return new RecordWithOneJsonPropertyWithoutJsonCreator(id);
        }
    }
record RecordWithTwoJsonPropertyWithoutJsonCreator(int id, String name, String email) {
        public RecordWithTwoJsonPropertyWithoutJsonCreator(
                @JsonProperty("the_id") int id, @JsonProperty("the_email") String email) {
            this(id, "TwoJsonPropertyConstructor", email);
        }

        public static RecordWithTwoJsonPropertyWithoutJsonCreator valueOf(int id) {
            return new RecordWithTwoJsonPropertyWithoutJsonCreator(id, "factory@example.com");
        }
    }
record RecordWithJsonPropertyWithJsonCreator(int id, String name) {
        @JsonCreator
        public RecordWithJsonPropertyWithJsonCreator(@JsonProperty("id_only") int id) {
            this(id, "JsonCreatorConstructor");
        }

        public static RecordWithJsonPropertyWithJsonCreator valueOf(int id) {
            return new RecordWithJsonPropertyWithJsonCreator(id);
        }
    }
record RecordWithMultiExplicitDelegatingConstructor(int id, String name) {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public RecordWithMultiExplicitDelegatingConstructor(int id) {
            this(id, "IntConstructor");
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public RecordWithMultiExplicitDelegatingConstructor(String id) {
            this(Integer.parseInt(id), "StringConstructor");
        }
    }
record RecordWithDisabledJsonCreator(int id, String name) {
        @JsonCreator(mode = JsonCreator.Mode.DISABLED)
        RecordWithDisabledJsonCreator { }
    }
record RecordWithExplicitFactoryMethod(BigDecimal id, String name) {
        @JsonCreator
        public static RecordWithExplicitFactoryMethod valueOf(int value) {
            return new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(value), "IntFactoryMethod");
        }

        public static RecordWithExplicitFactoryMethod valueOf(double value) {
            return new RecordWithExplicitFactoryMethod(BigDecimal.valueOf(value), "DoubleFactoryMethod");
        }

        @JsonCreator
        public static RecordWithExplicitFactoryMethod valueOf(String value) {
            return new RecordWithExplicitFactoryMethod(
                    BigDecimal.valueOf(Double.parseDouble(value)), "StringFactoryMethod");
        }
    }
record PersonRecord(String name, int age) {
        public String getDisplayName() {
            return name.toUpperCase();
        }
    }
record MixedRecord(int value) {
        @Override
        public int value() {
            return value;
        }

        public int getDoubleValue() {
            return value * 2;
        }
    }

    void __invoke_testComponentAccessorAlwaysWorksVpack() throws Exception {
        try {
            testComponentAccessorAlwaysWorksVpack();
        } finally {
        }
    }


    void __invoke_testDeserializationIgnoresNonComponentPropertiesVpack() throws Exception {
        try {
            testDeserializationIgnoresNonComponentPropertiesVpack();
        } finally {
        }
    }

}
