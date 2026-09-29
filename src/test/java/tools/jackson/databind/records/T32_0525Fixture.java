package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0525Fixture {
private static final ObjectMapper MAPPER_DEFAULT = new VPackMapper();
private static final ObjectMapper MAPPER_RESTRICTED = VPackMapper.builder()
            .enable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
            .build();
private static final byte[] PERSON_ALICE_25 = VPackWireFixtureTest.hex(
            "0b 16 02 44 6e 61 6d 65 45 61 6c 69 63 65 "
          + "43 61 67 65 28 19 0e 03");

    // Provenance: RecordIgnoreGettersTest#testFeatureDisabledByDefault().
    void testFeatureDisabledByDefaultVpack() {
        assertFalse(MAPPER_DEFAULT.isEnabled(
                MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY));
    }

    // Provenance: RecordIgnoreGettersTest#testHelperGetterIncluded_FeatureDisabled().
    void testHelperGetterIncludedFeatureDisabledVpack() throws Exception {
        JsonNode tree = MAPPER_DEFAULT.readTree(
                MAPPER_DEFAULT.writeValueAsBytes(new PersonRecord("john", 30)));

        assertTrue(tree.has("displayName"));
        assertEquals("JOHN", tree.get("displayName").textValue());
        assertEquals("john", tree.get("name").textValue());
        assertEquals(30, tree.get("age").intValue());
    }

    // Provenance: RecordIgnoreGettersTest#testHelperGetterExcluded_FeatureEnabled().
    void testHelperGetterExcludedFeatureEnabledVpack() throws Exception {
        JsonNode tree = MAPPER_RESTRICTED.readTree(
                MAPPER_RESTRICTED.writeValueAsBytes(new PersonRecord("john", 30)));

        assertFalse(tree.has("displayName"));
        assertFalse(tree.toString().contains("JOHN"));
        assertEquals("john", tree.get("name").textValue());
        assertEquals(30, tree.get("age").intValue());
    }

    // Provenance: RecordIgnoreGettersTest#testInterfaceGetterExcluded_FeatureEnabled().
    void testInterfaceGetterExcludedFeatureEnabledVpack() throws Exception {
        JsonNode tree = MAPPER_RESTRICTED.readTree(
                MAPPER_RESTRICTED.writeValueAsBytes(new UserRecord("alice")));

        assertEquals("alice", tree.get("name").textValue());
        assertFalse(tree.has("id"));
    }

    // Provenance: RecordIgnoreGettersTest#testInterfaceGetterIncluded_FeatureDisabled().
    void testInterfaceGetterIncludedFeatureDisabledVpack() throws Exception {
        JsonNode tree = MAPPER_DEFAULT.readTree(
                MAPPER_DEFAULT.writeValueAsBytes(new UserRecord("alice")));

        assertEquals("alice", tree.get("name").textValue());
        assertEquals("ID:alice", tree.get("id").textValue());
    }

    // Provenance: RecordIgnoreGettersTest#testExplicitAnnotation_AlwaysWorks_FeatureEnabled().
    void testExplicitAnnotationAlwaysWorksFeatureEnabledVpack() throws Exception {
        JsonNode tree = MAPPER_RESTRICTED.readTree(
                MAPPER_RESTRICTED.writeValueAsBytes(new AnnotatedHelperRecord("test")));

        assertEquals("TEST", tree.get("display").textValue());
    }

    // Provenance: RecordIgnoreGettersTest#testExplicitAnnotation_AlwaysWorks_FeatureDisabled().
    void testExplicitAnnotationAlwaysWorksFeatureDisabledVpack() throws Exception {
        JsonNode tree = MAPPER_DEFAULT.readTree(
                MAPPER_DEFAULT.writeValueAsBytes(new AnnotatedHelperRecord("test")));

        assertEquals("TEST", tree.get("display").textValue());
    }

    // Provenance: RecordIgnoreGettersTest#testIsGetterHelper_FeatureEnabled().
    void testIsGetterHelperFeatureEnabledVpack() throws Exception {
        JsonNode tree = MAPPER_RESTRICTED.readTree(
                MAPPER_RESTRICTED.writeValueAsBytes(new BooleanHelperRecord("Special Case", true)));

        assertTrue(tree.has("active"));
        assertFalse(tree.has("special"));
    }

    // Provenance: RecordIgnoreGettersTest#testIsGetterHelper_FeatureDisabled().
    void testIsGetterHelperFeatureDisabledVpack() throws Exception {
        JsonNode tree = MAPPER_DEFAULT.readTree(
                MAPPER_DEFAULT.writeValueAsBytes(new BooleanHelperRecord("Special Case", true)));

        assertTrue(tree.has("active"));
        assertTrue(tree.has("special"));
        assertTrue(tree.get("special").booleanValue());
    }

    // Provenance: RecordIgnoreGettersTest#testRoundTrip_FeatureEnabled().
    void testRoundTripFeatureEnabledVpack() throws Exception {
        PersonRecord result = MAPPER_RESTRICTED.readValue(PERSON_ALICE_25, PersonRecord.class);

        assertNotNull(result);
        assertEquals(new PersonRecord("alice", 25), result);
        assertEquals("alice", result.name());
        assertEquals(25, result.age());
    }

    // Provenance: RecordIgnoreGettersTest#testFeatureConfiguration_ViaBuilder().
    void testFeatureConfigurationViaBuilderVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .enable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
                .build();

        assertTrue(mapper.isEnabled(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY));
        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(new PersonRecord("test", 1)));
        assertFalse(tree.has("displayName"));
    }

    // Provenance: RecordIgnoreGettersTest#testFeatureConfiguration_ExplicitDisable().
    void testFeatureConfigurationExplicitDisableVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY)
                .build();

        assertFalse(mapper.isEnabled(MapperFeature.INFER_RECORD_GETTERS_FROM_COMPONENTS_ONLY));
        JsonNode tree = mapper.readTree(mapper.writeValueAsBytes(new PersonRecord("test", 1)));
        assertTrue(tree.has("displayName"));
    }
