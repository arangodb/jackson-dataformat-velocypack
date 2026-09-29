package tools.jackson.databind.ser;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0556F0 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final ObjectMapper ALPHA_MAPPER = VPackMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();
private static final byte[] MAP_AS_NUMBER = VPackWireFixtureTest.hex("28 2a");
private static final byte[] INHERITED_GETTERS = VPackWireFixtureTest.hex(
            "0b 0f 03 41 78 31 41 79 32 41 7a 33 03 06 09");
private static final byte[] CLASS_SERIALIZER = VPackWireFixtureTest.hex("1a");
private static final byte[] ACTIVE_METHOD_SERIALIZER = VPackWireFixtureTest.hex(
            "0b 0b 01 41 78 44 58 31 33 58 03");
private static final byte[] INACTIVE_METHOD_SERIALIZER = VPackWireFixtureTest.hex(
            "0b 07 01 41 78 38 03");
private static final byte[] IMPLICIT_CREATOR_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 63 31 41 61 32 41 62 30 06 09 03");
private static final byte[] EXPLICIT_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 63 33 41 61 31 41 62 32 41 64 34 06 09 03 0c");
private static final byte[] ALPHABETIC_ORDER = VPackWireFixtureTest.hex(
            "0b 13 04 41 64 34 41 61 31 41 62 32 41 63 33 06 09 0c 03");
private static final byte[] CREATOR_EXPLICIT_ORDER = VPackWireFixtureTest.hex(
            "0b 0f 03 41 61 31 41 63 33 41 62 32 03 09 06");
private static final byte[] ALPHA_CREATOR_ORDER = VPackWireFixtureTest.hex(
            "0b 0b 02 41 62 32 41 61 31 06 03");
private static final byte[] ALPHA_CREATOR_ORDER_WITHOUT_CREATOR_PRIORITY =
            VPackWireFixtureTest.hex("0b 0b 02 41 61 31 41 62 32 03 06");
private static final byte[] ORDER_BY_INDEX = VPackWireFixtureTest.hex(
            "0b 17 05 41 66 30 41 75 30 41 62 30 41 61 30 41 72 30 0c 09 03 0f 06");

    void testWithMapVpack() throws Exception {
        assertArrayEquals(MAP_AS_NUMBER, MAPPER.writeValueAsBytes(new MapAsNumber()));
    }

    void testWithValueToTreeVpackUnsupported() {
        // Provenance: JsonValueSerializationTest#testWithValueToTree().
        assertThrows(UnsupportedOperationException.class, () -> {
            JsonNode node = MAPPER.valueToTree(new RawWrapped("{ }"));
            MAPPER.writeValueAsBytes(node);
        });
    }
static class MapAsNumber extends HashMap<String, String> {
        @JsonValue public int value() { return 42; }
    }
static class RawWrapped {
        @JsonRawValue private final String json;
        RawWrapped(String json) { this.json = json; }
    }
static class BaseBean {
        public int getX() { return 1; }
        @JsonProperty("y") private int getY() { return 2; }
    }
static class SubClassBean extends BaseBean {
        public int getZ() { return 3; }
    }
@JsonSerialize(using = BogusSerializer.class)
    static class ClassSerializer { }
static class BogusSerializer extends StdSerializer<Object> {
        BogusSerializer() { super(Object.class); }
        @Override
        public void serialize(Object value, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeBoolean(true);
        }
    }
static class ClassMethodSerializer {
        private final int x;
        ClassMethodSerializer(int x) { this.x = x; }
        @JsonSerialize(using = StringSerializer.class)
        public int getX() { return x; }
    }
static class StringSerializer extends StdSerializer<Object> {
        StringSerializer() { super(Object.class); }
        @Override
        public void serialize(Object value, JsonGenerator generator,
                tools.jackson.databind.SerializationContext provider) {
            generator.writeString("X" + value + "X");
        }
    }
static class InactiveClassMethodSerializer {
        private final int x;
        InactiveClassMethodSerializer(int x) { this.x = x; }
        @JsonSerialize(using = ValueSerializer.None.class)
        public int getX() { return x; }
    }
static class BeanWithCreator {
        public int a;
        public int b;
        public int c;
        @JsonCreator
        BeanWithCreator(@JsonProperty("c") int c, @JsonProperty("a") int a) {
            this.a = a;
            this.c = c;
        }
    }
@JsonPropertyOrder({ "c", "a", "b" })
    static class BeanWithOrder {
        public int d, b, a, c;
        BeanWithOrder(int a, int b, int c, int d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }
@JsonPropertyOrder(value = { "d" }, alphabetic = true)
    static class SubBeanWithOrder extends BeanWithOrder {
        SubBeanWithOrder(int a, int b, int c, int d) { super(a, b, c, d); }
    }
@JsonPropertyOrder({ "a", "c" })
    static class BeanFor2879 {
        public int c;
        public int b;
        public int a;
        @JsonCreator
        BeanFor2879(@JsonProperty("a") int a, @JsonProperty("b") int b,
                @JsonProperty("c") int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
@JsonPropertyOrder(alphabetic = true)
    static class BeanForGH311 {
        private final int a;
        private final int b;
        @JsonCreator
        BeanForGH311(@JsonProperty("b") int b, @JsonProperty("a") int a) {
            this.a = a;
            this.b = b;
        }
        public int getA() { return a; }
        public int getB() { return b; }
    }
@JsonPropertyOrder({ "f" })
    static class OrderingByIndexBean {
        public int r;
        public int a;
        @JsonProperty(index = 1) public int b;
        @JsonProperty(index = 0) public int u;
        public int f;
    }

    void __invoke_testWithMapVpack() throws Exception {
        try {
            testWithMapVpack();
        } finally {
        }
    }


    void __invoke_testWithValueToTreeVpackUnsupported() throws Exception {
        try {
            testWithValueToTreeVpackUnsupported();
        } finally {
        }
    }

}
