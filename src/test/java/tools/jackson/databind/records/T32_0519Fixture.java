package tools.jackson.databind.records;

import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.InjectableValues;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.util.Converter;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0519Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: RecordBasicsTest#recordWithRenamedPropertyRoundTrips().
    void recordWithRenamedPropertyRoundTripsVpack() throws Exception {
        RenamedRecord5967 original = new RenamedRecord5967("someValue");
        java.util.Map<?, ?> wire = MAPPER.readValue(
                MAPPER.writeValueAsBytes(original), java.util.Map.class);
        assertEquals("someValue", wire.get("renamedProp"));
        assertEquals(1, wire.size());

        RenamedRecord5967 result = MAPPER.readValue(
                VPackWireFixtureTest.hex("14 19 4b 72 65 6e 61 6d 65 64 50 72 6f 70 "
                        + "49 73 6f 6d 65 56 61 6c 75 65 01"),
                RenamedRecord5967.class);
        assertEquals("someValue", result.prop());
    }

    // Provenance: RecordBasicsTest#testDeserializeConstructorInjectRecord4218().
    void testDeserializeConstructorInjectRecord4218Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(RecordWithConstructorInject.class)
                .with(new InjectableValues.Std().addValue(String.class, "Bob"));
        RecordWithConstructorInject value = reader.readValue(
                VPackWireFixtureTest.hex("0b 09 01 42 69 64 28 7b 03"));
        assertEquals(new RecordWithConstructorInject(123, "Bob"), value);
    }

    // Provenance: RecordBasicsTest#testDeserializeEmptyRecord().
    void testDeserializeEmptyRecordVpack() throws Exception {
        assertEquals(new EmptyRecord(), MAPPER.readValue(new byte[] { 0x0a }, EmptyRecord.class));
    }

    // Provenance: RecordBasicsTest#testDeserializeHeaderInjectRecord4218().
    void testDeserializeHeaderInjectRecord4218Vpack() throws Exception {
        ObjectReader reader = MAPPER.readerFor(RecordWithHeaderInject.class)
                .with(new InjectableValues.Std().addValue(String.class, "Bob"));
        assertNotNull(reader.readValue(
                VPackWireFixtureTest.hex("0b 09 01 42 69 64 28 7b 03")));
    }

    // Provenance: RecordBasicsTest#testDeserializeJsonDeserializeRecord().
    void testDeserializeJsonDeserializeRecordVpack() throws Exception {
        RecordWithJsonDeserialize value = MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 19 02 42 69 64 28 7b 44 6e 61 6d 65 "
                        + "49 20 20 20 42 6f 62 20 20 20 03 08"),
                RecordWithJsonDeserialize.class);
        assertEquals(new RecordWithJsonDeserialize(123, "Bob"), value);
    }

    // Provenance: RecordBasicsTest#testDeserializeJsonRename().
    void testDeserializeJsonRenameVpack() throws Exception {
        assertEquals(new RecordWithRename(123, "Bob"), MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 15 02 42 69 64 28 7b 46 72 65 6e 61 6d 65 "
                        + "43 42 6f 62 03 08"),
                RecordWithRename.class));
    }

    // Provenance: RecordBasicsTest#testDeserializePrivateTextRecord().
    void testDeserializePrivateTextRecordVpack() throws Exception {
        assertEquals(new PrivateTextRecord4175("anything"), MAPPER.readValue(
                VPackWireFixtureTest.hex("0b 12 01 44 74 65 78 74 48 61 6e 79 74 68 69 6e 67 03"),
                PrivateTextRecord4175.class));
    }
record EmptyRecord() { }
record RecordWithRename(int id, @JsonProperty("rename") String name) { }
record RecordWithHeaderInject(int id, @JacksonInject String name) { }
record RecordWithConstructorInject(int id, String name) {
        RecordWithConstructorInject(int id, @JacksonInject String name) {
            this.id = id;
            this.name = name;
        }
    }
record RecordWithJsonDeserialize(int id,
            @JsonDeserialize(converter = StringTrimmer.class) String name) { }
private static record PrivateTextRecord4175(String text) { }
public record RenamedRecord5967(@JsonProperty("renamedProp") String prop) { }
public static class StringTrimmer implements Converter<String, String> {
        @Override
        public String convert(DeserializationContext ctxt, String value) {
            return value.trim();
        }

        @Override
        public String convert(SerializationContext ctxt, String value) {
            return value.trim();
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "@c")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@id")
    interface BaseEntity4014 {
        @JsonProperty("@id") Integer getId();
    }
static class CreatorFoo implements BaseEntity4014 {
        private final Integer id;
        @JsonCreator CreatorFoo(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
    }
static class FooWithSetter implements BaseEntity4014 {
        private Integer id;
        public FooWithSetter() { }
        @JsonProperty("@id") public Integer getId() { return id; }
        @JsonProperty("@id") public void setId(Integer id) { this.id = id; }
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY,
            property = "@c")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@id")
    interface WrapperArrayEntity { @JsonProperty("@id") Integer getId(); }
static class WrapFoo implements WrapperArrayEntity {
        private final Integer id;
        @JsonCreator WrapFoo(@JsonProperty("@id") Integer id) { this.id = id; }
        @JsonProperty("@id") public Integer getId() { return id; }
    }
static class Container5872 { @JsonProperty public List<Base5872> list; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(name = "Derived", value = Derived5872.class))
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
    interface Base5872 { }
static class Derived5872 implements Base5872 { @JsonProperty public String a; }
static class ContainerInterfaceWithBuilder5872 {
        @JsonProperty public List<BaseWithBuilder5872> list;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes(@JsonSubTypes.Type(name = "DerivedWithBuilder", value = DerivedWithBuilder5872.class))
    @JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
    interface BaseWithBuilder5872 { }
@tools.jackson.databind.annotation.JsonDeserialize(builder = DerivedWithBuilder5872.DerivedBuilder.class)
    static class DerivedWithBuilder5872 implements BaseWithBuilder5872 {
        @JsonProperty public String a;
        DerivedWithBuilder5872(String a) { this.a = a; }
        @tools.jackson.databind.annotation.JsonPOJOBuilder(withPrefix = "", buildMethodName = "build")
        static class DerivedBuilder {
            private String a;
            @JsonProperty DerivedBuilder a(String value) { this.a = value; return this; }
            DerivedWithBuilder5872 build() { return new DerivedWithBuilder5872(a); }
        }
    }

    void __invoke_recordWithRenamedPropertyRoundTripsVpack() throws Exception {
        try {
            recordWithRenamedPropertyRoundTripsVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeConstructorInjectRecord4218Vpack() throws Exception {
        try {
            testDeserializeConstructorInjectRecord4218Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeEmptyRecordVpack() throws Exception {
        try {
            testDeserializeEmptyRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeHeaderInjectRecord4218Vpack() throws Exception {
        try {
            testDeserializeHeaderInjectRecord4218Vpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonDeserializeRecordVpack() throws Exception {
        try {
            testDeserializeJsonDeserializeRecordVpack();
        } finally {
        }
    }


    void __invoke_testDeserializeJsonRenameVpack() throws Exception {
        try {
            testDeserializeJsonRenameVpack();
        } finally {
        }
    }


    void __invoke_testDeserializePrivateTextRecordVpack() throws Exception {
        try {
            testDeserializePrivateTextRecordVpack();
        } finally {
        }
    }

}
