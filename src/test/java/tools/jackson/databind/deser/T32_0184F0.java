package tools.jackson.databind.deser;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.IgnoredPropertyException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import tools.jackson.dataformat.velocypack.*;

class T32_0184F0 {
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

    void testSimpleIgnore() {
        SizeClassIgnore result = MAPPER.readValue(SIMPLE_IGNORE, SizeClassIgnore.class);
        assertEquals(1, result._x);
        assertEquals(0, result._y);
    }

    void testSimpleInclude() {
        OnlyYAndZ result = MAPPER.readValue(SIMPLE_INCLUDE, OnlyYAndZ.class);
        assertEquals(0, result._x);
        assertEquals(4, result._y);
        assertEquals(3, result._z);
    }

    void testIncludeIgnoredAndUnrecognizedField() {
        ObjectReader reader = MAPPER.readerFor(OnlyY.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        OnlyY result = reader.readValue(INCLUDE_X_AND_Y);
        assertEquals(0, result.x);
        assertEquals(4, result.y);

        ObjectReader strictReader = reader.with(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        IgnoredPropertyException ignored = assertThrows(IgnoredPropertyException.class,
                () -> strictReader.readValue(INCLUDE_X_Y_AND_Z));
        assertTrue(ignored.getMessage().contains("Ignored field"));

        UnrecognizedPropertyException unrecognized = assertThrows(
                UnrecognizedPropertyException.class,
                () -> strictReader.readValue(INCLUDE_Y_AND_Z));
        assertTrue(unrecognized.getMessage().contains("Unrecognized property \"z\""));

        ObjectReader permissive = strictReader
                .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .without(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        OnlyY accepted = permissive.readValue(INCLUDE_Y_AND_Z);
        assertEquals(3, accepted.y);
    }

    void testMergeInclude() {
        OnlyYWrapperForOnlyYAndZ result = MAPPER.readValue(
                MERGE_INCLUDE, OnlyYWrapperForOnlyYAndZ.class);
        assertEquals(0, result.onlyY._x);
        assertEquals(6, result.onlyY._y);
        assertEquals(0, result.onlyY._z);
    }

    void testListInclude() {
        IncludeForListValuesY result = MAPPER.readValue(
                LIST_INCLUDE, IncludeForListValuesY.class);
        assertEquals(0, result.onlyYs.get(0)._x);
        assertEquals(4, result.onlyYs.get(0)._y);
        assertEquals(0, result.onlyYs.get(0)._z);
    }

    void testMapWrapper() {
        MapWrapper result = MAPPER.readValue(MAP_WRAPPER, MapWrapper.class);
        assertNotNull(result.value);
        assertEquals(2, result.value.get("a").intValue());
        assertFalse(result.value.containsKey("b"));
    }

    void testMyMap() {
        MyMap result = MAPPER.readValue(MY_MAP, MyMap.class);
        assertEquals("2", result.get("a"));
        assertFalse(result.containsKey("b"));
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

    void __invoke_testSimpleIgnore() throws Exception {
        try {
            testSimpleIgnore();
        } finally {
        }
    }


    void __invoke_testSimpleInclude() throws Exception {
        try {
            testSimpleInclude();
        } finally {
        }
    }


    void __invoke_testIncludeIgnoredAndUnrecognizedField() throws Exception {
        try {
            testIncludeIgnoredAndUnrecognizedField();
        } finally {
        }
    }


    void __invoke_testMergeInclude() throws Exception {
        try {
            testMergeInclude();
        } finally {
        }
    }


    void __invoke_testListInclude() throws Exception {
        try {
            testListInclude();
        } finally {
        }
    }


    void __invoke_testMapWrapper() throws Exception {
        try {
            testMapWrapper();
        } finally {
        }
    }


    void __invoke_testMyMap() throws Exception {
        try {
            testMyMap();
        } finally {
        }
    }

}
