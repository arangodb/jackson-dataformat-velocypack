package tools.jackson.databind.struct;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0603F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper UNWRAPPING = VPackMapper.builder()
            .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
private static final ObjectMapper JSON = JsonMapper.builder().build();
private static final byte[] STRING = VPackWireFixtureTest.hex("44 46 4f 4f 21");
private static final byte[] STRING_ARRAY = VPackWireFixtureTest.hex("13 08 44 46 4f 4f 21 01");
private static final byte[] TWO_STRINGS = VPackWireFixtureTest.hex("13 0d 44 46 4f 4f 21 44 46 4f 4f 21 02");
private static final byte[] URI_ARRAY = VPackWireFixtureTest.hex(
            "13 12 4e 68 74 74 70 3a 2f 2f 66 6f 6f 2e 63 6f 6d 01");
private static final byte[] UUID_ARRAY = VPackWireFixtureTest.hex(
            "13 28 64 37 36 65 36 64 31 38 33 2d 35 66 36 38 2d 34 61 66 61 2d 62 39 34 61 2d 39 32 32 63 31 66 64 62 38 33 66 38 01");
private static final byte[] NO_PREFIX = VPackWireFixtureTest.hex(
            "14 20 44 6e 61 6d 65 45 42 75 62 62 61 48 6c 6f 63 61 74 69 6f 6e 14 09 41 78 32 41 79 33 02 02");
