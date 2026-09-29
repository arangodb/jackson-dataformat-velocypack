package tools.jackson.databind.ext.jdk8;

import java.io.Serializable;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.core.type.TypeReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0377F1 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] EMPTY_STRING = VPackWireFixtureTest.hex("40");
private static final byte[] UNWRAPPED_OPTIONAL_PARENT = VPackWireFixtureTest.hex(
            "0b 10 01 47 58 58 2e 6e 61 6d 65 43 42 6f 62 03");
private static final byte[] POLYMORPHIC_FOO = VPackWireFixtureTest.hex(
            "0b 34 02 44 6e 61 6d 65 4c 66 6f 6f 20 73 74 72 61 74 65 67 79 "
          + "48 73 74 72 61 74 65 67 79 0b 14 02 44 74 79 70 65 43 46 6f 6f "
          + "43 66 6f 6f 28 2a 0c 03 03 15");
private static final byte[] POLYMORPHIC_BAR = VPackWireFixtureTest.hex(
            "0b 33 02 44 6e 61 6d 65 4c 62 61 72 20 73 74 72 61 74 65 67 79 "
          + "48 73 74 72 61 74 65 67 79 0b 13 02 44 74 79 70 65 43 42 61 72 "
          + "43 62 61 72 1a 0c 03 03 15");
private static final byte[] POLYMORPHIC_BAZ = VPackWireFixtureTest.hex(
            "0b 3f 02 44 6e 61 6d 65 4c 62 61 72 20 73 74 72 61 74 65 67 79 "
          + "48 73 74 72 61 74 65 67 79 0b 1f 02 44 74 79 70 65 43 42 61 7a "
          + "43 62 61 7a 4c 68 65 6c 6c 6f 20 77 6f 72 6c 64 21 0c 03 03 15");
private static final byte[] ABSTRACT_OPTIONAL_INTEGER = VPackWireFixtureTest.hex(
            "0b 0b 01 45 76 61 6c 75 65 35 03");

    // Provenance: OptionalWithEmptyTest#testOptionalFromEmpty().
    void testOptionalFromEmptyVpack() throws Exception {
        Optional<?> value = MAPPER.readValue(EMPTY_STRING,
                new TypeReference<Optional<Integer>>() { });
        assertNotNull(value);
        assertFalse(value.isPresent());
    }
private PolymorphicValues readPolymorphic(byte[] input) throws Exception {
        ContainerA optional = MAPPER.readValue(input, ContainerA.class);
        ContainerB plain = MAPPER.readValue(input, ContainerB.class);
        assertNotNull(optional);
        assertNotNull(plain);
        assertNotNull(optional.strategy);
        assertTrue(optional.strategy.isPresent());
        assertNotNull(plain.strategy);
        return new PolymorphicValues(optional.name.get(), optional.strategy.get(),
                plain.strategy);
    }
private record PolymorphicValues(String name, Strategy optionalStrategy,
            Strategy strategy) { }
static class OptionalParent {
        @JsonUnwrapped(prefix = "XX.")
        public Optional<Child> child = Optional.of(new Child());
    }
static class Child {
        public String name = "Bob";
    }
static class ContainerA {
        @JsonProperty private Optional<String> name = Optional.empty();
        @JsonProperty private Optional<Strategy> strategy = Optional.empty();
    }
static class ContainerB {
        @JsonProperty private Optional<String> name = Optional.empty();
        @JsonProperty private Strategy strategy;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({ @JsonSubTypes.Type(name = "Foo", value = Foo.class),
        @JsonSubTypes.Type(name = "Bar", value = Bar.class),
        @JsonSubTypes.Type(name = "Baz", value = Baz.class) })
    interface Strategy { }
static class Foo implements Strategy {
        @JsonProperty private final int foo;

        @com.fasterxml.jackson.annotation.JsonCreator
        Foo(@JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }
static class Bar implements Strategy {
        @JsonProperty private final boolean bar;

        @com.fasterxml.jackson.annotation.JsonCreator
        Bar(@JsonProperty("bar") boolean bar) {
            this.bar = bar;
        }
    }
static class Baz implements Strategy {
        @JsonProperty private final String baz;

        @com.fasterxml.jackson.annotation.JsonCreator
        Baz(@JsonProperty("baz") String baz) {
            this.baz = baz;
        }
    }
static class AbstractOptional {
        @JsonDeserialize(contentAs = Integer.class)
        public Optional<Serializable> value;
    }

    void __invoke_testOptionalFromEmptyVpack() throws Exception {
        try {
            testOptionalFromEmptyVpack();
        } finally {
        }
    }

}
