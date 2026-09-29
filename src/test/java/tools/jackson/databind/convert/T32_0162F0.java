package tools.jackson.databind.convert;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0162F0 {
private static final byte[] UPDATE_OBJECT = VPackWireFixtureTest.hex(
            "0b 10 03 41 78 33 41 79 34 41 77 28 6f 09 03 06");
private static final byte[] LOWERCASE_INPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 58 79 5a 03");
private static final byte[] LOWERCASE_OUTPUT = VPackWireFixtureTest.hex(
            "0b 0e 01 45 76 61 6c 75 65 43 61 62 63 03");
private final ObjectMapper MAPPER = new VPackMapper();

    void testPolymorphicTest() throws Exception {
        Child child = new Child();
        child.w = 10;
        child.h = 11;
        MAPPER.readerForUpdating(child).readValue(UPDATE_OBJECT);
        assertEquals(3, child.x);
        assertEquals(4, child.y);
        assertEquals(111, child.w);
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

    void __invoke_testPolymorphicTest() throws Exception {
        try {
            testPolymorphicTest();
        } finally {
        }
    }

}