private static final byte[] PREFIXED = VPackWireFixtureTest.hex(
            "14 22 45 5f 6e 61 6d 65 45 42 75 62 62 61 49 5f 6c 6f 63 61 74 69 6f 6e 14 09 41 78 32 41 79 33 02 02");

    // Provenance: UnwrapSingleArrayTest#testSingleString().
    void testSingleStringVpack() throws Exception {
        assertEquals("FOO!", MAPPER.readValue(STRING, String.class));
    }

    // Provenance: UnwrapSingleArrayTest#testSingleStringWrapped().
    void testSingleStringWrappedVpack() throws Exception {
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue(STRING_ARRAY, String.class));
        assertThrows(MismatchedInputException.class, () -> UNWRAPPING.readValue(TWO_STRINGS, String.class));
        assertEquals("FOO!", UNWRAPPING.readValue(STRING_ARRAY, String.class));
    }

    // Provenance: UnwrapSingleArrayTest#testURIAsArray().
    void testURIAsArrayVpack() throws Exception {
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue(URI_ARRAY, URI.class));
        assertThrows(MismatchedInputException.class, () -> UNWRAPPING.readValue(TWO_URIS, URI.class));
        assertEquals(URI.create("http://foo.com"), UNWRAPPING.readValue(URI_ARRAY, URI.class));
    }

    // Provenance: UnwrapSingleArrayTest#testUUIDAsArray().
    void testUUIDAsArrayVpack() throws Exception {
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue(UUID_ARRAY, UUID.class));
        assertThrows(MismatchedInputException.class, () -> UNWRAPPING.readValue(TWO_UUIDS, UUID.class));
        assertEquals(UUID.fromString("76e6d183-5f68-4afa-b94a-922c1fdb83f8"),
                UNWRAPPING.readValue(UUID_ARRAY, UUID.class));
    }

    // Provenance: UnwrapSingleArrayTest#testUnwrapWithPrimitiveArraysEtc().
    void testUnwrapWithPrimitiveArraysEtcVpack() throws Exception {
        assertEquals(JSON.readTree("{\"v\":7}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new ShortValue())));
        assertEquals(JSON.readTree("{\"v\":3}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new IntValue())));
        assertEquals(JSON.readTree("{\"v\":1}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new LongValue())));
        assertEquals(JSON.readTree("{\"v\":true}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new BooleanValue())));
        assertEquals(JSON.readTree("{\"v\":0.5}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new FloatValue())));
        assertEquals(JSON.readTree("{\"v\":0.25}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new DoubleValue())));
        assertEquals(JSON.readTree("0.5"), MAPPER.readTree(MAPPER.writer()
                .with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .writeValueAsBytes(new double[] { 0.5 })));
        assertEquals(JSON.readTree("{\"v\":\"foo\"}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new IterableValue())));
        assertEquals(JSON.readTree("{\"v\":\"x\"}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new IterableValue("x"))));
        assertEquals(JSON.readTree("{\"v\":\"foo\"}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new CollectionValue())));
        assertEquals(JSON.readTree("{\"v\":\"x\"}"), MAPPER.readTree(MAPPER.writeValueAsBytes(new CollectionValue("x"))));
        assertEquals(JSON.readTree("{\"v\":[\"x\",null]}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new IterableValue("x", null))));
        assertEquals(JSON.readTree("{\"v\":[\"x\",null]}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new CollectionValue("x", null))));
        assertEquals(JSON.readTree("{\"v\":\"http://foo\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(new UriArrayValue())));
    }

    // Provenance: UnwrapSingleArrayTest#testWithArrayTypes().
    void testWithArrayTypesVpack() throws Exception {
        ArrayFields fields = new ArrayFields();
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":[true]}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(fields)));
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":true}"),
                MAPPER.readTree(MAPPER.writer().with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .writeValueAsBytes(fields)));
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":[true]}"),
                MAPPER.readTree(MAPPER.writer().without(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .writeValueAsBytes(fields)));
        ObjectMapper override = VPackMapper.builder()
                .withConfigOverride(String[].class, v -> v.setFormat(JsonFormat.Value.empty()
                        .withFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)))
                .build();
        assertEquals(JSON.readTree("{\"values\":\"a\"}"),
                override.readTree(override.writeValueAsBytes(new StringArrayValue("a"))));
    }

    // Provenance: UnwrapSingleArrayTest#testWithCollectionTypes().
    void testWithCollectionTypesVpack() throws Exception {
        CollectionFields fields = new CollectionFields();
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":[true],\"enums\":\"B\"}"),
                MAPPER.readTree(MAPPER.writeValueAsBytes(fields)));
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":true,\"enums\":\"B\"}"),
                MAPPER.readTree(MAPPER.writer().with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .writeValueAsBytes(fields)));
        assertEquals(JSON.readTree("{\"strings\":\"a\",\"ints\":[1],\"bools\":[true],\"enums\":\"B\"}"),
                MAPPER.readTree(MAPPER.writer().without(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .writeValueAsBytes(fields)));
    }
private static final byte[] TWO_URIS = VPackWireFixtureTest.hex(
            "13 21 4e 68 74 74 70 3a 2f 2f 66 6f 6f 2e 63 6f 6d 4e 68 74 74 70 3a 2f 2f 66 6f 6f 2e 63 6f 6d 02");
private static final byte[] TWO_UUIDS = VPackWireFixtureTest.hex(
            "13 4d 64 37 36 65 36 64 31 38 33 2d 35 66 36 38 2d 34 61 66 61 2d 62 39 34 61 2d 39 32 32 63 31 66 64 62 38 33 66 38 64 37 36 65 36 64 31 38 33 2d 35 66 36 38 2d 34 61 66 61 2d 62 39 34 61 2d 39 32 32 63 31 66 64 62 38 33 66 38 02");
static class Location { public int x; public int y; }
static class Inner { public String name; public Location location; }
static class NoPrefixBean { @JsonUnwrapped public Inner unwrapped; }
static class PrefixBean { @JsonUnwrapped(prefix = "_") public Inner unwrapped; }
static class CreatorInner {
        public final String name;
        public final Location location;
        @JsonCreator CreatorInner(@JsonProperty("name") String name, @JsonProperty("location") Location location) {
            this.name = name; this.location = location;
        }
    }
static class CreatorPrefixBean { @JsonUnwrapped(prefix = "_") public CreatorInner unwrapped; }
static class Person { @JsonUnwrapped(prefix = "businessAddress.") public Address businessAddress; }
static class Address { public String street; public String addon; public String zip; public String town; public String country; }
@JsonInclude(JsonInclude.Include.NON_EMPTY) static final class Health { @JsonUnwrapped(prefix = "xxx.") public Status status; }
static final class Status { public String code; }
static class ShortValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public short[] v = { 7 }; }
static class IntValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public int[] v = { 3 }; }
static class LongValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public long[] v = { 1L }; }
static class BooleanValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public boolean[] v = { true }; }
static class FloatValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public float[] v = { 0.5f }; }
static class DoubleValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public double[] v = { 0.25 }; }
static class IterableValue {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public Iterable<String> v;
        IterableValue() { v = List.of("foo"); }
        IterableValue(String... values) { v = Arrays.asList(values); }
    }
static class CollectionValue {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public Collection<String> v;
        CollectionValue() { v = List.of("foo"); }
        CollectionValue(String... values) { v = Arrays.asList(values); }
    }
static class UriArrayValue { @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public URI[] v = { URI.create("http://foo") }; }
static class ArrayFields {
        @JsonProperty("strings") @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public String[] strings = { "a" };
        @JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public int[] ints = { 1 };
        public boolean[] bools = { true };
    }
static class StringArrayValue { public String[] values; StringArrayValue(String... values) { this.values = values; } }
enum Example { A, B }
@JsonPropertyOrder({ "strings", "ints", "bools", "enums" })
    static class CollectionFields {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public List<String> strings = List.of("a");
        @JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public Collection<Integer> ints = List.of(1);
        public Set<Boolean> bools = new LinkedHashSet<>(List.of(true));
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) public EnumSet<Example> enums = EnumSet.of(Example.B);
    }

    void __invoke_testSingleStringVpack() throws Exception {
        try {
            testSingleStringVpack();
        } finally {
        }
    }


    void __invoke_testSingleStringWrappedVpack() throws Exception {
        try {
            testSingleStringWrappedVpack();
        } finally {
        }
    }


    void __invoke_testURIAsArrayVpack() throws Exception {
        try {
            testURIAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testUUIDAsArrayVpack() throws Exception {
        try {
            testUUIDAsArrayVpack();
        } finally {
        }
    }


    void __invoke_testUnwrapWithPrimitiveArraysEtcVpack() throws Exception {
        try {
            testUnwrapWithPrimitiveArraysEtcVpack();
        } finally {
        }
    }


    void __invoke_testWithArrayTypesVpack() throws Exception {
        try {
            testWithArrayTypesVpack();
        } finally {
        }
    }


    void __invoke_testWithCollectionTypesVpack() throws Exception {
        try {
            testWithCollectionTypesVpack();
        } finally {
        }
    }

}
