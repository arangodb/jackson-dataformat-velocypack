package tools.jackson.databind.records;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.MapperConfig;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.introspect.AnnotatedClass;
import tools.jackson.databind.introspect.NopAnnotationIntrospector;
import tools.jackson.databind.introspect.VisibilityChecker;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0523F0 {
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

    // Provenance: RecordDeserializationTest#testDuplicatePropertyDeserialization2().
    void testDuplicatePropertyDeserialization2Vpack() throws Exception {
        DuplicatePropRecord4690 result = new VPackMapper().readValue(
                DUPLICATE_RECORD, DuplicatePropRecord4690.class);
        assertEquals("value2", result.first());
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordCreatorsVisible().
    void testEmptyJsonToRecordCreatorsVisibleVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .changeDefaultVisibility(vc ->
                        vc.withVisibility(PropertyAccessor.CREATOR, Visibility.NON_PRIVATE))
                .build();
        assertEquals(new Record3906(null, 0), mapper.readValue(EMPTY_OBJECT, Record3906.class));
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordDirectAutoDetectConfig().
    void testEmptyJsonToRecordDirectAutoDetectConfigVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertEquals(new Record3906Annotated(null, 0),
                mapper.readValue(EMPTY_OBJECT, Record3906Annotated.class));
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordJsonCreator().
    void testEmptyJsonToRecordJsonCreatorVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertEquals(new Record3906Creator(null, 0),
                mapper.readValue(EMPTY_OBJECT, Record3906Creator.class));
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordUsingModule().
    void testEmptyJsonToRecordUsingModuleVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.insertAnnotationIntrospector(new NopAnnotationIntrospector() {
                    @Override
                    public VisibilityChecker findAutoDetectVisibility(MapperConfig<?> cfg,
                            AnnotatedClass ac, VisibilityChecker checker) {
                        return ac.getType().isRecordType()
                                ? checker.withCreatorVisibility(JsonAutoDetect.Visibility.NON_PRIVATE)
                                : checker;
                    }
                });
            }
        }).disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
        assertEquals(new Record3906(null, 0), mapper.readValue(EMPTY_OBJECT, Record3906.class));
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordUsingModuleOther().
    void testEmptyJsonToRecordUsingModuleOtherVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder().addModule(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.insertAnnotationIntrospector(new NopAnnotationIntrospector() {
                    @Override
                    public VisibilityChecker findAutoDetectVisibility(MapperConfig<?> cfg,
                            AnnotatedClass ac, VisibilityChecker checker) {
                        if (ac.getType() == null || !ac.getType().isRecordType()) {
                            return checker;
                        }
                        return checker.withCreatorVisibility(Visibility.ANY);
                    }
                });
            }
        }).disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES).build();
        assertEquals(new Record3906(null, 0), mapper.readValue(EMPTY_OBJECT, Record3906.class));
        assertEquals(new PrivateRecord3906(null, 0),
                mapper.readValue(EMPTY_OBJECT, PrivateRecord3906.class));
    }

    // Provenance: RecordDeserializationTest#testEmptyJsonToRecordWorkAround().
    void testEmptyJsonToRecordWorkAroundVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(PropertyAccessor.ALL, Visibility.NONE)
                        .withVisibility(PropertyAccessor.CREATOR, Visibility.ANY))
                .build();
        assertEquals(new Record3906(null, 0), mapper.readValue(EMPTY_OBJECT, Record3906.class));
    }

    // Provenance: RecordDeserializationTest#testIssue5683().
    void testIssue5683Vpack() throws Exception {
        ObjectMapper mapper = new VPackMapper();
        JsonNode tree = mapper.readTree(ISSUE_5683);

        Outer5683 value = mapper.readValue(ISSUE_5683, Outer5683.class);
        assertEquals(123L, value.inner().value());

        value = mapper.treeToValue(tree, Outer5683.class);
        assertEquals(123L, value.inner().value());

        value = mapper.reader().treeToValue(tree, Outer5683.class);
        assertEquals(123L, value.inner().value());
    }

    // Provenance: RecordDeserializationTest#testRecordWithWriteOnly3897().
    void testRecordWithWriteOnly3897Vpack() throws Exception {
        ExampleWriteOnly result = new VPackMapper().readValue(WRITE_ONLY_RECORD,
                ExampleWriteOnly.class);
        assertNotNull(result);
        assertEquals("foo", result.value());
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

    void __invoke_testDuplicatePropertyDeserialization2Vpack() throws Exception {
        try {
            testDuplicatePropertyDeserialization2Vpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordCreatorsVisibleVpack() throws Exception {
        try {
            testEmptyJsonToRecordCreatorsVisibleVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordDirectAutoDetectConfigVpack() throws Exception {
        try {
            testEmptyJsonToRecordDirectAutoDetectConfigVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordJsonCreatorVpack() throws Exception {
        try {
            testEmptyJsonToRecordJsonCreatorVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordUsingModuleVpack() throws Exception {
        try {
            testEmptyJsonToRecordUsingModuleVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordUsingModuleOtherVpack() throws Exception {
        try {
            testEmptyJsonToRecordUsingModuleOtherVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonToRecordWorkAroundVpack() throws Exception {
        try {
            testEmptyJsonToRecordWorkAroundVpack();
        } finally {
        }
    }


    void __invoke_testIssue5683Vpack() throws Exception {
        try {
            testIssue5683Vpack();
        } finally {
        }
    }


    void __invoke_testRecordWithWriteOnly3897Vpack() throws Exception {
        try {
            testRecordWithWriteOnly3897Vpack();
        } finally {
        }
    }

}
