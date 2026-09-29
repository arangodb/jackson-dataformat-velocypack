package tools.jackson.databind.ser.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0573F2 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void testBooleanArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyBooleanArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0e 01 45 76 61 6c 75 65 02 04 1a 19 03"),
                MAPPER.writeValueAsBytes(new NonEmptyBooleanArray(true, false)));
    }

    void testByteArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyByteArray()));
    }

    void testCharArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyCharArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 01 45 76 61 6c 75 65 42 61 62 03"),
                MAPPER.writeValueAsBytes(new NonEmptyCharArray('a', 'b')));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 10 01 45 76 61 6c 75 65 02 06 41 61 41 62 03"),
                MAPPER.writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsBytes(new NonEmptyCharArray('a', 'b')));
    }

    void testCustomFilterWithObjectArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 41 31 41 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new ObjectArray5515Pojo("1", "foo", "2")));
    }

    void testCustomFilterWithStringArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 46 76 61 6c 75 65 73 02 06 41 31 41 32 03"),
                CONTAINER_MAPPER.writeValueAsBytes(
                        new StringArray5515PojoCustom("1", "foo", "2")));
    }

    void testDoubleArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyDoubleArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 1e 01 45 76 61 6c 75 65 02 14 "
              + "1b 00 00 00 00 00 00 d0 3f "
              + "1b 00 00 00 00 00 00 f0 bf 03"),
                MAPPER.writeValueAsBytes(new NonEmptyDoubleArray(0.25, -1.0)));
    }

    void testFloatArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyFloatArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 15 01 45 76 61 6c 75 65 02 0b "
              + "1b 00 00 00 00 00 00 e0 3f 03"),
                MAPPER.writeValueAsBytes(new NonEmptyFloatArray(0.5f)));
    }

    void testIntArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyIntArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0d 01 45 76 61 6c 75 65 02 03 32 03"),
                MAPPER.writeValueAsBytes(new NonEmptyIntArray(2)));
    }

    void testLongArrayVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                MAPPER.writeValueAsBytes(new NonEmptyLongArray()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0e 01 45 76 61 6c 75 65 02 04 33 34 03"),
                MAPPER.writeValueAsBytes(new NonEmptyLongArray(3, 4)));
    }
private static ObjectMapper realNonDefaultMapper() {
        return VPackMapper.builder()
                .enable(MapperFeature.USE_REAL_INCLUDE_NON_DEFAULT)
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyByteArray {
        public byte[] value;

        NonEmptyByteArray(byte... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyCharArray {
        public char[] value;

        NonEmptyCharArray(char... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyIntArray {
        public int[] value;

        NonEmptyIntArray(int... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyLongArray {
        public long[] value;

        NonEmptyLongArray(long... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyBooleanArray {
        public boolean[] value;

        NonEmptyBooleanArray(boolean... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyDoubleArray {
        public double[] value;

        NonEmptyDoubleArray(double... value) { this.value = value; }
    }
@JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class NonEmptyFloatArray {
        public float[] value;

        NonEmptyFloatArray(float... value) { this.value = value; }
    }
static class Foo5515Filter {
        @Override
        public boolean equals(Object other) {
            return other != null && "foo".equals(other);
        }
    }
static class ObjectArray5515Pojo {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = Foo5515Filter.class)
        public Object[] values;

        ObjectArray5515Pojo(Object... values) { this.values = values; }
    }
static class StringArray5515PojoCustom {
        @JsonInclude(content = JsonInclude.Include.CUSTOM,
                contentFilter = Foo5515Filter.class)
        public String[] values;

        StringArray5515PojoCustom(String... values) { this.values = values; }
    }
static class Bar {
        public String getName() { return "I_AM_EMPTY"; }
    }
public static class BarSerializer extends ValueSerializer<Bar> {
        @Override
        public void serialize(Bar value, JsonGenerator gen, SerializationContext provider) {
            gen.writePOJO(value);
        }

        @Override
        public boolean isEmpty(SerializationContext provider, Bar value) {
            return "I_AM_EMPTY".equals(value.getName());
        }
    }
static class Foo {
        @JsonSerialize(using = BarSerializer.class)
        public Bar getBar() { return new Bar(); }
    }
static class Entity {
        private String someFieldWithDefault = "a default";

        public void setSomeFieldWithDefault(String value) {
            someFieldWithDefault = value;
        }

        public String getSomeFieldWithDefault() {
            return someFieldWithDefault;
        }
    }

    void __invoke_testBooleanArrayVpack() throws Exception {
        try {
            testBooleanArrayVpack();
        } finally {
        }
    }


    void __invoke_testByteArrayVpack() throws Exception {
        try {
            testByteArrayVpack();
        } finally {
        }
    }


    void __invoke_testCharArrayVpack() throws Exception {
        try {
            testCharArrayVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithObjectArrayVpack() throws Exception {
        try {
            testCustomFilterWithObjectArrayVpack();
        } finally {
        }
    }


    void __invoke_testCustomFilterWithStringArrayVpack() throws Exception {
        try {
            testCustomFilterWithStringArrayVpack();
        } finally {
        }
    }


    void __invoke_testDoubleArrayVpack() throws Exception {
        try {
            testDoubleArrayVpack();
        } finally {
        }
    }


    void __invoke_testFloatArrayVpack() throws Exception {
        try {
            testFloatArrayVpack();
        } finally {
        }
    }


    void __invoke_testIntArrayVpack() throws Exception {
        try {
            testIntArrayVpack();
        } finally {
        }
    }


    void __invoke_testLongArrayVpack() throws Exception {
        try {
            testLongArrayVpack();
        } finally {
        }
    }

}
