package tools.jackson.databind.ext.desktop;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0298F0 {
private static final ObjectMapper MAPPER = VPackMapper.builder().build();
private static final byte[] READ_ONLY_TWO = VPackWireFixtureTest.hex(
            "14 1c 48 74 65 73 74 45 6e 75 6d 40 44 6e 61 6d 65 "
          + "49 63 68 61 6e 67 79 6f 6e 67 02");
private static final byte[] TRANSIENT_UNKNOWN = VPackWireFixtureTest.hex(
            "14 06 41 61 33 01");
private static final byte[] DECIMAL_ZERO = VPackWireFixtureTest.hex(
            "c8 01 00 00 00 00 00");
private static final byte[] DECIMAL_15_NANOS = VPackWireFixtureTest.hex(
            "c8 06 f7 ff ff ff 01 50 00 00 00 72");
private static final byte[] DECIMAL_15_72 = VPackWireFixtureTest.hex(
            "c8 02 fe ff ff ff 15 72");
private static final byte[] DECIMAL_LARGE_NANOS = VPackWireFixtureTest.hex(
            "c8 0a f7 ff ff ff 19 82 73 42 23 11 92 83 74 65");
private static final byte[] DECIMAL_LARGE_INTEGER = VPackWireFixtureTest.hex(
            "c8 06 00 00 00 00 01 98 27 34 22 31");

    // Provenance: ReadOnlyDeserWithConstructorProperties1890Test#testDeserializeTwoFields.
    void testDeserializeTwoFieldsVpack() throws Exception {
        Person person = MAPPER.readValue(READ_ONLY_TWO, Person.class);
        assertEquals(TestEnum.DEFAULT, person.getTestEnum());
        assertEquals("changyong", person.name);
    }
private static Map<?, ?> asMap(Object value) throws Exception {
        return asMap(MAPPER, value);
    }
private static Map<?, ?> asMap(ObjectMapper mapper, Object value) throws Exception {
        return mapper.readValue(mapper.writeValueAsBytes(value), Map.class);
    }
private static void assertDecimal(byte[] fixture, BigDecimal expected, long seconds)
            throws Exception {
        try (JsonParser parser = new VPackFactory().createParser(fixture)) {
            JsonToken token = parser.nextToken();
            assertTrue(token == JsonToken.VALUE_NUMBER_INT
                    || token == JsonToken.VALUE_NUMBER_FLOAT);
            BigDecimal value = parser.getDecimalValue();
            assertEquals(expected, value);
            assertEquals(expected.scale(), value.scale());
            assertEquals(seconds, value.longValue());
            assertNull(parser.nextToken());
        }
    }
@JsonPropertyOrder({ "x" })
    static class ClassyTransient {
        public transient int value = 3;
        public int getValue() { return value; }
        public int getX() { return 42; }
    }
static class SimplePrunableTransient {
        public int a = 1;
        public transient int b = 2;
    }
static class BeanTransient {
        @Transient
        public int getX() { return 3; }
        public int getY() { return 4; }
    }
static class OverridableTransient {
        @JsonProperty
        public transient int tValue;
        public OverridableTransient(int value) { tValue = value; }
    }
static class TransientToPrune {
        public transient int a;
        public int getA() { return a; }
    }
static class Obj3948 implements Serializable {
        private static final long serialVersionUID = -1L;
        private String a = "hello";
        @JsonIgnore
        private transient String b = "world";
        @JsonProperty("cat")
        private String c = "jackson";
        @JsonProperty("dog")
        private transient String d = "databind";
        public String getA() { return a; }
        public String getB() { return b; }
        public String getC() { return c; }
        public String getD() { return d; }
    }
static class Person {
        public String name;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private TestEnum testEnum = TestEnum.DEFAULT;

        Person() { }
        protected Person(TestEnum testEnum, String name) {
            this.testEnum = testEnum;
            this.name = name;
        }
        public TestEnum getTestEnum() { return testEnum; }
        public void setTestEnum(TestEnum testEnum) { this.testEnum = testEnum; }
    }
enum TestEnum { DEFAULT, TEST }

    void __invoke_testDeserializeTwoFieldsVpack() throws Exception {
        try {
            testDeserializeTwoFieldsVpack();
        } finally {
        }
    }

}
