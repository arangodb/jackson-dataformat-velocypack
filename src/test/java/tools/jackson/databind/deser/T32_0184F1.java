package tools.jackson.databind.deser;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.ValueInstantiationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import tools.jackson.dataformat.velocypack.*;

class T32_0184F1 {
private static final ObjectMapper MAPPER = new VPackMapper();
private static final byte[] SIMPLE_IGNORE = VPackWireFixtureTest.hex(
            "14 09 41 78 31 41 79 32 02");
private static final byte[] SIMPLE_INCLUDE = VPackWireFixtureTest.hex(
            "14 10 41 78 31 42 5f 78 31 41 79 32 41 7a 33 04");
private static final byte[] INCLUDE_X_AND_Y = VPackWireFixtureTest.hex(
            "14 09 41 78 33 41 79 34 02");
private static final byte[] INCLUDE_X_Y_AND_Z = VPackWireFixtureTest.hex(
            "14 0c 41 78 33 41 79 34 41 7a 35 03");
private static final byte[] INCLUDE_Y_AND_Z = VPackWireFixtureTest.hex(
            "14 09 41 79 33 41 7a 32 02");
private static final byte[] INCLUDE_NESTED = VPackWireFixtureTest.hex(
            "14 0c 41 78 32 41 79 33 41 7a 34 03");
private static final byte[] MERGE_INCLUDE = VPackWireFixtureTest.hex(
            "14 15 45 6f 6e 6c 79 59 14 0c 41 78 32 41 79 33 41 7a 34 03 01");
private static final byte[] LIST_INCLUDE = VPackWireFixtureTest.hex(
            "14 19 46 6f 6e 6c 79 59 73 13 0f 14 0c 41 78 31 41 79 32 41 7a 33 03 01 01");
private static final byte[] MAP_WRAPPER = VPackWireFixtureTest.hex(
            "14 12 45 76 61 6c 75 65 14 09 41 61 32 41 62 33 02 01");
private static final byte[] MY_MAP = VPackWireFixtureTest.hex(
            "14 09 41 61 32 41 62 33 02");
private static final byte[] INTEGER_FIVE = VPackWireFixtureTest.hex("35");

    void testJsonIntegerDeserializationPrefersInt() {
        A result = MAPPER.readValue(INTEGER_FIVE, A.class);
        assertEquals(1, result.creatorType);
    }

    void testJsonIntegerDeserializationPrefersLong() {
        B result = MAPPER.readValue(INTEGER_FIVE, B.class);
        assertEquals(2, result.creatorType);
    }

    void testJsonIntegerDeserializationPrefersBigInteger() {
        C result = MAPPER.readValue(INTEGER_FIVE, C.class);
        assertEquals(3, result.creatorType);
    }

    void testJsonIntegerToDouble() {
        Stuff result = MAPPER.readValue(INTEGER_FIVE, Stuff.class);
        assertEquals(5, result.value);
    }

    void testJsonIntegerIntoDoubleConstructorThrows() {
        ValueInstantiationException exception = assertThrows(
                ValueInstantiationException.class,
                () -> MAPPER.readValue(INTEGER_FIVE, D.class));
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertEquals("boo", exception.getCause().getMessage());
    }
static final class SizeClassIgnore {
        int _x;
        int _y;

        public void setX(int value) { _x = value; }
        @JsonIgnore public void setY(int value) { _y = value; }

        @JsonProperty("y")
        void replacementForY(int value) { }
    }
@JsonIncludeProperties({ "y", "z" })
    static class OnlyYAndZ {
        int _x;
        int _y;
        int _z;

        public void setX(int value) { _x = value; }
        public void setY(int value) { _y = value; }
        public void setZ(int value) { _z = value; }

        @JsonProperty("y")
        void replacementForY(int value) { _y = value * 2; }
    }
@JsonIncludeProperties({ "y", "z" })
    static class OnlyY {
        public int x;
        public int y = 1;
    }
static class OnlyYWrapperForOnlyYAndZ {
        @JsonIncludeProperties("y")
        public OnlyYAndZ onlyY;
    }
static class IncludeForListValuesY {
        @JsonIncludeProperties({ "y" })
        public List<OnlyYAndZ> onlyYs;
    }
@SuppressWarnings("serial")
    @JsonIncludeProperties({ "@class", "a" })
    static class MyMap extends HashMap<String, String> { }
static class MapWrapper {
        @JsonIncludeProperties({ "a" })
        public HashMap<String, Integer> value;
    }
static class A {
        final int creatorType;
        A(int value) { creatorType = 1; }
        A(long value) { creatorType = 2; }
        A(BigInteger value) { creatorType = 3; }
        A(double value) { creatorType = 4; }
    }
static class B {
        final int creatorType;
        B(long value) { creatorType = 2; }
        B(BigInteger value) { creatorType = 3; }
        B(double value) { creatorType = 4; }
    }
static class C {
        final int creatorType;
        C(BigInteger value) { creatorType = 3; }
        C(double value) { creatorType = 4; }
    }
static final class D {
        D(double value) { throw new IllegalArgumentException("boo"); }
    }
static class Stuff {
        final double value;
        Stuff(double value) { this.value = value; }
    }

    void __invoke_testJsonIntegerDeserializationPrefersInt() throws Exception {
        try {
            testJsonIntegerDeserializationPrefersInt();
        } finally {
        }
    }


    void __invoke_testJsonIntegerDeserializationPrefersLong() throws Exception {
        try {
            testJsonIntegerDeserializationPrefersLong();
        } finally {
        }
    }


    void __invoke_testJsonIntegerDeserializationPrefersBigInteger() throws Exception {
        try {
            testJsonIntegerDeserializationPrefersBigInteger();
        } finally {
        }
    }


    void __invoke_testJsonIntegerToDouble() throws Exception {
        try {
            testJsonIntegerToDouble();
        } finally {
        }
    }


    void __invoke_testJsonIntegerIntoDoubleConstructorThrows() throws Exception {
        try {
            testJsonIntegerIntoDoubleConstructorThrows();
        } finally {
        }
    }

}
