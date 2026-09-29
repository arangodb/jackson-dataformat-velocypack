package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0584Fixture {
private static final ObjectMapper CONTENT_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();
private static ObjectMapper orderedMapper() {
        return VPackMapper.builder()
                .enable(SerializationFeature.ORDER_SET_ELEMENTS)
                .build();
    }

    void testMapWithOnlyEmptyValuesVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 46 76 61 6c 75 65 73 "
              + "0b 0a 01 41 61 43 31 32 33 03 03"),
                CONTENT_MAPPER.writeValueAsBytes(new Wrapper497(new StringMap497()
                        .add("a", "123"))));
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                CONTENT_MAPPER.writeValueAsBytes(new Wrapper497(new StringMap497()
                        .add("a", "")
                        .add("b", null))));
    }

    void testNonNullValueMapViaPropVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1b 01 45 73 74 75 66 66 "
              + "0b 11 02 41 61 43 66 6f 6f 41 63 43 62 61 72 03 09 03"),
                CONTENT_MAPPER.writeValueAsBytes(new NoNullValuesMapContainer()
                        .add("a", "foo")
                        .add("b", null)
                        .add("c", "bar")));
    }
static class NonComparable {
        public final String name;

        NonComparable(String name) {
            this.name = name;
        }
    }
@JsonInclude(content = JsonInclude.Include.NON_NULL)
    static class NoNullValuesMapContainer {
        public java.util.Map<String, String> stuff = new java.util.LinkedHashMap<>();

        NoNullValuesMapContainer add(String key, String value) {
            stuff.put(key, value);
            return this;
        }
    }
static class Wrapper497 {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY,
                value = JsonInclude.Include.NON_EMPTY)
        public StringMap497 values;

        Wrapper497(StringMap497 values) {
            this.values = values;
        }
    }
static class StringMap497 extends java.util.LinkedHashMap<String, String> {
        StringMap497 add(String key, String value) {
            put(key, value);
            return this;
        }
    }

    void __invoke_testMapWithOnlyEmptyValuesVpack() throws Exception {
        try {
            testMapWithOnlyEmptyValuesVpack();
        } finally {
        }
    }


    void __invoke_testNonNullValueMapViaPropVpack() throws Exception {
        try {
            testNonNullValueMapViaPropVpack();
        } finally {
        }
    }

}
