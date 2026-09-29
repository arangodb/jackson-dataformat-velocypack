package tools.jackson.databind.objectid;

import java.util.List;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.util.Converter;
import tools.jackson.databind.type.TypeFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import tools.jackson.dataformat.velocypack.*;

class T32_0519Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: ObjectIdWithTypeInfo4014Test#testSimpleDeserializationViaInterface().
    void testSimpleDeserializationViaInterfaceVpack() throws Exception {
        FooWithSetter input = new FooWithSetter();
        input.setId(1);

        BaseEntity4014 result0 = MAPPER.readValue(
                MAPPER.writeValueAsBytes(input), BaseEntity4014.class);
        FooWithSetter result = assertInstanceOf(FooWithSetter.class, result0);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testSimpleDeserializationWithCreator().
    void testSimpleDeserializationWithCreatorVpack() throws Exception {
        BaseEntity4014 result0 = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new CreatorFoo(1)), BaseEntity4014.class);
        CreatorFoo result = assertInstanceOf(CreatorFoo.class, result0);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testSimpleDeserializationWrapperArray().
    void testSimpleDeserializationWrapperArrayVpack() throws Exception {
        WrapperArrayEntity result0 = MAPPER.readValue(
                MAPPER.writeValueAsBytes(new WrapFoo(1)), WrapperArrayEntity.class);
        WrapFoo result = assertInstanceOf(WrapFoo.class, result0);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testObjectIdWithInterfaceBaseType5872().
    void testObjectIdWithInterfaceBaseType5872Vpack() throws Exception {
        // Independent compact VPack fixture for {list:[{type,id,a},"id1"]}.
        byte[] fixture = VPackWireFixtureTest.hex(
                "14 2e 44 6c 69 73 74 13 26 14 1f "
              + "45 40 74 79 70 65 47 44 65 72 69 76 65 64 "
              + "43 40 69 64 43 69 64 31 41 61 43 66 6f 6f 03 "
              + "43 69 64 31 02 01");
        Container5872 container = MAPPER.readValue(fixture, Container5872.class);
        assertNotNull(container);
        assertNotNull(container.list);
        assertEquals(2, container.list.size());

        Base5872 first = container.list.get(0);
        assertEquals(Derived5872.class, first.getClass());
        assertEquals("foo", ((Derived5872) first).a);
        assertSame(first, container.list.get(1));
    }

    // Provenance: ObjectIdWithTypeInfo4014Test#testObjectIdWithInterfaceAndBuilder5872().
    void testObjectIdWithInterfaceAndBuilder5872Vpack() throws Exception {
        // Independent compact VPack fixture for {list:[{type,id,a},"id1"]}.
        byte[] fixture = VPackWireFixtureTest.hex(
                "14 39 44 6c 69 73 74 13 31 14 2a "
              + "45 40 74 79 70 65 52 44 65 72 69 76 65 64 57 69 74 68 42 75 69 6c 64 65 72 "
              + "43 40 69 64 43 69 64 31 41 61 43 66 6f 6f 03 "
              + "43 69 64 31 02 01");
        ContainerInterfaceWithBuilder5872 container = MAPPER.readValue(
                fixture, ContainerInterfaceWithBuilder5872.class);
        assertNotNull(container);
        assertNotNull(container.list);
        assertEquals(2, container.list.size());

        BaseWithBuilder5872 first = container.list.get(0);
        assertEquals(DerivedWithBuilder5872.class, first.getClass());
        assertEquals("foo", ((DerivedWithBuilder5872) first).a);
        assertSame(first, container.list.get(1));
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

    void __invoke_testSimpleDeserializationViaInterfaceVpack() throws Exception {
        try {
            testSimpleDeserializationViaInterfaceVpack();
        } finally {
        }
    }


    void __invoke_testSimpleDeserializationWithCreatorVpack() throws Exception {
        try {
            testSimpleDeserializationWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testSimpleDeserializationWrapperArrayVpack() throws Exception {
        try {
            testSimpleDeserializationWrapperArrayVpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithInterfaceBaseType5872Vpack() throws Exception {
        try {
            testObjectIdWithInterfaceBaseType5872Vpack();
        } finally {
        }
    }


    void __invoke_testObjectIdWithInterfaceAndBuilder5872Vpack() throws Exception {
        try {
            testObjectIdWithInterfaceAndBuilder5872Vpack();
        } finally {
        }
    }

}
