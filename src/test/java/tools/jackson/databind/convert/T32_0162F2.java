package tools.jackson.databind.convert;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.Base64Variants;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.util.StdConverter;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0162F2 {
private static final byte[] UPDATE_OBJECT = VPackWireFixtureTest.hex(
            "0b 10 03 41 78 33 41 79 34 41 77 28 6f 09 03 06");
private static final byte[] LOWERCASE_INPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 58 79 5a 03");
private static final byte[] LOWERCASE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testBytesToBase64AndBack() throws Exception {
        byte[] input = new byte[] { 1, 2, 3, 4, 5, 6, 7 };
        String encoded = MAPPER.convertValue(input, String.class);
        assertEquals("AQIDBAUGBw==", encoded);
        assertEquals(Base64Variants.MIME.encode(input), encoded);
        assertArrayEquals(input, MAPPER.convertValue(encoded, byte[].class));
    }

    void testBytestoCharArray() throws Exception {
        byte[] input = new byte[] { 1, 2, 3, 4, 5, 6, 7 };
        char[] expected = MAPPER.convertValue(input, String.class).toCharArray();
        assertArrayEquals(expected, MAPPER.convertValue(input, char[].class));
    }

    void testLowerCasingDeserializer() throws Exception {
        StringWrapperWithConvert value = MAPPER.readValue(LOWERCASE_INPUT,
                StringWrapperWithConvert.class);
        assertEquals("xyz", value.value);
    }

    void testLowerCasingSerializer() throws Exception {
        assertArrayEquals(LOWERCASE_OUTPUT,
                MAPPER.writeValueAsBytes(new StringWrapperWithConvert("ABC")));
    }

    void testSimple() {
        assertEquals(Boolean.TRUE, MAPPER.convertValue("true", Boolean.class));
        assertEquals(Integer.valueOf(-3), MAPPER.convertValue("-3", Integer.class));
        assertEquals(Long.valueOf(77), MAPPER.convertValue("77", Long.class));

        int[] ints = { 1, 2, 3 };
        List<Integer> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        values.add(3);
        assertArrayEquals(ints, MAPPER.convertValue(values, int[].class));
    }

    void testStringsToInts() {
        assertArrayEquals(new int[] { 1, 2, 3, 4, -1, 0 },
                MAPPER.convertValue("1  2 3    4  -1 0".split("\\s+"), int[].class));
    }
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_ARRAY,
            use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes(@JsonSubTypes.Type(value = Child.class))
    abstract static class Parent {
        public int x;
        public int y;
    }
@com.fasterxml.jackson.annotation.JsonTypeName("child")
    public static class Child extends Parent {
        public int w;
        public int h;
    }
static class LCConverter extends StdConverter<String, String> {
        @Override
        public String convert(String value) {
            return value.toLowerCase();
        }
    }
static class StringWrapperWithConvert {
        @JsonSerialize(converter = LCConverter.class)
        @JsonDeserialize(converter = LCConverter.class)
        public String value;

        protected StringWrapperWithConvert() { }

        StringWrapperWithConvert(String value) {
            this.value = value;
        }
    }

    void __invoke_testBytesToBase64AndBack() throws Exception {
        try {
            testBytesToBase64AndBack();
        } finally {
        }
    }


    void __invoke_testBytestoCharArray() throws Exception {
        try {
            testBytestoCharArray();
        } finally {
        }
    }


    void __invoke_testLowerCasingDeserializer() throws Exception {
        try {
            testLowerCasingDeserializer();
        } finally {
        }
    }


    void __invoke_testLowerCasingSerializer() throws Exception {
        try {
            testLowerCasingSerializer();
        } finally {
        }
    }


    void __invoke_testSimple() throws Exception {
        try {
            testSimple();
        } finally {
        }
    }


    void __invoke_testStringsToInts() throws Exception {
        try {
            testStringsToInts();
        } finally {
        }
    }

}
