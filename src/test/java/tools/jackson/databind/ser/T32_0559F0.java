package tools.jackson.databind.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.SerializationContexts;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0559F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SINGLE_STRING = VPackWireFixtureTest.hex(
            "43 78 79 7a");
private static final byte[] SINGLE_INT = VPackWireFixtureTest.hex(
            "28 0d");
private static final byte[] SINGLE_LONG = VPackWireFixtureTest.hex(
            "28 2a");
private static final byte[] SINGLE_BEAN_STRING = VPackWireFixtureTest.hex(
            "0b 0f 01 46 76 61 6c 75 65 73 43 66 6f 6f 03");
private static final byte[] SINGLE_TRUE = VPackWireFixtureTest.hex(
            "1a");
private static final byte[] TWO_BOOLEANS = VPackWireFixtureTest.hex(
            "02 04 1a 19");
private static final byte[] SINGLE_SHORT = VPackWireFixtureTest.hex(
            "33");
private static final byte[] TWO_SHORTS = VPackWireFixtureTest.hex(
            "02 04 33 32");
private static final byte[] SINGLE_INT_ARRAY = VPackWireFixtureTest.hex(
            "33");
private static final byte[] TWO_INTS = VPackWireFixtureTest.hex(
            "02 04 33 32");
private static final byte[] SINGLE_LONG_ARRAY = VPackWireFixtureTest.hex(
            "31");
private static final byte[] TWO_LONGS = VPackWireFixtureTest.hex(
            "02 04 3f 34");
private static final byte[] SINGLE_DOUBLE = VPackWireFixtureTest.hex(
            "1b 00 00 00 00 00 00 e0 3f");
private static final byte[] TWO_DOUBLES = VPackWireFixtureTest.hex(
            "02 14 1b 00 00 00 00 00 00 e0 3f "
          + "1b 00 00 00 00 00 00 04 40");
private static final byte[] SINGLE_FLOAT = SINGLE_DOUBLE;
private static final byte[] TWO_FLOATS = TWO_DOUBLES;
private static final byte[] READWRITE_ONLY = VPackWireFixtureTest.hex(
            "0b 0f 01 49 72 65 61 64 77 72 69 74 65 32 03");
private static final byte[] ANNO_BEAN = VPackWireFixtureTest.hex(
            "0b 0b 02 41 78 31 41 79 32 03 06");
