package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0606Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper JSON = JsonMapper.builder().build();
private static final ObjectMapper EMPTY_AS_OBJECT = VPackMapper.builder()
            .disable(DeserializationFeature.USE_NULL_FOR_EMPTY_UNWRAPPED).build();
private static final byte[] SIMPLE = VPackWireFixtureTest.hex(
            "14 14 44 6e 61 6d 65 44 54 61 74 75 41 79 37 41 78 20 f3 03");
private static final byte[] CREATOR = VPackWireFixtureTest.hex(
            "14 13 41 78 31 41 79 32 44 6e 61 6d 65 44 54 61 74 75 03");
private static final byte[] PARTIAL = VPackWireFixtureTest.hex(
            "14 0b 41 73 45 76 61 6c 75 65 01");
private static final byte[] CREATOR_FIELDS = VPackWireFixtureTest.hex(
            "14 3e 49 75 6e 72 65 6c 61 74 65 64 4e 75 6e 72 65 6c 61 74 65 64 56 61 6c 75 65 "
          + "49 70 72 6f 70 65 72 74 79 31 46 76 61 6c 75 65 31 "
          + "49 70 72 6f 70 65 72 74 79 32 46 76 61 6c 75 65 32 03");
private static final byte[] PREFIXED = VPackWireFixtureTest.hex(
            "14 15 44 6e 61 6d 65 44 41 78 65 6c 42 5f 78 34 42 5f 79 37 03");
