package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.ConstructorDetector;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import tools.jackson.dataformat.velocypack.*;

class T32_0526F0 {
private static final ObjectMapper DEFAULT_MAPPER = new VPackMapper();
private static final ObjectMapper RESTRICTED_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
            .build();
private static final ObjectMapper PROPERTIES_MAPPER = VPackMapper.builder()
            .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
            .build();
private static final ObjectMapper SNAKE_CASE_MAPPER = VPackMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();
private static final byte[] NAME_BOB = VPackWireFixtureTest.hex(
            "14 0c 44 6e 61 6d 65 43 42 6f 62 01");
private static final byte[] ID_NAME_EMAIL = VPackWireFixtureTest.hex(
            "14 27 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 "
          + "45 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d 03");
private static final byte[] ID_EMAIL = VPackWireFixtureTest.hex(
            "14 1e 42 69 64 28 7b 45 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d 02");
private static final byte[] ID_NAME = VPackWireFixtureTest.hex(
            "14 11 42 69 64 28 7b 44 6e 61 6d 65 43 42 6f 62 02");
private static final byte[] NAME_EMAIL = VPackWireFixtureTest.hex(
            "14 20 44 6e 61 6d 65 43 42 6f 62 45 65 6d 61 69 6c "
          + "4d 62 6f 62 40 65 6d 61 69 6c 2e 63 6f 6d 02");
private static final byte[] USERNAME_INTERNAL_ROLE = VPackWireFixtureTest.hex(
            "14 26 4d 69 6e 74 65 72 6e 61 6c 5f 72 6f 6c 65 "
          + "45 41 44 4d 49 4e 48 75 73 65 72 6e 61 6d 65 45 61 6c 69 63 65 02");

    // Provenance: RecordIgnoreGettersTest#testSerializeIgnoreInterfaceGetter_UsingVisibilityConfig().
    void testSerializeIgnoreInterfaceGetterUsingVisibilityConfigVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(PropertyAccessor.GETTER, NONE)
                        .withVisibility(PropertyAccessor.FIELD, ANY))
                .build();

        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(
                new RecordWithInterfaceWithGetter("Bob")));

        assertEquals(1, tree.size());
        assertEquals("Bob", tree.get("name").textValue());
        assertFalse(tree.has("id"));
        assertFalse(tree.has("count"));
    }

    // Provenance: RecordIgnoreGettersTest#testSerializeIgnoreInterfaceGetter_WithoutUsingVisibilityConfig().
    void testSerializeIgnoreInterfaceGetterWithoutUsingVisibilityConfigVpack() throws Exception {
        JsonNode tree = DEFAULT_MAPPER.readTree(DEFAULT_MAPPER.writeValueAsBytes(
                new RecordWithInterfaceWithGetter("Bob")));

        assertEquals(3, tree.size());
        assertEquals("ID:Bob", tree.get("id").textValue());
        assertEquals("Bob", tree.get("name").textValue());
        assertEquals(999, tree.get("count").intValue());
    }

    // Provenance: RecordIgnoreGettersTest#testStaticGetter_NeverIncluded().
    void testStaticGetterNeverIncludedVpack() throws Exception {
        assertEquals(0, DEFAULT_MAPPER.readTree(DEFAULT_MAPPER.writeValueAsBytes(
                new EmptyWithStatic())).size());
        assertEquals(0, RESTRICTED_MAPPER.readTree(RESTRICTED_MAPPER.writeValueAsBytes(
                new EmptyWithStatic())).size());
    }

    // Provenance: RecordIgnoreGettersTest#testWithInferGettersFromComponentsOnlyFeature().
    void testWithInferGettersFromComponentsOnlyFeatureVpack() throws Exception {
        JsonNode tree = RESTRICTED_MAPPER.readTree(RESTRICTED_MAPPER.writeValueAsBytes(
                new RecordWithInterfaceWithGetter("Bob")));

        assertEquals(1, tree.size());
        assertEquals("Bob", tree.get("name").textValue());
        assertFalse(tree.has("id"));
        assertFalse(tree.has("count"));
    }
interface InterfaceWithGetter {
        String getId();
        String getName();
    }
@com.fasterxml.jackson.annotation.JsonPropertyOrder({"id", "name", "count"})
    record RecordWithInterfaceWithGetter(String name) implements InterfaceWithGetter {
        @Override
        public String getId() {
            return "ID:" + name;
        }

        @Override
        public String getName() {
            return name;
        }

        public int getCount() {
            return 999;
        }
    }
record EmptyWithStatic() {
        public static String getStaticValue() {
            return "static";
        }
    }
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record SensitiveRecord(String username, @JsonIgnore String internalRole) { }
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    static class SensitivePojo {
        public String username;
        @JsonIgnore
        public String internalRole;
    }
record RecordWithNonCanonicalConstructor(int id, String name, String email) {
        public RecordWithNonCanonicalConstructor(int id, String email) {
            this(id, "NonCanonicalConstructor", email);
        }
    }
record RecordWithMultiValueCanonAndSingleValueAltConstructor(int id, String name) {
        public RecordWithMultiValueCanonAndSingleValueAltConstructor(int id) {
            this(id, "AltConstructor");
        }
    }
record RecordWithSingleValueCanonAndMultiValueAltConstructor(String name) {
        public RecordWithSingleValueCanonAndMultiValueAltConstructor(String name, String email) {
            this("AltConstructor");
        }
    }

    void __invoke_testSerializeIgnoreInterfaceGetterUsingVisibilityConfigVpack() throws Exception {
        try {
            testSerializeIgnoreInterfaceGetterUsingVisibilityConfigVpack();
        } finally {
        }
    }


    void __invoke_testSerializeIgnoreInterfaceGetterWithoutUsingVisibilityConfigVpack() throws Exception {
        try {
            testSerializeIgnoreInterfaceGetterWithoutUsingVisibilityConfigVpack();
        } finally {
        }
    }


    void __invoke_testStaticGetterNeverIncludedVpack() throws Exception {
        try {
            testStaticGetterNeverIncludedVpack();
        } finally {
        }
    }


    void __invoke_testWithInferGettersFromComponentsOnlyFeatureVpack() throws Exception {
        try {
            testWithInferGettersFromComponentsOnlyFeatureVpack();
        } finally {
        }
    }

}