private static final byte[] SIMPLE_BEAN = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 31 03");
private static final byte[] ALPHA_INDEX_DEFAULT = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 30 41 63 30 41 62 30 03 09 06");
private static final byte[] ALPHA_INDEX_DISABLED = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 30 41 62 30 41 63 30 03 06 09");
private static final byte[] EXPLICIT_NAME_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 62 30 41 61 30 41 63 30 06 03 09");
private static final byte[] CREATOR_PROPERTIES_NOT_FIRST = VPackWireFixtureTest.hex(
            "0b 30 03 45 6e 6f 74 65 73 45 6e 6f 74 65 73 "
          + "49 66 69 72 73 74 4e 61 6d 65 45 66 69 72 73 74 "
          + "48 6c 61 73 74 4e 61 6d 65 44 6c 61 73 74 0f 1f 03");

    // Provenance: SerializationFeaturesTest#testSingleElementCollections().
    void testSingleElementCollectionsVpack() throws Exception {
        ObjectWriter writer = MAPPER.writer()
                .with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("xyz");
        assertArrayEquals(SINGLE_STRING, writer.writeValueAsBytes(strings));

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(13);
        assertArrayEquals(SINGLE_INT, writer.writeValueAsBytes(integers));

        HashSet<Long> longs = new HashSet<>();
        longs.add(42L);
        assertArrayEquals(SINGLE_LONG, writer.writeValueAsBytes(longs));
        assertArrayEquals(SINGLE_BEAN_STRING,
                writer.writeValueAsBytes(new StringListBean(java.util.Collections.singletonList("foo"))));
        HashSet<String> stringSet = new HashSet<>();
        stringSet.add("foo");
        assertArrayEquals(SINGLE_BEAN_STRING,
                writer.writeValueAsBytes(new StringListBean(stringSet)));

        assertArrayEquals(SINGLE_TRUE, writer.writeValueAsBytes(new boolean[] { true }));
        assertArrayEquals(TWO_BOOLEANS, writer.writeValueAsBytes(new boolean[] { true, false }));
        assertArrayEquals(SINGLE_TRUE, writer.writeValueAsBytes(new Boolean[] { Boolean.TRUE }));

        assertArrayEquals(SINGLE_SHORT, writer.writeValueAsBytes(new short[] { 3 }));
        assertArrayEquals(TWO_SHORTS, writer.writeValueAsBytes(new short[] { 3, 2 }));
        assertArrayEquals(SINGLE_INT_ARRAY, writer.writeValueAsBytes(new int[] { 3 }));
        assertArrayEquals(TWO_INTS, writer.writeValueAsBytes(new int[] { 3, 2 }));
        assertArrayEquals(SINGLE_LONG_ARRAY, writer.writeValueAsBytes(new long[] { 1L }));
        assertArrayEquals(TWO_LONGS, writer.writeValueAsBytes(new long[] { -1L, 4L }));
        assertArrayEquals(SINGLE_DOUBLE, writer.writeValueAsBytes(new double[] { 0.5 }));
        assertArrayEquals(TWO_DOUBLES, writer.writeValueAsBytes(new double[] { 0.5, 2.5 }));
        assertArrayEquals(SINGLE_FLOAT, writer.writeValueAsBytes(new float[] { 0.5f }));
        assertArrayEquals(TWO_FLOATS, writer.writeValueAsBytes(new float[] { 0.5f, 2.5f }));
        assertArrayEquals(SINGLE_STRING, writer.writeValueAsBytes(new String[] { "xyz" }));
    }

    // Provenance: SerializationFeaturesTest#testNeedForSetters().
    void testNeedForSettersVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultVisibility(vc -> vc
                        .withVisibility(PropertyAccessor.ALL, Visibility.NONE)
                        .withVisibility(PropertyAccessor.FIELD, Visibility.NONE)
                        .withVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY)
                        .withVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY))
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .build();
        assertArrayEquals(READWRITE_ONLY, mapper.writeValueAsBytes(new Data736()));
    }

    // Provenance: SerializationFeaturesTest#testProviderConfig().
    void testProviderConfigVpack() throws Exception {
        TestObjectMapper mapper = new TestObjectMapper();
        SerializationContexts contexts = mapper.getSerializationContexts();
        assertEquals(0, contexts.cachedSerializersCount());

        assertArrayEquals(ANNO_BEAN, mapper.writeValueAsBytes(new AnnoBean()));
        int count = contexts.cachedSerializersCount();
        assertTrue(count >= 2 && count <= 10,
                "Expected at least 2 cached serializers, got " + count);
        contexts.flushCachedSerializers();
        assertEquals(0, contexts.cachedSerializersCount());
    }

    // Provenance: SerializationFeaturesTest#testNoAccessOverrides().
    void testNoAccessOverridesVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS)
                .build();
        assertArrayEquals(SIMPLE_BEAN, mapper.writeValueAsBytes(new SimpleBean()));
    }
static class StringListBean {
        public Collection<String> values;
        StringListBean(Collection<String> values) { this.values = values; }
    }
public static class Data736 {
        private int readonly;
        private int readwrite;

        Data736() {
            readonly = 1;
            readwrite = 2;
        }

        public int getReadwrite() { return readwrite; }
        public void setReadwrite(int value) { readwrite = value; }
        public int getReadonly() { return readonly; }
    }
static class AnnoBean {
        public int getX() { return 1; }
        @JsonProperty("y") private int getY() { return 2; }
    }
public static class SimpleBean {
        public int x = 1;
    }
static class TestObjectMapper extends VPackMapper {
        SerializationContexts getSerializationContexts() {
            return _serializationContexts;
        }
    }
@JsonPropertyOrder(alphabetic = true)
    static class AlphaWithIndexBean {
        @JsonProperty(index = 2) public int c;
        @JsonProperty(index = 0) public int a;
        public int b;
    }
@JsonPropertyOrder({ "b", "a" })
    static class ExplicitOrderWithIndexBean {
        @JsonProperty(index = 2) public int c;
        @JsonProperty(index = 0) public int a;
        public int b;
    }
static class BeanForGH5918 {
        private String notes;
        private String firstName;
        private String lastName;

        @JsonCreator
        BeanForGH5918(@JsonProperty("lastName") String lastName,
                @JsonProperty("firstName") String firstName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getNotes() { return notes; }
        public void setNotes(String value) { notes = value; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    void __invoke_testSingleElementCollectionsVpack() throws Exception {
        try {
            testSingleElementCollectionsVpack();
        } finally {
        }
    }


    void __invoke_testNeedForSettersVpack() throws Exception {
        try {
            testNeedForSettersVpack();
        } finally {
        }
    }


    void __invoke_testProviderConfigVpack() throws Exception {
        try {
            testProviderConfigVpack();
        } finally {
        }
    }


    void __invoke_testNoAccessOverridesVpack() throws Exception {
        try {
            testNoAccessOverridesVpack();
        } finally {
        }
    }

}
