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

class T32_0573F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper CONTAINER_MAPPER = VPackMapper.builder()
            .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
            .build();

    void test86Vpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .changeDefaultPropertyInclusion(incl ->
                        incl.withValueInclusion(JsonInclude.Include.NON_DEFAULT))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex("0a"),
                mapper.writeValueAsBytes(new Foo()));
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

    void __invoke_test86Vpack() throws Exception {
        try {
            test86Vpack();
        } finally {
        }
    }

}
