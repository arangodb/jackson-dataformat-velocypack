package tools.jackson.databind.jsontype.ext;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0446F0 {
private static final byte[] SCALAR_DATE = VPackWireFixtureTest.hex(
            "14 15 45 76 61 6c 75 65 28 7b 44 74 79 70 65 44 64 61 74 65 02");
private static final byte[] VIEWED_CONTAINER = VPackWireFixtureTest.hex(
            "14 29 45 6c 61 62 65 6c 43 62 6f 78 47 70 65 74 54 79 70 65 43 64 6f 67 "
          + "43 70 65 74 14 0c 44 6e 61 6d 65 43 52 65 78 01 03");
private static final byte[] NAR_ALIAS = VPackWireFixtureTest.hex(
            "14 1c 42 69 64 43 4e 41 52 48 6e 61 72 56 61 6c 75 65 "
          + "14 09 43 64 61 72 41 64 01 02");
private static final byte[] FOO_ALIAS = VPackWireFixtureTest.hex(
            "14 1c 42 69 64 43 46 4f 4f 48 66 6f 6f 56 61 6c 75 65 "
          + "14 09 43 62 61 72 41 62 01 02");
private static final byte[] CANONICAL_TARGET = VPackWireFixtureTest.hex(
            "14 1a 42 69 64 43 4e 41 52 46 74 61 72 67 65 74 "
          + "14 09 43 64 61 72 41 64 01 02");
private static final byte[] UNKNOWN_ALIAS = VPackWireFixtureTest.hex(
            "14 1e 42 69 64 43 4e 41 52 4a 62 6f 67 75 73 56 61 6c 75 65 "
          + "14 09 43 64 61 72 41 64 01 02");
private final ObjectMapper mapper = new VPackMapper();
private final ObjectMapper viewMapper = VPackMapper.builder()
            .enable(MapperFeature.DEFAULT_VIEW_INCLUSION).build();

    // Provenance: ExternalTypeIdTest#testWithScalar118().
    void testWithScalar118Vpack() throws Exception {
        ExternalTypeWithNonPOJO input = new ExternalTypeWithNonPOJO(new Date(123L));
        byte[] encoded = mapper.writeValueAsBytes(input);
        ExternalTypeWithNonPOJO roundTrip = mapper.readValue(encoded,
                ExternalTypeWithNonPOJO.class);
        assertNotNull(roundTrip.value);
        assertInstanceOf(Date.class, roundTrip.value);

        ExternalTypeWithNonPOJO literal = mapper.readValue(SCALAR_DATE,
                ExternalTypeWithNonPOJO.class);
        assertInstanceOf(Date.class, literal.value);
        assertEquals(123L, ((Date) literal.value).getTime());
    }
static class ExternalTypeWithNonPOJO {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type", visible = true, defaultImpl = String.class)
        @JsonSubTypes(@JsonSubTypes.Type(value = Date.class, name = "date"))
        public Object value;

        public ExternalTypeWithNonPOJO() { }
        ExternalTypeWithNonPOJO(Object value) { this.value = value; }
    }
static class Views {
        static class Public { }
        static class Internal { }
    }
static class Animal {
        public String name;
    }
static class Dog extends Animal { }
static class UnknownAnimal extends Animal { }
static class ViewContainer {
        @JsonView(Views.Internal.class)
        public String petType;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "petType", visible = true, defaultImpl = UnknownAnimal.class)
        @JsonSubTypes(@JsonSubTypes.Type(value = Dog.class, name = "dog"))
        @JsonView(Views.Internal.class)
        public Animal pet;

        public String label;

        @JsonCreator
        ViewContainer(@JsonProperty("label") String label) { this.label = label; }
    }
static class Nar {
        public String dar;
    }
static class Foo {
        public String bar;
    }
static class AliasContainer {
        public String id;

        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "id")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Foo.class, name = "FOO"),
                @JsonSubTypes.Type(value = Nar.class, name = "NAR")
        })
        @JsonAlias({ "fooValue", "narValue" })
        public Object target;
    }
static class CreatorAliasContainer {
        public final String id;
        public final Object target;

        @JsonCreator
        CreatorAliasContainer(@JsonProperty("id") String id,
                @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                        include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "id")
                @JsonSubTypes({
                        @JsonSubTypes.Type(value = Foo.class, name = "FOO"),
                        @JsonSubTypes.Type(value = Nar.class, name = "NAR")
                })
                @JsonAlias({ "fooValue", "narValue" })
                @JsonProperty("target") Object target) {
            this.id = id;
            this.target = target;
        }
    }

    void __invoke_testWithScalar118Vpack() throws Exception {
        try {
            testWithScalar118Vpack();
        } finally {
        }
    }

}
