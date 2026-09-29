package tools.jackson.databind.struct;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0604Fixture {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper JSON = JsonMapper.builder().build();
private static final ObjectMapper EMPTY_AS_NULL = VPackMapper.builder()
            .enable(DeserializationFeature.USE_NULL_FOR_EMPTY_UNWRAPPED).build();
private static final ObjectMapper EMPTY_AS_OBJECT = VPackMapper.builder()
            .disable(DeserializationFeature.USE_NULL_FOR_EMPTY_UNWRAPPED).build();
private static final ObjectMapper STRICT = VPackMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
private static final byte[] DEEP = VPackWireFixtureTest.hex(
            "14 13 41 78 33 44 6e 61 6d 65 43 42 6f 62 41 79 28 1b 03");
private static final byte[] DOUBLE = VPackWireFixtureTest.hex(
            "14 1f 45 66 69 72 73 74 43 4a 6f 65 41 79 37 44 6c 61 73 74 45 53 6d 69 74 68 41 78 20 f3 04");
private static final byte[] DEEP_PREFIX = VPackWireFixtureTest.hex(
            "14 1c 46 75 2e 6e 61 6d 65 45 42 75 62 62 61 44 75 2e 5f 78 32 44 75 2e 5f 79 33 03");
private static final byte[] EMPTY = VPackWireFixtureTest.hex("0a");
private static final byte[] NAME_TEST = VPackWireFixtureTest.hex("14 0d 44 6e 61 6d 65 44 74 65 73 74 01");
private static final byte[] UNKNOWN = VPackWireFixtureTest.hex(
            "14 1d 45 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 02");
private static final byte[] UNKNOWN_PREFIX = VPackWireFixtureTest.hex(
            "14 24 4c 6e 65 73 74 65 64 2e 66 69 65 6c 64 45 76 61 6c 75 65 43 62 61 64 49 62 61 64 20 76 61 6c 75 65 02");

    // Provenance: UnwrappedBasicTest#testDeepUnwrapping().
    void testDeepUnwrappingVpack() throws Exception {
        DeepUnwrap value = MAPPER.readValue(DEEP, DeepUnwrap.class);
        assertNotNull(value.unwrapped);
        assertEquals("Bob", value.unwrapped.name);
        assertNotNull(value.unwrapped.location);
        assertEquals(3, value.unwrapped.location.x);
        assertEquals(27, value.unwrapped.location.y);
    }

    // Provenance: UnwrappedBasicTest#testDeepUnwrappingSerialize().
    void testDeepUnwrappingSerializeVpack() throws Exception {
        assertEquals(JSON.readTree("{\"x\":1,\"y\":2,\"name\":\"Tatu\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new DeepUnwrap("Tatu", 1, 2))));
    }

    // Provenance: UnwrappedBasicTest#testDoubleUnwrapping().
    void testDoubleUnwrappingVpack() throws Exception {
        DoubleUnwrap value = MAPPER.readValue(DOUBLE, DoubleUnwrap.class);
        assertNotNull(value.location);
        assertEquals(-13, value.location.x);
        assertEquals(7, value.location.y);
        assertNotNull(value.name);
        assertEquals("Joe", value.name.first);
        assertEquals("Smith", value.name.last);
    }

    // Provenance: UnwrappedBasicTest#testDeepPrefixedUnwrapDeserialize().
    void testDeepPrefixedUnwrapDeserializeVpack() throws Exception {
        DeepPrefixUnwrap value = MAPPER.readValue(DEEP_PREFIX, DeepPrefixUnwrap.class);
        assertNotNull(value.unwrapped);
        assertNotNull(value.unwrapped.location);
        assertEquals(2, value.unwrapped.location.x);
        assertEquals(3, value.unwrapped.location.y);
        assertEquals("Bubba", value.unwrapped.name);
    }

    // Provenance: UnwrappedBasicTest#testDeepPrefixedUnwrappingSerialize().
    void testDeepPrefixedUnwrappingSerializeVpack() throws Exception {
        assertEquals(JSON.readTree("{\"u._x\":1,\"u._y\":1,\"u.name\":\"Bubba\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new DeepPrefixUnwrap("Bubba", 1, 1))));
    }

    // Provenance: UnwrappedBasicTest#testDefaultUnwrappedWithTypeInfo().
    void testDefaultUnwrappedWithTypeInfoVpack() {
        TypeOuter value = new TypeOuter();
        value.p1 = "101";
        value.inner = new TypeInner();
        value.inner.p2 = "202";
        DatabindException problem = assertThrows(DatabindException.class,
                () -> MAPPER.writeValueAsBytes(value));
        assertTrue(problem.getMessage().contains("requires use of type information"));
    }

    // Provenance: UnwrappedBasicTest#testEmptyUnwrappedAsNull().
    void testEmptyUnwrappedAsNullVpack() throws Exception {
        EmptyContainer value = EMPTY_AS_NULL.readValue(NAME_TEST, EmptyContainer.class);
        assertNotNull(value);
        assertEquals("test", value.name);
        assertNull(value.u);
    }

    // Provenance: UnwrappedBasicTest#testEmptyJsonEmptyUnwrappedAsNull().
    void testEmptyJsonEmptyUnwrappedAsNullVpack() throws Exception {
        EmptyContainer value = EMPTY_AS_NULL.readValue(EMPTY, EmptyContainer.class);
        assertNotNull(value);
        assertNull(value.name);
        assertNull(value.u);
    }

    // Provenance: UnwrappedBasicTest#testEmptyUnwrappedAsNullWhenDisabled().
    void testEmptyUnwrappedAsNullWhenDisabledVpack() throws Exception {
        EmptyContainer value = EMPTY_AS_OBJECT.readValue(NAME_TEST, EmptyContainer.class);
        assertNotNull(value);
        assertEquals("test", value.name);
        assertNotNull(value.u);
        assertNull(value.u.s);
        assertNull(value.u.n);
    }

    // Provenance: UnwrappedBasicTest#testEmptyJsonEmptyUnwrappedAsNullWhenDisabled().
    void testEmptyJsonEmptyUnwrappedAsNullWhenDisabledVpack() throws Exception {
        EmptyContainer value = EMPTY_AS_OBJECT.readValue(EMPTY, EmptyContainer.class);
        assertNotNull(value);
        assertNull(value.name);
        assertNotNull(value.u);
        assertNull(value.u.s);
        assertNull(value.u.n);
    }

    // Provenance: UnwrappedBasicTest#testFailOnUnknownPropertyUnwrapped().
    void testFailOnUnknownPropertyUnwrappedVpack() {
        assertThrows(UnrecognizedPropertyException.class,
                () -> STRICT.readValue(UNKNOWN, UnknownOuter.class));
    }

    // Provenance: UnwrappedBasicTest#testFailOnUnknownPropertyUnwrappedWithPrefix().
    void testFailOnUnknownPropertyUnwrappedWithPrefixVpack() {
        assertThrows(UnrecognizedPropertyException.class,
                () -> STRICT.readValue(UNKNOWN_PREFIX, UnknownPrefixOuter.class));
    }
static class Location { public int x; public int y; }
static class Unwrapping { public String name; @JsonUnwrapped public Location location; }
static class DeepUnwrap {
        @JsonUnwrapped public Unwrapping unwrapped;
        DeepUnwrap() { }
        DeepUnwrap(String name, int x, int y) {
            unwrapped = new Unwrapping();
            unwrapped.name = name;
            unwrapped.location = new Location();
            unwrapped.location.x = x;
            unwrapped.location.y = y;
        }
    }
static class Name { public String first; public String last; }
static class DoubleUnwrap { @JsonUnwrapped public Location location; @JsonUnwrapped public Name name; }
static class PrefixLocation { public int x; public int y; }
static class PrefixUnwrap { public String name; @JsonUnwrapped(prefix = "_") public PrefixLocation location; }
static class DeepPrefixUnwrap {
        @JsonUnwrapped(prefix = "u.") public PrefixUnwrap unwrapped;
        DeepPrefixUnwrap() { }
        DeepPrefixUnwrap(String name, int x, int y) {
            unwrapped = new PrefixUnwrap();
            unwrapped.name = name;
            unwrapped.location = new PrefixLocation();
            unwrapped.location.x = x;
            unwrapped.location.y = y;
        }
    }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class EmptyContainer { public String name; @JsonUnwrapped public EmptyValue u; }
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class EmptyValue { public String s; public Integer n; }
static class UnknownOuter { @JsonUnwrapped public UnknownValue value; }
static class UnknownPrefixOuter { @JsonUnwrapped(prefix = "nested.") public UnknownValue value; }
static class UnknownValue { public String field; }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonTypeName("OuterType")
    static class TypeOuter {
        public String p1;
        @JsonUnwrapped public TypeInner inner;
    }
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonTypeName("InnerType")
    static class TypeInner { public String p2; }

    void __invoke_testDeepUnwrappingVpack() throws Exception {
        try {
            testDeepUnwrappingVpack();
        } finally {
        }
    }


    void __invoke_testDeepUnwrappingSerializeVpack() throws Exception {
        try {
            testDeepUnwrappingSerializeVpack();
        } finally {
        }
    }


    void __invoke_testDoubleUnwrappingVpack() throws Exception {
        try {
            testDoubleUnwrappingVpack();
        } finally {
        }
    }


    void __invoke_testDeepPrefixedUnwrapDeserializeVpack() throws Exception {
        try {
            testDeepPrefixedUnwrapDeserializeVpack();
        } finally {
        }
    }


    void __invoke_testDeepPrefixedUnwrappingSerializeVpack() throws Exception {
        try {
            testDeepPrefixedUnwrappingSerializeVpack();
        } finally {
        }
    }


    void __invoke_testDefaultUnwrappedWithTypeInfoVpack() throws Exception {
        try {
            testDefaultUnwrappedWithTypeInfoVpack();
        } finally {
        }
    }


    void __invoke_testEmptyUnwrappedAsNullVpack() throws Exception {
        try {
            testEmptyUnwrappedAsNullVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonEmptyUnwrappedAsNullVpack() throws Exception {
        try {
            testEmptyJsonEmptyUnwrappedAsNullVpack();
        } finally {
        }
    }


    void __invoke_testEmptyUnwrappedAsNullWhenDisabledVpack() throws Exception {
        try {
            testEmptyUnwrappedAsNullWhenDisabledVpack();
        } finally {
        }
    }


    void __invoke_testEmptyJsonEmptyUnwrappedAsNullWhenDisabledVpack() throws Exception {
        try {
            testEmptyJsonEmptyUnwrappedAsNullWhenDisabledVpack();
        } finally {
        }
    }


    void __invoke_testFailOnUnknownPropertyUnwrappedVpack() throws Exception {
        try {
            testFailOnUnknownPropertyUnwrappedVpack();
        } finally {
        }
    }


    void __invoke_testFailOnUnknownPropertyUnwrappedWithPrefixVpack() throws Exception {
        try {
            testFailOnUnknownPropertyUnwrappedWithPrefixVpack();
        } finally {
        }
    }

}