private static final byte[] RECURSIVE = VPackWireFixtureTest.hex(
            "14 30 44 6e 61 6d 65 43 42 6f 62 43 61 67 65 28 2d 4a 63 68 69 6c 64 2e 6e 61 6d 65 "
          + "46 42 6f 62 20 6a 72 49 63 68 69 6c 64 2e 61 67 65 28 0f 04");

    // Provenance: UnwrappedBasicTest#testPartialNonNullUnwrappedPreservedWhenDisabled().
    void testPartialNonNullUnwrappedPreservedWhenDisabledVpack() throws Exception {
        Container value = EMPTY_AS_OBJECT.readValue(PARTIAL, Container.class);
        assertNotNull(value.u);
        assertEquals("value", value.u.s);
        assertNull(value.u.n);
    }

    // Provenance: UnwrappedBasicTest#testPrefixedUnwrapDeserialize().
    void testPrefixedUnwrapDeserializeVpack() throws Exception {
        PrefixUnwrap value = MAPPER.readValue(PREFIXED, PrefixUnwrap.class);
        assertEquals("Axel", value.name);
        assertNotNull(value.location);
        assertEquals(4, value.location.x);
        assertEquals(7, value.location.y);
    }

    // Provenance: UnwrappedBasicTest#testPrefixedUnwrappingSerialize().
    void testPrefixedUnwrappingSerializeVpack() throws Exception {
        assertEquals(JSON.readTree("{\"_x\":1,\"_y\":2,\"name\":\"Tatu\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new PrefixUnwrap("Tatu", 1, 2))));
    }

    // Provenance: UnwrappedBasicTest#testRecursiveUsage().
    void testRecursiveUsageVpack() throws Exception {
        RecursivePerson value = MAPPER.readValue(RECURSIVE, RecursivePerson.class);
        assertNotNull(value);
        assertEquals("Bob", value.name);
        assertNotNull(value.child);
        assertEquals("Bob jr", value.child.name);
    }

    // Provenance: UnwrappedBasicTest#testSimpleUnwrappedDeserialize().
    void testSimpleUnwrappedDeserializeVpack() throws Exception {
        Unwrapping value = MAPPER.readValue(SIMPLE, Unwrapping.class);
        assertEquals("Tatu", value.name);
        assertNotNull(value.location);
        assertEquals(-13, value.location.x);
        assertEquals(7, value.location.y);
    }

    // Provenance: UnwrappedBasicTest#testSimpleUnwrappingSerialize().
    void testSimpleUnwrappingSerializeVpack() throws Exception {
        assertEquals(JSON.readTree("{\"x\":1,\"y\":2,\"name\":\"Tatu\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new Unwrapping("Tatu", 1, 2))));
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedAsPropertyIndicator().
    void testUnwrappedAsPropertyIndicatorVpack() throws Exception {
        Outer outer = new Outer();
        outer.inner = new Inner();
        outer.inner.animal = "Zebra";
        var tree = MAPPER.readTree(MAPPER.writeValueAsBytes(outer));
        assertEquals(JSON.readTree("{\"animal\":\"Zebra\"}"), tree);
        assertFalse(tree.has("inner"));
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedCaching().
    void testUnwrappedCachingVpack() throws Exception {
        InnerContainer inner = new InnerContainer(new Base("12345"));
        OuterContainer outer = new OuterContainer(inner);
        var expectedInner = JSON.readTree("{\"base.id\":\"12345\"}");
        var expectedOuter = JSON.readTree("{\"container.base.id\":\"12345\"}");
        assertEquals(expectedOuter, MAPPER.readTree(MAPPER.writeValueAsBytes(outer)));
        assertEquals(expectedInner, MAPPER.readTree(MAPPER.writeValueAsBytes(inner)));
        assertEquals(expectedOuter, MAPPER.readTree(MAPPER.writeValueAsBytes(outer)));
        assertEquals(expectedInner, MAPPER.readTree(MAPPER.writeValueAsBytes(inner)));
        assertEquals(expectedOuter, MAPPER.readTree(MAPPER.writeValueAsBytes(outer)));
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedDeserializeWithCreator().
    void testUnwrappedDeserializeWithCreatorVpack() throws Exception {
        UnwrappingWithCreator value = MAPPER.readValue(CREATOR, UnwrappingWithCreator.class);
        assertEquals("Tatu", value.name);
        assertNotNull(value.location);
        assertEquals(1, value.location.x);
        assertEquals(2, value.location.y);
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithJsonCreatorExplicitWithName().
    void testUnwrappedWithJsonCreatorExplicitWithNameVpack() throws Exception {
        ExplicitWithName value = MAPPER.readValue(CREATOR_FIELDS, ExplicitWithName.class);
        assertCreatorValues(value.getUnrelated(), value.getInner());
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithJsonCreatorImplicitWithName().
    void testUnwrappedWithJsonCreatorImplicitWithNameVpack() throws Exception {
        ImplicitWithName value = MAPPER.readValue(CREATOR_FIELDS, ImplicitWithName.class);
        assertCreatorValues(value.getUnrelated(), value.getInner());
    }

    // Provenance: UnwrappedBasicTest#testUnwrappedWithJsonCreatorWithExplicitWithoutName().
    void testUnwrappedWithJsonCreatorWithExplicitWithoutNameVpack() throws Exception {
        ExplicitWithoutName value = MAPPER.readValue(CREATOR_FIELDS, ExplicitWithoutName.class);
        assertCreatorValues(value.getUnrelated(), value.getInner());
    }
private static void assertCreatorValues(String unrelated, Inner1467 inner) {
        assertEquals("unrelatedValue", unrelated);
        assertEquals("value1", inner.getProperty1());
        assertEquals("value2", inner.getProperty2());
    }
static class Unwrapping {
        public String name;
        @JsonUnwrapped public Location location;
        Unwrapping() { }
        Unwrapping(String name, int x, int y) { this.name=name; location=new Location(x,y); }
    }
static class Location { public int x; public int y; Location() { } Location(int x,int y) { this.x=x; this.y=y; } }
static class UnwrappingWithCreator {
        public String name;
        @JsonUnwrapped public Location location;
        @com.fasterxml.jackson.annotation.JsonCreator
        UnwrappingWithCreator(@JsonProperty("name") String name) { this.name=name; }
    }
static class RecursivePerson {
        public String name;
        public int age;
        @JsonUnwrapped(prefix="child.") public RecursivePerson child;
    }
static class Container { @JsonUnwrapped public EmptyValue u; }
static class EmptyValue { public String s; public Integer n; }
static class PrefixUnwrap {
        public String name;
        @JsonUnwrapped(prefix="_") public Location location;
        PrefixUnwrap() { }
        PrefixUnwrap(String name, int x, int y) { this.name=name; location=new Location(x,y); }
    }
static class Outer { @JsonUnwrapped Inner inner; }
static class Inner { public String animal; }
static class Base { public String id; Base(String id) { this.id=id; } }
static class InnerContainer { @JsonUnwrapped(prefix="base.") public Base base; InnerContainer(Base base) { this.base=base; } }
static class OuterContainer { @JsonUnwrapped(prefix="container.") public InnerContainer container; OuterContainer(InnerContainer container) { this.container=container; } }
static class Inner1467 {
        private final String property1;
        private final String property2;
        Inner1467(@JsonProperty("property1") String property1, @JsonProperty("property2") String property2) { this.property1=property1; this.property2=property2; }
        public String getProperty1() { return property1; }
        public String getProperty2() { return property2; }
    }
static class ExplicitWithoutName {
        private final String unrelated;
        private final Inner1467 inner;
        @com.fasterxml.jackson.annotation.JsonCreator
        ExplicitWithoutName(@JsonProperty("unrelated") String unrelated, @JsonUnwrapped Inner1467 inner) { this.unrelated=unrelated; this.inner=inner; }
        public String getUnrelated() { return unrelated; }
        @JsonUnwrapped public Inner1467 getInner() { return inner; }
    }
static class ExplicitWithName {
        private final String unrelated;
        private final Inner1467 inner;
        @com.fasterxml.jackson.annotation.JsonCreator
        ExplicitWithName(@JsonProperty("unrelated") String unrelated, @JsonProperty("inner") @JsonUnwrapped Inner1467 inner) { this.unrelated=unrelated; this.inner=inner; }
        public String getUnrelated() { return unrelated; }
        @JsonUnwrapped public Inner1467 getInner() { return inner; }
    }
static class ImplicitWithName {
        private final String unrelated;
        private final Inner1467 inner;
        ImplicitWithName(@JsonProperty("unrelated") String unrelated, @JsonProperty("inner") @JsonUnwrapped Inner1467 inner) { this.unrelated=unrelated; this.inner=inner; }
        public String getUnrelated() { return unrelated; }
        @JsonUnwrapped public Inner1467 getInner() { return inner; }
    }

    void __invoke_testPartialNonNullUnwrappedPreservedWhenDisabledVpack() throws Exception {
        try {
            testPartialNonNullUnwrappedPreservedWhenDisabledVpack();
        } finally {
        }
    }


    void __invoke_testPrefixedUnwrapDeserializeVpack() throws Exception {
        try {
            testPrefixedUnwrapDeserializeVpack();
        } finally {
        }
    }


    void __invoke_testPrefixedUnwrappingSerializeVpack() throws Exception {
        try {
            testPrefixedUnwrappingSerializeVpack();
        } finally {
        }
    }


    void __invoke_testRecursiveUsageVpack() throws Exception {
        try {
            testRecursiveUsageVpack();
        } finally {
        }
    }


    void __invoke_testSimpleUnwrappedDeserializeVpack() throws Exception {
        try {
            testSimpleUnwrappedDeserializeVpack();
        } finally {
        }
    }


    void __invoke_testSimpleUnwrappingSerializeVpack() throws Exception {
        try {
            testSimpleUnwrappingSerializeVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedAsPropertyIndicatorVpack() throws Exception {
        try {
            testUnwrappedAsPropertyIndicatorVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedCachingVpack() throws Exception {
        try {
            testUnwrappedCachingVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedDeserializeWithCreatorVpack() throws Exception {
        try {
            testUnwrappedDeserializeWithCreatorVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithJsonCreatorExplicitWithNameVpack() throws Exception {
        try {
            testUnwrappedWithJsonCreatorExplicitWithNameVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithJsonCreatorImplicitWithNameVpack() throws Exception {
        try {
            testUnwrappedWithJsonCreatorImplicitWithNameVpack();
        } finally {
        }
    }


    void __invoke_testUnwrappedWithJsonCreatorWithExplicitWithoutNameVpack() throws Exception {
        try {
            testUnwrappedWithJsonCreatorWithExplicitWithoutNameVpack();
        } finally {
        }
    }

}