record PersonRecord(String name, int age) {
        public String getDisplayName() {
            return name.toUpperCase();
        }
    }
interface Identifiable {
        String getId();
    }
record UserRecord(String name) implements Identifiable {
        @Override
        public String getId() {
            return "ID:" + name;
        }
    }
record AnnotatedHelperRecord(String name) {
        @JsonProperty("display")
        public String getDisplayName() {
            return name.toUpperCase();
        }
    }
record BooleanHelperRecord(String name, boolean active) {
        public boolean isSpecial() {
            return name.startsWith("Special");
        }
    }

    void __invoke_testFeatureDisabledByDefaultVpack() throws Exception {
        try {
            testFeatureDisabledByDefaultVpack();
        } finally {
        }
    }


    void __invoke_testHelperGetterIncludedFeatureDisabledVpack() throws Exception {
        try {
            testHelperGetterIncludedFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testHelperGetterExcludedFeatureEnabledVpack() throws Exception {
        try {
            testHelperGetterExcludedFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testInterfaceGetterExcludedFeatureEnabledVpack() throws Exception {
        try {
            testInterfaceGetterExcludedFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testInterfaceGetterIncludedFeatureDisabledVpack() throws Exception {
        try {
            testInterfaceGetterIncludedFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testExplicitAnnotationAlwaysWorksFeatureEnabledVpack() throws Exception {
        try {
            testExplicitAnnotationAlwaysWorksFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testExplicitAnnotationAlwaysWorksFeatureDisabledVpack() throws Exception {
        try {
            testExplicitAnnotationAlwaysWorksFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testIsGetterHelperFeatureEnabledVpack() throws Exception {
        try {
            testIsGetterHelperFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testIsGetterHelperFeatureDisabledVpack() throws Exception {
        try {
            testIsGetterHelperFeatureDisabledVpack();
        } finally {
        }
    }


    void __invoke_testRoundTripFeatureEnabledVpack() throws Exception {
        try {
            testRoundTripFeatureEnabledVpack();
        } finally {
        }
    }


    void __invoke_testFeatureConfigurationViaBuilderVpack() throws Exception {
        try {
            testFeatureConfigurationViaBuilderVpack();
        } finally {
        }
    }


    void __invoke_testFeatureConfigurationExplicitDisableVpack() throws Exception {
        try {
            testFeatureConfigurationExplicitDisableVpack();
        } finally {
        }
    }

}
