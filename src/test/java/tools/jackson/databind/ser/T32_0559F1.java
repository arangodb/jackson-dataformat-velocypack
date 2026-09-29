package tools.jackson.databind.ser;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.SerializationContexts;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0559F1 {
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

    // Provenance: SerializationOrderTest#testAlphabeticWithIndexDefaultBehavior().
    void testAlphabeticWithIndexDefaultBehaviorVpack() throws Exception {
        assertArrayEquals(ALPHA_INDEX_DEFAULT,
                MAPPER.writeValueAsBytes(new AlphaWithIndexBean()));
    }

    // Provenance: SerializationOrderTest#testAlphabeticWithIndexDisabled().
    void testAlphabeticWithIndexDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.SORT_PROPERTIES_BY_INDEX)
                .build();
        assertArrayEquals(ALPHA_INDEX_DISABLED,
                mapper.writeValueAsBytes(new AlphaWithIndexBean()));
    }

    // Provenance: SerializationOrderTest#testExplicitNameOrderWinsWhenIndexDisabled().
    void testExplicitNameOrderWinsWhenIndexDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.SORT_PROPERTIES_BY_INDEX)
                .build();
        assertArrayEquals(EXPLICIT_NAME_ORDER,
                mapper.writeValueAsBytes(new ExplicitOrderWithIndexBean()));
    }

    // Provenance: SerializationOrderTest#testCreatorPropsNotFirstWhenBothSortingDisabled().
    void testCreatorPropsNotFirstWhenBothSortingDisabledVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .disable(MapperFeature.SORT_CREATOR_PROPERTIES_FIRST)
                .build();
        BeanForGH5918 person = new BeanForGH5918("last", "first");
        person.setNotes("notes");
        assertArrayEquals(CREATOR_PROPERTIES_NOT_FIRST,
                mapper.writeValueAsBytes(person));
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

    void __invoke_testAlphabeticWithIndexDefaultBehaviorVpack() throws Exception {
        try {
            testAlphabeticWithIndexDefaultBehaviorVpack();
        } finally {
        }
    }


    void __invoke_testAlphabeticWithIndexDisabledVpack() throws Exception {
        try {
            testAlphabeticWithIndexDisabledVpack();
        } finally {
        }
    }


    void __invoke_testExplicitNameOrderWinsWhenIndexDisabledVpack() throws Exception {
        try {
            testExplicitNameOrderWinsWhenIndexDisabledVpack();
        } finally {
        }
    }


    void __invoke_testCreatorPropsNotFirstWhenBothSortingDisabledVpack() throws Exception {
        try {
            testCreatorPropsNotFirstWhenBothSortingDisabledVpack();
        } finally {
        }
    }

}
