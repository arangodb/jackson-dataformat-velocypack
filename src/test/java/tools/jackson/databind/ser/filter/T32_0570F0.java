package tools.jackson.databind.ser.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.OptBoolean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.module.SimpleModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import tools.jackson.dataformat.velocypack.*;

class T32_0570F0 {
private static final ObjectMapper MAPPER = new VPackMapper();

    // Provenance: IgnoredTypesTest#testIgnoredType().
    void testIgnoredTypeVpack() throws Exception {
        NonIgnoredType bean = MAPPER.readValue(VPackWireFixtureTest.hex(
                "0b 1a 02 47 69 67 6e 6f 72 65 64 13 06 31 32 0a 03 "
                + "45 76 61 6c 75 65 39 03 11"), NonIgnoredType.class);
        assertNotNull(bean);
        assertEquals(9, bean.value);
    }

    // Provenance: IgnoredTypesTest#testSingleWithMixins().
    void testSingleWithMixinsVpack() throws Exception {
        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Person.class, PersonMixin.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 31 03"),
                mapper.writeValueAsBytes(new PersonWrapper()));
    }

    // Provenance: IgnoredTypesTest#testListWithMixins().
    void testListWithMixinsVpack() throws Exception {
        SimpleModule module = new SimpleModule();
        module.setMixInAnnotation(Person.class, PersonMixin.class);
        ObjectMapper mapper = VPackMapper.builder().addModule(module).build();
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Bob"));
        assertArrayEquals(VPackWireFixtureTest.hex(
                "02 0f 0b 0d 01 44 6e 61 6d 65 43 42 6f 62 03"),
                mapper.writeValueAsBytes(persons));
    }

    // Provenance: IgnoredTypesTest#testIgnoreUsingConfigOverride().
    void testIgnoreUsingConfigOverrideVpack() throws Exception {
        ObjectMapper mapper = VPackMapper.builder()
                .withConfigOverride(Wrapped.class, o -> o.setIsIgnoredType(true))
                .build();
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 0b 01 45 76 61 6c 75 65 33 03"),
                mapper.writeValueAsBytes(new Wrapper()));
        Wrapper result = mapper.readValue(VPackWireFixtureTest.hex(
                "0b 15 02 45 76 61 6c 75 65 35 47 77 72 61 70 70 65 64 "
                + "19 03 0a"), Wrapper.class);
        assertEquals(5, result.value);
    }

    // Provenance: IgnoredTypesTest#testIgnoreTypeViaInterface().
    void testIgnoreTypeViaInterfaceVpack() throws Exception {
        assertArrayEquals(VPackWireFixtureTest.hex(
                "0b 08 01 41 78 28 0d 03"),
                MAPPER.writeValueAsBytes(new ContainsIgnorable()));
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

    void __invoke_testIgnoredTypeVpack() throws Exception {
        try {
            testIgnoredTypeVpack();
        } finally {
        }
    }


    void __invoke_testSingleWithMixinsVpack() throws Exception {
        try {
            testSingleWithMixinsVpack();
        } finally {
        }
    }


    void __invoke_testListWithMixinsVpack() throws Exception {
        try {
            testListWithMixinsVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreUsingConfigOverrideVpack() throws Exception {
        try {
            testIgnoreUsingConfigOverrideVpack();
        } finally {
        }
    }


    void __invoke_testIgnoreTypeViaInterfaceVpack() throws Exception {
        try {
            testIgnoreTypeViaInterfaceVpack();
        } finally {
        }
    }

}
