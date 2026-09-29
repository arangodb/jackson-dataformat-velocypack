package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.std.StdDeserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import tools.jackson.dataformat.velocypack.*;

class T32_0523F1 {
private static final byte[] EMPTY_OBJECT = VPackWireFixtureTest.hex("0a");
private static final byte[] DUPLICATE_RECORD = VPackWireFixtureTest.hex(
            "14 29 45 66 69 72 73 74 45 76 61 6c 75 65 "
          + "46 73 65 63 6f 6e 64 45 74 65 73 74 31 "
          + "45 66 69 72 73 74 46 76 61 6c 75 65 32 03");
private static final byte[] WRITE_ONLY_RECORD = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 66 6f 6f 03");
private static final byte[] ISSUE_5683 = VPackWireFixtureTest.hex(
            "14 0d 45 69 6e 6e 65 72 43 31 32 33 01");
private static final byte[] EXPLICIT_AND_IMPLICIT = VPackWireFixtureTest.hex(
            "14 23 47 69 64 5f 6f 6e 6c 79 28 7b "
          + "45 65 6d 61 69 6c 4f 62 6f 62 40 65 78 61 6d 70 6c 65 2e 63 6f 6d 02");
private static final byte[] CANONICAL_CREATOR_FAILURE = VPackWireFixtureTest.hex(
            "14 13 42 69 64 28 7b 44 6e 61 6d 65 45 42 6f 62 62 79 02");
private static final ObjectMapper CREATOR_MAPPER = VPackMapper.builder()
            .disable(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    // Provenance: RecordExplicitCreatorsTest#testDeserializeMultipleConstructorsRecord_WithExplicitAndImplicitParameterNames().
    void testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesVpack()
            throws Exception {
        RecordWithJsonPropertyAndImplicitPropertyWithoutJsonCreator value = CREATOR_MAPPER.readValue(
                EXPLICIT_AND_IMPLICIT, RecordWithJsonPropertyAndImplicitPropertyWithoutJsonCreator.class);
        assertEquals(123, value.id());
        assertEquals("bob@example.com", value.email());
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeMultipleConstructorsRecord_WithExplicitAndImplicitParameterNames_WithJsonCreator().
    void testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesWithJsonCreatorVpack()
            throws Exception {
        RecordWithJsonPropertyAndImplicitPropertyWithJsonCreator value = CREATOR_MAPPER.readValue(
                EXPLICIT_AND_IMPLICIT, RecordWithJsonPropertyAndImplicitPropertyWithJsonCreator.class);
        assertEquals(new RecordWithJsonPropertyAndImplicitPropertyWithJsonCreator(
                123, "bob@example.com"), value);
    }

    // Provenance: RecordExplicitCreatorsTest#testDeserializeUsingCanonicalConstructor_WhenJsonCreatorConstructorExists_WillFail().
    void testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack()
            throws Exception {
        try {
            CREATOR_MAPPER.readValue(CANONICAL_CREATOR_FAILURE,
                    RecordWithJsonPropertyWithJsonCreator.class);
            fail("should not pass");
        } catch (DatabindException e) {
            String message = e.getMessage();
            org.junit.jupiter.api.Assertions.assertTrue(message.contains("Unrecognized property \"id\""));
            org.junit.jupiter.api.Assertions.assertTrue(message.contains("RecordWithJsonPropertyWithJsonCreator"));
            org.junit.jupiter.api.Assertions.assertTrue(message.contains("id_only"));
        }
    }
@JsonDeserialize(using = Inner5683.Deser.class)
    record Inner5683(@JsonValue long value) {
        static class Deser extends StdDeserializer<Inner5683> {
            protected Deser() { super(Inner5683.class); }

            @Override
            public Inner5683 deserialize(JsonParser p, DeserializationContext ctxt) {
                return new Inner5683(p.readValueAs(Long.class));
            }
        }
    }
record Outer5683(Inner5683 inner) { }
record ExampleWriteOnly(
            @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String value) { }
record Record3906(String string, int integer) { }
@JsonAutoDetect(creatorVisibility = Visibility.NON_PRIVATE)
    record Record3906Annotated(String string, int integer) { }
record Record3906Creator(String string, int integer) {
        @JsonCreator
        Record3906Creator { }
    }
private record PrivateRecord3906(String string, int integer) { }
record DuplicatePropRecord4690(String first) { }
record RecordWithJsonPropertyAndImplicitPropertyWithoutJsonCreator(int id, String name, String email) {
        public RecordWithJsonPropertyAndImplicitPropertyWithoutJsonCreator(
                @JsonProperty("id_only") int id, @JsonProperty("email") String email) {
            this(id, "JsonPropertyConstructor", email);
        }
    }
record RecordWithJsonPropertyAndImplicitPropertyWithJsonCreator(int id, String name, String email) {
        @JsonCreator
        public RecordWithJsonPropertyAndImplicitPropertyWithJsonCreator(
                @JsonProperty("id_only") int id, @JsonProperty("email") String email) {
            this(id, "JsonPropertyConstructor", email);
        }
    }
record RecordWithJsonPropertyWithJsonCreator(int id, String name) {
        @JsonCreator
        public RecordWithJsonPropertyWithJsonCreator(@JsonProperty("id_only") int id) {
            this(id, "JsonCreatorConstructor");
        }
    }

    void __invoke_testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesWithJsonCreatorVpack() throws Exception {
        try {
            testDeserializeMultipleConstructorsRecordWithExplicitAndImplicitParameterNamesWithJsonCreatorVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack() throws Exception {
        try {
            testDeserializeUsingCanonicalConstructorWhenJsonCreatorConstructorExistsWillFailVpack();
        } finally {
        }
    }

}
