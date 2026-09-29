package tools.jackson.dataformat.velocypack;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Annotation-driven databind polymorphism and identity using ordinary VPack values. */
class VPackPolymorphicDatabindTest {
    private final VPackMapper mapper = new VPackMapper();

    @Test
    // Provenance: databind/jsontype/TestTypeNames#testRoundTrip, adapted from JSON text
    // to VPack objects; the type id remains a normal property value.
    void propertyTypeInfoRoundTripsSubtype() throws Exception {
        PropertyEnvelope input = new PropertyEnvelope(new PropertyDog("Rex", 3));

        PropertyEnvelope output = mapper.readValue(mapper.writeValueAsBytes(input),
                PropertyEnvelope.class);

        assertEquals(input, output);
        assertEquals(PropertyDog.class, output.animal.getClass());
        assertEquals("dog", ((java.util.Map<?, ?>) mapper.readValue(
                mapper.writeValueAsBytes(input), java.util.Map.class).get("animal")).get("kind"));
    }

    @Test
    // Provenance: databind/jsontype/TestTypeNames#testRoundTripMap, adapted to a
    // wrapper-object container; this is not a native VPack type-ID channel.
    void wrapperObjectTypeInfoRoundTripsSubtype() throws Exception {
        WrapperEnvelope input = new WrapperEnvelope(new WrapperCat("Maine Coon", true));

        WrapperEnvelope output = mapper.readValue(mapper.writeValueAsBytes(input),
                WrapperEnvelope.class);

        assertEquals(input, output);
        assertEquals(WrapperCat.class, output.animal.getClass());
        java.util.Map<?, ?> wire = mapper.readValue(mapper.writeValueAsBytes(input),
                java.util.Map.class);
        assertEquals(true, ((java.util.Map<?, ?>) wire.get("animal")).containsKey("cat"));
    }

    @Test
    // Provenance: databind/interop/KotlinIssueGH54JsonIdentityTest#testDeserWithIdentityInfo.
    // PropertyGenerator makes both the id and the later reference ordinary values.
    void propertyObjectIdentityUsesOrdinaryIdAndReferenceValues() throws Exception {
        IdentityNode shared = new IdentityNode("shared");
        IdentityEnvelope input = new IdentityEnvelope(shared, shared);

        byte[] encoded = mapper.writeValueAsBytes(input);
        java.util.Map<?, ?> wire = mapper.readValue(encoded, java.util.Map.class);
        assertEquals("shared", wire.get("second"));

        IdentityEnvelope output = mapper.readValue(encoded, IdentityEnvelope.class);
        assertSame(output.first, output.second);
        assertEquals("shared", output.first.id);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,
            property = "kind")
    @JsonSubTypes(@JsonSubTypes.Type(value = PropertyDog.class, name = "dog"))
    abstract static class PropertyAnimal {
        public String name;

        PropertyAnimal() { }
        PropertyAnimal(String name) { this.name = name; }
    }

    static final class PropertyDog extends PropertyAnimal {
        public int bark;

        PropertyDog() { }
        PropertyDog(String name, int bark) {
            super(name);
            this.bark = bark;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof PropertyDog that && bark == that.bark
                    && name.equals(that.name);
        }

        @Override
        public int hashCode() { return 31 * name.hashCode() + bark; }
    }

    record PropertyEnvelope(PropertyAnimal animal) { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
    @JsonSubTypes(@JsonSubTypes.Type(value = WrapperCat.class, name = "cat"))
    abstract static class WrapperAnimal {
        public String name;

        WrapperAnimal() { }
        WrapperAnimal(String name) { this.name = name; }
    }

    static final class WrapperCat extends WrapperAnimal {
        public boolean purrs;

        WrapperCat() { }
        WrapperCat(String name, boolean purrs) {
            super(name);
            this.purrs = purrs;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof WrapperCat that && purrs == that.purrs
                    && name.equals(that.name);
        }

        @Override
        public int hashCode() { return 31 * name.hashCode() + (purrs ? 1 : 0); }
    }

    record WrapperEnvelope(WrapperAnimal animal) { }

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    static final class IdentityNode {
        public String id;

        IdentityNode() { }
        IdentityNode(String id) { this.id = id; }
    }

    record IdentityEnvelope(IdentityNode first, IdentityNode second) { }
}
