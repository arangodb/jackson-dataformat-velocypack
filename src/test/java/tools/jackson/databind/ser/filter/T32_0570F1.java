package tools.jackson.databind.ser.filter;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import tools.jackson.dataformat.velocypack.*;

class T32_0570F1 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: IncludePropsForSerTest#testExplicitIncludeWithBean().
    void testExplicitIncludeWithBeanVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0e 02 41 61 33 41 64 43 61 62 63 03 06"),
                MAPPER.writeValueAsBytes(new IncludeSome()));
    }

    // Provenance: IncludePropsForSerTest#testExplicitIncludeWithMap().
    void testExplicitIncludeWithMapVpack() throws Exception {
        IncludeMap value = new IncludeMap();
        value.put("a", "b");
        value.put("c", "d");
        value.put("@class", IncludeMap.class.getName());
        byte[] serialized = MAPPER.writeValueAsBytes(value);
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 48 02 41 61 41 62 46 40 63 6c 61 73 73 77 74" +
                "6f 6f 6c 73 2e 6a 61 63 6b 73 6f 6e 2e 64 61 74" +
                "61 62 69 6e 64 2e 73 65 72 2e 66 69 6c 74 65 72" +
                "2e 54 33 32 5f 30 35 37 30 46 31 24 49 6e 63 6c" +
                "75 64 65 4d 61 70 07 03"), serialized);
        HashMap<String, String> result = MAPPER.readValue(serialized, HashMap.class);
        assertEquals(2, result.size());
        assertEquals(IncludeMap.class.getName(), result.get("@class"));
        assertEquals("b", result.get("a"));
    }

    // Provenance: IncludePropsForSerTest#testIncludeViaOnlyProps().
    void testIncludeViaOnlyPropsVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 79 32 03 03"),
                MAPPER.writeValueAsBytes(new WrapperWithPropInclude()));
    }

    // Provenance: IncludePropsForSerTest#testIncludeForListValues().
    void testIncludeForListValuesVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 19 01 4b 63 6f 6f 72 64 69 6e 61 74 65 73 "
                + "02 09 0b 07 01 41 78 31 03 03"),
                MAPPER.writeValueAsBytes(new IncludeForListValuesXY()));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 19 01 4b 63 6f 6f 72 64 69 6e 61 74 65 73 "
                + "02 09 0b 07 01 41 78 31 03 03"),
                MAPPER.writeValueAsBytes(new IncludeForListValuesXYZ()));
    }

    // Provenance: IncludePropsForSerTest#testIgnoreWithInclude().
    void testIgnoreWithIncludeVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 11 01 45 76 61 6c 75 65 0b 07 01 41 78 31 03 03"),
                MAPPER.writeValueAsBytes(new WrapperWithPropIgnore()));
    }

    // Provenance: IncludePropsForSerTest#testIncludePropertiesOrder().
    void testIncludePropertiesOrderVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 03 41 63 33 41 61 31 41 62 32 06 09 03"),
                MAPPER.writeValueAsBytes(new IncludeWithOrder()));
    }

    // Provenance: IncludePropsForSerTest#testIncludePropertiesOrderFalse().
    void testIncludePropertiesOrderFalseVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0f 03 41 61 31 41 62 32 41 63 33 03 06 09"),
                MAPPER.writeValueAsBytes(new IncludeWithOrderFalse()));
    }
@JsonIgnoreType
    class IgnoredType {
        IgnoredType(IgnoredType src) { }
    }
@JsonIgnoreType(false)
    static class NonIgnoredType {
        public int value = 13;
        public IgnoredType ignored;
    }
@JsonIgnoreType
    static class Person {
        public String name;
        Person() { }
        Person(String name) { this.name = name; }
    }
static class PersonWrapper {
        public int value = 1;
        public Person person = new Person("Foo");
    }
@JsonIgnoreType
    static class PersonMixin { }
static class Wrapper {
        public int value = 3;
        public Wrapped wrapped = new Wrapped(7);
    }
static class Wrapped {
        public int x;
        public Wrapped() { throw new RuntimeException("Should not be called"); }
        public Wrapped(int x) { this.x = x; }
    }
@JsonIgnoreType
    interface IgnoreMe { }
static class ChildOfIgnorable implements IgnoreMe {
        public int value = 42;
    }
static class ContainsIgnorable {
        public ChildOfIgnorable ign = new ChildOfIgnorable();
        public int x = 13;
    }
@JsonIncludeProperties({"a", "d"})
    static class IncludeSome {
        public int a = 3;
        public String b = "x";
        public int getC() { return -6; }
        public String getD() { return "abc"; }
    }
@SuppressWarnings("serial")
    @JsonIncludeProperties({"@class", "a"})
    static class IncludeMap extends HashMap<String, String> { }
static class WrapperWithPropInclude {
        @JsonIncludeProperties({"y"})
        public XY value = new XY();
    }
static class XY {
        public int x = 1;
        public int y = 2;
    }
@JsonIncludeProperties({"x", "y"})
    static class XYZ {
        public int x = 1;
        public int y = 2;
        public int z = 3;
    }
static class IncludeForListValuesXY {
        @JsonIncludeProperties({"x"})
        public List<XY> coordinates = List.of(new XY());
    }
static class IncludeForListValuesXYZ {
        @JsonIncludeProperties({"x"})
        public List<XYZ> coordinates = List.of(new XYZ());
    }
static class WrapperWithPropIgnore {
        @JsonIgnoreProperties("y")
        public XYZ value = new XYZ();
    }
@JsonIncludeProperties(value = {"c", "a", "b"}, order = OptBoolean.TRUE)
    static class IncludeWithOrder {
        public int a = 1;
        public int b = 2;
        public int c = 3;
    }
@JsonIncludeProperties(value = {"c", "a", "b"}, order = OptBoolean.FALSE)
    static class IncludeWithOrderFalse {
        public int a = 1;
        public int b = 2;
        public int c = 3;
    }

    void __invoke_testExplicitIncludeWithBeanVpack() throws Exception {
        try {
            testExplicitIncludeWithBeanVpack();
        } finally {
        }
    }


    void __invoke_testExplicitIncludeWithMapVpack() throws Exception {
        try {
            testExplicitIncludeWithMapVpack();
        } finally {
        }
    }


    void __invoke_testIncludeViaOnlyPropsVpack() throws Exception {
        try {
            testIncludeViaOnlyPropsVpack();
        } finally {
        }
    }


    void __invoke_testIncludeForListValuesVpack() throws Exception {
        try {
            testIncludeForListValuesVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreWithIncludeVpack() throws Exception {
        try {
            testIgnoreWithIncludeVpack();
        } finally {
        }
    }


    void __invoke_testIncludePropertiesOrderVpack() throws Exception {
        try {
            testIncludePropertiesOrderVpack();
        } finally {
        }
    }


    void __invoke_testIncludePropertiesOrderFalseVpack() throws Exception {
        try {
            testIncludePropertiesOrderFalseVpack();
        } finally {
        }
    }

}
