package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;
import tools.jackson.databind.cfg.ConstructorDetector;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0526F2 {
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

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructorsRecord_WithImplicitParameterNames_WillIgnoreNonCanonicalConstructor().
    void testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillIgnoreNonCanonicalConstructorVpack()
            throws Exception {
        RecordWithNonCanonicalConstructor value = DEFAULT_MAPPER.readValue(
                ID_EMAIL, RecordWithNonCanonicalConstructor.class);

        assertEquals(new RecordWithNonCanonicalConstructor(123, null, "bob@example.com"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructorsRecord_WithImplicitParameterNames_WillUseCanonicalConstructor().
    void testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillUseCanonicalConstructorVpack()
            throws Exception {
        RecordWithNonCanonicalConstructor value = DEFAULT_MAPPER.readValue(
                ID_NAME_EMAIL, RecordWithNonCanonicalConstructor.class);

        assertEquals(new RecordWithNonCanonicalConstructor(123, "Bob", "bob@example.com"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructors_UsingMultiValueCanonicalConstructor().
    void testDeserializeMultipleConstructorsUsingMultiValueCanonicalConstructorVpack()
            throws Exception {
        RecordWithMultiValueCanonAndSingleValueAltConstructor value = PROPERTIES_MAPPER.readValue(
                ID_NAME, RecordWithMultiValueCanonAndSingleValueAltConstructor.class);

        assertEquals(new RecordWithMultiValueCanonAndSingleValueAltConstructor(123, "Bob"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructors_UsingSingleValueCanonicalConstructor().
    void testDeserializeMultipleConstructorsUsingSingleValueCanonicalConstructorVpack()
            throws Exception {
        RecordWithSingleValueCanonAndMultiValueAltConstructor value = PROPERTIES_MAPPER.readValue(
                NAME_BOB, RecordWithSingleValueCanonAndMultiValueAltConstructor.class);

        assertEquals(new RecordWithSingleValueCanonAndMultiValueAltConstructor("Bob"), value);
    }

    // Provenance: RecordImplicitCreatorsTest#testDeserializeMultipleConstructors_WillIgnoreMultiValueAltConstructor().
    void testDeserializeMultipleConstructorsWillIgnoreMultiValueAltConstructorVpack()
            throws Exception {
        assertEquals(new RecordWithSingleValueCanonAndMultiValueAltConstructor("Bob"),
                PROPERTIES_MAPPER.readValue(NAME_EMAIL,
                        RecordWithSingleValueCanonAndMultiValueAltConstructor.class));
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

    void __invoke_testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillIgnoreNonCanonicalConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillIgnoreNonCanonicalConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillUseCanonicalConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsRecordWithImplicitParameterNamesWillUseCanonicalConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeMultipleConstructorsUsingMultiValueCanonicalConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsUsingMultiValueCanonicalConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeMultipleConstructorsUsingSingleValueCanonicalConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsUsingSingleValueCanonicalConstructorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeMultipleConstructorsWillIgnoreMultiValueAltConstructorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsWillIgnoreMultiValueAltConstructorVpack();
        } finally {
        }
    }

}
